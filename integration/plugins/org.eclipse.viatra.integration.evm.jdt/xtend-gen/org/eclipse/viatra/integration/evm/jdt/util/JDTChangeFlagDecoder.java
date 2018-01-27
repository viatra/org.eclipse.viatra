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
package org.eclipse.viatra.integration.evm.jdt.util;

import java.util.HashSet;
import java.util.Set;
import org.eclipse.viatra.integration.evm.jdt.util.ChangeFlag;
import org.eclipse.xtext.xbase.lib.CollectionLiterals;
import org.eclipse.xtext.xbase.lib.Conversions;
import org.eclipse.xtext.xbase.lib.Functions.Function1;
import org.eclipse.xtext.xbase.lib.IterableExtensions;
import org.eclipse.xtext.xbase.lib.Procedures.Procedure1;

@SuppressWarnings("all")
public class JDTChangeFlagDecoder {
  public static ChangeFlag toChangeFlag(final int value) {
    ChangeFlag[] _values = ChangeFlag.values();
    final Function1<ChangeFlag, Boolean> _function = new Function1<ChangeFlag, Boolean>() {
      @Override
      public Boolean apply(final ChangeFlag it) {
        int _value = it.getValue();
        return Boolean.valueOf((_value == value));
      }
    };
    return IterableExtensions.<ChangeFlag>findFirst(((Iterable<ChangeFlag>)Conversions.doWrapArray(_values)), _function);
  }
  
  public static Set<ChangeFlag> toChangeFlags(final int values) {
    final HashSet<ChangeFlag> result = CollectionLiterals.<ChangeFlag>newHashSet();
    ChangeFlag[] _values = ChangeFlag.values();
    final Procedure1<ChangeFlag> _function = new Procedure1<ChangeFlag>() {
      @Override
      public void apply(final ChangeFlag flag) {
        int _value = flag.getValue();
        int _bitwiseAnd = (values & _value);
        boolean _notEquals = (_bitwiseAnd != 0);
        if (_notEquals) {
          result.add(flag);
        }
      }
    };
    IterableExtensions.<ChangeFlag>forEach(((Iterable<ChangeFlag>)Conversions.doWrapArray(_values)), _function);
    return result;
  }
}
