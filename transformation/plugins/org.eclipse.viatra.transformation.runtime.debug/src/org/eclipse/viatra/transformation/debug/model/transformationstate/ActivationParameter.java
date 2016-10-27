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
package org.eclipse.viatra.transformation.debug.model.transformationstate;

import java.io.Serializable;

public class ActivationParameter implements Serializable{
    private static final long serialVersionUID = 4958117091707454774L;
    private final Serializable value;
    private final String name;
    
    
    public ActivationParameter(TransformationModelElement value, String name) {
        super();
        this.value = value;
        this.name = name;
    }


    public Object getValue() {
        return value;
    }


    public String getName() {
        return name;
    }
}
