/*******************************************************************************
 * Copyright (c) 2014-2016 Robert Doczi, IncQuery Labs Ltd.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Robert Doczi - initial API and implementation
 *******************************************************************************/
package org.eclipse.viatra.query.tooling.cpp.localsearch.api

import org.eclipse.viatra.query.tooling.cpp.localsearch.model.QueryDescriptor

/**
 * @author Robert Doczi
 */
interface ILocalsearchGeneratorOutputProvider extends IGeneratorOutputProvider {
	
	def void initialize(QueryDescriptor query)
	
}