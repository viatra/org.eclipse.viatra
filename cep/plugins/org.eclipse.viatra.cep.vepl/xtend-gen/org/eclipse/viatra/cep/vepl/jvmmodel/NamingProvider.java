/**
 * Copyright (c) 2004-2014, Istvan David, Istvan Rath and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Istvan David - initial API and implementation
 */
package org.eclipse.viatra.cep.vepl.jvmmodel;

import com.google.common.base.Objects;
import com.google.inject.Inject;
import java.util.HashMap;
import java.util.Map;
import org.eclipse.viatra.cep.vepl.vepl.AtomicEventPattern;
import org.eclipse.viatra.cep.vepl.vepl.ComplexEventPattern;
import org.eclipse.viatra.cep.vepl.vepl.EventModel;
import org.eclipse.viatra.cep.vepl.vepl.EventPattern;
import org.eclipse.viatra.cep.vepl.vepl.ModelElement;
import org.eclipse.viatra.cep.vepl.vepl.QueryResultChangeEventPattern;
import org.eclipse.viatra.cep.vepl.vepl.Rule;
import org.eclipse.viatra.cep.vepl.vepl.Trait;
import org.eclipse.xtext.naming.IQualifiedNameProvider;
import org.eclipse.xtext.naming.QualifiedName;
import org.eclipse.xtext.xbase.lib.CollectionLiterals;
import org.eclipse.xtext.xbase.lib.Extension;
import org.eclipse.xtext.xbase.lib.Functions.Function1;
import org.eclipse.xtext.xbase.lib.MapExtensions;
import org.eclipse.xtext.xbase.lib.StringExtensions;

@SuppressWarnings("all")
public class NamingProvider {
  public enum NamingPurpose {
    TRAIT,
    
    EVENT,
    
    PATTERN,
    
    RULE,
    
    JOB,
    
    MAPPING;
  }
  
  @Inject
  @Extension
  private IQualifiedNameProvider _iQualifiedNameProvider;
  
  public final static String TRAIT_PACKAGE_NAME_ELEMENT = "traits";
  
  public final static String EVENTCLASS_PACKAGE_NAME_ELEMENT = "events";
  
  public final static String QUERYRESULT_EVENTCLASS_PACKAGE_NAME_ELEMENT = "events.queryresult";
  
  public final static String ATOMIC_PATTERN_PACKAGE_NAME_ELEMENT = "patterns.atomic";
  
  public final static String QUERYRESULT_PATTERN_PACKAGE_NAME_ELEMENT = "patterns.atomic.queryresult";
  
  public final static String COMPLEX_PATTERN_PACKAGE_NAME_ELEMENT = "patterns.complex";
  
  public final static String ANONYMOUS_PATTERN_PACKAGE_NAME_ELEMENT = "patterns.complex.anonymous";
  
  public final static String RULES_PACKAGE_NAME_ELEMENT = "rules";
  
  public final static String JOBS_PACKAGE_NAME_ELEMENT = "jobs";
  
  public final static String MAPPING_PACKAGE_NAME_ELEMENT = "mapping";
  
  public final static String MAPPING_CLASS_NAME = "QueryEngine2ViatraCep";
  
  private final static String ANONYMOUS_PATTERN_NAME = "_AnonymousPattern_";
  
  private final static String EVENT_SUFFIX = "_Event";
  
  private final static String QUERYRESULT_EVENT_SUFFIX = "_QueryResultEvent";
  
  private final static String PATTERN_SUFFIX = "_Pattern";
  
  private final static String JOB_SUFFIX = "_Job";
  
  public QualifiedName getTraitInterfaceFqn(final ModelElement element) {
    QualifiedName _xblockexpression = null;
    {
      QualifiedName _fullyQualifiedName = this._iQualifiedNameProvider.getFullyQualifiedName(element);
      final String className = _fullyQualifiedName.getLastSegment();
      HashMap<NamingProvider.NamingPurpose, QualifiedName> _packageNames = this.getPackageNames(element);
      QualifiedName _get = _packageNames.get(NamingProvider.NamingPurpose.TRAIT);
      String _firstUpper = StringExtensions.toFirstUpper(className);
      _xblockexpression = _get.append(_firstUpper);
    }
    return _xblockexpression;
  }
  
  public QualifiedName getTraitSpecificationFqn(final ModelElement element) {
    QualifiedName _xblockexpression = null;
    {
      QualifiedName _fullyQualifiedName = this._iQualifiedNameProvider.getFullyQualifiedName(element);
      final String className = _fullyQualifiedName.getLastSegment();
      HashMap<NamingProvider.NamingPurpose, QualifiedName> _packageNames = this.getPackageNames(element);
      QualifiedName _get = _packageNames.get(NamingProvider.NamingPurpose.TRAIT);
      String _firstUpper = StringExtensions.toFirstUpper(className);
      String _plus = (_firstUpper + "Specification");
      _xblockexpression = _get.append(_plus);
    }
    return _xblockexpression;
  }
  
  public QualifiedName getClassFqn(final ModelElement element) {
    QualifiedName _xblockexpression = null;
    {
      QualifiedName _fullyQualifiedName = this._iQualifiedNameProvider.getFullyQualifiedName(element);
      final String className = _fullyQualifiedName.getLastSegment();
      HashMap<NamingProvider.NamingPurpose, QualifiedName> _packageNames = this.getPackageNames(element);
      QualifiedName _get = _packageNames.get(NamingProvider.NamingPurpose.EVENT);
      String _firstUpper = StringExtensions.toFirstUpper(className);
      String _plus = (_firstUpper + NamingProvider.EVENT_SUFFIX);
      _xblockexpression = _get.append(_plus);
    }
    return _xblockexpression;
  }
  
  public QualifiedName getPatternFqn(final ModelElement element) {
    QualifiedName _xblockexpression = null;
    {
      QualifiedName _fullyQualifiedName = this._iQualifiedNameProvider.getFullyQualifiedName(element);
      final String className = _fullyQualifiedName.getLastSegment();
      HashMap<NamingProvider.NamingPurpose, QualifiedName> _packageNames = this.getPackageNames(element);
      QualifiedName _get = _packageNames.get(NamingProvider.NamingPurpose.PATTERN);
      String _firstUpper = StringExtensions.toFirstUpper(className);
      String _plus = (_firstUpper + NamingProvider.PATTERN_SUFFIX);
      _xblockexpression = _get.append(_plus);
    }
    return _xblockexpression;
  }
  
  public HashMap<NamingProvider.NamingPurpose, QualifiedName> getPackageNames(final ModelElement modelElement) {
    HashMap<NamingProvider.NamingPurpose, QualifiedName> _xblockexpression = null;
    {
      final HashMap<NamingProvider.NamingPurpose, QualifiedName> associatedPackages = CollectionLiterals.<NamingProvider.NamingPurpose, QualifiedName>newHashMap();
      boolean _matched = false;
      if (!_matched) {
        if (modelElement instanceof Trait) {
          if (Objects.equal(modelElement, ((Trait)modelElement))) {
            _matched=true;
            QualifiedName _packageName = this.getPackageName(modelElement);
            QualifiedName _append = _packageName.append(NamingProvider.TRAIT_PACKAGE_NAME_ELEMENT);
            associatedPackages.put(
              NamingProvider.NamingPurpose.TRAIT, _append);
          }
        }
      }
      if (!_matched) {
        if (modelElement instanceof AtomicEventPattern) {
          if (Objects.equal(modelElement, ((AtomicEventPattern)modelElement))) {
            _matched=true;
            QualifiedName _packageName = this.getPackageName(modelElement);
            QualifiedName _append = _packageName.append(NamingProvider.EVENTCLASS_PACKAGE_NAME_ELEMENT);
            associatedPackages.put(NamingProvider.NamingPurpose.EVENT, _append);
            QualifiedName _packageName_1 = this.getPackageName(modelElement);
            QualifiedName _append_1 = _packageName_1.append(NamingProvider.ATOMIC_PATTERN_PACKAGE_NAME_ELEMENT);
            associatedPackages.put(NamingProvider.NamingPurpose.PATTERN, _append_1);
          }
        }
      }
      if (!_matched) {
        if (modelElement instanceof ComplexEventPattern) {
          if (Objects.equal(modelElement, ((ComplexEventPattern)modelElement))) {
            _matched=true;
            QualifiedName _packageName = this.getPackageName(modelElement);
            QualifiedName _append = _packageName.append(NamingProvider.COMPLEX_PATTERN_PACKAGE_NAME_ELEMENT);
            associatedPackages.put(NamingProvider.NamingPurpose.PATTERN, _append);
          }
        }
      }
      if (!_matched) {
        if (modelElement instanceof QueryResultChangeEventPattern) {
          if (Objects.equal(modelElement, ((QueryResultChangeEventPattern)modelElement))) {
            _matched=true;
            QualifiedName _packageName = this.getPackageName(modelElement);
            QualifiedName _append = _packageName.append(NamingProvider.QUERYRESULT_EVENTCLASS_PACKAGE_NAME_ELEMENT);
            associatedPackages.put(NamingProvider.NamingPurpose.EVENT, _append);
            QualifiedName _packageName_1 = this.getPackageName(modelElement);
            QualifiedName _append_1 = _packageName_1.append(NamingProvider.QUERYRESULT_PATTERN_PACKAGE_NAME_ELEMENT);
            associatedPackages.put(NamingProvider.NamingPurpose.PATTERN, _append_1);
            QualifiedName _packageName_2 = this.getPackageName(modelElement);
            QualifiedName _append_2 = _packageName_2.append(NamingProvider.MAPPING_PACKAGE_NAME_ELEMENT);
            associatedPackages.put(NamingProvider.NamingPurpose.MAPPING, _append_2);
          }
        }
      }
      if (!_matched) {
        if (modelElement instanceof Rule) {
          if (Objects.equal(modelElement, ((Rule)modelElement))) {
            _matched=true;
            QualifiedName _packageName = this.getPackageName(modelElement);
            QualifiedName _append = _packageName.append(NamingProvider.RULES_PACKAGE_NAME_ELEMENT);
            associatedPackages.put(NamingProvider.NamingPurpose.RULE, _append);
            QualifiedName _packageName_1 = this.getPackageName(modelElement);
            QualifiedName _append_1 = _packageName_1.append(NamingProvider.JOBS_PACKAGE_NAME_ELEMENT);
            associatedPackages.put(NamingProvider.NamingPurpose.JOB, _append_1);
          }
        }
      }
      _xblockexpression = associatedPackages;
    }
    return _xblockexpression;
  }
  
  public QualifiedName getPackageName(final ModelElement modelElement) {
    QualifiedName _fullyQualifiedName = this._iQualifiedNameProvider.getFullyQualifiedName(modelElement);
    return _fullyQualifiedName.skipLast(1);
  }
  
  public static Map<NamingProvider.NamingPurpose, String> asStrings(final HashMap<NamingProvider.NamingPurpose, QualifiedName> packageNames) {
    final Function1<QualifiedName, String> _function = new Function1<QualifiedName, String>() {
      @Override
      public String apply(final QualifiedName v) {
        return v.toString();
      }
    };
    return MapExtensions.<NamingProvider.NamingPurpose, QualifiedName, String>mapValues(packageNames, _function);
  }
  
  public QualifiedName getAnonymousName(final EventPattern element, final int suffix) {
    QualifiedName _fullyQualifiedName = this._iQualifiedNameProvider.getFullyQualifiedName(element);
    QualifiedName packageName = _fullyQualifiedName.skipLast(1);
    QualifiedName _append = packageName.append(NamingProvider.ANONYMOUS_PATTERN_PACKAGE_NAME_ELEMENT);
    return _append.append((NamingProvider.ANONYMOUS_PATTERN_NAME + Integer.valueOf(suffix)));
  }
  
  public QualifiedName getFqn(final Rule rule) {
    QualifiedName _fullyQualifiedName = this._iQualifiedNameProvider.getFullyQualifiedName(rule);
    String className = _fullyQualifiedName.getLastSegment();
    QualifiedName _fullyQualifiedName_1 = this._iQualifiedNameProvider.getFullyQualifiedName(rule);
    QualifiedName packageName = _fullyQualifiedName_1.skipLast(1);
    QualifiedName _append = packageName.append(NamingProvider.RULES_PACKAGE_NAME_ELEMENT);
    String _firstUpper = StringExtensions.toFirstUpper(className);
    return _append.append(_firstUpper);
  }
  
  public QualifiedName getJobClassName(final Rule rule) {
    QualifiedName _fullyQualifiedName = this._iQualifiedNameProvider.getFullyQualifiedName(rule);
    String className = _fullyQualifiedName.getLastSegment();
    QualifiedName _fullyQualifiedName_1 = this._iQualifiedNameProvider.getFullyQualifiedName(rule);
    QualifiedName packageName = _fullyQualifiedName_1.skipLast(1);
    QualifiedName _append = packageName.append(NamingProvider.JOBS_PACKAGE_NAME_ELEMENT);
    String _firstUpper = StringExtensions.toFirstUpper(className);
    String _plus = (_firstUpper + NamingProvider.JOB_SUFFIX);
    return _append.append(_plus);
  }
  
  public QualifiedName getQueryEngine2CepEngineClassFqn(final QueryResultChangeEventPattern pattern) {
    QualifiedName _fullyQualifiedName = this._iQualifiedNameProvider.getFullyQualifiedName(pattern);
    QualifiedName packageName = _fullyQualifiedName.skipLast(1);
    QualifiedName _append = packageName.append(NamingProvider.MAPPING_PACKAGE_NAME_ELEMENT);
    return _append.append(NamingProvider.MAPPING_CLASS_NAME);
  }
  
  public QualifiedName getPackageFqn(final EventModel model) {
    return this._iQualifiedNameProvider.getFullyQualifiedName(model);
  }
  
  public QualifiedName getFactoryFqn(final EventModel model) {
    QualifiedName _fullyQualifiedName = this._iQualifiedNameProvider.getFullyQualifiedName(model);
    return _fullyQualifiedName.append("CepFactory");
  }
  
  public boolean isEvent(final QualifiedName fqn) {
    boolean _or = false;
    String _string = fqn.toString();
    boolean _endsWith = _string.endsWith(NamingProvider.EVENT_SUFFIX);
    if (_endsWith) {
      _or = true;
    } else {
      String _string_1 = fqn.toString();
      boolean _endsWith_1 = _string_1.endsWith(NamingProvider.QUERYRESULT_EVENT_SUFFIX);
      _or = _endsWith_1;
    }
    return _or;
  }
  
  public boolean isRule(final QualifiedName fqn) {
    QualifiedName _skipLast = fqn.skipLast(1);
    String _string = _skipLast.toString();
    return _string.endsWith(NamingProvider.RULES_PACKAGE_NAME_ELEMENT);
  }
  
  public String getType(final QualifiedName fqn) {
    String _xifexpression = null;
    String _string = fqn.toString();
    boolean _endsWith = _string.endsWith(NamingProvider.EVENT_SUFFIX);
    if (_endsWith) {
      _xifexpression = "event class";
    } else {
      String _xifexpression_1 = null;
      String _string_1 = fqn.toString();
      boolean _endsWith_1 = _string_1.endsWith(NamingProvider.QUERYRESULT_EVENT_SUFFIX);
      if (_endsWith_1) {
        _xifexpression_1 = "query result event class";
      } else {
        String _xifexpression_2 = null;
        String _string_2 = fqn.toString();
        boolean _endsWith_2 = _string_2.endsWith(NamingProvider.PATTERN_SUFFIX);
        if (_endsWith_2) {
          String _xifexpression_3 = null;
          String _string_3 = fqn.toString();
          boolean _contains = _string_3.contains(NamingProvider.ATOMIC_PATTERN_PACKAGE_NAME_ELEMENT);
          if (_contains) {
            String _xifexpression_4 = null;
            String _string_4 = fqn.toString();
            boolean _contains_1 = _string_4.contains(NamingProvider.QUERYRESULT_PATTERN_PACKAGE_NAME_ELEMENT);
            if (_contains_1) {
              _xifexpression_4 = "atomic query result event pattern";
            } else {
              _xifexpression_4 = "atomic event pattern";
            }
            _xifexpression_3 = _xifexpression_4;
          } else {
            _xifexpression_3 = "complex event pattern";
          }
          _xifexpression_2 = _xifexpression_3;
        } else {
          String _xifexpression_5 = null;
          QualifiedName _skipLast = fqn.skipLast(1);
          String _string_5 = _skipLast.toString();
          boolean _contains_2 = _string_5.contains(NamingProvider.RULES_PACKAGE_NAME_ELEMENT);
          if (_contains_2) {
            _xifexpression_5 = "rule";
          }
          _xifexpression_2 = _xifexpression_5;
        }
        _xifexpression_1 = _xifexpression_2;
      }
      _xifexpression = _xifexpression_1;
    }
    return _xifexpression;
  }
}
