/*******************************************************************************
 * Copyright (c) 2010-2014, Bergmann Gabor, Istvan Rath and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Bergmann Gabor - initial API and implementation
 *******************************************************************************/
package org.eclipse.viatra.query.runtime.matchers.psystem.queries;

import org.eclipse.viatra.query.runtime.matchers.planning.QueryProcessingException;

/**
 * Represents an error that was detected while the {@link PQuery} object was built from a source.
 * @author Bergmann Gabor
 *
 */
public class PProblem {

	private final String shortMessage;
	private final Exception exception;

	public PProblem(String shortMessage) {
		this(null, shortMessage);
	}
	public PProblem(QueryProcessingException exception) {
		this(exception, exception.getShortMessage());
	}
	public PProblem(Exception exception, String shortMessage) {
		super();
		this.shortMessage = shortMessage;
		this.exception = exception;
	}

	public String getShortMessage() {
		return shortMessage;
	}
	public Exception getException() {
		return exception;
	}
	
	
	
}
