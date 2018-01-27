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
import java.util.List;
import java.util.Set;
import org.apache.log4j.Logger;
import org.eclipse.jdt.internal.core.builder.ReferenceCollection;
import org.eclipse.viatra.integration.evm.jdt.util.JDTInternalQualifiedName;
import org.eclipse.viatra.integration.evm.jdt.util.QualifiedName;
import org.eclipse.viatra.integration.evm.jdt.wrappers.ReferenceStorage;
import org.eclipse.xtend2.lib.StringConcatenation;
import org.eclipse.xtext.xbase.lib.CollectionLiterals;
import org.eclipse.xtext.xbase.lib.Conversions;
import org.eclipse.xtext.xbase.lib.Exceptions;
import org.eclipse.xtext.xbase.lib.Extension;
import org.eclipse.xtext.xbase.lib.Functions.Function1;
import org.eclipse.xtext.xbase.lib.IterableExtensions;
import org.eclipse.xtext.xbase.lib.ListExtensions;

@SuppressWarnings("all")
public class JDTReferenceStorage implements ReferenceStorage {
  @Extension
  private final Logger logger;
  
  private Set<QualifiedName> qualifiedNameReferences;
  
  private Set<String> simpleNameReferences;
  
  private Set<String> rootReferences;
  
  public JDTReferenceStorage(final ReferenceCollection referenceCollection) {
    try {
      Class<? extends JDTReferenceStorage> _class = this.getClass();
      Logger _logger = Logger.getLogger(_class);
      this.logger = _logger;
      if ((referenceCollection == null)) {
        throw new IllegalArgumentException("Reference collection cannot be null");
      }
      try {
        final Field qualifiedNameReferencesField = ReferenceCollection.class.getDeclaredField("qualifiedNameReferences");
        qualifiedNameReferencesField.setAccessible(true);
        Object _get = qualifiedNameReferencesField.get(referenceCollection);
        final char[][][] referredQualifiedNames = ((char[][][]) _get);
        final Function1<char[][], QualifiedName> _function = new Function1<char[][], QualifiedName>() {
          @Override
          public QualifiedName apply(final char[][] fqn) {
            return JDTInternalQualifiedName.create(fqn);
          }
        };
        List<QualifiedName> _map = ListExtensions.<char[][], QualifiedName>map(((List<char[][]>)Conversions.doWrapArray(referredQualifiedNames)), _function);
        Set<QualifiedName> _set = IterableExtensions.<QualifiedName>toSet(_map);
        this.qualifiedNameReferences = _set;
      } catch (final Throwable _t) {
        if (_t instanceof NoSuchFieldException) {
          final NoSuchFieldException e = (NoSuchFieldException)_t;
          StringConcatenation _builder = new StringConcatenation();
          _builder.append("Failed to get qualified name references from JDT build state");
          this.logger.error(_builder, e);
          this.qualifiedNameReferences = Collections.<QualifiedName>unmodifiableSet(CollectionLiterals.<QualifiedName>newHashSet());
        } else {
          throw Exceptions.sneakyThrow(_t);
        }
      }
      try {
        final Field simpleNameReferencesField = ReferenceCollection.class.getDeclaredField("simpleNameReferences");
        simpleNameReferencesField.setAccessible(true);
        Object _get_1 = simpleNameReferencesField.get(referenceCollection);
        final char[][] referredSimpleNames = ((char[][]) _get_1);
        final Function1<char[], String> _function_1 = new Function1<char[], String>() {
          @Override
          public String apply(final char[] name) {
            return new String(name);
          }
        };
        List<String> _map_1 = ListExtensions.<char[], String>map(((List<char[]>)Conversions.doWrapArray(referredSimpleNames)), _function_1);
        Set<String> _set_1 = IterableExtensions.<String>toSet(_map_1);
        this.simpleNameReferences = _set_1;
      } catch (final Throwable _t_1) {
        if (_t_1 instanceof NoSuchFieldException) {
          final NoSuchFieldException e_1 = (NoSuchFieldException)_t_1;
          StringConcatenation _builder_1 = new StringConcatenation();
          _builder_1.append("Failed to get simple name references from JDT build state");
          this.logger.error(_builder_1, e_1);
          this.simpleNameReferences = Collections.<String>unmodifiableSet(CollectionLiterals.<String>newHashSet());
        } else {
          throw Exceptions.sneakyThrow(_t_1);
        }
      }
      try {
        final Field rootReferencesField = ReferenceCollection.class.getDeclaredField("rootReferences");
        rootReferencesField.setAccessible(true);
        Object _get_2 = rootReferencesField.get(referenceCollection);
        final char[][] referredRootNames = ((char[][]) _get_2);
        final Function1<char[], String> _function_2 = new Function1<char[], String>() {
          @Override
          public String apply(final char[] name) {
            return new String(name);
          }
        };
        List<String> _map_2 = ListExtensions.<char[], String>map(((List<char[]>)Conversions.doWrapArray(referredRootNames)), _function_2);
        Set<String> _set_2 = IterableExtensions.<String>toSet(_map_2);
        this.rootReferences = _set_2;
      } catch (final Throwable _t_2) {
        if (_t_2 instanceof NoSuchFieldException) {
          final NoSuchFieldException e_2 = (NoSuchFieldException)_t_2;
          StringConcatenation _builder_2 = new StringConcatenation();
          _builder_2.append("Failed to get root references from JDT build state");
          this.logger.error(_builder_2, e_2);
          this.rootReferences = Collections.<String>unmodifiableSet(CollectionLiterals.<String>newHashSet());
        } else {
          throw Exceptions.sneakyThrow(_t_2);
        }
      }
    } catch (Throwable _e) {
      throw Exceptions.sneakyThrow(_e);
    }
  }
  
  @Override
  public Set<QualifiedName> getQualifiedNameReferences() {
    return this.qualifiedNameReferences;
  }
  
  @Override
  public Set<String> getRootReferences() {
    return this.rootReferences;
  }
  
  @Override
  public Set<String> getSimpleNameReferences() {
    return this.simpleNameReferences;
  }
}
