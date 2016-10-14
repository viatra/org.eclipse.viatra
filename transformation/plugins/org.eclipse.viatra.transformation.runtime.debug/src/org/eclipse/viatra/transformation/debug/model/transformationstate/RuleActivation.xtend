/**
 * Copyright (c) 2010-2016, Peter Lunk, IncQuery Labs Ltd.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *   Peter Lunk - initial API and implementation
 */
package org.eclipse.viatra.transformation.debug.model.transformationstate

import com.google.common.collect.Lists
import java.io.Serializable
import java.util.List
import org.eclipse.viatra.transformation.debug.transformationtrace.model.ActivationTrace

class RuleActivation implements Serializable{
    final TransformationState transformationState
    final ActivationTrace trace
    final boolean nextActivation
    final String state
    final String ruleName
    final List<ActivationParameter> paremeters

    new(ActivationTrace trace, boolean nextActivation, String state, String ruleName,
        List<ActivationParameter> paremeters, TransformationState transformationState) {
        super()
        this.trace = trace
        this.nextActivation = nextActivation
        this.state = state
        this.ruleName = ruleName
        this.paremeters = paremeters
        this.transformationState = transformationState
    }

    def ActivationTrace getTrace() {
        return trace
    }

    def boolean isNextActivation() {
        return nextActivation
    }

    def String getState() {
        return state
    }

    def String getRuleName() {
        return ruleName
    }

    def List<ActivationParameter> getParameters() {
        return Lists.newArrayList(paremeters)
    }
    
     def TransformationState getTransformationState() {
        return transformationState
    }
    
    override String toString() {
       
        '''Activation - State: «state» - Parameters: «FOR ActivationParameter param : parameters SEPARATOR ','» «param.name» : «param.value.toString»«ENDFOR»'''
    }
}
