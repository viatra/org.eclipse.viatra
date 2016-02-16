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
package org.eclipse.viatra.query.tooling.ui.handlers;

import java.util.List;

import org.eclipse.core.resources.ICommand;
import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceVisitor;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.emf.common.util.URI;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.viatra.query.patternlanguage.patternLanguage.PatternLanguagePackage;
import org.eclipse.viatra.query.tooling.core.project.ProjectGenerationHelper;
import org.eclipse.viatra.query.tooling.core.project.ViatraQueryNature;
import org.eclipse.viatra.query.tooling.ui.ViatraQueryGUIPlugin;
import org.eclipse.xtext.resource.IEObjectDescription;
import org.eclipse.xtext.resource.IResourceDescription;
import org.eclipse.xtext.resource.IResourceDescriptions;
import org.eclipse.xtext.ui.XtextProjectHelper;
import org.eclipse.xtext.xbase.lib.Pair;

import com.google.common.base.Function;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableList.Builder;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.inject.Inject;

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
class NatureUpdaterJob extends Job {

	private IProject project;
	@Inject
	private IResourceDescriptions index;

	public NatureUpdaterJob(IProject project) {
		super(String.format("Updating project %s", project.getName()));
		this.project = project;
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

			if (ProjectNatureUpdater.isIncorrectBuilderID(commands[i].getBuilderName())) {
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
				}
				return true;
			}
		});
    }

	private void removeGlobalEiq(IProject project) throws CoreException {
		final IResource globalEiqFile = project.findMember(ProjectNatureUpdater.GLOBAL_EIQ_PATH);
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
	
		removableExtensions.add(new Pair<String, String>("", "org.eclipse.incquery.runtime.queryspecification"));
		removableExtensions.add(new Pair<String, String>("", ProjectNatureUpdater.XEXPRESSIONEVALUATOR_EXTENSION_POINT_ID));
		ProjectGenerationHelper.removeAllExtension(project, removableExtensions);
	}

	@Override
	protected IStatus run(IProgressMonitor monitor) {
		try {
			repairErroneousBuilderEntry(project);
			reorderBuilderEntries(project);

			final ImmutableList<String> newIDs = project.hasNature(ViatraQueryNature.NATURE_ID)
					? ImmutableList.<String> of() : ImmutableList.of(ViatraQueryNature.NATURE_ID);
			Builder<String> builder = ImmutableList.<String> builder();
			for (String ID : ProjectNatureUpdater.INCORRECT_NATURE_IDS) {
				if (project.hasNature(ID)) {
					builder.add(ID);
				}
			}

			final ImmutableList<String> oldIDs = builder.build();

			if (newIDs.size() + oldIDs.size() > 0) {
				ProjectGenerationHelper.updateNatures(project, newIDs, oldIDs, monitor);
			}
			removeGlobalEiq(project);
			renamePatternDefinitionFiles(project);
			removeExpressionExtensions(project);
			ProjectGenerationHelper.ensurePackageImports(project, ImmutableList.<String> of("org.apache.log4j"));
		} catch (CoreException e) {
			return new Status(IStatus.ERROR, ViatraQueryGUIPlugin.PLUGIN_ID, "Error updating project natures", e);
		}
		return Status.OK_STATUS;
	}

}