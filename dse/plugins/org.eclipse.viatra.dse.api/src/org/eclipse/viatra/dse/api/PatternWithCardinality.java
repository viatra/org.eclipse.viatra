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
package org.eclipse.viatra.dse.api;

import static com.google.common.base.Preconditions.checkArgument;

import org.eclipse.incquery.runtime.api.IPatternMatch;
import org.eclipse.incquery.runtime.api.IQuerySpecification;
import org.eclipse.incquery.runtime.api.IncQueryEngine;
import org.eclipse.incquery.runtime.api.IncQueryMatcher;
import org.eclipse.incquery.runtime.exception.IncQueryException;

public class PatternWithCardinality {

    private final IQuerySpecification<? extends IncQueryMatcher<? extends IPatternMatch>> querySpecification;
    private final int cardinality;
    private final CardinalityType cardinalityType;

    private final RuleMetaData metaData;

    private String name;

    public PatternWithCardinality(
            IQuerySpecification<? extends IncQueryMatcher<? extends IPatternMatch>> querySpecification,
            int cardinality, CardinalityType cardinalityType, RuleMetaData metaData) {
        checkArgument(querySpecification != null);

        this.querySpecification = querySpecification;
        this.cardinality = cardinality;
        this.cardinalityType = cardinalityType;
        this.metaData = metaData;
        this.name = querySpecification.getFullyQualifiedName();
    }

    public PatternWithCardinality(
            IQuerySpecification<? extends IncQueryMatcher<? extends IPatternMatch>> querySpecification) {
        this(querySpecification, 1, CardinalityType.AT_LEAST, null);
    }

    public PatternWithCardinality(
            IQuerySpecification<? extends IncQueryMatcher<? extends IPatternMatch>> querySpecification, int cardinality) {
        this(querySpecification, cardinality, CardinalityType.AT_LEAST, null);
    }

    public PatternWithCardinality(
            IQuerySpecification<? extends IncQueryMatcher<? extends IPatternMatch>> querySpecification,
            int cardinality, CardinalityType cardinalityType) {
        this(querySpecification, cardinality, cardinalityType, null);
    }

    public PatternWithCardinality(
            IQuerySpecification<? extends IncQueryMatcher<? extends IPatternMatch>> querySpecification,
            RuleMetaData metaData) {
        this(querySpecification, 1, CardinalityType.AT_LEAST, metaData);
    }

    public IQuerySpecification<? extends IncQueryMatcher<? extends IPatternMatch>> getQuerySpecification() {
        return querySpecification;
    }

    public int getCardinality() {
        return cardinality;
    }

    public CardinalityType getCardinalityType() {
        return cardinalityType;
    }

    /**
     * Determines if the pattern is satisfied.
     * 
     * @return True if the pattern is satisfied.
     * @throws IncQueryException
     */
    public boolean isPatternSatisfied(IncQueryEngine engine) {
        // TODO optimization: cache matcher; catch exception elsewhere
        int numOfMatches = 0;
        try {
            numOfMatches = querySpecification.getMatcher(engine).countMatches();
        } catch (IncQueryException e) {
            throw new DSEException("IncqueryException from PatternWithCardinality.isPatternSatisfied", e);
        }
        switch (cardinalityType) {
        case AT_LEAST:
            if (cardinality <= numOfMatches) {
                return true;
            }
            break;

        case AT_MOST:
            if (cardinality >= numOfMatches) {
                return true;
            }
            break;

        case EXACTLY:
            if (cardinality == numOfMatches) {
                return true;
            }
            break;

        case NOT_EQUAL:
            if (cardinality != numOfMatches) {
                return true;
            }
            break;
        }
        return false;
    }

    public RuleMetaData getMetaData() {
        return metaData;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}
