/*******************************************************************************
 * Copyright (c) 2010-2016, Peter Lunk and IncQueryLabs Ltd.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-v20.html.
 * 
 * SPDX-License-Identifier: EPL-2.0
 *******************************************************************************/
package org.eclipse.viatra.transformation.evm.api.adapter;

import java.util.List;

public interface IAdaptableEVMFactoryListener {
    public void adaptableEVMPoolChanged(List<AdaptableEVM> adaptableEVM);
}
