/*******************************************************************************
 * Copyright (c) 2010-2013, Zoltan Ujhelyi, Istvan Rath and Daniel Varro
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-v20.html.
 * 
 * SPDX-License-Identifier: EPL-2.0
 *******************************************************************************/

package org.eclipse.viatra.addon.viewers.tooling.ui.handlers;

import org.eclipse.viatra.query.runtime.api.IModelConnectorTypeEnum;

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
