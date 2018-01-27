/**
 */
package org.eclipse.viatra.cep.vepl.vepl;

import org.eclipse.emf.ecore.EObject;

import org.eclipse.xtext.xbase.XExpression;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Typed Parameter With Default Value</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.viatra.cep.vepl.vepl.TypedParameterWithDefaultValue#getTypedParameter <em>Typed Parameter</em>}</li>
 *   <li>{@link org.eclipse.viatra.cep.vepl.vepl.TypedParameterWithDefaultValue#getValue <em>Value</em>}</li>
 * </ul>
 *
 * @see org.eclipse.viatra.cep.vepl.vepl.VeplPackage#getTypedParameterWithDefaultValue()
 * @model
 * @generated
 */
public interface TypedParameterWithDefaultValue extends EObject
{
  /**
   * Returns the value of the '<em><b>Typed Parameter</b></em>' containment reference.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Typed Parameter</em>' containment reference isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Typed Parameter</em>' containment reference.
   * @see #setTypedParameter(TypedParameter)
   * @see org.eclipse.viatra.cep.vepl.vepl.VeplPackage#getTypedParameterWithDefaultValue_TypedParameter()
   * @model containment="true"
   * @generated
   */
  TypedParameter getTypedParameter();

  /**
   * Sets the value of the '{@link org.eclipse.viatra.cep.vepl.vepl.TypedParameterWithDefaultValue#getTypedParameter <em>Typed Parameter</em>}' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>Typed Parameter</em>' containment reference.
   * @see #getTypedParameter()
   * @generated
   */
  void setTypedParameter(TypedParameter value);

  /**
   * Returns the value of the '<em><b>Value</b></em>' containment reference.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Value</em>' containment reference isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Value</em>' containment reference.
   * @see #setValue(XExpression)
   * @see org.eclipse.viatra.cep.vepl.vepl.VeplPackage#getTypedParameterWithDefaultValue_Value()
   * @model containment="true"
   * @generated
   */
  XExpression getValue();

  /**
   * Sets the value of the '{@link org.eclipse.viatra.cep.vepl.vepl.TypedParameterWithDefaultValue#getValue <em>Value</em>}' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>Value</em>' containment reference.
   * @see #getValue()
   * @generated
   */
  void setValue(XExpression value);

} // TypedParameterWithDefaultValue
