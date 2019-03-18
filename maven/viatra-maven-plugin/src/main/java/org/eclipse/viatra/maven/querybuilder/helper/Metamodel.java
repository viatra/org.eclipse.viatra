/*******************************************************************************
 * Copyright (c) 2010-2014, Jozsef Makai, Zoltan Ujhelyi, Istvan Rath and Daniel Varro
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-v20.html.
 * 
 * SPDX-License-Identifier: EPL-2.0
 *******************************************************************************/

package org.eclipse.viatra.maven.querybuilder.helper;

public class Metamodel {

    /**
     * @property
     */
    private String packageClass;

    /**
     * @property
     */
    private String genmodelUri;

    public String getPackageClass() {
        return packageClass;
    }

    public void setPackageClass(String packageClass) {
        this.packageClass = packageClass;
    }

    public String getGenmodelUri() {
        return genmodelUri;
    }

    public void setGenmodelUri(String genmodelUri) {
        this.genmodelUri = genmodelUri;
    }

}
