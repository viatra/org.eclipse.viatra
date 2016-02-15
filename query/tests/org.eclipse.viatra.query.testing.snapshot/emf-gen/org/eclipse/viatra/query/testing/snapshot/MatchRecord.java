/**
 */
package org.eclipse.viatra.query.testing.snapshot;

import org.eclipse.emf.common.util.EList;

import org.eclipse.emf.ecore.EObject;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Match Record</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.viatra.query.testing.snapshot.MatchRecord#getSubstitutions <em>Substitutions</em>}</li>
 *   <li>{@link org.eclipse.viatra.query.testing.snapshot.MatchRecord#getRole <em>Role</em>}</li>
 * </ul>
 *
 * @see org.eclipse.viatra.query.testing.snapshot.SnapshotPackage#getMatchRecord()
 * @model
 * @generated
 */
public interface MatchRecord extends EObject {
	/**
	 * Returns the value of the '<em><b>Substitutions</b></em>' containment reference list.
	 * The list contents are of type {@link org.eclipse.viatra.query.testing.snapshot.MatchSubstitutionRecord}.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Substitutions</em>' containment reference list isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Substitutions</em>' containment reference list.
	 * @see org.eclipse.viatra.query.testing.snapshot.SnapshotPackage#getMatchRecord_Substitutions()
	 * @model containment="true"
	 * @generated
	 */
	EList<MatchSubstitutionRecord> getSubstitutions();

	/**
	 * Returns the value of the '<em><b>Role</b></em>' attribute.
	 * The literals are from the enumeration {@link org.eclipse.viatra.query.testing.snapshot.RecordRole}.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Role</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Role</em>' attribute.
	 * @see org.eclipse.viatra.query.testing.snapshot.RecordRole
	 * @see org.eclipse.viatra.query.testing.snapshot.SnapshotPackage#getMatchRecord_Role()
	 * @model transient="true" changeable="false" volatile="true" derived="true"
	 *        annotation="org.eclipse.viatra.query.querybasedfeature patternFQN='org.eclipse.viatra.query.testing.queries.RecordRoleValue'"
	 * @generated
	 */
	RecordRole getRole();

} // MatchRecord
