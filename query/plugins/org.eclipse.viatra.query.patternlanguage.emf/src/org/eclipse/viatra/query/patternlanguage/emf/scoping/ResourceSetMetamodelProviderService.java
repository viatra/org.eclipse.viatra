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
package org.eclipse.viatra.query.patternlanguage.emf.scoping;

import java.util.Collection;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import org.eclipse.emf.common.util.TreeIterator;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.xtext.resource.DerivedStateAwareResource;

import com.google.common.collect.Lists;

/**
 * @author Zoltan Ujhelyi
 *
 */
public class ResourceSetMetamodelProviderService extends BaseMetamodelProviderService implements IMetamodelProviderInstance{


    @Override
    public String getIdentifier() {
        return "resourceset";
    }


    @Override
    public int getPriority() {
        return 2;
    }
    
    @Override
    public EPackage loadEPackage(String packageUri, ResourceSet resourceSet) {
        EPackage pack = lookUpEPackageInResourceSetContents(packageUri, resourceSet);
        if (pack != null) {
            return pack;
        } else {
            URI uri = null;
            try {
                uri = URI.createURI(packageUri);
                if (uri.fragment() == null) {
                    Resource resource = resourceSet.getResource(uri, true);
                    return (EPackage) resource.getContents().get(0);
                }
                return (EPackage) resourceSet.getEObject(uri, true);
            } catch (RuntimeException ex) {
                if (uri != null && uri.isPlatformResource()) {
                    String platformString = uri.toPlatformString(true);
                    URI platformPluginURI = URI.createPlatformPluginURI(platformString, true);
                    return loadEPackage(platformPluginURI.toString(), resourceSet);
                }
                logger.trace("Cannot load package with URI '" + packageUri + "'", ex);
                return null;
            }
        }
    }


    @Override
    protected Collection<String> getProvidedMetamodels() {
        return Lists.newArrayList();
    }
    
    private EPackage lookUpEPackageInResourceSetContents(String packageUri, ResourceSet resourceSet) {
        Set<Resource> processedResources = new HashSet<>();
        while (processedResources.size() != resourceSet.getResources().size()) {
            Set<Resource> resources = new HashSet<>(resourceSet.getResources());
            resources.removeAll(processedResources);
            for (Resource resource : resources) {
                EPackage pkg = findEPackageInResource(packageUri, resource);
                if (pkg != null) {
                    return pkg;
                }
                processedResources.add(resource);
            }
        }
        return null;
    }

    private EPackage findEPackageInResource(String packageUri, Resource resource) {
        if (resource instanceof DerivedStateAwareResource && !((DerivedStateAwareResource) resource).isFullyInitialized()) {
            return null;
        }
        TreeIterator<EObject> it = resource.getAllContents();
        while (it.hasNext()) {
            EObject obj = it.next();
            if (obj instanceof EPackage) {
                if (Objects.equals(((EPackage) obj).getNsURI(), packageUri)) {
                    return (EPackage) obj;
                }
            } else {
                it.prune();
            }
        }
        return null;
    }


    @Override
    protected String doGetQualifiedClassName(EClassifier classifier, EObject context) {
        return null;
    }

}
