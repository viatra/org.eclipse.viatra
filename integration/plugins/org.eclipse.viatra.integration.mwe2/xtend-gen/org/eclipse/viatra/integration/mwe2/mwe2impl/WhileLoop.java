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

import org.eclipse.emf.mwe2.runtime.workflow.IWorkflowContext;
import org.eclipse.viatra.integration.mwe2.ITransformationStep;
import org.eclipse.viatra.integration.mwe2.mwe2impl.Sequence;
import org.eclipse.viatra.integration.mwe2.providers.IConditionProvider;
import org.eclipse.xtext.xbase.lib.IterableExtensions;
import org.eclipse.xtext.xbase.lib.Procedures.Procedure1;

/**
 * Composite transformation step that implements a 'while' style loop. similar to the
 * conditional step, the dynamically evaluated loop condition is provided by an IConditionProvider.
 * 
 * @author Peter Lunk
 */
@SuppressWarnings("all")
public class WhileLoop extends Sequence {
  protected IConditionProvider condition;
  
  @Override
  public void execute() {
    while (this.condition.apply()) {
      final Procedure1<ITransformationStep> _function = new Procedure1<ITransformationStep>() {
        @Override
        public void apply(final ITransformationStep it) {
          it.execute();
        }
      };
      IterableExtensions.<ITransformationStep>forEach(this.step, _function);
    }
  }
  
  @Override
  public void initialize(final IWorkflowContext ctx) {
    super.initialize(ctx);
    if ((this.condition != null)) {
      this.condition.setContext(ctx);
    }
  }
  
  public void setCondition(final IConditionProvider condition) {
    this.condition = condition;
  }
}
