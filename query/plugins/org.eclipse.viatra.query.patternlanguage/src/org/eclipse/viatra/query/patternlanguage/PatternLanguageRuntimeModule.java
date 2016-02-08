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
package org.eclipse.viatra.query.patternlanguage;

import org.apache.log4j.Logger;
import org.eclipse.viatra.query.patternlanguage.annotations.PatternAnnotationProvider;
import org.eclipse.viatra.query.patternlanguage.naming.PatternNameProvider;
import org.eclipse.viatra.query.patternlanguage.typing.ITypeInferrer;
import org.eclipse.viatra.query.patternlanguage.typing.ITypeInferrer.NullTypeInferrer;
import org.eclipse.viatra.query.patternlanguage.typing.ITypeSystem;
import org.eclipse.viatra.query.patternlanguage.typing.ITypeSystem.NullTypeSystem;
import org.eclipse.xtext.naming.IQualifiedNameProvider;

import com.google.inject.Provides;

/**
 * Use this class to register components to be used at runtime / without the Equinox extension registry.
 */
public class PatternLanguageRuntimeModule extends AbstractPatternLanguageRuntimeModule {

    @Provides
    Logger provideLoggerImplementation() {
        return Logger.getLogger(PatternLanguageRuntimeModule.class);
    }

    @Override
    public Class<? extends IQualifiedNameProvider> bindIQualifiedNameProvider() {
        return PatternNameProvider.class;
    }

    public Class<? extends PatternAnnotationProvider> bindPatternAnnotationProvider() {
        return PatternAnnotationProvider.class;
    }
    
    public Class<? extends ITypeSystem> bindITypeSystem() {
        return NullTypeSystem.class;
    }
    
    public Class<? extends ITypeInferrer> bindITypeInferrer() {
        return NullTypeInferrer.class;
    }
}
