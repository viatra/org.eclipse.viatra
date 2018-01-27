/**
 * Copyright (c) 2004-2015, Peter Lunk, Zoltan Ujhelyi and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *   Peter Lunk - initial API and implementation
 */
package org.eclipse.viatra.integration.mwe2.mwe2impl;

import org.eclipse.viatra.integration.mwe2.ITransformationStep;
import org.eclipse.viatra.integration.mwe2.mwe2impl.Sequence;
import org.eclipse.viatra.integration.mwe2.providers.IIterationNumberProvider;
import org.eclipse.viatra.integration.mwe2.providers.impl.BaseIterationNumberProvider;
import org.eclipse.xtext.xbase.lib.Exceptions;
import org.eclipse.xtext.xbase.lib.IterableExtensions;
import org.eclipse.xtext.xbase.lib.Procedures.Procedure1;

/**
 * Composite transformation step that implements a 'for' style loop. The number
 * of iterations can either be explicitly specified at compile time, using the
 * iterations attribute, or dynamically calculated by an IIterationNumberProvider.
 * 
 * @author Peter Lunk
 */
@SuppressWarnings("all")
public class ForLoop extends Sequence {
  private IIterationNumberProvider provider;
  
  /**
   * Specify the numebr of iteration explicitly
   */
  public void setIterations(final String maxValue) {
    try {
      int value = Integer.parseInt(maxValue);
      BaseIterationNumberProvider _baseIterationNumberProvider = new BaseIterationNumberProvider(Integer.valueOf(value));
      this.provider = _baseIterationNumberProvider;
    } catch (final Throwable _t) {
      if (_t instanceof NumberFormatException) {
        final NumberFormatException e = (NumberFormatException)_t;
        e.printStackTrace();
      } else {
        throw Exceptions.sneakyThrow(_t);
      }
    }
  }
  
  /**
   * Add a provider
   */
  public void setIterationProvider(final IIterationNumberProvider provider) {
    this.provider = provider;
  }
  
  @Override
  public void execute() {
    for (int i = 0; (i < (this.provider.getIterationNumber()).intValue()); i = (i + 1)) {
      final Procedure1<ITransformationStep> _function = new Procedure1<ITransformationStep>() {
        @Override
        public void apply(final ITransformationStep it) {
          it.execute();
        }
      };
      IterableExtensions.<ITransformationStep>forEach(this.step, _function);
    }
  }
}
