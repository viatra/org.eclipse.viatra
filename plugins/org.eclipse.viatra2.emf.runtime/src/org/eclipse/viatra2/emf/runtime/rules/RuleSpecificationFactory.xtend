/*******************************************************************************
 * Copyright (c) 2004-2013, Abel Hegedus and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Abel Hegedus - initial API and implementation
 *******************************************************************************/
package org.eclipse.viatra2.emf.runtime.rules

import java.util.Set
import org.eclipse.incquery.runtime.api.IPatternMatch
import org.eclipse.incquery.runtime.api.IncQueryMatcher
import org.eclipse.incquery.runtime.evm.api.ActivationLifeCycle
import org.eclipse.incquery.runtime.evm.api.Job
import org.eclipse.incquery.runtime.evm.api.RuleSpecification
import org.eclipse.incquery.runtime.evm.specific.Rules
import org.eclipse.incquery.runtime.api.IQuerySpecification

/**
 * Wrapper class for transformation rule specification, users can override the abstract methods 
 * to return the required parameters of rule specification creation.
 * 
 * @author Abel Hegedus
 *
 */
abstract class RuleSpecificationFactory<Match extends IPatternMatch, Matcher extends IncQueryMatcher<Match>> {

	protected String ruleName
	
	RuleSpecification<Match> ruleSpec

	def protected ActivationLifeCycle getLifeCycle()
    
    def protected IQuerySpecification<Matcher> getQuerySpecification()
    
    def protected Set<Job<Match>> getJobs(){
        emptySet()
    }
    
    def getRuleName() {
    	ruleName
    }

	/**
	 * Returns a RuleSpecification that can be added to a rule engine.
	 */
    def getRuleSpec(){
    	if(ruleSpec == null){
		    val querySpec = getQuerySpecification
		    ruleSpec = Rules::newMatcherRuleSpecification(querySpec, getLifeCycle, getJobs)
    	}
    	ruleSpec
    }
    
	
}