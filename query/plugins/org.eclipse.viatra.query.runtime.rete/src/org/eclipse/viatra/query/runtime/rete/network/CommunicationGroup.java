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
import java.util.Collections;
import java.util.EnumMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import org.eclipse.viatra.query.runtime.matchers.util.CollectionsFactory;
import org.eclipse.viatra.query.runtime.rete.network.mailbox.Mailbox;

/**
 * A communication group represents a set of nodes in the communication graph
 * that form a strongly connected component.
 * 
 * @author Tamas Szabo
 * @since 1.6
 */
public abstract class CommunicationGroup implements Comparable<CommunicationGroup> {

	/**
	 * Marker for the {@link CommunicationTracker}
	 */
	public boolean isEnqueued = false;

	protected final Node representative;

	/**
	 * May be changed during bumping in
	 * {@link CommunicationTracker.registerDependency}
	 */
	protected int identifier;

	/**
	 * @since 1.7
	 */
	protected final CommunicationTracker tracker;

	/**
	 * @since 1.7
	 */
	public CommunicationGroup(final CommunicationTracker tracker, final Node representative, final int identifier) {
		this.tracker = tracker;
		this.representative = representative;
		this.identifier = identifier;
	}

	public abstract void deliverMessages();

	public Node getRepresentative() {
		return representative;
	}

	public abstract boolean isEmpty();

	/**
     * @since 2.0
     */
	public abstract void notifyLostAllMessages(final Mailbox mailbox, final MessageKind kind);

	/**
     * @since 2.0
     */
	public abstract void notifyHasMessage(final Mailbox mailbox, final MessageKind kind);

	public abstract void addRederivable(final RederivableNode node);

	public abstract void removeRederivable(final RederivableNode node);

	public abstract Map<MessageKind, Collection<Mailbox>> getMailboxes();

	public abstract Collection<RederivableNode> getRederivables();

	@Override
	public int hashCode() {
		return this.identifier;
	}

	@Override
	public String toString() {
		return "Group " + this.identifier + " - representative: " + this.representative + " - isEmpty: " + isEmpty();
	}

	@Override
	public boolean equals(final Object obj) {
		if (obj == null || this.getClass() != obj.getClass()) {
			return false;
		} else if (this == obj) {
			return true;
		} else {
			final CommunicationGroup that = (CommunicationGroup) obj;
			return this.identifier == that.identifier;
		}
	}

	@Override
	public int compareTo(final CommunicationGroup that) {
		return this.identifier - that.identifier;
	}

	/**
	 * A communication group containing only a single node with a single default
	 * mailbox.
	 */
	public static final class Singleton extends CommunicationGroup {

		private Mailbox mailbox;

		/**
		 * @since 1.7
		 */
		public Singleton(final CommunicationTracker tracker, final Node representative, final int identifier) {
			super(tracker, representative, identifier);
		}

		@Override
		public void deliverMessages() {
			this.mailbox.deliverAll(MessageKind.DEFAULT);
		}

		@Override
		public boolean isEmpty() {
			return this.mailbox == null;
		}

		@Override
		public void notifyHasMessage(final Mailbox mailbox, final MessageKind kind) {
			if (kind == MessageKind.DEFAULT) {
				this.mailbox = mailbox;
				if (!this.isEnqueued) {
					this.tracker.activateUnenqueued(this);
				}
			} else {
				throw new IllegalArgumentException("Unsupported message kind " + kind);
			}
		}

		@Override
		public void notifyLostAllMessages(final Mailbox mailbox, final MessageKind kind) {
			if (kind == MessageKind.DEFAULT) {
				this.mailbox = null;
				this.tracker.deactivate(this);
			} else {
				throw new IllegalArgumentException("Unsupported message kind " + kind);
			}
		}

		@Override
		public void addRederivable(final RederivableNode node) {
			throw new UnsupportedOperationException("Singleton group does not support DRED mode!");
		}

		@Override
		public void removeRederivable(final RederivableNode node) {
			throw new UnsupportedOperationException("Singleton group does not support DRED mode!");
		}

		@Override
		public Collection<RederivableNode> getRederivables() {
			return Collections.emptySet();
		}

		@Override
		public Map<MessageKind, Collection<Mailbox>> getMailboxes() {
            if (mailbox != null) {
                return Collections.singletonMap(MessageKind.DEFAULT, Collections.singleton(mailbox));
            } else {
                return Collections.emptyMap();
            }
		}

	}

	/**
	 * A communication group representing either (1) a single node where the
	 * node is a monotonicity aware one (2) a set of nodes that form an SCC
	 */
	public static final class Recursive extends CommunicationGroup {

		private final Set<Mailbox> antiMonotoneMailboxes;
		private final Set<Mailbox> monotoneMailboxes;
		private final Set<Mailbox> defaultMailboxes;
		private final Set<RederivableNode> rederivables;

		/**
		 * @since 1.7
		 */
		public Recursive(final CommunicationTracker tracker, final Node representative, final int identifier) {
			super(tracker, representative, identifier);
			this.antiMonotoneMailboxes = CollectionsFactory.createSet();
			this.monotoneMailboxes = CollectionsFactory.createSet();
			this.defaultMailboxes = CollectionsFactory.createSet();
			this.rederivables = new LinkedHashSet<RederivableNode>();
		}

		@Override
		public void deliverMessages() {
			// ANTI-MONOTONE PHASE
			while (!this.antiMonotoneMailboxes.isEmpty() || !this.defaultMailboxes.isEmpty()) {
				while (!this.antiMonotoneMailboxes.isEmpty()) {
					final Mailbox mailbox = this.antiMonotoneMailboxes.iterator().next();
					this.antiMonotoneMailboxes.remove(mailbox);
					mailbox.deliverAll(MessageKind.ANTI_MONOTONE);
				}
				while (!this.defaultMailboxes.isEmpty()) {
					final Mailbox mailbox = this.defaultMailboxes.iterator().next();
					this.defaultMailboxes.remove(mailbox);
					mailbox.deliverAll(MessageKind.DEFAULT);
				}
			}

			// REDERIVE PHASE
			while (!this.rederivables.isEmpty()) {
				// re-derivable nodes take care of their unregistration!!
				final RederivableNode node = this.rederivables.iterator().next();
				node.rederiveOne();
			}

			// MONOTONE PHASE
			while (!this.monotoneMailboxes.isEmpty() || !this.defaultMailboxes.isEmpty()) {
				while (!this.monotoneMailboxes.isEmpty()) {
					final Mailbox mailbox = this.monotoneMailboxes.iterator().next();
					this.monotoneMailboxes.remove(mailbox);
					mailbox.deliverAll(MessageKind.MONOTONE);
				}
				while (!this.defaultMailboxes.isEmpty()) {
					final Mailbox mailbox = this.defaultMailboxes.iterator().next();
					this.defaultMailboxes.remove(mailbox);
					mailbox.deliverAll(MessageKind.DEFAULT);
				}
			}
		}

		@Override
		public boolean isEmpty() {
			return this.rederivables.isEmpty() && this.antiMonotoneMailboxes.isEmpty()
					&& this.monotoneMailboxes.isEmpty() && this.defaultMailboxes.isEmpty();
		}

		@Override
		public void notifyHasMessage(final Mailbox mailbox, final MessageKind kind) {
			final Collection<Mailbox> mailboxes = getMailboxContainer(kind);
			mailboxes.add(mailbox);
			if (!this.isEnqueued) {
				this.tracker.activateUnenqueued(this);
			}
		}

		@Override
		public void notifyLostAllMessages(final Mailbox mailbox, final MessageKind kind) {
			final Collection<Mailbox> mailboxes = getMailboxContainer(kind);
			mailboxes.remove(mailbox);
			if (isEmpty()) {
				this.tracker.deactivate(this);
			}
		}

		private Collection<Mailbox> getMailboxContainer(final MessageKind kind) {
			if (kind == MessageKind.ANTI_MONOTONE) {
				return this.antiMonotoneMailboxes;
			} else if (kind == MessageKind.MONOTONE) {
				return this.monotoneMailboxes;
			} else if (kind == MessageKind.DEFAULT) {
				return this.defaultMailboxes;
			} else {
				throw new IllegalArgumentException("Unsupported message kind " + kind);
			}
		}

		@Override
		public void addRederivable(final RederivableNode node) {
			this.rederivables.add(node);
			if (!this.isEnqueued) {
				this.tracker.activateUnenqueued(this);
			}
		}

		@Override
		public void removeRederivable(final RederivableNode node) {
			this.rederivables.remove(node);
			if (isEmpty()) {
				this.tracker.deactivate(this);
			}
		}

		@Override
		public Collection<RederivableNode> getRederivables() {
			return this.rederivables;
		}

		@Override
		public Map<MessageKind, Collection<Mailbox>> getMailboxes() {
            Map<MessageKind, Collection<Mailbox>> map = new EnumMap<>(MessageKind.class);
            map.put(MessageKind.ANTI_MONOTONE, antiMonotoneMailboxes);
            map.put(MessageKind.MONOTONE, monotoneMailboxes);
            map.put(MessageKind.DEFAULT, defaultMailboxes);
            return Collections.unmodifiableMap(map);
		}

	}
}
