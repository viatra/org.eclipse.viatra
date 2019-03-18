/*******************************************************************************
 * Copyright (c) 2010-2012, Zoltan Ujhelyi, Istvan Rath and Daniel Varro
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-v20.html.
 * 
 * SPDX-License-Identifier: EPL-2.0
 *******************************************************************************/
package org.eclipse.viatra.query.patternlanguage.emf.scoping;

import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.xtext.resource.IEObjectDescription;
import org.eclipse.xtext.scoping.IScope;

/**
 * 
 * @author Zoltan Ujhelyi, Abel Hegedus
 * @noimplement This interface is not intended to be implemented by clients.
 *
 */
public interface IMetamodelProvider {

    /**
     * Returns a set of all available EPackages wrapped into {@link IEObjectDescription} for the use of scoping.
     * It uses the {@link IEObjectDescription}s from the delegate scope provider too, this way the 
     * {@link EPackage}s from the XText index will be available too. 
     * 
     * @param delegateScope the delegate scope
     * @param context the context object for the scoping
     */
    IScope getAllMetamodelObjects(IScope delegateScope, EObject context);

    /**
     * Loads an EMF package from the nsURI or resource URI of the model, and uses the resource set given as the second
     * parameter.
     * 
     * @param uri
     * @param resourceSet
     * @return the loaded EMF EPackage
     */
    EPackage loadEPackage(String uri, ResourceSet resourceSet);

    /**
     * Returns true if the generated code is available for the given EPackage, the code uses the given resource set
     * as context.
     * 
     * @param ePackage for which the availability of generated code is checked 
     * @param set used for context in the checking
     * @return true, if the generated code for the EPackage is available
     */
    boolean isGeneratedCodeAvailable(EPackage ePackage, ResourceSet set);
    
    /**
     * Returns the identifier of the plugin that contains the generated model code. 
     * 
     * @param ePackage for which the model plugin ID is requested  
     * @param set used for context in the searching
     * @return the model plugin ID or null if it cannot be found
     * @since 1.5
     */
    String getModelPluginId(EPackage ePackage, ResourceSet set);
    
    String getQualifiedClassName(EClassifier classifier, EObject context);
}