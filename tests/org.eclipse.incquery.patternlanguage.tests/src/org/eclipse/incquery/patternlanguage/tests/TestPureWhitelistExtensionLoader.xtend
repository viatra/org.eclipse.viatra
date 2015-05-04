/** 
 * Copyright (c) 2010-2012, Istvan Rath, Zoltan Ujhelyi and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Contributors:
 * Istvan Rath, Zoltan Ujhelyi - initial API and implementation
 */
package org.eclipse.incquery.patternlanguage.tests

import org.eclipse.incquery.patternlanguage.validation.whitelist.PureWhitelist
import org.eclipse.incquery.patternlanguage.validation.whitelist.PureWhitelistExtensionLoader
import org.eclipse.xtext.common.types.TypesFactory
import org.junit.Test

import static org.junit.Assert.assertTrue

class TestPureWhitelistExtensionLoader {
    
    @Test
    def void loaded() {
        val extension factory = TypesFactory.eINSTANCE
        PureWhitelistExtensionLoader.load
        val jvmOperation = createJvmOperation => [
            simpleName = "method" // see plugin.xml
        ]
        createJvmGenericType => [
            simpleName = "Class"
            packageName = "package"
            members += jvmOperation
        ]
        assertTrue(PureWhitelist.INSTANCE.contains(jvmOperation))
    }

}
