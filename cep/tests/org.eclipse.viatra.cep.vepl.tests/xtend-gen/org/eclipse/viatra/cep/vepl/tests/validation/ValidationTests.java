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
package org.eclipse.viatra.cep.vepl.tests.validation;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.viatra.cep.vepl.tests.VeplTestCase;
import org.eclipse.viatra.cep.vepl.validation.VeplValidator;
import org.eclipse.viatra.cep.vepl.vepl.AtomicEventPattern;
import org.eclipse.viatra.cep.vepl.vepl.EventModel;
import org.eclipse.viatra.cep.vepl.vepl.ModelElement;
import org.eclipse.viatra.cep.vepl.vepl.VeplPackage;
import org.eclipse.xtend2.lib.StringConcatenation;
import org.eclipse.xtext.xbase.lib.Functions.Function1;
import org.eclipse.xtext.xbase.lib.IterableExtensions;
import org.eclipse.xtext.xbase.lib.Procedures.Procedure1;
import org.junit.Test;

@SuppressWarnings("all")
public class ValidationTests extends VeplTestCase {
  @Test
  public void uniqueName() {
    StringConcatenation _builder = new StringConcatenation();
    _builder.append("atomicEvent a");
    _builder.newLine();
    _builder.append("atomicEvent a");
    _builder.newLine();
    final EventModel model1 = this.parse(_builder);
    EList<ModelElement> _modelElements = model1.getModelElements();
    final Function1<ModelElement, Boolean> _function = new Function1<ModelElement, Boolean>() {
      @Override
      public Boolean apply(final ModelElement e) {
        return Boolean.valueOf((e instanceof AtomicEventPattern));
      }
    };
    final Iterable<ModelElement> erroneousElements = IterableExtensions.<ModelElement>filter(_modelElements, _function);
    final Procedure1<ModelElement> _function_1 = new Procedure1<ModelElement>() {
      @Override
      public void apply(final ModelElement e) {
        EClass _atomicEventPattern = VeplPackage.eINSTANCE.getAtomicEventPattern();
        ValidationTests.this._validationTestHelper.assertError(e, _atomicEventPattern, VeplValidator.INVALID_NAME);
      }
    };
    IterableExtensions.<ModelElement>forEach(erroneousElements, _function_1);
    StringConcatenation _builder_1 = new StringConcatenation();
    _builder_1.append("atomicEvent a");
    _builder_1.newLine();
    _builder_1.append("atomicEvent b");
    _builder_1.newLine();
    final EventModel model2 = this.parse(_builder_1);
    this._validationTestHelper.assertNoErrors(model2);
  }
  
  @Test
  public void validPatternCallArguments() {
    StringConcatenation _builder = new StringConcatenation();
    _builder.append("atomicEvent a(p1:String, p2:int)");
    _builder.newLine();
    _builder.append("atomicEvent b(p1:String, p2:int)");
    _builder.newLine();
    _builder.newLine();
    _builder.append("complexEvent c(p1:String, p2:int){");
    _builder.newLine();
    _builder.append("    ");
    _builder.append("as a->b(p1, _)");
    _builder.newLine();
    _builder.append("}");
    _builder.newLine();
    final EventModel model1 = this.parse(_builder);
    this._validationTestHelper.assertNoErrors(model1);
    StringConcatenation _builder_1 = new StringConcatenation();
    _builder_1.append("atomicEvent a(p1:String, p2:int)");
    _builder_1.newLine();
    _builder_1.append("atomicEvent b(p1:String, p2:int)");
    _builder_1.newLine();
    _builder_1.newLine();
    _builder_1.append("complexEvent c(p1:String, p2:int){");
    _builder_1.newLine();
    _builder_1.append("    ");
    _builder_1.append("as a(p1)->b");
    _builder_1.newLine();
    _builder_1.append("}");
    _builder_1.newLine();
    final EventModel model2 = this.parse(_builder_1);
    EClass _parameterizedPatternCall = VeplPackage.eINSTANCE.getParameterizedPatternCall();
    this._validationTestHelper.assertError(model2, _parameterizedPatternCall, VeplValidator.INVALID_ARGUMENTS);
    StringConcatenation _builder_2 = new StringConcatenation();
    _builder_2.append("atomicEvent a(p1:String, p2:int)");
    _builder_2.newLine();
    _builder_2.append("atomicEvent b(p1:String, p2:int)");
    _builder_2.newLine();
    _builder_2.newLine();
    _builder_2.append("complexEvent c(p1:String, p2:int){");
    _builder_2.newLine();
    _builder_2.append("    ");
    _builder_2.append("as a(p1)->b()");
    _builder_2.newLine();
    _builder_2.append("}");
    _builder_2.newLine();
    final EventModel model3 = this.parse(_builder_2);
    EClass _parameterizedPatternCall_1 = VeplPackage.eINSTANCE.getParameterizedPatternCall();
    this._validationTestHelper.assertError(model3, _parameterizedPatternCall_1, VeplValidator.INVALID_ARGUMENTS);
  }
  
  @Test
  public void explicitlyImportedQueryPackage() {
    StringConcatenation _builder = new StringConcatenation();
    _builder.append("queryEvent ce() as someUnimportedQuery");
    _builder.newLine();
    final EventModel model = this.parse(_builder);
    EClass _queryResultChangeEventPattern = VeplPackage.eINSTANCE.getQueryResultChangeEventPattern();
    this._validationTestHelper.assertError(model, _queryResultChangeEventPattern, VeplValidator.MISSING_QUERY_IMPORT);
  }
  
  @Test
  public void expressionAtomWithTimewindowMustFeatureMultiplicity() {
    StringConcatenation _builder = new StringConcatenation();
    _builder.append("atomicEvent a");
    _builder.newLine();
    _builder.newLine();
    _builder.append("complexEvent c(){");
    _builder.newLine();
    _builder.append("    ");
    _builder.append("as a[1000]");
    _builder.newLine();
    _builder.append("}");
    _builder.newLine();
    _builder.newLine();
    final EventModel model1 = this.parse(_builder);
    EClass _atom = VeplPackage.eINSTANCE.getAtom();
    this._validationTestHelper.assertError(model1, _atom, VeplValidator.ATOM_TIMEWINDOW_NO_MULTIPLICITY);
    StringConcatenation _builder_1 = new StringConcatenation();
    _builder_1.append("atomicEvent a");
    _builder_1.newLine();
    _builder_1.newLine();
    _builder_1.newLine();
    _builder_1.append("complexEvent c(){");
    _builder_1.newLine();
    _builder_1.append("    ");
    _builder_1.append("as a{1}");
    _builder_1.newLine();
    _builder_1.append("}");
    _builder_1.newLine();
    final EventModel model2 = this.parse(_builder_1);
    EClass _atom_1 = VeplPackage.eINSTANCE.getAtom();
    this._validationTestHelper.assertError(model2, _atom_1, VeplValidator.ATOM_TIMEWINDOW_NO_MULTIPLICITY);
  }
  
  @Test
  public void complexEventPatternWithPlainAtomExpression() {
    StringConcatenation _builder = new StringConcatenation();
    _builder.append("atomicEvent a");
    _builder.newLine();
    _builder.newLine();
    _builder.append("complexEvent c1(){");
    _builder.newLine();
    _builder.append("    ");
    _builder.append("as a");
    _builder.newLine();
    _builder.append("}");
    _builder.newLine();
    _builder.newLine();
    final EventModel model = this.parse(_builder);
    EClass _complexEventPattern = VeplPackage.eINSTANCE.getComplexEventPattern();
    this._validationTestHelper.assertWarning(model, _complexEventPattern, 
      VeplValidator.SINGE_PLAIN_ATOM_IN_COMPLEX_EVENT_EXPRESSION);
  }
}
