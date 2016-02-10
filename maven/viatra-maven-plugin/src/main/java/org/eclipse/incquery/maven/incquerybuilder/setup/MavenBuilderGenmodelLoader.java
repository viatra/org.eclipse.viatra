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

package org.eclipse.incquery.maven.incquerybuilder.setup;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.incquery.patternlanguage.emf.IGenmodelMappingLoader;

public class MavenBuilderGenmodelLoader implements IGenmodelMappingLoader {

    protected static MavenBuilderGenmodelLoader genmodelLoader = new MavenBuilderGenmodelLoader();

    private Map<String, String> genModels;

    protected MavenBuilderGenmodelLoader() {
        genModels = new HashMap<String, String>();
    }

    public static void addGenmodel(String modelNsUri, String genmodelUri) {
        genmodelLoader.putGenmodel(modelNsUri, genmodelUri);
    }

    public Map<String, String> loadGenmodels() {
        return genModels;
    }

    protected void putGenmodel(String modelNsUri, String genmodelUri) {
        genModels.put(modelNsUri, genmodelUri);
    }

    public static MavenBuilderGenmodelLoader getInstance() {
        return genmodelLoader;
    }

}
