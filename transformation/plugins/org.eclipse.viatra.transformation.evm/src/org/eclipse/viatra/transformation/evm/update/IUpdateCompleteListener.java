/*******************************************************************************
 * Copyright (c) 2010-2012, Abel Hegedus, Istvan Rath and Daniel Varro
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-v20.html.
 * 
 * SPDX-License-Identifier: EPL-2.0
 *******************************************************************************/
package org.eclipse.viatra.transformation.evm.update;

/**
 * This interface is used for listening to update complete events sent by an {@link IUpdateCompleteProvider}.
 * 
 * @author Abel Hegedus
 * 
 */
public interface IUpdateCompleteListener {

    /**
     * This method is called when an update complete event occurs.
     */
    void updateComplete();

}
