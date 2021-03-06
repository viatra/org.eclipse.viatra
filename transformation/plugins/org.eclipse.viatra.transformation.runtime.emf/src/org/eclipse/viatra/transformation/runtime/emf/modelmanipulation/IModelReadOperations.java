/*******************************************************************************
 * Copyright (c) 2010-2018, Gabor Bergmann, IncQuery Labs Ltd.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-v20.html.
 * 
 * SPDX-License-Identifier: EPL-2.0
 *******************************************************************************/
package org.eclipse.viatra.transformation.runtime.emf.modelmanipulation;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;

/**
 * Model manipulation interface for conventional EMF instance models.
 *
 * @noextend API may be extended in the future.
 * @author Gabor Bergmann
 * @since 2.1
 */
public interface IModelReadOperations extends IEcoreReadOperations<Resource, EObject> {

}
