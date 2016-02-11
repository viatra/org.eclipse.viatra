/**
 */
package org.eclipse.viatra.query.testing.snapshot;

import org.eclipse.emf.ecore.EEnum;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Enum Substitution</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.viatra.query.testing.snapshot.EnumSubstitution#getValueLiteral <em>Value Literal</em>}</li>
 *   <li>{@link org.eclipse.viatra.query.testing.snapshot.EnumSubstitution#getEnumType <em>Enum Type</em>}</li>
 * </ul>
 *
 * @see org.eclipse.viatra.query.testing.snapshot.SnapshotPackage#getEnumSubstitution()
 * @model
 * @generated
 */
public interface EnumSubstitution extends MatchSubstitutionRecord {
	/**
	 * Returns the value of the '<em><b>Value Literal</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Value Literal</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Value Literal</em>' attribute.
	 * @see #setValueLiteral(String)
	 * @see org.eclipse.viatra.query.testing.snapshot.SnapshotPackage#getEnumSubstitution_ValueLiteral()
	 * @model
	 * @generated
	 */
	String getValueLiteral();

	/**
	 * Sets the value of the '{@link org.eclipse.viatra.query.testing.snapshot.EnumSubstitution#getValueLiteral <em>Value Literal</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Value Literal</em>' attribute.
	 * @see #getValueLiteral()
	 * @generated
	 */
	void setValueLiteral(String value);

	/**
	 * Returns the value of the '<em><b>Enum Type</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Enum Type</em>' reference isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Enum Type</em>' reference.
	 * @see #setEnumType(EEnum)
	 * @see org.eclipse.viatra.query.testing.snapshot.SnapshotPackage#getEnumSubstitution_EnumType()
	 * @model
	 * @generated
	 */
	EEnum getEnumType();

	/**
	 * Sets the value of the '{@link org.eclipse.viatra.query.testing.snapshot.EnumSubstitution#getEnumType <em>Enum Type</em>}' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Enum Type</em>' reference.
	 * @see #getEnumType()
	 * @generated
	 */
	void setEnumType(EEnum value);

} // EnumSubstitution
