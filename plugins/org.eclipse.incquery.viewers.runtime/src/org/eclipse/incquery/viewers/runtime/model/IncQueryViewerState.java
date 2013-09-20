/*******************************************************************************
 * Copyright (c) 2010-2013, Zoltan Ujhelyi, Istvan Rath and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Zoltan Ujhelyi, Istvan Rath - initial API and implementation
 *******************************************************************************/
package org.eclipse.incquery.viewers.runtime.model;

import java.util.Collection;

import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.incquery.patternlanguage.patternLanguage.Pattern;
import org.eclipse.incquery.runtime.api.IncQueryEngine;

/**
 * IncQuery-specific implementation of the {@link ViewerState}.
 * 
 * Accept IncQuery-specific sources for filling the ViewerState with contents.
 * 
 * @author istvanrath
 *
 */
public abstract class IncQueryViewerState extends ViewerState {

    // XXX Abel switch between set-list here
	protected static boolean setMode = false;
	
	/* factory method */
	
	public static ViewerState newInstance(ResourceSet set, IncQueryEngine engine,
			Collection<Pattern> patterns, ViewerDataFilter filter,
			Collection<ViewerStateFeature> features) {
		if (setMode)
			return new ViewerStateSet(set, engine, patterns, filter, features);
		else
			return new IncQueryViewerStateList(set, engine, patterns, filter, features);		
	}
	
	public static ViewerState newInstance(ViewerDataModel model, ViewerDataFilter filter,
			Collection<ViewerStateFeature> features)
	{
		IncQueryViewerState s = null;
		if (setMode)
			s = new ViewerStateSet(model, filter, features);
		else
			s = new ViewerStateList(model, filter, features);
		
		s.hasExternalViewerDataModel=true;
		return s;
	}
	
	
}
