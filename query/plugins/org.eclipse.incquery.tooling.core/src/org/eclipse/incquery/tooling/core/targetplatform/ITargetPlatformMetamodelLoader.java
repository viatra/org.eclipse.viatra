/*******************************************************************************
 * Copyright (c) 2010-2013, Zoltan Ujhelyi, Balazs Grill, Istvan Rath and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Zoltan Ujhelyi, Balazs Grill - initial API and implementation
 *******************************************************************************/

package org.eclipse.incquery.tooling.core.targetplatform;

import java.util.List;

import org.eclipse.emf.codegen.ecore.genmodel.GenPackage;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.resource.ResourceSet;

/**
 * Interface for loading metamodels from the target platform
 *
 */
public interface ITargetPlatformMetamodelLoader {

	/**
	 * List the URIs of the available EPackages 
	 * @return a list containing the ns URIs of the registered EPackages
	 */
	public List<String> listEPackages();
	
	/**
	 * Load the EPackage with the given ns URI
	 * @param nsURI
	 * @param resourceSet
	 * @return The EPackage instance, or null if it could not be loaded
	 */
	public EPackage loadPackage(ResourceSet resourceSet, String nsURI);
	
	/**
	 * Load the GenPackage of the EPackage with the given ns URI
	 * @param resourceSet
	 * @param nsURI
	 * @return the registered GenPackage of the generated package
	 */
	public GenPackage loadGenPackage(ResourceSet resourceSet, String nsURI);
	
}
