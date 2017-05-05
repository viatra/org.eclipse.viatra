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
package org.eclipse.viatra.cep.vepl.ui.syntaxhighlight

import org.eclipse.viatra.cep.vepl.vepl.AbstractMultiplicity
import org.eclipse.viatra.cep.vepl.vepl.Timewindow
import org.eclipse.xtext.nodemodel.INode
import org.eclipse.xtext.nodemodel.util.NodeModelUtils
import org.eclipse.xtext.resource.XtextResource
import org.eclipse.xtext.ui.editor.syntaxcoloring.IHighlightedPositionAcceptor
import org.eclipse.xtext.xbase.ui.highlighting.XbaseHighlightingCalculator

class VeplSemanticHighlightingCalculator extends XbaseHighlightingCalculator {

    override protected searchAndHighlightElements(XtextResource resource, IHighlightedPositionAcceptor acceptor) {
        val iterator = resource.getAllContents();
        while (iterator.hasNext()) {
            val object = iterator.next();
            if (object instanceof Timewindow) {
                val node = NodeModelUtils.findActualNodeFor(object);
                val children = node.getChildren();

                children.filter[ch|ch.bracketNode].forEach[bracketNode|
                    highlightNode(acceptor, bracketNode, CepDslHighlightingConfiguration.KEYWORD_ID);]
            } else if (object instanceof AbstractMultiplicity) {
                val node = NodeModelUtils.findActualNodeFor(object);
                val leafNodes = node.firstChild.leafNodes

                val braceNodes = leafNodes.filter[n|n.braceNode]
                braceNodes.forEach[n|highlightNode(acceptor, n, CepDslHighlightingConfiguration.KEYWORD_ID);]
                val valueNode = leafNodes.filter[n|!n.braceNode].head
                highlightNode(acceptor, valueNode, CepDslHighlightingConfiguration.NUMBER_ID);
            } else {
                computeReferencedJvmTypeHighlighting(acceptor, object);
            }
        }
    }

    def private isBracketNode(INode node) {
        node.text.equals("[") || node.text.equals("]")
    }

    def private isBraceNode(INode node) {
        node.text.equals("{") || node.text.equals("}")
    }
}
