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

import java.util.ArrayList;
import java.util.List;
import org.eclipse.emf.mwe2.runtime.workflow.IWorkflowComponent;
import org.eclipse.emf.mwe2.runtime.workflow.IWorkflowContext;
import org.eclipse.viatra.integration.mwe2.ICompositeStep;
import org.eclipse.viatra.integration.mwe2.ITransformationStep;
import org.eclipse.xtext.xbase.lib.IterableExtensions;
import org.eclipse.xtext.xbase.lib.Procedures.Procedure1;

/**
 * MWE2 workflow component that represents a Transformation chain. Each one of these transformation chains
 * can contain more transformation steps, as the class implements the ICompositeStep interface.
 * 
 * 
 * @author Peter Lunk
 */
@SuppressWarnings("all")
public class TransformationChain implements IWorkflowComponent, ICompositeStep {
  private List<ITransformationStep> steps = new ArrayList<ITransformationStep>();
  
  /**
   * Upon being invoked by the MWE runner, the transformation chain will initialize its
   * subcomponents and execute them as well.
   */
  @Override
  public void invoke(final IWorkflowContext ctx) {
    final Procedure1<ITransformationStep> _function = new Procedure1<ITransformationStep>() {
      @Override
      public void apply(final ITransformationStep it) {
        it.initialize(ctx);
      }
    };
    IterableExtensions.<ITransformationStep>forEach(this.steps, _function);
    final Procedure1<ITransformationStep> _function_1 = new Procedure1<ITransformationStep>() {
      @Override
      public void apply(final ITransformationStep it) {
        it.execute();
      }
    };
    IterableExtensions.<ITransformationStep>forEach(this.steps, _function_1);
  }
  
  /**
   * After it is invoked, it disposes all of the subcomponents.
   */
  @Override
  public void postInvoke() {
    final Procedure1<ITransformationStep> _function = new Procedure1<ITransformationStep>() {
      @Override
      public void apply(final ITransformationStep it) {
        it.dispose();
      }
    };
    IterableExtensions.<ITransformationStep>forEach(this.steps, _function);
  }
  
  @Override
  public void preInvoke() {
  }
  
  @Override
  public void addStep(final ITransformationStep step) {
    this.steps.add(step);
  }
  
  @Override
  public List<ITransformationStep> getStep() {
    return this.getStep();
  }
}
