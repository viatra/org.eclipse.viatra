/*******************************************************************************
 * Copyright (c) 2010-2018, Peter Lunk, IncQuery Labs Ltd.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-v20.html.
 * 
 * SPDX-License-Identifier: EPL-2.0
 *******************************************************************************/
package org.eclipse.viatra.query.patternlanguage.emf.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.viatra.query.patternlanguage.emf.helper.PatternLanguageHelper;
import org.eclipse.viatra.query.patternlanguage.emf.specification.SpecificationBuilder;
import org.eclipse.viatra.query.patternlanguage.emf.util.AdvancedPatternParsingResults.AdvancedPatternParsingResultsBuilder;
import org.eclipse.viatra.query.patternlanguage.emf.validation.PatternSetValidationDiagnostics;
import org.eclipse.viatra.query.patternlanguage.emf.validation.PatternValidationStatus;
import org.eclipse.viatra.query.patternlanguage.emf.vql.Pattern;
import org.eclipse.viatra.query.patternlanguage.emf.vql.PatternModel;
import org.eclipse.viatra.query.runtime.api.IQuerySpecification;
import org.eclipse.viatra.query.runtime.matchers.util.Preconditions;
import org.eclipse.xtext.resource.IResourceDescription;
import org.eclipse.xtext.resource.IResourceDescription.Delta;
import org.eclipse.xtext.resource.XtextResourceSet;
import org.eclipse.xtext.resource.impl.ResourceDescriptionsData;
import org.eclipse.xtext.util.UriUtil;
import org.eclipse.xtext.validation.Issue;
import org.eclipse.xtext.xbase.resource.BatchLinkableResource;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Multimap;
import com.google.inject.Provider;

/**
 * An updateable, stateful pattern parser that allows the management of complex query libraries with interresource cross
 * references.
 * 
 * 
 * @author Peter Lunk
 * @since 2.1
 *
 */
@SuppressWarnings("restriction")
public class AdvancedPatternParser extends BasePatternParser {

    private static final String UNINITIALIZED_RESOURCESET_MESSAGE = "Resource set was not initialized for the parser.";
    private final Map<URI, String> uriTextMap;
    private final Map<URI, PatternSetValidationDiagnostics> diagnosticsMap;
    private final Multimap<URI, URI> dependencyCache;

    protected AdvancedPatternParser(Set<IQuerySpecification<?>> librarySpecifications, Set<URI> libraryURIs) {
        super(librarySpecifications, libraryURIs);
        uriTextMap = new HashMap<>();
        diagnosticsMap = new HashMap<>();
        dependencyCache = HashMultimap.create();
    }

    @Override
    public void createResourceSet(Provider<XtextResourceSet> resourceSetProvider) {
        super.createResourceSet(resourceSetProvider);

        List<Resource> builtInLibraryResources = ImmutableList.copyOf(resourceSet.getResources());
        updateIndex(resourceSet, builtInLibraryResources);
    }

    protected AdvancedPatternParsingResults addSpecifications(Map<URI, String> input, Map<?, ?> options,
            ResourceSet resourceSet) {
        AdvancedPatternParserSnapshot results = addPatterns(input, options, resourceSet);
        AdvancedPatternParsingResultsBuilder builder = new AdvancedPatternParsingResultsBuilder();
        processSpecifications(results, builder);
        return builder.build();
    }

    protected void processSpecifications(AdvancedPatternParserSnapshot results,
            AdvancedPatternParsingResultsBuilder builder) {
        Set<URI> uris = results.getUriMap().keySet();

        for (URI uri : uris) {
            for (Pattern pattern : results.getRemovedPatterns(uri)) {
                builder.addRemovedSpecification(uri, getOrCreateQuerySpecification(pattern, results));
                removeFromImpactCache(pattern);
            }
        }
        uris.forEach(getOrCreateSpecificationBuilder()::forgetURI);

        for (URI uri : uris) {
            for (Pattern pattern : results.getAddedPatterns(uri)) {
                builder.addAddedSpecification(uri, getOrCreateQuerySpecification(pattern, results));
                updateImpactCache(pattern);
            }
            for (Pattern pattern : results.getUpdatedPatterns(uri)) {
                builder.addUpdatedSpecification(uri, getOrCreateQuerySpecification(pattern, results));
                updateImpactCache(pattern);
            }
            for (Pattern pattern : results.getImpactedPatterns(uri)) {
                builder.addImpactedSpecification(uri, getOrCreateQuerySpecification(pattern, results));
                updateImpactCache(pattern);
            }
        }
    }

    private void removeFromImpactCache(Pattern pattern) {
        PatternLanguageHelper.getReferencedPatternsTransitive(pattern).stream()
                .filter(p -> p.eResource() != null)
                .map(p -> p.eResource().getURI())
                .forEach(u -> dependencyCache.remove(u, pattern));
    }

    private void updateImpactCache(Pattern pattern) {
        dependencyCache.keySet().forEach(uri -> dependencyCache.remove(uri, pattern));
        URI patternResourceURI = pattern.eResource().getURI();
        Set<URI> referredUris = PatternLanguageHelper.getReferencedPatternsTransitive(pattern).stream()
                .map(p -> p.eResource().getURI())
                .filter(uri -> !Objects.equals(uri, patternResourceURI))
                .collect(Collectors.toSet());
        referredUris.forEach(u -> dependencyCache.put(u, patternResourceURI));
    }

    protected AdvancedPatternParsingResults updateSpecifications(Map<URI, String> input, Map<?, ?> options,
            ResourceSet resourceSet) {
        AdvancedPatternParserSnapshot results = updatePatterns(input, options, resourceSet);
        AdvancedPatternParsingResultsBuilder builder = new AdvancedPatternParsingResultsBuilder();
        processSpecifications(results, builder);
        return builder.build();
    }

    protected AdvancedPatternParsingResults removeSpecifications(Map<URI, String> input, Map<?, ?> options,
            ResourceSet resourceSet) {
        AdvancedPatternParserSnapshot results = removePatterns(input, options, resourceSet);
        AdvancedPatternParsingResultsBuilder builder = new AdvancedPatternParsingResultsBuilder();
        processSpecifications(results, builder);
        return builder.build();
    }

    protected AdvancedPatternParsingResults addSpecifications(Map<URI, String> input, ResourceSet resourceSet) {
        return addSpecifications(input, null, resourceSet);
    }

    protected AdvancedPatternParsingResults updateSpecifications(Map<URI, String> input, ResourceSet resourceSet) {
        return updateSpecifications(input, null, resourceSet);
    }

    protected AdvancedPatternParsingResults removeSpecifications(Map<URI, String> input, ResourceSet resourceSet) {
        return removeSpecifications(input, null, resourceSet);
    }

    /**
     * Parses the input as if they were multiple .vql files, and caches the contained queries for further use. The
     * produced query specifications can be later reused via referring their fully qualified names.
     * 
     * 
     * @param input
     *            Map containing the input in textual form. Each synthetic .vql file must have a unique URI that can be
     *            used to identify its contents.
     * @throws IllegalStateException
     *             if the input contains {@link URI} that has already been added.
     * @return {@link AdvancedPatternParsingResults} that contains the created {@link IQuerySpecification} objects.
     */
    public AdvancedPatternParsingResults addSpecifications(Map<URI, String> input) {
        return addSpecifications(input, null, resourceSet);
    }

    public AdvancedPatternParsingResults addSpecifications(URI uriToUse, String text) {
        return addSpecifications(Collections.singletonMap(uriToUse, text));
    }

    /**
     * Parses the input as if they were multiple .vql files, and updates the cache based on the results. Also updates
     * any impacted, already existing {@link IQuerySpecification} objects. The produced query specifications can be
     * later reused via referring their fully qualified names.
     * 
     * @param input
     *            Map containing the input in textual form. Each synthetic .vql file must have a unique URI that can be
     *            used to identify its contents.
     * @throws IllegalStateException
     *             if the input contains {@link URI} that has not yet been added to the cache.
     * @return {@link AdvancedPatternParsingResults} that contains the updated, and affected {@link IQuerySpecification}
     *         objects.
     */
    public AdvancedPatternParsingResults updateSpecifications(Map<URI, String> input) {
        return updateSpecifications(input, null, resourceSet);
    }

    public AdvancedPatternParsingResults updateSpecifications(URI uriToUse, String text) {
        return updateSpecifications(Collections.singletonMap(uriToUse, text));
    }

    /**
     * Removes the patterns provided in the input from the cache. Also updates any impacted, already existing
     * {@link IQuerySpecification} objects.
     * 
     * @param input
     *            Map containing the input in textual form. Each synthetic .vql file must have a unique URI that can be
     *            used to identify its contents.
     * @throws IllegalStateException
     *             if the input contains {@link URI} that has not yet been added to the cache.
     * @return {@link AdvancedPatternParsingResults} that contains removed, and affected {@link IQuerySpecification}
     *         objects.
     */
    public AdvancedPatternParsingResults removeSpecifications(Map<URI, String> input) {
        return removeSpecifications(input, null, resourceSet);
    }

    public AdvancedPatternParsingResults removeSpecifications(URI uriToUse, String text) {
        return removeSpecifications(Collections.singletonMap(uriToUse, text));
    }

    /**
     * Returns a collection of {@link URI}s that have been previously registered.
     */
    public Collection<URI> getRegisteredURIs() {
        return Collections.unmodifiableCollection(uriTextMap.keySet());
    }

    public void reset() {
        if(builder!=null) {
            builder = null;
        }
        final List<Resource> resourceList = uriTextMap.keySet().stream()
                .map(uri -> resourceSet.getResource(uri, false))
                .filter(Objects::nonNull)
                .filter(resource -> resource.isLoaded())
                .collect(Collectors.toList());
        Resource[] resourcesToRemove = resourceList.toArray(new Resource[resourceList.size()]);
        removeResource(resourceSet, resourcesToRemove);
        uriTextMap.clear();
        diagnosticsMap.clear();
        dependencyCache.clear();
    }
    
    private void removeResource(ResourceSet resourceSet, Resource... resources) {
        ResourceDescriptionsData resourceDescriptionsData = ResourceDescriptionsData.ResourceSetAdapter
                .findResourceDescriptionsData(resourceSet);
        if (resourceDescriptionsData != null) {
            for (Resource resource : resources) {
                resource.getContents().clear();
    
                IResourceDescription description = manager.getResourceDescription(resource);
                Delta delta = manager.createDelta(resourceDescriptionsData.getResourceDescription(resource.getURI()),
                        description);
                resourceDescriptionsData.register(delta);
                resourceSet.getResources().remove(resource);
                uriTextMap.remove(resource.getURI());
            }
        }
    }

    private IQuerySpecification<?> getOrCreateQuerySpecification(Pattern pattern, AdvancedPatternParserSnapshot results) {
        List<Issue> errors = results.getErrors(pattern);
        if (errors.isEmpty()) {
            return getOrCreateSpecificationBuilder().getOrCreateSpecification(pattern, false);
        } else {
            return getOrCreateSpecificationBuilder().buildErroneousSpecification(pattern, errors.stream(), false);
        }
    }

    protected AdvancedPatternParserSnapshot updatePatterns(Map<URI, String> input, Map<?, ?> options,
            ResourceSet resourceSet) {
        Preconditions.checkState(resourceSet != null, UNINITIALIZED_RESOURCESET_MESSAGE);
        Set<URI> uris = new HashSet<>(input.keySet());
        uris.removeAll(uriTextMap.keySet());
        Preconditions.checkState(uris.isEmpty(), "The following URIs have not been initialized yet: " + uris);

        AdvancedPatternParserSnapshot.Builder builder = AdvancedPatternParserSnapshot.Builder
                .on(getOrCreateSpecificationBuilder());
        Set<URI> impact = calculateImpact(input.keySet());
        impact.addAll(getErroneousPatternResources(resourceSet, input.keySet()));

        Map<URI, PatternParsingResults> updatedResults = parseBatch(input, options, resourceSet);
        updatedResults.keySet().forEach(key -> builder.updatedPatternResults(key, updatedResults.get(key)));

        reparsePatternImpact(options, resourceSet, builder, impact);

        return builder.build();
    }

    protected AdvancedPatternParserSnapshot addPatterns(Map<URI, String> input, Map<?, ?> options,
            ResourceSet resourceSet) {
        Preconditions.checkState(resourceSet != null, UNINITIALIZED_RESOURCESET_MESSAGE);
        Set<URI> uris = new HashMap<URI, String>(uriTextMap).keySet();
        uris.retainAll(input.keySet());
        Preconditions.checkState(uris.isEmpty(), "The following URIs are already in use: " + uris);

        AdvancedPatternParserSnapshot.Builder builder = AdvancedPatternParserSnapshot.Builder
                .on(getOrCreateSpecificationBuilder());
        Set<URI> impact = getErroneousPatternResources(resourceSet, Collections.emptySet());

        Map<URI, PatternParsingResults> addedResults = parseBatch(input, options, resourceSet);
        addedResults.keySet().forEach(key -> builder.addedPatternResults(key, addedResults.get(key)));

        reparsePatternImpact(options, resourceSet, builder, impact);

        return builder.build();
    }

    protected AdvancedPatternParserSnapshot removePatterns(Map<URI, String> input, Map<?, ?> options,
            ResourceSet resourceSet) {
        // Check preconditions
        Preconditions.checkState(resourceSet != null, UNINITIALIZED_RESOURCESET_MESSAGE);
        Set<URI> uris = new HashMap<URI, String>(input).keySet();
        uris.removeAll(uriTextMap.keySet());
        Preconditions.checkState(uris.isEmpty(), "The following URIs have not been initialized yet: " + uris);
        AdvancedPatternParserSnapshot.Builder builder = AdvancedPatternParserSnapshot.Builder
                .on(getOrCreateSpecificationBuilder());
        Set<URI> impact = calculateImpact(input.keySet());

        input.keySet()
            .stream()
            .map(uri -> resourceSet.getResource(uri, false))
            .filter(Objects::nonNull)
            .forEach(resource -> {
                    if (resource instanceof BatchLinkableResource) {
                        List<Pattern> patterns = streamPatternsInResource(resource).collect(Collectors.toList());
						PatternSetValidationDiagnostics diagnostics = diagnosticsMap.get(resource.getURI());

                        builder.removedPatternResults(resource.getURI(),
                                new PatternParsingResults(patterns, diagnostics, getOrCreateSpecificationBuilder()));

                    }
                    removeResource(resourceSet, resource);
					diagnosticsMap.remove(resource.getURI());

                });
        reparsePatternImpact(options, resourceSet, builder, impact);

        return builder.build();
    }

    private Stream<Pattern> streamPatternsInResource(Resource resource) {
        return resource.getContents().stream().filter(PatternModel.class::isInstance)
                .map(pm -> ((PatternModel) pm).getPatterns().stream()).flatMap(Function.identity());
    }

    private void reparsePatternImpact(Map<?, ?> options, ResourceSet resourceSet,
            AdvancedPatternParserSnapshot.Builder builder, Set<URI> impact) {
        if (!impact.isEmpty()) {
            Set<Resource> resources = impact.stream().map(uri -> resourceSet.getResource(uri, true)).collect(Collectors.toSet());

            resourceSet.getResources().removeAll(resources);

            Map<URI, String> textReprMap = resources.stream()
            		.filter(res -> uriTextMap.containsKey(res.getURI()))
                    .collect(Collectors.toMap(Resource::getURI, res -> uriTextMap.get(res.getURI()), (a, b) -> b));

            Map<URI, PatternParsingResults> results = parseBatch(textReprMap, options, resourceSet);
            results.keySet().forEach(key -> builder.impactedPatternResults(key, results.get(key)));
        }
    }

    /**
     * @deprecated use {@link #calculateImpact(Set)} instead, the resource set parameter is unnecessary
     */
    @Deprecated
    protected Set<URI> calculateImpact(Set<URI> input, ResourceSet rs) {
        return calculateImpact(input);
    }
    /**
     * @since 2.2
     */
    protected Set<URI> calculateImpact(Set<URI> input) {
        return input.stream()
                .map(dependencyCache::get)
                .flatMap(Collection::stream)
                .filter(uri -> !input.contains(uri))
                .collect(Collectors.toSet());
    }

    protected Map<URI, PatternParsingResults> parseBatch(Map<URI, String> input, Map<?, ?> options,
            ResourceSet resourceSet) {
        List<Resource> resources = new ArrayList<>();
        Multimap<URI, Pattern> existingPatternMap = HashMultimap.create();
        input.entrySet().forEach(entry -> {
            URI uri = entry.getKey();
            resourceSet.getResources().stream()
                    .filter(res -> Objects.equals(uri, res.getURI()))
                    .findFirst()
                    .ifPresent(resource -> {
                        existingPatternMap.putAll(uri, () -> streamPatternsInResource(resource).iterator());
                        removeResource(resourceSet, resource);
                    });
            String text = entry.getValue();
            Resource resource = resource(getAsStream(text), uri, options, resourceSet);
            uriTextMap.put(uri, text);
            resources.add(resource);
        });
        
        // Before validation the Xtext index needs to be updated with this content
        updateIndex(resourceSet, resources);
        
        // Validate all resources and collect all the patterns and diagnostics
        Map<URI, PatternParsingResults> results = new HashMap<>();
        for (Resource resource : resources) {
            List<Pattern> patterns = new ArrayList<>();
            Map<String, Pattern> missingPatterns = existingPatternMap.get(resource.getURI()).stream().collect(Collectors.toMap(Pattern::getName, Function.identity()));
            PatternSetValidationDiagnostics diagnostics = validator.validate(resource);
            diagnosticsMap.put(resource.getURI(), diagnostics);

            for (EObject eObject : resource.getContents()) {
                if (eObject instanceof PatternModel) {
                    for (Pattern pattern : ((PatternModel) eObject).getPatterns()) {
                        patterns.add(pattern);
                        missingPatterns.remove(pattern.getName());
                    }
                }
            }

            results.put(resource.getURI(),
                    new PatternParsingResults(patterns, missingPatterns.values(), diagnostics, getOrCreateSpecificationBuilder()));
        }

        return results;
    }

    private void updateIndex(ResourceSet resourceSet, List<Resource> resources) {
        for (Resource resource : resources) {
            ResourceDescriptionsData resourceDescriptionsData = ResourceDescriptionsData.ResourceSetAdapter
                    .findResourceDescriptionsData(resourceSet);
            if (resourceDescriptionsData == null) {
                resourceDescriptionsData = new ResourceDescriptionsData(new ArrayList<IResourceDescription>());
                ResourceDescriptionsData.ResourceSetAdapter.installResourceDescriptionsData(resourceSet,
                        resourceDescriptionsData);
            }

            addDeltaToIndex(resource.getURI(), resource, resourceDescriptionsData);
        }
    }

    private void addDeltaToIndex(URI uri, Resource resource, ResourceDescriptionsData index) {
        IResourceDescription description = manager.getResourceDescription(resource);
        Delta delta = manager.createDelta(index.getResourceDescription(uri), description);
        index.register(delta);
    }

    /**
     * @since 2.2
     */
    protected Set<URI> getErroneousPatternResources(ResourceSet resourceSet, Set<URI> urisToIgnore) {
        Set<URI> uris = new ArrayList<>(resourceSet.getResources()).stream()
        		.filter(BatchLinkableResource.class::isInstance)
        		.filter(res -> !urisToIgnore.contains(res.getURI()))
				.filter(res -> res.getContents().stream().anyMatch(PatternModel.class::isInstance))
				.filter(res -> diagnosticsMap.containsKey(res.getURI()) && diagnosticsMap.get(res.getURI()).getStatus() == PatternValidationStatus.ERROR)
				.map(res -> res.getURI())
				.collect(Collectors.toSet());
        uris.addAll(calculateImpact(uris));
        return uris;
    }

    /**
     * @since 2.2
     */
    public Set<Issue> getErrors(URI uri) {
    	return Optional.ofNullable(diagnosticsMap.get(uri))
    			.map(t -> t.getAllErrors().stream()
    					.filter(issue -> issue.getUriToProblem() != null )
    					// Model inference errors are not problematic during interpretative pattern matching
    					.filter(issue -> !Objects.equals(issue.getCode(), IErrorFeedback.JVMINFERENCE_ERROR_TYPE))
    					.filter(issue -> Objects.equals(uri, issue.getUriToProblem().trimFragment()))
    					.collect(Collectors.toSet()))
    			.orElse(Collections.emptySet());
    }

    public static class AdvancedPatternParserSnapshot {
        private PatternSetValidationDiagnostics diag;
        private final SpecificationBuilder builder;

        private Multimap<URI, Pattern> uriMap;
        private final Set<Pattern> addedPatterns;
        private final Set<Pattern> updatedPatterns;
        private final Set<Pattern> removedPatterns;
        private final Set<Pattern> impactedPatterns;

        protected AdvancedPatternParserSnapshot(SpecificationBuilder builder) {
            this.builder = builder;
            this.addedPatterns = new HashSet<Pattern>();
            this.updatedPatterns = new HashSet<Pattern>();
            this.removedPatterns = new HashSet<Pattern>();
            this.impactedPatterns = new HashSet<Pattern>();
            this.uriMap = ArrayListMultimap.create();
        }

        public SpecificationBuilder getBuilder() {
            return builder;
        }

        public Multimap<URI, Pattern> getUriMap() {
            return ArrayListMultimap.create(uriMap);
        }

        public Collection<Pattern> getAddedPatterns() {
            return Collections.unmodifiableCollection(addedPatterns);
        }

        public Collection<Pattern> getAddedPatterns(URI uri) {
            HashSet<Pattern> addedTemp = new HashSet<Pattern>(addedPatterns);
            addedTemp.retainAll(uriMap.get(uri));
            return Collections.unmodifiableCollection(addedTemp);
        }

        public Collection<Pattern> getUpdatedPatterns() {
            return Collections.unmodifiableCollection(updatedPatterns);
        }

        public Collection<Pattern> getUpdatedPatterns(URI uri) {
            HashSet<Pattern> updatedTemp = new HashSet<Pattern>(updatedPatterns);
            updatedTemp.retainAll(uriMap.get(uri));
            return Collections.unmodifiableCollection(updatedTemp);
        }

        public Collection<Pattern> getRemovedPatterns() {
            return Collections.unmodifiableCollection(removedPatterns);
        }

        public Collection<Pattern> getRemovedPatterns(URI uri) {
            HashSet<Pattern> temp = new HashSet<Pattern>(removedPatterns);
            temp.retainAll(uriMap.get(uri));
            return Collections.unmodifiableCollection(temp);
        }

        public Collection<Pattern> getImpactedPatterns() {
            return Collections.unmodifiableCollection(impactedPatterns);
        }

        public Collection<Pattern> getImpactedPatterns(URI uri) {
            HashSet<Pattern> temp = new HashSet<Pattern>(impactedPatterns);
            temp.retainAll(uriMap.get(uri));
            return Collections.unmodifiableCollection(temp);
        }

        public Collection<Pattern> getErroneousPatterns() {
            return getAllPatterns().stream().filter(pattern -> !getErrors(pattern).isEmpty())
                    .collect(Collectors.toList());
        }

        public Collection<Pattern> getErroneousPatterns(URI uri) {
            HashSet<Pattern> temp = new HashSet<Pattern>(getErroneousPatterns());
            temp.retainAll(uriMap.get(uri));
            return Collections.unmodifiableCollection(temp);
        }

        public Collection<Pattern> getAllPatterns() {
            Set<Pattern> result = new HashSet<>();
            result.addAll(addedPatterns);
            result.addAll(removedPatterns);
            result.addAll(updatedPatterns);
            result.addAll(impactedPatterns);
            return result;
        }

        public boolean hasWarning() {
            return !diag.getAllWarnings().isEmpty();
        }

        public boolean hasError() {
            return !diag.getAllErrors().isEmpty();
        }

        public Iterable<Issue> getAllDiagnostics() {
            return Stream.concat(diag.getAllErrors().stream(), diag.getAllWarnings().stream())
                    .collect(Collectors.toList());
        }

        /**
         * @since 2.2
         */
        public List<Issue> getErrors(URI uri) {
			return diag.getAllErrors().stream().filter(issue -> UriUtil.isPrefixOf(uri, issue.getUriToProblem()))
					.collect(Collectors.toList());
        }
        
        public List<Issue> getErrors(Pattern pattern) {
            Preconditions.checkArgument(getAllPatterns().contains(pattern),
                    "The referenced pattern %s is not parsed by the builder.", pattern.getName());
            final Resource resource = pattern.eResource();
            if (resource == null) {
                return new ArrayList<>();
            }
            final ResourceSet rs = resource.getResourceSet();
            if (rs == null) {
                return new ArrayList<>();
            }

            return diag.getAllErrors().stream()
                    .filter(issue -> EcoreUtil.isAncestor(pattern, rs.getEObject(issue.getUriToProblem(), false)))
                    .collect(Collectors.toList());
        }

        private void setDiagnostics(PatternSetValidationDiagnostics diag) {
            this.diag = diag;
        }

        public static class Builder {
            private final SpecificationBuilder specificationBuilder;
            private final Multimap<URI, Pattern> uriMap = ArrayListMultimap.create();
            private final Set<PatternParsingResults> addedPatterns = new HashSet<PatternParsingResults>();
            private final Set<PatternParsingResults> updatedPatterns = new HashSet<PatternParsingResults>();
            private final Set<PatternParsingResults> removedPatterns = new HashSet<PatternParsingResults>();
            private final Set<PatternParsingResults> impactedPatterns = new HashSet<PatternParsingResults>();

            private Builder(SpecificationBuilder builder) {
                this.specificationBuilder = builder;
            }

            public static Builder on(SpecificationBuilder builder) {
                return new Builder(builder);
            }

            public Builder addedPatternResults(URI uri, PatternParsingResults results) {
                addedPatterns.add(results);
                uriMap.putAll(uri, results.getPatterns());
                uriMap.putAll(uri, results.getRemovedPatterns());
                return this;
            }

            public Builder removedPatternResults(URI uri, PatternParsingResults results) {
                removedPatterns.add(results);
                uriMap.putAll(uri, results.getPatterns());
                uriMap.putAll(uri, results.getRemovedPatterns());
                return this;
            }

            public Builder updatedPatternResults(URI uri, PatternParsingResults results) {
                updatedPatterns.add(results);
                uriMap.putAll(uri, results.getPatterns());
                uriMap.putAll(uri, results.getRemovedPatterns());
                return this;
            }

            public Builder impactedPatternResults(URI uri, PatternParsingResults results) {
                impactedPatterns.add(results);
                uriMap.putAll(uri, results.getPatterns());
                uriMap.putAll(uri, results.getRemovedPatterns());
                return this;
            }

            /**
             * @deprecated This method was introduced by error, all its calls are ignored
             */
            @Deprecated
            public Builder unaffectedPatterns(URI uri, Collection<Pattern> results) {
                return this;
            }

            public AdvancedPatternParserSnapshot build() {
                AdvancedPatternParserSnapshot patternParserSnapshot = new AdvancedPatternParserSnapshot(specificationBuilder);
                List<Issue> issues = new ArrayList<Issue>();

                addedPatterns.forEach(result -> {
                    result.getAllDiagnostics().forEach(issues::add);
                    result.getPatterns().forEach(patternParserSnapshot.addedPatterns::add);
                    result.getRemovedPatterns().forEach(patternParserSnapshot.removedPatterns::add);
                });

                updatedPatterns.forEach(result -> {
                    result.getAllDiagnostics().forEach(issues::add);
                    result.getPatterns().forEach(patternParserSnapshot.updatedPatterns::add);
                    result.getRemovedPatterns().forEach(patternParserSnapshot.removedPatterns::add);
                });

                removedPatterns.forEach(result -> {
                    result.getAllDiagnostics().forEach(issues::add);
                    result.getPatterns().forEach(patternParserSnapshot.removedPatterns::add);
                    result.getRemovedPatterns().forEach(patternParserSnapshot.removedPatterns::add);
                });

                impactedPatterns.forEach(result -> {
                    result.getAllDiagnostics().forEach(issues::add);
                    result.getPatterns().forEach(patternParserSnapshot.impactedPatterns::add);
                    result.getRemovedPatterns().forEach(patternParserSnapshot.removedPatterns::add);
                });

                patternParserSnapshot.uriMap = uriMap;

                PatternSetValidationDiagnostics diag = new PatternSetValidationDiagnostics();
                issues.forEach(diag::accept);

                patternParserSnapshot.setDiagnostics(diag);

                return patternParserSnapshot;
            }
        }
    }
}
