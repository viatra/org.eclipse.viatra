/*******************************************************************************
 * Copyright (c) 2010-2017, Tamas Szabo, Istvan Rath and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Tamas Szabo - initial API and implementation
 *******************************************************************************/
package org.eclipse.viatra.query.runtime.rete.network;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Set;

import org.eclipse.viatra.query.runtime.base.itc.alg.incscc.IncSCCAlg;
import org.eclipse.viatra.query.runtime.base.itc.alg.misc.topsort.TopologicalSorting;
import org.eclipse.viatra.query.runtime.base.itc.graphimpl.Graph;

/**
 * An instance of this class is associated with every {@link ReteContainer}. The tracker serves two purposes: <br>
 * (1) It allows RETE nodes to register their communication dependencies on-the-fly. These dependencies can be
 * registered or unregistered when nodes are disposed of. <br>
 * (2) It allows RETE nodes to register their mailboxes as dirty, that is, they can tell the tracker that they have
 * something to send to other nodes in the network. The tracker is then responsible for ordering these messages (more
 * precisely, the mailboxes that contain the messages) for the associated {@link ReteContainer}. The ordering is
 * governed by the strongly connected components in the dependency network and follows a topological sorting scheme;
 * those mailboxes will be emptied first whose owner nodes' do not depend on other undelivered messages.
 * 
 * @author Tamas Szabo
 * @since 1.6
 */
public final class CommunicationTracker {

    /**
     * The dependency graph of the communications in the RETE network
     */
    private final Graph<Node> dependencyGraph;

    /**
     * Incremental SCC information about the dependency graph
     */
    private final IncSCCAlg<Node> sccInformationProvider;

    /**
     * Precomputed representative -> communication group map
     */
    private final Map<Node, CommunicationGroup> groupMap;

    /**
     * Priority queue of active communication groups
     */
    private final Queue<CommunicationGroup> groupQueue;

    /**
     * Companion set of the priority queue to avoid duplicates
     */
    private final Set<CommunicationGroup> activeGroups;

    // groups should have a simple integer flag which represents its position in a priority queue
    // priority queue only contains the ACTIVE groups

    public CommunicationTracker() {
        this.dependencyGraph = new Graph<Node>();
        this.sccInformationProvider = new IncSCCAlg<Node>(this.dependencyGraph);
        this.groupQueue = new PriorityQueue<CommunicationGroup>();
        this.activeGroups = new HashSet<CommunicationGroup>();
        this.groupMap = new HashMap<Node, CommunicationTracker.CommunicationGroup>();
    }

    private void precomputeGroups() {
        groupMap.clear();

        // reconstruct group map from dependency graph
        final Graph<Node> reducedGraph = sccInformationProvider.getReducedGraph();
        final List<Node> representatives = TopologicalSorting.compute(reducedGraph);

        for (int i = 0; i < representatives.size(); i++) {
            final Node representative = representatives.get(i);
            final CommunicationGroup group = new CommunicationGroup(representative, i);
            groupMap.put(representative, group);
        }

        // reconstruct new queue contents based on new group map
        if (!groupQueue.isEmpty()) {
            final Set<CommunicationGroup> immediatelyActiveGroups = new HashSet<CommunicationGroup>();

            for (final CommunicationGroup oldGroup : groupQueue) {
                for (final Mailbox mailbox : oldGroup.defaultMailboxes) {
                    final CommunicationGroup newGroup = getGroup(mailbox.getReceiver());
                    newGroup.defaultMailboxes.add(mailbox);
                    immediatelyActiveGroups.add(newGroup);
                }
                for (final Mailbox mailbox : oldGroup.antiMonotoneMailboxes) {
                    final CommunicationGroup newGroup = getGroup(mailbox.getReceiver());
                    newGroup.antiMonotoneMailboxes.add(mailbox);
                    immediatelyActiveGroups.add(newGroup);
                }
                for (final Mailbox mailbox : oldGroup.monotoneMailboxes) {
                    final CommunicationGroup newGroup = getGroup(mailbox.getReceiver());
                    newGroup.monotoneMailboxes.add(mailbox);
                    immediatelyActiveGroups.add(newGroup);
                }
                for (final RederivableNode node : oldGroup.rederivables) {
                    final CommunicationGroup newGroup = getGroup(node);
                    newGroup.rederivables.add(node);
                    immediatelyActiveGroups.add(newGroup);
                }
            }

            groupQueue.clear();
            activeGroups.clear();

            for (final CommunicationGroup group : immediatelyActiveGroups) {
                activate(group);
            }
        }
    }

    private void activate(final CommunicationGroup group) {
        if (!activeGroups.contains(group)) {
            groupQueue.add(group);
            activeGroups.add(group);
        }
    }

    private void deactivate(final CommunicationGroup group) {
        groupQueue.remove(group);
        activeGroups.remove(group);
    }

    private CommunicationGroup getGroup(final Node node) {
        final Node representative = sccInformationProvider.getRepresentative(node);
        final CommunicationGroup group = groupMap.get(representative);
        return group;
    }

    public void addRederivable(final RederivableNode node) {
        final CommunicationGroup group = getGroup(node);
        group.rederivables.add(node);

        activate(group);
    }

    public void removeRederivable(final RederivableNode node) {
        final CommunicationGroup group = getGroup(node);
        group.rederivables.remove(node);

        if (group.isEmpty()) {
            deactivate(group);
        }
    }

    public void notifyHasMessage(final Mailbox mailbox) {
        notifyHasMessage(mailbox, null);
    }

    public void notifyHasMessage(final Mailbox mailbox, final MessageKind kind) {
        final Receiver receiver = mailbox.getReceiver();
        final CommunicationGroup group = getGroup(receiver);

        if (kind == null) {
            group.defaultMailboxes.add(mailbox);
        } else if (kind == MessageKind.MONOTONE) {
            group.monotoneMailboxes.add(mailbox);
        } else if (kind == MessageKind.ANTI_MONOTONE) {
            group.antiMonotoneMailboxes.add(mailbox);
        } else {
            throw new IllegalArgumentException();
        }

        activate(group);
    }

    public void notifyLostAllMessages(final Mailbox mailbox) {
        notifyLostAllMessages(mailbox, null);
    }

    public void notifyLostAllMessages(final Mailbox mailbox, final MessageKind kind) {
        final Receiver receiver = mailbox.getReceiver();
        final CommunicationGroup group = getGroup(receiver);

        if (kind == null) {
            group.defaultMailboxes.remove(mailbox);
        } else if (kind == MessageKind.MONOTONE) {
            group.monotoneMailboxes.remove(mailbox);
        } else if (kind == MessageKind.ANTI_MONOTONE) {
            group.antiMonotoneMailboxes.remove(mailbox);
        } else {
            throw new IllegalArgumentException();
        }

        if (group.isEmpty()) {
            deactivate(group);
        }
    }

    public CommunicationGroup getAndRemoveFirstGroup() {
        final CommunicationGroup group = groupQueue.poll();
        activeGroups.remove(group);
        return group;
    }

    public boolean isEmpty() {
        return groupQueue.isEmpty();
    }

    /**
     * Registers the dependency that the target {@link Node} depends on the source {@link Node}. In other words, source
     * may send messages to target in the RETE network.
     *
     * @param source
     *            the source node
     * @param target
     *            the target node
     */
    public void registerDependency(final Node source, final Node target) {
        final Node rs = sccInformationProvider.getRepresentative(source);
        final Node rt = sccInformationProvider.getRepresentative(target);

        dependencyGraph.insertNode(source);
        dependencyGraph.insertNode(target);
        dependencyGraph.insertEdge(source, target);

        // if they were already in the same SCC, then this insertion did not affect the SCCs
        // otherwise, we need a new precomputation for the groupMap and groupQueue
        if (!(rs != null && rs.equals(rt))) {
            precomputeGroups();
        }
    }

    /**
     * Unregisters a dependency between source and target.
     * 
     * @param source
     *            the source node
     * @param target
     *            the target node
     */
    public void unregisterDependency(final Node source, final Node target) {
        this.dependencyGraph.deleteEdge(source, target);

        final Node rs = sccInformationProvider.getRepresentative(source);
        final Node rt = sccInformationProvider.getRepresentative(target);

        // if they are still in the same SCC, then this deletion did not affect the SCCs
        // otherwise, we need a new precomputation for the groupMap and groupQueue
        if (!(rs != null && rs.equals(rt))) {
            precomputeGroups();
        }
    }

    public final class CommunicationGroup implements Comparable<CommunicationGroup> {

        private final Node representative;
        private final int identifier;
        private final Set<Mailbox> antiMonotoneMailboxes;
        private final Set<Mailbox> monotoneMailboxes;
        private final Set<Mailbox> defaultMailboxes;
        private final Set<RederivableNode> rederivables;

        public CommunicationGroup(final Node representative, final int identifier) {
            this.representative = representative;
            this.identifier = identifier;
            this.antiMonotoneMailboxes = new LinkedHashSet<Mailbox>();
            this.monotoneMailboxes = new LinkedHashSet<Mailbox>();
            this.defaultMailboxes = new LinkedHashSet<Mailbox>();
            this.rederivables = new LinkedHashSet<RederivableNode>();
        }

        public int getIdentifier() {
            return identifier;
        }

        public Node getRepresentative() {
            return representative;
        }

        public Set<RederivableNode> getRederivables() {
            return rederivables;
        }

        public Set<Mailbox> getMonotoneMailboxes() {
            return monotoneMailboxes;
        }

        public Set<Mailbox> getAntiMonotoneMailboxes() {
            return antiMonotoneMailboxes;
        }

        public Set<Mailbox> getDefaultMailboxes() {
            return defaultMailboxes;
        }

        public boolean isEmpty() {
            return monotoneMailboxes.isEmpty() && antiMonotoneMailboxes.isEmpty() && rederivables.isEmpty()
                    && defaultMailboxes.isEmpty();
        }

        @Override
        public int hashCode() {
            return identifier;
        }

        @Override
        public String toString() {
            return "Group " + identifier + " - representative: " + representative + " - isEmpty: " + isEmpty();
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == null || this.getClass() != obj.getClass()) {
                return false;
            } else if (this == obj) {
                return true;
            } else {
                final CommunicationGroup that = (CommunicationGroup) obj;
                return identifier == that.identifier;
            }
        }

        @Override
        public int compareTo(final CommunicationGroup that) {
            return this.identifier - that.identifier;
        }

    }

}
