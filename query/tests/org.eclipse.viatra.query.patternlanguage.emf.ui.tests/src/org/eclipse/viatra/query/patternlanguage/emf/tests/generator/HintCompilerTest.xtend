/*******************************************************************************
 * Copyright (c) 2010-2017, Zoltan Ujhelyi, IncQuery Labs Ltd.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-v20.html.
 * 
 * SPDX-License-Identifier: EPL-2.0
 *******************************************************************************/
package org.eclipse.viatra.query.patternlanguage.emf.tests.generator

import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestName

class HintCompilerTest extends AbstractQueryCompilerTest {
 
    static val TEST_PROJECT_NAME_PREFIX = "org.eclipse.viatra.query.test"

    static val TEST_CONTENTS_DEFAULT = '''
        package test
        
        import "http://www.eclipse.org/emf/2002/Ecore"
        
        pattern testPattern(X : EClass) {
            EClass(X);
        }
    '''
    static val TEST_CONTENTS_LS = '''
        package test
        
        import "http://www.eclipse.org/emf/2002/Ecore"
        
        search pattern testPattern(X : EClass) {
            EClass(X);
        }
    '''
    static val TEST_CONTENTS_RETE = '''
        package test
        
        import "http://www.eclipse.org/emf/2002/Ecore"
        
        incremental pattern testPattern(X : EClass) {
            EClass(X);
        }
    '''
 
    @Rule
    public val name = new TestName
 
    override String calculateTestProjectName() {
        TEST_PROJECT_NAME_PREFIX + "." + name.methodName
    }
    
    @Test
    def void compileDefaultPattern() {
        testFileCreationAndBuild(TEST_CONTENTS_DEFAULT, 0)
    }
    
    @Test
    def void compileWithLSHint() {
        testFileCreationAndBuild(TEST_CONTENTS_LS, 0)
    }
    @Test
    def void compileWithIncrementalHint() {
        testFileCreationAndBuild(TEST_CONTENTS_RETE, 0)
    }
    
   
}