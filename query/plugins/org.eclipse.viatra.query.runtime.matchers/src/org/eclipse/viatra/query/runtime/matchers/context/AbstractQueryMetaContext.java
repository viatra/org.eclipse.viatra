/*******************************************************************************
 * Copyright (c) 2010-2016, Grill Balázs, IncQuery Labs Ltd.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Grill Balázs - initial API and implementation
 *******************************************************************************/
package org.eclipse.viatra.query.runtime.matchers.context;

/**
 * Common abstract class for implementers of {@link IQueryMetaContext}
 * 
 * @author Grill Balázs
 * @since 1.3
 *
 */
public abstract class AbstractQueryMetaContext implements IQueryMetaContext {

	@Override
	public boolean canLeadOutOfScope(IInputKey key) {
		return key.getArity() > 1;
	}
	
}
