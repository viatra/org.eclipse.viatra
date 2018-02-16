/*******************************************************************************
 * Copyright (c) 2010-2018, Gabor Bergmann, IncQuery Labs Ltd.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Gabor Bergmann - initial API and implementation
 *******************************************************************************/
package org.eclipse.viatra.query.runtime.matchers.scopes.tables;

import org.eclipse.viatra.query.runtime.matchers.context.IInputKey;
import org.eclipse.viatra.query.runtime.matchers.context.IQueryMetaContext;
import org.eclipse.viatra.query.runtime.matchers.context.IQueryRuntimeContext;

/**
 * Callbacks that {@link IIndexTable} implementations are expected to invoke on their environment.
 * 
 * @since 2.0
 * @author Gabor Bergmann
 */
public interface ITableContext {

    // TODO notifications?

    /**
     * Indicates that an error has occurred in maintaining an index table, e.g. duplicate value.
     */
    public void logError(String message);
}
