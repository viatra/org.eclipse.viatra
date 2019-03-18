/** 
 * Copyright (c) 2010-2012, Istvan Rath, Zoltan Ujhelyi and Daniel Varro
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-v20.html.
 * 
 * SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.viatra.query.patternlanguage.emf.tests.whitelist

import com.google.inject.Inject
import org.eclipse.viatra.query.patternlanguage.emf.validation.whitelist.PureWhitelist
import org.eclipse.xtext.common.types.TypesFactory
import org.eclipse.xtext.testing.InjectWith
import org.eclipse.xtext.testing.XtextRunner
import org.junit.Test
import org.junit.runner.RunWith

import static org.junit.Assert.assertTrue
import org.eclipse.viatra.query.patternlanguage.emf.tests.CustomizedEMFPatternLanguageInjectorProvider

@RunWith(typeof(XtextRunner))
@InjectWith(typeof(CustomizedEMFPatternLanguageInjectorProvider))
class TestPureWhitelistExtensionLoader {
    
    @Inject
    PureWhitelist whitelist
    
    @Test
    def void loaded() {
        val extension factory = TypesFactory.eINSTANCE
        whitelist.loadKnownExtensions
        val jvmOperation = createJvmOperation => [
            // Registered by TestWhitelistProvider
            simpleName = "method" 
        ]
        createJvmGenericType => [
            simpleName = "Class"
            packageName = "package"
            members += jvmOperation
        ]
        assertTrue(whitelist.contains(jvmOperation))
    }

}
