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
package org.eclipse.incquery.tooling.ui.handlers;

import java.util.List;

import org.eclipse.core.resources.ICommand;
import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceVisitor;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.emf.common.util.URI;
import org.eclipse.incquery.patternlanguage.patternLanguage.PatternLanguagePackage;
import org.eclipse.incquery.tooling.core.project.IncQueryNature;
import org.eclipse.incquery.tooling.core.project.ProjectGenerationHelper;
import org.eclipse.incquery.tooling.ui.IncQueryGUIPlugin;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.xtext.resource.IEObjectDescription;
import org.eclipse.xtext.resource.IResourceDescription;
import org.eclipse.xtext.resource.IResourceDescriptions;
import org.eclipse.xtext.ui.XtextProjectHelper;
import org.eclipse.xtext.xbase.lib.Pair;

import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.inject.Inject;

/**
 * Helper class for updating EMF-IncQuery project to current versions. Currently supported migration paths:
 *
 * <ul>
 *   <li>0.6.x -> 0.8</li>
 *   <li>0.7.x -> 0.8</li>
 * </ul>
 * @author Zoltan Ujhelyi
 *
 */
class NatureUpdaterJob extends Job {

    private IProject project;
    @Inject
    private IResourceDescriptions index;

    public NatureUpdaterJob(IProject project) {
        super(String.format("Updating project %s", project.getName()));
        this.project = project;
    }

    /**
     * This method checks for an earlier IncQuery builder entry, and updates it to the current version. See bug
     * https://bugs.eclipse.org/bugs/show_bug.cgi?id=404952 for details.
     */
    private void repairErroneousBuilderEntry(IProject project) throws CoreException {
        IProjectDescription desc = project.getDescription();
        ICommand[] commands = desc.getBuildSpec();
        for (int i = 0; i < commands.length; i++) {
            if (commands[i].getBuilderName().equals(ProjectNatureUpdater.INCORRECT_BUILDER_ID)) {
                commands[i].setBuilderName(IncQueryNature.BUILDER_ID);
            }
        }
        desc.setBuildSpec(commands);
        project.setDescription(desc, null);
    }

    private void reorderBuilderEntries(IProject project) throws CoreException {
        IProjectDescription desc = project.getDescription();
        ICommand[] commands = desc.getBuildSpec();

        //Lookup the IncQuery-related command indixii
        int xtextIndex = -1;
        int iqIndex = -1;
        for (int i = 0; i < commands.length; i++) {
            String id = commands[i].getBuilderName();
            if (IncQueryNature.BUILDER_ID.equals(id)) {
                iqIndex = i;
            } else if (XtextProjectHelper.BUILDER_ID.equals(id)) {
                xtextIndex = i;
            }
        }

        //Preparing reordered array
        ICommand[] newCommands = new ICommand[commands.length];
        newCommands[0] = commands[iqIndex];
        newCommands[1] = commands[xtextIndex];

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

    private void removeGlobalEiq(IProject project) throws CoreException {
        final IResource globalEiqFile = project.findMember(ProjectNatureUpdater.GLOBAL_EIQ_PATH);
        if (globalEiqFile != null) {
            final IContainer parent = globalEiqFile.getParent();
            globalEiqFile.delete(true, null);
            if (parent.members().length == 0) {
                parent.delete(true, null);
            }
        }
    }

    public void removeExpressionExtensions(IProject project) throws CoreException {
        final IJavaProject javaProject = JavaCore.create(project);
        final List<Pair<String, String>> removableExtensions = Lists.newArrayList();
        project.accept(new IResourceVisitor() {

            @Override
            public boolean visit(IResource resource) throws CoreException {
                if (resource instanceof IContainer) {
                    final IJavaElement element = JavaCore.create(resource, javaProject);
                    return element != null;
                } else if ("eiq".equals(resource.getFileExtension())) {
                    final IResourceDescription desc = index.getResourceDescription(URI.createPlatformResourceURI(resource.getFullPath().toString(), true));
                    final Iterable<Pair<String, String>> extensionHeaders = Iterables.transform(desc.getExportedObjectsByType(PatternLanguagePackage.Literals.PATTERN), new Function<IEObjectDescription, Pair<String, String>>() {

                        @Override
                        public Pair<String, String> apply(IEObjectDescription desc) {
                            return new Pair<String, String>(desc.getQualifiedName().toString(), ProjectNatureUpdater.XEXPRESSIONEVALUATOR_EXTENSION_POINT_ID);
                        }
                    });
                    removableExtensions.addAll(Lists.newArrayList(extensionHeaders));
                }
                return false;
            }
        });
        ProjectGenerationHelper.removeAllExtension(project, removableExtensions);
    }

    @Override
    protected IStatus run(IProgressMonitor monitor) {
        try {
            repairErroneousBuilderEntry(project);
            reorderBuilderEntries(project);

            final ImmutableList<String> newIDs = project.hasNature(IncQueryNature.NATURE_ID) ? ImmutableList
                    .<String> of() : ImmutableList.of(IncQueryNature.NATURE_ID);
            final ImmutableList<String> oldIDs = project.hasNature(ProjectNatureUpdater.OLD_NATURE_ID) ? ImmutableList.of(ProjectNatureUpdater.OLD_NATURE_ID)
                    : ImmutableList.<String> of();
            if (newIDs.size() + oldIDs.size() > 0) {
                ProjectGenerationHelper.updateNatures(project, newIDs, oldIDs, monitor);
            }

            removeGlobalEiq(project);
            removeExpressionExtensions(project);
        } catch (CoreException e) {
            return new Status(IStatus.ERROR, IncQueryGUIPlugin.PLUGIN_ID, "Error updating project natures", e);
        }
        return Status.OK_STATUS;
    }

}