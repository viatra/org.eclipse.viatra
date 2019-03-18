/*******************************************************************************
 * Copyright (c) 2010-2014, Balint Lorand, Zoltan Ujhelyi, Abel Hegedus, Tamas Szabo, Istvan Rath and Daniel Varro
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-v20.html.
 * 
 * SPDX-License-Identifier: EPL-2.0
 *******************************************************************************/

package org.eclipse.viatra.addon.validation.core;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.viatra.addon.validation.core.api.IEntry;
import org.eclipse.viatra.query.runtime.api.IPatternMatch;

public class Entry implements IEntry {

    private IPatternMatch match;
    private Violation violation;

    protected Entry(Violation violation, IPatternMatch match) {
        this.violation = violation;
        this.match = match;
    }

    @Override
    public Object getValue(String propertyName) {
        return match.get(propertyName);
    }

    @Override
    public List<Object> getValues() {
        List<Object> properties = new ArrayList<Object>();
        List<String> propertyNames = violation.getConstraint().getSpecification().getPropertyNames();
        for (String parameterName : propertyNames) {
            properties.add(match.get(parameterName));
        }
        return properties;
    }

    protected IPatternMatch getMatch() {
        return match;
    }

    protected void setMatch(IPatternMatch match) {
        this.match = match;
    }

}
