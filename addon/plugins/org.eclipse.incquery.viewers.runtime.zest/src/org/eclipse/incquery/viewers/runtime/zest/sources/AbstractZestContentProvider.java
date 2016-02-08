/*******************************************************************************
 * Copyright (c) 2010-2013, Zoltan Ujhelyi, Istvan Rath and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Zoltan Ujhelyi - initial API and implementation
 *******************************************************************************/
package org.eclipse.incquery.viewers.runtime.zest.sources;

import org.eclipse.gef4.zest.core.viewers.GraphViewer;
import org.eclipse.incquery.viewers.runtime.model.Containment;
import org.eclipse.incquery.viewers.runtime.model.Item;
import org.eclipse.incquery.viewers.runtime.model.ViewerState;
import org.eclipse.incquery.viewers.runtime.model.listeners.AbstractViewerStateListener;
import org.eclipse.jface.viewers.Viewer;

import com.google.common.base.Preconditions;

/**
 * Abstract base class for Zest content providers
 * @author Zoltan Ujhelyi
 *
 */
public abstract class AbstractZestContentProvider extends
		AbstractViewerStateListener   {

	protected GraphViewer viewer;
	protected ViewerState state;
	protected boolean displayContainment;

	public AbstractZestContentProvider() {
		this(false);
	}
	
	public AbstractZestContentProvider(boolean displayContainment) {
		super();
		this.displayContainment = displayContainment;
	}

	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		Preconditions.checkArgument(viewer instanceof GraphViewer);
		this.viewer = (GraphViewer) viewer;
		if (oldInput instanceof ViewerState) {
			((ViewerState) oldInput).removeStateListener(this);
		}
		if (newInput == null) {
			this.state = null;
		} else if (newInput instanceof ViewerState) {
			this.state = (ViewerState) newInput;
			if (this.state.isDisposed()) {
				this.state = null;
			} else {
				state.addStateListener(this);
			}
		} else {
			throw new IllegalArgumentException(String.format("Invalid input type %s for Zest Viewer.", newInput
	                .getClass().getName()));
		}
	}

	@Override
	public void itemAppeared(final Item item) {
	    viewer.getGraphControl().getDisplay().syncExec(new Runnable() {
            
            @Override
            public void run() {
                viewer.addNode(item);
            }
        });
	}

	@Override
	public void itemDisappeared(final Item item) {
	    viewer.getGraphControl().getDisplay().syncExec(new Runnable() {
            
            @Override
            public void run() {
                viewer.removeGraphModelNode(item);
                viewer.removeNode(item);
            }
        });
	}

	@Override
	public void containmentAppeared(Containment containment) {
		if (displayContainment) {
			edgeAppeared(containment);
		}
	}

	@Override
	public void containmentDisappeared(Containment containment) {
		if (displayContainment) {
			edgeDisappeared(containment);
		}
	}

	public void dispose() {
		if (state != null) {
			state.removeStateListener(this);
		}
		
	}

}