/*******************************************************************************
 * Copyright (c) 2010-2012, Zoltan Ujhelyi, Istvan Rath and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Zoltan Ujhelyi - initial API and implementation
 *******************************************************************************/

package org.eclipse.viatra.query.patternlanguage.emf.ide.highlight;

import org.eclipse.emf.common.util.TreeIterator;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.viatra.query.patternlanguage.annotations.PatternAnnotationProvider;
import org.eclipse.viatra.query.patternlanguage.emf.eMFPatternLanguage.ClassType;
import org.eclipse.viatra.query.patternlanguage.emf.eMFPatternLanguage.ReferenceType;
import org.eclipse.viatra.query.patternlanguage.patternLanguage.Annotation;
import org.eclipse.viatra.query.patternlanguage.patternLanguage.AnnotationParameter;
import org.eclipse.xtext.ide.editor.syntaxcoloring.IHighlightedPositionAcceptor;
import org.eclipse.xtext.nodemodel.ICompositeNode;
import org.eclipse.xtext.nodemodel.ILeafNode;
import org.eclipse.xtext.nodemodel.INode;
import org.eclipse.xtext.nodemodel.util.NodeModelUtils;
import org.eclipse.xtext.resource.XtextResource;
import org.eclipse.xtext.util.CancelIndicator;
import org.eclipse.xtext.xbase.XAbstractFeatureCall;
import org.eclipse.xtext.xbase.XNumberLiteral;
import org.eclipse.xtext.xbase.annotations.xAnnotations.XAnnotation;
import org.eclipse.xtext.xbase.ide.highlighting.XbaseHighlightingCalculator;

import com.google.inject.Inject;

@SuppressWarnings("restriction")
public class EMFPatternLanguageHighlightingCalculator extends XbaseHighlightingCalculator {

    @Inject
    private PatternAnnotationProvider annotationProvider;

    @Override
    protected void searchAndHighlightElements(XtextResource resource, IHighlightedPositionAcceptor acceptor, CancelIndicator indicator) {
        TreeIterator<EObject> iterator = resource.getAllContents();
        while (iterator.hasNext()) {
            EObject object = iterator.next();
            if (object instanceof XAbstractFeatureCall) {
                computeFeatureCallHighlighting((XAbstractFeatureCall) object, acceptor);
            } else if (object instanceof XNumberLiteral) {
                // Handle XAnnotation in a special way because we want the @ highlighted too
                highlightNumberLiterals((XNumberLiteral) object, acceptor);
            } else if (object instanceof XAnnotation) {
                highlightAnnotation((XAnnotation) object, acceptor);
            } else if (object instanceof ClassType || object instanceof ReferenceType) {
                ICompositeNode node = NodeModelUtils.findActualNodeFor(object);
                highlightNode(acceptor, node, EMFPatternLanguageHighlightingStyles.METAMODEL_REFERENCE);
            } else if (object instanceof Annotation && annotationProvider.isDeprecated((Annotation) object)) {
                Annotation annotation = (Annotation) object;
                ICompositeNode compositeNode = NodeModelUtils.findActualNodeFor(annotation);
                INode node = null;
                for (ILeafNode leafNode : compositeNode.getLeafNodes()) {
                    if (leafNode.getText().equals(annotation.getName())) {
                        node = leafNode;
                        break;
                    }
                }
                node = (node == null) ? compositeNode : node;
                highlightNode(acceptor, node, EMFPatternLanguageHighlightingStyles.DEPRECATED_MEMBERS);
            } else if (object instanceof AnnotationParameter
                    && annotationProvider.isDeprecated((AnnotationParameter) object)) {
                ICompositeNode compositeNode = NodeModelUtils.findActualNodeFor(object);
                INode node = null;
                for (ILeafNode leafNode : compositeNode.getLeafNodes()) {
                    if (leafNode.getText().equals(((AnnotationParameter) object).getName())) {
                        node = leafNode;
                        break;
                    }
                }
                node = (node == null) ? compositeNode : node;

                highlightNode(acceptor, node, EMFPatternLanguageHighlightingStyles.DEPRECATED_MEMBERS);
            } else {
                computeReferencedJvmTypeHighlighting(acceptor, object, indicator);
            }
        }
    }

}
