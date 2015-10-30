/**
 */
package transformationtrace;

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;

/**
 * <!-- begin-user-doc -->
 * The <b>Package</b> for the model.
 * It contains accessors for the meta objects to represent
 * <ul>
 *   <li>each class,</li>
 *   <li>each feature of each class,</li>
 *   <li>each operation of each class,</li>
 *   <li>each enum,</li>
 *   <li>and each data type</li>
 * </ul>
 * <!-- end-user-doc -->
 * @see transformationtrace.TransformationtraceFactory
 * @model kind="package"
 * @generated
 */
public interface TransformationtracePackage extends EPackage {
    /**
     * The package name.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    String eNAME = "transformationtrace";

    /**
     * The package namespace URI.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    String eNS_URI = "http://org.eclipse.viatra/model/transformationtrace";

    /**
     * The package namespace name.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    String eNS_PREFIX = "transformationtrace";

    /**
     * The singleton instance of the package.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    TransformationtracePackage eINSTANCE = transformationtrace.impl.TransformationtracePackageImpl.init();

    /**
     * The meta object id for the '{@link transformationtrace.impl.TransformationTraceImpl <em>Transformation Trace</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see transformationtrace.impl.TransformationTraceImpl
     * @see transformationtrace.impl.TransformationtracePackageImpl#getTransformationTrace()
     * @generated
     */
    int TRANSFORMATION_TRACE = 0;

    /**
     * The feature id for the '<em><b>Activation Traces</b></em>' containment reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int TRANSFORMATION_TRACE__ACTIVATION_TRACES = 0;

    /**
     * The number of structural features of the '<em>Transformation Trace</em>' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int TRANSFORMATION_TRACE_FEATURE_COUNT = 1;

    /**
     * The number of operations of the '<em>Transformation Trace</em>' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int TRANSFORMATION_TRACE_OPERATION_COUNT = 0;

    /**
     * The meta object id for the '{@link transformationtrace.impl.ActivationTraceImpl <em>Activation Trace</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see transformationtrace.impl.ActivationTraceImpl
     * @see transformationtrace.impl.TransformationtracePackageImpl#getActivationTrace()
     * @generated
     */
    int ACTIVATION_TRACE = 1;

    /**
     * The feature id for the '<em><b>Rule Name</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int ACTIVATION_TRACE__RULE_NAME = 0;

    /**
     * The feature id for the '<em><b>Rule Parameter Traces</b></em>' containment reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int ACTIVATION_TRACE__RULE_PARAMETER_TRACES = 1;

    /**
     * The number of structural features of the '<em>Activation Trace</em>' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int ACTIVATION_TRACE_FEATURE_COUNT = 2;

    /**
     * The number of operations of the '<em>Activation Trace</em>' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int ACTIVATION_TRACE_OPERATION_COUNT = 0;

    /**
     * The meta object id for the '{@link transformationtrace.impl.RuleParameterTraceImpl <em>Rule Parameter Trace</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see transformationtrace.impl.RuleParameterTraceImpl
     * @see transformationtrace.impl.TransformationtracePackageImpl#getRuleParameterTrace()
     * @generated
     */
    int RULE_PARAMETER_TRACE = 2;

    /**
     * The feature id for the '<em><b>Parameter Name</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int RULE_PARAMETER_TRACE__PARAMETER_NAME = 0;

    /**
     * The feature id for the '<em><b>Object Id</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int RULE_PARAMETER_TRACE__OBJECT_ID = 1;

    /**
     * The number of structural features of the '<em>Rule Parameter Trace</em>' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int RULE_PARAMETER_TRACE_FEATURE_COUNT = 2;

    /**
     * The number of operations of the '<em>Rule Parameter Trace</em>' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int RULE_PARAMETER_TRACE_OPERATION_COUNT = 0;


    /**
     * Returns the meta object for class '{@link transformationtrace.TransformationTrace <em>Transformation Trace</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for class '<em>Transformation Trace</em>'.
     * @see transformationtrace.TransformationTrace
     * @generated
     */
    EClass getTransformationTrace();

    /**
     * Returns the meta object for the containment reference list '{@link transformationtrace.TransformationTrace#getActivationTraces <em>Activation Traces</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the containment reference list '<em>Activation Traces</em>'.
     * @see transformationtrace.TransformationTrace#getActivationTraces()
     * @see #getTransformationTrace()
     * @generated
     */
    EReference getTransformationTrace_ActivationTraces();

    /**
     * Returns the meta object for class '{@link transformationtrace.ActivationTrace <em>Activation Trace</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for class '<em>Activation Trace</em>'.
     * @see transformationtrace.ActivationTrace
     * @generated
     */
    EClass getActivationTrace();

    /**
     * Returns the meta object for the attribute '{@link transformationtrace.ActivationTrace#getRuleName <em>Rule Name</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Rule Name</em>'.
     * @see transformationtrace.ActivationTrace#getRuleName()
     * @see #getActivationTrace()
     * @generated
     */
    EAttribute getActivationTrace_RuleName();

    /**
     * Returns the meta object for the containment reference list '{@link transformationtrace.ActivationTrace#getRuleParameterTraces <em>Rule Parameter Traces</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the containment reference list '<em>Rule Parameter Traces</em>'.
     * @see transformationtrace.ActivationTrace#getRuleParameterTraces()
     * @see #getActivationTrace()
     * @generated
     */
    EReference getActivationTrace_RuleParameterTraces();

    /**
     * Returns the meta object for class '{@link transformationtrace.RuleParameterTrace <em>Rule Parameter Trace</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for class '<em>Rule Parameter Trace</em>'.
     * @see transformationtrace.RuleParameterTrace
     * @generated
     */
    EClass getRuleParameterTrace();

    /**
     * Returns the meta object for the attribute '{@link transformationtrace.RuleParameterTrace#getParameterName <em>Parameter Name</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Parameter Name</em>'.
     * @see transformationtrace.RuleParameterTrace#getParameterName()
     * @see #getRuleParameterTrace()
     * @generated
     */
    EAttribute getRuleParameterTrace_ParameterName();

    /**
     * Returns the meta object for the attribute '{@link transformationtrace.RuleParameterTrace#getObjectId <em>Object Id</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Object Id</em>'.
     * @see transformationtrace.RuleParameterTrace#getObjectId()
     * @see #getRuleParameterTrace()
     * @generated
     */
    EAttribute getRuleParameterTrace_ObjectId();

    /**
     * Returns the factory that creates the instances of the model.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the factory that creates the instances of the model.
     * @generated
     */
    TransformationtraceFactory getTransformationtraceFactory();

    /**
     * <!-- begin-user-doc -->
     * Defines literals for the meta objects that represent
     * <ul>
     *   <li>each class,</li>
     *   <li>each feature of each class,</li>
     *   <li>each operation of each class,</li>
     *   <li>each enum,</li>
     *   <li>and each data type</li>
     * </ul>
     * <!-- end-user-doc -->
     * @generated
     */
    interface Literals {
        /**
         * The meta object literal for the '{@link transformationtrace.impl.TransformationTraceImpl <em>Transformation Trace</em>}' class.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @see transformationtrace.impl.TransformationTraceImpl
         * @see transformationtrace.impl.TransformationtracePackageImpl#getTransformationTrace()
         * @generated
         */
        EClass TRANSFORMATION_TRACE = eINSTANCE.getTransformationTrace();

        /**
         * The meta object literal for the '<em><b>Activation Traces</b></em>' containment reference list feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EReference TRANSFORMATION_TRACE__ACTIVATION_TRACES = eINSTANCE.getTransformationTrace_ActivationTraces();

        /**
         * The meta object literal for the '{@link transformationtrace.impl.ActivationTraceImpl <em>Activation Trace</em>}' class.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @see transformationtrace.impl.ActivationTraceImpl
         * @see transformationtrace.impl.TransformationtracePackageImpl#getActivationTrace()
         * @generated
         */
        EClass ACTIVATION_TRACE = eINSTANCE.getActivationTrace();

        /**
         * The meta object literal for the '<em><b>Rule Name</b></em>' attribute feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EAttribute ACTIVATION_TRACE__RULE_NAME = eINSTANCE.getActivationTrace_RuleName();

        /**
         * The meta object literal for the '<em><b>Rule Parameter Traces</b></em>' containment reference list feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EReference ACTIVATION_TRACE__RULE_PARAMETER_TRACES = eINSTANCE.getActivationTrace_RuleParameterTraces();

        /**
         * The meta object literal for the '{@link transformationtrace.impl.RuleParameterTraceImpl <em>Rule Parameter Trace</em>}' class.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @see transformationtrace.impl.RuleParameterTraceImpl
         * @see transformationtrace.impl.TransformationtracePackageImpl#getRuleParameterTrace()
         * @generated
         */
        EClass RULE_PARAMETER_TRACE = eINSTANCE.getRuleParameterTrace();

        /**
         * The meta object literal for the '<em><b>Parameter Name</b></em>' attribute feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EAttribute RULE_PARAMETER_TRACE__PARAMETER_NAME = eINSTANCE.getRuleParameterTrace_ParameterName();

        /**
         * The meta object literal for the '<em><b>Object Id</b></em>' attribute feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EAttribute RULE_PARAMETER_TRACE__OBJECT_ID = eINSTANCE.getRuleParameterTrace_ObjectId();

    }

} //TransformationtracePackage
