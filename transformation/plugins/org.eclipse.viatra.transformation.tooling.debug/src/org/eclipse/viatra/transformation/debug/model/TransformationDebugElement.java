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
package org.eclipse.viatra.transformation.debug.model;

import org.eclipse.debug.core.model.DebugElement;

public class TransformationDebugElement extends DebugElement{
    public static final String MODEL_ID = "org.eclipse.viatra.transformation.debug.model";
    
    public TransformationDebugElement(TransformationDebugTarget target) {
        super(target);
    }
    
    @Override
    public String getModelIdentifier() {
        return MODEL_ID;
    }
}
