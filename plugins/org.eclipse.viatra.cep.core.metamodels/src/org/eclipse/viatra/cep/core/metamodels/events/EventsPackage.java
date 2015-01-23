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
     * The feature id for the '<em><b>Operator</b></em>' containment reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int COMPLEX_EVENT_PATTERN__OPERATOR = EVENT_PATTERN_FEATURE_COUNT + 0;

    /**
     * The feature id for the '<em><b>Timewindow</b></em>' containment reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int COMPLEX_EVENT_PATTERN__TIMEWINDOW = EVENT_PATTERN_FEATURE_COUNT + 1;

    /**
     * The feature id for the '<em><b>Contained Event Patterns</b></em>' containment reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int COMPLEX_EVENT_PATTERN__CONTAINED_EVENT_PATTERNS = EVENT_PATTERN_FEATURE_COUNT + 2;

    /**
     * The number of structural features of the '<em>Complex Event Pattern</em>' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int COMPLEX_EVENT_PATTERN_FEATURE_COUNT = EVENT_PATTERN_FEATURE_COUNT + 3;

    /**
     * The operation id for the '<em>Evaluate Parameter Bindings</em>' operation.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int COMPLEX_EVENT_PATTERN___EVALUATE_PARAMETER_BINDINGS__EVENT = EVENT_PATTERN_OPERATION_COUNT + 0;

    /**
     * The number of operations of the '<em>Complex Event Pattern</em>' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int COMPLEX_EVENT_PATTERN_OPERATION_COUNT = EVENT_PATTERN_OPERATION_COUNT + 1;

    /**
     * The meta object id for the '{@link org.eclipse.viatra.cep.core.metamodels.events.impl.EventPatternReferenceImpl <em>Event Pattern Reference</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.eclipse.viatra.cep.core.metamodels.events.impl.EventPatternReferenceImpl
     * @see org.eclipse.viatra.cep.core.metamodels.events.impl.EventsPackageImpl#getEventPatternReference()
     * @generated
     */
    int EVENT_PATTERN_REFERENCE = 3;

    /**
     * The feature id for the '<em><b>Event Pattern</b></em>' containment reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int EVENT_PATTERN_REFERENCE__EVENT_PATTERN = 0;

    /**
     * The feature id for the '<em><b>Multiplicity</b></em>' containment reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int EVENT_PATTERN_REFERENCE__MULTIPLICITY = 1;

    /**
     * The number of structural features of the '<em>Event Pattern Reference</em>' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int EVENT_PATTERN_REFERENCE_FEATURE_COUNT = 2;

    /**
     * The number of operations of the '<em>Event Pattern Reference</em>' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int EVENT_PATTERN_REFERENCE_OPERATION_COUNT = 0;

    /**
     * The meta object id for the '{@link org.eclipse.viatra.cep.core.metamodels.events.impl.EventImpl <em>Event</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.eclipse.viatra.cep.core.metamodels.events.impl.EventImpl
     * @see org.eclipse.viatra.cep.core.metamodels.events.impl.EventsPackageImpl#getEvent()
     * @generated
     */
    int EVENT = 4;

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
     * The feature id for the '<em><b>Is Processed</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int EVENT__IS_PROCESSED = 3;

    /**
     * The number of structural features of the '<em>Event</em>' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int EVENT_FEATURE_COUNT = 4;

    /**
     * The number of operations of the '<em>Event</em>' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int EVENT_OPERATION_COUNT = 0;

    /**
     * The meta object id for the '{@link org.eclipse.viatra.cep.core.metamodels.events.impl.EventSourceImpl <em>Event Source</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.eclipse.viatra.cep.core.metamodels.events.impl.EventSourceImpl
     * @see org.eclipse.viatra.cep.core.metamodels.events.impl.EventsPackageImpl#getEventSource()
     * @generated
     */
    int EVENT_SOURCE = 5;

    /**
     * The number of structural features of the '<em>Event Source</em>' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int EVENT_SOURCE_FEATURE_COUNT = 0;

    /**
     * The operation id for the '<em>Get Id</em>' operation.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int EVENT_SOURCE___GET_ID = 0;

    /**
     * The number of operations of the '<em>Event Source</em>' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int EVENT_SOURCE_OPERATION_COUNT = 1;

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
     * The meta object id for the '{@link org.eclipse.viatra.cep.core.metamodels.events.impl.ORImpl <em>OR</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.eclipse.viatra.cep.core.metamodels.events.impl.ORImpl
     * @see org.eclipse.viatra.cep.core.metamodels.events.impl.EventsPackageImpl#getOR()
     * @generated
     */
    int OR = 7;

    /**
     * The number of structural features of the '<em>OR</em>' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int OR_FEATURE_COUNT = COMPLEX_EVENT_OPERATOR_FEATURE_COUNT + 0;

    /**
     * The number of operations of the '<em>OR</em>' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int OR_OPERATION_COUNT = COMPLEX_EVENT_OPERATOR_OPERATION_COUNT + 0;

    /**
     * The meta object id for the '{@link org.eclipse.viatra.cep.core.metamodels.events.impl.NEGImpl <em>NEG</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.eclipse.viatra.cep.core.metamodels.events.impl.NEGImpl
     * @see org.eclipse.viatra.cep.core.metamodels.events.impl.EventsPackageImpl#getNEG()
     * @generated
     */
    int NEG = 8;

    /**
     * The number of structural features of the '<em>NEG</em>' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int NEG_FEATURE_COUNT = COMPLEX_EVENT_OPERATOR_FEATURE_COUNT + 0;

    /**
     * The number of operations of the '<em>NEG</em>' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int NEG_OPERATION_COUNT = COMPLEX_EVENT_OPERATOR_OPERATION_COUNT + 0;

    /**
     * The meta object id for the '{@link org.eclipse.viatra.cep.core.metamodels.events.impl.FOLLOWSImpl <em>FOLLOWS</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.eclipse.viatra.cep.core.metamodels.events.impl.FOLLOWSImpl
     * @see org.eclipse.viatra.cep.core.metamodels.events.impl.EventsPackageImpl#getFOLLOWS()
     * @generated
     */
    int FOLLOWS = 9;

    /**
     * The number of structural features of the '<em>FOLLOWS</em>' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int FOLLOWS_FEATURE_COUNT = COMPLEX_EVENT_OPERATOR_FEATURE_COUNT + 0;

    /**
     * The number of operations of the '<em>FOLLOWS</em>' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int FOLLOWS_OPERATION_COUNT = COMPLEX_EVENT_OPERATOR_OPERATION_COUNT + 0;

    /**
     * The meta object id for the '{@link org.eclipse.viatra.cep.core.metamodels.events.impl.ANDImpl <em>AND</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.eclipse.viatra.cep.core.metamodels.events.impl.ANDImpl
     * @see org.eclipse.viatra.cep.core.metamodels.events.impl.EventsPackageImpl#getAND()
     * @generated
     */
    int AND = 10;

    /**
     * The number of structural features of the '<em>AND</em>' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int AND_FEATURE_COUNT = COMPLEX_EVENT_OPERATOR_FEATURE_COUNT + 0;

    /**
     * The number of operations of the '<em>AND</em>' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int AND_OPERATION_COUNT = COMPLEX_EVENT_OPERATOR_OPERATION_COUNT + 0;

    /**
     * The meta object id for the '{@link org.eclipse.viatra.cep.core.metamodels.events.impl.TimewindowImpl <em>Timewindow</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.eclipse.viatra.cep.core.metamodels.events.impl.TimewindowImpl
     * @see org.eclipse.viatra.cep.core.metamodels.events.impl.EventsPackageImpl#getTimewindow()
     * @generated
     */
    int TIMEWINDOW = 11;

    /**
     * The feature id for the '<em><b>Time</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int TIMEWINDOW__TIME = 0;

    /**
     * The number of structural features of the '<em>Timewindow</em>' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int TIMEWINDOW_FEATURE_COUNT = 1;

    /**
     * The number of operations of the '<em>Timewindow</em>' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int TIMEWINDOW_OPERATION_COUNT = 0;

    /**
     * The meta object id for the '{@link org.eclipse.viatra.cep.core.metamodels.events.impl.AbstractMultiplicityImpl <em>Abstract Multiplicity</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.eclipse.viatra.cep.core.metamodels.events.impl.AbstractMultiplicityImpl
     * @see org.eclipse.viatra.cep.core.metamodels.events.impl.EventsPackageImpl#getAbstractMultiplicity()
     * @generated
     */
    int ABSTRACT_MULTIPLICITY = 12;

    /**
     * The number of structural features of the '<em>Abstract Multiplicity</em>' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int ABSTRACT_MULTIPLICITY_FEATURE_COUNT = 0;

    /**
     * The number of operations of the '<em>Abstract Multiplicity</em>' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int ABSTRACT_MULTIPLICITY_OPERATION_COUNT = 0;

    /**
     * The meta object id for the '{@link org.eclipse.viatra.cep.core.metamodels.events.impl.MultiplicityImpl <em>Multiplicity</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.eclipse.viatra.cep.core.metamodels.events.impl.MultiplicityImpl
     * @see org.eclipse.viatra.cep.core.metamodels.events.impl.EventsPackageImpl#getMultiplicity()
     * @generated
     */
    int MULTIPLICITY = 13;

    /**
     * The feature id for the '<em><b>Value</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int MULTIPLICITY__VALUE = ABSTRACT_MULTIPLICITY_FEATURE_COUNT + 0;

    /**
     * The number of structural features of the '<em>Multiplicity</em>' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int MULTIPLICITY_FEATURE_COUNT = ABSTRACT_MULTIPLICITY_FEATURE_COUNT + 1;

    /**
     * The number of operations of the '<em>Multiplicity</em>' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int MULTIPLICITY_OPERATION_COUNT = ABSTRACT_MULTIPLICITY_OPERATION_COUNT + 0;

    /**
     * The meta object id for the '{@link org.eclipse.viatra.cep.core.metamodels.events.impl.InfiniteImpl <em>Infinite</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.eclipse.viatra.cep.core.metamodels.events.impl.InfiniteImpl
     * @see org.eclipse.viatra.cep.core.metamodels.events.impl.EventsPackageImpl#getInfinite()
     * @generated
     */
    int INFINITE = 14;

    /**
     * The number of structural features of the '<em>Infinite</em>' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int INFINITE_FEATURE_COUNT = ABSTRACT_MULTIPLICITY_FEATURE_COUNT + 0;

    /**
     * The number of operations of the '<em>Infinite</em>' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int INFINITE_OPERATION_COUNT = ABSTRACT_MULTIPLICITY_OPERATION_COUNT + 0;

    /**
     * The meta object id for the '{@link org.eclipse.viatra.cep.core.metamodels.events.impl.AtLeastOneImpl <em>At Least One</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.eclipse.viatra.cep.core.metamodels.events.impl.AtLeastOneImpl
     * @see org.eclipse.viatra.cep.core.metamodels.events.impl.EventsPackageImpl#getAtLeastOne()
     * @generated
     */
    int AT_LEAST_ONE = 15;

    /**
     * The number of structural features of the '<em>At Least One</em>' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int AT_LEAST_ONE_FEATURE_COUNT = ABSTRACT_MULTIPLICITY_FEATURE_COUNT + 0;

    /**
     * The number of operations of the '<em>At Least One</em>' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int AT_LEAST_ONE_OPERATION_COUNT = ABSTRACT_MULTIPLICITY_OPERATION_COUNT + 0;


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
     * Returns the meta object for the containment reference '{@link org.eclipse.viatra.cep.core.metamodels.events.ComplexEventPattern#getTimewindow <em>Timewindow</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the containment reference '<em>Timewindow</em>'.
     * @see org.eclipse.viatra.cep.core.metamodels.events.ComplexEventPattern#getTimewindow()
     * @see #getComplexEventPattern()
     * @generated
     */
    EReference getComplexEventPattern_Timewindow();

    /**
     * Returns the meta object for the containment reference list '{@link org.eclipse.viatra.cep.core.metamodels.events.ComplexEventPattern#getContainedEventPatterns <em>Contained Event Patterns</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the containment reference list '<em>Contained Event Patterns</em>'.
     * @see org.eclipse.viatra.cep.core.metamodels.events.ComplexEventPattern#getContainedEventPatterns()
     * @see #getComplexEventPattern()
     * @generated
     */
    EReference getComplexEventPattern_ContainedEventPatterns();

    /**
     * Returns the meta object for the '{@link org.eclipse.viatra.cep.core.metamodels.events.ComplexEventPattern#evaluateParameterBindings(org.eclipse.viatra.cep.core.metamodels.events.Event) <em>Evaluate Parameter Bindings</em>}' operation.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the '<em>Evaluate Parameter Bindings</em>' operation.
     * @see org.eclipse.viatra.cep.core.metamodels.events.ComplexEventPattern#evaluateParameterBindings(org.eclipse.viatra.cep.core.metamodels.events.Event)
     * @generated
     */
    EOperation getComplexEventPattern__EvaluateParameterBindings__Event();

    /**
     * Returns the meta object for class '{@link org.eclipse.viatra.cep.core.metamodels.events.EventPatternReference <em>Event Pattern Reference</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for class '<em>Event Pattern Reference</em>'.
     * @see org.eclipse.viatra.cep.core.metamodels.events.EventPatternReference
     * @generated
     */
    EClass getEventPatternReference();

    /**
     * Returns the meta object for the containment reference '{@link org.eclipse.viatra.cep.core.metamodels.events.EventPatternReference#getEventPattern <em>Event Pattern</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the containment reference '<em>Event Pattern</em>'.
     * @see org.eclipse.viatra.cep.core.metamodels.events.EventPatternReference#getEventPattern()
     * @see #getEventPatternReference()
     * @generated
     */
    EReference getEventPatternReference_EventPattern();

    /**
     * Returns the meta object for the containment reference '{@link org.eclipse.viatra.cep.core.metamodels.events.EventPatternReference#getMultiplicity <em>Multiplicity</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the containment reference '<em>Multiplicity</em>'.
     * @see org.eclipse.viatra.cep.core.metamodels.events.EventPatternReference#getMultiplicity()
     * @see #getEventPatternReference()
     * @generated
     */
    EReference getEventPatternReference_Multiplicity();

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
     * Returns the meta object for the attribute '{@link org.eclipse.viatra.cep.core.metamodels.events.Event#isIsProcessed <em>Is Processed</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Is Processed</em>'.
     * @see org.eclipse.viatra.cep.core.metamodels.events.Event#isIsProcessed()
     * @see #getEvent()
     * @generated
     */
    EAttribute getEvent_IsProcessed();

    /**
     * Returns the meta object for class '{@link org.eclipse.viatra.cep.core.metamodels.events.EventSource <em>Event Source</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for class '<em>Event Source</em>'.
     * @see org.eclipse.viatra.cep.core.metamodels.events.EventSource
     * @generated
     */
    EClass getEventSource();

    /**
     * Returns the meta object for the '{@link org.eclipse.viatra.cep.core.metamodels.events.EventSource#getId() <em>Get Id</em>}' operation.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the '<em>Get Id</em>' operation.
     * @see org.eclipse.viatra.cep.core.metamodels.events.EventSource#getId()
     * @generated
     */
    EOperation getEventSource__GetId();

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
     * Returns the meta object for class '{@link org.eclipse.viatra.cep.core.metamodels.events.AND <em>AND</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for class '<em>AND</em>'.
     * @see org.eclipse.viatra.cep.core.metamodels.events.AND
     * @generated
     */
    EClass getAND();

    /**
     * Returns the meta object for class '{@link org.eclipse.viatra.cep.core.metamodels.events.Timewindow <em>Timewindow</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for class '<em>Timewindow</em>'.
     * @see org.eclipse.viatra.cep.core.metamodels.events.Timewindow
     * @generated
     */
    EClass getTimewindow();

    /**
     * Returns the meta object for the attribute '{@link org.eclipse.viatra.cep.core.metamodels.events.Timewindow#getTime <em>Time</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Time</em>'.
     * @see org.eclipse.viatra.cep.core.metamodels.events.Timewindow#getTime()
     * @see #getTimewindow()
     * @generated
     */
    EAttribute getTimewindow_Time();

    /**
     * Returns the meta object for class '{@link org.eclipse.viatra.cep.core.metamodels.events.AbstractMultiplicity <em>Abstract Multiplicity</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for class '<em>Abstract Multiplicity</em>'.
     * @see org.eclipse.viatra.cep.core.metamodels.events.AbstractMultiplicity
     * @generated
     */
    EClass getAbstractMultiplicity();

    /**
     * Returns the meta object for class '{@link org.eclipse.viatra.cep.core.metamodels.events.Multiplicity <em>Multiplicity</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for class '<em>Multiplicity</em>'.
     * @see org.eclipse.viatra.cep.core.metamodels.events.Multiplicity
     * @generated
     */
    EClass getMultiplicity();

    /**
     * Returns the meta object for the attribute '{@link org.eclipse.viatra.cep.core.metamodels.events.Multiplicity#getValue <em>Value</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Value</em>'.
     * @see org.eclipse.viatra.cep.core.metamodels.events.Multiplicity#getValue()
     * @see #getMultiplicity()
     * @generated
     */
    EAttribute getMultiplicity_Value();

    /**
     * Returns the meta object for class '{@link org.eclipse.viatra.cep.core.metamodels.events.Infinite <em>Infinite</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for class '<em>Infinite</em>'.
     * @see org.eclipse.viatra.cep.core.metamodels.events.Infinite
     * @generated
     */
    EClass getInfinite();

    /**
     * Returns the meta object for class '{@link org.eclipse.viatra.cep.core.metamodels.events.AtLeastOne <em>At Least One</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for class '<em>At Least One</em>'.
     * @see org.eclipse.viatra.cep.core.metamodels.events.AtLeastOne
     * @generated
     */
    EClass getAtLeastOne();

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
         * The meta object literal for the '<em><b>Operator</b></em>' containment reference feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EReference COMPLEX_EVENT_PATTERN__OPERATOR = eINSTANCE.getComplexEventPattern_Operator();

        /**
         * The meta object literal for the '<em><b>Timewindow</b></em>' containment reference feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EReference COMPLEX_EVENT_PATTERN__TIMEWINDOW = eINSTANCE.getComplexEventPattern_Timewindow();

        /**
         * The meta object literal for the '<em><b>Contained Event Patterns</b></em>' containment reference list feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EReference COMPLEX_EVENT_PATTERN__CONTAINED_EVENT_PATTERNS = eINSTANCE.getComplexEventPattern_ContainedEventPatterns();

        /**
         * The meta object literal for the '<em><b>Evaluate Parameter Bindings</b></em>' operation.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EOperation COMPLEX_EVENT_PATTERN___EVALUATE_PARAMETER_BINDINGS__EVENT = eINSTANCE.getComplexEventPattern__EvaluateParameterBindings__Event();

        /**
         * The meta object literal for the '{@link org.eclipse.viatra.cep.core.metamodels.events.impl.EventPatternReferenceImpl <em>Event Pattern Reference</em>}' class.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @see org.eclipse.viatra.cep.core.metamodels.events.impl.EventPatternReferenceImpl
         * @see org.eclipse.viatra.cep.core.metamodels.events.impl.EventsPackageImpl#getEventPatternReference()
         * @generated
         */
        EClass EVENT_PATTERN_REFERENCE = eINSTANCE.getEventPatternReference();

        /**
         * The meta object literal for the '<em><b>Event Pattern</b></em>' containment reference feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EReference EVENT_PATTERN_REFERENCE__EVENT_PATTERN = eINSTANCE.getEventPatternReference_EventPattern();

        /**
         * The meta object literal for the '<em><b>Multiplicity</b></em>' containment reference feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EReference EVENT_PATTERN_REFERENCE__MULTIPLICITY = eINSTANCE.getEventPatternReference_Multiplicity();

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
         * The meta object literal for the '<em><b>Is Processed</b></em>' attribute feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EAttribute EVENT__IS_PROCESSED = eINSTANCE.getEvent_IsProcessed();

        /**
         * The meta object literal for the '{@link org.eclipse.viatra.cep.core.metamodels.events.impl.EventSourceImpl <em>Event Source</em>}' class.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @see org.eclipse.viatra.cep.core.metamodels.events.impl.EventSourceImpl
         * @see org.eclipse.viatra.cep.core.metamodels.events.impl.EventsPackageImpl#getEventSource()
         * @generated
         */
        EClass EVENT_SOURCE = eINSTANCE.getEventSource();

        /**
         * The meta object literal for the '<em><b>Get Id</b></em>' operation.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EOperation EVENT_SOURCE___GET_ID = eINSTANCE.getEventSource__GetId();

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
         * The meta object literal for the '{@link org.eclipse.viatra.cep.core.metamodels.events.impl.ANDImpl <em>AND</em>}' class.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @see org.eclipse.viatra.cep.core.metamodels.events.impl.ANDImpl
         * @see org.eclipse.viatra.cep.core.metamodels.events.impl.EventsPackageImpl#getAND()
         * @generated
         */
        EClass AND = eINSTANCE.getAND();

        /**
         * The meta object literal for the '{@link org.eclipse.viatra.cep.core.metamodels.events.impl.TimewindowImpl <em>Timewindow</em>}' class.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @see org.eclipse.viatra.cep.core.metamodels.events.impl.TimewindowImpl
         * @see org.eclipse.viatra.cep.core.metamodels.events.impl.EventsPackageImpl#getTimewindow()
         * @generated
         */
        EClass TIMEWINDOW = eINSTANCE.getTimewindow();

        /**
         * The meta object literal for the '<em><b>Time</b></em>' attribute feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EAttribute TIMEWINDOW__TIME = eINSTANCE.getTimewindow_Time();

        /**
         * The meta object literal for the '{@link org.eclipse.viatra.cep.core.metamodels.events.impl.AbstractMultiplicityImpl <em>Abstract Multiplicity</em>}' class.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @see org.eclipse.viatra.cep.core.metamodels.events.impl.AbstractMultiplicityImpl
         * @see org.eclipse.viatra.cep.core.metamodels.events.impl.EventsPackageImpl#getAbstractMultiplicity()
         * @generated
         */
        EClass ABSTRACT_MULTIPLICITY = eINSTANCE.getAbstractMultiplicity();

        /**
         * The meta object literal for the '{@link org.eclipse.viatra.cep.core.metamodels.events.impl.MultiplicityImpl <em>Multiplicity</em>}' class.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @see org.eclipse.viatra.cep.core.metamodels.events.impl.MultiplicityImpl
         * @see org.eclipse.viatra.cep.core.metamodels.events.impl.EventsPackageImpl#getMultiplicity()
         * @generated
         */
        EClass MULTIPLICITY = eINSTANCE.getMultiplicity();

        /**
         * The meta object literal for the '<em><b>Value</b></em>' attribute feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EAttribute MULTIPLICITY__VALUE = eINSTANCE.getMultiplicity_Value();

        /**
         * The meta object literal for the '{@link org.eclipse.viatra.cep.core.metamodels.events.impl.InfiniteImpl <em>Infinite</em>}' class.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @see org.eclipse.viatra.cep.core.metamodels.events.impl.InfiniteImpl
         * @see org.eclipse.viatra.cep.core.metamodels.events.impl.EventsPackageImpl#getInfinite()
         * @generated
         */
        EClass INFINITE = eINSTANCE.getInfinite();

        /**
         * The meta object literal for the '{@link org.eclipse.viatra.cep.core.metamodels.events.impl.AtLeastOneImpl <em>At Least One</em>}' class.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @see org.eclipse.viatra.cep.core.metamodels.events.impl.AtLeastOneImpl
         * @see org.eclipse.viatra.cep.core.metamodels.events.impl.EventsPackageImpl#getAtLeastOne()
         * @generated
         */
        EClass AT_LEAST_ONE = eINSTANCE.getAtLeastOne();

    }

} //EventsPackage
