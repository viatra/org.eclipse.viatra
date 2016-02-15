/**
 */
package org.eclipse.incquery.examples.eiqlibrary;

import org.eclipse.emf.ecore.EFactory;

/**
 * <!-- begin-user-doc -->
 * The <b>Factory</b> for the model.
 * It provides a create method for each non-abstract class of the model.
 * <!-- end-user-doc -->
 * @see org.eclipse.incquery.examples.eiqlibrary.EIQLibraryPackage
 * @generated
 */
public interface EIQLibraryFactory extends EFactory {
    /**
     * The singleton instance of the factory.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EIQLibraryFactory eINSTANCE = org.eclipse.incquery.examples.eiqlibrary.impl.EIQLibraryFactoryImpl.init();

    /**
     * Returns a new object of class '<em>Library</em>'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return a new object of class '<em>Library</em>'.
     * @generated
     */
    Library createLibrary();

    /**
     * Returns a new object of class '<em>Book</em>'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return a new object of class '<em>Book</em>'.
     * @generated
     */
    Book createBook();

    /**
     * Returns a new object of class '<em>Writer</em>'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return a new object of class '<em>Writer</em>'.
     * @generated
     */
    Writer createWriter();

    /**
     * Returns the package supported by this factory.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the package supported by this factory.
     * @generated
     */
    EIQLibraryPackage getEIQLibraryPackage();

} //EIQLibraryFactory
