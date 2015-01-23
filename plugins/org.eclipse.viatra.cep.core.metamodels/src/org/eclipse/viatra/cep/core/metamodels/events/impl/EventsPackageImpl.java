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

import org.eclipse.viatra.cep.core.metamodels.events.AbstractMultiplicity;
import org.eclipse.viatra.cep.core.metamodels.events.AtLeastOne;
import org.eclipse.viatra.cep.core.metamodels.events.AtomicEventPattern;
import org.eclipse.viatra.cep.core.metamodels.events.ComplexEventOperator;
import org.eclipse.viatra.cep.core.metamodels.events.ComplexEventPattern;
import org.eclipse.viatra.cep.core.metamodels.events.Event;
import org.eclipse.viatra.cep.core.metamodels.events.EventPattern;
import org.eclipse.viatra.cep.core.metamodels.events.EventPatternReference;
import org.eclipse.viatra.cep.core.metamodels.events.EventSource;
import org.eclipse.viatra.cep.core.metamodels.events.EventsFactory;
import org.eclipse.viatra.cep.core.metamodels.events.EventsPackage;
import org.eclipse.viatra.cep.core.metamodels.events.Infinite;
import org.eclipse.viatra.cep.core.metamodels.events.Multiplicity;
import org.eclipse.viatra.cep.core.metamodels.events.Timewindow;

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
    private EClass eventPatternReferenceEClass = null;

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
    private EClass eventSourceEClass = null;

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
    private EClass andEClass = null;

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    private EClass timewindowEClass = null;

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    private EClass abstractMultiplicityEClass = null;

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    private EClass multiplicityEClass = null;

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    private EClass infiniteEClass = null;

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    private EClass atLeastOneEClass = null;

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
    public EReference getComplexEventPattern_Operator() {
        return (EReference)complexEventPatternEClass.getEStructuralFeatures().get(0);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EReference getComplexEventPattern_Timewindow() {
        return (EReference)complexEventPatternEClass.getEStructuralFeatures().get(1);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EReference getComplexEventPattern_ContainedEventPatterns() {
        return (EReference)complexEventPatternEClass.getEStructuralFeatures().get(2);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EOperation getComplexEventPattern__EvaluateParameterBindings__Event() {
        return complexEventPatternEClass.getEOperations().get(0);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EClass getEventPatternReference() {
        return eventPatternReferenceEClass;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EReference getEventPatternReference_EventPattern() {
        return (EReference)eventPatternReferenceEClass.getEStructuralFeatures().get(0);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EReference getEventPatternReference_Multiplicity() {
        return (EReference)eventPatternReferenceEClass.getEStructuralFeatures().get(1);
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
    public EAttribute getEvent_IsProcessed() {
        return (EAttribute)eventEClass.getEStructuralFeatures().get(3);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EClass getEventSource() {
        return eventSourceEClass;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EOperation getEventSource__GetId() {
        return eventSourceEClass.getEOperations().get(0);
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
    public EClass getAND() {
        return andEClass;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EClass getTimewindow() {
        return timewindowEClass;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EAttribute getTimewindow_Time() {
        return (EAttribute)timewindowEClass.getEStructuralFeatures().get(0);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EClass getAbstractMultiplicity() {
        return abstractMultiplicityEClass;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EClass getMultiplicity() {
        return multiplicityEClass;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EAttribute getMultiplicity_Value() {
        return (EAttribute)multiplicityEClass.getEStructuralFeatures().get(0);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EClass getInfinite() {
        return infiniteEClass;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EClass getAtLeastOne() {
        return atLeastOneEClass;
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
        createEReference(complexEventPatternEClass, COMPLEX_EVENT_PATTERN__OPERATOR);
        createEReference(complexEventPatternEClass, COMPLEX_EVENT_PATTERN__TIMEWINDOW);
        createEReference(complexEventPatternEClass, COMPLEX_EVENT_PATTERN__CONTAINED_EVENT_PATTERNS);
        createEOperation(complexEventPatternEClass, COMPLEX_EVENT_PATTERN___EVALUATE_PARAMETER_BINDINGS__EVENT);

        eventPatternReferenceEClass = createEClass(EVENT_PATTERN_REFERENCE);
        createEReference(eventPatternReferenceEClass, EVENT_PATTERN_REFERENCE__EVENT_PATTERN);
        createEReference(eventPatternReferenceEClass, EVENT_PATTERN_REFERENCE__MULTIPLICITY);

        eventEClass = createEClass(EVENT);
        createEAttribute(eventEClass, EVENT__TYPE);
        createEAttribute(eventEClass, EVENT__TIMESTAMP);
        createEReference(eventEClass, EVENT__SOURCE);
        createEAttribute(eventEClass, EVENT__IS_PROCESSED);

        eventSourceEClass = createEClass(EVENT_SOURCE);
        createEOperation(eventSourceEClass, EVENT_SOURCE___GET_ID);

        complexEventOperatorEClass = createEClass(COMPLEX_EVENT_OPERATOR);

        orEClass = createEClass(OR);

        negEClass = createEClass(NEG);

        followsEClass = createEClass(FOLLOWS);

        andEClass = createEClass(AND);

        timewindowEClass = createEClass(TIMEWINDOW);
        createEAttribute(timewindowEClass, TIMEWINDOW__TIME);

        abstractMultiplicityEClass = createEClass(ABSTRACT_MULTIPLICITY);

        multiplicityEClass = createEClass(MULTIPLICITY);
        createEAttribute(multiplicityEClass, MULTIPLICITY__VALUE);

        infiniteEClass = createEClass(INFINITE);

        atLeastOneEClass = createEClass(AT_LEAST_ONE);
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
        orEClass.getESuperTypes().add(this.getComplexEventOperator());
        negEClass.getESuperTypes().add(this.getComplexEventOperator());
        followsEClass.getESuperTypes().add(this.getComplexEventOperator());
        andEClass.getESuperTypes().add(this.getComplexEventOperator());
        multiplicityEClass.getESuperTypes().add(this.getAbstractMultiplicity());
        infiniteEClass.getESuperTypes().add(this.getAbstractMultiplicity());
        atLeastOneEClass.getESuperTypes().add(this.getAbstractMultiplicity());

        // Initialize classes, features, and operations; add parameters
        initEClass(eventPatternEClass, EventPattern.class, "EventPattern", IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
        initEReference(getEventPattern_Automaton(), theAutomatonPackage.getAutomaton(), theAutomatonPackage.getAutomaton_EventPattern(), "automaton", null, 0, 1, EventPattern.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
        initEAttribute(getEventPattern_Id(), ecorePackage.getEString(), "id", null, 0, 1, EventPattern.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

        initEClass(atomicEventPatternEClass, AtomicEventPattern.class, "AtomicEventPattern", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
        initEAttribute(getAtomicEventPattern_Type(), ecorePackage.getEString(), "type", null, 0, 1, AtomicEventPattern.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

        initEClass(complexEventPatternEClass, ComplexEventPattern.class, "ComplexEventPattern", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
        initEReference(getComplexEventPattern_Operator(), this.getComplexEventOperator(), null, "operator", null, 1, 1, ComplexEventPattern.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
        initEReference(getComplexEventPattern_Timewindow(), this.getTimewindow(), null, "timewindow", null, 0, 1, ComplexEventPattern.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
        initEReference(getComplexEventPattern_ContainedEventPatterns(), this.getEventPatternReference(), null, "containedEventPatterns", null, 0, -1, ComplexEventPattern.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

        EOperation op = initEOperation(getComplexEventPattern__EvaluateParameterBindings__Event(), ecorePackage.getEBoolean(), "evaluateParameterBindings", 1, 1, IS_UNIQUE, IS_ORDERED);
        addEParameter(op, this.getEvent(), "event", 1, 1, IS_UNIQUE, IS_ORDERED);

        initEClass(eventPatternReferenceEClass, EventPatternReference.class, "EventPatternReference", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
        initEReference(getEventPatternReference_EventPattern(), this.getEventPattern(), null, "eventPattern", null, 1, 1, EventPatternReference.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
        initEReference(getEventPatternReference_Multiplicity(), this.getAbstractMultiplicity(), null, "multiplicity", null, 1, 1, EventPatternReference.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

        initEClass(eventEClass, Event.class, "Event", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
        initEAttribute(getEvent_Type(), ecorePackage.getEString(), "type", null, 1, 1, Event.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
        initEAttribute(getEvent_Timestamp(), ecorePackage.getELong(), "timestamp", null, 1, 1, Event.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
        initEReference(getEvent_Source(), this.getEventSource(), null, "source", null, 0, 1, Event.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
        initEAttribute(getEvent_IsProcessed(), ecorePackage.getEBoolean(), "isProcessed", null, 1, 1, Event.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

        initEClass(eventSourceEClass, EventSource.class, "EventSource", IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);

        initEOperation(getEventSource__GetId(), ecorePackage.getEString(), "getId", 1, 1, IS_UNIQUE, IS_ORDERED);

        initEClass(complexEventOperatorEClass, ComplexEventOperator.class, "ComplexEventOperator", IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);

        initEClass(orEClass, org.eclipse.viatra.cep.core.metamodels.events.OR.class, "OR", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);

        initEClass(negEClass, org.eclipse.viatra.cep.core.metamodels.events.NEG.class, "NEG", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);

        initEClass(followsEClass, org.eclipse.viatra.cep.core.metamodels.events.FOLLOWS.class, "FOLLOWS", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);

        initEClass(andEClass, org.eclipse.viatra.cep.core.metamodels.events.AND.class, "AND", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);

        initEClass(timewindowEClass, Timewindow.class, "Timewindow", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
        initEAttribute(getTimewindow_Time(), ecorePackage.getELong(), "time", null, 1, 1, Timewindow.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

        initEClass(abstractMultiplicityEClass, AbstractMultiplicity.class, "AbstractMultiplicity", IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);

        initEClass(multiplicityEClass, Multiplicity.class, "Multiplicity", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
        initEAttribute(getMultiplicity_Value(), ecorePackage.getEInt(), "value", null, 0, 1, Multiplicity.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

        initEClass(infiniteEClass, Infinite.class, "Infinite", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);

        initEClass(atLeastOneEClass, AtLeastOne.class, "AtLeastOne", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);

        // Create resource
        createResource(eNS_URI);
    }

} //EventsPackageImpl
