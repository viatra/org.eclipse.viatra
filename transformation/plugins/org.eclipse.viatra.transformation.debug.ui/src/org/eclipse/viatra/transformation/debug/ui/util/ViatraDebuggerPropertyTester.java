package org.eclipse.viatra.transformation.debug.ui.util;

import org.eclipse.core.expressions.PropertyTester;
import org.eclipse.viatra.transformation.debug.model.transformationstate.RuleActivation;
import org.eclipse.viatra.transformation.debug.model.transformationstate.TransformationState;
import org.eclipse.viatra.transformation.debug.ui.views.transformationbrowser.AdaptableTransformationBrowser;


public class ViatraDebuggerPropertyTester extends PropertyTester {

    private static final String ACTIVATION_SELECTED = "activation";
    private static final String ADAPTABLE_EVM_DEBUGGING = "running";

    @Override
    public boolean test(Object receiver, String property, Object[] args, Object expectedValue) {
        if (receiver instanceof AdaptableTransformationBrowser) {
            AdaptableTransformationBrowser debugView = (AdaptableTransformationBrowser) receiver;

            switch (property) {
            case ACTIVATION_SELECTED:
                return debugView.getSelection() instanceof RuleActivation;
            case ADAPTABLE_EVM_DEBUGGING:
                return debugView.getSelection() instanceof TransformationState;
            default:
                return false;
            }

        }

        return false;
    }

}
