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
package org.eclipse.incquery.viewers.runtime.extensions.jface;

import org.eclipse.core.runtime.Assert;
import org.eclipse.incquery.runtime.api.IModelConnectorTypeEnum;
import org.eclipse.incquery.runtime.api.IncQueryEngine;
import org.eclipse.incquery.viewers.runtime.IncQueryViewerSupport;
import org.eclipse.incquery.viewers.runtime.extensions.IncQueryViewersViewSupport;
import org.eclipse.incquery.viewers.runtime.extensions.ViewersComponentConfiguration;
import org.eclipse.incquery.viewers.runtime.model.IncQueryViewerDataModel;
import org.eclipse.incquery.viewers.runtime.model.ViewerState;
import org.eclipse.incquery.viewers.runtime.model.ViewerState.ViewerStateFeature;
import org.eclipse.jface.viewers.AbstractListViewer;
import org.eclipse.jface.viewers.AbstractTreeViewer;
import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.part.ViewPart;

import com.google.common.collect.ImmutableSet;

/**
 * 
 * IncQuery Viewers support class to support {@link ViewPart}s with a single JFace Viewer.
 * 
 * @author istvanrath
 *
 */
public class IncQueryViewersJFaceViewSupport extends IncQueryViewersViewSupport {

	protected StructuredViewer jfaceViewer;
	
	protected ViewerState state;
	
	/**
	 * @param _owner
	 */
	public IncQueryViewersJFaceViewSupport(IViewPart _owner, ViewersComponentConfiguration _config, IModelConnectorTypeEnum _scope, StructuredViewer _jfaceViewer) {
		super(_owner, _config, _scope);
		this.jfaceViewer = _jfaceViewer;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.incquery.viewers.runtime.extensions.IncQueryViewersViewSupport#bindModel()
	 */
	@Override
	protected void bindModel() {
		Assert.isNotNull(this.configuration);
		Assert.isNotNull(this.configuration.getPatterns());
		
		if (state!=null && !state.isDisposed()) {
    		state.dispose();
    	}
		IncQueryEngine engine = getEngine();
		if (engine!=null) {
			this.configuration.setModel(engine.getScope());
			state = IncQueryViewerDataModel.newViewerState(
					engine, 
	    			this.configuration.getPatterns(), 
	    			this.configuration.getFilter(), 
	    			ImmutableSet.of(ViewerStateFeature.EDGE, ViewerStateFeature.CONTAINMENT));
			if (jfaceViewer instanceof AbstractListViewer) {
				IncQueryViewerSupport.bind(((AbstractListViewer)jfaceViewer), state);
			}
			else if (jfaceViewer instanceof AbstractTreeViewer) {
				IncQueryViewerSupport.bind(((AbstractTreeViewer)jfaceViewer), state);
			}
		}
	}

	/* (non-Javadoc)
	 * @see org.eclipse.incquery.viewers.runtime.extensions.IncQueryViewersViewSupport#unbindModel()
	 */
	@Override
	protected void unbindModel() {
		jfaceViewer.setInput(null);
		state.dispose();
	}

}
