/*******************************************************************************
 * Copyright (c) 2010-2013, Istvan Rath and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   istvanrath - initial API and implementation
 *******************************************************************************/
package library.viewers.views

import com.google.common.collect.Sets
import library.viewers.util.InfluencedByQuerySpecification
import library.viewers.util.WriterQuerySpecification
import org.eclipse.gef4.zest.core.viewers.GraphViewer
import org.eclipse.gef4.zest.core.viewers.IZoomableWorkbenchPart
import org.eclipse.incquery.runtime.api.IModelConnectorTypeEnum
import org.eclipse.incquery.viewers.runtime.extensions.ViewersComponentConfiguration
import org.eclipse.incquery.viewers.runtime.zest.extensions.IncQueryViewersZestViewSupport
import org.eclipse.swt.SWT
import org.eclipse.swt.widgets.Composite
import org.eclipse.ui.part.ViewPart

/**
 * Sample graph view component based on IncQuery Viewers Extensions for Zest viewers.
 */
class SampleZestView extends ViewPart implements IZoomableWorkbenchPart {
	
	GraphViewer graphViewer;
	
	extension IncQueryViewersZestViewSupport viewSupport
	
	override createPartControl(Composite parent) {
		// initialize Zest viewer first
		graphViewer = new GraphViewer(parent, SWT.NONE)
		
		// initialize support object
		val queries = Sets.newHashSet
		queries.add( WriterQuerySpecification.instance.patternFullyQualifiedName )
		queries.add( InfluencedByQuerySpecification.instance.patternFullyQualifiedName )
		
		val config = ViewersComponentConfiguration.fromQuerySpecFQNs( queries )
		viewSupport = new IncQueryViewersZestViewSupport(
			this,config,IModelConnectorTypeEnum.RESOURCESET,graphViewer
		)
		
		// UI initialization with the help of support object
		createPartControl(parent,graphViewer.graphControl)
		// create a default toolbar
		createToolbar
		// "backward" selection synchronization
		site.setSelectionProvider(viewSupport)
	}
	
	override setFocus() {
		if (graphViewer!=null) {
			graphViewer.control.setFocus
		}
	}
	
	override dispose() {
		if (viewSupport!=null) {
			viewSupport.dispose
		}
		super.dispose()
	}
	
	override getZoomableViewer() {
		return graphViewer
	}
	
}