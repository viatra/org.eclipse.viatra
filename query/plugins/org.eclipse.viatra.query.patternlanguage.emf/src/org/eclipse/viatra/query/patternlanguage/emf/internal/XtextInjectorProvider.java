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
package org.eclipse.viatra.query.patternlanguage.emf.internal;

import org.apache.log4j.Logger;
import org.eclipse.viatra.query.patternlanguage.emf.EMFPatternLanguageStandaloneSetup;
import org.eclipse.viatra.query.runtime.util.ViatraQueryLoggingUtil;

import com.google.inject.Injector;

/**
 * A singleton provider for Xtext injectors
 *
 * @author Zoltan Ujhelyi
 *
 */
public final class XtextInjectorProvider {

    public static final XtextInjectorProvider INSTANCE = new XtextInjectorProvider();
    private Injector injector;

    private XtextInjectorProvider() {
    }

    public Injector getInjector() {
        return injector;
    }

    public void setInjector(Injector injector) {
        this.injector = injector;
        ViatraQueryLoggingUtil.setExternalLogger(injector.getInstance(Logger.class));
    }

    public void initializeHeadlessInjector() {
        EMFPatternLanguageStandaloneSetup setup = new EMFPatternLanguageStandaloneSetup();
        injector = setup.createInjectorAndDoEMFRegistration();
    }
}
