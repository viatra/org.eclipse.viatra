/*******************************************************************************
 * Copyright (c) 2010-2013, Zoltan Ujhelyi, Istvan Rath and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Istvan Rath - initial API and implementation
 *******************************************************************************/

package org.eclipse.incquery.viewers.tooling.ui.handlers;

import org.eclipse.incquery.runtime.api.IModelConnectorTypeEnum;

/**
 * Temporary handler class to initialize the sandbox viewer.
 * 
 * @author Istvan Rath
 * 
 */
public class InitializeViewersHandlerResource extends InitializeViewersHandler
{
	public InitializeViewersHandlerResource() {
		super(IModelConnectorTypeEnum.RESOURCE);
	}

}
