/*******************************************************************************
 * Copyright (c) 2010-2014, Jozsef Makai, Zoltan Ujhelyi, Istvan Rath and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Jozsef Makai - initial API and implementation
 *******************************************************************************/

package org.eclipse.viatra.maven.querybuilder.setup;

import org.eclipse.viatra.query.patternlanguage.emf.EMFPatternLanguageStandaloneCompilerModule;
import org.eclipse.viatra.query.patternlanguage.emf.EMFPatternLanguageStandaloneSetup;
import org.eclipse.viatra.query.patternlanguage.emf.IGenmodelMappingLoader;
import org.eclipse.viatra.query.patternlanguage.emf.scoping.IMetamodelProviderInstance;
import org.eclipse.xtext.resource.XtextResource;
import org.eclipse.xtext.xbase.compiler.IGeneratorConfigProvider;

import com.google.inject.Binder;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.multibindings.Multibinder;

public class EMFPatternLanguageMavenStandaloneSetup extends EMFPatternLanguageStandaloneSetup {

    @Override
    public Injector createInjector() {
        return Guice.createInjector(new EMFPatternLanguageStandaloneCompilerModule() {

            @SuppressWarnings("unused")
            public void configureIGenmodelMappingLoader(Binder bind) {
                bind.bind(IGenmodelMappingLoader.class).toInstance(MavenBuilderGenmodelLoader.getInstance());
                Multibinder<IMetamodelProviderInstance> metamodelProviderBinder = Multibinder.newSetBinder(bind, IMetamodelProviderInstance.class);
                metamodelProviderBinder.addBinding().to(MavenGenmodelMetamodelProvider.class);
            }
            
            /**
             * @since 1.7
             */
            @Override
            public Class<? extends IGeneratorConfigProvider> bindIGeneratorConfigProvider() {
                return MavenGeneratorConfigProvider.class;
            }
            
        });
    }

}
