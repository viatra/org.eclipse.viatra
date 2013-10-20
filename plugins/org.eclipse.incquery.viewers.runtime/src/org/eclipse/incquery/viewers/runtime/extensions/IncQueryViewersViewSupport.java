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
package org.eclipse.incquery.viewers.runtime.extensions;

import org.eclipse.incquery.runtime.api.IncQueryEngine;
import org.eclipse.incquery.runtime.exception.IncQueryException;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StackLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IPartListener;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.part.ViewPart;

/**
 * Utility class to serve as an extension for {@link ViewPart}s wishing to use
 * IncQuery Viewers.
 * @author istvanrath
 *
 */
public abstract class IncQueryViewersViewSupport extends IncQueryViewersPartSupport {

	/**
	 * Constructs a new View support instance.
	 */
	public IncQueryViewersViewSupport(IViewPart _owner, ViewersComponentConfiguration _config) {
		super(_owner, _config);
	}
	
	protected IViewPart getOwner() {
		return (IViewPart)owner;
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.incquery.viewers.runtime.extensions.IncQueryViewersPartSupport#init()
	 */
	@Override
	public void init() {
		super.init();
		owner.getSite().getPage().addPartListener(partListener);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.incquery.viewers.runtime.extensions.IncQueryViewersPartSupport#dispose()
	 */
	@Override
	public void dispose() {
		owner.getSite().getPage().removePartListener(partListener);
		super.dispose();
	}
	
	 /**
     * Part listener for making sure that our views always show content which is relevant to the currently active
     * .
     */
    private final IPartListener partListener = new IPartListener() {
        @Override
        public void partOpened(IWorkbenchPart part) {
            testAndSet();
        }

        @Override
        public void partDeactivated(IWorkbenchPart part) {
            testAndSet();
        }

        @Override
        public void partClosed(IWorkbenchPart part) {
            testAndSet();
        }

        @Override
        public void partBroughtToTop(IWorkbenchPart part) {
            testAndSet();
        }

        @Override
        public void partActivated(IWorkbenchPart part) {
            testAndSet();
        }

		private void testAndSet() {
			IWorkbenchPart part = owner.getSite().getPage().getActivePart();
			if (part!=null) {
				IViewersAwarePart vapart = null;
				if (part instanceof IViewersAwarePart) {
					vapart = (IViewersAwarePart) part;
				} else {
					vapart = (IViewersAwarePart) part
							.getAdapter(IViewersAwarePart.class);
				}
				if (vapart != null) {
					if (!vapart.equals(contentsSource)) {
						setContentsSource(((IViewersAwarePart) vapart));
					} else if (contentsSource != null) {
						unsetContentsSource();
					}
				}
			}
		}
    };
    
    protected IViewersAwarePart contentsSource;
    
    private void setContentsSource(IViewersAwarePart p) {
    	this.contentsSource = p;
    	// TODO propertySheetPage setCurrent
    	bindModel();
    	showView();
    }
    
    private void unsetContentsSource() {
    	hideView();
    	unbindModel();
    	// proppage setCurrentEditor null
    	this.contentsSource = null;
    }

    protected IncQueryEngine getEngine(IViewersAwarePart p) {
    	try {
			return IncQueryEngine.on(p.getModel());
		} catch (IncQueryException e) {
			// TODO proper logging
			e.printStackTrace();
		}
    	return null;
    }
    
    // ***************** layout stuff ***************** //
    
    private Composite parent, cover, contents;

    private StackLayout layout;

    public void createPartControl(Composite _parent, Composite _contents) {
        parent = _parent;
        layout = new StackLayout();
        parent.setLayout(layout);
        contents = _contents;
        cover = new Composite(parent, SWT.NO_SCROLL);
        layout.topControl = cover;
    }
    
    private void showView() {
        layout.topControl = contents;
        parent.layout();
    }

    private void hideView() {
        layout.topControl = cover;
        parent.layout();
    }
    
    /**
     * Subclasses bind their viewer models here.
     */
    protected abstract void bindModel();

    /**
     * Subclasses unbind their viewer models here.
     */
    protected abstract void unbindModel();
    
    
    // ******************** TODO propertsheetpage support ************* //
}
