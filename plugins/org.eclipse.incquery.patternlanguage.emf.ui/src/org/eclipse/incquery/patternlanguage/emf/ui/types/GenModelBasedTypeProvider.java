/*******************************************************************************
 * Copyright (c) 2010-2012, Mark Czotter, Istvan Rath and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Mark Czotter - initial API and implementation
 *******************************************************************************/

package org.eclipse.incquery.patternlanguage.emf.ui.types;

import org.eclipse.emf.codegen.ecore.genmodel.GenClass;
import org.eclipse.emf.codegen.ecore.genmodel.GenClassifier;
import org.eclipse.emf.codegen.ecore.genmodel.GenEnum;
import org.eclipse.emf.codegen.ecore.genmodel.GenPackage;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.incquery.patternlanguage.emf.types.EMFPatternTypeProvider;
import org.eclipse.incquery.patternlanguage.patternLanguage.Variable;
import org.eclipse.incquery.tooling.core.generator.genmodel.IEiqGenmodelProvider;
import org.eclipse.xtext.common.types.JvmTypeReference;

import com.google.common.base.Strings;
import com.google.inject.Inject;
import com.google.inject.Singleton;

/**
 * An extension of the {@link EMFPatternTypeProvider}, which can use the genmodel informations as well, if it is
 * present. The main logic for the type inference is implemented in the {@link EMFPatternTypeProvider}.
 */
@Singleton
public class GenModelBasedTypeProvider extends EMFPatternTypeProvider {

    @Inject
    private IEiqGenmodelProvider genModelProvider;

    @Override
    protected String getClassNameForEClassifier(EClassifier classifier, EObject context) {
        String className = super.getClassNameForEClassifier(classifier, context);
        if (Strings.isNullOrEmpty(className) && classifier != null) {
            EPackage ePackage = classifier.getEPackage();
            if (ePackage != null) {
                GenPackage genPackage = genModelProvider.findGenPackage(context, ePackage);
                if (genPackage != null) {
                    className = resolveTypeReference(genPackage, classifier);
                }
            }
        }
        
        return className;
    }

    /**
     * Resolves the {@link Variable} using information from the {@link GenPackage}. Tries to find an appropriate
     * {@link GenClass} for the {@link EClassifier}. If one is found, then returns a {@link JvmTypeReference} for it's
     * qualified interface name.
     *
     * @param genPackage
     * @param classifier
     * @param variable
     * @return
     */
    private String resolveTypeReference(GenPackage genPackage, EClassifier classifier) {
        GenClassifier genClassifier = findGenClassifier(genPackage, classifier);
        String className = null;
        if (!Strings.isNullOrEmpty(classifier.getInstanceClassName())) {
            className = classifier.getInstanceClassName();
        } else if (genClassifier instanceof GenClass) {
            className = ((GenClass) genClassifier).getQualifiedInterfaceName();
        } else if (genClassifier instanceof GenEnum) {
            className = ((GenEnum) genClassifier).getQualifiedInstanceClassName();
        } else {
            //At this point, no corresponding genpackage declaration was found; creating default type
            className = genPackage.getGenModel().getModelPluginPackageName() + "." + classifier.getName();
        }
        return className;
    }

    private GenClassifier findGenClassifier(GenPackage genPackage, EClassifier classifier) {
        for (GenClassifier genClassifier : genPackage.getGenClassifiers()) {
            if (classifier.equals(genClassifier.getEcoreClassifier())) {
                return genClassifier;
            }
        }
        return null;
    }

}
