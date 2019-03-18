/*******************************************************************************
 * Copyright (c) 2010-2017, Zoltan Ujhelyi, IncQuery Labs Ltd.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-v20.html.
 * 
 * SPDX-License-Identifier: EPL-2.0
 *******************************************************************************/
package org.eclipse.viatra.query.patternlanguage.emf.tests.generator

import org.eclipse.core.resources.IProject
import org.eclipse.core.runtime.IPath
import org.eclipse.core.runtime.Path
import org.eclipse.jdt.core.IClasspathEntry
import org.eclipse.jdt.core.JavaCore
import org.eclipse.viatra.query.runtime.IExtensions
import org.eclipse.viatra.query.tooling.core.project.PluginXmlModifier
import org.eclipse.viatra.query.tooling.core.project.ViatraQueryNature
import org.eclipse.xtext.ui.testing.util.IResourcesSetupUtil
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestName

class VQLCompilerOptionsTest extends AbstractQueryCompilerTest {
 
    static val TEST_PROJECT_NAME_PREFIX = "org.eclipse.viatra.query.test"

    static val TEST_CONTENTS_CONFLICT = '''
        package test
        
        import "http://www.eclipse.org/emf/2002/Ecore"
        
        pattern test(X : EClass) {
            EClass(X);
        }
    '''
    static val TEST_CONTENTS_OK = '''
        package test
        
        import "http://www.eclipse.org/emf/2002/Ecore"
        
        pattern testPattern(X : EClass) {
            EClass(X);
        }
    '''
 
    @Rule
    public val name = new TestName
    var IPath configPath
 
    override setUp() {
        super.setUp
        configPath = VQLGeneratorProperties.getVQLGeneratorPropertiesPath(testProjectName)
    }
 
    override String calculateTestProjectName() {
        TEST_PROJECT_NAME_PREFIX + "." + name.methodName
    }
    
    @Test
    def void groupPatternClashWithDefaultSettings() {
        testFileCreationAndBuild(TEST_CONTENTS_CONFLICT, 1)
    }
    
    @Test
    def void groupPatternClashWithSeparateMatchClassGeneration() {
        IResourcesSetupUtil.createFile(configPath, VQLGeneratorProperties.SEPARATE_CLASS_MATCHERS)
        testFileCreationAndBuild(TEST_CONTENTS_CONFLICT, 0)
    }
    
    @Test
    def void groupPatternClashWithNestedMatchClassGeneration() {
        IResourcesSetupUtil.createFile(configPath, VQLGeneratorProperties.NESTED_CLASS_MATCHERS)
        testFileCreationAndBuild(TEST_CONTENTS_CONFLICT, 1)
    }
    
    @Test
    def void groupPatternClashWithNoMatchClassGeneration() {
        IResourcesSetupUtil.createFile(configPath, VQLGeneratorProperties.NO_MATCHERS)
        testFileCreationAndBuild(TEST_CONTENTS_CONFLICT, 1)
    }
    
    @Test
    def void groupPatternNoClashWithDefaultSettings() {
        testFileCreationAndBuild(TEST_CONTENTS_OK, 0)
    }
    
    @Test
    def void groupPatternNoClashWithSeparateMatchClassGeneration() {
        IResourcesSetupUtil.createFile(configPath, VQLGeneratorProperties.SEPARATE_CLASS_MATCHERS)
        testFileCreationAndBuild(TEST_CONTENTS_OK, 0)
    }
    
    @Test
    def void groupPatternNoClashWithNestedMatchClassGeneration() {
        IResourcesSetupUtil.createFile(configPath, VQLGeneratorProperties.NESTED_CLASS_MATCHERS)
        testFileCreationAndBuild(TEST_CONTENTS_OK, 0)
    }
    
    @Test
    def void groupPatternNoClashWithNoMatchClassGeneration() {
        IResourcesSetupUtil.createFile(configPath, VQLGeneratorProperties.NO_MATCHERS)
        testFileCreationAndBuild(TEST_CONTENTS_OK, 0)
    }
    
    @Test
    def void pluginXml() {
        IResourcesSetupUtil.createFile(configPath, VQLGeneratorProperties.NESTED_CLASS_MATCHERS)
        testFileCreationAndBuild(TEST_CONTENTS_OK, 0)
        
        val project = IResourcesSetupUtil.root.getProject(testProjectName)
        val modifier = new PluginXmlModifier
        modifier.loadPluginXml(project)
        assertTrue(modifier.hasExtensionFollowingId(IExtensions.QUERY_SPECIFICATION_EXTENSION_POINT_ID))
    }
    
    @Test
    def void noPluginXml() {
        IResourcesSetupUtil.createFile(configPath, VQLGeneratorProperties.NESTED_CLASS_NO_PLUGINXML)
        testFileCreationAndBuild(TEST_CONTENTS_OK, 0)
        
        val project = IResourcesSetupUtil.root.getProject(testProjectName)
        val modifier = new PluginXmlModifier
        modifier.loadPluginXml(project)
        assertFalse(modifier.hasExtensionFollowingId(IExtensions.QUERY_SPECIFICATION_EXTENSION_POINT_ID))
    }
    
    @Test
    def void customOutputDirectory() {
        val outputDirectoryName = "viatra-gen"
        IResourcesSetupUtil.createFile(configPath, VQLGeneratorProperties.customOutputDirectory(outputDirectoryName))
        testFileCreationAndBuild(TEST_CONTENTS_OK, 0)
        
        val project = IResourcesSetupUtil.root.getProject(testProjectName)
        assertFalse(sourceFolderIsOnClassPath(project, ViatraQueryNature.SRCGEN_DIR))
        assertTrue(sourceFolderIsOnClassPath(project, outputDirectoryName))
    }
    
    private def boolean sourceFolderIsOnClassPath(IProject project, String directoryName) {
        val javaProject = JavaCore.create(project)
        javaProject.rawClasspath.exists[
            entryKind == IClasspathEntry.CPE_SOURCE && path == new Path('''/«project.name»/«directoryName»''')
        ]
    }

}