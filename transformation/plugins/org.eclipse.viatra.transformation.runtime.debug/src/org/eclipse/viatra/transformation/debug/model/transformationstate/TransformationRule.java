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

public class TransformationRule implements Serializable{
    private static final long serialVersionUID = -8125023765801802667L;
    private final String ruleName;
    private final boolean filtered;
    private final List<RuleActivation> activations;
    
    public TransformationRule(String ruleName, boolean filtered, List<RuleActivation> activations) {
        super();
        this.ruleName = ruleName;
        this.filtered = filtered;
        this.activations = activations;
    }


    public String getRuleName() {
        return ruleName;
    }

    public boolean isFiltered() {
        return filtered;
    }

    public List<RuleActivation> getActivations() {
        return new ArrayList<>(activations);
    }
    
    
}
