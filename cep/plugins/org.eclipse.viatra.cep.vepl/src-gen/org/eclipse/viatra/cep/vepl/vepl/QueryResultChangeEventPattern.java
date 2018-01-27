/**
 */
package org.eclipse.viatra.cep.vepl.vepl;


/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Query Result Change Event Pattern</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.viatra.cep.vepl.vepl.QueryResultChangeEventPattern#getQueryReference <em>Query Reference</em>}</li>
 *   <li>{@link org.eclipse.viatra.cep.vepl.vepl.QueryResultChangeEventPattern#getResultChangeType <em>Result Change Type</em>}</li>
 * </ul>
 *
 * @see org.eclipse.viatra.cep.vepl.vepl.VeplPackage#getQueryResultChangeEventPattern()
 * @model
 * @generated
 */
public interface QueryResultChangeEventPattern extends AbstractAtomicEventPattern
{
  /**
   * Returns the value of the '<em><b>Query Reference</b></em>' containment reference.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Query Reference</em>' containment reference isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Query Reference</em>' containment reference.
   * @see #setQueryReference(ParametrizedQueryReference)
   * @see org.eclipse.viatra.cep.vepl.vepl.VeplPackage#getQueryResultChangeEventPattern_QueryReference()
   * @model containment="true"
   * @generated
   */
  ParametrizedQueryReference getQueryReference();

  /**
   * Sets the value of the '{@link org.eclipse.viatra.cep.vepl.vepl.QueryResultChangeEventPattern#getQueryReference <em>Query Reference</em>}' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>Query Reference</em>' containment reference.
   * @see #getQueryReference()
   * @generated
   */
  void setQueryReference(ParametrizedQueryReference value);

  /**
   * Returns the value of the '<em><b>Result Change Type</b></em>' attribute.
   * The literals are from the enumeration {@link org.eclipse.viatra.cep.vepl.vepl.QueryResultChangeType}.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Result Change Type</em>' attribute isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Result Change Type</em>' attribute.
   * @see org.eclipse.viatra.cep.vepl.vepl.QueryResultChangeType
   * @see #setResultChangeType(QueryResultChangeType)
   * @see org.eclipse.viatra.cep.vepl.vepl.VeplPackage#getQueryResultChangeEventPattern_ResultChangeType()
   * @model
   * @generated
   */
  QueryResultChangeType getResultChangeType();

  /**
   * Sets the value of the '{@link org.eclipse.viatra.cep.vepl.vepl.QueryResultChangeEventPattern#getResultChangeType <em>Result Change Type</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>Result Change Type</em>' attribute.
   * @see org.eclipse.viatra.cep.vepl.vepl.QueryResultChangeType
   * @see #getResultChangeType()
   * @generated
   */
  void setResultChangeType(QueryResultChangeType value);

} // QueryResultChangeEventPattern
