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
package org.eclipse.incquery.databinding.runtime.util;

import org.eclipse.incquery.databinding.runtime.adapter.DatabindingAdapter;
import org.eclipse.incquery.databinding.runtime.api.IncQueryObservables;
import org.eclipse.incquery.runtime.api.IPatternMatch;
import org.eclipse.incquery.runtime.api.IQuerySpecification;

/**
 * @author istvanrath
 * @deprecated functionality moved to {@link IncQueryObservables}
 */
public class DatabindingUtil {

     /**
     * @deprecated Use {@link IncQueryObservables#OBSERVABLEVALUE_ANNOTATION} instead
     */
    public static final String OBSERVABLEVALUE_ANNOTATION = IncQueryObservables.OBSERVABLEVALUE_ANNOTATION;

    /**
     * @deprecated Use {@link IncQueryObservables#getDatabindingAdapter(IQuerySpecification<?>)} instead
     */
    public static DatabindingAdapter<IPatternMatch> getDatabindingAdapter(IQuerySpecification<?> query) {
        return IncQueryObservables.getDatabindingAdapter(query);
    }

}
