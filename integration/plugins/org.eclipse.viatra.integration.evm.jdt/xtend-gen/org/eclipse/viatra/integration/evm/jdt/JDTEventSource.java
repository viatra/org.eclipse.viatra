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

import com.google.common.base.Objects;
import com.google.common.collect.Sets;
import java.util.List;
import java.util.Set;
import org.apache.log4j.Logger;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaElementDelta;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.viatra.integration.evm.jdt.CompositeEventFilter;
import org.eclipse.viatra.integration.evm.jdt.JDTEvent;
import org.eclipse.viatra.integration.evm.jdt.JDTEventAtom;
import org.eclipse.viatra.integration.evm.jdt.JDTEventFilter;
import org.eclipse.viatra.integration.evm.jdt.JDTEventSourceSpecification;
import org.eclipse.viatra.integration.evm.jdt.JDTRealm;
import org.eclipse.viatra.integration.evm.jdt.transactions.JDTTransactionalEventType;
import org.eclipse.viatra.integration.evm.jdt.util.JDTEventTypeDecoder;
import org.eclipse.viatra.transformation.evm.api.event.EventFilter;
import org.eclipse.viatra.transformation.evm.api.event.EventHandler;
import org.eclipse.viatra.transformation.evm.api.event.EventRealm;
import org.eclipse.viatra.transformation.evm.api.event.EventSource;
import org.eclipse.viatra.transformation.evm.api.event.EventSourceSpecification;
import org.eclipse.viatra.transformation.evm.specific.crud.CRUDEventTypeEnum;
import org.eclipse.xtend2.lib.StringConcatenation;
import org.eclipse.xtext.xbase.lib.Conversions;
import org.eclipse.xtext.xbase.lib.Exceptions;
import org.eclipse.xtext.xbase.lib.Extension;
import org.eclipse.xtext.xbase.lib.Functions.Function1;
import org.eclipse.xtext.xbase.lib.IterableExtensions;
import org.eclipse.xtext.xbase.lib.Procedures.Procedure1;

@SuppressWarnings("all")
public class JDTEventSource implements EventSource<JDTEventAtom> {
  private JDTEventSourceSpecification spec;
  
  private JDTRealm realm;
  
  private Set<EventHandler<JDTEventAtom>> handlers = Sets.<EventHandler<JDTEventAtom>>newHashSet();
  
  @Extension
  protected final Logger logger = Logger.getLogger(this.getClass());
  
  public JDTEventSource(final JDTEventSourceSpecification spec, final JDTRealm realm) {
    this.spec = spec;
    this.realm = realm;
    realm.addSource(this);
  }
  
  @Override
  public EventSourceSpecification<JDTEventAtom> getSourceSpecification() {
    return this.spec;
  }
  
  @Override
  public EventRealm getRealm() {
    return this.realm;
  }
  
  @Override
  public void dispose() {
    this.realm.removeSource(this);
  }
  
  public void createEvent(final IJavaElementDelta delta) {
    final JDTEventAtom eventAtom = new JDTEventAtom(delta);
    int _kind = delta.getKind();
    final CRUDEventTypeEnum eventType = JDTEventTypeDecoder.toEventType(_kind);
    final JDTEvent event = new JDTEvent(eventType, eventAtom);
    final Procedure1<EventHandler<JDTEventAtom>> _function = new Procedure1<EventHandler<JDTEventAtom>>() {
      @Override
      public void apply(final EventHandler<JDTEventAtom> it) {
        it.handleEvent(event);
      }
    };
    IterableExtensions.<EventHandler<JDTEventAtom>>forEach(this.handlers, _function);
    IJavaElementDelta[] _affectedChildren = delta.getAffectedChildren();
    final Procedure1<IJavaElementDelta> _function_1 = new Procedure1<IJavaElementDelta>() {
      @Override
      public void apply(final IJavaElementDelta affectedChildren) {
        JDTEventSource.this.createEvent(affectedChildren);
      }
    };
    IterableExtensions.<IJavaElementDelta>forEach(((Iterable<IJavaElementDelta>)Conversions.doWrapArray(_affectedChildren)), _function_1);
    this.createEventsForAppearedPackageContents(delta);
  }
  
  public void createEventsForAppearedPackageContents(final IJavaElementDelta delta) {
    int _kind = delta.getKind();
    final CRUDEventTypeEnum eventType = JDTEventTypeDecoder.toEventType(_kind);
    final IJavaElement element = delta.getElement();
    boolean _and = false;
    boolean _equals = Objects.equal(eventType, CRUDEventTypeEnum.CREATED);
    if (!_equals) {
      _and = false;
    } else {
      _and = (element instanceof IPackageFragment);
    }
    if (_and) {
      final Procedure1<EventHandler<JDTEventAtom>> _function = new Procedure1<EventHandler<JDTEventAtom>>() {
        @Override
        public void apply(final EventHandler<JDTEventAtom> handler) {
          try {
            ICompilationUnit[] _compilationUnits = ((IPackageFragment) element).getCompilationUnits();
            final Procedure1<ICompilationUnit> _function = new Procedure1<ICompilationUnit>() {
              @Override
              public void apply(final ICompilationUnit it) {
                JDTEventSource.this.sendExistingEvents(handler, it);
              }
            };
            IterableExtensions.<ICompilationUnit>forEach(((Iterable<ICompilationUnit>)Conversions.doWrapArray(_compilationUnits)), _function);
          } catch (Throwable _e) {
            throw Exceptions.sneakyThrow(_e);
          }
        }
      };
      IterableExtensions.<EventHandler<JDTEventAtom>>forEach(this.handlers, _function);
    }
  }
  
  public void createReferenceRefreshEvent(final IJavaElement javaElement) {
    final JDTEventAtom eventAtom = new JDTEventAtom(javaElement);
    final JDTEvent event = new JDTEvent(JDTTransactionalEventType.UPDATE_DEPENDENCY, eventAtom);
    StringConcatenation _builder = new StringConcatenation();
    _builder.append("Created event with type UPDATE_DEPENDENCY for ");
    _builder.append(eventAtom, "");
    this.logger.debug(_builder);
    final Procedure1<EventHandler<JDTEventAtom>> _function = new Procedure1<EventHandler<JDTEventAtom>>() {
      @Override
      public void apply(final EventHandler<JDTEventAtom> it) {
        it.handleEvent(event);
      }
    };
    IterableExtensions.<EventHandler<JDTEventAtom>>forEach(this.handlers, _function);
  }
  
  public void addHandler(final EventHandler<JDTEventAtom> handler) {
    try {
      final EventFilter<? super JDTEventAtom> filter = handler.getEventFilter();
      final IJavaProject project = this.getJavaProject(filter);
      IPackageFragment[] _packageFragments = project.getPackageFragments();
      final Function1<IPackageFragment, Boolean> _function = new Function1<IPackageFragment, Boolean>() {
        @Override
        public Boolean apply(final IPackageFragment it) {
          try {
            boolean _and = false;
            int _kind = it.getKind();
            boolean _equals = (_kind == IPackageFragmentRoot.K_SOURCE);
            if (!_equals) {
              _and = false;
            } else {
              String _elementName = it.getElementName();
              boolean _isEmpty = _elementName.isEmpty();
              boolean _not = (!_isEmpty);
              _and = _not;
            }
            return Boolean.valueOf(_and);
          } catch (Throwable _e) {
            throw Exceptions.sneakyThrow(_e);
          }
        }
      };
      Iterable<IPackageFragment> _filter = IterableExtensions.<IPackageFragment>filter(((Iterable<IPackageFragment>)Conversions.doWrapArray(_packageFragments)), _function);
      final List<IPackageFragment> pckgfr = IterableExtensions.<IPackageFragment>toList(_filter);
      final Procedure1<IPackageFragment> _function_1 = new Procedure1<IPackageFragment>() {
        @Override
        public void apply(final IPackageFragment it) {
          try {
            JDTEventSource.this.sendExistingEvents(handler, it);
            ICompilationUnit[] _compilationUnits = it.getCompilationUnits();
            final Procedure1<ICompilationUnit> _function = new Procedure1<ICompilationUnit>() {
              @Override
              public void apply(final ICompilationUnit it) {
                JDTEventSource.this.sendExistingEvents(handler, it);
              }
            };
            IterableExtensions.<ICompilationUnit>forEach(((Iterable<ICompilationUnit>)Conversions.doWrapArray(_compilationUnits)), _function);
          } catch (Throwable _e) {
            throw Exceptions.sneakyThrow(_e);
          }
        }
      };
      IterableExtensions.<IPackageFragment>forEach(pckgfr, _function_1);
      this.handlers.add(handler);
    } catch (Throwable _e) {
      throw Exceptions.sneakyThrow(_e);
    }
  }
  
  public boolean removeHandler(final EventHandler<JDTEventAtom> handler) {
    return this.handlers.remove(handler);
  }
  
  public void sendExistingEvents(final EventHandler<JDTEventAtom> handler, final IJavaElement element) {
    final JDTEventAtom eventAtom = new JDTEventAtom(element);
    final JDTEvent createEvent = new JDTEvent(CRUDEventTypeEnum.CREATED, eventAtom);
    handler.handleEvent(createEvent);
  }
  
  public IJavaProject getJavaProject(final EventFilter<? super JDTEventAtom> filter) {
    if ((filter instanceof JDTEventFilter)) {
      return ((JDTEventFilter)filter).getProject();
    } else {
      if ((filter instanceof CompositeEventFilter)) {
        EventFilter _innerFilter = ((CompositeEventFilter)filter).getInnerFilter();
        return this.getJavaProject(_innerFilter);
      }
    }
    return null;
  }
  
  public Set<EventHandler<JDTEventAtom>> getHandlers() {
    return this.handlers;
  }
}
