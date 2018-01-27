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

import com.google.common.base.Joiner;
import java.util.List;
import org.eclipse.viatra.integration.evm.jdt.util.QualifiedName;
import org.eclipse.xtext.xbase.lib.Functions.Function2;
import org.eclipse.xtext.xbase.lib.IterableExtensions;
import org.eclipse.xtext.xbase.lib.ListExtensions;

@SuppressWarnings("all")
public class JDTQualifiedName extends QualifiedName {
  private final static String JDT_SEPARATOR = ".";
  
  public static QualifiedName create(final String qualifiedName) {
    final int lastIndexOfSeparator = qualifiedName.lastIndexOf(JDTQualifiedName.JDT_SEPARATOR);
    if ((lastIndexOfSeparator == (-1))) {
      return new JDTQualifiedName(qualifiedName, null);
    } else {
      int _length = JDTQualifiedName.JDT_SEPARATOR.length();
      int _plus = (lastIndexOfSeparator + _length);
      String _substring = qualifiedName.substring(_plus);
      String _substring_1 = qualifiedName.substring(0, lastIndexOfSeparator);
      QualifiedName _create = JDTQualifiedName.create(_substring_1);
      return new JDTQualifiedName(_substring, _create);
    }
  }
  
  public static QualifiedName create(final QualifiedName qualifiedName) {
    Joiner _on = Joiner.on(JDTQualifiedName.JDT_SEPARATOR);
    List<String> _list = IterableExtensions.<String>toList(qualifiedName);
    List<String> _reverse = ListExtensions.<String>reverse(_list);
    String _join = _on.join(_reverse);
    return JDTQualifiedName.create(_join);
  }
  
  protected JDTQualifiedName(final String qualifiedName, final QualifiedName parent) {
    super(qualifiedName, parent);
  }
  
  @Override
  public String getSeparator() {
    return JDTQualifiedName.JDT_SEPARATOR;
  }
  
  @Override
  public QualifiedName dropRoot() {
    List<String> _list = IterableExtensions.<String>toList(this);
    List<String> _reverse = ListExtensions.<String>reverse(_list);
    Iterable<String> _tail = IterableExtensions.<String>tail(_reverse);
    final Function2<JDTQualifiedName, String, JDTQualifiedName> _function = new Function2<JDTQualifiedName, String, JDTQualifiedName>() {
      @Override
      public JDTQualifiedName apply(final JDTQualifiedName parent, final String name) {
        return new JDTQualifiedName(name, parent);
      }
    };
    return IterableExtensions.<String, JDTQualifiedName>fold(_tail, null, _function);
  }
}
