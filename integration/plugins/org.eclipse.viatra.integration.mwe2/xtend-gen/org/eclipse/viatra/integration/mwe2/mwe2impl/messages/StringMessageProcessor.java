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
package org.eclipse.viatra.integration.mwe2.mwe2impl.messages;

import java.security.InvalidParameterException;
import org.eclipse.viatra.integration.mwe2.IMessage;
import org.eclipse.viatra.integration.mwe2.IMessageProcessor;
import org.eclipse.viatra.integration.mwe2.ITransformationStep;
import org.eclipse.viatra.integration.mwe2.mwe2impl.exceptions.InvalidParameterTypeException;
import org.eclipse.viatra.integration.mwe2.mwe2impl.messages.StringMessage;

/**
 * Message Processor that is responsible for processing StringMessage objects.
 * It serves as an example for user defined message processors.
 * @author Peter Lunk
 */
@SuppressWarnings("all")
public class StringMessageProcessor implements IMessageProcessor<String, StringMessage> {
  /**
   * Similar to every typical IMessageProcessor, this class also has a reference to its parent transformation step
   * This way it can hand its result to the transformation step.
   */
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
    if ((message instanceof StringMessage)) {
    } else {
      throw new InvalidParameterException();
    }
  }
}
