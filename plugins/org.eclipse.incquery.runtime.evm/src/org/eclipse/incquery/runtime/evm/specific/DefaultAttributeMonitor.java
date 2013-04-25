/*******************************************************************************
 * Copyright (c) 2010-2012, Tamas Szabo, Abel Hegedus, Istvan Rath and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Tamas Szabo, Abel Hegedus - initial API and implementation
 *******************************************************************************/

package org.eclipse.incquery.runtime.evm.specific;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.core.databinding.observable.value.IValueChangeListener;
import org.eclipse.core.databinding.observable.value.ValueChangeEvent;
import org.eclipse.emf.databinding.EMFProperties;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.incquery.runtime.api.IPatternMatch;
import org.eclipse.incquery.runtime.evm.api.event.Atom;
import org.eclipse.incquery.runtime.evm.notification.AttributeMonitor;
import org.eclipse.incquery.runtime.evm.specific.event.PatternMatchAtom;

/**
 * Default implementation of the {@link AttributeMonitor} that uses EMF Data binding to 
 * watch the values of each feature of each object in matches.
 * 
 * @author Abel Hegedus
 *
 * @param <MatchType>
 */
public class DefaultAttributeMonitor<MatchType extends IPatternMatch> extends AttributeMonitor {

    private ChangeListener changeListener;
    private Map<IObservableValue, PatternMatchAtom<?>> observableMap;
    private Map<PatternMatchAtom<?>, List<IObservableValue>> observableMapReversed;

    public DefaultAttributeMonitor() {
        super();
        this.changeListener = new ChangeListener();
        this.observableMap = new HashMap<IObservableValue, PatternMatchAtom<?>>();
        this.observableMapReversed = new HashMap<PatternMatchAtom<?>, List<IObservableValue>>();
    }

    /**
     * Simple change listener implementation that sends a notification on each change.
     * 
     * @author Abel Hegedus
     *
     */
    private class ChangeListener implements IValueChangeListener {
        @Override
        public void handleValueChange(final ValueChangeEvent event) {
            IObservableValue val = event.getObservableValue();
            if (val != null) {
                notifyListeners(observableMap.get(val));
            }
        }
    }

    @Override
    public void registerFor(final Atom atom) {
        List<IObservableValue> values = new ArrayList<IObservableValue>();
        if(atom instanceof PatternMatchAtom<?>) {
            IPatternMatch match = ((PatternMatchAtom<?>) atom).getMatch();
            for (String param : match.parameterNames()) {
                Object location = match.get(param);
                List<IObservableValue> observableValues = observeAllAttributes(changeListener, location);
                values.addAll(observableValues);
            }
            
            // inserting {observable,match} pairs
            for (IObservableValue val : values) {
                observableMap.put(val, (PatternMatchAtom<?>) atom);
            }
        }

        // inserting {match, list(observable)} pairs
        observableMapReversed.put((PatternMatchAtom<?>) atom, values);
    }

    /**
     * Iterates on all features and returns a list of observable values.
     * 
     * @param changeListener
     * @param object
     * @return
     */
    private List<IObservableValue> observeAllAttributes(final IValueChangeListener changeListener, final Object object) {
        List<IObservableValue> affectedValues = new ArrayList<IObservableValue>();
        if (object instanceof EObject) {
            for (EStructuralFeature feature : ((EObject) object).eClass().getEAllStructuralFeatures()) {
                IObservableValue val = EMFProperties.value(feature).observe(object);
                affectedValues.add(val);
                val.addValueChangeListener(changeListener);
            }
        }
        return affectedValues;
    }

    @Override
    public void unregisterForAll() {
        for (PatternMatchAtom<?> atom : observableMapReversed.keySet()) {
            unregisterFor(atom);
        }
    }

    @Override
    public void unregisterFor(final Atom atom) {
        List<IObservableValue> observables = observableMapReversed.get(atom);
        if (observables != null) {
            for (IObservableValue val : observables) {
                val.removeValueChangeListener(changeListener);
            }
        }
    }
}