/*******************************************************************************
 * Copyright (c) 2010-2012, Istvan Rath and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Tamas Szabo, Gabor Bergmann - initial API and implementation
 *******************************************************************************/
package org.eclipse.viatra.query.runtime.base.api;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;

/**
 * Interface for observing insertion / deletion of instances of EClass.
 * 
 * @author Tamas Szabo
 * 
 */
public interface InstanceListener {

    /**
     * Called when the given instance was added to the model.
     * 
     * @param clazz
     *            an EClass registered for this listener, for which a new instance (possibly an instance of a subclass) was inserted into the model
     * @param instance
     *            an EObject instance that was inserted into the model
     */
    public void instanceInserted(EClass clazz, EObject instance);

    /**
     * Called when the given instance was removed from the model.
     * 
     * @param clazz
     *            an EClass registered for this listener, for which an instance (possibly an instance of a subclass) was removed from the model
     * @param instance
     *            an EObject instance that was removed from the model
     */
    public void instanceDeleted(EClass clazz, EObject instance);
}
