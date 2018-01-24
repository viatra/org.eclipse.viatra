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
package org.eclipse.viatra.query.patternlanguage.emf.validation.whitelist

import org.eclipse.core.runtime.Platform

/**
 * Adds pure elements defined by the appropriate extension to the whitelist.
 * @since 2.0
 */
class PureWhitelistExtensionLoader {

    public static val EXTENSION_ID = "org.eclipse.viatra.query.patternlanguage.emf.purewhitelist"

    static def void load() {
        val configurationElements = Platform.extensionRegistry.getConfigurationElementsFor(EXTENSION_ID)
        val pureElements = configurationElements.map[
            // Bug 496473: make sure whitespace is removed from fqn
            val fullyQualifiedName = getAttribute("fully-qualified-name").replaceAll("\\s","");
            val type = PureWhitelist.PureElement.Type.valueOf(getAttribute("type").toUpperCase)
            new PureWhitelist.PureElement(fullyQualifiedName, type)
        ]
        pureElements.forEach[PureWhitelist.INSTANCE.add(it)]
    }

}
