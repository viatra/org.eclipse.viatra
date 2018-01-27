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
import java.util.List;
import org.eclipse.emf.common.util.EList;
import org.eclipse.viatra.cep.core.api.rules.ICepRule;
import org.eclipse.viatra.cep.core.metamodels.events.EventSource;
import org.eclipse.viatra.cep.vepl.jvmmodel.FactoryManager;
import org.eclipse.viatra.cep.vepl.jvmmodel.FactoryMethodParameter;
import org.eclipse.viatra.cep.vepl.jvmmodel.NamingProvider;
import org.eclipse.viatra.cep.vepl.jvmmodel.Utils;
import org.eclipse.viatra.cep.vepl.vepl.EventModel;
import org.eclipse.xtend2.lib.StringConcatenation;
import org.eclipse.xtext.common.types.JvmField;
import org.eclipse.xtext.common.types.JvmFormalParameter;
import org.eclipse.xtext.common.types.JvmGenericType;
import org.eclipse.xtext.common.types.JvmMember;
import org.eclipse.xtext.common.types.JvmOperation;
import org.eclipse.xtext.common.types.JvmTypeReference;
import org.eclipse.xtext.common.types.JvmWildcardTypeReference;
import org.eclipse.xtext.naming.QualifiedName;
import org.eclipse.xtext.xbase.compiler.output.ITreeAppendable;
import org.eclipse.xtext.xbase.jvmmodel.IJvmDeclaredTypeAcceptor;
import org.eclipse.xtext.xbase.jvmmodel.JvmTypeReferenceBuilder;
import org.eclipse.xtext.xbase.jvmmodel.JvmTypesBuilder;
import org.eclipse.xtext.xbase.lib.Extension;
import org.eclipse.xtext.xbase.lib.Functions.Function1;
import org.eclipse.xtext.xbase.lib.IterableExtensions;
import org.eclipse.xtext.xbase.lib.Procedures.Procedure1;

@SuppressWarnings({ "discouraged", "restriction" })
public class FactoryGenerator {
  @Inject
  @Extension
  private JvmTypesBuilder jvmTypesBuilder;
  
  @Inject
  @Extension
  private NamingProvider _namingProvider;
  
  @Inject
  @Extension
  private Utils _utils;
  
  public void generateFactory(final EventModel model, final IJvmDeclaredTypeAcceptor acceptor, final JvmTypeReferenceBuilder typeRefBuilder) {
    QualifiedName _factoryFqn = this._namingProvider.getFactoryFqn(model);
    JvmGenericType _class = this.jvmTypesBuilder.toClass(model, _factoryFqn);
    final Procedure1<JvmGenericType> _function = new Procedure1<JvmGenericType>() {
      @Override
      public void apply(final JvmGenericType it) {
        JvmTypeReference _typeRef = typeRefBuilder.typeRef("CepFactory");
        JvmField instanceField = FactoryGenerator.this.jvmTypesBuilder.toField(model, "instance", _typeRef);
        instanceField.setStatic(true);
        EList<JvmMember> _members = it.getMembers();
        FactoryGenerator.this.jvmTypesBuilder.<JvmField>operator_add(_members, instanceField);
        JvmTypeReference _typeRef_1 = typeRefBuilder.typeRef("CepFactory");
        final Procedure1<JvmOperation> _function = new Procedure1<JvmOperation>() {
          @Override
          public void apply(final JvmOperation it) {
            final Procedure1<ITreeAppendable> _function = new Procedure1<ITreeAppendable>() {
              @Override
              public void apply(final ITreeAppendable it) {
                StringConcatenation _builder = new StringConcatenation();
                _builder.append("if(instance == null){");
                _builder.newLine();
                _builder.append("    ");
                _builder.append("instance = new CepFactory();");
                _builder.newLine();
                _builder.append("}");
                _builder.newLine();
                _builder.append("return instance;");
                it.append(_builder);
              }
            };
            FactoryGenerator.this.jvmTypesBuilder.setBody(it, _function);
          }
        };
        JvmOperation instanceMethod = FactoryGenerator.this.jvmTypesBuilder.toMethod(model, "getInstance", _typeRef_1, _function);
        instanceMethod.setStatic(true);
        EList<JvmMember> _members_1 = it.getMembers();
        FactoryGenerator.this.jvmTypesBuilder.<JvmOperation>operator_add(_members_1, instanceMethod);
        FactoryManager _instance = FactoryManager.getInstance();
        List<QualifiedName> _registeredClasses = _instance.getRegisteredClasses();
        for (final QualifiedName fqn : _registeredClasses) {
          boolean _isEvent = FactoryGenerator.this._namingProvider.isEvent(fqn);
          if (_isEvent) {
            EList<JvmMember> _members_2 = it.getMembers();
            JvmOperation parametricEventMethod = FactoryGenerator.this.createFactoryMethod(fqn, model, acceptor, _members_2, 
              FactoryMethodParameter.EVENTSOURCE, typeRefBuilder);
            EList<JvmFormalParameter> _parameters = parametricEventMethod.getParameters();
            JvmTypeReference _typeRef_2 = typeRefBuilder.typeRef(EventSource.class);
            JvmFormalParameter _parameter = FactoryGenerator.this.jvmTypesBuilder.toParameter(model, "eventSource", _typeRef_2);
            _parameters.add(_parameter);
            EList<JvmMember> _members_3 = it.getMembers();
            FactoryGenerator.this.jvmTypesBuilder.<JvmOperation>operator_add(_members_3, parametricEventMethod);
            EList<JvmMember> _members_4 = it.getMembers();
            JvmOperation simpleEventMethod = FactoryGenerator.this.createFactoryMethod(fqn, model, acceptor, _members_4, 
              FactoryMethodParameter.NULL, typeRefBuilder);
            EList<JvmMember> _members_5 = it.getMembers();
            FactoryGenerator.this.jvmTypesBuilder.<JvmOperation>operator_add(_members_5, simpleEventMethod);
          } else {
            boolean _isRule = FactoryGenerator.this._namingProvider.isRule(fqn);
            if (_isRule) {
              EList<JvmMember> _members_6 = it.getMembers();
              JvmOperation method = FactoryGenerator.this.createRuleFactoryMethod(fqn, model, acceptor, _members_6, FactoryMethodParameter.EMPTY, typeRefBuilder);
              EList<JvmMember> _members_7 = it.getMembers();
              FactoryGenerator.this.jvmTypesBuilder.<JvmOperation>operator_add(_members_7, method);
            } else {
              EList<JvmMember> _members_8 = it.getMembers();
              JvmOperation method_1 = FactoryGenerator.this.createFactoryMethod(fqn, model, acceptor, _members_8, FactoryMethodParameter.EMPTY, typeRefBuilder);
              EList<JvmMember> _members_9 = it.getMembers();
              FactoryGenerator.this.jvmTypesBuilder.<JvmOperation>operator_add(_members_9, method_1);
            }
          }
        }
        FactoryManager _instance_1 = FactoryManager.getInstance();
        List<QualifiedName> _registeredClasses_1 = _instance_1.getRegisteredClasses();
        final Function1<QualifiedName, Boolean> _function_1 = new Function1<QualifiedName, Boolean>() {
          @Override
          public Boolean apply(final QualifiedName fqn) {
            return Boolean.valueOf(FactoryGenerator.this._namingProvider.isRule(fqn));
          }
        };
        Iterable<QualifiedName> _filter = IterableExtensions.<QualifiedName>filter(_registeredClasses_1, _function_1);
        final List<QualifiedName> rules = IterableExtensions.<QualifiedName>toList(_filter);
        EList<JvmMember> _members_10 = it.getMembers();
        EList<JvmMember> _members_11 = it.getMembers();
        JvmOperation _createAllRulesMethod = FactoryGenerator.this.createAllRulesMethod(rules, model, acceptor, _members_11, typeRefBuilder);
        FactoryGenerator.this.jvmTypesBuilder.<JvmOperation>operator_add(_members_10, _createAllRulesMethod);
      }
    };
    acceptor.<JvmGenericType>accept(_class, _function);
  }
  
  private JvmOperation createFactoryMethod(final QualifiedName fqn, final EventModel model, final IJvmDeclaredTypeAcceptor acceptor, final List<JvmMember> members, final FactoryMethodParameter methodParameter, final JvmTypeReferenceBuilder typeRefBuilder) {
    JvmOperation _xblockexpression = null;
    {
      String _lastSegment = fqn.getLastSegment();
      String _plus = ("create" + _lastSegment);
      String _string = fqn.toString();
      JvmTypeReference _typeRef = typeRefBuilder.typeRef(_string);
      final Procedure1<JvmOperation> _function = new Procedure1<JvmOperation>() {
        @Override
        public void apply(final JvmOperation it) {
          final Procedure1<ITreeAppendable> _function = new Procedure1<ITreeAppendable>() {
            @Override
            public void apply(final ITreeAppendable it) {
              StringConcatenation _builder = new StringConcatenation();
              _builder.append("return new ");
              ITreeAppendable _append = it.append(_builder);
              StringConcatenation _builder_1 = new StringConcatenation();
              ITreeAppendable _referClass = FactoryGenerator.this._utils.referClass(it, typeRefBuilder, fqn, model);
              _builder_1.append(_referClass, "");
              ITreeAppendable _append_1 = _append.append(_builder_1);
              StringConcatenation _builder_2 = new StringConcatenation();
              _builder_2.append("(");
              String _literal = methodParameter.getLiteral();
              _builder_2.append(_literal, "");
              _builder_2.append(");");
              _append_1.append(_builder_2);
            }
          };
          FactoryGenerator.this.jvmTypesBuilder.setBody(it, _function);
        }
      };
      JvmOperation method = this.jvmTypesBuilder.toMethod(model, _plus, _typeRef, _function);
      StringConcatenation _builder = new StringConcatenation();
      _builder.append("Factory method for ");
      String _type = this._namingProvider.getType(fqn);
      _builder.append(_type, "");
      _builder.append(" {@link ");
      String _lastSegment_1 = fqn.getLastSegment();
      _builder.append(_lastSegment_1, "");
      _builder.append("}.");
      this.jvmTypesBuilder.setDocumentation(method, _builder.toString());
      _xblockexpression = method;
    }
    return _xblockexpression;
  }
  
  private JvmOperation createRuleFactoryMethod(final QualifiedName fqn, final EventModel model, final IJvmDeclaredTypeAcceptor acceptor, final List<JvmMember> members, final FactoryMethodParameter methodParameter, final JvmTypeReferenceBuilder typeRefBuilder) {
    JvmOperation _xblockexpression = null;
    {
      String _lastSegment = fqn.getLastSegment();
      String _plus = ("rule_" + _lastSegment);
      JvmTypeReference _typeRef = typeRefBuilder.typeRef(ICepRule.class);
      JvmTypeReference _cloneWithProxies = this.jvmTypesBuilder.cloneWithProxies(_typeRef);
      JvmWildcardTypeReference _wildCardExtends = this._utils.wildCardExtends(_cloneWithProxies);
      JvmTypeReference _typeRef_1 = typeRefBuilder.typeRef(Class.class, _wildCardExtends);
      final Procedure1<JvmOperation> _function = new Procedure1<JvmOperation>() {
        @Override
        public void apply(final JvmOperation it) {
          final Procedure1<ITreeAppendable> _function = new Procedure1<ITreeAppendable>() {
            @Override
            public void apply(final ITreeAppendable it) {
              StringConcatenation _builder = new StringConcatenation();
              _builder.append("return ");
              ITreeAppendable _append = it.append(_builder);
              StringConcatenation _builder_1 = new StringConcatenation();
              ITreeAppendable _referClass = FactoryGenerator.this._utils.referClass(it, typeRefBuilder, fqn, model);
              _builder_1.append(_referClass, "");
              ITreeAppendable _append_1 = _append.append(_builder_1);
              StringConcatenation _builder_2 = new StringConcatenation();
              _builder_2.append(".class;");
              _append_1.append(_builder_2);
            }
          };
          FactoryGenerator.this.jvmTypesBuilder.setBody(it, _function);
        }
      };
      JvmOperation method = this.jvmTypesBuilder.toMethod(model, _plus, _typeRef_1, _function);
      StringConcatenation _builder = new StringConcatenation();
      _builder.append("Factory method for ");
      String _type = this._namingProvider.getType(fqn);
      _builder.append(_type, "");
      _builder.append(" {@link ");
      String _lastSegment_1 = fqn.getLastSegment();
      _builder.append(_lastSegment_1, "");
      _builder.append("}.");
      this.jvmTypesBuilder.setDocumentation(method, _builder.toString());
      _xblockexpression = method;
    }
    return _xblockexpression;
  }
  
  private JvmOperation createAllRulesMethod(final List<QualifiedName> ruleFqns, final EventModel model, final IJvmDeclaredTypeAcceptor acceptor, final List<JvmMember> members, final JvmTypeReferenceBuilder typeRefBuilder) {
    JvmOperation _xblockexpression = null;
    {
      JvmTypeReference _typeRef = typeRefBuilder.typeRef(ICepRule.class);
      JvmTypeReference _cloneWithProxies = this.jvmTypesBuilder.cloneWithProxies(_typeRef);
      JvmWildcardTypeReference _wildCardExtends = this._utils.wildCardExtends(_cloneWithProxies);
      JvmTypeReference _typeRef_1 = typeRefBuilder.typeRef(Class.class, _wildCardExtends);
      JvmTypeReference _typeRef_2 = typeRefBuilder.typeRef(List.class, _typeRef_1);
      final Procedure1<JvmOperation> _function = new Procedure1<JvmOperation>() {
        @Override
        public void apply(final JvmOperation it) {
          final Procedure1<ITreeAppendable> _function = new Procedure1<ITreeAppendable>() {
            @Override
            public void apply(final ITreeAppendable it) {
              StringConcatenation _builder = new StringConcatenation();
              _builder.append("List<Class<? extends ICepRule>> rules = ");
              it.append(_builder);
              StringConcatenation _builder_1 = new StringConcatenation();
              ITreeAppendable _referClass = FactoryGenerator.this._utils.referClass(it, typeRefBuilder, model, Lists.class);
              _builder_1.append(_referClass, "");
              _builder_1.append(".newArrayList();");
              _builder_1.newLineIfNotEmpty();
              it.append(_builder_1);
              for (final QualifiedName fqn : ruleFqns) {
                StringConcatenation _builder_2 = new StringConcatenation();
                _builder_2.append("rules.add(");
                ITreeAppendable _append = it.append(_builder_2);
                StringConcatenation _builder_3 = new StringConcatenation();
                ITreeAppendable _referClass_1 = FactoryGenerator.this._utils.referClass(it, typeRefBuilder, fqn, model);
                _builder_3.append(_referClass_1, "");
                ITreeAppendable _append_1 = _append.append(_builder_3);
                StringConcatenation _builder_4 = new StringConcatenation();
                _builder_4.append(".class);");
                _builder_4.newLine();
                _append_1.append(_builder_4);
              }
              StringConcatenation _builder_5 = new StringConcatenation();
              _builder_5.append("return rules;");
              it.append(_builder_5);
            }
          };
          FactoryGenerator.this.jvmTypesBuilder.setBody(it, _function);
        }
      };
      JvmOperation method = this.jvmTypesBuilder.toMethod(model, "allRules", _typeRef_2, _function);
      StringConcatenation _builder = new StringConcatenation();
      _builder.append("Factory method for instantiating every defined rule.");
      this.jvmTypesBuilder.setDocumentation(method, _builder.toString());
      _xblockexpression = method;
    }
    return _xblockexpression;
  }
}
