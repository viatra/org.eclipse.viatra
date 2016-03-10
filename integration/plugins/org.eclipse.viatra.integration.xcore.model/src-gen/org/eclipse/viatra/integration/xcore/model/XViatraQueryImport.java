/**
 */
package org.eclipse.viatra.integration.xcore.model;

import org.eclipse.emf.ecore.EObject;

import org.eclipse.viatra.query.patternlanguage.patternLanguage.PatternModel;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>XViatra Query Import</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.viatra.integration.xcore.model.XViatraQueryImport#getImportedPatternModel <em>Imported Pattern Model</em>}</li>
 * </ul>
 *
 * @see org.eclipse.viatra.integration.xcore.model.XcorePackage#getXViatraQueryImport()
 * @model
 * @generated
 */
public interface XViatraQueryImport extends EObject {
    /**
     * Returns the value of the '<em><b>Imported Pattern Model</b></em>' reference.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Imported Pattern Model</em>' reference isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Imported Pattern Model</em>' reference.
     * @see #setImportedPatternModel(PatternModel)
     * @see org.eclipse.viatra.integration.xcore.model.XcorePackage#getXViatraQueryImport_ImportedPatternModel()
     * @model required="true"
     * @generated
     */
    PatternModel getImportedPatternModel();

    /**
     * Sets the value of the '{@link org.eclipse.viatra.integration.xcore.model.XViatraQueryImport#getImportedPatternModel <em>Imported Pattern Model</em>}' reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Imported Pattern Model</em>' reference.
     * @see #getImportedPatternModel()
     * @generated
     */
    void setImportedPatternModel(PatternModel value);

} // XViatraQueryImport
