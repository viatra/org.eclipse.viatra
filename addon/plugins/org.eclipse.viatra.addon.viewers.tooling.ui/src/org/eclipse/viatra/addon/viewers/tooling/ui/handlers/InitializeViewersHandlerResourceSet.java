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

package org.eclipse.viatra.addon.viewers.tooling.ui.handlers;

import org.eclipse.viatra.query.runtime.api.IModelConnectorTypeEnum;


/**
 * Handler class to initialize the sandbox viewer.
 * 
 * @author Istvan Rath
 * 
 */
public class InitializeViewersHandlerResourceSet extends InitializeViewersHandler {

	public InitializeViewersHandlerResourceSet() {
		super(IModelConnectorTypeEnum.RESOURCESET);
	}

}
