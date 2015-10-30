/**
 */
package targetModel;

import org.eclipse.emf.ecore.EFactory;

/**
 * <!-- begin-user-doc -->
 * The <b>Factory</b> for the model.
 * It provides a create method for each non-abstract class of the model.
 * <!-- end-user-doc -->
 * @see targetModel.TargetModelPackage
 * @generated
 */
public interface TargetModelFactory extends EFactory {
    /**
     * The singleton instance of the factory.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    TargetModelFactory eINSTANCE = targetModel.impl.TargetModelFactoryImpl.init();

    /**
     * Returns a new object of class '<em>TModel</em>'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return a new object of class '<em>TModel</em>'.
     * @generated
     */
    TModel createTModel();

    /**
     * Returns a new object of class '<em>TClass</em>'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return a new object of class '<em>TClass</em>'.
     * @generated
     */
    TClass createTClass();

    /**
     * Returns a new object of class '<em>TProperty</em>'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return a new object of class '<em>TProperty</em>'.
     * @generated
     */
    TProperty createTProperty();

    /**
     * Returns a new object of class '<em>TOperation</em>'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return a new object of class '<em>TOperation</em>'.
     * @generated
     */
    TOperation createTOperation();

    /**
     * Returns the package supported by this factory.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the package supported by this factory.
     * @generated
     */
    TargetModelPackage getTargetModelPackage();

} //TargetModelFactory
