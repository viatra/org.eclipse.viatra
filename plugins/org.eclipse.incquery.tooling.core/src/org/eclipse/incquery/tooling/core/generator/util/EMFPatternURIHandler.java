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

import java.util.Collection;
import java.util.Map;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emf.ecore.xmi.impl.URIHandlerImpl;

import com.google.common.collect.Maps;

public class EMFPatternURIHandler extends URIHandlerImpl {

    private final Map<URI, EPackage> uriToEPackageMap = Maps.newHashMap();
    private final Map<EPackage, String> packageFragmentMap = Maps.newHashMap();
    private final Map<EPackage, String> packageUriMap = Maps.newHashMap();

    public EMFPatternURIHandler(Collection<EPackage> packages) {
        for (EPackage e : packages) {
            Resource resource = e.eResource();
            if (resource != null) {
                uriToEPackageMap.put(EcoreUtil.getURI(e), e);
                packageFragmentMap.put(e, resource.getURIFragment(e));
                packageUriMap.put(e, calculateModifiedUri(e, ""));
            }
        }
    }

    private String calculateModifiedUri(EPackage e, String fragment) {
        String uri = e.getNsURI();
        if (e.getESuperPackage() != null && uri.startsWith(e.getESuperPackage().getNsURI())) {
            String newFragment = uri.substring(e.getESuperPackage().getNsURI().length()) + fragment;
            return calculateModifiedUri(e.getESuperPackage(), newFragment);
        }
        return e.getNsURI() + "#/" + fragment;
        // if (fragment != "" && fragment != null) {
        // return fragment;
        // }
        // return res.getURIFragment(e);
    }

    @Override
    public URI deresolve(URI uri) {
        if (uri.isPlatform()) {
            String fragment = uri.fragment();
            
            String testFragment = fragment;
            while (!testFragment.isEmpty()) {
                int index = testFragment.lastIndexOf("/");
                testFragment = testFragment.substring(0, index);
                URI fragmentRemoved = uri.appendFragment(testFragment);
                EPackage p = uriToEPackageMap.get(fragmentRemoved);
                if (p != null) {
                    URI newURI = URI.createURI(packageUriMap.get(p));
                    String newFragment = newURI.fragment() + fragment.substring(index);
                    newURI = newURI.appendFragment(newFragment);
                    return newURI;
                }
            }
        }
        return super.deresolve(uri);
    }

}
