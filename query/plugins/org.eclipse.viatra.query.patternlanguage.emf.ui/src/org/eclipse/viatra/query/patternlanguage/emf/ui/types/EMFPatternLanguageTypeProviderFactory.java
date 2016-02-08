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
