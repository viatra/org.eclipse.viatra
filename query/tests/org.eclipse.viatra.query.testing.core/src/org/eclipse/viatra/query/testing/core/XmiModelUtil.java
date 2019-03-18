/*******************************************************************************
 * Copyright (c) 2010-2012, Mark Czotter, Andras Okros, Istvan Rath and Daniel Varro
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-v20.html.
 * 
 * SPDX-License-Identifier: EPL-2.0
 *******************************************************************************/
package org.eclipse.viatra.query.testing.core;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.URIConverter;
import org.eclipse.xtext.common.types.access.IJvmTypeProvider;
import org.eclipse.xtext.common.types.access.IJvmTypeProvider.Factory;

import com.google.inject.Injector;

/**
 * Utility class for loading Global XMI model on path queries/globalEiqModel.xmi.
 */
public class XmiModelUtil {

    /**
     * An enum implementation to be used in conjuction with the XmiModelUtil.
     */
    public enum XmiModelUtilRunningOptionEnum {

        /**
         * Use this if you intend to call the XmiModelUtil with workspace only URIs.
         */
        JUST_RESOURCE,

        /**
         * Use this if you intend to call the XmiModelUtil with plugin only URIs.
         */
        JUST_PLUGIN,

        /**
         * Default choice should be this. Use this if you intend to call the XmiModelUtil with both workspace and plugin
         * URIs.
         */
        BOTH;

    }

    /**
     * Prepares an Xtext resource set with the given injector
     *
     * @return the injected resource set
     */
    public static ResourceSet prepareXtextResource(Injector injector) {
        ResourceSet set = injector.getInstance(ResourceSet.class);
        Factory cptf = injector.getInstance(IJvmTypeProvider.Factory.class);
        cptf.createTypeProvider(set);
        return set;
    }

    /**
     * First tries to resolve the path with platformResource (in workspace), if not found, uses the platformPluginURI
     * (in bundles).
     *
     * @param platformURI
     *            the URI to resolve
     * @param optionEnum
     *            the option whether to use Resource or Plugin or both type of URIs
     * @return the EMF URI for the given platform URI
     */
    public static URI resolvePlatformURI(XmiModelUtilRunningOptionEnum optionEnum, String platformURI) {
        URI uri = resolvePlatformResourceURI(optionEnum, platformURI);
        if (uri != null) {
            return uri;
        }
        uri = resolvePlatformPluginURI(optionEnum, platformURI);
        if (uri != null) {
            return uri;
        }
        throw new RuntimeException("Error while opening platform resource " + platformURI + ": Resource not found.");
    }

    private static URI resolvePlatformResourceURI(XmiModelUtilRunningOptionEnum optionEnum, String platformURI) {
        if (XmiModelUtilRunningOptionEnum.BOTH.equals(optionEnum)
                || XmiModelUtilRunningOptionEnum.JUST_RESOURCE.equals(optionEnum)) {
            URI resourceURI = URI.createPlatformResourceURI(platformURI, true);
            if (URIConverter.INSTANCE.exists(resourceURI, null)) {
                return resourceURI;
            }
        }
        return null;
    }

    private static URI resolvePlatformPluginURI(XmiModelUtilRunningOptionEnum optionEnum, String platformURI) {
        if (XmiModelUtilRunningOptionEnum.BOTH.equals(optionEnum)
                || XmiModelUtilRunningOptionEnum.JUST_PLUGIN.equals(optionEnum)) {
            URI pluginURI = URI.createPlatformPluginURI(platformURI, true);
            if (URIConverter.INSTANCE.exists(pluginURI, null)) {
                return pluginURI;
            }
        }
        return null;
    }

}