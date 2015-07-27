/*******************************************************************************
 * Copyright (c) 2004-2015, Istvan David, Istvan Rath and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Istvan David - initial API and implementation
 *******************************************************************************/

package org.eclipse.viatra.cep.core.engine.compiler;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.ResourceSet;

import com.google.common.base.Preconditions;

public class TransformationBasedCompiler {
    public final static URI AUTOMATON_MODEL_URI = URI.createURI("cep/automaton.cep");
    public final static URI EVENT_MODEL_URI = URI.createURI("cep/events.cep");
    public final static URI TRACE_MODEL_URI = URI.createURI("cep/trace.cep");
    public final static String OMITTED_PARAMETER_SYMBOLIC_NAME = "_";

    private Pattern2AutomatonMapping mapping;

    public TransformationBasedCompiler() {
    }

    public void compile(ResourceSet resourceSet) {
        Preconditions.checkArgument(mapping == null);
        mapping = new Pattern2AutomatonMapping(resourceSet);
        mapping.mapPatterns();
    }
}