/**
 * Copyright (c) 2010-2016, Peter Lunk, IncQuery Labs Ltd.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-v20.html.
 * 
 * SPDX-License-Identifier: EPL-2.0
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
