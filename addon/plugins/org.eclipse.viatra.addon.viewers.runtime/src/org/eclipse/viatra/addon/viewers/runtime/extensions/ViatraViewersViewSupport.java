/*******************************************************************************
 * Copyright (c) 2010-2013, Istvan Rath and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Istvan Rath - initial API and implementation
 *   Abel Hegedus - migrate to EMF scope
 *   
 *******************************************************************************/
package org.eclipse.viatra.addon.viewers.runtime.extensions;

import java.util.List;
import java.util.Objects;
import java.util.Set;

import org.eclipse.core.runtime.Assert;
import org.eclipse.emf.common.notify.Notifier;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.BusyIndicator;
import org.eclipse.swt.custom.StackLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IPartListener2;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchPartReference;
import org.eclipse.ui.part.ViewPart;
import org.eclipse.viatra.addon.viewers.runtime.model.ViewerState;
import org.eclipse.viatra.query.runtime.api.IModelConnectorTypeEnum;
import org.eclipse.viatra.query.runtime.api.ViatraQueryEngine;
import org.eclipse.viatra.query.runtime.base.api.BaseIndexOptions;
import org.eclipse.viatra.query.runtime.emf.EMFScope;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;

/**
 * Utility class to serve as an extension for {@link ViewPart}s wishing to use VIATRA Viewers.
 * 
 * @author Istvan Rath
 *
 */
public abstract class ViatraViewersViewSupport extends ViatraViewersPartSupport {

    /**
     * Defines the matching scope for the underlying {@link ViatraQueryEngine}. TODO is {@link IModelConnectorTypeEnum}
     * the proper choice for this?
     */
    protected IModelConnectorTypeEnum connectorType = IModelConnectorTypeEnum.RESOURCESET;

    /**
     * The {@link ViewerState} that represents the stateful model behind the contents shown by the owner.
     */
    protected ViewerState state;
    
    protected boolean delayUpdates = false;
    protected List<Object> currentSelection;
    
    protected final String ownerID;
    
    protected final IPartListener2 partListener = new IPartListener2() {
        
        private void updateModel() {
            if (currentSelection != null) {
                unbindModel();
                if (!disposed) {
                    final Display display = owner.getSite().getShell().getDisplay();
                    display.asyncExec(() -> BusyIndicator.showWhile(display, ViatraViewersViewSupport.this::doUpdateDisplay));
                }
            }
            delayUpdates = false;
        }
        
        @Override
        public void partActivated(IWorkbenchPartReference partRef) {
            if (Objects.equals(partRef.getId(), ownerID)) {
                updateModel();
            }
        }
        
        @Override
        public void partBroughtToTop(IWorkbenchPartReference partRef) {
            if (Objects.equals(partRef.getId(), ownerID)) {
                updateModel();
            }
        }
        
        @Override
        public void partClosed(IWorkbenchPartReference partRef) {
            if (Objects.equals(partRef.getId(), ownerID)) {
                delayUpdates = true;
            }
        }
        
        @Override
        public void partDeactivated(IWorkbenchPartReference partRef) {}
        
        @Override
        public void partOpened(IWorkbenchPartReference partRef) {
            if (Objects.equals(partRef.getId(), ownerID)) {
                updateModel();
            }
        }
        
        @Override
        public void partHidden(IWorkbenchPartReference partRef) {
            if (Objects.equals(partRef.getId(), ownerID)) {
                delayUpdates = true;
            }
        }
        
        @Override
        public void partVisible(IWorkbenchPartReference partRef) {
            if (Objects.equals(partRef.getId(), ownerID)) {
                updateModel();
            }
        }
        
        @Override
        public void partInputChanged(IWorkbenchPartReference partRef) {
            
        }
        
    };
    
    /**
     * Constructs a new View support instance.
     */
    public ViatraViewersViewSupport(IViewPart _owner, ViewersComponentConfiguration _config,
            IModelConnectorTypeEnum _scope) {
        super(_owner, _config);
        this.connectorType = _scope;
        this.ownerID = _owner.getViewSite().getId();
        _owner.getViewSite().getPage().addPartListener(partListener);
    }

    protected IViewPart getOwner() {
        return (IViewPart) owner;
    }

    @Override
    public void dispose() {
        getOwner().getViewSite().getPage().removePartListener(partListener);
        unbindModel();
        super.dispose();
    }

    @Override
    protected void onSelectionChanged(List<Object> objects) {
        if (Objects.equals(objects, currentSelection)) {
            return;
        }
        currentSelection = objects;
        // extract model source
        if (!delayUpdates) {
            doUpdateDisplay();
        }
    }

    protected void doUpdateDisplay() {
        if (disposed) {
            return;
        }
        EMFScope target = extractModelSource(currentSelection);
        if (target != null && !target.equals(this.modelSource)) {
            // we have found a new target
            unsetModelSource();
            setModelSource(target);
        }
    }
    
    protected EMFScope extractModelSource(List<Object> objects) {
        Set<Notifier> notifiers = ImmutableSet.copyOf(Iterables.filter(objects, Notifier.class));
        // extract logic
        switch (connectorType) {
        default:
        case RESOURCESET:
            for (Notifier n : notifiers) {
                if (n instanceof ResourceSet) {
                    return new EMFScope(n);
                } else if (n instanceof Resource) {
                    return new EMFScope(((Resource) n).getResourceSet());
                } else {
                    EObject eO = (EObject) n;
                    return new EMFScope(eO.eResource().getResourceSet());
                }
            }
            break;
        case RESOURCE:
            for (Notifier n : notifiers) {
                if (n instanceof ResourceSet) {
                    continue; // we cannot extract a resource from a resourceset reliably
                } else if (n instanceof Resource) {
                    return new EMFScope(((Resource) n));
                } else {
                    EObject eO = (EObject) n;
                    return new EMFScope(eO.eResource());
                }
            }
        }

        return new EMFScope(notifiers, new BaseIndexOptions());
    }

    protected EMFScope modelSource;

    private void setModelSource(EMFScope p) {
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

    protected ViatraQueryEngine getEngine() {
        Assert.isNotNull(this.modelSource);
        return ViatraQueryEngine.on(this.modelSource);
    }

    // ***************** layout stuff ***************** //

    private Composite parent, cover;
    private Control contents;

    private StackLayout layout;

    /**
     * Create the SWT UI for the owner.
     * 
     * @param _parent
     *            the SWT part received by the owner in its createPartControl method
     * @param _contents
     *            the SWT UI to be displayed for actual contents
     */
    public void createPartControl(Composite _parent, Control _contents) {
        parent = _parent;
        layout = new StackLayout();
        parent.setLayout(layout);
        contents = _contents;
        cover = new Composite(parent, SWT.NO_SCROLL);
        layout.topControl = cover;
        // init
        init();
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

    // ******************** TODO propertysheetpage support
}
