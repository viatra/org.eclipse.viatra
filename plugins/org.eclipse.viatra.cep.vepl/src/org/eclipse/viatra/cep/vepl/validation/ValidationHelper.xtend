/*******************************************************************************
 * Copyright (c) 2004-2014, Istvan David, Istvan Rath and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Istvan David - initial API and implementation
 *******************************************************************************/
package org.eclipse.viatra.cep.vepl.validation

import org.eclipse.viatra.cep.vepl.vepl.Atom
import org.eclipse.viatra.cep.vepl.vepl.ComplexEventExpression
import org.eclipse.viatra.cep.vepl.vepl.ParameterizedPatternCall
import org.eclipse.viatra.cep.vepl.vepl.Rule

/**
 * Helper class for the {@link VeplValidator}.
 */
class ValidationHelper {
	def static dispatch hasTimewindow(ComplexEventExpression expression) {
		return expression.timewindow != null
	}

	def static dispatch hasTimewindow(Atom atom) {
		return atom.timewindow != null
	}

	def static dispatch hasMultiplicity(ComplexEventExpression expression) {
		return expression.multiplicity != null
	}

	def static dispatch hasMultiplicity(Atom atom) {
		return atom.multiplicity != null
	}

	def static hasParameterList(ParameterizedPatternCall patternCall) {
		return patternCall.parameterList != null
	}

	def static hasAction(Rule rule) {
		return rule.action != null
	}

	def static hasActionHandler(Rule rule) {
		return rule.actionHandler != null
	}
}
