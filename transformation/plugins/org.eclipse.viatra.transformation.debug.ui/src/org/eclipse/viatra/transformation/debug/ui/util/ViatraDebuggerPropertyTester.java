package org.eclipse.viatra.transformation.debug.ui.util;

import org.eclipse.core.expressions.PropertyTester;
import org.eclipse.viatra.transformation.debug.ui.views.transformationbrowser.AdaptableTransformationBrowser;
import org.eclipse.viatra.transformation.evm.api.Activation;
import org.eclipse.viatra.transformation.evm.api.adapter.AdaptableEVM;

public class ViatraDebuggerPropertyTester extends PropertyTester {

    private static final String ACTIVATION_SELECTED = "activation";
    private static final String ADAPTABLE_EVM_DEBUGGING = "running";

    @Override
    public boolean test(Object receiver, String property, Object[] args, Object expectedValue) {
        if (receiver instanceof AdaptableTransformationBrowser) {
            AdaptableTransformationBrowser debugView = (AdaptableTransformationBrowser) receiver;

            switch (property) {
            case ACTIVATION_SELECTED:
                return (debugView.getSelection() instanceof Activation<?>);
            case ADAPTABLE_EVM_DEBUGGING:
                if (debugView.getSelection() instanceof AdaptableEVM) {
                    return debugView.isUnderDebugging((AdaptableEVM) debugView.getSelection());
                }
                return false;
            default:
                return false;
            }

        }

        return false;
    }

}
