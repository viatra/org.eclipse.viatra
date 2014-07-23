/**
 */
package org.eclipse.viatra.dse.emf.designspace.impl;

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EDataType;
import org.eclipse.emf.ecore.EEnum;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;

import org.eclipse.emf.ecore.impl.EPackageImpl;

import org.eclipse.viatra.dse.emf.designspace.DesignSpace;
import org.eclipse.viatra.dse.emf.designspace.EMFDesignSpaceFactory;
import org.eclipse.viatra.dse.emf.designspace.EMFDesignSpacePackage;
import org.eclipse.viatra.dse.emf.designspace.EMFInternalTraversalState;
import org.eclipse.viatra.dse.emf.designspace.State;
import org.eclipse.viatra.dse.emf.designspace.Transition;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model <b>Package</b>.
 * <!-- end-user-doc -->
 * @generated
 */
public class EMFDesignSpacePackageImpl extends EPackageImpl implements EMFDesignSpacePackage {
    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    private EClass designSpaceEClass = null;

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    private EClass stateEClass = null;

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    private EClass transitionEClass = null;

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    private EEnum emfInternalTraversalStateEEnum = null;

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    private EDataType pojoEDataType = null;

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
     * @see org.eclipse.viatra.dse.emf.designspace.EMFDesignSpacePackage#eNS_URI
     * @see #init()
     * @generated
     */
    private EMFDesignSpacePackageImpl() {
        super(eNS_URI, EMFDesignSpaceFactory.eINSTANCE);
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
     * <p>This method is used to initialize {@link EMFDesignSpacePackage#eINSTANCE} when that field is accessed.
     * Clients should not invoke it directly. Instead, they should simply access that field to obtain the package.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #eNS_URI
     * @see #createPackageContents()
     * @see #initializePackageContents()
     * @generated
     */
    public static EMFDesignSpacePackage init() {
        if (isInited) return (EMFDesignSpacePackage)EPackage.Registry.INSTANCE.getEPackage(EMFDesignSpacePackage.eNS_URI);

        // Obtain or create and register package
        EMFDesignSpacePackageImpl theEMFDesignSpacePackage = (EMFDesignSpacePackageImpl)(EPackage.Registry.INSTANCE.get(eNS_URI) instanceof EMFDesignSpacePackageImpl ? EPackage.Registry.INSTANCE.get(eNS_URI) : new EMFDesignSpacePackageImpl());

        isInited = true;

        // Create package meta-data objects
        theEMFDesignSpacePackage.createPackageContents();

        // Initialize created meta-data
        theEMFDesignSpacePackage.initializePackageContents();

        // Mark meta-data to indicate it can't be changed
        theEMFDesignSpacePackage.freeze();

  
        // Update the registry and return the package
        EPackage.Registry.INSTANCE.put(EMFDesignSpacePackage.eNS_URI, theEMFDesignSpacePackage);
        return theEMFDesignSpacePackage;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EClass getDesignSpace() {
        return designSpaceEClass;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EReference getDesignSpace_States() {
        return (EReference)designSpaceEClass.getEStructuralFeatures().get(0);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EReference getDesignSpace_Transitions() {
        return (EReference)designSpaceEClass.getEStructuralFeatures().get(1);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EReference getDesignSpace_RootStates() {
        return (EReference)designSpaceEClass.getEStructuralFeatures().get(2);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EClass getState() {
        return stateEClass;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EAttribute getState_Id() {
        return (EAttribute)stateEClass.getEStructuralFeatures().get(0);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EAttribute getState_State() {
        return (EAttribute)stateEClass.getEStructuralFeatures().get(1);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EReference getState_OutTransitions() {
        return (EReference)stateEClass.getEStructuralFeatures().get(2);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EReference getState_InTransitions() {
        return (EReference)stateEClass.getEStructuralFeatures().get(3);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EAttribute getState_ThreadsafeFacade() {
        return (EAttribute)stateEClass.getEStructuralFeatures().get(4);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EClass getTransition() {
        return transitionEClass;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EAttribute getTransition_Id() {
        return (EAttribute)transitionEClass.getEStructuralFeatures().get(0);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EReference getTransition_FiredFrom() {
        return (EReference)transitionEClass.getEStructuralFeatures().get(1);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EReference getTransition_ResultsIn() {
        return (EReference)transitionEClass.getEStructuralFeatures().get(2);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EAttribute getTransition_RuleData() {
        return (EAttribute)transitionEClass.getEStructuralFeatures().get(3);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EAttribute getTransition_ThreadsafeFacade() {
        return (EAttribute)transitionEClass.getEStructuralFeatures().get(4);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EEnum getEMFInternalTraversalState() {
        return emfInternalTraversalStateEEnum;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EDataType getPOJO() {
        return pojoEDataType;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EMFDesignSpaceFactory getEMFDesignSpaceFactory() {
        return (EMFDesignSpaceFactory)getEFactoryInstance();
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
        designSpaceEClass = createEClass(DESIGN_SPACE);
        createEReference(designSpaceEClass, DESIGN_SPACE__STATES);
        createEReference(designSpaceEClass, DESIGN_SPACE__TRANSITIONS);
        createEReference(designSpaceEClass, DESIGN_SPACE__ROOT_STATES);

        stateEClass = createEClass(STATE);
        createEAttribute(stateEClass, STATE__ID);
        createEAttribute(stateEClass, STATE__STATE);
        createEReference(stateEClass, STATE__OUT_TRANSITIONS);
        createEReference(stateEClass, STATE__IN_TRANSITIONS);
        createEAttribute(stateEClass, STATE__THREADSAFE_FACADE);

        transitionEClass = createEClass(TRANSITION);
        createEAttribute(transitionEClass, TRANSITION__ID);
        createEReference(transitionEClass, TRANSITION__FIRED_FROM);
        createEReference(transitionEClass, TRANSITION__RESULTS_IN);
        createEAttribute(transitionEClass, TRANSITION__RULE_DATA);
        createEAttribute(transitionEClass, TRANSITION__THREADSAFE_FACADE);

        // Create enums
        emfInternalTraversalStateEEnum = createEEnum(EMF_INTERNAL_TRAVERSAL_STATE);

        // Create data types
        pojoEDataType = createEDataType(POJO);
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

        // Create type parameters

        // Set bounds for type parameters

        // Add supertypes to classes

        // Initialize classes, features, and operations; add parameters
        initEClass(designSpaceEClass, DesignSpace.class, "DesignSpace", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
        initEReference(getDesignSpace_States(), this.getState(), null, "states", null, 0, -1, DesignSpace.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
        initEReference(getDesignSpace_Transitions(), this.getTransition(), null, "transitions", null, 0, -1, DesignSpace.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
        initEReference(getDesignSpace_RootStates(), this.getState(), null, "rootStates", null, 0, -1, DesignSpace.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

        initEClass(stateEClass, State.class, "State", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
        initEAttribute(getState_Id(), this.getPOJO(), "id", null, 0, 1, State.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
        initEAttribute(getState_State(), this.getEMFInternalTraversalState(), "state", null, 0, 1, State.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
        initEReference(getState_OutTransitions(), this.getTransition(), this.getTransition_FiredFrom(), "outTransitions", null, 0, -1, State.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
        initEReference(getState_InTransitions(), this.getTransition(), this.getTransition_ResultsIn(), "inTransitions", null, 0, -1, State.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
        initEAttribute(getState_ThreadsafeFacade(), this.getPOJO(), "threadsafeFacade", null, 0, 1, State.class, IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

        initEClass(transitionEClass, Transition.class, "Transition", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
        initEAttribute(getTransition_Id(), this.getPOJO(), "id", null, 0, 1, Transition.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
        initEReference(getTransition_FiredFrom(), this.getState(), this.getState_OutTransitions(), "firedFrom", null, 1, 1, Transition.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
        initEReference(getTransition_ResultsIn(), this.getState(), this.getState_InTransitions(), "resultsIn", null, 0, 1, Transition.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
        initEAttribute(getTransition_RuleData(), this.getPOJO(), "ruleData", null, 0, 1, Transition.class, IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
        initEAttribute(getTransition_ThreadsafeFacade(), this.getPOJO(), "threadsafeFacade", null, 0, 1, Transition.class, IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

        // Initialize enums and add enum literals
        initEEnum(emfInternalTraversalStateEEnum, EMFInternalTraversalState.class, "EMFInternalTraversalState");
        addEEnumLiteral(emfInternalTraversalStateEEnum, EMFInternalTraversalState.NOT_YET_PROCESSED);
        addEEnumLiteral(emfInternalTraversalStateEEnum, EMFInternalTraversalState.TRAVERSED);
        addEEnumLiteral(emfInternalTraversalStateEEnum, EMFInternalTraversalState.CUT);
        addEEnumLiteral(emfInternalTraversalStateEEnum, EMFInternalTraversalState.GOAL);

        // Initialize data types
        initEDataType(pojoEDataType, Object.class, "POJO", IS_SERIALIZABLE, !IS_GENERATED_INSTANCE_CLASS);

        // Create resource
        createResource(eNS_URI);
    }

} //EMFDesignSpacePackageImpl
