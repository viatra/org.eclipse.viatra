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

package org.eclipse.viatra.cep.core.engine.compiler;

import java.util.List;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.viatra.cep.core.metamodels.events.AND;
import org.eclipse.viatra.cep.core.metamodels.events.ComplexEventOperator;
import org.eclipse.viatra.cep.core.metamodels.events.ComplexEventPattern;
import org.eclipse.viatra.cep.core.metamodels.events.EventPattern;
import org.eclipse.viatra.cep.core.metamodels.events.EventPatternReference;
import org.eclipse.viatra.cep.core.metamodels.events.EventsFactory;
import org.eclipse.viatra.cep.core.metamodels.events.Multiplicity;
import org.eclipse.viatra.cep.core.metamodels.events.NEG;

import com.google.common.base.Preconditions;

/**
 * Precompiler functionality to map the non-core complex event operators to core operators in a compilation step prior
 * to the actual {@link Compiler} is being invoked.
 * 
 * @author Istvan David
 * 
 */
public class Precompiler {

    /**
     * Translates the non-core {@link ComplexEventOperator}s of an {@link EventPattern} into core operators.
     * 
     * @param eventPattern
     *            the {@link EventPattern} to be translated
     * @return the translated {@link EventPattern}
     */
    public EventPattern unfoldEventPattern(EventPattern eventPattern) {
        if (!(eventPattern instanceof ComplexEventPattern)) {
            return eventPattern;
        }

        ComplexEventOperator operator = ((ComplexEventPattern) eventPattern).getOperator();

        Preconditions.checkArgument(operator != null);

        if (operator instanceof AND) {
            return unfoldAnd((ComplexEventPattern) eventPattern);
        } else if (operator instanceof NEG) {
            return unfoldNEG((ComplexEventPattern) eventPattern);
        } else {
            return eventPattern;
        }
    }

    private EventPattern unfoldAnd(ComplexEventPattern originalPattern) {
        Preconditions.checkArgument(originalPattern.getOperator() instanceof AND);

        List<EventPatternReference> containedEventPatterns = originalPattern.getContainedEventPatterns();

        Preconditions.checkArgument(containedEventPatterns != null && !containedEventPatterns.isEmpty());

        ComplexEventPattern newPattern = EventsFactory.eINSTANCE.createComplexEventPattern();
        newPattern.setId(originalPattern.getId());
        newPattern.setOperator(EventsFactory.eINSTANCE.createOR());

        for (final List<EventPatternReference> permutation : new PermutationsHelper<EventPatternReference>()
                .getAll(containedEventPatterns)) {
            ComplexEventPattern innerPattern = EventsFactory.eINSTANCE.createComplexEventPattern();
            innerPattern.setOperator(EventsFactory.eINSTANCE.createFOLLOWS());
            for (EventPatternReference eventPatternReference : permutation) {
                EObject copy = new EcoreUtil.Copier().copy(eventPatternReference.getEventPattern());
                EventPatternReference newReference = EventsFactory.eINSTANCE.createEventPatternReference();
                Multiplicity multiplicity = EventsFactory.eINSTANCE.createMultiplicity();
                multiplicity.setValue(1);
                newReference.setMultiplicity(multiplicity);
                newReference.setEventPattern((EventPattern) copy);
                innerPattern.getContainedEventPatterns().add(newReference);
            }
            EventPatternReference innerReference = EventsFactory.eINSTANCE.createEventPatternReference();
            Multiplicity multiplicity = EventsFactory.eINSTANCE.createMultiplicity();
            multiplicity.setValue(1);
            innerReference.setMultiplicity(multiplicity);
            innerReference.setEventPattern(innerPattern);
            newPattern.getContainedEventPatterns().add(innerReference);
        }

        return newPattern;
    }

    private EventPattern unfoldNEG(ComplexEventPattern eventPattern) {
        throw new UnsupportedOperationException();
    }
}
