/*******************************************************************************
 * Copyright (c) 2010-2019, Tamas Szabo, Istvan Rath and Daniel Varro
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-v20.html.
 * 
 * SPDX-License-Identifier: EPL-2.0
 *******************************************************************************/
package org.eclipse.viatra.query.runtime.rete.network.communication.timely;

import java.util.Collection;
import java.util.function.Function;

import org.eclipse.viatra.query.runtime.rete.index.IndexerListener;
import org.eclipse.viatra.query.runtime.rete.index.StandardIndexer;
import org.eclipse.viatra.query.runtime.rete.network.NetworkStructureChangeSensitiveNode;
import org.eclipse.viatra.query.runtime.rete.network.Node;
import org.eclipse.viatra.query.runtime.rete.network.ProductionNode;
import org.eclipse.viatra.query.runtime.rete.network.StandardNode;
import org.eclipse.viatra.query.runtime.rete.network.communication.CommunicationGroup;
import org.eclipse.viatra.query.runtime.rete.network.communication.CommunicationTracker;
import org.eclipse.viatra.query.runtime.rete.network.mailbox.Mailbox;
import org.eclipse.viatra.query.runtime.rete.single.DiscriminatorDispatcherNode;

/**
 * Differential dataflow-specific implementation of the {@link CommunicationTracker}.
 * 
 * @author Tamas Szabo
 * @since 2.2
 */
public class TimelyCommunicationTracker extends CommunicationTracker {

    @Override
    protected CommunicationGroup createGroup(Node representative, int index) {
        final boolean isSingleton = this.sccInformationProvider.sccs.getPartition(representative).size() == 1;
        return new TimelyCommunicationGroup(this, representative, index, isSingleton);
    }

    @Override
    public Mailbox proxifyMailbox(final Node requester, final Mailbox original) {
        final TimestampTransformation preprocessor = getPreprocessor(requester, original.getReceiver());
        if (preprocessor == null) {
            return original;
        } else {
            return new TimelyMailboxProxy(original, preprocessor);
        }
    }

    @Override
    public IndexerListener proxifyIndexerListener(final Node requester, final IndexerListener original) {
        final TimestampTransformation preprocessor = getPreprocessor(requester, original.getOwner());
        if (preprocessor == null) {
            return original;
        } else {
            return new TimelyIndexerListenerProxy(original, preprocessor);
        }
    }

    protected TimestampTransformation getPreprocessor(final Node source, final Node target) {
        final CommunicationGroup sourceGroup = this.getGroup(source);
        final CommunicationGroup targetGroup = this.getGroup(target);

        if (sourceGroup != null && targetGroup != null) {
            // during RETE construction, the groups may be still null
            if (sourceGroup != targetGroup && sourceGroup.isRecursive()) {
                // targetGroup is a successor SCC of sourceGroup
                // and sourceGroup is a recursive SCC
                // then we need to zero out the timestamps
                return TimestampTransformation.RESET;
            }
            if (sourceGroup == targetGroup && target instanceof ProductionNode) {
                // if requester and receiver are in the same SCC
                // and receiver is a production node
                // then we need to increment the timestamps
                return TimestampTransformation.INCREMENT;
            }
        }

        return null;
    }

    @Override
    protected void postProcessNode(final Node node) {
        if (node instanceof NetworkStructureChangeSensitiveNode) {
            ((NetworkStructureChangeSensitiveNode) node).networkStructureChanged();
        }
    }

    /**
     * This static field is used for debug purposes in the DotGenerator.
     */
    public static final Function<Node, Function<Node, String>> EDGE_MAPPER = new Function<Node, Function<Node, String>>() {

        @Override
        public Function<Node, String> apply(final Node source) {
            return new Function<Node, String>() {
                @Override
                public String apply(final Node target) {
                    if (source instanceof StandardIndexer) {
                        final Collection<IndexerListener> listeners = ((StandardIndexer) source).getListeners();
                        for (final IndexerListener listener : listeners) {
                            if (listener.getOwner() == target && listener instanceof TimelyIndexerListenerProxy) {
                                return ((TimelyIndexerListenerProxy) listener).preprocessor.toString();
                            }
                        }
                    }
                    if (source instanceof StandardNode) {
                        final Collection<Mailbox> mailboxes = ((StandardNode) source).getChildMailboxes();
                        for (final Mailbox mailbox : mailboxes) {
                            if (mailbox.getReceiver() == target && mailbox instanceof TimelyMailboxProxy) {
                                return ((TimelyMailboxProxy) mailbox).preprocessor.toString();
                            }
                        }
                    }
                    if (source instanceof DiscriminatorDispatcherNode) {
                        final Collection<Mailbox> mailboxes = ((DiscriminatorDispatcherNode) source)
                                .getBucketMailboxes().values();
                        for (final Mailbox mailbox : mailboxes) {
                            if (mailbox.getReceiver() == target && mailbox instanceof TimelyMailboxProxy) {
                                return ((TimelyMailboxProxy) mailbox).preprocessor.toString();
                            }
                        }
                    }
                    return null;
                }
            };
        }

    };

}
