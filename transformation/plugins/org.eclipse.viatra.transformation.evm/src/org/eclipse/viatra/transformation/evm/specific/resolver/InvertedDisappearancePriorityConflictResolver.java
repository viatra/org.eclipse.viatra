/*******************************************************************************
 * Copyright (c) 2010-2015, Zoltan Ujhelyi, Istvan Rath and Daniel Varro
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-v20.html.
 * 
 * SPDX-License-Identifier: EPL-2.0
 *******************************************************************************/
package org.eclipse.viatra.transformation.evm.specific.resolver;

import java.util.Map;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.eclipse.viatra.query.runtime.matchers.util.Preconditions;
import org.eclipse.viatra.query.runtime.util.ViatraQueryLoggingUtil;
import org.eclipse.viatra.transformation.evm.api.Activation;
import org.eclipse.viatra.transformation.evm.api.RuleSpecification;
import org.eclipse.viatra.transformation.evm.specific.crud.CRUDActivationStateEnum;

/**
 * A conflict resolver implementation that assigns a fixed priority for each {@link RuleSpecification} it understands,
 * and uses this priority for the appeared activations, while uses the inverse of this for the disappeared events.
 * <p>
 * This conflict resolver is especially useful to make sure all deletions precede the creation of new model elements.
 * <p>
 * <strong>NOTE</strong>: It is not recommended to use rules of zero priority with this conflict resolver, as in that
 * case the order of additions and deletions is unspecified. For this reason, starting VIATRA version 2.1 a default
 * priority of 1 is used instead.
 * 
 * @author Zoltan Ujhelyi
 * @since 1.0
 *
 */
public class InvertedDisappearancePriorityConflictResolver extends FixedPriorityConflictResolver {

    private static final String ZERO_PRIORITY_MESSAGE = "Priority 0 is set in inverted disappearance priority conflict resolver";
    Logger logger = ViatraQueryLoggingUtil.getLogger(getClass());
    
    /**
     * Initializes the conflict resolver with a default rule priority of 1.
     */
    public InvertedDisappearancePriorityConflictResolver() {
        super(1);
    }
    
    /**
     * Initializes the conflict resolver with the specified default rule priority.
     * @since 2.1
     */
    public InvertedDisappearancePriorityConflictResolver(int defaultPriority) {
        super(defaultPriority);
        if (defaultPriority == 0 && logger.getLevel().isGreaterOrEqual(Level.WARN)) {
            logger.warn(ZERO_PRIORITY_MESSAGE);
        }
    }

    @Override
    public void setPriority(RuleSpecification<?> specification, int priority) {
        if (priority == 0 && logger.getLevel().isGreaterOrEqual(Level.WARN)) {
            logger.warn(ZERO_PRIORITY_MESSAGE);
        }
        super.setPriority(specification, priority);
    }

    @Override
    protected FixedPriorityConflictSet createReconfigurableConflictSet() {
        return new InvertedDisappearancePriorityConflictSet(this, priorities, defaultPriority);
    }

    public static class InvertedDisappearancePriorityConflictSet extends FixedPriorityConflictSet {

        public InvertedDisappearancePriorityConflictSet(FixedPriorityConflictResolver resolver,
                Map<RuleSpecification<?>, Integer> priorities) {
            this(resolver, priorities, 1);
        }
        
        /**
         * @since 2.1
         */
        public InvertedDisappearancePriorityConflictSet(FixedPriorityConflictResolver resolver,
                Map<RuleSpecification<?>, Integer> priorities, int defaultPriority) {
            super(resolver, priorities, defaultPriority);
        }

        @Override
        protected Integer getRulePriority(Activation<?> activation) {
            if (CRUDActivationStateEnum.DELETED.equals(activation.getState())) {
                return (-1) * super.getRulePriority(activation);
            }
            return super.getRulePriority(activation);
        }

        @Override
        public boolean addActivation(Activation<?> activation) {
            Preconditions.checkArgument(activation != null, "Activation cannot be null!");
            Integer rulePriority = getRulePriority(activation);
            // it is possible that the activation has changed state after it was added previously
            super.removeActivation(activation, (-1) * rulePriority);
            return super.addActivation(activation);
        }
        
        @Override
        public boolean removeActivation(Activation<?> activation) {
            Preconditions.checkArgument(activation != null, "Activation cannot be null!");
            Integer rulePriority = getRulePriority(activation);
            // it is possible that the activation changed state before firing and is added to multiple buckets
            boolean removedFromInverted = super.removeActivation(activation, (-1) * rulePriority);
            boolean removed = super.removeActivation(activation, rulePriority);
            return removed || removedFromInverted;
        }

    }
}