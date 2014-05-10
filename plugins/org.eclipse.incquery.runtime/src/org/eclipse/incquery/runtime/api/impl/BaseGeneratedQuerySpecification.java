/*******************************************************************************
 * Copyright (c) 2004-2010 Gabor Bergmann and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Gabor Bergmann - initial API and implementation
 *******************************************************************************/

package org.eclipse.incquery.runtime.api.impl;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EEnum;
import org.eclipse.emf.ecore.EEnumLiteral;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.incquery.runtime.api.IPatternMatch;
import org.eclipse.incquery.runtime.api.IncQueryMatcher;
import org.eclipse.incquery.runtime.exception.IncQueryException;

import com.google.common.base.Preconditions;

/**
 * Provides common functionality of pattern-specific generated query specifications.
 *
 * @author Bergmann Gábor
 * @author Mark Czotter
 */
public abstract class BaseGeneratedQuerySpecification<Matcher extends IncQueryMatcher<? extends IPatternMatch>> extends
        BaseQuerySpecification<Matcher> {

    public BaseGeneratedQuerySpecification() {
        super();
        ensureInitialized();
    }
    
    protected static void processInitializerError(ExceptionInInitializerError err) throws IncQueryException {
        Throwable cause1 = err.getCause();
        if (cause1 instanceof RuntimeException) {
            Throwable cause2 = ((RuntimeException) cause1).getCause();
            if (cause2 instanceof IncQueryException) {
                throw (IncQueryException) cause2;
            }
        }
    }

    protected EClassifier getClassifierLiteral(String packageUri, String classifierName) {
        EPackage ePackage = EPackage.Registry.INSTANCE.getEPackage(packageUri);
        Preconditions.checkState(ePackage != null, "EPackage %s not found in EPackage Registry.", packageUri);
        EClassifier literal = ePackage.getEClassifier(classifierName);
        Preconditions.checkState(literal != null, "Classifier %s not found in EPackage %s", classifierName, packageUri);
        return literal;
    }

    protected EStructuralFeature getFeatureLiteral(String packageUri, String className, String featureName) {
        EClassifier container = getClassifierLiteral(packageUri, className);
        Preconditions.checkState(container instanceof EClass, "Classifier %s in EPackage %s does not refer to an EClass.", className, packageUri);
        EStructuralFeature feature = ((EClass)container).getEStructuralFeature(featureName);
        Preconditions.checkState(feature != null, "Feature %s not found in EClass %s", featureName, className);
        return feature;
    }

    protected EEnumLiteral getEnumLiteral(String packageUri, String enumName, String literalName) {
        EClassifier enumContainer = getClassifierLiteral(packageUri, enumName);
        Preconditions.checkState(enumContainer instanceof EEnum, "Classifier %s in EPackage %s is not an EEnum.", enumName, packageUri);
        EEnumLiteral literal = ((EEnum)enumContainer).getEEnumLiteral(literalName);
        Preconditions.checkState(literal != null, "Unknown literal %s in enum %s", literalName, enumName);
        return literal;
    }
}
