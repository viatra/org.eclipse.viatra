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
import org.eclipse.viatra.integration.mwe2.IMessage;
import org.eclipse.viatra.integration.mwe2.IMessageProcessor;
import org.eclipse.viatra.integration.mwe2.ISubscribeTo;
import org.eclipse.viatra.integration.mwe2.ITransformationStep;
import org.eclipse.viatra.integration.mwe2.mwe2impl.MessageBroker;
import org.eclipse.viatra.integration.mwe2.mwe2impl.exceptions.InvalidParameterTypeException;
import org.eclipse.xtext.xbase.lib.Exceptions;
import org.eclipse.xtext.xbase.lib.Extension;
import org.eclipse.xtext.xbase.lib.IterableExtensions;
import org.eclipse.xtext.xbase.lib.Procedures.Procedure1;

/**
 * SubscribeTo objects provide an interface for receiving messages from a given topic and processing them in a single call,
 * via binding a Topic and a MessageProcessor together
 * 
 * @author Peter Lunk
 */
@SuppressWarnings("all")
public class SubscribeTo implements ISubscribeTo {
  @Extension
  private MessageBroker broker = MessageBroker.getInstance();
  
  private int priority = 0;
  
  private ITransformationStep parent;
  
  private String topicName;
  
  private IMessageProcessor<?, ? extends IMessage<?>> processor;
  
  @Override
  public ITransformationStep getParent() {
    return this.parent;
  }
  
  @Override
  public IMessageProcessor<?, ? extends IMessage<?>> getProcessor() {
    return this.processor;
  }
  
  @Override
  public String getTopicName() {
    return this.topicName;
  }
  
  /**
   * Process all unprocessed messages contained by the topic specified in the topicName property
   */
  @Override
  public void processMessages() {
    final List<IMessage<?>> messages = this.broker.getMessages(this.topicName, this.parent);
    final ArrayList<IMessage<?>> msgToRemove = Lists.<IMessage<?>>newArrayList();
    final Procedure1<IMessage<?>> _function = new Procedure1<IMessage<?>>() {
      @Override
      public void apply(final IMessage<?> m) {
        try {
          SubscribeTo.this.processor.processMessage(m);
          msgToRemove.add(m);
        } catch (final Throwable _t) {
          if (_t instanceof InvalidParameterTypeException) {
            final InvalidParameterTypeException e = (InvalidParameterTypeException)_t;
            e.printStackTrace();
          } else {
            throw Exceptions.sneakyThrow(_t);
          }
        }
      }
    };
    IterableExtensions.<IMessage<?>>forEach(messages, _function);
    final Procedure1<IMessage<?>> _function_1 = new Procedure1<IMessage<?>>() {
      @Override
      public void apply(final IMessage<?> m) {
        SubscribeTo.this.broker.removeMessage(SubscribeTo.this.topicName, SubscribeTo.this.parent, m);
      }
    };
    IterableExtensions.<IMessage<?>>forEach(msgToRemove, _function_1);
  }
  
  @Override
  public void setParent(final ITransformationStep parent) {
    this.parent = parent;
  }
  
  @Override
  public void setProcessor(final IMessageProcessor<?, ? extends IMessage<?>> processor) {
    this.processor = processor;
  }
  
  @Override
  public void setTopicName(final String name) {
    this.topicName = name;
  }
  
  @Override
  public int getPriority() {
    return this.priority;
  }
  
  @Override
  public void setPriority(final String priority) {
    this.setPriority(priority);
  }
}
