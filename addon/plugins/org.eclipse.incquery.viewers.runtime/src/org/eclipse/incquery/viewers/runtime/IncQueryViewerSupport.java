/*******************************************************************************
 * Copyright (c) 2010-2013, Zoltan Ujhelyi, Istvan Rath and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Zoltan Ujhelyi - initial API and implementation
 *   Csaba Debreceni - add exception for deprecated methods
 *******************************************************************************/
package org.eclipse.incquery.viewers.runtime;

import org.eclipse.incquery.viewers.runtime.model.ViewerDataFilter;
import org.eclipse.incquery.viewers.runtime.model.ViewerDataModel;
import org.eclipse.incquery.viewers.runtime.model.ViewerState;
import org.eclipse.incquery.viewers.runtime.sources.ListContentProvider;
import org.eclipse.incquery.viewers.runtime.sources.QueryLabelProvider;
import org.eclipse.incquery.viewers.runtime.sources.TreeContentProvider;
import org.eclipse.jface.viewers.AbstractListViewer;
import org.eclipse.jface.viewers.AbstractTreeViewer;

/**
 * @author Zoltan Ujhelyi
 * 
 */
public class IncQueryViewerSupport {

	/**
	 * 
	 * @deprecated Use {@link #bind(AbstractListViewer, ViewerState)} where
	 *             {@link ViewerStateSet} consists of the shared data between
	 *             various viewers.
	 */
	public static void bind(AbstractListViewer viewer, ViewerDataModel model) {
		throw new UnsupportedOperationException();
	}

	/**
	 * 
	 * @deprecated Use {@link #bind(AbstractListViewer, ViewerStateSet)} where
	 *             {@link ViewerStateSet} consists of the shared data between
	 *             various viewers.
	 */
	public static void bind(AbstractListViewer viewer, ViewerDataModel model,
			ViewerDataFilter filter) {
	    throw new UnsupportedOperationException();
	 }

	/**
	 * Bind the viewerstate to the list viewer.
	 * @param viewer
	 * @param state
	 */
	public static void bind(AbstractListViewer viewer, ViewerState state) {
		// this seems to be necessary to avoid a databinding-related exception 
		// which comes when the viewer already had some contents before the current run		
		if (viewer.getInput()!=null) {
			viewer.setInput(null);
		}
		
		if (viewer.getContentProvider()!=null && viewer.getContentProvider() instanceof ListContentProvider) {
			// dispose already existing content provider
			((ListContentProvider)viewer.getContentProvider()).dispose();
		}

		
		if (viewer.getLabelProvider()!=null && viewer.getLabelProvider() instanceof QueryLabelProvider) {
			// dispose already existing label provider
			((QueryLabelProvider)viewer.getLabelProvider()).dispose();
		}

		viewer.setContentProvider(new ListContentProvider());
		viewer.setLabelProvider(new QueryLabelProvider(state));
		viewer.setInput(state);			
		viewer.refresh();
	}

	/**
	 * 
	 * @deprecated Use {@link #bind(AbstractTreeViewer, ViewerStateSet)} where
	 *             {@link ViewerStateSet} consists of the shared data between
	 *             various viewers.
	 */
	public static void bind(AbstractTreeViewer viewer, ViewerDataModel model) {
	    throw new UnsupportedOperationException();
	 }

	/**
	 * 
	 * @deprecated Use {@link #bind(AbstractTreeViewer, ViewerState)} where
	 *             {@link ViewerState} consists of the shared data between
	 *             various viewers.
	 */
	public static void bind(AbstractTreeViewer viewer, ViewerDataModel model,
			ViewerDataFilter filter) {
	    throw new UnsupportedOperationException();
	}

	/**
	 * Bind the viewerstate to the tree viewer.
	 * @param viewer
	 * @param state
	 */
	public static void bind(AbstractTreeViewer viewer, ViewerState state) {
		// this seems to be necessary to avoid a databinding-related exception 
		// which comes when the viewer already had some contents before the current run
		if (viewer.getInput()!=null) {
			viewer.setInput(null);
		}
		
		if (viewer.getContentProvider()!=null && viewer.getContentProvider() instanceof TreeContentProvider) {
			// dispose already existing content provider
			((TreeContentProvider)viewer.getContentProvider()).dispose();
		}
		
		if (viewer.getLabelProvider()!=null && viewer.getLabelProvider() instanceof QueryLabelProvider) {
			// dispose already existing label provider
			((QueryLabelProvider)viewer.getLabelProvider()).dispose();
		}
				
		viewer.setContentProvider(new TreeContentProvider());
		viewer.setLabelProvider(new QueryLabelProvider(state));
		viewer.setInput(state);	
		viewer.refresh();
	}
}
