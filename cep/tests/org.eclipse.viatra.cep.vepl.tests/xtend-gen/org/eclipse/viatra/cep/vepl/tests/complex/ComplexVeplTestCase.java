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

import org.eclipse.viatra.cep.vepl.tests.VeplTestCase;
import org.eclipse.viatra.cep.vepl.vepl.ComplexEventPattern;
import org.eclipse.viatra.cep.vepl.vepl.EventModel;
import org.eclipse.xtend2.lib.StringConcatenation;
import org.eclipse.xtext.xbase.lib.Functions.Function0;

/**
 * Abstract class for every {@link ComplexEventPattern}-related VEPL test case.
 * 
 * @author Istvan David
 */
@SuppressWarnings("all")
public abstract class ComplexVeplTestCase extends VeplTestCase {
  @Override
  protected EventModel parse(final CharSequence text) {
    return super.parse((this.baseModel + text));
  }
  
  private final String baseModel = new Function0<String>() {
    public String apply() {
      StringConcatenation _builder = new StringConcatenation();
      _builder.append("atomicEvent a1");
      _builder.newLine();
      _builder.append("atomicEvent a2");
      _builder.newLine();
      _builder.append("atomicEvent a3");
      _builder.newLine();
      _builder.append("atomicEvent a4");
      _builder.newLine();
      _builder.append("atomicEvent a5");
      _builder.newLine();
      _builder.append("atomicEvent a6");
      _builder.newLine();
      _builder.newLine();
      return _builder.toString();
    }
  }.apply();
}
