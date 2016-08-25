/**
 * Copyright (c) 2010-2016, Peter Lunk, IncQuery Labs Ltd.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *   Peter Lunk - initial API and implementation
 */
package org.eclipse.viatra.transformation.debug.transformationtrace.serializer

import org.eclipse.emf.common.util.URI
import org.eclipse.viatra.transformation.debug.transformationtrace.model.TransformationTrace

/**
 * Default trace model serializer implementation.
 * 
 * @author Peter Lunk
 */
class DefaultTraceModelSerializer implements ITraceModelSerializer {
    URI location

    new(URI targetlocation) {
        this.location = targetlocation
    }

    override loadTraceModel() {
        throw new UnsupportedOperationException("Serialization is not implemented yet") 
    }

    override serializeTraceModel(TransformationTrace trace) {
        throw new UnsupportedOperationException("Serialization is not implemented yet") 
    }

}
