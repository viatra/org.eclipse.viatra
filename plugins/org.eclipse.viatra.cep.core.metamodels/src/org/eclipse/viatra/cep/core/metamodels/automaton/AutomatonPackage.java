/**
 */
package org.eclipse.viatra.cep.core.metamodels.automaton;

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EEnum;
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
 * @see org.eclipse.viatra.cep.core.metamodels.automaton.AutomatonFactory
 * @model kind="package"
 * @generated
 */
public interface AutomatonPackage extends EPackage {
    /**
     * The package name.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    String eNAME = "automaton";

    /**
     * The package namespace URI.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    String eNS_URI = "automaton.meta";

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
    AutomatonPackage eINSTANCE = org.eclipse.viatra.cep.core.metamodels.automaton.impl.AutomatonPackageImpl.init();

    /**
     * The meta object id for the '{@link org.eclipse.viatra.cep.core.metamodels.automaton.impl.InternalModelImpl <em>Internal Model</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.eclipse.viatra.cep.core.metamodels.automaton.impl.InternalModelImpl
     * @see org.eclipse.viatra.cep.core.metamodels.automaton.impl.AutomatonPackageImpl#getInternalModel()
     * @generated
     */
    int INTERNAL_MODEL = 0;

    /**
     * The feature id for the '<em><b>Automata</b></em>' containment reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int INTERNAL_MODEL__AUTOMATA = 0;

    /**
     * The feature id for the '<em><b>Latest Event</b></em>' containment reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int INTERNAL_MODEL__LATEST_EVENT = 1;

    /**
     * The feature id for the '<em><b>Context</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int INTERNAL_MODEL__CONTEXT = 2;

    /**
     * The number of structural features of the '<em>Internal Model</em>' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int INTERNAL_MODEL_FEATURE_COUNT = 3;

    /**
     * The number of operations of the '<em>Internal Model</em>' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int INTERNAL_MODEL_OPERATION_COUNT = 0;

    /**
     * The meta object id for the '{@link org.eclipse.viatra.cep.core.metamodels.automaton.impl.AutomatonImpl <em>Automaton</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.eclipse.viatra.cep.core.metamodels.automaton.impl.AutomatonImpl
     * @see org.eclipse.viatra.cep.core.metamodels.automaton.impl.AutomatonPackageImpl#getAutomaton()
     * @generated
     */
    int AUTOMATON = 1;

    /**
     * The feature id for the '<em><b>States</b></em>' containment reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int AUTOMATON__STATES = 0;

    /**
     * The feature id for the '<em><b>Event Pattern</b></em>' reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int AUTOMATON__EVENT_PATTERN = 1;

    /**
     * The feature id for the '<em><b>Event Tokens</b></em>' containment reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int AUTOMATON__EVENT_TOKENS = 2;

    /**
     * The feature id for the '<em><b>Timed Zones</b></em>' containment reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int AUTOMATON__TIMED_ZONES = 3;

    /**
     * The number of structural features of the '<em>Automaton</em>' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int AUTOMATON_FEATURE_COUNT = 4;

    /**
     * The number of operations of the '<em>Automaton</em>' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int AUTOMATON_OPERATION_COUNT = 0;

    /**
     * The meta object id for the '{@link org.eclipse.viatra.cep.core.metamodels.automaton.impl.EventTokenImpl <em>Event Token</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.eclipse.viatra.cep.core.metamodels.automaton.impl.EventTokenImpl
     * @see org.eclipse.viatra.cep.core.metamodels.automaton.impl.AutomatonPackageImpl#getEventToken()
     * @generated
     */
    int EVENT_TOKEN = 2;

    /**
     * The feature id for the '<em><b>Current State</b></em>' reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int EVENT_TOKEN__CURRENT_STATE = 0;

    /**
     * The feature id for the '<em><b>Recorded Events</b></em>' reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int EVENT_TOKEN__RECORDED_EVENTS = 1;

    /**
     * The feature id for the '<em><b>Last Processed</b></em>' reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int EVENT_TOKEN__LAST_PROCESSED = 2;

    /**
     * The feature id for the '<em><b>Timed Zones</b></em>' reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int EVENT_TOKEN__TIMED_ZONES = 3;

    /**
     * The number of structural features of the '<em>Event Token</em>' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int EVENT_TOKEN_FEATURE_COUNT = 4;

    /**
     * The number of operations of the '<em>Event Token</em>' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int EVENT_TOKEN_OPERATION_COUNT = 0;

    /**
     * The meta object id for the '{@link org.eclipse.viatra.cep.core.metamodels.automaton.impl.StateImpl <em>State</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.eclipse.viatra.cep.core.metamodels.automaton.impl.StateImpl
     * @see org.eclipse.viatra.cep.core.metamodels.automaton.impl.AutomatonPackageImpl#getState()
     * @generated
     */
    int STATE = 3;

    /**
     * The feature id for the '<em><b>In Transitions</b></em>' reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int STATE__IN_TRANSITIONS = 0;

    /**
     * The feature id for the '<em><b>Out Transitions</b></em>' containment reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int STATE__OUT_TRANSITIONS = 1;

    /**
     * The feature id for the '<em><b>Label</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int STATE__LABEL = 2;

    /**
     * The feature id for the '<em><b>Event Tokens</b></em>' reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int STATE__EVENT_TOKENS = 3;

    /**
     * The feature id for the '<em><b>Last Processed Event</b></em>' reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int STATE__LAST_PROCESSED_EVENT = 4;

    /**
     * The feature id for the '<em><b>In State Of</b></em>' reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int STATE__IN_STATE_OF = 5;

    /**
     * The feature id for the '<em><b>Out State Of</b></em>' reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int STATE__OUT_STATE_OF = 6;

    /**
     * The number of structural features of the '<em>State</em>' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int STATE_FEATURE_COUNT = 7;

    /**
     * The number of operations of the '<em>State</em>' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int STATE_OPERATION_COUNT = 0;

    /**
     * The meta object id for the '{@link org.eclipse.viatra.cep.core.metamodels.automaton.impl.InitStateImpl <em>Init State</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.eclipse.viatra.cep.core.metamodels.automaton.impl.InitStateImpl
     * @see org.eclipse.viatra.cep.core.metamodels.automaton.impl.AutomatonPackageImpl#getInitState()
     * @generated
     */
    int INIT_STATE = 4;

    /**
     * The feature id for the '<em><b>In Transitions</b></em>' reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int INIT_STATE__IN_TRANSITIONS = STATE__IN_TRANSITIONS;

    /**
     * The feature id for the '<em><b>Out Transitions</b></em>' containment reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int INIT_STATE__OUT_TRANSITIONS = STATE__OUT_TRANSITIONS;

    /**
     * The feature id for the '<em><b>Label</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int INIT_STATE__LABEL = STATE__LABEL;

    /**
     * The feature id for the '<em><b>Event Tokens</b></em>' reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int INIT_STATE__EVENT_TOKENS = STATE__EVENT_TOKENS;

    /**
     * The feature id for the '<em><b>Last Processed Event</b></em>' reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int INIT_STATE__LAST_PROCESSED_EVENT = STATE__LAST_PROCESSED_EVENT;

    /**
     * The feature id for the '<em><b>In State Of</b></em>' reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int INIT_STATE__IN_STATE_OF = STATE__IN_STATE_OF;

    /**
     * The feature id for the '<em><b>Out State Of</b></em>' reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int INIT_STATE__OUT_STATE_OF = STATE__OUT_STATE_OF;

    /**
     * The number of structural features of the '<em>Init State</em>' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int INIT_STATE_FEATURE_COUNT = STATE_FEATURE_COUNT + 0;

    /**
     * The number of operations of the '<em>Init State</em>' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int INIT_STATE_OPERATION_COUNT = STATE_OPERATION_COUNT + 0;

    /**
     * The meta object id for the '{@link org.eclipse.viatra.cep.core.metamodels.automaton.impl.FinalStateImpl <em>Final State</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.eclipse.viatra.cep.core.metamodels.automaton.impl.FinalStateImpl
     * @see org.eclipse.viatra.cep.core.metamodels.automaton.impl.AutomatonPackageImpl#getFinalState()
     * @generated
     */
    int FINAL_STATE = 5;

    /**
     * The feature id for the '<em><b>In Transitions</b></em>' reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int FINAL_STATE__IN_TRANSITIONS = STATE__IN_TRANSITIONS;

    /**
     * The feature id for the '<em><b>Out Transitions</b></em>' containment reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int FINAL_STATE__OUT_TRANSITIONS = STATE__OUT_TRANSITIONS;

    /**
     * The feature id for the '<em><b>Label</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int FINAL_STATE__LABEL = STATE__LABEL;

    /**
     * The feature id for the '<em><b>Event Tokens</b></em>' reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int FINAL_STATE__EVENT_TOKENS = STATE__EVENT_TOKENS;

    /**
     * The feature id for the '<em><b>Last Processed Event</b></em>' reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int FINAL_STATE__LAST_PROCESSED_EVENT = STATE__LAST_PROCESSED_EVENT;

    /**
     * The feature id for the '<em><b>In State Of</b></em>' reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int FINAL_STATE__IN_STATE_OF = STATE__IN_STATE_OF;

    /**
     * The feature id for the '<em><b>Out State Of</b></em>' reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int FINAL_STATE__OUT_STATE_OF = STATE__OUT_STATE_OF;

    /**
     * The number of structural features of the '<em>Final State</em>' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int FINAL_STATE_FEATURE_COUNT = STATE_FEATURE_COUNT + 0;

    /**
     * The number of operations of the '<em>Final State</em>' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int FINAL_STATE_OPERATION_COUNT = STATE_OPERATION_COUNT + 0;

    /**
     * The meta object id for the '{@link org.eclipse.viatra.cep.core.metamodels.automaton.impl.TrapStateImpl <em>Trap State</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.eclipse.viatra.cep.core.metamodels.automaton.impl.TrapStateImpl
     * @see org.eclipse.viatra.cep.core.metamodels.automaton.impl.AutomatonPackageImpl#getTrapState()
     * @generated
     */
    int TRAP_STATE = 6;

    /**
     * The feature id for the '<em><b>In Transitions</b></em>' reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int TRAP_STATE__IN_TRANSITIONS = STATE__IN_TRANSITIONS;

    /**
     * The feature id for the '<em><b>Out Transitions</b></em>' containment reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int TRAP_STATE__OUT_TRANSITIONS = STATE__OUT_TRANSITIONS;

    /**
     * The feature id for the '<em><b>Label</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int TRAP_STATE__LABEL = STATE__LABEL;

    /**
     * The feature id for the '<em><b>Event Tokens</b></em>' reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int TRAP_STATE__EVENT_TOKENS = STATE__EVENT_TOKENS;

    /**
     * The feature id for the '<em><b>Last Processed Event</b></em>' reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int TRAP_STATE__LAST_PROCESSED_EVENT = STATE__LAST_PROCESSED_EVENT;

    /**
     * The feature id for the '<em><b>In State Of</b></em>' reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int TRAP_STATE__IN_STATE_OF = STATE__IN_STATE_OF;

    /**
     * The feature id for the '<em><b>Out State Of</b></em>' reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int TRAP_STATE__OUT_STATE_OF = STATE__OUT_STATE_OF;

    /**
     * The number of structural features of the '<em>Trap State</em>' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int TRAP_STATE_FEATURE_COUNT = STATE_FEATURE_COUNT + 0;

    /**
     * The number of operations of the '<em>Trap State</em>' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int TRAP_STATE_OPERATION_COUNT = STATE_OPERATION_COUNT + 0;

    /**
     * The meta object id for the '{@link org.eclipse.viatra.cep.core.metamodels.automaton.impl.TransitionImpl <em>Transition</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.eclipse.viatra.cep.core.metamodels.automaton.impl.TransitionImpl
     * @see org.eclipse.viatra.cep.core.metamodels.automaton.impl.AutomatonPackageImpl#getTransition()
     * @generated
     */
    int TRANSITION = 7;

    /**
     * The feature id for the '<em><b>Pre State</b></em>' container reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int TRANSITION__PRE_STATE = 0;

    /**
     * The feature id for the '<em><b>Post State</b></em>' reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int TRANSITION__POST_STATE = 1;

    /**
     * The number of structural features of the '<em>Transition</em>' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int TRANSITION_FEATURE_COUNT = 2;

    /**
     * The number of operations of the '<em>Transition</em>' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int TRANSITION_OPERATION_COUNT = 0;

    /**
     * The meta object id for the '{@link org.eclipse.viatra.cep.core.metamodels.automaton.impl.TypedTransitionImpl <em>Typed Transition</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.eclipse.viatra.cep.core.metamodels.automaton.impl.TypedTransitionImpl
     * @see org.eclipse.viatra.cep.core.metamodels.automaton.impl.AutomatonPackageImpl#getTypedTransition()
     * @generated
     */
    int TYPED_TRANSITION = 8;

    /**
     * The feature id for the '<em><b>Pre State</b></em>' container reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int TYPED_TRANSITION__PRE_STATE = TRANSITION__PRE_STATE;

    /**
     * The feature id for the '<em><b>Post State</b></em>' reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int TYPED_TRANSITION__POST_STATE = TRANSITION__POST_STATE;

    /**
     * The feature id for the '<em><b>Guard</b></em>' containment reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int TYPED_TRANSITION__GUARD = TRANSITION_FEATURE_COUNT + 0;

    /**
     * The number of structural features of the '<em>Typed Transition</em>' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int TYPED_TRANSITION_FEATURE_COUNT = TRANSITION_FEATURE_COUNT + 1;

    /**
     * The number of operations of the '<em>Typed Transition</em>' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int TYPED_TRANSITION_OPERATION_COUNT = TRANSITION_OPERATION_COUNT + 0;

    /**
     * The meta object id for the '{@link org.eclipse.viatra.cep.core.metamodels.automaton.impl.EpsilonTransitionImpl <em>Epsilon Transition</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.eclipse.viatra.cep.core.metamodels.automaton.impl.EpsilonTransitionImpl
     * @see org.eclipse.viatra.cep.core.metamodels.automaton.impl.AutomatonPackageImpl#getEpsilonTransition()
     * @generated
     */
    int EPSILON_TRANSITION = 9;

    /**
     * The feature id for the '<em><b>Pre State</b></em>' container reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int EPSILON_TRANSITION__PRE_STATE = TRANSITION__PRE_STATE;

    /**
     * The feature id for the '<em><b>Post State</b></em>' reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int EPSILON_TRANSITION__POST_STATE = TRANSITION__POST_STATE;

    /**
     * The number of structural features of the '<em>Epsilon Transition</em>' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int EPSILON_TRANSITION_FEATURE_COUNT = TRANSITION_FEATURE_COUNT + 0;

    /**
     * The number of operations of the '<em>Epsilon Transition</em>' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int EPSILON_TRANSITION_OPERATION_COUNT = TRANSITION_OPERATION_COUNT + 0;

    /**
     * The meta object id for the '{@link org.eclipse.viatra.cep.core.metamodels.automaton.impl.GuardImpl <em>Guard</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.eclipse.viatra.cep.core.metamodels.automaton.impl.GuardImpl
     * @see org.eclipse.viatra.cep.core.metamodels.automaton.impl.AutomatonPackageImpl#getGuard()
     * @generated
     */
    int GUARD = 10;

    /**
     * The feature id for the '<em><b>Event Type</b></em>' reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int GUARD__EVENT_TYPE = 0;

    /**
     * The feature id for the '<em><b>Transition</b></em>' container reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int GUARD__TRANSITION = 1;

    /**
     * The number of structural features of the '<em>Guard</em>' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int GUARD_FEATURE_COUNT = 2;

    /**
     * The number of operations of the '<em>Guard</em>' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int GUARD_OPERATION_COUNT = 0;

    /**
     * The meta object id for the '{@link org.eclipse.viatra.cep.core.metamodels.automaton.impl.TimedZoneImpl <em>Timed Zone</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.eclipse.viatra.cep.core.metamodels.automaton.impl.TimedZoneImpl
     * @see org.eclipse.viatra.cep.core.metamodels.automaton.impl.AutomatonPackageImpl#getTimedZone()
     * @generated
     */
    int TIMED_ZONE = 11;

    /**
     * The feature id for the '<em><b>In State</b></em>' reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int TIMED_ZONE__IN_STATE = 0;

    /**
     * The feature id for the '<em><b>Out State</b></em>' reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int TIMED_ZONE__OUT_STATE = 1;

    /**
     * The feature id for the '<em><b>Time</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int TIMED_ZONE__TIME = 2;

    /**
     * The number of structural features of the '<em>Timed Zone</em>' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int TIMED_ZONE_FEATURE_COUNT = 3;

    /**
     * The number of operations of the '<em>Timed Zone</em>' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int TIMED_ZONE_OPERATION_COUNT = 0;

    /**
     * The meta object id for the '{@link org.eclipse.viatra.cep.core.metamodels.automaton.impl.WithinImpl <em>Within</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.eclipse.viatra.cep.core.metamodels.automaton.impl.WithinImpl
     * @see org.eclipse.viatra.cep.core.metamodels.automaton.impl.AutomatonPackageImpl#getWithin()
     * @generated
     */
    int WITHIN = 12;

    /**
     * The feature id for the '<em><b>In State</b></em>' reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int WITHIN__IN_STATE = TIMED_ZONE__IN_STATE;

    /**
     * The feature id for the '<em><b>Out State</b></em>' reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int WITHIN__OUT_STATE = TIMED_ZONE__OUT_STATE;

    /**
     * The feature id for the '<em><b>Time</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int WITHIN__TIME = TIMED_ZONE__TIME;

    /**
     * The number of structural features of the '<em>Within</em>' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int WITHIN_FEATURE_COUNT = TIMED_ZONE_FEATURE_COUNT + 0;

    /**
     * The number of operations of the '<em>Within</em>' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int WITHIN_OPERATION_COUNT = TIMED_ZONE_OPERATION_COUNT + 0;

    /**
     * The meta object id for the '{@link org.eclipse.viatra.cep.core.metamodels.automaton.impl.HoldsForImpl <em>Holds For</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.eclipse.viatra.cep.core.metamodels.automaton.impl.HoldsForImpl
     * @see org.eclipse.viatra.cep.core.metamodels.automaton.impl.AutomatonPackageImpl#getHoldsFor()
     * @generated
     */
    int HOLDS_FOR = 13;

    /**
     * The feature id for the '<em><b>In State</b></em>' reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int HOLDS_FOR__IN_STATE = TIMED_ZONE__IN_STATE;

    /**
     * The feature id for the '<em><b>Out State</b></em>' reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int HOLDS_FOR__OUT_STATE = TIMED_ZONE__OUT_STATE;

    /**
     * The feature id for the '<em><b>Time</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int HOLDS_FOR__TIME = TIMED_ZONE__TIME;

    /**
     * The number of structural features of the '<em>Holds For</em>' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int HOLDS_FOR_FEATURE_COUNT = TIMED_ZONE_FEATURE_COUNT + 0;

    /**
     * The number of operations of the '<em>Holds For</em>' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int HOLDS_FOR_OPERATION_COUNT = TIMED_ZONE_OPERATION_COUNT + 0;

    /**
     * The meta object id for the '{@link org.eclipse.viatra.cep.core.metamodels.automaton.EventContext <em>Event Context</em>}' enum.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.eclipse.viatra.cep.core.metamodels.automaton.EventContext
     * @see org.eclipse.viatra.cep.core.metamodels.automaton.impl.AutomatonPackageImpl#getEventContext()
     * @generated
     */
    int EVENT_CONTEXT = 14;


    /**
     * Returns the meta object for class '{@link org.eclipse.viatra.cep.core.metamodels.automaton.InternalModel <em>Internal Model</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for class '<em>Internal Model</em>'.
     * @see org.eclipse.viatra.cep.core.metamodels.automaton.InternalModel
     * @generated
     */
    EClass getInternalModel();

    /**
     * Returns the meta object for the containment reference list '{@link org.eclipse.viatra.cep.core.metamodels.automaton.InternalModel#getAutomata <em>Automata</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the containment reference list '<em>Automata</em>'.
     * @see org.eclipse.viatra.cep.core.metamodels.automaton.InternalModel#getAutomata()
     * @see #getInternalModel()
     * @generated
     */
    EReference getInternalModel_Automata();

    /**
     * Returns the meta object for the containment reference '{@link org.eclipse.viatra.cep.core.metamodels.automaton.InternalModel#getLatestEvent <em>Latest Event</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the containment reference '<em>Latest Event</em>'.
     * @see org.eclipse.viatra.cep.core.metamodels.automaton.InternalModel#getLatestEvent()
     * @see #getInternalModel()
     * @generated
     */
    EReference getInternalModel_LatestEvent();

    /**
     * Returns the meta object for the attribute '{@link org.eclipse.viatra.cep.core.metamodels.automaton.InternalModel#getContext <em>Context</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Context</em>'.
     * @see org.eclipse.viatra.cep.core.metamodels.automaton.InternalModel#getContext()
     * @see #getInternalModel()
     * @generated
     */
    EAttribute getInternalModel_Context();

    /**
     * Returns the meta object for class '{@link org.eclipse.viatra.cep.core.metamodels.automaton.Automaton <em>Automaton</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for class '<em>Automaton</em>'.
     * @see org.eclipse.viatra.cep.core.metamodels.automaton.Automaton
     * @generated
     */
    EClass getAutomaton();

    /**
     * Returns the meta object for the containment reference list '{@link org.eclipse.viatra.cep.core.metamodels.automaton.Automaton#getStates <em>States</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the containment reference list '<em>States</em>'.
     * @see org.eclipse.viatra.cep.core.metamodels.automaton.Automaton#getStates()
     * @see #getAutomaton()
     * @generated
     */
    EReference getAutomaton_States();

    /**
     * Returns the meta object for the reference '{@link org.eclipse.viatra.cep.core.metamodels.automaton.Automaton#getEventPattern <em>Event Pattern</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the reference '<em>Event Pattern</em>'.
     * @see org.eclipse.viatra.cep.core.metamodels.automaton.Automaton#getEventPattern()
     * @see #getAutomaton()
     * @generated
     */
    EReference getAutomaton_EventPattern();

    /**
     * Returns the meta object for the containment reference list '{@link org.eclipse.viatra.cep.core.metamodels.automaton.Automaton#getEventTokens <em>Event Tokens</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the containment reference list '<em>Event Tokens</em>'.
     * @see org.eclipse.viatra.cep.core.metamodels.automaton.Automaton#getEventTokens()
     * @see #getAutomaton()
     * @generated
     */
    EReference getAutomaton_EventTokens();

    /**
     * Returns the meta object for the containment reference list '{@link org.eclipse.viatra.cep.core.metamodels.automaton.Automaton#getTimedZones <em>Timed Zones</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the containment reference list '<em>Timed Zones</em>'.
     * @see org.eclipse.viatra.cep.core.metamodels.automaton.Automaton#getTimedZones()
     * @see #getAutomaton()
     * @generated
     */
    EReference getAutomaton_TimedZones();

    /**
     * Returns the meta object for class '{@link org.eclipse.viatra.cep.core.metamodels.automaton.EventToken <em>Event Token</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for class '<em>Event Token</em>'.
     * @see org.eclipse.viatra.cep.core.metamodels.automaton.EventToken
     * @generated
     */
    EClass getEventToken();

    /**
     * Returns the meta object for the reference '{@link org.eclipse.viatra.cep.core.metamodels.automaton.EventToken#getCurrentState <em>Current State</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the reference '<em>Current State</em>'.
     * @see org.eclipse.viatra.cep.core.metamodels.automaton.EventToken#getCurrentState()
     * @see #getEventToken()
     * @generated
     */
    EReference getEventToken_CurrentState();

    /**
     * Returns the meta object for the reference list '{@link org.eclipse.viatra.cep.core.metamodels.automaton.EventToken#getRecordedEvents <em>Recorded Events</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the reference list '<em>Recorded Events</em>'.
     * @see org.eclipse.viatra.cep.core.metamodels.automaton.EventToken#getRecordedEvents()
     * @see #getEventToken()
     * @generated
     */
    EReference getEventToken_RecordedEvents();

    /**
     * Returns the meta object for the reference '{@link org.eclipse.viatra.cep.core.metamodels.automaton.EventToken#getLastProcessed <em>Last Processed</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the reference '<em>Last Processed</em>'.
     * @see org.eclipse.viatra.cep.core.metamodels.automaton.EventToken#getLastProcessed()
     * @see #getEventToken()
     * @generated
     */
    EReference getEventToken_LastProcessed();

    /**
     * Returns the meta object for the reference list '{@link org.eclipse.viatra.cep.core.metamodels.automaton.EventToken#getTimedZones <em>Timed Zones</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the reference list '<em>Timed Zones</em>'.
     * @see org.eclipse.viatra.cep.core.metamodels.automaton.EventToken#getTimedZones()
     * @see #getEventToken()
     * @generated
     */
    EReference getEventToken_TimedZones();

    /**
     * Returns the meta object for class '{@link org.eclipse.viatra.cep.core.metamodels.automaton.State <em>State</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for class '<em>State</em>'.
     * @see org.eclipse.viatra.cep.core.metamodels.automaton.State
     * @generated
     */
    EClass getState();

    /**
     * Returns the meta object for the reference list '{@link org.eclipse.viatra.cep.core.metamodels.automaton.State#getInTransitions <em>In Transitions</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the reference list '<em>In Transitions</em>'.
     * @see org.eclipse.viatra.cep.core.metamodels.automaton.State#getInTransitions()
     * @see #getState()
     * @generated
     */
    EReference getState_InTransitions();

    /**
     * Returns the meta object for the containment reference list '{@link org.eclipse.viatra.cep.core.metamodels.automaton.State#getOutTransitions <em>Out Transitions</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the containment reference list '<em>Out Transitions</em>'.
     * @see org.eclipse.viatra.cep.core.metamodels.automaton.State#getOutTransitions()
     * @see #getState()
     * @generated
     */
    EReference getState_OutTransitions();

    /**
     * Returns the meta object for the attribute '{@link org.eclipse.viatra.cep.core.metamodels.automaton.State#getLabel <em>Label</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Label</em>'.
     * @see org.eclipse.viatra.cep.core.metamodels.automaton.State#getLabel()
     * @see #getState()
     * @generated
     */
    EAttribute getState_Label();

    /**
     * Returns the meta object for the reference list '{@link org.eclipse.viatra.cep.core.metamodels.automaton.State#getEventTokens <em>Event Tokens</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the reference list '<em>Event Tokens</em>'.
     * @see org.eclipse.viatra.cep.core.metamodels.automaton.State#getEventTokens()
     * @see #getState()
     * @generated
     */
    EReference getState_EventTokens();

    /**
     * Returns the meta object for the reference '{@link org.eclipse.viatra.cep.core.metamodels.automaton.State#getLastProcessedEvent <em>Last Processed Event</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the reference '<em>Last Processed Event</em>'.
     * @see org.eclipse.viatra.cep.core.metamodels.automaton.State#getLastProcessedEvent()
     * @see #getState()
     * @generated
     */
    EReference getState_LastProcessedEvent();

    /**
     * Returns the meta object for the reference list '{@link org.eclipse.viatra.cep.core.metamodels.automaton.State#getInStateOf <em>In State Of</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the reference list '<em>In State Of</em>'.
     * @see org.eclipse.viatra.cep.core.metamodels.automaton.State#getInStateOf()
     * @see #getState()
     * @generated
     */
    EReference getState_InStateOf();

    /**
     * Returns the meta object for the reference list '{@link org.eclipse.viatra.cep.core.metamodels.automaton.State#getOutStateOf <em>Out State Of</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the reference list '<em>Out State Of</em>'.
     * @see org.eclipse.viatra.cep.core.metamodels.automaton.State#getOutStateOf()
     * @see #getState()
     * @generated
     */
    EReference getState_OutStateOf();

    /**
     * Returns the meta object for class '{@link org.eclipse.viatra.cep.core.metamodels.automaton.InitState <em>Init State</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for class '<em>Init State</em>'.
     * @see org.eclipse.viatra.cep.core.metamodels.automaton.InitState
     * @generated
     */
    EClass getInitState();

    /**
     * Returns the meta object for class '{@link org.eclipse.viatra.cep.core.metamodels.automaton.FinalState <em>Final State</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for class '<em>Final State</em>'.
     * @see org.eclipse.viatra.cep.core.metamodels.automaton.FinalState
     * @generated
     */
    EClass getFinalState();

    /**
     * Returns the meta object for class '{@link org.eclipse.viatra.cep.core.metamodels.automaton.TrapState <em>Trap State</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for class '<em>Trap State</em>'.
     * @see org.eclipse.viatra.cep.core.metamodels.automaton.TrapState
     * @generated
     */
    EClass getTrapState();

    /**
     * Returns the meta object for class '{@link org.eclipse.viatra.cep.core.metamodels.automaton.Transition <em>Transition</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for class '<em>Transition</em>'.
     * @see org.eclipse.viatra.cep.core.metamodels.automaton.Transition
     * @generated
     */
    EClass getTransition();

    /**
     * Returns the meta object for the container reference '{@link org.eclipse.viatra.cep.core.metamodels.automaton.Transition#getPreState <em>Pre State</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the container reference '<em>Pre State</em>'.
     * @see org.eclipse.viatra.cep.core.metamodels.automaton.Transition#getPreState()
     * @see #getTransition()
     * @generated
     */
    EReference getTransition_PreState();

    /**
     * Returns the meta object for the reference '{@link org.eclipse.viatra.cep.core.metamodels.automaton.Transition#getPostState <em>Post State</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the reference '<em>Post State</em>'.
     * @see org.eclipse.viatra.cep.core.metamodels.automaton.Transition#getPostState()
     * @see #getTransition()
     * @generated
     */
    EReference getTransition_PostState();

    /**
     * Returns the meta object for class '{@link org.eclipse.viatra.cep.core.metamodels.automaton.TypedTransition <em>Typed Transition</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for class '<em>Typed Transition</em>'.
     * @see org.eclipse.viatra.cep.core.metamodels.automaton.TypedTransition
     * @generated
     */
    EClass getTypedTransition();

    /**
     * Returns the meta object for the containment reference '{@link org.eclipse.viatra.cep.core.metamodels.automaton.TypedTransition#getGuard <em>Guard</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the containment reference '<em>Guard</em>'.
     * @see org.eclipse.viatra.cep.core.metamodels.automaton.TypedTransition#getGuard()
     * @see #getTypedTransition()
     * @generated
     */
    EReference getTypedTransition_Guard();

    /**
     * Returns the meta object for class '{@link org.eclipse.viatra.cep.core.metamodels.automaton.EpsilonTransition <em>Epsilon Transition</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for class '<em>Epsilon Transition</em>'.
     * @see org.eclipse.viatra.cep.core.metamodels.automaton.EpsilonTransition
     * @generated
     */
    EClass getEpsilonTransition();

    /**
     * Returns the meta object for class '{@link org.eclipse.viatra.cep.core.metamodels.automaton.Guard <em>Guard</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for class '<em>Guard</em>'.
     * @see org.eclipse.viatra.cep.core.metamodels.automaton.Guard
     * @generated
     */
    EClass getGuard();

    /**
     * Returns the meta object for the reference '{@link org.eclipse.viatra.cep.core.metamodels.automaton.Guard#getEventType <em>Event Type</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the reference '<em>Event Type</em>'.
     * @see org.eclipse.viatra.cep.core.metamodels.automaton.Guard#getEventType()
     * @see #getGuard()
     * @generated
     */
    EReference getGuard_EventType();

    /**
     * Returns the meta object for the container reference '{@link org.eclipse.viatra.cep.core.metamodels.automaton.Guard#getTransition <em>Transition</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the container reference '<em>Transition</em>'.
     * @see org.eclipse.viatra.cep.core.metamodels.automaton.Guard#getTransition()
     * @see #getGuard()
     * @generated
     */
    EReference getGuard_Transition();

    /**
     * Returns the meta object for class '{@link org.eclipse.viatra.cep.core.metamodels.automaton.TimedZone <em>Timed Zone</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for class '<em>Timed Zone</em>'.
     * @see org.eclipse.viatra.cep.core.metamodels.automaton.TimedZone
     * @generated
     */
    EClass getTimedZone();

    /**
     * Returns the meta object for the reference '{@link org.eclipse.viatra.cep.core.metamodels.automaton.TimedZone#getInState <em>In State</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the reference '<em>In State</em>'.
     * @see org.eclipse.viatra.cep.core.metamodels.automaton.TimedZone#getInState()
     * @see #getTimedZone()
     * @generated
     */
    EReference getTimedZone_InState();

    /**
     * Returns the meta object for the reference '{@link org.eclipse.viatra.cep.core.metamodels.automaton.TimedZone#getOutState <em>Out State</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the reference '<em>Out State</em>'.
     * @see org.eclipse.viatra.cep.core.metamodels.automaton.TimedZone#getOutState()
     * @see #getTimedZone()
     * @generated
     */
    EReference getTimedZone_OutState();

    /**
     * Returns the meta object for the attribute '{@link org.eclipse.viatra.cep.core.metamodels.automaton.TimedZone#getTime <em>Time</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Time</em>'.
     * @see org.eclipse.viatra.cep.core.metamodels.automaton.TimedZone#getTime()
     * @see #getTimedZone()
     * @generated
     */
    EAttribute getTimedZone_Time();

    /**
     * Returns the meta object for class '{@link org.eclipse.viatra.cep.core.metamodels.automaton.Within <em>Within</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for class '<em>Within</em>'.
     * @see org.eclipse.viatra.cep.core.metamodels.automaton.Within
     * @generated
     */
    EClass getWithin();

    /**
     * Returns the meta object for class '{@link org.eclipse.viatra.cep.core.metamodels.automaton.HoldsFor <em>Holds For</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for class '<em>Holds For</em>'.
     * @see org.eclipse.viatra.cep.core.metamodels.automaton.HoldsFor
     * @generated
     */
    EClass getHoldsFor();

    /**
     * Returns the meta object for enum '{@link org.eclipse.viatra.cep.core.metamodels.automaton.EventContext <em>Event Context</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for enum '<em>Event Context</em>'.
     * @see org.eclipse.viatra.cep.core.metamodels.automaton.EventContext
     * @generated
     */
    EEnum getEventContext();

    /**
     * Returns the factory that creates the instances of the model.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the factory that creates the instances of the model.
     * @generated
     */
    AutomatonFactory getAutomatonFactory();

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
         * The meta object literal for the '{@link org.eclipse.viatra.cep.core.metamodels.automaton.impl.InternalModelImpl <em>Internal Model</em>}' class.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @see org.eclipse.viatra.cep.core.metamodels.automaton.impl.InternalModelImpl
         * @see org.eclipse.viatra.cep.core.metamodels.automaton.impl.AutomatonPackageImpl#getInternalModel()
         * @generated
         */
        EClass INTERNAL_MODEL = eINSTANCE.getInternalModel();

        /**
         * The meta object literal for the '<em><b>Automata</b></em>' containment reference list feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EReference INTERNAL_MODEL__AUTOMATA = eINSTANCE.getInternalModel_Automata();

        /**
         * The meta object literal for the '<em><b>Latest Event</b></em>' containment reference feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EReference INTERNAL_MODEL__LATEST_EVENT = eINSTANCE.getInternalModel_LatestEvent();

        /**
         * The meta object literal for the '<em><b>Context</b></em>' attribute feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EAttribute INTERNAL_MODEL__CONTEXT = eINSTANCE.getInternalModel_Context();

        /**
         * The meta object literal for the '{@link org.eclipse.viatra.cep.core.metamodels.automaton.impl.AutomatonImpl <em>Automaton</em>}' class.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @see org.eclipse.viatra.cep.core.metamodels.automaton.impl.AutomatonImpl
         * @see org.eclipse.viatra.cep.core.metamodels.automaton.impl.AutomatonPackageImpl#getAutomaton()
         * @generated
         */
        EClass AUTOMATON = eINSTANCE.getAutomaton();

        /**
         * The meta object literal for the '<em><b>States</b></em>' containment reference list feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EReference AUTOMATON__STATES = eINSTANCE.getAutomaton_States();

        /**
         * The meta object literal for the '<em><b>Event Pattern</b></em>' reference feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EReference AUTOMATON__EVENT_PATTERN = eINSTANCE.getAutomaton_EventPattern();

        /**
         * The meta object literal for the '<em><b>Event Tokens</b></em>' containment reference list feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EReference AUTOMATON__EVENT_TOKENS = eINSTANCE.getAutomaton_EventTokens();

        /**
         * The meta object literal for the '<em><b>Timed Zones</b></em>' containment reference list feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EReference AUTOMATON__TIMED_ZONES = eINSTANCE.getAutomaton_TimedZones();

        /**
         * The meta object literal for the '{@link org.eclipse.viatra.cep.core.metamodels.automaton.impl.EventTokenImpl <em>Event Token</em>}' class.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @see org.eclipse.viatra.cep.core.metamodels.automaton.impl.EventTokenImpl
         * @see org.eclipse.viatra.cep.core.metamodels.automaton.impl.AutomatonPackageImpl#getEventToken()
         * @generated
         */
        EClass EVENT_TOKEN = eINSTANCE.getEventToken();

        /**
         * The meta object literal for the '<em><b>Current State</b></em>' reference feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EReference EVENT_TOKEN__CURRENT_STATE = eINSTANCE.getEventToken_CurrentState();

        /**
         * The meta object literal for the '<em><b>Recorded Events</b></em>' reference list feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EReference EVENT_TOKEN__RECORDED_EVENTS = eINSTANCE.getEventToken_RecordedEvents();

        /**
         * The meta object literal for the '<em><b>Last Processed</b></em>' reference feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EReference EVENT_TOKEN__LAST_PROCESSED = eINSTANCE.getEventToken_LastProcessed();

        /**
         * The meta object literal for the '<em><b>Timed Zones</b></em>' reference list feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EReference EVENT_TOKEN__TIMED_ZONES = eINSTANCE.getEventToken_TimedZones();

        /**
         * The meta object literal for the '{@link org.eclipse.viatra.cep.core.metamodels.automaton.impl.StateImpl <em>State</em>}' class.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @see org.eclipse.viatra.cep.core.metamodels.automaton.impl.StateImpl
         * @see org.eclipse.viatra.cep.core.metamodels.automaton.impl.AutomatonPackageImpl#getState()
         * @generated
         */
        EClass STATE = eINSTANCE.getState();

        /**
         * The meta object literal for the '<em><b>In Transitions</b></em>' reference list feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EReference STATE__IN_TRANSITIONS = eINSTANCE.getState_InTransitions();

        /**
         * The meta object literal for the '<em><b>Out Transitions</b></em>' containment reference list feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EReference STATE__OUT_TRANSITIONS = eINSTANCE.getState_OutTransitions();

        /**
         * The meta object literal for the '<em><b>Label</b></em>' attribute feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EAttribute STATE__LABEL = eINSTANCE.getState_Label();

        /**
         * The meta object literal for the '<em><b>Event Tokens</b></em>' reference list feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EReference STATE__EVENT_TOKENS = eINSTANCE.getState_EventTokens();

        /**
         * The meta object literal for the '<em><b>Last Processed Event</b></em>' reference feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EReference STATE__LAST_PROCESSED_EVENT = eINSTANCE.getState_LastProcessedEvent();

        /**
         * The meta object literal for the '<em><b>In State Of</b></em>' reference list feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EReference STATE__IN_STATE_OF = eINSTANCE.getState_InStateOf();

        /**
         * The meta object literal for the '<em><b>Out State Of</b></em>' reference list feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EReference STATE__OUT_STATE_OF = eINSTANCE.getState_OutStateOf();

        /**
         * The meta object literal for the '{@link org.eclipse.viatra.cep.core.metamodels.automaton.impl.InitStateImpl <em>Init State</em>}' class.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @see org.eclipse.viatra.cep.core.metamodels.automaton.impl.InitStateImpl
         * @see org.eclipse.viatra.cep.core.metamodels.automaton.impl.AutomatonPackageImpl#getInitState()
         * @generated
         */
        EClass INIT_STATE = eINSTANCE.getInitState();

        /**
         * The meta object literal for the '{@link org.eclipse.viatra.cep.core.metamodels.automaton.impl.FinalStateImpl <em>Final State</em>}' class.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @see org.eclipse.viatra.cep.core.metamodels.automaton.impl.FinalStateImpl
         * @see org.eclipse.viatra.cep.core.metamodels.automaton.impl.AutomatonPackageImpl#getFinalState()
         * @generated
         */
        EClass FINAL_STATE = eINSTANCE.getFinalState();

        /**
         * The meta object literal for the '{@link org.eclipse.viatra.cep.core.metamodels.automaton.impl.TrapStateImpl <em>Trap State</em>}' class.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @see org.eclipse.viatra.cep.core.metamodels.automaton.impl.TrapStateImpl
         * @see org.eclipse.viatra.cep.core.metamodels.automaton.impl.AutomatonPackageImpl#getTrapState()
         * @generated
         */
        EClass TRAP_STATE = eINSTANCE.getTrapState();

        /**
         * The meta object literal for the '{@link org.eclipse.viatra.cep.core.metamodels.automaton.impl.TransitionImpl <em>Transition</em>}' class.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @see org.eclipse.viatra.cep.core.metamodels.automaton.impl.TransitionImpl
         * @see org.eclipse.viatra.cep.core.metamodels.automaton.impl.AutomatonPackageImpl#getTransition()
         * @generated
         */
        EClass TRANSITION = eINSTANCE.getTransition();

        /**
         * The meta object literal for the '<em><b>Pre State</b></em>' container reference feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EReference TRANSITION__PRE_STATE = eINSTANCE.getTransition_PreState();

        /**
         * The meta object literal for the '<em><b>Post State</b></em>' reference feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EReference TRANSITION__POST_STATE = eINSTANCE.getTransition_PostState();

        /**
         * The meta object literal for the '{@link org.eclipse.viatra.cep.core.metamodels.automaton.impl.TypedTransitionImpl <em>Typed Transition</em>}' class.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @see org.eclipse.viatra.cep.core.metamodels.automaton.impl.TypedTransitionImpl
         * @see org.eclipse.viatra.cep.core.metamodels.automaton.impl.AutomatonPackageImpl#getTypedTransition()
         * @generated
         */
        EClass TYPED_TRANSITION = eINSTANCE.getTypedTransition();

        /**
         * The meta object literal for the '<em><b>Guard</b></em>' containment reference feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EReference TYPED_TRANSITION__GUARD = eINSTANCE.getTypedTransition_Guard();

        /**
         * The meta object literal for the '{@link org.eclipse.viatra.cep.core.metamodels.automaton.impl.EpsilonTransitionImpl <em>Epsilon Transition</em>}' class.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @see org.eclipse.viatra.cep.core.metamodels.automaton.impl.EpsilonTransitionImpl
         * @see org.eclipse.viatra.cep.core.metamodels.automaton.impl.AutomatonPackageImpl#getEpsilonTransition()
         * @generated
         */
        EClass EPSILON_TRANSITION = eINSTANCE.getEpsilonTransition();

        /**
         * The meta object literal for the '{@link org.eclipse.viatra.cep.core.metamodels.automaton.impl.GuardImpl <em>Guard</em>}' class.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @see org.eclipse.viatra.cep.core.metamodels.automaton.impl.GuardImpl
         * @see org.eclipse.viatra.cep.core.metamodels.automaton.impl.AutomatonPackageImpl#getGuard()
         * @generated
         */
        EClass GUARD = eINSTANCE.getGuard();

        /**
         * The meta object literal for the '<em><b>Event Type</b></em>' reference feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EReference GUARD__EVENT_TYPE = eINSTANCE.getGuard_EventType();

        /**
         * The meta object literal for the '<em><b>Transition</b></em>' container reference feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EReference GUARD__TRANSITION = eINSTANCE.getGuard_Transition();

        /**
         * The meta object literal for the '{@link org.eclipse.viatra.cep.core.metamodels.automaton.impl.TimedZoneImpl <em>Timed Zone</em>}' class.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @see org.eclipse.viatra.cep.core.metamodels.automaton.impl.TimedZoneImpl
         * @see org.eclipse.viatra.cep.core.metamodels.automaton.impl.AutomatonPackageImpl#getTimedZone()
         * @generated
         */
        EClass TIMED_ZONE = eINSTANCE.getTimedZone();

        /**
         * The meta object literal for the '<em><b>In State</b></em>' reference feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EReference TIMED_ZONE__IN_STATE = eINSTANCE.getTimedZone_InState();

        /**
         * The meta object literal for the '<em><b>Out State</b></em>' reference feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EReference TIMED_ZONE__OUT_STATE = eINSTANCE.getTimedZone_OutState();

        /**
         * The meta object literal for the '<em><b>Time</b></em>' attribute feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EAttribute TIMED_ZONE__TIME = eINSTANCE.getTimedZone_Time();

        /**
         * The meta object literal for the '{@link org.eclipse.viatra.cep.core.metamodels.automaton.impl.WithinImpl <em>Within</em>}' class.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @see org.eclipse.viatra.cep.core.metamodels.automaton.impl.WithinImpl
         * @see org.eclipse.viatra.cep.core.metamodels.automaton.impl.AutomatonPackageImpl#getWithin()
         * @generated
         */
        EClass WITHIN = eINSTANCE.getWithin();

        /**
         * The meta object literal for the '{@link org.eclipse.viatra.cep.core.metamodels.automaton.impl.HoldsForImpl <em>Holds For</em>}' class.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @see org.eclipse.viatra.cep.core.metamodels.automaton.impl.HoldsForImpl
         * @see org.eclipse.viatra.cep.core.metamodels.automaton.impl.AutomatonPackageImpl#getHoldsFor()
         * @generated
         */
        EClass HOLDS_FOR = eINSTANCE.getHoldsFor();

        /**
         * The meta object literal for the '{@link org.eclipse.viatra.cep.core.metamodels.automaton.EventContext <em>Event Context</em>}' enum.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @see org.eclipse.viatra.cep.core.metamodels.automaton.EventContext
         * @see org.eclipse.viatra.cep.core.metamodels.automaton.impl.AutomatonPackageImpl#getEventContext()
         * @generated
         */
        EEnum EVENT_CONTEXT = eINSTANCE.getEventContext();

    }

} //AutomatonPackage
