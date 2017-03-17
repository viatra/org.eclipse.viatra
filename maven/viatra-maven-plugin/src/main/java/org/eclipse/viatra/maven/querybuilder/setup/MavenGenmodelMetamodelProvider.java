/*******************************************************************************
 * Copyright (c) 2010-2015, Zoltan Ujhelyi, Istvan Rath and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Zoltan Ujhelyi - initial API and implementation
 *******************************************************************************/
package org.eclipse.viatra.maven.querybuilder.setup;

import java.util.Collection;

import org.eclipse.emf.codegen.ecore.genmodel.GenPackage;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.viatra.query.patternlanguage.emf.EcoreGenmodelRegistry;
import org.eclipse.viatra.query.patternlanguage.emf.helper.GeneratorModelHelper;
import org.eclipse.viatra.query.patternlanguage.emf.scoping.BaseMetamodelProviderService;
import org.eclipse.viatra.query.patternlanguage.emf.scoping.IMetamodelProviderInstance;

import com.google.inject.Inject;

public class MavenGenmodelMetamodelProvider extends BaseMetamodelProviderService implements IMetamodelProviderInstance {

    @Inject
    private EcoreGenmodelRegistry registry;
    
    @Override
    public String getIdentifier() {
        return "maven";
    }
    
    @Override
    public int getPriority() {
        return 0;
    }
    @Override
    public EPackage loadEPackage(String uri, ResourceSet resourceSet) {
        final GenPackage genPackage = registry.findGenPackage(uri, resourceSet);
        return genPackage == null ? null : genPackage.getEcorePackage();
    }


    @Override
    protected Collection<String> getProvidedMetamodels() {
        return registry.getPackageUris();
    }

    @Override
    protected String doGetQualifiedClassName(EClassifier classifier, EObject context) {
        if (context.eResource() != null) {
            ResourceSet set = context.eResource().getResourceSet();
            EPackage ePackage = classifier.getEPackage();
            if (ePackage != null && set != null) {
                GenPackage genPackage = registry.findGenPackage(ePackage.getNsURI(), set);
                if (genPackage != null) {
                    return GeneratorModelHelper.resolveTypeReference(genPackage, classifier);
                }
            }
        }
        return null;
    }

}
