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
import org.eclipse.viatra.integration.mwe2.mwe2impl.WhileLoop;
import org.eclipse.xtext.xbase.lib.IterableExtensions;
import org.eclipse.xtext.xbase.lib.Procedures.Procedure1;

/**
 * Composite transformation step that implements a 'do..while' style loop. similar to the
 * while loop, the dynamically evaluated loop condition is provided by an IConditionProvider.
 * 
 * @author Peter Lunk
 */
@SuppressWarnings("all")
public class DoWhileLoop extends WhileLoop {
  @Override
  public void execute() {
    do {
      final Procedure1<ITransformationStep> _function = new Procedure1<ITransformationStep>() {
        @Override
        public void apply(final ITransformationStep it) {
          it.execute();
        }
      };
      IterableExtensions.<ITransformationStep>forEach(this.step, _function);
    } while(this.condition.apply());
  }
}
