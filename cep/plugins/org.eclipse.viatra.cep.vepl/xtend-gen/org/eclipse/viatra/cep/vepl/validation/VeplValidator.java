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

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.List;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.viatra.cep.vepl.validation.AbstractVeplValidator;
import org.eclipse.viatra.cep.vepl.validation.ValidationHelper;
import org.eclipse.viatra.cep.vepl.vepl.AbstractMultiplicity;
import org.eclipse.viatra.cep.vepl.vepl.Atom;
import org.eclipse.viatra.cep.vepl.vepl.AtomicEventPattern;
import org.eclipse.viatra.cep.vepl.vepl.BinaryOperator;
import org.eclipse.viatra.cep.vepl.vepl.ChainedExpression;
import org.eclipse.viatra.cep.vepl.vepl.ComplexEventExpression;
import org.eclipse.viatra.cep.vepl.vepl.ComplexEventPattern;
import org.eclipse.viatra.cep.vepl.vepl.EventModel;
import org.eclipse.viatra.cep.vepl.vepl.EventPattern;
import org.eclipse.viatra.cep.vepl.vepl.Import;
import org.eclipse.viatra.cep.vepl.vepl.Infinite;
import org.eclipse.viatra.cep.vepl.vepl.ModelElement;
import org.eclipse.viatra.cep.vepl.vepl.Multiplicity;
import org.eclipse.viatra.cep.vepl.vepl.NegOperator;
import org.eclipse.viatra.cep.vepl.vepl.ParameterizedPatternCall;
import org.eclipse.viatra.cep.vepl.vepl.PatternCallParameter;
import org.eclipse.viatra.cep.vepl.vepl.PatternCallParameterList;
import org.eclipse.viatra.cep.vepl.vepl.QueryImport;
import org.eclipse.viatra.cep.vepl.vepl.QueryResultChangeEventPattern;
import org.eclipse.viatra.cep.vepl.vepl.Trait;
import org.eclipse.viatra.cep.vepl.vepl.TraitList;
import org.eclipse.viatra.cep.vepl.vepl.TraitTypedParameterList;
import org.eclipse.viatra.cep.vepl.vepl.TypedParameter;
import org.eclipse.viatra.cep.vepl.vepl.TypedParameterList;
import org.eclipse.viatra.cep.vepl.vepl.TypedParameterWithDefaultValue;
import org.eclipse.viatra.cep.vepl.vepl.VeplPackage;
import org.eclipse.xtext.validation.Check;
import org.eclipse.xtext.xbase.lib.Functions.Function1;
import org.eclipse.xtext.xbase.lib.Functions.Function2;
import org.eclipse.xtext.xbase.lib.IterableExtensions;
import org.eclipse.xtext.xbase.lib.StringExtensions;

@SuppressWarnings("all")
public class VeplValidator extends AbstractVeplValidator {
  public final static String INVALID_NAME = "invalidName";
  
  public final static String INVALID_ARGUMENTS = "invalidArguments";
  
  public final static String MISSING_QUERY_IMPORT = "missingQueryImport";
  
  public final static String ATOM_TIMEWINDOW_NO_MULTIPLICITY = "atomTimewindowNoMultiplicity";
  
  public final static String SINGE_PLAIN_ATOM_IN_COMPLEX_EVENT_EXPRESSION = "singlePlainAtomInComplexEventExpression";
  
  public final static String NON_POSITIVE_MULTIPLICITY = "nonPositiveMultiplicity";
  
  public final static String INFINITE_MULTIPLICITY_WITH_TIMEWINDOW = "infiniteMultiplicityWithTimewindow";
  
  public final static String NO_INFINITE_SUPPORT = "noInfiniteSupport";
  
  public final static String NEGATIVE_OPERATOR_ON_NONATOMIC_REFERENCE = "negativeOperatorOnNonAtomicReference";
  
  public final static String UNSAFE_INFINITE_MULTIPLICITY = "unsafeInfiniteMultiplicity";
  
  public final static String PARAMETER_ON_NON_ATOMIC_PATTERN_CALL = "parameterOnNonAtomicPatternCall";
  
  public final static String NEGATIVE_WITH_MULTIPLICITY = "negativeWithMultiplicity";
  
  public final static String NEGATIVE_WITH_TIMEWINDOW = "negativeWithTimewindow";
  
  public final static String DUPLICATE_TRAIT_PARAMETER_NAMES = "duplicateTraitParameterNames";
  
  public final static String TRAIT_EXPERIMENTAL = "traitExperimental";
  
  public final static String SHADOWED_TRAIT_PARAMETERS = "shadowedTraitParameters";
  
  @Check
  public void uniqueName(final ModelElement modelElement) {
    String _name = modelElement.getName();
    boolean _isNullOrEmpty = StringExtensions.isNullOrEmpty(_name);
    if (_isNullOrEmpty) {
      return;
    }
    EObject _eContainer = modelElement.eContainer();
    boolean _not = (!(_eContainer instanceof EventModel));
    if (_not) {
      return;
    }
    EObject _eContainer_1 = modelElement.eContainer();
    EventModel model = ((EventModel) _eContainer_1);
    EList<ModelElement> _modelElements = model.getModelElements();
    for (final ModelElement me : _modelElements) {
      this.checkUniqueness(modelElement, me);
    }
  }
  
  private void checkUniqueness(final ModelElement modelElement1, final ModelElement modelElement2) {
    boolean _equals = modelElement1.equals(modelElement2);
    if (_equals) {
      return;
    }
    String _name = modelElement1.getName();
    String _name_1 = modelElement2.getName();
    boolean _equalsIgnoreCase = _name.equalsIgnoreCase(_name_1);
    if (_equalsIgnoreCase) {
      this.error("All model elements must have a unique name!", VeplPackage.Literals.MODEL_ELEMENT__NAME, VeplValidator.INVALID_NAME);
    }
  }
  
  @Check
  public void validPatternCallArguments(final ParameterizedPatternCall patternCall) {
    boolean _or = false;
    boolean _hasParameterList = ValidationHelper.hasParameterList(patternCall);
    boolean _not = (!_hasParameterList);
    if (_not) {
      _or = true;
    } else {
      EventPattern _eventPattern = patternCall.getEventPattern();
      boolean _tripleEquals = (_eventPattern == null);
      _or = _tripleEquals;
    }
    if (_or) {
      return;
    }
    PatternCallParameterList parameterList = patternCall.getParameterList();
    EventPattern eventPatternParameter = patternCall.getEventPattern();
    int patternParameterNumber = this.getParameterNumber(eventPatternParameter);
    boolean _and = false;
    EList<PatternCallParameter> _parameters = parameterList.getParameters();
    boolean _isEmpty = _parameters.isEmpty();
    if (!_isEmpty) {
      _and = false;
    } else {
      _and = (patternParameterNumber != 0);
    }
    if (_and) {
      this.error("Pattern call parameters must be specified!", 
        VeplPackage.Literals.PARAMETERIZED_PATTERN_CALL__PARAMETER_LIST, VeplValidator.INVALID_ARGUMENTS);
    }
    EList<PatternCallParameter> _parameters_1 = parameterList.getParameters();
    int _size = _parameters_1.size();
    boolean _notEquals = (_size != patternParameterNumber);
    if (_notEquals) {
      this.error("The exact number of parameters in the referred pattern must be specified!", 
        VeplPackage.Literals.PARAMETERIZED_PATTERN_CALL__PARAMETER_LIST, VeplValidator.INVALID_ARGUMENTS);
    }
  }
  
  private int getParameterNumber(final EventPattern eventPattern) {
    int _switchResult = (int) 0;
    boolean _matched = false;
    if (!_matched) {
      if (eventPattern instanceof AtomicEventPattern) {
        _matched=true;
        TypedParameterList _parameters = ((AtomicEventPattern)eventPattern).getParameters();
        int _typedParameterListSize = this.getTypedParameterListSize(_parameters);
        TraitList _traits = ((AtomicEventPattern)eventPattern).getTraits();
        int _traitParameterListSize = this.getTraitParameterListSize(_traits);
        _switchResult = (_typedParameterListSize + _traitParameterListSize);
      }
    }
    if (!_matched) {
      if (eventPattern instanceof QueryResultChangeEventPattern) {
        _matched=true;
        TypedParameterList _parameters = ((QueryResultChangeEventPattern)eventPattern).getParameters();
        _switchResult = this.getTypedParameterListSize(_parameters);
      }
    }
    if (!_matched) {
      if (eventPattern instanceof ComplexEventPattern) {
        _matched=true;
        TypedParameterList _parameters = ((ComplexEventPattern)eventPattern).getParameters();
        _switchResult = this.getTypedParameterListSize(_parameters);
      }
    }
    if (!_matched) {
      _switchResult = 0;
    }
    return _switchResult;
  }
  
  private int getTypedParameterListSize(final TypedParameterList parameterList) {
    if ((parameterList == null)) {
      return 0;
    }
    EList<TypedParameter> _parameters = parameterList.getParameters();
    boolean _isNullOrEmpty = IterableExtensions.isNullOrEmpty(_parameters);
    if (_isNullOrEmpty) {
      return 0;
    }
    EList<TypedParameter> _parameters_1 = parameterList.getParameters();
    return _parameters_1.size();
  }
  
  private int getTraitParameterListSize(final TraitList traitList) {
    Integer _xifexpression = null;
    if ((traitList == null)) {
      return 0;
    } else {
      EList<Trait> _traits = traitList.getTraits();
      final Function2<Integer, Trait, Integer> _function = new Function2<Integer, Trait, Integer>() {
        @Override
        public Integer apply(final Integer count, final Trait trait) {
          TraitTypedParameterList _parameters = trait.getParameters();
          EList<TypedParameterWithDefaultValue> _parameters_1 = _parameters.getParameters();
          int _size = _parameters_1.size();
          return Integer.valueOf(((count).intValue() + _size));
        }
      };
      _xifexpression = IterableExtensions.<Trait, Integer>fold(_traits, Integer.valueOf(0), _function);
    }
    return (_xifexpression).intValue();
  }
  
  @Check
  public void explicitlyImportedQueryPackage(final QueryResultChangeEventPattern iqPatternEventPattern) {
    EObject _eContainer = iqPatternEventPattern.eContainer();
    EventModel eventModel = ((EventModel) _eContainer);
    EList<Import> _imports = eventModel.getImports();
    final Function1<Import, Boolean> _function = new Function1<Import, Boolean>() {
      @Override
      public Boolean apply(final Import i) {
        return Boolean.valueOf((i instanceof QueryImport));
      }
    };
    Iterable<Import> _filter = IterableExtensions.<Import>filter(_imports, _function);
    int _size = IterableExtensions.size(_filter);
    boolean _equals = (_size == 1);
    boolean _not = (!_equals);
    if (_not) {
      this.error(
        "Missing \'import-patterns\' statement for query reference.", 
        VeplPackage.Literals.QUERY_RESULT_CHANGE_EVENT_PATTERN__QUERY_REFERENCE, 
        VeplValidator.MISSING_QUERY_IMPORT);
    }
  }
  
  @Check
  public void expressionAtomWithTimewindowMustFeatureMultiplicity(final Atom atom) {
    boolean _and = false;
    boolean _hasTimewindow = ValidationHelper.hasTimewindow(atom);
    if (!_hasTimewindow) {
      _and = false;
    } else {
      boolean _hasMultiplicity = ValidationHelper.hasMultiplicity(atom);
      boolean _not = (!_hasMultiplicity);
      _and = _not;
    }
    if (_and) {
      this.error(
        "Timewindows on expression atoms are allowed only if multiplicity is also specified.", 
        VeplPackage.Literals.COMPLEX_EVENT_EXPRESSION__TIMEWINDOW, 
        VeplValidator.ATOM_TIMEWINDOW_NO_MULTIPLICITY);
    } else {
      boolean _and_1 = false;
      AbstractMultiplicity _multiplicity = atom.getMultiplicity();
      if (!(_multiplicity instanceof Multiplicity)) {
        _and_1 = false;
      } else {
        AbstractMultiplicity _multiplicity_1 = atom.getMultiplicity();
        int _value = ((Multiplicity) _multiplicity_1).getValue();
        boolean _lessThan = (_value < 2);
        _and_1 = _lessThan;
      }
      if (_and_1) {
        this.error(
          "One atomic event does not result in a valid complex event.", 
          VeplPackage.Literals.COMPLEX_EVENT_EXPRESSION__MULTIPLICITY, 
          VeplValidator.ATOM_TIMEWINDOW_NO_MULTIPLICITY);
      }
    }
  }
  
  @Check
  public void unsupportedMultiplicityTimewindowCombinations(final Atom atom) {
    boolean _and = false;
    boolean _hasMultiplicity = ValidationHelper.hasMultiplicity(atom);
    if (!_hasMultiplicity) {
      _and = false;
    } else {
      boolean _hasTimewindow = ValidationHelper.hasTimewindow(atom);
      _and = _hasTimewindow;
    }
    if (_and) {
      AbstractMultiplicity _multiplicity = atom.getMultiplicity();
      if ((_multiplicity instanceof Infinite)) {
        this.error(
          "Infinite multiplicity cannot be combined with timewindow.", 
          VeplPackage.Literals.COMPLEX_EVENT_EXPRESSION__MULTIPLICITY, 
          VeplValidator.INFINITE_MULTIPLICITY_WITH_TIMEWINDOW);
      }
    }
  }
  
  @Check
  public void complexEventPatternWithPlainAtomExpression(final ComplexEventPattern eventPattern) {
    final ComplexEventExpression expression = eventPattern.getComplexEventExpression();
    boolean _and = false;
    boolean _and_1 = false;
    if (!(expression != null)) {
      _and_1 = false;
    } else {
      EList<ChainedExpression> _right = expression.getRight();
      boolean _isEmpty = _right.isEmpty();
      _and_1 = _isEmpty;
    }
    if (!_and_1) {
      _and = false;
    } else {
      ComplexEventExpression _left = expression.getLeft();
      _and = (_left instanceof Atom);
    }
    if (_and) {
      ComplexEventExpression _left_1 = expression.getLeft();
      final Atom atom = ((Atom) _left_1);
      boolean _hasMultiplicity = ValidationHelper.hasMultiplicity(atom);
      boolean _not = (!_hasMultiplicity);
      if (_not) {
        this.warning(
          "Using a single plain atomic event pattern in the complex event pattern is a bad design.", 
          VeplPackage.Literals.COMPLEX_EVENT_PATTERN__COMPLEX_EVENT_EXPRESSION, 
          VeplValidator.SINGE_PLAIN_ATOM_IN_COMPLEX_EVENT_EXPRESSION);
      }
    }
  }
  
  @Check
  public void unsafeStarOperator(final ComplexEventExpression complexEventExpression) {
    boolean _hasInfiniteMultiplicity = ValidationHelper.hasInfiniteMultiplicity(complexEventExpression);
    boolean _not = (!_hasInfiniteMultiplicity);
    if (_not) {
      return;
    } else {
      boolean _starOperatorIsLast = this.starOperatorIsLast(complexEventExpression);
      if (_starOperatorIsLast) {
        this.error("Unsafe infinite multiplicity operator (\"{*}\").", 
          VeplPackage.Literals.COMPLEX_EVENT_EXPRESSION__MULTIPLICITY, VeplValidator.UNSAFE_INFINITE_MULTIPLICITY);
      }
    }
  }
  
  public boolean starOperatorIsLast(final ComplexEventExpression expression) {
    EObject _eContainer = expression.eContainer();
    if ((_eContainer instanceof ComplexEventPattern)) {
      return true;
    } else {
      EObject _eContainer_1 = expression.eContainer();
      if ((_eContainer_1 instanceof ComplexEventExpression)) {
        EObject _eContainer_2 = expression.eContainer();
        final ComplexEventExpression containerExpression = ((ComplexEventExpression) _eContainer_2);
        EList<ChainedExpression> _right = containerExpression.getRight();
        final Function1<ChainedExpression, Boolean> _function = new Function1<ChainedExpression, Boolean>() {
          @Override
          public Boolean apply(final ChainedExpression che) {
            BinaryOperator _operator = che.getOperator();
            return Boolean.valueOf(ValidationHelper.qualifiesAsFollowingOperator(_operator));
          }
        };
        boolean _exists = IterableExtensions.<ChainedExpression>exists(_right, _function);
        return (!_exists);
      } else {
        EObject _eContainer_3 = expression.eContainer();
        if ((_eContainer_3 instanceof ChainedExpression)) {
          EObject _eContainer_4 = expression.eContainer();
          final ChainedExpression containerExpression_1 = ((ChainedExpression) ((ChainedExpression) _eContainer_4));
          EObject _eContainer_5 = containerExpression_1.eContainer();
          Preconditions.checkArgument((_eContainer_5 instanceof ComplexEventExpression));
          EObject _eContainer_6 = containerExpression_1.eContainer();
          final EList<ChainedExpression> chainedExpressions = ((ComplexEventExpression) _eContainer_6).getRight();
          final List<ChainedExpression> followingExpressions = ValidationHelper.subListFrom(chainedExpressions, expression);
          final Function1<ChainedExpression, Boolean> _function_1 = new Function1<ChainedExpression, Boolean>() {
            @Override
            public Boolean apply(final ChainedExpression che) {
              BinaryOperator _operator = che.getOperator();
              return Boolean.valueOf(ValidationHelper.qualifiesAsFollowingOperator(_operator));
            }
          };
          final boolean hasFollowsExpression = IterableExtensions.<ChainedExpression>exists(followingExpressions, _function_1);
          if (hasFollowsExpression) {
            return false;
          } else {
            EObject _eContainer_7 = containerExpression_1.eContainer();
            return this.starOperatorIsLast(((ComplexEventExpression) _eContainer_7));
          }
        } else {
          throw new IllegalArgumentException();
        }
      }
    }
  }
  
  @Check
  public void negativeOperatorAndOtherOperatorCombinations(final ComplexEventExpression complexEventExpression) {
    NegOperator _negOperator = complexEventExpression.getNegOperator();
    boolean _tripleEquals = (_negOperator == null);
    if (_tripleEquals) {
      return;
    }
    ComplexEventExpression _left = complexEventExpression.getLeft();
    boolean _not = (!(_left instanceof Atom));
    if (_not) {
      return;
    }
    ComplexEventExpression _left_1 = complexEventExpression.getLeft();
    final Atom primary = ((Atom) _left_1);
    final AbstractMultiplicity multiplicity = primary.getMultiplicity();
    boolean _nullOrOneMultiplicity = ValidationHelper.nullOrOneMultiplicity(multiplicity);
    boolean _not_1 = (!_nullOrOneMultiplicity);
    if (_not_1) {
      this.error(
        "Cannot use multiplicity operator with a NOT expression.", 
        VeplPackage.Literals.COMPLEX_EVENT_EXPRESSION__LEFT, 
        VeplValidator.NEGATIVE_WITH_MULTIPLICITY);
    }
    boolean _hasTimewindow = ValidationHelper.hasTimewindow(primary);
    if (_hasTimewindow) {
      this.error(
        "Cannot use timewindow operator with a NOT expression.", 
        VeplPackage.Literals.COMPLEX_EVENT_EXPRESSION__LEFT, 
        VeplValidator.NEGATIVE_WITH_TIMEWINDOW);
    }
  }
  
  @Check
  public void duplicateTraitParameterNamesInDiamondInheritance(final AtomicEventPattern atomicEventPattern) {
    final TraitList traitList = atomicEventPattern.getTraits();
    if ((traitList == null)) {
      return;
    }
    final ArrayList<String> paramNames = Lists.<String>newArrayList();
    final ArrayList<String> errorousParamNames = Lists.<String>newArrayList();
    EList<Trait> _traits = traitList.getTraits();
    for (final Trait trait : _traits) {
      TraitTypedParameterList _parameters = trait.getParameters();
      EList<TypedParameterWithDefaultValue> _parameters_1 = _parameters.getParameters();
      for (final TypedParameterWithDefaultValue param : _parameters_1) {
        {
          final TypedParameter parameter = param.getTypedParameter();
          String _name = parameter.getName();
          boolean _contains = paramNames.contains(_name);
          if (_contains) {
            String _name_1 = parameter.getName();
            errorousParamNames.add(_name_1);
          } else {
            String _name_2 = parameter.getName();
            paramNames.add(_name_2);
          }
        }
      }
    }
    boolean _isEmpty = errorousParamNames.isEmpty();
    boolean _not = (!_isEmpty);
    if (_not) {
      String _foldWithComma = ValidationHelper.foldWithComma(errorousParamNames);
      String _plus = ("Duplicate parameter definition in traits. (" + _foldWithComma);
      this.error(_plus, 
        VeplPackage.Literals.ATOMIC_EVENT_PATTERN__TRAITS, 
        VeplValidator.DUPLICATE_TRAIT_PARAMETER_NAMES);
    }
  }
  
  @Check
  public void traitParameterShadowing(final AtomicEventPattern atomicEventPattern) {
    final TraitList traitList = atomicEventPattern.getTraits();
    if ((traitList == null)) {
      return;
    }
    final ArrayList<String> shadowedParameters = Lists.<String>newArrayList();
    TypedParameterList _parameters = atomicEventPattern.getParameters();
    boolean _tripleNotEquals = (_parameters != null);
    if (_tripleNotEquals) {
      TypedParameterList _parameters_1 = atomicEventPattern.getParameters();
      EList<TypedParameter> _parameters_2 = _parameters_1.getParameters();
      for (final TypedParameter parameter : _parameters_2) {
        EList<Trait> _traits = traitList.getTraits();
        for (final Trait trait : _traits) {
          TraitTypedParameterList _parameters_3 = trait.getParameters();
          EList<TypedParameterWithDefaultValue> _parameters_4 = _parameters_3.getParameters();
          for (final TypedParameterWithDefaultValue traitParam : _parameters_4) {
            TypedParameter _typedParameter = traitParam.getTypedParameter();
            String _name = _typedParameter.getName();
            String _name_1 = parameter.getName();
            boolean _equals = _name.equals(_name_1);
            if (_equals) {
              TypedParameter _typedParameter_1 = traitParam.getTypedParameter();
              String _name_2 = _typedParameter_1.getName();
              shadowedParameters.add(_name_2);
            }
          }
        }
      }
    }
    boolean _isEmpty = shadowedParameters.isEmpty();
    boolean _not = (!_isEmpty);
    if (_not) {
      String _foldWithComma = ValidationHelper.foldWithComma(shadowedParameters);
      String _plus = ("Parameters " + _foldWithComma);
      String _plus_1 = (_plus + " shadow parameters in the associated traits.");
      this.warning(_plus_1, 
        VeplPackage.Literals.ATOMIC_EVENT_PATTERN__TRAITS, 
        VeplValidator.SHADOWED_TRAIT_PARAMETERS);
    }
  }
  
  @Check
  public void traitsAreExperimentalFeature(final Trait trait) {
    this.info(
      "Traits are experimental features, use them carefully.", 
      VeplPackage.Literals.TRAIT__PARAMETERS, 
      VeplValidator.TRAIT_EXPERIMENTAL);
  }
}
