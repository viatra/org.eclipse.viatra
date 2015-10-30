/**
 */
package transformationtrace.impl;

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;

import org.eclipse.emf.ecore.impl.EPackageImpl;

import transformationtrace.ActivationTrace;
import transformationtrace.RuleParameterTrace;
import transformationtrace.TransformationTrace;
import transformationtrace.TransformationtraceFactory;
import transformationtrace.TransformationtracePackage;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model <b>Package</b>.
 * <!-- end-user-doc -->
 * @generated
 */
public class TransformationtracePackageImpl extends EPackageImpl implements TransformationtracePackage {
    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    private EClass transformationTraceEClass = null;

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    private EClass activationTraceEClass = null;

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    private EClass ruleParameterTraceEClass = null;

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
     * @see transformationtrace.TransformationtracePackage#eNS_URI
     * @see #init()
     * @generated
     */
    private TransformationtracePackageImpl() {
        super(eNS_URI, TransformationtraceFactory.eINSTANCE);
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
     * <p>This method is used to initialize {@link TransformationtracePackage#eINSTANCE} when that field is accessed.
     * Clients should not invoke it directly. Instead, they should simply access that field to obtain the package.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #eNS_URI
     * @see #createPackageContents()
     * @see #initializePackageContents()
     * @generated
     */
    public static TransformationtracePackage init() {
        if (isInited) return (TransformationtracePackage)EPackage.Registry.INSTANCE.getEPackage(TransformationtracePackage.eNS_URI);

        // Obtain or create and register package
        TransformationtracePackageImpl theTransformationtracePackage = (TransformationtracePackageImpl)(EPackage.Registry.INSTANCE.get(eNS_URI) instanceof TransformationtracePackageImpl ? EPackage.Registry.INSTANCE.get(eNS_URI) : new TransformationtracePackageImpl());

        isInited = true;

        // Create package meta-data objects
        theTransformationtracePackage.createPackageContents();

        // Initialize created meta-data
        theTransformationtracePackage.initializePackageContents();

        // Mark meta-data to indicate it can't be changed
        theTransformationtracePackage.freeze();

  
        // Update the registry and return the package
        EPackage.Registry.INSTANCE.put(TransformationtracePackage.eNS_URI, theTransformationtracePackage);
        return theTransformationtracePackage;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EClass getTransformationTrace() {
        return transformationTraceEClass;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EReference getTransformationTrace_ActivationTraces() {
        return (EReference)transformationTraceEClass.getEStructuralFeatures().get(0);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EClass getActivationTrace() {
        return activationTraceEClass;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EAttribute getActivationTrace_RuleName() {
        return (EAttribute)activationTraceEClass.getEStructuralFeatures().get(0);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EReference getActivationTrace_RuleParameterTraces() {
        return (EReference)activationTraceEClass.getEStructuralFeatures().get(1);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EClass getRuleParameterTrace() {
        return ruleParameterTraceEClass;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EAttribute getRuleParameterTrace_ParameterName() {
        return (EAttribute)ruleParameterTraceEClass.getEStructuralFeatures().get(0);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EAttribute getRuleParameterTrace_ObjectId() {
        return (EAttribute)ruleParameterTraceEClass.getEStructuralFeatures().get(1);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public TransformationtraceFactory getTransformationtraceFactory() {
        return (TransformationtraceFactory)getEFactoryInstance();
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
        transformationTraceEClass = createEClass(TRANSFORMATION_TRACE);
        createEReference(transformationTraceEClass, TRANSFORMATION_TRACE__ACTIVATION_TRACES);

        activationTraceEClass = createEClass(ACTIVATION_TRACE);
        createEAttribute(activationTraceEClass, ACTIVATION_TRACE__RULE_NAME);
        createEReference(activationTraceEClass, ACTIVATION_TRACE__RULE_PARAMETER_TRACES);

        ruleParameterTraceEClass = createEClass(RULE_PARAMETER_TRACE);
        createEAttribute(ruleParameterTraceEClass, RULE_PARAMETER_TRACE__PARAMETER_NAME);
        createEAttribute(ruleParameterTraceEClass, RULE_PARAMETER_TRACE__OBJECT_ID);
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
        initEClass(transformationTraceEClass, TransformationTrace.class, "TransformationTrace", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
        initEReference(getTransformationTrace_ActivationTraces(), this.getActivationTrace(), null, "activationTraces", null, 0, -1, TransformationTrace.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

        initEClass(activationTraceEClass, ActivationTrace.class, "ActivationTrace", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
        initEAttribute(getActivationTrace_RuleName(), ecorePackage.getEString(), "ruleName", null, 1, 1, ActivationTrace.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
        initEReference(getActivationTrace_RuleParameterTraces(), this.getRuleParameterTrace(), null, "ruleParameterTraces", null, 0, -1, ActivationTrace.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

        initEClass(ruleParameterTraceEClass, RuleParameterTrace.class, "RuleParameterTrace", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
        initEAttribute(getRuleParameterTrace_ParameterName(), ecorePackage.getEString(), "parameterName", null, 1, 1, RuleParameterTrace.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
        initEAttribute(getRuleParameterTrace_ObjectId(), ecorePackage.getEString(), "objectId", null, 1, 1, RuleParameterTrace.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

        // Create resource
        createResource(eNS_URI);
    }

} //TransformationtracePackageImpl
