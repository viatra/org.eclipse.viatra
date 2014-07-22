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

import org.eclipse.viatra.cep.vepl.vepl.AtomicEventPattern
import org.eclipse.viatra.cep.vepl.vepl.ComplexEventPattern
import org.eclipse.viatra.cep.vepl.vepl.EventPattern
import org.eclipse.viatra.cep.vepl.vepl.IQPatternEventPattern
import org.eclipse.viatra.cep.vepl.vepl.ModelElement
import org.eclipse.viatra.cep.vepl.vepl.PackagedModel
import org.eclipse.viatra.cep.vepl.vepl.ParameterizedPatternCall
import org.eclipse.viatra.cep.vepl.vepl.PatternUsage
import org.eclipse.viatra.cep.vepl.vepl.Rule
import org.eclipse.viatra.cep.vepl.vepl.TypedParameterList
import org.eclipse.viatra.cep.vepl.vepl.VeplPackage
import org.eclipse.xtext.validation.Check

class VeplValidator extends AbstractVeplValidator {

	private static val INVALID_NAME = 'invalidName'
	private static val INVALID_ARGUMENTS = 'invalidArguments'
	private static val INVALID_ACTION_IN_RULE = "invalidRuleActions"
	private static val MISSING_IQPATTERN_USAGE = "missingIqPatternUsage"

	@Check
	def uniqueName(ModelElement modelElement) {
		if(modelElement.name.nullOrEmpty) return;
		if(!(modelElement.eContainer instanceof PackagedModel)) return;

		var model = (modelElement.eContainer as PackagedModel)

		for (me : model.modelElements) {
			checkUniqueness(modelElement, me)
		}
	}

	def private checkUniqueness(ModelElement modelElement1, ModelElement modelElement2) {
		if (modelElement1.equals(modelElement2)) {
			return
		}
		if (modelElement1.name.equalsIgnoreCase(modelElement2.name))
			error("All model elements must have a unique name!", VeplPackage.Literals.MODEL_ELEMENT__NAME, INVALID_NAME)
	}

	@Check
	def validPatternCallArguments(ParameterizedPatternCall patternCall) {
		if(patternCall.parameterList == null || patternCall.eventPattern == null) return

		var parameterList = patternCall.parameterList
		var eventPatternParameter = patternCall.eventPattern

		var patternParameterNumber = getParameterNumber(eventPatternParameter)

		if (parameterList.parameters.empty && patternParameterNumber != 0) {
			error("Pattern call parameters must be specified!",
				VeplPackage.Literals.PARAMETERIZED_PATTERN_CALL__PARAMETER_LIST, INVALID_ARGUMENTS)
		}		
		if (parameterList.parameters.size != patternParameterNumber) {
			error("The exact number of parameters in the referred pattern must be specified!",
				VeplPackage.Literals.PARAMETERIZED_PATTERN_CALL__PARAMETER_LIST, INVALID_ARGUMENTS)
		}
	}

	@Check
	def checkRuleActions(Rule rule) {
		var actionHandler = rule.actionHandler
		var action = rule.action

		if (actionHandler == null && action == null) {
			error("There must be either an action handler or an action registered for this rule.",
				VeplPackage.Literals.MODEL_ELEMENT__NAME, INVALID_ACTION_IN_RULE)
		}

		if (actionHandler != null && action != null) {
			error("The rule has both an action handler and additional actions defined.",
				VeplPackage.Literals.MODEL_ELEMENT__NAME, INVALID_ACTION_IN_RULE)
		}
	}

	def private int getParameterNumber(EventPattern eventPattern) {
		switch (eventPattern) {
			AtomicEventPattern: getTypedParamterListSize(eventPattern.parameters)
			IQPatternEventPattern: getTypedParamterListSize(eventPattern.parameters)
			ComplexEventPattern: getTypedParamterListSize(eventPattern.parameters)
			default: 0
		}
	}

	def private int getTypedParamterListSize(TypedParameterList parameterList) {
		if(parameterList == null) return 0
		if(parameterList.parameters.nullOrEmpty) return 0
		return parameterList.parameters.size
	}

	@Check
	def explicitlyImportedIQPatternPackage(IQPatternEventPattern iqPatternEventPattern) {
		var packagedModel = (iqPatternEventPattern.eContainer as PackagedModel)
		if (!(packagedModel.usages.filter[u|u instanceof PatternUsage].size == 1)) {
			error(
				"Missing 'uses-patterns' statement for IncQuery patterns.",
				VeplPackage.Literals.IQ_PATTERN_EVENT_PATTERN__IQ_PATTERN_REF,
				MISSING_IQPATTERN_USAGE
			)
		}
	}
}
