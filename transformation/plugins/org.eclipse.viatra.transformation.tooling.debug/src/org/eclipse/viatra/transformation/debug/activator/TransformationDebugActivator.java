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
package org.eclipse.viatra.transformation.debug.activator;

import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

/**
 * Activator class for the VIATRA transformation debugger plug-in.
 * 
 * @author Peter Lunk
 */
public class TransformationDebugActivator extends AbstractUIPlugin{
    public static final String PLUGIN_ID = "org.eclipse.viatra.transformation.debug"; //$NON-NLS-1$

    private static TransformationDebugActivator plugin;

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

    public static TransformationDebugActivator getDefault() {
        return plugin;
    }
}
