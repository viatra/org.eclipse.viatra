/**
 * Copyright (c) 2004-2015, Istvan David, Istvan Rath and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Istvan David - initial API and implementation
 */
package org.eclipse.viatra.cep.vepl.tests.complex;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.viatra.cep.vepl.tests.complex.ComplexVeplTestCase;
import org.eclipse.viatra.cep.vepl.validation.VeplValidator;
import org.eclipse.viatra.cep.vepl.vepl.ComplexEventPattern;
import org.eclipse.viatra.cep.vepl.vepl.EventModel;
import org.eclipse.viatra.cep.vepl.vepl.ModelElement;
import org.eclipse.viatra.cep.vepl.vepl.VeplPackage;
import org.eclipse.xtend2.lib.StringConcatenation;
import org.eclipse.xtext.xbase.lib.Conversions;
import org.eclipse.xtext.xbase.lib.Functions.Function1;
import org.eclipse.xtext.xbase.lib.IterableExtensions;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.theories.Theory;

@SuppressWarnings("all")
public class ComplexTests extends ComplexVeplTestCase {
  public CharSequence getFullExpression(final String expression, final String multiplicity, final String timewindow) {
    StringConcatenation _builder = new StringConcatenation();
    _builder.append("complexEvent c1(){");
    _builder.newLine();
    _builder.append("    ");
    _builder.append("as (");
    _builder.append(expression, "    ");
    _builder.append(")");
    _builder.append(multiplicity, "    ");
    _builder.append(timewindow, "    ");
    _builder.newLineIfNotEmpty();
    _builder.append("}");
    _builder.newLine();
    return _builder;
  }
  
  public static String[] expressions() {
    StringConcatenation _builder = new StringConcatenation();
    _builder.append("a1->a2");
    StringConcatenation _builder_1 = new StringConcatenation();
    _builder_1.append("a1 OR a2");
    StringConcatenation _builder_2 = new StringConcatenation();
    _builder_2.append("a1 AND a2");
    StringConcatenation _builder_3 = new StringConcatenation();
    _builder_3.append("NOT a1");
    return new String[] { _builder.toString(), _builder_1.toString(), _builder_2.toString(), _builder_3.toString() };
  }
  
  public static String[] multiplicities() {
    StringConcatenation _builder = new StringConcatenation();
    StringConcatenation _builder_1 = new StringConcatenation();
    _builder_1.append("{10}");
    StringConcatenation _builder_2 = new StringConcatenation();
    _builder_2.append("{+}");
    StringConcatenation _builder_3 = new StringConcatenation();
    _builder_3.append("{*}");
    return new String[] { _builder.toString(), _builder_1.toString(), _builder_2.toString(), _builder_3.toString() };
  }
  
  public static String[] timewindows() {
    StringConcatenation _builder = new StringConcatenation();
    StringConcatenation _builder_1 = new StringConcatenation();
    _builder_1.append("[1000]");
    return new String[] { _builder.toString(), _builder_1.toString() };
  }
  
  /**
   * This test method should be captured by a {@link Theory},
   * but we rely on the XtextRunner and no hybrid Xtext-Theory runner is available currently.
   */
  @Test
  public void parseOperators() {
    String[] _expressions = ComplexTests.expressions();
    for (final String expression : _expressions) {
      String[] _timewindows = ComplexTests.timewindows();
      for (final String timewindow : _timewindows) {
        {
          String[] _multiplicities = ComplexTests.multiplicities();
          final Function1<String, Boolean> _function = new Function1<String, Boolean>() {
            @Override
            public Boolean apply(final String m) {
              boolean _equalsIgnoreCase = m.equalsIgnoreCase("{*}");
              return Boolean.valueOf((!_equalsIgnoreCase));
            }
          };
          Iterable<String> _filter = IterableExtensions.<String>filter(((Iterable<String>)Conversions.doWrapArray(_multiplicities)), _function);
          for (final String multiplicity : _filter) {
            this.testExpression(expression, multiplicity, timewindow, false);
          }
          this.testExpression(expression, "{*}", timewindow, true);
        }
      }
    }
  }
  
  public void testExpression(final String expression, final String multiplicity, final String timewindow, final boolean assertErrors) {
    final CharSequence fullExpression = this.getFullExpression(expression, multiplicity, timewindow);
    final EventModel model = this.parse(fullExpression);
    if (assertErrors) {
      EClass _complexEventExpression = VeplPackage.eINSTANCE.getComplexEventExpression();
      this._validationTestHelper.assertError(model, _complexEventExpression, 
        VeplValidator.UNSAFE_INFINITE_MULTIPLICITY);
    } else {
      this._validationTestHelper.assertNoErrors(model);
    }
    EList<ModelElement> _modelElements = model.getModelElements();
    final Function1<ModelElement, Boolean> _function = new Function1<ModelElement, Boolean>() {
      @Override
      public Boolean apply(final ModelElement m) {
        return Boolean.valueOf((m instanceof ComplexEventPattern));
      }
    };
    Iterable<ModelElement> _filter = IterableExtensions.<ModelElement>filter(_modelElements, _function);
    int _size = IterableExtensions.size(_filter);
    Assert.assertEquals(1, _size);
  }
}
