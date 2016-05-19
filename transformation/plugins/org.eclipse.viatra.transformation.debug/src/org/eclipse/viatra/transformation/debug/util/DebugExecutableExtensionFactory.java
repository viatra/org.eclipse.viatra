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
package org.eclipse.viatra.transformation.debug.util;

import org.eclipse.core.runtime.Platform;
import org.eclipse.viatra.query.patternlanguage.emf.ui.EMFPatternLanguageExecutableExtensionFactory;
import org.osgi.framework.Bundle;

public class DebugExecutableExtensionFactory extends EMFPatternLanguageExecutableExtensionFactory{
    @Override
    protected Bundle getBundle() {
        return Platform.getBundle("org.eclipse.viatra.transformation.debug");
    }
}
