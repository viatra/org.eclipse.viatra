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
package org.eclipse.viatra.transformation.debug.transformationtrace.serializer;

import org.eclipse.emf.common.util.URI;
import org.eclipse.viatra.transformation.debug.transformationtrace.model.TransformationTrace;
import org.eclipse.viatra.transformation.debug.transformationtrace.serializer.ITraceModelSerializer;

/**
 * Default trace model serializer implementation.
 * 
 * @author Peter Lunk
 */
public class DefaultTraceModelSerializer implements ITraceModelSerializer {

    public DefaultTraceModelSerializer(final URI targetlocation) {
    }

    @Override
    public TransformationTrace loadTraceModel() {
        throw new UnsupportedOperationException("Serialization is not implemented yet");
    }

    @Override
    public void serializeTraceModel(final TransformationTrace trace) {
        throw new UnsupportedOperationException("Serialization is not implemented yet");
    }
}
