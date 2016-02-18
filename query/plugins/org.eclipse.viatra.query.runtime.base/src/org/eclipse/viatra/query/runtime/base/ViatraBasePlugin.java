/*******************************************************************************
 * Copyright (c) 2010-2012, Istvan Rath and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Tamas Szabo, Gabor Bergmann - initial API and implementation
 *******************************************************************************/

package org.eclipse.viatra.query.runtime.base;

import org.eclipse.core.runtime.Plugin;
import org.eclipse.viatra.query.runtime.base.comprehension.WellbehavingDerivedFeatureRegistry;
import org.osgi.framework.BundleContext;

public class ViatraBasePlugin extends Plugin {

    // The shared instance
    private static ViatraBasePlugin plugin;

    public static final String PLUGIN_ID = "org.eclipse.viatra.query.runtime.base";
    public static final String WELLBEHAVING_DERIVED_FEATURE_EXTENSION_POINT_ID = "org.eclipse.viatra.query.runtime.base.wellbehaving.derived.features";

    @Override
    public void start(BundleContext context) throws Exception {
        super.start(context);
        plugin = this;
        WellbehavingDerivedFeatureRegistry.initRegistry();
    }

    @Override
    public void stop(BundleContext context) throws Exception {
        plugin = null;
        super.stop(context);
    }

    /**
     * Returns the shared instance
     * 
     * @return the shared instance
     */
    public static ViatraBasePlugin getDefault() {
        return plugin;
    }

}
