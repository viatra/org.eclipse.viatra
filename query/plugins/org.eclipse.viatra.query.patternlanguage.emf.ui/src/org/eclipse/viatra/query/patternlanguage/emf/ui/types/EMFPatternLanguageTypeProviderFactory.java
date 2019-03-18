/*******************************************************************************
 * Copyright (c) 2010-2014, Zoltan Ujhelyi, Istvan Rath and Daniel Varro
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-v20.html.
 * 
 * SPDX-License-Identifier: EPL-2.0
 *******************************************************************************/
package org.eclipse.viatra.query.patternlanguage.emf.ui.types;

import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.xtext.common.types.access.AbstractTypeProviderFactory;
import org.eclipse.xtext.common.types.access.ClasspathTypeProviderFactory;
import org.eclipse.xtext.common.types.access.IJvmTypeProvider;
import org.eclipse.xtext.common.types.access.jdt.IJavaProjectProvider;
import org.eclipse.xtext.common.types.access.jdt.JdtTypeProviderFactory;

import com.google.inject.Inject;

/**
 * @author Zoltan Ujhelyi
 *
 */
public class EMFPatternLanguageTypeProviderFactory extends AbstractTypeProviderFactory {

    @Inject
    private JdtTypeProviderFactory jdtFactory;
    @Inject
    private ClasspathTypeProviderFactory classPathFactory;
    @Inject
    private IJavaProjectProvider javaProjectProvider;
    
    @Override
    public IJvmTypeProvider createTypeProvider(ResourceSet resourceSet) {
        if (javaProjectProvider.getJavaProject(resourceSet) != null) {
            //Opening resource from a Java project
            return jdtFactory.createTypeProvider(resourceSet);
        }
        return classPathFactory.createTypeProvider(resourceSet);
    }

}
