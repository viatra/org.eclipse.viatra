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
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.viatra.query.tooling.core.project.ViatraQueryNature;
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
    Logger logger;

    @Override
    public void afterCreatePartControl(XtextEditor editor) {
        super.afterCreatePartControl(editor);
        try {
            IResource resource = editor.getResource();
            if (resource != null && resource.getProject().isAccessible() && !resource.getProject().isHidden()
                    && !resource.getProject().hasNature(ViatraQueryNature.NATURE_ID)) {
                String title = "Invalid VIATRA Query Project";
                String message = "The project " + resource.getProject().getName()
                        + " is not a valid VIATRA Query project.";
                MessageDialog.openError(editor.getShell(), title, message);

            }
        } catch (CoreException e) {
            logger.error("Error checking project nature", e);
        }
    }

}
