/*******************************************************************************
 * Copyright (c) 2010-2012, Abel Hegedus, Istvan Rath and Daniel Varro
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-v20.html.
 * 
 * SPDX-License-Identifier: EPL-2.0
 *******************************************************************************/

package org.eclipse.viatra.query.testing.ui;

import org.eclipse.viatra.query.patternlanguage.emf.ui.EMFPatternLanguageExecutableExtensionFactory;
import org.osgi.framework.Bundle;

public class ViatraQueryLanguageTestExecutableExtensionFactory extends
        EMFPatternLanguageExecutableExtensionFactory {

    @Override
    protected Bundle getBundle() {
        return ViatraQueryTestingUIPlugin.getDefault().getBundle();
    }

}
