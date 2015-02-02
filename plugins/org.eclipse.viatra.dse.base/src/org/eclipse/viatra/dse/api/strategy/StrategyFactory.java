/*******************************************************************************
 * Copyright (c) 2010-2014, Miklos Foldenyi, Andras Szabolcs Nagy, Abel Hegedus, Akos Horvath, Zoltan Ujhelyi and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Contributors:
 *   Miklos Foldenyi - initial API and implementation
 *   Andras Szabolcs Nagy - initial API and implementation
 *******************************************************************************/
package org.eclipse.viatra.dse.api.strategy;

import org.eclipse.viatra.dse.api.strategy.interfaces.IExplorerThread;
import org.eclipse.viatra.dse.api.strategy.interfaces.IExplorerThreadFactory;
import org.eclipse.viatra.dse.base.ExplorerThread;
import org.eclipse.viatra.dse.base.ThreadContext;

public class StrategyFactory implements IExplorerThreadFactory {

    @Override
    public IExplorerThread createStrategy(ThreadContext context) {
        return new ExplorerThread(context);
    }

}
