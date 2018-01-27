/**
 */
package org.eclipse.viatra.cep.vepl.vepl;

import org.eclipse.emf.common.util.EList;

import org.eclipse.xtext.xbase.XExpression;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Rule</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.viatra.cep.vepl.vepl.Rule#getEventPatterns <em>Event Patterns</em>}</li>
 *   <li>{@link org.eclipse.viatra.cep.vepl.vepl.Rule#getAction <em>Action</em>}</li>
 * </ul>
 *
 * @see org.eclipse.viatra.cep.vepl.vepl.VeplPackage#getRule()
 * @model
 * @generated
 */
public interface Rule extends ModelElement
{
  /**
   * Returns the value of the '<em><b>Event Patterns</b></em>' containment reference list.
   * The list contents are of type {@link org.eclipse.viatra.cep.vepl.vepl.ParameterizedPatternCall}.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Event Patterns</em>' containment reference list isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Event Patterns</em>' containment reference list.
   * @see org.eclipse.viatra.cep.vepl.vepl.VeplPackage#getRule_EventPatterns()
   * @model containment="true"
   * @generated
   */
  EList<ParameterizedPatternCall> getEventPatterns();

  /**
   * Returns the value of the '<em><b>Action</b></em>' containment reference.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Action</em>' containment reference isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Action</em>' containment reference.
   * @see #setAction(XExpression)
   * @see org.eclipse.viatra.cep.vepl.vepl.VeplPackage#getRule_Action()
   * @model containment="true"
   * @generated
   */
  XExpression getAction();

  /**
   * Sets the value of the '{@link org.eclipse.viatra.cep.vepl.vepl.Rule#getAction <em>Action</em>}' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>Action</em>' containment reference.
   * @see #getAction()
   * @generated
   */
  void setAction(XExpression value);

} // Rule
