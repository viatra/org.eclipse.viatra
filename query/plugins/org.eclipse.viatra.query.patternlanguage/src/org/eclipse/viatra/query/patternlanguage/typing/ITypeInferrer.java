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
package org.eclipse.viatra.query.patternlanguage.typing;

import java.util.Set;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.viatra.query.patternlanguage.patternLanguage.Expression;
import org.eclipse.viatra.query.patternlanguage.patternLanguage.Variable;
import org.eclipse.viatra.query.runtime.matchers.context.IInputKey;
import org.eclipse.xtext.common.types.JvmTypeReference;

/**
 * The type inferrer calculates the types of variables and variable references of the pattern model.
 * 
 * @author Zoltan Ujhelyi
 * @noimplement
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
    
    /**
     * An empty implementation of {@link ITypeInferrer} that can be used by the abstract pattern language module.
     * @since 1.3
     */
    public static class NullTypeInferrer extends AbstractTypeInferrer {

        /**
         * @since 1.3
         */
        @Override
        public IInputKey getInferredType(Expression ex) {
            throw new UnsupportedOperationException();
        }

        /**
         * @since 1.3
         */
        @Override
        public JvmTypeReference getJvmType(Expression ex, EObject context) {
            throw new UnsupportedOperationException();
        }

        /**
         * @since 1.3
         */
        @Override
        public IInputKey getType(Expression ex) {
            throw new UnsupportedOperationException();
        }

        /**
         * @since 1.3
         */
        @Override
        public IInputKey getDeclaredType(Expression ex) {
            throw new UnsupportedOperationException();
        }

        /**
         * @since 1.3
         */
        @Override
        public Set<IInputKey> getAllPossibleTypes(Expression ex) {
            throw new UnsupportedOperationException();
        }

    }
}
