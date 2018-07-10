/*******************************************************************************
 * Copyright (c) 2010-2018, Adam Lengyel, Zoltan Ujhelyi, IncQuery Labs Ltd.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Adam Lengyel, Zoltan Ujhelyi - initial API and implementation
 *******************************************************************************/
package org.eclipse.viatra.query.patternlanguage.emf.sirius;

import java.util.HashSet;
import java.util.Set;

import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.sirius.business.api.componentization.ViewpointRegistry;
import org.eclipse.sirius.viewpoint.description.Viewpoint;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.eclipse.viatra.query.runtime.util.ViatraQueryLoggingUtil;
import org.osgi.framework.BundleContext;

/**
 * The activator class controls the plug-in life cycle
 */
public class SiriusVQLGraphicalEditorPlugin extends AbstractUIPlugin {
    
    // The plug-in ID
    public static final String PLUGIN_ID = "org.eclipse.viatra.query.patternlanguage.emf.sirius";
    
    private static SiriusVQLGraphicalEditorPlugin INSTANCE;
    
    public static final String ICON_ECLASS = "eclass";
    public static final String ICON_EDATATYPE = "edatatype";
    public static final String ICON_EENUM = "eenum";

    private static Set<Viewpoint> viewpoints; 

    public void start(BundleContext context) throws Exception {
      super.start(context);
      
      INSTANCE = this;
	  viewpoints = new HashSet<Viewpoint>();
	  viewpoints.addAll(ViewpointRegistry.getInstance().registerFromPlugin(PLUGIN_ID + "/description/vqleditor.odesign"));	  
    }

    public void stop(BundleContext context) throws Exception {
        if (viewpoints != null) {
            for (final Viewpoint viewpoint : viewpoints) {
                ViewpointRegistry.getInstance().disposeFromPlugin(viewpoint);
            }
            viewpoints.clear();
            viewpoints = null;
        }
        
        INSTANCE = null;
        super.stop(context);
    }
    
	public static void logError(Throwable t) {
		ViatraQueryLoggingUtil.getLogger(SiriusVQLGraphicalEditorPlugin.class).error(t.getMessage(), t);
	}
	
    protected void initializeImageRegistry(ImageRegistry reg) {
        super.initializeImageRegistry(reg);
        reg.put(ICON_ECLASS, imageDescriptorFromPlugin(PLUGIN_ID, "icons/eclass.gif"));
        reg.put(ICON_EDATATYPE, imageDescriptorFromPlugin(PLUGIN_ID, "icons/edatatype.gif"));
        reg.put(ICON_EENUM, imageDescriptorFromPlugin(PLUGIN_ID, "icons/eenum.gif"));
    }
	
    public static SiriusVQLGraphicalEditorPlugin getDefault() {
        return INSTANCE;
    }
}
