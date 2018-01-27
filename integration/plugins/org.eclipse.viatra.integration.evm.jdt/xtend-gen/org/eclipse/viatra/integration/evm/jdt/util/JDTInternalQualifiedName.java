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

import java.util.List;
import org.eclipse.viatra.integration.evm.jdt.util.QualifiedName;
import org.eclipse.xtext.xbase.lib.Conversions;
import org.eclipse.xtext.xbase.lib.Functions.Function1;
import org.eclipse.xtext.xbase.lib.Functions.Function2;
import org.eclipse.xtext.xbase.lib.IterableExtensions;
import org.eclipse.xtext.xbase.lib.ListExtensions;

@SuppressWarnings("all")
public class JDTInternalQualifiedName extends QualifiedName {
  private final static String JDT_INTERNAL_SEPARATOR = "/";
  
  public static QualifiedName create(final String qualifiedName) {
    final int lastIndexOfSeparator = qualifiedName.lastIndexOf(JDTInternalQualifiedName.JDT_INTERNAL_SEPARATOR);
    if ((lastIndexOfSeparator == (-1))) {
      return new JDTInternalQualifiedName(qualifiedName, null);
    } else {
      int _length = JDTInternalQualifiedName.JDT_INTERNAL_SEPARATOR.length();
      int _plus = (lastIndexOfSeparator + _length);
      String _substring = qualifiedName.substring(_plus);
      String _substring_1 = qualifiedName.substring(0, lastIndexOfSeparator);
      QualifiedName _create = JDTInternalQualifiedName.create(_substring_1);
      return new JDTInternalQualifiedName(_substring, _create);
    }
  }
  
  public static QualifiedName create(final QualifiedName qualifiedName) {
    List<String> _list = IterableExtensions.<String>toList(qualifiedName);
    List<String> _reverse = ListExtensions.<String>reverse(_list);
    String _join = IterableExtensions.join(_reverse, JDTInternalQualifiedName.JDT_INTERNAL_SEPARATOR);
    return JDTInternalQualifiedName.create(_join);
  }
  
  public static QualifiedName create(final char[][] qualifiedName) {
    QualifiedName _xblockexpression = null;
    {
      final Function1<char[], String> _function = new Function1<char[], String>() {
        @Override
        public String apply(final char[] fragment) {
          return new String(fragment);
        }
      };
      List<String> _map = ListExtensions.<char[], String>map(((List<char[]>)Conversions.doWrapArray(qualifiedName)), _function);
      final String qualifiedNameString = IterableExtensions.join(_map, JDTInternalQualifiedName.JDT_INTERNAL_SEPARATOR);
      _xblockexpression = JDTInternalQualifiedName.create(qualifiedNameString);
    }
    return _xblockexpression;
  }
  
  protected JDTInternalQualifiedName(final String qualifiedName, final QualifiedName parent) {
    super(qualifiedName, parent);
  }
  
  @Override
  public String getSeparator() {
    return JDTInternalQualifiedName.JDT_INTERNAL_SEPARATOR;
  }
  
  @Override
  public QualifiedName dropRoot() {
    List<String> _list = IterableExtensions.<String>toList(this);
    List<String> _reverse = ListExtensions.<String>reverse(_list);
    Iterable<String> _tail = IterableExtensions.<String>tail(_reverse);
    final Function2<JDTInternalQualifiedName, String, JDTInternalQualifiedName> _function = new Function2<JDTInternalQualifiedName, String, JDTInternalQualifiedName>() {
      @Override
      public JDTInternalQualifiedName apply(final JDTInternalQualifiedName parent, final String name) {
        return new JDTInternalQualifiedName(name, parent);
      }
    };
    return IterableExtensions.<String, JDTInternalQualifiedName>fold(_tail, null, _function);
  }
}
