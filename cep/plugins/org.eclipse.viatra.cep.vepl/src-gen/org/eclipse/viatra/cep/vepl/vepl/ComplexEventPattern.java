/**
 */
package org.eclipse.viatra.cep.vepl.vepl;


/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Complex Event Pattern</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.viatra.cep.vepl.vepl.ComplexEventPattern#getComplexEventExpression <em>Complex Event Expression</em>}</li>
 *   <li>{@link org.eclipse.viatra.cep.vepl.vepl.ComplexEventPattern#getContext <em>Context</em>}</li>
 * </ul>
 *
 * @see org.eclipse.viatra.cep.vepl.vepl.VeplPackage#getComplexEventPattern()
 * @model
 * @generated
 */
public interface ComplexEventPattern extends EventPattern
{
  /**
   * Returns the value of the '<em><b>Complex Event Expression</b></em>' containment reference.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Complex Event Expression</em>' containment reference isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Complex Event Expression</em>' containment reference.
   * @see #setComplexEventExpression(ComplexEventExpression)
   * @see org.eclipse.viatra.cep.vepl.vepl.VeplPackage#getComplexEventPattern_ComplexEventExpression()
   * @model containment="true"
   * @generated
   */
  ComplexEventExpression getComplexEventExpression();

  /**
   * Sets the value of the '{@link org.eclipse.viatra.cep.vepl.vepl.ComplexEventPattern#getComplexEventExpression <em>Complex Event Expression</em>}' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>Complex Event Expression</em>' containment reference.
   * @see #getComplexEventExpression()
   * @generated
   */
  void setComplexEventExpression(ComplexEventExpression value);

  /**
   * Returns the value of the '<em><b>Context</b></em>' attribute.
   * The literals are from the enumeration {@link org.eclipse.viatra.cep.vepl.vepl.ContextEnum}.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Context</em>' attribute isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Context</em>' attribute.
   * @see org.eclipse.viatra.cep.vepl.vepl.ContextEnum
   * @see #setContext(ContextEnum)
   * @see org.eclipse.viatra.cep.vepl.vepl.VeplPackage#getComplexEventPattern_Context()
   * @model
   * @generated
   */
  ContextEnum getContext();

  /**
   * Sets the value of the '{@link org.eclipse.viatra.cep.vepl.vepl.ComplexEventPattern#getContext <em>Context</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>Context</em>' attribute.
   * @see org.eclipse.viatra.cep.vepl.vepl.ContextEnum
   * @see #getContext()
   * @generated
   */
  void setContext(ContextEnum value);

} // ComplexEventPattern
