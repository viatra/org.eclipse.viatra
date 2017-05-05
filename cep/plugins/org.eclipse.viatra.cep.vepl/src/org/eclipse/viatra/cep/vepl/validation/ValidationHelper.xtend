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
package org.eclipse.viatra.cep.vepl.validation

import java.util.List
import org.eclipse.emf.ecore.EObject
import org.eclipse.viatra.cep.vepl.vepl.AbstractMultiplicity
import org.eclipse.viatra.cep.vepl.vepl.AndOperator
import org.eclipse.viatra.cep.vepl.vepl.Atom
import org.eclipse.viatra.cep.vepl.vepl.ChainedExpression
import org.eclipse.viatra.cep.vepl.vepl.ComplexEventExpression
import org.eclipse.viatra.cep.vepl.vepl.ComplexEventOperator
import org.eclipse.viatra.cep.vepl.vepl.ComplexEventPattern
import org.eclipse.viatra.cep.vepl.vepl.FollowsOperator
import org.eclipse.viatra.cep.vepl.vepl.Infinite
import org.eclipse.viatra.cep.vepl.vepl.Multiplicity
import org.eclipse.viatra.cep.vepl.vepl.ParameterizedPatternCall

/**
 * Helper class for the {@link VeplValidator}.
 */
class ValidationHelper {
    def static dispatch hasTimewindow(ComplexEventExpression expression) {
        return expression.timewindow != null
    }

    def static dispatch hasTimewindow(Atom atom) {
        return atom.timewindow != null
    }

    def static dispatch hasMultiplicity(ComplexEventExpression expression) {
        return expression.multiplicity != null
    }

    def static dispatch hasMultiplicity(Atom atom) {
        return atom.multiplicity != null
    }

    def static hasInfiniteMultiplicity(ComplexEventExpression complexEventExpression) {
        return complexEventExpression.multiplicity instanceof Infinite
    }

    def static nullOrOneMultiplicity(AbstractMultiplicity multiplicity) {
        if (multiplicity == null) {
            return true
        }
        if (!(multiplicity instanceof Multiplicity)) {
            return false
        }
        return (multiplicity as Multiplicity).value == 1
    }

    def static hasParameterList(ParameterizedPatternCall patternCall) {
        return patternCall.parameterList != null
    }

    def static qualifiesAsFollowingOperator(ComplexEventOperator operator) {
        return (operator instanceof FollowsOperator) || (operator instanceof AndOperator)
    }

    def static subListFrom(List<ChainedExpression> list, ComplexEventExpression element) {
        return list.subList(list.indexOf(list.findFirst [ che |
            che.expression.equals(element)
        ]) + 1, list.size)
    }

    def static findContainingComplexEventPatternDefinition(ParameterizedPatternCall parameterizedPatternCall) {
        var ComplexEventPattern complexEventPattern = null
        var EObject tmp = parameterizedPatternCall.eContainer

        while (complexEventPattern == null) {
            if (tmp instanceof ComplexEventPattern) {
                complexEventPattern = tmp as ComplexEventPattern
            } else {
                tmp = tmp.eContainer
            }
        }

        return complexEventPattern
    }

    def static foldWithComma(Iterable<String> iterable) {
        iterable.fold("")[a, b|if (a.empty) {a +  b} else {a +", " +b}]
    }
}
