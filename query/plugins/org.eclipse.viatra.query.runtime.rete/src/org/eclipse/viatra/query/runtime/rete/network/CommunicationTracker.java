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

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
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
 *
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
     * Precomputed node -> communication group map
     */
    private final Map<Node, CommunicationGroup> groupMap;

    /**
     * Priority queue of active communication groups
     */
    private final Queue<CommunicationGroup> groupQueue;

    // groups should have a simple integer flag which represents its position in a priority queue
    // priority queue only contains the ACTIVE groups

    public CommunicationTracker() {
        this.dependencyGraph = new Graph<Node>();
        this.sccInformationProvider = new IncSCCAlg<Node>(this.dependencyGraph);
        this.groupQueue = new PriorityQueue<CommunicationGroup>();
        this.groupMap = new HashMap<Node, CommunicationGroup>();
    }

    private void precomputeGroups() {
        groupMap.clear();

        // reconstruct group map from dependency graph
        final Graph<Node> reducedGraph = sccInformationProvider.getReducedGraph();
        final List<Node> representatives = TopologicalSorting.compute(reducedGraph);

        for (int i = 0; i < representatives.size(); i++) { // groups for SCC representatives
            final Node representative = representatives.get(i);
            final boolean isSingleton = sccInformationProvider.sccs.getPartition(representative).size() == 1;
            final boolean isDefault = representative instanceof Receiver && ((Receiver) representative).getMailbox() instanceof DefaultMailbox;
            
            CommunicationGroup group = null;
            if (isSingleton && isDefault) {
                group = new CommunicationGroup.Singleton(representative, i);
            } else {
                group = new CommunicationGroup.Recursive(representative, i);
            }
            groupMap.put(representative, group);
        }

        for (final Node node : dependencyGraph.getAllNodes()) { // extend group map to the rest of nodes
            final Node representative = sccInformationProvider.getRepresentative(node);
            if (representative != node) {
                groupMap.put(node, groupMap.get(representative));
            }
        }

        // reconstruct new queue contents based on new group map
        if (!groupQueue.isEmpty()) {
            final Set<CommunicationGroup> immediatelyActiveGroups = new HashSet<CommunicationGroup>();

            for (final CommunicationGroup oldGroup : groupQueue) {
                for (final Entry<MessageKind, Collection<Mailbox>> entry : oldGroup.getMailboxes().entrySet()) {
                    for (final Mailbox mailbox : entry.getValue()) {
                        final CommunicationGroup newGroup = this.groupMap.get(mailbox.getReceiver());
                        newGroup.notifyHasMessage(mailbox, entry.getKey());
                        immediatelyActiveGroups.add(newGroup);                        
                    }
                }
                
                for (final RederivableNode node : oldGroup.getRederivables()) {
                    final CommunicationGroup newGroup = this.groupMap.get(node);
                    newGroup.addRederivable(node);
                    immediatelyActiveGroups.add(newGroup);
                }
                oldGroup.isEnqueued = false;
            }

            groupQueue.clear();

            for (final CommunicationGroup group : immediatelyActiveGroups) {
                activate(group);
            }
        }
    }

    private void activate(final CommunicationGroup group) {
        if (!group.isEnqueued) {
            groupQueue.add(group);
            group.isEnqueued = true;
        }
    }

    private void deactivate(final CommunicationGroup group) {
        groupQueue.remove(group);
        group.isEnqueued = false;
    }

    public void addRederivable(final RederivableNode node) {
        final CommunicationGroup group = this.groupMap.get(node);
        group.addRederivable(node);
        activate(group);
    }

    public void removeRederivable(final RederivableNode node) {
        final CommunicationGroup group = this.groupMap.get(node);
        group.removeRederivable(node);
        if (group.isEmpty()) {
            deactivate(group);
        }
    }

    public void notifyHasMessage(final Mailbox mailbox, final MessageKind kind) {
        final Receiver receiver = mailbox.getReceiver();
        final CommunicationGroup group = this.groupMap.get(receiver);

        group.notifyHasMessage(mailbox, kind);

        activate(group);
    }

    public void notifyLostAllMessages(final Mailbox mailbox, final MessageKind kind) {
        final Receiver receiver = mailbox.getReceiver();
        final CommunicationGroup group = this.groupMap.get(receiver);

        group.notifyLostAllMessages(mailbox, kind);

        if (group.isEmpty()) {
            deactivate(group);
        }
    }

    public CommunicationGroup getAndRemoveFirstGroup() {
        final CommunicationGroup group = groupQueue.poll();
        group.isEnqueued = false;
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
   
}
