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
package org.eclipse.incquery.runtime.matchers.context;


/**
 * Listens for changes in the runtime context.
 * @author Bergmann Gabor
 *
 */
public interface IPatternMatcherRuntimeContextListener {

	public void updateUnary(boolean insertion, 
			Object entity, Object typeObject);

	public void updateTernaryEdge(boolean insertion, 
			Object relation, Object from, Object to, Object typeObject);

	public void updateBinaryEdge(boolean insertion, 
			Object from, Object to, Object typeObject);

	public void updateContainment(boolean insertion, 
			Object container, Object element);

	public void updateInstantiation(boolean insertion, 
			Object parent, Object child);

	public void updateGeneralization(boolean insertion, 
			Object parent, Object child);

}