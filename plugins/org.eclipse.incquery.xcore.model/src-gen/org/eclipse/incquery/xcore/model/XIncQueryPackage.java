/**
 */
package org.eclipse.incquery.xcore.model;

import org.eclipse.emf.common.util.EList;

import org.eclipse.emf.ecore.xcore.XPackage;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>XInc Query Package</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link org.eclipse.incquery.xcore.model.XIncQueryPackage#getImportedIncQueries <em>Imported Inc Queries</em>}</li>
 * </ul>
 * </p>
 *
 * @see org.eclipse.incquery.xcore.model.XcorePackage#getXIncQueryPackage()
 * @model
 * @generated
 */
public interface XIncQueryPackage extends XPackage {
	/**
	 * Returns the value of the '<em><b>Imported Inc Queries</b></em>' containment reference list.
	 * The list contents are of type {@link org.eclipse.incquery.xcore.model.XIncQueryImport}.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Imported Inc Queries</em>' containment reference list isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Imported Inc Queries</em>' containment reference list.
	 * @see org.eclipse.incquery.xcore.model.XcorePackage#getXIncQueryPackage_ImportedIncQueries()
	 * @model containment="true"
	 * @generated
	 */
	EList<XIncQueryImport> getImportedIncQueries();

} // XIncQueryPackage
