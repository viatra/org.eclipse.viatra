/*******************************************************************************
 * Copyright (c) 2010-2016, Abel Hegedus, IncQuery Labs Ltd.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-v20.html.
 * 
 * SPDX-License-Identifier: EPL-2.0
 *******************************************************************************/
package org.eclipse.viatra.query.tooling.ui.util;

import org.eclipse.viatra.query.runtime.ui.modelconnector.IModelConnector;

/**
 * This interface is used to notify model connector users about
 * changes in the connected model source.
 * 
 * @author Abel Hegedus
 *
 */
public interface IModelConnectorListener {

    void modelUnloaded(IModelConnector modelConnector);
    
}
