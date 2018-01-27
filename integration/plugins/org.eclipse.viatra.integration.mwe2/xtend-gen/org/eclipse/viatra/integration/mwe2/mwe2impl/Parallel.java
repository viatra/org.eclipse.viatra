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
import org.eclipse.viatra.integration.mwe2.ITransformationStep;
import org.eclipse.viatra.integration.mwe2.mwe2impl.Sequence;
import org.eclipse.xtext.xbase.lib.Exceptions;
import org.eclipse.xtext.xbase.lib.IterableExtensions;
import org.eclipse.xtext.xbase.lib.Procedures.Procedure1;

/**
 * Composite transformation step that enables the parallel execution of transformation steps.
 * Each transformation step will be assigned to a new thread.
 * 
 * Be advised: The parallel regions should be independent from each other, as there is no order
 * of execution defined. This means, that typically parallel regions should not send each other
 * parametric messages.
 * 
 * @author Peter Lunk
 */
@SuppressWarnings("all")
public class Parallel extends Sequence {
  public static class ParallelRunnable implements Runnable {
    private ITransformationStep step;
    
    public ParallelRunnable(final ITransformationStep step) {
      this.step = step;
    }
    
    @Override
    public void run() {
      this.step.execute();
    }
  }
  
  /**
   * Assign each transformation step to a Thread and run them
   */
  @Override
  public void execute() {
    try {
      boolean finished = false;
      final ArrayList<Thread> threads = Lists.<Thread>newArrayList();
      final Procedure1<ITransformationStep> _function = new Procedure1<ITransformationStep>() {
        @Override
        public void apply(final ITransformationStep s) {
          Parallel.ParallelRunnable _parallelRunnable = new Parallel.ParallelRunnable(s);
          final Thread worker = new Thread(_parallelRunnable);
          threads.add(worker);
          worker.start();
        }
      };
      IterableExtensions.<ITransformationStep>forEach(this.step, _function);
      while ((!finished)) {
        {
          Thread.sleep(10);
          finished = true;
          for (final Thread thread : threads) {
            boolean _isAlive = thread.isAlive();
            if (_isAlive) {
              finished = false;
            }
          }
        }
      }
    } catch (Throwable _e) {
      throw Exceptions.sneakyThrow(_e);
    }
  }
}
