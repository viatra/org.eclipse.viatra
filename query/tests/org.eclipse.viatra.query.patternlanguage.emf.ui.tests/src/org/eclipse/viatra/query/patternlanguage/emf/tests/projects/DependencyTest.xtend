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
package org.eclipse.viatra.query.patternlanguage.emf.tests.projects

import java.util.Collections
import org.eclipse.core.resources.IProject
import org.eclipse.core.runtime.NullProgressMonitor
import org.eclipse.pde.core.project.IBundleProjectService
import org.eclipse.viatra.query.patternlanguage.emf.tests.util.TestUtil
import org.eclipse.viatra.query.tooling.core.generator.ViatraQueryGeneratorPlugin
import org.eclipse.viatra.query.tooling.core.project.ProjectGenerationHelper
import org.eclipse.xtext.ui.testing.AbstractWorkbenchTest
import org.eclipse.xtext.ui.testing.util.IResourcesSetupUtil
import org.junit.Before
import org.junit.BeforeClass
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestName
import org.osgi.framework.BundleContext
import org.osgi.framework.ServiceReference

class DependencyTest extends AbstractWorkbenchTest {
 
    static val TEST_PROJECT_NAME_PREFIX = "org.eclipse.viatra.query.test"
 
    @Rule
    public val name = new TestName
    var String testProjectName
    var IProject project
 
    @BeforeClass
    def static void installTargetPlatform() {
        TestUtil.setTargetPlatform
    }
 
    @Before
    override void setUp() {
        super.setUp
        val ws = IResourcesSetupUtil.root.workspace
        
        testProjectName = TEST_PROJECT_NAME_PREFIX + "." + name.methodName
        
        val desc = ws.newProjectDescription(testProjectName)
        project = ws.root.getProject(testProjectName)
        ProjectGenerationHelper.createProject(desc, project, newArrayList, new NullProgressMonitor)
    }
    
    @Test
    def void ensureSelfDependency() {
        ProjectGenerationHelper.ensureBundleDependencies(project, Collections.singletonList(testProjectName))
        assertFalse(project.dependencies.map[it.name].contains(testProjectName))
    }
    
    @Test
    def void ensureDependency() {
        val projectName = "org.eclipse.emf"
        ProjectGenerationHelper.ensureBundleDependencies(project, Collections.singletonList(projectName))
        assertTrue(project.dependencies.map[it.name].contains(projectName))
    }
    
    private def getDependencies(IProject project) {
        var BundleContext context = null
        var ServiceReference<IBundleProjectService> ref = null
        try {
            context = ViatraQueryGeneratorPlugin.getContext();
            ref = context.getServiceReference(IBundleProjectService);
            val IBundleProjectService service = context.getService(ref);
            val bundleDesc = service.getDescription(project);
            return bundleDesc.getRequiredBundles()
        } finally {
            if (context !== null && ref !== null) {
                context.ungetService(ref);
            }
        }
    }
}
