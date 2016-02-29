/*******************************************************************************
 * Copyright (c) 2010-2016, Gyorgy Gerencser, Gabor Bergmann, Istvan Rath and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Gyorgy Gerencser - initial API and implementation
 *******************************************************************************/
package org.eclipse.viatra.addon.querybyexample.interfaces.beans;

import org.eclipse.emf.ecore.EClass;

public class VQLVariableSetting {

    private boolean inputVariable;
    private boolean visible = true;

    private String variableName;

    private EClass type;

    public boolean isInputVariable() {
        return inputVariable;
    }

    public void setInputVariable(boolean inputVariable) {
        this.inputVariable = inputVariable;
    }

    public boolean isVisible() {
        return visible;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    public String getVariableName() {
        return variableName;
    }

    public void setVariableName(String variableName) {
        this.variableName = variableName;
    }

    public EClass getType() {
        return type;
    }

    public void setType(EClass type) {
        this.type = type;
    }
}
