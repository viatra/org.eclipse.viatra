/*******************************************************************************
 * Copyright (c) 2010-2012, Zoltan Ujhelyi, Istvan Rath and Daniel Varro
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-v20.html.
 * 
 * SPDX-License-Identifier: EPL-2.0
 *******************************************************************************/
package org.eclipse.viatra.query.patternlanguage.emf.scoping;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import org.apache.log4j.Logger;
import org.eclipse.emf.ecore.EEnum;
import org.eclipse.emf.ecore.EEnumLiteral;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.viatra.query.patternlanguage.emf.helper.PatternLanguageHelper;
import org.eclipse.viatra.query.patternlanguage.emf.vql.PatternLanguagePackage;
import org.eclipse.viatra.query.patternlanguage.emf.vql.EnumValue;
import org.eclipse.viatra.query.patternlanguage.emf.vql.PackageImport;
import org.eclipse.viatra.query.patternlanguage.emf.vql.PathExpressionConstraint;
import org.eclipse.xtext.conversion.IValueConverterService;
import org.eclipse.xtext.conversion.ValueConverterException;
import org.eclipse.xtext.linking.impl.DefaultLinkingService;
import org.eclipse.xtext.nodemodel.ILeafNode;
import org.eclipse.xtext.nodemodel.INode;

import com.google.inject.Inject;

public class EMFPatternLanguageLinkingService extends DefaultLinkingService {

    @Inject
    private Logger logger;
    @Inject
    private IValueConverterService valueConverterService;
    @Inject
    private IMetamodelProvider metamodelProvider;

    @Override
    public List<EObject> getLinkedObjects(EObject context, EReference ref, INode node) {
        if (Objects.equals(ref, PatternLanguagePackage.eINSTANCE.getPackageImport_EPackage()) && context instanceof PackageImport
                && node instanceof ILeafNode) {
            return getPackage((PackageImport) context, (ILeafNode) node);
        } else if (Objects.equals(ref, PatternLanguagePackage.eINSTANCE.getEnumValue_Literal()) && context instanceof EnumValue
                && node instanceof ILeafNode) {
            return getEnumLiteral((EnumValue) context, node);
        }
        return super.getLinkedObjects(context, ref, node);
    }
    
    private List<EObject> getEnumLiteral(EnumValue value, INode node) {
        EEnum type = null;
        if (value.getEnumeration() != null) {
            type = value.getEnumeration();
        } else if (value.eContainer() instanceof PathExpressionConstraint) {
            Optional<EEnum> optType = PatternLanguageHelper
                    .getPathExpressionEMFTailType((PathExpressionConstraint) value.eContainer())
                    .filter(EEnum.class::isInstance)
                    .map(EEnum.class::cast);
            if (!optType.isPresent()) {
                return Collections.emptyList();
            }
            type = optType.get();
        } else {
            return Collections.emptyList();
        }
        String typename = ((ILeafNode) node).getText();
        typename = (typename.startsWith("^")) ? typename.substring(1) : typename;
        EEnumLiteral literal = type.getEEnumLiteralByLiteral(typename);
        if (literal == null) {
            literal = type.getEEnumLiteral(typename);
        }
        if (literal != null) {
            return Collections.<EObject> singletonList(literal);
        } else {
            return Collections.emptyList();
        }
    }

    private List<EObject> getPackage(PackageImport context, ILeafNode text) {
        String nsUri = getMetamodelNsURI(text);
        if (nsUri == null) {
            return Collections.emptyList();
        }
        EPackage pack = metamodelProvider.loadEPackage(nsUri, context.eResource().getResourceSet());
        if (pack != null) {
            return Collections.<EObject> singletonList(pack);
        }
        return Collections.emptyList();
    }

    private String getMetamodelNsURI(ILeafNode text) {
        try {
            return (String) valueConverterService.toValue(text.getText(),
                    getLinkingHelper().getRuleNameFrom(text.getGrammarElement()), text);
        } catch (ValueConverterException e) {
            logger.debug("Exception on leaf '" + text.getText() + "'", e);
            return null;
        }
    }
}
