/*******************************************************************************
 * Copyright (c) 2010-2014, Marton Bur, Akos Horvath, Zoltan Ujhelyi, Istvan Rath and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Marton Bur - initial API and implementation
 *******************************************************************************/
package org.eclipse.viatra.query.tooling.localsearch.ui.debugger.provider;

import java.util.List;

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.viatra.query.tooling.localsearch.ui.debugger.provider.viewelement.SearchOperationViewerNode;
import org.eclipse.viatra.query.tooling.localsearch.ui.debugger.provider.viewelement.SearchPlanViewModel;

/**
 * Content provider class for the search plan tree viewer
 * 
 * @author Marton Bur
 *
 */
public class OperationListContentProvider implements ITreeContentProvider {

	@Override
	public void dispose() {
	}

	/**
	 * Initialiser method that is called after each top level search plan change
	 */
	@Override
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput /*new search plan*/) {
		
	}

	@Override
	public Object[] getElements(Object inputElement/*root*/) {
		if (inputElement != null) {
			SearchPlanViewModel model = (SearchPlanViewModel)inputElement;
			List<SearchOperationViewerNode> elements = model.getTopLevelElements();
			return elements.toArray(new Object[elements.size()]);
		}
		return new Object[0];
	}

	@Override
	public Object[] getChildren(Object parentElement) {
		if(parentElement instanceof SearchOperationViewerNode){
			return ((SearchOperationViewerNode) parentElement).getChildren().toArray();
		}
		return null;
	}

	@Override
	public Object getParent(Object element) {
		if(element instanceof SearchOperationViewerNode){
			return ((SearchOperationViewerNode) element).getParent();
		}
		return null;
	}

	@Override
	public boolean hasChildren(Object element) {
		if(element instanceof SearchOperationViewerNode){
			return ((SearchOperationViewerNode) element).getChildren().size() > 0;
		}
		return false;
	}

}
