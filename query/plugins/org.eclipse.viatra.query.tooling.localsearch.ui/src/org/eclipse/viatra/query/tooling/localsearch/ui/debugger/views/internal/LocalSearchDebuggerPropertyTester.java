package org.eclipse.viatra.query.tooling.localsearch.ui.debugger.views.internal;

import org.eclipse.core.expressions.PropertyTester;
import org.eclipse.ui.IViewPart;
import org.eclipse.viatra.query.tooling.localsearch.ui.debugger.views.LocalSearchDebugView;

public class LocalSearchDebuggerPropertyTester extends PropertyTester {

    private final String IS_DEBUGGER_RUNNING = "operational";

    @Override
    public boolean test(Object receiver, String property, Object[] args, Object expectedValue) {
        if (IS_DEBUGGER_RUNNING.equals(property) && receiver instanceof IViewPart) {
            if (receiver instanceof LocalSearchDebugView) {
                LocalSearchDebugView debugView = (LocalSearchDebugView) receiver;
                return debugView.getDebugger() != null;
            }
        }
        return false;
    }

}
