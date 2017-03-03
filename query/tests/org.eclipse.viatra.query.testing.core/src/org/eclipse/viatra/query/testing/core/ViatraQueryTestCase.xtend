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
import org.junit.Assert
import org.junit.Assume
import org.junit.ComparisonFailure

/** 
 * @author Grill Balazs
 */
class ViatraQueryTestCase {

    private static val String SEVERITY_AGGREGATOR_LOGAPPENDER_NAME = ViatraQueryTestCase.name +
        ".severityAggregatorLogAppender";

    public static val String UNEXPECTED_MATCH = "Unexpected match"
    public static val String EXPECTED_NOT_FOUND = "Expected match not found"

    private EMFScope scope = new EMFScope(new ResourceSetImpl)

    final List<IMatchSetModelProvider> modelProviders
    extension SnapshotHelper = new SnapshotHelper

    val TestingSeverityAggregatorLogAppender appender

    new() {
        this.modelProviders = new LinkedList

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
    }

    /**
     * Sets the scope of the VIATRA Query test case
     * 
     * @since 1.5.2 
     */
    def setScope(EMFScope scope) {
        this.scope = scope;
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

    def <T extends EObject> modifyModel(Class<T> clazz, (T)=>boolean condition, (T)=>void operation) {
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
                    if (condition.apply(cast)) {
                        elementsToModify += clazz.cast(element)
                    }
                }
            }
        }

        for (element : elementsToModify) {
            operation.apply(element)
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
        if (modelProviders.size < 2) {
            throw new IllegalArgumentException("At least two model providers shall be set")
        }

        val reference = modelProviders.head

        modelProviders.tail.forEach [
            val matchDiff = getMatchSetDiff(querySpecification, reference, it)

            if (!matchDiff.empty) {
                val joiner = Joiner.on("\n")
                throw new ComparisonFailure(
                    matchDiff.toString,
                    joiner.join(matchDiff.additions.map[prettyPrint]),
                    joiner.join(matchDiff.removals.map[prettyPrint])
                )
            // new AssertionFailedError(diff.toString)
            }
        ]
    }

    def assertMatchSetsEqual(IQueryGroup queryGroup) {
        queryGroup.specifications.forEach [
            assertMatchSetsEqual(it as IQuerySpecification<? extends ViatraQueryMatcher<IPatternMatch>>)
        ]
    }

    def <Match extends IPatternMatch> getMatchSetDiff(
        IQuerySpecification<? extends ViatraQueryMatcher<Match>> querySpecification,
        IMatchSetModelProvider expectedProvider, IMatchSetModelProvider actualProvider) {

        var Match filter = null;

        var expected = expectedProvider.getMatchSetRecord(scope, querySpecification, filter)
        if (expected.filter != null) {
            filter = createMatchForMatchRecord(querySpecification, expected.filter)
        }

        val actual = actualProvider.getMatchSetRecord(scope, querySpecification, filter)
        if (actual.filter != null) {
            if (filter != null) {
                throw new IllegalArgumentException(
                    "Filter is provided by more than one sources: " + expectedProvider + ", " + actualProvider)
            } else {
                filter = createMatchForMatchRecord(querySpecification, actual.filter)
                // Reexecute expected to have the same filtering
                expected = expectedProvider.getMatchSetRecord(scope, querySpecification, filter)
            }
        }

        MatchSetRecordDiff::compute(expected, actual)
    }

    private def <Match extends IPatternMatch> compareMatchSets(
        IQuerySpecification<? extends ViatraQueryMatcher<Match>> querySpecification,
        IMatchSetModelProvider expectedProvider, IMatchSetModelProvider actualProvider) {

        val matchdiff = getMatchSetDiff(querySpecification, expectedProvider, actualProvider)
        val diff = newHashSet
        diff.addAll(matchdiff.additions.map[UNEXPECTED_MATCH + " (" + it.prettyPrint + ")"])
        diff.addAll(matchdiff.removals.map[EXPECTED_NOT_FOUND + " (" + it.prettyPrint + ")"])

        return diff
    }

}
    