/**
 */
package org.eclipse.viatra.cep.core.metamodels.events.impl;

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EOperation;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;

import org.eclipse.emf.ecore.impl.EPackageImpl;

import org.eclipse.viatra.cep.core.metamodels.automaton.AutomatonPackage;

import org.eclipse.viatra.cep.core.metamodels.automaton.impl.AutomatonPackageImpl;

import org.eclipse.viatra.cep.core.metamodels.events.AtomicEventPattern;
import org.eclipse.viatra.cep.core.metamodels.events.ComplexEventOperator;
import org.eclipse.viatra.cep.core.metamodels.events.ComplexEventPattern;
import org.eclipse.viatra.cep.core.metamodels.events.Event;
import org.eclipse.viatra.cep.core.metamodels.events.EventPattern;
import org.eclipse.viatra.cep.core.metamodels.events.EventsFactory;
import org.eclipse.viatra.cep.core.metamodels.events.EventsPackage;
import org.eclipse.viatra.cep.core.metamodels.events.IEventSource;
import org.eclipse.viatra.cep.core.metamodels.events.LogicalOperator;
import org.eclipse.viatra.cep.core.metamodels.events.Time;
import org.eclipse.viatra.cep.core.metamodels.events.TimingOperator;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model <b>Package</b>.
 * <!-- end-user-doc -->
 * @generated
 */
public class EventsPackageImpl extends EPackageImpl implements EventsPackage {
    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    private EClass eventPatternEClass = null;

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    private EClass atomicEventPatternEClass = null;

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    private EClass complexEventPatternEClass = null;

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    private EClass eventEClass = null;

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    private EClass iEventSourceEClass = null;

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    private EClass timeEClass = null;

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    private EClass complexEventOperatorEClass = null;

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    private EClass logicalOperatorEClass = null;

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    private EClass orEClass = null;

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    private EClass negEClass = null;

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    private EClass followsEClass = null;

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    private EClass untilEClass = null;

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    private EClass timingOperatorEClass = null;

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    private EClass withinEClass = null;

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    private EClass atleastEClass = null;

    /**
     * Creates an instance of the model <b>Package</b>, registered with
     * {@link org.eclipse.emf.ecore.EPackage.Registry EPackage.Registry} by the package
     * package URI value.
     * <p>Note: the correct way to create the package is via the static
     * factory method {@link #init init()}, which also performs
     * initialization of the package, or returns the registered package,
     * if one already exists.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.eclipse.emf.ecore.EPackage.Registry
     * @see org.eclipse.viatra.cep.core.metamodels.events.EventsPackage#eNS_URI
     * @see #init()
     * @generated
     */
    private EventsPackageImpl() {
        super(eNS_URI, EventsFactory.eINSTANCE);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    private static boolean isInited = false;

    /**
     * Creates, registers, and initializes the <b>Package</b> for this model, and for any others upon which it depends.
     * 
     * <p>This method is used to initialize {@link EventsPackage#eINSTANCE} when that field is accessed.
     * Clients should not invoke it directly. Instead, they should simply access that field to obtain the package.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #eNS_URI
     * @see #createPackageContents()
     * @see #initializePackageContents()
     * @generated
     */
    public static EventsPackage init() {
        if (isInited) return (EventsPackage)EPackage.Registry.INSTANCE.getEPackage(EventsPackage.eNS_URI);

        // Obtain or create and register package
        EventsPackageImpl theEventsPackage = (EventsPackageImpl)(EPackage.Registry.INSTANCE.get(eNS_URI) instanceof EventsPackageImpl ? EPackage.Registry.INSTANCE.get(eNS_URI) : new EventsPackageImpl());

        isInited = true;

        // Obtain or create and register interdependencies
        AutomatonPackageImpl theAutomatonPackage = (AutomatonPackageImpl)(EPackage.Registry.INSTANCE.getEPackage(AutomatonPackage.eNS_URI) instanceof AutomatonPackageImpl ? EPackage.Registry.INSTANCE.getEPackage(AutomatonPackage.eNS_URI) : AutomatonPackage.eINSTANCE);

        // Create package meta-data objects
        theEventsPackage.createPackageContents();
        theAutomatonPackage.createPackageContents();

        // Initialize created meta-data
        theEventsPackage.initializePackageContents();
        theAutomatonPackage.initializePackageContents();

        // Mark meta-data to indicate it can't be changed
        theEventsPackage.freeze();

  
        // Update the registry and return the package
        EPackage.Registry.INSTANCE.put(EventsPackage.eNS_URI, theEventsPackage);
        return theEventsPackage;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EClass getEventPattern() {
        return eventPatternEClass;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EReference getEventPattern_Automaton() {
        return (EReference)eventPatternEClass.getEStructuralFeatures().get(0);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EAttribute getEventPattern_Id() {
        return (EAttribute)eventPatternEClass.getEStructuralFeatures().get(1);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EClass getAtomicEventPattern() {
        return atomicEventPatternEClass;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EAttribute getAtomicEventPattern_Type() {
        return (EAttribute)atomicEventPatternEClass.getEStructuralFeatures().get(0);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EClass getComplexEventPattern() {
        return complexEventPatternEClass;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EReference getComplexEventPattern_CompositionEvents() {
        return (EReference)complexEventPatternEClass.getEStructuralFeatures().get(0);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EReference getComplexEventPattern_Operator() {
        return (EReference)complexEventPatternEClass.getEStructuralFeatures().get(1);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EOperation getComplexEventPattern__AddCompositionEventPattern__EventPattern() {
        return complexEventPatternEClass.getEOperations().get(0);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EOperation getComplexEventPattern__EvaluateParameterBindigs__Event() {
        return complexEventPatternEClass.getEOperations().get(1);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EClass getEvent() {
        return eventEClass;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EAttribute getEvent_Type() {
        return (EAttribute)eventEClass.getEStructuralFeatures().get(0);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EAttribute getEvent_Timestamp() {
        return (EAttribute)eventEClass.getEStructuralFeatures().get(1);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EReference getEvent_Source() {
        return (EReference)eventEClass.getEStructuralFeatures().get(2);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EClass getIEventSource() {
        return iEventSourceEClass;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EOperation getIEventSource__GetId() {
        return iEventSourceEClass.getEOperations().get(0);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EClass getTime() {
        return timeEClass;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EAttribute getTime_Length() {
        return (EAttribute)timeEClass.getEStructuralFeatures().get(0);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EReference getTime_Operator() {
        return (EReference)timeEClass.getEStructuralFeatures().get(1);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EClass getComplexEventOperator() {
        return complexEventOperatorEClass;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EClass getLogicalOperator() {
        return logicalOperatorEClass;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EReference getLogicalOperator_Timing() {
        return (EReference)logicalOperatorEClass.getEStructuralFeatures().get(0);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EReference getLogicalOperator_EventPattern() {
        return (EReference)logicalOperatorEClass.getEStructuralFeatures().get(1);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EClass getOR() {
        return orEClass;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EClass getNEG() {
        return negEClass;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EClass getFOLLOWS() {
        return followsEClass;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EClass getUNTIL() {
        return untilEClass;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EClass getTimingOperator() {
        return timingOperatorEClass;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EReference getTimingOperator_Time() {
        return (EReference)timingOperatorEClass.getEStructuralFeatures().get(0);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EReference getTimingOperator_Operator() {
        return (EReference)timingOperatorEClass.getEStructuralFeatures().get(1);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EClass getWITHIN() {
        return withinEClass;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EClass getATLEAST() {
        return atleastEClass;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EventsFactory getEventsFactory() {
        return (EventsFactory)getEFactoryInstance();
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    private boolean isCreated = false;

    /**
     * Creates the meta-model objects for the package.  This method is
     * guarded to have no affect on any invocation but its first.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public void createPackageContents() {
        if (isCreated) return;
        isCreated = true;

        // Create classes and their features
        eventPatternEClass = createEClass(EVENT_PATTERN);
        createEReference(eventPatternEClass, EVENT_PATTERN__AUTOMATON);
        createEAttribute(eventPatternEClass, EVENT_PATTERN__ID);

        atomicEventPatternEClass = createEClass(ATOMIC_EVENT_PATTERN);
        createEAttribute(atomicEventPatternEClass, ATOMIC_EVENT_PATTERN__TYPE);

        complexEventPatternEClass = createEClass(COMPLEX_EVENT_PATTERN);
        createEReference(complexEventPatternEClass, COMPLEX_EVENT_PATTERN__COMPOSITION_EVENTS);
        createEReference(complexEventPatternEClass, COMPLEX_EVENT_PATTERN__OPERATOR);
        createEOperation(complexEventPatternEClass, COMPLEX_EVENT_PATTERN___ADD_COMPOSITION_EVENT_PATTERN__EVENTPATTERN);
        createEOperation(complexEventPatternEClass, COMPLEX_EVENT_PATTERN___EVALUATE_PARAMETER_BINDIGS__EVENT);

        eventEClass = createEClass(EVENT);
        createEAttribute(eventEClass, EVENT__TYPE);
        createEAttribute(eventEClass, EVENT__TIMESTAMP);
        createEReference(eventEClass, EVENT__SOURCE);

        iEventSourceEClass = createEClass(IEVENT_SOURCE);
        createEOperation(iEventSourceEClass, IEVENT_SOURCE___GET_ID);

        timeEClass = createEClass(TIME);
        createEAttribute(timeEClass, TIME__LENGTH);
        createEReference(timeEClass, TIME__OPERATOR);

        complexEventOperatorEClass = createEClass(COMPLEX_EVENT_OPERATOR);

        logicalOperatorEClass = createEClass(LOGICAL_OPERATOR);
        createEReference(logicalOperatorEClass, LOGICAL_OPERATOR__TIMING);
        createEReference(logicalOperatorEClass, LOGICAL_OPERATOR__EVENT_PATTERN);

        orEClass = createEClass(OR);

        negEClass = createEClass(NEG);

        followsEClass = createEClass(FOLLOWS);

        untilEClass = createEClass(UNTIL);

        timingOperatorEClass = createEClass(TIMING_OPERATOR);
        createEReference(timingOperatorEClass, TIMING_OPERATOR__TIME);
        createEReference(timingOperatorEClass, TIMING_OPERATOR__OPERATOR);

        withinEClass = createEClass(WITHIN);

        atleastEClass = createEClass(ATLEAST);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    private boolean isInitialized = false;

    /**
     * Complete the initialization of the package and its meta-model.  This
     * method is guarded to have no affect on any invocation but its first.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public void initializePackageContents() {
        if (isInitialized) return;
        isInitialized = true;

        // Initialize package
        setName(eNAME);
        setNsPrefix(eNS_PREFIX);
        setNsURI(eNS_URI);

        // Obtain other dependent packages
        AutomatonPackage theAutomatonPackage = (AutomatonPackage)EPackage.Registry.INSTANCE.getEPackage(AutomatonPackage.eNS_URI);

        // Create type parameters

        // Set bounds for type parameters

        // Add supertypes to classes
        atomicEventPatternEClass.getESuperTypes().add(this.getEventPattern());
        complexEventPatternEClass.getESuperTypes().add(this.getEventPattern());
        logicalOperatorEClass.getESuperTypes().add(this.getComplexEventOperator());
        orEClass.getESuperTypes().add(this.getLogicalOperator());
        negEClass.getESuperTypes().add(this.getLogicalOperator());
        followsEClass.getESuperTypes().add(this.getLogicalOperator());
        untilEClass.getESuperTypes().add(this.getLogicalOperator());
        timingOperatorEClass.getESuperTypes().add(this.getComplexEventOperator());
        withinEClass.getESuperTypes().add(this.getTimingOperator());
        atleastEClass.getESuperTypes().add(this.getTimingOperator());

        // Initialize classes, features, and operations; add parameters
        initEClass(eventPatternEClass, EventPattern.class, "EventPattern", IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
        initEReference(getEventPattern_Automaton(), theAutomatonPackage.getAutomaton(), theAutomatonPackage.getAutomaton_EventPattern(), "automaton", null, 0, 1, EventPattern.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
        initEAttribute(getEventPattern_Id(), ecorePackage.getEString(), "id", null, 0, 1, EventPattern.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

        initEClass(atomicEventPatternEClass, AtomicEventPattern.class, "AtomicEventPattern", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
        initEAttribute(getAtomicEventPattern_Type(), ecorePackage.getEString(), "type", null, 0, 1, AtomicEventPattern.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

        initEClass(complexEventPatternEClass, ComplexEventPattern.class, "ComplexEventPattern", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
        initEReference(getComplexEventPattern_CompositionEvents(), this.getEventPattern(), null, "compositionEvents", null, 0, -1, ComplexEventPattern.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
        initEReference(getComplexEventPattern_Operator(), this.getLogicalOperator(), this.getLogicalOperator_EventPattern(), "operator", null, 1, 1, ComplexEventPattern.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

        EOperation op = initEOperation(getComplexEventPattern__AddCompositionEventPattern__EventPattern(), null, "addCompositionEventPattern", 1, 1, IS_UNIQUE, IS_ORDERED);
        addEParameter(op, this.getEventPattern(), "compositionEventPattern", 1, 1, IS_UNIQUE, IS_ORDERED);

        op = initEOperation(getComplexEventPattern__EvaluateParameterBindigs__Event(), ecorePackage.getEBoolean(), "evaluateParameterBindigs", 1, 1, IS_UNIQUE, IS_ORDERED);
        addEParameter(op, this.getEvent(), "event", 1, 1, IS_UNIQUE, IS_ORDERED);

        initEClass(eventEClass, Event.class, "Event", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
        initEAttribute(getEvent_Type(), ecorePackage.getEString(), "type", null, 1, 1, Event.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
        initEAttribute(getEvent_Timestamp(), ecorePackage.getELong(), "timestamp", null, 1, 1, Event.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
        initEReference(getEvent_Source(), this.getIEventSource(), null, "source", null, 0, 1, Event.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

        initEClass(iEventSourceEClass, IEventSource.class, "IEventSource", IS_ABSTRACT, IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);

        initEOperation(getIEventSource__GetId(), ecorePackage.getEString(), "getId", 1, 1, IS_UNIQUE, IS_ORDERED);

        initEClass(timeEClass, Time.class, "Time", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
        initEAttribute(getTime_Length(), ecorePackage.getELong(), "length", null, 1, 1, Time.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
        initEReference(getTime_Operator(), this.getTimingOperator(), this.getTimingOperator_Time(), "operator", null, 1, 1, Time.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

        initEClass(complexEventOperatorEClass, ComplexEventOperator.class, "ComplexEventOperator", IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);

        initEClass(logicalOperatorEClass, LogicalOperator.class, "LogicalOperator", IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
        initEReference(getLogicalOperator_Timing(), this.getTimingOperator(), this.getTimingOperator_Operator(), "timing", null, 0, 1, LogicalOperator.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
        initEReference(getLogicalOperator_EventPattern(), this.getComplexEventPattern(), this.getComplexEventPattern_Operator(), "eventPattern", null, 1, 1, LogicalOperator.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

        initEClass(orEClass, org.eclipse.viatra.cep.core.metamodels.events.OR.class, "OR", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);

        initEClass(negEClass, org.eclipse.viatra.cep.core.metamodels.events.NEG.class, "NEG", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);

        initEClass(followsEClass, org.eclipse.viatra.cep.core.metamodels.events.FOLLOWS.class, "FOLLOWS", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);

        initEClass(untilEClass, org.eclipse.viatra.cep.core.metamodels.events.UNTIL.class, "UNTIL", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);

        initEClass(timingOperatorEClass, TimingOperator.class, "TimingOperator", IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
        initEReference(getTimingOperator_Time(), this.getTime(), this.getTime_Operator(), "time", null, 1, 1, TimingOperator.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
        initEReference(getTimingOperator_Operator(), this.getLogicalOperator(), this.getLogicalOperator_Timing(), "operator", null, 1, 1, TimingOperator.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

        initEClass(withinEClass, org.eclipse.viatra.cep.core.metamodels.events.WITHIN.class, "WITHIN", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);

        initEClass(atleastEClass, org.eclipse.viatra.cep.core.metamodels.events.ATLEAST.class, "ATLEAST", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);

        // Create resource
        createResource(eNS_URI);
    }

} //EventsPackageImpl
