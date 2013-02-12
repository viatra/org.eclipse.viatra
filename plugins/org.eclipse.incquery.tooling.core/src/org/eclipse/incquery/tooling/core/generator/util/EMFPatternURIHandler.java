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
        // XXX is the isEmpty needed?
        if (sup != null && !sup.getEClassifiers().isEmpty()) {
            return sup;
        } else {
            return null;
        }
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
        if (uri.isPlatform()) {
            String fragment = uri.fragment();
            
            String testFragment = fragment;
            String remainingFragment = "";
            while (!testFragment.isEmpty()) {
                URI fragmentRemoved = testFragment.isEmpty() ? uri : uri.appendFragment(testFragment);
                EPackage p = uriToEPackageMap.get(fragmentRemoved);
                if (p != null) {
                    URI newUri = packageUriMap.get(p);
                    String newFragment = newUri.fragment() == null ? remainingFragment : newUri.fragment()
                            + remainingFragment;
                    if (newFragment == null) {
                        newUri = newUri.trimFragment();
                    } else {
                        newUri = newUri.appendFragment(newFragment);
                    }
                    return newUri;
                }
                int index = testFragment.lastIndexOf("/");
                testFragment = testFragment.substring(0, index);
                remainingFragment = fragment.substring(index);
            }
        }
        return super.deresolve(uri);
    }

}
