/*******************************************************************************
 * Copyright (c) 2010-2012, Zoltan Ujhelyi, Tamas Szabo, Istvan Rath and Daniel Varro
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-v20.html.
 * 
 * SPDX-License-Identifier: EPL-2.0
 *******************************************************************************/

package org.eclipse.viatra.query.tooling.ui.wizards.internal;

import org.eclipse.emf.ecore.EClassifier;

/**
 * Instances of this class represents the specification of a pattern parameter. It has a parameter name and type
 * specification as an {@link EClassifier} instance.
 * 
 * @author Tamas Szabo
 * 
 */
public class ObjectParameter {

    private EClassifier object;
    private String parameterName;

    public ObjectParameter() {
        super();
        this.object = null;
        this.parameterName = "";
    }

    public ObjectParameter(EClassifier object, String parameterName) {
        super();
        this.object = object;
        this.parameterName = parameterName;
    }

    public EClassifier getObject() {
        return object;
    }

    public String getParameterName() {
        return parameterName;
    }

    public void setObject(EClassifier object) {
        this.object = object;
    }

    public void setParameterName(String parameterName) {
        this.parameterName = parameterName;
    }

}
