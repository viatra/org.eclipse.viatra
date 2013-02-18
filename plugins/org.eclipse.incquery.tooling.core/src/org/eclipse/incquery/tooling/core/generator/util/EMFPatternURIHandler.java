/*******************************************************************************
 * Copyright (c) 2010-2012, Mark Czotter, Istvan Rath and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Mark Czotter - initial API and implementation
 *******************************************************************************/

package org.eclipse.incquery.tooling.core.generator.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.ENamedElement;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emf.ecore.xmi.impl.URIHandlerImpl;

import com.google.common.collect.Maps;

public class EMFPatternURIHandler extends URIHandlerImpl {

    private final Map<URI, EPackage> uriToEPackageMap = Maps.newHashMap();
    private final Map<EPackage, URI> packageUriMap = Maps.newHashMap();

    public EMFPatternURIHandler(Collection<EPackage> packages) {
        for (EPackage e : packages) {
            URI uri = EcoreUtil.getURI(e);
            uriToEPackageMap.put(uri, e);
            URI modifiedUri = calculateModifiedUri(e);
            packageUriMap.put(e, modifiedUri);
        }
    }

    private EPackage nonEmptySuperPackage(EPackage p) {
        EPackage sup = p.getESuperPackage();
        while (sup != null && sup.getEClassifiers().isEmpty()) {
            // Skipping empty superpackages
            sup = sup.getESuperPackage();
        }
        return sup;
    }

    private URI calculateModifiedUri(EPackage e) {
        String uriString = e.getNsURI();
        List<String> fragments = new ArrayList<String>();
        EPackage p = e;
        while (nonEmptySuperPackage(p) != null) {
            fragments.add(p.getNsPrefix());
            p = nonEmptySuperPackage(p);
            uriString = p.getNsURI();

        }
        StringBuilder sb = new StringBuilder();
        for (int i = fragments.size() - 1; i >= 0; i--) {
            sb.append("/");
            sb.append(fragments.get(i));
        }
        URI uri = URI.createURI(uriString);
        if (sb.length() > 0) {
            return uri.appendFragment("/" + sb.toString());
        }
        return uri;
    }

    @Override
    public URI deresolve(URI uri) {
        if (uri.isPlatform() || uri.isFile() || uri.isArchive()) {
            String fragment = uri.fragment();
            
            String testFragment = fragment;
            String remainingFragment = "";
            while (!testFragment.isEmpty()) {
                URI fragmentRemoved = testFragment.isEmpty() ? uri : uri.appendFragment(testFragment);
                EPackage p = uriToEPackageMap.get(fragmentRemoved);
                if (p != null) {
                    EObject eObject = p.eResource().getEObject(fragment);
                    if(eObject != null) {
                        if(eObject != p && eObject instanceof ENamedElement) {
                            String name = ((ENamedElement) eObject).getName();
                            if(!remainingFragment.endsWith(name)) {
                                remainingFragment = "/" + name;
                            }
                        }
                        URI newUri = packageUriMap.get(p);
                        String newFragment = newUri.fragment() == null ? "/" + remainingFragment : newUri.fragment()
                            + remainingFragment;
                        newUri = newUri.appendFragment(newFragment);
                        return newUri;
                    }
                }
                int index = testFragment.lastIndexOf('/');
                testFragment = index == -1 ? "/" : testFragment.substring(0, index);
                remainingFragment = index == -1 ? fragment : fragment.substring(index);
            }
            
            /*
             * Alternative implementation when IDs are used.
             * It first finds the package that contains the object,
             * then constructs a new URI using the package URI. 
             * FIXME what happens if two packages contain objects with the exact same ID???
             */
            for (EPackage p : uriToEPackageMap.values()) {
                EObject eObject = p.eResource().getEObject(fragment);
                if(eObject != null) {
                    if(eObject instanceof ENamedElement) {
                        String name = ((ENamedElement) eObject).getName();
                        remainingFragment = "/" + name;
                    }
                    URI newUri = packageUriMap.get(p);
                    String newFragment = newUri.fragment() == null ? "/" + remainingFragment : newUri.fragment()
                        + remainingFragment;
                    newUri = newUri.appendFragment(newFragment);
                    return newUri;
                }
            }
        }
        return super.deresolve(uri);
    }

}
