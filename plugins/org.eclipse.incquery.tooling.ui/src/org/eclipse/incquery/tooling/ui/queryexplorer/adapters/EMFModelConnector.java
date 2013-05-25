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
package org.eclipse.incquery.tooling.ui.queryexplorer.adapters;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import org.eclipse.core.runtime.ILog;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.emf.common.notify.Notifier;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.edit.domain.IEditingDomainProvider;
import org.eclipse.incquery.runtime.api.IModelConnector;
import org.eclipse.incquery.runtime.api.IModelConnectorTypeEnum;
import org.eclipse.incquery.tooling.ui.IncQueryGUIPlugin;
import org.eclipse.incquery.tooling.ui.queryexplorer.QueryExplorer;
import org.eclipse.incquery.tooling.ui.queryexplorer.content.matcher.ModelConnectorTreeViewerKey;
import org.eclipse.incquery.tooling.ui.queryexplorer.util.ModelEditorPartListener;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TreePath;
import org.eclipse.jface.viewers.TreeSelection;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPage;

/**
 * Model connector implementation for the default EMF generated model editors.
 */
public class EMFModelConnector implements IModelConnector {

    protected IEditorPart editorPart;

    protected ILog logger;

    private ModelConnectorTreeViewerKey key;

    protected IWorkbenchPage workbenchPage;

    private ModelEditorPartListener modelEditorPartListener;

    public EMFModelConnector(IEditorPart editorPart) {
        super();
        this.logger = IncQueryGUIPlugin.getDefault().getLog();
        this.editorPart = editorPart;
    }

    @Override
    public void loadModel(IModelConnectorTypeEnum modelConnectorTypeEnum) {
        Notifier notifier = getNotifier(modelConnectorTypeEnum);
        if (notifier != null) {
            key = new ModelConnectorTreeViewerKey(editorPart, notifier);
            workbenchPage = key.getEditorPart().getSite().getPage();
            modelEditorPartListener = new ModelEditorPartListener(this);
            workbenchPage.addPartListener(modelEditorPartListener);
            if (QueryExplorer.getInstance() != null) {
                QueryExplorer.getInstance().getMatcherTreeViewerRoot().addPatternMatcherRoot(key);
            }
        }
    }

    @Override
    public void unloadModel() {
        workbenchPage.removePartListener(modelEditorPartListener);
        if (QueryExplorer.getInstance() != null) {
            QueryExplorer.getInstance().getMatcherTreeViewerRoot().removePatternMatcherRoot(key);
        }
    }

    @Override
    public void showLocation(Object[] locationObjects) {
        IStructuredSelection preparedSelection = prepareSelection(locationObjects);
        navigateToElements(key.getEditorPart(), preparedSelection);
        workbenchPage.bringToTop(key.getEditorPart());
        //workbenchPage.activate(key.getEditorPart());
        reflectiveSetSelection(key.getEditorPart(), preparedSelection);
    }

    // XXX This is only needed for the current QueryExplorer. In the future these should be removed.
    public ModelConnectorTreeViewerKey getKey() {
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
        } else if (IModelConnectorTypeEnum.RESOURCE.equals(modelConnectorTypeEnum)) {
            if (editorPart instanceof ISelectionProvider) {
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
        try {
            Method m = editorPart.getClass().getMethod("setSelectionToViewer", Collection.class);
            if (m!=null) {
                m.invoke(editorPart, preparedSelection.toList());
            }
        } catch (NoSuchMethodException e) {
            logger.log(new Status(IStatus.ERROR, IncQueryGUIPlugin.PLUGIN_ID, "setSelectionToViewer method not found",
                    e));
        } catch (Exception e) {
            logger.log(new Status(IStatus.ERROR, IncQueryGUIPlugin.PLUGIN_ID, "setSelectionToViewer call failed", e));
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

}
