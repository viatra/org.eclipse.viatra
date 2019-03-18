/*******************************************************************************
 * Copyright (c) 2010-2012, Zoltan Ujhelyi, Istvan Rath and Daniel Varro
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-v20.html.
 * 
 * SPDX-License-Identifier: EPL-2.0
 *******************************************************************************/
package org.eclipse.viatra.query.patternlanguage.emf.serializer;

import org.eclipse.emf.ecore.ENamedElement;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.xtext.CrossReference;
import org.eclipse.xtext.nodemodel.INode;
import org.eclipse.xtext.serializer.diagnostic.ISerializationDiagnostic.Acceptor;
import org.eclipse.xtext.serializer.tokens.CrossReferenceSerializer;

public class EMFPatternLanguageCrossRefSerializer extends CrossReferenceSerializer {

    @Override
    public String serializeCrossRef(EObject semanticObject, CrossReference crossref, EObject target, INode node,
            Acceptor errors) {
        if (target instanceof EPackage) {
            return String.format("\"%s\"", ((EPackage) target).getNsURI());
        } else if (target instanceof ENamedElement) {
            return ((ENamedElement) target).getName();
        }
        return super.serializeCrossRef(semanticObject, crossref, target, node, errors);
    }

}
