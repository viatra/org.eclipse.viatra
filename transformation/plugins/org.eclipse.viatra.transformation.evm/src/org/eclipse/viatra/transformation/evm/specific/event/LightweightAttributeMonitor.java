/*******************************************************************************
 * Copyright (c) 2010-2013, Abel Hegedus, Istvan Rath and Daniel Varro
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-v20.html.
 * 
 * SPDX-License-Identifier: EPL-2.0
 *******************************************************************************/
package org.eclipse.viatra.transformation.evm.specific.event;

import java.util.Collection;
import java.util.HashSet;

import org.eclipse.viatra.query.runtime.api.IPatternMatch;
import org.eclipse.viatra.query.runtime.api.scope.IBaseIndex;
import org.eclipse.viatra.query.runtime.api.scope.IInstanceObserver;
import org.eclipse.viatra.query.runtime.matchers.util.CollectionsFactory;
import org.eclipse.viatra.query.runtime.matchers.util.CollectionsFactory.MemoryType;
import org.eclipse.viatra.query.runtime.matchers.util.IMemoryView;
import org.eclipse.viatra.query.runtime.matchers.util.IMultiLookup;
import org.eclipse.viatra.transformation.evm.notification.AttributeMonitor;

/**
 * @author Abel Hegedus
 *
 */
public class LightweightAttributeMonitor<MatchType extends IPatternMatch> extends AttributeMonitor<MatchType> {

    private IInstanceObserver observer;
    private IMultiLookup<Object, MatchType> observedMultimap;
    private IBaseIndex index;
    
    public LightweightAttributeMonitor(IBaseIndex index) {
        super();
        this.index = index;
        this.observer = new IInstanceObserver() {
            @Override
            public void notifyBinaryChanged(Object sourceElement,
                    Object edgeType) {
                IMemoryView<MatchType> matches = observedMultimap.lookupOrEmpty(sourceElement);
                for (MatchType matchType : matches) {
                    notifyListeners(matchType);
                }
            }
            
            @Override
            public void notifyTernaryChanged(Object sourceElement,
                    Object edgeType) {
                IMemoryView<MatchType> matches = observedMultimap.lookupOrEmpty(sourceElement);
                for (MatchType matchType : matches) {
                    notifyListeners(matchType);
                }
            }        
        };
        this.observedMultimap = CollectionsFactory.createMultiLookup(Object.class, MemoryType.SETS, Object.class);
    }
    
    @Override
    public void registerFor(MatchType atom) {
        Collection<Object> allObjects = findAllObjects(atom);
        for (Object object : allObjects) {
            index.addInstanceObserver(observer, object);
            observedMultimap.addPair(object, atom);
        }
    }

    @Override
    public void unregisterForAll() {
        for (Object eobj : observedMultimap.distinctKeys()) {
            index.removeInstanceObserver(observer, eobj);
        }
    }

    @Override
    public void unregisterFor(MatchType atom) {
        Collection<Object> allObjects = findAllObjects(atom);
        for (Object object : allObjects) {
            index.removeInstanceObserver(observer, object);
            observedMultimap.removePair(object, atom);
        }
    }

    private Collection<Object> findAllObjects(MatchType atom){
        Collection<Object> objs = new HashSet<>();
        for (String param : atom.parameterNames()) {
            Object location = atom.get(param);
            objs.add(location);
        }
        return objs;
    }
    
}
