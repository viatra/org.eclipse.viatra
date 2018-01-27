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
package org.eclipse.viatra.integration.evm.jdt.transactions;

import com.google.common.base.Objects;
import com.google.common.base.Optional;
import java.util.ArrayList;
import java.util.Set;
import org.apache.log4j.Level;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaElementDelta;
import org.eclipse.viatra.integration.evm.jdt.JDTEvent;
import org.eclipse.viatra.integration.evm.jdt.JDTEventAtom;
import org.eclipse.viatra.integration.evm.jdt.JDTEventSource;
import org.eclipse.viatra.integration.evm.jdt.JDTEventSourceSpecification;
import org.eclipse.viatra.integration.evm.jdt.JDTRealm;
import org.eclipse.viatra.integration.evm.jdt.transactions.JDTTransactionalEventType;
import org.eclipse.viatra.integration.evm.jdt.util.ChangeFlag;
import org.eclipse.viatra.integration.evm.jdt.util.JDTChangeFlagDecoder;
import org.eclipse.viatra.transformation.evm.api.event.EventHandler;
import org.eclipse.viatra.transformation.evm.api.event.EventSource;
import org.eclipse.xtend2.lib.StringConcatenation;
import org.eclipse.xtext.xbase.lib.CollectionLiterals;
import org.eclipse.xtext.xbase.lib.Conversions;
import org.eclipse.xtext.xbase.lib.Functions.Function1;
import org.eclipse.xtext.xbase.lib.IterableExtensions;
import org.eclipse.xtext.xbase.lib.Procedures.Procedure1;

@SuppressWarnings("all")
public class JDTTransactionalEventSource extends JDTEventSource implements EventSource<JDTEventAtom> {
  public JDTTransactionalEventSource(final JDTEventSourceSpecification spec, final JDTRealm realm) {
    super(spec, realm);
    this.logger.setLevel(Level.DEBUG);
  }
  
  @Override
  public void createEvent(final IJavaElementDelta delta) {
    IJavaElement _element = delta.getElement();
    if ((_element instanceof ICompilationUnit)) {
      final JDTEventAtom eventAtom = new JDTEventAtom(delta);
      final ArrayList<JDTTransactionalEventType> eventTypes = this.getTransactionalEventTypes(delta);
      final Procedure1<JDTTransactionalEventType> _function = new Procedure1<JDTTransactionalEventType>() {
        @Override
        public void apply(final JDTTransactionalEventType eventTpye) {
          final JDTEvent event = new JDTEvent(eventTpye, eventAtom);
          StringConcatenation _builder = new StringConcatenation();
          _builder.append("Created event with type ");
          _builder.append(eventTpye, "");
          _builder.append(" for ");
          Optional<? extends IJavaElementDelta> _delta = eventAtom.getDelta();
          _builder.append(_delta, "");
          JDTTransactionalEventSource.this.logger.debug(_builder);
          Set<EventHandler<JDTEventAtom>> _handlers = JDTTransactionalEventSource.this.getHandlers();
          final Procedure1<EventHandler<JDTEventAtom>> _function = new Procedure1<EventHandler<JDTEventAtom>>() {
            @Override
            public void apply(final EventHandler<JDTEventAtom> it) {
              it.handleEvent(event);
            }
          };
          IterableExtensions.<EventHandler<JDTEventAtom>>forEach(_handlers, _function);
        }
      };
      IterableExtensions.<JDTTransactionalEventType>forEach(eventTypes, _function);
    } else {
      this.createEventsForAppearedPackageContents(delta);
    }
    IJavaElementDelta[] _affectedChildren = delta.getAffectedChildren();
    final Procedure1<IJavaElementDelta> _function_1 = new Procedure1<IJavaElementDelta>() {
      @Override
      public void apply(final IJavaElementDelta affectedChildren) {
        JDTTransactionalEventSource.this.createEvent(affectedChildren);
      }
    };
    IterableExtensions.<IJavaElementDelta>forEach(((Iterable<IJavaElementDelta>)Conversions.doWrapArray(_affectedChildren)), _function_1);
  }
  
  @Override
  public void sendExistingEvents(final EventHandler<JDTEventAtom> handler, final IJavaElement element) {
    final JDTEventAtom eventAtom = new JDTEventAtom(element);
    final JDTEvent createEvent = new JDTEvent(JDTTransactionalEventType.CREATE, eventAtom);
    handler.handleEvent(createEvent);
    final JDTEvent commitEvent = new JDTEvent(JDTTransactionalEventType.COMMIT, eventAtom);
    handler.handleEvent(commitEvent);
  }
  
  private ArrayList<JDTTransactionalEventType> getTransactionalEventTypes(final IJavaElementDelta delta) {
    final ArrayList<JDTTransactionalEventType> result = CollectionLiterals.<JDTTransactionalEventType>newArrayList();
    int _flags = delta.getFlags();
    final Set<ChangeFlag> flags = JDTChangeFlagDecoder.toChangeFlags(_flags);
    int _kind = delta.getKind();
    int _bitwiseAnd = (_kind & IJavaElementDelta.REMOVED);
    boolean _notEquals = (_bitwiseAnd != 0);
    if (_notEquals) {
      result.add(JDTTransactionalEventType.DELETE);
    } else {
      int _kind_1 = delta.getKind();
      int _bitwiseAnd_1 = (_kind_1 & IJavaElementDelta.ADDED);
      boolean _notEquals_1 = (_bitwiseAnd_1 != 0);
      if (_notEquals_1) {
        result.add(JDTTransactionalEventType.CREATE);
        result.add(JDTTransactionalEventType.COMMIT);
      } else {
        final Function1<ChangeFlag, Boolean> _function = new Function1<ChangeFlag, Boolean>() {
          @Override
          public Boolean apply(final ChangeFlag flag) {
            boolean _or = false;
            boolean _equals = Objects.equal(flag, ChangeFlag.CONTENT);
            if (_equals) {
              _or = true;
            } else {
              boolean _equals_1 = Objects.equal(flag, ChangeFlag.CHILDREN);
              _or = _equals_1;
            }
            return Boolean.valueOf(_or);
          }
        };
        boolean _exists = IterableExtensions.<ChangeFlag>exists(flags, _function);
        if (_exists) {
          result.add(JDTTransactionalEventType.MODIFY);
        }
        final Function1<ChangeFlag, Boolean> _function_1 = new Function1<ChangeFlag, Boolean>() {
          @Override
          public Boolean apply(final ChangeFlag flag) {
            return Boolean.valueOf(Objects.equal(flag, ChangeFlag.PRIMARY_RESOURCE));
          }
        };
        boolean _exists_1 = IterableExtensions.<ChangeFlag>exists(flags, _function_1);
        if (_exists_1) {
          result.add(JDTTransactionalEventType.COMMIT);
        }
      }
    }
    return result;
  }
}
