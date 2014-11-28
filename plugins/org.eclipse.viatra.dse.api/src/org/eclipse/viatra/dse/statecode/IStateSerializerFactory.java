/*******************************************************************************
 * Copyright (c) 2010-2014, Miklos Foldenyi, Andras Szabolcs Nagy, Abel Hegedus, Akos Horvath, Zoltan Ujhelyi and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Contributors:
 *   Miklos Foldenyi - initial API and implementation
 *   Andras Szabolcs Nagy - initial API and implementation
 *******************************************************************************/
package org.eclipse.viatra.dse.statecode;

import org.eclipse.emf.common.notify.Notifier;
import org.eclipse.incquery.runtime.api.IncQueryEngine;

/**
 * Interface for a factory class that creates instances of {@link IStateSerializer} objects. This is required because
 * serializers have to be created on-demand if the design space exploration process decides that a new thread is to be
 * spawned. Since each thread requires it's own working model instance and a serializer is linked to the underlying
 * model (via the {@link IncQueryEngine}), a new {@link IStateSerializer} needs to be created per processing thread.
 * 
 * @author Miklos Foldenyi
 * 
 */
public interface IStateSerializerFactory {

    /**
     * Creates a new {@link IStateSerializer} instance specific to this {@link IStateSerializerFactory} on top of the
     * given {@link IncQueryEngine} and the underlying EMF model instance.
     * 
     * 
     * @param modelRoot
     *            The root of the model (given to the DSE engine). Can be an EObject, Resource or ResourceSet.
     * @return the new {@link IStateSerializer} instance specific to this working model.
     * @throws UnsupportedMetaModel
     *             in case of an unsupported EMF model.
     */
    IStateSerializer createStateSerializer(Notifier modelRoot) throws UnsupportedMetaModel;

    /**
     * {@link IStateSerializer}s and thus {@link IStateSerializerFactory}s can be meta model specific, resulting in an
     * {@link UnsupportedMetaModel} exception if they are initialized on a non-supported EMF model instance.
     */
    class UnsupportedMetaModel extends RuntimeException {
        private static final long serialVersionUID = -1127325174889348662L;
    }
}
