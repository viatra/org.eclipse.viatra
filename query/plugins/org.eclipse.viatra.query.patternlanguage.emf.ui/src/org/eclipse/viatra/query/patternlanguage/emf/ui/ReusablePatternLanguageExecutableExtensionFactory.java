/*******************************************************************************
 * Copyright (c) 2010-2018, Zoltan Ujhelyi, IncQuery Labs Ltd.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-v20.html.
 * 
 * SPDX-License-Identifier: EPL-2.0
 *******************************************************************************/
package org.eclipse.viatra.query.patternlanguage.emf.ui;

import org.eclipse.core.runtime.Platform;
import org.osgi.framework.Bundle;

/**
 * An executable extension factory that injects execution instances with the injector of the pattern language, but
 * retains the classpath of the extension classpath. The injector will only inject classes from the classes of the
 * pattern language plug-ins, but can be used outside the pattern language plug-ins themselves.
 * 
 * @since 2.0
 *
 */
public class ReusablePatternLanguageExecutableExtensionFactory extends EMFPatternLanguageExecutableExtensionFactory {

    @Override
    protected Bundle getBundle() {
        return Platform.getBundle(config.getContributor().getName());
    }

}
