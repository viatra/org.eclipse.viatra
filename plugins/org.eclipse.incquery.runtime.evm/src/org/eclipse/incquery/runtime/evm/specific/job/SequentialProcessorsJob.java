/*******************************************************************************
 * Copyright (c) 2010-2014, Abel Hegedus, Istvan Rath and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Abel Hegedus - initial API and implementation
 *******************************************************************************/
package org.eclipse.incquery.runtime.evm.specific.job;

import java.util.List;

import org.eclipse.incquery.runtime.api.IMatchProcessor;
import org.eclipse.incquery.runtime.api.IPatternMatch;
import org.eclipse.incquery.runtime.evm.specific.event.IncQueryActivationStateEnum;

/**
 * Sequentially executes a list of match processors in a stateless manner.
 * 
 * @author Abel Hegedus
 *
 */
public class SequentialProcessorsJob<Match extends IPatternMatch> extends StatelessJob<Match> {

    /**
     * Creates a stateless job for the given state and list of processors.
     * 
     * @param incQueryActivationStateEnum
     * @param matchProcessors
     */
    public SequentialProcessorsJob(IncQueryActivationStateEnum incQueryActivationStateEnum,
            final List<? extends IMatchProcessor<Match>> matchProcessors) {
        super(incQueryActivationStateEnum, new IMatchProcessor<Match>() {
            @Override
            public void process(Match match) {
                for (IMatchProcessor<Match> matchProcessor : matchProcessors) {
                    matchProcessor.process(match);
                }
            }
        });
    }

}
