/**
 */
package org.eclipse.incquery.xcore.impl;

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;

import org.eclipse.emf.ecore.impl.EPackageImpl;

import org.eclipse.incquery.patternlanguage.patternLanguage.PatternLanguagePackage;

import org.eclipse.incquery.xcore.XIncQueryDerivedFeature;
import org.eclipse.incquery.xcore.XIncQueryImport;
import org.eclipse.incquery.xcore.XIncQueryPackage;
import org.eclipse.incquery.xcore.XcoreFactory;
import org.eclipse.incquery.xcore.XcorePackage;

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
    private EClass xIncQueryDerivedFeatureEClass = null;

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    private EClass xIncQueryPackageEClass = null;

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    private EClass xIncQueryImportEClass = null;

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
     * @see org.eclipse.incquery.xcore.XcorePackage#eNS_URI
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
        org.eclipse.emf.ecore.xcore.XcorePackage.eINSTANCE.eClass();
        PatternLanguagePackage.eINSTANCE.eClass();

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
    public EClass getXIncQueryDerivedFeature() {
        return xIncQueryDerivedFeatureEClass;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EReference getXIncQueryDerivedFeature_Pattern() {
        return (EReference)xIncQueryDerivedFeatureEClass.getEStructuralFeatures().get(0);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EAttribute getXIncQueryDerivedFeature_Reference() {
        return (EAttribute)xIncQueryDerivedFeatureEClass.getEStructuralFeatures().get(1);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EClass getXIncQueryPackage() {
        return xIncQueryPackageEClass;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EReference getXIncQueryPackage_ImportedIncQueries() {
        return (EReference)xIncQueryPackageEClass.getEStructuralFeatures().get(0);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EClass getXIncQueryImport() {
        return xIncQueryImportEClass;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EReference getXIncQueryImport_ImportedPatternModel() {
        return (EReference)xIncQueryImportEClass.getEStructuralFeatures().get(0);
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
        xIncQueryDerivedFeatureEClass = createEClass(XINC_QUERY_DERIVED_FEATURE);
        createEReference(xIncQueryDerivedFeatureEClass, XINC_QUERY_DERIVED_FEATURE__PATTERN);
        createEAttribute(xIncQueryDerivedFeatureEClass, XINC_QUERY_DERIVED_FEATURE__REFERENCE);

        xIncQueryPackageEClass = createEClass(XINC_QUERY_PACKAGE);
        createEReference(xIncQueryPackageEClass, XINC_QUERY_PACKAGE__IMPORTED_INC_QUERIES);

        xIncQueryImportEClass = createEClass(XINC_QUERY_IMPORT);
        createEReference(xIncQueryImportEClass, XINC_QUERY_IMPORT__IMPORTED_PATTERN_MODEL);
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
        xIncQueryDerivedFeatureEClass.getESuperTypes().add(theXcorePackage_1.getXMember());
        xIncQueryPackageEClass.getESuperTypes().add(theXcorePackage_1.getXPackage());

        // Initialize classes and features; add operations and parameters
        initEClass(xIncQueryDerivedFeatureEClass, XIncQueryDerivedFeature.class, "XIncQueryDerivedFeature", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
        initEReference(getXIncQueryDerivedFeature_Pattern(), thePatternLanguagePackage.getPattern(), null, "pattern", null, 0, 1, XIncQueryDerivedFeature.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
        initEAttribute(getXIncQueryDerivedFeature_Reference(), ecorePackage.getEBoolean(), "reference", "false", 1, 1, XIncQueryDerivedFeature.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

        initEClass(xIncQueryPackageEClass, XIncQueryPackage.class, "XIncQueryPackage", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
        initEReference(getXIncQueryPackage_ImportedIncQueries(), this.getXIncQueryImport(), null, "importedIncQueries", null, 0, -1, XIncQueryPackage.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

        initEClass(xIncQueryImportEClass, XIncQueryImport.class, "XIncQueryImport", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
        initEReference(getXIncQueryImport_ImportedPatternModel(), thePatternLanguagePackage.getPatternModel(), null, "importedPatternModel", null, 1, 1, XIncQueryImport.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

        // Create resource
        createResource(eNS_URI);
    }

} //XcorePackageImpl
