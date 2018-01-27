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
package org.eclipse.viatra.cep.vepl.validation;

import java.util.Arrays;
import java.util.List;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.viatra.cep.vepl.vepl.AbstractMultiplicity;
import org.eclipse.viatra.cep.vepl.vepl.AndOperator;
import org.eclipse.viatra.cep.vepl.vepl.Atom;
import org.eclipse.viatra.cep.vepl.vepl.ChainedExpression;
import org.eclipse.viatra.cep.vepl.vepl.ComplexEventExpression;
import org.eclipse.viatra.cep.vepl.vepl.ComplexEventOperator;
import org.eclipse.viatra.cep.vepl.vepl.ComplexEventPattern;
import org.eclipse.viatra.cep.vepl.vepl.FollowsOperator;
import org.eclipse.viatra.cep.vepl.vepl.Infinite;
import org.eclipse.viatra.cep.vepl.vepl.Multiplicity;
import org.eclipse.viatra.cep.vepl.vepl.ParameterizedPatternCall;
import org.eclipse.viatra.cep.vepl.vepl.PatternCallParameterList;
import org.eclipse.viatra.cep.vepl.vepl.Timewindow;
import org.eclipse.xtext.xbase.lib.Functions.Function1;
import org.eclipse.xtext.xbase.lib.Functions.Function2;
import org.eclipse.xtext.xbase.lib.IterableExtensions;

/**
 * Helper class for the {@link VeplValidator}.
 */
@SuppressWarnings("all")
public class ValidationHelper {
  protected static boolean _hasTimewindow(final ComplexEventExpression expression) {
    Timewindow _timewindow = expression.getTimewindow();
    return (_timewindow != null);
  }
  
  protected static boolean _hasTimewindow(final Atom atom) {
    Timewindow _timewindow = atom.getTimewindow();
    return (_timewindow != null);
  }
  
  protected static boolean _hasMultiplicity(final ComplexEventExpression expression) {
    AbstractMultiplicity _multiplicity = expression.getMultiplicity();
    return (_multiplicity != null);
  }
  
  protected static boolean _hasMultiplicity(final Atom atom) {
    AbstractMultiplicity _multiplicity = atom.getMultiplicity();
    return (_multiplicity != null);
  }
  
  public static boolean hasInfiniteMultiplicity(final ComplexEventExpression complexEventExpression) {
    AbstractMultiplicity _multiplicity = complexEventExpression.getMultiplicity();
    return (_multiplicity instanceof Infinite);
  }
  
  public static boolean nullOrOneMultiplicity(final AbstractMultiplicity multiplicity) {
    if ((multiplicity == null)) {
      return true;
    }
    if ((!(multiplicity instanceof Multiplicity))) {
      return false;
    }
    int _value = ((Multiplicity) multiplicity).getValue();
    return (_value == 1);
  }
  
  public static boolean hasParameterList(final ParameterizedPatternCall patternCall) {
    PatternCallParameterList _parameterList = patternCall.getParameterList();
    return (_parameterList != null);
  }
  
  public static boolean qualifiesAsFollowingOperator(final ComplexEventOperator operator) {
    boolean _or = false;
    if ((operator instanceof FollowsOperator)) {
      _or = true;
    } else {
      _or = (operator instanceof AndOperator);
    }
    return _or;
  }
  
  public static List<ChainedExpression> subListFrom(final List<ChainedExpression> list, final ComplexEventExpression element) {
    final Function1<ChainedExpression, Boolean> _function = new Function1<ChainedExpression, Boolean>() {
      @Override
      public Boolean apply(final ChainedExpression che) {
        ComplexEventExpression _expression = che.getExpression();
        return Boolean.valueOf(_expression.equals(element));
      }
    };
    ChainedExpression _findFirst = IterableExtensions.<ChainedExpression>findFirst(list, _function);
    int _indexOf = list.indexOf(_findFirst);
    int _plus = (_indexOf + 1);
    int _size = list.size();
    return list.subList(_plus, _size);
  }
  
  public static ComplexEventPattern findContainingComplexEventPatternDefinition(final ParameterizedPatternCall parameterizedPatternCall) {
    ComplexEventPattern complexEventPattern = null;
    EObject tmp = parameterizedPatternCall.eContainer();
    while ((complexEventPattern == null)) {
      if ((tmp instanceof ComplexEventPattern)) {
        complexEventPattern = ((ComplexEventPattern) tmp);
      } else {
        EObject _eContainer = tmp.eContainer();
        tmp = _eContainer;
      }
    }
    return complexEventPattern;
  }
  
  public static String foldWithComma(final Iterable<String> iterable) {
    final Function2<String, String, String> _function = new Function2<String, String, String>() {
      @Override
      public String apply(final String a, final String b) {
        String _xifexpression = null;
        boolean _isEmpty = a.isEmpty();
        if (_isEmpty) {
          _xifexpression = (a + b);
        } else {
          _xifexpression = ((a + ", ") + b);
        }
        return _xifexpression;
      }
    };
    return IterableExtensions.<String, String>fold(iterable, "", _function);
  }
  
  public static boolean hasTimewindow(final ComplexEventExpression atom) {
    if (atom instanceof Atom) {
      return _hasTimewindow((Atom)atom);
    } else if (atom != null) {
      return _hasTimewindow(atom);
    } else {
      throw new IllegalArgumentException("Unhandled parameter types: " +
        Arrays.<Object>asList(atom).toString());
    }
  }
  
  public static boolean hasMultiplicity(final ComplexEventExpression atom) {
    if (atom instanceof Atom) {
      return _hasMultiplicity((Atom)atom);
    } else if (atom != null) {
      return _hasMultiplicity(atom);
    } else {
      throw new IllegalArgumentException("Unhandled parameter types: " +
        Arrays.<Object>asList(atom).toString());
    }
  }
}
