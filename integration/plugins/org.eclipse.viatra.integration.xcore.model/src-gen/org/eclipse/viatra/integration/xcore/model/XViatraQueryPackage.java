/**
 */
package org.eclipse.viatra.integration.xcore.model;

import org.eclipse.emf.common.util.EList;

import org.eclipse.emf.ecore.xcore.XPackage;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>XViatra Query Package</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.viatra.integration.xcore.model.XViatraQueryPackage#getImportedIncQueries <em>Imported Inc Queries</em>}</li>
 * </ul>
 *
 * @see org.eclipse.viatra.integration.xcore.model.XcorePackage#getXViatraQueryPackage()
 * @model
 * @generated
 */
public interface XViatraQueryPackage extends XPackage {
    /**
     * Returns the value of the '<em><b>Imported Inc Queries</b></em>' containment reference list.
     * The list contents are of type {@link org.eclipse.viatra.integration.xcore.model.XViatraQueryImport}.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Imported Inc Queries</em>' containment reference list isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Imported Inc Queries</em>' containment reference list.
     * @see org.eclipse.viatra.integration.xcore.model.XcorePackage#getXViatraQueryPackage_ImportedIncQueries()
     * @model containment="true"
     * @generated
     */
    EList<XViatraQueryImport> getImportedIncQueries();

} // XViatraQueryPackage
