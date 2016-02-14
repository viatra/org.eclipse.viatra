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
import org.eclipse.emf.common.notify.Notifier;
import org.eclipse.viatra.addon.viewers.runtime.model.ViewerDataFilter;
import org.eclipse.viatra.query.runtime.api.IPatternMatch;
import org.eclipse.viatra.query.runtime.api.IQuerySpecification;
import org.eclipse.viatra.query.runtime.api.ViatraQueryMatcher;
import org.eclipse.viatra.query.runtime.extensibility.QuerySpecificationRegistry;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

/**
 * Configuration DTO for IncQuery Viewers components.
 * 
 * @author istvanrath
 *
 */
public class ViewersComponentConfiguration
{
	private Notifier model;
	private Collection<IQuerySpecification<?>> patterns;
	private ViewerDataFilter filter;
	
	private ViewersComponentConfiguration() {
		this.model = null;
		this.patterns = Sets.newHashSet();
		this.filter = ViewerDataFilter.UNFILTERED;
	}
	
	public ViewersComponentConfiguration(Notifier _model, Collection<IQuerySpecification<?>> _patterns, ViewerDataFilter _filter) {
		Assert.isNotNull(_model);
		Assert.isNotNull(_patterns);
		Assert.isNotNull(_filter);
		this.setModel(_model);
		this.setPatterns(_patterns);
		this.setFilter(_filter);
	}
	
	public ViewersComponentConfiguration(Notifier _model, Collection<IQuerySpecification<?>> _patterns) {
		this(_model,_patterns,ViewerDataFilter.UNFILTERED);
	}
	
	/**
	 * TODO this does not seem to be usable at the moment
	 * 
	 * Java generics bugs?
	 * 
	 * @param specs
	 * @return
	 */
	public static ViewersComponentConfiguration fromQuerySpecs(Collection<IQuerySpecification<ViatraQueryMatcher<? extends IPatternMatch>>> specs)
	{
		ViewersComponentConfiguration c = new ViewersComponentConfiguration();
		for (IQuerySpecification<ViatraQueryMatcher<? extends IPatternMatch>> spec : specs) {
			c.patterns.add(spec);
		}
		return c;
	}
	
	public static ViewersComponentConfiguration fromQuerySpecFQNs(Collection<String> fqns)
	{
		ViewersComponentConfiguration c = new ViewersComponentConfiguration();
		for (String fqn : fqns) {
			c.patterns.add( QuerySpecificationRegistry.getQuerySpecification(fqn));
		}
		return c;
	}
	
	/**
	 * @return the model
	 */
	public Notifier getModel() {
		return model;
	}
	/**
	 * @param model the model to set
	 */
	public void setModel(Notifier model) {
		this.model = model;
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
		return new ViewersComponentConfiguration(getModel(), r, getFilter());
	}
}