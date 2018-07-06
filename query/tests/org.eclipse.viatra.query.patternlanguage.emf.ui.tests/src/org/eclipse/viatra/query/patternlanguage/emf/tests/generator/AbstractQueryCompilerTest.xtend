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

import org.eclipse.core.runtime.NullProgressMonitor
import org.eclipse.core.runtime.Path
import org.eclipse.viatra.query.patternlanguage.emf.tests.util.TestUtil
import org.eclipse.viatra.query.tooling.core.project.ProjectGenerationHelper
import org.eclipse.xtext.ui.testing.AbstractWorkbenchTest
import org.eclipse.xtext.ui.testing.util.IResourcesSetupUtil
import org.junit.Before
import org.junit.BeforeClass
import org.eclipse.core.runtime.IPath
import org.eclipse.core.resources.IMarker
import org.eclipse.core.resources.IResource
import java.util.function.Consumer
import org.eclipse.core.resources.IProject

abstract class AbstractQueryCompilerTest extends AbstractWorkbenchTest {
    
    @BeforeClass
    def static void installTargetPlatform() {
        TestUtil.setTargetPlatform
    }
    
    protected var String testProjectName
    protected var IPath filePath 
    
    def abstract String calculateTestProjectName();
    
    @Before
    override void setUp() {
        super.setUp
        val ws = IResourcesSetupUtil.root.workspace
        
        testProjectName = calculateTestProjectName
        filePath = new Path(testProjectName).append("src").append("test").append("test.vql")
        
        val desc = ws.newProjectDescription(testProjectName)
        val proj = ws.root.getProject(testProjectName)
        ProjectGenerationHelper.createProject(desc, proj, newArrayList, new NullProgressMonitor)
    }
    
    protected def void testFileCreationAndBuild(String contents, int expectedIssueCount) {
        testFileCreationAndBuild(contents, expectedIssueCount, [])
    }
    
    protected def void testFileCreationAndBuild(String contents, int expectedIssueCount, Consumer<IProject> projectConfigurer) {
        val testFile = IResourcesSetupUtil.createFile(filePath, contents)
        val project = testFile.project
        projectConfigurer.accept(project)
        IResourcesSetupUtil.waitForBuild
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
}