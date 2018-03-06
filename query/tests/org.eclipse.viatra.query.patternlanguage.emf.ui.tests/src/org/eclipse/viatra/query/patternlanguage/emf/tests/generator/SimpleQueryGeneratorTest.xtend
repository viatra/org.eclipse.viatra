/*******************************************************************************
 * Copyright (c) 2010-2018, Zoltan Ujhelyi, IncQuery Labs Ltd.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Zoltan Ujhelyi - initial API and implementation
 *******************************************************************************/
package org.eclipse.viatra.query.patternlanguage.emf.tests.generator

import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestName

class SimpleQueryGeneratorTest extends AbstractQueryCompilerTest {
 
    static val TEST_PROJECT_NAME_PREFIX = "org.eclipse.viatra.query.test"

    @Rule
    public val name = new TestName
 
    override String calculateTestProjectName() {
        TEST_PROJECT_NAME_PREFIX + "." + name.methodName
    }
    
    @Test
    def void compileParameterlessPattern() {
        testFileCreationAndBuild('''
        package test
        
        import "http://www.eclipse.org/emf/2002/Ecore"
        
        pattern testPattern() {
            EClass(x);
        }
        ''', 0)
    }
    
}