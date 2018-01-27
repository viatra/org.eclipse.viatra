/**
 */
package org.eclipse.viatra.cep.vepl.vepl;


/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Event Pattern</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.viatra.cep.vepl.vepl.EventPattern#getParameters <em>Parameters</em>}</li>
 * </ul>
 *
 * @see org.eclipse.viatra.cep.vepl.vepl.VeplPackage#getEventPattern()
 * @model
 * @generated
 */
public interface EventPattern extends ModelElement
{
  /**
   * Returns the value of the '<em><b>Parameters</b></em>' containment reference.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Parameters</em>' containment reference isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Parameters</em>' containment reference.
   * @see #setParameters(TypedParameterList)
   * @see org.eclipse.viatra.cep.vepl.vepl.VeplPackage#getEventPattern_Parameters()
   * @model containment="true"
   * @generated
   */
  TypedParameterList getParameters();

  /**
   * Sets the value of the '{@link org.eclipse.viatra.cep.vepl.vepl.EventPattern#getParameters <em>Parameters</em>}' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>Parameters</em>' containment reference.
   * @see #getParameters()
   * @generated
   */
  void setParameters(TypedParameterList value);

} // EventPattern
