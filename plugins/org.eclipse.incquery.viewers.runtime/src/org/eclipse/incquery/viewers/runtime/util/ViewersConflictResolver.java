/*******************************************************************************
 * Copyright (c) 2010-2013, Zoltan Ujhelyi, Istvan Rath and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Zoltan Ujhelyi - initial API and implementation
 *******************************************************************************/
package org.eclipse.incquery.viewers.runtime.util;

import static com.google.common.base.Preconditions.checkArgument;

import java.util.Map;

import org.eclipse.incquery.runtime.evm.api.Activation;
import org.eclipse.incquery.runtime.evm.api.RuleSpecification;
import org.eclipse.incquery.runtime.evm.specific.event.IncQueryActivationStateEnum;
import org.eclipse.incquery.runtime.evm.specific.resolver.FixedPriorityConflictResolver;
import org.eclipse.incquery.runtime.evm.specific.resolver.FixedPriorityConflictSet;

/**
 * A specific conflict resolver to support inverse priorities for node deletions
 *  
 * @author Zoltan Ujhelyi
 *
 */
public class ViewersConflictResolver extends FixedPriorityConflictResolver {

	@Override
	protected FixedPriorityConflictSet createReconfigurableConflictSet() {
		return new ViewerConflictSet(this, priorities);
	}

	public class ViewerConflictSet extends FixedPriorityConflictSet {

		public ViewerConflictSet(FixedPriorityConflictResolver resolver,
				Map<RuleSpecification<?>, Integer> priorities) {
			super(resolver, priorities);
		}

		@Override
		protected Integer getRulePriority(Activation<?> activation) {
			if (IncQueryActivationStateEnum.DISAPPEARED.equals(activation.getState())) {
				return (-1) * super.getRulePriority(activation);
			}
			return super.getRulePriority(activation);
		}


	    @Override
	    public boolean removeActivation(Activation<?> activation) {
	        checkArgument(activation != null, "Activation cannot be null!");
	        Integer rulePriority = getRulePriority(activation);
	        return priorityBuckets.remove(rulePriority, activation) 
	        	|| priorityBuckets.remove((-1) * rulePriority, activation);
	    }
        
	}
}
