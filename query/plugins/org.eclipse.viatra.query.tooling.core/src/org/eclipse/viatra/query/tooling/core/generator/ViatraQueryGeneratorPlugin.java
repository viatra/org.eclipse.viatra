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

package org.eclipse.viatra.query.tooling.core.generator;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

public class ViatraQueryGeneratorPlugin implements BundleActivator {

    private static BundleContext context;
    public static ViatraQueryGeneratorPlugin INSTANCE;
    public static final String BUNDLE_ID = "org.eclipse.viatra.query.tooling.core";

    public static BundleContext getContext() {
        return context;
    }

    @Override
    public void start(BundleContext bundleContext) {
        INSTANCE = this;
        ViatraQueryGeneratorPlugin.context = bundleContext;
    }

    @Override
    public void stop(BundleContext bundleContext) {
        ViatraQueryGeneratorPlugin.context = null;
        INSTANCE = null;
    }


}
