/*******************************************************************************
 * Copyright (c) 2010-2012, Abel Hegedus, Istvan Rath and Daniel Varro
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-v20.html.
 * 
 * SPDX-License-Identifier: EPL-2.0
 *******************************************************************************/
package org.eclipse.viatra.addon.querybasedfeatures.runtime;

import java.util.List;

import org.eclipse.emf.common.util.EList;

/**
 * @author Abel Hegedus
 * 
 */
public interface IQueryBasedFeatureHandler {

    Object getValue(Object source);

    int getIntValue(Object source);

    Object getSingleReferenceValue(Object source);

    List<?> getManyReferenceValue(Object source);

    EList getManyReferenceValueAsEList(Object source);

}