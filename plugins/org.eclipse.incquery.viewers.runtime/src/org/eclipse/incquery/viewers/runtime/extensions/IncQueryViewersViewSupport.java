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

import java.util.List;

import org.eclipse.core.runtime.Assert;
import org.eclipse.emf.common.notify.Notifier;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.incquery.runtime.api.IModelConnectorTypeEnum;
import org.eclipse.incquery.runtime.api.IncQueryEngine;
import org.eclipse.incquery.runtime.exception.IncQueryException;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StackLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.part.ViewPart;

/**
 * Utility class to serve as an extension for {@link ViewPart}s wishing to use
 * IncQuery Viewers.
 * @author istvanrath
 *
 */
public abstract class IncQueryViewersViewSupport extends IncQueryViewersPartSupport {

    // TODO change me
	protected IModelConnectorTypeEnum connectorType = IModelConnectorTypeEnum.RESOURCESET;
	
	/**
	 * Constructs a new View support instance.
	 */
	public IncQueryViewersViewSupport(IViewPart _owner, ViewersComponentConfiguration _config, IModelConnectorTypeEnum _scope) {
		super(_owner, _config);
		this.connectorType = _scope;
	}
	
	protected IViewPart getOwner() {
		return (IViewPart)owner;
	}
    
    /* (non-Javadoc)
     * @see org.eclipse.incquery.viewers.runtime.extensions.IncQueryViewersPartSupport#filteredSelectionChanged(java.util.List)
     */
    @Override
    protected void filteredSelectionChanged(List<Notifier> eObjects) {
    	Notifier target = extractModelSource(eObjects);
    	if (target!=null && !target.equals(this.modelSource)) {
    		// we have found a new target
    		setModelSource(target);
    	}
    }
    

    
    private Notifier extractModelSource(List<Notifier> notifiers) {
    	// extract logic
    	switch (connectorType) {
    	default:
    	case RESOURCESET:
	    	for (Notifier n : notifiers) {
	    		if (n instanceof ResourceSet) {
	    			return n;
	    		}
	    		else if (n instanceof Resource) {
	    			return ((Resource)n).getResourceSet();
	    		}
	    		else {
	    			EObject eO = (EObject)n;
	    			return eO.eResource().getResourceSet();
	    		}
	    	}
    	case RESOURCE:
    		for (Notifier n : notifiers) {
	    		if (n instanceof ResourceSet) {
	    			continue; // we cannot extract a resource from a resourceset reliably
	    		}
	    		else if (n instanceof Resource) {
	    			return ((Resource)n);
	    		}
	    		else {
	    			EObject eO = (EObject)n;
	    			return eO.eResource();
	    		}
	    	}
    	}
    	return null;
    }
    
    protected Notifier modelSource;
    
    private void setModelSource(Notifier p) {
    	this.modelSource = p;
    	// TODO propertySheetPage setCurrent
    	bindModel();
    	showView();
    }
    
    private void unsetModelSource() {
    	hideView();
    	unbindModel();
    	// proppage setCurrentEditor null
    	this.modelSource = null;
    }
    
    protected IncQueryEngine getEngine() {
    	Assert.isNotNull(this.modelSource);
    	try {
			return IncQueryEngine.on( this.modelSource );
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
