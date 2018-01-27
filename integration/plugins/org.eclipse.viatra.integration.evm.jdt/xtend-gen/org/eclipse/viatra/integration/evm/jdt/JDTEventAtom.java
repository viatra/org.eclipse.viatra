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
import com.google.common.base.Optional;
import com.google.common.collect.Lists;
import java.util.Collections;
import java.util.Deque;
import java.util.LinkedList;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaElementDelta;
import org.eclipse.xtend.lib.annotations.Accessors;
import org.eclipse.xtext.xbase.lib.CollectionLiterals;
import org.eclipse.xtext.xbase.lib.Pure;

@SuppressWarnings("all")
public class JDTEventAtom {
  @Accessors
  private final IJavaElement element;
  
  @Accessors
  private Optional<? extends IJavaElementDelta> delta;
  
  @Accessors
  private final Deque<IJavaElementDelta> unprocessedDeltas;
  
  public JDTEventAtom(final IJavaElementDelta delta) {
    Optional<IJavaElementDelta> _of = Optional.<IJavaElementDelta>of(delta);
    this.delta = _of;
    IJavaElement _element = delta.getElement();
    this.element = _element;
    LinkedList<IJavaElementDelta> _newLinkedList = Lists.<IJavaElementDelta>newLinkedList(Collections.<IJavaElementDelta>unmodifiableSet(CollectionLiterals.<IJavaElementDelta>newHashSet(delta)));
    this.unprocessedDeltas = _newLinkedList;
  }
  
  public JDTEventAtom(final IJavaElement javaElement) {
    Optional<IJavaElementDelta> _absent = Optional.<IJavaElementDelta>absent();
    this.delta = _absent;
    this.element = javaElement;
    LinkedList<IJavaElementDelta> _newLinkedList = Lists.<IJavaElementDelta>newLinkedList();
    this.unprocessedDeltas = _newLinkedList;
  }
  
  @Override
  public boolean equals(final Object obj) {
    if ((obj instanceof JDTEventAtom)) {
      return Objects.equal(this.element, ((JDTEventAtom)obj).element);
    }
    return false;
  }
  
  @Override
  public String toString() {
    String _string = this.element.toString();
    String _plus = (_string + " : ");
    String _string_1 = this.delta.toString();
    return (_plus + _string_1);
  }
  
  @Pure
  public IJavaElement getElement() {
    return this.element;
  }
  
  @Pure
  public Optional<? extends IJavaElementDelta> getDelta() {
    return this.delta;
  }
  
  public void setDelta(final Optional<? extends IJavaElementDelta> delta) {
    this.delta = delta;
  }
  
  @Pure
  public Deque<IJavaElementDelta> getUnprocessedDeltas() {
    return this.unprocessedDeltas;
  }
}
