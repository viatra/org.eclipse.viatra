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
package org.eclipse.viatra.cep.vepl.tests.atomic;

import org.eclipse.emf.common.util.EList;
import org.eclipse.viatra.cep.vepl.tests.VeplTestCase;
import org.eclipse.viatra.cep.vepl.vepl.AtomicEventPattern;
import org.eclipse.viatra.cep.vepl.vepl.EventModel;
import org.eclipse.viatra.cep.vepl.vepl.ModelElement;
import org.eclipse.xtend2.lib.StringConcatenation;
import org.eclipse.xtext.xbase.lib.Functions.Function1;
import org.eclipse.xtext.xbase.lib.IterableExtensions;
import org.junit.Assert;
import org.junit.Test;

@SuppressWarnings("all")
public class AtomicTests extends VeplTestCase {
  @Test
  public void parseAtomicPatterns() {
    StringConcatenation _builder = new StringConcatenation();
    _builder.append("atomicEvent a1\t\t//no parameters no body");
    _builder.newLine();
    _builder.append("atomicEvent a2()\t//parameters present but no body");
    _builder.newLine();
    _builder.append("atomicEvent a3{}\t//no parameters but body present");
    _builder.newLine();
    _builder.append("atomicEvent a4(){}\t//both parameters and body present");
    _builder.newLine();
    _builder.newLine();
    final EventModel model = this.parse(_builder);
    this._validationTestHelper.assertNoErrors(model);
    EList<ModelElement> _modelElements = model.getModelElements();
    final Function1<ModelElement, Boolean> _function = new Function1<ModelElement, Boolean>() {
      @Override
      public Boolean apply(final ModelElement m) {
        return Boolean.valueOf((m instanceof AtomicEventPattern));
      }
    };
    Iterable<ModelElement> _filter = IterableExtensions.<ModelElement>filter(_modelElements, _function);
    int _size = IterableExtensions.size(_filter);
    Assert.assertEquals(4, _size);
  }
}
