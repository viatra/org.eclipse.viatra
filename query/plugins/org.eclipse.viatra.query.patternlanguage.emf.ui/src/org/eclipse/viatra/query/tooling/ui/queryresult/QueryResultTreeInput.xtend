/*******************************************************************************
 * Copyright (c) 2010-2016, Abel Hegedus, IncQuery Labs Ltd.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-v20.html.
 * 
 * SPDX-License-Identifier: EPL-2.0
 *******************************************************************************/
package org.eclipse.viatra.query.tooling.ui.queryresult

import com.google.common.base.Preconditions
import com.google.common.collect.HashBasedTable
import com.google.common.collect.Maps
import com.google.common.collect.Multimap
import com.google.common.collect.Sets
import com.google.common.collect.Table
import com.google.common.collect.TreeMultimap
import java.util.Map
import java.util.Set
import org.eclipse.viatra.query.patternlanguage.emf.specification.SpecificationBuilder
import org.eclipse.viatra.query.runtime.api.AdvancedViatraQueryEngine
import org.eclipse.viatra.query.runtime.api.IPatternMatch
import org.eclipse.viatra.query.runtime.api.IQuerySpecification
import org.eclipse.viatra.query.runtime.api.ViatraQueryEngineLifecycleListener
import org.eclipse.viatra.query.runtime.api.ViatraQueryMatcher
import org.eclipse.viatra.query.runtime.emf.EMFScope
import org.eclipse.viatra.query.runtime.extensibility.IQuerySpecificationProvider
import org.eclipse.viatra.query.runtime.matchers.backend.QueryEvaluationHint
import org.eclipse.viatra.query.runtime.registry.IQuerySpecificationRegistry
import org.eclipse.viatra.query.runtime.registry.IQuerySpecificationRegistryChangeListener
import org.eclipse.viatra.query.runtime.registry.IQuerySpecificationRegistryEntry
import org.eclipse.viatra.query.runtime.registry.IRegistryView
import org.eclipse.viatra.query.runtime.registry.view.AbstractRegistryView
import org.eclipse.viatra.query.tooling.ui.queryregistry.index.IPatternBasedSpecificationProvider
import org.eclipse.viatra.transformation.evm.api.ExecutionSchema
import org.eclipse.viatra.transformation.evm.api.RuleSpecification
import org.eclipse.viatra.transformation.evm.specific.ExecutionSchemas
import org.eclipse.viatra.transformation.evm.specific.Jobs
import org.eclipse.viatra.transformation.evm.specific.Lifecycles
import org.eclipse.viatra.transformation.evm.specific.Rules
import org.eclipse.viatra.transformation.evm.specific.Schedulers
import org.eclipse.viatra.transformation.evm.specific.crud.CRUDActivationStateEnum
import org.eclipse.viatra.transformation.evm.specific.resolver.InvertedDisappearancePriorityConflictResolver
import org.eclipse.xtend.lib.annotations.Accessors
import org.eclipse.xtend.lib.annotations.FinalFieldsConstructor
import org.eclipse.viatra.query.tooling.ui.util.IFilteredMatcherContent
import org.eclipse.viatra.query.tooling.ui.util.IFilteredMatcherCollection
import org.eclipse.viatra.query.runtime.ui.modelconnector.IModelConnector
import org.eclipse.viatra.query.runtime.matchers.psystem.queries.PQuery.PQueryStatus
import org.eclipse.viatra.query.tooling.ui.preferences.RuntimePreferencesInterpreter
import org.eclipse.viatra.query.patternlanguage.emf.ui.EMFPatternLanguageUIPlugin

/**
 * @author Abel Hegedus
 *
 */
class QueryResultTreeInput implements IFilteredMatcherCollection {
    
    @Accessors(PUBLIC_GETTER)
    AdvancedViatraQueryEngine engine
    
    @Accessors(PUBLIC_GETTER)
    boolean readOnlyEngine
    
    @Accessors(PUBLIC_GETTER)
    boolean engineOperational
    
    @Accessors(PUBLIC_GETTER)
    Map<String, QueryResultTreeMatcher<?>> matchers
    
    /**
     * @since 1.4
     */
    @Accessors(PUBLIC_GETTER, PROTECTED_SETTER)
    IModelConnector modelConnector
    
    Table<String, String, IQuerySpecificationRegistryEntry> loadedEntries
    Multimap<String, String> knownErrorEntries 
    
    SpecificationBuilder builder
    ExecutionSchema schema
    EngineLifecycleListener lifecycleListener
    RegistryChangeListener registryListener
    IRegistryView view
    
    @Accessors(PUBLIC_GETTER)
    QueryEvaluationHint hintForBackendSelection
    
    Set<IQueryResultViewModelListener> listeners
    
    new(
        AdvancedViatraQueryEngine engine,
        IQuerySpecificationRegistry registry,
        boolean readOnlyEngine,
        QueryEvaluationHint hint
    ) {
        this.engine = engine
        this.engineOperational = true
        this.hintForBackendSelection = hint
        this.readOnlyEngine = readOnlyEngine
        this.matchers = Maps.newTreeMap()
        this.loadedEntries = HashBasedTable.create
        this.knownErrorEntries = TreeMultimap.create
        this.builder = new SpecificationBuilder
        this.listeners = Sets.newHashSet
        
        this.schema = ExecutionSchemas.createViatraQueryExecutionSchema(
            engine,
            Schedulers.getQueryEngineSchedulerFactory(engine),
            new InvertedDisappearancePriorityConflictResolver()
        )
        
        engine.currentMatchers.forEach[
            createMatcher(it)
        ]
        this.schema.startUnscheduledExecution
        
        lifecycleListener = new EngineLifecycleListener(this)
        engine.addLifecycleListener(lifecycleListener)
        
        if(!readOnlyEngine) {
            registryListener = new RegistryChangeListener(this)
            view = registry.createView[
                return new AbstractRegistryView(registry, true) {
                    override protected isEntryRelevant(IQuerySpecificationRegistryEntry entry) {
                        true
                    }
                }
            ]
            view.addViewListener(registryListener)
        }
    }
    
    def setHint(QueryEvaluationHint hint) {
        Preconditions.checkNotNull(hint);
        this.hintForBackendSelection = hint;
    }
    
    def <MATCH extends IPatternMatch> createMatcher(ViatraQueryMatcher<MATCH> matcher) {
        val treeMatcher = new QueryResultTreeMatcher(this, matcher)
        val matchCreatedJob = Jobs.<MATCH>newStatelessJob(CRUDActivationStateEnum.CREATED, [ match |
            listeners.forEach[
                it.matchAdded(treeMatcher, match)
            ]
        ])
        val matchUpdatedJob = Jobs.<MATCH>newStatelessJob(CRUDActivationStateEnum.UPDATED, [ match |
            listeners.forEach[
                it.matchUpdated(treeMatcher, match)
            ]
        ])
        val matchDeletedJob = Jobs.<MATCH>newStatelessJob(CRUDActivationStateEnum.DELETED, [ match |
            listeners.forEach[
                it.matchRemoved(treeMatcher, match)
            ]
        ])
        val ruleSpec = Rules.newMatcherRuleSpecification(
            matcher,
            Lifecycles.getDefault(true, true),
            #{matchCreatedJob, matchUpdatedJob, matchDeletedJob}
        )
        treeMatcher.ruleSpec = ruleSpec
        val fullyQualifiedName = matcher.specification.fullyQualifiedName
        matchers.put(fullyQualifiedName, treeMatcher)
        listeners.forEach[
            it.matcherAdded(treeMatcher)
        ]
        schema.addRule(ruleSpec)
        return treeMatcher
    }
    
    def addMatcherIfLoaded(IQuerySpecificationRegistryEntry entry) {
        if(loadedEntries.contains(entry.sourceIdentifier, entry.fullyQualifiedName)) {
              // TODO handle incremental updates in registry
//            entry.loadQuery
        }
    }

    def removeMatcherIfLoaded(IQuerySpecificationRegistryEntry entry) {
        if(loadedEntries.contains(entry.sourceIdentifier, entry.fullyQualifiedName)) {
              // TODO handle incremental updates in registry
//            entry.removeMatcher
        }
    }
    
    def removeMatcher(IQuerySpecificationRegistryEntry entry) {
        val treeMatcher = matchers.get(entry.fullyQualifiedName)
        if(treeMatcher !== null && treeMatcher.entry.sourceIdentifier == entry.sourceIdentifier) {
            return treeMatcher.removeMatcher
        }
        return null
    }
    
    def removeMatcher(QueryResultTreeMatcher<?> matcher) {
        matchers.remove(matcher.entry.fullyQualifiedName)
        listeners.forEach[
            it.matcherRemoved(matcher)
        ]
        if(matcher.ruleSpec !== null){
            schema.removeRule(matcher.ruleSpec)
        }
        if (matcher.matcher !== null){
            // TODO deal with duplicates and dependencies
            builder.forgetSpecificationTransitively(matcher.matcher.specification)
        } else {
            builder.getSpecification(matcher.entry.fullyQualifiedName).ifPresent[builder.forgetSpecificationTransitively(it)]
        }
        return matcher
    }
    
    def loadQueries(Iterable<IQuerySpecificationRegistryEntry> entries) {
        if(readOnlyEngine){
            throw new UnsupportedOperationException("Cannot load queries to read-only engine")
        }
        builder = new SpecificationBuilder
        val iterator = entries.iterator
        while(engineOperational && iterator.hasNext) {
            val entry = iterator.next
            if(matchers.containsKey(entry.fullyQualifiedName)){
                entry.removeMatcher
            }
            entry.loadQuery
            loadedEntries.put(entry.sourceIdentifier, entry.fullyQualifiedName, entry)
        }
        if (engineOperational) {
            schema.startUnscheduledExecution
        }
    }
    
    private def loadQuery(IQuerySpecificationRegistryEntry entry) {
        if (!engineOperational) {
            entry.addErroneousMatcher(new IllegalStateException("Query engine encountered a fatal error or has been disposed"))
        }
        val entryFQN = entry.fullyQualifiedName
        if(matchers.containsKey(entryFQN)){
            entry.removeMatcher
        }
        try{
            val specification = entry.provider.specificationOfProvider as IQuerySpecification
            if(specification.internalQueryRepresentation.status == PQueryStatus.ERROR){
                entry.addErroneousMatcher(new IllegalArgumentException("Query definition contains errors"))
            } else {
                val currentHint = hintForBackendSelection.overrideBy(RuntimePreferencesInterpreter.getHintOverridesFromPreferences()) 
                val matcher = engine.getMatcher(specification, currentHint)
                val specificationFQN = specification.fullyQualifiedName
                val treeMatcher = matchers.get(specificationFQN)
                if(specificationFQN != entryFQN){
                    // inconsistent state during Xtext index update
                    matchers.remove(specificationFQN)
                    matchers.put(entryFQN, treeMatcher)
                }
                treeMatcher.entry = entry
                treeMatcher.hint = currentHint
                knownErrorEntries.remove(entry.sourceIdentifier, entryFQN)
                return treeMatcher
            }
        } catch (Exception ex) {
            return entry.addErroneousMatcher(ex)
        }
    }
    
    private def addErroneousMatcher(IQuerySpecificationRegistryEntry entry, Exception ex) {
        val entryFQN = entry.fullyQualifiedName
        val treeMatcher = new QueryResultTreeMatcher(this, null)
        treeMatcher.exception = ex
        treeMatcher.entry = entry
        matchers.put(entryFQN, treeMatcher)
        listeners.forEach[
            it.matcherAdded(treeMatcher)
        ]
        if(knownErrorEntries.put(entry.sourceIdentifier, entryFQN)){
            val logMessage = String.format("Query Explorer has encountered an error during evaluation of query %s: %s", entryFQN, ex.message)
            EMFPatternLanguageUIPlugin.instance.logException(logMessage, ex)
        }
        return treeMatcher
    }
    
    private def getSpecificationOfProvider(IQuerySpecificationProvider provider) {
        if(provider instanceof IPatternBasedSpecificationProvider){
            val specification = provider.getSpecification(builder)
            return specification
        } else {
            val specification = provider.get
            return specification
        }
    }
    
    def boolean addListener(IQueryResultViewModelListener listener) {
        listeners.add(listener)
    }
    
    def boolean removeListener(IQueryResultViewModelListener listener) {
        listeners.remove(listener)
    }
    
    def dispose() {
        schema?.dispose
        schema = null
        engine?.removeLifecycleListener(lifecycleListener)
        engine = null
        resetMatchers()
        listeners.clear
        view?.removeViewListener(registryListener)
        view = null
    }
    
    protected def void resetInput() {
        matchers.values.forEach[
            if(ruleSpec !== null){
                schema.removeRule(ruleSpec)
            }
        ]
        resetMatchers
        if(!readOnlyEngine) {
            engine.removeLifecycleListener(lifecycleListener)
            engine.wipe
            engine.addLifecycleListener(lifecycleListener)
        }
    }
    
    protected def void resetMatchers() {
        matchers.values.forEach[ matcher |
            listeners.forEach[
                it.matcherRemoved(matcher)
            ]
        ]
        builder = new SpecificationBuilder
        matchers.clear
        loadedEntries.clear
    }
    
    @FinalFieldsConstructor
    static class RegistryChangeListener implements IQuerySpecificationRegistryChangeListener {
        
        final QueryResultTreeInput input
        
        override entryAdded(IQuerySpecificationRegistryEntry entry) {
            input.addMatcherIfLoaded(entry)
        }
        
        override entryRemoved(IQuerySpecificationRegistryEntry entry) {
            input.removeMatcherIfLoaded(entry)
        }
        
    }
    
    @FinalFieldsConstructor
    static class EngineLifecycleListener implements ViatraQueryEngineLifecycleListener {

        final QueryResultTreeInput input

        override engineBecameTainted(String message, Throwable t) {
            input.engineOperational = false
            input.dispose
        }

        override engineDisposed() {
            input.engineOperational = false
            input.dispose
        }

        override engineWiped() {
            input.resetInput
        }

        override matcherInstantiated(ViatraQueryMatcher<? extends IPatternMatch> matcher) {
            input.createMatcher(matcher)
        }
    }
    
    def getBaseIndexOptions() {
        val emfScope = engine.scope as EMFScope
        return emfScope.options
    }
    
    override getFilteredMatchers() {
        return matchers.values.filter(IFilteredMatcherContent).map[Object matcher | matcher as IFilteredMatcherContent<?>]
    }
    
    def matcherFilterUpdated(QueryResultTreeMatcher<?> matcher) {
        listeners.forEach[
            it.matcherFilterUpdated(matcher)
        ]
    }
}

/**
 * @author Abel Hegedus
 */
@FinalFieldsConstructor
class QueryResultTreeMatcher <MATCH extends IPatternMatch> implements IFilteredMatcherContent<MATCH> {
    
    @Accessors(PUBLIC_GETTER)
    final QueryResultTreeInput parent
    
    @Accessors(PUBLIC_GETTER)
    final ViatraQueryMatcher<MATCH> matcher
    
    @Accessors(PROTECTED_SETTER)
    MATCH filterMatch

    @Accessors(PUBLIC_GETTER, PROTECTED_SETTER)
    QueryEvaluationHint hint
    
    @Accessors(PUBLIC_GETTER, PROTECTED_SETTER)
    IQuerySpecificationRegistryEntry entry
    
    @Accessors(PUBLIC_GETTER, PROTECTED_SETTER)
    RuleSpecification<MATCH> ruleSpec
    
    @Accessors(PUBLIC_GETTER, PROTECTED_SETTER)
    Exception exception
    
    @Accessors(PUBLIC_GETTER, PROTECTED_SETTER)
    int matchCount
    
    override getFilterMatch() {
        if(filterMatch === null) {
            filterMatch = matcher.newEmptyMatch
        }
        return filterMatch
    }
    
    def resetFilter() {
        filterMatch = matcher.newEmptyMatch
        filterMatch.filterUpdated
    }
    
    def filterUpdated(MATCH filterMatch) {
        if(filterMatch !== this.filterMatch){
            this.filterMatch = filterMatch
        }
        parent.matcherFilterUpdated(this)
    }
    
    def isFiltered() {
        return getFilterMatch.toArray.exists[it !== null]
    }
    
    def remove() {
        parent.removeMatcher(this)
    }
}

/**
 * @author Abel Hegedus
 */
interface IQueryResultViewModelListener {
    
    def void matcherAdded(QueryResultTreeMatcher<?> matcher)
    
    def void matcherFilterUpdated(QueryResultTreeMatcher<?> matcher)

    def void matcherRemoved(QueryResultTreeMatcher<?> matcher)
    
    def void matchAdded(QueryResultTreeMatcher<?> matcher, IPatternMatch match)
    
    def void matchUpdated(QueryResultTreeMatcher<?> matcher, IPatternMatch match)
    
    def void matchRemoved(QueryResultTreeMatcher<?> matcher, IPatternMatch match)
}