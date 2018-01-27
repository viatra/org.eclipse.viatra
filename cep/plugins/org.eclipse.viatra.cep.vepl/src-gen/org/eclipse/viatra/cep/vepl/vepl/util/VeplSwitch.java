/**
 */
package org.eclipse.viatra.cep.vepl.vepl.util;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;

import org.eclipse.emf.ecore.util.Switch;

import org.eclipse.viatra.cep.vepl.vepl.*;

/**
 * <!-- begin-user-doc -->
 * The <b>Switch</b> for the model's inheritance hierarchy.
 * It supports the call {@link #doSwitch(EObject) doSwitch(object)}
 * to invoke the <code>caseXXX</code> method for each class of the model,
 * starting with the actual class of the object
 * and proceeding up the inheritance hierarchy
 * until a non-null result is returned,
 * which is the result of the switch.
 * <!-- end-user-doc -->
 * @see org.eclipse.viatra.cep.vepl.vepl.VeplPackage
 * @generated
 */
public class VeplSwitch<T> extends Switch<T>
{
  /**
   * The cached model package
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  protected static VeplPackage modelPackage;

  /**
   * Creates an instance of the switch.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public VeplSwitch()
  {
    if (modelPackage == null)
    {
      modelPackage = VeplPackage.eINSTANCE;
    }
  }

  /**
   * Checks whether this is a switch for the given package.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param ePackage the package in question.
   * @return whether this is a switch for the given package.
   * @generated
   */
  @Override
  protected boolean isSwitchFor(EPackage ePackage)
  {
    return ePackage == modelPackage;
  }

  /**
   * Calls <code>caseXXX</code> for each class of the model until one returns a non null result; it yields that result.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the first non-null result returned by a <code>caseXXX</code> call.
   * @generated
   */
  @Override
  protected T doSwitch(int classifierID, EObject theEObject)
  {
    switch (classifierID)
    {
      case VeplPackage.EVENT_MODEL:
      {
        EventModel eventModel = (EventModel)theEObject;
        T result = caseEventModel(eventModel);
        if (result == null) result = defaultCase(theEObject);
        return result;
      }
      case VeplPackage.IMPORT:
      {
        Import import_ = (Import)theEObject;
        T result = caseImport(import_);
        if (result == null) result = defaultCase(theEObject);
        return result;
      }
      case VeplPackage.GENERIC_IMPORT:
      {
        GenericImport genericImport = (GenericImport)theEObject;
        T result = caseGenericImport(genericImport);
        if (result == null) result = caseImport(genericImport);
        if (result == null) result = defaultCase(theEObject);
        return result;
      }
      case VeplPackage.QUERY_IMPORT:
      {
        QueryImport queryImport = (QueryImport)theEObject;
        T result = caseQueryImport(queryImport);
        if (result == null) result = caseImport(queryImport);
        if (result == null) result = defaultCase(theEObject);
        return result;
      }
      case VeplPackage.MODEL_ELEMENT:
      {
        ModelElement modelElement = (ModelElement)theEObject;
        T result = caseModelElement(modelElement);
        if (result == null) result = defaultCase(theEObject);
        return result;
      }
      case VeplPackage.TRAIT:
      {
        Trait trait = (Trait)theEObject;
        T result = caseTrait(trait);
        if (result == null) result = caseModelElement(trait);
        if (result == null) result = defaultCase(theEObject);
        return result;
      }
      case VeplPackage.EVENT_PATTERN:
      {
        EventPattern eventPattern = (EventPattern)theEObject;
        T result = caseEventPattern(eventPattern);
        if (result == null) result = caseModelElement(eventPattern);
        if (result == null) result = defaultCase(theEObject);
        return result;
      }
      case VeplPackage.ABSTRACT_ATOMIC_EVENT_PATTERN:
      {
        AbstractAtomicEventPattern abstractAtomicEventPattern = (AbstractAtomicEventPattern)theEObject;
        T result = caseAbstractAtomicEventPattern(abstractAtomicEventPattern);
        if (result == null) result = caseEventPattern(abstractAtomicEventPattern);
        if (result == null) result = caseModelElement(abstractAtomicEventPattern);
        if (result == null) result = defaultCase(theEObject);
        return result;
      }
      case VeplPackage.ATOMIC_EVENT_PATTERN:
      {
        AtomicEventPattern atomicEventPattern = (AtomicEventPattern)theEObject;
        T result = caseAtomicEventPattern(atomicEventPattern);
        if (result == null) result = caseAbstractAtomicEventPattern(atomicEventPattern);
        if (result == null) result = caseEventPattern(atomicEventPattern);
        if (result == null) result = caseModelElement(atomicEventPattern);
        if (result == null) result = defaultCase(theEObject);
        return result;
      }
      case VeplPackage.QUERY_RESULT_CHANGE_EVENT_PATTERN:
      {
        QueryResultChangeEventPattern queryResultChangeEventPattern = (QueryResultChangeEventPattern)theEObject;
        T result = caseQueryResultChangeEventPattern(queryResultChangeEventPattern);
        if (result == null) result = caseAbstractAtomicEventPattern(queryResultChangeEventPattern);
        if (result == null) result = caseEventPattern(queryResultChangeEventPattern);
        if (result == null) result = caseModelElement(queryResultChangeEventPattern);
        if (result == null) result = defaultCase(theEObject);
        return result;
      }
      case VeplPackage.COMPLEX_EVENT_PATTERN:
      {
        ComplexEventPattern complexEventPattern = (ComplexEventPattern)theEObject;
        T result = caseComplexEventPattern(complexEventPattern);
        if (result == null) result = caseEventPattern(complexEventPattern);
        if (result == null) result = caseModelElement(complexEventPattern);
        if (result == null) result = defaultCase(theEObject);
        return result;
      }
      case VeplPackage.RULE:
      {
        Rule rule = (Rule)theEObject;
        T result = caseRule(rule);
        if (result == null) result = caseModelElement(rule);
        if (result == null) result = defaultCase(theEObject);
        return result;
      }
      case VeplPackage.TRAIT_LIST:
      {
        TraitList traitList = (TraitList)theEObject;
        T result = caseTraitList(traitList);
        if (result == null) result = defaultCase(theEObject);
        return result;
      }
      case VeplPackage.TYPED_PARAMETER_LIST:
      {
        TypedParameterList typedParameterList = (TypedParameterList)theEObject;
        T result = caseTypedParameterList(typedParameterList);
        if (result == null) result = defaultCase(theEObject);
        return result;
      }
      case VeplPackage.TYPED_PARAMETER:
      {
        TypedParameter typedParameter = (TypedParameter)theEObject;
        T result = caseTypedParameter(typedParameter);
        if (result == null) result = defaultCase(theEObject);
        return result;
      }
      case VeplPackage.TYPED_PARAMETER_WITH_DEFAULT_VALUE:
      {
        TypedParameterWithDefaultValue typedParameterWithDefaultValue = (TypedParameterWithDefaultValue)theEObject;
        T result = caseTypedParameterWithDefaultValue(typedParameterWithDefaultValue);
        if (result == null) result = defaultCase(theEObject);
        return result;
      }
      case VeplPackage.TRAIT_TYPED_PARAMETER_LIST:
      {
        TraitTypedParameterList traitTypedParameterList = (TraitTypedParameterList)theEObject;
        T result = caseTraitTypedParameterList(traitTypedParameterList);
        if (result == null) result = defaultCase(theEObject);
        return result;
      }
      case VeplPackage.PARAMETRIZED_QUERY_REFERENCE:
      {
        ParametrizedQueryReference parametrizedQueryReference = (ParametrizedQueryReference)theEObject;
        T result = caseParametrizedQueryReference(parametrizedQueryReference);
        if (result == null) result = defaultCase(theEObject);
        return result;
      }
      case VeplPackage.COMPLEX_EVENT_EXPRESSION:
      {
        ComplexEventExpression complexEventExpression = (ComplexEventExpression)theEObject;
        T result = caseComplexEventExpression(complexEventExpression);
        if (result == null) result = defaultCase(theEObject);
        return result;
      }
      case VeplPackage.CHAINED_EXPRESSION:
      {
        ChainedExpression chainedExpression = (ChainedExpression)theEObject;
        T result = caseChainedExpression(chainedExpression);
        if (result == null) result = defaultCase(theEObject);
        return result;
      }
      case VeplPackage.ATOM:
      {
        Atom atom = (Atom)theEObject;
        T result = caseAtom(atom);
        if (result == null) result = caseComplexEventExpression(atom);
        if (result == null) result = defaultCase(theEObject);
        return result;
      }
      case VeplPackage.ABSTRACT_MULTIPLICITY:
      {
        AbstractMultiplicity abstractMultiplicity = (AbstractMultiplicity)theEObject;
        T result = caseAbstractMultiplicity(abstractMultiplicity);
        if (result == null) result = defaultCase(theEObject);
        return result;
      }
      case VeplPackage.TIMEWINDOW:
      {
        Timewindow timewindow = (Timewindow)theEObject;
        T result = caseTimewindow(timewindow);
        if (result == null) result = defaultCase(theEObject);
        return result;
      }
      case VeplPackage.PARAMETERIZED_PATTERN_CALL:
      {
        ParameterizedPatternCall parameterizedPatternCall = (ParameterizedPatternCall)theEObject;
        T result = caseParameterizedPatternCall(parameterizedPatternCall);
        if (result == null) result = defaultCase(theEObject);
        return result;
      }
      case VeplPackage.PATTERN_CALL_PARAMETER_LIST:
      {
        PatternCallParameterList patternCallParameterList = (PatternCallParameterList)theEObject;
        T result = casePatternCallParameterList(patternCallParameterList);
        if (result == null) result = defaultCase(theEObject);
        return result;
      }
      case VeplPackage.PATTERN_CALL_PARAMETER:
      {
        PatternCallParameter patternCallParameter = (PatternCallParameter)theEObject;
        T result = casePatternCallParameter(patternCallParameter);
        if (result == null) result = defaultCase(theEObject);
        return result;
      }
      case VeplPackage.COMPLEX_EVENT_OPERATOR:
      {
        ComplexEventOperator complexEventOperator = (ComplexEventOperator)theEObject;
        T result = caseComplexEventOperator(complexEventOperator);
        if (result == null) result = defaultCase(theEObject);
        return result;
      }
      case VeplPackage.BINARY_OPERATOR:
      {
        BinaryOperator binaryOperator = (BinaryOperator)theEObject;
        T result = caseBinaryOperator(binaryOperator);
        if (result == null) result = caseComplexEventOperator(binaryOperator);
        if (result == null) result = defaultCase(theEObject);
        return result;
      }
      case VeplPackage.UNARY_OPERATOR:
      {
        UnaryOperator unaryOperator = (UnaryOperator)theEObject;
        T result = caseUnaryOperator(unaryOperator);
        if (result == null) result = caseComplexEventOperator(unaryOperator);
        if (result == null) result = defaultCase(theEObject);
        return result;
      }
      case VeplPackage.FOLLOWS_OPERATOR:
      {
        FollowsOperator followsOperator = (FollowsOperator)theEObject;
        T result = caseFollowsOperator(followsOperator);
        if (result == null) result = caseBinaryOperator(followsOperator);
        if (result == null) result = caseComplexEventOperator(followsOperator);
        if (result == null) result = defaultCase(theEObject);
        return result;
      }
      case VeplPackage.OR_OPERATOR:
      {
        OrOperator orOperator = (OrOperator)theEObject;
        T result = caseOrOperator(orOperator);
        if (result == null) result = caseBinaryOperator(orOperator);
        if (result == null) result = caseComplexEventOperator(orOperator);
        if (result == null) result = defaultCase(theEObject);
        return result;
      }
      case VeplPackage.AND_OPERATOR:
      {
        AndOperator andOperator = (AndOperator)theEObject;
        T result = caseAndOperator(andOperator);
        if (result == null) result = caseBinaryOperator(andOperator);
        if (result == null) result = caseComplexEventOperator(andOperator);
        if (result == null) result = defaultCase(theEObject);
        return result;
      }
      case VeplPackage.UNTIL_OPERATOR:
      {
        UntilOperator untilOperator = (UntilOperator)theEObject;
        T result = caseUntilOperator(untilOperator);
        if (result == null) result = defaultCase(theEObject);
        return result;
      }
      case VeplPackage.NEG_OPERATOR:
      {
        NegOperator negOperator = (NegOperator)theEObject;
        T result = caseNegOperator(negOperator);
        if (result == null) result = caseUnaryOperator(negOperator);
        if (result == null) result = caseComplexEventOperator(negOperator);
        if (result == null) result = defaultCase(theEObject);
        return result;
      }
      case VeplPackage.MULTIPLICITY:
      {
        Multiplicity multiplicity = (Multiplicity)theEObject;
        T result = caseMultiplicity(multiplicity);
        if (result == null) result = caseAbstractMultiplicity(multiplicity);
        if (result == null) result = defaultCase(theEObject);
        return result;
      }
      case VeplPackage.INFINITE:
      {
        Infinite infinite = (Infinite)theEObject;
        T result = caseInfinite(infinite);
        if (result == null) result = caseAbstractMultiplicity(infinite);
        if (result == null) result = defaultCase(theEObject);
        return result;
      }
      case VeplPackage.AT_LEAST_ONE:
      {
        AtLeastOne atLeastOne = (AtLeastOne)theEObject;
        T result = caseAtLeastOne(atLeastOne);
        if (result == null) result = caseAbstractMultiplicity(atLeastOne);
        if (result == null) result = defaultCase(theEObject);
        return result;
      }
      default: return defaultCase(theEObject);
    }
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>Event Model</em>'.
   * <!-- begin-user-doc -->
   * This implementation returns null;
   * returning a non-null result will terminate the switch.
   * <!-- end-user-doc -->
   * @param object the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>Event Model</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseEventModel(EventModel object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>Import</em>'.
   * <!-- begin-user-doc -->
   * This implementation returns null;
   * returning a non-null result will terminate the switch.
   * <!-- end-user-doc -->
   * @param object the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>Import</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseImport(Import object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>Generic Import</em>'.
   * <!-- begin-user-doc -->
   * This implementation returns null;
   * returning a non-null result will terminate the switch.
   * <!-- end-user-doc -->
   * @param object the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>Generic Import</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseGenericImport(GenericImport object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>Query Import</em>'.
   * <!-- begin-user-doc -->
   * This implementation returns null;
   * returning a non-null result will terminate the switch.
   * <!-- end-user-doc -->
   * @param object the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>Query Import</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseQueryImport(QueryImport object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>Model Element</em>'.
   * <!-- begin-user-doc -->
   * This implementation returns null;
   * returning a non-null result will terminate the switch.
   * <!-- end-user-doc -->
   * @param object the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>Model Element</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseModelElement(ModelElement object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>Trait</em>'.
   * <!-- begin-user-doc -->
   * This implementation returns null;
   * returning a non-null result will terminate the switch.
   * <!-- end-user-doc -->
   * @param object the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>Trait</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseTrait(Trait object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>Event Pattern</em>'.
   * <!-- begin-user-doc -->
   * This implementation returns null;
   * returning a non-null result will terminate the switch.
   * <!-- end-user-doc -->
   * @param object the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>Event Pattern</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseEventPattern(EventPattern object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>Abstract Atomic Event Pattern</em>'.
   * <!-- begin-user-doc -->
   * This implementation returns null;
   * returning a non-null result will terminate the switch.
   * <!-- end-user-doc -->
   * @param object the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>Abstract Atomic Event Pattern</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseAbstractAtomicEventPattern(AbstractAtomicEventPattern object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>Atomic Event Pattern</em>'.
   * <!-- begin-user-doc -->
   * This implementation returns null;
   * returning a non-null result will terminate the switch.
   * <!-- end-user-doc -->
   * @param object the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>Atomic Event Pattern</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseAtomicEventPattern(AtomicEventPattern object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>Query Result Change Event Pattern</em>'.
   * <!-- begin-user-doc -->
   * This implementation returns null;
   * returning a non-null result will terminate the switch.
   * <!-- end-user-doc -->
   * @param object the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>Query Result Change Event Pattern</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseQueryResultChangeEventPattern(QueryResultChangeEventPattern object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>Complex Event Pattern</em>'.
   * <!-- begin-user-doc -->
   * This implementation returns null;
   * returning a non-null result will terminate the switch.
   * <!-- end-user-doc -->
   * @param object the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>Complex Event Pattern</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseComplexEventPattern(ComplexEventPattern object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>Rule</em>'.
   * <!-- begin-user-doc -->
   * This implementation returns null;
   * returning a non-null result will terminate the switch.
   * <!-- end-user-doc -->
   * @param object the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>Rule</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseRule(Rule object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>Trait List</em>'.
   * <!-- begin-user-doc -->
   * This implementation returns null;
   * returning a non-null result will terminate the switch.
   * <!-- end-user-doc -->
   * @param object the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>Trait List</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseTraitList(TraitList object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>Typed Parameter List</em>'.
   * <!-- begin-user-doc -->
   * This implementation returns null;
   * returning a non-null result will terminate the switch.
   * <!-- end-user-doc -->
   * @param object the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>Typed Parameter List</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseTypedParameterList(TypedParameterList object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>Typed Parameter</em>'.
   * <!-- begin-user-doc -->
   * This implementation returns null;
   * returning a non-null result will terminate the switch.
   * <!-- end-user-doc -->
   * @param object the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>Typed Parameter</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseTypedParameter(TypedParameter object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>Typed Parameter With Default Value</em>'.
   * <!-- begin-user-doc -->
   * This implementation returns null;
   * returning a non-null result will terminate the switch.
   * <!-- end-user-doc -->
   * @param object the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>Typed Parameter With Default Value</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseTypedParameterWithDefaultValue(TypedParameterWithDefaultValue object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>Trait Typed Parameter List</em>'.
   * <!-- begin-user-doc -->
   * This implementation returns null;
   * returning a non-null result will terminate the switch.
   * <!-- end-user-doc -->
   * @param object the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>Trait Typed Parameter List</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseTraitTypedParameterList(TraitTypedParameterList object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>Parametrized Query Reference</em>'.
   * <!-- begin-user-doc -->
   * This implementation returns null;
   * returning a non-null result will terminate the switch.
   * <!-- end-user-doc -->
   * @param object the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>Parametrized Query Reference</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseParametrizedQueryReference(ParametrizedQueryReference object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>Complex Event Expression</em>'.
   * <!-- begin-user-doc -->
   * This implementation returns null;
   * returning a non-null result will terminate the switch.
   * <!-- end-user-doc -->
   * @param object the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>Complex Event Expression</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseComplexEventExpression(ComplexEventExpression object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>Chained Expression</em>'.
   * <!-- begin-user-doc -->
   * This implementation returns null;
   * returning a non-null result will terminate the switch.
   * <!-- end-user-doc -->
   * @param object the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>Chained Expression</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseChainedExpression(ChainedExpression object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>Atom</em>'.
   * <!-- begin-user-doc -->
   * This implementation returns null;
   * returning a non-null result will terminate the switch.
   * <!-- end-user-doc -->
   * @param object the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>Atom</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseAtom(Atom object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>Abstract Multiplicity</em>'.
   * <!-- begin-user-doc -->
   * This implementation returns null;
   * returning a non-null result will terminate the switch.
   * <!-- end-user-doc -->
   * @param object the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>Abstract Multiplicity</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseAbstractMultiplicity(AbstractMultiplicity object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>Timewindow</em>'.
   * <!-- begin-user-doc -->
   * This implementation returns null;
   * returning a non-null result will terminate the switch.
   * <!-- end-user-doc -->
   * @param object the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>Timewindow</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseTimewindow(Timewindow object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>Parameterized Pattern Call</em>'.
   * <!-- begin-user-doc -->
   * This implementation returns null;
   * returning a non-null result will terminate the switch.
   * <!-- end-user-doc -->
   * @param object the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>Parameterized Pattern Call</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseParameterizedPatternCall(ParameterizedPatternCall object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>Pattern Call Parameter List</em>'.
   * <!-- begin-user-doc -->
   * This implementation returns null;
   * returning a non-null result will terminate the switch.
   * <!-- end-user-doc -->
   * @param object the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>Pattern Call Parameter List</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T casePatternCallParameterList(PatternCallParameterList object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>Pattern Call Parameter</em>'.
   * <!-- begin-user-doc -->
   * This implementation returns null;
   * returning a non-null result will terminate the switch.
   * <!-- end-user-doc -->
   * @param object the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>Pattern Call Parameter</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T casePatternCallParameter(PatternCallParameter object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>Complex Event Operator</em>'.
   * <!-- begin-user-doc -->
   * This implementation returns null;
   * returning a non-null result will terminate the switch.
   * <!-- end-user-doc -->
   * @param object the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>Complex Event Operator</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseComplexEventOperator(ComplexEventOperator object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>Binary Operator</em>'.
   * <!-- begin-user-doc -->
   * This implementation returns null;
   * returning a non-null result will terminate the switch.
   * <!-- end-user-doc -->
   * @param object the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>Binary Operator</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseBinaryOperator(BinaryOperator object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>Unary Operator</em>'.
   * <!-- begin-user-doc -->
   * This implementation returns null;
   * returning a non-null result will terminate the switch.
   * <!-- end-user-doc -->
   * @param object the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>Unary Operator</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseUnaryOperator(UnaryOperator object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>Follows Operator</em>'.
   * <!-- begin-user-doc -->
   * This implementation returns null;
   * returning a non-null result will terminate the switch.
   * <!-- end-user-doc -->
   * @param object the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>Follows Operator</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseFollowsOperator(FollowsOperator object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>Or Operator</em>'.
   * <!-- begin-user-doc -->
   * This implementation returns null;
   * returning a non-null result will terminate the switch.
   * <!-- end-user-doc -->
   * @param object the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>Or Operator</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseOrOperator(OrOperator object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>And Operator</em>'.
   * <!-- begin-user-doc -->
   * This implementation returns null;
   * returning a non-null result will terminate the switch.
   * <!-- end-user-doc -->
   * @param object the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>And Operator</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseAndOperator(AndOperator object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>Until Operator</em>'.
   * <!-- begin-user-doc -->
   * This implementation returns null;
   * returning a non-null result will terminate the switch.
   * <!-- end-user-doc -->
   * @param object the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>Until Operator</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseUntilOperator(UntilOperator object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>Neg Operator</em>'.
   * <!-- begin-user-doc -->
   * This implementation returns null;
   * returning a non-null result will terminate the switch.
   * <!-- end-user-doc -->
   * @param object the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>Neg Operator</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseNegOperator(NegOperator object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>Multiplicity</em>'.
   * <!-- begin-user-doc -->
   * This implementation returns null;
   * returning a non-null result will terminate the switch.
   * <!-- end-user-doc -->
   * @param object the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>Multiplicity</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseMultiplicity(Multiplicity object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>Infinite</em>'.
   * <!-- begin-user-doc -->
   * This implementation returns null;
   * returning a non-null result will terminate the switch.
   * <!-- end-user-doc -->
   * @param object the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>Infinite</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseInfinite(Infinite object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>At Least One</em>'.
   * <!-- begin-user-doc -->
   * This implementation returns null;
   * returning a non-null result will terminate the switch.
   * <!-- end-user-doc -->
   * @param object the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>At Least One</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseAtLeastOne(AtLeastOne object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>EObject</em>'.
   * <!-- begin-user-doc -->
   * This implementation returns null;
   * returning a non-null result will terminate the switch, but this is the last case anyway.
   * <!-- end-user-doc -->
   * @param object the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>EObject</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject)
   * @generated
   */
  @Override
  public T defaultCase(EObject object)
  {
    return null;
  }

} //VeplSwitch
