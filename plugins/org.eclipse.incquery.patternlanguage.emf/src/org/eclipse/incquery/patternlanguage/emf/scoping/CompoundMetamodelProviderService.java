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
package org.eclipse.incquery.patternlanguage.emf.scoping;

import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.xtext.scoping.IScope;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.inject.Inject;
import com.google.inject.Singleton;

/**
 * A new, delegating metamodel provider that can handle multiple different {@link IMetamodelProviderInstance}
 * implementations, and sorts them based on priority.
 * 
 * @author Zoltan Ujhelyi
 * @since 1.0
 */
@Singleton
public class CompoundMetamodelProviderService implements IMetamodelProvider {

    private List<IMetamodelProviderInstance> sortedProviders;

    @Inject
    public CompoundMetamodelProviderService(Set<IMetamodelProviderInstance> providers) {
        sortedProviders = Lists.newArrayList(providers);
        Collections.sort(sortedProviders, new Comparator<IMetamodelProviderInstance>() {

            @Override
            public int compare(IMetamodelProviderInstance o1, IMetamodelProviderInstance o2) {
                return o1.getPriority() - o2.getPriority();
            }
        });
    }

    @Override
    public IScope getAllMetamodelObjects(IScope delegateScope, EObject context) {
        IScope calculatedScope = delegateScope;
        for (IMetamodelProviderInstance instance : sortedProviders) {
            calculatedScope = instance.getAllMetamodelObjects(calculatedScope, context);
        }
        return calculatedScope;
    }

    @Override
    public EPackage loadEPackage(String uri, ResourceSet resourceSet) {
        EPackage ePackage = null;
        Iterator<IMetamodelProviderInstance> it = sortedProviders.iterator();
        while (ePackage == null && it.hasNext()) {
            ePackage = it.next().loadEPackage(uri, resourceSet);
        }
        return ePackage;
    }

    @Override
    public boolean isGeneratedCodeAvailable(EPackage ePackage, ResourceSet set) {
        boolean codeFound = false;
        Iterator<IMetamodelProviderInstance> it = sortedProviders.iterator();
        while (!codeFound && it.hasNext()) {
            codeFound = it.next().isGeneratedCodeAvailable(ePackage, set);
        }
        return codeFound;
    }

    @Override
    public String getQualifiedClassName(EClassifier classifier, ResourceSet set) {
        String fqn = null;
        Iterator<IMetamodelProviderInstance> it = sortedProviders.iterator();
        while (Strings.isNullOrEmpty(fqn) && it.hasNext()) {
            fqn = it.next().getQualifiedClassName(classifier, set);
        }
        return fqn;
    }
}
