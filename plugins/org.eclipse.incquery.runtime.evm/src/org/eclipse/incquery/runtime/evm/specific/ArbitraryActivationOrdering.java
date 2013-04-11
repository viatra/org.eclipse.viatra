/*******************************************************************************
 * Copyright (c) 2010-2013, Abel Hegedus, Istvan Rath and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Abel Hegedus - initial API and implementation
 *******************************************************************************/
package org.eclipse.incquery.runtime.evm.specific;

import java.util.Set;

import org.eclipse.incquery.runtime.evm.api.Activation;
import org.eclipse.incquery.runtime.evm.api.IActivationOrdering;

import com.google.common.collect.Sets;

/**
 * @author Abel Hegedus
 *
 */
public class ArbitraryActivationOrdering implements IActivationOrdering {

    @Override
    public Set<Activation<?>> createActivationContainer() {
        return Sets.newHashSet();
    }

    @Override
    public boolean removeActivationOnChange() {
        return false;
    }

    
    
}
