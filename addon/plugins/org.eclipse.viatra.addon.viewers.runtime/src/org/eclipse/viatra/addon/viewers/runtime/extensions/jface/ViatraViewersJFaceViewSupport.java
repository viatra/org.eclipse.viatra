/*******************************************************************************
 * Copyright (c) 2010-2013, istvanrath, Istvan Rath and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   istvanrath - initial API and implementation
 *******************************************************************************/
package org.eclipse.viatra.addon.viewers.runtime.extensions.jface;

import java.util.List;

import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.viewers.AbstractListViewer;
import org.eclipse.jface.viewers.AbstractTreeViewer;
import org.eclipse.jface.viewers.ContentViewer;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.part.ViewPart;
import org.eclipse.viatra.addon.viewers.runtime.ViatraViewerSupport;
import org.eclipse.viatra.addon.viewers.runtime.extensions.ViatraViewersViewSupport;
import org.eclipse.viatra.addon.viewers.runtime.extensions.SelectionHelper;
import org.eclipse.viatra.addon.viewers.runtime.extensions.ViewersComponentConfiguration;
import org.eclipse.viatra.addon.viewers.runtime.model.ViatraViewerDataModel;
import org.eclipse.viatra.addon.viewers.runtime.model.ViewerState.ViewerStateFeature;
import org.eclipse.viatra.query.runtime.api.IModelConnectorTypeEnum;
import org.eclipse.viatra.query.runtime.api.ViatraQueryEngine;
import org.eclipse.viatra.query.runtime.emf.EMFScope;

import com.google.common.collect.ImmutableSet;

/**
 * 
 * VIATRA Viewers support class to support {@link ViewPart}s with a single JFace Viewer.
 * 
 * @author istvanrath
 *
 */
public class ViatraViewersJFaceViewSupport extends ViatraViewersViewSupport implements ISelectionProvider {

	protected ContentViewer jfaceViewer;
	
	public ViatraViewersJFaceViewSupport(IViewPart _owner, ViewersComponentConfiguration _config, IModelConnectorTypeEnum _scope, ContentViewer _jfaceViewer) {
		super(_owner, _config, _scope);
		this.jfaceViewer = _jfaceViewer;
	}

	@Override
	protected void init() {
		super.init();
		jfaceViewer.addSelectionChangedListener(selectionHelper.trickyListener);
	}
	
	@Override
	public void dispose() {
	    if (jfaceViewer != null) {
	        jfaceViewer.removeSelectionChangedListener(selectionHelper.trickyListener);
	    }
		super.dispose();
	}
	
	@Override
	protected void bindModel() {
		Assert.isNotNull(this.configuration);
		Assert.isNotNull(this.configuration.getPatterns());
		
		if (state!=null && !state.isDisposed()) {
    		state.dispose();
    	}
		ViatraQueryEngine engine = getEngine();
		if (engine!=null) {
			this.configuration.setModel((EMFScope) engine.getScope());
			state = ViatraViewerDataModel.newViewerState(
					engine, 
	    			this.configuration.getPatterns(), 
	    			this.configuration.getFilter(), 
	    			ImmutableSet.of(ViewerStateFeature.EDGE, ViewerStateFeature.CONTAINMENT));
			if (jfaceViewer instanceof AbstractListViewer) {
				ViatraViewerSupport.bind(((AbstractListViewer)jfaceViewer), state);
			}
			else if (jfaceViewer instanceof AbstractTreeViewer) {
				ViatraViewerSupport.bind(((AbstractTreeViewer)jfaceViewer), state);
			}
		}
	}

	@Override
	protected void unbindModel() {
		if (jfaceViewer != null && !jfaceViewer.getControl().isDisposed() && jfaceViewer.getInput() !=null) {
        	jfaceViewer.setInput(null);
        	jfaceViewer = null;
        }
		if (state != null && !state.isDisposed()) {
			state.dispose();
		}
	}

    // ******************** selection synchronization support **********//
    
	// "Backward"
	
	SelectionHelper selectionHelper = new SelectionHelper();

	@Override
	public void addSelectionChangedListener(ISelectionChangedListener listener) {
		selectionHelper.selectionChangedListeners.add(listener);
	}

	@Override
	public ISelection getSelection() {
		return selectionHelper.unwrapElements_ViewersElementsToEObjects(jfaceViewer.getSelection());
	}

	@Override
	public void removeSelectionChangedListener(
			ISelectionChangedListener listener) {
		selectionHelper.selectionChangedListeners.remove(listener);
	}

	@Override
	public void setSelection(ISelection selection) {
		// unwrap elements
		this.jfaceViewer.setSelection(selectionHelper.unwrapElements_EObjectsToViewersElements(selection, state));
	}

	
	// "Forward"
	
	@Override
	protected void onSelectionChanged(List<Object> objects) {
		super.onSelectionChanged(objects);
		// additionally, attempt to sychronize our contents
        setSelection(new StructuredSelection(objects));
	}

}
