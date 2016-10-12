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

import org.eclipse.viatra.transformation.debug.transformationtrace.model.TransformationTrace;

/**
 * Interface that defines methods for loading and saving transformation trace models.
 *
 * @author Peter Lunk
 *
 */
public interface ITraceModelSerializer {
    public void serializeTraceModel(TransformationTrace trace);

    public TransformationTrace loadTraceModel();
}
