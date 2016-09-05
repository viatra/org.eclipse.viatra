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

import java.util.List;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.viatra.query.patternlanguage.patternLanguage.AggregatedValue;
import org.eclipse.viatra.query.patternlanguage.patternLanguage.Expression;
import org.eclipse.viatra.query.patternlanguage.patternLanguage.ParameterRef;
import org.eclipse.viatra.query.patternlanguage.patternLanguage.Type;
import org.eclipse.viatra.query.patternlanguage.patternLanguage.Variable;
import org.eclipse.viatra.query.patternlanguage.patternLanguage.VariableReference;
import org.eclipse.viatra.query.patternlanguage.util.AggregatorUtil;
import org.eclipse.viatra.query.runtime.matchers.context.IInputKey;
import org.eclipse.viatra.query.runtime.matchers.context.common.JavaTransitiveInstancesKey;
import org.eclipse.xtext.common.types.JvmType;
import org.eclipse.xtext.common.types.JvmTypeReference;

import com.google.inject.Inject;

/**
 * @author Zoltan Ujhelyi
 *
 */
public abstract class AbstractTypeInferrer implements ITypeInferrer {

    @Inject
    private ITypeSystem typeSystem;
    
    /**
     * @since 1.3
     */
    @Override
    public IInputKey getType(Expression ex) {
        final IInputKey declaredType = getDeclaredType(ex);
        if (declaredType != null) {
            return declaredType;
        } else {
            return getInferredType(ex);
        }
    }

    /**
     * @since 1.3
     */
    @Override
    public IInputKey getDeclaredType(Expression ex) {
        if (ex instanceof ParameterRef) {
            Type type = ((ParameterRef) ex).getReferredParam().getType();
            if (typeSystem.isValidType(type)) {
                return typeSystem.extractTypeDescriptor(type);
            } 
        } else if (ex instanceof Variable) {
            final Type type = ((Variable)ex).getType();
            if (typeSystem.isValidType(type)) {
                return typeSystem.extractTypeDescriptor(type);
            } 
        } else if (ex instanceof AggregatedValue) {
            List<JvmType> returnTypes = AggregatorUtil.getReturnTypes(((AggregatedValue) ex).getAggregator());
            if (returnTypes.size() == 1) {
                JvmType jvmType = returnTypes.get(0);
                return new JavaTransitiveInstancesKey(jvmType.getIdentifier());
            }
        }
        return null;
    }

    /**
     * @since 1.3
     */
    @Override
    public JvmTypeReference getJvmType(Expression ex, EObject context) {
        return typeSystem.toJvmTypeReference(getType(ex), context);
    }
    
    @Override
    public IInputKey getInferredVariableType(Variable ex) {
        return getInferredType(ex);
    }

    @Override
    public JvmTypeReference getVariableJvmType(Variable ex, EObject context) {
        return getJvmType(ex, context);
    }

    @Override
    public IInputKey getDeclaredType(Variable ex) {
        return getDeclaredType((Expression)ex);
    }

    @Override
    public IInputKey getVariableReferenceType(VariableReference ref) {
        return getType(ref);
    }

    @Override
    public IInputKey getVariableType(Variable var) {
        return getType(var);
    }
    
}
