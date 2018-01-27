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

import com.google.common.collect.Lists;
import com.google.inject.Inject;
import java.util.ArrayList;
import java.util.List;
import org.eclipse.emf.common.util.EList;
import org.eclipse.viatra.cep.core.api.evm.CepActivationStates;
import org.eclipse.viatra.cep.core.api.patterns.IObservableComplexEventPattern;
import org.eclipse.viatra.cep.core.api.rules.CepJob;
import org.eclipse.viatra.cep.core.api.rules.ICepRule;
import org.eclipse.viatra.cep.core.metamodels.events.EventPattern;
import org.eclipse.viatra.cep.vepl.jvmmodel.FactoryManager;
import org.eclipse.viatra.cep.vepl.jvmmodel.NamingProvider;
import org.eclipse.viatra.cep.vepl.jvmmodel.Utils;
import org.eclipse.viatra.cep.vepl.vepl.ParameterizedPatternCall;
import org.eclipse.viatra.cep.vepl.vepl.Rule;
import org.eclipse.viatra.transformation.evm.api.Activation;
import org.eclipse.viatra.transformation.evm.api.Context;
import org.eclipse.viatra.transformation.evm.api.event.ActivationState;
import org.eclipse.xtend2.lib.StringConcatenation;
import org.eclipse.xtext.common.types.JvmConstructor;
import org.eclipse.xtext.common.types.JvmField;
import org.eclipse.xtext.common.types.JvmFormalParameter;
import org.eclipse.xtext.common.types.JvmGenericType;
import org.eclipse.xtext.common.types.JvmMember;
import org.eclipse.xtext.common.types.JvmOperation;
import org.eclipse.xtext.common.types.JvmTypeReference;
import org.eclipse.xtext.common.types.JvmWildcardTypeReference;
import org.eclipse.xtext.naming.QualifiedName;
import org.eclipse.xtext.xbase.XExpression;
import org.eclipse.xtext.xbase.compiler.output.ITreeAppendable;
import org.eclipse.xtext.xbase.jvmmodel.IJvmDeclaredTypeAcceptor;
import org.eclipse.xtext.xbase.jvmmodel.JvmTypeReferenceBuilder;
import org.eclipse.xtext.xbase.jvmmodel.JvmTypesBuilder;
import org.eclipse.xtext.xbase.lib.Extension;
import org.eclipse.xtext.xbase.lib.IterableExtensions;
import org.eclipse.xtext.xbase.lib.Procedures.Procedure1;

@SuppressWarnings({ "discouraged", "restriction" })
public class RuleGenerator {
  @Inject
  @Extension
  private JvmTypesBuilder jvmTypesBuilder;
  
  @Inject
  @Extension
  private Utils _utils;
  
  @Inject
  @Extension
  private NamingProvider _namingProvider;
  
  private JvmTypeReferenceBuilder typeRefBuilder;
  
  public void generateRulesAndJobs(final List<Rule> rules, final IJvmDeclaredTypeAcceptor acceptor, final JvmTypeReferenceBuilder typeRefBuilder) {
    this.typeRefBuilder = typeRefBuilder;
    ArrayList<QualifiedName> generatedRuleClassNames = Lists.<QualifiedName>newArrayList();
    for (final Rule r : rules) {
      {
        final Rule rule = ((Rule) r);
        this.generateRuleClass(rule, acceptor);
        this.generateJobClass(rule, acceptor);
        QualifiedName _fqn = this._namingProvider.getFqn(rule);
        generatedRuleClassNames.add(_fqn);
      }
    }
  }
  
  private void generateRuleClass(final Rule rule, final IJvmDeclaredTypeAcceptor acceptor) {
    QualifiedName _fqn = this._namingProvider.getFqn(rule);
    JvmGenericType _class = this.jvmTypesBuilder.toClass(rule, _fqn);
    final Procedure1<JvmGenericType> _function = new Procedure1<JvmGenericType>() {
      @Override
      public void apply(final JvmGenericType it) {
        String _documentation = RuleGenerator.this.jvmTypesBuilder.getDocumentation(rule);
        RuleGenerator.this.jvmTypesBuilder.setDocumentation(it, _documentation);
        EList<JvmTypeReference> _superTypes = it.getSuperTypes();
        JvmTypeReference _typeRef = RuleGenerator.this.typeRefBuilder.typeRef(ICepRule.class);
        RuleGenerator.this.jvmTypesBuilder.<JvmTypeReference>operator_add(_superTypes, _typeRef);
        final EList<ParameterizedPatternCall> eventPatterns = rule.getEventPatterns();
        EList<JvmMember> _members = it.getMembers();
        JvmTypeReference _typeRef_1 = RuleGenerator.this.typeRefBuilder.typeRef(EventPattern.class);
        JvmTypeReference _typeRef_2 = RuleGenerator.this.typeRefBuilder.typeRef(List.class, _typeRef_1);
        final Procedure1<JvmField> _function = new Procedure1<JvmField>() {
          @Override
          public void apply(final JvmField it) {
            final Procedure1<ITreeAppendable> _function = new Procedure1<ITreeAppendable>() {
              @Override
              public void apply(final ITreeAppendable it) {
                StringConcatenation _builder = new StringConcatenation();
                ITreeAppendable _referClass = RuleGenerator.this._utils.referClass(it, RuleGenerator.this.typeRefBuilder, rule, Lists.class);
                _builder.append(_referClass, "");
                _builder.append(".newArrayList()");
                it.append(_builder);
              }
            };
            RuleGenerator.this.jvmTypesBuilder.setInitializer(it, _function);
          }
        };
        JvmField _field = RuleGenerator.this.jvmTypesBuilder.toField(rule, "eventPatterns", _typeRef_2, _function);
        RuleGenerator.this.jvmTypesBuilder.<JvmField>operator_add(_members, _field);
        EList<JvmMember> _members_1 = it.getMembers();
        JvmTypeReference _typeRef_3 = RuleGenerator.this.typeRefBuilder.typeRef(IObservableComplexEventPattern.class);
        JvmTypeReference _typeRef_4 = RuleGenerator.this.typeRefBuilder.typeRef(CepJob.class, _typeRef_3);
        final Procedure1<JvmField> _function_1 = new Procedure1<JvmField>() {
          @Override
          public void apply(final JvmField it) {
            final Procedure1<ITreeAppendable> _function = new Procedure1<ITreeAppendable>() {
              @Override
              public void apply(final ITreeAppendable it) {
                StringConcatenation _builder = new StringConcatenation();
                _builder.append("new ");
                ITreeAppendable _append = it.append(_builder);
                StringConcatenation _builder_1 = new StringConcatenation();
                QualifiedName _jobClassName = RuleGenerator.this._namingProvider.getJobClassName(rule);
                ITreeAppendable _referClass = RuleGenerator.this._utils.referClass(it, RuleGenerator.this.typeRefBuilder, _jobClassName, rule);
                _builder_1.append(_referClass, "");
                ITreeAppendable _append_1 = _append.append(_builder_1);
                StringConcatenation _builder_2 = new StringConcatenation();
                _builder_2.append("(");
                ITreeAppendable _append_2 = _append_1.append(_builder_2);
                StringConcatenation _builder_3 = new StringConcatenation();
                ITreeAppendable _referClass_1 = RuleGenerator.this._utils.referClass(it, RuleGenerator.this.typeRefBuilder, rule, CepActivationStates.class);
                _builder_3.append(_referClass_1, "");
                _builder_3.append(".ACTIVE)");
                _append_2.append(_builder_3);
              }
            };
            RuleGenerator.this.jvmTypesBuilder.setInitializer(it, _function);
          }
        };
        JvmField _field_1 = RuleGenerator.this.jvmTypesBuilder.toField(rule, "job", _typeRef_4, _function_1);
        RuleGenerator.this.jvmTypesBuilder.<JvmField>operator_add(_members_1, _field_1);
        EList<JvmMember> _members_2 = it.getMembers();
        final Procedure1<JvmConstructor> _function_2 = new Procedure1<JvmConstructor>() {
          @Override
          public void apply(final JvmConstructor it) {
            final Procedure1<ITreeAppendable> _function = new Procedure1<ITreeAppendable>() {
              @Override
              public void apply(final ITreeAppendable it) {
                StringConcatenation _builder = new StringConcatenation();
                String _enumerateAssignableEventPatterns = RuleGenerator.this.enumerateAssignableEventPatterns(it, rule);
                _builder.append(_enumerateAssignableEventPatterns, "");
                it.append(_builder);
              }
            };
            RuleGenerator.this.jvmTypesBuilder.setBody(it, _function);
          }
        };
        JvmConstructor _constructor = RuleGenerator.this.jvmTypesBuilder.toConstructor(rule, _function_2);
        RuleGenerator.this.jvmTypesBuilder.<JvmConstructor>operator_add(_members_2, _constructor);
        JvmTypeReference _typeRef_5 = RuleGenerator.this.typeRefBuilder.typeRef(EventPattern.class);
        JvmTypeReference _typeRef_6 = RuleGenerator.this.typeRefBuilder.typeRef(List.class, _typeRef_5);
        JvmOperation patternsGetter = RuleGenerator.this.jvmTypesBuilder.toGetter(rule, "eventPatterns", _typeRef_6);
        JvmTypeReference _typeRef_7 = RuleGenerator.this.typeRefBuilder.typeRef(IObservableComplexEventPattern.class);
        JvmTypeReference _typeRef_8 = RuleGenerator.this.typeRefBuilder.typeRef(CepJob.class, _typeRef_7);
        JvmOperation jobGetter = RuleGenerator.this.jvmTypesBuilder.toGetter(rule, "job", _typeRef_8);
        RuleGenerator.this._utils.addOverrideAnnotation(patternsGetter, rule);
        RuleGenerator.this._utils.addOverrideAnnotation(jobGetter, rule);
        EList<JvmMember> _members_3 = it.getMembers();
        RuleGenerator.this.jvmTypesBuilder.<JvmOperation>operator_add(_members_3, patternsGetter);
        EList<JvmMember> _members_4 = it.getMembers();
        RuleGenerator.this.jvmTypesBuilder.<JvmOperation>operator_add(_members_4, jobGetter);
      }
    };
    acceptor.<JvmGenericType>accept(_class, _function);
    FactoryManager _instance = FactoryManager.getInstance();
    QualifiedName _fqn_1 = this._namingProvider.getFqn(rule);
    _instance.add(_fqn_1);
  }
  
  private void generateJobClass(final Rule appRule, final IJvmDeclaredTypeAcceptor acceptor) {
    QualifiedName _jobClassName = this._namingProvider.getJobClassName(appRule);
    JvmGenericType _class = this.jvmTypesBuilder.toClass(appRule, _jobClassName);
    final Procedure1<JvmGenericType> _function = new Procedure1<JvmGenericType>() {
      @Override
      public void apply(final JvmGenericType it) {
        String _documentation = RuleGenerator.this.jvmTypesBuilder.getDocumentation(appRule);
        RuleGenerator.this.jvmTypesBuilder.setDocumentation(it, _documentation);
        EList<JvmTypeReference> _superTypes = it.getSuperTypes();
        JvmTypeReference _typeRef = RuleGenerator.this.typeRefBuilder.typeRef(IObservableComplexEventPattern.class);
        JvmTypeReference _typeRef_1 = RuleGenerator.this.typeRefBuilder.typeRef(CepJob.class, _typeRef);
        RuleGenerator.this.jvmTypesBuilder.<JvmTypeReference>operator_add(_superTypes, _typeRef_1);
        EList<JvmMember> _members = it.getMembers();
        final Procedure1<JvmConstructor> _function = new Procedure1<JvmConstructor>() {
          @Override
          public void apply(final JvmConstructor it) {
            EList<JvmFormalParameter> _parameters = it.getParameters();
            JvmTypeReference _typeRef = RuleGenerator.this.typeRefBuilder.typeRef(ActivationState.class);
            JvmFormalParameter _parameter = RuleGenerator.this.jvmTypesBuilder.toParameter(appRule, "activationState", _typeRef);
            RuleGenerator.this.jvmTypesBuilder.<JvmFormalParameter>operator_add(_parameters, _parameter);
            final Procedure1<ITreeAppendable> _function = new Procedure1<ITreeAppendable>() {
              @Override
              public void apply(final ITreeAppendable it) {
                StringConcatenation _builder = new StringConcatenation();
                _builder.append("super(activationState);");
                it.append(_builder);
              }
            };
            RuleGenerator.this.jvmTypesBuilder.setBody(it, _function);
          }
        };
        JvmConstructor _constructor = RuleGenerator.this.jvmTypesBuilder.toConstructor(appRule, _function);
        RuleGenerator.this.jvmTypesBuilder.<JvmConstructor>operator_add(_members, _constructor);
        JvmTypeReference _typeRef_2 = RuleGenerator.this.typeRefBuilder.typeRef("void");
        final Procedure1<JvmOperation> _function_1 = new Procedure1<JvmOperation>() {
          @Override
          public void apply(final JvmOperation it) {
            EList<JvmFormalParameter> _parameters = it.getParameters();
            JvmTypeReference _typeRef = RuleGenerator.this.typeRefBuilder.typeRef(IObservableComplexEventPattern.class);
            JvmTypeReference _cloneWithProxies = RuleGenerator.this.jvmTypesBuilder.cloneWithProxies(_typeRef);
            JvmWildcardTypeReference _wildCardExtends = RuleGenerator.this._utils.wildCardExtends(_cloneWithProxies);
            JvmTypeReference _typeRef_1 = RuleGenerator.this.typeRefBuilder.typeRef(Activation.class, _wildCardExtends);
            JvmFormalParameter _parameter = RuleGenerator.this.jvmTypesBuilder.toParameter(appRule, "ruleInstance", _typeRef_1);
            RuleGenerator.this.jvmTypesBuilder.<JvmFormalParameter>operator_add(_parameters, _parameter);
            EList<JvmFormalParameter> _parameters_1 = it.getParameters();
            JvmTypeReference _typeRef_2 = RuleGenerator.this.typeRefBuilder.typeRef(Context.class);
            JvmFormalParameter _parameter_1 = RuleGenerator.this.jvmTypesBuilder.toParameter(appRule, "context", _typeRef_2);
            RuleGenerator.this.jvmTypesBuilder.<JvmFormalParameter>operator_add(_parameters_1, _parameter_1);
            XExpression _action = appRule.getAction();
            boolean _tripleNotEquals = (_action != null);
            if (_tripleNotEquals) {
              XExpression _action_1 = appRule.getAction();
              RuleGenerator.this.jvmTypesBuilder.setBody(it, _action_1);
            }
          }
        };
        JvmOperation executeMethod = RuleGenerator.this.jvmTypesBuilder.toMethod(appRule, "execute", _typeRef_2, _function_1);
        RuleGenerator.this._utils.addOverrideAnnotation(executeMethod, appRule);
        JvmTypeReference _typeRef_3 = RuleGenerator.this.typeRefBuilder.typeRef("void");
        final Procedure1<JvmOperation> _function_2 = new Procedure1<JvmOperation>() {
          @Override
          public void apply(final JvmOperation it) {
            EList<JvmFormalParameter> _parameters = it.getParameters();
            JvmTypeReference _typeRef = RuleGenerator.this.typeRefBuilder.typeRef(IObservableComplexEventPattern.class);
            JvmTypeReference _cloneWithProxies = RuleGenerator.this.jvmTypesBuilder.cloneWithProxies(_typeRef);
            JvmWildcardTypeReference _wildCardExtends = RuleGenerator.this._utils.wildCardExtends(_cloneWithProxies);
            JvmTypeReference _typeRef_1 = RuleGenerator.this.typeRefBuilder.typeRef(Activation.class, _wildCardExtends);
            JvmFormalParameter _parameter = RuleGenerator.this.jvmTypesBuilder.toParameter(appRule, "ruleInstance", _typeRef_1);
            RuleGenerator.this.jvmTypesBuilder.<JvmFormalParameter>operator_add(_parameters, _parameter);
            EList<JvmFormalParameter> _parameters_1 = it.getParameters();
            JvmTypeReference _typeRef_2 = RuleGenerator.this.typeRefBuilder.typeRef(Exception.class);
            JvmFormalParameter _parameter_1 = RuleGenerator.this.jvmTypesBuilder.toParameter(appRule, "exception", _typeRef_2);
            RuleGenerator.this.jvmTypesBuilder.<JvmFormalParameter>operator_add(_parameters_1, _parameter_1);
            EList<JvmFormalParameter> _parameters_2 = it.getParameters();
            JvmTypeReference _typeRef_3 = RuleGenerator.this.typeRefBuilder.typeRef(Context.class);
            JvmFormalParameter _parameter_2 = RuleGenerator.this.jvmTypesBuilder.toParameter(appRule, "context", _typeRef_3);
            RuleGenerator.this.jvmTypesBuilder.<JvmFormalParameter>operator_add(_parameters_2, _parameter_2);
            final Procedure1<ITreeAppendable> _function = new Procedure1<ITreeAppendable>() {
              @Override
              public void apply(final ITreeAppendable it) {
                StringConcatenation _builder = new StringConcatenation();
                _builder.append("//not gonna happen");
                it.append(_builder);
              }
            };
            RuleGenerator.this.jvmTypesBuilder.setBody(it, _function);
          }
        };
        JvmOperation errorMethod = RuleGenerator.this.jvmTypesBuilder.toMethod(appRule, "handleError", _typeRef_3, _function_2);
        RuleGenerator.this._utils.addOverrideAnnotation(errorMethod, appRule);
        EList<JvmMember> _members_1 = it.getMembers();
        RuleGenerator.this.jvmTypesBuilder.<JvmOperation>operator_add(_members_1, executeMethod);
        EList<JvmMember> _members_2 = it.getMembers();
        RuleGenerator.this.jvmTypesBuilder.<JvmOperation>operator_add(_members_2, errorMethod);
      }
    };
    acceptor.<JvmGenericType>accept(_class, _function);
  }
  
  public String enumerateAssignableEventPatterns(final ITreeAppendable appendable, final Rule rule) {
    boolean _or = false;
    if ((rule == null)) {
      _or = true;
    } else {
      EList<ParameterizedPatternCall> _eventPatterns = rule.getEventPatterns();
      boolean _isEmpty = _eventPatterns.isEmpty();
      _or = _isEmpty;
    }
    if (_or) {
      return "";
    }
    EList<ParameterizedPatternCall> _eventPatterns_1 = rule.getEventPatterns();
    for (final ParameterizedPatternCall ep : _eventPatterns_1) {
      {
        StringConcatenation _builder = new StringConcatenation();
        _builder.append("eventPatterns.add(new ");
        ITreeAppendable _append = appendable.append(_builder);
        StringConcatenation _builder_1 = new StringConcatenation();
        org.eclipse.viatra.cep.vepl.vepl.EventPattern _eventPattern = ep.getEventPattern();
        QualifiedName _patternFqn = this._namingProvider.getPatternFqn(_eventPattern);
        ITreeAppendable _referClass = this._utils.referClass(appendable, this.typeRefBuilder, _patternFqn, rule);
        _builder_1.append(_referClass, "");
        ITreeAppendable _append_1 = _append.append(_builder_1);
        StringConcatenation _builder_2 = new StringConcatenation();
        _builder_2.append("());");
        _append_1.append(_builder_2);
        EList<ParameterizedPatternCall> _eventPatterns_2 = rule.getEventPatterns();
        ParameterizedPatternCall _last = IterableExtensions.<ParameterizedPatternCall>last(_eventPatterns_2);
        boolean _equals = ep.equals(_last);
        boolean _not = (!_equals);
        if (_not) {
          appendable.append("\n");
        }
      }
    }
    return null;
  }
}
