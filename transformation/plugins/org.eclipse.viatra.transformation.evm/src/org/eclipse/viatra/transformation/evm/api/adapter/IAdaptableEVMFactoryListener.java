/*******************************************************************************
 * Copyright (c) 2010-2016, Peter Lunk and IncQueryLabs Ltd.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Peter Lunk - initial API and implementation
 *******************************************************************************/
package org.eclipse.viatra.transformation.evm.api.adapter;

import java.util.List;

public interface IAdaptableEVMFactoryListener {
    public void adaptableEVMPoolChanged(List<AdaptableEVM> adaptableEVM);
}
