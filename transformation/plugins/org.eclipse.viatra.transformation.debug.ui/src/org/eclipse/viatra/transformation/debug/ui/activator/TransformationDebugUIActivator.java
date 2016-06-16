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
package org.eclipse.viatra.transformation.debug.ui.activator;

import org.eclipse.core.runtime.ILog;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;

public class TransformationDebugUIActivator extends AbstractUIPlugin {
    public static final String PLUGIN_ID = "org.eclipse.viatra.transformation.debug.ui"; //$NON-NLS-1$
    
    public static final String ICON_VIATRA_ACT_BRKP = "viatra_debug_act_brkp";
    public static final String ICON_VIATRA_ACT_BRKPD = "viatra_debug_act_brkp_dis";
    public static final String ICON_VIATRA_ACT_STOPPED = "viatra_debug_act_stopped";
    public static final String ICON_VIATRA_ACTIVATION = "viatra_debug_act";
    public static final String ICON_VIATRA_ATOM = "viatra_debug_atom";
    public static final String ICON_VIATRA_BRKP = "viatra_debug_brkp";
    public static final String ICON_VIATRA_BRKPD = "viatra_debug_brkpd";
    public static final String ICON_VIATRA_LOGO = "viatra_debug_logo";
    public static final String ICON_VIATRA_DEBUG_LOGO = "viatra_debug_logo_debugging";

    private static TransformationDebugUIActivator plugin;

    public void start(BundleContext context) throws Exception {
        super.start(context);
        plugin = this;
    }

    public void stop(BundleContext context) throws Exception {
        plugin = null;
        super.stop(context);
    }

    /**
     * Returns the shared instance
     *
     * @return the shared instance
     */
    public static TransformationDebugUIActivator getDefault() {
        return plugin;
    }
    
    @Override
    protected void initializeImageRegistry(ImageRegistry reg) {
        super.initializeImageRegistry(reg);
        reg.put(ICON_VIATRA_ACT_BRKP, imageDescriptorFromPlugin(PLUGIN_ID, "icons/activation_brkp.gif"));
        reg.put(ICON_VIATRA_ACT_BRKPD, imageDescriptorFromPlugin(PLUGIN_ID, "icons/activation_brkpd.gif"));
        reg.put(ICON_VIATRA_ACT_STOPPED, imageDescriptorFromPlugin(PLUGIN_ID, "icons/activation_stopped.gif"));
        reg.put(ICON_VIATRA_ACTIVATION, imageDescriptorFromPlugin(PLUGIN_ID, "icons/activation.gif"));
        reg.put(ICON_VIATRA_ATOM, imageDescriptorFromPlugin(PLUGIN_ID, "icons/atom.gif"));
        reg.put(ICON_VIATRA_BRKP, imageDescriptorFromPlugin(PLUGIN_ID, "icons/brkp_obj.gif"));
        reg.put(ICON_VIATRA_BRKPD, imageDescriptorFromPlugin(PLUGIN_ID, "icons/brkpd_obj.gif"));
        reg.put(ICON_VIATRA_LOGO, imageDescriptorFromPlugin(PLUGIN_ID, "icons/rsz_viatra_logo.png"));
        reg.put(ICON_VIATRA_DEBUG_LOGO, imageDescriptorFromPlugin(PLUGIN_ID, "icons/viatra_debug.gif"));
    }

    public void logException(String message, Throwable exception) {
        ILog logger = getLog();
        logger.log(new Status(IStatus.ERROR, PLUGIN_ID, message, exception));
    }

}
