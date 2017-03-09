/** 
 * Copyright (c) 2010-2015, Balazs Grill, Istvan Rath and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Contributors:
 * Balazs Grill - initial API and implementation
 * Peter Lunk - EMFScope support added
 */
package org.eclipse.viatra.query.testing.core.api

import com.google.common.base.Preconditions
import com.google.common.collect.Maps
import com.google.inject.Injector
import java.util.LinkedList
import java.util.List
import java.util.Map
import org.apache.log4j.Level
import org.eclipse.emf.common.util.URI
import org.eclipse.emf.ecore.EObject
import org.eclipse.viatra.query.patternlanguage.emf.eMFPatternLanguage.PatternModel
import org.eclipse.viatra.query.patternlanguage.emf.specification.SpecificationBuilder
import org.eclipse.viatra.query.runtime.api.IPatternMatch
import org.eclipse.viatra.query.runtime.api.IQueryGroup
import org.eclipse.viatra.query.runtime.api.IQuerySpecification
import org.eclipse.viatra.query.runtime.api.ViatraQueryMatcher
import org.eclipse.viatra.query.runtime.emf.EMFScope
import org.eclipse.viatra.query.runtime.matchers.backend.IQueryBackendFactory
import org.eclipse.viatra.query.runtime.matchers.backend.QueryEvaluationHint
import org.eclipse.viatra.query.runtime.registry.QuerySpecificationRegistry
import org.eclipse.viatra.query.testing.core.IMatchSetModelProvider
import org.eclipse.viatra.query.testing.core.InitializedSnapshotMatchSetModelProvider
import org.eclipse.viatra.query.testing.core.PatternBasedMatchSetModelProvider
import org.eclipse.viatra.query.testing.core.SnapshotMatchSetModelProvider
import org.eclipse.viatra.query.testing.core.ViatraQueryTestCase
import org.eclipse.viatra.query.testing.core.XmiModelUtil
import org.eclipse.viatra.query.testing.core.XmiModelUtil.XmiModelUtilRunningOptionEnum
import org.eclipse.viatra.query.testing.core.internal.DefaultMatchRecordEquivalence
import org.eclipse.viatra.query.testing.snapshot.QuerySnapshot

/**
 * This class defines an API to easily construct test cases. The base conception is to provide
 * a set of match set sources (from a snapshot or from the execution of a pattern) and make assertions
 * on them being equal.
 */
class ViatraQueryTest {

    val ViatraQueryTestCase testCase;
    private val List<IQuerySpecification<? extends ViatraQueryMatcher<? extends IPatternMatch>>> patterns = new LinkedList;
    private Map<String, JavaObjectAccess> accessMap;

    private new() {
        this(Maps.newHashMap)
    }
    
    /**
     * Initializes a {@link ViatraQueryTest} with a map containing {@link JavaObjectAccess} objects for 
     * serialization and deserialization of plain Java types.
     * 
     * @since 1.6
     */
    private new(Map<String, JavaObjectAccess> accessMap) {
        this.accessMap = accessMap
        testCase = new ViatraQueryTestCase(accessMap)
    }
    

    /**
     * Test the specified query
     */
    static def <Match extends IPatternMatch> test(IQuerySpecification<? extends ViatraQueryMatcher<Match>> pattern) {
        new ViatraQueryTest().and(pattern)
    }

    static def test(IQueryGroup patterns) {
        new ViatraQueryTest().and(patterns)
    }
    
    static def <Match extends IPatternMatch> test(IQuerySpecification<? extends ViatraQueryMatcher<Match>> pattern, Map<String, JavaObjectAccess> accessMap) {
        new ViatraQueryTest(accessMap).and(pattern)
    }

    static def test(IQueryGroup patterns, Map<String, JavaObjectAccess> accessMap) {
        new ViatraQueryTest(accessMap).and(patterns)
    }

    static def test() {
        new ViatraQueryTest
    }


    /**
     * Test the specified query
     */
    static def <Match extends IPatternMatch> test(String pattern) {
        test().and(pattern)
    }

    def and(IQueryGroup patterns) {
        patterns.specifications.forEach [
            this.patterns += it as IQuerySpecification<ViatraQueryMatcher<IPatternMatch>>
        ]
        this
    }

    /**
     * Test the given pattern parsed from file
     */
    def and(URI patternModel, Injector injector, String patternName) {
        val resourceSet = XmiModelUtil.prepareXtextResource(injector)
        val resource = resourceSet.getResource(patternModel, true);
        Preconditions.checkState(!resource.contents.empty)
        val patternmodel = resource.contents.get(0)
        Preconditions.checkState(patternmodel instanceof PatternModel)
        val patterns = (patternmodel as PatternModel).patterns.filter[it.name == patternName]
        Preconditions.checkState(patterns.size == 1)
        val builder = new SpecificationBuilder
        this.patterns.add(builder.getOrCreateSpecification(patterns.get(0)))
    }

    def and(IQuerySpecification<? extends ViatraQueryMatcher<? extends IPatternMatch>> pattern) {
        patterns.add(pattern)
        this
    }

    def and(String pattern) {
        val view = QuerySpecificationRegistry.instance.defaultView
        and(view.getEntry(pattern).get as IQuerySpecification<ViatraQueryMatcher<IPatternMatch>>)
    }

    private new(IQuerySpecification<? extends ViatraQueryMatcher<? extends IPatternMatch>> pattern) {
        this()
        this.patterns.add(pattern);
    }
    
    /**
     * Add Java Object Access elements
     * 
     * @since 1.6
     */
    def withClasses(Map<String, JavaObjectAccess> accessmap) {
        this.accessMap = accessmap;
        this
    }

    /**
     * Add match result set with a query initialized using the given hints
     */
    def with(QueryEvaluationHint hint) {
        val modelProvider = new PatternBasedMatchSetModelProvider(hint, accessMap);
        testCase.addMatchSetModelProvider(modelProvider)
        this
    }

    /**
     * Add match result set with a query initialized using the given query backend
     */
    def with(IQueryBackendFactory queryBackendFactory) {
        val QueryEvaluationHint hint = new QueryEvaluationHint(null, queryBackendFactory);
        with(hint)
    }

    /**
     * Add match result set loaded from the given snapshot
     * 
     * @deprecated This method is deprecated, use with(QuerySnapshot ... snapshot) instead to add snapshot providers.
     */
     @Deprecated
    def with(URI snapshotURI) {
        testCase.addMatchSetModelProvider(new SnapshotMatchSetModelProvider(snapshotURI))
        this
    }
    
    /**
     * Add a number of query snapshots to be used as reference.
     * 
     * @since 1.5.2
     */
    def with(QuerySnapshot ... snapshot) {
        testCase.addMatchSetModelProvider(new InitializedSnapshotMatchSetModelProvider(snapshot))
        this
    }

    /**
     * Add match result set loaded from the given snapshot
     */
    def with(String snapshotURI) {
        with(XmiModelUtil::resolvePlatformURI(XmiModelUtilRunningOptionEnum.BOTH, snapshotURI))
    }

    /**
     * Load input model
     * 
     * @deprecated use on(EMFScope scope) instead
     */
     @Deprecated
    def on(URI inputURI) {
        testCase.loadModel(inputURI)
        this
    }

    /**
     * Initialize test using EMF Scope
     * @since 1.5.2
     */
    def on(EMFScope scope) {
        testCase.scope = scope
        this
    }

    /**
     * Execute the given operation on the model. This call will also remove every non-incremental result set.
     */
    def <T extends EObject> modify(Class<T> clazz, (T)=>boolean condition, (T)=>void operation) {
        testCase.modifyModel(clazz, condition, operation)
        this
    }

    /**
     * Execute all queries and check that the result sets are equal, then return itself to continue the testing. This
     * is separated from assertEquals because JUnit requires test methods to return void, therefore a test shall end with
     * assertEquals and this method shall be used where further actions are intended (e.g. incremental scenarios)
     */
    def assertEqualsThen() {
        assertEquals
        this
    }

    /**
     * Assert that there were no log events with higher level than given severity during the execution 
     */
    def assertLogSeverityThreshold(Level severity) {
        testCase.assertLogSeverityThreshold(severity)
    }

    /**
     * Assert that the highest level of log events occurred during execution equals to the given severity
     */
    def assertLogSeverity(Level severity) {
        testCase.assertLogSeverity(severity)
    }

    /**
     * Assert that there were no log events with higher level than given severity during the execution 
     */
    def assertLogSeverityThresholdThen(Level severity) {
        this.assertLogSeverityThreshold(severity)
        this
    }

    /**
     * Assert that the highest level of log events occurred during execution equals to the given severity
     */
    def assertLogSeverityThen(Level severity) {
        this.assertLogSeverity(severity)
        this
    }

    /**
     * Assert that the specified {@link IMatchSetModelProvider} instances produce the same match set snapshots, using a user defined equivalence algorithm.  
     * 
     * @since 1.6
     */
    def assertEquals(MatchRecordEquivalence equivalence) {
        this.assertEquals(Level::INFO, equivalence)
    }
    
    /**
     * Execute all queries and check that the result sets are equal and no error log message was thrown
     * 
     */
    def assertEquals() {
        this.assertEquals(Level::INFO)
    }

    /**
     * Execute all queries and check that the result sets are equal with a selected log level threshold, using a user specified equivalence algorithm.
     * 
     * @since 1.6
     */
    def assertEquals(Level treshold, MatchRecordEquivalence equivalence) {
        patterns.forEach [
            testCase.assertMatchSetsEqual(it as IQuerySpecification<ViatraQueryMatcher<IPatternMatch>>, equivalence)
        ]
        testCase.assertLogSeverityThreshold(treshold)
    }
    
    /**
     * Execute all queries and check that the result sets are equal with a selected log level threshold
     */
    def assertEquals(Level treshold) {
        patterns.forEach [
            testCase.assertMatchSetsEqual(it as IQuerySpecification<ViatraQueryMatcher<IPatternMatch>>, new DefaultMatchRecordEquivalence(accessMap))
        ]
        testCase.assertLogSeverityThreshold(treshold)
    }

    /**
     * Make assumptions that each provided engine and snapshot can provide Match result set for each tested patterns
     */
    def assumeInputs() {
        patterns.forEach [
            testCase.assumeMatchSetsAreAvailable(it as IQuerySpecification<ViatraQueryMatcher<IPatternMatch>>)
        ]
        this
    }

}
