/*******************************************************************************
 * Copyright (c) 2010-2012, Andras Okros, Tamas Szabo, Istvan Rath and Daniel Varro

 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Tamas Szabo - initial API and implementation
 *   Andras Okros - second version implementation
 *******************************************************************************/
package org.eclipse.viatra.query.tooling.ui.queryexplorer.adapters;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

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
import org.eclipse.viatra.query.tooling.ui.ViatraQueryGUIPlugin;
import org.eclipse.viatra.query.tooling.ui.queryexplorer.IModelConnector;
import org.eclipse.viatra.query.tooling.ui.queryexplorer.content.matcher.PatternMatcherRootContentKey;
import org.eclipse.viatra.query.tooling.ui.queryexplorer.util.ModelEditorPartListener;
import org.eclipse.viatra.query.tooling.ui.util.IModelConnectorListener;

import com.google.common.base.Preconditions;
import com.google.common.collect.Iterators;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

/**
 * Model connector implementation for the default EMF generated model editors.
 */
public class EMFModelConnector implements IModelConnector {

    protected IEditorPart editorPart;

    protected ILog logger;

    private PatternMatcherRootContentKey key;

    protected IWorkbenchPage workbenchPage;

    private ModelEditorPartListener modelEditorPartListener;
    
    private Set<IModelConnectorListener> listeners;

    public EMFModelConnector(IEditorPart editorPart) {
        super();
        this.logger = ViatraQueryGUIPlugin.getDefault().getLog();
        this.editorPart = editorPart;
        this.listeners = Sets.newHashSet();
    }

    @Override
    public void loadModel(IModelConnectorTypeEnum modelConnectorTypeEnum) {
        Notifier notifier = getNotifier(modelConnectorTypeEnum);
        if (notifier != null) {
            key = new PatternMatcherRootContentKey(editorPart, notifier);
            workbenchPage = key.getEditorPart().getSite().getPage();
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
        navigateToElements(key.getEditorPart(), preparedSelection);
        workbenchPage.bringToTop(key.getEditorPart());
        if (key.getEditorPart() instanceof ISelectionProvider) {
            ISelectionProvider selectionProvider = (ISelectionProvider) key.getEditorPart();
            selectionProvider.setSelection(preparedSelection);
            
        }
        reflectiveSetSelection(key.getEditorPart(), preparedSelection);
    }

    // XXX This is only needed for the current QueryExplorer. In the future these should be removed.
    /**
     * @deprecated
     * @noreference This method is only used by the Query Explorer
     */
    public PatternMatcherRootContentKey getKey() {
        return key;
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
                logger.log(new Status(IStatus.INFO, ViatraQueryGUIPlugin.PLUGIN_ID, "Error while setting selection. If this is not an EMF Tree editor, consider providing a specialized ModelConnector implementation.", e));
            }
        }
    }

    protected TreeSelection prepareSelection(Object[] locationObjects) {
        List<TreePath> paths = new ArrayList<TreePath>();
        for (Object o : locationObjects) {
            if (o instanceof EObject) {
                TreePath path = createTreePath(key.getEditorPart(), (EObject) o);
                if (path != null) {
                    paths.add(path);
                }
            }
        }

        if (paths.size() > 0) {
            return new TreeSelection(paths.toArray(new TreePath[1]));
        }
        return new TreeSelection();
    }

    protected void navigateToElements(IEditorPart editorPart, IStructuredSelection selection) {
        // ISelectionProvider selectionProvider = editorPart.getEditorSite().getSelectionProvider();
        ISelectionProvider selectionProvider = editorPart.getSite().getSelectionProvider();
        selectionProvider.setSelection(selection);
    }

    protected TreePath createTreePath(IEditorPart editorPart, EObject obj) {
        List<Object> nodes = new LinkedList<Object>();
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
            Iterator<EObject> selectionIterator = Iterators.filter((((IStructuredSelection) selection).iterator()), EObject.class);
            return Lists.newArrayList(selectionIterator);
        } else {
            return Collections.emptyList();
        }
    }
    
    public boolean addListener(IModelConnectorListener listener) {
        Preconditions.checkArgument(listener != null, "Listener cannot be null!");
        boolean added = listeners.add(listener);
        return added;
    }

    public boolean removeListener(IModelConnectorListener listener) {
        Preconditions.checkArgument(listener != null, "Listener cannot be null!");
        boolean removed = listeners.remove(listener);
        return removed;
    }
    
}
