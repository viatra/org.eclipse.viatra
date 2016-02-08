/*******************************************************************************
 * Copyright (c) 2010-2012, Zoltan Ujhelyi, Istvan Rath and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Zoltan Ujhelyi - initial API and implementation
 *******************************************************************************/
package org.eclipse.incquery.patternlanguage.emf;

import org.apache.log4j.Logger;
import org.eclipse.incquery.patternlanguage.emf.internal.XtextInjectorProvider;
import org.eclipse.incquery.runtime.util.IncQueryLoggingUtil;

import com.google.inject.Injector;

/**
 * Initialization support for running Xtext languages without equinox extension registry
 */
public class EMFPatternLanguageStandaloneSetup extends EMFPatternLanguageStandaloneSetupGenerated {

    public static void doSetup() {
        new EMFPatternLanguageStandaloneSetup().createInjectorAndDoEMFRegistration();
    }

    @Override
    public void register(Injector injector) {
        super.register(injector);
        IncQueryLoggingUtil.setExternalLogger(injector.getInstance(Logger.class));
        XtextInjectorProvider.INSTANCE.setInjector(injector);
    }
    
    
}
