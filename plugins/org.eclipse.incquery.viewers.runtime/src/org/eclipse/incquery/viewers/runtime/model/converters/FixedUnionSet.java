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
package org.eclipse.incquery.viewers.runtime.model.converters;

import java.util.Set;

import org.eclipse.core.databinding.observable.set.ComputedSet;
import org.eclipse.core.databinding.observable.set.IObservableSet;
import org.eclipse.core.databinding.observable.set.UnionSet;

import com.google.common.collect.Sets;

/**
 * Computed set implementation that works around the bugs in {@link UnionSet}.
 * 
 * For now, this is an inefficient implementation, as on every compute, it iterates through all of the 
 * sources and completely recomputes the result.
 * 
 * TODO improve performance by incremental update handling
 * 
 * @author Istvan Rath
 *
 */
public class FixedUnionSet extends ComputedSet {

	IObservableSet[] sets;
	
	/**
	 * 
	 */
	public FixedUnionSet(IObservableSet[] sets) {
		this.sets = sets;
	}
	
	
	/* (non-Javadoc)
	 * @see org.eclipse.core.databinding.observable.set.ComputedSet#calculate()
	 */
	@Override
	protected Set<?> calculate() {
		Set<?> r = Sets.newHashSet();
		for (IObservableSet s : sets) {
			r.addAll(s);
		}
		return r;
	}

}
