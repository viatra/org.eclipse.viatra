/*******************************************************************************
 * Copyright (c) 2010-2016, Abel Hegedus, IncQuery Labs Ltd.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Abel Hegedus - initial API and implementation
 *******************************************************************************/
package org.eclipse.viatra.query.runtime.registry.impl;

import org.eclipse.viatra.query.runtime.registry.IQuerySpecificationRegistry;
import org.eclipse.viatra.query.runtime.registry.IQuerySpecificationRegistryEntry;

/**
 * Registry view implementation that considers all specifications relevant.
 * 
 * @author Abel Hegedus
 *
 */
public class GlobalRegistryView extends AbstractRegistryView {
    
    /**
     * Creates a new instance of the global view.
     * 
     * @param registry that defines the view
     */
    public GlobalRegistryView(IQuerySpecificationRegistry registry) {
        super(registry);
    }
    
    @Override
    protected boolean isEntryRelevant(IQuerySpecificationRegistryEntry entry) {
        return true;
    }

}
