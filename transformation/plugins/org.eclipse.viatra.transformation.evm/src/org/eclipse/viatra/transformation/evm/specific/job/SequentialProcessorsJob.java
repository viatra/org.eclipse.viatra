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
package org.eclipse.viatra.transformation.evm.specific.job;

import java.util.List;

import org.eclipse.viatra.query.runtime.api.IMatchProcessor;
import org.eclipse.viatra.query.runtime.api.IPatternMatch;
import org.eclipse.viatra.transformation.evm.specific.crud.CRUDActivationStateEnum;

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
     * @param cRUDActivationStateEnum
     * @param matchProcessors
     */
    public SequentialProcessorsJob(CRUDActivationStateEnum cRUDActivationStateEnum,
            final List<? extends IMatchProcessor<Match>> matchProcessors) {
        super(cRUDActivationStateEnum, new IMatchProcessor<Match>() {
            @Override
            public void process(Match match) {
                for (IMatchProcessor<Match> matchProcessor : matchProcessors) {
                    matchProcessor.process(match);
                }
            }
        });
    }

}
