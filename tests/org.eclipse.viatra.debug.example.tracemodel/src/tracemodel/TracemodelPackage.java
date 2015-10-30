/**
 */
package tracemodel;

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
 * @see tracemodel.TracemodelFactory
 * @model kind="package"
 * @generated
 */
public interface TracemodelPackage extends EPackage {
    /**
     * The package name.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    String eNAME = "tracemodel";

    /**
     * The package namespace URI.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    String eNS_URI = "http://www.incquerylabs.com/uml/tracemodel";

    /**
     * The package namespace name.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    String eNS_PREFIX = "tracemodel";

    /**
     * The singleton instance of the package.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    TracemodelPackage eINSTANCE = tracemodel.impl.TracemodelPackageImpl.init();

    /**
     * The meta object id for the '{@link tracemodel.impl.TraceRootImpl <em>Trace Root</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see tracemodel.impl.TraceRootImpl
     * @see tracemodel.impl.TracemodelPackageImpl#getTraceRoot()
     * @generated
     */
    int TRACE_ROOT = 0;

    /**
     * The feature id for the '<em><b>Trace</b></em>' containment reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int TRACE_ROOT__TRACE = 0;

    /**
     * The number of structural features of the '<em>Trace Root</em>' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int TRACE_ROOT_FEATURE_COUNT = 1;

    /**
     * The number of operations of the '<em>Trace Root</em>' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int TRACE_ROOT_OPERATION_COUNT = 0;

    /**
     * The meta object id for the '{@link tracemodel.impl.TraceImpl <em>Trace</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see tracemodel.impl.TraceImpl
     * @see tracemodel.impl.TracemodelPackageImpl#getTrace()
     * @generated
     */
    int TRACE = 1;

    /**
     * The feature id for the '<em><b>Dt UML Element</b></em>' reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int TRACE__DT_UML_ELEMENT = 0;

    /**
     * The feature id for the '<em><b>Uml Element</b></em>' reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int TRACE__UML_ELEMENT = 1;

    /**
     * The number of structural features of the '<em>Trace</em>' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int TRACE_FEATURE_COUNT = 2;

    /**
     * The number of operations of the '<em>Trace</em>' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int TRACE_OPERATION_COUNT = 0;


    /**
     * Returns the meta object for class '{@link tracemodel.TraceRoot <em>Trace Root</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for class '<em>Trace Root</em>'.
     * @see tracemodel.TraceRoot
     * @generated
     */
    EClass getTraceRoot();

    /**
     * Returns the meta object for the containment reference list '{@link tracemodel.TraceRoot#getTrace <em>Trace</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the containment reference list '<em>Trace</em>'.
     * @see tracemodel.TraceRoot#getTrace()
     * @see #getTraceRoot()
     * @generated
     */
    EReference getTraceRoot_Trace();

    /**
     * Returns the meta object for class '{@link tracemodel.Trace <em>Trace</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for class '<em>Trace</em>'.
     * @see tracemodel.Trace
     * @generated
     */
    EClass getTrace();

    /**
     * Returns the meta object for the reference '{@link tracemodel.Trace#getDtUMLElement <em>Dt UML Element</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the reference '<em>Dt UML Element</em>'.
     * @see tracemodel.Trace#getDtUMLElement()
     * @see #getTrace()
     * @generated
     */
    EReference getTrace_DtUMLElement();

    /**
     * Returns the meta object for the reference '{@link tracemodel.Trace#getUmlElement <em>Uml Element</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the reference '<em>Uml Element</em>'.
     * @see tracemodel.Trace#getUmlElement()
     * @see #getTrace()
     * @generated
     */
    EReference getTrace_UmlElement();

    /**
     * Returns the factory that creates the instances of the model.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the factory that creates the instances of the model.
     * @generated
     */
    TracemodelFactory getTracemodelFactory();

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
         * The meta object literal for the '{@link tracemodel.impl.TraceRootImpl <em>Trace Root</em>}' class.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @see tracemodel.impl.TraceRootImpl
         * @see tracemodel.impl.TracemodelPackageImpl#getTraceRoot()
         * @generated
         */
        EClass TRACE_ROOT = eINSTANCE.getTraceRoot();

        /**
         * The meta object literal for the '<em><b>Trace</b></em>' containment reference list feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EReference TRACE_ROOT__TRACE = eINSTANCE.getTraceRoot_Trace();

        /**
         * The meta object literal for the '{@link tracemodel.impl.TraceImpl <em>Trace</em>}' class.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @see tracemodel.impl.TraceImpl
         * @see tracemodel.impl.TracemodelPackageImpl#getTrace()
         * @generated
         */
        EClass TRACE = eINSTANCE.getTrace();

        /**
         * The meta object literal for the '<em><b>Dt UML Element</b></em>' reference feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EReference TRACE__DT_UML_ELEMENT = eINSTANCE.getTrace_DtUMLElement();

        /**
         * The meta object literal for the '<em><b>Uml Element</b></em>' reference feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EReference TRACE__UML_ELEMENT = eINSTANCE.getTrace_UmlElement();

    }

} //TracemodelPackage
