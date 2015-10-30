/**
 */
package transformationtrace.impl;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;

import org.eclipse.emf.ecore.impl.EFactoryImpl;

import org.eclipse.emf.ecore.plugin.EcorePlugin;

import transformationtrace.*;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model <b>Factory</b>.
 * <!-- end-user-doc -->
 * @generated
 */
public class TransformationtraceFactoryImpl extends EFactoryImpl implements TransformationtraceFactory {
    /**
     * Creates the default factory implementation.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public static TransformationtraceFactory init() {
        try {
            TransformationtraceFactory theTransformationtraceFactory = (TransformationtraceFactory)EPackage.Registry.INSTANCE.getEFactory(TransformationtracePackage.eNS_URI);
            if (theTransformationtraceFactory != null) {
                return theTransformationtraceFactory;
            }
        }
        catch (Exception exception) {
            EcorePlugin.INSTANCE.log(exception);
        }
        return new TransformationtraceFactoryImpl();
    }

    /**
     * Creates an instance of the factory.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public TransformationtraceFactoryImpl() {
        super();
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public EObject create(EClass eClass) {
        switch (eClass.getClassifierID()) {
            case TransformationtracePackage.TRANSFORMATION_TRACE: return createTransformationTrace();
            case TransformationtracePackage.ACTIVATION_TRACE: return createActivationTrace();
            case TransformationtracePackage.RULE_PARAMETER_TRACE: return createRuleParameterTrace();
            default:
                throw new IllegalArgumentException("The class '" + eClass.getName() + "' is not a valid classifier");
        }
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public TransformationTrace createTransformationTrace() {
        TransformationTraceImpl transformationTrace = new TransformationTraceImpl();
        return transformationTrace;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public ActivationTrace createActivationTrace() {
        ActivationTraceImpl activationTrace = new ActivationTraceImpl();
        return activationTrace;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public RuleParameterTrace createRuleParameterTrace() {
        RuleParameterTraceImpl ruleParameterTrace = new RuleParameterTraceImpl();
        return ruleParameterTrace;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public TransformationtracePackage getTransformationtracePackage() {
        return (TransformationtracePackage)getEPackage();
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @deprecated
     * @generated
     */
    @Deprecated
    public static TransformationtracePackage getPackage() {
        return TransformationtracePackage.eINSTANCE;
    }

} //TransformationtraceFactoryImpl
