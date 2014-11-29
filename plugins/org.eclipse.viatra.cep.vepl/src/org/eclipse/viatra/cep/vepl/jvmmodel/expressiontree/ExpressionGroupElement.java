package org.eclipse.viatra.cep.vepl.jvmmodel.expressiontree;

import org.eclipse.viatra.cep.core.metamodels.events.TimeWindow;
import org.eclipse.viatra.cep.vepl.vepl.ComplexEventExpression;

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
