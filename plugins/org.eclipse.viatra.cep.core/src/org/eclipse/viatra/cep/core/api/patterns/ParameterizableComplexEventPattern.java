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

package org.eclipse.viatra.cep.core.api.patterns;

import java.util.List;

import org.eclipse.viatra.cep.core.metamodels.events.AbstractMultiplicity;
import org.eclipse.viatra.cep.core.metamodels.events.ComplexEventPattern;
import org.eclipse.viatra.cep.core.metamodels.events.EventPattern;
import org.eclipse.viatra.cep.core.metamodels.events.EventPatternReference;
import org.eclipse.viatra.cep.core.metamodels.events.EventsFactory;
import org.eclipse.viatra.cep.core.metamodels.events.Multiplicity;
import org.eclipse.viatra.cep.core.metamodels.events.impl.ComplexEventPatternImpl;

/**
 * An extension of the {@link ComplexEventPattern} type that additionally captures parameters of the pattern and
 * provides functionality to evaluate parameter bindings at runtime.
 * 
 * <p>
 * In event processing scenarios where parameter binding is involved, event patterns should extend this superclass.
 * 
 * @author Istvan David
 * 
 */
public abstract class ParameterizableComplexEventPattern extends ComplexEventPatternImpl {

    public void addEventPatternRefrence(EventPattern eventPatternToBeReffered, int multiplicity,
            List<String> parameterSymbolicNames) {
        EventPatternReference eventPatternRefrence = addEventPatternRefrence(eventPatternToBeReffered, multiplicity);
        eventPatternRefrence.getParameterSymbolicNames().addAll(parameterSymbolicNames);
    }

    public EventPatternReference addEventPatternRefrence(EventPattern eventPatternToBeReffered, int multiplicity) {
        Multiplicity multiplicityObject = EventsFactory.eINSTANCE.createMultiplicity();
        multiplicityObject.setValue(multiplicity);
        return addEventPatternRefrence(eventPatternToBeReffered, multiplicityObject);
    }

    public EventPatternReference addEventPatternRefrence(EventPattern eventPatternToBeReffered,
            AbstractMultiplicity multiplicity) {
        EventPatternReference eventPatternReference = EventsFactory.eINSTANCE.createEventPatternReference();
        eventPatternReference.setEventPattern(eventPatternToBeReffered);
        eventPatternReference.setMultiplicity(multiplicity);
        getContainedEventPatterns().add(eventPatternReference);
        return eventPatternReference;
    }
}
