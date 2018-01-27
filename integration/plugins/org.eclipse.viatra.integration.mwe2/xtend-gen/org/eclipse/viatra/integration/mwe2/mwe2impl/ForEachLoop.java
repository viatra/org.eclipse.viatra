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
import org.eclipse.viatra.integration.mwe2.providers.IIterableProvider;
import org.eclipse.xtext.xbase.lib.IterableExtensions;
import org.eclipse.xtext.xbase.lib.Procedures.Procedure1;

/**
 * Composite transformation step that implements a 'foreach' style loop.
 * This kind of loop requires an IIterable object to iterate through. It is provided runtime
 * by an IIterableProvider.
 * 
 * @author Peter Lunk
 */
@SuppressWarnings("all")
public class ForEachLoop extends Sequence {
  private IIterableProvider provider;
  
  public void setIterable(final IIterableProvider iterable) {
    this.provider = iterable;
  }
  
  @Override
  public void execute() {
    Iterable<?> _iterable = this.provider.getIterable();
    final Procedure1<Object> _function = new Procedure1<Object>() {
      @Override
      public void apply(final Object it) {
        final Procedure1<ITransformationStep> _function = new Procedure1<ITransformationStep>() {
          @Override
          public void apply(final ITransformationStep it) {
            it.execute();
          }
        };
        IterableExtensions.<ITransformationStep>forEach(ForEachLoop.this.step, _function);
      }
    };
    IterableExtensions.forEach(_iterable, _function);
  }
}
