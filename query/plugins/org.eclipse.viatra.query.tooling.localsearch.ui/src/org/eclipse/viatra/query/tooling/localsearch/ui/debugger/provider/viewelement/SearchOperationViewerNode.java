/*******************************************************************************
 * Copyright (c) 2010-2015, Marton Bur, Zoltan Ujhelyi, Akos Horvath, Istvan Rath and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Marton Bur - initial API and implementation
 *******************************************************************************/
package org.eclipse.viatra.query.tooling.localsearch.ui.debugger.provider.viewelement;

import java.util.List;

import org.eclipse.viatra.query.runtime.localsearch.operations.IMatcherBasedOperation;
import org.eclipse.viatra.query.runtime.localsearch.operations.ISearchOperation;
import org.eclipse.viatra.query.runtime.localsearch.operations.check.NACOperation;
import org.eclipse.viatra.query.runtime.localsearch.operations.extend.CountOperation;
import org.eclipse.viatra.query.runtime.localsearch.operations.extend.ExtendOperation;
import org.eclipse.viatra.query.runtime.localsearch.plan.SearchPlanExecutor;
import org.eclipse.viatra.query.runtime.matchers.psystem.PVariable;

import com.google.common.collect.BiMap;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;

/**
 * Represents a search operation as a nodes in the search plan view
 * 
 * @author Marton Bur
 *
 */
public class SearchOperationViewerNode {

	private OperationKind operationKind;
	private OperationStatus operationStatus;
	private boolean breakpoint;
	private List<SearchOperationViewerNode> children;
	private SearchOperationViewerNode parent;
	private String labelText;
	private ISearchOperation searchOperation;
	private SearchPlanExecutor planExecutor;
	private boolean matcherBased;

	public SearchOperationViewerNode(SearchPlanExecutor planExecutor) {
		// Dummy operation for representing "match found"
		this.planExecutor = planExecutor;
		operationKind = OperationKind.MATCH;
		setup();
	}

	public SearchOperationViewerNode(ISearchOperation searchOperation, SearchPlanExecutor planExecutor) {
		this.searchOperation = searchOperation;
		this.planExecutor = planExecutor;
		if (searchOperation instanceof ExtendOperation<?>) {
			operationKind = OperationKind.EXTEND;
		} else if (searchOperation instanceof NACOperation) {
			operationKind = OperationKind.NAC;
		} else if (searchOperation instanceof CountOperation) {
			operationKind = OperationKind.COUNT;
		} else {
			// This case there is a check operation
			operationKind = OperationKind.CHECK;
		}
		// TODO For now toString() yields the obtainable type information, this might need some redesign work
		setup();
	}

	private void setup() {
		matcherBased = searchOperation == null ? false : searchOperation instanceof IMatcherBasedOperation;
		operationStatus = OperationStatus.QUEUED;
		children = Lists.newArrayList();

		if (searchOperation != null) {

			this.labelText = searchOperation.toString();

			this.labelText += "(";
			BiMap<Integer, PVariable> variableMapping = planExecutor.getVariableMapping();
			List<Integer> variablePositions = searchOperation.getVariablePositions();
			for (int i = 0; i < variablePositions.size(); i++) {
				PVariable pVariable = variableMapping.get(variablePositions.get(i));
				this.labelText += pVariable.getName();
				if (i != variablePositions.size() - 1) {
					this.labelText += ", ";
				}
			}
			this.labelText += ")";
		} else {
			this.labelText = "Match found";
		}

	}

	public OperationKind getOperationKind() {
		return operationKind;
	}

	public OperationStatus getOperationStatus() {
		return operationStatus;
	}

	public void setOperationStatus(OperationStatus operationStatus) {
		this.operationStatus = operationStatus;
	}

	public boolean isBreakpoint() {
		return breakpoint;
	}

	public void setBreakpoint(boolean breakpoint) {
		this.breakpoint = breakpoint;
	}

	public ImmutableList<SearchOperationViewerNode> getChildren() {
		return ImmutableList.copyOf(children);
	}

	public void addChild(SearchOperationViewerNode child) {
		children.add(child);
		child.parent = this;
	}

	public void addChildren(List<SearchOperationViewerNode> children) {
		this.children.addAll(children);
		for (SearchOperationViewerNode child : children) {
			child.parent = this;
		}
	}

	public void setChildren(List<SearchOperationViewerNode> children) {
		this.children = children;
		for (SearchOperationViewerNode child : children) {
			child.parent = this;
		}
	}

	public SearchOperationViewerNode getParent() {
		return parent;
	}

	public void setParent(SearchOperationViewerNode parent) {
		this.parent = parent;
		parent.addChild(this);
	}

	public String getLabelText() {
		return labelText;
	}

	public void setLabelText(String labelText) {
		this.labelText = labelText;
	}

	public ISearchOperation getSearchOperation() {
		return searchOperation;
	}

	public SearchPlanExecutor getPlanExecutor() {
		return planExecutor;
	}

	public boolean isMatcherBased() {
		return matcherBased;
	}


}
