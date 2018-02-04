/*******************************************************************************
 * Copyright (c) 2010-2013, Zoltan Ujhelyi, Istvan Rath and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Zoltan Ujhelyi - initial API and implementation
 *******************************************************************************/
package org.eclipse.viatra.query.patternlanguage.emf;

import org.eclipse.viatra.query.patternlanguage.emf.internal.XtextInjectorProvider;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

import com.google.inject.Injector;

public class EMFPatternLanguagePlugin implements BundleActivator {

    private static EMFPatternLanguagePlugin instance;
    private int currentInjectorPriority = NO_EXTERNAL_INJECTOR_PRIORITY;
    public static final int NO_EXTERNAL_INJECTOR_PRIORITY = 0;
    public static final int GENERATOR_INJECTOR_PRIORITY = 10;
    public static final int EDITOR_INJECTOR_PRIORITY = 20;
    public static final int TEST_INJECTOR_PRIORITY = 10;

    @Override
    public void start(BundleContext context) throws Exception {
        instance = this;
    }

    @Override
    public void stop(BundleContext context) throws Exception {
        instance = null;
    }

    public static EMFPatternLanguagePlugin getInstance() {
        return instance;
    }

    public boolean addCompoundInjector(Injector injector, int injectorPriority) {
        boolean highPriority = injectorPriority > currentInjectorPriority;
        if (highPriority) {
            XtextInjectorProvider.INSTANCE.setInjector(injector);
        }
        return highPriority;
    }
}
