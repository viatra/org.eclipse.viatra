/*******************************************************************************
 * Copyright (c) 2010-2013, Abel Hegedus, Istvan Rath and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Abel Hegedus - initial API and implementation
 *******************************************************************************/
package org.eclipse.incquery.runtime.evm.specific;

import org.eclipse.emf.common.command.Command;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.transaction.RecordingCommand;
import org.eclipse.emf.transaction.TransactionalEditingDomain;
import org.eclipse.emf.transaction.util.TransactionUtil;
import org.eclipse.incquery.runtime.api.IMatchProcessor;
import org.eclipse.incquery.runtime.api.IPatternMatch;
import org.eclipse.incquery.runtime.evm.api.Activation;
import org.eclipse.incquery.runtime.evm.api.ActivationState;
import org.eclipse.incquery.runtime.evm.api.Context;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;

/**
 * A stateless job implementation that executes its action inside a {@link RecordingCommand}
 * if there is a {@link TransactionalEditingDomain} available.
 * 
 * @author Abel Hegedus
 * 
 */
public class RecordingJob<Match extends IPatternMatch> extends StatelessJob<Match> {

    public static final String TRANSACTIONAL_EDITING_DOMAIN = "org.eclipse.incquery.evm.TransactionalEditingDomain";
    public static final String RECORDING_JOB = "org.eclipse.incquery.evm.specifc.RecordingJobExecution";
    public static final String RECORDING_JOB_SESSION_DATA_KEY = "org.eclipse.incquery.evm.specific.RecordingJob.SessionData";

    /**
     * Data transfer class for storing the commands created by recording jobs.
     * 
     * @author Abel Hegedus
     *
     */
    public static class RecordingJobContextData {

        private Table<Activation<? extends IPatternMatch>, RecordingJob<? extends IPatternMatch>, Command> table;

        /**
         * Creates a new data transfer object
         */
        public RecordingJobContextData() {
            this.table = HashBasedTable.create();
        }

        /**
         * @return the table
         */
        public Table<Activation<? extends IPatternMatch>, RecordingJob<? extends IPatternMatch>, Command> getTable() {
            return table;
        }

    }

    /**
     * Creates a new recording job associated with the given state and processor.
     * 
     * @param activationState
     * @param matchProcessor
     */
    public RecordingJob(final ActivationState activationState, final IMatchProcessor<Match> matchProcessor) {
        super(activationState, matchProcessor);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.eclipse.incquery.runtime.evm.api.StatelessJob#execute(org.eclipse.incquery.runtime.evm
     * .api.Activation)
     */
    @Override
    protected void execute(final Activation<Match> activation, final Context context) {
        Object target = findDomainTarget(activation, context);
        TransactionalEditingDomain domain = TransactionUtil.getEditingDomain(target);
        if (domain == null) {
            super.execute(activation, context);
        } else {
            final RecordingCommand command = new RecordingCommand(domain) {
                @Override
                protected void doExecute() {
                    RecordingJob.super.execute(activation, context);
                }
            };
            command.setLabel(RECORDING_JOB);
            domain.getCommandStack().execute(command);

            updateSessionData(activation, context, command);
        }
    }

    /**
     * This method is used to find a target that can be used for getting the {@link TransactionalEditingDomain}.
     * If the match of the activation has an EObject parameter, it uses that, otherwise tries to retrieve the
     * domain from the context.
     * 
     * @param activation
     * @param context
     * @return the object to be used for finding the domain
     */
    private Object findDomainTarget(final Activation<Match> activation, final Context context) {
        Match match = activation.getPatternMatch();
        if(match.parameterNames().length > 0) {
            for (int i = 0; i < match.parameterNames().length; i++) {
                if(match.get(i) instanceof EObject) {
                    return match.get(i);
                }
            }
        }
        return context.get(TRANSACTIONAL_EDITING_DOMAIN);
    }

    /**
     * Updates the data transfer object in the context with the command that was just executed.
     * 
     * @param activation
     * @param context
     * @param command
     */
    private void updateSessionData(final Activation<Match> activation, final Context context, final Command command) {
        Object data = context.get(RECORDING_JOB_SESSION_DATA_KEY);
        RecordingJobContextData result = null;
        if (data instanceof RecordingJobContextData) {
            result = (RecordingJobContextData) data;
        } else {
            result = new RecordingJobContextData();
            context.put(RECORDING_JOB_SESSION_DATA_KEY, result);
        }
        result.getTable().put(activation, this, command);
    }

}
