/**
 */
package org.eclipse.viatra.cep.vepl.vepl;

import org.eclipse.emf.ecore.EObject;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Parameterized Pattern Call</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.viatra.cep.vepl.vepl.ParameterizedPatternCall#getEventPattern <em>Event Pattern</em>}</li>
 *   <li>{@link org.eclipse.viatra.cep.vepl.vepl.ParameterizedPatternCall#getParameterList <em>Parameter List</em>}</li>
 * </ul>
 *
 * @see org.eclipse.viatra.cep.vepl.vepl.VeplPackage#getParameterizedPatternCall()
 * @model
 * @generated
 */
public interface ParameterizedPatternCall extends EObject
{
  /**
   * Returns the value of the '<em><b>Event Pattern</b></em>' reference.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Event Pattern</em>' reference isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Event Pattern</em>' reference.
   * @see #setEventPattern(EventPattern)
   * @see org.eclipse.viatra.cep.vepl.vepl.VeplPackage#getParameterizedPatternCall_EventPattern()
   * @model
   * @generated
   */
  EventPattern getEventPattern();

  /**
   * Sets the value of the '{@link org.eclipse.viatra.cep.vepl.vepl.ParameterizedPatternCall#getEventPattern <em>Event Pattern</em>}' reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>Event Pattern</em>' reference.
   * @see #getEventPattern()
   * @generated
   */
  void setEventPattern(EventPattern value);

  /**
   * Returns the value of the '<em><b>Parameter List</b></em>' containment reference.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Parameter List</em>' containment reference isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Parameter List</em>' containment reference.
   * @see #setParameterList(PatternCallParameterList)
   * @see org.eclipse.viatra.cep.vepl.vepl.VeplPackage#getParameterizedPatternCall_ParameterList()
   * @model containment="true"
   * @generated
   */
  PatternCallParameterList getParameterList();

  /**
   * Sets the value of the '{@link org.eclipse.viatra.cep.vepl.vepl.ParameterizedPatternCall#getParameterList <em>Parameter List</em>}' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>Parameter List</em>' containment reference.
   * @see #getParameterList()
   * @generated
   */
  void setParameterList(PatternCallParameterList value);

} // ParameterizedPatternCall
