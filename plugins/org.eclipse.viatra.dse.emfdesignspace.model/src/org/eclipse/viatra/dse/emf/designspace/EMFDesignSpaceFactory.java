/**
 */
package org.eclipse.viatra.dse.emf.designspace;

import org.eclipse.emf.ecore.EFactory;

/**
 * <!-- begin-user-doc -->
 * The <b>Factory</b> for the model.
 * It provides a create method for each non-abstract class of the model.
 * <!-- end-user-doc -->
 * @see org.eclipse.viatra.dse.emf.designspace.EMFDesignSpacePackage
 * @generated
 */
public interface EMFDesignSpaceFactory extends EFactory {
    /**
     * The singleton instance of the factory.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EMFDesignSpaceFactory eINSTANCE = org.eclipse.viatra.dse.emf.designspace.impl.EMFDesignSpaceFactoryImpl.init();

    /**
     * Returns a new object of class '<em>Design Space</em>'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return a new object of class '<em>Design Space</em>'.
     * @generated
     */
    DesignSpace createDesignSpace();

    /**
     * Returns a new object of class '<em>State</em>'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return a new object of class '<em>State</em>'.
     * @generated
     */
    State createState();

    /**
     * Returns a new object of class '<em>Transition</em>'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return a new object of class '<em>Transition</em>'.
     * @generated
     */
    Transition createTransition();

    /**
     * Returns the package supported by this factory.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the package supported by this factory.
     * @generated
     */
    EMFDesignSpacePackage getEMFDesignSpacePackage();

} //EMFDesignSpaceFactory
