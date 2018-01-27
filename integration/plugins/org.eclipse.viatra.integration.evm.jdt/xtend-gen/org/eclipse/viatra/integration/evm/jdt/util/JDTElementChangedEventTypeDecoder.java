/**
 * Copyright (c) 2015-2016, IncQuery Labs Ltd., CEA LIST
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *   Abel Hegedus - initial API and implementation
 */
package org.eclipse.viatra.integration.evm.jdt.util;

import java.util.HashSet;
import java.util.Set;
import org.eclipse.viatra.integration.evm.jdt.util.ElementChangedEventType;
import org.eclipse.xtext.xbase.lib.CollectionLiterals;
import org.eclipse.xtext.xbase.lib.Conversions;
import org.eclipse.xtext.xbase.lib.Functions.Function1;
import org.eclipse.xtext.xbase.lib.IterableExtensions;
import org.eclipse.xtext.xbase.lib.Procedures.Procedure1;

@SuppressWarnings("all")
public class JDTElementChangedEventTypeDecoder {
  public static ElementChangedEventType toEventType(final int value) {
    ElementChangedEventType[] _values = ElementChangedEventType.values();
    final Function1<ElementChangedEventType, Boolean> _function = new Function1<ElementChangedEventType, Boolean>() {
      @Override
      public Boolean apply(final ElementChangedEventType it) {
        int _value = it.getValue();
        return Boolean.valueOf((_value == value));
      }
    };
    return IterableExtensions.<ElementChangedEventType>findFirst(((Iterable<ElementChangedEventType>)Conversions.doWrapArray(_values)), _function);
  }
  
  public static Set<ElementChangedEventType> toEventTypes(final int values) {
    final HashSet<ElementChangedEventType> result = CollectionLiterals.<ElementChangedEventType>newHashSet();
    ElementChangedEventType[] _values = ElementChangedEventType.values();
    final Procedure1<ElementChangedEventType> _function = new Procedure1<ElementChangedEventType>() {
      @Override
      public void apply(final ElementChangedEventType flag) {
        int _value = flag.getValue();
        int _bitwiseAnd = (values & _value);
        boolean _notEquals = (_bitwiseAnd != 0);
        if (_notEquals) {
          result.add(flag);
        }
      }
    };
    IterableExtensions.<ElementChangedEventType>forEach(((Iterable<ElementChangedEventType>)Conversions.doWrapArray(_values)), _function);
    return result;
  }
}
