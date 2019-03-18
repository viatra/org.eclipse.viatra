/**
 * Copyright (c) 2010-2016, Peter Lunk, IncQuery Labs Ltd.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-v20.html.
 * 
 * SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.viatra.transformation.debug.transformationtrace.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class TransformationTrace implements Serializable {
    private static final long serialVersionUID = -5640407055932939677L;
    private List<ActivationTrace> activationTraces = new ArrayList<>();

    public List<ActivationTrace> getActivationTraces() {
        return activationTraces;
    }
}
