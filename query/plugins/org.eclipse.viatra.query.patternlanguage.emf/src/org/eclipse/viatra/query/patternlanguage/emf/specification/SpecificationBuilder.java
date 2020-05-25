/*******************************************************************************
 * Copyright (c) 2010-2014, Zoltan Ujhelyi, Istvan Rath and Daniel Varro
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-v20.html.
 * 
 * SPDX-License-Identifier: EPL-2.0
 *******************************************************************************/
package org.eclipse.viatra.query.patternlanguage.emf.specification;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.eclipse.emf.common.util.URI;
import org.eclipse.viatra.query.patternlanguage.emf.helper.PatternLanguageHelper;
import org.eclipse.viatra.query.patternlanguage.emf.specification.internal.EPMToPBody;
import org.eclipse.viatra.query.patternlanguage.emf.specification.internal.NameToSpecificationMap;
import org.eclipse.viatra.query.patternlanguage.emf.specification.internal.PatternBodyTransformer;
import org.eclipse.viatra.query.patternlanguage.emf.specification.internal.PatternSanitizer;
import org.eclipse.viatra.query.patternlanguage.emf.validation.ValidationHelper;
import org.eclipse.viatra.query.patternlanguage.emf.vql.Annotation;
import org.eclipse.viatra.query.patternlanguage.emf.vql.Modifiers;
import org.eclipse.viatra.query.patternlanguage.emf.vql.Pattern;
import org.eclipse.viatra.query.patternlanguage.emf.vql.PatternBody;
import org.eclipse.viatra.query.runtime.api.IPatternMatch;
import org.eclipse.viatra.query.runtime.api.IQuerySpecification;
import org.eclipse.viatra.query.runtime.api.ViatraQueryMatcher;
import org.eclipse.viatra.query.runtime.matchers.ViatraQueryRuntimeException;
import org.eclipse.viatra.query.runtime.matchers.backend.QueryEvaluationHint;
import org.eclipse.viatra.query.runtime.matchers.backend.QueryEvaluationHint.BackendRequirement;
import org.eclipse.viatra.query.runtime.matchers.psystem.InitializablePQuery;
import org.eclipse.viatra.query.runtime.matchers.psystem.PBody;
import org.eclipse.viatra.query.runtime.matchers.psystem.annotations.PAnnotation;
import org.eclipse.viatra.query.runtime.matchers.psystem.queries.PProblem;
import org.eclipse.viatra.query.runtime.matchers.psystem.queries.PQuery;
import org.eclipse.viatra.query.runtime.matchers.psystem.queries.PQuery.PQueryStatus;
import org.eclipse.viatra.query.runtime.matchers.psystem.rewriters.RewriterException;
import org.eclipse.viatra.query.runtime.matchers.util.Preconditions;
import org.eclipse.xtext.common.types.JvmField;
import org.eclipse.xtext.validation.Issue;

import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;

/**
 * An instance class to initialize {@link PBody} instances from {@link Pattern} definitions. A single instance of this
 * builder is used during construction, that maintains the mapping between {@link Pattern} and {@link PQuery} objects,
 * and can be initialized with a pre-defined set of mappings.</p>
 *
 * <p>
 * The SpecificationBuilder is stateful: it stores all previously built specifications, allowing further re-use.
 *
 * @author Zoltan Ujhelyi
 * @since 2.0
 *
 */
public final class SpecificationBuilder {

    private NameToSpecificationMap patternMap;
    /**
     * This map is used to detect a re-addition of a pattern with a fqn that is used by a previously added pattern.
     */
    private Map<String, Pattern> patternNameMap = new HashMap<>();
    private Multimap<PQuery, IQuerySpecification<?>> dependantQueries = Multimaps.newSetMultimap(
            new HashMap<>(), HashSet::new);
    private PatternSanitizer sanitizer = new PatternSanitizer(/*logger*/ null /* do not log all errors */);

    /**
     * Initializes a query builder with no previously known query specifications
     */
    public SpecificationBuilder() {
        patternMap = new NameToSpecificationMap();
    }

    /**
     * Sets up a query builder with a predefined set of specifications
     */
    public SpecificationBuilder(IQuerySpecification<?>... specifications) {
        patternMap = new NameToSpecificationMap(specifications);
        processPatternSpecifications();
    }

    /**
     * Sets up a query builder with a predefined collection of specifications
     */
    public SpecificationBuilder(
            Collection<? extends IQuerySpecification<? extends ViatraQueryMatcher<? extends IPatternMatch>>> specifications) {
        patternMap = new NameToSpecificationMap(specifications);
        processPatternSpecifications();
    }

    public SpecificationBuilder(NameToSpecificationMap patternMap) {
        this.patternMap = patternMap;
        processPatternSpecifications();
    }

    /**
     * Processes all existing query specifications searching for possible pattern instances, and if found, add it to the
     * {@link #patternNameMap}.
     */
    private void processPatternSpecifications() {
        patternMap.values().stream().filter(GenericQuerySpecification.class::isInstance)
                .map(GenericQuerySpecification.class::cast)
                .forEach(spec -> patternNameMap.put(spec.getFullyQualifiedName(),
                        spec.getInternalQueryRepresentation().getPattern()));
    }

    /**
     * Creates a new or returns an existing query specification for the pattern. It is expected, that the builder will
     * not be called with different patterns having the same fqn over its entire lifecycle.
     *
     * @param pattern
     * @throws ViatraQueryRuntimeException
     * @since 2.0
     */
    public IQuerySpecification<? extends ViatraQueryMatcher<? extends IPatternMatch>> getOrCreateSpecification(
            Pattern pattern) {
        return getOrCreateSpecification(pattern, false);
    }

    /**
     * Creates a new or returns an existing query specification for the pattern. It is expected, that the builder will
     * not be called with different patterns having the same fqn over its entire lifecycle.
     *
     * @param pattern
     * @param skipPatternValidation
     *            if set to true, detailed pattern validation is skipped - true for model inferrer; not recommended for
     *            generic API
     * @throws ViatraQueryRuntimeException
     * @since 2.0
     */
    public IQuerySpecification<? extends ViatraQueryMatcher<? extends IPatternMatch>> getOrCreateSpecification(
            Pattern pattern, boolean skipPatternValidation) {
        return getOrCreateSpecification(pattern, new ArrayList<>(), skipPatternValidation);
    }

    /**
     * @since 2.0
     */
    public IQuerySpecification<? extends ViatraQueryMatcher<? extends IPatternMatch>> getOrCreateSpecification(
            Pattern pattern, List<IQuerySpecification<?>> createdPatternList, boolean skipPatternValidation) {
        Preconditions.checkArgument(pattern != null && !pattern.eIsProxy(), "Cannot create specification from a null pattern");
        String fqn = PatternLanguageHelper.getFullyQualifiedName(pattern);
        Preconditions.checkArgument(fqn != null && !"".equals(fqn), "Pattern name cannot be empty");
        Preconditions.checkArgument(!patternNameMap.containsKey(fqn) || pattern.equals(patternNameMap.get(fqn)),
                "This builder already contains a different pattern with the fqn %s of the newly added pattern.", fqn);
        return getSpecification(pattern).orElseGet(() -> buildSpecification(pattern, skipPatternValidation, createdPatternList));
    }

    protected IQuerySpecification<?> buildSpecification(Pattern pattern) {
        return buildSpecification(pattern, false, new ArrayList<>());
    }

    protected IQuerySpecification<?> buildSpecification(Pattern pattern, List<IQuerySpecification<?>> newSpecifications) {
        return buildSpecification(pattern, false, newSpecifications);
    }

    protected IQuerySpecification<?> buildSpecification(Pattern pattern, boolean skipPatternValidation, List<IQuerySpecification<?>> newSpecifications) {
        String fqn = PatternLanguageHelper.getFullyQualifiedName(pattern);
        Preconditions.checkArgument(!patternMap.containsKey(fqn), "Builder already stores query with the name of %s",
                fqn);
        if (sanitizer.admit(pattern, skipPatternValidation)) {
            Set<Pattern> newPatterns = sanitizer.getAdmittedPatterns().stream()
                    .filter(p -> !p.eIsProxy())
                    .filter(p -> {
                        final String name = PatternLanguageHelper.getFullyQualifiedName(p);
                        return !"".equals(name)
                               && !patternMap.containsKey(name);
                    }).collect(Collectors.toSet());
            // Initializing new query specifications
            for (Pattern newPattern : newPatterns) {
                String patternFqn = PatternLanguageHelper.getFullyQualifiedName(newPattern);
                GenericEMFPatternPQuery pquery = new GenericEMFPatternPQuery(newPattern, true);
                pquery.setEvaluationHints(buildHints(newPattern));
                GenericQuerySpecification specification = new GenericQuerySpecification(pquery);
                patternMap.put(patternFqn, specification);
                patternNameMap.put(patternFqn, newPattern);
                newSpecifications.add(specification);
            }
            // Updating bodies
            for (Pattern newPattern : newPatterns) {
                String patternFqn = PatternLanguageHelper.getFullyQualifiedName(newPattern);
                GenericQuerySpecification specification = (GenericQuerySpecification) patternMap.get(patternFqn);
                GenericEMFPatternPQuery pQuery = specification.getInternalQueryRepresentation();
                try {
                    buildAnnotations(newPattern, pQuery);
                    buildBodies(newPattern, pQuery);
                } catch (RewriterException e) {
                    pQuery.addError(new PProblem(e, e.getShortMessage()));
                }
                if (!PQueryStatus.ERROR.equals(pQuery.getStatus())) {
                    for (PQuery query : pQuery.getDirectReferredQueries()) {
                        dependantQueries.put(query, specification);
                    }
                }
            }
        } else {
            for (Pattern rejectedPattern : sanitizer.getRejectedPatterns()) {
                String patternFqn = PatternLanguageHelper.getFullyQualifiedName(rejectedPattern);
                if (!patternMap.containsKey(patternFqn)) {
                    newSpecifications.add(doBuildErroneousSpecification(rejectedPattern,
                            sanitizer.getProblemByPattern(rejectedPattern).stream(), true));
                }
            }
        }
        return patternMap.computeIfAbsent(fqn, name -> buildErroneousSpecification(pattern,
                "Unable to compile pattern due to an unspecified error", true));
    }

    /**
     * Creates an erroneous query specification from a given pattern object with a stream of precalculated issues. The
     * resulting query specification may or may not be stored for future reference in this specification.
     * 
     * @param pattern
     *            the pattern definition to start from
     * @param errorMessage
     *            an error message to fill the erroneous specification
     * @param storeInMaps
     *            if true, all future references for this query, including references by fqn; if a query is already
     *            stored with this name, an {@link IllegalStateException} is thrown.
     * 
     * @since 2.0
     */
    public IQuerySpecification<?> buildErroneousSpecification(Pattern pattern, String errorMessage, boolean storeInMaps) {
        return doBuildErroneousSpecification(pattern, Stream.of(new PProblem(errorMessage)), storeInMaps);
    }
    
    /**
     * Creates an erroneous query specification from a given pattern object with a stream of precalculated issues. The
     * resulting query specification may or may not be stored for future reference in this specification.
     * 
     * @param pattern
     *            the pattern definition to start from
     * @param issues
     *            a stream of issues that are to be stored in the created specification
     * @param storeInMaps
     *            if true, all future references for this query, including references by fqn; if a query is already
     *            stored with this name, an {@link IllegalStateException} is thrown.
     * 
     * @since 2.0
     */
    public IQuerySpecification<?> buildErroneousSpecification(Pattern pattern, Stream<Issue> issues, boolean storeInMaps) {
        return doBuildErroneousSpecification(pattern, issues.map(ValidationHelper::toPProblem), storeInMaps);
    }
    
    private IQuerySpecification<?> doBuildErroneousSpecification(Pattern pattern, Stream<PProblem> problems, boolean storeInMaps) {
        GenericQuerySpecification erroneousSpecification = new GenericQuerySpecification(new GenericEMFPatternPQuery(pattern, true));
        final GenericEMFPatternPQuery pQuery = erroneousSpecification.getInternalQueryRepresentation();
        problems.forEach(pQuery::addError);
        if (storeInMaps) {
            String fqn = PatternLanguageHelper.getFullyQualifiedName(pattern);
            Preconditions.checkState(!patternMap.containsKey(fqn), "The builder already contains a pattern with the qualified name %s", fqn);
            patternMap.put(fqn, erroneousSpecification);
            patternNameMap.put(fqn, pattern);
        }
        return erroneousSpecification;
    }
    
    protected void buildAnnotations(Pattern pattern, InitializablePQuery query) {
        for (Annotation annotation : pattern.getAnnotations()) {
            PAnnotation pAnnotation = new PAnnotation(annotation.getName());
            for (Entry<String, Object> attribute : 
                PatternLanguageHelper.evaluateAnnotationParametersWithMultiplicity(annotation).entries()) 
            {
                Object value = attribute.getValue();
                if (value instanceof JvmField) {
                    JvmConstantEvaluator constantEvaluator = new JvmConstantEvaluator((JvmField)value, pattern);
                    value = constantEvaluator.evaluateConstantExpression();
                }
                pAnnotation.addAttribute(attribute.getKey(), value);
            }
            query.addAnnotation(pAnnotation);
        }
    }

    /**
     * @throws ViatraQueryRuntimeException
     * @since 2.0
     */
    public Set<PBody> buildBodies(Pattern pattern, InitializablePQuery query) {
        Set<PBody> bodies = getBodies(pattern, query);
        query.initializeBodies(bodies);
        return bodies;
    }

    /**
     * @throws ViatraQueryRuntimeException
     * @since 2.0
     */
    public Set<PBody> getBodies(Pattern pattern, PQuery query) {
        PatternBodyTransformer transformer = new PatternBodyTransformer(pattern);
        Set<PBody> pBodies = new LinkedHashSet<>();
        for (PatternBody body : pattern.getBodies()) {
            EPMToPBody acceptor = new EPMToPBody(pattern, query, patternMap);
            PBody pBody = transformer.transform(body, acceptor);
            pBodies.add(pBody);
        }
        return pBodies;
    }

    /**
     * Returns whether the builder knows a specification with the selected name
     * @since 2.0
     */
    public boolean hasSpecification(Pattern pattern) {
        return patternMap.containsKey(PatternLanguageHelper.getFullyQualifiedName(pattern));
    }
    
    /**
     * Returns whether the builder knows a specification with the selected name
     * @since 2.0
     */
    public boolean hasSpecification(String fqn) {
        return patternMap.containsKey(fqn);
    }
    
    /**
     * @since 2.0
     */
    public Optional<IQuerySpecification<?>> getSpecification(Pattern pattern) {
        return getSpecification(PatternLanguageHelper.getFullyQualifiedName(pattern));
    }

    /**
     * @since 2.0
     */
    public Optional<IQuerySpecification<?>> getSpecification(String fqn) {
        return Optional.ofNullable(patternMap.get(fqn));
    }

    /**
     * Forgets a specification in the builder. </p>
     * <p>
     * <strong>Warning!</strong> Removing a specification does not change any specification created previously, even if
     * they are referring to the old version of the specification. Only use this if you are sure all dependant queries
     * are also removed, otherwise use {@link #forgetSpecificationTransitively(IQuerySpecification)} instead.
     *
     */
    public void forgetSpecification(IQuerySpecification<?> specification) {
        String fqn = specification.getFullyQualifiedName();
        patternMap.remove(fqn);
        if (specification instanceof GenericQuerySpecification) {
            patternNameMap.remove(fqn);
            sanitizer.forgetPattern(((GenericQuerySpecification) specification).getInternalQueryRepresentation().getPattern());
        }
    }

    private void forgetSpecificationTransitively(IQuerySpecification<?> specification,
            Set<IQuerySpecification<?>> forgottenSpecifications) {
        forgetSpecification(specification);
        forgottenSpecifications.add(specification);
        for (IQuerySpecification<?> dependant : dependantQueries.get(specification.getInternalQueryRepresentation())) {
            if (!forgottenSpecifications.contains(dependant)) {
                forgetSpecificationTransitively(dependant, forgottenSpecifications);
            }
        }
        dependantQueries.removeAll(specification.getInternalQueryRepresentation());
    }
    
    /**
     * 
     * Removes {@link IQuerySpecification} objects from the cache that originate from a given URI
     * 
     * @since 2.1
     */
    public void forgetURI(URI uri) {
        //Collect elements to avoid Concurrent modification of patternNameMap
        Set<Pattern> patternsWithUri = patternNameMap.values().stream().filter(pattern -> {
            if(pattern.eResource() != null) {
                return Objects.equals(pattern.eResource().getURI(), uri);
            }
            return true;
        }).collect(Collectors.toSet());
        patternsWithUri.forEach(pattern -> {
            IQuerySpecification<?> specification = patternMap.get(PatternLanguageHelper.getFullyQualifiedName(pattern));
            if(specification!=null) {
                forgetSpecification(specification);
            }
        });
    }

    /**
     * Forgets a specification in the builder, and also removes anything that depends on it.
     *
     * @param specification
     * @returns the set of specifications that were removed from the builder
     */
    public Set<IQuerySpecification<?>> forgetSpecificationTransitively(IQuerySpecification<?> specification) {
        Set<IQuerySpecification<?>> forgottenSpecifications = new HashSet<>();
        forgetSpecificationTransitively(specification, forgottenSpecifications);
        return forgottenSpecifications;
    }
    
    /**
     * Build a {@link QueryEvaluationHint} based on the pattern modifiers and annotations.
     * @since 1.5
     */
    protected QueryEvaluationHint buildHints(Pattern pattern){
        BackendRequirement requirement = BackendRequirement.UNSPECIFIED;
        Modifiers modifiers = pattern.getModifiers();
        if (modifiers != null){
            switch(modifiers.getExecution()){
            case INCREMENTAL:
                requirement = BackendRequirement.DEFAULT_CACHING;
                break;
            case SEARCH:
                requirement = BackendRequirement.DEFAULT_SEARCH;
                break;
            case UNSPECIFIED:
            default:
                requirement = BackendRequirement.UNSPECIFIED;
                break;
            }
        }
        return new QueryEvaluationHint(null, requirement);
    }
}
