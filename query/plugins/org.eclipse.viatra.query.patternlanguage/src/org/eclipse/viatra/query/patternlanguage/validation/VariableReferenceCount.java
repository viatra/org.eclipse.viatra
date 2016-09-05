/*******************************************************************************
 * Copyright (c) 2010-2012, Zoltan Ujhelyi, Istvan Rath and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Zoltan Ujhelyi - initial API and implementation
 *******************************************************************************/
package org.eclipse.viatra.query.patternlanguage.validation;

import java.util.Hashtable;
import java.util.Map;
import java.util.Set;

import org.eclipse.viatra.query.patternlanguage.patternLanguage.JavaType;
import org.eclipse.viatra.query.patternlanguage.patternLanguage.ParameterRef;
import org.eclipse.viatra.query.patternlanguage.patternLanguage.Variable;

import com.google.common.base.Joiner;

/**
 * Helper class for counting variable references
 * 
 * @author Zoltan Ujhelyi
 * 
 */
class VariableReferenceCount {

    public enum ReferenceType {
        POSITIVE, NEGATIVE, READ_ONLY
    }

    private Set<Variable> variables;
    private Map<ReferenceType, Integer> counters = new Hashtable<ReferenceType, Integer>();
    private int size = 0;
    private boolean parameter;

    public VariableReferenceCount(Set<Variable> variables, boolean parameter) {
        this.variables = variables;
        this.parameter = parameter;
        for (ReferenceType type : ReferenceType.values()) {
            counters.put(type, 0);
        }
        
        for (Variable variable : variables) {
        	if (variable instanceof ParameterRef 
        	        && ((ParameterRef) variable).getReferredParam().getType() != null
        	        && !(((ParameterRef) variable).getReferredParam().getType() instanceof JavaType)) {
        		counters.put(ReferenceType.POSITIVE, 1);
        	}
        }
    }

    public Set<Variable> getVariables() {
        return variables;
    }

    /**
     * @return the number of references stored regardless of reference type
     */
    public int getReferenceCount() {
        return size;
    }

    public boolean isParameter() {
        return parameter;
    }

    /**
     * @param type
     * @return the number of references stored of a selected reference type
     */
    public int getReferenceCount(ReferenceType type) {
        return counters.get(type);
    }

    public void incrementCounter(ReferenceType type) {
        size++;
        counters.put(type, counters.get(type) + 1);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        if (parameter) {
            sb.append("Variable: ");
        } else {
            sb.append("Parameter: ");
        }
        sb.append("<");
        Joiner joiner = Joiner.on(", ");
        joiner.appendTo(sb, getVariables());
        sb.append("> ");
        joiner.withKeyValueSeparator("=").appendTo(sb, counters);
        sb.append(" ---- Total: ");
        sb.append(size);
        return sb.toString();
    }
}
