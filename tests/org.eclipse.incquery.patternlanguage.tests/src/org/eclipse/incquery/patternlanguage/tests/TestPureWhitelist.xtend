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
import org.eclipse.incquery.patternlanguage.validation.whitelist.PureWhitelist.PureElement
import org.eclipse.xtext.common.types.JvmOperation
import org.eclipse.xtext.common.types.TypesFactory
import org.junit.Before
import org.junit.Test

import static org.junit.Assert.assertTrue

class TestPureWhitelist {

    @Before
    def void setUp() {
        PureWhitelist.INSTANCE.clear // XXX NOOO static state
    }

    static val extension TypesFactory factory = TypesFactory.eINSTANCE

    @Test
    def void pureMethod() {
        val methodName = "method"
        val type = PureElement.Type.METHOD
        PureWhitelist.INSTANCE.add(new PureElement(methodName + "()", type))
        val jvmOperation = createJvmOperation => [
            simpleName = methodName
        ]
        assertTrue(PureWhitelist.INSTANCE.contains(jvmOperation))
    }

    @Test
    def void pureClass() {
        val className = "Class"
        val type = PureElement.Type.CLASS
        PureWhitelist.INSTANCE.add(new PureElement(className, type))
        val jvmOperation = createJvmOperation
        createJvmGenericType => [
            simpleName = className
            members += jvmOperation
        ]
        assertTrue(PureWhitelist.INSTANCE.contains(jvmOperation))
    }

    @Test
    def void purePackage() {
        val packageName = "package"
        val type = PureElement.Type.PACKAGE
        PureWhitelist.INSTANCE.add(new PureElement(packageName, type))
        val JvmOperation jvmOperation = createJvmOperation
        createJvmGenericType => [
            it.packageName = packageName
            members += jvmOperation
        ]
        assertTrue(PureWhitelist.INSTANCE.contains(jvmOperation))
    }

}
