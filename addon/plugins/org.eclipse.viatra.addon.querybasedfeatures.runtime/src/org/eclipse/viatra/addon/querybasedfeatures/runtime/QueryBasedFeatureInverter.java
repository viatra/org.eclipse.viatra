/*******************************************************************************
 * Copyright (c) 2010-2012, Abel Hegedus, Istvan Rath and Daniel Varro
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-v20.html.
 * 
 * SPDX-License-Identifier: EPL-2.0
 *******************************************************************************/
package org.eclipse.viatra.addon.querybasedfeatures.runtime;

public interface QueryBasedFeatureInverter<ComputedType, StorageType> {
    /**
     * Return the storage value for the computed value.
     */
    StorageType invert(ComputedType computedValue);

    /**
     * Validate the computed value to ensure that inverting is possible
     */
    ComputedType validate(ComputedType computedValue);
}