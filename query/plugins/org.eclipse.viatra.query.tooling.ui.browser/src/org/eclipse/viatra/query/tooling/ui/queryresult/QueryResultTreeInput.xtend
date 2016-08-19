/*******************************************************************************
 * Copyright (c) 2010-2016, Abel Hegedus, IncQuery Labs Ltd.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Abel Hegedus - initial API and implementation
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
import org.eclipse.core.runtime.IStatus
import org.eclipse.core.runtime.Status
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
import org.eclipse.viatra.query.tooling.ui.ViatraQueryGUIPlugin
import org.eclipse.viatra.query.tooling.ui.browser.ViatraQueryToolingBrowserPlugin
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
import org.eclipse.viatra.query.tooling.ui.queryexplorer.IModelConnector
import org.eclipse.viatra.query.runtime.matchers.psystem.queries.PQuery.PQueryStatus

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
    Map<String, QueryResultTreeMatcher> matchers
    
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
    QueryEvaluationHint hint
    
    Set<IQueryResultViewModelListener> listeners
    
    new(
        AdvancedViatraQueryEngine engine,
        IQuerySpecificationRegistry registry,
        boolean readOnlyEngine,
        QueryEvaluationHint hint
    ) {
        this.engine = engine
        this.hint = hint
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
            val treeMatcher = createMatcher(it)
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
        this.hint = hint;
    }
    
    def createMatcher(ViatraQueryMatcher matcher) {
        val treeMatcher = new QueryResultTreeMatcher(this, matcher)
        val matchCreatedJob = Jobs.newStatelessJob(CRUDActivationStateEnum.CREATED, [ match |
            listeners.forEach[
                it.matchAdded(treeMatcher, match)
            ]
        ])
        val matchUpdatedJob = Jobs.newStatelessJob(CRUDActivationStateEnum.UPDATED, [ match |
            listeners.forEach[
                it.matchUpdated(treeMatcher, match)
            ]
        ])
        val matchDeletedJob = Jobs.newStatelessJob(CRUDActivationStateEnum.DELETED, [ match |
            listeners.forEach[
                it.matchRemoved(treeMatcher, match)
            ]
        ])
        val ruleSpec = Rules.newMatcherRuleSpecification(
            matcher as ViatraQueryMatcher<IPatternMatch>,
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
        if(treeMatcher != null && treeMatcher.entry.sourceIdentifier == entry.sourceIdentifier) {
            matchers.remove(entry.fullyQualifiedName)
            listeners.forEach[
                it.matcherRemoved(treeMatcher)
            ]
            if(treeMatcher.ruleSpec != null){
                schema.removeRule(treeMatcher.ruleSpec)
            }
            if(treeMatcher.matcher != null){
                // TODO deal with duplicates and dependencies
                builder.forgetSpecificationTransitively(treeMatcher.matcher.specification)
            } else {
                val spec = builder.getSpecification(treeMatcher.entry.fullyQualifiedName)
                if(spec != null) {
                    builder.forgetSpecificationTransitively(spec)
                }
            }
            return treeMatcher
        }
        return null
    }
    
    def loadQueries(Iterable<IQuerySpecificationRegistryEntry> entries) {
        if(readOnlyEngine){
            throw new UnsupportedOperationException("Cannot load queries to read-only engine")
        }
        builder = new SpecificationBuilder
        entries.forEach[ entry |
            if(matchers.containsKey(entry.fullyQualifiedName)){
                val removedTreeMatcher = entry.removeMatcher
            }
            entry.loadQuery
            loadedEntries.put(entry.sourceIdentifier, entry.fullyQualifiedName, entry)
        ]
        schema.startUnscheduledExecution
    }
    
    private def loadQuery(IQuerySpecificationRegistryEntry entry) {
        val entryFQN = entry.fullyQualifiedName
        if(matchers.containsKey(entryFQN)){
            entry.removeMatcher
        }
        try{
            val specification = entry.provider.specificationOfProvider as IQuerySpecification
            if(specification.internalQueryRepresentation.status == PQueryStatus.ERROR){
                entry.addErroneousMatcher(new IllegalArgumentException("Query definition contains errors"))
            } else {
                val matcher = engine.getMatcher(specification, hint)
                val specificationFQN = specification.fullyQualifiedName
                val treeMatcher = matchers.get(specificationFQN)
                if(specificationFQN != entryFQN){
                    // inconsistent state during Xtext index update
                    matchers.remove(specificationFQN)
                    matchers.put(entryFQN, treeMatcher)
                }
                treeMatcher.entry = entry
                treeMatcher.hint = hint
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
            ViatraQueryToolingBrowserPlugin.getDefault().getLog().log(new Status(
                    IStatus.ERROR, ViatraQueryGUIPlugin.getDefault().getBundle().getSymbolicName(), logMessage, ex));
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
            if(ruleSpec != null){
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
            input.dispose
        }

        override engineDisposed() {
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
        return matchers.values.filter(IFilteredMatcherContent)
    }
    
    def matcherFilterUpdated(QueryResultTreeMatcher matcher) {
        listeners.forEach[
            it.matcherFilterUpdated(matcher)
        ]
    }
}

/**
 * @author Abel Hegedus
 */
@FinalFieldsConstructor
class QueryResultTreeMatcher implements IFilteredMatcherContent {
    
    @Accessors(PUBLIC_GETTER)
    final QueryResultTreeInput parent
    
    @Accessors(PUBLIC_GETTER)
    final ViatraQueryMatcher matcher
    
    @Accessors(PROTECTED_SETTER)
    IPatternMatch filterMatch

    @Accessors(PUBLIC_GETTER, PROTECTED_SETTER)
    QueryEvaluationHint hint
    
    @Accessors(PUBLIC_GETTER, PROTECTED_SETTER)
    IQuerySpecificationRegistryEntry entry
    
    @Accessors(PUBLIC_GETTER, PROTECTED_SETTER)
    RuleSpecification ruleSpec
    
    @Accessors(PUBLIC_GETTER, PROTECTED_SETTER)
    Exception exception;
    
    override def getFilterMatch() {
        if(filterMatch == null) {
            filterMatch = matcher.newEmptyMatch
        }
        return filterMatch
    }
    
    def resetFilter() {
        filterMatch = matcher.newEmptyMatch
        filterMatch.filterUpdated
    }
    
    def filterUpdated(IPatternMatch filterMatch) {
        if(filterMatch !== this.filterMatch){
            this.filterMatch = filterMatch
        }
        parent.matcherFilterUpdated(this)
    }
    
    def isFiltered() {
        return getFilterMatch.toArray.exists[it != null]
    }
}

/**
 * @author Abel Hegedus
 */
interface IQueryResultViewModelListener {
    
    def void matcherAdded(QueryResultTreeMatcher matcher)
    
    def void matcherFilterUpdated(QueryResultTreeMatcher matcher)

    def void matcherRemoved(QueryResultTreeMatcher matcher)
    
    def void matchAdded(QueryResultTreeMatcher matcher, IPatternMatch match)
    
    def void matchUpdated(QueryResultTreeMatcher matcher, IPatternMatch match)
    
    def void matchRemoved(QueryResultTreeMatcher matcher, IPatternMatch match)
}