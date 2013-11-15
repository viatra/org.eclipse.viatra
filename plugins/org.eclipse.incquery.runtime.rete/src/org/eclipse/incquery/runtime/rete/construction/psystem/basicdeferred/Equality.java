/*******************************************************************************
 * Copyright (c) 2004-2010 Gabor Bergmann and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Gabor Bergmann - initial API and implementation
 *******************************************************************************/

package org.eclipse.incquery.runtime.rete.construction.psystem.basicdeferred;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.eclipse.incquery.runtime.rete.collections.CollectionsFactory;
import org.eclipse.incquery.runtime.rete.construction.SubPlan;
import org.eclipse.incquery.runtime.rete.construction.psystem.DeferredPConstraint;
import org.eclipse.incquery.runtime.rete.construction.psystem.PSystem;
import org.eclipse.incquery.runtime.rete.construction.psystem.PVariable;

/**
 * @author Gabor Bergmann
 * 
 */
public class Equality extends DeferredPConstraint {

    private PVariable who;
    private PVariable withWhom;

    /**
     * @param buildable
     * @param affectedVariables
     */
    public Equality(PSystem pSystem, PVariable who, PVariable withWhom) {
        super(pSystem, buildSet(who, withWhom));
        this.who = who;
        this.withWhom = withWhom;
    }

    private static Set<PVariable> buildSet(PVariable who, PVariable withWhom) {
        Set<PVariable> set = CollectionsFactory.getSet();//new HashSet<PVariable>();
        set.add(who);
        set.add(withWhom);
        return set;
    }

    public boolean isMoot() {
        return who.equals(withWhom);
    }

    @Override
    public void doReplaceVariable(PVariable obsolete, PVariable replacement) {
        if (obsolete.equals(who))
            who = replacement;
        if (obsolete.equals(withWhom))
            withWhom = replacement;
    }

    @Override
    protected String toStringRest() {
        return who.getName() + "=" + withWhom.getName();
    }

    /**
     * @return the who
     */
    public PVariable getWho() {
        return who;
    }

    /**
     * @return the withWhom
     */
    public PVariable getWithWhom() {
        return withWhom;
    }

    @Override
    public Set<PVariable> getDeducedVariables() {
        return Collections.emptySet();
    }
    
    /* (non-Javadoc)
     * @see org.eclipse.incquery.runtime.rete.construction.psystem.BasePConstraint#getFunctionalKeys()
     */
    @Override
    public Map<Set<PVariable>, Set<PVariable>> getFunctionalDependencies() {
    	final HashMap<Set<PVariable>, Set<PVariable>> result = new HashMap<Set<PVariable>, Set<PVariable>>();
    	result.put(Collections.singleton(who), Collections.singleton(withWhom));
    	result.put(Collections.singleton(withWhom), Collections.singleton(who));
		return result;
    }

    @Override
    public boolean isReadyAt(SubPlan olan) {
        return olan.getVariablesIndex().containsKey(who) && olan.getVariablesIndex().containsKey(withWhom);
        // will be replaced by || if copierNode is available;
        // until then, LayoutHelper.unifyVariablesAlongEqualities(PSystem<PatternDescription, StubHandle, Collector>) is
        // recommended.
    }
}
