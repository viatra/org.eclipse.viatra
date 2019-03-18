/*******************************************************************************
 * Copyright (c) 2010-2013, Abel Hegedus, Istvan Rath and Daniel Varro
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-v20.html.
 * 
 * SPDX-License-Identifier: EPL-2.0
 *******************************************************************************/
package org.eclipse.viatra.transformation.evm.transactions.specific;

import org.eclipse.emf.transaction.TransactionalEditingDomain;
import org.eclipse.viatra.transformation.evm.specific.scheduler.UpdateCompleteBasedScheduler.UpdateCompleteBasedSchedulerFactory;
import org.eclipse.viatra.transformation.evm.transactions.update.TransactionUpdateCompleteProvider;

/**
 * @author Abel Hegedus
 *
 */
public final class TransactionalSchedulers {

    /**
     * 
     */
    private TransactionalSchedulers() {
    }

    /**
     * Creates a scheduler factory that creates schedulers by registering a listener
     *  for the transaction events on the given domain.
     *  
     * @param domain
     */
    public static UpdateCompleteBasedSchedulerFactory getTransactionSchedulerFactory(final TransactionalEditingDomain domain) {
        TransactionUpdateCompleteProvider provider = new TransactionUpdateCompleteProvider(domain);
        return new UpdateCompleteBasedSchedulerFactory(provider);
    }
    
}
