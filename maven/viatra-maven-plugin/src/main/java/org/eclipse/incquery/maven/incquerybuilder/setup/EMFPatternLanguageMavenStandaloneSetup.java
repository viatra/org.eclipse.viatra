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

package org.eclipse.incquery.maven.incquerybuilder.setup;

import org.eclipse.incquery.patternlanguage.emf.EMFPatternLanguageRuntimeModule;
import org.eclipse.incquery.patternlanguage.emf.EMFPatternLanguageStandaloneSetup;
import org.eclipse.incquery.patternlanguage.emf.IGenmodelMappingLoader;
import org.eclipse.incquery.patternlanguage.emf.scoping.IMetamodelProviderInstance;

import com.google.inject.Binder;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.multibindings.Multibinder;

public class EMFPatternLanguageMavenStandaloneSetup extends EMFPatternLanguageStandaloneSetup {

    @Override
    public Injector createInjector() {
        return Guice.createInjector(new EMFPatternLanguageRuntimeModule() {

            @SuppressWarnings("unused")
            public void configureIGenmodelMappingLoader(Binder bind) {
                bind.bind(IGenmodelMappingLoader.class).toInstance(MavenBuilderGenmodelLoader.getInstance());
                Multibinder<IMetamodelProviderInstance> metamodelProviderBinder = Multibinder.newSetBinder(bind, IMetamodelProviderInstance.class);
                metamodelProviderBinder.addBinding().to(MavenGenmodelMetamodelProvider.class);
            }

        });
    }

}
