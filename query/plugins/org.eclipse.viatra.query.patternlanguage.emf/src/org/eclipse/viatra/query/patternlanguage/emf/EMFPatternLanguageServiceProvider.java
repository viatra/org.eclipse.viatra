/*******************************************************************************
 * Copyright (c) 2010-2012, Zoltan Ujhelyi, Istvan Rath and Daniel Varro
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-v20.html.
 * 
 * SPDX-License-Identifier: EPL-2.0
 *******************************************************************************/
package org.eclipse.viatra.query.patternlanguage.emf;

import java.util.Objects;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.viatra.query.patternlanguage.emf.vql.PatternLanguagePackage;
import org.eclipse.xtext.resource.IGlobalServiceProvider.ResourceServiceProviderImpl;
import org.eclipse.xtext.resource.IResourceServiceProvider;
import org.eclipse.xtext.resource.IResourceServiceProvider.Registry;

import com.google.inject.Inject;

/**
 * @author Zoltan Ujhelyi
 * 
 */
public class EMFPatternLanguageServiceProvider extends ResourceServiceProviderImpl {

    private final IResourceServiceProvider serviceProvider;

    @Inject
    public EMFPatternLanguageServiceProvider(Registry registry, IResourceServiceProvider serviceProvider) {
        super(registry, serviceProvider);
        this.serviceProvider = serviceProvider;
    }

    @Override
    public <T> T findService(EObject eObject, Class<T> serviceClazz) {
        Resource res = eObject.eResource();
        String nsURI = eObject.eClass().getEPackage().getNsURI();
        if (res == null && Objects.equals(nsURI, PatternLanguagePackage.eNS_URI)) {
            T service = serviceProvider.get(serviceClazz);
            return service;
        } else {
            return super.findService(eObject, serviceClazz);
        }
    }
}
