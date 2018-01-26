/*******************************************************************************
 * Copyright (c) 2010-2017, Zoltan Ujhelyi, IncQuery Labs Ltd.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Zoltan Ujhelyi - initial API and implementation
 *******************************************************************************/
package org.eclipse.viatra.query.patternlanguage.emf.tests.generator

import org.eclipse.core.resources.IMarker
import org.eclipse.core.resources.IResource
import org.eclipse.core.runtime.IPath
import org.eclipse.core.runtime.NullProgressMonitor
import org.eclipse.core.runtime.Path
import org.eclipse.viatra.query.runtime.IExtensions
import org.eclipse.viatra.query.tooling.core.project.PluginXmlModifier
import org.eclipse.viatra.query.tooling.core.project.ProjectGenerationHelper
import org.eclipse.xtext.ui.testing.AbstractWorkbenchTest
import org.eclipse.xtext.ui.testing.util.IResourcesSetupUtil
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestName
import org.junit.BeforeClass
import org.eclipse.viatra.query.patternlanguage.emf.tests.util.TestUtil

class VQLCompilerOptionsTest extends AbstractWorkbenchTest {
 
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
    private var String testProjectName
    private var IPath filePath 
    private var IPath configPath
 
 
    @BeforeClass
    def static void installTargetPlatform() {
        TestUtil.setTargetPlatform
    }
 
    @Before
    override void setUp() {
        super.setUp
        val ws = IResourcesSetupUtil.root.workspace
        
        testProjectName = TEST_PROJECT_NAME_PREFIX + "." + name.methodName
        filePath = new Path(testProjectName).append("src").append("test").append("test.vql")
        configPath = VQLGeneratorProperties.getVQLGeneratorPropertiesPath(testProjectName)
        
        val desc = ws.newProjectDescription(testProjectName)
        val proj = ws.root.getProject(testProjectName)
        ProjectGenerationHelper.createProject(desc, proj, newArrayList, new NullProgressMonitor)
    }
    
    private def void testFileCreationAndBuild(String contents, int expectedIssueCount) {
        val testFile = IResourcesSetupUtil.createFile(filePath, contents)
        IResourcesSetupUtil.waitForBuild
        val project = testFile.project
        val markers = project.findMarkers(IMarker.PROBLEM, true, IResource.DEPTH_INFINITE).
                    filter[IMarker.SEVERITY_ERROR == it.attributes.get(IMarker.SEVERITY)]
        assertEquals(
            '''
            Unexpected number error markers found - expected «expectedIssueCount» but found:
            «FOR marker : markers»
                «marker.resource.projectRelativePath»(«marker.getAttribute(IMarker.LOCATION)»): «marker.getAttribute(IMarker.MESSAGE)» 
            «ENDFOR»
            ''', expectedIssueCount, markers.size)
        
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
}