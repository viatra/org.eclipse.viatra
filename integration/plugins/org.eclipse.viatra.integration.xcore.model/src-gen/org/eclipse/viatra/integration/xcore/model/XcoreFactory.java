/**
 */
package org.eclipse.viatra.integration.xcore.model;

import org.eclipse.emf.ecore.EFactory;

/**
 * <!-- begin-user-doc -->
 * The <b>Factory</b> for the model.
 * It provides a create method for each non-abstract class of the model.
 * <!-- end-user-doc -->
 * @see org.eclipse.viatra.integration.xcore.model.XcorePackage
 * @generated
 */
public interface XcoreFactory extends EFactory {
    /**
     * The singleton instance of the factory.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    XcoreFactory eINSTANCE = org.eclipse.viatra.integration.xcore.model.impl.XcoreFactoryImpl.init();

    /**
     * Returns a new object of class '<em>XViatra Query Derived Feature</em>'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return a new object of class '<em>XViatra Query Derived Feature</em>'.
     * @generated
     */
    XViatraQueryDerivedFeature createXViatraQueryDerivedFeature();

    /**
     * Returns a new object of class '<em>XViatra Query Package</em>'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return a new object of class '<em>XViatra Query Package</em>'.
     * @generated
     */
    XViatraQueryPackage createXViatraQueryPackage();

    /**
     * Returns a new object of class '<em>XViatra Query Import</em>'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return a new object of class '<em>XViatra Query Import</em>'.
     * @generated
     */
    XViatraQueryImport createXViatraQueryImport();

    /**
     * Returns the package supported by this factory.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the package supported by this factory.
     * @generated
     */
    XcorePackage getXcorePackage();

} //XcoreFactory
