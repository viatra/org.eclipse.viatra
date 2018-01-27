/**
 * Copyright (c) 2004-2014, Istvan David, Istvan Rath and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *   Istvan David - initial API and implementation
 */
package org.eclipse.viatra.cep.vepl.ui.contentassist;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.swt.graphics.Image;
import org.eclipse.viatra.cep.vepl.ui.contentassist.AbstractVeplProposalProvider;
import org.eclipse.viatra.cep.vepl.vepl.ComplexEventOperator;
import org.eclipse.viatra.cep.vepl.vepl.VeplFactory;
import org.eclipse.xtext.Assignment;
import org.eclipse.xtext.RuleCall;
import org.eclipse.xtext.ui.editor.contentassist.ContentAssistContext;
import org.eclipse.xtext.ui.editor.contentassist.ICompletionProposalAcceptor;

/**
 * see http://www.eclipse.org/Xtext/documentation.html#contentAssist on how to customize content assistant
 */
@SuppressWarnings("all")
public class VeplProposalProvider extends AbstractVeplProposalProvider {
  @Override
  public void complete_ComplexEventOperator(final EObject model, final RuleCall ruleCall, final ContentAssistContext context, final ICompletionProposalAcceptor acceptor) {
    this.createOperatorProposal("->", context, acceptor);
    this.createOperatorProposal("OR", context, acceptor);
    this.createOperatorProposal("AND", context, acceptor);
  }
  
  private void createOperatorProposal(final String text, final ContentAssistContext context, final ICompletionProposalAcceptor acceptor) {
    final ComplexEventOperator obj = VeplFactory.eINSTANCE.createComplexEventOperator();
    ILabelProvider _labelProvider = this.getLabelProvider();
    Image _image = _labelProvider.getImage(obj);
    ICompletionProposal _createCompletionProposal = this.createCompletionProposal(((" " + text) + " "), text, _image, context);
    acceptor.accept(_createCompletionProposal);
  }
  
  @Override
  public void completeChainedExpression_Operator(final EObject model, final Assignment assignment, final ContentAssistContext context, final ICompletionProposalAcceptor acceptor) {
    return;
  }
}
