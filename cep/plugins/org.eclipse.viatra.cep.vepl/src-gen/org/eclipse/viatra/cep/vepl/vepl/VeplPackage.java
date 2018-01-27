/**
 */
package org.eclipse.viatra.cep.vepl.vepl;

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
 *   <li>each enum,</li>
 *   <li>and each data type</li>
 * </ul>
 * <!-- end-user-doc -->
 * @see org.eclipse.viatra.cep.vepl.vepl.VeplFactory
 * @model kind="package"
 * @generated
 */
public interface VeplPackage extends EPackage
{
  /**
   * The package name.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  String eNAME = "vepl";

  /**
   * The package namespace URI.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  String eNS_URI = "http://www.eclipse.org/viatra/cep/vepl/Vepl";

  /**
   * The package namespace name.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  String eNS_PREFIX = "vepl";

  /**
   * The singleton instance of the package.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  VeplPackage eINSTANCE = org.eclipse.viatra.cep.vepl.vepl.impl.VeplPackageImpl.init();

  /**
   * The meta object id for the '{@link org.eclipse.viatra.cep.vepl.vepl.impl.EventModelImpl <em>Event Model</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see org.eclipse.viatra.cep.vepl.vepl.impl.EventModelImpl
   * @see org.eclipse.viatra.cep.vepl.vepl.impl.VeplPackageImpl#getEventModel()
   * @generated
   */
  int EVENT_MODEL = 0;

  /**
   * The feature id for the '<em><b>Name</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int EVENT_MODEL__NAME = 0;

  /**
   * The feature id for the '<em><b>Imports</b></em>' containment reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int EVENT_MODEL__IMPORTS = 1;

  /**
   * The feature id for the '<em><b>Context</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int EVENT_MODEL__CONTEXT = 2;

  /**
   * The feature id for the '<em><b>Model Elements</b></em>' containment reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int EVENT_MODEL__MODEL_ELEMENTS = 3;

  /**
   * The number of structural features of the '<em>Event Model</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int EVENT_MODEL_FEATURE_COUNT = 4;

  /**
   * The meta object id for the '{@link org.eclipse.viatra.cep.vepl.vepl.impl.ImportImpl <em>Import</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see org.eclipse.viatra.cep.vepl.vepl.impl.ImportImpl
   * @see org.eclipse.viatra.cep.vepl.vepl.impl.VeplPackageImpl#getImport()
   * @generated
   */
  int IMPORT = 1;

  /**
   * The feature id for the '<em><b>Imported Namespace</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int IMPORT__IMPORTED_NAMESPACE = 0;

  /**
   * The number of structural features of the '<em>Import</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int IMPORT_FEATURE_COUNT = 1;

  /**
   * The meta object id for the '{@link org.eclipse.viatra.cep.vepl.vepl.impl.GenericImportImpl <em>Generic Import</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see org.eclipse.viatra.cep.vepl.vepl.impl.GenericImportImpl
   * @see org.eclipse.viatra.cep.vepl.vepl.impl.VeplPackageImpl#getGenericImport()
   * @generated
   */
  int GENERIC_IMPORT = 2;

  /**
   * The feature id for the '<em><b>Imported Namespace</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int GENERIC_IMPORT__IMPORTED_NAMESPACE = IMPORT__IMPORTED_NAMESPACE;

  /**
   * The number of structural features of the '<em>Generic Import</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int GENERIC_IMPORT_FEATURE_COUNT = IMPORT_FEATURE_COUNT + 0;

  /**
   * The meta object id for the '{@link org.eclipse.viatra.cep.vepl.vepl.impl.QueryImportImpl <em>Query Import</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see org.eclipse.viatra.cep.vepl.vepl.impl.QueryImportImpl
   * @see org.eclipse.viatra.cep.vepl.vepl.impl.VeplPackageImpl#getQueryImport()
   * @generated
   */
  int QUERY_IMPORT = 3;

  /**
   * The feature id for the '<em><b>Imported Namespace</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int QUERY_IMPORT__IMPORTED_NAMESPACE = IMPORT__IMPORTED_NAMESPACE;

  /**
   * The number of structural features of the '<em>Query Import</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int QUERY_IMPORT_FEATURE_COUNT = IMPORT_FEATURE_COUNT + 0;

  /**
   * The meta object id for the '{@link org.eclipse.viatra.cep.vepl.vepl.impl.ModelElementImpl <em>Model Element</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see org.eclipse.viatra.cep.vepl.vepl.impl.ModelElementImpl
   * @see org.eclipse.viatra.cep.vepl.vepl.impl.VeplPackageImpl#getModelElement()
   * @generated
   */
  int MODEL_ELEMENT = 4;

  /**
   * The feature id for the '<em><b>Name</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int MODEL_ELEMENT__NAME = 0;

  /**
   * The number of structural features of the '<em>Model Element</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int MODEL_ELEMENT_FEATURE_COUNT = 1;

  /**
   * The meta object id for the '{@link org.eclipse.viatra.cep.vepl.vepl.impl.TraitImpl <em>Trait</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see org.eclipse.viatra.cep.vepl.vepl.impl.TraitImpl
   * @see org.eclipse.viatra.cep.vepl.vepl.impl.VeplPackageImpl#getTrait()
   * @generated
   */
  int TRAIT = 5;

  /**
   * The feature id for the '<em><b>Name</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int TRAIT__NAME = MODEL_ELEMENT__NAME;

  /**
   * The feature id for the '<em><b>Parameters</b></em>' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int TRAIT__PARAMETERS = MODEL_ELEMENT_FEATURE_COUNT + 0;

  /**
   * The number of structural features of the '<em>Trait</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int TRAIT_FEATURE_COUNT = MODEL_ELEMENT_FEATURE_COUNT + 1;

  /**
   * The meta object id for the '{@link org.eclipse.viatra.cep.vepl.vepl.impl.EventPatternImpl <em>Event Pattern</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see org.eclipse.viatra.cep.vepl.vepl.impl.EventPatternImpl
   * @see org.eclipse.viatra.cep.vepl.vepl.impl.VeplPackageImpl#getEventPattern()
   * @generated
   */
  int EVENT_PATTERN = 6;

  /**
   * The feature id for the '<em><b>Name</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int EVENT_PATTERN__NAME = MODEL_ELEMENT__NAME;

  /**
   * The feature id for the '<em><b>Parameters</b></em>' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int EVENT_PATTERN__PARAMETERS = MODEL_ELEMENT_FEATURE_COUNT + 0;

  /**
   * The number of structural features of the '<em>Event Pattern</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int EVENT_PATTERN_FEATURE_COUNT = MODEL_ELEMENT_FEATURE_COUNT + 1;

  /**
   * The meta object id for the '{@link org.eclipse.viatra.cep.vepl.vepl.impl.AbstractAtomicEventPatternImpl <em>Abstract Atomic Event Pattern</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see org.eclipse.viatra.cep.vepl.vepl.impl.AbstractAtomicEventPatternImpl
   * @see org.eclipse.viatra.cep.vepl.vepl.impl.VeplPackageImpl#getAbstractAtomicEventPattern()
   * @generated
   */
  int ABSTRACT_ATOMIC_EVENT_PATTERN = 7;

  /**
   * The feature id for the '<em><b>Name</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int ABSTRACT_ATOMIC_EVENT_PATTERN__NAME = EVENT_PATTERN__NAME;

  /**
   * The feature id for the '<em><b>Parameters</b></em>' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int ABSTRACT_ATOMIC_EVENT_PATTERN__PARAMETERS = EVENT_PATTERN__PARAMETERS;

  /**
   * The number of structural features of the '<em>Abstract Atomic Event Pattern</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int ABSTRACT_ATOMIC_EVENT_PATTERN_FEATURE_COUNT = EVENT_PATTERN_FEATURE_COUNT + 0;

  /**
   * The meta object id for the '{@link org.eclipse.viatra.cep.vepl.vepl.impl.AtomicEventPatternImpl <em>Atomic Event Pattern</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see org.eclipse.viatra.cep.vepl.vepl.impl.AtomicEventPatternImpl
   * @see org.eclipse.viatra.cep.vepl.vepl.impl.VeplPackageImpl#getAtomicEventPattern()
   * @generated
   */
  int ATOMIC_EVENT_PATTERN = 8;

  /**
   * The feature id for the '<em><b>Name</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int ATOMIC_EVENT_PATTERN__NAME = ABSTRACT_ATOMIC_EVENT_PATTERN__NAME;

  /**
   * The feature id for the '<em><b>Parameters</b></em>' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int ATOMIC_EVENT_PATTERN__PARAMETERS = ABSTRACT_ATOMIC_EVENT_PATTERN__PARAMETERS;

  /**
   * The feature id for the '<em><b>Traits</b></em>' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int ATOMIC_EVENT_PATTERN__TRAITS = ABSTRACT_ATOMIC_EVENT_PATTERN_FEATURE_COUNT + 0;

  /**
   * The feature id for the '<em><b>Check Expression</b></em>' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int ATOMIC_EVENT_PATTERN__CHECK_EXPRESSION = ABSTRACT_ATOMIC_EVENT_PATTERN_FEATURE_COUNT + 1;

  /**
   * The number of structural features of the '<em>Atomic Event Pattern</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int ATOMIC_EVENT_PATTERN_FEATURE_COUNT = ABSTRACT_ATOMIC_EVENT_PATTERN_FEATURE_COUNT + 2;

  /**
   * The meta object id for the '{@link org.eclipse.viatra.cep.vepl.vepl.impl.QueryResultChangeEventPatternImpl <em>Query Result Change Event Pattern</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see org.eclipse.viatra.cep.vepl.vepl.impl.QueryResultChangeEventPatternImpl
   * @see org.eclipse.viatra.cep.vepl.vepl.impl.VeplPackageImpl#getQueryResultChangeEventPattern()
   * @generated
   */
  int QUERY_RESULT_CHANGE_EVENT_PATTERN = 9;

  /**
   * The feature id for the '<em><b>Name</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int QUERY_RESULT_CHANGE_EVENT_PATTERN__NAME = ABSTRACT_ATOMIC_EVENT_PATTERN__NAME;

  /**
   * The feature id for the '<em><b>Parameters</b></em>' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int QUERY_RESULT_CHANGE_EVENT_PATTERN__PARAMETERS = ABSTRACT_ATOMIC_EVENT_PATTERN__PARAMETERS;

  /**
   * The feature id for the '<em><b>Query Reference</b></em>' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int QUERY_RESULT_CHANGE_EVENT_PATTERN__QUERY_REFERENCE = ABSTRACT_ATOMIC_EVENT_PATTERN_FEATURE_COUNT + 0;

  /**
   * The feature id for the '<em><b>Result Change Type</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int QUERY_RESULT_CHANGE_EVENT_PATTERN__RESULT_CHANGE_TYPE = ABSTRACT_ATOMIC_EVENT_PATTERN_FEATURE_COUNT + 1;

  /**
   * The number of structural features of the '<em>Query Result Change Event Pattern</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int QUERY_RESULT_CHANGE_EVENT_PATTERN_FEATURE_COUNT = ABSTRACT_ATOMIC_EVENT_PATTERN_FEATURE_COUNT + 2;

  /**
   * The meta object id for the '{@link org.eclipse.viatra.cep.vepl.vepl.impl.ComplexEventPatternImpl <em>Complex Event Pattern</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see org.eclipse.viatra.cep.vepl.vepl.impl.ComplexEventPatternImpl
   * @see org.eclipse.viatra.cep.vepl.vepl.impl.VeplPackageImpl#getComplexEventPattern()
   * @generated
   */
  int COMPLEX_EVENT_PATTERN = 10;

  /**
   * The feature id for the '<em><b>Name</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int COMPLEX_EVENT_PATTERN__NAME = EVENT_PATTERN__NAME;

  /**
   * The feature id for the '<em><b>Parameters</b></em>' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int COMPLEX_EVENT_PATTERN__PARAMETERS = EVENT_PATTERN__PARAMETERS;

  /**
   * The feature id for the '<em><b>Complex Event Expression</b></em>' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int COMPLEX_EVENT_PATTERN__COMPLEX_EVENT_EXPRESSION = EVENT_PATTERN_FEATURE_COUNT + 0;

  /**
   * The feature id for the '<em><b>Context</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int COMPLEX_EVENT_PATTERN__CONTEXT = EVENT_PATTERN_FEATURE_COUNT + 1;

  /**
   * The number of structural features of the '<em>Complex Event Pattern</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int COMPLEX_EVENT_PATTERN_FEATURE_COUNT = EVENT_PATTERN_FEATURE_COUNT + 2;

  /**
   * The meta object id for the '{@link org.eclipse.viatra.cep.vepl.vepl.impl.RuleImpl <em>Rule</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see org.eclipse.viatra.cep.vepl.vepl.impl.RuleImpl
   * @see org.eclipse.viatra.cep.vepl.vepl.impl.VeplPackageImpl#getRule()
   * @generated
   */
  int RULE = 11;

  /**
   * The feature id for the '<em><b>Name</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int RULE__NAME = MODEL_ELEMENT__NAME;

  /**
   * The feature id for the '<em><b>Event Patterns</b></em>' containment reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int RULE__EVENT_PATTERNS = MODEL_ELEMENT_FEATURE_COUNT + 0;

  /**
   * The feature id for the '<em><b>Action</b></em>' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int RULE__ACTION = MODEL_ELEMENT_FEATURE_COUNT + 1;

  /**
   * The number of structural features of the '<em>Rule</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int RULE_FEATURE_COUNT = MODEL_ELEMENT_FEATURE_COUNT + 2;

  /**
   * The meta object id for the '{@link org.eclipse.viatra.cep.vepl.vepl.impl.TraitListImpl <em>Trait List</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see org.eclipse.viatra.cep.vepl.vepl.impl.TraitListImpl
   * @see org.eclipse.viatra.cep.vepl.vepl.impl.VeplPackageImpl#getTraitList()
   * @generated
   */
  int TRAIT_LIST = 12;

  /**
   * The feature id for the '<em><b>Traits</b></em>' reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int TRAIT_LIST__TRAITS = 0;

  /**
   * The number of structural features of the '<em>Trait List</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int TRAIT_LIST_FEATURE_COUNT = 1;

  /**
   * The meta object id for the '{@link org.eclipse.viatra.cep.vepl.vepl.impl.TypedParameterListImpl <em>Typed Parameter List</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see org.eclipse.viatra.cep.vepl.vepl.impl.TypedParameterListImpl
   * @see org.eclipse.viatra.cep.vepl.vepl.impl.VeplPackageImpl#getTypedParameterList()
   * @generated
   */
  int TYPED_PARAMETER_LIST = 13;

  /**
   * The feature id for the '<em><b>Parameters</b></em>' containment reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int TYPED_PARAMETER_LIST__PARAMETERS = 0;

  /**
   * The number of structural features of the '<em>Typed Parameter List</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int TYPED_PARAMETER_LIST_FEATURE_COUNT = 1;

  /**
   * The meta object id for the '{@link org.eclipse.viatra.cep.vepl.vepl.impl.TypedParameterImpl <em>Typed Parameter</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see org.eclipse.viatra.cep.vepl.vepl.impl.TypedParameterImpl
   * @see org.eclipse.viatra.cep.vepl.vepl.impl.VeplPackageImpl#getTypedParameter()
   * @generated
   */
  int TYPED_PARAMETER = 14;

  /**
   * The feature id for the '<em><b>Name</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int TYPED_PARAMETER__NAME = 0;

  /**
   * The feature id for the '<em><b>Type</b></em>' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int TYPED_PARAMETER__TYPE = 1;

  /**
   * The number of structural features of the '<em>Typed Parameter</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int TYPED_PARAMETER_FEATURE_COUNT = 2;

  /**
   * The meta object id for the '{@link org.eclipse.viatra.cep.vepl.vepl.impl.TypedParameterWithDefaultValueImpl <em>Typed Parameter With Default Value</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see org.eclipse.viatra.cep.vepl.vepl.impl.TypedParameterWithDefaultValueImpl
   * @see org.eclipse.viatra.cep.vepl.vepl.impl.VeplPackageImpl#getTypedParameterWithDefaultValue()
   * @generated
   */
  int TYPED_PARAMETER_WITH_DEFAULT_VALUE = 15;

  /**
   * The feature id for the '<em><b>Typed Parameter</b></em>' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int TYPED_PARAMETER_WITH_DEFAULT_VALUE__TYPED_PARAMETER = 0;

  /**
   * The feature id for the '<em><b>Value</b></em>' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int TYPED_PARAMETER_WITH_DEFAULT_VALUE__VALUE = 1;

  /**
   * The number of structural features of the '<em>Typed Parameter With Default Value</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int TYPED_PARAMETER_WITH_DEFAULT_VALUE_FEATURE_COUNT = 2;

  /**
   * The meta object id for the '{@link org.eclipse.viatra.cep.vepl.vepl.impl.TraitTypedParameterListImpl <em>Trait Typed Parameter List</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see org.eclipse.viatra.cep.vepl.vepl.impl.TraitTypedParameterListImpl
   * @see org.eclipse.viatra.cep.vepl.vepl.impl.VeplPackageImpl#getTraitTypedParameterList()
   * @generated
   */
  int TRAIT_TYPED_PARAMETER_LIST = 16;

  /**
   * The feature id for the '<em><b>Parameters</b></em>' containment reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int TRAIT_TYPED_PARAMETER_LIST__PARAMETERS = 0;

  /**
   * The number of structural features of the '<em>Trait Typed Parameter List</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int TRAIT_TYPED_PARAMETER_LIST_FEATURE_COUNT = 1;

  /**
   * The meta object id for the '{@link org.eclipse.viatra.cep.vepl.vepl.impl.ParametrizedQueryReferenceImpl <em>Parametrized Query Reference</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see org.eclipse.viatra.cep.vepl.vepl.impl.ParametrizedQueryReferenceImpl
   * @see org.eclipse.viatra.cep.vepl.vepl.impl.VeplPackageImpl#getParametrizedQueryReference()
   * @generated
   */
  int PARAMETRIZED_QUERY_REFERENCE = 17;

  /**
   * The feature id for the '<em><b>Query</b></em>' reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int PARAMETRIZED_QUERY_REFERENCE__QUERY = 0;

  /**
   * The feature id for the '<em><b>Parameter List</b></em>' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int PARAMETRIZED_QUERY_REFERENCE__PARAMETER_LIST = 1;

  /**
   * The number of structural features of the '<em>Parametrized Query Reference</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int PARAMETRIZED_QUERY_REFERENCE_FEATURE_COUNT = 2;

  /**
   * The meta object id for the '{@link org.eclipse.viatra.cep.vepl.vepl.impl.ComplexEventExpressionImpl <em>Complex Event Expression</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see org.eclipse.viatra.cep.vepl.vepl.impl.ComplexEventExpressionImpl
   * @see org.eclipse.viatra.cep.vepl.vepl.impl.VeplPackageImpl#getComplexEventExpression()
   * @generated
   */
  int COMPLEX_EVENT_EXPRESSION = 18;

  /**
   * The feature id for the '<em><b>Left</b></em>' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int COMPLEX_EVENT_EXPRESSION__LEFT = 0;

  /**
   * The feature id for the '<em><b>Right</b></em>' containment reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int COMPLEX_EVENT_EXPRESSION__RIGHT = 1;

  /**
   * The feature id for the '<em><b>Multiplicity</b></em>' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int COMPLEX_EVENT_EXPRESSION__MULTIPLICITY = 2;

  /**
   * The feature id for the '<em><b>Timewindow</b></em>' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int COMPLEX_EVENT_EXPRESSION__TIMEWINDOW = 3;

  /**
   * The feature id for the '<em><b>Neg Operator</b></em>' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int COMPLEX_EVENT_EXPRESSION__NEG_OPERATOR = 4;

  /**
   * The number of structural features of the '<em>Complex Event Expression</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int COMPLEX_EVENT_EXPRESSION_FEATURE_COUNT = 5;

  /**
   * The meta object id for the '{@link org.eclipse.viatra.cep.vepl.vepl.impl.ChainedExpressionImpl <em>Chained Expression</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see org.eclipse.viatra.cep.vepl.vepl.impl.ChainedExpressionImpl
   * @see org.eclipse.viatra.cep.vepl.vepl.impl.VeplPackageImpl#getChainedExpression()
   * @generated
   */
  int CHAINED_EXPRESSION = 19;

  /**
   * The feature id for the '<em><b>Operator</b></em>' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int CHAINED_EXPRESSION__OPERATOR = 0;

  /**
   * The feature id for the '<em><b>Expression</b></em>' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int CHAINED_EXPRESSION__EXPRESSION = 1;

  /**
   * The number of structural features of the '<em>Chained Expression</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int CHAINED_EXPRESSION_FEATURE_COUNT = 2;

  /**
   * The meta object id for the '{@link org.eclipse.viatra.cep.vepl.vepl.impl.AtomImpl <em>Atom</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see org.eclipse.viatra.cep.vepl.vepl.impl.AtomImpl
   * @see org.eclipse.viatra.cep.vepl.vepl.impl.VeplPackageImpl#getAtom()
   * @generated
   */
  int ATOM = 20;

  /**
   * The feature id for the '<em><b>Left</b></em>' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int ATOM__LEFT = COMPLEX_EVENT_EXPRESSION__LEFT;

  /**
   * The feature id for the '<em><b>Right</b></em>' containment reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int ATOM__RIGHT = COMPLEX_EVENT_EXPRESSION__RIGHT;

  /**
   * The feature id for the '<em><b>Multiplicity</b></em>' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int ATOM__MULTIPLICITY = COMPLEX_EVENT_EXPRESSION__MULTIPLICITY;

  /**
   * The feature id for the '<em><b>Timewindow</b></em>' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int ATOM__TIMEWINDOW = COMPLEX_EVENT_EXPRESSION__TIMEWINDOW;

  /**
   * The feature id for the '<em><b>Neg Operator</b></em>' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int ATOM__NEG_OPERATOR = COMPLEX_EVENT_EXPRESSION__NEG_OPERATOR;

  /**
   * The feature id for the '<em><b>Pattern Call</b></em>' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int ATOM__PATTERN_CALL = COMPLEX_EVENT_EXPRESSION_FEATURE_COUNT + 0;

  /**
   * The number of structural features of the '<em>Atom</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int ATOM_FEATURE_COUNT = COMPLEX_EVENT_EXPRESSION_FEATURE_COUNT + 1;

  /**
   * The meta object id for the '{@link org.eclipse.viatra.cep.vepl.vepl.impl.AbstractMultiplicityImpl <em>Abstract Multiplicity</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see org.eclipse.viatra.cep.vepl.vepl.impl.AbstractMultiplicityImpl
   * @see org.eclipse.viatra.cep.vepl.vepl.impl.VeplPackageImpl#getAbstractMultiplicity()
   * @generated
   */
  int ABSTRACT_MULTIPLICITY = 21;

  /**
   * The number of structural features of the '<em>Abstract Multiplicity</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int ABSTRACT_MULTIPLICITY_FEATURE_COUNT = 0;

  /**
   * The meta object id for the '{@link org.eclipse.viatra.cep.vepl.vepl.impl.TimewindowImpl <em>Timewindow</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see org.eclipse.viatra.cep.vepl.vepl.impl.TimewindowImpl
   * @see org.eclipse.viatra.cep.vepl.vepl.impl.VeplPackageImpl#getTimewindow()
   * @generated
   */
  int TIMEWINDOW = 22;

  /**
   * The feature id for the '<em><b>Length</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int TIMEWINDOW__LENGTH = 0;

  /**
   * The number of structural features of the '<em>Timewindow</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int TIMEWINDOW_FEATURE_COUNT = 1;

  /**
   * The meta object id for the '{@link org.eclipse.viatra.cep.vepl.vepl.impl.ParameterizedPatternCallImpl <em>Parameterized Pattern Call</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see org.eclipse.viatra.cep.vepl.vepl.impl.ParameterizedPatternCallImpl
   * @see org.eclipse.viatra.cep.vepl.vepl.impl.VeplPackageImpl#getParameterizedPatternCall()
   * @generated
   */
  int PARAMETERIZED_PATTERN_CALL = 23;

  /**
   * The feature id for the '<em><b>Event Pattern</b></em>' reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int PARAMETERIZED_PATTERN_CALL__EVENT_PATTERN = 0;

  /**
   * The feature id for the '<em><b>Parameter List</b></em>' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int PARAMETERIZED_PATTERN_CALL__PARAMETER_LIST = 1;

  /**
   * The number of structural features of the '<em>Parameterized Pattern Call</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int PARAMETERIZED_PATTERN_CALL_FEATURE_COUNT = 2;

  /**
   * The meta object id for the '{@link org.eclipse.viatra.cep.vepl.vepl.impl.PatternCallParameterListImpl <em>Pattern Call Parameter List</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see org.eclipse.viatra.cep.vepl.vepl.impl.PatternCallParameterListImpl
   * @see org.eclipse.viatra.cep.vepl.vepl.impl.VeplPackageImpl#getPatternCallParameterList()
   * @generated
   */
  int PATTERN_CALL_PARAMETER_LIST = 24;

  /**
   * The feature id for the '<em><b>Parameters</b></em>' containment reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int PATTERN_CALL_PARAMETER_LIST__PARAMETERS = 0;

  /**
   * The number of structural features of the '<em>Pattern Call Parameter List</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int PATTERN_CALL_PARAMETER_LIST_FEATURE_COUNT = 1;

  /**
   * The meta object id for the '{@link org.eclipse.viatra.cep.vepl.vepl.impl.PatternCallParameterImpl <em>Pattern Call Parameter</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see org.eclipse.viatra.cep.vepl.vepl.impl.PatternCallParameterImpl
   * @see org.eclipse.viatra.cep.vepl.vepl.impl.VeplPackageImpl#getPatternCallParameter()
   * @generated
   */
  int PATTERN_CALL_PARAMETER = 25;

  /**
   * The feature id for the '<em><b>Name</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int PATTERN_CALL_PARAMETER__NAME = 0;

  /**
   * The number of structural features of the '<em>Pattern Call Parameter</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int PATTERN_CALL_PARAMETER_FEATURE_COUNT = 1;

  /**
   * The meta object id for the '{@link org.eclipse.viatra.cep.vepl.vepl.impl.ComplexEventOperatorImpl <em>Complex Event Operator</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see org.eclipse.viatra.cep.vepl.vepl.impl.ComplexEventOperatorImpl
   * @see org.eclipse.viatra.cep.vepl.vepl.impl.VeplPackageImpl#getComplexEventOperator()
   * @generated
   */
  int COMPLEX_EVENT_OPERATOR = 26;

  /**
   * The number of structural features of the '<em>Complex Event Operator</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int COMPLEX_EVENT_OPERATOR_FEATURE_COUNT = 0;

  /**
   * The meta object id for the '{@link org.eclipse.viatra.cep.vepl.vepl.impl.BinaryOperatorImpl <em>Binary Operator</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see org.eclipse.viatra.cep.vepl.vepl.impl.BinaryOperatorImpl
   * @see org.eclipse.viatra.cep.vepl.vepl.impl.VeplPackageImpl#getBinaryOperator()
   * @generated
   */
  int BINARY_OPERATOR = 27;

  /**
   * The number of structural features of the '<em>Binary Operator</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int BINARY_OPERATOR_FEATURE_COUNT = COMPLEX_EVENT_OPERATOR_FEATURE_COUNT + 0;

  /**
   * The meta object id for the '{@link org.eclipse.viatra.cep.vepl.vepl.impl.UnaryOperatorImpl <em>Unary Operator</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see org.eclipse.viatra.cep.vepl.vepl.impl.UnaryOperatorImpl
   * @see org.eclipse.viatra.cep.vepl.vepl.impl.VeplPackageImpl#getUnaryOperator()
   * @generated
   */
  int UNARY_OPERATOR = 28;

  /**
   * The number of structural features of the '<em>Unary Operator</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int UNARY_OPERATOR_FEATURE_COUNT = COMPLEX_EVENT_OPERATOR_FEATURE_COUNT + 0;

  /**
   * The meta object id for the '{@link org.eclipse.viatra.cep.vepl.vepl.impl.FollowsOperatorImpl <em>Follows Operator</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see org.eclipse.viatra.cep.vepl.vepl.impl.FollowsOperatorImpl
   * @see org.eclipse.viatra.cep.vepl.vepl.impl.VeplPackageImpl#getFollowsOperator()
   * @generated
   */
  int FOLLOWS_OPERATOR = 29;

  /**
   * The number of structural features of the '<em>Follows Operator</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int FOLLOWS_OPERATOR_FEATURE_COUNT = BINARY_OPERATOR_FEATURE_COUNT + 0;

  /**
   * The meta object id for the '{@link org.eclipse.viatra.cep.vepl.vepl.impl.OrOperatorImpl <em>Or Operator</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see org.eclipse.viatra.cep.vepl.vepl.impl.OrOperatorImpl
   * @see org.eclipse.viatra.cep.vepl.vepl.impl.VeplPackageImpl#getOrOperator()
   * @generated
   */
  int OR_OPERATOR = 30;

  /**
   * The number of structural features of the '<em>Or Operator</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int OR_OPERATOR_FEATURE_COUNT = BINARY_OPERATOR_FEATURE_COUNT + 0;

  /**
   * The meta object id for the '{@link org.eclipse.viatra.cep.vepl.vepl.impl.AndOperatorImpl <em>And Operator</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see org.eclipse.viatra.cep.vepl.vepl.impl.AndOperatorImpl
   * @see org.eclipse.viatra.cep.vepl.vepl.impl.VeplPackageImpl#getAndOperator()
   * @generated
   */
  int AND_OPERATOR = 31;

  /**
   * The number of structural features of the '<em>And Operator</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int AND_OPERATOR_FEATURE_COUNT = BINARY_OPERATOR_FEATURE_COUNT + 0;

  /**
   * The meta object id for the '{@link org.eclipse.viatra.cep.vepl.vepl.impl.UntilOperatorImpl <em>Until Operator</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see org.eclipse.viatra.cep.vepl.vepl.impl.UntilOperatorImpl
   * @see org.eclipse.viatra.cep.vepl.vepl.impl.VeplPackageImpl#getUntilOperator()
   * @generated
   */
  int UNTIL_OPERATOR = 32;

  /**
   * The number of structural features of the '<em>Until Operator</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int UNTIL_OPERATOR_FEATURE_COUNT = 0;

  /**
   * The meta object id for the '{@link org.eclipse.viatra.cep.vepl.vepl.impl.NegOperatorImpl <em>Neg Operator</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see org.eclipse.viatra.cep.vepl.vepl.impl.NegOperatorImpl
   * @see org.eclipse.viatra.cep.vepl.vepl.impl.VeplPackageImpl#getNegOperator()
   * @generated
   */
  int NEG_OPERATOR = 33;

  /**
   * The number of structural features of the '<em>Neg Operator</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int NEG_OPERATOR_FEATURE_COUNT = UNARY_OPERATOR_FEATURE_COUNT + 0;

  /**
   * The meta object id for the '{@link org.eclipse.viatra.cep.vepl.vepl.impl.MultiplicityImpl <em>Multiplicity</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see org.eclipse.viatra.cep.vepl.vepl.impl.MultiplicityImpl
   * @see org.eclipse.viatra.cep.vepl.vepl.impl.VeplPackageImpl#getMultiplicity()
   * @generated
   */
  int MULTIPLICITY = 34;

  /**
   * The feature id for the '<em><b>Value</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int MULTIPLICITY__VALUE = ABSTRACT_MULTIPLICITY_FEATURE_COUNT + 0;

  /**
   * The number of structural features of the '<em>Multiplicity</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int MULTIPLICITY_FEATURE_COUNT = ABSTRACT_MULTIPLICITY_FEATURE_COUNT + 1;

  /**
   * The meta object id for the '{@link org.eclipse.viatra.cep.vepl.vepl.impl.InfiniteImpl <em>Infinite</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see org.eclipse.viatra.cep.vepl.vepl.impl.InfiniteImpl
   * @see org.eclipse.viatra.cep.vepl.vepl.impl.VeplPackageImpl#getInfinite()
   * @generated
   */
  int INFINITE = 35;

  /**
   * The number of structural features of the '<em>Infinite</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int INFINITE_FEATURE_COUNT = ABSTRACT_MULTIPLICITY_FEATURE_COUNT + 0;

  /**
   * The meta object id for the '{@link org.eclipse.viatra.cep.vepl.vepl.impl.AtLeastOneImpl <em>At Least One</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see org.eclipse.viatra.cep.vepl.vepl.impl.AtLeastOneImpl
   * @see org.eclipse.viatra.cep.vepl.vepl.impl.VeplPackageImpl#getAtLeastOne()
   * @generated
   */
  int AT_LEAST_ONE = 36;

  /**
   * The number of structural features of the '<em>At Least One</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int AT_LEAST_ONE_FEATURE_COUNT = ABSTRACT_MULTIPLICITY_FEATURE_COUNT + 0;

  /**
   * The meta object id for the '{@link org.eclipse.viatra.cep.vepl.vepl.ContextEnum <em>Context Enum</em>}' enum.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see org.eclipse.viatra.cep.vepl.vepl.ContextEnum
   * @see org.eclipse.viatra.cep.vepl.vepl.impl.VeplPackageImpl#getContextEnum()
   * @generated
   */
  int CONTEXT_ENUM = 37;

  /**
   * The meta object id for the '{@link org.eclipse.viatra.cep.vepl.vepl.QueryResultChangeType <em>Query Result Change Type</em>}' enum.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see org.eclipse.viatra.cep.vepl.vepl.QueryResultChangeType
   * @see org.eclipse.viatra.cep.vepl.vepl.impl.VeplPackageImpl#getQueryResultChangeType()
   * @generated
   */
  int QUERY_RESULT_CHANGE_TYPE = 38;


  /**
   * Returns the meta object for class '{@link org.eclipse.viatra.cep.vepl.vepl.EventModel <em>Event Model</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>Event Model</em>'.
   * @see org.eclipse.viatra.cep.vepl.vepl.EventModel
   * @generated
   */
  EClass getEventModel();

  /**
   * Returns the meta object for the attribute '{@link org.eclipse.viatra.cep.vepl.vepl.EventModel#getName <em>Name</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Name</em>'.
   * @see org.eclipse.viatra.cep.vepl.vepl.EventModel#getName()
   * @see #getEventModel()
   * @generated
   */
  EAttribute getEventModel_Name();

  /**
   * Returns the meta object for the containment reference list '{@link org.eclipse.viatra.cep.vepl.vepl.EventModel#getImports <em>Imports</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the containment reference list '<em>Imports</em>'.
   * @see org.eclipse.viatra.cep.vepl.vepl.EventModel#getImports()
   * @see #getEventModel()
   * @generated
   */
  EReference getEventModel_Imports();

  /**
   * Returns the meta object for the attribute '{@link org.eclipse.viatra.cep.vepl.vepl.EventModel#getContext <em>Context</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Context</em>'.
   * @see org.eclipse.viatra.cep.vepl.vepl.EventModel#getContext()
   * @see #getEventModel()
   * @generated
   */
  EAttribute getEventModel_Context();

  /**
   * Returns the meta object for the containment reference list '{@link org.eclipse.viatra.cep.vepl.vepl.EventModel#getModelElements <em>Model Elements</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the containment reference list '<em>Model Elements</em>'.
   * @see org.eclipse.viatra.cep.vepl.vepl.EventModel#getModelElements()
   * @see #getEventModel()
   * @generated
   */
  EReference getEventModel_ModelElements();

  /**
   * Returns the meta object for class '{@link org.eclipse.viatra.cep.vepl.vepl.Import <em>Import</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>Import</em>'.
   * @see org.eclipse.viatra.cep.vepl.vepl.Import
   * @generated
   */
  EClass getImport();

  /**
   * Returns the meta object for the attribute '{@link org.eclipse.viatra.cep.vepl.vepl.Import#getImportedNamespace <em>Imported Namespace</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Imported Namespace</em>'.
   * @see org.eclipse.viatra.cep.vepl.vepl.Import#getImportedNamespace()
   * @see #getImport()
   * @generated
   */
  EAttribute getImport_ImportedNamespace();

  /**
   * Returns the meta object for class '{@link org.eclipse.viatra.cep.vepl.vepl.GenericImport <em>Generic Import</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>Generic Import</em>'.
   * @see org.eclipse.viatra.cep.vepl.vepl.GenericImport
   * @generated
   */
  EClass getGenericImport();

  /**
   * Returns the meta object for class '{@link org.eclipse.viatra.cep.vepl.vepl.QueryImport <em>Query Import</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>Query Import</em>'.
   * @see org.eclipse.viatra.cep.vepl.vepl.QueryImport
   * @generated
   */
  EClass getQueryImport();

  /**
   * Returns the meta object for class '{@link org.eclipse.viatra.cep.vepl.vepl.ModelElement <em>Model Element</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>Model Element</em>'.
   * @see org.eclipse.viatra.cep.vepl.vepl.ModelElement
   * @generated
   */
  EClass getModelElement();

  /**
   * Returns the meta object for the attribute '{@link org.eclipse.viatra.cep.vepl.vepl.ModelElement#getName <em>Name</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Name</em>'.
   * @see org.eclipse.viatra.cep.vepl.vepl.ModelElement#getName()
   * @see #getModelElement()
   * @generated
   */
  EAttribute getModelElement_Name();

  /**
   * Returns the meta object for class '{@link org.eclipse.viatra.cep.vepl.vepl.Trait <em>Trait</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>Trait</em>'.
   * @see org.eclipse.viatra.cep.vepl.vepl.Trait
   * @generated
   */
  EClass getTrait();

  /**
   * Returns the meta object for the containment reference '{@link org.eclipse.viatra.cep.vepl.vepl.Trait#getParameters <em>Parameters</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the containment reference '<em>Parameters</em>'.
   * @see org.eclipse.viatra.cep.vepl.vepl.Trait#getParameters()
   * @see #getTrait()
   * @generated
   */
  EReference getTrait_Parameters();

  /**
   * Returns the meta object for class '{@link org.eclipse.viatra.cep.vepl.vepl.EventPattern <em>Event Pattern</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>Event Pattern</em>'.
   * @see org.eclipse.viatra.cep.vepl.vepl.EventPattern
   * @generated
   */
  EClass getEventPattern();

  /**
   * Returns the meta object for the containment reference '{@link org.eclipse.viatra.cep.vepl.vepl.EventPattern#getParameters <em>Parameters</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the containment reference '<em>Parameters</em>'.
   * @see org.eclipse.viatra.cep.vepl.vepl.EventPattern#getParameters()
   * @see #getEventPattern()
   * @generated
   */
  EReference getEventPattern_Parameters();

  /**
   * Returns the meta object for class '{@link org.eclipse.viatra.cep.vepl.vepl.AbstractAtomicEventPattern <em>Abstract Atomic Event Pattern</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>Abstract Atomic Event Pattern</em>'.
   * @see org.eclipse.viatra.cep.vepl.vepl.AbstractAtomicEventPattern
   * @generated
   */
  EClass getAbstractAtomicEventPattern();

  /**
   * Returns the meta object for class '{@link org.eclipse.viatra.cep.vepl.vepl.AtomicEventPattern <em>Atomic Event Pattern</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>Atomic Event Pattern</em>'.
   * @see org.eclipse.viatra.cep.vepl.vepl.AtomicEventPattern
   * @generated
   */
  EClass getAtomicEventPattern();

  /**
   * Returns the meta object for the containment reference '{@link org.eclipse.viatra.cep.vepl.vepl.AtomicEventPattern#getTraits <em>Traits</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the containment reference '<em>Traits</em>'.
   * @see org.eclipse.viatra.cep.vepl.vepl.AtomicEventPattern#getTraits()
   * @see #getAtomicEventPattern()
   * @generated
   */
  EReference getAtomicEventPattern_Traits();

  /**
   * Returns the meta object for the containment reference '{@link org.eclipse.viatra.cep.vepl.vepl.AtomicEventPattern#getCheckExpression <em>Check Expression</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the containment reference '<em>Check Expression</em>'.
   * @see org.eclipse.viatra.cep.vepl.vepl.AtomicEventPattern#getCheckExpression()
   * @see #getAtomicEventPattern()
   * @generated
   */
  EReference getAtomicEventPattern_CheckExpression();

  /**
   * Returns the meta object for class '{@link org.eclipse.viatra.cep.vepl.vepl.QueryResultChangeEventPattern <em>Query Result Change Event Pattern</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>Query Result Change Event Pattern</em>'.
   * @see org.eclipse.viatra.cep.vepl.vepl.QueryResultChangeEventPattern
   * @generated
   */
  EClass getQueryResultChangeEventPattern();

  /**
   * Returns the meta object for the containment reference '{@link org.eclipse.viatra.cep.vepl.vepl.QueryResultChangeEventPattern#getQueryReference <em>Query Reference</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the containment reference '<em>Query Reference</em>'.
   * @see org.eclipse.viatra.cep.vepl.vepl.QueryResultChangeEventPattern#getQueryReference()
   * @see #getQueryResultChangeEventPattern()
   * @generated
   */
  EReference getQueryResultChangeEventPattern_QueryReference();

  /**
   * Returns the meta object for the attribute '{@link org.eclipse.viatra.cep.vepl.vepl.QueryResultChangeEventPattern#getResultChangeType <em>Result Change Type</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Result Change Type</em>'.
   * @see org.eclipse.viatra.cep.vepl.vepl.QueryResultChangeEventPattern#getResultChangeType()
   * @see #getQueryResultChangeEventPattern()
   * @generated
   */
  EAttribute getQueryResultChangeEventPattern_ResultChangeType();

  /**
   * Returns the meta object for class '{@link org.eclipse.viatra.cep.vepl.vepl.ComplexEventPattern <em>Complex Event Pattern</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>Complex Event Pattern</em>'.
   * @see org.eclipse.viatra.cep.vepl.vepl.ComplexEventPattern
   * @generated
   */
  EClass getComplexEventPattern();

  /**
   * Returns the meta object for the containment reference '{@link org.eclipse.viatra.cep.vepl.vepl.ComplexEventPattern#getComplexEventExpression <em>Complex Event Expression</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the containment reference '<em>Complex Event Expression</em>'.
   * @see org.eclipse.viatra.cep.vepl.vepl.ComplexEventPattern#getComplexEventExpression()
   * @see #getComplexEventPattern()
   * @generated
   */
  EReference getComplexEventPattern_ComplexEventExpression();

  /**
   * Returns the meta object for the attribute '{@link org.eclipse.viatra.cep.vepl.vepl.ComplexEventPattern#getContext <em>Context</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Context</em>'.
   * @see org.eclipse.viatra.cep.vepl.vepl.ComplexEventPattern#getContext()
   * @see #getComplexEventPattern()
   * @generated
   */
  EAttribute getComplexEventPattern_Context();

  /**
   * Returns the meta object for class '{@link org.eclipse.viatra.cep.vepl.vepl.Rule <em>Rule</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>Rule</em>'.
   * @see org.eclipse.viatra.cep.vepl.vepl.Rule
   * @generated
   */
  EClass getRule();

  /**
   * Returns the meta object for the containment reference list '{@link org.eclipse.viatra.cep.vepl.vepl.Rule#getEventPatterns <em>Event Patterns</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the containment reference list '<em>Event Patterns</em>'.
   * @see org.eclipse.viatra.cep.vepl.vepl.Rule#getEventPatterns()
   * @see #getRule()
   * @generated
   */
  EReference getRule_EventPatterns();

  /**
   * Returns the meta object for the containment reference '{@link org.eclipse.viatra.cep.vepl.vepl.Rule#getAction <em>Action</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the containment reference '<em>Action</em>'.
   * @see org.eclipse.viatra.cep.vepl.vepl.Rule#getAction()
   * @see #getRule()
   * @generated
   */
  EReference getRule_Action();

  /**
   * Returns the meta object for class '{@link org.eclipse.viatra.cep.vepl.vepl.TraitList <em>Trait List</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>Trait List</em>'.
   * @see org.eclipse.viatra.cep.vepl.vepl.TraitList
   * @generated
   */
  EClass getTraitList();

  /**
   * Returns the meta object for the reference list '{@link org.eclipse.viatra.cep.vepl.vepl.TraitList#getTraits <em>Traits</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the reference list '<em>Traits</em>'.
   * @see org.eclipse.viatra.cep.vepl.vepl.TraitList#getTraits()
   * @see #getTraitList()
   * @generated
   */
  EReference getTraitList_Traits();

  /**
   * Returns the meta object for class '{@link org.eclipse.viatra.cep.vepl.vepl.TypedParameterList <em>Typed Parameter List</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>Typed Parameter List</em>'.
   * @see org.eclipse.viatra.cep.vepl.vepl.TypedParameterList
   * @generated
   */
  EClass getTypedParameterList();

  /**
   * Returns the meta object for the containment reference list '{@link org.eclipse.viatra.cep.vepl.vepl.TypedParameterList#getParameters <em>Parameters</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the containment reference list '<em>Parameters</em>'.
   * @see org.eclipse.viatra.cep.vepl.vepl.TypedParameterList#getParameters()
   * @see #getTypedParameterList()
   * @generated
   */
  EReference getTypedParameterList_Parameters();

  /**
   * Returns the meta object for class '{@link org.eclipse.viatra.cep.vepl.vepl.TypedParameter <em>Typed Parameter</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>Typed Parameter</em>'.
   * @see org.eclipse.viatra.cep.vepl.vepl.TypedParameter
   * @generated
   */
  EClass getTypedParameter();

  /**
   * Returns the meta object for the attribute '{@link org.eclipse.viatra.cep.vepl.vepl.TypedParameter#getName <em>Name</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Name</em>'.
   * @see org.eclipse.viatra.cep.vepl.vepl.TypedParameter#getName()
   * @see #getTypedParameter()
   * @generated
   */
  EAttribute getTypedParameter_Name();

  /**
   * Returns the meta object for the containment reference '{@link org.eclipse.viatra.cep.vepl.vepl.TypedParameter#getType <em>Type</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the containment reference '<em>Type</em>'.
   * @see org.eclipse.viatra.cep.vepl.vepl.TypedParameter#getType()
   * @see #getTypedParameter()
   * @generated
   */
  EReference getTypedParameter_Type();

  /**
   * Returns the meta object for class '{@link org.eclipse.viatra.cep.vepl.vepl.TypedParameterWithDefaultValue <em>Typed Parameter With Default Value</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>Typed Parameter With Default Value</em>'.
   * @see org.eclipse.viatra.cep.vepl.vepl.TypedParameterWithDefaultValue
   * @generated
   */
  EClass getTypedParameterWithDefaultValue();

  /**
   * Returns the meta object for the containment reference '{@link org.eclipse.viatra.cep.vepl.vepl.TypedParameterWithDefaultValue#getTypedParameter <em>Typed Parameter</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the containment reference '<em>Typed Parameter</em>'.
   * @see org.eclipse.viatra.cep.vepl.vepl.TypedParameterWithDefaultValue#getTypedParameter()
   * @see #getTypedParameterWithDefaultValue()
   * @generated
   */
  EReference getTypedParameterWithDefaultValue_TypedParameter();

  /**
   * Returns the meta object for the containment reference '{@link org.eclipse.viatra.cep.vepl.vepl.TypedParameterWithDefaultValue#getValue <em>Value</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the containment reference '<em>Value</em>'.
   * @see org.eclipse.viatra.cep.vepl.vepl.TypedParameterWithDefaultValue#getValue()
   * @see #getTypedParameterWithDefaultValue()
   * @generated
   */
  EReference getTypedParameterWithDefaultValue_Value();

  /**
   * Returns the meta object for class '{@link org.eclipse.viatra.cep.vepl.vepl.TraitTypedParameterList <em>Trait Typed Parameter List</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>Trait Typed Parameter List</em>'.
   * @see org.eclipse.viatra.cep.vepl.vepl.TraitTypedParameterList
   * @generated
   */
  EClass getTraitTypedParameterList();

  /**
   * Returns the meta object for the containment reference list '{@link org.eclipse.viatra.cep.vepl.vepl.TraitTypedParameterList#getParameters <em>Parameters</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the containment reference list '<em>Parameters</em>'.
   * @see org.eclipse.viatra.cep.vepl.vepl.TraitTypedParameterList#getParameters()
   * @see #getTraitTypedParameterList()
   * @generated
   */
  EReference getTraitTypedParameterList_Parameters();

  /**
   * Returns the meta object for class '{@link org.eclipse.viatra.cep.vepl.vepl.ParametrizedQueryReference <em>Parametrized Query Reference</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>Parametrized Query Reference</em>'.
   * @see org.eclipse.viatra.cep.vepl.vepl.ParametrizedQueryReference
   * @generated
   */
  EClass getParametrizedQueryReference();

  /**
   * Returns the meta object for the reference '{@link org.eclipse.viatra.cep.vepl.vepl.ParametrizedQueryReference#getQuery <em>Query</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the reference '<em>Query</em>'.
   * @see org.eclipse.viatra.cep.vepl.vepl.ParametrizedQueryReference#getQuery()
   * @see #getParametrizedQueryReference()
   * @generated
   */
  EReference getParametrizedQueryReference_Query();

  /**
   * Returns the meta object for the containment reference '{@link org.eclipse.viatra.cep.vepl.vepl.ParametrizedQueryReference#getParameterList <em>Parameter List</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the containment reference '<em>Parameter List</em>'.
   * @see org.eclipse.viatra.cep.vepl.vepl.ParametrizedQueryReference#getParameterList()
   * @see #getParametrizedQueryReference()
   * @generated
   */
  EReference getParametrizedQueryReference_ParameterList();

  /**
   * Returns the meta object for class '{@link org.eclipse.viatra.cep.vepl.vepl.ComplexEventExpression <em>Complex Event Expression</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>Complex Event Expression</em>'.
   * @see org.eclipse.viatra.cep.vepl.vepl.ComplexEventExpression
   * @generated
   */
  EClass getComplexEventExpression();

  /**
   * Returns the meta object for the containment reference '{@link org.eclipse.viatra.cep.vepl.vepl.ComplexEventExpression#getLeft <em>Left</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the containment reference '<em>Left</em>'.
   * @see org.eclipse.viatra.cep.vepl.vepl.ComplexEventExpression#getLeft()
   * @see #getComplexEventExpression()
   * @generated
   */
  EReference getComplexEventExpression_Left();

  /**
   * Returns the meta object for the containment reference list '{@link org.eclipse.viatra.cep.vepl.vepl.ComplexEventExpression#getRight <em>Right</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the containment reference list '<em>Right</em>'.
   * @see org.eclipse.viatra.cep.vepl.vepl.ComplexEventExpression#getRight()
   * @see #getComplexEventExpression()
   * @generated
   */
  EReference getComplexEventExpression_Right();

  /**
   * Returns the meta object for the containment reference '{@link org.eclipse.viatra.cep.vepl.vepl.ComplexEventExpression#getMultiplicity <em>Multiplicity</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the containment reference '<em>Multiplicity</em>'.
   * @see org.eclipse.viatra.cep.vepl.vepl.ComplexEventExpression#getMultiplicity()
   * @see #getComplexEventExpression()
   * @generated
   */
  EReference getComplexEventExpression_Multiplicity();

  /**
   * Returns the meta object for the containment reference '{@link org.eclipse.viatra.cep.vepl.vepl.ComplexEventExpression#getTimewindow <em>Timewindow</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the containment reference '<em>Timewindow</em>'.
   * @see org.eclipse.viatra.cep.vepl.vepl.ComplexEventExpression#getTimewindow()
   * @see #getComplexEventExpression()
   * @generated
   */
  EReference getComplexEventExpression_Timewindow();

  /**
   * Returns the meta object for the containment reference '{@link org.eclipse.viatra.cep.vepl.vepl.ComplexEventExpression#getNegOperator <em>Neg Operator</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the containment reference '<em>Neg Operator</em>'.
   * @see org.eclipse.viatra.cep.vepl.vepl.ComplexEventExpression#getNegOperator()
   * @see #getComplexEventExpression()
   * @generated
   */
  EReference getComplexEventExpression_NegOperator();

  /**
   * Returns the meta object for class '{@link org.eclipse.viatra.cep.vepl.vepl.ChainedExpression <em>Chained Expression</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>Chained Expression</em>'.
   * @see org.eclipse.viatra.cep.vepl.vepl.ChainedExpression
   * @generated
   */
  EClass getChainedExpression();

  /**
   * Returns the meta object for the containment reference '{@link org.eclipse.viatra.cep.vepl.vepl.ChainedExpression#getOperator <em>Operator</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the containment reference '<em>Operator</em>'.
   * @see org.eclipse.viatra.cep.vepl.vepl.ChainedExpression#getOperator()
   * @see #getChainedExpression()
   * @generated
   */
  EReference getChainedExpression_Operator();

  /**
   * Returns the meta object for the containment reference '{@link org.eclipse.viatra.cep.vepl.vepl.ChainedExpression#getExpression <em>Expression</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the containment reference '<em>Expression</em>'.
   * @see org.eclipse.viatra.cep.vepl.vepl.ChainedExpression#getExpression()
   * @see #getChainedExpression()
   * @generated
   */
  EReference getChainedExpression_Expression();

  /**
   * Returns the meta object for class '{@link org.eclipse.viatra.cep.vepl.vepl.Atom <em>Atom</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>Atom</em>'.
   * @see org.eclipse.viatra.cep.vepl.vepl.Atom
   * @generated
   */
  EClass getAtom();

  /**
   * Returns the meta object for the containment reference '{@link org.eclipse.viatra.cep.vepl.vepl.Atom#getPatternCall <em>Pattern Call</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the containment reference '<em>Pattern Call</em>'.
   * @see org.eclipse.viatra.cep.vepl.vepl.Atom#getPatternCall()
   * @see #getAtom()
   * @generated
   */
  EReference getAtom_PatternCall();

  /**
   * Returns the meta object for class '{@link org.eclipse.viatra.cep.vepl.vepl.AbstractMultiplicity <em>Abstract Multiplicity</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>Abstract Multiplicity</em>'.
   * @see org.eclipse.viatra.cep.vepl.vepl.AbstractMultiplicity
   * @generated
   */
  EClass getAbstractMultiplicity();

  /**
   * Returns the meta object for class '{@link org.eclipse.viatra.cep.vepl.vepl.Timewindow <em>Timewindow</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>Timewindow</em>'.
   * @see org.eclipse.viatra.cep.vepl.vepl.Timewindow
   * @generated
   */
  EClass getTimewindow();

  /**
   * Returns the meta object for the attribute '{@link org.eclipse.viatra.cep.vepl.vepl.Timewindow#getLength <em>Length</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Length</em>'.
   * @see org.eclipse.viatra.cep.vepl.vepl.Timewindow#getLength()
   * @see #getTimewindow()
   * @generated
   */
  EAttribute getTimewindow_Length();

  /**
   * Returns the meta object for class '{@link org.eclipse.viatra.cep.vepl.vepl.ParameterizedPatternCall <em>Parameterized Pattern Call</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>Parameterized Pattern Call</em>'.
   * @see org.eclipse.viatra.cep.vepl.vepl.ParameterizedPatternCall
   * @generated
   */
  EClass getParameterizedPatternCall();

  /**
   * Returns the meta object for the reference '{@link org.eclipse.viatra.cep.vepl.vepl.ParameterizedPatternCall#getEventPattern <em>Event Pattern</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the reference '<em>Event Pattern</em>'.
   * @see org.eclipse.viatra.cep.vepl.vepl.ParameterizedPatternCall#getEventPattern()
   * @see #getParameterizedPatternCall()
   * @generated
   */
  EReference getParameterizedPatternCall_EventPattern();

  /**
   * Returns the meta object for the containment reference '{@link org.eclipse.viatra.cep.vepl.vepl.ParameterizedPatternCall#getParameterList <em>Parameter List</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the containment reference '<em>Parameter List</em>'.
   * @see org.eclipse.viatra.cep.vepl.vepl.ParameterizedPatternCall#getParameterList()
   * @see #getParameterizedPatternCall()
   * @generated
   */
  EReference getParameterizedPatternCall_ParameterList();

  /**
   * Returns the meta object for class '{@link org.eclipse.viatra.cep.vepl.vepl.PatternCallParameterList <em>Pattern Call Parameter List</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>Pattern Call Parameter List</em>'.
   * @see org.eclipse.viatra.cep.vepl.vepl.PatternCallParameterList
   * @generated
   */
  EClass getPatternCallParameterList();

  /**
   * Returns the meta object for the containment reference list '{@link org.eclipse.viatra.cep.vepl.vepl.PatternCallParameterList#getParameters <em>Parameters</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the containment reference list '<em>Parameters</em>'.
   * @see org.eclipse.viatra.cep.vepl.vepl.PatternCallParameterList#getParameters()
   * @see #getPatternCallParameterList()
   * @generated
   */
  EReference getPatternCallParameterList_Parameters();

  /**
   * Returns the meta object for class '{@link org.eclipse.viatra.cep.vepl.vepl.PatternCallParameter <em>Pattern Call Parameter</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>Pattern Call Parameter</em>'.
   * @see org.eclipse.viatra.cep.vepl.vepl.PatternCallParameter
   * @generated
   */
  EClass getPatternCallParameter();

  /**
   * Returns the meta object for the attribute '{@link org.eclipse.viatra.cep.vepl.vepl.PatternCallParameter#getName <em>Name</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Name</em>'.
   * @see org.eclipse.viatra.cep.vepl.vepl.PatternCallParameter#getName()
   * @see #getPatternCallParameter()
   * @generated
   */
  EAttribute getPatternCallParameter_Name();

  /**
   * Returns the meta object for class '{@link org.eclipse.viatra.cep.vepl.vepl.ComplexEventOperator <em>Complex Event Operator</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>Complex Event Operator</em>'.
   * @see org.eclipse.viatra.cep.vepl.vepl.ComplexEventOperator
   * @generated
   */
  EClass getComplexEventOperator();

  /**
   * Returns the meta object for class '{@link org.eclipse.viatra.cep.vepl.vepl.BinaryOperator <em>Binary Operator</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>Binary Operator</em>'.
   * @see org.eclipse.viatra.cep.vepl.vepl.BinaryOperator
   * @generated
   */
  EClass getBinaryOperator();

  /**
   * Returns the meta object for class '{@link org.eclipse.viatra.cep.vepl.vepl.UnaryOperator <em>Unary Operator</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>Unary Operator</em>'.
   * @see org.eclipse.viatra.cep.vepl.vepl.UnaryOperator
   * @generated
   */
  EClass getUnaryOperator();

  /**
   * Returns the meta object for class '{@link org.eclipse.viatra.cep.vepl.vepl.FollowsOperator <em>Follows Operator</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>Follows Operator</em>'.
   * @see org.eclipse.viatra.cep.vepl.vepl.FollowsOperator
   * @generated
   */
  EClass getFollowsOperator();

  /**
   * Returns the meta object for class '{@link org.eclipse.viatra.cep.vepl.vepl.OrOperator <em>Or Operator</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>Or Operator</em>'.
   * @see org.eclipse.viatra.cep.vepl.vepl.OrOperator
   * @generated
   */
  EClass getOrOperator();

  /**
   * Returns the meta object for class '{@link org.eclipse.viatra.cep.vepl.vepl.AndOperator <em>And Operator</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>And Operator</em>'.
   * @see org.eclipse.viatra.cep.vepl.vepl.AndOperator
   * @generated
   */
  EClass getAndOperator();

  /**
   * Returns the meta object for class '{@link org.eclipse.viatra.cep.vepl.vepl.UntilOperator <em>Until Operator</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>Until Operator</em>'.
   * @see org.eclipse.viatra.cep.vepl.vepl.UntilOperator
   * @generated
   */
  EClass getUntilOperator();

  /**
   * Returns the meta object for class '{@link org.eclipse.viatra.cep.vepl.vepl.NegOperator <em>Neg Operator</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>Neg Operator</em>'.
   * @see org.eclipse.viatra.cep.vepl.vepl.NegOperator
   * @generated
   */
  EClass getNegOperator();

  /**
   * Returns the meta object for class '{@link org.eclipse.viatra.cep.vepl.vepl.Multiplicity <em>Multiplicity</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>Multiplicity</em>'.
   * @see org.eclipse.viatra.cep.vepl.vepl.Multiplicity
   * @generated
   */
  EClass getMultiplicity();

  /**
   * Returns the meta object for the attribute '{@link org.eclipse.viatra.cep.vepl.vepl.Multiplicity#getValue <em>Value</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Value</em>'.
   * @see org.eclipse.viatra.cep.vepl.vepl.Multiplicity#getValue()
   * @see #getMultiplicity()
   * @generated
   */
  EAttribute getMultiplicity_Value();

  /**
   * Returns the meta object for class '{@link org.eclipse.viatra.cep.vepl.vepl.Infinite <em>Infinite</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>Infinite</em>'.
   * @see org.eclipse.viatra.cep.vepl.vepl.Infinite
   * @generated
   */
  EClass getInfinite();

  /**
   * Returns the meta object for class '{@link org.eclipse.viatra.cep.vepl.vepl.AtLeastOne <em>At Least One</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>At Least One</em>'.
   * @see org.eclipse.viatra.cep.vepl.vepl.AtLeastOne
   * @generated
   */
  EClass getAtLeastOne();

  /**
   * Returns the meta object for enum '{@link org.eclipse.viatra.cep.vepl.vepl.ContextEnum <em>Context Enum</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for enum '<em>Context Enum</em>'.
   * @see org.eclipse.viatra.cep.vepl.vepl.ContextEnum
   * @generated
   */
  EEnum getContextEnum();

  /**
   * Returns the meta object for enum '{@link org.eclipse.viatra.cep.vepl.vepl.QueryResultChangeType <em>Query Result Change Type</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for enum '<em>Query Result Change Type</em>'.
   * @see org.eclipse.viatra.cep.vepl.vepl.QueryResultChangeType
   * @generated
   */
  EEnum getQueryResultChangeType();

  /**
   * Returns the factory that creates the instances of the model.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the factory that creates the instances of the model.
   * @generated
   */
  VeplFactory getVeplFactory();

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
  interface Literals
  {
    /**
     * The meta object literal for the '{@link org.eclipse.viatra.cep.vepl.vepl.impl.EventModelImpl <em>Event Model</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.eclipse.viatra.cep.vepl.vepl.impl.EventModelImpl
     * @see org.eclipse.viatra.cep.vepl.vepl.impl.VeplPackageImpl#getEventModel()
     * @generated
     */
    EClass EVENT_MODEL = eINSTANCE.getEventModel();

    /**
     * The meta object literal for the '<em><b>Name</b></em>' attribute feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EAttribute EVENT_MODEL__NAME = eINSTANCE.getEventModel_Name();

    /**
     * The meta object literal for the '<em><b>Imports</b></em>' containment reference list feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference EVENT_MODEL__IMPORTS = eINSTANCE.getEventModel_Imports();

    /**
     * The meta object literal for the '<em><b>Context</b></em>' attribute feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EAttribute EVENT_MODEL__CONTEXT = eINSTANCE.getEventModel_Context();

    /**
     * The meta object literal for the '<em><b>Model Elements</b></em>' containment reference list feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference EVENT_MODEL__MODEL_ELEMENTS = eINSTANCE.getEventModel_ModelElements();

    /**
     * The meta object literal for the '{@link org.eclipse.viatra.cep.vepl.vepl.impl.ImportImpl <em>Import</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.eclipse.viatra.cep.vepl.vepl.impl.ImportImpl
     * @see org.eclipse.viatra.cep.vepl.vepl.impl.VeplPackageImpl#getImport()
     * @generated
     */
    EClass IMPORT = eINSTANCE.getImport();

    /**
     * The meta object literal for the '<em><b>Imported Namespace</b></em>' attribute feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EAttribute IMPORT__IMPORTED_NAMESPACE = eINSTANCE.getImport_ImportedNamespace();

    /**
     * The meta object literal for the '{@link org.eclipse.viatra.cep.vepl.vepl.impl.GenericImportImpl <em>Generic Import</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.eclipse.viatra.cep.vepl.vepl.impl.GenericImportImpl
     * @see org.eclipse.viatra.cep.vepl.vepl.impl.VeplPackageImpl#getGenericImport()
     * @generated
     */
    EClass GENERIC_IMPORT = eINSTANCE.getGenericImport();

    /**
     * The meta object literal for the '{@link org.eclipse.viatra.cep.vepl.vepl.impl.QueryImportImpl <em>Query Import</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.eclipse.viatra.cep.vepl.vepl.impl.QueryImportImpl
     * @see org.eclipse.viatra.cep.vepl.vepl.impl.VeplPackageImpl#getQueryImport()
     * @generated
     */
    EClass QUERY_IMPORT = eINSTANCE.getQueryImport();

    /**
     * The meta object literal for the '{@link org.eclipse.viatra.cep.vepl.vepl.impl.ModelElementImpl <em>Model Element</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.eclipse.viatra.cep.vepl.vepl.impl.ModelElementImpl
     * @see org.eclipse.viatra.cep.vepl.vepl.impl.VeplPackageImpl#getModelElement()
     * @generated
     */
    EClass MODEL_ELEMENT = eINSTANCE.getModelElement();

    /**
     * The meta object literal for the '<em><b>Name</b></em>' attribute feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EAttribute MODEL_ELEMENT__NAME = eINSTANCE.getModelElement_Name();

    /**
     * The meta object literal for the '{@link org.eclipse.viatra.cep.vepl.vepl.impl.TraitImpl <em>Trait</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.eclipse.viatra.cep.vepl.vepl.impl.TraitImpl
     * @see org.eclipse.viatra.cep.vepl.vepl.impl.VeplPackageImpl#getTrait()
     * @generated
     */
    EClass TRAIT = eINSTANCE.getTrait();

    /**
     * The meta object literal for the '<em><b>Parameters</b></em>' containment reference feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference TRAIT__PARAMETERS = eINSTANCE.getTrait_Parameters();

    /**
     * The meta object literal for the '{@link org.eclipse.viatra.cep.vepl.vepl.impl.EventPatternImpl <em>Event Pattern</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.eclipse.viatra.cep.vepl.vepl.impl.EventPatternImpl
     * @see org.eclipse.viatra.cep.vepl.vepl.impl.VeplPackageImpl#getEventPattern()
     * @generated
     */
    EClass EVENT_PATTERN = eINSTANCE.getEventPattern();

    /**
     * The meta object literal for the '<em><b>Parameters</b></em>' containment reference feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference EVENT_PATTERN__PARAMETERS = eINSTANCE.getEventPattern_Parameters();

    /**
     * The meta object literal for the '{@link org.eclipse.viatra.cep.vepl.vepl.impl.AbstractAtomicEventPatternImpl <em>Abstract Atomic Event Pattern</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.eclipse.viatra.cep.vepl.vepl.impl.AbstractAtomicEventPatternImpl
     * @see org.eclipse.viatra.cep.vepl.vepl.impl.VeplPackageImpl#getAbstractAtomicEventPattern()
     * @generated
     */
    EClass ABSTRACT_ATOMIC_EVENT_PATTERN = eINSTANCE.getAbstractAtomicEventPattern();

    /**
     * The meta object literal for the '{@link org.eclipse.viatra.cep.vepl.vepl.impl.AtomicEventPatternImpl <em>Atomic Event Pattern</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.eclipse.viatra.cep.vepl.vepl.impl.AtomicEventPatternImpl
     * @see org.eclipse.viatra.cep.vepl.vepl.impl.VeplPackageImpl#getAtomicEventPattern()
     * @generated
     */
    EClass ATOMIC_EVENT_PATTERN = eINSTANCE.getAtomicEventPattern();

    /**
     * The meta object literal for the '<em><b>Traits</b></em>' containment reference feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference ATOMIC_EVENT_PATTERN__TRAITS = eINSTANCE.getAtomicEventPattern_Traits();

    /**
     * The meta object literal for the '<em><b>Check Expression</b></em>' containment reference feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference ATOMIC_EVENT_PATTERN__CHECK_EXPRESSION = eINSTANCE.getAtomicEventPattern_CheckExpression();

    /**
     * The meta object literal for the '{@link org.eclipse.viatra.cep.vepl.vepl.impl.QueryResultChangeEventPatternImpl <em>Query Result Change Event Pattern</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.eclipse.viatra.cep.vepl.vepl.impl.QueryResultChangeEventPatternImpl
     * @see org.eclipse.viatra.cep.vepl.vepl.impl.VeplPackageImpl#getQueryResultChangeEventPattern()
     * @generated
     */
    EClass QUERY_RESULT_CHANGE_EVENT_PATTERN = eINSTANCE.getQueryResultChangeEventPattern();

    /**
     * The meta object literal for the '<em><b>Query Reference</b></em>' containment reference feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference QUERY_RESULT_CHANGE_EVENT_PATTERN__QUERY_REFERENCE = eINSTANCE.getQueryResultChangeEventPattern_QueryReference();

    /**
     * The meta object literal for the '<em><b>Result Change Type</b></em>' attribute feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EAttribute QUERY_RESULT_CHANGE_EVENT_PATTERN__RESULT_CHANGE_TYPE = eINSTANCE.getQueryResultChangeEventPattern_ResultChangeType();

    /**
     * The meta object literal for the '{@link org.eclipse.viatra.cep.vepl.vepl.impl.ComplexEventPatternImpl <em>Complex Event Pattern</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.eclipse.viatra.cep.vepl.vepl.impl.ComplexEventPatternImpl
     * @see org.eclipse.viatra.cep.vepl.vepl.impl.VeplPackageImpl#getComplexEventPattern()
     * @generated
     */
    EClass COMPLEX_EVENT_PATTERN = eINSTANCE.getComplexEventPattern();

    /**
     * The meta object literal for the '<em><b>Complex Event Expression</b></em>' containment reference feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference COMPLEX_EVENT_PATTERN__COMPLEX_EVENT_EXPRESSION = eINSTANCE.getComplexEventPattern_ComplexEventExpression();

    /**
     * The meta object literal for the '<em><b>Context</b></em>' attribute feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EAttribute COMPLEX_EVENT_PATTERN__CONTEXT = eINSTANCE.getComplexEventPattern_Context();

    /**
     * The meta object literal for the '{@link org.eclipse.viatra.cep.vepl.vepl.impl.RuleImpl <em>Rule</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.eclipse.viatra.cep.vepl.vepl.impl.RuleImpl
     * @see org.eclipse.viatra.cep.vepl.vepl.impl.VeplPackageImpl#getRule()
     * @generated
     */
    EClass RULE = eINSTANCE.getRule();

    /**
     * The meta object literal for the '<em><b>Event Patterns</b></em>' containment reference list feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference RULE__EVENT_PATTERNS = eINSTANCE.getRule_EventPatterns();

    /**
     * The meta object literal for the '<em><b>Action</b></em>' containment reference feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference RULE__ACTION = eINSTANCE.getRule_Action();

    /**
     * The meta object literal for the '{@link org.eclipse.viatra.cep.vepl.vepl.impl.TraitListImpl <em>Trait List</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.eclipse.viatra.cep.vepl.vepl.impl.TraitListImpl
     * @see org.eclipse.viatra.cep.vepl.vepl.impl.VeplPackageImpl#getTraitList()
     * @generated
     */
    EClass TRAIT_LIST = eINSTANCE.getTraitList();

    /**
     * The meta object literal for the '<em><b>Traits</b></em>' reference list feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference TRAIT_LIST__TRAITS = eINSTANCE.getTraitList_Traits();

    /**
     * The meta object literal for the '{@link org.eclipse.viatra.cep.vepl.vepl.impl.TypedParameterListImpl <em>Typed Parameter List</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.eclipse.viatra.cep.vepl.vepl.impl.TypedParameterListImpl
     * @see org.eclipse.viatra.cep.vepl.vepl.impl.VeplPackageImpl#getTypedParameterList()
     * @generated
     */
    EClass TYPED_PARAMETER_LIST = eINSTANCE.getTypedParameterList();

    /**
     * The meta object literal for the '<em><b>Parameters</b></em>' containment reference list feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference TYPED_PARAMETER_LIST__PARAMETERS = eINSTANCE.getTypedParameterList_Parameters();

    /**
     * The meta object literal for the '{@link org.eclipse.viatra.cep.vepl.vepl.impl.TypedParameterImpl <em>Typed Parameter</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.eclipse.viatra.cep.vepl.vepl.impl.TypedParameterImpl
     * @see org.eclipse.viatra.cep.vepl.vepl.impl.VeplPackageImpl#getTypedParameter()
     * @generated
     */
    EClass TYPED_PARAMETER = eINSTANCE.getTypedParameter();

    /**
     * The meta object literal for the '<em><b>Name</b></em>' attribute feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EAttribute TYPED_PARAMETER__NAME = eINSTANCE.getTypedParameter_Name();

    /**
     * The meta object literal for the '<em><b>Type</b></em>' containment reference feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference TYPED_PARAMETER__TYPE = eINSTANCE.getTypedParameter_Type();

    /**
     * The meta object literal for the '{@link org.eclipse.viatra.cep.vepl.vepl.impl.TypedParameterWithDefaultValueImpl <em>Typed Parameter With Default Value</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.eclipse.viatra.cep.vepl.vepl.impl.TypedParameterWithDefaultValueImpl
     * @see org.eclipse.viatra.cep.vepl.vepl.impl.VeplPackageImpl#getTypedParameterWithDefaultValue()
     * @generated
     */
    EClass TYPED_PARAMETER_WITH_DEFAULT_VALUE = eINSTANCE.getTypedParameterWithDefaultValue();

    /**
     * The meta object literal for the '<em><b>Typed Parameter</b></em>' containment reference feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference TYPED_PARAMETER_WITH_DEFAULT_VALUE__TYPED_PARAMETER = eINSTANCE.getTypedParameterWithDefaultValue_TypedParameter();

    /**
     * The meta object literal for the '<em><b>Value</b></em>' containment reference feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference TYPED_PARAMETER_WITH_DEFAULT_VALUE__VALUE = eINSTANCE.getTypedParameterWithDefaultValue_Value();

    /**
     * The meta object literal for the '{@link org.eclipse.viatra.cep.vepl.vepl.impl.TraitTypedParameterListImpl <em>Trait Typed Parameter List</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.eclipse.viatra.cep.vepl.vepl.impl.TraitTypedParameterListImpl
     * @see org.eclipse.viatra.cep.vepl.vepl.impl.VeplPackageImpl#getTraitTypedParameterList()
     * @generated
     */
    EClass TRAIT_TYPED_PARAMETER_LIST = eINSTANCE.getTraitTypedParameterList();

    /**
     * The meta object literal for the '<em><b>Parameters</b></em>' containment reference list feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference TRAIT_TYPED_PARAMETER_LIST__PARAMETERS = eINSTANCE.getTraitTypedParameterList_Parameters();

    /**
     * The meta object literal for the '{@link org.eclipse.viatra.cep.vepl.vepl.impl.ParametrizedQueryReferenceImpl <em>Parametrized Query Reference</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.eclipse.viatra.cep.vepl.vepl.impl.ParametrizedQueryReferenceImpl
     * @see org.eclipse.viatra.cep.vepl.vepl.impl.VeplPackageImpl#getParametrizedQueryReference()
     * @generated
     */
    EClass PARAMETRIZED_QUERY_REFERENCE = eINSTANCE.getParametrizedQueryReference();

    /**
     * The meta object literal for the '<em><b>Query</b></em>' reference feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference PARAMETRIZED_QUERY_REFERENCE__QUERY = eINSTANCE.getParametrizedQueryReference_Query();

    /**
     * The meta object literal for the '<em><b>Parameter List</b></em>' containment reference feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference PARAMETRIZED_QUERY_REFERENCE__PARAMETER_LIST = eINSTANCE.getParametrizedQueryReference_ParameterList();

    /**
     * The meta object literal for the '{@link org.eclipse.viatra.cep.vepl.vepl.impl.ComplexEventExpressionImpl <em>Complex Event Expression</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.eclipse.viatra.cep.vepl.vepl.impl.ComplexEventExpressionImpl
     * @see org.eclipse.viatra.cep.vepl.vepl.impl.VeplPackageImpl#getComplexEventExpression()
     * @generated
     */
    EClass COMPLEX_EVENT_EXPRESSION = eINSTANCE.getComplexEventExpression();

    /**
     * The meta object literal for the '<em><b>Left</b></em>' containment reference feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference COMPLEX_EVENT_EXPRESSION__LEFT = eINSTANCE.getComplexEventExpression_Left();

    /**
     * The meta object literal for the '<em><b>Right</b></em>' containment reference list feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference COMPLEX_EVENT_EXPRESSION__RIGHT = eINSTANCE.getComplexEventExpression_Right();

    /**
     * The meta object literal for the '<em><b>Multiplicity</b></em>' containment reference feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference COMPLEX_EVENT_EXPRESSION__MULTIPLICITY = eINSTANCE.getComplexEventExpression_Multiplicity();

    /**
     * The meta object literal for the '<em><b>Timewindow</b></em>' containment reference feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference COMPLEX_EVENT_EXPRESSION__TIMEWINDOW = eINSTANCE.getComplexEventExpression_Timewindow();

    /**
     * The meta object literal for the '<em><b>Neg Operator</b></em>' containment reference feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference COMPLEX_EVENT_EXPRESSION__NEG_OPERATOR = eINSTANCE.getComplexEventExpression_NegOperator();

    /**
     * The meta object literal for the '{@link org.eclipse.viatra.cep.vepl.vepl.impl.ChainedExpressionImpl <em>Chained Expression</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.eclipse.viatra.cep.vepl.vepl.impl.ChainedExpressionImpl
     * @see org.eclipse.viatra.cep.vepl.vepl.impl.VeplPackageImpl#getChainedExpression()
     * @generated
     */
    EClass CHAINED_EXPRESSION = eINSTANCE.getChainedExpression();

    /**
     * The meta object literal for the '<em><b>Operator</b></em>' containment reference feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference CHAINED_EXPRESSION__OPERATOR = eINSTANCE.getChainedExpression_Operator();

    /**
     * The meta object literal for the '<em><b>Expression</b></em>' containment reference feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference CHAINED_EXPRESSION__EXPRESSION = eINSTANCE.getChainedExpression_Expression();

    /**
     * The meta object literal for the '{@link org.eclipse.viatra.cep.vepl.vepl.impl.AtomImpl <em>Atom</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.eclipse.viatra.cep.vepl.vepl.impl.AtomImpl
     * @see org.eclipse.viatra.cep.vepl.vepl.impl.VeplPackageImpl#getAtom()
     * @generated
     */
    EClass ATOM = eINSTANCE.getAtom();

    /**
     * The meta object literal for the '<em><b>Pattern Call</b></em>' containment reference feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference ATOM__PATTERN_CALL = eINSTANCE.getAtom_PatternCall();

    /**
     * The meta object literal for the '{@link org.eclipse.viatra.cep.vepl.vepl.impl.AbstractMultiplicityImpl <em>Abstract Multiplicity</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.eclipse.viatra.cep.vepl.vepl.impl.AbstractMultiplicityImpl
     * @see org.eclipse.viatra.cep.vepl.vepl.impl.VeplPackageImpl#getAbstractMultiplicity()
     * @generated
     */
    EClass ABSTRACT_MULTIPLICITY = eINSTANCE.getAbstractMultiplicity();

    /**
     * The meta object literal for the '{@link org.eclipse.viatra.cep.vepl.vepl.impl.TimewindowImpl <em>Timewindow</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.eclipse.viatra.cep.vepl.vepl.impl.TimewindowImpl
     * @see org.eclipse.viatra.cep.vepl.vepl.impl.VeplPackageImpl#getTimewindow()
     * @generated
     */
    EClass TIMEWINDOW = eINSTANCE.getTimewindow();

    /**
     * The meta object literal for the '<em><b>Length</b></em>' attribute feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EAttribute TIMEWINDOW__LENGTH = eINSTANCE.getTimewindow_Length();

    /**
     * The meta object literal for the '{@link org.eclipse.viatra.cep.vepl.vepl.impl.ParameterizedPatternCallImpl <em>Parameterized Pattern Call</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.eclipse.viatra.cep.vepl.vepl.impl.ParameterizedPatternCallImpl
     * @see org.eclipse.viatra.cep.vepl.vepl.impl.VeplPackageImpl#getParameterizedPatternCall()
     * @generated
     */
    EClass PARAMETERIZED_PATTERN_CALL = eINSTANCE.getParameterizedPatternCall();

    /**
     * The meta object literal for the '<em><b>Event Pattern</b></em>' reference feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference PARAMETERIZED_PATTERN_CALL__EVENT_PATTERN = eINSTANCE.getParameterizedPatternCall_EventPattern();

    /**
     * The meta object literal for the '<em><b>Parameter List</b></em>' containment reference feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference PARAMETERIZED_PATTERN_CALL__PARAMETER_LIST = eINSTANCE.getParameterizedPatternCall_ParameterList();

    /**
     * The meta object literal for the '{@link org.eclipse.viatra.cep.vepl.vepl.impl.PatternCallParameterListImpl <em>Pattern Call Parameter List</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.eclipse.viatra.cep.vepl.vepl.impl.PatternCallParameterListImpl
     * @see org.eclipse.viatra.cep.vepl.vepl.impl.VeplPackageImpl#getPatternCallParameterList()
     * @generated
     */
    EClass PATTERN_CALL_PARAMETER_LIST = eINSTANCE.getPatternCallParameterList();

    /**
     * The meta object literal for the '<em><b>Parameters</b></em>' containment reference list feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference PATTERN_CALL_PARAMETER_LIST__PARAMETERS = eINSTANCE.getPatternCallParameterList_Parameters();

    /**
     * The meta object literal for the '{@link org.eclipse.viatra.cep.vepl.vepl.impl.PatternCallParameterImpl <em>Pattern Call Parameter</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.eclipse.viatra.cep.vepl.vepl.impl.PatternCallParameterImpl
     * @see org.eclipse.viatra.cep.vepl.vepl.impl.VeplPackageImpl#getPatternCallParameter()
     * @generated
     */
    EClass PATTERN_CALL_PARAMETER = eINSTANCE.getPatternCallParameter();

    /**
     * The meta object literal for the '<em><b>Name</b></em>' attribute feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EAttribute PATTERN_CALL_PARAMETER__NAME = eINSTANCE.getPatternCallParameter_Name();

    /**
     * The meta object literal for the '{@link org.eclipse.viatra.cep.vepl.vepl.impl.ComplexEventOperatorImpl <em>Complex Event Operator</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.eclipse.viatra.cep.vepl.vepl.impl.ComplexEventOperatorImpl
     * @see org.eclipse.viatra.cep.vepl.vepl.impl.VeplPackageImpl#getComplexEventOperator()
     * @generated
     */
    EClass COMPLEX_EVENT_OPERATOR = eINSTANCE.getComplexEventOperator();

    /**
     * The meta object literal for the '{@link org.eclipse.viatra.cep.vepl.vepl.impl.BinaryOperatorImpl <em>Binary Operator</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.eclipse.viatra.cep.vepl.vepl.impl.BinaryOperatorImpl
     * @see org.eclipse.viatra.cep.vepl.vepl.impl.VeplPackageImpl#getBinaryOperator()
     * @generated
     */
    EClass BINARY_OPERATOR = eINSTANCE.getBinaryOperator();

    /**
     * The meta object literal for the '{@link org.eclipse.viatra.cep.vepl.vepl.impl.UnaryOperatorImpl <em>Unary Operator</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.eclipse.viatra.cep.vepl.vepl.impl.UnaryOperatorImpl
     * @see org.eclipse.viatra.cep.vepl.vepl.impl.VeplPackageImpl#getUnaryOperator()
     * @generated
     */
    EClass UNARY_OPERATOR = eINSTANCE.getUnaryOperator();

    /**
     * The meta object literal for the '{@link org.eclipse.viatra.cep.vepl.vepl.impl.FollowsOperatorImpl <em>Follows Operator</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.eclipse.viatra.cep.vepl.vepl.impl.FollowsOperatorImpl
     * @see org.eclipse.viatra.cep.vepl.vepl.impl.VeplPackageImpl#getFollowsOperator()
     * @generated
     */
    EClass FOLLOWS_OPERATOR = eINSTANCE.getFollowsOperator();

    /**
     * The meta object literal for the '{@link org.eclipse.viatra.cep.vepl.vepl.impl.OrOperatorImpl <em>Or Operator</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.eclipse.viatra.cep.vepl.vepl.impl.OrOperatorImpl
     * @see org.eclipse.viatra.cep.vepl.vepl.impl.VeplPackageImpl#getOrOperator()
     * @generated
     */
    EClass OR_OPERATOR = eINSTANCE.getOrOperator();

    /**
     * The meta object literal for the '{@link org.eclipse.viatra.cep.vepl.vepl.impl.AndOperatorImpl <em>And Operator</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.eclipse.viatra.cep.vepl.vepl.impl.AndOperatorImpl
     * @see org.eclipse.viatra.cep.vepl.vepl.impl.VeplPackageImpl#getAndOperator()
     * @generated
     */
    EClass AND_OPERATOR = eINSTANCE.getAndOperator();

    /**
     * The meta object literal for the '{@link org.eclipse.viatra.cep.vepl.vepl.impl.UntilOperatorImpl <em>Until Operator</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.eclipse.viatra.cep.vepl.vepl.impl.UntilOperatorImpl
     * @see org.eclipse.viatra.cep.vepl.vepl.impl.VeplPackageImpl#getUntilOperator()
     * @generated
     */
    EClass UNTIL_OPERATOR = eINSTANCE.getUntilOperator();

    /**
     * The meta object literal for the '{@link org.eclipse.viatra.cep.vepl.vepl.impl.NegOperatorImpl <em>Neg Operator</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.eclipse.viatra.cep.vepl.vepl.impl.NegOperatorImpl
     * @see org.eclipse.viatra.cep.vepl.vepl.impl.VeplPackageImpl#getNegOperator()
     * @generated
     */
    EClass NEG_OPERATOR = eINSTANCE.getNegOperator();

    /**
     * The meta object literal for the '{@link org.eclipse.viatra.cep.vepl.vepl.impl.MultiplicityImpl <em>Multiplicity</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.eclipse.viatra.cep.vepl.vepl.impl.MultiplicityImpl
     * @see org.eclipse.viatra.cep.vepl.vepl.impl.VeplPackageImpl#getMultiplicity()
     * @generated
     */
    EClass MULTIPLICITY = eINSTANCE.getMultiplicity();

    /**
     * The meta object literal for the '<em><b>Value</b></em>' attribute feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EAttribute MULTIPLICITY__VALUE = eINSTANCE.getMultiplicity_Value();

    /**
     * The meta object literal for the '{@link org.eclipse.viatra.cep.vepl.vepl.impl.InfiniteImpl <em>Infinite</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.eclipse.viatra.cep.vepl.vepl.impl.InfiniteImpl
     * @see org.eclipse.viatra.cep.vepl.vepl.impl.VeplPackageImpl#getInfinite()
     * @generated
     */
    EClass INFINITE = eINSTANCE.getInfinite();

    /**
     * The meta object literal for the '{@link org.eclipse.viatra.cep.vepl.vepl.impl.AtLeastOneImpl <em>At Least One</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.eclipse.viatra.cep.vepl.vepl.impl.AtLeastOneImpl
     * @see org.eclipse.viatra.cep.vepl.vepl.impl.VeplPackageImpl#getAtLeastOne()
     * @generated
     */
    EClass AT_LEAST_ONE = eINSTANCE.getAtLeastOne();

    /**
     * The meta object literal for the '{@link org.eclipse.viatra.cep.vepl.vepl.ContextEnum <em>Context Enum</em>}' enum.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.eclipse.viatra.cep.vepl.vepl.ContextEnum
     * @see org.eclipse.viatra.cep.vepl.vepl.impl.VeplPackageImpl#getContextEnum()
     * @generated
     */
    EEnum CONTEXT_ENUM = eINSTANCE.getContextEnum();

    /**
     * The meta object literal for the '{@link org.eclipse.viatra.cep.vepl.vepl.QueryResultChangeType <em>Query Result Change Type</em>}' enum.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.eclipse.viatra.cep.vepl.vepl.QueryResultChangeType
     * @see org.eclipse.viatra.cep.vepl.vepl.impl.VeplPackageImpl#getQueryResultChangeType()
     * @generated
     */
    EEnum QUERY_RESULT_CHANGE_TYPE = eINSTANCE.getQueryResultChangeType();

  }

} //VeplPackage
