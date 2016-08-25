/**
 * Copyright (c) 2010-2016, Peter Lunk, IncQuery Labs Ltd.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *   Peter Lunk - initial API and implementation
 */
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
