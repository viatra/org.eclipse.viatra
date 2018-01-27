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
package org.eclipse.viatra.cep.vepl.tests.complex;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.viatra.cep.vepl.tests.complex.ComplexVeplTestCase;
import org.eclipse.viatra.cep.vepl.validation.VeplValidator;
import org.eclipse.viatra.cep.vepl.vepl.EventModel;
import org.eclipse.viatra.cep.vepl.vepl.VeplPackage;
import org.eclipse.xtend2.lib.StringConcatenation;
import org.junit.Test;

@SuppressWarnings("all")
public class InfinityTests extends ComplexVeplTestCase {
  public static String[] validExpressions() {
    StringConcatenation _builder = new StringConcatenation();
    _builder.append("a1{*}->a2");
    StringConcatenation _builder_1 = new StringConcatenation();
    _builder_1.append("a1{*} AND a2");
    StringConcatenation _builder_2 = new StringConcatenation();
    _builder_2.append("NOT a1");
    StringConcatenation _builder_3 = new StringConcatenation();
    _builder_3.append("(a1 -> a2{*}) -> a3");
    StringConcatenation _builder_4 = new StringConcatenation();
    _builder_4.append("(a1 -> (a2 -> a3{*})) -> a4");
    return new String[] { _builder.toString(), _builder_1.toString(), _builder_2.toString(), _builder_3.toString(), _builder_4.toString() };
  }
  
  public static String[] invalidExpressions() {
    StringConcatenation _builder = new StringConcatenation();
    _builder.append("a1{*}");
    StringConcatenation _builder_1 = new StringConcatenation();
    _builder_1.append("a1{*} OR a2");
    StringConcatenation _builder_2 = new StringConcatenation();
    _builder_2.append("NOT a1{*}");
    return new String[] { _builder.toString(), _builder_1.toString(), _builder_2.toString() };
  }
  
  @Test
  public void parseExpressions() {
    String[] _validExpressions = InfinityTests.validExpressions();
    for (final String expression : _validExpressions) {
      StringConcatenation _builder = new StringConcatenation();
      _builder.append("complexEvent c1(){");
      _builder.newLine();
      _builder.append("                ");
      _builder.append("as (");
      _builder.append(expression, "                ");
      _builder.append(")");
      _builder.newLineIfNotEmpty();
      _builder.append("            ");
      _builder.append("}");
      EventModel _parse = this.parse(_builder);
      this._validationTestHelper.assertNoErrors(_parse);
    }
    String[] _invalidExpressions = InfinityTests.invalidExpressions();
    for (final String expression_1 : _invalidExpressions) {
      StringConcatenation _builder_1 = new StringConcatenation();
      _builder_1.append("complexEvent c1(){");
      _builder_1.newLine();
      _builder_1.append("                ");
      _builder_1.append("as (");
      _builder_1.append(expression_1, "                ");
      _builder_1.append(")");
      _builder_1.newLineIfNotEmpty();
      _builder_1.append("            ");
      _builder_1.append("}");
      EventModel _parse_1 = this.parse(_builder_1);
      EClass _complexEventExpression = VeplPackage.eINSTANCE.getComplexEventExpression();
      this._validationTestHelper.assertError(_parse_1, _complexEventExpression, 
        VeplValidator.UNSAFE_INFINITE_MULTIPLICITY);
    }
  }
}
