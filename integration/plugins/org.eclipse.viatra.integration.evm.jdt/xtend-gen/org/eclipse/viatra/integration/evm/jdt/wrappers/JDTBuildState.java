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
package org.eclipse.viatra.integration.evm.jdt.wrappers;

import java.lang.reflect.Field;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.eclipse.jdt.internal.compiler.util.SimpleLookupTable;
import org.eclipse.jdt.internal.core.builder.ReferenceCollection;
import org.eclipse.jdt.internal.core.builder.State;
import org.eclipse.jdt.internal.core.builder.StringSet;
import org.eclipse.viatra.integration.evm.jdt.util.JDTInternalQualifiedName;
import org.eclipse.viatra.integration.evm.jdt.util.QualifiedName;
import org.eclipse.viatra.integration.evm.jdt.wrappers.BuildState;
import org.eclipse.viatra.integration.evm.jdt.wrappers.JDTReferenceStorage;
import org.eclipse.viatra.integration.evm.jdt.wrappers.ReferenceStorage;
import org.eclipse.xtext.xbase.lib.CollectionLiterals;
import org.eclipse.xtext.xbase.lib.Conversions;
import org.eclipse.xtext.xbase.lib.Exceptions;
import org.eclipse.xtext.xbase.lib.ExclusiveRange;
import org.eclipse.xtext.xbase.lib.Functions.Function1;
import org.eclipse.xtext.xbase.lib.Functions.Function2;
import org.eclipse.xtext.xbase.lib.IterableExtensions;
import org.eclipse.xtext.xbase.lib.IteratorExtensions;
import org.eclipse.xtext.xbase.lib.ListExtensions;
import org.eclipse.xtext.xbase.lib.MapExtensions;

@SuppressWarnings("all")
public class JDTBuildState implements BuildState {
  private final State state;
  
  public JDTBuildState(final State state) {
    this.state = state;
  }
  
  @Override
  public List<String> getStructurallyChangedTypes() {
    try {
      Class<? extends State> _class = this.state.getClass();
      final Field field = _class.getDeclaredField("structurallyChangedTypes");
      field.setAccessible(true);
      Object _get = field.get(this.state);
      final StringSet structurallyChangedTypes = ((StringSet) _get);
      if ((structurallyChangedTypes == null)) {
        return Collections.<String>unmodifiableList(CollectionLiterals.<String>newArrayList());
      }
      return this.toList(structurallyChangedTypes);
    } catch (Throwable _e) {
      throw Exceptions.sneakyThrow(_e);
    }
  }
  
  @Override
  public Map<String, ReferenceStorage> getReferences() {
    final SimpleLookupTable referencesLookup = this.state.getReferences();
    final Object[] keySet = referencesLookup.keyTable;
    final Object[] valueSet = referencesLookup.valueTable;
    final HashMap<String, ReferenceStorage> references = CollectionLiterals.<String, ReferenceStorage>newHashMap();
    int _length = keySet.length;
    ExclusiveRange _doubleDotLessThan = new ExclusiveRange(0, _length, true);
    for (final Integer i : _doubleDotLessThan) {
      {
        Object _get = keySet[(i).intValue()];
        final String currentKey = ((String) _get);
        Object _get_1 = valueSet[(i).intValue()];
        final ReferenceCollection currentValue = ((ReferenceCollection) _get_1);
        if (((currentKey != null) && (currentValue != null))) {
          final JDTReferenceStorage referenceStorage = new JDTReferenceStorage(currentValue);
          references.put(currentKey, referenceStorage);
        }
      }
    }
    return references;
  }
  
  @Override
  public Iterable<QualifiedName> getAffectedCompilationUnitsInProject() {
    final List<String> changedTypes = this.getStructurallyChangedTypes();
    final Map<String, ReferenceStorage> references = this.getReferences();
    final Function2<String, ReferenceStorage, Boolean> _function = new Function2<String, ReferenceStorage, Boolean>() {
      @Override
      public Boolean apply(final String referer, final ReferenceStorage referenceStorage) {
        final Function1<String, Boolean> _function = new Function1<String, Boolean>() {
          @Override
          public Boolean apply(final String nameString) {
            final QualifiedName fqn = JDTInternalQualifiedName.create(nameString);
            Set<QualifiedName> _qualifiedNameReferences = referenceStorage.getQualifiedNameReferences();
            final boolean containedAsQualifiedName = _qualifiedNameReferences.contains(fqn);
            Set<String> _simpleNameReferences = referenceStorage.getSimpleNameReferences();
            String _name = fqn.getName();
            final boolean containedAsSimpleName = _simpleNameReferences.contains(_name);
            boolean _or = false;
            if (containedAsQualifiedName) {
              _or = true;
            } else {
              _or = containedAsSimpleName;
            }
            return Boolean.valueOf(_or);
          }
        };
        return Boolean.valueOf(IterableExtensions.<String>exists(changedTypes, _function));
      }
    };
    Map<String, ReferenceStorage> _filter = MapExtensions.<String, ReferenceStorage>filter(references, _function);
    Set<String> _keySet = _filter.keySet();
    final Function1<String, QualifiedName> _function_1 = new Function1<String, QualifiedName>() {
      @Override
      public QualifiedName apply(final String it) {
        QualifiedName _xblockexpression = null;
        {
          final QualifiedName fullPath = JDTInternalQualifiedName.create(it);
          Iterator<String> _iterator = fullPath.iterator();
          List<String> _list = IteratorExtensions.<String>toList(_iterator);
          List<String> _reverse = ListExtensions.<String>reverse(_list);
          Iterable<String> _tail = IterableExtensions.<String>tail(_reverse);
          final String pathWithoutSrcSegment = IterableExtensions.join(_tail, "/");
          _xblockexpression = JDTInternalQualifiedName.create(pathWithoutSrcSegment);
        }
        return _xblockexpression;
      }
    };
    final Iterable<QualifiedName> affectedCompilationUnits = IterableExtensions.<String, QualifiedName>map(_keySet, _function_1);
    return affectedCompilationUnits;
  }
  
  private List<String> toList(final StringSet stringSet) {
    Iterable<String> _filterNull = IterableExtensions.<String>filterNull(((Iterable<String>)Conversions.doWrapArray(stringSet.values)));
    return IterableExtensions.<String>toList(_filterNull);
  }
}
