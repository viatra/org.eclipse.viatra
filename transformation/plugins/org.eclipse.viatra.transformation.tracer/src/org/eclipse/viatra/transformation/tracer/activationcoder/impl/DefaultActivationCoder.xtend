/*******************************************************************************
 * Copyright (c) 2004-2015, Peter Lunk, Zoltan Ujhelyi and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *   Peter Lunk - initial API and implementation
 *******************************************************************************/
package org.eclipse.viatra.transformation.tracer.activationcoder.impl

import org.eclipse.emf.ecore.EObject
import org.eclipse.emf.ecore.util.EcoreUtil
import org.eclipse.viatra.query.runtime.api.IPatternMatch
import org.eclipse.viatra.transformation.evm.api.Activation
import org.eclipse.viatra.transformation.tracer.activationcoder.IActivationCoder
import org.eclipse.viatra.transformation.tracer.transformationtrace.ActivationTrace
import org.eclipse.viatra.transformation.tracer.transformationtrace.TransformationtraceFactory

/**
 * Default activation coder implementation that creates transformation trace objects based on the rule 
 * instance of the activation and the parameter objects of the rule query specification.
 * 
 * @author Peter Lunk
 */
class DefaultActivationCoder implements IActivationCoder {
    extension TransformationtraceFactory factory = TransformationtraceFactory.eINSTANCE

    override createActivationCode(Activation<?> activation) {
        val specification = activation.instance.specification
        val ActivationTrace trace = factory.createActivationTrace

        if (specification.name == "") {
            throw new IllegalStateException("Rule specification has no defined name:" + specification.toString());
        }

        trace.ruleName = specification.name

        try {
            val match = activation.atom as IPatternMatch

            var boolean running = true
            var i = 0
            while (running) {
                val param = match.get(i)

                if (param instanceof EObject) {
                    val paramName = match.parameterNames.get(i)
                    trace.ruleParameterTraces.add(factory.createRuleParameterTrace => [
                        parameterName = paramName
                        objectId = EcoreUtil.getURI(param).toString
                    ])
                    i++
                } else {
                    running = false
                }
            }
        } catch (ClassCastException e) {
            e.printStackTrace
        }
        trace
    }

}
