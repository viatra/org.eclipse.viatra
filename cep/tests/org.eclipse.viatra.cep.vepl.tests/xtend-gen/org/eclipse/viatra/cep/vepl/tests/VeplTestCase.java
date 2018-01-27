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
package org.eclipse.viatra.cep.vepl.tests;

import com.google.inject.Inject;
import org.eclipse.viatra.cep.vepl.VeplInjectorProvider;
import org.eclipse.viatra.cep.vepl.vepl.EventModel;
import org.eclipse.xtend2.lib.StringConcatenation;
import org.eclipse.xtext.junit4.InjectWith;
import org.eclipse.xtext.junit4.XtextRunner;
import org.eclipse.xtext.junit4.util.ParseHelper;
import org.eclipse.xtext.junit4.validation.ValidationTestHelper;
import org.eclipse.xtext.xbase.lib.Exceptions;
import org.eclipse.xtext.xbase.lib.Extension;
import org.eclipse.xtext.xbase.lib.Functions.Function0;
import org.junit.runner.RunWith;

/**
 * Abstract class for every VEPL-related test case.
 * 
 * @author Istvan David
 */
@InjectWith(VeplInjectorProvider.class)
@RunWith(XtextRunner.class)
@SuppressWarnings("all")
public abstract class VeplTestCase {
  @Inject
  @Extension
  protected ParseHelper<EventModel> parser;
  
  @Inject
  @Extension
  protected ValidationTestHelper _validationTestHelper;
  
  protected EventModel parse(final CharSequence text) {
    try {
      return this.parser.parse((this.packageDeclaration + text));
    } catch (Throwable _e) {
      throw Exceptions.sneakyThrow(_e);
    }
  }
  
  private final String packageDeclaration = new Function0<String>() {
    public String apply() {
      StringConcatenation _builder = new StringConcatenation();
      _builder.append("package org.eclipse.viatra.cep.vepl.tests");
      _builder.newLine();
      _builder.newLine();
      return _builder.toString();
    }
  }.apply();
}
