/*******************************************************************************
 * Copyright (c) 2010-2015, Denes Harmath, Istvan Rath and Daniel Varro
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-v20.html.
 * 
 * SPDX-License-Identifier: EPL-2.0
 *******************************************************************************/
package org.eclipse.viatra.query.patternlanguage.emf.validation.whitelist

import org.eclipse.xtend.lib.annotations.Data
import org.eclipse.xtext.common.types.JvmOperation
import com.google.inject.Singleton
import javax.inject.Inject
import org.apache.log4j.Logger
import org.eclipse.viatra.query.runtime.util.ViatraQueryLoggingUtil

/**
 * A whitelist that contains pure elements.
 * @since 2.0
 */
 @Singleton
class PureWhitelist {

    /**
     * Declares that a method is pure or all methods in a class or package are pure. 
     */
    @Data
    static class PureElement {
        
        String fullyQualifiedName
        
        Type type
        
        enum Type {
            METHOD,
            CLASS,
            PACKAGE
        }

        def boolean covers(JvmOperation jvmOperation) {
            if (jvmOperation.eIsProxy) {
                false 
            } else {
                val qualifiedNameToCheck = switch type {
                    case METHOD: jvmOperation.identifier
                    case CLASS: jvmOperation.declaringType?.identifier
                    case PACKAGE: jvmOperation.declaringType?.packageName
                }
                qualifiedNameToCheck == fullyQualifiedName
            }
        }

    }

    val pureElements = <PureElement>newHashSet
    var initialized = false
    val IPureWhitelistExtensionProvider extensionProvider
    val Logger logger

    new() {
        initialized = false
        extensionProvider = new ServiceLoaderBasedWhitelistExtensionProvider
        logger = ViatraQueryLoggingUtil.getLogger(PureWhitelist)
    }
    
    /**
     * If loadExtensions is false, instead of loading the list of known extensions start with an empty whitelist
     */
    new(boolean loadExtensions) {
        initialized = !loadExtensions
        extensionProvider = new ServiceLoaderBasedWhitelistExtensionProvider
        logger = ViatraQueryLoggingUtil.getLogger(PureWhitelist)
    }
    
    @Inject
    new(IPureWhitelistExtensionProvider extensionProvider, Logger logger) {
        initialized = false
        this.extensionProvider = extensionProvider
        this.logger = logger
    }
    

    def boolean contains(JvmOperation jvmOperation) {
        loadKnownExtensions
        
        pureElements.exists[covers(jvmOperation)]
    }

    def void add(PureElement pureElement) {
        pureElements.add(pureElement)
    }
    
    def void loadKnownExtensions() {
        if (!initialized) {
            for (provider : extensionProvider.pureElementExtensions) {
                try {
                    pureElements.addAll(provider.pureElements)                    
                } catch (Exception e) {
                    logger.error("Error while loading extensions from provider " + provider.class.name, e)
                }
            }
            initialized = true
        }
    }

}
