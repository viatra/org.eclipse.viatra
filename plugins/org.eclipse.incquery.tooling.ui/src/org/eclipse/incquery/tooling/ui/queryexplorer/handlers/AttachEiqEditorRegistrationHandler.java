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

package org.eclipse.incquery.tooling.ui.queryexplorer.handlers;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IFile;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.handlers.HandlerUtil;
import org.eclipse.ui.part.FileEditorInput;
import org.eclipse.xtext.resource.XtextResource;
import org.eclipse.xtext.ui.editor.model.IXtextModelListener;
import org.eclipse.xtext.ui.editor.model.XtextDocument;
import org.eclipse.xtext.xbase.ui.editor.XbaseEditor;

import com.google.inject.Inject;
import com.google.inject.Injector;

/**
 * "Attach Eiq Editor" registration handler.
 * 
 * Attempts to glue itself to an open EIQ editor by registering an Xtext model change listener.
 * 
 * This listener will listen to resource (model) changes inside the  Eiq editor and update the Query Explorer
 * accordingly.
 * 
 * @author istvanrath
 *
 */
@SuppressWarnings("restriction")
public class AttachEiqEditorRegistrationHandler extends AbstractHandler {

    @Inject
    private Injector injector;

    @Override
    public Object execute(ExecutionEvent event) throws ExecutionException {
//        IFile file = null;
//        Resource res = null;
//        ISelection selection = HandlerUtil.getCurrentSelection(event);
//
//        if (selection instanceof IStructuredSelection) {
//            Object firstElement = ((IStructuredSelection) selection).getFirstElement();
//            if (firstElement instanceof IFile) {
//                file = (IFile) firstElement;
//            }
//        } else {
            IEditorPart editor = HandlerUtil.getActiveEditor(event);
            if (editor instanceof XbaseEditor) {
                FileEditorInput input = (FileEditorInput) HandlerUtil.getActiveEditorInput(event);
                // TODO check if we already have a model listener for that particular file
                ( (XtextDocument) ((XbaseEditor)editor).getDocument()).addModelListener(new TrickyXtextModelListener(input.getFile()));
            }
//        }

//        if (file != null) {
//            RuntimeMatcherRegistrator registrator = new RuntimeMatcherRegistrator(file, res);
//            injector.injectMembers(registrator);
//            Display.getDefault().asyncExec(registrator);
//        }

        return null;
    }
    
    private class TrickyXtextModelListener implements IXtextModelListener {

    	IFile targetFile = null;
    	
    	/**
		 * 
		 */
		public TrickyXtextModelListener(IFile f) {
			targetFile = f;
		}
    	
		/* (non-Javadoc)
		 * @see org.eclipse.xtext.ui.editor.model.IXtextModelListener#modelChanged(org.eclipse.xtext.resource.XtextResource)
		 */
		@Override
		public void modelChanged(XtextResource resource) {
			// update Query Explorer contents
			RuntimeMatcherRegistrator registrator = new RuntimeMatcherRegistrator(targetFile, resource);
	        injector.injectMembers(registrator);
	        Display.getDefault().asyncExec(registrator);
		}
    	
    }

}
