/*******************************************************************************
 * Copyright (c) 2010-2014, Zoltan Ujhelyi, Istvan Rath and Daniel Varro
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-v20.html.
 * 
 * SPDX-License-Identifier: EPL-2.0
 *******************************************************************************/
package org.eclipse.viatra.query.patternlanguage.emf.types;

import java.util.Set;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.viatra.query.patternlanguage.emf.vql.Expression;
import org.eclipse.viatra.query.patternlanguage.emf.vql.Variable;
import org.eclipse.viatra.query.runtime.matchers.context.IInputKey;
import org.eclipse.xtext.common.types.JvmTypeReference;

/**
 * The type inferrer calculates the types of variables and variable references of the pattern model.
 * 
 * @author Zoltan Ujhelyi
 * @noimplement
 * @since 2.0
 *
 */
public interface ITypeInferrer {
    /**
     * Returns the declared type of a variable
     * 
     * @param ex
     * @return the declared type of the variable, or null if no type declaration is available
     * @since 1.3
     */
    IInputKey getDeclaredType(Expression ex);
    
    IInputKey getDeclaredType(Variable ex);

    /**
     * Returns the inferred type of a variable
     * 
     * @param ex
     * @since 1.3
     */
    IInputKey getInferredType(Expression ex);
    
    /**
     * Returns a collection of possible inferred types. Used for validating inputs; not recommended to use when
     * processing the patterns

     * @since 1.3
     */
    Set<IInputKey> getAllPossibleTypes(Expression ex);

    /**
     * Returns the type of a variable.
     * 
     * @return if the variable has a declared type, it is returned; otherwise the inferred type is calculated.
     * @since 1.3
     */
    IInputKey getType(Expression ex);
    
    /**
     * Creates a Jvm Type Reference for a selected expression. Useful during Jvm Model Inference
     * @param ex
     * @param context
     * @since 1.3
     */
    JvmTypeReference getJvmType(Expression ex, EObject context);
}
