/*******************************************************************************
 * Copyright (c) 2010-2014, Marton Bur, Akos Horvath, Zoltan Ujhelyi, Istvan Rath and Daniel Varro
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-v20.html.
 * 
 * SPDX-License-Identifier: EPL-2.0
 *******************************************************************************/
package org.eclipse.viatra.query.tooling.localsearch.ui;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

/**
 * 
 * @author Marton Bur, Zoltan Ujhelyi
 *
 */
public class LocalSearchToolingActivator extends AbstractUIPlugin {

    public static final String PLUGIN_ID = "org.eclipse.viatra.query.tooling.localsearch.ui";
    
    public static final String ICON_NOT_APPLIED_OPERATION = "not_applied_operation";
    public static final String ICON_APPLIED_OPERATION = "applied_operation";
    public static final String ICON_CURRENT_OPERATION = "current_operation";
    
    // The shared instance
    private static LocalSearchToolingActivator plugin;
    
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
    public static LocalSearchToolingActivator getDefault() {
        return plugin;
    }

    @Override
    protected void initializeImageRegistry(ImageRegistry reg) {
        super.initializeImageRegistry(reg);
        reg.put(ICON_NOT_APPLIED_OPERATION, imageDescriptorFromPlugin(PlatformUI.PLUGIN_ID, "icons/full/etool16/help_contents.png"));
        reg.put(ICON_APPLIED_OPERATION, imageDescriptorFromPlugin(PLUGIN_ID, "icons/complete_status.gif"));
        reg.put(ICON_CURRENT_OPERATION, imageDescriptorFromPlugin(PLUGIN_ID, "icons/nav_go.png"));
    }
}
