/*******************************************************************************
 * Copyright (c) 2010-2012, Zoltan Ujhelyi, Istvan Rath and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Zoltan Ujhelyi - initial API and implementation
 *******************************************************************************/

package org.eclipse.incquery.tooling.core.generator;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

public class IncQueryGeneratorPlugin implements BundleActivator {

    private static BundleContext context;
    public static IncQueryGeneratorPlugin INSTANCE;
    public static final String BUNDLE_ID = "org.eclipse.incquery.tooling.core";

    public static BundleContext getContext() {
        return context;
    }

    @Override
    public void start(BundleContext bundleContext) {
        INSTANCE = this;
        IncQueryGeneratorPlugin.context = bundleContext;
    }

    @Override
    public void stop(BundleContext bundleContext) {
        IncQueryGeneratorPlugin.context = null;
        INSTANCE = null;
    }


}
