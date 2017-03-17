/*******************************************************************************
 * Copyright (c) 2010-2016, Abel Hegedus and IncQueryLabs Ltd.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Abel Hegedus - initial API and implementation
 *******************************************************************************/
package org.eclipse.viatra.query.tooling.ui.browser;

import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

public class ViatraQueryToolingBrowserPlugin extends AbstractUIPlugin {

    /**
     * The plug-in ID
     */
    public static final String PLUGIN_ID = "org.eclipse.viatra.query.tooling.ui.browser";

    public static final String ICON_BASE_OPTIONS = "base_options";
    public static final String ICON_ENGINE_OPTIONS = "engine_options";
    
    
    // The shared instance
    private static ViatraQueryToolingBrowserPlugin plugin;

    @Override
    public void start(BundleContext context) throws Exception {
        super.start(context);
        plugin = this;
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
    public static ViatraQueryToolingBrowserPlugin getDefault() {
        return plugin;
    }

    @Override
    protected void initializeImageRegistry(ImageRegistry reg) {
        super.initializeImageRegistry(reg);
        reg.put(ICON_BASE_OPTIONS, imageDescriptorFromPlugin(PLUGIN_ID, "icons/base_options.png"));
        reg.put(ICON_ENGINE_OPTIONS, imageDescriptorFromPlugin(PLUGIN_ID, "icons/engine_options.png"));
    }
}
