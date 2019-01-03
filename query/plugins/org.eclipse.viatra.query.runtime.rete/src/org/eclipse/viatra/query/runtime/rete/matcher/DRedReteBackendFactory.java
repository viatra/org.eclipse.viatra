/*******************************************************************************
 * Copyright (c) 2010-2019, Tamas Szabo, Istvan Rath and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Tamas Szabo - initial API and implementation
 *******************************************************************************/
package org.eclipse.viatra.query.runtime.rete.matcher;

import org.eclipse.viatra.query.runtime.matchers.backend.IQueryBackend;
import org.eclipse.viatra.query.runtime.matchers.context.IQueryBackendContext;

/**
 * A {@link ReteBackendFactory} implementation that creates {@link ReteEngine}s that use delete and re-derive
 * evaluation.
 * 
 * @author Tamas Szabo
 * @since 2.2
 */
public class DRedReteBackendFactory extends ReteBackendFactory {

    public static final DRedReteBackendFactory INSTANCE = new DRedReteBackendFactory();

    @Override
    public IQueryBackend create(IQueryBackendContext context) {
        return create(context, true, false);
    }

    @Override
    public int hashCode() {
        return DRedReteBackendFactory.class.hashCode();
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof DRedReteBackendFactory)) {
            return false;
        }
        return true;
    }

}
