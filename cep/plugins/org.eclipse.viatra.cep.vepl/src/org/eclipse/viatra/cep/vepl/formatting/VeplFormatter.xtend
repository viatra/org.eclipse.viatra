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
import org.eclipse.emf.ecore.EObject
import org.eclipse.viatra.cep.vepl.services.VeplGrammarAccess
import org.eclipse.xtext.formatting.impl.AbstractDeclarativeFormatter
import org.eclipse.xtext.formatting.impl.FormattingConfig

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

        for (k : grammar.findKeywords(".")) {
            c.setNoSpace.before(k)
            c.setNoSpace.after(k)
        }

        for (k : grammar.findKeywords(";")) {
            c.setNoSpace.before(k)
            c.setLinewrap().after(k)
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

        c.setLinewrap(0, 1, 2).before(SL_COMMENTRule)
        c.setLinewrap(0, 1, 2).before(ML_COMMENTRule)
        c.setLinewrap(0, 1, 1).after(ML_COMMENTRule)

        c.setLinewrap(2).after(grammar.eventModelAccess.nameAssignment_1)

        val imports = grammar.eventModelAccess.importsImportParserRuleCall_2_0
        c.setLinewrap(2).before(imports)
        c.setLinewrap(2).after(imports)

        for (k : grammar.findKeywords("import")) {
            c.setLinewrap.before(k)
        }

        for (k : grammar.findKeywords("import-queries")) {
            c.setLinewrap.before(k)
        }

        val defaultContext = grammar.eventModelAccess.contextContextEnumRuleCall_3_1_0
        c.setLinewrap(2).after(defaultContext)

        // separate logical blocks with an empty line
        c.setLinewrap(2).after(grammar.eventPatternAccess.rule)
        c.setLinewrap(2).after(grammar.ruleAccess.rule)
        c.setLinewrap(2).after(grammar.traitAccess.rule)

        // handle line breaks and indentation in patterns' and rules' bodies
        c.lineBreakAndIncrementIndentation(grammar.atomicEventPatternAccess.leftCurlyBracketKeyword_4_0)
        c.lineBreakAndIncrementIndentationBefore(grammar.queryResultChangeEventPatternAccess.asKeyword_5)
        c.lineBreakAndIncrementIndentation(grammar.complexEventPatternAccess.leftCurlyBracketKeyword_5)
        c.lineBreakAndIncrementIndentation(grammar.traitAccess.leftCurlyBracketKeyword_2)

        c.lineBreakAndDecrementIndentation(grammar.atomicEventPatternAccess.rightCurlyBracketKeyword_4_2)
        c.lineBreakAndDecrementIndentationAfter(grammar.queryResultChangeEventPatternAccess.rule)
        c.lineBreakAndDecrementIndentation(grammar.complexEventPatternAccess.rightCurlyBracketKeyword_9)
        c.lineBreakAndDecrementIndentation(grammar.traitAccess.rightCurlyBracketKeyword_4)

        // TODO: handle line breaks in TRAIT bodies
        // handle line breaks in ATOMIC bodies
        c.lineBreakAndIncrementIndentation(grammar.XBlockExpressionAccess.leftCurlyBracketKeyword_1)
        c.lineBreakAndDecrementIndentation(grammar.XBlockExpressionAccess.rightCurlyBracketKeyword_3)

        // handle line breaks in complex bodies
        c.setLinewrap().after(grammar.complexEventPatternAccess.complexEventExpressionAssignment_7)
        c.setSpace().after(grammar.complexEventPatternAccess.asKeyword_6)

        // handle time windows and multiplicity line breaks in COMPLEX bodies
        c.setNoSpace.before(grammar.timewindowAccess.leftSquareBracketKeyword_0)
        c.setNoSpace.after(grammar.timewindowAccess.leftSquareBracketKeyword_0)
        c.setNoSpace.before(grammar.timewindowAccess.rightSquareBracketKeyword_2)
        c.setLinewrap().after(grammar.timewindowAccess.rightSquareBracketKeyword_2)

        c.setNoSpace.before(grammar.multiplicityAccess.leftCurlyBracketKeyword_1)
        c.setNoSpace.after(grammar.multiplicityAccess.leftCurlyBracketKeyword_1)
        c.setNoSpace.before(grammar.multiplicityAccess.rightCurlyBracketKeyword_3)

        c.setNoSpace.before(grammar.infiniteAccess.leftCurlyBracketKeyword_1)
        c.setNoSpace.after(grammar.infiniteAccess.leftCurlyBracketKeyword_1)
        c.setNoSpace.before(grammar.infiniteAccess.rightCurlyBracketKeyword_3)

        c.setNoSpace.before(grammar.atLeastOneAccess.leftCurlyBracketKeyword_1)
        c.setNoSpace.after(grammar.atLeastOneAccess.leftCurlyBracketKeyword_1)
        c.setNoSpace.before(grammar.atLeastOneAccess.rightCurlyBracketKeyword_3)

        // complex event operators
        c.setSpace.before(grammar.complexEventOperatorAccess.rule)
        c.setSpace.after(grammar.complexEventOperatorAccess.rule)
        c.setNoLinewrap.before(grammar.complexEventOperatorAccess.rule)
        c.setNoLinewrap.after(grammar.complexEventOperatorAccess.rule)

        // comments
        c.setLinewrap.before(grammar.SL_COMMENTRule)
        c.setLinewrap.after(grammar.SL_COMMENTRule)
        c.setLinewrap.before(grammar.ML_COMMENTRule)
        c.setLinewrap.after(grammar.ML_COMMENTRule)
    }

    def private setSpace(FormattingConfig c) {
        c.setSpace(" ")
    }

    def private lineBreakAndIncrementIndentation(FormattingConfig c, EObject eObject) {
        lineBreakAndIncrementIndentation(c, eObject, 1, Location::AFTER)
    }

    def private lineBreakAndIncrementIndentationBefore(FormattingConfig c, EObject eObject) {
        lineBreakAndIncrementIndentation(c, eObject, 1, Location::BEFORE)
    }

    def private lineBreakAndDecrementIndentation(FormattingConfig c, EObject eObject) {
        lineBreakAndDecrementIndentation(c, eObject, 1, Location::BEFORE)
    }

    def private lineBreakAndDecrementIndentationAfter(FormattingConfig c, EObject eObject) {
        lineBreakAndDecrementIndentation(c, eObject, 1, Location::AFTER)
    }

    enum Location {
        BEFORE,
        AFTER
    }

    def private lineBreakAndIncrementIndentation(FormattingConfig c, EObject eObject, int lineWrap, Location location) {
        switch (location) {
            case BEFORE: {
                c.setLinewrap(lineWrap).before(eObject)
                c.setIndentationIncrement.before(eObject)
            }
            case AFTER: {
                c.setNoSpace.before(eObject)
                c.setLinewrap(lineWrap).after(eObject)
                c.setIndentationIncrement.after(eObject)
            }
            default:
                IllegalArgumentException
        }
    }

    def private lineBreakAndDecrementIndentation(FormattingConfig c, EObject eObject, int lineWrap, Location location) {
        switch (location) {
            case BEFORE: {
                c.setLinewrap(lineWrap).before(eObject)
                c.setIndentationDecrement.before(eObject)
            }
            case AFTER: {
                c.setLinewrap(lineWrap).after(eObject)
                c.setIndentationDecrement.after(eObject)
            }
            default:
                IllegalArgumentException
        }
    }
}
