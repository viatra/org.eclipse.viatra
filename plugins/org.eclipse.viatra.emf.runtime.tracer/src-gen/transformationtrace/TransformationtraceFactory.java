/**
 */
package transformationtrace;

import org.eclipse.emf.ecore.EFactory;

/**
 * <!-- begin-user-doc -->
 * The <b>Factory</b> for the model.
 * It provides a create method for each non-abstract class of the model.
 * <!-- end-user-doc -->
 * @see transformationtrace.TransformationtracePackage
 * @generated
 */
public interface TransformationtraceFactory extends EFactory {
    /**
     * The singleton instance of the factory.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    TransformationtraceFactory eINSTANCE = transformationtrace.impl.TransformationtraceFactoryImpl.init();

    /**
     * Returns a new object of class '<em>Transformation Trace</em>'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return a new object of class '<em>Transformation Trace</em>'.
     * @generated
     */
    TransformationTrace createTransformationTrace();

    /**
     * Returns a new object of class '<em>Activation Trace</em>'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return a new object of class '<em>Activation Trace</em>'.
     * @generated
     */
    ActivationTrace createActivationTrace();

    /**
     * Returns a new object of class '<em>Rule Parameter Trace</em>'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return a new object of class '<em>Rule Parameter Trace</em>'.
     * @generated
     */
    RuleParameterTrace createRuleParameterTrace();

    /**
     * Returns the package supported by this factory.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the package supported by this factory.
     * @generated
     */
    TransformationtracePackage getTransformationtracePackage();

} //TransformationtraceFactory
