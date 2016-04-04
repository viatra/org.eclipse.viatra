/*******************************************************************************
 * Copyright (c) 2010-2012, Andras Okros, Istvan Rath and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Andras Okros - second version implementation
 *******************************************************************************/
package org.eclipse.viatra.query.tooling.ui.queryexplorer;

import java.util.Collection;

import org.eclipse.emf.common.notify.Notifier;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.viatra.query.runtime.api.IModelConnectorTypeEnum;

/**
 * This interface provides all api calls for an instance model registered in the Query Explorer. Implementations of this
 * interface should contain the editor specific handling of loadModel, unloadModel, getNotifier and showLocation
 * methods.
 */
public interface IModelConnector {

    /**
     * Loads the instance model into the {@link QueryExplorer}.
     * 
     * @param modelConnectorTypeEnum
     *            The model type which should be loaded.
     */
    public abstract void loadModel(IModelConnectorTypeEnum modelConnectorTypeEnum);

    /**
     * Unloads the instance model from the {@link QueryExplorer}.
     */
    public abstract void unloadModel();

    /**
     * @param modelConnectorTypeEnum
     * @return A Notifier implementation for the given IModelConnectorType.
     */
    public Notifier getNotifier(IModelConnectorTypeEnum modelConnectorTypeEnum);

    /**
     * @param locationObjects
     *            Shows the location of these objects inside the specific editor.
     */
    public abstract void showLocation(Object[] locationObjects);
    
    /**
     * @return a workbench part (view, editor) which is the owner of the model adapted by the model connector
     */
    public IWorkbenchPart getOwner();

    /**
     * Returns the objects currently selected at the adapted model. If there are both domain and view model elements in
     * the adapted model, it is expected that the domain model elements are returned.
     * 
     * @return a non-null, but possibly empty collection of model objects
     */
    public Collection<EObject> getSelectedEObjects();
}
