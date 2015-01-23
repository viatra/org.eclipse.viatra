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
import java.util.Map;
import java.util.Map.Entry;

import org.eclipse.viatra.cep.core.api.events.ParameterizableEventInstance;
import org.eclipse.viatra.cep.core.metamodels.events.AbstractMultiplicity;
import org.eclipse.viatra.cep.core.metamodels.events.ComplexEventPattern;
import org.eclipse.viatra.cep.core.metamodels.events.EventPattern;
import org.eclipse.viatra.cep.core.metamodels.events.EventPatternReference;
import org.eclipse.viatra.cep.core.metamodels.events.EventsFactory;
import org.eclipse.viatra.cep.core.metamodels.events.Multiplicity;
import org.eclipse.viatra.cep.core.metamodels.events.impl.ComplexEventPatternImpl;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

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
    private Map<String, Object> paramValues = Maps.newHashMap();
    private List<ParameterizableEventInstance> observedEvents = Lists.newArrayList();

    protected boolean evaluateParamBinding(Map<String, Object> params) {
        if (params.isEmpty()) {
            return true;
        }
        for (Entry<String, Object> param : params.entrySet()) {
            if (!(evaluateParamBinding(param.getKey(), param.getValue()))) {
                return false;
            }
        }
        return true;
    }

    protected boolean evaluateParamBinding(Map<String, Object> params, ParameterizableEventInstance observedEvent) {
        if (evaluateParamBinding(params)) {
            observedEvents.add(observedEvent);
            return true;
        }
        return false;
    }

    protected boolean evaluateParamBinding(String paramName, Object paramValue) {
        if (Strings.isNullOrEmpty(paramName) || paramValue == null) {
            return false;
        }
        Object value = paramValues.get(paramName);
        if (value == null) {
            paramValues.put(paramName, paramValue);
            return true;
        }
        return value.equals(paramValue);
    }

    public void addEventPatternRefrence(EventPattern eventPatternToBeReffered, int multiplicity) {
        Multiplicity multiplicityObject = EventsFactory.eINSTANCE.createMultiplicity();
        multiplicityObject.setValue(multiplicity);
        addEventPatternRefrence(eventPatternToBeReffered, multiplicityObject);
    }

    public void addEventPatternRefrence(EventPattern eventPatternToBeReffered, AbstractMultiplicity multiplicity) {
        EventPatternReference eventPatternReference = EventsFactory.eINSTANCE.createEventPatternReference();
        eventPatternReference.setEventPattern(eventPatternToBeReffered);
        eventPatternReference.setMultiplicity(multiplicity);
        getContainedEventPatterns().add(eventPatternReference);
    }

    public Map<String, Object> getParamValues() {
        return paramValues;
    }

    public List<ParameterizableEventInstance> getObservedEvents() {
        return observedEvents;
    }
}
