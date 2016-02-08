/*******************************************************************************
 * Copyright (c) 2010-2013, Zoltan Ujhelyi, Istvan Rath and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Zoltan Ujhelyi - initial API and implementation
 *******************************************************************************/
package org.eclipse.incquery.databinding.runtime.adapter;

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
