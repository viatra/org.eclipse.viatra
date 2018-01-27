/**
 */
package org.eclipse.viatra.cep.vepl.vepl;

import org.eclipse.emf.ecore.EObject;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Chained Expression</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.viatra.cep.vepl.vepl.ChainedExpression#getOperator <em>Operator</em>}</li>
 *   <li>{@link org.eclipse.viatra.cep.vepl.vepl.ChainedExpression#getExpression <em>Expression</em>}</li>
 * </ul>
 *
 * @see org.eclipse.viatra.cep.vepl.vepl.VeplPackage#getChainedExpression()
 * @model
 * @generated
 */
public interface ChainedExpression extends EObject
{
  /**
   * Returns the value of the '<em><b>Operator</b></em>' containment reference.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Operator</em>' containment reference isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Operator</em>' containment reference.
   * @see #setOperator(BinaryOperator)
   * @see org.eclipse.viatra.cep.vepl.vepl.VeplPackage#getChainedExpression_Operator()
   * @model containment="true"
   * @generated
   */
  BinaryOperator getOperator();

  /**
   * Sets the value of the '{@link org.eclipse.viatra.cep.vepl.vepl.ChainedExpression#getOperator <em>Operator</em>}' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>Operator</em>' containment reference.
   * @see #getOperator()
   * @generated
   */
  void setOperator(BinaryOperator value);

  /**
   * Returns the value of the '<em><b>Expression</b></em>' containment reference.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Expression</em>' containment reference isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Expression</em>' containment reference.
   * @see #setExpression(ComplexEventExpression)
   * @see org.eclipse.viatra.cep.vepl.vepl.VeplPackage#getChainedExpression_Expression()
   * @model containment="true"
   * @generated
   */
  ComplexEventExpression getExpression();

  /**
   * Sets the value of the '{@link org.eclipse.viatra.cep.vepl.vepl.ChainedExpression#getExpression <em>Expression</em>}' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>Expression</em>' containment reference.
   * @see #getExpression()
   * @generated
   */
  void setExpression(ComplexEventExpression value);

} // ChainedExpression
