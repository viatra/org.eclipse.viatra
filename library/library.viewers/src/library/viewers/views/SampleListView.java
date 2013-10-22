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
package library.viewers.views;

import java.util.Collection;

import library.viewers.util.WriterQuerySpecification;

import org.eclipse.incquery.runtime.api.IModelConnectorTypeEnum;
import org.eclipse.incquery.runtime.exception.IncQueryException;
import org.eclipse.incquery.viewers.runtime.extensions.ViewersComponentConfiguration;
import org.eclipse.incquery.viewers.runtime.extensions.jface.IncQueryViewersJFaceViewSupport;
import org.eclipse.jface.viewers.ListViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.part.ViewPart;

import com.google.common.collect.Sets;

/**
 * Sample query-based list viewer using IncQuery Viewers Extensions for JFace viewers.
 * 
 * @author istvanrath
 *
 */
public class SampleListView extends ViewPart {
	
	/**
	 * The JFace-based list viewer.
	 */
	private ListViewer listViewer;

	/**
	 * The extension instance (helper object).
	 */
	private IncQueryViewersJFaceViewSupport viewSupport;

	public void createPartControl(final Composite parent) {
		try {
			// initialize list viewer
			this.listViewer = new ListViewer(parent, SWT.NONE);
			
			// set up the queries that this demo will be using
			final Collection<String> queries = Sets.newHashSet();
			queries.add(WriterQuerySpecification.instance().getPatternFullyQualifiedName());
			
			// initialize the helper object
			this.viewSupport = new IncQueryViewersJFaceViewSupport(
					this, 
					ViewersComponentConfiguration.fromQuerySpecFQNs( queries ),
					IModelConnectorTypeEnum.RESOURCESET, // specifies the scope of query evaluation
					listViewer);
			this.viewSupport.createPartControl(parent,this.listViewer.getControl());
		} catch (IncQueryException e) {
			// TODO proper logging
			e.printStackTrace();
		}
	}

	public void setFocus() {
		if (listViewer!=null) {
			listViewer.getControl().setFocus();
		}
	}

	public void dispose() {
		// it is important to dispose the helper object when the editor is being closed
		// in order to release all IncQuery Viewers internal structures
		this.viewSupport.dispose();
		super.dispose();
	}

}
