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
import com.google.common.collect.Maps;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.eclipse.viatra.integration.mwe2.IMessage;
import org.eclipse.viatra.integration.mwe2.ITopic;
import org.eclipse.viatra.integration.mwe2.ITransformationStep;
import org.eclipse.xtext.xbase.lib.IterableExtensions;
import org.eclipse.xtext.xbase.lib.Procedures.Procedure1;

/**
 * Topics contain subscribing transformation steps and messages sent to these subscribers.
 *  Supports the addition of new subscribers and messages. To remove processed messages,
 *  transformation steps have to call the removeMessage method, this way ensuring that no message
 *  gets lost due a failure during processing
 * 
 *  This implementation can be used with most of the workflows that can be described using this library.
 * 
 * @author Peter Lunk
 */
@SuppressWarnings("all")
public class Topic implements ITopic {
  protected String name;
  
  protected Map<ITransformationStep, List<IMessage<?>>> subscriberMap;
  
  public Topic(final String name) {
    this.name = name;
    HashMap<ITransformationStep, List<IMessage<?>>> _newHashMap = Maps.<ITransformationStep, List<IMessage<?>>>newHashMap();
    this.subscriberMap = _newHashMap;
  }
  
  @Override
  public void addMessage(final IMessage<?> message) {
    Set<ITransformationStep> _keySet = this.subscriberMap.keySet();
    final Procedure1<ITransformationStep> _function = new Procedure1<ITransformationStep>() {
      @Override
      public void apply(final ITransformationStep k) {
        List<IMessage<?>> _get = Topic.this.subscriberMap.get(k);
        _get.add(message);
      }
    };
    IterableExtensions.<ITransformationStep>forEach(_keySet, _function);
  }
  
  @Override
  public void addSubscriber(final ITransformationStep subscriber) {
    ArrayList<IMessage<?>> _newArrayList = Lists.<IMessage<?>>newArrayList();
    this.subscriberMap.put(subscriber, _newArrayList);
  }
  
  @Override
  public List<IMessage<?>> getMessages(final ITransformationStep sub) {
    return this.subscriberMap.get(sub);
  }
  
  @Override
  public String getName() {
    return this.name;
  }
  
  @Override
  public List<ITransformationStep> getSubscribers() {
    Set<ITransformationStep> _keySet = this.subscriberMap.keySet();
    return IterableExtensions.<ITransformationStep>toList(_keySet);
  }
  
  @Override
  public void setName(final String name) {
    this.name = name;
  }
  
  @Override
  public void removeMessage(final IMessage<?> message) {
    Set<ITransformationStep> _keySet = this.subscriberMap.keySet();
    final Procedure1<ITransformationStep> _function = new Procedure1<ITransformationStep>() {
      @Override
      public void apply(final ITransformationStep k) {
        List<IMessage<?>> _get = Topic.this.subscriberMap.get(k);
        _get.remove(message);
      }
    };
    IterableExtensions.<ITransformationStep>forEach(_keySet, _function);
  }
  
  /**
   * Removes the message from the specified step. This is used to inform
   * the topic that the message processing was successful and the message
   * is no longer needed.
   */
  @Override
  public void removeMessage(final IMessage<?> message, final ITransformationStep sub) {
    List<IMessage<?>> _get = this.subscriberMap.get(sub);
    _get.remove(message);
  }
}
