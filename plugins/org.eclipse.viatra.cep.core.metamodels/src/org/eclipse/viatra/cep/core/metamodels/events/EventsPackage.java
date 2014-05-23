/**
 */
package org.eclipse.viatra.cep.core.metamodels.events;

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EOperation;
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
 * @see org.eclipse.viatra.cep.core.metamodels.events.EventsFactory
 * @model kind="package"
 * @generated
 */
public interface EventsPackage extends EPackage {
    /**
     * The package name.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    String eNAME = "events";

    /**
     * The package namespace URI.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    String eNS_URI = "cep.meta";

    /**
     * The package namespace name.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    String eNS_PREFIX = "org.eclipse.viatra.cep.core.metamodels";

    /**
     * The singleton instance of the package.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EventsPackage eINSTANCE = org.eclipse.viatra.cep.core.metamodels.events.impl.EventsPackageImpl.init();

    /**
     * The meta object id for the '{@link org.eclipse.viatra.cep.core.metamodels.events.impl.EventPatternImpl <em>Event Pattern</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.eclipse.viatra.cep.core.metamodels.events.impl.EventPatternImpl
     * @see org.eclipse.viatra.cep.core.metamodels.events.impl.EventsPackageImpl#getEventPattern()
     * @generated
     */
    int EVENT_PATTERN = 0;

    /**
     * The feature id for the '<em><b>Automaton</b></em>' reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int EVENT_PATTERN__AUTOMATON = 0;

    /**
     * The feature id for the '<em><b>Id</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int EVENT_PATTERN__ID = 1;

    /**
     * The number of structural features of the '<em>Event Pattern</em>' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int EVENT_PATTERN_FEATURE_COUNT = 2;

    /**
     * The number of operations of the '<em>Event Pattern</em>' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int EVENT_PATTERN_OPERATION_COUNT = 0;

    /**
     * The meta object id for the '{@link org.eclipse.viatra.cep.core.metamodels.events.impl.AtomicEventPatternImpl <em>Atomic Event Pattern</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.eclipse.viatra.cep.core.metamodels.events.impl.AtomicEventPatternImpl
     * @see org.eclipse.viatra.cep.core.metamodels.events.impl.EventsPackageImpl#getAtomicEventPattern()
     * @generated
     */
    int ATOMIC_EVENT_PATTERN = 1;

    /**
     * The feature id for the '<em><b>Automaton</b></em>' reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int ATOMIC_EVENT_PATTERN__AUTOMATON = EVENT_PATTERN__AUTOMATON;

    /**
     * The feature id for the '<em><b>Id</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int ATOMIC_EVENT_PATTERN__ID = EVENT_PATTERN__ID;

    /**
     * The feature id for the '<em><b>Type</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int ATOMIC_EVENT_PATTERN__TYPE = EVENT_PATTERN_FEATURE_COUNT + 0;

    /**
     * The number of structural features of the '<em>Atomic Event Pattern</em>' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int ATOMIC_EVENT_PATTERN_FEATURE_COUNT = EVENT_PATTERN_FEATURE_COUNT + 1;

    /**
     * The number of operations of the '<em>Atomic Event Pattern</em>' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int ATOMIC_EVENT_PATTERN_OPERATION_COUNT = EVENT_PATTERN_OPERATION_COUNT + 0;

    /**
     * The meta object id for the '{@link org.eclipse.viatra.cep.core.metamodels.events.impl.ComplexEventPatternImpl <em>Complex Event Pattern</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.eclipse.viatra.cep.core.metamodels.events.impl.ComplexEventPatternImpl
     * @see org.eclipse.viatra.cep.core.metamodels.events.impl.EventsPackageImpl#getComplexEventPattern()
     * @generated
     */
    int COMPLEX_EVENT_PATTERN = 2;

    /**
     * The feature id for the '<em><b>Automaton</b></em>' reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int COMPLEX_EVENT_PATTERN__AUTOMATON = EVENT_PATTERN__AUTOMATON;

    /**
     * The feature id for the '<em><b>Id</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int COMPLEX_EVENT_PATTERN__ID = EVENT_PATTERN__ID;

    /**
     * The feature id for the '<em><b>Composition Events</b></em>' containment reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int COMPLEX_EVENT_PATTERN__COMPOSITION_EVENTS = EVENT_PATTERN_FEATURE_COUNT + 0;

    /**
     * The feature id for the '<em><b>Operator</b></em>' containment reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int COMPLEX_EVENT_PATTERN__OPERATOR = EVENT_PATTERN_FEATURE_COUNT + 1;

    /**
     * The number of structural features of the '<em>Complex Event Pattern</em>' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int COMPLEX_EVENT_PATTERN_FEATURE_COUNT = EVENT_PATTERN_FEATURE_COUNT + 2;

    /**
     * The operation id for the '<em>Add Composition Event Pattern</em>' operation.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int COMPLEX_EVENT_PATTERN___ADD_COMPOSITION_EVENT_PATTERN__EVENTPATTERN = EVENT_PATTERN_OPERATION_COUNT + 0;

    /**
     * The operation id for the '<em>Evaluate Parameter Bindigs</em>' operation.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int COMPLEX_EVENT_PATTERN___EVALUATE_PARAMETER_BINDIGS__EVENT = EVENT_PATTERN_OPERATION_COUNT + 1;

    /**
     * The number of operations of the '<em>Complex Event Pattern</em>' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int COMPLEX_EVENT_PATTERN_OPERATION_COUNT = EVENT_PATTERN_OPERATION_COUNT + 2;

    /**
     * The meta object id for the '{@link org.eclipse.viatra.cep.core.metamodels.events.impl.EventImpl <em>Event</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.eclipse.viatra.cep.core.metamodels.events.impl.EventImpl
     * @see org.eclipse.viatra.cep.core.metamodels.events.impl.EventsPackageImpl#getEvent()
     * @generated
     */
    int EVENT = 3;

    /**
     * The feature id for the '<em><b>Type</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int EVENT__TYPE = 0;

    /**
     * The feature id for the '<em><b>Timestamp</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int EVENT__TIMESTAMP = 1;

    /**
     * The feature id for the '<em><b>Source</b></em>' reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int EVENT__SOURCE = 2;

    /**
     * The number of structural features of the '<em>Event</em>' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int EVENT_FEATURE_COUNT = 3;

    /**
     * The number of operations of the '<em>Event</em>' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int EVENT_OPERATION_COUNT = 0;

    /**
     * The meta object id for the '{@link org.eclipse.viatra.cep.core.metamodels.events.IEventSource <em>IEvent Source</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.eclipse.viatra.cep.core.metamodels.events.IEventSource
     * @see org.eclipse.viatra.cep.core.metamodels.events.impl.EventsPackageImpl#getIEventSource()
     * @generated
     */
    int IEVENT_SOURCE = 4;

    /**
     * The number of structural features of the '<em>IEvent Source</em>' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int IEVENT_SOURCE_FEATURE_COUNT = 0;

    /**
     * The operation id for the '<em>Get Id</em>' operation.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int IEVENT_SOURCE___GET_ID = 0;

    /**
     * The number of operations of the '<em>IEvent Source</em>' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int IEVENT_SOURCE_OPERATION_COUNT = 1;

    /**
     * The meta object id for the '{@link org.eclipse.viatra.cep.core.metamodels.events.impl.TimeImpl <em>Time</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.eclipse.viatra.cep.core.metamodels.events.impl.TimeImpl
     * @see org.eclipse.viatra.cep.core.metamodels.events.impl.EventsPackageImpl#getTime()
     * @generated
     */
    int TIME = 5;

    /**
     * The feature id for the '<em><b>Length</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int TIME__LENGTH = 0;

    /**
     * The feature id for the '<em><b>Operator</b></em>' container reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int TIME__OPERATOR = 1;

    /**
     * The number of structural features of the '<em>Time</em>' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int TIME_FEATURE_COUNT = 2;

    /**
     * The number of operations of the '<em>Time</em>' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int TIME_OPERATION_COUNT = 0;

    /**
     * The meta object id for the '{@link org.eclipse.viatra.cep.core.metamodels.events.impl.ComplexEventOperatorImpl <em>Complex Event Operator</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.eclipse.viatra.cep.core.metamodels.events.impl.ComplexEventOperatorImpl
     * @see org.eclipse.viatra.cep.core.metamodels.events.impl.EventsPackageImpl#getComplexEventOperator()
     * @generated
     */
    int COMPLEX_EVENT_OPERATOR = 6;

    /**
     * The number of structural features of the '<em>Complex Event Operator</em>' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int COMPLEX_EVENT_OPERATOR_FEATURE_COUNT = 0;

    /**
     * The number of operations of the '<em>Complex Event Operator</em>' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int COMPLEX_EVENT_OPERATOR_OPERATION_COUNT = 0;

    /**
     * The meta object id for the '{@link org.eclipse.viatra.cep.core.metamodels.events.impl.LogicalOperatorImpl <em>Logical Operator</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.eclipse.viatra.cep.core.metamodels.events.impl.LogicalOperatorImpl
     * @see org.eclipse.viatra.cep.core.metamodels.events.impl.EventsPackageImpl#getLogicalOperator()
     * @generated
     */
    int LOGICAL_OPERATOR = 7;

    /**
     * The feature id for the '<em><b>Timing</b></em>' containment reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int LOGICAL_OPERATOR__TIMING = COMPLEX_EVENT_OPERATOR_FEATURE_COUNT + 0;

    /**
     * The feature id for the '<em><b>Event Pattern</b></em>' container reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int LOGICAL_OPERATOR__EVENT_PATTERN = COMPLEX_EVENT_OPERATOR_FEATURE_COUNT + 1;

    /**
     * The number of structural features of the '<em>Logical Operator</em>' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int LOGICAL_OPERATOR_FEATURE_COUNT = COMPLEX_EVENT_OPERATOR_FEATURE_COUNT + 2;

    /**
     * The number of operations of the '<em>Logical Operator</em>' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int LOGICAL_OPERATOR_OPERATION_COUNT = COMPLEX_EVENT_OPERATOR_OPERATION_COUNT + 0;

    /**
     * The meta object id for the '{@link org.eclipse.viatra.cep.core.metamodels.events.impl.ORImpl <em>OR</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.eclipse.viatra.cep.core.metamodels.events.impl.ORImpl
     * @see org.eclipse.viatra.cep.core.metamodels.events.impl.EventsPackageImpl#getOR()
     * @generated
     */
    int OR = 8;

    /**
     * The feature id for the '<em><b>Timing</b></em>' containment reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int OR__TIMING = LOGICAL_OPERATOR__TIMING;

    /**
     * The feature id for the '<em><b>Event Pattern</b></em>' container reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int OR__EVENT_PATTERN = LOGICAL_OPERATOR__EVENT_PATTERN;

    /**
     * The number of structural features of the '<em>OR</em>' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int OR_FEATURE_COUNT = LOGICAL_OPERATOR_FEATURE_COUNT + 0;

    /**
     * The number of operations of the '<em>OR</em>' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int OR_OPERATION_COUNT = LOGICAL_OPERATOR_OPERATION_COUNT + 0;

    /**
     * The meta object id for the '{@link org.eclipse.viatra.cep.core.metamodels.events.impl.NEGImpl <em>NEG</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.eclipse.viatra.cep.core.metamodels.events.impl.NEGImpl
     * @see org.eclipse.viatra.cep.core.metamodels.events.impl.EventsPackageImpl#getNEG()
     * @generated
     */
    int NEG = 9;

    /**
     * The feature id for the '<em><b>Timing</b></em>' containment reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int NEG__TIMING = LOGICAL_OPERATOR__TIMING;

    /**
     * The feature id for the '<em><b>Event Pattern</b></em>' container reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int NEG__EVENT_PATTERN = LOGICAL_OPERATOR__EVENT_PATTERN;

    /**
     * The number of structural features of the '<em>NEG</em>' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int NEG_FEATURE_COUNT = LOGICAL_OPERATOR_FEATURE_COUNT + 0;

    /**
     * The number of operations of the '<em>NEG</em>' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int NEG_OPERATION_COUNT = LOGICAL_OPERATOR_OPERATION_COUNT + 0;

    /**
     * The meta object id for the '{@link org.eclipse.viatra.cep.core.metamodels.events.impl.FOLLOWSImpl <em>FOLLOWS</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.eclipse.viatra.cep.core.metamodels.events.impl.FOLLOWSImpl
     * @see org.eclipse.viatra.cep.core.metamodels.events.impl.EventsPackageImpl#getFOLLOWS()
     * @generated
     */
    int FOLLOWS = 10;

    /**
     * The feature id for the '<em><b>Timing</b></em>' containment reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int FOLLOWS__TIMING = LOGICAL_OPERATOR__TIMING;

    /**
     * The feature id for the '<em><b>Event Pattern</b></em>' container reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int FOLLOWS__EVENT_PATTERN = LOGICAL_OPERATOR__EVENT_PATTERN;

    /**
     * The number of structural features of the '<em>FOLLOWS</em>' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int FOLLOWS_FEATURE_COUNT = LOGICAL_OPERATOR_FEATURE_COUNT + 0;

    /**
     * The number of operations of the '<em>FOLLOWS</em>' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int FOLLOWS_OPERATION_COUNT = LOGICAL_OPERATOR_OPERATION_COUNT + 0;

    /**
     * The meta object id for the '{@link org.eclipse.viatra.cep.core.metamodels.events.impl.UNTILImpl <em>UNTIL</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.eclipse.viatra.cep.core.metamodels.events.impl.UNTILImpl
     * @see org.eclipse.viatra.cep.core.metamodels.events.impl.EventsPackageImpl#getUNTIL()
     * @generated
     */
    int UNTIL = 11;

    /**
     * The feature id for the '<em><b>Timing</b></em>' containment reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int UNTIL__TIMING = LOGICAL_OPERATOR__TIMING;

    /**
     * The feature id for the '<em><b>Event Pattern</b></em>' container reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int UNTIL__EVENT_PATTERN = LOGICAL_OPERATOR__EVENT_PATTERN;

    /**
     * The number of structural features of the '<em>UNTIL</em>' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int UNTIL_FEATURE_COUNT = LOGICAL_OPERATOR_FEATURE_COUNT + 0;

    /**
     * The number of operations of the '<em>UNTIL</em>' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int UNTIL_OPERATION_COUNT = LOGICAL_OPERATOR_OPERATION_COUNT + 0;

    /**
     * The meta object id for the '{@link org.eclipse.viatra.cep.core.metamodels.events.impl.TimingOperatorImpl <em>Timing Operator</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.eclipse.viatra.cep.core.metamodels.events.impl.TimingOperatorImpl
     * @see org.eclipse.viatra.cep.core.metamodels.events.impl.EventsPackageImpl#getTimingOperator()
     * @generated
     */
    int TIMING_OPERATOR = 12;

    /**
     * The feature id for the '<em><b>Time</b></em>' containment reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int TIMING_OPERATOR__TIME = COMPLEX_EVENT_OPERATOR_FEATURE_COUNT + 0;

    /**
     * The feature id for the '<em><b>Operator</b></em>' container reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int TIMING_OPERATOR__OPERATOR = COMPLEX_EVENT_OPERATOR_FEATURE_COUNT + 1;

    /**
     * The number of structural features of the '<em>Timing Operator</em>' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int TIMING_OPERATOR_FEATURE_COUNT = COMPLEX_EVENT_OPERATOR_FEATURE_COUNT + 2;

    /**
     * The number of operations of the '<em>Timing Operator</em>' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int TIMING_OPERATOR_OPERATION_COUNT = COMPLEX_EVENT_OPERATOR_OPERATION_COUNT + 0;

    /**
     * The meta object id for the '{@link org.eclipse.viatra.cep.core.metamodels.events.impl.WITHINImpl <em>WITHIN</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.eclipse.viatra.cep.core.metamodels.events.impl.WITHINImpl
     * @see org.eclipse.viatra.cep.core.metamodels.events.impl.EventsPackageImpl#getWITHIN()
     * @generated
     */
    int WITHIN = 13;

    /**
     * The feature id for the '<em><b>Time</b></em>' containment reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int WITHIN__TIME = TIMING_OPERATOR__TIME;

    /**
     * The feature id for the '<em><b>Operator</b></em>' container reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int WITHIN__OPERATOR = TIMING_OPERATOR__OPERATOR;

    /**
     * The number of structural features of the '<em>WITHIN</em>' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int WITHIN_FEATURE_COUNT = TIMING_OPERATOR_FEATURE_COUNT + 0;

    /**
     * The number of operations of the '<em>WITHIN</em>' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int WITHIN_OPERATION_COUNT = TIMING_OPERATOR_OPERATION_COUNT + 0;

    /**
     * The meta object id for the '{@link org.eclipse.viatra.cep.core.metamodels.events.impl.ATLEASTImpl <em>ATLEAST</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.eclipse.viatra.cep.core.metamodels.events.impl.ATLEASTImpl
     * @see org.eclipse.viatra.cep.core.metamodels.events.impl.EventsPackageImpl#getATLEAST()
     * @generated
     */
    int ATLEAST = 14;

    /**
     * The feature id for the '<em><b>Time</b></em>' containment reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int ATLEAST__TIME = TIMING_OPERATOR__TIME;

    /**
     * The feature id for the '<em><b>Operator</b></em>' container reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int ATLEAST__OPERATOR = TIMING_OPERATOR__OPERATOR;

    /**
     * The number of structural features of the '<em>ATLEAST</em>' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int ATLEAST_FEATURE_COUNT = TIMING_OPERATOR_FEATURE_COUNT + 0;

    /**
     * The number of operations of the '<em>ATLEAST</em>' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int ATLEAST_OPERATION_COUNT = TIMING_OPERATOR_OPERATION_COUNT + 0;


    /**
     * Returns the meta object for class '{@link org.eclipse.viatra.cep.core.metamodels.events.EventPattern <em>Event Pattern</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for class '<em>Event Pattern</em>'.
     * @see org.eclipse.viatra.cep.core.metamodels.events.EventPattern
     * @generated
     */
    EClass getEventPattern();

    /**
     * Returns the meta object for the reference '{@link org.eclipse.viatra.cep.core.metamodels.events.EventPattern#getAutomaton <em>Automaton</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the reference '<em>Automaton</em>'.
     * @see org.eclipse.viatra.cep.core.metamodels.events.EventPattern#getAutomaton()
     * @see #getEventPattern()
     * @generated
     */
    EReference getEventPattern_Automaton();

    /**
     * Returns the meta object for the attribute '{@link org.eclipse.viatra.cep.core.metamodels.events.EventPattern#getId <em>Id</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Id</em>'.
     * @see org.eclipse.viatra.cep.core.metamodels.events.EventPattern#getId()
     * @see #getEventPattern()
     * @generated
     */
    EAttribute getEventPattern_Id();

    /**
     * Returns the meta object for class '{@link org.eclipse.viatra.cep.core.metamodels.events.AtomicEventPattern <em>Atomic Event Pattern</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for class '<em>Atomic Event Pattern</em>'.
     * @see org.eclipse.viatra.cep.core.metamodels.events.AtomicEventPattern
     * @generated
     */
    EClass getAtomicEventPattern();

    /**
     * Returns the meta object for the attribute '{@link org.eclipse.viatra.cep.core.metamodels.events.AtomicEventPattern#getType <em>Type</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Type</em>'.
     * @see org.eclipse.viatra.cep.core.metamodels.events.AtomicEventPattern#getType()
     * @see #getAtomicEventPattern()
     * @generated
     */
    EAttribute getAtomicEventPattern_Type();

    /**
     * Returns the meta object for class '{@link org.eclipse.viatra.cep.core.metamodels.events.ComplexEventPattern <em>Complex Event Pattern</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for class '<em>Complex Event Pattern</em>'.
     * @see org.eclipse.viatra.cep.core.metamodels.events.ComplexEventPattern
     * @generated
     */
    EClass getComplexEventPattern();

    /**
     * Returns the meta object for the containment reference list '{@link org.eclipse.viatra.cep.core.metamodels.events.ComplexEventPattern#getCompositionEvents <em>Composition Events</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the containment reference list '<em>Composition Events</em>'.
     * @see org.eclipse.viatra.cep.core.metamodels.events.ComplexEventPattern#getCompositionEvents()
     * @see #getComplexEventPattern()
     * @generated
     */
    EReference getComplexEventPattern_CompositionEvents();

    /**
     * Returns the meta object for the containment reference '{@link org.eclipse.viatra.cep.core.metamodels.events.ComplexEventPattern#getOperator <em>Operator</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the containment reference '<em>Operator</em>'.
     * @see org.eclipse.viatra.cep.core.metamodels.events.ComplexEventPattern#getOperator()
     * @see #getComplexEventPattern()
     * @generated
     */
    EReference getComplexEventPattern_Operator();

    /**
     * Returns the meta object for the '{@link org.eclipse.viatra.cep.core.metamodels.events.ComplexEventPattern#addCompositionEventPattern(org.eclipse.viatra.cep.core.metamodels.events.EventPattern) <em>Add Composition Event Pattern</em>}' operation.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the '<em>Add Composition Event Pattern</em>' operation.
     * @see org.eclipse.viatra.cep.core.metamodels.events.ComplexEventPattern#addCompositionEventPattern(org.eclipse.viatra.cep.core.metamodels.events.EventPattern)
     * @generated
     */
    EOperation getComplexEventPattern__AddCompositionEventPattern__EventPattern();

    /**
     * Returns the meta object for the '{@link org.eclipse.viatra.cep.core.metamodels.events.ComplexEventPattern#evaluateParameterBindigs(org.eclipse.viatra.cep.core.metamodels.events.Event) <em>Evaluate Parameter Bindigs</em>}' operation.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the '<em>Evaluate Parameter Bindigs</em>' operation.
     * @see org.eclipse.viatra.cep.core.metamodels.events.ComplexEventPattern#evaluateParameterBindigs(org.eclipse.viatra.cep.core.metamodels.events.Event)
     * @generated
     */
    EOperation getComplexEventPattern__EvaluateParameterBindigs__Event();

    /**
     * Returns the meta object for class '{@link org.eclipse.viatra.cep.core.metamodels.events.Event <em>Event</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for class '<em>Event</em>'.
     * @see org.eclipse.viatra.cep.core.metamodels.events.Event
     * @generated
     */
    EClass getEvent();

    /**
     * Returns the meta object for the attribute '{@link org.eclipse.viatra.cep.core.metamodels.events.Event#getType <em>Type</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Type</em>'.
     * @see org.eclipse.viatra.cep.core.metamodels.events.Event#getType()
     * @see #getEvent()
     * @generated
     */
    EAttribute getEvent_Type();

    /**
     * Returns the meta object for the attribute '{@link org.eclipse.viatra.cep.core.metamodels.events.Event#getTimestamp <em>Timestamp</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Timestamp</em>'.
     * @see org.eclipse.viatra.cep.core.metamodels.events.Event#getTimestamp()
     * @see #getEvent()
     * @generated
     */
    EAttribute getEvent_Timestamp();

    /**
     * Returns the meta object for the reference '{@link org.eclipse.viatra.cep.core.metamodels.events.Event#getSource <em>Source</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the reference '<em>Source</em>'.
     * @see org.eclipse.viatra.cep.core.metamodels.events.Event#getSource()
     * @see #getEvent()
     * @generated
     */
    EReference getEvent_Source();

    /**
     * Returns the meta object for class '{@link org.eclipse.viatra.cep.core.metamodels.events.IEventSource <em>IEvent Source</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for class '<em>IEvent Source</em>'.
     * @see org.eclipse.viatra.cep.core.metamodels.events.IEventSource
     * @generated
     */
    EClass getIEventSource();

    /**
     * Returns the meta object for the '{@link org.eclipse.viatra.cep.core.metamodels.events.IEventSource#getId() <em>Get Id</em>}' operation.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the '<em>Get Id</em>' operation.
     * @see org.eclipse.viatra.cep.core.metamodels.events.IEventSource#getId()
     * @generated
     */
    EOperation getIEventSource__GetId();

    /**
     * Returns the meta object for class '{@link org.eclipse.viatra.cep.core.metamodels.events.Time <em>Time</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for class '<em>Time</em>'.
     * @see org.eclipse.viatra.cep.core.metamodels.events.Time
     * @generated
     */
    EClass getTime();

    /**
     * Returns the meta object for the attribute '{@link org.eclipse.viatra.cep.core.metamodels.events.Time#getLength <em>Length</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Length</em>'.
     * @see org.eclipse.viatra.cep.core.metamodels.events.Time#getLength()
     * @see #getTime()
     * @generated
     */
    EAttribute getTime_Length();

    /**
     * Returns the meta object for the container reference '{@link org.eclipse.viatra.cep.core.metamodels.events.Time#getOperator <em>Operator</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the container reference '<em>Operator</em>'.
     * @see org.eclipse.viatra.cep.core.metamodels.events.Time#getOperator()
     * @see #getTime()
     * @generated
     */
    EReference getTime_Operator();

    /**
     * Returns the meta object for class '{@link org.eclipse.viatra.cep.core.metamodels.events.ComplexEventOperator <em>Complex Event Operator</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for class '<em>Complex Event Operator</em>'.
     * @see org.eclipse.viatra.cep.core.metamodels.events.ComplexEventOperator
     * @generated
     */
    EClass getComplexEventOperator();

    /**
     * Returns the meta object for class '{@link org.eclipse.viatra.cep.core.metamodels.events.LogicalOperator <em>Logical Operator</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for class '<em>Logical Operator</em>'.
     * @see org.eclipse.viatra.cep.core.metamodels.events.LogicalOperator
     * @generated
     */
    EClass getLogicalOperator();

    /**
     * Returns the meta object for the containment reference '{@link org.eclipse.viatra.cep.core.metamodels.events.LogicalOperator#getTiming <em>Timing</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the containment reference '<em>Timing</em>'.
     * @see org.eclipse.viatra.cep.core.metamodels.events.LogicalOperator#getTiming()
     * @see #getLogicalOperator()
     * @generated
     */
    EReference getLogicalOperator_Timing();

    /**
     * Returns the meta object for the container reference '{@link org.eclipse.viatra.cep.core.metamodels.events.LogicalOperator#getEventPattern <em>Event Pattern</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the container reference '<em>Event Pattern</em>'.
     * @see org.eclipse.viatra.cep.core.metamodels.events.LogicalOperator#getEventPattern()
     * @see #getLogicalOperator()
     * @generated
     */
    EReference getLogicalOperator_EventPattern();

    /**
     * Returns the meta object for class '{@link org.eclipse.viatra.cep.core.metamodels.events.OR <em>OR</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for class '<em>OR</em>'.
     * @see org.eclipse.viatra.cep.core.metamodels.events.OR
     * @generated
     */
    EClass getOR();

    /**
     * Returns the meta object for class '{@link org.eclipse.viatra.cep.core.metamodels.events.NEG <em>NEG</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for class '<em>NEG</em>'.
     * @see org.eclipse.viatra.cep.core.metamodels.events.NEG
     * @generated
     */
    EClass getNEG();

    /**
     * Returns the meta object for class '{@link org.eclipse.viatra.cep.core.metamodels.events.FOLLOWS <em>FOLLOWS</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for class '<em>FOLLOWS</em>'.
     * @see org.eclipse.viatra.cep.core.metamodels.events.FOLLOWS
     * @generated
     */
    EClass getFOLLOWS();

    /**
     * Returns the meta object for class '{@link org.eclipse.viatra.cep.core.metamodels.events.UNTIL <em>UNTIL</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for class '<em>UNTIL</em>'.
     * @see org.eclipse.viatra.cep.core.metamodels.events.UNTIL
     * @generated
     */
    EClass getUNTIL();

    /**
     * Returns the meta object for class '{@link org.eclipse.viatra.cep.core.metamodels.events.TimingOperator <em>Timing Operator</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for class '<em>Timing Operator</em>'.
     * @see org.eclipse.viatra.cep.core.metamodels.events.TimingOperator
     * @generated
     */
    EClass getTimingOperator();

    /**
     * Returns the meta object for the containment reference '{@link org.eclipse.viatra.cep.core.metamodels.events.TimingOperator#getTime <em>Time</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the containment reference '<em>Time</em>'.
     * @see org.eclipse.viatra.cep.core.metamodels.events.TimingOperator#getTime()
     * @see #getTimingOperator()
     * @generated
     */
    EReference getTimingOperator_Time();

    /**
     * Returns the meta object for the container reference '{@link org.eclipse.viatra.cep.core.metamodels.events.TimingOperator#getOperator <em>Operator</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the container reference '<em>Operator</em>'.
     * @see org.eclipse.viatra.cep.core.metamodels.events.TimingOperator#getOperator()
     * @see #getTimingOperator()
     * @generated
     */
    EReference getTimingOperator_Operator();

    /**
     * Returns the meta object for class '{@link org.eclipse.viatra.cep.core.metamodels.events.WITHIN <em>WITHIN</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for class '<em>WITHIN</em>'.
     * @see org.eclipse.viatra.cep.core.metamodels.events.WITHIN
     * @generated
     */
    EClass getWITHIN();

    /**
     * Returns the meta object for class '{@link org.eclipse.viatra.cep.core.metamodels.events.ATLEAST <em>ATLEAST</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for class '<em>ATLEAST</em>'.
     * @see org.eclipse.viatra.cep.core.metamodels.events.ATLEAST
     * @generated
     */
    EClass getATLEAST();

    /**
     * Returns the factory that creates the instances of the model.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the factory that creates the instances of the model.
     * @generated
     */
    EventsFactory getEventsFactory();

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
         * The meta object literal for the '{@link org.eclipse.viatra.cep.core.metamodels.events.impl.EventPatternImpl <em>Event Pattern</em>}' class.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @see org.eclipse.viatra.cep.core.metamodels.events.impl.EventPatternImpl
         * @see org.eclipse.viatra.cep.core.metamodels.events.impl.EventsPackageImpl#getEventPattern()
         * @generated
         */
        EClass EVENT_PATTERN = eINSTANCE.getEventPattern();

        /**
         * The meta object literal for the '<em><b>Automaton</b></em>' reference feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EReference EVENT_PATTERN__AUTOMATON = eINSTANCE.getEventPattern_Automaton();

        /**
         * The meta object literal for the '<em><b>Id</b></em>' attribute feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EAttribute EVENT_PATTERN__ID = eINSTANCE.getEventPattern_Id();

        /**
         * The meta object literal for the '{@link org.eclipse.viatra.cep.core.metamodels.events.impl.AtomicEventPatternImpl <em>Atomic Event Pattern</em>}' class.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @see org.eclipse.viatra.cep.core.metamodels.events.impl.AtomicEventPatternImpl
         * @see org.eclipse.viatra.cep.core.metamodels.events.impl.EventsPackageImpl#getAtomicEventPattern()
         * @generated
         */
        EClass ATOMIC_EVENT_PATTERN = eINSTANCE.getAtomicEventPattern();

        /**
         * The meta object literal for the '<em><b>Type</b></em>' attribute feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EAttribute ATOMIC_EVENT_PATTERN__TYPE = eINSTANCE.getAtomicEventPattern_Type();

        /**
         * The meta object literal for the '{@link org.eclipse.viatra.cep.core.metamodels.events.impl.ComplexEventPatternImpl <em>Complex Event Pattern</em>}' class.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @see org.eclipse.viatra.cep.core.metamodels.events.impl.ComplexEventPatternImpl
         * @see org.eclipse.viatra.cep.core.metamodels.events.impl.EventsPackageImpl#getComplexEventPattern()
         * @generated
         */
        EClass COMPLEX_EVENT_PATTERN = eINSTANCE.getComplexEventPattern();

        /**
         * The meta object literal for the '<em><b>Composition Events</b></em>' containment reference list feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EReference COMPLEX_EVENT_PATTERN__COMPOSITION_EVENTS = eINSTANCE.getComplexEventPattern_CompositionEvents();

        /**
         * The meta object literal for the '<em><b>Operator</b></em>' containment reference feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EReference COMPLEX_EVENT_PATTERN__OPERATOR = eINSTANCE.getComplexEventPattern_Operator();

        /**
         * The meta object literal for the '<em><b>Add Composition Event Pattern</b></em>' operation.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EOperation COMPLEX_EVENT_PATTERN___ADD_COMPOSITION_EVENT_PATTERN__EVENTPATTERN = eINSTANCE.getComplexEventPattern__AddCompositionEventPattern__EventPattern();

        /**
         * The meta object literal for the '<em><b>Evaluate Parameter Bindigs</b></em>' operation.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EOperation COMPLEX_EVENT_PATTERN___EVALUATE_PARAMETER_BINDIGS__EVENT = eINSTANCE.getComplexEventPattern__EvaluateParameterBindigs__Event();

        /**
         * The meta object literal for the '{@link org.eclipse.viatra.cep.core.metamodels.events.impl.EventImpl <em>Event</em>}' class.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @see org.eclipse.viatra.cep.core.metamodels.events.impl.EventImpl
         * @see org.eclipse.viatra.cep.core.metamodels.events.impl.EventsPackageImpl#getEvent()
         * @generated
         */
        EClass EVENT = eINSTANCE.getEvent();

        /**
         * The meta object literal for the '<em><b>Type</b></em>' attribute feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EAttribute EVENT__TYPE = eINSTANCE.getEvent_Type();

        /**
         * The meta object literal for the '<em><b>Timestamp</b></em>' attribute feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EAttribute EVENT__TIMESTAMP = eINSTANCE.getEvent_Timestamp();

        /**
         * The meta object literal for the '<em><b>Source</b></em>' reference feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EReference EVENT__SOURCE = eINSTANCE.getEvent_Source();

        /**
         * The meta object literal for the '{@link org.eclipse.viatra.cep.core.metamodels.events.IEventSource <em>IEvent Source</em>}' class.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @see org.eclipse.viatra.cep.core.metamodels.events.IEventSource
         * @see org.eclipse.viatra.cep.core.metamodels.events.impl.EventsPackageImpl#getIEventSource()
         * @generated
         */
        EClass IEVENT_SOURCE = eINSTANCE.getIEventSource();

        /**
         * The meta object literal for the '<em><b>Get Id</b></em>' operation.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EOperation IEVENT_SOURCE___GET_ID = eINSTANCE.getIEventSource__GetId();

        /**
         * The meta object literal for the '{@link org.eclipse.viatra.cep.core.metamodels.events.impl.TimeImpl <em>Time</em>}' class.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @see org.eclipse.viatra.cep.core.metamodels.events.impl.TimeImpl
         * @see org.eclipse.viatra.cep.core.metamodels.events.impl.EventsPackageImpl#getTime()
         * @generated
         */
        EClass TIME = eINSTANCE.getTime();

        /**
         * The meta object literal for the '<em><b>Length</b></em>' attribute feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EAttribute TIME__LENGTH = eINSTANCE.getTime_Length();

        /**
         * The meta object literal for the '<em><b>Operator</b></em>' container reference feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EReference TIME__OPERATOR = eINSTANCE.getTime_Operator();

        /**
         * The meta object literal for the '{@link org.eclipse.viatra.cep.core.metamodels.events.impl.ComplexEventOperatorImpl <em>Complex Event Operator</em>}' class.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @see org.eclipse.viatra.cep.core.metamodels.events.impl.ComplexEventOperatorImpl
         * @see org.eclipse.viatra.cep.core.metamodels.events.impl.EventsPackageImpl#getComplexEventOperator()
         * @generated
         */
        EClass COMPLEX_EVENT_OPERATOR = eINSTANCE.getComplexEventOperator();

        /**
         * The meta object literal for the '{@link org.eclipse.viatra.cep.core.metamodels.events.impl.LogicalOperatorImpl <em>Logical Operator</em>}' class.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @see org.eclipse.viatra.cep.core.metamodels.events.impl.LogicalOperatorImpl
         * @see org.eclipse.viatra.cep.core.metamodels.events.impl.EventsPackageImpl#getLogicalOperator()
         * @generated
         */
        EClass LOGICAL_OPERATOR = eINSTANCE.getLogicalOperator();

        /**
         * The meta object literal for the '<em><b>Timing</b></em>' containment reference feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EReference LOGICAL_OPERATOR__TIMING = eINSTANCE.getLogicalOperator_Timing();

        /**
         * The meta object literal for the '<em><b>Event Pattern</b></em>' container reference feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EReference LOGICAL_OPERATOR__EVENT_PATTERN = eINSTANCE.getLogicalOperator_EventPattern();

        /**
         * The meta object literal for the '{@link org.eclipse.viatra.cep.core.metamodels.events.impl.ORImpl <em>OR</em>}' class.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @see org.eclipse.viatra.cep.core.metamodels.events.impl.ORImpl
         * @see org.eclipse.viatra.cep.core.metamodels.events.impl.EventsPackageImpl#getOR()
         * @generated
         */
        EClass OR = eINSTANCE.getOR();

        /**
         * The meta object literal for the '{@link org.eclipse.viatra.cep.core.metamodels.events.impl.NEGImpl <em>NEG</em>}' class.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @see org.eclipse.viatra.cep.core.metamodels.events.impl.NEGImpl
         * @see org.eclipse.viatra.cep.core.metamodels.events.impl.EventsPackageImpl#getNEG()
         * @generated
         */
        EClass NEG = eINSTANCE.getNEG();

        /**
         * The meta object literal for the '{@link org.eclipse.viatra.cep.core.metamodels.events.impl.FOLLOWSImpl <em>FOLLOWS</em>}' class.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @see org.eclipse.viatra.cep.core.metamodels.events.impl.FOLLOWSImpl
         * @see org.eclipse.viatra.cep.core.metamodels.events.impl.EventsPackageImpl#getFOLLOWS()
         * @generated
         */
        EClass FOLLOWS = eINSTANCE.getFOLLOWS();

        /**
         * The meta object literal for the '{@link org.eclipse.viatra.cep.core.metamodels.events.impl.UNTILImpl <em>UNTIL</em>}' class.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @see org.eclipse.viatra.cep.core.metamodels.events.impl.UNTILImpl
         * @see org.eclipse.viatra.cep.core.metamodels.events.impl.EventsPackageImpl#getUNTIL()
         * @generated
         */
        EClass UNTIL = eINSTANCE.getUNTIL();

        /**
         * The meta object literal for the '{@link org.eclipse.viatra.cep.core.metamodels.events.impl.TimingOperatorImpl <em>Timing Operator</em>}' class.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @see org.eclipse.viatra.cep.core.metamodels.events.impl.TimingOperatorImpl
         * @see org.eclipse.viatra.cep.core.metamodels.events.impl.EventsPackageImpl#getTimingOperator()
         * @generated
         */
        EClass TIMING_OPERATOR = eINSTANCE.getTimingOperator();

        /**
         * The meta object literal for the '<em><b>Time</b></em>' containment reference feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EReference TIMING_OPERATOR__TIME = eINSTANCE.getTimingOperator_Time();

        /**
         * The meta object literal for the '<em><b>Operator</b></em>' container reference feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EReference TIMING_OPERATOR__OPERATOR = eINSTANCE.getTimingOperator_Operator();

        /**
         * The meta object literal for the '{@link org.eclipse.viatra.cep.core.metamodels.events.impl.WITHINImpl <em>WITHIN</em>}' class.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @see org.eclipse.viatra.cep.core.metamodels.events.impl.WITHINImpl
         * @see org.eclipse.viatra.cep.core.metamodels.events.impl.EventsPackageImpl#getWITHIN()
         * @generated
         */
        EClass WITHIN = eINSTANCE.getWITHIN();

        /**
         * The meta object literal for the '{@link org.eclipse.viatra.cep.core.metamodels.events.impl.ATLEASTImpl <em>ATLEAST</em>}' class.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @see org.eclipse.viatra.cep.core.metamodels.events.impl.ATLEASTImpl
         * @see org.eclipse.viatra.cep.core.metamodels.events.impl.EventsPackageImpl#getATLEAST()
         * @generated
         */
        EClass ATLEAST = eINSTANCE.getATLEAST();

    }

} //EventsPackage
