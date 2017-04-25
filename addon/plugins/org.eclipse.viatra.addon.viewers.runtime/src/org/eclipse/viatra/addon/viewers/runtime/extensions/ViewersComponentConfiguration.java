/*******************************************************************************
 * Copyright (c) 2010-2013, istvanrath, Istvan Rath and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   istvanrath - initial API and implementation
 *******************************************************************************/
package org.eclipse.viatra.addon.viewers.runtime.extensions;

import java.util.ArrayList;
import java.util.Collection;

import org.eclipse.core.runtime.Assert;
import org.eclipse.viatra.addon.viewers.runtime.model.ViewerDataFilter;
import org.eclipse.viatra.query.runtime.api.IQuerySpecification;
import org.eclipse.viatra.query.runtime.emf.EMFScope;
import org.eclipse.viatra.query.runtime.registry.IQuerySpecificationRegistry;
import org.eclipse.viatra.query.runtime.registry.QuerySpecificationRegistry;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

/**
 * Configuration DTO for VIATRA Viewers components.
 * 
 * @author istvanrath
 *
 */
public class ViewersComponentConfiguration
{
	private EMFScope scope;
	private Collection<IQuerySpecification<?>> patterns;
	private ViewerDataFilter filter;
	
	private ViewersComponentConfiguration() {
		this.scope = null;
		this.patterns = Sets.newHashSet();
		this.filter = ViewerDataFilter.UNFILTERED;
	}
	
	public ViewersComponentConfiguration(EMFScope _scope, Collection<IQuerySpecification<?>> _patterns, ViewerDataFilter _filter) {
		Assert.isNotNull(_scope);
		Assert.isNotNull(_patterns);
		Assert.isNotNull(_filter);
		this.setModel(_scope);
		this.setPatterns(_patterns);
		this.setFilter(_filter);
	}
	
	public ViewersComponentConfiguration(EMFScope _model, Collection<IQuerySpecification<?>> _patterns) {
		this(_model,_patterns,ViewerDataFilter.UNFILTERED);
	}
	
	public static ViewersComponentConfiguration fromQuerySpecs(Collection<? extends IQuerySpecification<?>> specs)
	{
		ViewersComponentConfiguration c = new ViewersComponentConfiguration();
		for (IQuerySpecification<?> spec : specs) {
			c.patterns.add(spec);
		}
		return c;
	}
	
	public static ViewersComponentConfiguration fromQuerySpecFQNs(Collection<String> fqns)
	{
		ViewersComponentConfiguration c = new ViewersComponentConfiguration();
		for (String fqn : fqns) {
		    IQuerySpecificationRegistry registry = QuerySpecificationRegistry.getInstance();
			IQuerySpecification<?> querySpecification = registry.getDefaultView().getEntry(fqn).get();
			c.patterns.add(querySpecification);
		}
		return c;
	}
	
	/**
	 * @return the model
	 */
	public EMFScope getScope() {
		return scope;
	}
	/**
	 * @param model the model to set
	 */
	public void setModel(EMFScope model) {
		this.scope = model;
	}
	/**
	 * @return the patterns
	 */
	public Collection<IQuerySpecification<?>> getPatterns() {
		return patterns;
	}
	/**
	 * @param patterns the patterns to set
	 */
	public void setPatterns(Collection<IQuerySpecification<?>> patterns) {
		this.patterns = patterns;
	}
	/**
	 * @return the filter
	 */
	public ViewerDataFilter getFilter() {
		return filter;
	}
	/**
	 * @param filter the filter to set
	 */
	public void setFilter(ViewerDataFilter filter) {
		this.filter = filter;
	}
	public ViewersComponentConfiguration newCopy() {
		ArrayList<IQuerySpecification<?>> r = Lists.newArrayList();
		r.addAll(getPatterns());
		// TODO proper copy support for filters
		return new ViewersComponentConfiguration(getScope(), r, getFilter());
	}
}