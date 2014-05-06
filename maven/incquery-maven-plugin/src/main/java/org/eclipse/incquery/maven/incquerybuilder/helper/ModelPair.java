/*******************************************************************************
 * Copyright (c) 2010-2014, Jozsef Makai, Zoltan Ujhelyi, Istvan Rath and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Jozsef Makai - initial API and implementation
 *******************************************************************************/

package org.eclipse.incquery.maven.incquerybuilder.helper;

public class ModelPair {

    /**
     * @property
     * @required
     */
    private String modelNsUri;

    /**
     * @property
     * @required
     */
    private String genmodelUri;

    public String getModelNsUri() {
        return modelNsUri;
    }

    public void setModelNsUri(String modelNsUri) {
        this.modelNsUri = modelNsUri;
    }

    public String getGenmodelUri() {
        return genmodelUri;
    }

    public void setGenmodelUri(String genmodelUri) {
        this.genmodelUri = genmodelUri;
    }

}
