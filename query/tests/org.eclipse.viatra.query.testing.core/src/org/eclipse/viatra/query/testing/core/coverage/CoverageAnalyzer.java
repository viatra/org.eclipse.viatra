/*******************************************************************************
 * Copyright (c) 2010-2017, Grill Balázs, IncQueryLabs
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-v20.html.
 * 
 * SPDX-License-Identifier: EPL-2.0
 *******************************************************************************/
package org.eclipse.viatra.query.testing.core.coverage;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.viatra.query.runtime.api.ViatraQueryMatcher;
import org.eclipse.viatra.query.runtime.api.impl.BaseMatcher;
import org.eclipse.viatra.query.runtime.matchers.backend.CommonQueryHintOptions;
import org.eclipse.viatra.query.runtime.matchers.backend.QueryEvaluationHint;
import org.eclipse.viatra.query.runtime.matchers.backend.QueryEvaluationHint.BackendRequirement;
import org.eclipse.viatra.query.runtime.matchers.backend.QueryHintOption;
import org.eclipse.viatra.query.runtime.matchers.psystem.PTraceable;
import org.eclipse.viatra.query.runtime.matchers.psystem.rewriters.IRewriterTraceCollector;
import org.eclipse.viatra.query.runtime.matchers.psystem.rewriters.MappingTraceCollector;
import org.eclipse.viatra.query.runtime.rete.matcher.IncrementalMatcherCapability;
import org.eclipse.viatra.query.runtime.rete.matcher.ReteBackendFactory;
import org.eclipse.viatra.query.runtime.rete.network.Node;
import org.eclipse.viatra.query.testing.core.api.IPatternExecutionAnalyzer;
import org.eclipse.viatra.query.testing.core.api.ViatraQueryTest;

/**
 * This is an implementation of {@link IPatternExecutionAnalyzer} which can extract and aggregate
 * coverage information through adding it to one or more {@link ViatraQueryTest} executions via
 * the {@link ViatraQueryTest#analyzeWith(IPatternExecutionAnalyzer)} method.
 * 
 * @since 1.6
 */
public class CoverageAnalyzer implements IPatternExecutionAnalyzer {

    private final IRewriterTraceCollector traceCollector = new MappingTraceCollector();
    private CoverageInfo<PTraceable> coverage = new CoverageInfo<>();

    @Override
    public QueryEvaluationHint configure(QueryEvaluationHint hints) {
        if (hints.getQueryBackendFactory() instanceof ReteBackendFactory){
            Map<QueryHintOption<?>, Object> values = new HashMap<>(1);
            values.put(CommonQueryHintOptions.normalizationTraceCollector, traceCollector);
            return hints.overrideBy(new QueryEvaluationHint(values, BackendRequirement.UNSPECIFIED));
        }
        return hints;
    }

    /**
     * Returns the coverage information of every analyzed pattern.
     */
    public CoverageInfo<PTraceable> getCoverage() {
        return coverage;
    }

    @Override
    public void processMatcher(ViatraQueryMatcher<?> matcher) {
        // Only Rete backend is supported
        if (matcher instanceof BaseMatcher) {
            BaseMatcher<?> baseMatcher = (BaseMatcher<?>) matcher;
            if (baseMatcher.getCapabilities() instanceof IncrementalMatcherCapability) {
                ReteNetworkTrace trace = new ReteNetworkTrace(matcher, traceCollector);
                CoverageInfo<Node> reteCoverage = new ReteCoverage(matcher).reteCoverage();
                CoverageInfo<PTraceable> newCoverage = trace.traceCoverage(matcher, reteCoverage);
                coverage = coverage.mergeWith(newCoverage);
            }
        }
    }

}
