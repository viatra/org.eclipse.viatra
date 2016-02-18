/*******************************************************************************
 * Copyright (c) 2010-2013, Zoltan Ujhelyi, Istvan Rath and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Zoltan Ujhelyi - initial API and implementation
 *   Csaba Debreceni - remove support for observable collections
 *******************************************************************************/
package org.eclipse.viatra.addon.viewers.runtime.model;

import java.util.Collection;
import java.util.Set;

import org.apache.log4j.Logger;
import org.eclipse.viatra.addon.viewers.runtime.model.ViewerState.ViewerStateFeature;
import org.eclipse.viatra.query.runtime.api.IQuerySpecification;
import org.eclipse.viatra.query.runtime.api.ViatraQueryEngine;
import org.eclipse.viatra.query.runtime.base.exception.ViatraBaseException;
import org.eclipse.viatra.query.runtime.exception.ViatraQueryException;
import org.eclipse.viatra.query.runtime.util.ViatraQueryLoggingUtil;

import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import com.google.common.collect.Sets;

/**
 * A {@link ViewerDataModel} implementation for VIATRA Query/EVM model sources.
 * 
 * If you instantiate this class yourself, then be sure to dispose() of it once it is not needed anymore.
 * 
 * @author Zoltan Ujhelyi
 * @author Istvan Rath
 * @author Csaba Debreceni
 * 
 */
public class ViatraViewerDataModel extends ViewerDataModel {
    private ViatraQueryEngine engine;
    private Logger logger;
    private Set<IQuerySpecification<?>> patterns;
    
    /**
     * Initializes a Viewer Data model using a set of patterns and a selected engine.
     * 
     * @param patterns
     * @param engine
     * @throws ViatraQueryException
     * @throws ViatraBaseException
     */
    public ViatraViewerDataModel(Collection<IQuerySpecification<?>> patterns, ViatraQueryEngine engine) throws ViatraQueryException, ViatraBaseException {
    	super(engine);
        this.patterns = Sets.newHashSet(patterns);
        this.engine = engine;
        this.logger = ViatraQueryLoggingUtil.getLogger(getClass());
    }

    @Override
    public ViatraQueryEngine getEngine() {
        return engine;
    }

	public Collection<IQuerySpecification<?>> getPatterns(final String annotation) {
		return Collections2.filter(patterns, new Predicate<IQuerySpecification<?>>() {

			@Override
			public boolean apply(IQuerySpecification<?> pattern) {
				return !pattern.getAnnotationsByName(annotation).isEmpty();
			}
		});
	}

    public Logger getLogger() {
        return logger;
    }

    /**
     * @deprecated Due to independence from databinding
     * Dispose of this {@link ViatraViewerDataModel} instance.
     */
    @Deprecated
    @Override
    public void dispose(){
    }
    
    /* factory method */
 	
 	/**
 	 * Instantiate a {@link ViewerState} and its corresponding {@link ViewerDataModel} instance, for VIATRA-based 
 	 * model sources.
 	 * 
 	 * When the state is disposed, the model will be disposed too.
 	 * 
 	 * @param set
 	 * @param engine
 	 * @param patterns
 	 * @param filter
 	 * @param features
 	 * @return
 	 */
 	public static ViewerState newViewerState(ViatraQueryEngine engine,
 			Collection<IQuerySpecification<?>> patterns, ViewerDataFilter filter,
 			Collection<ViewerStateFeature> features) {
 		ViatraViewerDataModel m;
		try {
			m = new ViatraViewerDataModel(patterns, engine);
		} catch (ViatraQueryException e) {
			Logger.getLogger(ViatraViewerDataModel.class).error(e.getMessage());
			return null;
		} catch (ViatraBaseException e) {
			Logger.getLogger(ViatraViewerDataModel.class).error(e.getMessage());
			return null;
		}
  		ViewerState r = newViewerState(m, filter, features);
  		r.hasExternalViewerDataModel=false;
  		return r;
 	}
 	
 	/**
 	 * Instantiate a new {@link ViewerState}, for an already existing {@link ViatraViewerDataModel} instance.
 	 * 
 	 * This {@link ViewerDataModel} will not be disposed when the state is disposed.
 	 * 
 	 * @param model
 	 * @param filter
 	 * @param features
 	 * @return
 	 */
 	public static ViewerState newViewerState(ViatraViewerDataModel model, ViewerDataFilter filter,
 			Collection<ViewerStateFeature> features)
 	{
 		ViewerState s = new ViewerState(model, filter, features);
 		s.hasExternalViewerDataModel=true;
 		return s;
 	}

	@Override
	public Collection<IQuerySpecification<?>> getPatterns() {
		return patterns;
	}
}
