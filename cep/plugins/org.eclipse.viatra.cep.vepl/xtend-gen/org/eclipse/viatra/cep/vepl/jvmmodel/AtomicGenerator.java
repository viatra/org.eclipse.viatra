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

import com.google.inject.Inject;
import org.eclipse.emf.common.util.EList;
import org.eclipse.viatra.cep.core.api.events.ParameterizableEventInstance;
import org.eclipse.viatra.cep.core.api.events.ParameterizableViatraQueryPatternEventInstance;
import org.eclipse.viatra.cep.core.metamodels.events.EventSource;
import org.eclipse.viatra.cep.core.metamodels.events.impl.AtomicEventPatternImpl;
import org.eclipse.viatra.cep.vepl.jvmmodel.FactoryManager;
import org.eclipse.viatra.cep.vepl.jvmmodel.NamingProvider;
import org.eclipse.viatra.cep.vepl.jvmmodel.Utils;
import org.eclipse.viatra.cep.vepl.vepl.AtomicEventPattern;
import org.eclipse.viatra.cep.vepl.vepl.ModelElement;
import org.eclipse.viatra.cep.vepl.vepl.QueryResultChangeEventPattern;
import org.eclipse.viatra.cep.vepl.vepl.Trait;
import org.eclipse.viatra.cep.vepl.vepl.TraitList;
import org.eclipse.viatra.cep.vepl.vepl.TraitTypedParameterList;
import org.eclipse.viatra.cep.vepl.vepl.TypedParameter;
import org.eclipse.viatra.cep.vepl.vepl.TypedParameterList;
import org.eclipse.viatra.cep.vepl.vepl.TypedParameterWithDefaultValue;
import org.eclipse.xtend2.lib.StringConcatenation;
import org.eclipse.xtext.common.types.JvmConstructor;
import org.eclipse.xtext.common.types.JvmField;
import org.eclipse.xtext.common.types.JvmFormalParameter;
import org.eclipse.xtext.common.types.JvmGenericType;
import org.eclipse.xtext.common.types.JvmMember;
import org.eclipse.xtext.common.types.JvmOperation;
import org.eclipse.xtext.common.types.JvmTypeReference;
import org.eclipse.xtext.naming.QualifiedName;
import org.eclipse.xtext.xbase.XExpression;
import org.eclipse.xtext.xbase.compiler.output.ITreeAppendable;
import org.eclipse.xtext.xbase.jvmmodel.IJvmDeclaredTypeAcceptor;
import org.eclipse.xtext.xbase.jvmmodel.JvmTypeReferenceBuilder;
import org.eclipse.xtext.xbase.jvmmodel.JvmTypesBuilder;
import org.eclipse.xtext.xbase.lib.Extension;
import org.eclipse.xtext.xbase.lib.Procedures.Procedure1;

@SuppressWarnings("all")
public class AtomicGenerator {
  @Inject
  @Extension
  private JvmTypesBuilder jvmTypesBuilder;
  
  @Inject
  @Extension
  private Utils _utils;
  
  @Inject
  @Extension
  private NamingProvider _namingProvider;
  
  public void generateAtomicEventClasses(final Iterable<ModelElement> patterns, final IJvmDeclaredTypeAcceptor acceptor, final JvmTypeReferenceBuilder typeRefBuilder) {
    for (final ModelElement pattern : patterns) {
      {
        QualifiedName _classFqn = this._namingProvider.getClassFqn(pattern);
        JvmGenericType _class = this.jvmTypesBuilder.toClass(pattern, _classFqn);
        final Procedure1<JvmGenericType> _function = new Procedure1<JvmGenericType>() {
          @Override
          public void apply(final JvmGenericType it) {
            String _documentation = AtomicGenerator.this.jvmTypesBuilder.getDocumentation(pattern);
            AtomicGenerator.this.jvmTypesBuilder.setDocumentation(it, _documentation);
            if ((pattern instanceof QueryResultChangeEventPattern)) {
              EList<JvmTypeReference> _superTypes = it.getSuperTypes();
              JvmTypeReference _typeRef = typeRefBuilder.typeRef(ParameterizableViatraQueryPatternEventInstance.class);
              AtomicGenerator.this.jvmTypesBuilder.<JvmTypeReference>operator_add(_superTypes, _typeRef);
            } else {
              if ((pattern instanceof AtomicEventPattern)) {
                EList<JvmTypeReference> _superTypes_1 = it.getSuperTypes();
                JvmTypeReference _typeRef_1 = typeRefBuilder.typeRef(ParameterizableEventInstance.class);
                AtomicGenerator.this.jvmTypesBuilder.<JvmTypeReference>operator_add(_superTypes_1, _typeRef_1);
                TraitList _traits = ((AtomicEventPattern) pattern).getTraits();
                boolean _tripleNotEquals = (_traits != null);
                if (_tripleNotEquals) {
                  TraitList _traits_1 = ((AtomicEventPattern) pattern).getTraits();
                  EList<Trait> _traits_2 = _traits_1.getTraits();
                  boolean _isEmpty = _traits_2.isEmpty();
                  boolean _not = (!_isEmpty);
                  if (_not) {
                    TraitList _traits_3 = ((AtomicEventPattern) pattern).getTraits();
                    EList<Trait> _traits_4 = _traits_3.getTraits();
                    for (final Trait trait : _traits_4) {
                      EList<JvmTypeReference> _superTypes_2 = it.getSuperTypes();
                      QualifiedName _traitInterfaceFqn = AtomicGenerator.this._namingProvider.getTraitInterfaceFqn(trait);
                      String _string = _traitInterfaceFqn.toString();
                      JvmTypeReference _typeRef_2 = typeRefBuilder.typeRef(_string);
                      AtomicGenerator.this.jvmTypesBuilder.<JvmTypeReference>operator_add(_superTypes_2, _typeRef_2);
                    }
                  }
                }
              }
            }
            final TypedParameterList paramList = AtomicGenerator.this.getParamList(pattern);
            if ((paramList != null)) {
              EList<TypedParameter> _parameters = paramList.getParameters();
              for (final TypedParameter parameter : _parameters) {
                EList<JvmMember> _members = it.getMembers();
                String _name = parameter.getName();
                JvmTypeReference _type = parameter.getType();
                JvmField _field = AtomicGenerator.this.jvmTypesBuilder.toField(pattern, _name, _type);
                AtomicGenerator.this.jvmTypesBuilder.<JvmField>operator_add(_members, _field);
              }
            }
            if ((pattern instanceof AtomicEventPattern)) {
              final TraitList traitList = ((AtomicEventPattern) pattern).getTraits();
              if ((traitList != null)) {
                EList<Trait> _traits_5 = traitList.getTraits();
                for (final Trait trait_1 : _traits_5) {
                  TraitTypedParameterList _parameters_1 = trait_1.getParameters();
                  EList<TypedParameterWithDefaultValue> _parameters_2 = _parameters_1.getParameters();
                  for (final TypedParameterWithDefaultValue param : _parameters_2) {
                    {
                      final TypedParameter parameter_1 = param.getTypedParameter();
                      EList<JvmMember> _members_1 = it.getMembers();
                      String _name_1 = parameter_1.getName();
                      JvmTypeReference _type_1 = parameter_1.getType();
                      final Procedure1<JvmField> _function = new Procedure1<JvmField>() {
                        @Override
                        public void apply(final JvmField it) {
                          XExpression _value = param.getValue();
                          boolean _tripleNotEquals = (_value != null);
                          if (_tripleNotEquals) {
                            XExpression _value_1 = param.getValue();
                            AtomicGenerator.this.jvmTypesBuilder.setInitializer(it, _value_1);
                          }
                        }
                      };
                      JvmField _field_1 = AtomicGenerator.this.jvmTypesBuilder.toField(pattern, _name_1, _type_1, _function);
                      AtomicGenerator.this.jvmTypesBuilder.<JvmField>operator_add(_members_1, _field_1);
                    }
                  }
                }
              }
            }
            EList<JvmMember> _members_1 = it.getMembers();
            final Procedure1<JvmConstructor> _function = new Procedure1<JvmConstructor>() {
              @Override
              public void apply(final JvmConstructor it) {
                EList<JvmFormalParameter> _parameters = it.getParameters();
                JvmTypeReference _typeRef = typeRefBuilder.typeRef(EventSource.class);
                JvmFormalParameter _parameter = AtomicGenerator.this.jvmTypesBuilder.toParameter(pattern, "eventSource", _typeRef);
                AtomicGenerator.this.jvmTypesBuilder.<JvmFormalParameter>operator_add(_parameters, _parameter);
                final Procedure1<ITreeAppendable> _function = new Procedure1<ITreeAppendable>() {
                  @Override
                  public void apply(final ITreeAppendable it) {
                    StringConcatenation _builder = new StringConcatenation();
                    _builder.append("super(eventSource);");
                    ITreeAppendable _append = it.append(_builder);
                    StringConcatenation _builder_1 = new StringConcatenation();
                    _builder_1.newLine();
                    {
                      if ((paramList != null)) {
                        {
                          EList<TypedParameter> _parameters = paramList.getParameters();
                          for(final TypedParameter parameter : _parameters) {
                            _builder_1.append("getParameters().add(");
                            String _name = parameter.getName();
                            _builder_1.append(_name, "");
                            _builder_1.append(");");
                            _builder_1.newLineIfNotEmpty();
                          }
                        }
                      }
                    }
                    ITreeAppendable _append_1 = _append.append(_builder_1);
                    StringConcatenation _builder_2 = new StringConcatenation();
                    {
                      if ((pattern instanceof AtomicEventPattern)) {
                        {
                          TraitList _traits = ((AtomicEventPattern) pattern).getTraits();
                          boolean _tripleNotEquals = (_traits != null);
                          if (_tripleNotEquals) {
                            {
                              TraitList _traits_1 = ((AtomicEventPattern) pattern).getTraits();
                              EList<Trait> _traits_2 = _traits_1.getTraits();
                              for(final Trait trait : _traits_2) {
                                {
                                  TraitTypedParameterList _parameters_1 = trait.getParameters();
                                  EList<TypedParameterWithDefaultValue> _parameters_2 = _parameters_1.getParameters();
                                  for(final TypedParameterWithDefaultValue param : _parameters_2) {
                                    _builder_2.append("getParameters().add(");
                                    TypedParameter _typedParameter = param.getTypedParameter();
                                    String _name_1 = _typedParameter.getName();
                                    _builder_2.append(_name_1, "");
                                    _builder_2.append(");");
                                    _builder_2.newLineIfNotEmpty();
                                  }
                                }
                              }
                            }
                          }
                        }
                      }
                    }
                    _append_1.append(_builder_2);
                  }
                };
                AtomicGenerator.this.jvmTypesBuilder.setBody(it, _function);
              }
            };
            JvmConstructor _constructor = AtomicGenerator.this.jvmTypesBuilder.toConstructor(pattern, _function);
            AtomicGenerator.this.jvmTypesBuilder.<JvmConstructor>operator_add(_members_1, _constructor);
            int i = 0;
            if ((paramList != null)) {
              EList<TypedParameter> _parameters_3 = paramList.getParameters();
              for (final TypedParameter parameter_1 : _parameters_3) {
                {
                  EList<JvmMember> _members_2 = it.getMembers();
                  String _name_1 = parameter_1.getName();
                  JvmTypeReference _type_1 = parameter_1.getType();
                  JvmOperation _getter = AtomicGenerator.this.jvmTypesBuilder.toGetter(pattern, _name_1, _type_1);
                  AtomicGenerator.this.jvmTypesBuilder.<JvmOperation>operator_add(_members_2, _getter);
                  EList<JvmMember> _members_3 = it.getMembers();
                  String _name_2 = parameter_1.getName();
                  JvmTypeReference _type_2 = parameter_1.getType();
                  JvmOperation _advancedSetter = AtomicGenerator.this._utils.toAdvancedSetter(pattern, _name_2, _type_2, typeRefBuilder, i);
                  AtomicGenerator.this.jvmTypesBuilder.<JvmOperation>operator_add(_members_3, _advancedSetter);
                  i = (i + 1);
                }
              }
            }
            if ((pattern instanceof AtomicEventPattern)) {
              final TraitList traitList_1 = ((AtomicEventPattern) pattern).getTraits();
              if ((traitList_1 != null)) {
                EList<Trait> _traits_6 = traitList_1.getTraits();
                for (final Trait trait_2 : _traits_6) {
                  TraitTypedParameterList _parameters_4 = trait_2.getParameters();
                  EList<TypedParameterWithDefaultValue> _parameters_5 = _parameters_4.getParameters();
                  for (final TypedParameterWithDefaultValue param_1 : _parameters_5) {
                    {
                      final TypedParameter parameter_2 = param_1.getTypedParameter();
                      String _name_1 = parameter_2.getName();
                      JvmTypeReference _type_1 = parameter_2.getType();
                      final JvmOperation getter = AtomicGenerator.this.jvmTypesBuilder.toGetter(pattern, _name_1, _type_1);
                      AtomicGenerator.this._utils.addOverrideAnnotation(getter, parameter_2);
                      EList<JvmMember> _members_2 = it.getMembers();
                      AtomicGenerator.this.jvmTypesBuilder.<JvmOperation>operator_add(_members_2, getter);
                      String _name_2 = parameter_2.getName();
                      JvmTypeReference _type_2 = parameter_2.getType();
                      final JvmOperation setter = AtomicGenerator.this._utils.toAdvancedSetter(pattern, _name_2, _type_2, typeRefBuilder, i);
                      AtomicGenerator.this._utils.addOverrideAnnotation(setter, pattern);
                      EList<JvmMember> _members_3 = it.getMembers();
                      AtomicGenerator.this.jvmTypesBuilder.<JvmOperation>operator_add(_members_3, setter);
                      i = (i + 1);
                    }
                  }
                }
              }
            }
            EList<JvmMember> _members_2 = it.getMembers();
            JvmTypeReference _typeRef_3 = typeRefBuilder.typeRef("boolean");
            final Procedure1<JvmOperation> _function_1 = new Procedure1<JvmOperation>() {
              @Override
              public void apply(final JvmOperation it) {
                AtomicGenerator.this._utils.addOverrideAnnotation(it, pattern);
                XExpression _checkExpression = AtomicGenerator.this.getCheckExpression(pattern);
                boolean _tripleEquals = (_checkExpression == null);
                if (_tripleEquals) {
                  final Procedure1<ITreeAppendable> _function = new Procedure1<ITreeAppendable>() {
                    @Override
                    public void apply(final ITreeAppendable it) {
                      StringConcatenation _builder = new StringConcatenation();
                      _builder.append("return true;");
                      it.append(_builder);
                    }
                  };
                  AtomicGenerator.this.jvmTypesBuilder.setBody(it, _function);
                } else {
                  XExpression _checkExpression_1 = AtomicGenerator.this.getCheckExpression(pattern);
                  AtomicGenerator.this.jvmTypesBuilder.setBody(it, _checkExpression_1);
                }
              }
            };
            JvmOperation _method = AtomicGenerator.this.jvmTypesBuilder.toMethod(pattern, "evaluateCheckExpression", _typeRef_3, _function_1);
            AtomicGenerator.this.jvmTypesBuilder.<JvmOperation>operator_add(_members_2, _method);
          }
        };
        acceptor.<JvmGenericType>accept(_class, _function);
        FactoryManager _instance = FactoryManager.getInstance();
        QualifiedName _classFqn_1 = this._namingProvider.getClassFqn(pattern);
        _instance.add(_classFqn_1);
      }
    }
  }
  
  public void generateAtomicEventPatterns(final Iterable<ModelElement> patterns, final IJvmDeclaredTypeAcceptor acceptor, final JvmTypeReferenceBuilder typeRefBuilder) {
    for (final ModelElement pattern : patterns) {
      {
        QualifiedName _patternFqn = this._namingProvider.getPatternFqn(pattern);
        JvmGenericType _class = this.jvmTypesBuilder.toClass(pattern, _patternFqn);
        final Procedure1<JvmGenericType> _function = new Procedure1<JvmGenericType>() {
          @Override
          public void apply(final JvmGenericType it) {
            String _documentation = AtomicGenerator.this.jvmTypesBuilder.getDocumentation(pattern);
            AtomicGenerator.this.jvmTypesBuilder.setDocumentation(it, _documentation);
            EList<JvmTypeReference> _superTypes = it.getSuperTypes();
            JvmTypeReference _typeRef = typeRefBuilder.typeRef(AtomicEventPatternImpl.class);
            AtomicGenerator.this.jvmTypesBuilder.<JvmTypeReference>operator_add(_superTypes, _typeRef);
            final TypedParameterList paramList = AtomicGenerator.this.getParamList(pattern);
            if ((paramList != null)) {
              EList<TypedParameter> _parameters = paramList.getParameters();
              for (final TypedParameter parameter : _parameters) {
                EList<JvmMember> _members = it.getMembers();
                String _name = parameter.getName();
                JvmTypeReference _type = parameter.getType();
                JvmField _field = AtomicGenerator.this.jvmTypesBuilder.toField(pattern, _name, _type);
                AtomicGenerator.this.jvmTypesBuilder.<JvmField>operator_add(_members, _field);
              }
            }
            EList<JvmMember> _members_1 = it.getMembers();
            final Procedure1<JvmConstructor> _function = new Procedure1<JvmConstructor>() {
              @Override
              public void apply(final JvmConstructor it) {
                final Procedure1<ITreeAppendable> _function = new Procedure1<ITreeAppendable>() {
                  @Override
                  public void apply(final ITreeAppendable it) {
                    StringConcatenation _builder = new StringConcatenation();
                    _builder.append("super();");
                    _builder.newLine();
                    _builder.append("setType(");
                    ITreeAppendable _append = it.append(_builder);
                    StringConcatenation _builder_1 = new StringConcatenation();
                    QualifiedName _classFqn = AtomicGenerator.this._namingProvider.getClassFqn(pattern);
                    ITreeAppendable _referClass = AtomicGenerator.this._utils.referClass(it, typeRefBuilder, _classFqn, pattern);
                    _builder_1.append(_referClass, "");
                    ITreeAppendable _append_1 = _append.append(_builder_1);
                    StringConcatenation _builder_2 = new StringConcatenation();
                    _builder_2.append(".class.getCanonicalName());");
                    _builder_2.newLine();
                    ITreeAppendable appendable = _append_1.append(_builder_2);
                    if ((paramList != null)) {
                      EList<TypedParameter> _parameters = paramList.getParameters();
                      for (final TypedParameter parameter : _parameters) {
                        StringConcatenation _builder_3 = new StringConcatenation();
                        _builder_3.append("getParameterNames().add(\"");
                        String _name = parameter.getName();
                        _builder_3.append(_name, "");
                        _builder_3.append("\");");
                        _builder_3.newLineIfNotEmpty();
                        ITreeAppendable _append_2 = appendable.append(_builder_3);
                        appendable = _append_2;
                      }
                    }
                    if ((pattern instanceof AtomicEventPattern)) {
                      TraitList _traits = ((AtomicEventPattern)pattern).getTraits();
                      boolean _tripleNotEquals = (_traits != null);
                      if (_tripleNotEquals) {
                        TraitList _traits_1 = ((AtomicEventPattern)pattern).getTraits();
                        EList<Trait> _traits_2 = _traits_1.getTraits();
                        for (final Trait trait : _traits_2) {
                          TraitTypedParameterList _parameters_1 = trait.getParameters();
                          boolean _tripleNotEquals_1 = (_parameters_1 != null);
                          if (_tripleNotEquals_1) {
                            TraitTypedParameterList _parameters_2 = trait.getParameters();
                            EList<TypedParameterWithDefaultValue> _parameters_3 = _parameters_2.getParameters();
                            for (final TypedParameterWithDefaultValue traitParameter : _parameters_3) {
                              TypedParameter _typedParameter = traitParameter.getTypedParameter();
                              boolean _tripleNotEquals_2 = (_typedParameter != null);
                              if (_tripleNotEquals_2) {
                                StringConcatenation _builder_4 = new StringConcatenation();
                                _builder_4.append("getParameterNames().add(\"");
                                TypedParameter _typedParameter_1 = traitParameter.getTypedParameter();
                                String _name_1 = _typedParameter_1.getName();
                                _builder_4.append(_name_1, "");
                                _builder_4.append("\");");
                                _builder_4.newLineIfNotEmpty();
                                ITreeAppendable _append_3 = appendable.append(_builder_4);
                                appendable = _append_3;
                              }
                            }
                          }
                        }
                      }
                    }
                    StringConcatenation _builder_5 = new StringConcatenation();
                    _builder_5.append("setId(\"");
                    QualifiedName _patternFqn = AtomicGenerator.this._namingProvider.getPatternFqn(pattern);
                    String _string = _patternFqn.toString();
                    String _lowerCase = _string.toLowerCase();
                    _builder_5.append(_lowerCase, "");
                    _builder_5.append("\");");
                    ITreeAppendable _append_4 = appendable.append(_builder_5);
                    appendable = _append_4;
                  }
                };
                AtomicGenerator.this.jvmTypesBuilder.setBody(it, _function);
              }
            };
            JvmConstructor _constructor = AtomicGenerator.this.jvmTypesBuilder.toConstructor(pattern, _function);
            AtomicGenerator.this.jvmTypesBuilder.<JvmConstructor>operator_add(_members_1, _constructor);
          }
        };
        acceptor.<JvmGenericType>accept(_class, _function);
        FactoryManager _instance = FactoryManager.getInstance();
        QualifiedName _patternFqn_1 = this._namingProvider.getPatternFqn(pattern);
        _instance.add(_patternFqn_1);
      }
    }
  }
  
  private XExpression getCheckExpression(final ModelElement element) {
    boolean _matched = false;
    if (!_matched) {
      if (element instanceof AtomicEventPattern) {
        _matched=true;
        return ((AtomicEventPattern) element).getCheckExpression();
      }
    }
    return null;
  }
  
  private TypedParameterList getParamList(final ModelElement modelElement) {
    if ((modelElement instanceof AtomicEventPattern)) {
      return ((AtomicEventPattern) modelElement).getParameters();
    } else {
      if ((modelElement instanceof QueryResultChangeEventPattern)) {
        return ((QueryResultChangeEventPattern) modelElement).getParameters();
      }
    }
    return null;
  }
}
