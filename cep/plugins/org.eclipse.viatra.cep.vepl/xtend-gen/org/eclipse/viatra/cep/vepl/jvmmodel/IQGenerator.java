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

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import com.google.inject.Inject;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.viatra.cep.core.streams.EventStream;
import org.eclipse.viatra.cep.vepl.jvmmodel.NamingProvider;
import org.eclipse.viatra.cep.vepl.jvmmodel.Utils;
import org.eclipse.viatra.cep.vepl.vepl.EventModel;
import org.eclipse.viatra.cep.vepl.vepl.Import;
import org.eclipse.viatra.cep.vepl.vepl.ParametrizedQueryReference;
import org.eclipse.viatra.cep.vepl.vepl.PatternCallParameter;
import org.eclipse.viatra.cep.vepl.vepl.PatternCallParameterList;
import org.eclipse.viatra.cep.vepl.vepl.QueryImport;
import org.eclipse.viatra.cep.vepl.vepl.QueryResultChangeEventPattern;
import org.eclipse.viatra.cep.vepl.vepl.QueryResultChangeType;
import org.eclipse.viatra.cep.vepl.vepl.TypedParameter;
import org.eclipse.viatra.cep.vepl.vepl.TypedParameterList;
import org.eclipse.viatra.query.patternlanguage.patternLanguage.Pattern;
import org.eclipse.viatra.query.runtime.api.IMatchProcessor;
import org.eclipse.viatra.query.runtime.emf.EMFScope;
import org.eclipse.viatra.query.runtime.exception.ViatraQueryException;
import org.eclipse.viatra.transformation.evm.specific.Lifecycles;
import org.eclipse.viatra.transformation.evm.specific.crud.CRUDActivationStateEnum;
import org.eclipse.viatra.transformation.runtime.emf.rules.eventdriven.EventDrivenTransformationRule;
import org.eclipse.viatra.transformation.runtime.emf.rules.eventdriven.EventDrivenTransformationRuleFactory;
import org.eclipse.viatra.transformation.runtime.emf.transformation.eventdriven.EventDrivenTransformation;
import org.eclipse.viatra.transformation.runtime.emf.transformation.eventdriven.InconsistentEventSemanticsException;
import org.eclipse.xtend2.lib.StringConcatenation;
import org.eclipse.xtext.common.types.JvmConstructor;
import org.eclipse.xtext.common.types.JvmField;
import org.eclipse.xtext.common.types.JvmFormalParameter;
import org.eclipse.xtext.common.types.JvmGenericType;
import org.eclipse.xtext.common.types.JvmMember;
import org.eclipse.xtext.common.types.JvmOperation;
import org.eclipse.xtext.common.types.JvmTypeReference;
import org.eclipse.xtext.common.types.JvmVisibility;
import org.eclipse.xtext.naming.QualifiedName;
import org.eclipse.xtext.xbase.compiler.output.ITreeAppendable;
import org.eclipse.xtext.xbase.jvmmodel.IJvmDeclaredTypeAcceptor;
import org.eclipse.xtext.xbase.jvmmodel.JvmTypeReferenceBuilder;
import org.eclipse.xtext.xbase.jvmmodel.JvmTypesBuilder;
import org.eclipse.xtext.xbase.lib.Extension;
import org.eclipse.xtext.xbase.lib.Functions.Function1;
import org.eclipse.xtext.xbase.lib.IterableExtensions;
import org.eclipse.xtext.xbase.lib.Procedures.Procedure1;
import org.eclipse.xtext.xbase.lib.StringExtensions;

@SuppressWarnings({ "restriction", "discouraged" })
public class IQGenerator {
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
  
  public void generateQueryEngine2CepEngine(final List<QueryResultChangeEventPattern> patterns, final EventModel model, final IJvmDeclaredTypeAcceptor acceptor, final JvmTypeReferenceBuilder typeRefBuilder) {
    this.typeRefBuilder = typeRefBuilder;
    EList<Import> _imports = model.getImports();
    final Function1<Import, Boolean> _function = new Function1<Import, Boolean>() {
      @Override
      public Boolean apply(final Import e) {
        return Boolean.valueOf((e instanceof QueryImport));
      }
    };
    Iterable<Import> _filter = IterableExtensions.<Import>filter(_imports, _function);
    int _size = IterableExtensions.size(_filter);
    boolean _equals = (_size == 0);
    if (_equals) {
      return;
    }
    QueryResultChangeEventPattern _head = IterableExtensions.<QueryResultChangeEventPattern>head(patterns);
    final QualifiedName fqn = this._namingProvider.getQueryEngine2CepEngineClassFqn(_head);
    JvmGenericType _class = this.jvmTypesBuilder.toClass(model, fqn);
    final Procedure1<JvmGenericType> _function_1 = new Procedure1<JvmGenericType>() {
      @Override
      public void apply(final JvmGenericType it) {
        String _documentation = IQGenerator.this.jvmTypesBuilder.getDocumentation(model);
        IQGenerator.this.jvmTypesBuilder.setDocumentation(it, _documentation);
        EList<JvmMember> _members = it.getMembers();
        JvmTypeReference _typeRef = typeRefBuilder.typeRef(EventStream.class);
        JvmField _field = IQGenerator.this.jvmTypesBuilder.toField(model, "eventStream", _typeRef);
        IQGenerator.this.jvmTypesBuilder.<JvmField>operator_add(_members, _field);
        EList<JvmMember> _members_1 = it.getMembers();
        JvmTypeReference _typeRef_1 = typeRefBuilder.typeRef(ResourceSet.class);
        JvmField _field_1 = IQGenerator.this.jvmTypesBuilder.toField(model, "resourceSet", _typeRef_1);
        IQGenerator.this.jvmTypesBuilder.<JvmField>operator_add(_members_1, _field_1);
        EList<JvmMember> _members_2 = it.getMembers();
        JvmTypeReference _typeRef_2 = typeRefBuilder.typeRef(EventDrivenTransformation.class);
        JvmField _field_2 = IQGenerator.this.jvmTypesBuilder.toField(model, "transformation", _typeRef_2);
        IQGenerator.this.jvmTypesBuilder.<JvmField>operator_add(_members_2, _field_2);
        final Procedure1<JvmConstructor> _function = new Procedure1<JvmConstructor>() {
          @Override
          public void apply(final JvmConstructor it) {
            EList<JvmFormalParameter> _parameters = it.getParameters();
            JvmTypeReference _typeRef = typeRefBuilder.typeRef(ResourceSet.class);
            JvmFormalParameter _parameter = IQGenerator.this.jvmTypesBuilder.toParameter(model, "resourceSet", _typeRef);
            IQGenerator.this.jvmTypesBuilder.<JvmFormalParameter>operator_add(_parameters, _parameter);
            EList<JvmFormalParameter> _parameters_1 = it.getParameters();
            JvmTypeReference _typeRef_1 = typeRefBuilder.typeRef(EventStream.class);
            JvmFormalParameter _parameter_1 = IQGenerator.this.jvmTypesBuilder.toParameter(model, "eventStream", _typeRef_1);
            IQGenerator.this.jvmTypesBuilder.<JvmFormalParameter>operator_add(_parameters_1, _parameter_1);
            final Procedure1<ITreeAppendable> _function = new Procedure1<ITreeAppendable>() {
              @Override
              public void apply(final ITreeAppendable it) {
                StringConcatenation _builder = new StringConcatenation();
                _builder.append("this.resourceSet = resourceSet;");
                _builder.newLine();
                _builder.append("this.eventStream = eventStream;");
                _builder.newLine();
                _builder.append("registerRules();");
                it.append(_builder);
              }
            };
            IQGenerator.this.jvmTypesBuilder.setBody(it, _function);
          }
        };
        JvmConstructor constructor = IQGenerator.this.jvmTypesBuilder.toConstructor(model, _function);
        constructor.setVisibility(JvmVisibility.PRIVATE);
        EList<JvmMember> _members_3 = it.getMembers();
        IQGenerator.this.jvmTypesBuilder.<JvmConstructor>operator_add(_members_3, constructor);
        final Multimap<Pattern, QueryResultChangeEventPattern> groupedPatterns = IQGenerator.this.groupEventPatternsByIqPatternRef(patterns);
        String _string = fqn.toString();
        JvmTypeReference _typeRef_3 = typeRefBuilder.typeRef(_string);
        final Procedure1<JvmOperation> _function_1 = new Procedure1<JvmOperation>() {
          @Override
          public void apply(final JvmOperation it) {
            EList<JvmFormalParameter> _parameters = it.getParameters();
            JvmTypeReference _typeRef = typeRefBuilder.typeRef(ResourceSet.class);
            JvmFormalParameter _parameter = IQGenerator.this.jvmTypesBuilder.toParameter(model, "resourceSet", _typeRef);
            IQGenerator.this.jvmTypesBuilder.<JvmFormalParameter>operator_add(_parameters, _parameter);
            EList<JvmFormalParameter> _parameters_1 = it.getParameters();
            JvmTypeReference _typeRef_1 = typeRefBuilder.typeRef(EventStream.class);
            JvmFormalParameter _parameter_1 = IQGenerator.this.jvmTypesBuilder.toParameter(model, "eventStream", _typeRef_1);
            IQGenerator.this.jvmTypesBuilder.<JvmFormalParameter>operator_add(_parameters_1, _parameter_1);
            final Procedure1<ITreeAppendable> _function = new Procedure1<ITreeAppendable>() {
              @Override
              public void apply(final ITreeAppendable it) {
                StringConcatenation _builder = new StringConcatenation();
                _builder.append("return new QueryEngine2ViatraCep(resourceSet, eventStream);");
                it.append(_builder);
              }
            };
            IQGenerator.this.jvmTypesBuilder.setBody(it, _function);
          }
        };
        JvmOperation registerMappingMethod = IQGenerator.this.jvmTypesBuilder.toMethod(model, "register", _typeRef_3, _function_1);
        registerMappingMethod.setVisibility(JvmVisibility.PUBLIC);
        registerMappingMethod.setStatic(true);
        EList<JvmMember> _members_4 = it.getMembers();
        IQGenerator.this.jvmTypesBuilder.<JvmOperation>operator_add(_members_4, registerMappingMethod);
        EList<JvmMember> _members_5 = it.getMembers();
        JvmTypeReference _typeRef_4 = typeRefBuilder.typeRef("org.eclipse.viatra.transformation.runtime.emf.rules.EventDrivenTransformationRuleGroup");
        final Procedure1<JvmOperation> _function_2 = new Procedure1<JvmOperation>() {
          @Override
          public void apply(final JvmOperation it) {
            final Procedure1<ITreeAppendable> _function = new Procedure1<ITreeAppendable>() {
              @Override
              public void apply(final ITreeAppendable it) {
                StringConcatenation _builder = new StringConcatenation();
                _builder.append("EventDrivenTransformationRuleGroup ruleGroup = new EventDrivenTransformationRuleGroup(");
                _builder.newLine();
                {
                  Set<Pattern> _keySet = groupedPatterns.keySet();
                  boolean _hasElements = false;
                  for(final Pattern p : _keySet) {
                    if (!_hasElements) {
                      _hasElements = true;
                    } else {
                      _builder.appendImmediate(", ", "    ");
                    }
                    _builder.append("    ");
                    String _mappingMethodName = IQGenerator.this.getMappingMethodName(p);
                    _builder.append(_mappingMethodName, "    ");
                    _builder.append("()");
                    _builder.newLineIfNotEmpty();
                  }
                  if (_hasElements) {
                    _builder.append(");", "    ");
                  }
                }
                _builder.newLine();
                _builder.append("return ruleGroup;");
                it.append(_builder);
              }
            };
            IQGenerator.this.jvmTypesBuilder.setBody(it, _function);
          }
        };
        JvmOperation _method = IQGenerator.this.jvmTypesBuilder.toMethod(model, "getRules", _typeRef_4, _function_2);
        IQGenerator.this.jvmTypesBuilder.<JvmOperation>operator_add(_members_5, _method);
        JvmTypeReference _typeRef_5 = typeRefBuilder.typeRef(void.class);
        final Procedure1<JvmOperation> _function_3 = new Procedure1<JvmOperation>() {
          @Override
          public void apply(final JvmOperation it) {
            final Procedure1<ITreeAppendable> _function = new Procedure1<ITreeAppendable>() {
              @Override
              public void apply(final ITreeAppendable it) {
                StringConcatenation _builder = new StringConcatenation();
                _builder.append("try {");
                _builder.newLine();
                _builder.append("    ");
                _builder.append("transformation = EventDrivenTransformation.forScope(new ");
                it.append(_builder);
                StringConcatenation _builder_1 = new StringConcatenation();
                ITreeAppendable _referClass = IQGenerator.this._utils.referClass(it, typeRefBuilder, model, EMFScope.class);
                _builder_1.append(_referClass, "");
                it.append(_builder_1);
                StringConcatenation _builder_2 = new StringConcatenation();
                _builder_2.append("(resourceSet)).addRules(getRules()).build();");
                it.append(_builder_2);
                it.newLine();
                StringConcatenation _builder_3 = new StringConcatenation();
                _builder_3.append("} catch (ViatraQueryException e) {");
                _builder_3.newLine();
                _builder_3.append("    ");
                _builder_3.append("e.printStackTrace();");
                _builder_3.newLine();
                _builder_3.append("}");
                it.append(_builder_3);
              }
            };
            IQGenerator.this.jvmTypesBuilder.setBody(it, _function);
          }
        };
        JvmOperation registerTransformationMethod = IQGenerator.this.jvmTypesBuilder.toMethod(model, "registerRules", _typeRef_5, _function_3);
        registerTransformationMethod.setVisibility(JvmVisibility.PRIVATE);
        EList<JvmMember> _members_6 = it.getMembers();
        IQGenerator.this.jvmTypesBuilder.<JvmOperation>operator_add(_members_6, registerTransformationMethod);
        EList<Import> _imports = model.getImports();
        final Function1<Import, Boolean> _function_4 = new Function1<Import, Boolean>() {
          @Override
          public Boolean apply(final Import e) {
            return Boolean.valueOf((e instanceof QueryImport));
          }
        };
        Iterable<Import> _filter = IterableExtensions.<Import>filter(_imports, _function_4);
        Import _head = IterableExtensions.<Import>head(_filter);
        String _importedNamespace = _head.getImportedNamespace();
        final String patternsNamespace = _importedNamespace.replace("*", "");
        Set<Pattern> _keySet = groupedPatterns.keySet();
        for (final Pattern p : _keySet) {
          if ((p != null)) {
            String _name = p.getName();
            String _firstUpper = StringExtensions.toFirstUpper(_name);
            String _plus = (patternsNamespace + _firstUpper);
            final String matcher = (_plus + "Matcher");
            String _name_1 = p.getName();
            String _firstUpper_1 = StringExtensions.toFirstUpper(_name_1);
            String _plus_1 = (patternsNamespace + _firstUpper_1);
            final String match = (_plus_1 + "Match");
            EList<JvmMember> _members_7 = it.getMembers();
            String _mappingMethodName = IQGenerator.this.getMappingMethodName(p);
            JvmTypeReference _typeRef_6 = typeRefBuilder.typeRef(match);
            JvmTypeReference _typeRef_7 = typeRefBuilder.typeRef(matcher);
            JvmTypeReference _typeRef_8 = typeRefBuilder.typeRef(EventDrivenTransformationRule.class, _typeRef_6, _typeRef_7);
            final Procedure1<JvmOperation> _function_5 = new Procedure1<JvmOperation>() {
              @Override
              public void apply(final JvmOperation it) {
                final Procedure1<ITreeAppendable> _function = new Procedure1<ITreeAppendable>() {
                  @Override
                  public void apply(final ITreeAppendable it) {
                    StringConcatenation _builder = new StringConcatenation();
                    _builder.append("try{");
                    ITreeAppendable _append = it.append(_builder);
                    _append.increaseIndentation();
                    it.newLine();
                    StringConcatenation _builder_1 = new StringConcatenation();
                    JvmTypeReference _typeRef = typeRefBuilder.typeRef(match);
                    JvmTypeReference _typeRef_1 = typeRefBuilder.typeRef(matcher);
                    ITreeAppendable _referClass = IQGenerator.this._utils.referClass(it, typeRefBuilder, p, EventDrivenTransformationRuleFactory.EventDrivenTransformationRuleBuilder.class, _typeRef, _typeRef_1);
                    _builder_1.append(_referClass, "");
                    it.append(_builder_1);
                    StringConcatenation _builder_2 = new StringConcatenation();
                    _builder_2.append(" ");
                    _builder_2.append("builder = new ");
                    it.append(_builder_2);
                    StringConcatenation _builder_3 = new StringConcatenation();
                    ITreeAppendable _referClass_1 = IQGenerator.this._utils.referClass(it, typeRefBuilder, p, EventDrivenTransformationRuleFactory.class);
                    _builder_3.append(_referClass_1, "");
                    it.append(_builder_3);
                    StringConcatenation _builder_4 = new StringConcatenation();
                    _builder_4.append("().createRule();");
                    it.append(_builder_4);
                    it.newLine();
                    StringConcatenation _builder_5 = new StringConcatenation();
                    _builder_5.append("builder.addLifeCycle(");
                    it.append(_builder_5);
                    StringConcatenation _builder_6 = new StringConcatenation();
                    ITreeAppendable _referClass_2 = IQGenerator.this._utils.referClass(it, typeRefBuilder, p, Lifecycles.class);
                    _builder_6.append(_referClass_2, "");
                    it.append(_builder_6);
                    StringConcatenation _builder_7 = new StringConcatenation();
                    _builder_7.append(".getDefault(false, true));");
                    _builder_7.newLine();
                    it.append(_builder_7);
                    StringConcatenation _builder_8 = new StringConcatenation();
                    _builder_8.append("builder.precondition(");
                    ITreeAppendable _append_1 = it.append(_builder_8);
                    StringConcatenation _builder_9 = new StringConcatenation();
                    ITreeAppendable _referClass_3 = IQGenerator.this._utils.referClass(it, typeRefBuilder, matcher, p);
                    _builder_9.append(_referClass_3, "");
                    ITreeAppendable _append_2 = _append_1.append(_builder_9);
                    StringConcatenation _builder_10 = new StringConcatenation();
                    _builder_10.append(".querySpecification());");
                    _builder_10.newLine();
                    _append_2.append(_builder_10);
                    Collection<QueryResultChangeEventPattern> _get = groupedPatterns.get(p);
                    List<QueryResultChangeEventPattern> _list = IterableExtensions.<QueryResultChangeEventPattern>toList(_get);
                    Iterable<QueryResultChangeEventPattern> _patternsRequiringAppearAction = IQGenerator.this.patternsRequiringAppearAction(_list);
                    final List<QueryResultChangeEventPattern> appearActionPatterns = IterableExtensions.<QueryResultChangeEventPattern>toList(_patternsRequiringAppearAction);
                    Collection<QueryResultChangeEventPattern> _get_1 = groupedPatterns.get(p);
                    List<QueryResultChangeEventPattern> _list_1 = IterableExtensions.<QueryResultChangeEventPattern>toList(_get_1);
                    Iterable<QueryResultChangeEventPattern> _patternsRequiringDisappearAction = IQGenerator.this.patternsRequiringDisappearAction(_list_1);
                    final List<QueryResultChangeEventPattern> disappearActionPatterns = IterableExtensions.<QueryResultChangeEventPattern>toList(_patternsRequiringDisappearAction);
                    int counter = 0;
                    for (final QueryResultChangeEventPattern eventPattern : appearActionPatterns) {
                      {
                        IQGenerator.this.generateAction(QueryResultChangeType.FOUND, it, typeRefBuilder, eventPattern, match, p, counter);
                        counter = (counter + 1);
                      }
                    }
                    boolean _isEmpty = appearActionPatterns.isEmpty();
                    if (_isEmpty) {
                      final QueryResultChangeEventPattern eventPattern_1 = IterableExtensions.<QueryResultChangeEventPattern>head(disappearActionPatterns);
                      IQGenerator.this.generateAction(QueryResultChangeType.FOUND, it, typeRefBuilder, eventPattern_1, match, p, counter, true);
                    }
                    counter = 0;
                    for (final QueryResultChangeEventPattern eventPattern_2 : disappearActionPatterns) {
                      {
                        IQGenerator.this.generateAction(QueryResultChangeType.LOST, it, typeRefBuilder, eventPattern_2, match, p, counter);
                        counter = (counter + 1);
                      }
                    }
                    boolean _isEmpty_1 = disappearActionPatterns.isEmpty();
                    if (_isEmpty_1) {
                      final QueryResultChangeEventPattern eventPattern_3 = IterableExtensions.<QueryResultChangeEventPattern>head(appearActionPatterns);
                      IQGenerator.this.generateAction(QueryResultChangeType.LOST, it, typeRefBuilder, eventPattern_3, match, p, counter, true);
                    }
                    it.newLine();
                    StringConcatenation _builder_11 = new StringConcatenation();
                    _builder_11.append("return builder.build();");
                    ITreeAppendable _append_3 = it.append(_builder_11);
                    _append_3.decreaseIndentation();
                    it.newLine();
                    StringConcatenation _builder_12 = new StringConcatenation();
                    _builder_12.append("} catch (");
                    ITreeAppendable _append_4 = it.append(_builder_12);
                    StringConcatenation _builder_13 = new StringConcatenation();
                    ITreeAppendable _referClass_4 = IQGenerator.this._utils.referClass(it, typeRefBuilder, p, ViatraQueryException.class);
                    _builder_13.append(_referClass_4, "");
                    _builder_13.append(" e) {");
                    ITreeAppendable _append_5 = _append_4.append(_builder_13);
                    _append_5.increaseIndentation();
                    it.newLine();
                    StringConcatenation _builder_14 = new StringConcatenation();
                    _builder_14.append("e.printStackTrace();");
                    ITreeAppendable _append_6 = it.append(_builder_14);
                    _append_6.decreaseIndentation();
                    it.newLine();
                    StringConcatenation _builder_15 = new StringConcatenation();
                    _builder_15.append("} catch (");
                    ITreeAppendable _append_7 = it.append(_builder_15);
                    StringConcatenation _builder_16 = new StringConcatenation();
                    ITreeAppendable _referClass_5 = IQGenerator.this._utils.referClass(it, typeRefBuilder, p, InconsistentEventSemanticsException.class);
                    _builder_16.append(_referClass_5, "");
                    ITreeAppendable _append_8 = _append_7.append(_builder_16);
                    StringConcatenation _builder_17 = new StringConcatenation();
                    _builder_17.append(" ");
                    _builder_17.append("e) {");
                    ITreeAppendable _append_9 = _append_8.append(_builder_17);
                    _append_9.increaseIndentation();
                    it.newLine();
                    StringConcatenation _builder_18 = new StringConcatenation();
                    _builder_18.append("e.printStackTrace();");
                    ITreeAppendable _append_10 = it.append(_builder_18);
                    _append_10.decreaseIndentation();
                    it.newLine();
                    StringConcatenation _builder_19 = new StringConcatenation();
                    _builder_19.append("}");
                    it.append(_builder_19);
                    it.newLine();
                    StringConcatenation _builder_20 = new StringConcatenation();
                    _builder_20.append("return null;");
                    it.append(_builder_20);
                  }
                };
                IQGenerator.this.jvmTypesBuilder.setBody(it, _function);
              }
            };
            JvmOperation _method_1 = IQGenerator.this.jvmTypesBuilder.toMethod(model, _mappingMethodName, _typeRef_8, _function_5);
            IQGenerator.this.jvmTypesBuilder.<JvmOperation>operator_add(_members_7, _method_1);
          }
        }
        JvmTypeReference _typeRef_9 = typeRefBuilder.typeRef("void");
        final Procedure1<JvmOperation> _function_6 = new Procedure1<JvmOperation>() {
          @Override
          public void apply(final JvmOperation it) {
            final Procedure1<ITreeAppendable> _function = new Procedure1<ITreeAppendable>() {
              @Override
              public void apply(final ITreeAppendable it) {
                StringConcatenation _builder = new StringConcatenation();
                _builder.append("this.transformation = null;");
                it.append(_builder);
              }
            };
            IQGenerator.this.jvmTypesBuilder.setBody(it, _function);
          }
        };
        JvmOperation disposeMethod = IQGenerator.this.jvmTypesBuilder.toMethod(model, "dispose", _typeRef_9, _function_6);
        EList<JvmMember> _members_8 = it.getMembers();
        IQGenerator.this.jvmTypesBuilder.<JvmOperation>operator_add(_members_8, disposeMethod);
      }
    };
    acceptor.<JvmGenericType>accept(_class, _function_1);
  }
  
  public boolean requiresAppearAction(final QueryResultChangeEventPattern pattern) {
    final QueryResultChangeType changeType = pattern.getResultChangeType();
    boolean _or = false;
    if ((changeType == null)) {
      _or = true;
    } else {
      boolean _equals = changeType.equals(QueryResultChangeType.FOUND);
      _or = _equals;
    }
    return _or;
  }
  
  public Iterable<QueryResultChangeEventPattern> patternsRequiringAppearAction(final List<QueryResultChangeEventPattern> patterns) {
    final Function1<QueryResultChangeEventPattern, Boolean> _function = new Function1<QueryResultChangeEventPattern, Boolean>() {
      @Override
      public Boolean apply(final QueryResultChangeEventPattern p) {
        return Boolean.valueOf(IQGenerator.this.requiresAppearAction(p));
      }
    };
    return IterableExtensions.<QueryResultChangeEventPattern>filter(patterns, _function);
  }
  
  public boolean requiresDisappearAction(final QueryResultChangeEventPattern pattern) {
    final QueryResultChangeType changeType = pattern.getResultChangeType();
    return changeType.equals(QueryResultChangeType.LOST);
  }
  
  public Iterable<QueryResultChangeEventPattern> patternsRequiringDisappearAction(final List<QueryResultChangeEventPattern> patterns) {
    final Function1<QueryResultChangeEventPattern, Boolean> _function = new Function1<QueryResultChangeEventPattern, Boolean>() {
      @Override
      public Boolean apply(final QueryResultChangeEventPattern p) {
        return Boolean.valueOf(IQGenerator.this.requiresDisappearAction(p));
      }
    };
    return IterableExtensions.<QueryResultChangeEventPattern>filter(patterns, _function);
  }
  
  private ITreeAppendable generateAction(final QueryResultChangeType changeType, final ITreeAppendable ita, final JvmTypeReferenceBuilder typeRefBuilder, final QueryResultChangeEventPattern eventPattern, final String match, final Pattern p, final int counter) {
    return this.generateAction(changeType, ita, typeRefBuilder, eventPattern, match, p, counter, false);
  }
  
  private ITreeAppendable generateAction(final QueryResultChangeType changeType, final ITreeAppendable ita, final JvmTypeReferenceBuilder typeRefBuilder, final QueryResultChangeEventPattern eventPattern, final String match, final Pattern p, final int counter, final boolean empty) {
    ITreeAppendable _xblockexpression = null;
    {
      ita.newLine();
      StringConcatenation _builder = new StringConcatenation();
      JvmTypeReference _typeRef = typeRefBuilder.typeRef(match);
      ITreeAppendable _referClass = this._utils.referClass(ita, typeRefBuilder, eventPattern, IMatchProcessor.class, _typeRef);
      _builder.append(_referClass, "");
      _builder.append(" ");
      String _actionName = this.getActionName(changeType);
      _builder.append(_actionName, "");
      _builder.append("_");
      _builder.append(counter, "");
      ITreeAppendable _append = ita.append(_builder);
      StringConcatenation _builder_1 = new StringConcatenation();
      _builder_1.append(" ");
      _builder_1.append("= new ");
      ITreeAppendable _append_1 = _append.append(_builder_1);
      StringConcatenation _builder_2 = new StringConcatenation();
      JvmTypeReference _typeRef_1 = typeRefBuilder.typeRef(match);
      ITreeAppendable _referClass_1 = this._utils.referClass(ita, typeRefBuilder, eventPattern, IMatchProcessor.class, _typeRef_1);
      _builder_2.append(_referClass_1, "");
      _builder_2.append("() {");
      ITreeAppendable _append_2 = _append_1.append(_builder_2);
      _append_2.increaseIndentation();
      ita.newLine();
      StringConcatenation _builder_3 = new StringConcatenation();
      _builder_3.append("public void process(final ");
      ITreeAppendable _append_3 = ita.append(_builder_3);
      StringConcatenation _builder_4 = new StringConcatenation();
      ITreeAppendable _referClass_2 = this._utils.referClass(ita, typeRefBuilder, match, p);
      _builder_4.append(_referClass_2, "");
      ITreeAppendable _append_4 = _append_3.append(_builder_4);
      StringConcatenation _builder_5 = new StringConcatenation();
      _builder_5.append(" ");
      _builder_5.append("matchedPattern) {");
      _append_4.append(_builder_5);
      if ((!empty)) {
        ita.increaseIndentation();
        ita.newLine();
        StringConcatenation _builder_6 = new StringConcatenation();
        QualifiedName _classFqn = this._namingProvider.getClassFqn(eventPattern);
        ITreeAppendable _referClass_3 = this._utils.referClass(ita, typeRefBuilder, _classFqn, p);
        _builder_6.append(_referClass_3, "");
        ITreeAppendable _append_5 = ita.append(_builder_6);
        StringConcatenation _builder_7 = new StringConcatenation();
        _builder_7.append(" ");
        _builder_7.append("event = new ");
        ITreeAppendable _append_6 = _append_5.append(_builder_7);
        StringConcatenation _builder_8 = new StringConcatenation();
        QualifiedName _classFqn_1 = this._namingProvider.getClassFqn(eventPattern);
        ITreeAppendable _referClass_4 = this._utils.referClass(ita, typeRefBuilder, _classFqn_1, p);
        _builder_8.append(_referClass_4, "");
        ITreeAppendable _append_7 = _append_6.append(_builder_8);
        StringConcatenation _builder_9 = new StringConcatenation();
        _builder_9.append("(null);");
        _append_7.append(_builder_9);
        StringConcatenation _builder_10 = new StringConcatenation();
        this.getParameterMapping(ita, eventPattern);
        ita.append(_builder_10);
        ita.newLine();
        StringConcatenation _builder_11 = new StringConcatenation();
        _builder_11.append("event.setQueryMatch(matchedPattern);");
        ita.append(_builder_11);
        ita.newLine();
        StringConcatenation _builder_12 = new StringConcatenation();
        _builder_12.append("eventStream.push(event);");
        ITreeAppendable _append_8 = ita.append(_builder_12);
        _append_8.decreaseIndentation();
      }
      ita.newLine();
      StringConcatenation _builder_13 = new StringConcatenation();
      _builder_13.append("}");
      ITreeAppendable _append_9 = ita.append(_builder_13);
      _append_9.decreaseIndentation();
      ita.newLine();
      StringConcatenation _builder_14 = new StringConcatenation();
      _builder_14.append("};");
      ita.append(_builder_14);
      ita.newLine();
      StringConcatenation _builder_15 = new StringConcatenation();
      _builder_15.append("builder.action(");
      ITreeAppendable _append_10 = ita.append(_builder_15);
      StringConcatenation _builder_16 = new StringConcatenation();
      ITreeAppendable _referClass_5 = this._utils.referClass(ita, typeRefBuilder, eventPattern, CRUDActivationStateEnum.class);
      _builder_16.append(_referClass_5, "");
      _builder_16.append(".");
      ITreeAppendable _append_11 = _append_10.append(_builder_16);
      StringConcatenation _builder_17 = new StringConcatenation();
      CRUDActivationStateEnum _activationState = this.getActivationState(changeType);
      _builder_17.append(_activationState, "");
      _builder_17.append(", ");
      String _actionName_1 = this.getActionName(changeType);
      _builder_17.append(_actionName_1, "");
      _builder_17.append("_");
      _builder_17.append(counter, "");
      ITreeAppendable _append_12 = _append_11.append(_builder_17);
      StringConcatenation _builder_18 = new StringConcatenation();
      _builder_18.append(");");
      _append_12.append(_builder_18);
      _xblockexpression = ita.newLine();
    }
    return _xblockexpression;
  }
  
  private String getMappingMethodName(final Pattern pattern) {
    String _name = pattern.getName();
    String _plus = ("create" + _name);
    return (_plus + "_MappingRule");
  }
  
  private Multimap<Pattern, QueryResultChangeEventPattern> groupEventPatternsByIqPatternRef(final List<QueryResultChangeEventPattern> eventPatterns) {
    Multimap<Pattern, QueryResultChangeEventPattern> groupedPatterns = ArrayListMultimap.<Pattern, QueryResultChangeEventPattern>create();
    for (final QueryResultChangeEventPattern p : eventPatterns) {
      ParametrizedQueryReference _queryReference = p.getQueryReference();
      boolean _tripleNotEquals = (_queryReference != null);
      if (_tripleNotEquals) {
        ParametrizedQueryReference _queryReference_1 = p.getQueryReference();
        Pattern query = _queryReference_1.getQuery();
        groupedPatterns.put(query, ((QueryResultChangeEventPattern) p));
      }
    }
    return groupedPatterns;
  }
  
  private CRUDActivationStateEnum getActivationState(final QueryResultChangeType changeType) {
    if (changeType != null) {
      switch (changeType) {
        case FOUND:
          return CRUDActivationStateEnum.CREATED;
        case LOST:
          return CRUDActivationStateEnum.DELETED;
        default:
          break;
      }
    }
    return null;
  }
  
  private String getActionName(final QueryResultChangeType changeType) {
    if (changeType != null) {
      switch (changeType) {
        case FOUND:
          return "actionOnAppear";
        case LOST:
          return "actionOnDisappear";
        default:
          break;
      }
    }
    return null;
  }
  
  private void getParameterMapping(final ITreeAppendable appendable, final EObject ctx) {
    TypedParameterList params = ((QueryResultChangeEventPattern) ctx).getParameters();
    if ((params == null)) {
      return;
    }
    EList<TypedParameter> eventPatternParams = params.getParameters();
    ParametrizedQueryReference _queryReference = ((QueryResultChangeEventPattern) ctx).getQueryReference();
    PatternCallParameterList _parameterList = _queryReference.getParameterList();
    EList<PatternCallParameter> iqPatternParams = _parameterList.getParameters();
    int i = (-1);
    while (((i = (i + 1)) < iqPatternParams.size())) {
      {
        PatternCallParameter _get = iqPatternParams.get(i);
        String iqParamName = _get.getName();
        Integer eventParamPosition = this.getEventParamPosition(iqParamName, eventPatternParams);
        boolean _startsWith = iqParamName.startsWith("_");
        boolean _not = (!_startsWith);
        if (_not) {
          TypedParameter _get_1 = eventPatternParams.get((eventParamPosition).intValue());
          JvmTypeReference eventParamType = _get_1.getType();
          StringConcatenation _builder = new StringConcatenation();
          _builder.append("event.set");
          String _firstUpper = StringExtensions.toFirstUpper(iqParamName);
          _builder.append(_firstUpper, "");
          _builder.append("((");
          ITreeAppendable _append = appendable.append(_builder);
          StringConcatenation _builder_1 = new StringConcatenation();
          String _qualifiedName = eventParamType.getQualifiedName();
          _builder_1.append(_qualifiedName, "");
          ITreeAppendable _append_1 = _append.append(_builder_1);
          StringConcatenation _builder_2 = new StringConcatenation();
          _builder_2.append(")matchedPattern.get(");
          _builder_2.append(i, "");
          _builder_2.append("));");
          _builder_2.newLineIfNotEmpty();
          _append_1.append(_builder_2);
        }
      }
    }
  }
  
  private Integer getEventParamPosition(final String iqParamName, final List<TypedParameter> eventPatternParams) {
    int i = 0;
    for (final TypedParameter ep : eventPatternParams) {
      {
        String _name = ep.getName();
        boolean _equals = _name.equals(iqParamName);
        if (_equals) {
          return Integer.valueOf(i);
        }
        i = (i + 1);
      }
    }
    return null;
  }
}
