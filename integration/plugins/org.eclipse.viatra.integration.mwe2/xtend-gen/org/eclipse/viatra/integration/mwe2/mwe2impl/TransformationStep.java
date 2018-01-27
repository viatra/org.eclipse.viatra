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
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ListMultimap;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.eclipse.emf.mwe2.runtime.workflow.IWorkflowContext;
import org.eclipse.viatra.integration.mwe2.IMessage;
import org.eclipse.viatra.integration.mwe2.IMessageProcessor;
import org.eclipse.viatra.integration.mwe2.IPublishTo;
import org.eclipse.viatra.integration.mwe2.ISubscribeTo;
import org.eclipse.viatra.integration.mwe2.ITransformationStep;
import org.eclipse.viatra.integration.mwe2.mwe2impl.MessageBroker;
import org.eclipse.viatra.integration.mwe2.mwe2impl.exceptions.NoSuchTopicNameException;
import org.eclipse.xtext.xbase.lib.Extension;
import org.eclipse.xtext.xbase.lib.Functions.Function1;
import org.eclipse.xtext.xbase.lib.IterableExtensions;
import org.eclipse.xtext.xbase.lib.Procedures.Procedure1;

/**
 * The TransformationStep abstract class implements the ITransformationStep and adds further
 * basic functions that are typically present in case of (VIATRA based) model transformation steps.
 * 
 * These include the following:
 * 		1. Support for containing ISubscribeTo and IPublishTo objects
 * 		2. Process all incoming messages
 * 		3. Support the addition of user defined execute functionality and message publication
 * 
 * @author Peter Lunk
 */
@SuppressWarnings("all")
public abstract class TransformationStep implements ITransformationStep {
  /**
   * Broker used to manage Topic subscriptions
   */
  @Extension
  private MessageBroker broker = MessageBroker.getInstance();
  
  protected ListMultimap<Integer, ISubscribeTo> subscribeTo = ArrayListMultimap.<Integer, ISubscribeTo>create();
  
  protected List<IPublishTo> publishTo = new ArrayList<IPublishTo>();
  
  protected IWorkflowContext context;
  
  public IWorkflowContext getContext() {
    return this.context;
  }
  
  public void addSubscription(final ISubscribeTo sub) {
    int _priority = sub.getPriority();
    this.subscribeTo.put(Integer.valueOf(_priority), sub);
    sub.setParent(this);
    IMessageProcessor<?, ? extends IMessage<?>> _processor = sub.getProcessor();
    _processor.setParent(this);
    String _topicName = sub.getTopicName();
    this.broker.subscribeTo(_topicName, this);
  }
  
  public List<ISubscribeTo> getSubscriptions(final Integer priority) {
    return this.subscribeTo.get(priority);
  }
  
  public List<ISubscribeTo> getSubscriptions() {
    final ArrayList<ISubscribeTo> ret = new ArrayList<ISubscribeTo>();
    Collection<ISubscribeTo> _values = this.subscribeTo.values();
    ret.addAll(_values);
    return ret;
  }
  
  public void addPublishing(final IPublishTo channel) {
    this.publishTo.add(channel);
  }
  
  public List<IPublishTo> getPublishings() {
    return this.publishTo;
  }
  
  public ISubscribeTo getSubscription(final String topicName) throws NoSuchTopicNameException {
    List<ISubscribeTo> _subscriptions = this.getSubscriptions();
    final Function1<ISubscribeTo, Boolean> _function = new Function1<ISubscribeTo, Boolean>() {
      @Override
      public Boolean apply(final ISubscribeTo it) {
        String _topicName = it.getTopicName();
        return Boolean.valueOf(Objects.equal(_topicName, topicName));
      }
    };
    final Iterable<ISubscribeTo> subs = IterableExtensions.<ISubscribeTo>filter(_subscriptions, _function);
    boolean _isEmpty = IterableExtensions.isEmpty(subs);
    boolean _not = (!_isEmpty);
    if (_not) {
      return IterableExtensions.<ISubscribeTo>head(subs);
    }
    throw new NoSuchTopicNameException();
  }
  
  public IPublishTo getPublishing(final String topicName) throws NoSuchTopicNameException {
    final Function1<IPublishTo, Boolean> _function = new Function1<IPublishTo, Boolean>() {
      @Override
      public Boolean apply(final IPublishTo it) {
        String _topicName = it.getTopicName();
        return Boolean.valueOf(Objects.equal(_topicName, topicName));
      }
    };
    final Iterable<IPublishTo> pubs = IterableExtensions.<IPublishTo>filter(this.publishTo, _function);
    boolean _isEmpty = IterableExtensions.isEmpty(pubs);
    boolean _not = (!_isEmpty);
    if (_not) {
      return IterableExtensions.<IPublishTo>head(pubs);
    }
    throw new NoSuchTopicNameException();
  }
  
  @Override
  public void execute() {
    this.processMessages();
    this.doExecute();
    this.publishMessages();
  }
  
  public void processMessages() {
    List<ISubscribeTo> _subscriptions = this.getSubscriptions();
    final Procedure1<ISubscribeTo> _function = new Procedure1<ISubscribeTo>() {
      @Override
      public void apply(final ISubscribeTo it) {
        it.processMessages();
      }
    };
    IterableExtensions.<ISubscribeTo>forEach(_subscriptions, _function);
  }
  
  public void publishMessages() {
  }
  
  public abstract void doExecute();
  
  @Override
  public void initialize(final IWorkflowContext ctx) {
    this.context = ctx;
    this.doInitialize(ctx);
  }
  
  public abstract void doInitialize(final IWorkflowContext ctx);
}
