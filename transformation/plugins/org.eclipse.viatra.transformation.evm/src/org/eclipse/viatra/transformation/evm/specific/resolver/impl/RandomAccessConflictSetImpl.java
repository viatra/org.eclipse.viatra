/*******************************************************************************
 * Copyright (c) 2010-2013 Gabor Bergmann, Istvan Rath and Daniel Varro
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-v20.html.
 * 
 * SPDX-License-Identifier: EPL-2.0
 *******************************************************************************/
package org.eclipse.viatra.transformation.evm.specific.resolver.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;

import org.eclipse.viatra.transformation.evm.api.Activation;
import org.eclipse.viatra.transformation.evm.api.resolver.ChangeableConflictSet;

public abstract class RandomAccessConflictSetImpl implements ChangeableConflictSet {
    
    protected List<Activation<?>> activationList = new ArrayList<Activation<?>>();
    protected Map<Activation<?>, Integer> activationToPosition = new HashMap<Activation<?>, Integer>();

    @Override
    public Set<Activation<?>> getConflictingActivations() {
        return Collections.unmodifiableSet(new HashSet<>(activationToPosition.keySet()));
    }

    @Override
    public boolean addActivation(Activation<?> activation) {
        if (activationToPosition.containsKey(activation)) return false;
        activationToPosition.put(activation, activationList.size());
        activationList.add(activation);
        return true;
    }

    @Override
    public boolean removeActivation(Activation<?> activation) {
        Integer position = activationToPosition.remove(activation);
        if (position == null) throw new NoSuchElementException();
        int lastPos = activationList.size()-1;
        if (position != lastPos) {
            Activation<?> lastElement = activationList.get(lastPos);
            activationList.set(position, lastElement);
            activationToPosition.put(lastElement, position);
        }
        activationList.remove(lastPos);
        return true;
    }
    
    @Override
    public Set<Activation<?>> getNextActivations() {
        return getConflictingActivations();
    }
}

