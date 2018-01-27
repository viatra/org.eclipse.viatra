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

import org.eclipse.viatra.integration.mwe2.IMessage;
import org.eclipse.viatra.integration.mwe2.IMessageFactory;
import org.eclipse.viatra.integration.mwe2.IPublishTo;
import org.eclipse.viatra.integration.mwe2.mwe2impl.MessageBroker;
import org.eclipse.xtext.xbase.lib.Exceptions;
import org.eclipse.xtext.xbase.lib.Extension;

/**
 * SubscribeTo objects provide an interface for creating messages and sending them to a given topic in a single call,
 * via binding a Topic and a MessageFactory together
 * 
 * @author Peter Lunk
 */
@SuppressWarnings("all")
public class PublishTo implements IPublishTo {
  @Extension
  private MessageBroker broker = MessageBroker.getInstance();
  
  private String topicName;
  
  private IMessageFactory<?, ? extends IMessage<?>> factory;
  
  @Override
  public IMessageFactory<?, ? extends IMessage<?>> getFactory() {
    return this.factory;
  }
  
  @Override
  public String getTopicName() {
    return this.topicName;
  }
  
  /**
   * Uses the factory to create a new message instance and sends it to the Topic specified by the topicName attribute.
   */
  @Override
  public void publishMessage(final Object parameter) {
    try {
      boolean _isValidParameter = this.factory.isValidParameter(parameter);
      if (_isValidParameter) {
        try {
          final IMessage<?> message = this.factory.createMessage(parameter);
          this.broker.sendMessage(this.topicName, message);
        } catch (final Throwable _t) {
          if (_t instanceof InterruptedException) {
            final InterruptedException e = (InterruptedException)_t;
            e.printStackTrace();
          } else {
            throw Exceptions.sneakyThrow(_t);
          }
        }
      }
    } catch (Throwable _e) {
      throw Exceptions.sneakyThrow(_e);
    }
  }
  
  @Override
  public void setFactory(final IMessageFactory<?, ? extends IMessage<?>> factory) {
    this.factory = factory;
  }
  
  @Override
  public void setTopicName(final String name) {
    this.topicName = name;
  }
}
