/*******************************************************************************
 * Copyright (c) 2010-2014, Miklos Foldenyi, Andras Szabolcs Nagy, Abel Hegedus, Akos Horvath, Zoltan Ujhelyi and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Contributors:
 *   Miklos Foldenyi - initial API and implementation
 *   Andras Szabolcs Nagy - initial API and implementation
 *******************************************************************************/
package org.eclipse.viatra.dse.genetic.core;

import org.eclipse.incquery.runtime.api.IPatternMatch;
import org.eclipse.incquery.runtime.api.IQuerySpecification;
import org.eclipse.incquery.runtime.api.IncQueryEngine;
import org.eclipse.incquery.runtime.api.IncQueryMatcher;
import org.eclipse.incquery.runtime.exception.IncQueryException;
import org.eclipse.viatra.dse.api.DSEException;
import org.eclipse.viatra.dse.api.PatternWithCardinality;

public class SoftConstraint extends PatternWithCardinality {

    private final double weight;

    public SoftConstraint(IQuerySpecification<? extends IncQueryMatcher<? extends IPatternMatch>> querySpecification) {
        this("", querySpecification, 1);
    }

    public SoftConstraint(IQuerySpecification<? extends IncQueryMatcher<? extends IPatternMatch>> querySpecification,
            double weight) {
        this("", querySpecification, weight);
    }

    public SoftConstraint(String name,
            IQuerySpecification<? extends IncQueryMatcher<? extends IPatternMatch>> querySpecification) {
        this(name, querySpecification, 1);
    }

    public SoftConstraint(String name,
            IQuerySpecification<? extends IncQueryMatcher<? extends IPatternMatch>> querySpecification, double weight) {
        super(querySpecification);
        setName(name);
        this.weight = weight;
    }

    public int getNumberOfMatches(IncQueryEngine engine) {
        try {
            return getQuerySpecification().getMatcher(engine).countMatches();
        } catch (IncQueryException e) {
            throw new DSEException("Incquery matcher initialization is failed.", e);
        }
    }

    public double getViolationMeasurement(IncQueryEngine engine) {
        return getNumberOfMatches(engine) * weight;
    }

    public double getWeight() {
        return weight;
    }

}
