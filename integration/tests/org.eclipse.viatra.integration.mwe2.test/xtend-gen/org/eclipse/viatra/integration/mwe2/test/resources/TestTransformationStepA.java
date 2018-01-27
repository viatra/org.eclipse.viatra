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
package org.eclipse.viatra.integration.mwe2.test.resources;

import java.util.List;
import java.util.concurrent.BlockingQueue;
import org.eclipse.emf.mwe2.runtime.workflow.IWorkflowContext;
import org.eclipse.viatra.integration.mwe2.IPublishTo;
import org.eclipse.viatra.integration.mwe2.mwe2impl.TransformationStep;
import org.eclipse.xtext.xbase.lib.Exceptions;
import org.eclipse.xtext.xbase.lib.IterableExtensions;
import org.eclipse.xtext.xbase.lib.Procedures.Procedure1;

@SuppressWarnings("all")
public class TestTransformationStepA extends TransformationStep {
  @Override
  public void dispose() {
  }
  
  @Override
  public void doExecute() {
    try {
      Object _get = this.context.get("TestOutput");
      final BlockingQueue<String> list = ((BlockingQueue<String>) _get);
      if ((list != null)) {
        list.put("exec_A");
      }
    } catch (Throwable _e) {
      throw Exceptions.sneakyThrow(_e);
    }
  }
  
  @Override
  public void publishMessages() {
    List<IPublishTo> _publishings = this.getPublishings();
    final Procedure1<IPublishTo> _function = new Procedure1<IPublishTo>() {
      @Override
      public void apply(final IPublishTo p) {
        String _topicName = p.getTopicName();
        String _plus = ("message_A" + _topicName);
        p.publishMessage(_plus);
      }
    };
    IterableExtensions.<IPublishTo>forEach(_publishings, _function);
  }
  
  @Override
  public void doInitialize(final IWorkflowContext ctx) {
  }
}
