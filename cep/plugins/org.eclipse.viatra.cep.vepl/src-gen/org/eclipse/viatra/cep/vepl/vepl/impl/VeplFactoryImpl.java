/**
 */
package org.eclipse.viatra.cep.vepl.vepl.impl;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EDataType;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;

import org.eclipse.emf.ecore.impl.EFactoryImpl;

import org.eclipse.emf.ecore.plugin.EcorePlugin;

import org.eclipse.viatra.cep.vepl.vepl.*;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model <b>Factory</b>.
 * <!-- end-user-doc -->
 * @generated
 */
public class VeplFactoryImpl extends EFactoryImpl implements VeplFactory
{
  /**
   * Creates the default factory implementation.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public static VeplFactory init()
  {
    try
    {
      VeplFactory theVeplFactory = (VeplFactory)EPackage.Registry.INSTANCE.getEFactory(VeplPackage.eNS_URI);
      if (theVeplFactory != null)
      {
        return theVeplFactory;
      }
    }
    catch (Exception exception)
    {
      EcorePlugin.INSTANCE.log(exception);
    }
    return new VeplFactoryImpl();
  }

  /**
   * Creates an instance of the factory.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public VeplFactoryImpl()
  {
    super();
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EObject create(EClass eClass)
  {
    switch (eClass.getClassifierID())
    {
      case VeplPackage.EVENT_MODEL: return createEventModel();
      case VeplPackage.IMPORT: return createImport();
      case VeplPackage.GENERIC_IMPORT: return createGenericImport();
      case VeplPackage.QUERY_IMPORT: return createQueryImport();
      case VeplPackage.MODEL_ELEMENT: return createModelElement();
      case VeplPackage.TRAIT: return createTrait();
      case VeplPackage.EVENT_PATTERN: return createEventPattern();
      case VeplPackage.ABSTRACT_ATOMIC_EVENT_PATTERN: return createAbstractAtomicEventPattern();
      case VeplPackage.ATOMIC_EVENT_PATTERN: return createAtomicEventPattern();
      case VeplPackage.QUERY_RESULT_CHANGE_EVENT_PATTERN: return createQueryResultChangeEventPattern();
      case VeplPackage.COMPLEX_EVENT_PATTERN: return createComplexEventPattern();
      case VeplPackage.RULE: return createRule();
      case VeplPackage.TRAIT_LIST: return createTraitList();
      case VeplPackage.TYPED_PARAMETER_LIST: return createTypedParameterList();
      case VeplPackage.TYPED_PARAMETER: return createTypedParameter();
      case VeplPackage.TYPED_PARAMETER_WITH_DEFAULT_VALUE: return createTypedParameterWithDefaultValue();
      case VeplPackage.TRAIT_TYPED_PARAMETER_LIST: return createTraitTypedParameterList();
      case VeplPackage.PARAMETRIZED_QUERY_REFERENCE: return createParametrizedQueryReference();
      case VeplPackage.COMPLEX_EVENT_EXPRESSION: return createComplexEventExpression();
      case VeplPackage.CHAINED_EXPRESSION: return createChainedExpression();
      case VeplPackage.ATOM: return createAtom();
      case VeplPackage.ABSTRACT_MULTIPLICITY: return createAbstractMultiplicity();
      case VeplPackage.TIMEWINDOW: return createTimewindow();
      case VeplPackage.PARAMETERIZED_PATTERN_CALL: return createParameterizedPatternCall();
      case VeplPackage.PATTERN_CALL_PARAMETER_LIST: return createPatternCallParameterList();
      case VeplPackage.PATTERN_CALL_PARAMETER: return createPatternCallParameter();
      case VeplPackage.COMPLEX_EVENT_OPERATOR: return createComplexEventOperator();
      case VeplPackage.BINARY_OPERATOR: return createBinaryOperator();
      case VeplPackage.UNARY_OPERATOR: return createUnaryOperator();
      case VeplPackage.FOLLOWS_OPERATOR: return createFollowsOperator();
      case VeplPackage.OR_OPERATOR: return createOrOperator();
      case VeplPackage.AND_OPERATOR: return createAndOperator();
      case VeplPackage.UNTIL_OPERATOR: return createUntilOperator();
      case VeplPackage.NEG_OPERATOR: return createNegOperator();
      case VeplPackage.MULTIPLICITY: return createMultiplicity();
      case VeplPackage.INFINITE: return createInfinite();
      case VeplPackage.AT_LEAST_ONE: return createAtLeastOne();
      default:
        throw new IllegalArgumentException("The class '" + eClass.getName() + "' is not a valid classifier");
    }
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public Object createFromString(EDataType eDataType, String initialValue)
  {
    switch (eDataType.getClassifierID())
    {
      case VeplPackage.CONTEXT_ENUM:
        return createContextEnumFromString(eDataType, initialValue);
      case VeplPackage.QUERY_RESULT_CHANGE_TYPE:
        return createQueryResultChangeTypeFromString(eDataType, initialValue);
      default:
        throw new IllegalArgumentException("The datatype '" + eDataType.getName() + "' is not a valid classifier");
    }
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public String convertToString(EDataType eDataType, Object instanceValue)
  {
    switch (eDataType.getClassifierID())
    {
      case VeplPackage.CONTEXT_ENUM:
        return convertContextEnumToString(eDataType, instanceValue);
      case VeplPackage.QUERY_RESULT_CHANGE_TYPE:
        return convertQueryResultChangeTypeToString(eDataType, instanceValue);
      default:
        throw new IllegalArgumentException("The datatype '" + eDataType.getName() + "' is not a valid classifier");
    }
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EventModel createEventModel()
  {
    EventModelImpl eventModel = new EventModelImpl();
    return eventModel;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public Import createImport()
  {
    ImportImpl import_ = new ImportImpl();
    return import_;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public GenericImport createGenericImport()
  {
    GenericImportImpl genericImport = new GenericImportImpl();
    return genericImport;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public QueryImport createQueryImport()
  {
    QueryImportImpl queryImport = new QueryImportImpl();
    return queryImport;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public ModelElement createModelElement()
  {
    ModelElementImpl modelElement = new ModelElementImpl();
    return modelElement;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public Trait createTrait()
  {
    TraitImpl trait = new TraitImpl();
    return trait;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EventPattern createEventPattern()
  {
    EventPatternImpl eventPattern = new EventPatternImpl();
    return eventPattern;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public AbstractAtomicEventPattern createAbstractAtomicEventPattern()
  {
    AbstractAtomicEventPatternImpl abstractAtomicEventPattern = new AbstractAtomicEventPatternImpl();
    return abstractAtomicEventPattern;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public AtomicEventPattern createAtomicEventPattern()
  {
    AtomicEventPatternImpl atomicEventPattern = new AtomicEventPatternImpl();
    return atomicEventPattern;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public QueryResultChangeEventPattern createQueryResultChangeEventPattern()
  {
    QueryResultChangeEventPatternImpl queryResultChangeEventPattern = new QueryResultChangeEventPatternImpl();
    return queryResultChangeEventPattern;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public ComplexEventPattern createComplexEventPattern()
  {
    ComplexEventPatternImpl complexEventPattern = new ComplexEventPatternImpl();
    return complexEventPattern;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public Rule createRule()
  {
    RuleImpl rule = new RuleImpl();
    return rule;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public TraitList createTraitList()
  {
    TraitListImpl traitList = new TraitListImpl();
    return traitList;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public TypedParameterList createTypedParameterList()
  {
    TypedParameterListImpl typedParameterList = new TypedParameterListImpl();
    return typedParameterList;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public TypedParameter createTypedParameter()
  {
    TypedParameterImpl typedParameter = new TypedParameterImpl();
    return typedParameter;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public TypedParameterWithDefaultValue createTypedParameterWithDefaultValue()
  {
    TypedParameterWithDefaultValueImpl typedParameterWithDefaultValue = new TypedParameterWithDefaultValueImpl();
    return typedParameterWithDefaultValue;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public TraitTypedParameterList createTraitTypedParameterList()
  {
    TraitTypedParameterListImpl traitTypedParameterList = new TraitTypedParameterListImpl();
    return traitTypedParameterList;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public ParametrizedQueryReference createParametrizedQueryReference()
  {
    ParametrizedQueryReferenceImpl parametrizedQueryReference = new ParametrizedQueryReferenceImpl();
    return parametrizedQueryReference;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public ComplexEventExpression createComplexEventExpression()
  {
    ComplexEventExpressionImpl complexEventExpression = new ComplexEventExpressionImpl();
    return complexEventExpression;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public ChainedExpression createChainedExpression()
  {
    ChainedExpressionImpl chainedExpression = new ChainedExpressionImpl();
    return chainedExpression;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public Atom createAtom()
  {
    AtomImpl atom = new AtomImpl();
    return atom;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public AbstractMultiplicity createAbstractMultiplicity()
  {
    AbstractMultiplicityImpl abstractMultiplicity = new AbstractMultiplicityImpl();
    return abstractMultiplicity;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public Timewindow createTimewindow()
  {
    TimewindowImpl timewindow = new TimewindowImpl();
    return timewindow;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public ParameterizedPatternCall createParameterizedPatternCall()
  {
    ParameterizedPatternCallImpl parameterizedPatternCall = new ParameterizedPatternCallImpl();
    return parameterizedPatternCall;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public PatternCallParameterList createPatternCallParameterList()
  {
    PatternCallParameterListImpl patternCallParameterList = new PatternCallParameterListImpl();
    return patternCallParameterList;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public PatternCallParameter createPatternCallParameter()
  {
    PatternCallParameterImpl patternCallParameter = new PatternCallParameterImpl();
    return patternCallParameter;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public ComplexEventOperator createComplexEventOperator()
  {
    ComplexEventOperatorImpl complexEventOperator = new ComplexEventOperatorImpl();
    return complexEventOperator;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public BinaryOperator createBinaryOperator()
  {
    BinaryOperatorImpl binaryOperator = new BinaryOperatorImpl();
    return binaryOperator;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public UnaryOperator createUnaryOperator()
  {
    UnaryOperatorImpl unaryOperator = new UnaryOperatorImpl();
    return unaryOperator;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public FollowsOperator createFollowsOperator()
  {
    FollowsOperatorImpl followsOperator = new FollowsOperatorImpl();
    return followsOperator;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public OrOperator createOrOperator()
  {
    OrOperatorImpl orOperator = new OrOperatorImpl();
    return orOperator;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public AndOperator createAndOperator()
  {
    AndOperatorImpl andOperator = new AndOperatorImpl();
    return andOperator;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public UntilOperator createUntilOperator()
  {
    UntilOperatorImpl untilOperator = new UntilOperatorImpl();
    return untilOperator;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public NegOperator createNegOperator()
  {
    NegOperatorImpl negOperator = new NegOperatorImpl();
    return negOperator;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public Multiplicity createMultiplicity()
  {
    MultiplicityImpl multiplicity = new MultiplicityImpl();
    return multiplicity;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public Infinite createInfinite()
  {
    InfiniteImpl infinite = new InfiniteImpl();
    return infinite;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public AtLeastOne createAtLeastOne()
  {
    AtLeastOneImpl atLeastOne = new AtLeastOneImpl();
    return atLeastOne;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public ContextEnum createContextEnumFromString(EDataType eDataType, String initialValue)
  {
    ContextEnum result = ContextEnum.get(initialValue);
    if (result == null) throw new IllegalArgumentException("The value '" + initialValue + "' is not a valid enumerator of '" + eDataType.getName() + "'");
    return result;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public String convertContextEnumToString(EDataType eDataType, Object instanceValue)
  {
    return instanceValue == null ? null : instanceValue.toString();
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public QueryResultChangeType createQueryResultChangeTypeFromString(EDataType eDataType, String initialValue)
  {
    QueryResultChangeType result = QueryResultChangeType.get(initialValue);
    if (result == null) throw new IllegalArgumentException("The value '" + initialValue + "' is not a valid enumerator of '" + eDataType.getName() + "'");
    return result;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public String convertQueryResultChangeTypeToString(EDataType eDataType, Object instanceValue)
  {
    return instanceValue == null ? null : instanceValue.toString();
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public VeplPackage getVeplPackage()
  {
    return (VeplPackage)getEPackage();
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @deprecated
   * @generated
   */
  @Deprecated
  public static VeplPackage getPackage()
  {
    return VeplPackage.eINSTANCE;
  }

} //VeplFactoryImpl
