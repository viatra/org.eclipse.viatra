/*******************************************************************************
 * Copyright (c) 2010-2012, Andras Okros, Tamas Szabo, Istvan Rath and Daniel Varro
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-v20.html.
 * 
 * SPDX-License-Identifier: EPL-2.0
 *******************************************************************************/
package org.eclipse.viatra.query.runtime.ui.modelconnector;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.eclipse.core.runtime.ILog;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.emf.common.notify.Notifier;
import org.eclipse.emf.common.ui.viewer.IViewerProvider;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.edit.domain.IEditingDomainProvider;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TreePath;
import org.eclipse.jface.viewers.TreeSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.viatra.query.runtime.api.IModelConnectorTypeEnum;
import org.eclipse.viatra.query.runtime.matchers.util.Preconditions;
import org.eclipse.viatra.query.runtime.ui.ViatraQueryRuntimeUIPlugin;
import org.eclipse.viatra.query.runtime.ui.modelconnector.internal.ModelEditorPartListener;

/**
 * Model connector implementation for the default EMF generated model editors.
 */
public class EMFModelConnector implements IModelConnector {

    private static final String LISTENER_NULL_MSG = "Listener cannot be null!";

    protected IEditorPart editorPart;

    protected ILog logger;

    protected IWorkbenchPage workbenchPage;

    private ModelEditorPartListener modelEditorPartListener;
    
    private Set<IModelConnectorListener> listeners;

    public EMFModelConnector(IEditorPart editorPart) {
        super();
        this.logger = ViatraQueryRuntimeUIPlugin.getDefault().getLog();
        this.editorPart = editorPart;
        this.listeners = new HashSet<>();
    }

    @Override
    public void loadModel(IModelConnectorTypeEnum modelConnectorTypeEnum) {
        Notifier notifier = getNotifier(modelConnectorTypeEnum);
        if (notifier != null) {
            workbenchPage = editorPart.getSite().getPage();
            modelEditorPartListener = new ModelEditorPartListener(this);
            workbenchPage.addPartListener(modelEditorPartListener);
        }
    }

    @Override
    public void unloadModel() {
        for (IModelConnectorListener listener : listeners) {
            listener.modelUnloaded(this);
        }
        workbenchPage.removePartListener(modelEditorPartListener);
    }

    @Override
    public void showLocation(Object[] locationObjects) {
        IStructuredSelection preparedSelection = prepareSelection(locationObjects);
        navigateToElements(editorPart, preparedSelection);
        workbenchPage.bringToTop(editorPart);
        if (editorPart instanceof ISelectionProvider) {
            ISelectionProvider selectionProvider = (ISelectionProvider) editorPart;
            selectionProvider.setSelection(preparedSelection);
            
        }
        reflectiveSetSelection(editorPart, preparedSelection);
    }

    @Override
    public Notifier getNotifier(IModelConnectorTypeEnum modelConnectorTypeEnum) {
        Notifier result = null;
        if (IModelConnectorTypeEnum.RESOURCESET.equals(modelConnectorTypeEnum)) {
            if (editorPart instanceof IEditingDomainProvider) {
                IEditingDomainProvider editingDomainProvider = (IEditingDomainProvider) editorPart;
                result = editingDomainProvider.getEditingDomain().getResourceSet();
            }
        } else if (IModelConnectorTypeEnum.RESOURCE.equals(modelConnectorTypeEnum) && editorPart instanceof ISelectionProvider) {
            ISelectionProvider selectionProvider = (ISelectionProvider) editorPart;
            if (selectionProvider.getSelection() instanceof TreeSelection) {
                Object object = ((TreeSelection) selectionProvider.getSelection()).getFirstElement();
                if (object instanceof Resource) {
                    result = (Resource) object;
                } else if (object instanceof EObject) {
                    result = ((EObject) object).eResource();
                }
            }
        }
        return result;
    }

    /**
     * This is a somewhat "hackish" workaround in case the selection setting through the provider doesn't work.
     * 
     * Unfortunately, this seems to be the most reliable way to do this. *sigh*
     * 
     */
    private void reflectiveSetSelection(IEditorPart editorPart, IStructuredSelection preparedSelection) {
        if (editorPart instanceof IViewerProvider 
                && (((IViewerProvider) editorPart).getViewer() instanceof TreeViewer)) {
            try {
                Method m = editorPart.getClass().getMethod("setSelectionToViewer", Collection.class);
                if (m!=null) {
                    m.invoke(editorPart, preparedSelection.toList());
                }
            } catch (Exception e) {
                logger.log(new Status(IStatus.INFO, ViatraQueryRuntimeUIPlugin.PLUGIN_ID, "Error while setting selection. If this is not an EMF Tree editor, consider providing a specialized ModelConnector implementation.", e));
            }
        }
    }

    protected TreeSelection prepareSelection(Object[] locationObjects) {
        List<TreePath> paths = new ArrayList<>();
        for (Object o : locationObjects) {
            if (o instanceof EObject) {
                TreePath path = createTreePath(editorPart, (EObject) o);
                if (path != null) {
                    paths.add(path);
                }
            }
        }

        return paths.isEmpty() ? new TreeSelection() : new TreeSelection(paths.toArray(new TreePath[1]));
    }

    protected void navigateToElements(IEditorPart editorPart, IStructuredSelection selection) {
        // ISelectionProvider selectionProvider = editorPart.getEditorSite().getSelectionProvider();
        ISelectionProvider selectionProvider = editorPart.getSite().getSelectionProvider();
        selectionProvider.setSelection(selection);
    }

    protected TreePath createTreePath(IEditorPart editorPart, EObject obj) {
        List<Object> nodes = new LinkedList<>();
        nodes.add(obj);
        EObject tmp = obj.eContainer();

        while (tmp != null) {
            nodes.add(0, tmp);
            tmp = tmp.eContainer();
        }

        return new TreePath(nodes.toArray());
    }

    @Override
    public IWorkbenchPart getOwner() {
        return this.editorPart;
    }

    @Override
    public Collection<EObject> getSelectedEObjects() {
        return getSelectedEObjects(getCurrentSelection());
    }

    protected ISelection getCurrentSelection() {
        ISelectionProvider selectionProvider = editorPart.getSite().getSelectionProvider();
        return selectionProvider.getSelection();
    }
    
    protected Collection<EObject> getSelectedEObjects(ISelection selection) {
        if (selection instanceof IStructuredSelection) {
            return Arrays.stream(((IStructuredSelection) selection).toArray())
                    .filter(EObject.class::isInstance)
                    .map(EObject.class::cast).collect(Collectors.toList());
        } else {
            return Collections.emptyList();
        }
    }
    
    public boolean addListener(IModelConnectorListener listener) {
        Preconditions.checkArgument(listener != null, LISTENER_NULL_MSG);
        return listeners.add(listener);
    }

    public boolean removeListener(IModelConnectorListener listener) {
        Preconditions.checkArgument(listener != null, LISTENER_NULL_MSG);
        return listeners.remove(listener);
    }
    
}
