/**
 * Copyright (c) 2010-2016, Peter Lunk, IncQuery Labs Ltd.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-v20.html.
 * 
 * SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.viatra.transformation.debug.ui.views.model;

import java.util.Arrays;

public class CompositeItem{
    private String name;
    private Object[] children; 
    
    public CompositeItem(String name, Object[] children) {
        super();
        this.name = name;
        this.children = Arrays.copyOf(children, children.length);
    }
    
    public String getName() {
        return name;
    }
    
    public Object[] getChildren() {
        return Arrays.copyOf(children, children.length);
    }
}
