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
package org.eclipse.viatra.transformation.evm.transactions.specific.job;

import org.eclipse.emf.common.command.Command;
import org.eclipse.emf.transaction.RecordingCommand;
import org.eclipse.emf.transaction.TransactionalEditingDomain;
import org.eclipse.emf.transaction.util.TransactionUtil;
import org.eclipse.viatra.query.runtime.matchers.util.Preconditions;
import org.eclipse.viatra.transformation.evm.api.Activation;
import org.eclipse.viatra.transformation.evm.api.CompositeJob;
import org.eclipse.viatra.transformation.evm.api.Context;
import org.eclipse.viatra.transformation.evm.api.Job;

/**
 * A stateless job implementation that executes its action inside a {@link RecordingCommand} if there is a
 * {@link TransactionalEditingDomain} available. It is possible to access all created commands by adding a session
 * recorder implementation to the build.
 *
 * @author Abel Hegedus
 *
 */
public class RecordingJob<EventAtom> extends CompositeJob<EventAtom> {

    public static final String TRANSACTIONAL_EDITING_DOMAIN = CommandExecutingJob.EDITING_DOMAIN;
    public static final String RECORDING_JOB = "org.eclipse.viatra.transformation.evm.specifc.RecordingJobExecution";
    public static final String RECORDING_JOB_SESSION_DATA_KEY = "org.eclipse.viatra.transformation.evm.specific.RecordingJob.SessionData";
    private final EventAtomEditingDomainProvider<EventAtom> provider;
    private final ICommandRecorder<EventAtom> commandRecorder;

    /**
     * @since 2.0
     */
    public interface ICommandRecorder<EventAtom> {

        /**
         * Reports that a command that was just executed based on the current activation over the given context.
         */
        void recordCommandExecution(final Activation<? extends EventAtom> activation, final Context context,
                final Command command);
    }

    /**
     * Creates a new recording job associated with the given state and processor.
     *
     * @param recordedJob
     */
    public RecordingJob(final Job<EventAtom> recordedJob) {
        super(recordedJob);
        this.provider = null;
        this.commandRecorder = null;
    }

    public RecordingJob(final Job<EventAtom> recordedJob, final EventAtomEditingDomainProvider<EventAtom> provider) {
        super(recordedJob);
        Preconditions.checkArgument(provider != null, "Provider cannot be null!");
        this.provider = provider;
        this.commandRecorder = null;
    }

    /**
     * @since 2.0
     */
    public RecordingJob(final Job<EventAtom> recordedJob, ICommandRecorder<EventAtom> commandRecorder) {
        super(recordedJob);
        this.provider = null;
        this.commandRecorder = commandRecorder;
    }

    /**
     * @since 2.0
     */
    public RecordingJob(final Job<EventAtom> recordedJob, final EventAtomEditingDomainProvider<EventAtom> provider,
            ICommandRecorder<EventAtom> commandRecorder) {
        super(recordedJob);
        Preconditions.checkArgument(provider != null, "Provider cannot be null!");
        this.provider = provider;
        this.commandRecorder = commandRecorder;
    }

    @Override
    protected void execute(final Activation<? extends EventAtom> activation, final Context context) {
        final Object target = findDomainTarget(activation, context);
        final TransactionalEditingDomain domain = TransactionUtil.getEditingDomain(target);
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

            if (commandRecorder != null) {
                commandRecorder.recordCommandExecution(activation, context, command);
            }
        }
    }

    /**
     * This method is used to find a target that can be used for getting the {@link TransactionalEditingDomain}. It
     * tries to retrieve the domain from the context, otherwise it tries to find an EObject parameter in the event atom
     * of the activation.
     *
     * @param activation
     * @param context
     * @return the object to be used for finding the domain
     */
    protected Object findDomainTarget(final Activation<? extends EventAtom> activation, final Context context) {
        Object domainTarget = context.get(TRANSACTIONAL_EDITING_DOMAIN);
        if (domainTarget == null && provider != null) {
            domainTarget = provider.findEditingDomain(activation, context);
        }
        return domainTarget;
    }

}
