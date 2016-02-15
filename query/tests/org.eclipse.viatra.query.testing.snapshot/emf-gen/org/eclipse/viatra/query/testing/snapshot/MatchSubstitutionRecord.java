/**
 */
package org.eclipse.viatra.query.testing.snapshot;

import org.eclipse.emf.ecore.EObject;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Match Substitution Record</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.viatra.query.testing.snapshot.MatchSubstitutionRecord#getParameterName <em>Parameter Name</em>}</li>
 *   <li>{@link org.eclipse.viatra.query.testing.snapshot.MatchSubstitutionRecord#getDerivedValue <em>Derived Value</em>}</li>
 * </ul>
 *
 * @see org.eclipse.viatra.query.testing.snapshot.SnapshotPackage#getMatchSubstitutionRecord()
 * @model abstract="true"
 * @generated
 */
public interface MatchSubstitutionRecord extends EObject {
	/**
	 * Returns the value of the '<em><b>Parameter Name</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Parameter Name</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Parameter Name</em>' attribute.
	 * @see #setParameterName(String)
	 * @see org.eclipse.viatra.query.testing.snapshot.SnapshotPackage#getMatchSubstitutionRecord_ParameterName()
	 * @model
	 * @generated
	 */
	String getParameterName();

	/**
	 * Sets the value of the '{@link org.eclipse.viatra.query.testing.snapshot.MatchSubstitutionRecord#getParameterName <em>Parameter Name</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Parameter Name</em>' attribute.
	 * @see #getParameterName()
	 * @generated
	 */
	void setParameterName(String value);

	/**
	 * Returns the value of the '<em><b>Derived Value</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Derived Value</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Derived Value</em>' attribute.
	 * @see org.eclipse.viatra.query.testing.snapshot.SnapshotPackage#getMatchSubstitutionRecord_DerivedValue()
	 * @model transient="true" changeable="false" volatile="true" derived="true"
	 *        annotation="org.eclipse.viatra.query.querybasedfeature patternFQN='org.eclipse.viatra.query.testing.queries.SubstitutionValue'"
	 * @generated
	 */
	Object getDerivedValue();

} // MatchSubstitutionRecord
