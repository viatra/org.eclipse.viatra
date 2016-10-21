/*******************************************************************************
 * Copyright (c) 2010-2016, Gabor Bergmann, IncQueryLabs Ltd.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Gabor Bergmann - initial API and implementation
 *******************************************************************************/
package org.eclipse.viatra.query.runtime.rete.eval;

import org.eclipse.viatra.query.runtime.rete.network.ReteContainer;

/**
 * This interface is required for the communication between the evaluation core end the evaluator node.
 * @author Gabor Bergmann
 * @since 1.5
 */
public interface IEvaluatorNode {

    ReteContainer getReteContainer();

    String prettyPrintTraceInfoPatternList();
    

}
