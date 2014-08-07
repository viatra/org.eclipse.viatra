/*******************************************************************************
 * Copyright (c) 2010-2014, Bergmann Gabor, Istvan Rath and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Bergmann Gabor - initial API and implementation
 *******************************************************************************/
package org.eclipse.incquery.runtime.emf;

import org.apache.log4j.Logger;
import org.eclipse.emf.common.notify.Notifier;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.incquery.runtime.api.IncQueryEngine;
import org.eclipse.incquery.runtime.api.scope.IEngineContext;
import org.eclipse.incquery.runtime.api.scope.IncQueryScope;
import org.eclipse.incquery.runtime.base.api.BaseIndexOptions;
import org.eclipse.incquery.runtime.exception.IncQueryException;

/**
 * A IncQuery scope consisting of EMF objects contained in an EMF resource set, a single resource, or a containment subtree below a given object.
 * 
 * <p> The scope is characterized by a root, and some options (see {@link BaseIndexOptions}) such as dynamic EMF mode, subtree filtering, etc.
 * <p>
 * The scope of pattern matching will be the given EMF model root and below (see FAQ for more precise definition).
 * @author Bergmann Gabor
 *
 */
public class EMFScope extends IncQueryScope {
	
	Notifier scopeRoot;
	BaseIndexOptions options;

	/**
	 * Creates an EMF scope at the given root, with default options (recommended for most users).
	 * @param scopeRoot the root of the EMF scope
	 * @throws IncQueryException if scopeRoot is not an EMF ResourceSet, Resource or EObject
	 */
	public EMFScope(Notifier scopeRoot) throws IncQueryException {
		this(scopeRoot, new BaseIndexOptions() );
	}
	/**
	 * Creates an EMF scope at the given root, with customizable options.
	 * <p> Most users should consider {@link #EMFScope(Notifier)} instead.
	 * @param scopeRoot the root of the EMF scope
	 * @param options 
	 * @throws IncQueryException if scopeRoot is not an EMF ResourceSet, Resource or EObject
	 */
	public EMFScope(Notifier scopeRoot, BaseIndexOptions options) throws IncQueryException {
		super();
		this.scopeRoot = scopeRoot;
		this.options = options.copy();
        if (!(scopeRoot instanceof EObject || scopeRoot instanceof Resource || scopeRoot instanceof ResourceSet))
            throw new IncQueryException(IncQueryException.INVALID_EMFROOT
                    + (scopeRoot == null ? "(null)" : scopeRoot.getClass().getName()),
                    IncQueryException.INVALID_EMFROOT_SHORT);
	}

	/**
	 * @return the root element (resource set, resource or eObject) containing the model
	 */
	public Notifier getScopeRoot() {
		return scopeRoot;
	}
	
	/**
	 * @return the options
	 */
	public BaseIndexOptions getOptions() {
		return options.copy();
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((options == null) ? 0 : options.hashCode());
		result = prime * result
				+ ((scopeRoot == null) ? 0 : scopeRoot.hashCode());
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof EMFScope))
			return false;
		EMFScope other = (EMFScope) obj;
		if (options == null) {
			if (other.options != null)
				return false;
		} else if (!options.equals(other.options))
			return false;
		if (scopeRoot == null) {
			if (other.scopeRoot != null)
				return false;
		} else if (!scopeRoot.equals(other.scopeRoot))
			return false;
		return true;
	}	
	

	@Override
	public String toString() {
		return String.format("EMFScope(%s):%s", options, scopeRoot);
	}

	@Override
	protected IEngineContext createEngineContext(IncQueryEngine engine, Logger logger) {
		return new EMFEngineContext(this, engine, logger);
	}
	
}
