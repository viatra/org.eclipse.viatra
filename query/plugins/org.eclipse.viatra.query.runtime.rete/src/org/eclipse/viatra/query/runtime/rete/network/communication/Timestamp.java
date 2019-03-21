/*******************************************************************************
 * Copyright (c) 2010-2018, Tamas Szabo, Istvan Rath and Daniel Varro
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-v20.html.
 * 
 * SPDX-License-Identifier: EPL-2.0
 *******************************************************************************/
package org.eclipse.viatra.query.runtime.rete.network.communication;

import java.util.AbstractMap;
import java.util.Collection;
import java.util.Map;
import java.util.Set;

/**
 * A timestamp associated with update messages in differential dataflow evaluation. 
 * 
 * @author Tamas Szabo
 * @since 2.2
 */
public class Timestamp implements Comparable<Timestamp>, MessageSelector {

    protected final int value;
    public static final Timestamp ZERO = new Timestamp(0);

    public Timestamp(final int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public Timestamp max(final Timestamp that) {
        if (this.value >= that.value) {
            return this;
        } else {
            return that;
        }
    }

    @Override
    public int compareTo(final Timestamp that) {
        return this.value - that.value;
    }

    @Override
    public boolean equals(final Object obj) {
        if (obj == null || obj.getClass() != this.getClass()) {
            return false;
        } else {
            return this.value == ((Timestamp) obj).value;
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
    public static final class AllZeroMap<T> extends AbstractMap<T, Timestamp> {

        private final Collection<T> wrapped;

        public AllZeroMap(Set<T> wrapped) {
            this.wrapped = wrapped;
        }

        @Override
        public Set<Entry<T, Timestamp>> entrySet() {
            throw new UnsupportedOperationException("Use the combination of keySet() and get()!");
        }

        @Override
        public Timestamp get(final Object key) {
            return Timestamp.ZERO;
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
