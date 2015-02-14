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
import org.eclipse.viatra.cep.vepl.vepl.AtomicEventPattern
import org.eclipse.viatra.cep.vepl.vepl.ComplexEventPattern
import org.eclipse.viatra.cep.vepl.vepl.EventModel
import org.eclipse.viatra.cep.vepl.vepl.EventPattern
import org.eclipse.viatra.cep.vepl.vepl.Infinite
import org.eclipse.viatra.cep.vepl.vepl.ModelElement
import org.eclipse.viatra.cep.vepl.vepl.Multiplicity
import org.eclipse.viatra.cep.vepl.vepl.ParameterizedPatternCall
import org.eclipse.viatra.cep.vepl.vepl.QueryImport
import org.eclipse.viatra.cep.vepl.vepl.QueryResultChangeEventPattern
import org.eclipse.viatra.cep.vepl.vepl.Rule
import org.eclipse.viatra.cep.vepl.vepl.TypedParameterList
import org.eclipse.viatra.cep.vepl.vepl.VeplPackage
import org.eclipse.xtext.validation.Check

import static extension org.eclipse.viatra.cep.vepl.validation.ValidationHelper.*

class VeplValidator extends AbstractVeplValidator {

	public static val INVALID_NAME = 'invalidName'
	public static val INVALID_ARGUMENTS = 'invalidArguments'
	public static val INVALID_ACTION_IN_RULE = "invalidRuleActions"
	public static val MISSING_QUERY_IMPORT = "missingQueryImport"
	public static val ATOM_TIMEWINDOW_NO_MULTIPLICITY = "atomTimewindowNoMultiplicity"
	public static val SINGE_PLAIN_ATOM_IN_COMPLEX_EVENT_EXPRESSION = "singlePlainAtomInComplexEventExpression"
	public static val NON_POSITIVE_MULTIPLICITY = "nonPositiveMultiplicity"
	public static val INFINITE_MULTIPLICITY_WITH_TIMEWINDOW = "infiniteMultiplicityWithTimewindow"
	public static val NO_INFINITE_SUPPORT = "noInfiniteSupport"

	@Check
	def uniqueName(ModelElement modelElement) {
		if(modelElement.name.nullOrEmpty) return;
		if(!(modelElement.eContainer instanceof EventModel)) return;

		var model = (modelElement.eContainer as EventModel)

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
		if(!patternCall.hasParameterList || patternCall.eventPattern == null) return

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

	def private int getParameterNumber(EventPattern eventPattern) {
		switch (eventPattern) {
			AtomicEventPattern: getTypedParameterListSize(eventPattern.parameters)
			QueryResultChangeEventPattern: getTypedParameterListSize(eventPattern.parameters)
			ComplexEventPattern: getTypedParameterListSize(eventPattern.parameters)
			default: 0
		}
	}

	def private int getTypedParameterListSize(TypedParameterList parameterList) {
		if(parameterList == null) return 0
		if(parameterList.parameters.nullOrEmpty) return 0
		return parameterList.parameters.size
	}

	@Check
	def checkRuleActions(Rule rule) {
		if (!rule.hasActionHandler && !rule.hasAction) {
			error("There must be either an action handler or an action registered for this rule.",
				VeplPackage.Literals.MODEL_ELEMENT__NAME, INVALID_ACTION_IN_RULE)
		}

		if (rule.hasActionHandler && rule.hasAction) {
			error("The rule has both an action handler and additional actions defined.",
				VeplPackage.Literals.MODEL_ELEMENT__NAME, INVALID_ACTION_IN_RULE)
		}
	}

	@Check
	def explicitlyImportedQueryPackage(QueryResultChangeEventPattern iqPatternEventPattern) {
		var eventModel = (iqPatternEventPattern.eContainer as EventModel)
		if (!(eventModel.imports.filter[i|i instanceof QueryImport].size == 1)) {
			error(
				"Missing 'import-patterns' statement for query reference.",
				VeplPackage.Literals.QUERY_RESULT_CHANGE_EVENT_PATTERN__QUERY_REFERENCE,
				MISSING_QUERY_IMPORT
			)
		}
	}

	@Check
	def infiniteMultiplicityNotYetSupported(Atom atom) {
		if (atom.multiplicity instanceof Infinite) {
			error(
				"Infinite multiplicity not yet supported.",
				VeplPackage.Literals.COMPLEX_EVENT_EXPRESSION__MULTIPLICITY,
				NO_INFINITE_SUPPORT
			)
		}
	}

	@Check
	def expressionAtomWithTimewindowMustFeatureMultiplicity(Atom atom) {
		if (atom.hasTimewindow && !atom.hasMultiplicity) {
			error(
				"Timewindows on expression atoms are allowed only if multiplicity is also specified.",
				VeplPackage.Literals.COMPLEX_EVENT_EXPRESSION__TIMEWINDOW,
				ATOM_TIMEWINDOW_NO_MULTIPLICITY
			)
		} else if (!atom.hasTimewindow && (atom.multiplicity instanceof Multiplicity) &&
			(atom.multiplicity as Multiplicity).value < 2) {
			error(
				"One atomic event does not result in a valid complex event.",
				VeplPackage.Literals.COMPLEX_EVENT_EXPRESSION__MULTIPLICITY,
				ATOM_TIMEWINDOW_NO_MULTIPLICITY
			)
		}
	}

	@Check
	def unsupportedMultiplicityTimewindowCombinations(Atom atom) {
		if (atom.hasMultiplicity && atom.hasTimewindow) {
			if (atom.multiplicity instanceof Infinite)
				error(
					"Infinite multiplicity cannot be combined with timewindow.",
					VeplPackage.Literals.COMPLEX_EVENT_EXPRESSION__MULTIPLICITY,
					INFINITE_MULTIPLICITY_WITH_TIMEWINDOW
				)
		}
	}

	@Check
	def complexEventPatternWithPlainAtomExpression(ComplexEventPattern eventPattern) {
		val expression = eventPattern.complexEventExpression

		if (expression.right.empty && (expression.left instanceof Atom)) {
			val atom = expression.left as Atom

			if (!atom.hasMultiplicity) {
				warning("Using a single plain atomic event pattern in the complex event pattern is a bad design.",
					VeplPackage.Literals.COMPLEX_EVENT_PATTERN__COMPLEX_EVENT_EXPRESSION,
					SINGE_PLAIN_ATOM_IN_COMPLEX_EVENT_EXPRESSION)
			}
		}
	}
}
