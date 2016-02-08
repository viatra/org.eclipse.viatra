/** 
 * Copyright (c) 2010-2012, Istvan Rath, Zoltan Ujhelyi and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Contributors:
 * Istvan Rath, Zoltan Ujhelyi - initial API and implementation
 */
package org.eclipse.viatra.query.patternlanguage.tests

import org.eclipse.viatra.query.patternlanguage.validation.whitelist.PurityChecker
import org.eclipse.xtext.common.types.TypesFactory
import org.junit.Test

import static org.junit.Assert.assertTrue

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
        assertTrue(PurityChecker.hasPureAnnotation(jvmOperation))
    }

}
