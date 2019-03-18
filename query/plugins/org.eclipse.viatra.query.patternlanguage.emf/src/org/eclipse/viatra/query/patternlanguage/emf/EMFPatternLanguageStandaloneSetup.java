/*******************************************************************************
 * Copyright (c) 2010-2012, Zoltan Ujhelyi, Istvan Rath and Daniel Varro
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-v20.html.
 * 
 * SPDX-License-Identifier: EPL-2.0
 *******************************************************************************/
package org.eclipse.viatra.query.patternlanguage.emf;

import org.apache.log4j.Logger;
import org.eclipse.viatra.query.patternlanguage.emf.internal.XtextInjectorProvider;
import org.eclipse.viatra.query.patternlanguage.emf.util.PatternParser;
import org.eclipse.viatra.query.patternlanguage.emf.util.internal.PatternParserResourceDescriptions;
import org.eclipse.viatra.query.patternlanguage.emf.vql.PatternLanguagePackage;
import org.eclipse.viatra.query.runtime.util.ViatraQueryLoggingUtil;
import org.eclipse.xtext.ISetup;
import org.eclipse.xtext.generator.IGenerator;
import org.eclipse.xtext.resource.IResourceDescriptions;
import org.eclipse.xtext.service.SingletonBinding;
import org.eclipse.xtext.xbase.XbaseStandaloneSetup;
import org.eclipse.xtext.xbase.validation.UniqueClassNameValidator;

import com.google.inject.Binder;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.name.Names;

/**
 * Initialization support for running Xtext languages without equinox extension registry
 */
@SuppressWarnings("restriction")
public class EMFPatternLanguageStandaloneSetup extends EMFPatternLanguageStandaloneSetupGenerated implements ISetup {

    /**
     * Module implementation that is optimized for headless parsing, e.g. code generation is turned off
     * @since 2.0
     *
     */
    public static class StandaloneParserModule extends EMFPatternLanguageRuntimeModule {
        
        @Override
        public Class<? extends IGenerator> bindIGenerator() {
            return IGenerator.NullGenerator.class;
        }
        
        /**
         * @since 2.0
         */
        @Override
        public void configureClasspathValidation(Binder binder) {
            binder.bind(Boolean.class)
            .annotatedWith(Names.named(EMFPatternLanguageConfigurationConstants.VALIDATE_CLASSPATH_KEY))
            .toInstance(false);
        }
    }
    
    /**
     * Module implementation that is used for headless parsing by {@link PatternParser}, where separate parse calls
     * should be considered independent without recreating the the pattern parser instance.
     * </p>
     * 
     * <strong>Note</strong>: Instead of this rely on {@link StandaloneParserModule} instead, unless recreating the
     * pattern parser is really expensive.
     * 
     * @since 2.0
     *
     */
    public static class StandaloneParserWithSeparateModules extends StandaloneParserModule {
        @Override
        public void configurePatternReuse(Binder binder) {
            binder.bind(Boolean.class)
                    .annotatedWith(
                            Names.named(EMFPatternLanguageConfigurationConstants.SEPARATE_PATTERN_PARSER_RUNS_KEY))
                    .toInstance(false);
        }
        
        @Override
        public void configureIResourceDescriptions(Binder binder) {
            binder.bind(IResourceDescriptions.class).to(PatternParserResourceDescriptions.class);
        }
        
        @SingletonBinding(eager = true)
        @Override
        public Class<? extends UniqueClassNameValidator> bindUniqueClassNameValidator() {
            // Do not bind unique class validator from Xbase
            return null;
        }
    }
    
    public static void doSetup() {
        new EMFPatternLanguageStandaloneSetup().createInjectorAndDoEMFRegistration();
    }

    /**
     * Initializes an injector recommended for standalone parsing. 
     * </p>
     * 
     * <strong>Warning</strong>: In each Java application, at most one of the following methods should be called to
     * avoid corrupting the EMF registries:
     * 
     * <ul>
     * <li>{@link #createInjector()},</li>
     * <li>{@link #createInjectorAndDoEMFRegistration()},</li>
     * <li>{@link #createStandaloneInjector()},</li>
     * <li>{@link #createStandaloneInjectorWithSeparateModules()}</li>
     * <ul>
     * 
     * @since 2.0
     */
    public Injector createStandaloneInjector() {
        XbaseStandaloneSetup.doSetup();

        Injector injector = Guice.createInjector(new StandaloneParserModule());
        register(injector);
        return injector;
    }
    
    /**
     * Initializes an injector recommended for standalone parsing using the {@link StandaloneParserWithSeparateModules} module.
     * </p>
     * 
     * <strong>Note</strong>: Instead of this rely on {@link #createStandaloneInjector()} instead, unless recreating the
     * pattern parser is really expensive.
     * </p>
     * 
     * <strong>Warning</strong>: In each Java application, at most one of the following methods should be called to
     * avoid corrupting the EMF registries:
     * 
     * <ul>
     * <li>{@link #createInjector()},</li>
     * <li>{@link #createInjectorAndDoEMFRegistration()},</li>
     * <li>{@link #createStandaloneInjector()},</li>
     * <li>{@link #createStandaloneInjectorWithSeparateModules()}</li>
     * <ul>
     * 
     * @since 2.0
     */
    public Injector createStandaloneInjectorWithSeparateModules() {
        XbaseStandaloneSetup.doSetup();

        Injector injector = Guice.createInjector(new StandaloneParserWithSeparateModules());
        register(injector);
        return injector;

    }
    
    @Override
    public void register(Injector injector) {
        super.register(injector);
        // Instance access initializes EPackage
        PatternLanguagePackage.eINSTANCE.getNsURI();
        ViatraQueryLoggingUtil.setExternalLogger(injector.getInstance(Logger.class));
        XtextInjectorProvider.INSTANCE.setInjector(injector);
    }
    
    
}
