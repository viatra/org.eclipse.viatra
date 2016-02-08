/*******************************************************************************
 * Copyright (c) 2010-2014, Zoltan Ujhelyi, Istvan Rath and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Zoltan Ujhelyi - initial API and implementation
 *******************************************************************************/
package org.eclipse.incquery.runtime.base.api.filters;

import org.eclipse.emf.common.notify.Notifier;

/**
 * 
 * Stores a collection of {@link Notifier} instances that need not to be indexed by IncQuery Base.
 * 
 * @author Zoltan Ujhelyi
 * 
 */
public interface IBaseIndexObjectFilter {

    /**
     * Decides whether the selected notifier is filtered.
     * 
     * @param notifier
     * @return true, if the notifier should not be indexed
     */
    boolean isFiltered(Notifier notifier);

}