/*******************************************************************************
 * Copyright (c) 2010-2019, Tamas Szabo, Istvan Rath and Daniel Varro
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-v20.html.
 * 
 * SPDX-License-Identifier: EPL-2.0
 *******************************************************************************/
package org.eclipse.viatra.query.runtime.rete;

import org.eclipse.viatra.query.runtime.matchers.tuple.Tuple;
import org.eclipse.viatra.query.runtime.rete.network.communication.ddf.DifferentialTimestamp;

public interface IntervalTree {
        
    public boolean insert(final Tuple tuple, final DifferentialTimestamp timestamp);
    
    public boolean remove(final Tuple tuple, final DifferentialTimestamp timestamp);
    
    public int getCount(final Tuple tuple, final DifferentialTimestamp timestamp);

}
