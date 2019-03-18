/*******************************************************************************
 * Copyright (c) 2010-2012, Tamas Szabo, Istvan Rath and Daniel Varro
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-v20.html.
 * 
 * SPDX-License-Identifier: EPL-2.0
 *******************************************************************************/
package org.eclipse.viatra.query.tooling.debug.variables;

import org.eclipse.viatra.query.runtime.api.scope.QueryScope;
import org.eclipse.viatra.query.runtime.emf.EMFScope;

public class NameConstants {
    
    private NameConstants() {/*Utility class constructor*/}
    
    public static final String VIATRA_QUERY_ENGINE_IMPL_NAME = "org.eclipse.viatra.query.runtime.internal.apiimpl.ViatraQueryEngineImpl";
    public static final String VIATRA_QUERY_SCOPE_NAME = QueryScope.class.getName();
    public static final String EMF_SCOPE_NAME = EMFScope.class.getName();
    
}
