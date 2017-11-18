/** 
 * Copyright (c) 2010-2015, Balazs Grill, Istvan Rath and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Contributors:
 * Balazs Grill - initial API and implementation
 */
package org.eclipse.viatra.query.testing.core

import com.google.common.base.Joiner
import java.util.Iterator
import java.util.LinkedList
import java.util.List
import java.util.Map
import org.apache.log4j.Level
import org.eclipse.emf.common.notify.Notifier
import org.eclipse.emf.common.util.URI
import org.eclipse.emf.ecore.EObject
import org.eclipse.emf.ecore.resource.Resource
import org.eclipse.emf.ecore.resource.ResourceSet
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl
import org.eclipse.viatra.query.runtime.api.IPatternMatch
import org.eclipse.viatra.query.runtime.api.IQueryGroup
import org.eclipse.viatra.query.runtime.api.IQuerySpecification
import org.eclipse.viatra.query.runtime.api.ViatraQueryEngine
import org.eclipse.viatra.query.runtime.api.ViatraQueryMatcher
import org.eclipse.viatra.query.runtime.emf.EMFScope
import org.eclipse.viatra.query.runtime.util.ViatraQueryLoggingUtil
import org.eclipse.viatra.query.testing.core.api.JavaObjectAccess
import org.eclipse.viatra.query.testing.core.api.MatchRecordEquivalence
import org.eclipse.viatra.query.testing.core.internal.DefaultMatchRecordEquivalence
import org.junit.Assert
import org.junit.Assume
import org.junit.ComparisonFailure
import com.google.common.collect.Maps
import java.util.function.Predicate
import java.util.function.Consumer

/** 
 * @author Grill Balazs
 */
class ViatraQueryTestCase {

    private static val String SEVERITY_AGGREGATOR_LOGAPPENDER_NAME = ViatraQueryTestCase.name +
        ".severityAggregatorLogAppender";

    public static val String UNEXPECTED_MATCH = "Unexpected match"
    public static val String EXPECTED_NOT_FOUND = "Expected match not found"

    private EMFScope scope = new EMFScope(new ResourceSetImpl)
    private boolean isScopeSet = false

    final List<IMatchSetModelProvider> modelProviders
    extension SnapshotHelper snapshotHelper
    private Map<String, JavaObjectAccess> accessMap;
    
    val TestingSeverityAggregatorLogAppender appender

    new() {
        this(Maps.newHashMap)
    }
    
    new(Map<String, JavaObjectAccess> accessMap) {
        this.modelProviders = new LinkedList
        this.accessMap = accessMap
        this.snapshotHelper = new SnapshotHelper(accessMap)
        val a = ViatraQueryLoggingUtil.getLogger(ViatraQueryEngine).getAppender(
            SEVERITY_AGGREGATOR_LOGAPPENDER_NAME)
        this.appender = if (a instanceof TestingSeverityAggregatorLogAppender) {
            a.clear
            a
        } else {
            val na = new TestingSeverityAggregatorLogAppender
            na.name = SEVERITY_AGGREGATOR_LOGAPPENDER_NAME
            ViatraQueryLoggingUtil.getLogger(ViatraQueryEngine).addAppender(na)
            na
        }
    }

    def assertLogSeverityThreshold(Level severity) {
        if (appender.severity.toInt > severity.toInt) {
            Assert.fail(appender.severity.toString + " message on log: " + appender.event.renderedMessage);
        }
    }

    def assertLogSeverity(Level severity) {
        Assert.assertEquals(severity, appender.severity)
    }

    def loadModel(URI uri) {
        val resourceSet = new ResourceSetImpl
        resourceSet.getResource(uri, true)
        scope = new EMFScope(resourceSet)
        isScopeSet = true
    }

    /**
     * Sets the scope of the VIATRA Query test case
     * 
     * @since 1.5.2 
     */
    def setScope(EMFScope scope) {
        this.scope = scope;
        isScopeSet = true
    }

    def void dispose() {
        modelProviders.forEach[dispose]
        modelProviders.clear

        for (Notifier n : scope.scopeRoots) {
            switch (n) {
                ResourceSet: n.resources.forEach[unload]
                Resource: n.unload
                EObject: n.eResource.unload
            }
        }
    }

    def <T extends EObject> modifyModel(Class<T> clazz, Predicate<T> condition, Consumer<T> operation) {
        val nonIncrementals = modelProviders.filter[!updatedByModify]
        modelProviders.removeAll(nonIncrementals)
        nonIncrementals.forEach[dispose]
        val elementsToModify = <T>newLinkedList

        for (Notifier n : scope.scopeRoots) {
            var Iterator<? extends Notifier> iterator
            switch (n) {
                ResourceSet: iterator = n.allContents
                Resource: iterator = n.allContents
                EObject: iterator = n.eAllContents
            }
            while (iterator.hasNext) {
                val element = iterator.next
                if (clazz.isInstance(element)) {
                    val cast = clazz.cast(element)
                    if (condition.test(cast)) {
                        elementsToModify += clazz.cast(element)
                    }
                }
            }
        }
        
        for (element : elementsToModify) {
            operation.accept(element)
        }
        appender.clear
    }

    def addMatchSetModelProvider(IMatchSetModelProvider matchSetModelProvider) {
        modelProviders.add(matchSetModelProvider);
    }

    def <Match extends IPatternMatch> assumeMatchSetsAreAvailable(
        IQuerySpecification<? extends ViatraQueryMatcher<Match>> querySpecification) {

        modelProviders.forEach [
            try {
                it.getMatchSetRecord(scope, querySpecification, null)
            } catch (IllegalArgumentException e) {
                Assume.assumeNoException(e)
            }
        ]

    }
    
    def <Match extends IPatternMatch> assertMatchSetsEqual(
        IQuerySpecification<? extends ViatraQueryMatcher<Match>> querySpecification) {
        assertMatchSetsEqual(querySpecification, new DefaultMatchRecordEquivalence(accessMap))
    }


     /**
     * Checks if the match sets of the given query specification are equivalent, based on a user specified equivalence logic.
     * 
     * @since 1.6
     */
    def <Match extends IPatternMatch> assertMatchSetsEqual(
        IQuerySpecification<? extends ViatraQueryMatcher<Match>> querySpecification, MatchRecordEquivalence equivalence) {
        validateTestCase

        val reference = modelProviders.head

        modelProviders.tail.forEach [
            val matchDiff = getMatchSetDiff(querySpecification, reference, it, equivalence)

            if (!matchDiff.empty) {
                val joiner = Joiner.on("\n")
                throw new ComparisonFailure(
                    '''Differences found between reference «reference.class.name» and match set provider «it.class.name»''',
                    joiner.join(matchDiff.additions.map[prettyPrint]),
                    joiner.join(matchDiff.removals.map[prettyPrint])
                )
            // new AssertionFailedError(diff.toString)
            }
        ]
    }
    
    def assertMatchSetsEqual(IQueryGroup queryGroup) {
        assertMatchSetsEqual(queryGroup, new DefaultMatchRecordEquivalence(accessMap))
    }

     /**
     * Checks if the match sets of the queries contained in the provided query group are equivalent in the scope of added {@link IMatchSetModelProvider} instances.
     * 
     * @since 1.6
     */
    def assertMatchSetsEqual(IQueryGroup queryGroup, MatchRecordEquivalence equivalence) {
        queryGroup.specifications.forEach [
            assertMatchSetsEqual(it as IQuerySpecification<? extends ViatraQueryMatcher<IPatternMatch>>, equivalence)
        ]
    }

    
     /**
     * Calculates the differences between the match sets of a given {@link IQuerySpecification} based on the specified {@link IMatchSetModelProvider} instances.
     * 
     * @since 1.6
     */
    def <Match extends IPatternMatch> getMatchSetDiff(
        IQuerySpecification<? extends ViatraQueryMatcher<Match>> querySpecification,
        IMatchSetModelProvider expectedProvider, IMatchSetModelProvider actualProvider, MatchRecordEquivalence equivalence) {

        validateTestCase

        var Match filter = null;

        var expected = expectedProvider.getMatchSetRecord(scope, querySpecification, filter)
        if (expected.filter !== null) {
            filter = createMatchForMatchRecord(querySpecification, expected.filter)
        }

        val actual = actualProvider.getMatchSetRecord(scope, querySpecification, filter)
        if (actual.filter !== null) {
            if (filter !== null) {
                throw new IllegalArgumentException(
                    "Filter is provided by more than one sources: " + expectedProvider + ", " + actualProvider)
            } else {
                filter = createMatchForMatchRecord(querySpecification, actual.filter)
                // Reexecute expected to have the same filtering
                expected = expectedProvider.getMatchSetRecord(scope, querySpecification, filter)
            }
        }

        MatchSetRecordDiff::compute(expected, actual, equivalence)
    }
    
    def <Match extends IPatternMatch> getMatchSetDiff(
        IQuerySpecification<? extends ViatraQueryMatcher<Match>> querySpecification,
        IMatchSetModelProvider expectedProvider, IMatchSetModelProvider actualProvider) {
        getMatchSetDiff(querySpecification, expectedProvider, actualProvider, new DefaultMatchRecordEquivalence(accessMap))
    }

    /**
     * Validate the created test configuration before calculating and comparing the query results
     * 
     * @since 1.6
     */
    private def validateTestCase() {
        if (modelProviders.size < 2) {
            throw new IllegalArgumentException("At least two model providers shall be set")
        }
        // If the scope is set explicitly by the test case, or a snapshot model provider is added, there exists a test model
        if (!isScopeSet && !modelProviders.exists[
               it instanceof SnapshotMatchSetModelProvider
            || it instanceof InitializedSnapshotMatchSetModelProvider
        ]) {
            throw new IllegalArgumentException("Always include a model in the test specification")
        }
    }
}
    