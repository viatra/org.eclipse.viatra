/**
 */
package org.eclipse.viatra.cep.core.metamodels.automaton.util;

import org.eclipse.emf.common.notify.Adapter;
import org.eclipse.emf.common.notify.Notifier;

import org.eclipse.emf.common.notify.impl.AdapterFactoryImpl;

import org.eclipse.emf.ecore.EObject;

import org.eclipse.viatra.cep.core.metamodels.automaton.*;

/**
 * <!-- begin-user-doc -->
 * The <b>Adapter Factory</b> for the model.
 * It provides an adapter <code>createXXX</code> method for each class of the model.
 * <!-- end-user-doc -->
 * @see org.eclipse.viatra.cep.core.metamodels.automaton.AutomatonPackage
 * @generated
 */
public class AutomatonAdapterFactory extends AdapterFactoryImpl {
    /**
     * The cached model package.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    protected static AutomatonPackage modelPackage;

    /**
     * Creates an instance of the adapter factory.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public AutomatonAdapterFactory() {
        if (modelPackage == null) {
            modelPackage = AutomatonPackage.eINSTANCE;
        }
    }

    /**
     * Returns whether this factory is applicable for the type of the object.
     * <!-- begin-user-doc -->
     * This implementation returns <code>true</code> if the object is either the model's package or is an instance object of the model.
     * <!-- end-user-doc -->
     * @return whether this factory is applicable for the type of the object.
     * @generated
     */
    @Override
    public boolean isFactoryForType(Object object) {
        if (object == modelPackage) {
            return true;
        }
        if (object instanceof EObject) {
            return ((EObject)object).eClass().getEPackage() == modelPackage;
        }
        return false;
    }

    /**
     * The switch that delegates to the <code>createXXX</code> methods.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    protected AutomatonSwitch<Adapter> modelSwitch =
        new AutomatonSwitch<Adapter>() {
            @Override
            public Adapter caseInternalModel(InternalModel object) {
                return createInternalModelAdapter();
            }
            @Override
            public Adapter caseAutomaton(Automaton object) {
                return createAutomatonAdapter();
            }
            @Override
            public Adapter caseEventToken(EventToken object) {
                return createEventTokenAdapter();
            }
            @Override
            public Adapter caseState(State object) {
                return createStateAdapter();
            }
            @Override
            public Adapter caseInitState(InitState object) {
                return createInitStateAdapter();
            }
            @Override
            public Adapter caseFinalState(FinalState object) {
                return createFinalStateAdapter();
            }
            @Override
            public Adapter caseTrapState(TrapState object) {
                return createTrapStateAdapter();
            }
            @Override
            public Adapter caseTransition(Transition object) {
                return createTransitionAdapter();
            }
            @Override
            public Adapter caseTypedTransition(TypedTransition object) {
                return createTypedTransitionAdapter();
            }
            @Override
            public Adapter caseEpsilonTransition(EpsilonTransition object) {
                return createEpsilonTransitionAdapter();
            }
            @Override
            public Adapter caseGuard(Guard object) {
                return createGuardAdapter();
            }
            @Override
            public Adapter caseTimedZone(TimedZone object) {
                return createTimedZoneAdapter();
            }
            @Override
            public Adapter caseWithin(Within object) {
                return createWithinAdapter();
            }
            @Override
            public Adapter caseHoldsFor(HoldsFor object) {
                return createHoldsForAdapter();
            }
            @Override
            public Adapter defaultCase(EObject object) {
                return createEObjectAdapter();
            }
        };

    /**
     * Creates an adapter for the <code>target</code>.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param target the object to adapt.
     * @return the adapter for the <code>target</code>.
     * @generated
     */
    @Override
    public Adapter createAdapter(Notifier target) {
        return modelSwitch.doSwitch((EObject)target);
    }


    /**
     * Creates a new adapter for an object of class '{@link org.eclipse.viatra.cep.core.metamodels.automaton.InternalModel <em>Internal Model</em>}'.
     * <!-- begin-user-doc -->
     * This default implementation returns null so that we can easily ignore cases;
     * it's useful to ignore a case when inheritance will catch all the cases anyway.
     * <!-- end-user-doc -->
     * @return the new adapter.
     * @see org.eclipse.viatra.cep.core.metamodels.automaton.InternalModel
     * @generated
     */
    public Adapter createInternalModelAdapter() {
        return null;
    }

    /**
     * Creates a new adapter for an object of class '{@link org.eclipse.viatra.cep.core.metamodels.automaton.Automaton <em>Automaton</em>}'.
     * <!-- begin-user-doc -->
     * This default implementation returns null so that we can easily ignore cases;
     * it's useful to ignore a case when inheritance will catch all the cases anyway.
     * <!-- end-user-doc -->
     * @return the new adapter.
     * @see org.eclipse.viatra.cep.core.metamodels.automaton.Automaton
     * @generated
     */
    public Adapter createAutomatonAdapter() {
        return null;
    }

    /**
     * Creates a new adapter for an object of class '{@link org.eclipse.viatra.cep.core.metamodels.automaton.EventToken <em>Event Token</em>}'.
     * <!-- begin-user-doc -->
     * This default implementation returns null so that we can easily ignore cases;
     * it's useful to ignore a case when inheritance will catch all the cases anyway.
     * <!-- end-user-doc -->
     * @return the new adapter.
     * @see org.eclipse.viatra.cep.core.metamodels.automaton.EventToken
     * @generated
     */
    public Adapter createEventTokenAdapter() {
        return null;
    }

    /**
     * Creates a new adapter for an object of class '{@link org.eclipse.viatra.cep.core.metamodels.automaton.State <em>State</em>}'.
     * <!-- begin-user-doc -->
     * This default implementation returns null so that we can easily ignore cases;
     * it's useful to ignore a case when inheritance will catch all the cases anyway.
     * <!-- end-user-doc -->
     * @return the new adapter.
     * @see org.eclipse.viatra.cep.core.metamodels.automaton.State
     * @generated
     */
    public Adapter createStateAdapter() {
        return null;
    }

    /**
     * Creates a new adapter for an object of class '{@link org.eclipse.viatra.cep.core.metamodels.automaton.InitState <em>Init State</em>}'.
     * <!-- begin-user-doc -->
     * This default implementation returns null so that we can easily ignore cases;
     * it's useful to ignore a case when inheritance will catch all the cases anyway.
     * <!-- end-user-doc -->
     * @return the new adapter.
     * @see org.eclipse.viatra.cep.core.metamodels.automaton.InitState
     * @generated
     */
    public Adapter createInitStateAdapter() {
        return null;
    }

    /**
     * Creates a new adapter for an object of class '{@link org.eclipse.viatra.cep.core.metamodels.automaton.FinalState <em>Final State</em>}'.
     * <!-- begin-user-doc -->
     * This default implementation returns null so that we can easily ignore cases;
     * it's useful to ignore a case when inheritance will catch all the cases anyway.
     * <!-- end-user-doc -->
     * @return the new adapter.
     * @see org.eclipse.viatra.cep.core.metamodels.automaton.FinalState
     * @generated
     */
    public Adapter createFinalStateAdapter() {
        return null;
    }

    /**
     * Creates a new adapter for an object of class '{@link org.eclipse.viatra.cep.core.metamodels.automaton.TrapState <em>Trap State</em>}'.
     * <!-- begin-user-doc -->
     * This default implementation returns null so that we can easily ignore cases;
     * it's useful to ignore a case when inheritance will catch all the cases anyway.
     * <!-- end-user-doc -->
     * @return the new adapter.
     * @see org.eclipse.viatra.cep.core.metamodels.automaton.TrapState
     * @generated
     */
    public Adapter createTrapStateAdapter() {
        return null;
    }

    /**
     * Creates a new adapter for an object of class '{@link org.eclipse.viatra.cep.core.metamodels.automaton.Transition <em>Transition</em>}'.
     * <!-- begin-user-doc -->
     * This default implementation returns null so that we can easily ignore cases;
     * it's useful to ignore a case when inheritance will catch all the cases anyway.
     * <!-- end-user-doc -->
     * @return the new adapter.
     * @see org.eclipse.viatra.cep.core.metamodels.automaton.Transition
     * @generated
     */
    public Adapter createTransitionAdapter() {
        return null;
    }

    /**
     * Creates a new adapter for an object of class '{@link org.eclipse.viatra.cep.core.metamodels.automaton.TypedTransition <em>Typed Transition</em>}'.
     * <!-- begin-user-doc -->
     * This default implementation returns null so that we can easily ignore cases;
     * it's useful to ignore a case when inheritance will catch all the cases anyway.
     * <!-- end-user-doc -->
     * @return the new adapter.
     * @see org.eclipse.viatra.cep.core.metamodels.automaton.TypedTransition
     * @generated
     */
    public Adapter createTypedTransitionAdapter() {
        return null;
    }

    /**
     * Creates a new adapter for an object of class '{@link org.eclipse.viatra.cep.core.metamodels.automaton.EpsilonTransition <em>Epsilon Transition</em>}'.
     * <!-- begin-user-doc -->
     * This default implementation returns null so that we can easily ignore cases;
     * it's useful to ignore a case when inheritance will catch all the cases anyway.
     * <!-- end-user-doc -->
     * @return the new adapter.
     * @see org.eclipse.viatra.cep.core.metamodels.automaton.EpsilonTransition
     * @generated
     */
    public Adapter createEpsilonTransitionAdapter() {
        return null;
    }

    /**
     * Creates a new adapter for an object of class '{@link org.eclipse.viatra.cep.core.metamodels.automaton.Guard <em>Guard</em>}'.
     * <!-- begin-user-doc -->
     * This default implementation returns null so that we can easily ignore cases;
     * it's useful to ignore a case when inheritance will catch all the cases anyway.
     * <!-- end-user-doc -->
     * @return the new adapter.
     * @see org.eclipse.viatra.cep.core.metamodels.automaton.Guard
     * @generated
     */
    public Adapter createGuardAdapter() {
        return null;
    }

    /**
     * Creates a new adapter for an object of class '{@link org.eclipse.viatra.cep.core.metamodels.automaton.TimedZone <em>Timed Zone</em>}'.
     * <!-- begin-user-doc -->
     * This default implementation returns null so that we can easily ignore cases;
     * it's useful to ignore a case when inheritance will catch all the cases anyway.
     * <!-- end-user-doc -->
     * @return the new adapter.
     * @see org.eclipse.viatra.cep.core.metamodels.automaton.TimedZone
     * @generated
     */
    public Adapter createTimedZoneAdapter() {
        return null;
    }

    /**
     * Creates a new adapter for an object of class '{@link org.eclipse.viatra.cep.core.metamodels.automaton.Within <em>Within</em>}'.
     * <!-- begin-user-doc -->
     * This default implementation returns null so that we can easily ignore cases;
     * it's useful to ignore a case when inheritance will catch all the cases anyway.
     * <!-- end-user-doc -->
     * @return the new adapter.
     * @see org.eclipse.viatra.cep.core.metamodels.automaton.Within
     * @generated
     */
    public Adapter createWithinAdapter() {
        return null;
    }

    /**
     * Creates a new adapter for an object of class '{@link org.eclipse.viatra.cep.core.metamodels.automaton.HoldsFor <em>Holds For</em>}'.
     * <!-- begin-user-doc -->
     * This default implementation returns null so that we can easily ignore cases;
     * it's useful to ignore a case when inheritance will catch all the cases anyway.
     * <!-- end-user-doc -->
     * @return the new adapter.
     * @see org.eclipse.viatra.cep.core.metamodels.automaton.HoldsFor
     * @generated
     */
    public Adapter createHoldsForAdapter() {
        return null;
    }

    /**
     * Creates a new adapter for the default case.
     * <!-- begin-user-doc -->
     * This default implementation returns null.
     * <!-- end-user-doc -->
     * @return the new adapter.
     * @generated
     */
    public Adapter createEObjectAdapter() {
        return null;
    }

} //AutomatonAdapterFactory
