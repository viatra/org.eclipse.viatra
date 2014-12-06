/**
 */
package org.eclipse.viatra.cep.core.metamodels.automaton.impl;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EDataType;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;

import org.eclipse.emf.ecore.impl.EFactoryImpl;

import org.eclipse.emf.ecore.plugin.EcorePlugin;

import org.eclipse.viatra.cep.core.metamodels.automaton.*;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model <b>Factory</b>.
 * <!-- end-user-doc -->
 * @generated
 */
public class AutomatonFactoryImpl extends EFactoryImpl implements AutomatonFactory {
    /**
     * Creates the default factory implementation.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public static AutomatonFactory init() {
        try {
            AutomatonFactory theAutomatonFactory = (AutomatonFactory)EPackage.Registry.INSTANCE.getEFactory(AutomatonPackage.eNS_URI);
            if (theAutomatonFactory != null) {
                return theAutomatonFactory;
            }
        }
        catch (Exception exception) {
            EcorePlugin.INSTANCE.log(exception);
        }
        return new AutomatonFactoryImpl();
    }

    /**
     * Creates an instance of the factory.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public AutomatonFactoryImpl() {
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
            case AutomatonPackage.INTERNAL_MODEL: return createInternalModel();
            case AutomatonPackage.AUTOMATON: return createAutomaton();
            case AutomatonPackage.EVENT_TOKEN: return createEventToken();
            case AutomatonPackage.STATE: return createState();
            case AutomatonPackage.INIT_STATE: return createInitState();
            case AutomatonPackage.FINAL_STATE: return createFinalState();
            case AutomatonPackage.TRAP_STATE: return createTrapState();
            case AutomatonPackage.TYPED_TRANSITION: return createTypedTransition();
            case AutomatonPackage.EPSILON_TRANSITION: return createEpsilonTransition();
            case AutomatonPackage.GUARD: return createGuard();
            case AutomatonPackage.WITHIN: return createWithin();
            case AutomatonPackage.HOLDS_FOR: return createHoldsFor();
            default:
                throw new IllegalArgumentException("The class '" + eClass.getName() + "' is not a valid classifier");
        }
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public Object createFromString(EDataType eDataType, String initialValue) {
        switch (eDataType.getClassifierID()) {
            case AutomatonPackage.EVENT_CONTEXT:
                return createEventContextFromString(eDataType, initialValue);
            default:
                throw new IllegalArgumentException("The datatype '" + eDataType.getName() + "' is not a valid classifier");
        }
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public String convertToString(EDataType eDataType, Object instanceValue) {
        switch (eDataType.getClassifierID()) {
            case AutomatonPackage.EVENT_CONTEXT:
                return convertEventContextToString(eDataType, instanceValue);
            default:
                throw new IllegalArgumentException("The datatype '" + eDataType.getName() + "' is not a valid classifier");
        }
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public InternalModel createInternalModel() {
        InternalModelImpl internalModel = new InternalModelImpl();
        return internalModel;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public Automaton createAutomaton() {
        AutomatonImpl automaton = new AutomatonImpl();
        return automaton;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EventToken createEventToken() {
        EventTokenImpl eventToken = new EventTokenImpl();
        return eventToken;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public State createState() {
        StateImpl state = new StateImpl();
        return state;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public InitState createInitState() {
        InitStateImpl initState = new InitStateImpl();
        return initState;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public FinalState createFinalState() {
        FinalStateImpl finalState = new FinalStateImpl();
        return finalState;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public TrapState createTrapState() {
        TrapStateImpl trapState = new TrapStateImpl();
        return trapState;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public TypedTransition createTypedTransition() {
        TypedTransitionImpl typedTransition = new TypedTransitionImpl();
        return typedTransition;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EpsilonTransition createEpsilonTransition() {
        EpsilonTransitionImpl epsilonTransition = new EpsilonTransitionImpl();
        return epsilonTransition;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public Guard createGuard() {
        GuardImpl guard = new GuardImpl();
        return guard;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public Within createWithin() {
        WithinImpl within = new WithinImpl();
        return within;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public HoldsFor createHoldsFor() {
        HoldsForImpl holdsFor = new HoldsForImpl();
        return holdsFor;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EventContext createEventContextFromString(EDataType eDataType, String initialValue) {
        EventContext result = EventContext.get(initialValue);
        if (result == null) throw new IllegalArgumentException("The value '" + initialValue + "' is not a valid enumerator of '" + eDataType.getName() + "'");
        return result;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public String convertEventContextToString(EDataType eDataType, Object instanceValue) {
        return instanceValue == null ? null : instanceValue.toString();
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public AutomatonPackage getAutomatonPackage() {
        return (AutomatonPackage)getEPackage();
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @deprecated
     * @generated
     */
    @Deprecated
    public static AutomatonPackage getPackage() {
        return AutomatonPackage.eINSTANCE;
    }

} //AutomatonFactoryImpl
