/*******************************************************************************
 * Copyright (c) 2010-2014, Miklos Foldenyi, Andras Szabolcs Nagy, Abel Hegedus, Akos Horvath, Zoltan Ujhelyi and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Contributors:
 *   Miklos Foldenyi - initial API and implementation
 *   Andras Szabolcs Nagy - initial API and implementation
 *******************************************************************************/
package org.eclipse.viatra.dse;

import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.Platform;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

public class DSEActivator implements BundleActivator {

    // The plug-in ID
    //$NON-NLS-1$
    public static final String PLUGIN_ID = "org.eclipse.viatra.dse";

    @Override
    public void start(BundleContext context) {
        IExtensionRegistry extensionRegistry = Platform.getExtensionRegistry();

    }

    @Override
    public void stop(BundleContext context) {

    }

}
