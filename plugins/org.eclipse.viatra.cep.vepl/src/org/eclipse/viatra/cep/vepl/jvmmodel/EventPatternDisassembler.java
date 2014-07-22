/*******************************************************************************
 * Copyright (c) 2004-2014, Istvan David, Istvan Rath and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Istvan David - initial API and implementation
 *******************************************************************************/

package org.eclipse.viatra.cep.vepl.jvmmodel;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.viatra.cep.vepl.vepl.AugmentedExpression;
import org.eclipse.viatra.cep.vepl.vepl.ComplexEventExpression;
import org.eclipse.viatra.cep.vepl.vepl.ComplexEventOperator;
import org.eclipse.viatra.cep.vepl.vepl.ComplexExpressionAtom;
import org.eclipse.viatra.cep.vepl.vepl.EventPattern;
import org.eclipse.viatra.cep.vepl.vepl.PlainExpression;
import org.eclipse.viatra.cep.vepl.vepl.TailExpressionAtom;
import org.eclipse.viatra.cep.vepl.vepl.UntilOperator;

import com.google.common.collect.LinkedListMultimap;
import com.google.common.collect.ListMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;

public class EventPatternDisassembler {

    private static EventPatternDisassembler instance;

    public static EventPatternDisassembler getInstance() {
        if (instance == null) {
            instance = new EventPatternDisassembler();
        }
        return instance;
    }

    public ListMultimap<ComplexEventOperator, List<EventPattern>> decomposeComplexPattern(
            ComplexEventExpression expression) {
        if (expression instanceof PlainExpression) {
            return decomposeComplexPattern((PlainExpression) expression);
        } else if (expression instanceof AugmentedExpression) {
            return decomposeComplexPattern(((AugmentedExpression) expression).getExpression());
        }
        throw new IllegalArgumentException("Unsupported complex event expression type.");
    }

    private ListMultimap<ComplexEventOperator, List<EventPattern>> decomposeComplexPattern(PlainExpression expression) {
        ListMultimap<ComplexEventOperator, List<EventPattern>> decomposedPattern = LinkedListMultimap.create();

        if (expression.getTailExpressionAtoms() == null || expression.getTailExpressionAtoms().isEmpty()) {
            return decomposedPattern;
        }

        List<EventPattern> patternsToBeGrouped = Lists.newArrayList();
        ComplexEventOperator lastOperator = expression.getTailExpressionAtoms().get(0).getOperator();

        ComplexExpressionAtom headExpressionAtom = expression.getHeadExpressionAtom();
        int headMultiplicity = 1;
        if (headExpressionAtom.getMultiplicity() != null) {
            headMultiplicity = headExpressionAtom.getMultiplicity().getMultiplicity();
        }
        for (int i = 0; i < headMultiplicity; i++) {
            patternsToBeGrouped.add(headExpressionAtom.getPatternCall().getEventPattern());
        }

        for (TailExpressionAtom e : expression.getTailExpressionAtoms()) {
            if (!sameOperators(e.getOperator(), lastOperator) || (e.getOperator() instanceof UntilOperator)) {
                // UNTIL is binary, thus needs to be curried one by one
                packageCurrentPatternGroup(lastOperator, patternsToBeGrouped, decomposedPattern);

                lastOperator = e.getOperator();
                patternsToBeGrouped.clear();
            }

            ComplexExpressionAtom expressionAtom = e.getExpressionAtom();
            int tailMultiplicity = 1;
            if (expressionAtom.getMultiplicity() != null) {
                tailMultiplicity = expressionAtom.getMultiplicity().getMultiplicity();
            }
            for (int i = 0; i < tailMultiplicity; i++) {
                patternsToBeGrouped.add(e.getExpressionAtom().getPatternCall().getEventPattern());
            }
        }

        packageCurrentPatternGroup(lastOperator, patternsToBeGrouped, decomposedPattern);

        return decomposedPattern;
    }

    private void packageCurrentPatternGroup(ComplexEventOperator operator, List<EventPattern> patterns,
            Multimap<ComplexEventOperator, List<EventPattern>> decomposedPattern) {
        decomposedPattern.put(operator, new ArrayList<EventPattern>(patterns));
    }

    private boolean sameOperators(ComplexEventOperator operator1, ComplexEventOperator operator2) {
        return operator1.getClass().equals(operator2.getClass());
    }
}
