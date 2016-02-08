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

import org.eclipse.emf.ecore.EObject;
import org.eclipse.viatra.query.patternlanguage.patternLanguage.Type;
import org.eclipse.viatra.query.patternlanguage.patternLanguage.Variable;
import org.eclipse.viatra.query.patternlanguage.patternLanguage.VariableReference;
import org.eclipse.viatra.query.runtime.matchers.context.IInputKey;
import org.eclipse.xtext.common.types.JvmTypeReference;

import com.google.inject.Inject;

/**
 * @author Zoltan Ujhelyi
 *
 */
public abstract class AbstractTypeInferrer implements ITypeInferrer {

    @Inject
    private ITypeSystem typeSystem;
    
    @Override
    public IInputKey getVariableType(Variable var) {
        final IInputKey declaredType = getDeclaredType(var);
        if (declaredType != null) {
            return declaredType;
        } else {
            return getInferredVariableType(var);
        }
    }

    @Override
    public IInputKey getDeclaredType(Variable var) {
        final Type type = var.getType();
        if (type != null) {
            return typeSystem.extractTypeDescriptor(type);
        } else {
            return null;
        }
    }

    @Override
    public IInputKey getVariableReferenceType(VariableReference reference) {
        return getVariableType(reference.getVariable());
    }

    @Override
    public JvmTypeReference getVariableJvmType(Variable var, EObject context) {
        return typeSystem.toJvmTypeReference(getVariableType(var), context);
    }
}
