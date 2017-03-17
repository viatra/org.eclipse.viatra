/*******************************************************************************
 * Copyright (c) 2010-2016, Zoltan Ujhelyi and IncQueryLabs Ltd.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Zoltan Ujhelyi - initial API and implementation
 *******************************************************************************/
package org.eclipse.viatra.query.patternlanguage.emf.tests;

import org.apache.log4j.Logger;
import org.eclipse.viatra.query.patternlanguage.emf.EMFPatternLanguagePlugin;
import org.eclipse.viatra.query.patternlanguage.emf.ui.internal.EMFPatternLanguageActivator;
import org.eclipse.viatra.query.runtime.util.ViatraQueryLoggingUtil;
import org.eclipse.xtext.junit4.IInjectorProvider;

import com.google.inject.Injector;

public class EMFPatternLanguageUiInjectorProvider implements IInjectorProvider {

	public Injector getInjector() {
		Injector injector = EMFPatternLanguageActivator.getInstance().getInjector("org.eclipse.viatra.query.patternlanguage.emf.EMFPatternLanguage");
	    ViatraQueryLoggingUtil.setExternalLogger(injector.getInstance(Logger.class));
	    EMFPatternLanguagePlugin.getInstance().addCompoundInjector(injector, EMFPatternLanguagePlugin.TEST_INJECTOR_PRIORITY);
        return injector;
	}

}
