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
package org.eclipse.viatra.cep.vepl.formatting

import com.google.inject.Inject
import org.eclipse.viatra.cep.vepl.services.VeplGrammarAccess
import org.eclipse.xtext.Keyword
import org.eclipse.xtext.formatting.impl.AbstractDeclarativeFormatter
import org.eclipse.xtext.formatting.impl.FormattingConfig

// import com.google.inject.Inject;
// import org.eclipse.viatra.cep.vepl.services.VeplGrammarAccess
/**
 * This class contains custom formatting description.
 * 
 * see : http://www.eclipse.org/Xtext/documentation.html#formatting
 * on how and when to use it 
 * 
 * Also see {@link org.eclipse.xtext.xtext.XtextFormattingTokenSerializer} as an example
 */
class VeplFormatter extends AbstractDeclarativeFormatter {

	@Inject extension VeplGrammarAccess grammar

	override protected void configureFormatting(FormattingConfig c) {

		for (k : grammar.findKeywords(",")) {
			c.setNoSpace.before(k)
			c.setSpace.after(k)
		}

		for (k : grammar.findKeywords(":")) {
			c.setNoSpace.before(k)
			c.setSpace.after(k)
		}

		for (pair : grammar.findKeywordPairs("(", ")")) {
			c.setNoSpace().before(pair.getFirst());
			c.setNoSpace().after(pair.getFirst());
			c.setNoSpace().before(pair.getSecond());
		}

		val packageKeyword = grammar.eventModelAccess.packageKeyword_0
		c.setNoLinewrap.before(packageKeyword)

		val usages = grammar.eventModelAccess.importsImportParserRuleCall_2_0
		c.setLinewrap(2).before(usages)
		c.setLinewrap(2).after(usages)

		for (k : grammar.findKeywords("import")) {
			c.setLinewrap.before(k)
		}

		for (k : grammar.findKeywords("import-queries")) {
			c.setLinewrap.before(k)
		}

		//separate logical blocks with an empty line
		c.setLinewrap(2).after(grammar.eventPatternAccess.rule)
		c.setLinewrap(2).after(grammar.ruleAccess.rule)
		c.setLinewrap(2).after(grammar.sourceAccess.rule)
		c.setLinewrap(2).before(grammar.sourceAccess.rule)

		//handle line breaks and indentation in patterns' and rules' bodies
		c.lineBreakAndIncrementIndentation(grammar.atomicEventPatternAccess.leftCurlyBracketKeyword_3_0)
		c.lineBreakAndIncrementIndentation(grammar.queryResultChangeEventPatternAccess.leftCurlyBracketKeyword_5)
		c.lineBreakAndIncrementIndentation(grammar.complexEventPatternAccess.leftCurlyBracketKeyword_5)
		c.lineBreakAndIncrementIndentation(grammar.ruleAccess.leftCurlyBracketKeyword_2)
		c.lineBreakAndIncrementIndentation(grammar.sourceAccess.leftCurlyBracketKeyword_2)

		c.lineBreakAndDecrementIndentation(grammar.atomicEventPatternAccess.rightCurlyBracketKeyword_3_3)
		c.lineBreakAndDecrementIndentation(grammar.queryResultChangeEventPatternAccess.rightCurlyBracketKeyword_10)
		c.lineBreakAndDecrementIndentation(grammar.complexEventPatternAccess.rightCurlyBracketKeyword_7)
		c.lineBreakAndDecrementIndentation(grammar.ruleAccess.rightCurlyBracketKeyword_9)
		c.lineBreakAndDecrementIndentation(grammar.sourceAccess.rightCurlyBracketKeyword_4)

		//handle line breaks in ATOMIC bodies
		c.setLinewrap().after(grammar.atomicEventPatternAccess.sourceAssignment_3_1_2)
		c.setLinewrap().after(grammar.atomicEventPatternAccess.staticBindingsAssignment_3_2_1)

		c.lineBreakAndIncrementIndentation(grammar.XBlockExpressionAccess.leftCurlyBracketKeyword_1)
		c.lineBreakAndDecrementIndentation(grammar.XBlockExpressionAccess.rightCurlyBracketKeyword_3)

		//handle line breaks in IQ bodies
		c.setLinewrap().after(grammar.queryResultChangeEventPatternAccess.queryReferenceAssignment_8)
		c.setLinewrap().after(grammar.queryResultChangeEventPatternAccess.resultChangeTypeAssignment_9_2)

		//handle line breaks in RULE bodies
		c.setLinewrap().before(grammar.ruleAccess.actionKeyword_8_0)
		c.setLinewrap().before(grammar.ruleAccess.actionHandlerKeyword_7_0)

		//handle time windows and multiplicity line breaks in COMPLEX bodies
		c.setNoSpace.before(grammar.timewindowAccess.leftSquareBracketKeyword_0)
		c.setNoSpace.after(grammar.timewindowAccess.leftSquareBracketKeyword_0)
		c.setNoSpace.before(grammar.timewindowAccess.rightSquareBracketKeyword_2)
		c.setLinewrap().after(grammar.timewindowAccess.rightSquareBracketKeyword_2)

		c.setNoSpace.before(grammar.multiplicityAccess.leftCurlyBracketKeyword_1)
		c.setNoSpace.after(grammar.multiplicityAccess.leftCurlyBracketKeyword_1)
		c.setNoSpace.before(grammar.multiplicityAccess.rightCurlyBracketKeyword_3)
		c.setLinewrap().after(grammar.multiplicityAccess.rightCurlyBracketKeyword_3)
		
		c.setNoSpace.before(grammar.infiniteAccess.leftCurlyBracketKeyword_1)
		c.setNoSpace.after(grammar.infiniteAccess.leftCurlyBracketKeyword_1)
		c.setNoSpace.before(grammar.infiniteAccess.rightCurlyBracketKeyword_3)
		c.setLinewrap().after(grammar.infiniteAccess.rightCurlyBracketKeyword_3)
		
		c.setNoSpace.before(grammar.atLeastOneAccess.leftCurlyBracketKeyword_1)
		c.setNoSpace.after(grammar.atLeastOneAccess.leftCurlyBracketKeyword_1)
		c.setNoSpace.before(grammar.atLeastOneAccess.rightCurlyBracketKeyword_3)
		c.setLinewrap().after(grammar.atLeastOneAccess.rightCurlyBracketKeyword_3)

		//complex event operators
		c.setSpace.before(grammar.complexEventOperatorAccess.rule)
		c.setSpace.after(grammar.complexEventOperatorAccess.rule)
		c.setNoLinewrap.before(grammar.complexEventOperatorAccess.rule)
		c.setNoLinewrap.after(grammar.complexEventOperatorAccess.rule)

		//comments
		c.setLinewrap.before(grammar.SL_COMMENTRule)
		c.setLinewrap.after(grammar.SL_COMMENTRule)
		c.setLinewrap.before(grammar.ML_COMMENTRule)
		c.setLinewrap.after(grammar.ML_COMMENTRule)
	}

	def private setSpace(FormattingConfig c) {
		c.setSpace(" ")
	}

	def private lineBreakAndIncrementIndentation(FormattingConfig c, Keyword keyword) {
		c.setNoSpace.before(keyword)
		c.setLinewrap().after(keyword)
		c.setIndentationIncrement.after(keyword)
	}

	def private lineBreakAndDecrementIndentation(FormattingConfig c, Keyword keyword) {
		c.setLinewrap().before(keyword)
		c.setIndentationDecrement.before(keyword)
	}
}
