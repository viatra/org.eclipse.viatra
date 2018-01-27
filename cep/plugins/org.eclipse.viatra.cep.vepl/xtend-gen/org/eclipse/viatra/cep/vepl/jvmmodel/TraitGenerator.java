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
import org.eclipse.viatra.cep.vepl.jvmmodel.NamingProvider;
import org.eclipse.viatra.cep.vepl.vepl.ModelElement;
import org.eclipse.viatra.cep.vepl.vepl.Trait;
import org.eclipse.viatra.cep.vepl.vepl.TraitTypedParameterList;
import org.eclipse.viatra.cep.vepl.vepl.TypedParameter;
import org.eclipse.viatra.cep.vepl.vepl.TypedParameterWithDefaultValue;
import org.eclipse.xtext.common.types.JvmFormalParameter;
import org.eclipse.xtext.common.types.JvmGenericType;
import org.eclipse.xtext.common.types.JvmMember;
import org.eclipse.xtext.common.types.JvmOperation;
import org.eclipse.xtext.common.types.JvmTypeReference;
import org.eclipse.xtext.common.types.JvmVisibility;
import org.eclipse.xtext.naming.QualifiedName;
import org.eclipse.xtext.xbase.jvmmodel.IJvmDeclaredTypeAcceptor;
import org.eclipse.xtext.xbase.jvmmodel.JvmTypeReferenceBuilder;
import org.eclipse.xtext.xbase.jvmmodel.JvmTypesBuilder;
import org.eclipse.xtext.xbase.lib.Extension;
import org.eclipse.xtext.xbase.lib.Procedures.Procedure1;
import org.eclipse.xtext.xbase.lib.StringExtensions;

@SuppressWarnings("all")
public class TraitGenerator {
  @Inject
  @Extension
  private JvmTypesBuilder jvmTypesBuilder;
  
  @Inject
  @Extension
  private NamingProvider _namingProvider;
  
  public void generateInterface(final Iterable<ModelElement> traits, final IJvmDeclaredTypeAcceptor acceptor, final JvmTypeReferenceBuilder typeRefBuilder) {
    for (final ModelElement trait : traits) {
      QualifiedName _traitInterfaceFqn = this._namingProvider.getTraitInterfaceFqn(trait);
      String _string = _traitInterfaceFqn.toString();
      final Procedure1<JvmGenericType> _function = new Procedure1<JvmGenericType>() {
        @Override
        public void apply(final JvmGenericType it) {
          TraitTypedParameterList _parameters = ((Trait) trait).getParameters();
          EList<TypedParameterWithDefaultValue> _parameters_1 = _parameters.getParameters();
          for (final TypedParameterWithDefaultValue param : _parameters_1) {
            {
              EList<JvmMember> _members = it.getMembers();
              TypedParameter _typedParameter = param.getTypedParameter();
              String _name = _typedParameter.getName();
              String _firstUpper = StringExtensions.toFirstUpper(_name);
              String _plus = ("get" + _firstUpper);
              TypedParameter _typedParameter_1 = param.getTypedParameter();
              JvmTypeReference _type = _typedParameter_1.getType();
              final Procedure1<JvmOperation> _function = new Procedure1<JvmOperation>() {
                @Override
                public void apply(final JvmOperation it) {
                  it.setVisibility(JvmVisibility.PUBLIC);
                  it.setAbstract(true);
                }
              };
              JvmOperation _method = TraitGenerator.this.jvmTypesBuilder.toMethod(param, _plus, _type, _function);
              TraitGenerator.this.jvmTypesBuilder.<JvmOperation>operator_add(_members, _method);
              EList<JvmMember> _members_1 = it.getMembers();
              TypedParameter _typedParameter_2 = param.getTypedParameter();
              String _name_1 = _typedParameter_2.getName();
              String _firstUpper_1 = StringExtensions.toFirstUpper(_name_1);
              String _plus_1 = ("set" + _firstUpper_1);
              JvmTypeReference _typeRef = typeRefBuilder.typeRef("void");
              final Procedure1<JvmOperation> _function_1 = new Procedure1<JvmOperation>() {
                @Override
                public void apply(final JvmOperation it) {
                  it.setVisibility(JvmVisibility.PUBLIC);
                  EList<JvmFormalParameter> _parameters = it.getParameters();
                  TypedParameter _typedParameter = param.getTypedParameter();
                  String _name = _typedParameter.getName();
                  TypedParameter _typedParameter_1 = param.getTypedParameter();
                  JvmTypeReference _type = _typedParameter_1.getType();
                  JvmFormalParameter _parameter = TraitGenerator.this.jvmTypesBuilder.toParameter(param, _name, _type);
                  TraitGenerator.this.jvmTypesBuilder.<JvmFormalParameter>operator_add(_parameters, _parameter);
                  it.setAbstract(true);
                }
              };
              JvmOperation _method_1 = TraitGenerator.this.jvmTypesBuilder.toMethod(param, _plus_1, _typeRef, _function_1);
              TraitGenerator.this.jvmTypesBuilder.<JvmOperation>operator_add(_members_1, _method_1);
            }
          }
        }
      };
      JvmGenericType _interface = this.jvmTypesBuilder.toInterface(trait, _string, _function);
      acceptor.<JvmGenericType>accept(_interface);
    }
  }
}
