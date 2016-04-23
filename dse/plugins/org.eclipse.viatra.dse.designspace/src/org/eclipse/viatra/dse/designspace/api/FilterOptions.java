/*******************************************************************************
 * Copyright (c) 2010-2014, Miklos Foldenyi, Andras Szabolcs Nagy, Abel Hegedus, Akos Horvath, Zoltan Ujhelyi and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Contributors:
 *   Miklos Foldenyi - initial API and implementation
 *   Andras Szabolcs Nagy - initial API and implementation
 *******************************************************************************/
package org.eclipse.viatra.dse.designspace.api;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.viatra.dse.api.DSETransformationRule;

/**
 * Filter options for retrieving transitions. 
 */
public class FilterOptions {
    /**
     * If set to true and the current state dissatisfies the global constraints an empty list will be returned.
     */
    public boolean nothingIfCut = false;
    
    /**
     * If set to true and the current state satisfies the hard objectives an empty list will be returned.
     */
    public boolean nothingIfGoal = false;
    
    /**
     * Already traversed transitions won't be returned.
     */
    public boolean untraversedOnly = false;
    
    /**
     * Only transitions with rules referenced by this list will be retrieved.
     */
    public List<DSETransformationRule<?, ?>> ruleFilter;

    /**
     * Will return an empty list if the current state dissatisfies the global constraints.
     * @return this
     */
    public FilterOptions nothingIfCut() {
        nothingIfCut = true;
        return this;
    }

    /**
     * Will return an empty list if the current state satisfies the hard objectives.
     * @return this
     */
    public FilterOptions nothingIfGoal() {
        nothingIfGoal = true;
        return this;
    }

    /**
     * Will return only untraversed transitions.
     * @return this
     */
    public FilterOptions untraversedOnly() {
        untraversedOnly = true;
        return this;
    }

    /**
     * Will return transitions derived from the given rule. Multiple rules can be given.
     * @param rule of the transitions to return
     * @return this
     */
    public FilterOptions withRuleFilter(DSETransformationRule<?, ?> rule) {
        if (ruleFilter == null) {
            ruleFilter = new ArrayList<DSETransformationRule<?, ?>>(1);
        }
        ruleFilter.add(rule);
        return this;
    }

    /**
     * Checks if the given rule is already in the filter list. If the filter list is empty, it will evaluate to true.
     * @param rule
     * @return True if it contains the rule or no rule was specified. False otherwise.
     */
    public boolean containsRule(DSETransformationRule<?, ?> rule) {
        if (ruleFilter == null || ruleFilter.contains(rule)) {
            return true;
        }
        return false;
    }
}