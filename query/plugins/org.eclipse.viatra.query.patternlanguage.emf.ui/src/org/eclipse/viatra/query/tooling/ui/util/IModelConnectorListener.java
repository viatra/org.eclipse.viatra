/*******************************************************************************
 * Copyright (c) 2010-2016, Abel Hegedus, IncQuery Labs Ltd.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Abel Hegedus - initial API and implementation
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
