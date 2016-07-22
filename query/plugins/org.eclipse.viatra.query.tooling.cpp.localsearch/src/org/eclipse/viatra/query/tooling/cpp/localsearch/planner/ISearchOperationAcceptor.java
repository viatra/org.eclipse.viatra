/*******************************************************************************
 * Copyright (c) 2014-2016 Robert Doczi, IncQuery Labs Ltd.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Robert Doczi - initial API and implementation
 *******************************************************************************/
package org.eclipse.viatra.query.tooling.cpp.localsearch.planner;

import java.util.Map;
import java.util.Set;

import org.eclipse.viatra.query.runtime.matchers.context.IInputKey;
import org.eclipse.viatra.query.runtime.matchers.planning.SubPlan;
import org.eclipse.viatra.query.runtime.matchers.psystem.PConstraint;
import org.eclipse.viatra.query.runtime.matchers.psystem.PVariable;
import org.eclipse.viatra.query.runtime.matchers.psystem.queries.PParameter;
import org.eclipse.viatra.query.runtime.matchers.psystem.queries.PQuery;

/**
 * @author Robert Doczi
 */
public interface ISearchOperationAcceptor {
	
	public void initialize(SubPlan plan, Map<PVariable, Integer> variableMapping, Map<PConstraint, Set<Integer>> variableBindings);
	
	public void acceptContainmentCheck(PVariable sourceVariable, PVariable targetVariable, IInputKey inputKey);
	public void acceptInstanceOfClassCheck(PVariable checkedVariable, IInputKey inputKey);
	
	public void acceptIterateOverClassInstances(PVariable location, IInputKey inputKey);
	public void acceptExtendToAssociationSource(PVariable sourceVariable, PVariable targetVariable, IInputKey inputKey);
	public void acceptExtendToAssociationTarget(PVariable sourceVariable, PVariable targetVariable, IInputKey inputKey);
	public void acceptNACOperation(PQuery calledPQuery, Set<PVariable> boundVariables, Set<PParameter> boundParameters);
}
