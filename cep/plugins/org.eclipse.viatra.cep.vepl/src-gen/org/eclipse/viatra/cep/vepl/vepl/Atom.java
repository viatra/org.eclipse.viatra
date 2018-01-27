/**
 */
package org.eclipse.viatra.cep.vepl.vepl;


/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Atom</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.viatra.cep.vepl.vepl.Atom#getPatternCall <em>Pattern Call</em>}</li>
 * </ul>
 *
 * @see org.eclipse.viatra.cep.vepl.vepl.VeplPackage#getAtom()
 * @model
 * @generated
 */
public interface Atom extends ComplexEventExpression
{
  /**
   * Returns the value of the '<em><b>Pattern Call</b></em>' containment reference.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Pattern Call</em>' containment reference isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Pattern Call</em>' containment reference.
   * @see #setPatternCall(ParameterizedPatternCall)
   * @see org.eclipse.viatra.cep.vepl.vepl.VeplPackage#getAtom_PatternCall()
   * @model containment="true"
   * @generated
   */
  ParameterizedPatternCall getPatternCall();

  /**
   * Sets the value of the '{@link org.eclipse.viatra.cep.vepl.vepl.Atom#getPatternCall <em>Pattern Call</em>}' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>Pattern Call</em>' containment reference.
   * @see #getPatternCall()
   * @generated
   */
  void setPatternCall(ParameterizedPatternCall value);

} // Atom
