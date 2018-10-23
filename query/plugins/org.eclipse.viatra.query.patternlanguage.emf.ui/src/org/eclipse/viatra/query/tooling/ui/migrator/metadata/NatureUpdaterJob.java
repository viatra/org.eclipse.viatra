/*******************************************************************************
 * Copyright (c) 2010-2014, Zoltan Ujhelyi, Istvan Rath and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Zoltan Ujhelyi - initial API and implementation
 *******************************************************************************/
package org.eclipse.viatra.query.tooling.ui.migrator.metadata;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.ICommand;
import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceVisitor;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.pde.internal.core.natures.PDE;
import org.eclipse.viatra.query.patternlanguage.emf.ui.EMFPatternLanguageUIPlugin;
import org.eclipse.viatra.query.tooling.core.project.ProjectGenerationHelper;
import org.eclipse.viatra.query.tooling.core.project.ViatraQueryNature;
import org.eclipse.viatra.query.tooling.ui.migrator.MigratorConstants;
import org.eclipse.xtext.builder.EclipseOutputConfigurationProvider;
import org.eclipse.xtext.ui.XtextProjectHelper;
import org.eclipse.xtext.xbase.lib.Pair;

import com.google.common.collect.Lists;

/**
 * Helper class for updating EMF-IncQuery project to current versions. Currently
 * supported migration paths:
 *
 * <ul>
 * <li>0.6.x -> 0.8</li>
 * <li>0.7.x -> 0.8</li>
 * </ul>
 * 
 * @author Zoltan Ujhelyi
 *
 */
@SuppressWarnings("restriction")
public class NatureUpdaterJob extends Job {

    private final IProject project;
    private final EclipseOutputConfigurationProvider outputConfigurationProvider;

    public NatureUpdaterJob(IProject project, EclipseOutputConfigurationProvider outputConfigurationProvider) {
        super(String.format("Updating project %s", project.getName()));
        this.project = project;
        this.outputConfigurationProvider = outputConfigurationProvider;
    }

    /**
     * This method checks for an earlier IncQuery builder entry, and updates it
     * to the current version. See bug
     * https://bugs.eclipse.org/bugs/show_bug.cgi?id=404952 for details.
     */
    private void repairErroneousBuilderEntry(IProject project) throws CoreException {
        IProjectDescription desc = project.getDescription();
        ICommand[] commands = desc.getBuildSpec();
        for (int i = 0; i < commands.length; i++) {

            if (MigratorConstants.isIncorrectBuilderID(commands[i].getBuilderName())) {
                commands[i].setBuilderName(ViatraQueryNature.BUILDER_ID);
            }
        }
        desc.setBuildSpec(commands);
        project.setDescription(desc, null);
    }

    private void reorderBuilderEntries(IProject project) throws CoreException {
        IProjectDescription desc = project.getDescription();
        ICommand[] commands = desc.getBuildSpec();

        // Lookup the IncQuery-related command indixii
        int xtextIndex = -1;
        ICommand xtextCommand = null;
        int iqIndex = -1;
        ICommand iqCommand = null;
        int commandListSize = commands.length;
        for (int i = 0; i < commandListSize; i++) {
            String id = commands[i].getBuilderName();
            if (ViatraQueryNature.BUILDER_ID.equals(id)) {
                iqIndex = i;
                iqCommand = commands[i];
            } else if (XtextProjectHelper.BUILDER_ID.equals(id)) {
                xtextIndex = i;
                xtextCommand = commands[i];
            }
        }

        // Preparing reordered array
        if (iqIndex < 0) {
            commandListSize++;
            iqCommand = desc.newCommand();
            iqCommand.setBuilderName(ViatraQueryNature.BUILDER_ID);
        }
        if (xtextIndex < 0) {
            commandListSize++;
            xtextCommand = desc.newCommand();
            xtextCommand.setBuilderName(XtextProjectHelper.BUILDER_ID);
        }
        ICommand[] newCommands = new ICommand[commandListSize];
        newCommands[0] = iqCommand;
        newCommands[1] = xtextCommand;

        int commandIndex = 2;
        for (int i = 0; i < commands.length; i++) {
            if (i != xtextIndex && i != iqIndex) {
                newCommands[commandIndex] = commands[i];
                commandIndex++;
            }
        }
        desc.setBuildSpec(newCommands);
        project.setDescription(desc, null);
    }

    private void renamePatternDefinitionFiles(IProject project) throws CoreException {
        final IProgressMonitor monitor = new NullProgressMonitor();
        project.accept(new IResourceVisitor() {
            
            @Override
            public boolean visit(IResource resource) throws CoreException {
                if (resource instanceof IFile && "eiq".equals(resource.getFileExtension())) {
                    ((IFile)resource).move(resource.getFullPath().removeFileExtension().addFileExtension("vql"), false, monitor);
                } else if (resource instanceof IFile && "eiqgen".equals(resource.getFileExtension())) {
                    ((IFile)resource).move(resource.getFullPath().removeFileExtension().addFileExtension("vqgen"), false, monitor);
                } 
                return true;
            }
        });
    }

    private void removeGlobalEiq(IProject project) throws CoreException {
        final IResource globalEiqFile = project.findMember(MigratorConstants.GLOBAL_EIQ_PATH);
        if (globalEiqFile != null) {
            final IProgressMonitor monitor = new NullProgressMonitor();
            final IContainer parent = globalEiqFile.getParent();
            globalEiqFile.delete(true, monitor);
            if (parent.members().length == 0) {
                parent.delete(true, monitor);
            }
        }
    }

    public void removeExpressionExtensions(IProject project) throws CoreException {
        final List<Pair<String, String>> removableExtensions = Lists.newArrayList();
    
        removableExtensions.add(new Pair<>("", "org.eclipse.incquery.runtime.queryspecification"));
        removableExtensions.add(new Pair<>("", MigratorConstants.XEXPRESSIONEVALUATOR_EXTENSION_POINT_ID));
        ProjectGenerationHelper.removeAllExtension(project, removableExtensions);
    }

    @Override
    protected IStatus run(IProgressMonitor monitor) {
        try {
            project.refreshLocal(IResource.DEPTH_INFINITE, monitor);
            repairErroneousBuilderEntry(project);
            reorderBuilderEntries(project);

            final List<String> newIDs = new ArrayList<>(); 
            if (!project.hasNature(ViatraQueryNature.NATURE_ID)) {
                newIDs.add(ViatraQueryNature.NATURE_ID);
            }
            if (!project.hasNature(XtextProjectHelper.NATURE_ID)) {
                newIDs.add(XtextProjectHelper.NATURE_ID);
            }
            final List<String> oldIDs = new ArrayList<>();
            for (String ID : MigratorConstants.INCORRECT_NATURE_IDS) {
                if (project.hasNature(ID)) {
                    oldIDs.add(ID);
                }
            }

            if (newIDs.size() + oldIDs.size() > 0) {
                ProjectGenerationHelper.updateNatures(project, newIDs, oldIDs, monitor);
            }
            removeGlobalEiq(project);
            renamePatternDefinitionFiles(project);
            
            if (PDE.hasPluginNature(project)) {
                ProjectGenerationHelper.ensureSourceFolder(project,
                        outputConfigurationProvider.getOutputConfigurations(), monitor);
                ProjectGenerationHelper.ensureSingletonDeclaration(project);
                removeExpressionExtensions(project);
                ProjectGenerationHelper.ensureBundleDependenciesAndPackageImports(project,
                ProjectGenerationHelper.DEFAULT_VIATRA_BUNDLE_REQUIREMENTS,
                ProjectGenerationHelper.DEFAULT_VIATRA_IMPORT_PACKAGES, monitor);
            }
            project.build(IncrementalProjectBuilder.CLEAN_BUILD, monitor);
        } catch (CoreException e) {
            return new Status(IStatus.ERROR, EMFPatternLanguageUIPlugin.ID, "Error updating project natures", e);
        }
        return Status.OK_STATUS;
    }

}