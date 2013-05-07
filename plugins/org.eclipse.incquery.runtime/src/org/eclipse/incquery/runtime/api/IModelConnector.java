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
package org.eclipse.incquery.runtime.api;

import org.eclipse.emf.common.notify.Notifier;

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

}
