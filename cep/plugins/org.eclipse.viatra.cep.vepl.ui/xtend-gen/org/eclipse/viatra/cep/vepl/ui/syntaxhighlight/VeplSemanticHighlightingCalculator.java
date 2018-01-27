/**
 * Copyright (c) 2004-2015, Istvan David, Istvan Rath and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Istvan David - initial API and implementation
 */
package org.eclipse.viatra.cep.vepl.ui.syntaxhighlight;

import org.eclipse.emf.common.util.TreeIterator;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.viatra.cep.vepl.ui.syntaxhighlight.CepDslHighlightingConfiguration;
import org.eclipse.viatra.cep.vepl.vepl.AbstractMultiplicity;
import org.eclipse.viatra.cep.vepl.vepl.Timewindow;
import org.eclipse.xtext.nodemodel.BidiIterable;
import org.eclipse.xtext.nodemodel.ICompositeNode;
import org.eclipse.xtext.nodemodel.ILeafNode;
import org.eclipse.xtext.nodemodel.INode;
import org.eclipse.xtext.nodemodel.util.NodeModelUtils;
import org.eclipse.xtext.resource.XtextResource;
import org.eclipse.xtext.ui.editor.syntaxcoloring.IHighlightedPositionAcceptor;
import org.eclipse.xtext.xbase.lib.Functions.Function1;
import org.eclipse.xtext.xbase.lib.IterableExtensions;
import org.eclipse.xtext.xbase.lib.Procedures.Procedure1;
import org.eclipse.xtext.xbase.ui.highlighting.XbaseHighlightingCalculator;

@SuppressWarnings("all")
public class VeplSemanticHighlightingCalculator extends XbaseHighlightingCalculator {
  @Override
  protected void searchAndHighlightElements(final XtextResource resource, final IHighlightedPositionAcceptor acceptor) {
    final TreeIterator<EObject> iterator = resource.getAllContents();
    while (iterator.hasNext()) {
      {
        final EObject object = iterator.next();
        if ((object instanceof Timewindow)) {
          final ICompositeNode node = NodeModelUtils.findActualNodeFor(object);
          final BidiIterable<INode> children = node.getChildren();
          final Function1<INode, Boolean> _function = new Function1<INode, Boolean>() {
            @Override
            public Boolean apply(final INode ch) {
              return Boolean.valueOf(VeplSemanticHighlightingCalculator.this.isBracketNode(ch));
            }
          };
          Iterable<INode> _filter = IterableExtensions.<INode>filter(children, _function);
          final Procedure1<INode> _function_1 = new Procedure1<INode>() {
            @Override
            public void apply(final INode bracketNode) {
              VeplSemanticHighlightingCalculator.this.highlightNode(acceptor, bracketNode, CepDslHighlightingConfiguration.KEYWORD_ID);
            }
          };
          IterableExtensions.<INode>forEach(_filter, _function_1);
        } else {
          if ((object instanceof AbstractMultiplicity)) {
            final ICompositeNode node_1 = NodeModelUtils.findActualNodeFor(object);
            INode _firstChild = node_1.getFirstChild();
            final Iterable<ILeafNode> leafNodes = _firstChild.getLeafNodes();
            final Function1<ILeafNode, Boolean> _function_2 = new Function1<ILeafNode, Boolean>() {
              @Override
              public Boolean apply(final ILeafNode n) {
                return Boolean.valueOf(VeplSemanticHighlightingCalculator.this.isBraceNode(n));
              }
            };
            final Iterable<ILeafNode> braceNodes = IterableExtensions.<ILeafNode>filter(leafNodes, _function_2);
            final Procedure1<ILeafNode> _function_3 = new Procedure1<ILeafNode>() {
              @Override
              public void apply(final ILeafNode n) {
                VeplSemanticHighlightingCalculator.this.highlightNode(acceptor, n, CepDslHighlightingConfiguration.KEYWORD_ID);
              }
            };
            IterableExtensions.<ILeafNode>forEach(braceNodes, _function_3);
            final Function1<ILeafNode, Boolean> _function_4 = new Function1<ILeafNode, Boolean>() {
              @Override
              public Boolean apply(final ILeafNode n) {
                boolean _isBraceNode = VeplSemanticHighlightingCalculator.this.isBraceNode(n);
                return Boolean.valueOf((!_isBraceNode));
              }
            };
            Iterable<ILeafNode> _filter_1 = IterableExtensions.<ILeafNode>filter(leafNodes, _function_4);
            final ILeafNode valueNode = IterableExtensions.<ILeafNode>head(_filter_1);
            this.highlightNode(acceptor, valueNode, CepDslHighlightingConfiguration.NUMBER_ID);
          } else {
            this.computeReferencedJvmTypeHighlighting(acceptor, object);
          }
        }
      }
    }
  }
  
  private boolean isBracketNode(final INode node) {
    boolean _or = false;
    String _text = node.getText();
    boolean _equals = _text.equals("[");
    if (_equals) {
      _or = true;
    } else {
      String _text_1 = node.getText();
      boolean _equals_1 = _text_1.equals("]");
      _or = _equals_1;
    }
    return _or;
  }
  
  private boolean isBraceNode(final INode node) {
    boolean _or = false;
    String _text = node.getText();
    boolean _equals = _text.equals("{");
    if (_equals) {
      _or = true;
    } else {
      String _text_1 = node.getText();
      boolean _equals_1 = _text_1.equals("}");
      _or = _equals_1;
    }
    return _or;
  }
}
