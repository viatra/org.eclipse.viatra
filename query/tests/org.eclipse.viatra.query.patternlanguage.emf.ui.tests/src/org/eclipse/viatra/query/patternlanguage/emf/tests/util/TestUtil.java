/*******************************************************************************
 * Copyright (c) 2010-2016, Zoltan Ujhelyi and IncQuery Labs Ltd.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-v20.html.
 * 
 * SPDX-License-Identifier: EPL-2.0
 *******************************************************************************/
package org.eclipse.viatra.query.patternlanguage.emf.tests.util;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.osgi.internal.framework.EquinoxBundle;
import org.eclipse.osgi.storage.BundleInfo.Generation;
import org.eclipse.pde.core.target.ITargetDefinition;
import org.eclipse.pde.core.target.ITargetLocation;
import org.eclipse.pde.core.target.ITargetPlatformService;
import org.eclipse.pde.core.target.LoadTargetDefinitionJob;
import org.eclipse.pde.internal.core.target.TargetPlatformService;
import org.osgi.framework.Bundle;

@SuppressWarnings("restriction")
public class TestUtil {

    private TestUtil() {}

    
    /**
     * Sets a target platform in the test platform to get workspace builds OK with PDE.
     * 
     * Copied from org.eclipse.gmf.tests.Utils test utility of the Eclipse GMF project; referenced from bug
     * https://bugs.eclipse.org/bugs/show_bug.cgi?id=343156
     * 
     */
    public static void setTargetPlatform() throws Exception {
        ITargetPlatformService tpService = TargetPlatformService.getDefault();
        ITargetDefinition targetDef = tpService.newTarget();
        targetDef.setName("Tycho platform");
        Bundle[] bundles = Platform.getBundle("org.eclipse.core.runtime").getBundleContext().getBundles();
        List<ITargetLocation> bundleContainers = new ArrayList<ITargetLocation>();
        Set<File> dirs = new HashSet<File>();
        for (Bundle bundle : bundles) {
            EquinoxBundle bundleImpl = (EquinoxBundle) bundle;
            Generation generation = (Generation) bundleImpl.getModule().getCurrentRevision().getRevisionInfo();
            File file = generation.getBundleFile().getBaseFile();
            File folder = file.getParentFile();
            if (!dirs.contains(folder)) {
                dirs.add(folder);
                bundleContainers.add(tpService.newDirectoryLocation(folder.getAbsolutePath()));
            }
        }
        targetDef.setTargetLocations(bundleContainers.toArray(new ITargetLocation[bundleContainers.size()]));
        targetDef.setArch(Platform.getOSArch());
        targetDef.setOS(Platform.getOS());
        targetDef.setWS(Platform.getWS());
        targetDef.setNL(Platform.getNL());
        // targetDef.setJREContainer()
        tpService.saveTargetDefinition(targetDef);

        Job job = new LoadTargetDefinitionJob(targetDef);
        job.schedule();
        job.join();
    }
}
