/*******************************************************************************
 * Copyright (c) 2010-2015, Denes Harmath, Istvan Rath and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Denes Harmath - initial API and implementation
 *******************************************************************************/
package org.eclipse.incquery.facet.browser;

import org.eclipse.emf.common.notify.Notifier;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.gmt.modisco.infra.browser.editors.EcoreBrowser;
import org.eclipse.incquery.runtime.api.IModelConnectorTypeEnum;
import org.eclipse.incquery.tooling.ui.queryexplorer.adapters.EMFModelConnector;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IEditorPart;

/**
 * Model connector implementation for the EMF Facet Model Browser editor.
 */
@SuppressWarnings("restriction")
public class FacetModelConnector extends EMFModelConnector {

    public FacetModelConnector(IEditorPart editorPart) {
        super(editorPart);
    }

    @Override
    public Notifier getNotifier(IModelConnectorTypeEnum modelConnectorTypeEnum) {
        Notifier result = null;
        if (editorPart instanceof EcoreBrowser) {
            EcoreBrowser ecoreBrowser = (EcoreBrowser) editorPart;
            if (IModelConnectorTypeEnum.RESOURCESET.equals(modelConnectorTypeEnum)) {
                return ecoreBrowser.getEditingDomain().getResourceSet();
            } else if (IModelConnectorTypeEnum.RESOURCE.equals(modelConnectorTypeEnum)) {
                ISelection selection = ecoreBrowser.getSelection();
                if (selection instanceof IStructuredSelection) {
                    final IStructuredSelection structuredSelection = (IStructuredSelection) selection;
                    Object firstElement = structuredSelection.getFirstElement();
                    if (firstElement instanceof EObject) {
                        EObject eObject = (EObject) firstElement;
                        return eObject.eResource();
                    }
                }
            }
        }
        return result;
    }

    @Override
    public void showLocation(Object[] locationObjects) {
        IEditorPart editorPart = getKey().getEditorPart();
        if (editorPart instanceof EcoreBrowser) {
            EcoreBrowser ecoreBrowser = (EcoreBrowser) editorPart;
            if (locationObjects.length > 0) {
                Object object = locationObjects[0];
                if (object instanceof EObject) {
                    EObject eObject = (EObject) object;
                    ecoreBrowser.browseTo(eObject);
                }
            }
        }
        workbenchPage.bringToTop(editorPart);
    }

}
