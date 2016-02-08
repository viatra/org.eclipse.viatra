/*******************************************************************************
 * Copyright (c) 2010-2015, Zoltan Ujhelyi, Istvan Rath and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Zoltan Ujhelyi - initial API and implementation
 *******************************************************************************/
package org.eclipse.viatra.query.runtime.extensibility;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.viatra.query.runtime.api.IQuerySpecification;

/**
 * An extension factory to access PQuery instances from Query Specifications.
 * 
 * @author Zoltan Ujhelyi
 *
 */
public class PQueryExtensionFactory extends SingletonExtensionFactory {

    @Override
    public Object create() throws CoreException {
        final Object _spec = super.create();
        if (_spec instanceof IQuerySpecification<?>) {
            return ((IQuerySpecification<?>) _spec).getInternalQueryRepresentation();
        }
        throw new CoreException(new Status(IStatus.ERROR, getBundle().getSymbolicName(), "Cannot instantiate PQuery instance."));
    }

}
