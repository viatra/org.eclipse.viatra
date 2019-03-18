/*******************************************************************************
 * Copyright (c) 2010-2013, Abel Hegedus, Istvan Rath and Daniel Varro
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-v20.html.
 * 
 * SPDX-License-Identifier: EPL-2.0
 *******************************************************************************/
package org.eclipse.viatra.transformation.evm.transactions.specific.job;

import org.eclipse.emf.common.command.Command;
import org.eclipse.emf.common.command.CompoundCommand;
import org.eclipse.emf.edit.domain.EditingDomain;
import org.eclipse.viatra.query.runtime.matchers.util.Preconditions;
import org.eclipse.viatra.transformation.evm.api.Activation;
import org.eclipse.viatra.transformation.evm.api.Context;
import org.eclipse.viatra.transformation.evm.api.Job;
import org.eclipse.viatra.transformation.evm.api.event.ActivationState;

/**
 * @author Abel Hegedus
 *
 */
public abstract class CommandExecutingJob<EventAtom> extends Job<EventAtom>{

    public static final String COMMAND_EXECUTING_JOB_HELPER = "org.eclipse.viatra.transformation.evm.CommandExecutingJob.Helper";
    public static final String EDITING_DOMAIN = "org.eclipse.viatra.transformation.evm.CommandExecutingJob.EditingDomain";
    private final EventAtomEditingDomainProvider<EventAtom> provider;

    /**
     * Create a new job that supports execution through commands by finding the editing
     * domain using the given provider and giving a {@link JobExecutionHelper} for providing
     * the execution with a way to easily create commands and execute them.
     *
     * @param enablingState
     * @param provider
     */
    public CommandExecutingJob(final ActivationState enablingState, final EventAtomEditingDomainProvider<EventAtom> provider) {
        super(enablingState);
        Preconditions.checkArgument(provider != null, "Provider cannot be null!");
        this.provider = provider;
    }

    @Override
    protected void execute(final Activation<? extends EventAtom> activation, final Context context) {
        final EditingDomain domain = findEditingDomain(activation, context);
        if (domain == null) {
            final StringBuilder sb = new StringBuilder();
            sb.append("Cannot find editing domain for activation ");
            sb.append(activation);
            sb.append(" and context ");
            sb.append(context);
            throw new IllegalStateException(sb.toString());
        } else {
            final JobExecutionHelper executionHelper = new JobExecutionHelper(domain, activation, context);
            context.put(COMMAND_EXECUTING_JOB_HELPER, executionHelper);
            executeCommands(executionHelper);
            context.remove(COMMAND_EXECUTING_JOB_HELPER);
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    protected void handleError(final Activation<? extends EventAtom> activation, final Exception exception, final Context context) {
        handleExecutableCommandError((JobExecutionHelper) context.remove(COMMAND_EXECUTING_JOB_HELPER), exception);
    }

    /**
     * Use the passed editing domain to instantiate and append commands to the supplied {@link JobExecutionHelper}.
     *
     * @param executionHelper
     */
    protected abstract void executeCommands(JobExecutionHelper executionHelper);

    /**
     * This method will be called if any error happens during the execution.
     *
     * @param executionHelper
     * @param exception
     */
    protected abstract void handleExecutableCommandError(JobExecutionHelper executionHelper, Exception exception);

    /**
     * This method is used to find a target that can be used for getting the {@link EditingDomain}.
     * It tries to retrieve the domain from the context, otherwise it tries to find an EObject parameter in
     * the event atom of the activation.
     *
     * @param activation
     * @param context
     * @return the object to be used for finding the domain
     */
    protected EditingDomain findEditingDomain(final Activation<? extends EventAtom> activation, final Context context) {
        EditingDomain editingDomain = null;
        final Object domainTarget = context.get(EDITING_DOMAIN);
        if(domainTarget instanceof EditingDomain) {
            editingDomain = (EditingDomain) domainTarget;
        }
        if (editingDomain == null && provider != null) {
            editingDomain = provider.findEditingDomain(activation, context);
        }
        return editingDomain;
    }

    /**
     * Helper class for constructing the job execution from individual commands.
     *
     * @author Abel Hegedus
     *
     */
    public class JobExecutionHelper{

        public static final String COMMAND_EXECUTING_JOB_COMMAND = "org.eclipse.viatra.transformation.evm.CommandExecutingJob.Command";

        private final EditingDomain editingDomain;
        private final Activation<? extends EventAtom> activation;
        private final Context context;
        private final CompoundCommand compoundCommand;

        /**
         *
         */
        public JobExecutionHelper(final EditingDomain domain, final Activation<? extends EventAtom> activation, final Context context) {
            this.editingDomain = domain;
            this.activation = activation;
            this.context = context;
            this.compoundCommand = new CompoundCommand(COMMAND_EXECUTING_JOB_COMMAND);
        }

        /**
         * @return the editingDomain
         */
        public EditingDomain getEditingDomain() {
            return editingDomain;
        }

        /**
         * @return the activation
         */
        public Activation<? extends EventAtom> getActivation() {
            return activation;
        }

        /**
         * @return the context
         */
        public Context getContext() {
            return context;
        }

        /**
         * Calls {@link CompoundCommand#appendAndExecute(Command)} with the provided subcommand
         *
         * @param subcommand
         * @return the result of {@link CompoundCommand#appendAndExecute(Command)} executed with the provided subcommand
         */
        public boolean appendAndExecuteSubCommand(final Command subcommand) {
            return this.compoundCommand.appendAndExecute(subcommand);
        }

    }

}
