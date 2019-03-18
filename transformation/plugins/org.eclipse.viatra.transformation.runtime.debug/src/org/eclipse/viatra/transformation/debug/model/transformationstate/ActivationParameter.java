/**
 * Copyright (c) 2010-2016, Peter Lunk, IncQuery Labs Ltd.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-v20.html.
 * 
 * SPDX-License-Identifier: EPL-2.0
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
