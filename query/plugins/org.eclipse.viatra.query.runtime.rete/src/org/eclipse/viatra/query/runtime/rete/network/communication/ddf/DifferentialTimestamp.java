/*******************************************************************************
 * Copyright (c) 2010-2018, Tamas Szabo, Istvan Rath and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   szabta - initial API and implementation
 *******************************************************************************/
package org.eclipse.viatra.query.runtime.rete.network.communication.ddf;

import java.util.AbstractMap;
import java.util.Collection;
import java.util.Map;
import java.util.Set;

import org.eclipse.viatra.query.runtime.rete.network.communication.MessageSelector;

/**
 * A timestamp associated with update messages in differential dataflow evaluation. 
 * 
 * @author Tamas Szabo
 * @since 2.2
 */
public class DifferentialTimestamp implements Comparable<DifferentialTimestamp>, MessageSelector {

    protected final int value;
    public static final DifferentialTimestamp ZERO = new DifferentialTimestamp(0);

    public DifferentialTimestamp(final int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public DifferentialTimestamp max(final DifferentialTimestamp that) {
        if (this.value >= that.value) {
            return this;
        } else {
            return that;
        }
    }

    @Override
    public int compareTo(final DifferentialTimestamp that) {
        return this.value - that.value;
    }

    @Override
    public boolean equals(final Object obj) {
        if (obj == null || obj.getClass() != this.getClass()) {
            return false;
        } else {
            return this.value == ((DifferentialTimestamp) obj).value;
        }
    }

    @Override
    public int hashCode() {
        return this.value;
    }

    @Override
    public String toString() {
        return Integer.toString(this.value);
    }

    /**
     * A {@link Map} implementation that associates the zero timestamp with every key.
     * There is no suppor for {@link Map#entrySet()} due to performance reasons.   
     * 
     * @author Tamas Szabo
     */
    public static final class AllZeroMap<T> extends AbstractMap<T, DifferentialTimestamp> {

        private final Collection<T> wrapped;

        public AllZeroMap(Set<T> wrapped) {
            this.wrapped = wrapped;
        }

        @Override
        public Set<Entry<T, DifferentialTimestamp>> entrySet() {
            throw new UnsupportedOperationException("Use the combination of keySet() and get()!");
        }

        @Override
        public DifferentialTimestamp get(final Object key) {
            return DifferentialTimestamp.ZERO;
        }
        
        @Override
        public Set<T> keySet() {
            return (Set<T>) this.wrapped;
        }

        @Override
        public String toString() {
            return this.getClass().getSimpleName() + ": " + this.keySet().toString();
        }

    }

}
