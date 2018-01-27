/**
 */
package org.eclipse.viatra.cep.vepl.vepl;

import org.eclipse.emf.ecore.EFactory;

/**
 * <!-- begin-user-doc -->
 * The <b>Factory</b> for the model.
 * It provides a create method for each non-abstract class of the model.
 * <!-- end-user-doc -->
 * @see org.eclipse.viatra.cep.vepl.vepl.VeplPackage
 * @generated
 */
public interface VeplFactory extends EFactory
{
  /**
   * The singleton instance of the factory.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  VeplFactory eINSTANCE = org.eclipse.viatra.cep.vepl.vepl.impl.VeplFactoryImpl.init();

  /**
   * Returns a new object of class '<em>Event Model</em>'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return a new object of class '<em>Event Model</em>'.
   * @generated
   */
  EventModel createEventModel();

  /**
   * Returns a new object of class '<em>Import</em>'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return a new object of class '<em>Import</em>'.
   * @generated
   */
  Import createImport();

  /**
   * Returns a new object of class '<em>Generic Import</em>'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return a new object of class '<em>Generic Import</em>'.
   * @generated
   */
  GenericImport createGenericImport();

  /**
   * Returns a new object of class '<em>Query Import</em>'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return a new object of class '<em>Query Import</em>'.
   * @generated
   */
  QueryImport createQueryImport();

  /**
   * Returns a new object of class '<em>Model Element</em>'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return a new object of class '<em>Model Element</em>'.
   * @generated
   */
  ModelElement createModelElement();

  /**
   * Returns a new object of class '<em>Trait</em>'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return a new object of class '<em>Trait</em>'.
   * @generated
   */
  Trait createTrait();

  /**
   * Returns a new object of class '<em>Event Pattern</em>'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return a new object of class '<em>Event Pattern</em>'.
   * @generated
   */
  EventPattern createEventPattern();

  /**
   * Returns a new object of class '<em>Abstract Atomic Event Pattern</em>'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return a new object of class '<em>Abstract Atomic Event Pattern</em>'.
   * @generated
   */
  AbstractAtomicEventPattern createAbstractAtomicEventPattern();

  /**
   * Returns a new object of class '<em>Atomic Event Pattern</em>'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return a new object of class '<em>Atomic Event Pattern</em>'.
   * @generated
   */
  AtomicEventPattern createAtomicEventPattern();

  /**
   * Returns a new object of class '<em>Query Result Change Event Pattern</em>'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return a new object of class '<em>Query Result Change Event Pattern</em>'.
   * @generated
   */
  QueryResultChangeEventPattern createQueryResultChangeEventPattern();

  /**
   * Returns a new object of class '<em>Complex Event Pattern</em>'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return a new object of class '<em>Complex Event Pattern</em>'.
   * @generated
   */
  ComplexEventPattern createComplexEventPattern();

  /**
   * Returns a new object of class '<em>Rule</em>'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return a new object of class '<em>Rule</em>'.
   * @generated
   */
  Rule createRule();

  /**
   * Returns a new object of class '<em>Trait List</em>'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return a new object of class '<em>Trait List</em>'.
   * @generated
   */
  TraitList createTraitList();

  /**
   * Returns a new object of class '<em>Typed Parameter List</em>'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return a new object of class '<em>Typed Parameter List</em>'.
   * @generated
   */
  TypedParameterList createTypedParameterList();

  /**
   * Returns a new object of class '<em>Typed Parameter</em>'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return a new object of class '<em>Typed Parameter</em>'.
   * @generated
   */
  TypedParameter createTypedParameter();

  /**
   * Returns a new object of class '<em>Typed Parameter With Default Value</em>'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return a new object of class '<em>Typed Parameter With Default Value</em>'.
   * @generated
   */
  TypedParameterWithDefaultValue createTypedParameterWithDefaultValue();

  /**
   * Returns a new object of class '<em>Trait Typed Parameter List</em>'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return a new object of class '<em>Trait Typed Parameter List</em>'.
   * @generated
   */
  TraitTypedParameterList createTraitTypedParameterList();

  /**
   * Returns a new object of class '<em>Parametrized Query Reference</em>'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return a new object of class '<em>Parametrized Query Reference</em>'.
   * @generated
   */
  ParametrizedQueryReference createParametrizedQueryReference();

  /**
   * Returns a new object of class '<em>Complex Event Expression</em>'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return a new object of class '<em>Complex Event Expression</em>'.
   * @generated
   */
  ComplexEventExpression createComplexEventExpression();

  /**
   * Returns a new object of class '<em>Chained Expression</em>'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return a new object of class '<em>Chained Expression</em>'.
   * @generated
   */
  ChainedExpression createChainedExpression();

  /**
   * Returns a new object of class '<em>Atom</em>'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return a new object of class '<em>Atom</em>'.
   * @generated
   */
  Atom createAtom();

  /**
   * Returns a new object of class '<em>Abstract Multiplicity</em>'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return a new object of class '<em>Abstract Multiplicity</em>'.
   * @generated
   */
  AbstractMultiplicity createAbstractMultiplicity();

  /**
   * Returns a new object of class '<em>Timewindow</em>'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return a new object of class '<em>Timewindow</em>'.
   * @generated
   */
  Timewindow createTimewindow();

  /**
   * Returns a new object of class '<em>Parameterized Pattern Call</em>'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return a new object of class '<em>Parameterized Pattern Call</em>'.
   * @generated
   */
  ParameterizedPatternCall createParameterizedPatternCall();

  /**
   * Returns a new object of class '<em>Pattern Call Parameter List</em>'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return a new object of class '<em>Pattern Call Parameter List</em>'.
   * @generated
   */
  PatternCallParameterList createPatternCallParameterList();

  /**
   * Returns a new object of class '<em>Pattern Call Parameter</em>'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return a new object of class '<em>Pattern Call Parameter</em>'.
   * @generated
   */
  PatternCallParameter createPatternCallParameter();

  /**
   * Returns a new object of class '<em>Complex Event Operator</em>'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return a new object of class '<em>Complex Event Operator</em>'.
   * @generated
   */
  ComplexEventOperator createComplexEventOperator();

  /**
   * Returns a new object of class '<em>Binary Operator</em>'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return a new object of class '<em>Binary Operator</em>'.
   * @generated
   */
  BinaryOperator createBinaryOperator();

  /**
   * Returns a new object of class '<em>Unary Operator</em>'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return a new object of class '<em>Unary Operator</em>'.
   * @generated
   */
  UnaryOperator createUnaryOperator();

  /**
   * Returns a new object of class '<em>Follows Operator</em>'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return a new object of class '<em>Follows Operator</em>'.
   * @generated
   */
  FollowsOperator createFollowsOperator();

  /**
   * Returns a new object of class '<em>Or Operator</em>'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return a new object of class '<em>Or Operator</em>'.
   * @generated
   */
  OrOperator createOrOperator();

  /**
   * Returns a new object of class '<em>And Operator</em>'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return a new object of class '<em>And Operator</em>'.
   * @generated
   */
  AndOperator createAndOperator();

  /**
   * Returns a new object of class '<em>Until Operator</em>'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return a new object of class '<em>Until Operator</em>'.
   * @generated
   */
  UntilOperator createUntilOperator();

  /**
   * Returns a new object of class '<em>Neg Operator</em>'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return a new object of class '<em>Neg Operator</em>'.
   * @generated
   */
  NegOperator createNegOperator();

  /**
   * Returns a new object of class '<em>Multiplicity</em>'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return a new object of class '<em>Multiplicity</em>'.
   * @generated
   */
  Multiplicity createMultiplicity();

  /**
   * Returns a new object of class '<em>Infinite</em>'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return a new object of class '<em>Infinite</em>'.
   * @generated
   */
  Infinite createInfinite();

  /**
   * Returns a new object of class '<em>At Least One</em>'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return a new object of class '<em>At Least One</em>'.
   * @generated
   */
  AtLeastOne createAtLeastOne();

  /**
   * Returns the package supported by this factory.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the package supported by this factory.
   * @generated
   */
  VeplPackage getVeplPackage();

} //VeplFactory
