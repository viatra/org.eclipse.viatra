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

import org.eclipse.viatra.cep.core.metamodels.events.TimeWindow;
import org.eclipse.viatra.cep.vepl.vepl.ComplexEventExpression;

/**
 * Represents a group of expressions to be packaged under a {@link Node}.
 * @author steve
 *
 */
public class ExpressionGroupElement {
    private ComplexEventExpression complexEventExpression;
    private int multiplicity;
    private TimeWindow timeWindow;

    public ExpressionGroupElement(ComplexEventExpression complexEventExpression, int multiplicity, TimeWindow timeWindow) {
        this.complexEventExpression = complexEventExpression;
        this.multiplicity = multiplicity;
        this.timeWindow = timeWindow;
    }

    public ComplexEventExpression getComplexEventExpression() {
        return complexEventExpression;
    }

    public void setComplexEventExpression(ComplexEventExpression complexEventExpression) {
        this.complexEventExpression = complexEventExpression;
    }

    public int getMultiplicity() {
        return multiplicity;
    }

    public void setMultiplicity(int multiplicity) {
        this.multiplicity = multiplicity;
    }

    public TimeWindow getTimeWindow() {
        return timeWindow;
    }

    public void setTimeWindow(TimeWindow timeWindow) {
        this.timeWindow = timeWindow;
    }
}
