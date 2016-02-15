/**
 */
package org.eclipse.viatra.query.testing.snapshot.impl;

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EEnum;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EcorePackage;

import org.eclipse.emf.ecore.impl.EPackageImpl;

import org.eclipse.viatra.query.testing.snapshot.BooleanSubstitution;
import org.eclipse.viatra.query.testing.snapshot.DateSubstitution;
import org.eclipse.viatra.query.testing.snapshot.DoubleSubstitution;
import org.eclipse.viatra.query.testing.snapshot.EMFSubstitution;
import org.eclipse.viatra.query.testing.snapshot.EnumSubstitution;
import org.eclipse.viatra.query.testing.snapshot.FloatSubstitution;
import org.eclipse.viatra.query.testing.snapshot.InputSpecification;
import org.eclipse.viatra.query.testing.snapshot.IntSubstitution;
import org.eclipse.viatra.query.testing.snapshot.LongSubstitution;
import org.eclipse.viatra.query.testing.snapshot.MatchRecord;
import org.eclipse.viatra.query.testing.snapshot.MatchSetRecord;
import org.eclipse.viatra.query.testing.snapshot.MatchSubstitutionRecord;
import org.eclipse.viatra.query.testing.snapshot.MiscellaneousSubstitution;
import org.eclipse.viatra.query.testing.snapshot.QuerySnapshot;
import org.eclipse.viatra.query.testing.snapshot.RecordRole;
import org.eclipse.viatra.query.testing.snapshot.SnapshotFactory;
import org.eclipse.viatra.query.testing.snapshot.SnapshotPackage;
import org.eclipse.viatra.query.testing.snapshot.StringSubstitution;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model <b>Package</b>.
 * <!-- end-user-doc -->
 * @generated
 */
public class SnapshotPackageImpl extends EPackageImpl implements SnapshotPackage {
	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass matchSetRecordEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass matchRecordEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass matchSubstitutionRecordEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass emfSubstitutionEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass intSubstitutionEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass longSubstitutionEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass doubleSubstitutionEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass floatSubstitutionEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass booleanSubstitutionEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass stringSubstitutionEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass dateSubstitutionEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass enumSubstitutionEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass miscellaneousSubstitutionEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass querySnapshotEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EEnum inputSpecificationEEnum = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EEnum recordRoleEEnum = null;

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
	 * @see org.eclipse.viatra.query.testing.snapshot.SnapshotPackage#eNS_URI
	 * @see #init()
	 * @generated
	 */
	private SnapshotPackageImpl() {
		super(eNS_URI, SnapshotFactory.eINSTANCE);
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
	 * <p>This method is used to initialize {@link SnapshotPackage#eINSTANCE} when that field is accessed.
	 * Clients should not invoke it directly. Instead, they should simply access that field to obtain the package.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #eNS_URI
	 * @see #createPackageContents()
	 * @see #initializePackageContents()
	 * @generated
	 */
	public static SnapshotPackage init() {
		if (isInited) return (SnapshotPackage)EPackage.Registry.INSTANCE.getEPackage(SnapshotPackage.eNS_URI);

		// Obtain or create and register package
		SnapshotPackageImpl theSnapshotPackage = (SnapshotPackageImpl)(EPackage.Registry.INSTANCE.get(eNS_URI) instanceof SnapshotPackageImpl ? EPackage.Registry.INSTANCE.get(eNS_URI) : new SnapshotPackageImpl());

		isInited = true;

		// Initialize simple dependencies
		EcorePackage.eINSTANCE.eClass();

		// Create package meta-data objects
		theSnapshotPackage.createPackageContents();

		// Initialize created meta-data
		theSnapshotPackage.initializePackageContents();

		// Mark meta-data to indicate it can't be changed
		theSnapshotPackage.freeze();

  
		// Update the registry and return the package
		EPackage.Registry.INSTANCE.put(SnapshotPackage.eNS_URI, theSnapshotPackage);
		return theSnapshotPackage;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EClass getMatchSetRecord() {
		return matchSetRecordEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getMatchSetRecord_PatternQualifiedName() {
		return (EAttribute)matchSetRecordEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EReference getMatchSetRecord_Matches() {
		return (EReference)matchSetRecordEClass.getEStructuralFeatures().get(1);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EReference getMatchSetRecord_Filter() {
		return (EReference)matchSetRecordEClass.getEStructuralFeatures().get(2);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EClass getMatchRecord() {
		return matchRecordEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EReference getMatchRecord_Substitutions() {
		return (EReference)matchRecordEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EClass getMatchSubstitutionRecord() {
		return matchSubstitutionRecordEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getMatchSubstitutionRecord_ParameterName() {
		return (EAttribute)matchSubstitutionRecordEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EClass getEMFSubstitution() {
		return emfSubstitutionEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EReference getEMFSubstitution_Value() {
		return (EReference)emfSubstitutionEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EClass getIntSubstitution() {
		return intSubstitutionEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getIntSubstitution_Value() {
		return (EAttribute)intSubstitutionEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EClass getLongSubstitution() {
		return longSubstitutionEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getLongSubstitution_Value() {
		return (EAttribute)longSubstitutionEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EClass getDoubleSubstitution() {
		return doubleSubstitutionEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getDoubleSubstitution_Value() {
		return (EAttribute)doubleSubstitutionEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EClass getFloatSubstitution() {
		return floatSubstitutionEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getFloatSubstitution_Value() {
		return (EAttribute)floatSubstitutionEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EClass getBooleanSubstitution() {
		return booleanSubstitutionEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getBooleanSubstitution_Value() {
		return (EAttribute)booleanSubstitutionEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EClass getStringSubstitution() {
		return stringSubstitutionEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getStringSubstitution_Value() {
		return (EAttribute)stringSubstitutionEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EClass getDateSubstitution() {
		return dateSubstitutionEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getDateSubstitution_Value() {
		return (EAttribute)dateSubstitutionEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EClass getEnumSubstitution() {
		return enumSubstitutionEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getEnumSubstitution_ValueLiteral() {
		return (EAttribute)enumSubstitutionEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EReference getEnumSubstitution_EnumType() {
		return (EReference)enumSubstitutionEClass.getEStructuralFeatures().get(1);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EClass getMiscellaneousSubstitution() {
		return miscellaneousSubstitutionEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getMiscellaneousSubstitution_Value() {
		return (EAttribute)miscellaneousSubstitutionEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EClass getQuerySnapshot() {
		return querySnapshotEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EReference getQuerySnapshot_MatchSetRecords() {
		return (EReference)querySnapshotEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EReference getQuerySnapshot_ModelRoots() {
		return (EReference)querySnapshotEClass.getEStructuralFeatures().get(1);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getQuerySnapshot_InputSpecification() {
		return (EAttribute)querySnapshotEClass.getEStructuralFeatures().get(2);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EEnum getInputSpecification() {
		return inputSpecificationEEnum;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EEnum getRecordRole() {
		return recordRoleEEnum;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public SnapshotFactory getSnapshotFactory() {
		return (SnapshotFactory)getEFactoryInstance();
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
		matchSetRecordEClass = createEClass(MATCH_SET_RECORD);
		createEAttribute(matchSetRecordEClass, MATCH_SET_RECORD__PATTERN_QUALIFIED_NAME);
		createEReference(matchSetRecordEClass, MATCH_SET_RECORD__MATCHES);
		createEReference(matchSetRecordEClass, MATCH_SET_RECORD__FILTER);

		matchRecordEClass = createEClass(MATCH_RECORD);
		createEReference(matchRecordEClass, MATCH_RECORD__SUBSTITUTIONS);

		matchSubstitutionRecordEClass = createEClass(MATCH_SUBSTITUTION_RECORD);
		createEAttribute(matchSubstitutionRecordEClass, MATCH_SUBSTITUTION_RECORD__PARAMETER_NAME);

		emfSubstitutionEClass = createEClass(EMF_SUBSTITUTION);
		createEReference(emfSubstitutionEClass, EMF_SUBSTITUTION__VALUE);

		intSubstitutionEClass = createEClass(INT_SUBSTITUTION);
		createEAttribute(intSubstitutionEClass, INT_SUBSTITUTION__VALUE);

		longSubstitutionEClass = createEClass(LONG_SUBSTITUTION);
		createEAttribute(longSubstitutionEClass, LONG_SUBSTITUTION__VALUE);

		doubleSubstitutionEClass = createEClass(DOUBLE_SUBSTITUTION);
		createEAttribute(doubleSubstitutionEClass, DOUBLE_SUBSTITUTION__VALUE);

		floatSubstitutionEClass = createEClass(FLOAT_SUBSTITUTION);
		createEAttribute(floatSubstitutionEClass, FLOAT_SUBSTITUTION__VALUE);

		booleanSubstitutionEClass = createEClass(BOOLEAN_SUBSTITUTION);
		createEAttribute(booleanSubstitutionEClass, BOOLEAN_SUBSTITUTION__VALUE);

		stringSubstitutionEClass = createEClass(STRING_SUBSTITUTION);
		createEAttribute(stringSubstitutionEClass, STRING_SUBSTITUTION__VALUE);

		dateSubstitutionEClass = createEClass(DATE_SUBSTITUTION);
		createEAttribute(dateSubstitutionEClass, DATE_SUBSTITUTION__VALUE);

		enumSubstitutionEClass = createEClass(ENUM_SUBSTITUTION);
		createEAttribute(enumSubstitutionEClass, ENUM_SUBSTITUTION__VALUE_LITERAL);
		createEReference(enumSubstitutionEClass, ENUM_SUBSTITUTION__ENUM_TYPE);

		miscellaneousSubstitutionEClass = createEClass(MISCELLANEOUS_SUBSTITUTION);
		createEAttribute(miscellaneousSubstitutionEClass, MISCELLANEOUS_SUBSTITUTION__VALUE);

		querySnapshotEClass = createEClass(QUERY_SNAPSHOT);
		createEReference(querySnapshotEClass, QUERY_SNAPSHOT__MATCH_SET_RECORDS);
		createEReference(querySnapshotEClass, QUERY_SNAPSHOT__MODEL_ROOTS);
		createEAttribute(querySnapshotEClass, QUERY_SNAPSHOT__INPUT_SPECIFICATION);

		// Create enums
		inputSpecificationEEnum = createEEnum(INPUT_SPECIFICATION);
		recordRoleEEnum = createEEnum(RECORD_ROLE);
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
		EcorePackage theEcorePackage = (EcorePackage)EPackage.Registry.INSTANCE.getEPackage(EcorePackage.eNS_URI);

		// Create type parameters

		// Set bounds for type parameters

		// Add supertypes to classes
		emfSubstitutionEClass.getESuperTypes().add(this.getMatchSubstitutionRecord());
		intSubstitutionEClass.getESuperTypes().add(this.getMatchSubstitutionRecord());
		longSubstitutionEClass.getESuperTypes().add(this.getMatchSubstitutionRecord());
		doubleSubstitutionEClass.getESuperTypes().add(this.getMatchSubstitutionRecord());
		floatSubstitutionEClass.getESuperTypes().add(this.getMatchSubstitutionRecord());
		booleanSubstitutionEClass.getESuperTypes().add(this.getMatchSubstitutionRecord());
		stringSubstitutionEClass.getESuperTypes().add(this.getMatchSubstitutionRecord());
		dateSubstitutionEClass.getESuperTypes().add(this.getMatchSubstitutionRecord());
		enumSubstitutionEClass.getESuperTypes().add(this.getMatchSubstitutionRecord());
		miscellaneousSubstitutionEClass.getESuperTypes().add(this.getMatchSubstitutionRecord());

		// Initialize classes, features, and operations; add parameters
		initEClass(matchSetRecordEClass, MatchSetRecord.class, "MatchSetRecord", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEAttribute(getMatchSetRecord_PatternQualifiedName(), ecorePackage.getEString(), "patternQualifiedName", null, 0, 1, MatchSetRecord.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getMatchSetRecord_Matches(), this.getMatchRecord(), null, "matches", null, 0, -1, MatchSetRecord.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, !IS_ORDERED);
		initEReference(getMatchSetRecord_Filter(), this.getMatchRecord(), null, "filter", null, 0, 1, MatchSetRecord.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

		initEClass(matchRecordEClass, MatchRecord.class, "MatchRecord", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEReference(getMatchRecord_Substitutions(), this.getMatchSubstitutionRecord(), null, "substitutions", null, 0, -1, MatchRecord.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

		initEClass(matchSubstitutionRecordEClass, MatchSubstitutionRecord.class, "MatchSubstitutionRecord", IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEAttribute(getMatchSubstitutionRecord_ParameterName(), ecorePackage.getEString(), "parameterName", null, 0, 1, MatchSubstitutionRecord.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

		initEClass(emfSubstitutionEClass, EMFSubstitution.class, "EMFSubstitution", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEReference(getEMFSubstitution_Value(), ecorePackage.getEObject(), null, "value", null, 0, 1, EMFSubstitution.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

		initEClass(intSubstitutionEClass, IntSubstitution.class, "IntSubstitution", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEAttribute(getIntSubstitution_Value(), ecorePackage.getEInt(), "value", null, 0, 1, IntSubstitution.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

		initEClass(longSubstitutionEClass, LongSubstitution.class, "LongSubstitution", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEAttribute(getLongSubstitution_Value(), ecorePackage.getELong(), "value", null, 0, 1, LongSubstitution.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

		initEClass(doubleSubstitutionEClass, DoubleSubstitution.class, "DoubleSubstitution", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEAttribute(getDoubleSubstitution_Value(), ecorePackage.getEDouble(), "value", null, 0, 1, DoubleSubstitution.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

		initEClass(floatSubstitutionEClass, FloatSubstitution.class, "FloatSubstitution", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEAttribute(getFloatSubstitution_Value(), ecorePackage.getEFloat(), "value", null, 0, 1, FloatSubstitution.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

		initEClass(booleanSubstitutionEClass, BooleanSubstitution.class, "BooleanSubstitution", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEAttribute(getBooleanSubstitution_Value(), ecorePackage.getEBoolean(), "value", null, 0, 1, BooleanSubstitution.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

		initEClass(stringSubstitutionEClass, StringSubstitution.class, "StringSubstitution", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEAttribute(getStringSubstitution_Value(), ecorePackage.getEString(), "value", null, 0, 1, StringSubstitution.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

		initEClass(dateSubstitutionEClass, DateSubstitution.class, "DateSubstitution", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEAttribute(getDateSubstitution_Value(), ecorePackage.getEDate(), "value", null, 0, 1, DateSubstitution.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

		initEClass(enumSubstitutionEClass, EnumSubstitution.class, "EnumSubstitution", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEAttribute(getEnumSubstitution_ValueLiteral(), theEcorePackage.getEString(), "valueLiteral", null, 0, 1, EnumSubstitution.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getEnumSubstitution_EnumType(), theEcorePackage.getEEnum(), null, "enumType", null, 0, 1, EnumSubstitution.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

		initEClass(miscellaneousSubstitutionEClass, MiscellaneousSubstitution.class, "MiscellaneousSubstitution", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEAttribute(getMiscellaneousSubstitution_Value(), ecorePackage.getEJavaObject(), "value", null, 0, 1, MiscellaneousSubstitution.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

		initEClass(querySnapshotEClass, QuerySnapshot.class, "QuerySnapshot", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEReference(getQuerySnapshot_MatchSetRecords(), this.getMatchSetRecord(), null, "matchSetRecords", null, 0, -1, QuerySnapshot.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getQuerySnapshot_ModelRoots(), theEcorePackage.getEObject(), null, "modelRoots", null, 0, -1, QuerySnapshot.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getQuerySnapshot_InputSpecification(), this.getInputSpecification(), "inputSpecification", null, 0, 1, QuerySnapshot.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

		// Initialize enums and add enum literals
		initEEnum(inputSpecificationEEnum, InputSpecification.class, "InputSpecification");
		addEEnumLiteral(inputSpecificationEEnum, InputSpecification.UNSET);
		addEEnumLiteral(inputSpecificationEEnum, InputSpecification.RESOURCE_SET);
		addEEnumLiteral(inputSpecificationEEnum, InputSpecification.RESOURCE);
		addEEnumLiteral(inputSpecificationEEnum, InputSpecification.EOBJECT);

		initEEnum(recordRoleEEnum, RecordRole.class, "RecordRole");
		addEEnumLiteral(recordRoleEEnum, RecordRole.MATCH);
		addEEnumLiteral(recordRoleEEnum, RecordRole.FILTER);

		// Create resource
		createResource(eNS_URI);

		// Create annotations
		// http://www.eclipse.org/emf/2002/Ecore
		createEcoreAnnotations();
	}

	/**
	 * Initializes the annotations for <b>http://www.eclipse.org/emf/2002/Ecore</b>.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected void createEcoreAnnotations() {
		String source = "http://www.eclipse.org/emf/2002/Ecore";	
		addAnnotation
		  (this, 
		   source, 
		   new String[] {
			 "settingDelegates", "org.eclipse.viatra.query.querybasedfeature"
		   });
	}

} //SnapshotPackageImpl
