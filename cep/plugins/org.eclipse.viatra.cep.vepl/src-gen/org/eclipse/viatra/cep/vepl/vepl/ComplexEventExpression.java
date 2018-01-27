/**
 */
package org.eclipse.viatra.cep.vepl.vepl;

import org.eclipse.emf.common.util.EList;

import org.eclipse.emf.ecore.EObject;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Complex Event Expression</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.viatra.cep.vepl.vepl.ComplexEventExpression#getLeft <em>Left</em>}</li>
 *   <li>{@link org.eclipse.viatra.cep.vepl.vepl.ComplexEventExpression#getRight <em>Right</em>}</li>
 *   <li>{@link org.eclipse.viatra.cep.vepl.vepl.ComplexEventExpression#getMultiplicity <em>Multiplicity</em>}</li>
 *   <li>{@link org.eclipse.viatra.cep.vepl.vepl.ComplexEventExpression#getTimewindow <em>Timewindow</em>}</li>
 *   <li>{@link org.eclipse.viatra.cep.vepl.vepl.ComplexEventExpression#getNegOperator <em>Neg Operator</em>}</li>
 * </ul>
 *
 * @see org.eclipse.viatra.cep.vepl.vepl.VeplPackage#getComplexEventExpression()
 * @model
 * @generated
 */
public interface ComplexEventExpression extends EObject
{
  /**
   * Returns the value of the '<em><b>Left</b></em>' containment reference.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Left</em>' containment reference isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Left</em>' containment reference.
   * @see #setLeft(ComplexEventExpression)
   * @see org.eclipse.viatra.cep.vepl.vepl.VeplPackage#getComplexEventExpression_Left()
   * @model containment="true"
   * @generated
   */
  ComplexEventExpression getLeft();

  /**
   * Sets the value of the '{@link org.eclipse.viatra.cep.vepl.vepl.ComplexEventExpression#getLeft <em>Left</em>}' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>Left</em>' containment reference.
   * @see #getLeft()
   * @generated
   */
  void setLeft(ComplexEventExpression value);

  /**
   * Returns the value of the '<em><b>Right</b></em>' containment reference list.
   * The list contents are of type {@link org.eclipse.viatra.cep.vepl.vepl.ChainedExpression}.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Right</em>' containment reference list isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Right</em>' containment reference list.
   * @see org.eclipse.viatra.cep.vepl.vepl.VeplPackage#getComplexEventExpression_Right()
   * @model containment="true"
   * @generated
   */
  EList<ChainedExpression> getRight();

  /**
   * Returns the value of the '<em><b>Multiplicity</b></em>' containment reference.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Multiplicity</em>' containment reference isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Multiplicity</em>' containment reference.
   * @see #setMultiplicity(AbstractMultiplicity)
   * @see org.eclipse.viatra.cep.vepl.vepl.VeplPackage#getComplexEventExpression_Multiplicity()
   * @model containment="true"
   * @generated
   */
  AbstractMultiplicity getMultiplicity();

  /**
   * Sets the value of the '{@link org.eclipse.viatra.cep.vepl.vepl.ComplexEventExpression#getMultiplicity <em>Multiplicity</em>}' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>Multiplicity</em>' containment reference.
   * @see #getMultiplicity()
   * @generated
   */
  void setMultiplicity(AbstractMultiplicity value);

  /**
   * Returns the value of the '<em><b>Timewindow</b></em>' containment reference.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Timewindow</em>' containment reference isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Timewindow</em>' containment reference.
   * @see #setTimewindow(Timewindow)
   * @see org.eclipse.viatra.cep.vepl.vepl.VeplPackage#getComplexEventExpression_Timewindow()
   * @model containment="true"
   * @generated
   */
  Timewindow getTimewindow();

  /**
   * Sets the value of the '{@link org.eclipse.viatra.cep.vepl.vepl.ComplexEventExpression#getTimewindow <em>Timewindow</em>}' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>Timewindow</em>' containment reference.
   * @see #getTimewindow()
   * @generated
   */
  void setTimewindow(Timewindow value);

  /**
   * Returns the value of the '<em><b>Neg Operator</b></em>' containment reference.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Neg Operator</em>' containment reference isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Neg Operator</em>' containment reference.
   * @see #setNegOperator(NegOperator)
   * @see org.eclipse.viatra.cep.vepl.vepl.VeplPackage#getComplexEventExpression_NegOperator()
   * @model containment="true"
   * @generated
   */
  NegOperator getNegOperator();

  /**
   * Sets the value of the '{@link org.eclipse.viatra.cep.vepl.vepl.ComplexEventExpression#getNegOperator <em>Neg Operator</em>}' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>Neg Operator</em>' containment reference.
   * @see #getNegOperator()
   * @generated
   */
  void setNegOperator(NegOperator value);

} // ComplexEventExpression
