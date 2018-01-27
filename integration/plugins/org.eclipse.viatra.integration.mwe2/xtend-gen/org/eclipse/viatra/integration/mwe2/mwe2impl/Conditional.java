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
import org.eclipse.viatra.integration.mwe2.providers.IConditionProvider;

/**
 * Composite transformation step that implements an IF style conditional construction. The condition is
 * specified by an IConditionProvider which enables the specification of dynamically evaluated conditions.
 * 
 * If the condition evaluation returns true, the ifTrue step is executed, if otherwise, the ifFalse step is executed.
 * 
 * Note: As the condition is evaluated runtime, both of the steps is initialized.
 * 
 * @author Peter Lunk
 */
@SuppressWarnings("all")
public class Conditional implements ITransformationStep {
  protected IWorkflowContext ctx;
  
  protected IConditionProvider condition;
  
  protected ITransformationStep ifTrue;
  
  protected ITransformationStep ifFalse;
  
  public ITransformationStep setIfTrue(final ITransformationStep ifTrue) {
    return this.ifTrue = ifTrue;
  }
  
  public ITransformationStep setIfFalse(final ITransformationStep ifFalse) {
    return this.ifFalse = ifFalse;
  }
  
  public void setCondition(final IConditionProvider condition) {
    this.condition = condition;
  }
  
  @Override
  public void initialize(final IWorkflowContext ctx) {
    this.ctx = ctx;
    if ((this.condition != null)) {
      this.condition.setContext(ctx);
    }
    this.ifTrue.initialize(ctx);
    this.ifFalse.initialize(ctx);
  }
  
  @Override
  public void execute() {
    boolean _apply = this.condition.apply();
    if (_apply) {
      this.ifTrue.execute();
    } else {
      this.ifFalse.execute();
    }
  }
  
  @Override
  public void dispose() {
    this.ifTrue.dispose();
    this.ifFalse.dispose();
  }
}
