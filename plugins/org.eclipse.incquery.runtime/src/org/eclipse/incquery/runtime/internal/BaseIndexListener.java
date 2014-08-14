/*******************************************************************************
 * Copyright (c) 2004-2012 Gabor Bergmann and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Gabor Bergmann - initial API and implementation
 *******************************************************************************/

package org.eclipse.incquery.runtime.internal;

import java.util.HashSet;
import java.util.Set;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EDataType;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.incquery.runtime.api.IncQueryEngine;
import org.eclipse.incquery.runtime.base.api.DataTypeListener;
import org.eclipse.incquery.runtime.base.api.FeatureListener;
import org.eclipse.incquery.runtime.base.api.InstanceListener;
import org.eclipse.incquery.runtime.base.api.NavigationHelper;
import org.eclipse.incquery.runtime.matchers.context.IPatternMatcherRuntimeContextListener;

/**
 * A listener binding as Rete boundary to an eiqBase index
 * 
 * @author Bergmann Gábor
 * 
 */
public class BaseIndexListener implements FeatureListener, InstanceListener, DataTypeListener {
	private final NavigationHelper baseIndex;
    final Set<IPatternMatcherRuntimeContextListener> listeners = 
    		new HashSet<IPatternMatcherRuntimeContextListener>();

    /**
     * This reference is vital, to avoid the premature GC of the engine while the EMF model is still reachable.
     * Retention path: EMF model -> IQBase -> BaseIndexListener -> IQEngine
     */
    @SuppressWarnings("unused")
    private final IncQueryEngine iqEngine;


    /**
     * @param inputConnector
     */
    public BaseIndexListener(IncQueryEngine iqEngine, NavigationHelper baseIndex) {
        super();
        this.iqEngine = iqEngine;
        this.baseIndex = baseIndex;
    }
    
    public void addListener(IPatternMatcherRuntimeContextListener listener) {
    	listeners.add(listener);
    }
    public void removeListener(IPatternMatcherRuntimeContextListener listener) {
    	listeners.remove(listener);
    }


    @Override
    public void instanceInserted(EClass clazz, EObject instance) {
        for (IPatternMatcherRuntimeContextListener listener : listeners) listener.updateUnary(true, instance, clazz);
        for (IPatternMatcherRuntimeContextListener listener : listeners) listener.updateInstantiation(true, clazz, instance);
    }

    @Override
    public void instanceDeleted(EClass clazz, EObject instance) {
        for (IPatternMatcherRuntimeContextListener listener : listeners) listener.updateUnary(false, instance, clazz);
        for (IPatternMatcherRuntimeContextListener listener : listeners) listener.updateInstantiation(false, clazz, instance);
    }

    @Override
    public void dataTypeInstanceInserted(EDataType type, Object instance, boolean first) {
    	if (first) {
	        for (IPatternMatcherRuntimeContextListener listener : listeners) listener.updateUnary(true, instance, type);
	        for (IPatternMatcherRuntimeContextListener listener : listeners) listener.updateInstantiation(true, type, instance);
    	}
    }

    @Override
    public void dataTypeInstanceDeleted(EDataType type, Object instance, boolean last) {
    	if (last) {
	        for (IPatternMatcherRuntimeContextListener listener : listeners) listener.updateUnary(false, instance, type);
	        for (IPatternMatcherRuntimeContextListener listener : listeners) listener.updateInstantiation(false, type, instance);
    	}
    }

    @Override
    public void featureInserted(EObject host, EStructuralFeature feature, Object value) {
        for (IPatternMatcherRuntimeContextListener listener : listeners) listener.updateBinaryEdge(true, host, value, feature);
    }

    @Override
    public void featureDeleted(EObject host, EStructuralFeature feature, Object value) {
        for (IPatternMatcherRuntimeContextListener listener : listeners) listener.updateBinaryEdge(false, host, value, feature);
    }

}
