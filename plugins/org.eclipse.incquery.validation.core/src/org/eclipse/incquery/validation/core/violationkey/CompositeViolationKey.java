/*******************************************************************************
 * Copyright (c) 2010-2014, Balint Lorand, Zoltan Ujhelyi, Abel Hegedus, Tamas Szabo, Istvan Rath and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Zoltan Ujhelyi, Abel Hegedus, Tamas Szabo - original initial API and implementation
 *   Balint Lorand - revised API and implementation
 *******************************************************************************/

package org.eclipse.incquery.validation.core.violationkey;

import java.util.Map;

/**
 * A composite violation key serves as an object to uniquely identify a set of String - Object pairs.
 * 
 * @author Balint Lorand
 *
 */
public class CompositeViolationKey implements ViolationKey {

    private Map<String, Object> keyObjects;

    public CompositeViolationKey(Map<String, Object> keyObjects) {
        super();
        this.keyObjects = keyObjects;
    }

    public Map<String, Object> getKeyObjects() {
        return keyObjects;
    }

    public void setKeyObjects(Map<String, Object> keyObjects) {
        this.keyObjects = keyObjects;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((keyObjects == null) ? 0 : keyObjects.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        CompositeViolationKey other = (CompositeViolationKey) obj;
        if (keyObjects == null) {
            if (other.keyObjects != null)
                return false;
        } else if (!keyObjects.equals(other.keyObjects))
            return false;
        return true;
    }
}
