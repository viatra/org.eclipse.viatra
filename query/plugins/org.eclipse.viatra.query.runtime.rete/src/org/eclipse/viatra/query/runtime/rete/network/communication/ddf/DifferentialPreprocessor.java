/*******************************************************************************
 * Copyright (c) 2010-2019, Tamas Szabo, Istvan Rath and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Tamas Szabo - initial API and implementation
 *******************************************************************************/
package org.eclipse.viatra.query.runtime.rete.network.communication.ddf;

import org.eclipse.viatra.query.runtime.rete.network.Node;

/**
 * Values of this enum perform different kind of preprocessing on {@link DifferentialTimestamp}s. 
 * This is used on edges leading in and out from {@link Node}s in recursive {@link DifferentialCommunicationGroup}s. 
 * 
 * @author Tamas Szabo
 * @since 2.2
 */
public enum DifferentialPreprocessor {

    INCREMENT {
        @Override
        public DifferentialTimestamp process(final DifferentialTimestamp timestamp) {
            return new DifferentialTimestamp(timestamp.getValue() + 1);
        }
        
        @Override
        public String toString() {
            return "INCREMENT";
        }
    },
    RESET {
        @Override
        public DifferentialTimestamp process(final DifferentialTimestamp timestamp) {
            return DifferentialTimestamp.ZERO;
        }
        
        @Override
        public String toString() {
            return "RESET";
        }
    };

    public abstract DifferentialTimestamp process(final DifferentialTimestamp timestamp);

}
