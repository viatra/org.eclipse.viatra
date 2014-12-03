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
package org.eclipse.incquery.patternlanguage.typing;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.incquery.patternlanguage.patternLanguage.Variable;
import org.eclipse.incquery.patternlanguage.patternLanguage.VariableReference;
import org.eclipse.xtext.common.types.JvmTypeReference;

/**
 * The type inferrer calculates the types of variables and variable references of the pattern model.
 * 
 * @author Zoltan Ujhelyi
 *
 */
public interface ITypeInferrer {
    /**
     * Returns the declared type of a variable
     * 
     * @param var
     * @return the declared type of the variable, or null if no type declaration is available
     */
    Object getDeclaredType(Variable var);

    /**
     * Returns the inferred type of a variable
     * 
     * @param var
     * @return
     */
    Object getInferredVariableType(Variable var);

    /**
     * Returns the type of a variable.
     * 
     * @param var
     * @return if the variable has a declared type, it is returned; otherwise the inferred type is calculated.
     */
    Object getVariableType(Variable var);

    /**
     * Calculates the type of a variable reference. A type of a reference is the type of the referred variable.
     * 
     * @param reference
     * @return
     */
    Object getVariableReferenceType(VariableReference reference);

    /**
     * Creates a Jvm Type Reference for a selected variable. Useful during Jvm Model Inference
     * @param var
     * @param context
     * @return
     */
    JvmTypeReference getVariableJvmType(Variable var, EObject context);

    /**
     * An empty implementation of {@link ITypeInferrer} that can be used by the abstract pattern language module.
     */
    public static class NullTypeInferrer implements ITypeInferrer {

        @Override
        public Object getInferredVariableType(Variable var) {
            throw new UnsupportedOperationException();
        }

        @Override
        public JvmTypeReference getVariableJvmType(Variable var, EObject context) {
            throw new UnsupportedOperationException();
        }

        @Override
        public Object getVariableReferenceType(VariableReference reference) {
            throw new UnsupportedOperationException();
        }

        @Override
        public Object getDeclaredType(Variable var) {
            throw new UnsupportedOperationException();
        }

        @Override
        public Object getVariableType(Variable var) {
            throw new UnsupportedOperationException();
        }

    }
}
