/*******************************************************************************
 * Copyright (c) 2010-2012, Zoltan Ujhelyi, Tamas Szabo, Istvan Rath and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Zoltan Ujhelyi, Tamas Szabo - initial API and implementation
 *******************************************************************************/
package org.eclipse.incquery.tooling.ui.queryexplorer.util;

import org.eclipse.core.resources.IFile;
import org.eclipse.incquery.runtime.api.IQuerySpecification;
import org.eclipse.incquery.tooling.ui.queryexplorer.handlers.AttachEiqEditorRegistrationHandler;
import org.eclipse.incquery.tooling.ui.queryexplorer.handlers.PatternUnregistrationHandler;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.part.FileEditorInput;

/**
 * The PartListener is used to observe EditorPart close actions.
 * 
 * The main functionality is that if an EIQ editor is being closed while "attached"
 * to the QE, the attachment must be eliminated.
 * 
 * @author Tamas Szabo
 * @author Istvan Rath
 */
public class AttachFileEditorPartListener extends BasePartListener {

    //private final static String dialogTitle = ".eiq file editor closing";

    private AttachEiqEditorRegistrationHandler attachHandler;
    
    public AttachFileEditorPartListener(AttachEiqEditorRegistrationHandler h) {
    	this.attachHandler = h;
    }
    
    @Override
    public void partClosed(IWorkbenchPart part) {
        if (part instanceof IEditorPart) {
            IEditorPart closedEditor = (IEditorPart) part;
            IEditorInput editorInput = closedEditor.getEditorInput();

            if (editorInput instanceof FileEditorInput) {
                IFile file = ((FileEditorInput) editorInput).getFile();
                if (file != null && file.getFileExtension().matches("eiq") 
                		&& attachHandler.thereIsAnAttachedEditorForFile(file)
                		&& QueryExplorerPatternRegistry.getInstance().getFiles().contains(file)) {
//                    String question = "There are patterns (from file named '" + file.getName()
//                            + "') attached to the Query Explorer.\nWould you like to unregister them?";
//                    boolean answer = MessageDialog.openQuestion(closedEditor.getSite().getShell(), dialogTitle,
//                            question);
                	boolean answer = true;
                    if (answer) {
                    	// use PatternUnregistrationHandler
                    	PatternUnregistrationHandler puh = new PatternUnregistrationHandler();
                    	// collect registered patterns for file
                    	for (IQuerySpecification<?> qs : QueryExplorerPatternRegistry.getInstance().getRegisteredPatternsForFile(file)) {
                    		puh.unregisterPattern(qs.getFullyQualifiedName());
                    	}
                    	
                    }
                    // remove from attached state regardless of answer
                    attachHandler.removeAttachmentRegistrationForFile(file);
                }
            }
        }
    }

}
