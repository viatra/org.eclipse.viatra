/*******************************************************************************
 * Copyright (c) 2010-2018, Peter Lunk, IncQuery Labs Ltd.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-v20.html.
 * 
 * SPDX-License-Identifier: EPL-2.0
 *******************************************************************************/
package org.eclipse.viatra.query.patternlanguage.emf.util;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import org.eclipse.emf.common.util.URI;
import org.eclipse.viatra.query.runtime.api.IQuerySpecification;
import org.eclipse.viatra.query.runtime.matchers.psystem.queries.PQuery.PQueryStatus;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;

/**
 * Contains the results of the {@link AdvancedPatternParser} operations.
 * 
 * @author Peter Lunk
 * @since 2.1
 */
@SuppressWarnings("rawtypes")
public class AdvancedPatternParsingResults {

    private Multimap<URI, IQuerySpecification> uriMap;
    private final Set<IQuerySpecification> addedSpecifications;
    private final Set<IQuerySpecification> updatedSpecifications;
    private final Set<IQuerySpecification> removedSpecifications;
    private final Set<IQuerySpecification> impactedSpecifications;

    protected AdvancedPatternParsingResults() {
        this.addedSpecifications = new HashSet<IQuerySpecification>();
        this.updatedSpecifications = new HashSet<IQuerySpecification>();
        this.removedSpecifications = new HashSet<IQuerySpecification>();
        this.impactedSpecifications = new HashSet<IQuerySpecification>();
        this.uriMap = ArrayListMultimap.create();
    }

    public Multimap<URI, IQuerySpecification> getUriMap() {
        return ArrayListMultimap.create(uriMap);
    }

    /**
     * Returns a {@link Collection} of {@link IQuerySpecification} objects that have been added to the cache.
     * 
     * @return
     */
    public Collection<IQuerySpecification> getAddedSpecifications() {
        return Collections.unmodifiableCollection(addedSpecifications);
    }

    /**
     * Returns a {@link Collection} of {@link IQuerySpecification} objects that have been added to the cache, which
     * originate from the same {@link URI}
     * 
     * @return
     */
    public Collection<IQuerySpecification> getAddedSpecifications(URI uri) {
        Set<IQuerySpecification> temp = new HashSet<IQuerySpecification>(addedSpecifications);
        temp.retainAll(uriMap.get(uri));
        return Collections.unmodifiableCollection(temp);
    }

    /**
     * Returns a {@link Collection} of {@link IQuerySpecification} objects that have been updated via a direct 'update'
     * operation.
     * 
     * @return
     */
    public Collection<IQuerySpecification> getUpdatedSpecifications() {
        return Collections.unmodifiableCollection(updatedSpecifications);
    }

    /**
     * Returns a {@link Collection} of {@link IQuerySpecification} objects that have been updated via a direct 'update'
     * operation, and originate from the same {@link URI}.
     * 
     * @return
     */
    public Collection<IQuerySpecification> getUpdatedSpecifications(URI uri) {
        Set<IQuerySpecification> temp = new HashSet<IQuerySpecification>(updatedSpecifications);
        temp.retainAll(uriMap.get(uri));
        return Collections.unmodifiableCollection(temp);
    }

    /**
     * Returns a {@link Collection} of {@link IQuerySpecification} objects that have been removed from the cache via a
     * direct 'remove' operation.
     * 
     * @return
     */
    public Collection<IQuerySpecification> getRemovedSpecifications() {
        return Collections.unmodifiableCollection(removedSpecifications);
    }

    /**
     * Returns a {@link Collection} of {@link IQuerySpecification} objects that have been removed from the cache via a
     * direct 'remove' operation, and originate from the same {@link URI}
     * 
     * @return
     */
    public Collection<IQuerySpecification> getRemovedSpecifications(URI uri) {
        Set<IQuerySpecification> temp = new HashSet<IQuerySpecification>(removedSpecifications);
        temp.retainAll(uriMap.get(uri));
        return Collections.unmodifiableCollection(temp);
    }

    /**
     * Returns a {@link Collection} of {@link IQuerySpecification} objects that have been updated as a side effect of an
     * 'update', 'add' or 'remove' operation.
     * 
     * @return
     */
    public Collection<IQuerySpecification> getImpactedSpecifications() {
        return Collections.unmodifiableCollection(impactedSpecifications);
    }

    /**
     * Returns a {@link Collection} of {@link IQuerySpecification} objects that have been updated as a side effect of an
     * 'update', 'add' or 'remove' operation, and originate from the same {@link URI}.
     * 
     * @return
     */
    public Collection<IQuerySpecification> getImpactedSpecifications(URI uri) {
        Set<IQuerySpecification> temp = new HashSet<IQuerySpecification>(impactedSpecifications);
        temp.retainAll(uriMap.get(uri));
        return Collections.unmodifiableCollection(temp);
    }

    public Collection<IQuerySpecification> getErroneousSpecifications() {
        return Lists
                .newArrayList(Iterables.concat(addedSpecifications, updatedSpecifications, removedSpecifications,
                        impactedSpecifications))
                .stream().filter(spec -> spec.getInternalQueryRepresentation().getStatus().equals(PQueryStatus.ERROR))
                .collect(Collectors.toList());
    }

    public static class AdvancedPatternParsingResultsBuilder {
        private final Multimap<URI, IQuerySpecification> uriMap = ArrayListMultimap.create();
        private final Set<IQuerySpecification> addedSpecifications = new HashSet<IQuerySpecification>();
        private final Set<IQuerySpecification> updatedSpecifications = new HashSet<IQuerySpecification>();
        private final Set<IQuerySpecification> removedSpecifications = new HashSet<IQuerySpecification>();
        private final Set<IQuerySpecification> impactedSpecifications = new HashSet<IQuerySpecification>();
        private final Set<IQuerySpecification> unaffectedSpecifications = new HashSet<IQuerySpecification>();

        public AdvancedPatternParsingResultsBuilder addAddedSpecification(URI uri, IQuerySpecification spec) {
            addedSpecifications.add(spec);
            uriMap.put(uri, spec);
            return this;
        }

        public AdvancedPatternParsingResultsBuilder addAddedSpecifications(URI uri,
                Collection<IQuerySpecification> specs) {
            addedSpecifications.addAll(specs);
            uriMap.putAll(uri, specs);
            return this;
        }

        public AdvancedPatternParsingResultsBuilder addRemovedSpecification(URI uri, IQuerySpecification spec) {
            removedSpecifications.add(spec);
            uriMap.put(uri, spec);
            return this;
        }

        public AdvancedPatternParsingResultsBuilder addRemovedSpecifications(URI uri,
                Collection<IQuerySpecification> specs) {
            removedSpecifications.addAll(specs);
            uriMap.putAll(uri, specs);
            return this;
        }

        public AdvancedPatternParsingResultsBuilder addUpdatedSpecification(URI uri, IQuerySpecification spec) {
            updatedSpecifications.add(spec);
            uriMap.put(uri, spec);
            return this;
        }

        public AdvancedPatternParsingResultsBuilder addUpdatedSpecifications(URI uri,
                Collection<IQuerySpecification> specs) {
            updatedSpecifications.addAll(specs);
            uriMap.putAll(uri, specs);
            return this;
        }

        public AdvancedPatternParsingResultsBuilder addImpactedSpecification(URI uri, IQuerySpecification spec) {
            impactedSpecifications.add(spec);
            uriMap.put(uri, spec);
            return this;
        }

        public AdvancedPatternParsingResultsBuilder addImpactedSpecifications(URI uri,
                Collection<IQuerySpecification> specs) {
            impactedSpecifications.addAll(specs);
            uriMap.putAll(uri, specs);
            return this;
        }

        public AdvancedPatternParsingResultsBuilder addUnaffectedSpecification(URI uri, IQuerySpecification spec) {
            unaffectedSpecifications.add(spec);
            uriMap.put(uri, spec);
            return this;
        }

        public AdvancedPatternParsingResultsBuilder addUnaffectedSpecifications(URI uri,
                Collection<IQuerySpecification> specs) {
            unaffectedSpecifications.addAll(specs);
            uriMap.putAll(uri, specs);
            return this;
        }

        public AdvancedPatternParsingResults build() {
            AdvancedPatternParsingResults specBuilderSnapshot = new AdvancedPatternParsingResults();
            specBuilderSnapshot.addedSpecifications.addAll(addedSpecifications);
            specBuilderSnapshot.removedSpecifications.addAll(removedSpecifications);
            specBuilderSnapshot.updatedSpecifications.addAll(updatedSpecifications);
            specBuilderSnapshot.impactedSpecifications.addAll(impactedSpecifications);
            specBuilderSnapshot.uriMap = uriMap;
            return specBuilderSnapshot;
        }
    }

}
