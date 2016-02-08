/*******************************************************************************
 * Copyright (c) 2010-2014, Zoltan Ujhelyi, Istvan Rath and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Zoltan Ujhelyi - initial API and implementation
 *******************************************************************************/
package org.eclipse.incquery.patternlanguage.emf.ui.types;

import org.eclipse.xtext.common.types.access.IJvmTypeProvider;
import org.eclipse.xtext.common.types.access.IJvmTypeProvider.Factory;
import org.eclipse.xtext.common.types.access.jdt.IJdtTypeProvider;
import org.eclipse.xtext.common.types.xtext.AbstractConstructorScope;
import org.eclipse.xtext.common.types.xtext.AbstractTypeScope;
import org.eclipse.xtext.common.types.xtext.AbstractTypeScopeProvider;
import org.eclipse.xtext.common.types.xtext.ClasspathBasedTypeScopeProvider;
import org.eclipse.xtext.common.types.xtext.ui.JdtBasedSimpleTypeScopeProvider;
import org.eclipse.xtext.resource.IEObjectDescription;

import com.google.common.base.Predicate;
import com.google.inject.Inject;

/**
 * @author Zoltan Ujhelyi
 *
 */
public class EMFPatternLanguageTypeScopeProvider extends AbstractTypeScopeProvider {

    @Inject
    ClasspathBasedTypeScopeProvider classpathTypeScopeProvider;
    @Inject
    JdtBasedSimpleTypeScopeProvider jdtBasedTypeScopeProvider;
    @Inject
    EMFPatternLanguageTypeProviderFactory factory;
    
    @Override
    public AbstractTypeScope createTypeScope(IJvmTypeProvider typeProvider, Predicate<IEObjectDescription> filter) {
        if (typeProvider instanceof IJdtTypeProvider) {
            return jdtBasedTypeScopeProvider.createTypeScope(typeProvider, filter);
        } else {
            return classpathTypeScopeProvider.createTypeScope(typeProvider, filter);
        }
    }

    @Override
    public AbstractConstructorScope createConstructorScope(IJvmTypeProvider typeProvider,
            Predicate<IEObjectDescription> filter) {
        if (typeProvider instanceof IJdtTypeProvider) {
            return jdtBasedTypeScopeProvider.createConstructorScope(typeProvider, filter);
        } else {
            return classpathTypeScopeProvider.createConstructorScope(typeProvider, filter);
        }
    }

    @Override
    public Factory getTypeProviderFactory() {
        return factory;
    }

}
