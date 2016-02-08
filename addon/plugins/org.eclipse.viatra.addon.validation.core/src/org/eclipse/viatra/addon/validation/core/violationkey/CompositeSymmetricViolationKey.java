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

package org.eclipse.viatra.addon.validation.core.violationkey;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * A composite and symmetric violation key serves as an object to uniquely identify a set of String - Object pairs with
 * the possibility of symmetric equivalence relationships between pairs.
 * 
 * @author Balint Lorand
 *
 */
public class CompositeSymmetricViolationKey implements ViolationKey {

    private Map<String, Object> keyObjects;
    private Set<List<String>> symmetricKeyObjectNames;

    public CompositeSymmetricViolationKey(Map<String, Object> keyObjects, Set<List<String>> symmetricKeyObjectNames) {
        super();
        this.keyObjects = keyObjects;
        this.symmetricKeyObjectNames = symmetricKeyObjectNames;
    }

    public Map<String, Object> getKeyObjects() {
        return keyObjects;
    }

    public void setKeyObjects(Map<String, Object> keyObjects) {
        this.keyObjects = keyObjects;
    }

    public Set<List<String>> getSymmetricKeyObjectNames() {
        return symmetricKeyObjectNames;
    }

    public void setSymmetricKeyObjectNames(Set<List<String>> symmetricKeyObjectNames) {
        this.symmetricKeyObjectNames = symmetricKeyObjectNames;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        if (keyObjects == null) {
            result = prime * result;
        } else {
            int parametersHash = 0;
            for (Map.Entry<String, Object> entry : keyObjects.entrySet()) {
                boolean nonSymmetric = true;
                for (List<String> symmetrics : symmetricKeyObjectNames) {
                    if (symmetrics.contains(entry.getKey())) {
                        parametersHash += ((symmetrics.get(0) == null) ? 0 : symmetrics.get(0).hashCode())
                                ^ ((entry.getValue() == null) ? 0 : entry.getValue().hashCode());
                        nonSymmetric = false;
                        break;
                    }
                }
                if (nonSymmetric) {
                    parametersHash += entry.hashCode();
                }
            }
            result = prime * result + parametersHash;
        }
        result = prime * result + ((symmetricKeyObjectNames == null) ? 0 : symmetricKeyObjectNames.hashCode());
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
        CompositeSymmetricViolationKey other = (CompositeSymmetricViolationKey) obj;
        if (keyObjects == null) {
            if (other.keyObjects != null)
                return false;
        } else {
            for (Map.Entry<String, Object> entry : keyObjects.entrySet()) {
                if (entry.getValue().equals(other.keyObjects.get(entry.getKey()))) {
                    continue;
                } else {
                    boolean symmetric = false;
                    for (List<String> symmetrics : symmetricKeyObjectNames) {
                        if (symmetrics.contains(entry.getKey())) {
                            symmetric = true;
                            boolean match = false;
                            for (String key : symmetrics) {
                                if (entry.getValue().equals(other.keyObjects.get(key))) {
                                    match = true;
                                    break;
                                }
                            }
                            if (!match) {
                                return false;
                            }
                            break;
                        }
                    }
                    if (!symmetric) {
                        return false;
                    }
                }
            }
        }
        if (symmetricKeyObjectNames == null) {
            if (other.symmetricKeyObjectNames != null)
                return false;
        } else if (!symmetricKeyObjectNames.equals(other.symmetricKeyObjectNames))
            return false;
        return true;
    }

}
