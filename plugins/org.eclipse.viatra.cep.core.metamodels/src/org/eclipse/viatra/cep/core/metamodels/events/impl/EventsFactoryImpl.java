/**
 */
package org.eclipse.viatra.cep.core.metamodels.events.impl;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;

import org.eclipse.emf.ecore.impl.EFactoryImpl;

import org.eclipse.emf.ecore.plugin.EcorePlugin;

import org.eclipse.viatra.cep.core.metamodels.events.*;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model <b>Factory</b>.
 * <!-- end-user-doc -->
 * @generated
 */
public class EventsFactoryImpl extends EFactoryImpl implements EventsFactory {
    /**
     * Creates the default factory implementation.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public static EventsFactory init() {
        try {
            EventsFactory theEventsFactory = (EventsFactory)EPackage.Registry.INSTANCE.getEFactory(EventsPackage.eNS_URI);
            if (theEventsFactory != null) {
                return theEventsFactory;
            }
        }
        catch (Exception exception) {
            EcorePlugin.INSTANCE.log(exception);
        }
        return new EventsFactoryImpl();
    }

    /**
     * Creates an instance of the factory.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EventsFactoryImpl() {
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
            case EventsPackage.ATOMIC_EVENT_PATTERN: return createAtomicEventPattern();
            case EventsPackage.COMPLEX_EVENT_PATTERN: return createComplexEventPattern();
            case EventsPackage.EVENT: return createEvent();
            case EventsPackage.OR: return createOR();
            case EventsPackage.NEG: return createNEG();
            case EventsPackage.FOLLOWS: return createFOLLOWS();
            case EventsPackage.UNTIL: return createUNTIL();
            case EventsPackage.AND: return createAND();
            case EventsPackage.TIME_WINDOW: return createTimeWindow();
            default:
                throw new IllegalArgumentException("The class '" + eClass.getName() + "' is not a valid classifier");
        }
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public AtomicEventPattern createAtomicEventPattern() {
        AtomicEventPatternImpl atomicEventPattern = new AtomicEventPatternImpl();
        return atomicEventPattern;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public ComplexEventPattern createComplexEventPattern() {
        ComplexEventPatternImpl complexEventPattern = new ComplexEventPatternImpl();
        return complexEventPattern;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public Event createEvent() {
        EventImpl event = new EventImpl();
        return event;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public OR createOR() {
        ORImpl or = new ORImpl();
        return or;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public NEG createNEG() {
        NEGImpl neg = new NEGImpl();
        return neg;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public FOLLOWS createFOLLOWS() {
        FOLLOWSImpl follows = new FOLLOWSImpl();
        return follows;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public UNTIL createUNTIL() {
        UNTILImpl until = new UNTILImpl();
        return until;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public AND createAND() {
        ANDImpl and = new ANDImpl();
        return and;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public TimeWindow createTimeWindow() {
        TimeWindowImpl timeWindow = new TimeWindowImpl();
        return timeWindow;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EventsPackage getEventsPackage() {
        return (EventsPackage)getEPackage();
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @deprecated
     * @generated
     */
    @Deprecated
    public static EventsPackage getPackage() {
        return EventsPackage.eINSTANCE;
    }

} //EventsFactoryImpl
