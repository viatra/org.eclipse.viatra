/*******************************************************************************
 * Copyright (c) 2010-2014, Marton Bur, Akos Horvath, Zoltan Ujhelyi, Istvan Rath and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Marton Bur - initial API and implementation
 *******************************************************************************/
package org.eclipse.incquery.tooling.localsearch.ui.debugger.provider;

import java.util.List;
import java.util.Map;

import org.eclipse.incquery.runtime.localsearch.matcher.LocalSearchMatcher;
import org.eclipse.incquery.runtime.localsearch.operations.IMatcherBasedOperation;
import org.eclipse.incquery.runtime.localsearch.operations.ISearchOperation;
import org.eclipse.incquery.runtime.localsearch.plan.SearchPlanExecutor;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

/**
 * Content provider class for the search plan tree viewer
 * 
 * @author Marton Bur
 *
 */
public class OperationListContentProvider implements ITreeContentProvider {

	private Map<LocalSearchMatcher,SearchPlanExecutor> matcherCurrentExecutorMappings = Maps.newHashMap();
	private OperationListLabelProvider operationListLabelProvider;
	private Map<Object, Object> dummyOperations = Maps.newHashMap();

	public Map<LocalSearchMatcher,SearchPlanExecutor> getMatcherCurrentExecutorMappings() {
		return matcherCurrentExecutorMappings;
	}


	@Override
	public void dispose() {
	}

	/**
	 * Initialiser method that is called after each top level search plan
	 */
	@Override
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {

	}

	@Override
	public Object[] getElements(Object inputElement) {
		if (inputElement instanceof SearchPlanExecutor) {
			List<Object> planToShow = null;
			if(inputElement != null){
				planToShow = createOperationsList(inputElement);
			}
			return planToShow.toArray(new Object[planToShow.size()]);
		}
		return new Object[0];
	}


	private List<Object> createOperationsList(Object inputElement) {
		List<Object> operationsToShow = Lists.newArrayList();
		
		List<ISearchOperation> plan = ((SearchPlanExecutor)inputElement).getSearchPlan().getOperations();
		operationsToShow.addAll(plan);
		// Final dummy operation
		
		Object dummyOperation = dummyOperations.get(inputElement);
		if(dummyOperation == null){
			dummyOperation = new Object();
			dummyOperations.put(inputElement, dummyOperation);
		}
		operationListLabelProvider.createDummyMatchOperationMapping(dummyOperation,(SearchPlanExecutor)inputElement);
		operationsToShow.add(dummyOperation);
		return operationsToShow;
	}

	@Override
	public Object[] getChildren(Object parentElement) {
		LocalSearchMatcher calledMatcher = null;
		if (parentElement instanceof IMatcherBasedOperation) {
			calledMatcher = ((IMatcherBasedOperation) parentElement).getCalledMatcher();
		}

		if(calledMatcher != null){
			SearchPlanExecutor searchPlanExecutor = matcherCurrentExecutorMappings.get(calledMatcher);
			List<Object> operations = createOperationsList(searchPlanExecutor);
			return operations.toArray(new Object[operations.size()]);
		}
		
		return null;
	}

	@Override
	public Object getParent(Object element) {
		return null;
	}

	@Override
	public boolean hasChildren(Object element) {
		LocalSearchMatcher calledMatcher = null;
		if (element instanceof IMatcherBasedOperation) {
			calledMatcher = ((IMatcherBasedOperation) element).getCalledMatcher();
		}
		return calledMatcher != null;
	}


	public void setLabelProvider(OperationListLabelProvider operationListLabelProvider) {
		this.operationListLabelProvider = operationListLabelProvider;
	}




}
