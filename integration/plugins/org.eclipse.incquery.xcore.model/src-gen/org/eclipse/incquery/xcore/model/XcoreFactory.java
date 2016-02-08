/**
 */
package org.eclipse.incquery.xcore.model;

import org.eclipse.emf.ecore.EFactory;

/**
 * <!-- begin-user-doc -->
 * The <b>Factory</b> for the model.
 * It provides a create method for each non-abstract class of the model.
 * <!-- end-user-doc -->
 * @see org.eclipse.incquery.xcore.model.XcorePackage
 * @generated
 */
public interface XcoreFactory extends EFactory {
	/**
     * The singleton instance of the factory.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     */
	XcoreFactory eINSTANCE = org.eclipse.incquery.xcore.model.impl.XcoreFactoryImpl.init();

	/**
     * Returns a new object of class '<em>XInc Query Derived Feature</em>'.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @return a new object of class '<em>XInc Query Derived Feature</em>'.
     * @generated
     */
	XIncQueryDerivedFeature createXIncQueryDerivedFeature();

	/**
     * Returns a new object of class '<em>XInc Query Package</em>'.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @return a new object of class '<em>XInc Query Package</em>'.
     * @generated
     */
	XIncQueryPackage createXIncQueryPackage();

	/**
     * Returns a new object of class '<em>XInc Query Import</em>'.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @return a new object of class '<em>XInc Query Import</em>'.
     * @generated
     */
	XIncQueryImport createXIncQueryImport();

	/**
     * Returns the package supported by this factory.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @return the package supported by this factory.
     * @generated
     */
	XcorePackage getXcorePackage();

} //XcoreFactory
