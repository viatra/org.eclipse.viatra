/*******************************************************************************
 * Copyright (c) 2010-2013, Zoltan Ujhelyi, Istvan Rath and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Zoltan Ujhelyi - initial API and implementation
 *******************************************************************************/
package org.eclipse.viatra.query.patternlanguage;

import org.eclipse.viatra.query.patternlanguage.validation.whitelist.PureWhitelistExtensionLoader;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

public class PatternLanguagePlugin implements BundleActivator {

    @Override
    public void start(BundleContext context) throws Exception {
        PureWhitelistExtensionLoader.load();
    }

    @Override
    public void stop(BundleContext context) throws Exception {
    }

}
