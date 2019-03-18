/*******************************************************************************
 * Copyright (c) 2010-2013, Zoltan Ujhelyi, Istvan Rath and Daniel Varro
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-v20.html.
 * 
 * SPDX-License-Identifier: EPL-2.0
 *******************************************************************************/
package org.eclipse.viatra.addon.databinding.runtime.adapter;

/**
 * @author Zoltan Ujhelyi
 *
 */
public class ObservableDefinition {

    public enum ObservableType {
        OBSERVABLE_FEATURE, OBSERVABLE_LABEL
    }

    private String name;
    private String expression;
    private ObservableType type;

    public ObservableDefinition(String name, String expression, ObservableType type) {
        super();
        this.name = name;
        this.expression = expression;
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public String getExpression() {
        return expression;
    }

    public ObservableType getType() {
        return type;
    }

}
