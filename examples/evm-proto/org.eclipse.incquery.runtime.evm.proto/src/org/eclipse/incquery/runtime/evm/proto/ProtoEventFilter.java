/*******************************************************************************
 * Copyright (c) 2010-2013, Abel Hegedus, Istvan Rath and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Abel Hegedus - initial API and implementation
 *******************************************************************************/
package org.eclipse.incquery.runtime.evm.proto;

import org.eclipse.incquery.runtime.evm.api.event.EventFilter;

/**
 * @author Abel Hegedus
 *
 */
public class ProtoEventFilter implements EventFilter<String> {

    private String regexp;
    
    @Override
    public boolean isProcessable(String eventAtom) {
        return eventAtom.matches(regexp);
    }

    public ProtoEventFilter(String regexp) {
        this.regexp = regexp;
    }
    
}
