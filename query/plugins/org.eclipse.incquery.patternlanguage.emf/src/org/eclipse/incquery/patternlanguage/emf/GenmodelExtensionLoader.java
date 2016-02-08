/*******************************************************************************
 * Copyright (c) 2010-2014, Zoltan Ujhelyi, Istvan Rath and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Zoltan Ujhelyi - initial API and implementation
 *******************************************************************************/
package org.eclipse.incquery.patternlanguage.emf;

import java.util.Map;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.Platform;
import org.eclipse.emf.common.util.URI;

import com.google.common.collect.Maps;
import com.google.inject.Singleton;

/**
 * A genmodel mapping loader that uses the {@value #EPACKAGE_EXTENSION_ID} extensions to populate the
 * {@link EcoreGenmodelRegistry}.
 * 
 * @author Zoltan Ujhelyi
 * 
 */
@Singleton
public class GenmodelExtensionLoader implements IGenmodelMappingLoader {

    private static final String EPACKAGE_EXTENSION_ID = "org.eclipse.emf.ecore.generated_package";
    private static final String GENMODEL_ATTRIBUTE = "genModel";
    private static final String URI_ATTRIBUTE = "uri";
    private Map<String, String> genmodelUriMap;

    @Override
    public Map<String, String> loadGenmodels() {
        if (genmodelUriMap != null) {
            return genmodelUriMap;
        }
        genmodelUriMap = Maps.newHashMap();
        if (Platform.getExtensionRegistry() != null) {
            IConfigurationElement[] packages = Platform.getExtensionRegistry().getConfigurationElementsFor(
                    EPACKAGE_EXTENSION_ID);
            for (IConfigurationElement packageExtension : packages) {
                if (packageExtension.isValid()) {
                    String genmodelUri = packageExtension.getAttribute(GENMODEL_ATTRIBUTE);
                    if (genmodelUri != null && !genmodelUri.isEmpty()) {
                        String uri = packageExtension.getAttribute(URI_ATTRIBUTE);
                        if (URI.createURI(genmodelUri).isRelative()) {
                            genmodelUriMap.put(uri, String.format("platform:/plugin/%s/%s", packageExtension
                                    .getContributor().getName(), genmodelUri));
                        } else {
                            genmodelUriMap.put(uri, genmodelUri);
                        }
                    }
                }
            }
        }
        return genmodelUriMap;
    }
}
