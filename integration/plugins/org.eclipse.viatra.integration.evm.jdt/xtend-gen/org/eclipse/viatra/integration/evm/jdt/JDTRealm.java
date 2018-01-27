/**
 * Copyright (c) 2015-2016, IncQuery Labs Ltd., Ericsson AB, CEA LIST
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *   Abel Hegedus, Daniel Segesdi, Robert Doczi, Zoltan Ujhelyi - initial API and implementation
 */
package org.eclipse.viatra.integration.evm.jdt;

import com.google.common.base.Joiner;
import com.google.common.collect.Sets;
import java.util.Set;
import org.eclipse.jdt.core.ElementChangedEvent;
import org.eclipse.jdt.core.IElementChangedListener;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaElementDelta;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.viatra.integration.evm.jdt.JDTEventSource;
import org.eclipse.viatra.integration.evm.jdt.JDTUpdateCompleteProvider;
import org.eclipse.viatra.integration.evm.jdt.util.ElementChangedEventType;
import org.eclipse.viatra.integration.evm.jdt.util.JDTElementChangedEventTypeDecoder;
import org.eclipse.viatra.transformation.evm.api.event.EventRealm;
import org.eclipse.viatra.transformation.evm.specific.scheduler.UpdateCompleteBasedScheduler;
import org.eclipse.xtend.lib.annotations.AccessorType;
import org.eclipse.xtend.lib.annotations.Accessors;
import org.eclipse.xtext.xbase.lib.IterableExtensions;
import org.eclipse.xtext.xbase.lib.Procedures.Procedure1;
import org.eclipse.xtext.xbase.lib.Pure;

@SuppressWarnings("all")
public class JDTRealm implements EventRealm {
  private Set<JDTEventSource> sources = Sets.<JDTEventSource>newHashSet();
  
  private IElementChangedListener listener;
  
  private JDTUpdateCompleteProvider provider;
  
  private UpdateCompleteBasedScheduler.UpdateCompleteBasedSchedulerFactory schedulerFactory;
  
  @Accessors(AccessorType.PROTECTED_GETTER)
  private boolean active = false;
  
  private static JDTRealm instance = null;
  
  /**
   * Constructor hidden for singleton class.
   */
  protected JDTRealm() {
    final IElementChangedListener _function = new IElementChangedListener() {
      @Override
      public void elementChanged(final ElementChangedEvent event) {
        final IJavaElementDelta delta = event.getDelta();
        int _type = event.getType();
        final Set<ElementChangedEventType> types = JDTElementChangedEventTypeDecoder.toEventTypes(_type);
        Joiner _on = Joiner.on(",");
        final String typeString = _on.join(types);
        boolean _contains = types.contains(ElementChangedEventType.POST_CHANGE);
        if (_contains) {
          JDTRealm.this.notifySources(delta);
        }
      }
    };
    this.listener = _function;
    JDTUpdateCompleteProvider _jDTUpdateCompleteProvider = new JDTUpdateCompleteProvider();
    this.provider = _jDTUpdateCompleteProvider;
    UpdateCompleteBasedScheduler.UpdateCompleteBasedSchedulerFactory _updateCompleteBasedSchedulerFactory = new UpdateCompleteBasedScheduler.UpdateCompleteBasedSchedulerFactory(this.provider);
    this.schedulerFactory = _updateCompleteBasedSchedulerFactory;
  }
  
  public static JDTRealm getInstance() {
    if ((JDTRealm.instance == null)) {
      JDTRealm _jDTRealm = new JDTRealm();
      JDTRealm.instance = _jDTRealm;
    }
    return JDTRealm.instance;
  }
  
  /**
   * All events JDT sends on various threads should be handled synchronously.
   */
  protected synchronized void notifySources(final IJavaElement javaElement) {
    final Procedure1<JDTEventSource> _function = new Procedure1<JDTEventSource>() {
      @Override
      public void apply(final JDTEventSource it) {
        it.createReferenceRefreshEvent(javaElement);
      }
    };
    IterableExtensions.<JDTEventSource>forEach(this.sources, _function);
  }
  
  /**
   * All events JDT sends on various threads should be handled synchronously.
   */
  private synchronized void notifySources(final IJavaElementDelta delta) {
    final Procedure1<JDTEventSource> _function = new Procedure1<JDTEventSource>() {
      @Override
      public void apply(final JDTEventSource it) {
        it.createEvent(delta);
      }
    };
    IterableExtensions.<JDTEventSource>forEach(this.sources, _function);
  }
  
  protected void addSource(final JDTEventSource source) {
    boolean _isEmpty = this.sources.isEmpty();
    if (_isEmpty) {
      JavaCore.addElementChangedListener(this.listener);
      this.active = true;
    }
    this.sources.add(source);
  }
  
  protected void removeSource(final JDTEventSource source) {
    this.sources.remove(source);
  }
  
  protected void buildFinishedOnProject(final IJavaProject project) {
    this.provider.updateCompleted();
  }
  
  public UpdateCompleteBasedScheduler.UpdateCompleteBasedSchedulerFactory getJDTBuilderSchedulerFactory() {
    return this.schedulerFactory;
  }
  
  @Pure
  protected boolean isActive() {
    return this.active;
  }
}
