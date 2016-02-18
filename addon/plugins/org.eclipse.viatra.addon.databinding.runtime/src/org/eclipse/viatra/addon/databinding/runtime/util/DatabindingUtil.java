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
package org.eclipse.viatra.addon.databinding.runtime.util;

import org.eclipse.viatra.addon.databinding.runtime.adapter.DatabindingAdapter;
import org.eclipse.viatra.addon.databinding.runtime.api.ViatraObservables;
import org.eclipse.viatra.query.runtime.api.IPatternMatch;
import org.eclipse.viatra.query.runtime.api.IQuerySpecification;

/**
 * @author istvanrath
 * @deprecated functionality moved to {@link ViatraObservables}
 */
public class DatabindingUtil {

    /**
     * @deprecated Use {@link ViatraObservables#getDatabindingAdapter(IQuerySpecification) } instead
     */
    public static DatabindingAdapter<IPatternMatch> getDatabindingAdapter(IQuerySpecification<?> query) {
        return ViatraObservables.getDatabindingAdapter(query);
    }

}
