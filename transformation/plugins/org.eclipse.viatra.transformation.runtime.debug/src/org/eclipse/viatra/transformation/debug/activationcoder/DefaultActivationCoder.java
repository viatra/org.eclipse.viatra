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
package org.eclipse.viatra.transformation.debug.activationcoder;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.viatra.query.runtime.api.IPatternMatch;
import org.eclipse.viatra.query.runtime.matchers.util.Preconditions;
import org.eclipse.viatra.transformation.debug.activationcoder.IActivationCoder;
import org.eclipse.viatra.transformation.debug.transformationtrace.model.ActivationTrace;
import org.eclipse.viatra.transformation.debug.transformationtrace.model.RuleParameterTrace;
import org.eclipse.viatra.transformation.evm.api.Activation;
import org.eclipse.viatra.transformation.evm.api.RuleSpecification;

/**
 * Default activation coder implementation that creates transformation trace objects based on the rule instance of the
 * activation and the parameter objects of the rule query specification.
 * 
 * @author Peter Lunk
 */
public class DefaultActivationCoder implements IActivationCoder {
    @Override
    public ActivationTrace createActivationCode(final Activation<?> activation) {
        final RuleSpecification<?> specification = activation.getInstance().getSpecification();
        Preconditions.checkState(!specification.getName().isEmpty(),
                "Rule specification has no defined name:" + specification.toString());
        final ActivationTrace trace = new ActivationTrace(specification.getName());
        try {
            final IPatternMatch match = ((IPatternMatch) activation.getAtom());
            boolean running = true;
            int i = 0;
            while (running) {
                {
                    final Object param = match.get(i);
                    if ((param instanceof EObject)) {
                        final String paramName = match.parameterNames().get(i);
                        trace.getRuleParameterTraces()
                                .add(new RuleParameterTrace(paramName, EcoreUtil.getURI(((EObject) param)).toString()));
                        i++;
                    } else {
                        running = false;
                    }
                }
            }
        } catch (ClassCastException e) {
            e.printStackTrace();
        }
        return trace;
    }
}
