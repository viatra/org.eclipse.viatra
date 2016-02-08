/*******************************************************************************
 * Copyright (c) 2010-2012, Istvan Rath and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Tamas Szabo, Gabor Bergmann - initial API and implementation
 *******************************************************************************/

package org.eclipse.incquery.runtime.base.core;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.incquery.runtime.base.api.IEStructuralFeatureProcessor;
import org.eclipse.incquery.runtime.base.api.NavigationHelper;
import org.eclipse.incquery.runtime.base.itc.igraph.IGraphDataSource;
import org.eclipse.incquery.runtime.base.itc.igraph.IGraphObserver;

import com.google.common.collect.HashMultiset;
import com.google.common.collect.Multiset;

// TODO IBiDirectionalGraphDataSource
public class EMFDataSource implements IGraphDataSource<EObject> {

	private static final long serialVersionUID = 5404152895901306358L;
	private List<IGraphObserver<EObject>> observers;
    private Set<EReference> references;
    private Set<EClass> classes;
	private NavigationHelper navigationHelper;
	private Multiset<EObject> allEObjects;	// contains objects even if only appearing as sources or targets
	
	/**
	 * @param navigationHelper
	 * @param references
	 * @param classes additional classes to treat as nodes. Source and target classes of references need not be added. 
	 */
	public EMFDataSource(NavigationHelper navigationHelper, Set<EReference> references, Set<EClass> classes) {
		this.references = references;
		this.classes = classes;
		this.observers = new ArrayList<IGraphObserver<EObject>>();
		this.navigationHelper = navigationHelper;
	}
	
	@Override
	public Set<EObject> getAllNodes() {
		return getAllEObjects().elementSet();
	}
	
	@Override
	public List<EObject> getTargetNodes(EObject source) {
		List<EObject> targetNodes = new ArrayList<EObject>();
		
		for (EReference ref : references) {
			final Set<EObject> referenceValues = navigationHelper.getReferenceValues(source, ref);
			targetNodes.addAll(referenceValues);
		}
		
		return targetNodes;
	}

    @Override
    public void attachObserver(IGraphObserver<EObject> go) {
        observers.add(go);
    }

    @Override
    public void detachObserver(IGraphObserver<EObject> go) {
        observers.remove(go);
    }

    public void notifyEdgeInserted(EObject source, EObject target) {
    	nodeAdditionInternal(source);
    	nodeAdditionInternal(target);
        for (IGraphObserver<EObject> o : observers) {
            o.edgeInserted(source, target);
        }
    }

    public void notifyEdgeDeleted(EObject source, EObject target) {
        for (IGraphObserver<EObject> o : observers) {
            o.edgeDeleted(source, target);
        }
    	nodeRemovalInternal(source);
    	nodeRemovalInternal(target);
    }

    public void notifyNodeInserted(EObject node) {
		nodeAdditionInternal(node);
    }

    public void notifyNodeDeleted(EObject node) {
		nodeRemovalInternal(node);
    }

    private void nodeAdditionInternal(EObject node) {
    	boolean news = !getAllEObjects().contains(node);
    	allEObjects.add(node);
    	if (news) for (IGraphObserver<EObject> o : observers) {
    		o.nodeInserted(node);
    	}
    }
    
	private void nodeRemovalInternal(EObject node) {
		getAllEObjects().remove(node);
		boolean news = !allEObjects.contains(node);
		if (news) for (IGraphObserver<EObject> o : observers) {
		    o.nodeDeleted(node);
		}
	}

	protected Multiset<EObject> getAllEObjects() {
		if (allEObjects == null) {
			allEObjects = HashMultiset.create();
			for (EClass clazz : classes) {
				allEObjects.addAll(navigationHelper.getAllInstances(clazz));
			}
	        for (EReference ref : references) {
                navigationHelper.processAllFeatureInstances(ref,
                        new IEStructuralFeatureProcessor() {

                            @Override
                            public void process(EStructuralFeature feature, EObject source, Object target) {
                                allEObjects.add(source);
                                allEObjects.add((EObject) target);
                            }
                        });
	        }		
		}
		return allEObjects;
	}
}
