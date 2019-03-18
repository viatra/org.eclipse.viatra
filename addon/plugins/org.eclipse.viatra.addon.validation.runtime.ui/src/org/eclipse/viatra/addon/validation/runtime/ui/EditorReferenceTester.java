/*******************************************************************************
 * Copyright (c) 2010-2012, Abel Hegedus, Istvan Rath and Daniel Varro
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-v20.html.
 * 
 * SPDX-License-Identifier: EPL-2.0
 *******************************************************************************/
package org.eclipse.viatra.addon.validation.runtime.ui;

import org.eclipse.core.expressions.PropertyTester;
import org.eclipse.viatra.addon.validation.runtime.ValidationUtil;

/**
 * @author Abel Hegedus
 * 
 */
public class EditorReferenceTester extends PropertyTester {

    @Override
    public boolean test(Object receiver, String property, Object[] args, Object expectedValue) {
        if (receiver instanceof String) {
            String editorId = (String) receiver;
            if (property.equals("hasConstraint")) {
                return ValidationUtil.isConstraintsRegisteredForEditorId(editorId);
            }
        }
        return false;
    }

}