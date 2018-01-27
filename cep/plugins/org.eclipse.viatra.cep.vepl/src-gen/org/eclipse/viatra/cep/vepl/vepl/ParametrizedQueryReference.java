/**
 */
package org.eclipse.viatra.cep.vepl.vepl;

import org.eclipse.emf.ecore.EObject;

import org.eclipse.viatra.query.patternlanguage.patternLanguage.Pattern;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Parametrized Query Reference</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.viatra.cep.vepl.vepl.ParametrizedQueryReference#getQuery <em>Query</em>}</li>
 *   <li>{@link org.eclipse.viatra.cep.vepl.vepl.ParametrizedQueryReference#getParameterList <em>Parameter List</em>}</li>
 * </ul>
 *
 * @see org.eclipse.viatra.cep.vepl.vepl.VeplPackage#getParametrizedQueryReference()
 * @model
 * @generated
 */
public interface ParametrizedQueryReference extends EObject
{
  /**
   * Returns the value of the '<em><b>Query</b></em>' reference.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Query</em>' reference isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Query</em>' reference.
   * @see #setQuery(Pattern)
   * @see org.eclipse.viatra.cep.vepl.vepl.VeplPackage#getParametrizedQueryReference_Query()
   * @model
   * @generated
   */
  Pattern getQuery();

  /**
   * Sets the value of the '{@link org.eclipse.viatra.cep.vepl.vepl.ParametrizedQueryReference#getQuery <em>Query</em>}' reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>Query</em>' reference.
   * @see #getQuery()
   * @generated
   */
  void setQuery(Pattern value);

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
   * @see org.eclipse.viatra.cep.vepl.vepl.VeplPackage#getParametrizedQueryReference_ParameterList()
   * @model containment="true"
   * @generated
   */
  PatternCallParameterList getParameterList();

  /**
   * Sets the value of the '{@link org.eclipse.viatra.cep.vepl.vepl.ParametrizedQueryReference#getParameterList <em>Parameter List</em>}' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>Parameter List</em>' containment reference.
   * @see #getParameterList()
   * @generated
   */
  void setParameterList(PatternCallParameterList value);

} // ParametrizedQueryReference
