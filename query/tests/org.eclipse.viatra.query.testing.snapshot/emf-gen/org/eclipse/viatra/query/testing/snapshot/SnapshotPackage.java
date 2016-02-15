/**
 */
package org.eclipse.viatra.query.testing.snapshot;

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EEnum;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;

/**
 * <!-- begin-user-doc -->
 * The <b>Package</b> for the model.
 * It contains accessors for the meta objects to represent
 * <ul>
 *   <li>each class,</li>
 *   <li>each feature of each class,</li>
 *   <li>each operation of each class,</li>
 *   <li>each enum,</li>
 *   <li>and each data type</li>
 * </ul>
 * <!-- end-user-doc -->
 * @see org.eclipse.viatra.query.testing.snapshot.SnapshotFactory
 * @model kind="package"
 *        annotation="http://www.eclipse.org/emf/2002/Ecore settingDelegates='org.eclipse.viatra.query.querybasedfeature'"
 * @generated
 */
public interface SnapshotPackage extends EPackage {
	/**
	 * The package name.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	String eNAME = "snapshot";

	/**
	 * The package namespace URI.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	String eNS_URI = "http://www.eclipse.org/viatra/query/snapshot";

	/**
	 * The package namespace name.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	String eNS_PREFIX = "vqSnapshot";

	/**
	 * The singleton instance of the package.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	SnapshotPackage eINSTANCE = org.eclipse.viatra.query.testing.snapshot.impl.SnapshotPackageImpl.init();

	/**
	 * The meta object id for the '{@link org.eclipse.viatra.query.testing.snapshot.impl.MatchSetRecordImpl <em>Match Set Record</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.viatra.query.testing.snapshot.impl.MatchSetRecordImpl
	 * @see org.eclipse.viatra.query.testing.snapshot.impl.SnapshotPackageImpl#getMatchSetRecord()
	 * @generated
	 */
	int MATCH_SET_RECORD = 0;

	/**
	 * The feature id for the '<em><b>Pattern Qualified Name</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int MATCH_SET_RECORD__PATTERN_QUALIFIED_NAME = 0;

	/**
	 * The feature id for the '<em><b>Matches</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int MATCH_SET_RECORD__MATCHES = 1;

	/**
	 * The feature id for the '<em><b>Filter</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int MATCH_SET_RECORD__FILTER = 2;

	/**
	 * The number of structural features of the '<em>Match Set Record</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int MATCH_SET_RECORD_FEATURE_COUNT = 3;

	/**
	 * The number of operations of the '<em>Match Set Record</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int MATCH_SET_RECORD_OPERATION_COUNT = 0;

	/**
	 * The meta object id for the '{@link org.eclipse.viatra.query.testing.snapshot.impl.MatchRecordImpl <em>Match Record</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.viatra.query.testing.snapshot.impl.MatchRecordImpl
	 * @see org.eclipse.viatra.query.testing.snapshot.impl.SnapshotPackageImpl#getMatchRecord()
	 * @generated
	 */
	int MATCH_RECORD = 1;

	/**
	 * The feature id for the '<em><b>Substitutions</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int MATCH_RECORD__SUBSTITUTIONS = 0;

	/**
	 * The number of structural features of the '<em>Match Record</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int MATCH_RECORD_FEATURE_COUNT = 1;

	/**
	 * The number of operations of the '<em>Match Record</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int MATCH_RECORD_OPERATION_COUNT = 0;

	/**
	 * The meta object id for the '{@link org.eclipse.viatra.query.testing.snapshot.impl.MatchSubstitutionRecordImpl <em>Match Substitution Record</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.viatra.query.testing.snapshot.impl.MatchSubstitutionRecordImpl
	 * @see org.eclipse.viatra.query.testing.snapshot.impl.SnapshotPackageImpl#getMatchSubstitutionRecord()
	 * @generated
	 */
	int MATCH_SUBSTITUTION_RECORD = 2;

	/**
	 * The feature id for the '<em><b>Parameter Name</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int MATCH_SUBSTITUTION_RECORD__PARAMETER_NAME = 0;

	/**
	 * The number of structural features of the '<em>Match Substitution Record</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int MATCH_SUBSTITUTION_RECORD_FEATURE_COUNT = 1;

	/**
	 * The number of operations of the '<em>Match Substitution Record</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int MATCH_SUBSTITUTION_RECORD_OPERATION_COUNT = 0;

	/**
	 * The meta object id for the '{@link org.eclipse.viatra.query.testing.snapshot.impl.EMFSubstitutionImpl <em>EMF Substitution</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.viatra.query.testing.snapshot.impl.EMFSubstitutionImpl
	 * @see org.eclipse.viatra.query.testing.snapshot.impl.SnapshotPackageImpl#getEMFSubstitution()
	 * @generated
	 */
	int EMF_SUBSTITUTION = 3;

	/**
	 * The feature id for the '<em><b>Parameter Name</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int EMF_SUBSTITUTION__PARAMETER_NAME = MATCH_SUBSTITUTION_RECORD__PARAMETER_NAME;

	/**
	 * The feature id for the '<em><b>Value</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int EMF_SUBSTITUTION__VALUE = MATCH_SUBSTITUTION_RECORD_FEATURE_COUNT + 0;

	/**
	 * The number of structural features of the '<em>EMF Substitution</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int EMF_SUBSTITUTION_FEATURE_COUNT = MATCH_SUBSTITUTION_RECORD_FEATURE_COUNT + 1;

	/**
	 * The number of operations of the '<em>EMF Substitution</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int EMF_SUBSTITUTION_OPERATION_COUNT = MATCH_SUBSTITUTION_RECORD_OPERATION_COUNT + 0;

	/**
	 * The meta object id for the '{@link org.eclipse.viatra.query.testing.snapshot.impl.IntSubstitutionImpl <em>Int Substitution</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.viatra.query.testing.snapshot.impl.IntSubstitutionImpl
	 * @see org.eclipse.viatra.query.testing.snapshot.impl.SnapshotPackageImpl#getIntSubstitution()
	 * @generated
	 */
	int INT_SUBSTITUTION = 4;

	/**
	 * The feature id for the '<em><b>Parameter Name</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int INT_SUBSTITUTION__PARAMETER_NAME = MATCH_SUBSTITUTION_RECORD__PARAMETER_NAME;

	/**
	 * The feature id for the '<em><b>Value</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int INT_SUBSTITUTION__VALUE = MATCH_SUBSTITUTION_RECORD_FEATURE_COUNT + 0;

	/**
	 * The number of structural features of the '<em>Int Substitution</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int INT_SUBSTITUTION_FEATURE_COUNT = MATCH_SUBSTITUTION_RECORD_FEATURE_COUNT + 1;

	/**
	 * The number of operations of the '<em>Int Substitution</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int INT_SUBSTITUTION_OPERATION_COUNT = MATCH_SUBSTITUTION_RECORD_OPERATION_COUNT + 0;

	/**
	 * The meta object id for the '{@link org.eclipse.viatra.query.testing.snapshot.impl.LongSubstitutionImpl <em>Long Substitution</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.viatra.query.testing.snapshot.impl.LongSubstitutionImpl
	 * @see org.eclipse.viatra.query.testing.snapshot.impl.SnapshotPackageImpl#getLongSubstitution()
	 * @generated
	 */
	int LONG_SUBSTITUTION = 5;

	/**
	 * The feature id for the '<em><b>Parameter Name</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int LONG_SUBSTITUTION__PARAMETER_NAME = MATCH_SUBSTITUTION_RECORD__PARAMETER_NAME;

	/**
	 * The feature id for the '<em><b>Value</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int LONG_SUBSTITUTION__VALUE = MATCH_SUBSTITUTION_RECORD_FEATURE_COUNT + 0;

	/**
	 * The number of structural features of the '<em>Long Substitution</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int LONG_SUBSTITUTION_FEATURE_COUNT = MATCH_SUBSTITUTION_RECORD_FEATURE_COUNT + 1;

	/**
	 * The number of operations of the '<em>Long Substitution</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int LONG_SUBSTITUTION_OPERATION_COUNT = MATCH_SUBSTITUTION_RECORD_OPERATION_COUNT + 0;

	/**
	 * The meta object id for the '{@link org.eclipse.viatra.query.testing.snapshot.impl.DoubleSubstitutionImpl <em>Double Substitution</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.viatra.query.testing.snapshot.impl.DoubleSubstitutionImpl
	 * @see org.eclipse.viatra.query.testing.snapshot.impl.SnapshotPackageImpl#getDoubleSubstitution()
	 * @generated
	 */
	int DOUBLE_SUBSTITUTION = 6;

	/**
	 * The feature id for the '<em><b>Parameter Name</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int DOUBLE_SUBSTITUTION__PARAMETER_NAME = MATCH_SUBSTITUTION_RECORD__PARAMETER_NAME;

	/**
	 * The feature id for the '<em><b>Value</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int DOUBLE_SUBSTITUTION__VALUE = MATCH_SUBSTITUTION_RECORD_FEATURE_COUNT + 0;

	/**
	 * The number of structural features of the '<em>Double Substitution</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int DOUBLE_SUBSTITUTION_FEATURE_COUNT = MATCH_SUBSTITUTION_RECORD_FEATURE_COUNT + 1;

	/**
	 * The number of operations of the '<em>Double Substitution</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int DOUBLE_SUBSTITUTION_OPERATION_COUNT = MATCH_SUBSTITUTION_RECORD_OPERATION_COUNT + 0;

	/**
	 * The meta object id for the '{@link org.eclipse.viatra.query.testing.snapshot.impl.FloatSubstitutionImpl <em>Float Substitution</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.viatra.query.testing.snapshot.impl.FloatSubstitutionImpl
	 * @see org.eclipse.viatra.query.testing.snapshot.impl.SnapshotPackageImpl#getFloatSubstitution()
	 * @generated
	 */
	int FLOAT_SUBSTITUTION = 7;

	/**
	 * The feature id for the '<em><b>Parameter Name</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int FLOAT_SUBSTITUTION__PARAMETER_NAME = MATCH_SUBSTITUTION_RECORD__PARAMETER_NAME;

	/**
	 * The feature id for the '<em><b>Value</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int FLOAT_SUBSTITUTION__VALUE = MATCH_SUBSTITUTION_RECORD_FEATURE_COUNT + 0;

	/**
	 * The number of structural features of the '<em>Float Substitution</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int FLOAT_SUBSTITUTION_FEATURE_COUNT = MATCH_SUBSTITUTION_RECORD_FEATURE_COUNT + 1;

	/**
	 * The number of operations of the '<em>Float Substitution</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int FLOAT_SUBSTITUTION_OPERATION_COUNT = MATCH_SUBSTITUTION_RECORD_OPERATION_COUNT + 0;

	/**
	 * The meta object id for the '{@link org.eclipse.viatra.query.testing.snapshot.impl.BooleanSubstitutionImpl <em>Boolean Substitution</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.viatra.query.testing.snapshot.impl.BooleanSubstitutionImpl
	 * @see org.eclipse.viatra.query.testing.snapshot.impl.SnapshotPackageImpl#getBooleanSubstitution()
	 * @generated
	 */
	int BOOLEAN_SUBSTITUTION = 8;

	/**
	 * The feature id for the '<em><b>Parameter Name</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int BOOLEAN_SUBSTITUTION__PARAMETER_NAME = MATCH_SUBSTITUTION_RECORD__PARAMETER_NAME;

	/**
	 * The feature id for the '<em><b>Value</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int BOOLEAN_SUBSTITUTION__VALUE = MATCH_SUBSTITUTION_RECORD_FEATURE_COUNT + 0;

	/**
	 * The number of structural features of the '<em>Boolean Substitution</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int BOOLEAN_SUBSTITUTION_FEATURE_COUNT = MATCH_SUBSTITUTION_RECORD_FEATURE_COUNT + 1;

	/**
	 * The number of operations of the '<em>Boolean Substitution</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int BOOLEAN_SUBSTITUTION_OPERATION_COUNT = MATCH_SUBSTITUTION_RECORD_OPERATION_COUNT + 0;

	/**
	 * The meta object id for the '{@link org.eclipse.viatra.query.testing.snapshot.impl.StringSubstitutionImpl <em>String Substitution</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.viatra.query.testing.snapshot.impl.StringSubstitutionImpl
	 * @see org.eclipse.viatra.query.testing.snapshot.impl.SnapshotPackageImpl#getStringSubstitution()
	 * @generated
	 */
	int STRING_SUBSTITUTION = 9;

	/**
	 * The feature id for the '<em><b>Parameter Name</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int STRING_SUBSTITUTION__PARAMETER_NAME = MATCH_SUBSTITUTION_RECORD__PARAMETER_NAME;

	/**
	 * The feature id for the '<em><b>Value</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int STRING_SUBSTITUTION__VALUE = MATCH_SUBSTITUTION_RECORD_FEATURE_COUNT + 0;

	/**
	 * The number of structural features of the '<em>String Substitution</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int STRING_SUBSTITUTION_FEATURE_COUNT = MATCH_SUBSTITUTION_RECORD_FEATURE_COUNT + 1;

	/**
	 * The number of operations of the '<em>String Substitution</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int STRING_SUBSTITUTION_OPERATION_COUNT = MATCH_SUBSTITUTION_RECORD_OPERATION_COUNT + 0;

	/**
	 * The meta object id for the '{@link org.eclipse.viatra.query.testing.snapshot.impl.DateSubstitutionImpl <em>Date Substitution</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.viatra.query.testing.snapshot.impl.DateSubstitutionImpl
	 * @see org.eclipse.viatra.query.testing.snapshot.impl.SnapshotPackageImpl#getDateSubstitution()
	 * @generated
	 */
	int DATE_SUBSTITUTION = 10;

	/**
	 * The feature id for the '<em><b>Parameter Name</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int DATE_SUBSTITUTION__PARAMETER_NAME = MATCH_SUBSTITUTION_RECORD__PARAMETER_NAME;

	/**
	 * The feature id for the '<em><b>Value</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int DATE_SUBSTITUTION__VALUE = MATCH_SUBSTITUTION_RECORD_FEATURE_COUNT + 0;

	/**
	 * The number of structural features of the '<em>Date Substitution</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int DATE_SUBSTITUTION_FEATURE_COUNT = MATCH_SUBSTITUTION_RECORD_FEATURE_COUNT + 1;

	/**
	 * The number of operations of the '<em>Date Substitution</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int DATE_SUBSTITUTION_OPERATION_COUNT = MATCH_SUBSTITUTION_RECORD_OPERATION_COUNT + 0;

	/**
	 * The meta object id for the '{@link org.eclipse.viatra.query.testing.snapshot.impl.EnumSubstitutionImpl <em>Enum Substitution</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.viatra.query.testing.snapshot.impl.EnumSubstitutionImpl
	 * @see org.eclipse.viatra.query.testing.snapshot.impl.SnapshotPackageImpl#getEnumSubstitution()
	 * @generated
	 */
	int ENUM_SUBSTITUTION = 11;

	/**
	 * The feature id for the '<em><b>Parameter Name</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ENUM_SUBSTITUTION__PARAMETER_NAME = MATCH_SUBSTITUTION_RECORD__PARAMETER_NAME;

	/**
	 * The feature id for the '<em><b>Value Literal</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ENUM_SUBSTITUTION__VALUE_LITERAL = MATCH_SUBSTITUTION_RECORD_FEATURE_COUNT + 0;

	/**
	 * The feature id for the '<em><b>Enum Type</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ENUM_SUBSTITUTION__ENUM_TYPE = MATCH_SUBSTITUTION_RECORD_FEATURE_COUNT + 1;

	/**
	 * The number of structural features of the '<em>Enum Substitution</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ENUM_SUBSTITUTION_FEATURE_COUNT = MATCH_SUBSTITUTION_RECORD_FEATURE_COUNT + 2;

	/**
	 * The number of operations of the '<em>Enum Substitution</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ENUM_SUBSTITUTION_OPERATION_COUNT = MATCH_SUBSTITUTION_RECORD_OPERATION_COUNT + 0;

	/**
	 * The meta object id for the '{@link org.eclipse.viatra.query.testing.snapshot.impl.MiscellaneousSubstitutionImpl <em>Miscellaneous Substitution</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.viatra.query.testing.snapshot.impl.MiscellaneousSubstitutionImpl
	 * @see org.eclipse.viatra.query.testing.snapshot.impl.SnapshotPackageImpl#getMiscellaneousSubstitution()
	 * @generated
	 */
	int MISCELLANEOUS_SUBSTITUTION = 12;

	/**
	 * The feature id for the '<em><b>Parameter Name</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int MISCELLANEOUS_SUBSTITUTION__PARAMETER_NAME = MATCH_SUBSTITUTION_RECORD__PARAMETER_NAME;

	/**
	 * The feature id for the '<em><b>Value</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int MISCELLANEOUS_SUBSTITUTION__VALUE = MATCH_SUBSTITUTION_RECORD_FEATURE_COUNT + 0;

	/**
	 * The number of structural features of the '<em>Miscellaneous Substitution</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int MISCELLANEOUS_SUBSTITUTION_FEATURE_COUNT = MATCH_SUBSTITUTION_RECORD_FEATURE_COUNT + 1;

	/**
	 * The number of operations of the '<em>Miscellaneous Substitution</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int MISCELLANEOUS_SUBSTITUTION_OPERATION_COUNT = MATCH_SUBSTITUTION_RECORD_OPERATION_COUNT + 0;

	/**
	 * The meta object id for the '{@link org.eclipse.viatra.query.testing.snapshot.impl.QuerySnapshotImpl <em>Query Snapshot</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.viatra.query.testing.snapshot.impl.QuerySnapshotImpl
	 * @see org.eclipse.viatra.query.testing.snapshot.impl.SnapshotPackageImpl#getQuerySnapshot()
	 * @generated
	 */
	int QUERY_SNAPSHOT = 13;

	/**
	 * The feature id for the '<em><b>Match Set Records</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int QUERY_SNAPSHOT__MATCH_SET_RECORDS = 0;

	/**
	 * The feature id for the '<em><b>Model Roots</b></em>' reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int QUERY_SNAPSHOT__MODEL_ROOTS = 1;

	/**
	 * The feature id for the '<em><b>Input Specification</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int QUERY_SNAPSHOT__INPUT_SPECIFICATION = 2;

	/**
	 * The number of structural features of the '<em>Query Snapshot</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int QUERY_SNAPSHOT_FEATURE_COUNT = 3;

	/**
	 * The number of operations of the '<em>Query Snapshot</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int QUERY_SNAPSHOT_OPERATION_COUNT = 0;

	/**
	 * The meta object id for the '{@link org.eclipse.viatra.query.testing.snapshot.InputSpecification <em>Input Specification</em>}' enum.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.viatra.query.testing.snapshot.InputSpecification
	 * @see org.eclipse.viatra.query.testing.snapshot.impl.SnapshotPackageImpl#getInputSpecification()
	 * @generated
	 */
	int INPUT_SPECIFICATION = 14;

	/**
	 * The meta object id for the '{@link org.eclipse.viatra.query.testing.snapshot.RecordRole <em>Record Role</em>}' enum.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.viatra.query.testing.snapshot.RecordRole
	 * @see org.eclipse.viatra.query.testing.snapshot.impl.SnapshotPackageImpl#getRecordRole()
	 * @generated
	 */
	int RECORD_ROLE = 15;


	/**
	 * Returns the meta object for class '{@link org.eclipse.viatra.query.testing.snapshot.MatchSetRecord <em>Match Set Record</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Match Set Record</em>'.
	 * @see org.eclipse.viatra.query.testing.snapshot.MatchSetRecord
	 * @generated
	 */
	EClass getMatchSetRecord();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.viatra.query.testing.snapshot.MatchSetRecord#getPatternQualifiedName <em>Pattern Qualified Name</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Pattern Qualified Name</em>'.
	 * @see org.eclipse.viatra.query.testing.snapshot.MatchSetRecord#getPatternQualifiedName()
	 * @see #getMatchSetRecord()
	 * @generated
	 */
	EAttribute getMatchSetRecord_PatternQualifiedName();

	/**
	 * Returns the meta object for the containment reference list '{@link org.eclipse.viatra.query.testing.snapshot.MatchSetRecord#getMatches <em>Matches</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference list '<em>Matches</em>'.
	 * @see org.eclipse.viatra.query.testing.snapshot.MatchSetRecord#getMatches()
	 * @see #getMatchSetRecord()
	 * @generated
	 */
	EReference getMatchSetRecord_Matches();

	/**
	 * Returns the meta object for the containment reference '{@link org.eclipse.viatra.query.testing.snapshot.MatchSetRecord#getFilter <em>Filter</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference '<em>Filter</em>'.
	 * @see org.eclipse.viatra.query.testing.snapshot.MatchSetRecord#getFilter()
	 * @see #getMatchSetRecord()
	 * @generated
	 */
	EReference getMatchSetRecord_Filter();

	/**
	 * Returns the meta object for class '{@link org.eclipse.viatra.query.testing.snapshot.MatchRecord <em>Match Record</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Match Record</em>'.
	 * @see org.eclipse.viatra.query.testing.snapshot.MatchRecord
	 * @generated
	 */
	EClass getMatchRecord();

	/**
	 * Returns the meta object for the containment reference list '{@link org.eclipse.viatra.query.testing.snapshot.MatchRecord#getSubstitutions <em>Substitutions</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference list '<em>Substitutions</em>'.
	 * @see org.eclipse.viatra.query.testing.snapshot.MatchRecord#getSubstitutions()
	 * @see #getMatchRecord()
	 * @generated
	 */
	EReference getMatchRecord_Substitutions();

	/**
	 * Returns the meta object for class '{@link org.eclipse.viatra.query.testing.snapshot.MatchSubstitutionRecord <em>Match Substitution Record</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Match Substitution Record</em>'.
	 * @see org.eclipse.viatra.query.testing.snapshot.MatchSubstitutionRecord
	 * @generated
	 */
	EClass getMatchSubstitutionRecord();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.viatra.query.testing.snapshot.MatchSubstitutionRecord#getParameterName <em>Parameter Name</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Parameter Name</em>'.
	 * @see org.eclipse.viatra.query.testing.snapshot.MatchSubstitutionRecord#getParameterName()
	 * @see #getMatchSubstitutionRecord()
	 * @generated
	 */
	EAttribute getMatchSubstitutionRecord_ParameterName();

	/**
	 * Returns the meta object for class '{@link org.eclipse.viatra.query.testing.snapshot.EMFSubstitution <em>EMF Substitution</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>EMF Substitution</em>'.
	 * @see org.eclipse.viatra.query.testing.snapshot.EMFSubstitution
	 * @generated
	 */
	EClass getEMFSubstitution();

	/**
	 * Returns the meta object for the reference '{@link org.eclipse.viatra.query.testing.snapshot.EMFSubstitution#getValue <em>Value</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the reference '<em>Value</em>'.
	 * @see org.eclipse.viatra.query.testing.snapshot.EMFSubstitution#getValue()
	 * @see #getEMFSubstitution()
	 * @generated
	 */
	EReference getEMFSubstitution_Value();

	/**
	 * Returns the meta object for class '{@link org.eclipse.viatra.query.testing.snapshot.IntSubstitution <em>Int Substitution</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Int Substitution</em>'.
	 * @see org.eclipse.viatra.query.testing.snapshot.IntSubstitution
	 * @generated
	 */
	EClass getIntSubstitution();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.viatra.query.testing.snapshot.IntSubstitution#getValue <em>Value</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Value</em>'.
	 * @see org.eclipse.viatra.query.testing.snapshot.IntSubstitution#getValue()
	 * @see #getIntSubstitution()
	 * @generated
	 */
	EAttribute getIntSubstitution_Value();

	/**
	 * Returns the meta object for class '{@link org.eclipse.viatra.query.testing.snapshot.LongSubstitution <em>Long Substitution</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Long Substitution</em>'.
	 * @see org.eclipse.viatra.query.testing.snapshot.LongSubstitution
	 * @generated
	 */
	EClass getLongSubstitution();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.viatra.query.testing.snapshot.LongSubstitution#getValue <em>Value</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Value</em>'.
	 * @see org.eclipse.viatra.query.testing.snapshot.LongSubstitution#getValue()
	 * @see #getLongSubstitution()
	 * @generated
	 */
	EAttribute getLongSubstitution_Value();

	/**
	 * Returns the meta object for class '{@link org.eclipse.viatra.query.testing.snapshot.DoubleSubstitution <em>Double Substitution</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Double Substitution</em>'.
	 * @see org.eclipse.viatra.query.testing.snapshot.DoubleSubstitution
	 * @generated
	 */
	EClass getDoubleSubstitution();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.viatra.query.testing.snapshot.DoubleSubstitution#getValue <em>Value</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Value</em>'.
	 * @see org.eclipse.viatra.query.testing.snapshot.DoubleSubstitution#getValue()
	 * @see #getDoubleSubstitution()
	 * @generated
	 */
	EAttribute getDoubleSubstitution_Value();

	/**
	 * Returns the meta object for class '{@link org.eclipse.viatra.query.testing.snapshot.FloatSubstitution <em>Float Substitution</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Float Substitution</em>'.
	 * @see org.eclipse.viatra.query.testing.snapshot.FloatSubstitution
	 * @generated
	 */
	EClass getFloatSubstitution();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.viatra.query.testing.snapshot.FloatSubstitution#getValue <em>Value</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Value</em>'.
	 * @see org.eclipse.viatra.query.testing.snapshot.FloatSubstitution#getValue()
	 * @see #getFloatSubstitution()
	 * @generated
	 */
	EAttribute getFloatSubstitution_Value();

	/**
	 * Returns the meta object for class '{@link org.eclipse.viatra.query.testing.snapshot.BooleanSubstitution <em>Boolean Substitution</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Boolean Substitution</em>'.
	 * @see org.eclipse.viatra.query.testing.snapshot.BooleanSubstitution
	 * @generated
	 */
	EClass getBooleanSubstitution();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.viatra.query.testing.snapshot.BooleanSubstitution#isValue <em>Value</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Value</em>'.
	 * @see org.eclipse.viatra.query.testing.snapshot.BooleanSubstitution#isValue()
	 * @see #getBooleanSubstitution()
	 * @generated
	 */
	EAttribute getBooleanSubstitution_Value();

	/**
	 * Returns the meta object for class '{@link org.eclipse.viatra.query.testing.snapshot.StringSubstitution <em>String Substitution</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>String Substitution</em>'.
	 * @see org.eclipse.viatra.query.testing.snapshot.StringSubstitution
	 * @generated
	 */
	EClass getStringSubstitution();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.viatra.query.testing.snapshot.StringSubstitution#getValue <em>Value</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Value</em>'.
	 * @see org.eclipse.viatra.query.testing.snapshot.StringSubstitution#getValue()
	 * @see #getStringSubstitution()
	 * @generated
	 */
	EAttribute getStringSubstitution_Value();

	/**
	 * Returns the meta object for class '{@link org.eclipse.viatra.query.testing.snapshot.DateSubstitution <em>Date Substitution</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Date Substitution</em>'.
	 * @see org.eclipse.viatra.query.testing.snapshot.DateSubstitution
	 * @generated
	 */
	EClass getDateSubstitution();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.viatra.query.testing.snapshot.DateSubstitution#getValue <em>Value</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Value</em>'.
	 * @see org.eclipse.viatra.query.testing.snapshot.DateSubstitution#getValue()
	 * @see #getDateSubstitution()
	 * @generated
	 */
	EAttribute getDateSubstitution_Value();

	/**
	 * Returns the meta object for class '{@link org.eclipse.viatra.query.testing.snapshot.EnumSubstitution <em>Enum Substitution</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Enum Substitution</em>'.
	 * @see org.eclipse.viatra.query.testing.snapshot.EnumSubstitution
	 * @generated
	 */
	EClass getEnumSubstitution();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.viatra.query.testing.snapshot.EnumSubstitution#getValueLiteral <em>Value Literal</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Value Literal</em>'.
	 * @see org.eclipse.viatra.query.testing.snapshot.EnumSubstitution#getValueLiteral()
	 * @see #getEnumSubstitution()
	 * @generated
	 */
	EAttribute getEnumSubstitution_ValueLiteral();

	/**
	 * Returns the meta object for the reference '{@link org.eclipse.viatra.query.testing.snapshot.EnumSubstitution#getEnumType <em>Enum Type</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the reference '<em>Enum Type</em>'.
	 * @see org.eclipse.viatra.query.testing.snapshot.EnumSubstitution#getEnumType()
	 * @see #getEnumSubstitution()
	 * @generated
	 */
	EReference getEnumSubstitution_EnumType();

	/**
	 * Returns the meta object for class '{@link org.eclipse.viatra.query.testing.snapshot.MiscellaneousSubstitution <em>Miscellaneous Substitution</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Miscellaneous Substitution</em>'.
	 * @see org.eclipse.viatra.query.testing.snapshot.MiscellaneousSubstitution
	 * @generated
	 */
	EClass getMiscellaneousSubstitution();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.viatra.query.testing.snapshot.MiscellaneousSubstitution#getValue <em>Value</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Value</em>'.
	 * @see org.eclipse.viatra.query.testing.snapshot.MiscellaneousSubstitution#getValue()
	 * @see #getMiscellaneousSubstitution()
	 * @generated
	 */
	EAttribute getMiscellaneousSubstitution_Value();

	/**
	 * Returns the meta object for class '{@link org.eclipse.viatra.query.testing.snapshot.QuerySnapshot <em>Query Snapshot</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Query Snapshot</em>'.
	 * @see org.eclipse.viatra.query.testing.snapshot.QuerySnapshot
	 * @generated
	 */
	EClass getQuerySnapshot();

	/**
	 * Returns the meta object for the containment reference list '{@link org.eclipse.viatra.query.testing.snapshot.QuerySnapshot#getMatchSetRecords <em>Match Set Records</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference list '<em>Match Set Records</em>'.
	 * @see org.eclipse.viatra.query.testing.snapshot.QuerySnapshot#getMatchSetRecords()
	 * @see #getQuerySnapshot()
	 * @generated
	 */
	EReference getQuerySnapshot_MatchSetRecords();

	/**
	 * Returns the meta object for the reference list '{@link org.eclipse.viatra.query.testing.snapshot.QuerySnapshot#getModelRoots <em>Model Roots</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the reference list '<em>Model Roots</em>'.
	 * @see org.eclipse.viatra.query.testing.snapshot.QuerySnapshot#getModelRoots()
	 * @see #getQuerySnapshot()
	 * @generated
	 */
	EReference getQuerySnapshot_ModelRoots();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.viatra.query.testing.snapshot.QuerySnapshot#getInputSpecification <em>Input Specification</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Input Specification</em>'.
	 * @see org.eclipse.viatra.query.testing.snapshot.QuerySnapshot#getInputSpecification()
	 * @see #getQuerySnapshot()
	 * @generated
	 */
	EAttribute getQuerySnapshot_InputSpecification();

	/**
	 * Returns the meta object for enum '{@link org.eclipse.viatra.query.testing.snapshot.InputSpecification <em>Input Specification</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for enum '<em>Input Specification</em>'.
	 * @see org.eclipse.viatra.query.testing.snapshot.InputSpecification
	 * @generated
	 */
	EEnum getInputSpecification();

	/**
	 * Returns the meta object for enum '{@link org.eclipse.viatra.query.testing.snapshot.RecordRole <em>Record Role</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for enum '<em>Record Role</em>'.
	 * @see org.eclipse.viatra.query.testing.snapshot.RecordRole
	 * @generated
	 */
	EEnum getRecordRole();

	/**
	 * Returns the factory that creates the instances of the model.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the factory that creates the instances of the model.
	 * @generated
	 */
	SnapshotFactory getSnapshotFactory();

	/**
	 * <!-- begin-user-doc -->
	 * Defines literals for the meta objects that represent
	 * <ul>
	 *   <li>each class,</li>
	 *   <li>each feature of each class,</li>
	 *   <li>each operation of each class,</li>
	 *   <li>each enum,</li>
	 *   <li>and each data type</li>
	 * </ul>
	 * <!-- end-user-doc -->
	 * @generated
	 */
	interface Literals {
		/**
		 * The meta object literal for the '{@link org.eclipse.viatra.query.testing.snapshot.impl.MatchSetRecordImpl <em>Match Set Record</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.eclipse.viatra.query.testing.snapshot.impl.MatchSetRecordImpl
		 * @see org.eclipse.viatra.query.testing.snapshot.impl.SnapshotPackageImpl#getMatchSetRecord()
		 * @generated
		 */
		EClass MATCH_SET_RECORD = eINSTANCE.getMatchSetRecord();

		/**
		 * The meta object literal for the '<em><b>Pattern Qualified Name</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute MATCH_SET_RECORD__PATTERN_QUALIFIED_NAME = eINSTANCE.getMatchSetRecord_PatternQualifiedName();

		/**
		 * The meta object literal for the '<em><b>Matches</b></em>' containment reference list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference MATCH_SET_RECORD__MATCHES = eINSTANCE.getMatchSetRecord_Matches();

		/**
		 * The meta object literal for the '<em><b>Filter</b></em>' containment reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference MATCH_SET_RECORD__FILTER = eINSTANCE.getMatchSetRecord_Filter();

		/**
		 * The meta object literal for the '{@link org.eclipse.viatra.query.testing.snapshot.impl.MatchRecordImpl <em>Match Record</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.eclipse.viatra.query.testing.snapshot.impl.MatchRecordImpl
		 * @see org.eclipse.viatra.query.testing.snapshot.impl.SnapshotPackageImpl#getMatchRecord()
		 * @generated
		 */
		EClass MATCH_RECORD = eINSTANCE.getMatchRecord();

		/**
		 * The meta object literal for the '<em><b>Substitutions</b></em>' containment reference list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference MATCH_RECORD__SUBSTITUTIONS = eINSTANCE.getMatchRecord_Substitutions();

		/**
		 * The meta object literal for the '{@link org.eclipse.viatra.query.testing.snapshot.impl.MatchSubstitutionRecordImpl <em>Match Substitution Record</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.eclipse.viatra.query.testing.snapshot.impl.MatchSubstitutionRecordImpl
		 * @see org.eclipse.viatra.query.testing.snapshot.impl.SnapshotPackageImpl#getMatchSubstitutionRecord()
		 * @generated
		 */
		EClass MATCH_SUBSTITUTION_RECORD = eINSTANCE.getMatchSubstitutionRecord();

		/**
		 * The meta object literal for the '<em><b>Parameter Name</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute MATCH_SUBSTITUTION_RECORD__PARAMETER_NAME = eINSTANCE.getMatchSubstitutionRecord_ParameterName();

		/**
		 * The meta object literal for the '{@link org.eclipse.viatra.query.testing.snapshot.impl.EMFSubstitutionImpl <em>EMF Substitution</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.eclipse.viatra.query.testing.snapshot.impl.EMFSubstitutionImpl
		 * @see org.eclipse.viatra.query.testing.snapshot.impl.SnapshotPackageImpl#getEMFSubstitution()
		 * @generated
		 */
		EClass EMF_SUBSTITUTION = eINSTANCE.getEMFSubstitution();

		/**
		 * The meta object literal for the '<em><b>Value</b></em>' reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference EMF_SUBSTITUTION__VALUE = eINSTANCE.getEMFSubstitution_Value();

		/**
		 * The meta object literal for the '{@link org.eclipse.viatra.query.testing.snapshot.impl.IntSubstitutionImpl <em>Int Substitution</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.eclipse.viatra.query.testing.snapshot.impl.IntSubstitutionImpl
		 * @see org.eclipse.viatra.query.testing.snapshot.impl.SnapshotPackageImpl#getIntSubstitution()
		 * @generated
		 */
		EClass INT_SUBSTITUTION = eINSTANCE.getIntSubstitution();

		/**
		 * The meta object literal for the '<em><b>Value</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute INT_SUBSTITUTION__VALUE = eINSTANCE.getIntSubstitution_Value();

		/**
		 * The meta object literal for the '{@link org.eclipse.viatra.query.testing.snapshot.impl.LongSubstitutionImpl <em>Long Substitution</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.eclipse.viatra.query.testing.snapshot.impl.LongSubstitutionImpl
		 * @see org.eclipse.viatra.query.testing.snapshot.impl.SnapshotPackageImpl#getLongSubstitution()
		 * @generated
		 */
		EClass LONG_SUBSTITUTION = eINSTANCE.getLongSubstitution();

		/**
		 * The meta object literal for the '<em><b>Value</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute LONG_SUBSTITUTION__VALUE = eINSTANCE.getLongSubstitution_Value();

		/**
		 * The meta object literal for the '{@link org.eclipse.viatra.query.testing.snapshot.impl.DoubleSubstitutionImpl <em>Double Substitution</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.eclipse.viatra.query.testing.snapshot.impl.DoubleSubstitutionImpl
		 * @see org.eclipse.viatra.query.testing.snapshot.impl.SnapshotPackageImpl#getDoubleSubstitution()
		 * @generated
		 */
		EClass DOUBLE_SUBSTITUTION = eINSTANCE.getDoubleSubstitution();

		/**
		 * The meta object literal for the '<em><b>Value</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute DOUBLE_SUBSTITUTION__VALUE = eINSTANCE.getDoubleSubstitution_Value();

		/**
		 * The meta object literal for the '{@link org.eclipse.viatra.query.testing.snapshot.impl.FloatSubstitutionImpl <em>Float Substitution</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.eclipse.viatra.query.testing.snapshot.impl.FloatSubstitutionImpl
		 * @see org.eclipse.viatra.query.testing.snapshot.impl.SnapshotPackageImpl#getFloatSubstitution()
		 * @generated
		 */
		EClass FLOAT_SUBSTITUTION = eINSTANCE.getFloatSubstitution();

		/**
		 * The meta object literal for the '<em><b>Value</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute FLOAT_SUBSTITUTION__VALUE = eINSTANCE.getFloatSubstitution_Value();

		/**
		 * The meta object literal for the '{@link org.eclipse.viatra.query.testing.snapshot.impl.BooleanSubstitutionImpl <em>Boolean Substitution</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.eclipse.viatra.query.testing.snapshot.impl.BooleanSubstitutionImpl
		 * @see org.eclipse.viatra.query.testing.snapshot.impl.SnapshotPackageImpl#getBooleanSubstitution()
		 * @generated
		 */
		EClass BOOLEAN_SUBSTITUTION = eINSTANCE.getBooleanSubstitution();

		/**
		 * The meta object literal for the '<em><b>Value</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute BOOLEAN_SUBSTITUTION__VALUE = eINSTANCE.getBooleanSubstitution_Value();

		/**
		 * The meta object literal for the '{@link org.eclipse.viatra.query.testing.snapshot.impl.StringSubstitutionImpl <em>String Substitution</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.eclipse.viatra.query.testing.snapshot.impl.StringSubstitutionImpl
		 * @see org.eclipse.viatra.query.testing.snapshot.impl.SnapshotPackageImpl#getStringSubstitution()
		 * @generated
		 */
		EClass STRING_SUBSTITUTION = eINSTANCE.getStringSubstitution();

		/**
		 * The meta object literal for the '<em><b>Value</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute STRING_SUBSTITUTION__VALUE = eINSTANCE.getStringSubstitution_Value();

		/**
		 * The meta object literal for the '{@link org.eclipse.viatra.query.testing.snapshot.impl.DateSubstitutionImpl <em>Date Substitution</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.eclipse.viatra.query.testing.snapshot.impl.DateSubstitutionImpl
		 * @see org.eclipse.viatra.query.testing.snapshot.impl.SnapshotPackageImpl#getDateSubstitution()
		 * @generated
		 */
		EClass DATE_SUBSTITUTION = eINSTANCE.getDateSubstitution();

		/**
		 * The meta object literal for the '<em><b>Value</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute DATE_SUBSTITUTION__VALUE = eINSTANCE.getDateSubstitution_Value();

		/**
		 * The meta object literal for the '{@link org.eclipse.viatra.query.testing.snapshot.impl.EnumSubstitutionImpl <em>Enum Substitution</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.eclipse.viatra.query.testing.snapshot.impl.EnumSubstitutionImpl
		 * @see org.eclipse.viatra.query.testing.snapshot.impl.SnapshotPackageImpl#getEnumSubstitution()
		 * @generated
		 */
		EClass ENUM_SUBSTITUTION = eINSTANCE.getEnumSubstitution();

		/**
		 * The meta object literal for the '<em><b>Value Literal</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute ENUM_SUBSTITUTION__VALUE_LITERAL = eINSTANCE.getEnumSubstitution_ValueLiteral();

		/**
		 * The meta object literal for the '<em><b>Enum Type</b></em>' reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference ENUM_SUBSTITUTION__ENUM_TYPE = eINSTANCE.getEnumSubstitution_EnumType();

		/**
		 * The meta object literal for the '{@link org.eclipse.viatra.query.testing.snapshot.impl.MiscellaneousSubstitutionImpl <em>Miscellaneous Substitution</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.eclipse.viatra.query.testing.snapshot.impl.MiscellaneousSubstitutionImpl
		 * @see org.eclipse.viatra.query.testing.snapshot.impl.SnapshotPackageImpl#getMiscellaneousSubstitution()
		 * @generated
		 */
		EClass MISCELLANEOUS_SUBSTITUTION = eINSTANCE.getMiscellaneousSubstitution();

		/**
		 * The meta object literal for the '<em><b>Value</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute MISCELLANEOUS_SUBSTITUTION__VALUE = eINSTANCE.getMiscellaneousSubstitution_Value();

		/**
		 * The meta object literal for the '{@link org.eclipse.viatra.query.testing.snapshot.impl.QuerySnapshotImpl <em>Query Snapshot</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.eclipse.viatra.query.testing.snapshot.impl.QuerySnapshotImpl
		 * @see org.eclipse.viatra.query.testing.snapshot.impl.SnapshotPackageImpl#getQuerySnapshot()
		 * @generated
		 */
		EClass QUERY_SNAPSHOT = eINSTANCE.getQuerySnapshot();

		/**
		 * The meta object literal for the '<em><b>Match Set Records</b></em>' containment reference list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference QUERY_SNAPSHOT__MATCH_SET_RECORDS = eINSTANCE.getQuerySnapshot_MatchSetRecords();

		/**
		 * The meta object literal for the '<em><b>Model Roots</b></em>' reference list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference QUERY_SNAPSHOT__MODEL_ROOTS = eINSTANCE.getQuerySnapshot_ModelRoots();

		/**
		 * The meta object literal for the '<em><b>Input Specification</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute QUERY_SNAPSHOT__INPUT_SPECIFICATION = eINSTANCE.getQuerySnapshot_InputSpecification();

		/**
		 * The meta object literal for the '{@link org.eclipse.viatra.query.testing.snapshot.InputSpecification <em>Input Specification</em>}' enum.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.eclipse.viatra.query.testing.snapshot.InputSpecification
		 * @see org.eclipse.viatra.query.testing.snapshot.impl.SnapshotPackageImpl#getInputSpecification()
		 * @generated
		 */
		EEnum INPUT_SPECIFICATION = eINSTANCE.getInputSpecification();

		/**
		 * The meta object literal for the '{@link org.eclipse.viatra.query.testing.snapshot.RecordRole <em>Record Role</em>}' enum.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.eclipse.viatra.query.testing.snapshot.RecordRole
		 * @see org.eclipse.viatra.query.testing.snapshot.impl.SnapshotPackageImpl#getRecordRole()
		 * @generated
		 */
		EEnum RECORD_ROLE = eINSTANCE.getRecordRole();

	}

} //SnapshotPackage
