/*******************************************************************************
 * Copyright (c) 2010-2015, Andras Szabolcs Nagy and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Contributors:
 *   Andras Szabolcs Nagy - initial API and implementation
 *******************************************************************************/
package org.eclipse.viatra.dse.util;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import org.eclipse.emf.ecore.EObject;

import com.google.common.base.Functions;
import com.google.common.collect.Ordering;

/**
 * 
 * @author Andras Szabolcs Nagy
 *
 */
public class ValueComparableEObjectStringMap extends TreeMap<EObject, String> {

    private final static class EObjectComparator implements Comparator<EObject> {
        @Override
        public int compare(EObject o1, EObject o2) {
            return Integer.valueOf(System.identityHashCode(o1)).compareTo(Integer.valueOf(System.identityHashCode(o2)));
        }
    }

    private final Map<EObject, String> innerMap;

    public ValueComparableEObjectStringMap() {
        this(new HashMap<EObject, String>());
    }

    private ValueComparableEObjectStringMap(Map<EObject, String> innerMap) {
        super(Ordering.natural().onResultOf(Functions.forMap(innerMap)).compound(new EObjectComparator()));
        this.innerMap = innerMap;
    }

    public String put(EObject keyEObject, String stringValue) {
        if (innerMap.containsKey(keyEObject)) {
            remove(keyEObject);
        }
        innerMap.put(keyEObject, stringValue);
        return super.put(keyEObject, stringValue);
    }
}
