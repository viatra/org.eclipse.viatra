/**
 */
package org.eclipse.viatra.cep.vepl.vepl;

import org.eclipse.xtext.xbase.XExpression;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Atomic Event Pattern</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.viatra.cep.vepl.vepl.AtomicEventPattern#getTraits <em>Traits</em>}</li>
 *   <li>{@link org.eclipse.viatra.cep.vepl.vepl.AtomicEventPattern#getCheckExpression <em>Check Expression</em>}</li>
 * </ul>
 *
 * @see org.eclipse.viatra.cep.vepl.vepl.VeplPackage#getAtomicEventPattern()
 * @model
 * @generated
 */
public interface AtomicEventPattern extends AbstractAtomicEventPattern
{
  /**
   * Returns the value of the '<em><b>Traits</b></em>' containment reference.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Traits</em>' containment reference isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Traits</em>' containment reference.
   * @see #setTraits(TraitList)
   * @see org.eclipse.viatra.cep.vepl.vepl.VeplPackage#getAtomicEventPattern_Traits()
   * @model containment="true"
   * @generated
   */
  TraitList getTraits();

  /**
   * Sets the value of the '{@link org.eclipse.viatra.cep.vepl.vepl.AtomicEventPattern#getTraits <em>Traits</em>}' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>Traits</em>' containment reference.
   * @see #getTraits()
   * @generated
   */
  void setTraits(TraitList value);

  /**
   * Returns the value of the '<em><b>Check Expression</b></em>' containment reference.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Check Expression</em>' containment reference isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Check Expression</em>' containment reference.
   * @see #setCheckExpression(XExpression)
   * @see org.eclipse.viatra.cep.vepl.vepl.VeplPackage#getAtomicEventPattern_CheckExpression()
   * @model containment="true"
   * @generated
   */
  XExpression getCheckExpression();

  /**
   * Sets the value of the '{@link org.eclipse.viatra.cep.vepl.vepl.AtomicEventPattern#getCheckExpression <em>Check Expression</em>}' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>Check Expression</em>' containment reference.
   * @see #getCheckExpression()
   * @generated
   */
  void setCheckExpression(XExpression value);

} // AtomicEventPattern
