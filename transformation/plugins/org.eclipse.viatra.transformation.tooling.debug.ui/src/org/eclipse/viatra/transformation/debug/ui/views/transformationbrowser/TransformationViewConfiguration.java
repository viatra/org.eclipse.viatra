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
package org.eclipse.viatra.transformation.debug.ui.views.transformationbrowser;

public enum TransformationViewConfiguration{
    RULE_BROWSER("Transformation Rules"),
    CONFLICTSET_BROWSER("Conflict Set State");
    
    private String name;
    
    private TransformationViewConfiguration(String name){
        this.name = name;
    }
    
    public boolean equalsName(String otherName) {
        return (otherName == null) ? false : name.equals(otherName);
    }

    @Override
    public String toString() {
       return this.name;
    }
    
}
