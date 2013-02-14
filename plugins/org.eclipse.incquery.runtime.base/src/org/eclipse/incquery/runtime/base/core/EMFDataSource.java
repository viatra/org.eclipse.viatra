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
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.incquery.runtime.base.api.NavigationHelper;
import org.eclipse.incquery.runtime.base.exception.IncQueryBaseException;
import org.eclipse.incquery.runtime.base.itc.igraph.IGraphDataSource;
import org.eclipse.incquery.runtime.base.itc.igraph.IGraphObserver;

public class EMFDataSource implements IGraphDataSource<EObject> {

	private static final long serialVersionUID = 5404152895901306358L;
	private List<IGraphObserver<EObject>> observers;
    private Set<EReference> references;
    private Set<EClass> classes;
	private NavigationHelper navigationHelper;
	
	public EMFDataSource(NavigationHelper navigationHelper, Set<EReference> references, Set<EClass> classes) throws IncQueryBaseException {
		this.references = references;
		this.classes = classes;
		this.observers = new ArrayList<IGraphObserver<EObject>>();
		this.navigationHelper = navigationHelper;
	}
	
	@Override
	public Set<EObject> getAllNodes() {
		Set<EObject> nodes = new HashSet<EObject>();
		for (EClass clazz : classes) {
			nodes.addAll(navigationHelper.getAllInstances(clazz));
		}
		return nodes;
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
        for (IGraphObserver<EObject> o : observers) {
            o.edgeInserted(source, target);
        }
    }

    public void notifyEdgeDeleted(EObject source, EObject target) {
        for (IGraphObserver<EObject> o : observers) {
            o.edgeDeleted(source, target);
        }
    }

    public void notifyNodeInserted(EObject node) {
        for (IGraphObserver<EObject> o : observers) {
            o.nodeInserted(node);
        }
    }

    public void notifyNodeDeleted(EObject node) {
        for (IGraphObserver<EObject> o : observers) {
            o.nodeDeleted(node);
        }
    }

    @Override
    public List<EObject> getTargetNodes(EObject source) {
        List<EObject> targetNodes = new ArrayList<EObject>();

        for (EReference ref : references) {
           targetNodes.addAll(navigationHelper.getReferenceValues(source, ref));
        }
        
        return targetNodes;
    }
}
