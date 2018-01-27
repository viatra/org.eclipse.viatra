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

import com.google.common.base.Objects;
import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.List;
import org.eclipse.viatra.integration.mwe2.IMessage;
import org.eclipse.viatra.integration.mwe2.IMessageBroker;
import org.eclipse.viatra.integration.mwe2.ITopic;
import org.eclipse.viatra.integration.mwe2.ITransformationStep;
import org.eclipse.viatra.integration.mwe2.mwe2impl.Topic;
import org.eclipse.xtext.xbase.lib.CollectionExtensions;
import org.eclipse.xtext.xbase.lib.Functions.Function1;
import org.eclipse.xtext.xbase.lib.IterableExtensions;

/**
 * The MessageBroker is a singleton class that manages topics and subscriptions.
 * 
 * @author Peter Lunk
 */
@SuppressWarnings("all")
public class MessageBroker implements IMessageBroker {
  protected static MessageBroker broker;
  
  protected List<ITopic> topics;
  
  protected MessageBroker() {
    ArrayList<ITopic> _newArrayList = Lists.<ITopic>newArrayList();
    this.topics = _newArrayList;
  }
  
  public static MessageBroker getInstance() {
    if ((MessageBroker.broker == null)) {
      MessageBroker _messageBroker = new MessageBroker();
      MessageBroker.broker = _messageBroker;
    }
    return MessageBroker.broker;
  }
  
  @Override
  public void subscribeTo(final String topicName, final ITransformationStep step) {
    ITopic _topic = this.getTopic(topicName);
    _topic.addSubscriber(step);
  }
  
  @Override
  public void sendMessage(final String topicName, final IMessage<?> message) {
    final ITopic topic = this.getTopic(topicName);
    topic.addMessage(message);
  }
  
  /**
   * Returns all messages sent to a topic that have not been processed by the specified transformation step.
   */
  @Override
  public List<IMessage<?>> getMessages(final String topicName, final ITransformationStep step) {
    List<IMessage<?>> _xblockexpression = null;
    {
      final ITopic topic = this.getTopic(topicName);
      _xblockexpression = topic.getMessages(step);
    }
    return _xblockexpression;
  }
  
  /**
   * Removes the specified message from the specified topic and the specified subscriber queue.
   * It can be used to inform the broker and the topic that the given transformation step has
   * finished processing the specified message, and therefore it is no longer needed.
   */
  @Override
  public void removeMessage(final String topicName, final ITransformationStep step, final IMessage<?> message) {
    final ITopic topic = this.getTopic(topicName);
    topic.removeMessage(message, step);
  }
  
  protected ITopic getTopic(final String topicName) {
    ITopic retVal = null;
    final Function1<ITopic, Boolean> _function = new Function1<ITopic, Boolean>() {
      @Override
      public Boolean apply(final ITopic it) {
        String _name = it.getName();
        return Boolean.valueOf(Objects.equal(_name, topicName));
      }
    };
    final Iterable<ITopic> reducedList = IterableExtensions.<ITopic>filter(this.topics, _function);
    boolean _isEmpty = IterableExtensions.isEmpty(reducedList);
    if (_isEmpty) {
      Topic _topic = new Topic(topicName);
      retVal = _topic;
      this.topics.add(retVal);
    } else {
      ITopic _head = IterableExtensions.<ITopic>head(reducedList);
      retVal = _head;
    }
    return retVal;
  }
  
  protected void removeTopic(final String topicName) {
    final Function1<ITopic, Boolean> _function = new Function1<ITopic, Boolean>() {
      @Override
      public Boolean apply(final ITopic it) {
        String _name = it.getName();
        return Boolean.valueOf(Objects.equal(_name, topicName));
      }
    };
    final Iterable<ITopic> reducedList = IterableExtensions.<ITopic>filter(this.topics, _function);
    boolean _isEmpty = IterableExtensions.isEmpty(reducedList);
    boolean _not = (!_isEmpty);
    if (_not) {
      CollectionExtensions.<ITopic>removeAll(this.topics, reducedList);
    }
  }
}
