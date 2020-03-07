/*******************************************************************************
 * Copyright (c) 2010-2012, Zoltan Ujhelyi, Istvan Rath and Daniel Varro
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-v20.html.
 * 
 * SPDX-License-Identifier: EPL-2.0
 *******************************************************************************/
package org.eclipse.viatra.query.tooling.generator.model.ui;

import org.apache.log4j.Logger;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.eclipse.viatra.query.tooling.generator.model.validation.GeneratorModelValidator;
import org.eclipse.xtext.service.SingletonBinding;

import com.google.inject.Provides;
import com.google.inject.Singleton;

/**
 * Use this class to register components to be used within the IDE.
 */
public class GeneratorModelUiModule extends AbstractGeneratorModelUiModule {
    public GeneratorModelUiModule(AbstractUIPlugin plugin) {
        super(plugin);
    }

    private static final String loggerRoot = "org.eclipse.viatra.query";

    @Provides
    @Singleton
    Logger provideLoggerImplementation() {
        return Logger.getLogger(loggerRoot);
    }

    @SingletonBinding(eager = true)
    public Class<? extends GeneratorModelValidator> bindGeneratorModelJavaValidator() {
        return GenmodelProjectBasedValidation.class;
    }
}
