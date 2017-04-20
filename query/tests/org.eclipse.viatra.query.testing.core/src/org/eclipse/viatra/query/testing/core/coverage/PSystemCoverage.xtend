/*******************************************************************************
 * Copyright (c) 2010-2017, Dénes Harmath, IncQueryLabs
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Dénes Harmath - initial API and implementation
 *******************************************************************************/
 package org.eclipse.viatra.query.testing.core.coverage

import com.google.common.base.Optional
import com.google.common.math.DoubleMath
import org.eclipse.viatra.query.runtime.matchers.psystem.PBody
import org.eclipse.viatra.query.runtime.matchers.psystem.PConstraint
import org.eclipse.viatra.query.runtime.matchers.psystem.queries.PQuery
import org.eclipse.xtend.lib.annotations.Data

import static extension com.google.common.base.Optional.*

/**
 * Associates coverage to {@link PQuery}, {@link PBody} and {@link PConstraint} objects.
 * 
 * @since 1.6
 */
@Data
class PSystemCoverage {
	CoverageInfo<PConstraint> constraintCoverage
	CoverageInfo<PBody> bodyCoverage
	CoverageInfo<PQuery> queryCoverage

	def PSystemCoverage mergeWith(PSystemCoverage other) {
		new PSystemCoverage(
			constraintCoverage.mergeWith(other.constraintCoverage),
			bodyCoverage.mergeWith(other.bodyCoverage),
			queryCoverage.mergeWith(other.queryCoverage)
		)
	}
	
	def double getAggregatedCoveragePercent() {
		val queryCoverages = queryCoverage.keySet.map[it.coveragePercent]
		DoubleMath.mean(queryCoverages)
	}

	def double getCoveragePercent(PQuery query) {
		val constraintCoverages = query.disjunctBodies.bodies.map[constraints.map[getCoveragePercent(constraintCoverage)]].flatten
		val bodyCoverages = query.disjunctBodies.bodies.map[getCoveragePercent(bodyCoverage)]
		val headerCoverage = #[query.getCoveragePercent(queryCoverage)]
		DoubleMath.mean((constraintCoverages + bodyCoverages + headerCoverage).presentInstances)
	}
	
	private def <T> Optional<Double> getCoveragePercent(T element, CoverageInfo<T> coverage) {
		switch coverage.get(element) {
		    case COVERED: Optional.of(100.0)
            case NOT_COVERED: Optional.of(0.0)
            case NOT_REPRESENTED: Optional.absent
            case UNDEFINED: Optional.of(0.0)
		}
	}

}
