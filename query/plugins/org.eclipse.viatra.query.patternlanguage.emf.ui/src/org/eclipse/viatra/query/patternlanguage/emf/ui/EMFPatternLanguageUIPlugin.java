/*******************************************************************************
 * Copyright (c) 2010-2014, Zoltan Ujhelyi, Istvan Rath and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Zoltan Ujhelyi - initial API and implementation
 *******************************************************************************/
package org.eclipse.viatra.query.patternlanguage.emf.ui;

import org.eclipse.core.runtime.ILog;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jdt.ui.JavaUI;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.ui.PlatformUI;
import org.eclipse.viatra.query.patternlanguage.emf.EMFPatternLanguagePlugin;
import org.eclipse.viatra.query.patternlanguage.emf.ui.internal.EmfActivator;
import org.osgi.framework.BundleContext;

/**
 * @author Zoltan Ujhelyi
 * @since 2.0
 *
 */
public class EMFPatternLanguageUIPlugin extends EmfActivator {

    /**
     * @since 2.1
     */
    public static final String ID = "org.eclipse.viatra.query.patternlanguage.emf.ui";
    /**
     * @since 2.1
     */
    public static final String ICON_ROOT = "navigator_root";
    /**
     * @since 2.1
     */
    public static final String ICON_MATCHER = "matcher";
    /**
     * @since 2.1
     */
    public static final String ICON_MATCH = "match";
    /**
     * @since 2.1
     */
    public static final String ICON_ERROR = "error";
    /**
     * @since 2.1
     */
    public static final String ICON_ARROW_RIGHT = "arrow_right";
    /**
     * @since 2.1
     */
    public static final String ICON_ARROW_LEFT = "arrow_left";
    /**
     * @since 2.1
     */
    public static final String ICON_PIN = "pin";
    /**
     * @since 2.1
     */
    public static final String ICON_ARROW_TOP = "arrow_top";
    /**
     * @since 2.1
     */
    public static final String ICON_ARROW_BOTTOM = "arrow_bottom";
    /**
     * @since 2.1
     */
    public static final String ICON_EPACKAGE = "epackage";
    /**
     * @since 2.1
     */
    public static final String ICON_VQL = "vql";
    /**
     * @since 2.1
     */
    public static final String ICON_PROJECT = "project";
    /**
     * @since 2.1
     */
    public static final String ICON_VIATRA = "viatra";
    /**
     * @since 2.1
     */
    public static final String ICON_BASE_OPTIONS = "base_options";
    /**
     * @since 2.1
     */
    public static final String ICON_ENGINE_OPTIONS = "engine_options";
    
    public static EMFPatternLanguageUIPlugin getInstance() {
        return (EMFPatternLanguageUIPlugin) EmfActivator.getInstance();
    }
    
    @Override
    public void start(BundleContext context) throws Exception {
        super.start(context);
        EMFPatternLanguagePlugin.getInstance().addCompoundInjector(getInjector(ORG_ECLIPSE_VIATRA_QUERY_PATTERNLANGUAGE_EMF_EMFPATTERNLANGUAGE), EMFPatternLanguagePlugin.EDITOR_INJECTOR_PRIORITY);
    }

    @Override
    protected void initializeImageRegistry(ImageRegistry reg) {
        super.initializeImageRegistry(reg);
        reg.put(ICON_ROOT, imageDescriptorFromPlugin(JavaUI.ID_PLUGIN, "icons/full/eview16/types.png"));
        reg.put(ICON_MATCHER, imageDescriptorFromPlugin("org.eclipse.debug.ui", "icons/full/eview16/breakpoint_view.png"));
        reg.put(ICON_MATCH, imageDescriptorFromPlugin(JavaUI.ID_PLUGIN, "icons/full/obj16/methpub_obj.png"));
        reg.put(ICON_ERROR, imageDescriptorFromPlugin(PlatformUI.PLUGIN_ID, "icons/full/progress/errorstate.png"));
        reg.put(ICON_PIN, imageDescriptorFromPlugin(ID, "icons/pin.gif"));
        reg.put(ICON_ARROW_RIGHT, imageDescriptorFromPlugin(ID, "icons/arrow_right.gif"));
        reg.put(ICON_ARROW_LEFT, imageDescriptorFromPlugin(ID, "icons/arrow_left.gif"));
        reg.put(ICON_ARROW_TOP, imageDescriptorFromPlugin(ID, "icons/arrow_top.gif"));
        reg.put(ICON_ARROW_BOTTOM, imageDescriptorFromPlugin(ID, "icons/arrow_bottom.gif"));
        reg.put(ICON_EPACKAGE, imageDescriptorFromPlugin(ID, "icons/epackage.gif"));
        reg.put(ICON_VQL, imageDescriptorFromPlugin(ID, "icons/logo2.png"));
        reg.put(ICON_VIATRA, imageDescriptorFromPlugin(ID, "icons/rsz_viatra_logo.png"));
        reg.put(ICON_PROJECT, imageDescriptorFromPlugin("org.eclipse.ui.ide", "icons/full/obj16/prj_obj.png"));
        reg.put(ICON_BASE_OPTIONS, imageDescriptorFromPlugin(ID, "icons/base_options.png"));
        reg.put(ICON_ENGINE_OPTIONS, imageDescriptorFromPlugin(ID, "icons/engine_options.png"));
    }

    /**
     * @since 2.1
     */
    public void logException(String message, Throwable exception) {
        ILog logger = getLog();
        logger.log(new Status(IStatus.ERROR, ID, message, exception));
    }
}
