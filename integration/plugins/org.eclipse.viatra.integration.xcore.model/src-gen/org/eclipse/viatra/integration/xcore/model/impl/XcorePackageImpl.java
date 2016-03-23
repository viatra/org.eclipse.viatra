/**
 */
package org.eclipse.viatra.integration.xcore.model.impl;

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;

import org.eclipse.emf.ecore.impl.EPackageImpl;

import org.eclipse.viatra.integration.xcore.model.XViatraQueryDerivedFeature;
import org.eclipse.viatra.integration.xcore.model.XViatraQueryImport;
import org.eclipse.viatra.integration.xcore.model.XViatraQueryPackage;
import org.eclipse.viatra.integration.xcore.model.XcoreFactory;
import org.eclipse.viatra.integration.xcore.model.XcorePackage;

import org.eclipse.viatra.query.patternlanguage.patternLanguage.PatternLanguagePackage;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model <b>Package</b>.
 * <!-- end-user-doc -->
 * @generated
 */
public class XcorePackageImpl extends EPackageImpl implements XcorePackage {
    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    private EClass xViatraQueryDerivedFeatureEClass = null;

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    private EClass xViatraQueryPackageEClass = null;

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    private EClass xViatraQueryImportEClass = null;

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
     * @see org.eclipse.viatra.integration.xcore.model.XcorePackage#eNS_URI
     * @see #init()
     * @generated
     */
    private XcorePackageImpl() {
        super(eNS_URI, XcoreFactory.eINSTANCE);
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
     * <p>This method is used to initialize {@link XcorePackage#eINSTANCE} when that field is accessed.
     * Clients should not invoke it directly. Instead, they should simply access that field to obtain the package.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #eNS_URI
     * @see #createPackageContents()
     * @see #initializePackageContents()
     * @generated
     */
    public static XcorePackage init() {
        if (isInited) return (XcorePackage)EPackage.Registry.INSTANCE.getEPackage(XcorePackage.eNS_URI);

        // Obtain or create and register package
        XcorePackageImpl theXcorePackage = (XcorePackageImpl)(EPackage.Registry.INSTANCE.get(eNS_URI) instanceof XcorePackageImpl ? EPackage.Registry.INSTANCE.get(eNS_URI) : new XcorePackageImpl());

        isInited = true;

        // Initialize simple dependencies
        PatternLanguagePackage.eINSTANCE.eClass();
        org.eclipse.emf.ecore.xcore.XcorePackage.eINSTANCE.eClass();

        // Create package meta-data objects
        theXcorePackage.createPackageContents();

        // Initialize created meta-data
        theXcorePackage.initializePackageContents();

        // Mark meta-data to indicate it can't be changed
        theXcorePackage.freeze();

  
        // Update the registry and return the package
        EPackage.Registry.INSTANCE.put(XcorePackage.eNS_URI, theXcorePackage);
        return theXcorePackage;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EClass getXViatraQueryDerivedFeature() {
        return xViatraQueryDerivedFeatureEClass;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EReference getXViatraQueryDerivedFeature_Pattern() {
        return (EReference)xViatraQueryDerivedFeatureEClass.getEStructuralFeatures().get(0);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EAttribute getXViatraQueryDerivedFeature_Reference() {
        return (EAttribute)xViatraQueryDerivedFeatureEClass.getEStructuralFeatures().get(1);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EClass getXViatraQueryPackage() {
        return xViatraQueryPackageEClass;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EReference getXViatraQueryPackage_ImportedIncQueries() {
        return (EReference)xViatraQueryPackageEClass.getEStructuralFeatures().get(0);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EClass getXViatraQueryImport() {
        return xViatraQueryImportEClass;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EReference getXViatraQueryImport_ImportedPatternModel() {
        return (EReference)xViatraQueryImportEClass.getEStructuralFeatures().get(0);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public XcoreFactory getXcoreFactory() {
        return (XcoreFactory)getEFactoryInstance();
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
        xViatraQueryDerivedFeatureEClass = createEClass(XVIATRA_QUERY_DERIVED_FEATURE);
        createEReference(xViatraQueryDerivedFeatureEClass, XVIATRA_QUERY_DERIVED_FEATURE__PATTERN);
        createEAttribute(xViatraQueryDerivedFeatureEClass, XVIATRA_QUERY_DERIVED_FEATURE__REFERENCE);

        xViatraQueryPackageEClass = createEClass(XVIATRA_QUERY_PACKAGE);
        createEReference(xViatraQueryPackageEClass, XVIATRA_QUERY_PACKAGE__IMPORTED_INC_QUERIES);

        xViatraQueryImportEClass = createEClass(XVIATRA_QUERY_IMPORT);
        createEReference(xViatraQueryImportEClass, XVIATRA_QUERY_IMPORT__IMPORTED_PATTERN_MODEL);
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
        org.eclipse.emf.ecore.xcore.XcorePackage theXcorePackage_1 = (org.eclipse.emf.ecore.xcore.XcorePackage)EPackage.Registry.INSTANCE.getEPackage(org.eclipse.emf.ecore.xcore.XcorePackage.eNS_URI);
        PatternLanguagePackage thePatternLanguagePackage = (PatternLanguagePackage)EPackage.Registry.INSTANCE.getEPackage(PatternLanguagePackage.eNS_URI);

        // Create type parameters

        // Set bounds for type parameters

        // Add supertypes to classes
        xViatraQueryDerivedFeatureEClass.getESuperTypes().add(theXcorePackage_1.getXStructuralFeature());
        xViatraQueryPackageEClass.getESuperTypes().add(theXcorePackage_1.getXPackage());

        // Initialize classes and features; add operations and parameters
        initEClass(xViatraQueryDerivedFeatureEClass, XViatraQueryDerivedFeature.class, "XViatraQueryDerivedFeature", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
        initEReference(getXViatraQueryDerivedFeature_Pattern(), thePatternLanguagePackage.getPattern(), null, "pattern", null, 0, 1, XViatraQueryDerivedFeature.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
        initEAttribute(getXViatraQueryDerivedFeature_Reference(), ecorePackage.getEBoolean(), "reference", "false", 1, 1, XViatraQueryDerivedFeature.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

        initEClass(xViatraQueryPackageEClass, XViatraQueryPackage.class, "XViatraQueryPackage", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
        initEReference(getXViatraQueryPackage_ImportedIncQueries(), this.getXViatraQueryImport(), null, "importedIncQueries", null, 0, -1, XViatraQueryPackage.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

        initEClass(xViatraQueryImportEClass, XViatraQueryImport.class, "XViatraQueryImport", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
        initEReference(getXViatraQueryImport_ImportedPatternModel(), thePatternLanguagePackage.getPatternModel(), null, "importedPatternModel", null, 1, 1, XViatraQueryImport.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

        // Create resource
        createResource(eNS_URI);
    }

} //XcorePackageImpl
