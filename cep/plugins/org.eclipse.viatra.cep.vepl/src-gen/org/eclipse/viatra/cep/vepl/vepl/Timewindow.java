/**
 */
package org.eclipse.viatra.cep.vepl.vepl;

import org.eclipse.emf.ecore.EObject;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Timewindow</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.viatra.cep.vepl.vepl.Timewindow#getLength <em>Length</em>}</li>
 * </ul>
 *
 * @see org.eclipse.viatra.cep.vepl.vepl.VeplPackage#getTimewindow()
 * @model
 * @generated
 */
public interface Timewindow extends EObject
{
  /**
   * Returns the value of the '<em><b>Length</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Length</em>' attribute isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Length</em>' attribute.
   * @see #setLength(int)
   * @see org.eclipse.viatra.cep.vepl.vepl.VeplPackage#getTimewindow_Length()
   * @model
   * @generated
   */
  int getLength();

  /**
   * Sets the value of the '{@link org.eclipse.viatra.cep.vepl.vepl.Timewindow#getLength <em>Length</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>Length</em>' attribute.
   * @see #getLength()
   * @generated
   */
  void setLength(int value);

} // Timewindow
