/*******************************************************************************
 * Copyright (c) 2010-2014, Zoltan Ujhelyi, Istvan Rath and Daniel Varro
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-v20.html.
 * 
 * SPDX-License-Identifier: EPL-2.0
 *******************************************************************************/
package org.eclipse.viatra.query.runtime.matchers.psystem;

import org.eclipse.viatra.query.runtime.matchers.psystem.queries.PQuery;


/**
 * @author Zoltan Ujhelyi
 *
 */
public interface IQueryReference {

    PQuery getReferredQuery();
}
