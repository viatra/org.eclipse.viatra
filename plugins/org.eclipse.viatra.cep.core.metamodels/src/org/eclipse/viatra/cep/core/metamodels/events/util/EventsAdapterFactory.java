/**
 */
package org.eclipse.viatra.cep.core.metamodels.events.util;

import org.eclipse.emf.common.notify.Adapter;
import org.eclipse.emf.common.notify.Notifier;

import org.eclipse.emf.common.notify.impl.AdapterFactoryImpl;

import org.eclipse.emf.ecore.EObject;

import org.eclipse.viatra.cep.core.metamodels.events.*;

/**
 * <!-- begin-user-doc -->
 * The <b>Adapter Factory</b> for the model.
 * It provides an adapter <code>createXXX</code> method for each class of the model.
 * <!-- end-user-doc -->
 * @see org.eclipse.viatra.cep.core.metamodels.events.EventsPackage
 * @generated
 */
public class EventsAdapterFactory extends AdapterFactoryImpl {
    /**
     * The cached model package.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    protected static EventsPackage modelPackage;

    /**
     * Creates an instance of the adapter factory.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EventsAdapterFactory() {
        if (modelPackage == null) {
            modelPackage = EventsPackage.eINSTANCE;
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
    protected EventsSwitch<Adapter> modelSwitch =
        new EventsSwitch<Adapter>() {
            @Override
            public Adapter caseEventPattern(EventPattern object) {
                return createEventPatternAdapter();
            }
            @Override
            public Adapter caseAtomicEventPattern(AtomicEventPattern object) {
                return createAtomicEventPatternAdapter();
            }
            @Override
            public Adapter caseComplexEventPattern(ComplexEventPattern object) {
                return createComplexEventPatternAdapter();
            }
            @Override
            public Adapter caseEventPatternReference(EventPatternReference object) {
                return createEventPatternReferenceAdapter();
            }
            @Override
            public Adapter caseEvent(Event object) {
                return createEventAdapter();
            }
            @Override
            public Adapter caseEventSource(EventSource object) {
                return createEventSourceAdapter();
            }
            @Override
            public Adapter caseComplexEventOperator(ComplexEventOperator object) {
                return createComplexEventOperatorAdapter();
            }
            @Override
            public Adapter caseOR(OR object) {
                return createORAdapter();
            }
            @Override
            public Adapter caseNEG(NEG object) {
                return createNEGAdapter();
            }
            @Override
            public Adapter caseFOLLOWS(FOLLOWS object) {
                return createFOLLOWSAdapter();
            }
            @Override
            public Adapter caseAND(AND object) {
                return createANDAdapter();
            }
            @Override
            public Adapter caseTimewindow(Timewindow object) {
                return createTimewindowAdapter();
            }
            @Override
            public Adapter caseAbstractMultiplicity(AbstractMultiplicity object) {
                return createAbstractMultiplicityAdapter();
            }
            @Override
            public Adapter caseMultiplicity(Multiplicity object) {
                return createMultiplicityAdapter();
            }
            @Override
            public Adapter caseInfinite(Infinite object) {
                return createInfiniteAdapter();
            }
            @Override
            public Adapter caseAtLeastOne(AtLeastOne object) {
                return createAtLeastOneAdapter();
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
     * Creates a new adapter for an object of class '{@link org.eclipse.viatra.cep.core.metamodels.events.EventPattern <em>Event Pattern</em>}'.
     * <!-- begin-user-doc -->
     * This default implementation returns null so that we can easily ignore cases;
     * it's useful to ignore a case when inheritance will catch all the cases anyway.
     * <!-- end-user-doc -->
     * @return the new adapter.
     * @see org.eclipse.viatra.cep.core.metamodels.events.EventPattern
     * @generated
     */
    public Adapter createEventPatternAdapter() {
        return null;
    }

    /**
     * Creates a new adapter for an object of class '{@link org.eclipse.viatra.cep.core.metamodels.events.AtomicEventPattern <em>Atomic Event Pattern</em>}'.
     * <!-- begin-user-doc -->
     * This default implementation returns null so that we can easily ignore cases;
     * it's useful to ignore a case when inheritance will catch all the cases anyway.
     * <!-- end-user-doc -->
     * @return the new adapter.
     * @see org.eclipse.viatra.cep.core.metamodels.events.AtomicEventPattern
     * @generated
     */
    public Adapter createAtomicEventPatternAdapter() {
        return null;
    }

    /**
     * Creates a new adapter for an object of class '{@link org.eclipse.viatra.cep.core.metamodels.events.ComplexEventPattern <em>Complex Event Pattern</em>}'.
     * <!-- begin-user-doc -->
     * This default implementation returns null so that we can easily ignore cases;
     * it's useful to ignore a case when inheritance will catch all the cases anyway.
     * <!-- end-user-doc -->
     * @return the new adapter.
     * @see org.eclipse.viatra.cep.core.metamodels.events.ComplexEventPattern
     * @generated
     */
    public Adapter createComplexEventPatternAdapter() {
        return null;
    }

    /**
     * Creates a new adapter for an object of class '{@link org.eclipse.viatra.cep.core.metamodels.events.EventPatternReference <em>Event Pattern Reference</em>}'.
     * <!-- begin-user-doc -->
     * This default implementation returns null so that we can easily ignore cases;
     * it's useful to ignore a case when inheritance will catch all the cases anyway.
     * <!-- end-user-doc -->
     * @return the new adapter.
     * @see org.eclipse.viatra.cep.core.metamodels.events.EventPatternReference
     * @generated
     */
    public Adapter createEventPatternReferenceAdapter() {
        return null;
    }

    /**
     * Creates a new adapter for an object of class '{@link org.eclipse.viatra.cep.core.metamodels.events.Event <em>Event</em>}'.
     * <!-- begin-user-doc -->
     * This default implementation returns null so that we can easily ignore cases;
     * it's useful to ignore a case when inheritance will catch all the cases anyway.
     * <!-- end-user-doc -->
     * @return the new adapter.
     * @see org.eclipse.viatra.cep.core.metamodels.events.Event
     * @generated
     */
    public Adapter createEventAdapter() {
        return null;
    }

    /**
     * Creates a new adapter for an object of class '{@link org.eclipse.viatra.cep.core.metamodels.events.EventSource <em>Event Source</em>}'.
     * <!-- begin-user-doc -->
     * This default implementation returns null so that we can easily ignore cases;
     * it's useful to ignore a case when inheritance will catch all the cases anyway.
     * <!-- end-user-doc -->
     * @return the new adapter.
     * @see org.eclipse.viatra.cep.core.metamodels.events.EventSource
     * @generated
     */
    public Adapter createEventSourceAdapter() {
        return null;
    }

    /**
     * Creates a new adapter for an object of class '{@link org.eclipse.viatra.cep.core.metamodels.events.ComplexEventOperator <em>Complex Event Operator</em>}'.
     * <!-- begin-user-doc -->
     * This default implementation returns null so that we can easily ignore cases;
     * it's useful to ignore a case when inheritance will catch all the cases anyway.
     * <!-- end-user-doc -->
     * @return the new adapter.
     * @see org.eclipse.viatra.cep.core.metamodels.events.ComplexEventOperator
     * @generated
     */
    public Adapter createComplexEventOperatorAdapter() {
        return null;
    }

    /**
     * Creates a new adapter for an object of class '{@link org.eclipse.viatra.cep.core.metamodels.events.OR <em>OR</em>}'.
     * <!-- begin-user-doc -->
     * This default implementation returns null so that we can easily ignore cases;
     * it's useful to ignore a case when inheritance will catch all the cases anyway.
     * <!-- end-user-doc -->
     * @return the new adapter.
     * @see org.eclipse.viatra.cep.core.metamodels.events.OR
     * @generated
     */
    public Adapter createORAdapter() {
        return null;
    }

    /**
     * Creates a new adapter for an object of class '{@link org.eclipse.viatra.cep.core.metamodels.events.NEG <em>NEG</em>}'.
     * <!-- begin-user-doc -->
     * This default implementation returns null so that we can easily ignore cases;
     * it's useful to ignore a case when inheritance will catch all the cases anyway.
     * <!-- end-user-doc -->
     * @return the new adapter.
     * @see org.eclipse.viatra.cep.core.metamodels.events.NEG
     * @generated
     */
    public Adapter createNEGAdapter() {
        return null;
    }

    /**
     * Creates a new adapter for an object of class '{@link org.eclipse.viatra.cep.core.metamodels.events.FOLLOWS <em>FOLLOWS</em>}'.
     * <!-- begin-user-doc -->
     * This default implementation returns null so that we can easily ignore cases;
     * it's useful to ignore a case when inheritance will catch all the cases anyway.
     * <!-- end-user-doc -->
     * @return the new adapter.
     * @see org.eclipse.viatra.cep.core.metamodels.events.FOLLOWS
     * @generated
     */
    public Adapter createFOLLOWSAdapter() {
        return null;
    }

    /**
     * Creates a new adapter for an object of class '{@link org.eclipse.viatra.cep.core.metamodels.events.AND <em>AND</em>}'.
     * <!-- begin-user-doc -->
     * This default implementation returns null so that we can easily ignore cases;
     * it's useful to ignore a case when inheritance will catch all the cases anyway.
     * <!-- end-user-doc -->
     * @return the new adapter.
     * @see org.eclipse.viatra.cep.core.metamodels.events.AND
     * @generated
     */
    public Adapter createANDAdapter() {
        return null;
    }

    /**
     * Creates a new adapter for an object of class '{@link org.eclipse.viatra.cep.core.metamodels.events.Timewindow <em>Timewindow</em>}'.
     * <!-- begin-user-doc -->
     * This default implementation returns null so that we can easily ignore cases;
     * it's useful to ignore a case when inheritance will catch all the cases anyway.
     * <!-- end-user-doc -->
     * @return the new adapter.
     * @see org.eclipse.viatra.cep.core.metamodels.events.Timewindow
     * @generated
     */
    public Adapter createTimewindowAdapter() {
        return null;
    }

    /**
     * Creates a new adapter for an object of class '{@link org.eclipse.viatra.cep.core.metamodels.events.AbstractMultiplicity <em>Abstract Multiplicity</em>}'.
     * <!-- begin-user-doc -->
     * This default implementation returns null so that we can easily ignore cases;
     * it's useful to ignore a case when inheritance will catch all the cases anyway.
     * <!-- end-user-doc -->
     * @return the new adapter.
     * @see org.eclipse.viatra.cep.core.metamodels.events.AbstractMultiplicity
     * @generated
     */
    public Adapter createAbstractMultiplicityAdapter() {
        return null;
    }

    /**
     * Creates a new adapter for an object of class '{@link org.eclipse.viatra.cep.core.metamodels.events.Multiplicity <em>Multiplicity</em>}'.
     * <!-- begin-user-doc -->
     * This default implementation returns null so that we can easily ignore cases;
     * it's useful to ignore a case when inheritance will catch all the cases anyway.
     * <!-- end-user-doc -->
     * @return the new adapter.
     * @see org.eclipse.viatra.cep.core.metamodels.events.Multiplicity
     * @generated
     */
    public Adapter createMultiplicityAdapter() {
        return null;
    }

    /**
     * Creates a new adapter for an object of class '{@link org.eclipse.viatra.cep.core.metamodels.events.Infinite <em>Infinite</em>}'.
     * <!-- begin-user-doc -->
     * This default implementation returns null so that we can easily ignore cases;
     * it's useful to ignore a case when inheritance will catch all the cases anyway.
     * <!-- end-user-doc -->
     * @return the new adapter.
     * @see org.eclipse.viatra.cep.core.metamodels.events.Infinite
     * @generated
     */
    public Adapter createInfiniteAdapter() {
        return null;
    }

    /**
     * Creates a new adapter for an object of class '{@link org.eclipse.viatra.cep.core.metamodels.events.AtLeastOne <em>At Least One</em>}'.
     * <!-- begin-user-doc -->
     * This default implementation returns null so that we can easily ignore cases;
     * it's useful to ignore a case when inheritance will catch all the cases anyway.
     * <!-- end-user-doc -->
     * @return the new adapter.
     * @see org.eclipse.viatra.cep.core.metamodels.events.AtLeastOne
     * @generated
     */
    public Adapter createAtLeastOneAdapter() {
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

} //EventsAdapterFactory
