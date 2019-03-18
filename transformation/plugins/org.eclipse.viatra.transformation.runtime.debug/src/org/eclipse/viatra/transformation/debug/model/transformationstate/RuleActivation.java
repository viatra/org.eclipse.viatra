/**
 * Copyright (c) 2010-2016, Peter Lunk, IncQuery Labs Ltd.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-v20.html.
 * 
 * SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.viatra.transformation.debug.model.transformationstate;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.eclipse.viatra.transformation.debug.model.transformationstate.ActivationParameter;
import org.eclipse.viatra.transformation.debug.model.transformationstate.TransformationState;
import org.eclipse.viatra.transformation.debug.transformationtrace.model.ActivationTrace;

public class RuleActivation implements Serializable {
    private static final long serialVersionUID = -2143167768838736621L;

    private final TransformationState transformationState;

    private final ActivationTrace trace;

    private final boolean nextActivation;

    private final String state;

    private final String ruleName;

    private final List<ActivationParameter> paremeters;

    public RuleActivation(final ActivationTrace trace, final boolean nextActivation, final String state,
            final String ruleName, final List<ActivationParameter> paremeters,
            final TransformationState transformationState) {
        super();
        this.trace = trace;
        this.nextActivation = nextActivation;
        this.state = state;
        this.ruleName = ruleName;
        this.paremeters = paremeters;
        this.transformationState = transformationState;
    }

    public ActivationTrace getTrace() {
        return this.trace;
    }

    public boolean isNextActivation() {
        return this.nextActivation;
    }

    public String getState() {
        return this.state;
    }

    public String getRuleName() {
        return this.ruleName;
    }

    public List<ActivationParameter> getParameters() {
        return new ArrayList<>(paremeters);
    }

    public TransformationState getTransformationState() {
        return this.transformationState;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("State: ");
        builder.append(state);
        builder.append(" - Parameters(");

        builder.append(getParameters().stream().map(param -> param.getName() + " = " + param.getValue())
                .collect(Collectors.joining(",")));
        builder.append(")");

        return builder.toString();
    }
}
