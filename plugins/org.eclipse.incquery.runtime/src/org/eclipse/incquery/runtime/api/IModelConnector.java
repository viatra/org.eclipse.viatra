/*******************************************************************************
 * Copyright (c) 2010-2012, Tamas Szabo, Istvan Rath and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Tamas Szabo - initial API and implementation
 *   Andras Okros - second version implementation
 *******************************************************************************/
package org.eclipse.incquery.runtime.api;

import org.eclipse.emf.common.notify.Notifier;

/**
 * FIXME do it. The class represents an instance model registered in the Query Explorer along with its source
 * {@link IEditorPart}. Subclasses of this class must implement the editor specific handling of load/unload/showLocation
 * actions.
 */
public interface IModelConnector {

    /**
     * Loads the instance model into the {@link QueryExplorer}
     */
    public abstract void loadModel(IModelConnectorTypeEnum modelConnectorTypeEnum);

    @Deprecated
    // FIXME DO IT: Check if we need this in the long run.
    public Notifier getNotifier(IModelConnectorTypeEnum modelConnectorTypeEnum);

    @Deprecated
    // FIXME DO IT: Check if we need this in the long run.
    public abstract void loadModel(Notifier notifier);

    /**
     * Unloads the instance model from the {@link QueryExplorer}
     */
    public abstract void unloadModel();

    /**
     * Shows the location of the given objects inside the specific editor
     * 
     * @param locationObjects
     *            the objects whose location will be shown
     */
    public abstract void showLocation(Object[] locationObjects);

}
