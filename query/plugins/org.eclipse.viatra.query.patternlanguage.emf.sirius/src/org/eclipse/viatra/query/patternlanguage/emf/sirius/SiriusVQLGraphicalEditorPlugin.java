/*******************************************************************************
 * Copyright (c) 2010-2018, Adam Lengyel, Zoltan Ujhelyi, IncQuery Labs Ltd.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-v20.html.
 * 
 * SPDX-License-Identifier: EPL-2.0
 *******************************************************************************/
package org.eclipse.viatra.query.patternlanguage.emf.sirius;

import java.util.HashSet;
import java.util.Set;

import org.eclipse.emf.ecore.EValidator;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.sirius.business.api.componentization.ViewpointRegistry;
import org.eclipse.sirius.viewpoint.description.Viewpoint;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.eclipse.viatra.query.patternlanguage.emf.sirius.validation.EValidatorAdapter;
import org.eclipse.viatra.query.patternlanguage.metamodel.vgql.VgqlPackage;
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

    private Set<Viewpoint> viewpoints; 

    @Override
    public void start(BundleContext context) throws Exception {
      super.start(context);
      
      INSTANCE = this;
	  viewpoints = new HashSet<Viewpoint>();
	  viewpoints.addAll(ViewpointRegistry.getInstance().registerFromPlugin(PLUGIN_ID + "/description/vqleditor.odesign"));
	  
	  EValidator.Registry.INSTANCE.put(
	            VgqlPackage.eINSTANCE,
	            new EValidatorAdapter());
    }

    @Override
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
	
	@Override
    protected void initializeImageRegistry(ImageRegistry reg) {
        super.initializeImageRegistry(reg);
        reg.put(ICON_ECLASS, imageDescriptorFromPlugin(PLUGIN_ID, "icons/eclass.gif"));
        reg.put(ICON_EDATATYPE, imageDescriptorFromPlugin(PLUGIN_ID, "icons/edatatype.gif"));
        reg.put(ICON_EENUM, imageDescriptorFromPlugin(PLUGIN_ID, "icons/eenum.gif"));
    }
	
    public static SiriusVQLGraphicalEditorPlugin getDefault() {
        return INSTANCE;
    }
    
    public Set<Viewpoint> getViewpoints() {
        return viewpoints;
    }
}
