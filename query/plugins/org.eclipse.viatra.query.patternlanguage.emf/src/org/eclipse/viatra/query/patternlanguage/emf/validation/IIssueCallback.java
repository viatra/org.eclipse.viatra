/*******************************************************************************
 * Copyright (c) 2010-2012, Zoltan Ujhelyi, Istvan Rath and Daniel Varro
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-v20.html.
 * 
 * SPDX-License-Identifier: EPL-2.0
 *******************************************************************************/
package org.eclipse.viatra.query.patternlanguage.emf.validation;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;

/**
 * @author Zoltan Ujhelyi
 * @since 2.0
 * 
 */
public interface IIssueCallback {

    public abstract void warning(String message, EObject source, EStructuralFeature feature, String code,
            String... issueData);

    public abstract void error(String message, EObject source, EStructuralFeature feature, String code,
            String... issueData);

}