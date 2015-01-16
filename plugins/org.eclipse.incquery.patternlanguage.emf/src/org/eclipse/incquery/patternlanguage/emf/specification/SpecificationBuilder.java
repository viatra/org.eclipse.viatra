/*******************************************************************************
 * Copyright (c) 2010-2014, Zoltan Ujhelyi, Istvan Rath and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Zoltan Ujhelyi - initial API and implementation
 *******************************************************************************/
package org.eclipse.incquery.patternlanguage.emf.specification;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.eclipse.incquery.patternlanguage.emf.specification.builder.EPMToPBody;
import org.eclipse.incquery.patternlanguage.emf.specification.builder.NameToSpecificationMap;
import org.eclipse.incquery.patternlanguage.emf.specification.builder.PatternSanitizer;
import org.eclipse.incquery.patternlanguage.helper.CorePatternLanguageHelper;
import org.eclipse.incquery.patternlanguage.patternLanguage.Annotation;
import org.eclipse.incquery.patternlanguage.patternLanguage.Pattern;
import org.eclipse.incquery.patternlanguage.patternLanguage.PatternBody;
import org.eclipse.incquery.runtime.api.IPatternMatch;
import org.eclipse.incquery.runtime.api.IQuerySpecification;
import org.eclipse.incquery.runtime.api.IncQueryMatcher;
import org.eclipse.incquery.runtime.emf.EMFPatternMatcherContext;
import org.eclipse.incquery.runtime.exception.IncQueryException;
import org.eclipse.incquery.runtime.matchers.context.IPatternMatcherContext;
import org.eclipse.incquery.runtime.matchers.psystem.InitializablePQuery;
import org.eclipse.incquery.runtime.matchers.psystem.PBody;
import org.eclipse.incquery.runtime.matchers.psystem.annotations.PAnnotation;
import org.eclipse.incquery.runtime.matchers.psystem.queries.PProblem;
import org.eclipse.incquery.runtime.matchers.psystem.queries.PQuery;
import org.eclipse.incquery.runtime.matchers.psystem.queries.PQuery.PQueryStatus;
import org.eclipse.incquery.runtime.matchers.psystem.queries.QueryInitializationException;
import org.eclipse.incquery.runtime.matchers.psystem.rewriters.PBodyNormalizer;
import org.eclipse.incquery.runtime.matchers.psystem.rewriters.RewriterException;

import com.google.common.base.Preconditions;
import com.google.common.base.Predicate;
import com.google.common.base.Supplier;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;
import com.google.common.collect.Sets;

/**
 * An instance class to initialize {@link PBody} instances from {@link Pattern} definitions. A single instance of this
 * builder is used during construction, that maintains the mapping between {@link Pattern} and {@link PQuery} objects,
 * and can be initialized with a pre-defined set of mappings.</p>
 *
 * <p>
 * The SpecificationBuilder is stateful: it stores all previously built specifications, allowing further re-use.
 *
 * @author Zoltan Ujhelyi
 *
 */
public class SpecificationBuilder {

    protected static final PBodyNormalizer NORMALIZER = new PBodyNormalizer(EMFPatternMatcherContext.STATIC_INSTANCE);

    private Logger logger = Logger.getLogger(SpecificationBuilder.class);
    private NameToSpecificationMap patternMap;
    /**
     * This map is used to detect a re-addition of a pattern with a fqn that is used by a previously added pattern.
     */
    private Map<String, Pattern> patternNameMap = new HashMap<String, Pattern>();
    private Multimap<PQuery, IQuerySpecification<?>> dependantQueries = Multimaps.newSetMultimap(
            new HashMap<PQuery, Collection<IQuerySpecification<?>>>(), new Supplier<Set<IQuerySpecification<?>>>() {

                @Override
                public Set<IQuerySpecification<?>> get() {
                    return Sets.newHashSet();
                }
            });
    private IPatternMatcherContext context = new EMFPatternMatcherContext(logger);
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
            Collection<? extends IQuerySpecification<? extends IncQueryMatcher<? extends IPatternMatch>>> specifications) {
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
        for (GenericQuerySpecification spec : Iterables.filter(patternMap.values(), GenericQuerySpecification.class)) {
            patternNameMap.put(spec.getFullyQualifiedName(), spec.getInternalQueryRepresentation().getPattern());
        }
    }

    /**
     * Creates a new or returns an existing query specification for the pattern. It is expected, that the builder will
     * not be called with different patterns having the same fqn over its entire lifecycle.
     *
     * @param pattern
     * @return
     * @throws IncQueryException
     */
    public IQuerySpecification<? extends IncQueryMatcher<? extends IPatternMatch>> getOrCreateSpecification(
            Pattern pattern) throws IncQueryException {
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
     * @return
     * @throws IncQueryException
     */
    public IQuerySpecification<? extends IncQueryMatcher<? extends IPatternMatch>> getOrCreateSpecification(
            Pattern pattern, boolean skipPatternValidation) throws IncQueryException {
        return getOrCreateSpecification(pattern, Lists.<IQuerySpecification<?>>newArrayList(), skipPatternValidation);
    }

    public IQuerySpecification<? extends IncQueryMatcher<? extends IPatternMatch>> getOrCreateSpecification(
            Pattern pattern, List<IQuerySpecification<?>> createdPatternList, boolean skipPatternValidation) throws IncQueryException {
        Preconditions.checkArgument(pattern != null && !pattern.eIsProxy(), "Cannot create specification from a null pattern");
        String fqn = CorePatternLanguageHelper.getFullyQualifiedName(pattern);
        Preconditions.checkArgument(fqn != null && !"".equals(fqn), "Pattern name cannot be empty");
        Preconditions.checkArgument(!patternNameMap.containsKey(fqn) || pattern.equals(patternNameMap.get(fqn)),
                "This builder already contains a different pattern with the fqn %s of the newly added pattern.", fqn);
        IQuerySpecification<?> specification = getSpecification(pattern);
        if (specification == null) {
            try {
				specification = buildSpecification(pattern, skipPatternValidation, createdPatternList);
			} catch (QueryInitializationException e) {
				throw new IncQueryException(e);
			}
        }
        return specification;
    }

    protected IQuerySpecification<?> buildSpecification(Pattern pattern) throws QueryInitializationException {
        return buildSpecification(pattern, false, Lists.<IQuerySpecification<?>>newArrayList());
    }

    protected IQuerySpecification<?> buildSpecification(Pattern pattern, List<IQuerySpecification<?>> newSpecifications) throws QueryInitializationException {
        return buildSpecification(pattern, false, newSpecifications);
    }

    protected IQuerySpecification<?> buildSpecification(Pattern pattern, boolean skipPatternValidation, List<IQuerySpecification<?>> newSpecifications)
            throws QueryInitializationException {
        String fqn = CorePatternLanguageHelper.getFullyQualifiedName(pattern);
        Preconditions.checkArgument(!patternMap.containsKey(fqn), "Builder already stores query with the name of "
                + fqn);
        if (sanitizer.admit(pattern, skipPatternValidation)) {
            Set<Pattern> newPatterns = Sets.newHashSet(Sets.filter(sanitizer.getAdmittedPatterns(),
                    new Predicate<Pattern>() {

                        @Override
                        public boolean apply(Pattern pattern) {
                            final String name = CorePatternLanguageHelper.getFullyQualifiedName(pattern);
                            return !pattern.eIsProxy() && !"".equals(name)
                                   && !patternMap.containsKey(name);
                        }
                    }));
            // Initializing new query specifications
            for (Pattern newPattern : newPatterns) {
                String patternFqn = CorePatternLanguageHelper.getFullyQualifiedName(newPattern);
                GenericQuerySpecification specification = new GenericQuerySpecification(new GenericEMFPQuery(newPattern, true));
                patternMap.put(patternFqn, specification);
                patternNameMap.put(patternFqn, newPattern);
                newSpecifications.add(specification);
            }
            // Updating bodies
            for (Pattern newPattern : newPatterns) {
            	String patternFqn = CorePatternLanguageHelper.getFullyQualifiedName(newPattern);
            	GenericQuerySpecification specification = (GenericQuerySpecification) patternMap.get(patternFqn);
            	try {
                	EPMToPBody converter = new EPMToPBody(newPattern, specification.getInternalQueryRepresentation(), context, patternMap);
                	buildAnnotations(newPattern, specification.getInternalQueryRepresentation(), converter);
                	buildBodies(newPattern, specification.getInternalQueryRepresentation(), converter);
            	} catch (IncQueryException e) {
            		specification.getInternalQueryRepresentation().addError(new PProblem(e, e.getShortMessage()));
                } catch (RewriterException e) {
                	specification.getInternalQueryRepresentation().addError(new PProblem(e, e.getShortMessage()));
                }
                if (!PQueryStatus.ERROR.equals(specification.getInternalQueryRepresentation().getStatus())) {
                    for (PQuery query : specification.getInternalQueryRepresentation().getDirectReferredQueries()) {
                        dependantQueries.put(query, specification);
                    }
                }
            }
        } else {
            for (Pattern rejectedPattern : sanitizer.getRejectedPatterns()) {
                String patternFqn = CorePatternLanguageHelper.getFullyQualifiedName(rejectedPattern);
                if (!patternMap.containsKey(patternFqn)) {
                    GenericQuerySpecification rejected = new GenericQuerySpecification(new GenericEMFPQuery(rejectedPattern, true));
                    for (PProblem problem: sanitizer.getProblemByPattern(rejectedPattern)) 
                    	rejected.getInternalQueryRepresentation().addError(problem);
                    patternMap.put(patternFqn, rejected);
                    patternNameMap.put(patternFqn, rejectedPattern);
                    newSpecifications.add(rejected);
                }
            }
        }
        IQuerySpecification<?> specification = patternMap.get(fqn);
        if (specification == null) {
            GenericQuerySpecification erroneousSpecification = new GenericQuerySpecification(new GenericEMFPQuery(pattern, true));
            erroneousSpecification.getInternalQueryRepresentation().addError( new PProblem("Unable to compile pattern due to an unspecified error") );
            patternMap.put(fqn, erroneousSpecification);
            patternNameMap.put(fqn, pattern);
            newSpecifications.add(erroneousSpecification);
            specification = erroneousSpecification;
        }
        return specification;
    }

    protected void buildAnnotations(Pattern pattern, InitializablePQuery query, EPMToPBody converter)
            throws IncQueryException {
        for (Annotation annotation : pattern.getAnnotations()) {
            PAnnotation pAnnotation = converter.toPAnnotation(annotation);
            query.addAnnotation(pAnnotation);
        }
    }

    public Set<PBody> buildBodies(Pattern pattern, InitializablePQuery query) throws QueryInitializationException {
        return buildBodies(pattern, query, new EPMToPBody(pattern, query, context, patternMap));
    }

    protected Set<PBody> buildBodies(Pattern pattern, InitializablePQuery query, EPMToPBody converter)
            throws QueryInitializationException {
        Set<PBody> bodies = getBodies(pattern, converter);
        query.initializeBodies(bodies);
        return bodies;
    }

    public Set<PBody> getBodies(Pattern pattern, PQuery query) throws QueryInitializationException {
        return getBodies(pattern, new EPMToPBody(pattern, query, context, patternMap));
    }
    
    public Set<PBody> getBodies(Pattern pattern, EPMToPBody converter) throws QueryInitializationException {
        Set<PBody> bodies = Sets.newLinkedHashSet();
        for (PatternBody body : pattern.getBodies()) {
            PBody pBody = converter.toPBody(body);
			bodies.add(NORMALIZER.normalizeBody(pBody));
        }
        return bodies;
    }

    public IQuerySpecification<?> getSpecification(Pattern pattern) {
        String fqn = CorePatternLanguageHelper.getFullyQualifiedName(pattern);
        return getSpecification(fqn);
    }

    public IQuerySpecification<?> getSpecification(String fqn) {
        return patternMap.get(fqn);
    }

    /**
     * Forgets a specification in the builder. </p>
     * <p>
     * <strong>Warning!</strong> Removing a specification does not change any specification created previously, even if
     * they are referring to the old version of the specification. Only use this if you are sure all dependant queries
     * are also removed, otherwise use {@link #forgetSpecificationTransitively(IQuerySpecification)} instead.
     *
     * @param pattern
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
        dependantQueries.removeAll(specification);
    }

    /**
     * Forgets a specification in the builder, and also removes anything that depends on it.
     *
     * @param specification
     * @returns the set of specifications that were removed from the builder
     */
    public Set<IQuerySpecification<?>> forgetSpecificationTransitively(IQuerySpecification<?> specification) {
        Set<IQuerySpecification<?>> forgottenSpecifications = Sets.newHashSet();
        forgetSpecificationTransitively(specification, forgottenSpecifications);
        return forgottenSpecifications;
    }
}
