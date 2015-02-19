/*******************************************************************************
 * Copyright (c) 2004-2015, Istvan David, Istvan Rath and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Istvan David - initial API and implementation
 *******************************************************************************/

package org.eclipse.viatra.cep.vepl.ui.builder;

import static org.eclipse.viatra.cep.vepl.jvmmodel.NamingProvider.asStrings;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.log4j.Logger;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.viatra.cep.tooling.core.project.ProjectGenerationHelper;
import org.eclipse.viatra.cep.vepl.jvmmodel.NamingProvider;
import org.eclipse.viatra.cep.vepl.jvmmodel.NamingProvider.NamingPurpose;
import org.eclipse.viatra.cep.vepl.vepl.EventModel;
import org.eclipse.viatra.cep.vepl.vepl.ModelElement;
import org.eclipse.xtext.naming.QualifiedName;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import com.google.inject.Inject;
import com.google.inject.Singleton;

/**
 * Ensure support for BuilderParticipant.
 * 
 * @author Istvan David
 * 
 */
@Singleton
public class PackageExportProvider {

    @Inject
    private Logger logger;

    @Inject
    NamingProvider namingProvider;

    @Inject
    PackageExportHelper packageExportHelper;

    private Map<IProject, String> basePackageMap = Maps.newHashMap();
    private Multimap<IProject, String> potentialCepPackages = HashMultimap.create();
    private Multimap<IProject, String> exportablePackageMap = HashMultimap.create();

    public void addBasePackage(IProject project, String basePackage) {
        basePackageMap.put(project, basePackage);
    }

    public void setupCepPackageCollection(IProject project, EventModel eventModel) {
        potentialCepPackages.putAll(project, packageExportHelper.getExportablePackages(eventModel));
    }

    public void addExportablePackage(IProject project, ModelElement modelElement) {
        HashMap<NamingPurpose, QualifiedName> associatedPackageNames = namingProvider.getPackageNames(modelElement);

        exportablePackageMap.putAll(project, Lists.newArrayList(asStrings(associatedPackageNames).values()));
    }

    public void removeCepPackages(IProject project, IProgressMonitor monitor) {
        try {
            ProjectGenerationHelper.removePackageExports(project,
                    Lists.newArrayList(potentialCepPackages.removeAll(project)));
        } catch (Exception e) {
            logger.error("Exception during Extension/Package ensure Phase", e);
        } finally {
            monitor.worked(1);
        }
    }

    public void export(IProject modelProject, IProgressMonitor monitor) {
        try {
            exportPackages(monitor);
            exportablePackageMap.clear();
            basePackageMap.clear();
        } catch (Exception e) {
            logger.error("Exception during Extension/Package ensure Phase", e);
        } finally {
            monitor.worked(1);
        }
    }

    private void exportPackages(IProgressMonitor monitor) throws CoreException {
        if (exportablePackageMap.isEmpty()) {
            return;
        }

        for (IProject proj : exportablePackageMap.keySet()) {
            for (Entry<IProject, String> entry : basePackageMap.entrySet()) {
                exportablePackageMap.put(entry.getKey(), entry.getValue());
            }

            ProjectGenerationHelper.ensurePackageExports(proj, exportablePackageMap.removeAll(proj),
                    potentialCepPackages.removeAll(proj));
        }
    }
}
