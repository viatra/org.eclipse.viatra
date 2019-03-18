/*******************************************************************************
 * Copyright (c) 2010-2014, Abel Hegedus, Istvan Rath and Daniel Varro
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-v20.html.
 * 
 * SPDX-License-Identifier: EPL-2.0
 *******************************************************************************/
package org.eclipse.viatra.transformation.evm.specific.job;

import java.util.List;
import java.util.function.Consumer;

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
            final List<? extends Consumer<Match>> matchProcessors) {
        super(cRUDActivationStateEnum, match -> matchProcessors.forEach(processor -> processor.accept(match)));
    }

}
