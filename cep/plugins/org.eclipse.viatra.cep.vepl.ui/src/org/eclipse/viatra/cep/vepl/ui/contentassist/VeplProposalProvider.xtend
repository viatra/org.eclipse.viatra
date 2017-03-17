/*******************************************************************************
 * Copyright (c) 2004-2014, Istvan David, Istvan Rath and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Istvan David - initial API and implementation
 *******************************************************************************/
package org.eclipse.viatra.cep.vepl.ui.contentassist

import org.eclipse.emf.ecore.EObject
import org.eclipse.viatra.cep.vepl.vepl.VeplFactory
import org.eclipse.xtext.Assignment
import org.eclipse.xtext.RuleCall
import org.eclipse.xtext.ui.editor.contentassist.ContentAssistContext
import org.eclipse.xtext.ui.editor.contentassist.ICompletionProposalAcceptor

/**
 * see http://www.eclipse.org/Xtext/documentation.html#contentAssist on how to customize content assistant
 */
class VeplProposalProvider extends AbstractVeplProposalProvider {

	override complete_ComplexEventOperator(EObject model, RuleCall ruleCall, ContentAssistContext context,
		ICompletionProposalAcceptor acceptor) {
		createOperatorProposal("->", context, acceptor)
		createOperatorProposal("OR", context, acceptor)
		createOperatorProposal("AND", context, acceptor)
	}

	private def createOperatorProposal(String text, ContentAssistContext context, ICompletionProposalAcceptor acceptor) {
		val obj = VeplFactory.eINSTANCE.createComplexEventOperator

		acceptor.accept(createCompletionProposal(" " + text + " ", text, labelProvider.getImage(obj), context))
	}

	override completeChainedExpression_Operator(EObject model, Assignment assignment, ContentAssistContext context,
		ICompletionProposalAcceptor acceptor) {
		return
	}
}
