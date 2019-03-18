/*******************************************************************************
 * Copyright (c) 2004-2013, Zoltan Ujhelyi and Daniel Varro
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-v20.html.
 * 
 * SPDX-License-Identifier: EPL-2.0
 *******************************************************************************/
package org.eclipse.viatra.transformation.runtime.emf.modelmanipulation;

public class ModelManipulationException extends Exception {

    private static final long serialVersionUID = -1855203209863514291L;

    public ModelManipulationException() {
        super();
    }

    public ModelManipulationException(String arg0, Throwable arg1) {
        super(arg0, arg1);
    }

    public ModelManipulationException(String arg0) {
        super(arg0);
    }

    public ModelManipulationException(Throwable arg0) {
        super(arg0);
    }

    
}
