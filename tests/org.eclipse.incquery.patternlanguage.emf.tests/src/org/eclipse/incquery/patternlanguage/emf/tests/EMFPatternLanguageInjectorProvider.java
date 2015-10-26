/*
 * generated by Xtext
 */
package org.eclipse.incquery.patternlanguage.emf.tests;

import org.apache.log4j.Logger;
import org.eclipse.incquery.patternlanguage.annotations.ExtensionBasedAnnotationValidatorLoader;
import org.eclipse.incquery.patternlanguage.annotations.IAnnotationValidatorLoader;
import org.eclipse.incquery.patternlanguage.emf.EMFPatternLanguagePlugin;
import org.eclipse.incquery.patternlanguage.emf.EMFPatternLanguageRuntimeModule;
import org.eclipse.incquery.patternlanguage.emf.EMFPatternLanguageStandaloneSetup;
import org.eclipse.incquery.patternlanguage.emf.GenmodelExtensionLoader;
import org.eclipse.incquery.patternlanguage.emf.IGenmodelMappingLoader;
import org.eclipse.incquery.runtime.util.IncQueryLoggingUtil;
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
        IncQueryLoggingUtil.setExternalLogger(newInjector.getInstance(Logger.class));
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