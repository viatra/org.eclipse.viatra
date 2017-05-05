/*******************************************************************************
 * Copyright (c) 2010-2015, Bergmann Gabor, Istvan Rath and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Bergmann Gabor - initial API and implementation
 *******************************************************************************/
package org.eclipse.viatra.query.runtime.matchers.context.common;

import org.eclipse.viatra.query.runtime.matchers.context.IInputKey;


/**
 * An input key that is identified by a single wrapped object and the class of the wrapper. 
 * @author Bergmann Gabor
 *
 */
public abstract class BaseInputKeyWrapper<Wrapped> implements IInputKey {
    protected Wrapped wrappedKey;

    public BaseInputKeyWrapper(Wrapped wrappedKey) {
        super();
        this.wrappedKey = wrappedKey;
    }

    public Wrapped getWrappedKey() {
        return wrappedKey;
    }
    
    
    @Override
    public int hashCode() {
        return ((wrappedKey == null) ? 0 : wrappedKey.hashCode());
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (!(this.getClass().equals(obj.getClass())))
            return false;
        BaseInputKeyWrapper other = (BaseInputKeyWrapper) obj;
        if (wrappedKey == null) {
            if (other.wrappedKey != null)
                return false;
        } else if (!wrappedKey.equals(other.wrappedKey))
            return false;
        return true;
    }


}
