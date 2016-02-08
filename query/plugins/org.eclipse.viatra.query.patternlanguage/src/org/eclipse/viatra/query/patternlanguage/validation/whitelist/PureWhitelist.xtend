/*******************************************************************************
 * Copyright (c) 2010-2015, Denes Harmath, Istvan Rath and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Denes Harmath - initial API and implementation
 *******************************************************************************/
package org.eclipse.viatra.query.patternlanguage.validation.whitelist

import org.eclipse.xtend.lib.annotations.Data
import org.eclipse.xtext.common.types.JvmOperation
import com.google.common.annotations.VisibleForTesting

/**
 * A whitelist that contains pure elements.
 */
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

    public static val INSTANCE = new PureWhitelist // XXX use DI to eliminate static state

    private new() {
    }

    val pureElements = <PureElement>newHashSet

    def boolean contains(JvmOperation jvmOperation) {
        pureElements.exists[covers(jvmOperation)]
    }

    def void add(PureElement pureElement) {
        pureElements.add(pureElement)
    }
    
    @VisibleForTesting
    def void clear() {
        pureElements.clear
    }

}