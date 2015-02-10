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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.emf.codegen.ecore.genmodel.GenPackage;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.incquery.patternlanguage.emf.scoping.MetamodelProviderService;
import org.eclipse.xtext.naming.IQualifiedNameConverter;
import org.eclipse.xtext.naming.QualifiedName;
import org.eclipse.xtext.resource.EObjectDescription;
import org.eclipse.xtext.resource.IEObjectDescription;
import org.eclipse.xtext.scoping.IScope;
import org.eclipse.xtext.scoping.impl.SimpleScope;

import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.google.inject.Inject;

/**
 * Subclass implementation of MetamdelProviderService, which queires registered metamodel packages
 * from the TargetPlatform instead of the PackageRegistry.
 *
 */
public class TargetPlatformMetamodelProviderService extends
		MetamodelProviderService {

	@Inject
	private ITargetPlatformMetamodelLoader metamodelLoader;
	
	 @Inject
	 private IQualifiedNameConverter qualifiedNameConverter;
	
	@Override
	public IScope getAllMetamodelObjects(IScope delegateScope, EObject context) {
		final ResourceSet resourceSet = context.eResource().getResourceSet();
		List<String> tpmetamodels = metamodelLoader.listEPackages();
		List<IEObjectDescription> metamodels = new ArrayList<IEObjectDescription>();
		for (String metamodel : tpmetamodels) {
		    EPackage ePackage = metamodelLoader.loadPackage(resourceSet, metamodel);
            QualifiedName qualifiedName = qualifiedNameConverter.toQualifiedName(metamodel);
            metamodels.add(EObjectDescription.create(qualifiedName, ePackage,
                    Collections.singletonMap("nsURI", "true")));
		}
		
		for (IEObjectDescription description : delegateScope.getAllElements()) {
		    String value = description.getUserData("nsURI");
		    boolean isNsURI = (value == null) ? false : Boolean.valueOf(value);
		    if (!tpmetamodels.contains(description.getQualifiedName().toString()) && isNsURI) {
		        metamodels.add(description);
		        tpmetamodels.add(description.getQualifiedName().toString());
		    }
		}
        return new SimpleScope(IScope.NULLSCOPE, Iterables.filter(metamodels, 
        		new Predicate<IEObjectDescription>() {
        	public boolean apply(IEObjectDescription desc){
        		return desc.getEObjectOrProxy() != null;
        	}
		}));
	}
	
	protected GenPackage internalFindGenPackage(ResourceSet resourceSet, String packageUri){
		return metamodelLoader.loadGenPackage(resourceSet, packageUri);
	}
	
	@Override
	public EPackage loadEPackage(String packageUri, ResourceSet resourceSet) {
	    EPackage pack;
	    pack = super.loadEPackage(packageUri, resourceSet);
	    if (pack != null) return pack;
        pack = metamodelLoader.loadPackage(resourceSet, packageUri);
        return pack;
	}

    @Override
    public boolean isGeneratedCodeAvailable(EPackage ePackage, ResourceSet set) {
        return (metamodelLoader.loadGenPackage(set, ePackage.getNsURI()) != null)
                || super.isGeneratedCodeAvailable(ePackage, set);
    }
	
}
