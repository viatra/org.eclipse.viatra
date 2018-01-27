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

import java.util.concurrent.BlockingQueue;
import org.eclipse.emf.mwe2.runtime.workflow.IWorkflowContext;
import org.eclipse.viatra.integration.mwe2.IMessage;
import org.eclipse.viatra.integration.mwe2.IMessageProcessor;
import org.eclipse.viatra.integration.mwe2.ITransformationStep;
import org.eclipse.viatra.integration.mwe2.mwe2impl.TransformationStep;
import org.eclipse.viatra.integration.mwe2.mwe2impl.exceptions.InvalidParameterTypeException;
import org.eclipse.viatra.integration.mwe2.mwe2impl.messages.StringMessage;
import org.eclipse.xtext.xbase.lib.Exceptions;

@SuppressWarnings("all")
public class TestMessageProcessor implements IMessageProcessor<String, StringMessage> {
  protected ITransformationStep parent;
  
  @Override
  public ITransformationStep getParent() {
    return this.parent;
  }
  
  @Override
  public void setParent(final ITransformationStep parent) {
    this.parent = parent;
  }
  
  @Override
  public void processMessage(final IMessage<?> message) throws InvalidParameterTypeException {
    try {
      if ((message instanceof StringMessage)) {
        final TransformationStep castparent = ((TransformationStep) this.parent);
        IWorkflowContext _context = castparent.getContext();
        Object _get = _context.get("TestOutput");
        final BlockingQueue<String> list = ((BlockingQueue<String>) _get);
        if ((list != null)) {
          String _parameter = ((StringMessage)message).getParameter();
          list.put(_parameter);
        }
      } else {
        throw new InvalidParameterTypeException();
      }
    } catch (Throwable _e) {
      throw Exceptions.sneakyThrow(_e);
    }
  }
}
