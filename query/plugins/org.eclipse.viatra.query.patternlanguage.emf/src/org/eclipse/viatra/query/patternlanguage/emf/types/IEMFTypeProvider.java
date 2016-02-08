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
package org.eclipse.viatra.query.patternlanguage.emf.types;

import java.util.Map;
import java.util.Set;

import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.viatra.query.patternlanguage.patternLanguage.ComputationValue;
import org.eclipse.viatra.query.patternlanguage.patternLanguage.LiteralValueReference;
import org.eclipse.viatra.query.patternlanguage.patternLanguage.PathExpressionTail;
import org.eclipse.viatra.query.patternlanguage.patternLanguage.PatternBody;
import org.eclipse.viatra.query.patternlanguage.patternLanguage.Type;
import org.eclipse.viatra.query.patternlanguage.patternLanguage.ValueReference;
import org.eclipse.viatra.query.patternlanguage.patternLanguage.Variable;
import org.eclipse.xtext.common.types.JvmTypeReference;

/**
 * A small interface extending the {@link ITypeProvider} capabilities.
 */
/**
 * @author Andras Okros, Zoltan Ujhelyi
 *
 */
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
    Set<EClassifier> getIrreducibleClassifiersForVariableInBody(PatternBody patternBody, Variable variable);

    /**
     * @param variable
     * @return the {@link EClassifier} explicitly given in the parameter list for the given pattern parameter {@link Variable}. Returns null, if none found or not a parameter(Ref).
     */
    EClassifier getExplicitClassifierForPatternParameterVariable(Variable variable);

    /**
	 * Calculates the JVM type of the selected variable. The calculation
	 * includes the inference of the corresponding EMF type, and then the
	 * calculation of corresponding Java class.
	 * 
	 * @param variable
	 * @return
	 */
	JvmTypeReference getVariableType(Variable variable);

	/**
	 * Calculates the corresponding JVM type of a selected EClassifier. The context is used to select the model-specific information, e.g. ResourceSet, project, etc.
	 * @param classifier
	 * @param context
	 * @return
	 */
	JvmTypeReference getJvmType(EClassifier classifier, EObject context);
	/**
     * @param valueReference
     * @return an {@link EClassifier} for the given input {@link ValueReference}. The ValueReference can be a
     *         {@link LiteralValueReference}, or a {@link ComputationValue}.
     */
    EClassifier getClassifierForLiteralComputationEnumValueReference(ValueReference valueReference);

    Map<PathExpressionTail,EStructuralFeature> getAllFeaturesFromPathExpressionTail(PathExpressionTail pathExpressionTail);

    Type getTypeFromPathExpressionTail(PathExpressionTail pathExpressionTail);

}