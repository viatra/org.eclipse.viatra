/*******************************************************************************
 * Copyright (c) 2010-2012, Andras Okros, Zoltan Ujhelyi, Istvan Rath and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Andras Okros, Zoltan Ujhelyi - initial API and implementation
 *******************************************************************************/
package org.eclipse.incquery.patternlanguage.emf.types;

import java.util.Set;

import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.incquery.patternlanguage.patternLanguage.PatternBody;
import org.eclipse.incquery.patternlanguage.patternLanguage.Type;
import org.eclipse.incquery.patternlanguage.patternLanguage.Variable;
import org.eclipse.xtext.common.types.JvmTypeReference;

/**
 * A small interface extending the {@link ITypeProvider} capabilities.
 */
/**
 * @author Andras Okros, Zoltan Ujhelyi
 *
 */
@SuppressWarnings("restriction")
public interface IEMFTypeProvider {

    /**
     * @param variable
     * @return the {@link EClassifier} for the given {@link Variable}. Returns null, if it fails.
     */
    EClassifier getClassifierForVariable(Variable variable);

    /**
     * @param type
     * @return the {@link EClassifier} for the given {@link Type}. Returns null, if it fails.
     */
    EClassifier getClassifierForType(Type type);

    /**
     * @param patternBody
     * @param variable
     * @return the list of possible classifiers computed from the constraints in the patternbody.
     */
    Set<EClassifier> getPossibleClassifiersForVariableInBody(PatternBody patternBody, Variable variable);

    /**
     * @param variable
     * @return the {@link EClassifier} for the given {@link Variable}. Returns null, if it fails.
     */
    EClassifier getClassifierForPatternParameterVariable(Variable variable);

    /**
	 * Calculates the JVM type of the selected variable. The calculation
	 * includes the inference of the corresponding EMF type, and then the
	 * calculation of corresponding Java class.
	 * 
	 * @param variable
	 * @return
	 */
	JvmTypeReference getVariableType(Variable variable);
}