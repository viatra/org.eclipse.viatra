/*******************************************************************************
 * Copyright (c) 2010-2012, Mark Czotter, Istvan Rath and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Mark Czotter - initial API and implementation
 *******************************************************************************/
package org.eclipse.viatra.query.runtime.api.impl;

import org.eclipse.emf.common.notify.Notifier;
import org.eclipse.viatra.query.runtime.api.AdvancedIncQueryEngine;
import org.eclipse.viatra.query.runtime.api.IQueryGroup;
import org.eclipse.viatra.query.runtime.api.IncQueryEngine;
import org.eclipse.viatra.query.runtime.exception.IncQueryException;

/**
 * Base implementation of {@link IQueryGroup}.
 *
 * @author Mark Czotter
 *
 */
public abstract class BasePatternGroup implements IQueryGroup {

    @Override
    public void prepare(Notifier emfRoot) throws IncQueryException {
        prepare(AdvancedIncQueryEngine.on(emfRoot));
    }

    @Override
    public void prepare(IncQueryEngine engine) throws IncQueryException {
    	prepare(AdvancedIncQueryEngine.from(engine));
    }
    
    protected void prepare(AdvancedIncQueryEngine engine) throws IncQueryException {
        engine.prepareGroup(this, null /* default options */);
    }
    

}
