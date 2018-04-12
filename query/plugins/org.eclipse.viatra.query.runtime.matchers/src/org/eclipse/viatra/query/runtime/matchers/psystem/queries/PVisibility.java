/*******************************************************************************
 * Copyright (c) 2010-2017, Zoltan Ujhelyi, IncQuery Labs Ltd.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Zoltan Ujhelyi - initial API and implementation
 *******************************************************************************/
package org.eclipse.viatra.query.runtime.matchers.psystem.queries;

/**
 * @author Zoltan Ujhelyi
 * @since 2.0
 *
 */
public enum PVisibility {

    /**
     * A public (default) visibility means a pattern can be called at any time.
     */
    PUBLIC,
    /**
     * A private query is not expected to be called directly, only by a different query matcher. 
     */
    PRIVATE,
    /**
     * A query that is only used inside a single caller query and is invisible outside.
     */
    EMBEDDED

}
