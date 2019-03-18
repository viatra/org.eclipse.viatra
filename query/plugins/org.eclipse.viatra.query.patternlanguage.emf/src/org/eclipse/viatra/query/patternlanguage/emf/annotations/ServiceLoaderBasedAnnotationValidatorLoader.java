/*******************************************************************************
 * Copyright (c) 2010-2018, Zoltan Ujhelyi, IncQuery Labs Ltd.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-v20.html.
 * 
 * SPDX-License-Identifier: EPL-2.0
 *******************************************************************************/
package org.eclipse.viatra.query.patternlanguage.emf.annotations;

import java.util.Map;
import java.util.ServiceLoader;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import com.google.inject.Inject;
import com.google.inject.Injector;

/**
 * @since 2.0
 * @noreference This class is not intended to be referenced by clients.
 *
 */
public class ServiceLoaderBasedAnnotationValidatorLoader implements IAnnotationValidatorLoader {

    @Inject
    private Injector injector;
    
    @Override
    public Map<String, IPatternAnnotationValidator> getKnownValidators() {
        return StreamSupport.stream(ServiceLoader.load(IPatternAnnotationValidator.class).spliterator(), false)
                .map(this::injectValidator)
                .collect(Collectors.toMap(IPatternAnnotationValidator::getAnnotationName, v -> v));
    }

    private IPatternAnnotationValidator injectValidator(IPatternAnnotationValidator v) {
        injector.injectMembers(v);
        return v;
    }
}
