/*******************************************************************************
 * Copyright (c) 2010-2018, Zoltan Ujhelyi, IncQuery Labs Ltd.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-v20.html.
 * 
 * SPDX-License-Identifier: EPL-2.0
 *******************************************************************************/
package org.eclipse.viatra.query.patternlanguage.emf.tests.generator

import org.eclipse.core.resources.IncrementalProjectBuilder
import org.eclipse.core.runtime.NullProgressMonitor
import org.eclipse.jdt.core.IClasspathEntry
import org.eclipse.jdt.core.JavaCore
import org.eclipse.jdt.launching.JavaRuntime
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestName
import org.junit.Ignore

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
    
    @Test
    def void compileUnresolvablePattern() {
        testFileCreationAndBuild('''
        package test
        
        import "http://foo"
        
        pattern foo(foo : Foo, bar : java Integer) {
            Foo.bar(foo, bar);
        }
        ''', 4)
    }
    
    //TODO Fix this test
    @Test
    @Ignore("The test does not run correctly on the build server - theory: the Eclipse did not find Java 7 installed.")
    def void oldJavaVersion() {
        testFileCreationAndBuild('''
        package test
        
        pattern testPattern() {
            EClass(x);
        }
        ''', 1, [project |
            val javaProject = JavaCore.create(project)
            javaProject.setRawClasspath(javaProject.rawClasspath.map[
                if (it.entryKind === IClasspathEntry.CPE_CONTAINER && it.path.toString.endsWith("JavaSE-1.8")) {
                    JavaCore.newContainerEntry(JavaRuntime.newJREContainerPath(JavaRuntime.getExecutionEnvironmentsManager().getEnvironment("JavaSE-1.7")))
                } else {
                    it                
                }
            ], new NullProgressMonitor)
            project.build(IncrementalProjectBuilder.CLEAN_BUILD, new NullProgressMonitor)
        ])
    }
}