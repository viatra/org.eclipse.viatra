/*******************************************************************************
 * Copyright (c) 2004-2015, Peter Lunk, Zoltan Ujhelyi and Daniel Varro
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-v20.html.
 * 
 * SPDX-License-Identifier: EPL-2.0
 *******************************************************************************/
package org.eclipse.viatra.transformation.evm.api.adapter;

import java.util.List;

/**
 * Interface that defines the methods of VIATRA transformation adapter configurations. Adapter configurations can
 * contain {@link IEVMListener} and {@link IEVMAdapter} objects
 * 
 * @author Peter Lunk
 */
public interface IAdapterConfiguration {
    public List<IEVMListener> getListeners();

    public List<IEVMAdapter> getAdapters();
}
