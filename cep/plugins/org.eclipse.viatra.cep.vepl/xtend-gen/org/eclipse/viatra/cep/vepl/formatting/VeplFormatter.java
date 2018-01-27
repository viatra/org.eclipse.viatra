/**
 * Copyright (c) 2004-2014, Istvan David, Istvan Rath and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Istvan David - initial API and implementation
 */
package org.eclipse.viatra.cep.vepl.formatting;

import com.google.inject.Inject;
import java.util.List;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.viatra.cep.vepl.services.VeplGrammarAccess;
import org.eclipse.xtext.Assignment;
import org.eclipse.xtext.Keyword;
import org.eclipse.xtext.ParserRule;
import org.eclipse.xtext.RuleCall;
import org.eclipse.xtext.TerminalRule;
import org.eclipse.xtext.formatting.impl.AbstractDeclarativeFormatter;
import org.eclipse.xtext.formatting.impl.FormattingConfig;
import org.eclipse.xtext.util.Pair;
import org.eclipse.xtext.xbase.lib.Extension;
import org.eclipse.xtext.xbase.services.XbaseGrammarAccess;

/**
 * This class contains custom formatting description.
 * 
 * see : http://www.eclipse.org/Xtext/documentation.html#formatting
 * on how and when to use it
 * 
 * Also see {@link org.eclipse.xtext.xtext.XtextFormattingTokenSerializer} as an example
 */
@SuppressWarnings("all")
public class VeplFormatter extends AbstractDeclarativeFormatter {
  public enum Location {
    BEFORE,
    
    AFTER;
  }
  
  @Inject
  @Extension
  private VeplGrammarAccess grammar;
  
  @Override
  protected void configureFormatting(final FormattingConfig c) {
    List<Keyword> _findKeywords = this.grammar.findKeywords(",");
    for (final Keyword k : _findKeywords) {
      {
        FormattingConfig.NoSpaceLocator _setNoSpace = c.setNoSpace();
        _setNoSpace.before(k);
        FormattingConfig.SpaceLocator _setSpace = this.setSpace(c);
        _setSpace.after(k);
      }
    }
    List<Keyword> _findKeywords_1 = this.grammar.findKeywords(".");
    for (final Keyword k_1 : _findKeywords_1) {
      {
        FormattingConfig.NoSpaceLocator _setNoSpace = c.setNoSpace();
        _setNoSpace.before(k_1);
        FormattingConfig.NoSpaceLocator _setNoSpace_1 = c.setNoSpace();
        _setNoSpace_1.after(k_1);
      }
    }
    List<Keyword> _findKeywords_2 = this.grammar.findKeywords(";");
    for (final Keyword k_2 : _findKeywords_2) {
      {
        FormattingConfig.NoSpaceLocator _setNoSpace = c.setNoSpace();
        _setNoSpace.before(k_2);
        FormattingConfig.LinewrapLocator _setLinewrap = c.setLinewrap();
        _setLinewrap.after(k_2);
      }
    }
    List<Keyword> _findKeywords_3 = this.grammar.findKeywords(":");
    for (final Keyword k_3 : _findKeywords_3) {
      {
        FormattingConfig.NoSpaceLocator _setNoSpace = c.setNoSpace();
        _setNoSpace.before(k_3);
        FormattingConfig.SpaceLocator _setSpace = this.setSpace(c);
        _setSpace.after(k_3);
      }
    }
    List<Pair<Keyword, Keyword>> _findKeywordPairs = this.grammar.findKeywordPairs("(", ")");
    for (final Pair<Keyword, Keyword> pair : _findKeywordPairs) {
      {
        FormattingConfig.NoSpaceLocator _setNoSpace = c.setNoSpace();
        Keyword _first = pair.getFirst();
        _setNoSpace.before(_first);
        FormattingConfig.NoSpaceLocator _setNoSpace_1 = c.setNoSpace();
        Keyword _first_1 = pair.getFirst();
        _setNoSpace_1.after(_first_1);
        FormattingConfig.NoSpaceLocator _setNoSpace_2 = c.setNoSpace();
        Keyword _second = pair.getSecond();
        _setNoSpace_2.before(_second);
      }
    }
    FormattingConfig.LinewrapLocator _setLinewrap = c.setLinewrap(0, 1, 2);
    TerminalRule _sL_COMMENTRule = this.grammar.getSL_COMMENTRule();
    _setLinewrap.before(_sL_COMMENTRule);
    FormattingConfig.LinewrapLocator _setLinewrap_1 = c.setLinewrap(0, 1, 2);
    TerminalRule _mL_COMMENTRule = this.grammar.getML_COMMENTRule();
    _setLinewrap_1.before(_mL_COMMENTRule);
    FormattingConfig.LinewrapLocator _setLinewrap_2 = c.setLinewrap(0, 1, 1);
    TerminalRule _mL_COMMENTRule_1 = this.grammar.getML_COMMENTRule();
    _setLinewrap_2.after(_mL_COMMENTRule_1);
    FormattingConfig.LinewrapLocator _setLinewrap_3 = c.setLinewrap(2);
    VeplGrammarAccess.EventModelElements _eventModelAccess = this.grammar.getEventModelAccess();
    Assignment _nameAssignment_1 = _eventModelAccess.getNameAssignment_1();
    _setLinewrap_3.after(_nameAssignment_1);
    VeplGrammarAccess.EventModelElements _eventModelAccess_1 = this.grammar.getEventModelAccess();
    final RuleCall imports = _eventModelAccess_1.getImportsImportParserRuleCall_2_0();
    FormattingConfig.LinewrapLocator _setLinewrap_4 = c.setLinewrap(2);
    _setLinewrap_4.before(imports);
    FormattingConfig.LinewrapLocator _setLinewrap_5 = c.setLinewrap(2);
    _setLinewrap_5.after(imports);
    List<Keyword> _findKeywords_4 = this.grammar.findKeywords("import");
    for (final Keyword k_4 : _findKeywords_4) {
      FormattingConfig.LinewrapLocator _setLinewrap_6 = c.setLinewrap();
      _setLinewrap_6.before(k_4);
    }
    List<Keyword> _findKeywords_5 = this.grammar.findKeywords("import-queries");
    for (final Keyword k_5 : _findKeywords_5) {
      FormattingConfig.LinewrapLocator _setLinewrap_7 = c.setLinewrap();
      _setLinewrap_7.before(k_5);
    }
    VeplGrammarAccess.EventModelElements _eventModelAccess_2 = this.grammar.getEventModelAccess();
    final RuleCall defaultContext = _eventModelAccess_2.getContextContextEnumRuleCall_3_1_0();
    FormattingConfig.LinewrapLocator _setLinewrap_8 = c.setLinewrap(2);
    _setLinewrap_8.after(defaultContext);
    FormattingConfig.LinewrapLocator _setLinewrap_9 = c.setLinewrap(2);
    VeplGrammarAccess.EventPatternElements _eventPatternAccess = this.grammar.getEventPatternAccess();
    ParserRule _rule = _eventPatternAccess.getRule();
    _setLinewrap_9.after(_rule);
    FormattingConfig.LinewrapLocator _setLinewrap_10 = c.setLinewrap(2);
    VeplGrammarAccess.RuleElements _ruleAccess = this.grammar.getRuleAccess();
    ParserRule _rule_1 = _ruleAccess.getRule();
    _setLinewrap_10.after(_rule_1);
    FormattingConfig.LinewrapLocator _setLinewrap_11 = c.setLinewrap(2);
    VeplGrammarAccess.TraitElements _traitAccess = this.grammar.getTraitAccess();
    ParserRule _rule_2 = _traitAccess.getRule();
    _setLinewrap_11.after(_rule_2);
    VeplGrammarAccess.AtomicEventPatternElements _atomicEventPatternAccess = this.grammar.getAtomicEventPatternAccess();
    Keyword _leftCurlyBracketKeyword_4_0 = _atomicEventPatternAccess.getLeftCurlyBracketKeyword_4_0();
    this.lineBreakAndIncrementIndentation(c, _leftCurlyBracketKeyword_4_0);
    VeplGrammarAccess.QueryResultChangeEventPatternElements _queryResultChangeEventPatternAccess = this.grammar.getQueryResultChangeEventPatternAccess();
    Keyword _asKeyword_5 = _queryResultChangeEventPatternAccess.getAsKeyword_5();
    this.lineBreakAndIncrementIndentationBefore(c, _asKeyword_5);
    VeplGrammarAccess.ComplexEventPatternElements _complexEventPatternAccess = this.grammar.getComplexEventPatternAccess();
    Keyword _leftCurlyBracketKeyword_5 = _complexEventPatternAccess.getLeftCurlyBracketKeyword_5();
    this.lineBreakAndIncrementIndentation(c, _leftCurlyBracketKeyword_5);
    VeplGrammarAccess.TraitElements _traitAccess_1 = this.grammar.getTraitAccess();
    Keyword _leftCurlyBracketKeyword_2 = _traitAccess_1.getLeftCurlyBracketKeyword_2();
    this.lineBreakAndIncrementIndentation(c, _leftCurlyBracketKeyword_2);
    VeplGrammarAccess.AtomicEventPatternElements _atomicEventPatternAccess_1 = this.grammar.getAtomicEventPatternAccess();
    Keyword _rightCurlyBracketKeyword_4_2 = _atomicEventPatternAccess_1.getRightCurlyBracketKeyword_4_2();
    this.lineBreakAndDecrementIndentation(c, _rightCurlyBracketKeyword_4_2);
    VeplGrammarAccess.QueryResultChangeEventPatternElements _queryResultChangeEventPatternAccess_1 = this.grammar.getQueryResultChangeEventPatternAccess();
    ParserRule _rule_3 = _queryResultChangeEventPatternAccess_1.getRule();
    this.lineBreakAndDecrementIndentationAfter(c, _rule_3);
    VeplGrammarAccess.ComplexEventPatternElements _complexEventPatternAccess_1 = this.grammar.getComplexEventPatternAccess();
    Keyword _rightCurlyBracketKeyword_9 = _complexEventPatternAccess_1.getRightCurlyBracketKeyword_9();
    this.lineBreakAndDecrementIndentation(c, _rightCurlyBracketKeyword_9);
    VeplGrammarAccess.TraitElements _traitAccess_2 = this.grammar.getTraitAccess();
    Keyword _rightCurlyBracketKeyword_4 = _traitAccess_2.getRightCurlyBracketKeyword_4();
    this.lineBreakAndDecrementIndentation(c, _rightCurlyBracketKeyword_4);
    XbaseGrammarAccess.XBlockExpressionElements _xBlockExpressionAccess = this.grammar.getXBlockExpressionAccess();
    Keyword _leftCurlyBracketKeyword_1 = _xBlockExpressionAccess.getLeftCurlyBracketKeyword_1();
    this.lineBreakAndIncrementIndentation(c, _leftCurlyBracketKeyword_1);
    XbaseGrammarAccess.XBlockExpressionElements _xBlockExpressionAccess_1 = this.grammar.getXBlockExpressionAccess();
    Keyword _rightCurlyBracketKeyword_3 = _xBlockExpressionAccess_1.getRightCurlyBracketKeyword_3();
    this.lineBreakAndDecrementIndentation(c, _rightCurlyBracketKeyword_3);
    FormattingConfig.LinewrapLocator _setLinewrap_12 = c.setLinewrap();
    VeplGrammarAccess.ComplexEventPatternElements _complexEventPatternAccess_2 = this.grammar.getComplexEventPatternAccess();
    Assignment _complexEventExpressionAssignment_7 = _complexEventPatternAccess_2.getComplexEventExpressionAssignment_7();
    _setLinewrap_12.after(_complexEventExpressionAssignment_7);
    FormattingConfig.SpaceLocator _setSpace = this.setSpace(c);
    VeplGrammarAccess.ComplexEventPatternElements _complexEventPatternAccess_3 = this.grammar.getComplexEventPatternAccess();
    Keyword _asKeyword_6 = _complexEventPatternAccess_3.getAsKeyword_6();
    _setSpace.after(_asKeyword_6);
    FormattingConfig.NoSpaceLocator _setNoSpace = c.setNoSpace();
    VeplGrammarAccess.TimewindowElements _timewindowAccess = this.grammar.getTimewindowAccess();
    Keyword _leftSquareBracketKeyword_0 = _timewindowAccess.getLeftSquareBracketKeyword_0();
    _setNoSpace.before(_leftSquareBracketKeyword_0);
    FormattingConfig.NoSpaceLocator _setNoSpace_1 = c.setNoSpace();
    VeplGrammarAccess.TimewindowElements _timewindowAccess_1 = this.grammar.getTimewindowAccess();
    Keyword _leftSquareBracketKeyword_0_1 = _timewindowAccess_1.getLeftSquareBracketKeyword_0();
    _setNoSpace_1.after(_leftSquareBracketKeyword_0_1);
    FormattingConfig.NoSpaceLocator _setNoSpace_2 = c.setNoSpace();
    VeplGrammarAccess.TimewindowElements _timewindowAccess_2 = this.grammar.getTimewindowAccess();
    Keyword _rightSquareBracketKeyword_2 = _timewindowAccess_2.getRightSquareBracketKeyword_2();
    _setNoSpace_2.before(_rightSquareBracketKeyword_2);
    FormattingConfig.LinewrapLocator _setLinewrap_13 = c.setLinewrap();
    VeplGrammarAccess.TimewindowElements _timewindowAccess_3 = this.grammar.getTimewindowAccess();
    Keyword _rightSquareBracketKeyword_2_1 = _timewindowAccess_3.getRightSquareBracketKeyword_2();
    _setLinewrap_13.after(_rightSquareBracketKeyword_2_1);
    FormattingConfig.NoSpaceLocator _setNoSpace_3 = c.setNoSpace();
    VeplGrammarAccess.MultiplicityElements _multiplicityAccess = this.grammar.getMultiplicityAccess();
    Keyword _leftCurlyBracketKeyword_1_1 = _multiplicityAccess.getLeftCurlyBracketKeyword_1();
    _setNoSpace_3.before(_leftCurlyBracketKeyword_1_1);
    FormattingConfig.NoSpaceLocator _setNoSpace_4 = c.setNoSpace();
    VeplGrammarAccess.MultiplicityElements _multiplicityAccess_1 = this.grammar.getMultiplicityAccess();
    Keyword _leftCurlyBracketKeyword_1_2 = _multiplicityAccess_1.getLeftCurlyBracketKeyword_1();
    _setNoSpace_4.after(_leftCurlyBracketKeyword_1_2);
    FormattingConfig.NoSpaceLocator _setNoSpace_5 = c.setNoSpace();
    VeplGrammarAccess.MultiplicityElements _multiplicityAccess_2 = this.grammar.getMultiplicityAccess();
    Keyword _rightCurlyBracketKeyword_3_1 = _multiplicityAccess_2.getRightCurlyBracketKeyword_3();
    _setNoSpace_5.before(_rightCurlyBracketKeyword_3_1);
    FormattingConfig.NoSpaceLocator _setNoSpace_6 = c.setNoSpace();
    VeplGrammarAccess.InfiniteElements _infiniteAccess = this.grammar.getInfiniteAccess();
    Keyword _leftCurlyBracketKeyword_1_3 = _infiniteAccess.getLeftCurlyBracketKeyword_1();
    _setNoSpace_6.before(_leftCurlyBracketKeyword_1_3);
    FormattingConfig.NoSpaceLocator _setNoSpace_7 = c.setNoSpace();
    VeplGrammarAccess.InfiniteElements _infiniteAccess_1 = this.grammar.getInfiniteAccess();
    Keyword _leftCurlyBracketKeyword_1_4 = _infiniteAccess_1.getLeftCurlyBracketKeyword_1();
    _setNoSpace_7.after(_leftCurlyBracketKeyword_1_4);
    FormattingConfig.NoSpaceLocator _setNoSpace_8 = c.setNoSpace();
    VeplGrammarAccess.InfiniteElements _infiniteAccess_2 = this.grammar.getInfiniteAccess();
    Keyword _rightCurlyBracketKeyword_3_2 = _infiniteAccess_2.getRightCurlyBracketKeyword_3();
    _setNoSpace_8.before(_rightCurlyBracketKeyword_3_2);
    FormattingConfig.NoSpaceLocator _setNoSpace_9 = c.setNoSpace();
    VeplGrammarAccess.AtLeastOneElements _atLeastOneAccess = this.grammar.getAtLeastOneAccess();
    Keyword _leftCurlyBracketKeyword_1_5 = _atLeastOneAccess.getLeftCurlyBracketKeyword_1();
    _setNoSpace_9.before(_leftCurlyBracketKeyword_1_5);
    FormattingConfig.NoSpaceLocator _setNoSpace_10 = c.setNoSpace();
    VeplGrammarAccess.AtLeastOneElements _atLeastOneAccess_1 = this.grammar.getAtLeastOneAccess();
    Keyword _leftCurlyBracketKeyword_1_6 = _atLeastOneAccess_1.getLeftCurlyBracketKeyword_1();
    _setNoSpace_10.after(_leftCurlyBracketKeyword_1_6);
    FormattingConfig.NoSpaceLocator _setNoSpace_11 = c.setNoSpace();
    VeplGrammarAccess.AtLeastOneElements _atLeastOneAccess_2 = this.grammar.getAtLeastOneAccess();
    Keyword _rightCurlyBracketKeyword_3_3 = _atLeastOneAccess_2.getRightCurlyBracketKeyword_3();
    _setNoSpace_11.before(_rightCurlyBracketKeyword_3_3);
    FormattingConfig.SpaceLocator _setSpace_1 = this.setSpace(c);
    VeplGrammarAccess.ComplexEventOperatorElements _complexEventOperatorAccess = this.grammar.getComplexEventOperatorAccess();
    ParserRule _rule_4 = _complexEventOperatorAccess.getRule();
    _setSpace_1.before(_rule_4);
    FormattingConfig.SpaceLocator _setSpace_2 = this.setSpace(c);
    VeplGrammarAccess.ComplexEventOperatorElements _complexEventOperatorAccess_1 = this.grammar.getComplexEventOperatorAccess();
    ParserRule _rule_5 = _complexEventOperatorAccess_1.getRule();
    _setSpace_2.after(_rule_5);
    FormattingConfig.NoLinewrapLocator _setNoLinewrap = c.setNoLinewrap();
    VeplGrammarAccess.ComplexEventOperatorElements _complexEventOperatorAccess_2 = this.grammar.getComplexEventOperatorAccess();
    ParserRule _rule_6 = _complexEventOperatorAccess_2.getRule();
    _setNoLinewrap.before(_rule_6);
    FormattingConfig.NoLinewrapLocator _setNoLinewrap_1 = c.setNoLinewrap();
    VeplGrammarAccess.ComplexEventOperatorElements _complexEventOperatorAccess_3 = this.grammar.getComplexEventOperatorAccess();
    ParserRule _rule_7 = _complexEventOperatorAccess_3.getRule();
    _setNoLinewrap_1.after(_rule_7);
    FormattingConfig.LinewrapLocator _setLinewrap_14 = c.setLinewrap();
    TerminalRule _sL_COMMENTRule_1 = this.grammar.getSL_COMMENTRule();
    _setLinewrap_14.before(_sL_COMMENTRule_1);
    FormattingConfig.LinewrapLocator _setLinewrap_15 = c.setLinewrap();
    TerminalRule _sL_COMMENTRule_2 = this.grammar.getSL_COMMENTRule();
    _setLinewrap_15.after(_sL_COMMENTRule_2);
    FormattingConfig.LinewrapLocator _setLinewrap_16 = c.setLinewrap();
    TerminalRule _mL_COMMENTRule_2 = this.grammar.getML_COMMENTRule();
    _setLinewrap_16.before(_mL_COMMENTRule_2);
    FormattingConfig.LinewrapLocator _setLinewrap_17 = c.setLinewrap();
    TerminalRule _mL_COMMENTRule_3 = this.grammar.getML_COMMENTRule();
    _setLinewrap_17.after(_mL_COMMENTRule_3);
  }
  
  private FormattingConfig.SpaceLocator setSpace(final FormattingConfig c) {
    return c.setSpace(" ");
  }
  
  private Class<IllegalArgumentException> lineBreakAndIncrementIndentation(final FormattingConfig c, final EObject eObject) {
    return this.lineBreakAndIncrementIndentation(c, eObject, 1, VeplFormatter.Location.AFTER);
  }
  
  private Class<IllegalArgumentException> lineBreakAndIncrementIndentationBefore(final FormattingConfig c, final EObject eObject) {
    return this.lineBreakAndIncrementIndentation(c, eObject, 1, VeplFormatter.Location.BEFORE);
  }
  
  private Class<IllegalArgumentException> lineBreakAndDecrementIndentation(final FormattingConfig c, final EObject eObject) {
    return this.lineBreakAndDecrementIndentation(c, eObject, 1, VeplFormatter.Location.BEFORE);
  }
  
  private Class<IllegalArgumentException> lineBreakAndDecrementIndentationAfter(final FormattingConfig c, final EObject eObject) {
    return this.lineBreakAndDecrementIndentation(c, eObject, 1, VeplFormatter.Location.AFTER);
  }
  
  private Class<IllegalArgumentException> lineBreakAndIncrementIndentation(final FormattingConfig c, final EObject eObject, final int lineWrap, final VeplFormatter.Location location) {
    Class<IllegalArgumentException> _switchResult = null;
    if (location != null) {
      switch (location) {
        case BEFORE:
          FormattingConfig.LinewrapLocator _setLinewrap = c.setLinewrap(lineWrap);
          _setLinewrap.before(eObject);
          FormattingConfig.IndentationLocatorStart _setIndentationIncrement = c.setIndentationIncrement();
          _setIndentationIncrement.before(eObject);
          break;
        case AFTER:
          FormattingConfig.NoSpaceLocator _setNoSpace = c.setNoSpace();
          _setNoSpace.before(eObject);
          FormattingConfig.LinewrapLocator _setLinewrap_1 = c.setLinewrap(lineWrap);
          _setLinewrap_1.after(eObject);
          FormattingConfig.IndentationLocatorStart _setIndentationIncrement_1 = c.setIndentationIncrement();
          _setIndentationIncrement_1.after(eObject);
          break;
        default:
          _switchResult = IllegalArgumentException.class;
          break;
      }
    } else {
      _switchResult = IllegalArgumentException.class;
    }
    return _switchResult;
  }
  
  private Class<IllegalArgumentException> lineBreakAndDecrementIndentation(final FormattingConfig c, final EObject eObject, final int lineWrap, final VeplFormatter.Location location) {
    Class<IllegalArgumentException> _switchResult = null;
    if (location != null) {
      switch (location) {
        case BEFORE:
          FormattingConfig.LinewrapLocator _setLinewrap = c.setLinewrap(lineWrap);
          _setLinewrap.before(eObject);
          FormattingConfig.IndentationLocatorEnd _setIndentationDecrement = c.setIndentationDecrement();
          _setIndentationDecrement.before(eObject);
          break;
        case AFTER:
          FormattingConfig.LinewrapLocator _setLinewrap_1 = c.setLinewrap(lineWrap);
          _setLinewrap_1.after(eObject);
          FormattingConfig.IndentationLocatorEnd _setIndentationDecrement_1 = c.setIndentationDecrement();
          _setIndentationDecrement_1.after(eObject);
          break;
        default:
          _switchResult = IllegalArgumentException.class;
          break;
      }
    } else {
      _switchResult = IllegalArgumentException.class;
    }
    return _switchResult;
  }
}
