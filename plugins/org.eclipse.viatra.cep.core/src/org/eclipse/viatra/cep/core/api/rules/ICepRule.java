/*******************************************************************************
 * Copyright (c) 2004-2014, Istvan David, Istvan Rath and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Istvan David - initial API and implementation
 *******************************************************************************/

package org.eclipse.viatra.cep.core.api.rules;

import java.util.List;

import org.eclipse.incquery.runtime.evm.api.Job;
import org.eclipse.viatra.cep.core.api.patterns.IObservableComplexEventPattern;
import org.eclipse.viatra.cep.core.metamodels.events.EventPattern;

/**
 * Standard data structure for associating {@link EventPattern}s with {@link Job}s to be executed when patterns get
 * matched.
 * 
 * @author Istvan David
 * 
 */
public interface ICepRule {
    /**
     * @return the {@link EventPattern}s of the rule
     */
    List<EventPattern> getEventPatterns();

    /**
     * @return the {@link Job} to be executed when patterns get matched
     */
    Job<IObservableComplexEventPattern> getJob();
}
