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

import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.List;
import org.eclipse.emf.mwe2.runtime.workflow.IWorkflowContext;
import org.eclipse.viatra.integration.mwe2.ICompositeStep;
import org.eclipse.viatra.integration.mwe2.ITransformationStep;
import org.eclipse.xtext.xbase.lib.IterableExtensions;
import org.eclipse.xtext.xbase.lib.Procedures.Procedure1;

/**
 * Composite transformation step that implements a basic sequence control flow construction.
 * It initializes and executes its contained steps in the same sequence they have been defined.
 * 
 * @author Peter Lunk
 */
@SuppressWarnings("all")
public class Sequence implements ITransformationStep, ICompositeStep {
  protected final List<ITransformationStep> step;
  
  protected IWorkflowContext ctx;
  
  public Sequence() {
    super();
    ArrayList<ITransformationStep> _newArrayList = Lists.<ITransformationStep>newArrayList();
    this.step = _newArrayList;
  }
  
  @Override
  public void addStep(final ITransformationStep step) {
    this.step.add(step);
  }
  
  @Override
  public List<ITransformationStep> getStep() {
    return this.step;
  }
  
  @Override
  public void initialize(final IWorkflowContext ctx) {
    this.ctx = ctx;
    final Procedure1<ITransformationStep> _function = new Procedure1<ITransformationStep>() {
      @Override
      public void apply(final ITransformationStep it) {
        it.initialize(ctx);
      }
    };
    IterableExtensions.<ITransformationStep>forEach(this.step, _function);
  }
  
  @Override
  public void execute() {
    final Procedure1<ITransformationStep> _function = new Procedure1<ITransformationStep>() {
      @Override
      public void apply(final ITransformationStep it) {
        it.execute();
      }
    };
    IterableExtensions.<ITransformationStep>forEach(this.step, _function);
  }
  
  @Override
  public void dispose() {
    final Procedure1<ITransformationStep> _function = new Procedure1<ITransformationStep>() {
      @Override
      public void apply(final ITransformationStep it) {
        it.dispose();
      }
    };
    IterableExtensions.<ITransformationStep>forEach(this.step, _function);
  }
}
