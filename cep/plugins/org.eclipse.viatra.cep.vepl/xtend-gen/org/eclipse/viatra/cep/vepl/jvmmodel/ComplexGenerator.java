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
package org.eclipse.viatra.cep.vepl.jvmmodel;

import com.google.common.base.Objects;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.inject.Inject;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.internal.xtend.util.Pair;
import org.eclipse.viatra.cep.core.api.patterns.ParameterizableComplexEventPattern;
import org.eclipse.viatra.cep.core.metamodels.automaton.EventContext;
import org.eclipse.viatra.cep.core.metamodels.events.EventsFactory;
import org.eclipse.viatra.cep.core.metamodels.events.Timewindow;
import org.eclipse.viatra.cep.vepl.jvmmodel.AnonymousPatternManager;
import org.eclipse.viatra.cep.vepl.jvmmodel.ComplexPatternType;
import org.eclipse.viatra.cep.vepl.jvmmodel.FactoryManager;
import org.eclipse.viatra.cep.vepl.jvmmodel.NamingProvider;
import org.eclipse.viatra.cep.vepl.jvmmodel.Utils;
import org.eclipse.viatra.cep.vepl.jvmmodel.expressiontree.ExpressionTree;
import org.eclipse.viatra.cep.vepl.jvmmodel.expressiontree.ExpressionTreeBuilder;
import org.eclipse.viatra.cep.vepl.jvmmodel.expressiontree.Leaf;
import org.eclipse.viatra.cep.vepl.jvmmodel.expressiontree.Node;
import org.eclipse.viatra.cep.vepl.jvmmodel.expressiontree.TreeElement;
import org.eclipse.viatra.cep.vepl.vepl.AbstractMultiplicity;
import org.eclipse.viatra.cep.vepl.vepl.AndOperator;
import org.eclipse.viatra.cep.vepl.vepl.AtLeastOne;
import org.eclipse.viatra.cep.vepl.vepl.Atom;
import org.eclipse.viatra.cep.vepl.vepl.ComplexEventExpression;
import org.eclipse.viatra.cep.vepl.vepl.ComplexEventOperator;
import org.eclipse.viatra.cep.vepl.vepl.ComplexEventPattern;
import org.eclipse.viatra.cep.vepl.vepl.ContextEnum;
import org.eclipse.viatra.cep.vepl.vepl.EventModel;
import org.eclipse.viatra.cep.vepl.vepl.EventPattern;
import org.eclipse.viatra.cep.vepl.vepl.FollowsOperator;
import org.eclipse.viatra.cep.vepl.vepl.Infinite;
import org.eclipse.viatra.cep.vepl.vepl.Multiplicity;
import org.eclipse.viatra.cep.vepl.vepl.NegOperator;
import org.eclipse.viatra.cep.vepl.vepl.OrOperator;
import org.eclipse.viatra.cep.vepl.vepl.ParameterizedPatternCall;
import org.eclipse.viatra.cep.vepl.vepl.PatternCallParameter;
import org.eclipse.viatra.cep.vepl.vepl.PatternCallParameterList;
import org.eclipse.viatra.cep.vepl.vepl.TypedParameter;
import org.eclipse.viatra.cep.vepl.vepl.TypedParameterList;
import org.eclipse.viatra.cep.vepl.vepl.UntilOperator;
import org.eclipse.xtend2.lib.StringConcatenation;
import org.eclipse.xtext.common.types.JvmConstructor;
import org.eclipse.xtext.common.types.JvmGenericType;
import org.eclipse.xtext.common.types.JvmMember;
import org.eclipse.xtext.common.types.JvmTypeReference;
import org.eclipse.xtext.naming.QualifiedName;
import org.eclipse.xtext.xbase.compiler.output.ITreeAppendable;
import org.eclipse.xtext.xbase.jvmmodel.IJvmDeclaredTypeAcceptor;
import org.eclipse.xtext.xbase.jvmmodel.JvmTypeReferenceBuilder;
import org.eclipse.xtext.xbase.jvmmodel.JvmTypesBuilder;
import org.eclipse.xtext.xbase.lib.Extension;
import org.eclipse.xtext.xbase.lib.IterableExtensions;
import org.eclipse.xtext.xbase.lib.Procedures.Procedure1;

@SuppressWarnings("all")
public class ComplexGenerator {
  @Inject
  @Extension
  private JvmTypesBuilder jvmTypesBuilder;
  
  @Inject
  @Extension
  private Utils _utils;
  
  @Inject
  @Extension
  private NamingProvider _namingProvider;
  
  @Inject
  private AnonymousPatternManager anonManager = AnonymousPatternManager.getInstance();
  
  @Inject
  private ExpressionTreeBuilder expressionTreeBuilder = ExpressionTreeBuilder.getInstance();
  
  private JvmTypeReferenceBuilder typeRefBuilder;
  
  public void generateComplexEventPatterns(final List<ComplexEventPattern> patterns, final IJvmDeclaredTypeAcceptor acceptor, final JvmTypeReferenceBuilder typeRefBuilder) {
    this.typeRefBuilder = typeRefBuilder;
    this.anonManager.flush();
    for (final ComplexEventPattern pattern : patterns) {
      this.generateComplexEventPattern(pattern, acceptor);
    }
  }
  
  public void generateComplexEventPattern(final ComplexEventPattern pattern, final IJvmDeclaredTypeAcceptor acceptor) {
    boolean _or = false;
    ComplexEventExpression _complexEventExpression = pattern.getComplexEventExpression();
    boolean _tripleEquals = (_complexEventExpression == null);
    if (_tripleEquals) {
      _or = true;
    } else {
      ComplexEventExpression _complexEventExpression_1 = pattern.getComplexEventExpression();
      ComplexEventExpression _left = _complexEventExpression_1.getLeft();
      boolean _tripleEquals_1 = (_left == null);
      _or = _tripleEquals_1;
    }
    if (_or) {
      return;
    }
    ComplexEventExpression _complexEventExpression_2 = pattern.getComplexEventExpression();
    final ExpressionTree expressionTree = this.expressionTreeBuilder.buildExpressionTree(_complexEventExpression_2);
    Node _root = expressionTree.getRoot();
    QualifiedName _patternFqn = this._namingProvider.getPatternFqn(pattern);
    this.generateComplexEventPattern(pattern, _root, _patternFqn, acceptor);
  }
  
  public boolean isRoot(final Node node) {
    Node _parentNode = node.getParentNode();
    return (_parentNode == null);
  }
  
  public QualifiedName generateComplexEventPattern(final ComplexEventPattern pattern, final Node node, final QualifiedName className, final IJvmDeclaredTypeAcceptor acceptor) {
    List<Pair<QualifiedName, List<String>>> compositionEvents = Lists.<Pair<QualifiedName, List<String>>>newArrayList();
    List<TreeElement> _children = node.getChildren();
    for (final TreeElement child : _children) {
      if ((child instanceof Node)) {
        int _nextIndex = this.anonManager.getNextIndex();
        QualifiedName _anonymousName = this._namingProvider.getAnonymousName(pattern, _nextIndex);
        final QualifiedName referredAnonymousPatternFqn = this.generateComplexEventPattern(pattern, ((Node) child), _anonymousName, acceptor);
        final Pair<QualifiedName, List<String>> compositionEvent = new Pair<QualifiedName, List<String>>();
        compositionEvent.setFirst(referredAnonymousPatternFqn);
        compositionEvent.setSecond(null);
        compositionEvents.add(compositionEvent);
      } else {
        final Leaf leaf = ((Leaf) child);
        ComplexEventExpression _expression = leaf.getExpression();
        ParameterizedPatternCall _patternCall = ((Atom) _expression).getPatternCall();
        final EventPattern eventPattern = _patternCall.getEventPattern();
        boolean _eIsProxy = eventPattern.eIsProxy();
        boolean _not = (!_eIsProxy);
        if (_not) {
          final Pair<QualifiedName, List<String>> compositionEvent_1 = new Pair<QualifiedName, List<String>>();
          QualifiedName _patternFqn = this._namingProvider.getPatternFqn(eventPattern);
          compositionEvent_1.setFirst(_patternFqn);
          ArrayList<String> parameters = new ArrayList<String>();
          ComplexEventExpression _expression_1 = leaf.getExpression();
          ParameterizedPatternCall _patternCall_1 = ((Atom) _expression_1).getPatternCall();
          final PatternCallParameterList paramList = _patternCall_1.getParameterList();
          boolean _and = false;
          if (!(paramList != null)) {
            _and = false;
          } else {
            EList<PatternCallParameter> _parameters = paramList.getParameters();
            boolean _isEmpty = _parameters.isEmpty();
            boolean _not_1 = (!_isEmpty);
            _and = _not_1;
          }
          if (_and) {
            EList<PatternCallParameter> _parameters_1 = paramList.getParameters();
            for (final PatternCallParameter parameter : _parameters_1) {
              String _name = parameter.getName();
              parameters.add(_name);
            }
          }
          compositionEvent_1.setSecond(parameters);
          compositionEvents.add(compositionEvent_1);
        }
      }
    }
    QualifiedName _xifexpression = null;
    boolean _isRoot = this.isRoot(node);
    if (_isRoot) {
      _xifexpression = this._namingProvider.getPatternFqn(pattern);
    } else {
      int _nextIndex_1 = this.anonManager.getNextIndex();
      _xifexpression = this._namingProvider.getAnonymousName(pattern, _nextIndex_1);
    }
    final QualifiedName currentClassName = _xifexpression;
    ComplexPatternType _xifexpression_1 = null;
    boolean _isRoot_1 = this.isRoot(node);
    if (_isRoot_1) {
      _xifexpression_1 = ComplexPatternType.NORMAL;
    } else {
      _xifexpression_1 = ComplexPatternType.ANONYMOUS;
    }
    final ComplexPatternType patternType = _xifexpression_1;
    this.generateComplexEventPattern(pattern, node, currentClassName, compositionEvents, acceptor, patternType);
    return currentClassName;
  }
  
  public QualifiedName generateComplexEventPattern(final ComplexEventPattern pattern, final Node node, final QualifiedName className, final List<Pair<QualifiedName, List<String>>> compositionPatterns, final IJvmDeclaredTypeAcceptor acceptor, final ComplexPatternType complexPatternType) {
    JvmGenericType _class = this.jvmTypesBuilder.toClass(pattern, className);
    final Procedure1<JvmGenericType> _function = new Procedure1<JvmGenericType>() {
      @Override
      public void apply(final JvmGenericType it) {
        EList<JvmTypeReference> _superTypes = it.getSuperTypes();
        JvmTypeReference _typeRef = ComplexGenerator.this.typeRefBuilder.typeRef(ParameterizableComplexEventPattern.class);
        ComplexGenerator.this.jvmTypesBuilder.<JvmTypeReference>operator_add(_superTypes, _typeRef);
        EList<JvmMember> _members = it.getMembers();
        final Procedure1<JvmConstructor> _function = new Procedure1<JvmConstructor>() {
          @Override
          public void apply(final JvmConstructor it) {
            final Procedure1<ITreeAppendable> _function = new Procedure1<ITreeAppendable>() {
              @Override
              public void apply(final ITreeAppendable it) {
                StringConcatenation _builder = new StringConcatenation();
                _builder.append("super();");
                _builder.newLine();
                it.append(_builder);
                StringConcatenation _builder_1 = new StringConcatenation();
                _builder_1.append("setOperator(");
                ITreeAppendable _append = it.append(_builder_1);
                StringConcatenation _builder_2 = new StringConcatenation();
                ITreeAppendable _referClass = ComplexGenerator.this._utils.referClass(it, ComplexGenerator.this.typeRefBuilder, pattern, EventsFactory.class);
                _builder_2.append(_referClass, "");
                _builder_2.append(".eINSTANCE");
                ITreeAppendable _append_1 = _append.append(_builder_2);
                StringConcatenation _builder_3 = new StringConcatenation();
                _builder_3.append(".");
                ComplexEventOperator _operator = node.getOperator();
                String _factoryMethod = ComplexGenerator.this.getFactoryMethod(_operator);
                _builder_3.append(_factoryMethod, "");
                ITreeAppendable _append_2 = _append_1.append(_builder_3);
                StringConcatenation _builder_4 = new StringConcatenation();
                _builder_4.append(");");
                _builder_4.newLine();
                _append_2.append(_builder_4);
                StringConcatenation _builder_5 = new StringConcatenation();
                _builder_5.newLine();
                _builder_5.append("// contained event patterns");
                _builder_5.newLine();
                it.append(_builder_5);
                for (final Pair<QualifiedName, List<String>> p : compositionPatterns) {
                  {
                    StringConcatenation _builder_6 = new StringConcatenation();
                    _builder_6.append("addEventPatternRefrence(new ");
                    ITreeAppendable _append_3 = it.append(_builder_6);
                    StringConcatenation _builder_7 = new StringConcatenation();
                    QualifiedName _first = p.getFirst();
                    ITreeAppendable _referClass_1 = ComplexGenerator.this._utils.referClass(it, ComplexGenerator.this.typeRefBuilder, _first, pattern);
                    _builder_7.append(_referClass_1, "");
                    ITreeAppendable _append_4 = _append_3.append(_builder_7);
                    StringConcatenation _builder_8 = new StringConcatenation();
                    _builder_8.append("(), ");
                    _append_4.append(_builder_8);
                    AbstractMultiplicity _multiplicity = node.getMultiplicity();
                    if ((_multiplicity instanceof Multiplicity)) {
                      StringConcatenation _builder_9 = new StringConcatenation();
                      AbstractMultiplicity _multiplicity_1 = node.getMultiplicity();
                      int _value = ((Multiplicity) _multiplicity_1).getValue();
                      _builder_9.append(_value, "");
                      it.append(_builder_9);
                    } else {
                      AbstractMultiplicity _multiplicity_2 = node.getMultiplicity();
                      if ((_multiplicity_2 instanceof Infinite)) {
                        StringConcatenation _builder_10 = new StringConcatenation();
                        ITreeAppendable _referClass_2 = ComplexGenerator.this._utils.referClass(it, ComplexGenerator.this.typeRefBuilder, pattern, EventsFactory.class);
                        _builder_10.append(_referClass_2, "");
                        ITreeAppendable _append_5 = it.append(_builder_10);
                        StringConcatenation _builder_11 = new StringConcatenation();
                        _builder_11.append(".eINSTANCE.createInfinite()");
                        _append_5.append(_builder_11);
                      } else {
                        AbstractMultiplicity _multiplicity_3 = node.getMultiplicity();
                        if ((_multiplicity_3 instanceof AtLeastOne)) {
                          StringConcatenation _builder_12 = new StringConcatenation();
                          ITreeAppendable _referClass_3 = ComplexGenerator.this._utils.referClass(it, ComplexGenerator.this.typeRefBuilder, pattern, EventsFactory.class);
                          _builder_12.append(_referClass_3, "");
                          ITreeAppendable _append_6 = it.append(_builder_12);
                          StringConcatenation _builder_13 = new StringConcatenation();
                          _builder_13.append(".eINSTANCE.createAtLeastOne()");
                          _append_6.append(_builder_13);
                        } else {
                          StringConcatenation _builder_14 = new StringConcatenation();
                          _builder_14.append("1");
                          it.append(_builder_14);
                        }
                      }
                    }
                    boolean _and = false;
                    List<String> _second = p.getSecond();
                    boolean _tripleNotEquals = (_second != null);
                    if (!_tripleNotEquals) {
                      _and = false;
                    } else {
                      List<String> _second_1 = p.getSecond();
                      boolean _isEmpty = _second_1.isEmpty();
                      boolean _not = (!_isEmpty);
                      _and = _not;
                    }
                    if (_and) {
                      StringConcatenation _builder_15 = new StringConcatenation();
                      _builder_15.append(", ");
                      ITreeAppendable _append_7 = it.append(_builder_15);
                      StringConcatenation _builder_16 = new StringConcatenation();
                      ITreeAppendable _referClass_4 = ComplexGenerator.this._utils.referClass(it, ComplexGenerator.this.typeRefBuilder, pattern, Lists.class);
                      _builder_16.append(_referClass_4, "");
                      ITreeAppendable _append_8 = _append_7.append(_builder_16);
                      StringConcatenation _builder_17 = new StringConcatenation();
                      _builder_17.append(".newArrayList(");
                      _append_8.append(_builder_17);
                      StringConcatenation _builder_18 = new StringConcatenation();
                      _builder_18.append("\"");
                      List<String> _second_2 = p.getSecond();
                      String _head = IterableExtensions.<String>head(_second_2);
                      _builder_18.append(_head, "");
                      _builder_18.append("\"");
                      it.append(_builder_18);
                      List<String> _second_3 = p.getSecond();
                      Iterable<String> _tail = IterableExtensions.<String>tail(_second_3);
                      for (final String param : _tail) {
                        StringConcatenation _builder_19 = new StringConcatenation();
                        _builder_19.append(", \"");
                        _builder_19.append(param, "");
                        _builder_19.append("\"");
                        it.append(_builder_19);
                      }
                      StringConcatenation _builder_20 = new StringConcatenation();
                      _builder_20.append(")");
                      it.append(_builder_20);
                    }
                    StringConcatenation _builder_21 = new StringConcatenation();
                    _builder_21.append(");");
                    _builder_21.newLine();
                    it.append(_builder_21);
                  }
                }
                Timewindow _timewindow = node.getTimewindow();
                boolean _tripleNotEquals = (_timewindow != null);
                if (_tripleNotEquals) {
                  StringConcatenation _builder_6 = new StringConcatenation();
                  _builder_6.append("                        ");
                  _builder_6.newLine();
                  ITreeAppendable _append_3 = it.append(_builder_6);
                  StringConcatenation _builder_7 = new StringConcatenation();
                  ITreeAppendable _referClass_1 = ComplexGenerator.this._utils.referClass(it, ComplexGenerator.this.typeRefBuilder, pattern, Timewindow.class);
                  _builder_7.append(_referClass_1, "");
                  ITreeAppendable _append_4 = _append_3.append(_builder_7);
                  StringConcatenation _builder_8 = new StringConcatenation();
                  _builder_8.append(" ");
                  _builder_8.append("timewindow = ");
                  ITreeAppendable _append_5 = _append_4.append(_builder_8);
                  StringConcatenation _builder_9 = new StringConcatenation();
                  ITreeAppendable _referClass_2 = ComplexGenerator.this._utils.referClass(it, ComplexGenerator.this.typeRefBuilder, pattern, EventsFactory.class);
                  _builder_9.append(_referClass_2, "");
                  _builder_9.append(".eINSTANCE");
                  ITreeAppendable _append_6 = _append_5.append(_builder_9);
                  StringConcatenation _builder_10 = new StringConcatenation();
                  _builder_10.append(".createTimewindow();");
                  _builder_10.newLine();
                  ITreeAppendable _append_7 = _append_6.append(_builder_10);
                  StringConcatenation _builder_11 = new StringConcatenation();
                  _builder_11.append("timewindow.setTime(");
                  Timewindow _timewindow_1 = node.getTimewindow();
                  long _time = _timewindow_1.getTime();
                  _builder_11.append(_time, "");
                  _builder_11.append(");");
                  _builder_11.newLineIfNotEmpty();
                  _builder_11.append("setTimewindow(timewindow);");
                  _builder_11.newLine();
                  _builder_11.append("    ");
                  _builder_11.newLine();
                  _append_7.append(_builder_11);
                }
                TypedParameterList _parameters = pattern.getParameters();
                boolean _tripleNotEquals_1 = (_parameters != null);
                if (_tripleNotEquals_1) {
                  StringConcatenation _builder_12 = new StringConcatenation();
                  {
                    TypedParameterList _parameters_1 = pattern.getParameters();
                    EList<TypedParameter> _parameters_2 = _parameters_1.getParameters();
                    for(final TypedParameter parameter : _parameters_2) {
                      _builder_12.newLineIfNotEmpty();
                      _builder_12.append("getParameterNames().add(\"");
                      String _name = parameter.getName();
                      _builder_12.append(_name, "");
                      _builder_12.append("\");");
                      _builder_12.newLineIfNotEmpty();
                    }
                  }
                  it.append(_builder_12);
                }
                StringConcatenation _builder_13 = new StringConcatenation();
                _builder_13.append("setId(\"");
                QualifiedName _lowerCase = className.toLowerCase();
                _builder_13.append(_lowerCase, "");
                _builder_13.append("\");");
                _builder_13.newLineIfNotEmpty();
                it.append(_builder_13);
                StringConcatenation _builder_14 = new StringConcatenation();
                _builder_14.append("setEventContext(");
                ITreeAppendable _append_8 = it.append(_builder_14);
                StringConcatenation _builder_15 = new StringConcatenation();
                ITreeAppendable _referClass_3 = ComplexGenerator.this._utils.referClass(it, ComplexGenerator.this.typeRefBuilder, pattern, EventContext.class);
                _builder_15.append(_referClass_3, "");
                ITreeAppendable _append_9 = _append_8.append(_builder_15);
                StringConcatenation _builder_16 = new StringConcatenation();
                _builder_16.append(".");
                ITreeAppendable _append_10 = _append_9.append(_builder_16);
                StringConcatenation _builder_17 = new StringConcatenation();
                EventContext _deriveContext = ComplexGenerator.this.deriveContext(pattern);
                String _literal = _deriveContext.getLiteral();
                _builder_17.append(_literal, "");
                ITreeAppendable _append_11 = _append_10.append(_builder_17);
                StringConcatenation _builder_18 = new StringConcatenation();
                _builder_18.append(");");
                _append_11.append(_builder_18);
              }
            };
            ComplexGenerator.this.jvmTypesBuilder.setBody(it, _function);
          }
        };
        JvmConstructor _constructor = ComplexGenerator.this.jvmTypesBuilder.toConstructor(pattern, _function);
        ComplexGenerator.this.jvmTypesBuilder.<JvmConstructor>operator_add(_members, _constructor);
      }
    };
    acceptor.<JvmGenericType>accept(_class, _function);
    boolean _isNormal = this.isNormal(complexPatternType);
    if (_isNormal) {
      FactoryManager _instance = FactoryManager.getInstance();
      _instance.add(className);
    } else {
      boolean _isAnonymous = this.isAnonymous(complexPatternType);
      if (_isAnonymous) {
        String _string = className.toString();
        this.anonManager.add(_string);
        return className;
      }
    }
    return null;
  }
  
  public EventContext deriveContext(final ComplexEventPattern pattern) {
    EventContext _xblockexpression = null;
    {
      final ContextEnum patternContext = pattern.getContext();
      EObject _eContainer = pattern.eContainer();
      final ContextEnum defaultContext = ((EventModel) _eContainer).getContext();
      EventContext _xifexpression = null;
      boolean _equals = patternContext.equals(ContextEnum.NOT_SET);
      boolean _not = (!_equals);
      if (_not) {
        HashMap<ContextEnum, EventContext> _contextMap = this.contextMap();
        _xifexpression = _contextMap.get(patternContext);
      } else {
        EventContext _xifexpression_1 = null;
        boolean _equals_1 = defaultContext.equals(ContextEnum.NOT_SET);
        if (_equals_1) {
          _xifexpression_1 = EventContext.CHRONICLE;
        } else {
          HashMap<ContextEnum, EventContext> _contextMap_1 = this.contextMap();
          _xifexpression_1 = _contextMap_1.get(defaultContext);
        }
        _xifexpression = _xifexpression_1;
      }
      _xblockexpression = _xifexpression;
    }
    return _xblockexpression;
  }
  
  private HashMap<ContextEnum, EventContext> contextMap() {
    HashMap<ContextEnum, EventContext> _xblockexpression = null;
    {
      final HashMap<ContextEnum, EventContext> map = Maps.<ContextEnum, EventContext>newHashMap();
      map.put(ContextEnum.NOT_SET, EventContext.NOT_SET);
      map.put(ContextEnum.CHRONICLE, EventContext.CHRONICLE);
      map.put(ContextEnum.IMMEDIATE, EventContext.IMMEDIATE);
      map.put(ContextEnum.STRICT, EventContext.STRICT_IMMEDIATE);
      _xblockexpression = map;
    }
    return _xblockexpression;
  }
  
  public ITreeAppendable expandMultiplicity(final Node node, final ITreeAppendable treeAppendable, final ComplexEventPattern pattern) {
    ITreeAppendable _xblockexpression = null;
    {
      final AbstractMultiplicity multiplicity = node.getMultiplicity();
      ITreeAppendable _switchResult = null;
      boolean _matched = false;
      if (!_matched) {
        if (multiplicity instanceof Multiplicity) {
          if (Objects.equal(multiplicity, ((Multiplicity)multiplicity))) {
            _matched=true;
            StringConcatenation _builder = new StringConcatenation();
            int _value = ((Multiplicity)multiplicity).getValue();
            _builder.append(_value, "");
            _switchResult = treeAppendable.append(_builder);
          }
        }
      }
      if (!_matched) {
        if (multiplicity instanceof Infinite) {
          if (Objects.equal(multiplicity, ((Infinite)multiplicity))) {
            _matched=true;
            StringConcatenation _builder = new StringConcatenation();
            ITreeAppendable _referClass = this._utils.referClass(treeAppendable, this.typeRefBuilder, pattern, EventsFactory.class);
            _builder.append(_referClass, "");
            ITreeAppendable _append = treeAppendable.append(_builder);
            StringConcatenation _builder_1 = new StringConcatenation();
            _builder_1.append(".eINSTANCE().createInfinite()");
            ITreeAppendable _append_1 = _append.append(_builder_1);
            StringConcatenation _builder_2 = new StringConcatenation();
            _builder_2.append(";");
            _switchResult = _append_1.append(_builder_2);
          }
        }
      }
      if (!_matched) {
        if (multiplicity instanceof AtLeastOne) {
          if (Objects.equal(multiplicity, ((AtLeastOne)multiplicity))) {
            _matched=true;
            StringConcatenation _builder = new StringConcatenation();
            ITreeAppendable _referClass = this._utils.referClass(treeAppendable, this.typeRefBuilder, pattern, EventsFactory.class);
            _builder.append(_referClass, "");
            ITreeAppendable _append = treeAppendable.append(_builder);
            StringConcatenation _builder_1 = new StringConcatenation();
            _builder_1.append(".eINSTANCE().createAtLeastOne()");
            ITreeAppendable _append_1 = _append.append(_builder_1);
            StringConcatenation _builder_2 = new StringConcatenation();
            _builder_2.append(";");
            _switchResult = _append_1.append(_builder_2);
          }
        }
      }
      _xblockexpression = _switchResult;
    }
    return _xblockexpression;
  }
  
  public boolean isNormal(final ComplexPatternType complexPatternType) {
    return complexPatternType.equals(ComplexPatternType.NORMAL);
  }
  
  public boolean isAnonymous(final ComplexPatternType complexPatternType) {
    return complexPatternType.equals(ComplexPatternType.ANONYMOUS);
  }
  
  protected String _getFactoryMethod(final FollowsOperator operator) {
    return "createFOLLOWS()";
  }
  
  protected String _getFactoryMethod(final OrOperator operator) {
    return "createOR()";
  }
  
  protected String _getFactoryMethod(final AndOperator operator) {
    return "createAND()";
  }
  
  protected String _getFactoryMethod(final UntilOperator operator) {
    return "createUNTIL()";
  }
  
  protected String _getFactoryMethod(final NegOperator operator) {
    return "createNEG()";
  }
  
  private boolean firstCondition = true;
  
  public CharSequence getCondition() {
    CharSequence _xifexpression = null;
    if (this.firstCondition) {
      StringConcatenation _builder = new StringConcatenation();
      _builder.append("if");
      _xifexpression = _builder;
    } else {
      StringConcatenation _builder_1 = new StringConcatenation();
      _builder_1.append("else if");
      _xifexpression = _builder_1;
    }
    return _xifexpression;
  }
  
  public String getFactoryMethod(final EObject operator) {
    if (operator instanceof AndOperator) {
      return _getFactoryMethod((AndOperator)operator);
    } else if (operator instanceof FollowsOperator) {
      return _getFactoryMethod((FollowsOperator)operator);
    } else if (operator instanceof NegOperator) {
      return _getFactoryMethod((NegOperator)operator);
    } else if (operator instanceof OrOperator) {
      return _getFactoryMethod((OrOperator)operator);
    } else if (operator instanceof UntilOperator) {
      return _getFactoryMethod((UntilOperator)operator);
    } else {
      throw new IllegalArgumentException("Unhandled parameter types: " +
        Arrays.<Object>asList(operator).toString());
    }
  }
}
