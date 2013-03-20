/*******************************************************************************
 * Copyright (c) 2010-2012, Andras Okros, Istvan Rath and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Andras Okros - initial API and implementation
 *******************************************************************************/
package org.eclipse.incquery.tooling.ui.patternregistry.handlers;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IFile;
import org.eclipse.incquery.patternlanguage.patternLanguage.Pattern;
import org.eclipse.incquery.runtime.patternregistry.IPatternInfo;
import org.eclipse.jface.text.TextSelection;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorReference;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.handlers.HandlerUtil;
import org.eclipse.xtext.resource.ILocationInFileProvider;
import org.eclipse.xtext.ui.editor.XtextEditor;
import org.eclipse.xtext.util.ITextRegion;

import com.google.inject.Inject;

public class ShowLocationHandler extends AbstractHandler {

    @Inject
    private ILocationInFileProvider locationProvider;

    @Override
    public Object execute(ExecutionEvent event) throws ExecutionException {
        ISelection selection = HandlerUtil.getCurrentSelection(event);
        if (selection instanceof IStructuredSelection) {
            IStructuredSelection structuredSelection = (IStructuredSelection) selection;
            Object element = structuredSelection.getFirstElement();
            if (element instanceof IPatternInfo) {
                IPatternInfo patternInfo = (IPatternInfo) element;
                setSelectionInXtextEditor(patternInfo);
            }
        }
        return null;
    }

    protected void setSelectionInXtextEditor(IPatternInfo patternInfo) {
        IFile file = patternInfo.getRelatedFile();
        if (file != null) {
            for (IEditorReference editorReference : PlatformUI.getWorkbench().getActiveWorkbenchWindow()
                    .getActivePage().getEditorReferences()) {
                String editorId = editorReference.getId();
                if ("org.eclipse.incquery.patternlanguage.emf.EMFPatternLanguage".equals(editorId)) {
                    IEditorPart editorPart = editorReference.getEditor(true);
                    if (editorPart instanceof XtextEditor) {
                        XtextEditor xtextEditor = (XtextEditor) editorPart;
                        IEditorInput editorInput = xtextEditor.getEditorInput();
                        if (editorInput instanceof IFileEditorInput) {
                            IFileEditorInput fileEditorInput = (IFileEditorInput) editorInput;
                            if (fileEditorInput.getFile().equals(file)) {
                                // Bringing the editor to top
                                editorPart.getSite().getPage().bringToTop(editorPart);
                            }
                        }

                        // Finding location using location service
                        Pattern pattern = patternInfo.getPattern();
                        ITextRegion location = locationProvider.getSignificantTextRegion(pattern);
                        if (location != null) {
                            xtextEditor.reveal(location.getOffset(), location.getLength());
                            xtextEditor.getSelectionProvider().setSelection(
                                    new TextSelection(location.getOffset(), location.getLength()));
                        }
                    }
                }
            }
        }
    }

}
