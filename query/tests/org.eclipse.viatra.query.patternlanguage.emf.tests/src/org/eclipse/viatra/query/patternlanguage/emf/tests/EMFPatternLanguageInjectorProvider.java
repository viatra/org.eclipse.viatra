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
import org.eclipse.viatra.query.patternlanguage.annotations.ExtensionBasedAnnotationValidatorLoader;
import org.eclipse.viatra.query.patternlanguage.annotations.IAnnotationValidatorLoader;
import org.eclipse.viatra.query.patternlanguage.emf.EMFPatternLanguagePlugin;
import org.eclipse.viatra.query.patternlanguage.emf.EMFPatternLanguageRuntimeModule;
import org.eclipse.viatra.query.patternlanguage.emf.EMFPatternLanguageStandaloneSetup;
import org.eclipse.viatra.query.patternlanguage.emf.GenmodelExtensionLoader;
import org.eclipse.viatra.query.patternlanguage.emf.IGenmodelMappingLoader;
import org.eclipse.viatra.query.runtime.util.ViatraQueryLoggingUtil;
import org.eclipse.xtext.junit4.GlobalRegistries;
import org.eclipse.xtext.junit4.GlobalRegistries.GlobalStateMemento;
import org.eclipse.xtext.junit4.IInjectorProvider;
import org.eclipse.xtext.junit4.IRegistryConfigurator;

import com.google.inject.Guice;
import com.google.inject.Injector;

public class EMFPatternLanguageInjectorProvider implements IInjectorProvider, IRegistryConfigurator {

    protected GlobalStateMemento stateBeforeInjectorCreation;
    protected GlobalStateMemento stateAfterInjectorCreation;
    protected Injector injector;

    static {
        GlobalRegistries.initializeDefaults();
    }

    public Injector getInjector() {
        if (injector == null) {
            stateBeforeInjectorCreation = GlobalRegistries.makeCopyOfGlobalState();
            this.injector = internalCreateInjector();
            stateAfterInjectorCreation = GlobalRegistries.makeCopyOfGlobalState();
        }
        return injector;
    }

    protected Injector internalCreateInjector() {
        Injector newInjector = new EMFPatternLanguageStandaloneSetup().createInjectorAndDoEMFRegistration();
        //XXX the following line enforce the tests to be run in an Eclipse environment
        newInjector = Guice.createInjector(new EMFPatternLanguageRuntimeModule() {
            @SuppressWarnings("unused")
            public Class<? extends IAnnotationValidatorLoader> bindAnnotationValidatorLoader() {
                return ExtensionBasedAnnotationValidatorLoader.class;
            }
            @SuppressWarnings("unused")
            public Class<? extends IGenmodelMappingLoader> bindGenmodelMappingLoader() {
                return GenmodelExtensionLoader.class;
            }
        });
        ViatraQueryLoggingUtil.setExternalLogger(newInjector.getInstance(Logger.class));
        EMFPatternLanguagePlugin.getInstance().addCompoundInjector(newInjector,
                EMFPatternLanguagePlugin.TEST_INJECTOR_PRIORITY);
        return newInjector;
    }

    public void restoreRegistry() {
        stateBeforeInjectorCreation.restoreGlobalState();
    }

    public void setupRegistry() {
        getInjector();
        stateAfterInjectorCreation.restoreGlobalState();
    }
}
