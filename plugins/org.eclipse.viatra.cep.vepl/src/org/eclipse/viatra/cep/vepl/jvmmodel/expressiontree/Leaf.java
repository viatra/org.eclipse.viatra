/*******************************************************************************
 * Copyright (c) 2004-2014, Istvan David, Istvan Rath and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Istvan David - initial API and implementation
 *******************************************************************************/

package org.eclipse.viatra.cep.vepl.jvmmodel.expressiontree;

import org.eclipse.viatra.cep.vepl.vepl.ComplexEventExpression;

/**
 * Leaf of the {@link ExpressionTree}. Always a child of a {@link Node}. Might have multiple siblings.
 * 
 * @author Istvan David
 * 
 */
public class Leaf extends TreeElement {
    private ComplexEventExpression expression;

    public Leaf(ComplexEventExpression expression) {
        this.expression = expression;
    }

    public ComplexEventExpression getExpression() {
        return expression;
    }

    public void setExpression(ComplexEventExpression expression) {
        this.expression = expression;
    }
}
