/**
 */
package org.eclipse.viatra.integration.xcore.model;

import org.eclipse.emf.ecore.xcore.XStructuralFeature;
import org.eclipse.viatra.query.patternlanguage.patternLanguage.Pattern;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>XInc Query Derived Feature</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link org.eclipse.viatra.integration.xcore.model.XIncQueryDerivedFeature#getPattern <em>Pattern</em>}</li>
 *   <li>{@link org.eclipse.viatra.integration.xcore.model.XIncQueryDerivedFeature#isReference <em>Reference</em>}</li>
 * </ul>
 * </p>
 *
 * @see org.eclipse.viatra.integration.xcore.model.XcorePackage#getXIncQueryDerivedFeature()
 * @model
 * @generated
 */
public interface XIncQueryDerivedFeature extends XStructuralFeature {
	/**
     * Returns the value of the '<em><b>Pattern</b></em>' reference.
     * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Pattern</em>' reference isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
     * @return the value of the '<em>Pattern</em>' reference.
     * @see #setPattern(Pattern)
     * @see org.eclipse.viatra.integration.xcore.model.XcorePackage#getXIncQueryDerivedFeature_Pattern()
     * @model
     * @generated
     */
	Pattern getPattern();

	/**
     * Sets the value of the '{@link org.eclipse.viatra.integration.xcore.model.XIncQueryDerivedFeature#getPattern <em>Pattern</em>}' reference.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @param value the new value of the '<em>Pattern</em>' reference.
     * @see #getPattern()
     * @generated
     */
	void setPattern(Pattern value);

	/**
     * Returns the value of the '<em><b>Reference</b></em>' attribute.
     * The default value is <code>"false"</code>.
     * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Reference</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
     * @return the value of the '<em>Reference</em>' attribute.
     * @see #setReference(boolean)
     * @see org.eclipse.viatra.integration.xcore.model.XcorePackage#getXIncQueryDerivedFeature_Reference()
     * @model default="false" required="true"
     * @generated
     */
	boolean isReference();

	/**
     * Sets the value of the '{@link org.eclipse.viatra.integration.xcore.model.XIncQueryDerivedFeature#isReference <em>Reference</em>}' attribute.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @param value the new value of the '<em>Reference</em>' attribute.
     * @see #isReference()
     * @generated
     */
	void setReference(boolean value);

} // XIncQueryDerivedFeature
