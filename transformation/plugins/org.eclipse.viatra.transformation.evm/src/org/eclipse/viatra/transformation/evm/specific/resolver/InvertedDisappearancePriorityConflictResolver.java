/*******************************************************************************
 * Copyright (c) 2010-2015, Zoltan Ujhelyi, Istvan Rath and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Zoltan Ujhelyi - initial API and implementation
 *******************************************************************************/
package org.eclipse.viatra.transformation.evm.specific.resolver;

import static com.google.common.base.Preconditions.checkArgument;

import java.util.Map;

import org.eclipse.viatra.transformation.evm.api.Activation;
import org.eclipse.viatra.transformation.evm.api.RuleSpecification;
import org.eclipse.viatra.transformation.evm.specific.crud.CRUDActivationStateEnum;

/**
 * A conflict resolver implementation that assigns a fixed priority for each
 * {@link RuleSpecification} it understands, and uses this priority for the
 * appeared activations, while uses the inverse of this for the disappeared
 * events.
 * <p>
 * This conflict resolver is especially useful to make sure all deletions
 * precede the creation of new model elements.
 * 
 * @author Zoltan Ujhelyi
 * @since 1.0
 *
 */
public class InvertedDisappearancePriorityConflictResolver extends FixedPriorityConflictResolver {

	public InvertedDisappearancePriorityConflictResolver() {
		super();
	}

	@Override
	protected FixedPriorityConflictSet createReconfigurableConflictSet() {
		return new InvertedDisappearancePriorityConflictSet(this, priorities);
	}

	public static class InvertedDisappearancePriorityConflictSet extends FixedPriorityConflictSet {

		public InvertedDisappearancePriorityConflictSet(FixedPriorityConflictResolver resolver,
				Map<RuleSpecification<?>, Integer> priorities) {
			super(resolver, priorities);
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
            checkArgument(activation != null, "Activation cannot be null!");
            Integer rulePriority = getRulePriority(activation);
            // it is possible that the activation has changed state after it was added previously
            priorityBuckets.remove((-1) * rulePriority, activation);
            return super.addActivation(activation);
        }
		
		@Override
		public boolean removeActivation(Activation<?> activation) {
			checkArgument(activation != null, "Activation cannot be null!");
			Integer rulePriority = getRulePriority(activation);
			// it is possible that the activation changed state before firing and is added to multiple buckets
			boolean removedFromInverted = priorityBuckets.remove((-1) * rulePriority, activation);
			boolean removed = super.removeActivation(activation);
            return removed || removedFromInverted;
		}

	}
}