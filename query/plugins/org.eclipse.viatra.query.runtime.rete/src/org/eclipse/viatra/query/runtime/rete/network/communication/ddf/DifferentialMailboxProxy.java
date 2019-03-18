/*******************************************************************************
 * Copyright (c) 2010-2019, Tamas Szabo, Istvan Rath and Daniel Varro
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-v20.html.
 * 
 * SPDX-License-Identifier: EPL-2.0
 *******************************************************************************/
package org.eclipse.viatra.query.runtime.rete.network.communication.ddf;

import org.eclipse.viatra.query.runtime.matchers.tuple.Tuple;
import org.eclipse.viatra.query.runtime.rete.network.Direction;
import org.eclipse.viatra.query.runtime.rete.network.Receiver;
import org.eclipse.viatra.query.runtime.rete.network.communication.CommunicationGroup;
import org.eclipse.viatra.query.runtime.rete.network.communication.MessageSelector;
import org.eclipse.viatra.query.runtime.rete.network.mailbox.Mailbox;

/**
 * A differential proxy for another {@link Mailbox}, which performs some preprocessing 
 * on the differential timestamps before passing it on to the real recipient. 
 * 
 * @author Tamas Szabo
 * @since 2.2
 */
public class DifferentialMailboxProxy implements Mailbox {

    protected final DifferentialPreprocessor preprocessor;
    protected final Mailbox wrapped;

    public DifferentialMailboxProxy(final Mailbox wrapped, final DifferentialPreprocessor preprocessor) {
        this.wrapped = wrapped;
        this.preprocessor = preprocessor;
    }

    @Override
    public void postMessage(final Direction direction, final Tuple update, final DifferentialTimestamp timestamp) {
        this.wrapped.postMessage(direction, update, preprocessor.process(timestamp));
    }

    @Override
    public String toString() {
        return this.preprocessor.toString() + "_PROXY -> " + this.wrapped.toString();
    }

    @Override
    public void clear() {
        this.wrapped.clear();
    }

    @Override
    public void deliverAll(final MessageSelector selector) {
        this.wrapped.deliverAll(selector);
    }

    @Override
    public CommunicationGroup getCurrentGroup() {
        return this.wrapped.getCurrentGroup();
    }

    @Override
    public void setCurrentGroup(final CommunicationGroup group) {
        this.wrapped.setCurrentGroup(group);
    }

    @Override
    public Receiver getReceiver() {
        return this.wrapped.getReceiver();
    }

    @Override
    public boolean isEmpty() {
        return this.wrapped.isEmpty();
    }

    @Override
    public boolean equals(final Object obj) {
        if (obj == null || obj.getClass() != this.getClass()) {
            return false;
        } else if (obj == this) {
            return true;
        } else {
            final DifferentialMailboxProxy that = (DifferentialMailboxProxy) obj;
            return this.wrapped.equals(that.wrapped) && this.preprocessor == that.preprocessor;
        }
    }

    @Override
    public int hashCode() {
        int hash = 1;
        hash = hash * 17 + this.wrapped.hashCode();
        hash = hash * 31 + this.preprocessor.hashCode();
        return hash;
    }

}
