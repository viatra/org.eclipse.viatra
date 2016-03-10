/*******************************************************************************
 * Copyright (c) 2010-2012, Tamas Szabo, Istvan Rath and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Tamas Szabo (itemis AG) - initial API and implementation
 *******************************************************************************/
package org.eclipse.viatra.integration.xcore.ui;

import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.resource.ImageRegistry;
import org.osgi.framework.Bundle;

public class ViatraQueryXcoreActivator extends org.eclipse.viatra.integration.xcore.ui.internal.ViatraQueryXcoreActivator {
	
    private static final String PLUGIN_ID = "org.eclipse.viatra.integration.xcore.ui";

    @Override
    protected void initializeImageRegistry(ImageRegistry reg) {
        super.initializeImageRegistry(reg);
        @SuppressWarnings("unused")
        Bundle bundle = Platform.getBundle(PLUGIN_ID);
        reg.put("Underlay", imageDescriptorFromPlugin(PLUGIN_ID, "icons/Underlay.gif"));
        reg.put("EAttribute", imageDescriptorFromPlugin(PLUGIN_ID, "icons/EAttribute.gif"));
        reg.put("EReference", imageDescriptorFromPlugin(PLUGIN_ID, "icons/EReference.gif"));
    }
    
}
