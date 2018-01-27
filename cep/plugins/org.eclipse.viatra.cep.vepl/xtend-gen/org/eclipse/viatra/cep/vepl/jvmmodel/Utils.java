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
import org.eclipse.emf.ecore.EObject;
import org.eclipse.viatra.cep.vepl.vepl.ModelElement;
import org.eclipse.xtend2.lib.StringConcatenation;
import org.eclipse.xtext.common.types.JvmAnnotationReference;
import org.eclipse.xtext.common.types.JvmAnnotationType;
import org.eclipse.xtext.common.types.JvmFormalParameter;
import org.eclipse.xtext.common.types.JvmOperation;
import org.eclipse.xtext.common.types.JvmType;
import org.eclipse.xtext.common.types.JvmTypeConstraint;
import org.eclipse.xtext.common.types.JvmTypeReference;
import org.eclipse.xtext.common.types.JvmUpperBound;
import org.eclipse.xtext.common.types.JvmVisibility;
import org.eclipse.xtext.common.types.JvmWildcardTypeReference;
import org.eclipse.xtext.common.types.TypesFactory;
import org.eclipse.xtext.common.types.util.TypeReferences;
import org.eclipse.xtext.naming.QualifiedName;
import org.eclipse.xtext.xbase.compiler.TypeReferenceSerializer;
import org.eclipse.xtext.xbase.compiler.output.ITreeAppendable;
import org.eclipse.xtext.xbase.jvmmodel.JvmTypeReferenceBuilder;
import org.eclipse.xtext.xbase.jvmmodel.JvmTypesBuilder;
import org.eclipse.xtext.xbase.lib.Extension;
import org.eclipse.xtext.xbase.lib.ObjectExtensions;
import org.eclipse.xtext.xbase.lib.Procedures.Procedure1;
import org.eclipse.xtext.xbase.lib.StringExtensions;

@SuppressWarnings("all")
public class Utils {
  @Inject
  @Extension
  private TypeReferenceSerializer typeReferenceSerializer;
  
  @Inject
  @Extension
  private JvmTypesBuilder jvmTypesBuilder;
  
  @Inject
  @Extension
  private TypeReferences references;
  
  public JvmOperation toAdvancedSetter(final ModelElement element, final String name, final JvmTypeReference type, final JvmTypeReferenceBuilder typeRefBuilder, final int index) {
    final JvmOperation advancedSetter = TypesFactory.eINSTANCE.createJvmOperation();
    String _firstUpper = StringExtensions.toFirstUpper(name);
    String _plus = ("set" + _firstUpper);
    advancedSetter.setSimpleName(_plus);
    JvmTypeReference _typeRef = typeRefBuilder.typeRef("void");
    advancedSetter.setReturnType(_typeRef);
    EList<JvmFormalParameter> _parameters = advancedSetter.getParameters();
    JvmFormalParameter _parameter = this.jvmTypesBuilder.toParameter(element, name, type);
    _parameters.add(_parameter);
    advancedSetter.setVisibility(JvmVisibility.PUBLIC);
    final Procedure1<ITreeAppendable> _function = new Procedure1<ITreeAppendable>() {
      @Override
      public void apply(final ITreeAppendable it) {
        StringConcatenation _builder = new StringConcatenation();
        _builder.append("this.");
        _builder.append(name, "");
        _builder.append(" = ");
        _builder.append(name, "");
        _builder.append(";");
        _builder.newLineIfNotEmpty();
        _builder.append("getParameters().set(");
        _builder.append(index, "");
        _builder.append(", ");
        _builder.append(name, "");
        _builder.append(");");
        it.append(_builder);
      }
    };
    this.jvmTypesBuilder.setBody(advancedSetter, _function);
    return advancedSetter;
  }
  
  public boolean addOverrideAnnotation(final JvmOperation method, final EObject context) {
    EList<JvmAnnotationReference> _annotations = method.getAnnotations();
    JvmAnnotationReference _createJvmAnnotationReference = TypesFactory.eINSTANCE.createJvmAnnotationReference();
    final Procedure1<JvmAnnotationReference> _function = new Procedure1<JvmAnnotationReference>() {
      @Override
      public void apply(final JvmAnnotationReference it) {
        JvmType _findDeclaredType = Utils.this.references.findDeclaredType(Override.class, context);
        it.setAnnotation(((JvmAnnotationType) _findDeclaredType));
      }
    };
    JvmAnnotationReference _doubleArrow = ObjectExtensions.<JvmAnnotationReference>operator_doubleArrow(_createJvmAnnotationReference, _function);
    return this.jvmTypesBuilder.<JvmAnnotationReference>operator_add(_annotations, _doubleArrow);
  }
  
  public JvmWildcardTypeReference wildCardExtends(final JvmTypeReference clone) {
    JvmWildcardTypeReference result = TypesFactory.eINSTANCE.createJvmWildcardTypeReference();
    JvmUpperBound upperBound = TypesFactory.eINSTANCE.createJvmUpperBound();
    upperBound.setTypeReference(clone);
    EList<JvmTypeConstraint> _constraints = result.getConstraints();
    _constraints.add(upperBound);
    return result;
  }
  
  public ITreeAppendable referClass(final ITreeAppendable appendable, final JvmTypeReferenceBuilder typeRefBuilder, final QualifiedName fqn, final EObject ctx) {
    String _string = fqn.toString();
    return this.referClass(appendable, typeRefBuilder, _string, ctx);
  }
  
  public ITreeAppendable referClass(final ITreeAppendable appendable, final JvmTypeReferenceBuilder typeRefBuilder, final String fqn, final EObject ctx) {
    ITreeAppendable _xblockexpression = null;
    {
      final JvmTypeReference ref = typeRefBuilder.typeRef(fqn);
      ITreeAppendable _xifexpression = null;
      if ((ref != null)) {
        this.serialize(appendable, ref, ctx);
      } else {
        String _string = fqn.toString();
        _xifexpression = appendable.append(_string);
      }
      _xblockexpression = _xifexpression;
    }
    return _xblockexpression;
  }
  
  public ITreeAppendable referClass(final ITreeAppendable appendable, final JvmTypeReferenceBuilder typeRefBuilder, final EObject ctx, final Class<?> clazz, final JvmTypeReference... typeArgs) {
    ITreeAppendable _xblockexpression = null;
    {
      final JvmTypeReference ref = typeRefBuilder.typeRef(clazz, typeArgs);
      ITreeAppendable _xifexpression = null;
      if ((ref != null)) {
        this.serialize(appendable, ref, ctx);
      } else {
        String _canonicalName = clazz.getCanonicalName();
        _xifexpression = appendable.append(_canonicalName);
      }
      _xblockexpression = _xifexpression;
    }
    return _xblockexpression;
  }
  
  public void serialize(final ITreeAppendable appendable, final JvmTypeReference ref, final EObject ctx) {
    this.typeReferenceSerializer.serialize(ref, ctx, appendable);
  }
}
