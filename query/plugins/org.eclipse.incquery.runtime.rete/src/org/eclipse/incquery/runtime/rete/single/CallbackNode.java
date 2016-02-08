/*******************************************************************************
 * Copyright (c) 2010-2012, Bergmann Gabor, Istvan Rath and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Bergmann Gabor - initial API and implementation
 *******************************************************************************/
package org.eclipse.incquery.runtime.rete.single;

import org.eclipse.incquery.runtime.matchers.backend.IUpdateable;
import org.eclipse.incquery.runtime.matchers.tuple.Tuple;
import org.eclipse.incquery.runtime.rete.misc.SimpleReceiver;
import org.eclipse.incquery.runtime.rete.network.Direction;
import org.eclipse.incquery.runtime.rete.network.ReteContainer;

/**
 * @author Bergmann Gabor
 * 
 */
public class CallbackNode extends SimpleReceiver {

    IUpdateable updateable;

    public CallbackNode(ReteContainer reteContainer, IUpdateable updateable) 
    {
        super(reteContainer);
        this.updateable = updateable;
    }

    @Override
    public void update(Direction direction, Tuple updateElement) {
    	updateable.update(updateElement, direction == Direction.INSERT);
    }

}
