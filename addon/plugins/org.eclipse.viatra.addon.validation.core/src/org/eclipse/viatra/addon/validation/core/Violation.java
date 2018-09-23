/*******************************************************************************
 * Copyright (c) 2010-2014, Balint Lorand, Zoltan Ujhelyi, Abel Hegedus, Tamas Szabo, Istvan Rath and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Zoltan Ujhelyi, Abel Hegedus, Tamas Szabo - original initial API and implementation
 *   Balint Lorand - revised API and implementation
 *******************************************************************************/

package org.eclipse.viatra.addon.validation.core;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.viatra.addon.validation.core.api.IEntry;
import org.eclipse.viatra.addon.validation.core.api.IViolation;
import org.eclipse.viatra.addon.validation.core.listeners.ViolationListener;
import org.eclipse.viatra.addon.validation.core.violationkey.CompositeSymmetricViolationKey;
import org.eclipse.viatra.addon.validation.core.violationkey.ViolationKey;
import org.eclipse.viatra.query.runtime.api.IPatternMatch;

public class Violation implements IViolation {

    private Constraint constraint;

    @Override
    public Constraint getConstraint() {
        return constraint;
    }

    protected void setConstraint(Constraint constraint) {
        this.constraint = constraint;
    }

    private String message;

    @Override
    public String getMessage() {
        return message;
    }

    protected void setMessage(String message) {
        this.message = message;
    }

    private Map<String, Object> keyObjects;

    @Override
    public Map<String, Object> getKeyObjects() {
        return keyObjects;
    }

    protected void setKeyObjects(Map<String, Object> keyObjects) {
        this.keyObjects = keyObjects;
    }

    private Map<ViolationKey, IPatternMatch> matches = new HashMap<ViolationKey, IPatternMatch>();

    protected Map<ViolationKey, IPatternMatch> getMatches() {
        return matches;
    }

    protected boolean addMatch(IPatternMatch match) {

        Map<String, Object> parameters = new HashMap<String, Object>();
        for (String name : match.parameterNames()) {
            parameters.put(name, match.get(name));
        }
        Set<List<String>> symmetrics = new HashSet<List<String>>();
        symmetrics.addAll(constraint.getSpecification().getSymmetricKeyNames());
        symmetrics.addAll(constraint.getSpecification().getSymmetricPropertyNames());

        ViolationKey key = new CompositeSymmetricViolationKey(parameters, symmetrics);

        if (!matches.containsKey(key)) {
            matches.put(key, match);
            return true;
        }
        return false;
    }

    protected boolean removeMatch(IPatternMatch match) {

        Map<String, Object> parameters = new HashMap<String, Object>();
        for (String name : match.parameterNames()) {
            parameters.put(name, match.get(name));
        }
        Set<List<String>> symmetrics = new HashSet<List<String>>();
        symmetrics.addAll(constraint.getSpecification().getSymmetricKeyNames());
        symmetrics.addAll(constraint.getSpecification().getSymmetricPropertyNames());

        ViolationKey key = new CompositeSymmetricViolationKey(parameters, symmetrics);

        return matches.remove(key) != null;
    }

    @Override
    public Set<IEntry> getEntries() {
        Set<IEntry> entries = new HashSet<IEntry>();
        for (Map.Entry<ViolationKey, IPatternMatch> entry : matches.entrySet()) {
            entries.add(new Entry(this, entry.getValue()));
        }
        return entries;
    }

    @Override
    public Set<Object> getValuesOfProperty(String propertyName) {
        Set<Object> propertyValues = new HashSet<Object>();
        for (Map.Entry<ViolationKey, IPatternMatch> entry : matches.entrySet()) {
            propertyValues.add(entry.getValue().get(propertyName));
        }
        return propertyValues;
    }

    private Set<ViolationListener> listeners = new HashSet<ViolationListener>();

    @Override
    public Set<ViolationListener> getListeners() {
        return Collections.unmodifiableSet(new HashSet<>(listeners));
    }

    @Override
    public boolean addListener(ViolationListener listener) {
        return listeners.add(listener);
    }

    @Override
    public boolean removeListener(ViolationListener listener) {
        return listeners.remove(listener);
    }

    protected void notifyListenersViolationEntryAppeared(IPatternMatch match) {
        for (ViolationListener listener : listeners) {
            listener.violationEntryAppeared(this, new Entry(this, match));
        }
    }

    protected void notifyListenersViolationMessageUpdated() {
        for (ViolationListener listener : listeners) {
            listener.violationMessageUpdated(this);
        }
    }

    protected void notifyListenersViolationEntryDisappeared(IPatternMatch match) {
        for (ViolationListener listener : listeners) {
            listener.violationEntryDisappeared(this, new Entry(this, match));
        }
    }

}
