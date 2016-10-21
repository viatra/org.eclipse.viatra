/*******************************************************************************
 * Copyright (c) 2010-2012, Abel Hegedus, Istvan Rath and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Abel Hegedus - initial API and implementation
 *******************************************************************************/
package org.eclipse.viatra.transformation.evm.transactions.update;

import org.eclipse.emf.transaction.TransactionalEditingDomain;
import org.eclipse.emf.transaction.TransactionalEditingDomain.Lifecycle;
import org.eclipse.emf.transaction.TransactionalEditingDomainEvent;
import org.eclipse.emf.transaction.TransactionalEditingDomainListenerImpl;
import org.eclipse.emf.transaction.impl.EMFCommandTransaction;
import org.eclipse.emf.transaction.util.TransactionUtil;
import org.eclipse.viatra.transformation.evm.transactions.specific.job.RecordingJob;
import org.eclipse.viatra.transformation.evm.update.UpdateCompleteProvider;

/**
 * This provider implementation uses the EMF Transaction notification as
 * an event source for update complete events.
 * 
 * @author Abel Hegedus
 * 
 */
public class TransactionUpdateCompleteProvider extends UpdateCompleteProvider {
    private final TransactionListener transactionListener;
    private final Lifecycle lifecycle;

    /**
     * Creates a new provider for the given {@link TransactionalEditingDomain}
     * 
     * @param editingDomain
     */
    public TransactionUpdateCompleteProvider(final TransactionalEditingDomain editingDomain) {
        this.transactionListener = new TransactionListener();
        this.lifecycle = TransactionUtil.getAdapter(editingDomain, Lifecycle.class);
    }

    @Override
    protected void firstListenerAdded() {
        super.firstListenerAdded();
        this.lifecycle.addTransactionalEditingDomainListener(transactionListener);
    }
    
    @Override
    protected void lastListenerRemoved() {
        super.lastListenerRemoved();
        this.lifecycle.removeTransactionalEditingDomainListener(transactionListener);
    }
    
    protected void sendTransactionUpdateCompleted() {
        super.updateCompleted();
    }
    
    /**
     * Listener implementation that is invoked by the transaction life-cycle.
     * 
     * @author Abel Hegedus
     *
     */
    private class TransactionListener extends TransactionalEditingDomainListenerImpl {

        @Override
        public void transactionClosed(final TransactionalEditingDomainEvent event) {
            boolean needsNotification = true;

            /*
             * Omit notifications about the executions of a RecordingJob
             */
            if (event.getTransaction() instanceof EMFCommandTransaction) {
            	EMFCommandTransaction transaction = (EMFCommandTransaction) event.getTransaction();
                // FIXME this is a really ugly hack!
                if (transaction.getCommand().getLabel().equals(RecordingJob.RECORDING_JOB)) {
                    needsNotification = false;
                }
            }

            if (needsNotification) {
                sendTransactionUpdateCompleted();
            }
        }
    }

}
