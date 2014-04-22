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

import java.util.Map;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IFile;
import org.eclipse.incquery.tooling.ui.queryexplorer.util.AttachFileEditorPartListener;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.handlers.HandlerUtil;
import org.eclipse.ui.part.FileEditorInput;
import org.eclipse.xtext.resource.XtextResource;
import org.eclipse.xtext.ui.editor.model.IXtextModelListener;
import org.eclipse.xtext.ui.editor.model.XtextDocument;
import org.eclipse.xtext.util.concurrent.IUnitOfWork;
import org.eclipse.xtext.xbase.ui.editor.XbaseEditor;

import com.google.common.collect.Maps;
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
    
    /**
     * Map to store already registered listeners.
     */
    private Map<IFile, TrickyXtextModelListener> listenerMap = Maps.newHashMap();
    
    private AttachFileEditorPartListener pListener = new AttachFileEditorPartListener(this);
    
    private boolean pListenerRegistered = false;

    public boolean thereIsAnAttachedEditorForFile(IFile f) {
    	return listenerMap.containsKey(f);
    }
    
    public void removeAttachmentRegistrationForFile(IFile f) {
    	listenerMap.remove(f);
    }
    
    private void registerListener(XbaseEditor editor) {
    	FileEditorInput input = (FileEditorInput) editor.getEditorInput();
        IFile targetfile = input.getFile();
    	XtextDocument doc = (XtextDocument) editor.getDocument();
    	
    	// check if we already have a model listener for that particular file
    	if (listenerMap.containsKey(targetfile)) {
    		// attempt to remove listener
    		doc.removeModelListener(listenerMap.get(targetfile));
    		//System.out.println("removed listener");
    	}
    	// create and add a new listener
        final TrickyXtextModelListener l = new TrickyXtextModelListener(targetfile);
        doc.addModelListener(l);
        listenerMap.put(targetfile, l);
        // force an initial trigger
        doc.readOnly(
        new IUnitOfWork<Object, XtextResource>(){
			@Override
			public String exec(XtextResource resource) {
				l.modelChanged(resource);
				return null;
			}
         });
        // register our part listener if not registered yet
        if (!this.pListenerRegistered) {
	        editor.getEditorSite().getPage().addPartListener(this.pListener);
	        this.pListenerRegistered = true;
        }
    }
    
    
    @Override
    public Object execute(ExecutionEvent event) throws ExecutionException {
        IEditorPart editor = HandlerUtil.getActiveEditor(event);
        if (editor instanceof XbaseEditor) {
            registerListener((XbaseEditor)editor);
        }
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
			// update Query Explorer contents if the are not errors
			if (resource!=null && resource.getErrors().isEmpty()) {
				RuntimeMatcherRegistrator registrator = new RuntimeMatcherRegistrator(targetFile, resource);
		        injector.injectMembers(registrator);
		        Display.getDefault().asyncExec(registrator);
			}
		}
    	
    }

}
