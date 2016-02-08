/**
 */
package org.eclipse.viatra.integration.xcore.model;

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;

/**
 * <!-- begin-user-doc -->
 * The <b>Package</b> for the model.
 * It contains accessors for the meta objects to represent
 * <ul>
 *   <li>each class,</li>
 *   <li>each feature of each class,</li>
 *   <li>each enum,</li>
 *   <li>and each data type</li>
 * </ul>
 * <!-- end-user-doc -->
 * @see org.eclipse.viatra.integration.xcore.model.XcoreFactory
 * @model kind="package"
 * @generated
 */
public interface XcorePackage extends EPackage {
	/**
     * The package name.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     */
	String eNAME = "xcore";

	/**
     * The package namespace URI.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     */
	String eNS_URI = "http://www.eclipse.org/incquery/IncQueryXcoreLanguage";

	/**
     * The package namespace name.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     */
	String eNS_PREFIX = "xcore";

	/**
     * The singleton instance of the package.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     */
	XcorePackage eINSTANCE = org.eclipse.viatra.integration.xcore.model.impl.XcorePackageImpl.init();

	/**
     * The meta object id for the '{@link org.eclipse.viatra.integration.xcore.model.impl.XIncQueryDerivedFeatureImpl <em>XInc Query Derived Feature</em>}' class.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @see org.eclipse.viatra.integration.xcore.model.impl.XIncQueryDerivedFeatureImpl
     * @see org.eclipse.viatra.integration.xcore.model.impl.XcorePackageImpl#getXIncQueryDerivedFeature()
     * @generated
     */
	int XINC_QUERY_DERIVED_FEATURE = 0;

	/**
     * The feature id for the '<em><b>Annotations</b></em>' containment reference list.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
	int XINC_QUERY_DERIVED_FEATURE__ANNOTATIONS = org.eclipse.emf.ecore.xcore.XcorePackage.XSTRUCTURAL_FEATURE__ANNOTATIONS;

	/**
     * The feature id for the '<em><b>Name</b></em>' attribute.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
	int XINC_QUERY_DERIVED_FEATURE__NAME = org.eclipse.emf.ecore.xcore.XcorePackage.XSTRUCTURAL_FEATURE__NAME;

	/**
     * The feature id for the '<em><b>Unordered</b></em>' attribute.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
	int XINC_QUERY_DERIVED_FEATURE__UNORDERED = org.eclipse.emf.ecore.xcore.XcorePackage.XSTRUCTURAL_FEATURE__UNORDERED;

	/**
     * The feature id for the '<em><b>Unique</b></em>' attribute.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
	int XINC_QUERY_DERIVED_FEATURE__UNIQUE = org.eclipse.emf.ecore.xcore.XcorePackage.XSTRUCTURAL_FEATURE__UNIQUE;

	/**
     * The feature id for the '<em><b>Type</b></em>' containment reference.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
	int XINC_QUERY_DERIVED_FEATURE__TYPE = org.eclipse.emf.ecore.xcore.XcorePackage.XSTRUCTURAL_FEATURE__TYPE;

	/**
     * The feature id for the '<em><b>Multiplicity</b></em>' attribute.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
	int XINC_QUERY_DERIVED_FEATURE__MULTIPLICITY = org.eclipse.emf.ecore.xcore.XcorePackage.XSTRUCTURAL_FEATURE__MULTIPLICITY;

	/**
     * The feature id for the '<em><b>Containing Class</b></em>' container reference.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
	int XINC_QUERY_DERIVED_FEATURE__CONTAINING_CLASS = org.eclipse.emf.ecore.xcore.XcorePackage.XSTRUCTURAL_FEATURE__CONTAINING_CLASS;

	/**
     * The feature id for the '<em><b>Readonly</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int XINC_QUERY_DERIVED_FEATURE__READONLY = org.eclipse.emf.ecore.xcore.XcorePackage.XSTRUCTURAL_FEATURE__READONLY;

    /**
     * The feature id for the '<em><b>Volatile</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int XINC_QUERY_DERIVED_FEATURE__VOLATILE = org.eclipse.emf.ecore.xcore.XcorePackage.XSTRUCTURAL_FEATURE__VOLATILE;

    /**
     * The feature id for the '<em><b>Transient</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int XINC_QUERY_DERIVED_FEATURE__TRANSIENT = org.eclipse.emf.ecore.xcore.XcorePackage.XSTRUCTURAL_FEATURE__TRANSIENT;

    /**
     * The feature id for the '<em><b>Unsettable</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int XINC_QUERY_DERIVED_FEATURE__UNSETTABLE = org.eclipse.emf.ecore.xcore.XcorePackage.XSTRUCTURAL_FEATURE__UNSETTABLE;

    /**
     * The feature id for the '<em><b>Derived</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int XINC_QUERY_DERIVED_FEATURE__DERIVED = org.eclipse.emf.ecore.xcore.XcorePackage.XSTRUCTURAL_FEATURE__DERIVED;

    /**
     * The feature id for the '<em><b>Get Body</b></em>' containment reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int XINC_QUERY_DERIVED_FEATURE__GET_BODY = org.eclipse.emf.ecore.xcore.XcorePackage.XSTRUCTURAL_FEATURE__GET_BODY;

    /**
     * The feature id for the '<em><b>Set Body</b></em>' containment reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int XINC_QUERY_DERIVED_FEATURE__SET_BODY = org.eclipse.emf.ecore.xcore.XcorePackage.XSTRUCTURAL_FEATURE__SET_BODY;

    /**
     * The feature id for the '<em><b>Is Set Body</b></em>' containment reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int XINC_QUERY_DERIVED_FEATURE__IS_SET_BODY = org.eclipse.emf.ecore.xcore.XcorePackage.XSTRUCTURAL_FEATURE__IS_SET_BODY;

    /**
     * The feature id for the '<em><b>Unset Body</b></em>' containment reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int XINC_QUERY_DERIVED_FEATURE__UNSET_BODY = org.eclipse.emf.ecore.xcore.XcorePackage.XSTRUCTURAL_FEATURE__UNSET_BODY;

    /**
     * The feature id for the '<em><b>Pattern</b></em>' reference.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
	int XINC_QUERY_DERIVED_FEATURE__PATTERN = org.eclipse.emf.ecore.xcore.XcorePackage.XSTRUCTURAL_FEATURE_FEATURE_COUNT + 0;

	/**
     * The feature id for the '<em><b>Reference</b></em>' attribute.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
	int XINC_QUERY_DERIVED_FEATURE__REFERENCE = org.eclipse.emf.ecore.xcore.XcorePackage.XSTRUCTURAL_FEATURE_FEATURE_COUNT + 1;

	/**
     * The number of structural features of the '<em>XInc Query Derived Feature</em>' class.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
	int XINC_QUERY_DERIVED_FEATURE_FEATURE_COUNT = org.eclipse.emf.ecore.xcore.XcorePackage.XSTRUCTURAL_FEATURE_FEATURE_COUNT + 2;

	/**
     * The meta object id for the '{@link org.eclipse.viatra.integration.xcore.model.impl.XIncQueryPackageImpl <em>XInc Query Package</em>}' class.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @see org.eclipse.viatra.integration.xcore.model.impl.XIncQueryPackageImpl
     * @see org.eclipse.viatra.integration.xcore.model.impl.XcorePackageImpl#getXIncQueryPackage()
     * @generated
     */
	int XINC_QUERY_PACKAGE = 1;

	/**
     * The feature id for the '<em><b>Annotations</b></em>' containment reference list.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
	int XINC_QUERY_PACKAGE__ANNOTATIONS = org.eclipse.emf.ecore.xcore.XcorePackage.XPACKAGE__ANNOTATIONS;

	/**
     * The feature id for the '<em><b>Name</b></em>' attribute.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
	int XINC_QUERY_PACKAGE__NAME = org.eclipse.emf.ecore.xcore.XcorePackage.XPACKAGE__NAME;

	/**
     * The feature id for the '<em><b>Import Directives</b></em>' containment reference list.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
	int XINC_QUERY_PACKAGE__IMPORT_DIRECTIVES = org.eclipse.emf.ecore.xcore.XcorePackage.XPACKAGE__IMPORT_DIRECTIVES;

	/**
     * The feature id for the '<em><b>Annotation Directives</b></em>' containment reference list.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
	int XINC_QUERY_PACKAGE__ANNOTATION_DIRECTIVES = org.eclipse.emf.ecore.xcore.XcorePackage.XPACKAGE__ANNOTATION_DIRECTIVES;

	/**
     * The feature id for the '<em><b>Classifiers</b></em>' containment reference list.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
	int XINC_QUERY_PACKAGE__CLASSIFIERS = org.eclipse.emf.ecore.xcore.XcorePackage.XPACKAGE__CLASSIFIERS;

	/**
     * The feature id for the '<em><b>Imported Inc Queries</b></em>' containment reference list.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
	int XINC_QUERY_PACKAGE__IMPORTED_INC_QUERIES = org.eclipse.emf.ecore.xcore.XcorePackage.XPACKAGE_FEATURE_COUNT + 0;

	/**
     * The number of structural features of the '<em>XInc Query Package</em>' class.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
	int XINC_QUERY_PACKAGE_FEATURE_COUNT = org.eclipse.emf.ecore.xcore.XcorePackage.XPACKAGE_FEATURE_COUNT + 1;

	/**
     * The meta object id for the '{@link org.eclipse.viatra.integration.xcore.model.impl.XIncQueryImportImpl <em>XInc Query Import</em>}' class.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @see org.eclipse.viatra.integration.xcore.model.impl.XIncQueryImportImpl
     * @see org.eclipse.viatra.integration.xcore.model.impl.XcorePackageImpl#getXIncQueryImport()
     * @generated
     */
	int XINC_QUERY_IMPORT = 2;

	/**
     * The feature id for the '<em><b>Imported Pattern Model</b></em>' reference.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
	int XINC_QUERY_IMPORT__IMPORTED_PATTERN_MODEL = 0;

	/**
     * The number of structural features of the '<em>XInc Query Import</em>' class.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
	int XINC_QUERY_IMPORT_FEATURE_COUNT = 1;


	/**
     * Returns the meta object for class '{@link org.eclipse.viatra.integration.xcore.model.XIncQueryDerivedFeature <em>XInc Query Derived Feature</em>}'.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @return the meta object for class '<em>XInc Query Derived Feature</em>'.
     * @see org.eclipse.viatra.integration.xcore.model.XIncQueryDerivedFeature
     * @generated
     */
	EClass getXIncQueryDerivedFeature();

	/**
     * Returns the meta object for the reference '{@link org.eclipse.viatra.integration.xcore.model.XIncQueryDerivedFeature#getPattern <em>Pattern</em>}'.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @return the meta object for the reference '<em>Pattern</em>'.
     * @see org.eclipse.viatra.integration.xcore.model.XIncQueryDerivedFeature#getPattern()
     * @see #getXIncQueryDerivedFeature()
     * @generated
     */
	EReference getXIncQueryDerivedFeature_Pattern();

	/**
     * Returns the meta object for the attribute '{@link org.eclipse.viatra.integration.xcore.model.XIncQueryDerivedFeature#isReference <em>Reference</em>}'.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Reference</em>'.
     * @see org.eclipse.viatra.integration.xcore.model.XIncQueryDerivedFeature#isReference()
     * @see #getXIncQueryDerivedFeature()
     * @generated
     */
	EAttribute getXIncQueryDerivedFeature_Reference();

	/**
     * Returns the meta object for class '{@link org.eclipse.viatra.integration.xcore.model.XIncQueryPackage <em>XInc Query Package</em>}'.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @return the meta object for class '<em>XInc Query Package</em>'.
     * @see org.eclipse.viatra.integration.xcore.model.XIncQueryPackage
     * @generated
     */
	EClass getXIncQueryPackage();

	/**
     * Returns the meta object for the containment reference list '{@link org.eclipse.viatra.integration.xcore.model.XIncQueryPackage#getImportedIncQueries <em>Imported Inc Queries</em>}'.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @return the meta object for the containment reference list '<em>Imported Inc Queries</em>'.
     * @see org.eclipse.viatra.integration.xcore.model.XIncQueryPackage#getImportedIncQueries()
     * @see #getXIncQueryPackage()
     * @generated
     */
	EReference getXIncQueryPackage_ImportedIncQueries();

	/**
     * Returns the meta object for class '{@link org.eclipse.viatra.integration.xcore.model.XIncQueryImport <em>XInc Query Import</em>}'.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @return the meta object for class '<em>XInc Query Import</em>'.
     * @see org.eclipse.viatra.integration.xcore.model.XIncQueryImport
     * @generated
     */
	EClass getXIncQueryImport();

	/**
     * Returns the meta object for the reference '{@link org.eclipse.viatra.integration.xcore.model.XIncQueryImport#getImportedPatternModel <em>Imported Pattern Model</em>}'.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @return the meta object for the reference '<em>Imported Pattern Model</em>'.
     * @see org.eclipse.viatra.integration.xcore.model.XIncQueryImport#getImportedPatternModel()
     * @see #getXIncQueryImport()
     * @generated
     */
	EReference getXIncQueryImport_ImportedPatternModel();

	/**
     * Returns the factory that creates the instances of the model.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @return the factory that creates the instances of the model.
     * @generated
     */
	XcoreFactory getXcoreFactory();

	/**
     * <!-- begin-user-doc -->
	 * Defines literals for the meta objects that represent
	 * <ul>
	 *   <li>each class,</li>
	 *   <li>each feature of each class,</li>
	 *   <li>each enum,</li>
	 *   <li>and each data type</li>
	 * </ul>
	 * <!-- end-user-doc -->
     * @generated
     */
	interface Literals {
		/**
         * The meta object literal for the '{@link org.eclipse.viatra.integration.xcore.model.impl.XIncQueryDerivedFeatureImpl <em>XInc Query Derived Feature</em>}' class.
         * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
         * @see org.eclipse.viatra.integration.xcore.model.impl.XIncQueryDerivedFeatureImpl
         * @see org.eclipse.viatra.integration.xcore.model.impl.XcorePackageImpl#getXIncQueryDerivedFeature()
         * @generated
         */
		EClass XINC_QUERY_DERIVED_FEATURE = eINSTANCE.getXIncQueryDerivedFeature();

		/**
         * The meta object literal for the '<em><b>Pattern</b></em>' reference feature.
         * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
         * @generated
         */
		EReference XINC_QUERY_DERIVED_FEATURE__PATTERN = eINSTANCE.getXIncQueryDerivedFeature_Pattern();

		/**
         * The meta object literal for the '<em><b>Reference</b></em>' attribute feature.
         * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
         * @generated
         */
		EAttribute XINC_QUERY_DERIVED_FEATURE__REFERENCE = eINSTANCE.getXIncQueryDerivedFeature_Reference();

		/**
         * The meta object literal for the '{@link org.eclipse.viatra.integration.xcore.model.impl.XIncQueryPackageImpl <em>XInc Query Package</em>}' class.
         * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
         * @see org.eclipse.viatra.integration.xcore.model.impl.XIncQueryPackageImpl
         * @see org.eclipse.viatra.integration.xcore.model.impl.XcorePackageImpl#getXIncQueryPackage()
         * @generated
         */
		EClass XINC_QUERY_PACKAGE = eINSTANCE.getXIncQueryPackage();

		/**
         * The meta object literal for the '<em><b>Imported Inc Queries</b></em>' containment reference list feature.
         * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
         * @generated
         */
		EReference XINC_QUERY_PACKAGE__IMPORTED_INC_QUERIES = eINSTANCE.getXIncQueryPackage_ImportedIncQueries();

		/**
         * The meta object literal for the '{@link org.eclipse.viatra.integration.xcore.model.impl.XIncQueryImportImpl <em>XInc Query Import</em>}' class.
         * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
         * @see org.eclipse.viatra.integration.xcore.model.impl.XIncQueryImportImpl
         * @see org.eclipse.viatra.integration.xcore.model.impl.XcorePackageImpl#getXIncQueryImport()
         * @generated
         */
		EClass XINC_QUERY_IMPORT = eINSTANCE.getXIncQueryImport();

		/**
         * The meta object literal for the '<em><b>Imported Pattern Model</b></em>' reference feature.
         * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
         * @generated
         */
		EReference XINC_QUERY_IMPORT__IMPORTED_PATTERN_MODEL = eINSTANCE.getXIncQueryImport_ImportedPatternModel();

	}

} //XcorePackage
