/** 
 * Copyright (c) 2010-2012, Istvan Rath, Zoltan Ujhelyi and Daniel Varro
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-v20.html.
 * 
 * SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.viatra.query.patternlanguage.emf.tests.whitelist

import org.eclipse.xtext.common.types.TypesFactory
import org.junit.Test

import static org.junit.Assert.assertTrue
import org.eclipse.viatra.query.patternlanguage.emf.helper.JavaTypesHelper

class TestPurityChecker {

    @Test
    def void hasPureAnnotation() {
        val extension factory = TypesFactory.eINSTANCE
        val jvmOperation = createJvmOperation => [
            annotations += createJvmAnnotationReference => [
                annotation = createJvmAnnotationType => [
                    packageName = Pure.package.name
                    simpleName = Pure.simpleName
                ]
            ]
        ]
        assertTrue(JavaTypesHelper.hasPureAnnotation(jvmOperation))
    }

}
