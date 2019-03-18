/*******************************************************************************
 * Copyright (c) 2010-2012, Zoltan Ujhelyi, Istvan Rath and Daniel Varro
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-v20.html.
 * 
 * SPDX-License-Identifier: EPL-2.0
 *******************************************************************************/
package org.eclipse.viatra.query.tooling.generator.model.scoping;

import org.eclipse.emf.codegen.ecore.genmodel.GenModel;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.xtext.CrossReference;
import org.eclipse.xtext.nodemodel.INode;
import org.eclipse.xtext.serializer.diagnostic.ISerializationDiagnostic.Acceptor;
import org.eclipse.xtext.serializer.tokens.CrossReferenceSerializer;

public class GeneratorModelCrossRefSerializer extends CrossReferenceSerializer {

    @Override
    public String serializeCrossRef(EObject semanticObject, CrossReference crossref, EObject target, INode node,
            Acceptor errors) {
        if (target instanceof GenModel && target.eResource() != null) {
            return String.format("\"%s\"", target.eResource().getURI().toString());
        }
        return super.serializeCrossRef(semanticObject, crossref, target, node, errors);
    }

}
