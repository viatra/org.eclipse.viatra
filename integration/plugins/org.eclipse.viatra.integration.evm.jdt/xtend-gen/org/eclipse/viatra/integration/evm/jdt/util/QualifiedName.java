/**
 * Copyright (c) 2015-2016, IncQuery Labs Ltd. and Ericsson AB
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *   Abel Hegedus, Daniel Segesdi, Robert Doczi, Zoltan Ujhelyi - initial API and implementation
 */
package org.eclipse.viatra.integration.evm.jdt.util;

import com.google.common.base.Optional;
import java.util.Iterator;

@SuppressWarnings("all")
public abstract class QualifiedName implements Iterable<String> {
  private static class QualifiedNameIterator implements Iterator<String> {
    private QualifiedName current;
    
    public QualifiedNameIterator(final QualifiedName current) {
      this.current = current;
    }
    
    @Override
    public boolean hasNext() {
      return (this.current != null);
    }
    
    @Override
    public String next() {
      final String name = this.current.name;
      QualifiedName _orNull = this.current.parent.orNull();
      this.current = _orNull;
      return name;
    }
    
    /**
     * Not supported by this iterator!
     * @noreference
     */
    @Override
    public void remove() {
      throw new UnsupportedOperationException("Qualified Name Iterator does not support removal");
    }
  }
  
  protected final String name;
  
  protected final Optional<? extends QualifiedName> parent;
  
  protected QualifiedName(final String qualifiedName, final QualifiedName parent) {
    this.name = qualifiedName;
    Optional<QualifiedName> _fromNullable = Optional.<QualifiedName>fromNullable(parent);
    this.parent = _fromNullable;
  }
  
  public String getName() {
    return this.name;
  }
  
  public Optional<? extends QualifiedName> getParent() {
    return this.parent;
  }
  
  @Override
  public Iterator<String> iterator() {
    return new QualifiedName.QualifiedNameIterator(this);
  }
  
  @Override
  public String toString() {
    final StringBuilder builder = new StringBuilder();
    boolean _isPresent = this.parent.isPresent();
    if (_isPresent) {
      QualifiedName _get = this.parent.get();
      String _string = _get.toString();
      StringBuilder _append = builder.append(_string);
      String _separator = this.getSeparator();
      _append.append(_separator);
    }
    StringBuilder _append_1 = builder.append(this.name);
    return _append_1.toString();
  }
  
  public abstract String getSeparator();
  
  public abstract QualifiedName dropRoot();
  
  @Override
  public boolean equals(final Object obj) {
    if ((obj instanceof QualifiedName)) {
      String _string = this.toString();
      String _string_1 = ((QualifiedName)obj).toString();
      return _string.equals(_string_1);
    }
    return false;
  }
  
  @Override
  public int hashCode() {
    String _string = this.toString();
    return _string.hashCode();
  }
}
