/*******************************************************************************
 * Copyright (c) 2010-2012, Zoltan Ujhelyi, Istvan Rath and Daniel Varro
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-v20.html.
 * 
 * SPDX-License-Identifier: EPL-2.0
 *******************************************************************************/
package org.eclipse.viatra.query.tooling.generator.model;

import org.eclipse.viatra.query.tooling.generator.model.scoping.GeneratorModelCrossRefSerializer;
import org.eclipse.viatra.query.tooling.generator.model.scoping.GeneratorModelLinkingService;
import org.eclipse.xtext.linking.ILinkingService;
import org.eclipse.xtext.serializer.tokens.ICrossReferenceSerializer;

/**
 * Use this class to register components to be used at runtime / without the Equinox extension registry.
 */
public class GeneratorModelRuntimeModule extends AbstractGeneratorModelRuntimeModule {

    @Override
    public Class<? extends ILinkingService> bindILinkingService() {
        return GeneratorModelLinkingService.class;
    }

    public Class<? extends ICrossReferenceSerializer> bindICrossReferenceSerializer() {
        return GeneratorModelCrossRefSerializer.class;
    }
}
