/*******************************************************************************
 * Copyright (c) 2010-2014, Csaba Debreceni, Istvan Rath and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Csaba Debreceni - initial API and implementation
 *******************************************************************************/
package org.eclipse.viatra.addon.viewers.runtime.model;

import java.util.Collection;
import java.util.List;

import org.eclipse.viatra.query.runtime.api.IPatternMatch;
import org.eclipse.viatra.transformation.evm.api.event.EventFilter;
import org.eclipse.viatra.transformation.evm.specific.Rules;
import org.eclipse.viatra.transformation.views.traceablilty.generic.GenericReferencedQuerySpecification;

import com.google.common.collect.Lists;

/**
 * @author Csaba Debreceni
 *
 */
public final class EventFilterBuilder {

	//Disable constructor
	private EventFilterBuilder(){}
	
	@SuppressWarnings("unchecked")
	public static <T extends IPatternMatch> EventFilter<T> createEventFilter(ViewerFilterDefinition filterDefinition, GenericReferencedQuerySpecification specification) {
		if(filterDefinition.singleFilterMatch != null) {
			IPatternMatch singleFilterMatch = filterDefinition.singleFilterMatch;
			T newSingleFilterMatch = (T) specification.createFromBaseMatch(singleFilterMatch);
			return Rules.newSingleMatchFilter(newSingleFilterMatch);
		}
		
		Collection<IPatternMatch> filterMatches = filterDefinition.filterMatches;
		List<T> newFilterMatches = Lists.newArrayList();
		for (IPatternMatch filterMatch : filterMatches) {
			T newFilterMatch = (T) specification.createFromBaseMatch(filterMatch);
			newFilterMatches.add(newFilterMatch);
		}
		return Rules.newMultiMatchFilter(newFilterMatches, filterDefinition.semantics);
	}
}
