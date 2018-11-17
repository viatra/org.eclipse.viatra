/*******************************************************************************
 * Copyright (c) 2010-2013, Zoltan Ujhelyi, Istvan Rath and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Zoltan Ujhelyi - initial API and implementation
 *******************************************************************************/
package org.eclipse.viatra.query.patternlanguage.emf.ui;

import org.apache.log4j.Logger;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.pde.internal.core.natures.PDE;
import org.eclipse.viatra.query.tooling.core.project.ViatraQueryNature;
import org.eclipse.viatra.query.tooling.ui.migrator.JavaProjectMigrator;
import org.eclipse.viatra.query.tooling.ui.migrator.metadata.NatureUpdaterJob;
import org.eclipse.xtext.builder.EclipseOutputConfigurationProvider;
import org.eclipse.xtext.builder.nature.NatureAddingEditorCallback;
import org.eclipse.xtext.ui.editor.XtextEditor;

import com.google.inject.Inject;

/**
 * @author Zoltan Ujhelyi
 *
 */
@SuppressWarnings("restriction")
public class EMFPatternLanguageEditorCallback extends NatureAddingEditorCallback {

    @Inject
    private Logger logger;
    @Inject
    private EclipseOutputConfigurationProvider outputConfigurationProvider;

    @Override
    public void afterCreatePartControl(XtextEditor editor) {
        try {
            IResource resource = editor.getResource();
            if (resource != null) {
                final IProject project = resource.getProject();
                if (project.isAccessible() && !project.isHidden() && !project.hasNature(ViatraQueryNature.NATURE_ID)) {
                    String question = (PDE.hasPluginNature(project))
                            ? String.format("Do you want to convert project %s to a VIATRA Query Project?",
                                    project.getName())
                            : String.format(
                                    "Do you want to convert project %s to a VIATRA Query Project? (Note: dependencies to VIATRA Query runtime will have to be set up manually.)",
                                    project.getName());

                    // TODO Xtext 2.14 has a new API called DontAskAgainDialogs - we should update to that after minimum
                    // Xtext requirement in increased to at least 2.14
                    if (MessageDialog.openQuestion(editor.getShell(), "Invalid VIATRA Query Project", question)) {
                        final NatureUpdaterJob job = new NatureUpdaterJob(project, outputConfigurationProvider);
                        job.schedule();
                        final JavaProjectMigrator migrator = new JavaProjectMigrator(project);
                        migrator.migrate(new NullProgressMonitor());
                    }
                }
            }
        } catch (CoreException e) {
            logger.error("Error checking project nature", e);
        }
    }

}
