/**
 */
package org.eclipse.viatra.cep.vepl.vepl.impl;

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EEnum;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;

import org.eclipse.emf.ecore.impl.EPackageImpl;

import org.eclipse.viatra.cep.vepl.vepl.AbstractAtomicEventPattern;
import org.eclipse.viatra.cep.vepl.vepl.AbstractMultiplicity;
import org.eclipse.viatra.cep.vepl.vepl.AndOperator;
import org.eclipse.viatra.cep.vepl.vepl.AtLeastOne;
import org.eclipse.viatra.cep.vepl.vepl.Atom;
import org.eclipse.viatra.cep.vepl.vepl.AtomicEventPattern;
import org.eclipse.viatra.cep.vepl.vepl.BinaryOperator;
import org.eclipse.viatra.cep.vepl.vepl.ChainedExpression;
import org.eclipse.viatra.cep.vepl.vepl.ComplexEventExpression;
import org.eclipse.viatra.cep.vepl.vepl.ComplexEventOperator;
import org.eclipse.viatra.cep.vepl.vepl.ComplexEventPattern;
import org.eclipse.viatra.cep.vepl.vepl.ContextEnum;
import org.eclipse.viatra.cep.vepl.vepl.EventModel;
import org.eclipse.viatra.cep.vepl.vepl.EventPattern;
import org.eclipse.viatra.cep.vepl.vepl.FollowsOperator;
import org.eclipse.viatra.cep.vepl.vepl.GenericImport;
import org.eclipse.viatra.cep.vepl.vepl.Import;
import org.eclipse.viatra.cep.vepl.vepl.Infinite;
import org.eclipse.viatra.cep.vepl.vepl.ModelElement;
import org.eclipse.viatra.cep.vepl.vepl.Multiplicity;
import org.eclipse.viatra.cep.vepl.vepl.NegOperator;
import org.eclipse.viatra.cep.vepl.vepl.OrOperator;
import org.eclipse.viatra.cep.vepl.vepl.ParameterizedPatternCall;
import org.eclipse.viatra.cep.vepl.vepl.ParametrizedQueryReference;
import org.eclipse.viatra.cep.vepl.vepl.PatternCallParameter;
import org.eclipse.viatra.cep.vepl.vepl.PatternCallParameterList;
import org.eclipse.viatra.cep.vepl.vepl.QueryImport;
import org.eclipse.viatra.cep.vepl.vepl.QueryResultChangeEventPattern;
import org.eclipse.viatra.cep.vepl.vepl.QueryResultChangeType;
import org.eclipse.viatra.cep.vepl.vepl.Rule;
import org.eclipse.viatra.cep.vepl.vepl.Timewindow;
import org.eclipse.viatra.cep.vepl.vepl.Trait;
import org.eclipse.viatra.cep.vepl.vepl.TraitList;
import org.eclipse.viatra.cep.vepl.vepl.TraitTypedParameterList;
import org.eclipse.viatra.cep.vepl.vepl.TypedParameter;
import org.eclipse.viatra.cep.vepl.vepl.TypedParameterList;
import org.eclipse.viatra.cep.vepl.vepl.TypedParameterWithDefaultValue;
import org.eclipse.viatra.cep.vepl.vepl.UnaryOperator;
import org.eclipse.viatra.cep.vepl.vepl.UntilOperator;
import org.eclipse.viatra.cep.vepl.vepl.VeplFactory;
import org.eclipse.viatra.cep.vepl.vepl.VeplPackage;

import org.eclipse.viatra.query.patternlanguage.patternLanguage.PatternLanguagePackage;

import org.eclipse.xtext.common.types.TypesPackage;

import org.eclipse.xtext.xbase.XbasePackage;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model <b>Package</b>.
 * <!-- end-user-doc -->
 * @generated
 */
public class VeplPackageImpl extends EPackageImpl implements VeplPackage
{
  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EClass eventModelEClass = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EClass importEClass = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EClass genericImportEClass = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EClass queryImportEClass = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EClass modelElementEClass = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EClass traitEClass = null;

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
  private EClass abstractAtomicEventPatternEClass = null;

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
  private EClass queryResultChangeEventPatternEClass = null;

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
  private EClass ruleEClass = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EClass traitListEClass = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EClass typedParameterListEClass = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EClass typedParameterEClass = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EClass typedParameterWithDefaultValueEClass = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EClass traitTypedParameterListEClass = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EClass parametrizedQueryReferenceEClass = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EClass complexEventExpressionEClass = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EClass chainedExpressionEClass = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EClass atomEClass = null;

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
  private EClass timewindowEClass = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EClass parameterizedPatternCallEClass = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EClass patternCallParameterListEClass = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EClass patternCallParameterEClass = null;

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
  private EClass binaryOperatorEClass = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EClass unaryOperatorEClass = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EClass followsOperatorEClass = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EClass orOperatorEClass = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EClass andOperatorEClass = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EClass untilOperatorEClass = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EClass negOperatorEClass = null;

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
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EEnum contextEnumEEnum = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EEnum queryResultChangeTypeEEnum = null;

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
   * @see org.eclipse.viatra.cep.vepl.vepl.VeplPackage#eNS_URI
   * @see #init()
   * @generated
   */
  private VeplPackageImpl()
  {
    super(eNS_URI, VeplFactory.eINSTANCE);
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
   * <p>This method is used to initialize {@link VeplPackage#eINSTANCE} when that field is accessed.
   * Clients should not invoke it directly. Instead, they should simply access that field to obtain the package.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #eNS_URI
   * @see #createPackageContents()
   * @see #initializePackageContents()
   * @generated
   */
  public static VeplPackage init()
  {
    if (isInited) return (VeplPackage)EPackage.Registry.INSTANCE.getEPackage(VeplPackage.eNS_URI);

    // Obtain or create and register package
    VeplPackageImpl theVeplPackage = (VeplPackageImpl)(EPackage.Registry.INSTANCE.get(eNS_URI) instanceof VeplPackageImpl ? EPackage.Registry.INSTANCE.get(eNS_URI) : new VeplPackageImpl());

    isInited = true;

    // Initialize simple dependencies
    PatternLanguagePackage.eINSTANCE.eClass();

    // Create package meta-data objects
    theVeplPackage.createPackageContents();

    // Initialize created meta-data
    theVeplPackage.initializePackageContents();

    // Mark meta-data to indicate it can't be changed
    theVeplPackage.freeze();

  
    // Update the registry and return the package
    EPackage.Registry.INSTANCE.put(VeplPackage.eNS_URI, theVeplPackage);
    return theVeplPackage;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EClass getEventModel()
  {
    return eventModelEClass;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EAttribute getEventModel_Name()
  {
    return (EAttribute)eventModelEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EReference getEventModel_Imports()
  {
    return (EReference)eventModelEClass.getEStructuralFeatures().get(1);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EAttribute getEventModel_Context()
  {
    return (EAttribute)eventModelEClass.getEStructuralFeatures().get(2);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EReference getEventModel_ModelElements()
  {
    return (EReference)eventModelEClass.getEStructuralFeatures().get(3);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EClass getImport()
  {
    return importEClass;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EAttribute getImport_ImportedNamespace()
  {
    return (EAttribute)importEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EClass getGenericImport()
  {
    return genericImportEClass;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EClass getQueryImport()
  {
    return queryImportEClass;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EClass getModelElement()
  {
    return modelElementEClass;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EAttribute getModelElement_Name()
  {
    return (EAttribute)modelElementEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EClass getTrait()
  {
    return traitEClass;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EReference getTrait_Parameters()
  {
    return (EReference)traitEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EClass getEventPattern()
  {
    return eventPatternEClass;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EReference getEventPattern_Parameters()
  {
    return (EReference)eventPatternEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EClass getAbstractAtomicEventPattern()
  {
    return abstractAtomicEventPatternEClass;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EClass getAtomicEventPattern()
  {
    return atomicEventPatternEClass;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EReference getAtomicEventPattern_Traits()
  {
    return (EReference)atomicEventPatternEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EReference getAtomicEventPattern_CheckExpression()
  {
    return (EReference)atomicEventPatternEClass.getEStructuralFeatures().get(1);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EClass getQueryResultChangeEventPattern()
  {
    return queryResultChangeEventPatternEClass;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EReference getQueryResultChangeEventPattern_QueryReference()
  {
    return (EReference)queryResultChangeEventPatternEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EAttribute getQueryResultChangeEventPattern_ResultChangeType()
  {
    return (EAttribute)queryResultChangeEventPatternEClass.getEStructuralFeatures().get(1);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EClass getComplexEventPattern()
  {
    return complexEventPatternEClass;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EReference getComplexEventPattern_ComplexEventExpression()
  {
    return (EReference)complexEventPatternEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EAttribute getComplexEventPattern_Context()
  {
    return (EAttribute)complexEventPatternEClass.getEStructuralFeatures().get(1);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EClass getRule()
  {
    return ruleEClass;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EReference getRule_EventPatterns()
  {
    return (EReference)ruleEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EReference getRule_Action()
  {
    return (EReference)ruleEClass.getEStructuralFeatures().get(1);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EClass getTraitList()
  {
    return traitListEClass;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EReference getTraitList_Traits()
  {
    return (EReference)traitListEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EClass getTypedParameterList()
  {
    return typedParameterListEClass;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EReference getTypedParameterList_Parameters()
  {
    return (EReference)typedParameterListEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EClass getTypedParameter()
  {
    return typedParameterEClass;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EAttribute getTypedParameter_Name()
  {
    return (EAttribute)typedParameterEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EReference getTypedParameter_Type()
  {
    return (EReference)typedParameterEClass.getEStructuralFeatures().get(1);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EClass getTypedParameterWithDefaultValue()
  {
    return typedParameterWithDefaultValueEClass;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EReference getTypedParameterWithDefaultValue_TypedParameter()
  {
    return (EReference)typedParameterWithDefaultValueEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EReference getTypedParameterWithDefaultValue_Value()
  {
    return (EReference)typedParameterWithDefaultValueEClass.getEStructuralFeatures().get(1);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EClass getTraitTypedParameterList()
  {
    return traitTypedParameterListEClass;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EReference getTraitTypedParameterList_Parameters()
  {
    return (EReference)traitTypedParameterListEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EClass getParametrizedQueryReference()
  {
    return parametrizedQueryReferenceEClass;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EReference getParametrizedQueryReference_Query()
  {
    return (EReference)parametrizedQueryReferenceEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EReference getParametrizedQueryReference_ParameterList()
  {
    return (EReference)parametrizedQueryReferenceEClass.getEStructuralFeatures().get(1);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EClass getComplexEventExpression()
  {
    return complexEventExpressionEClass;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EReference getComplexEventExpression_Left()
  {
    return (EReference)complexEventExpressionEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EReference getComplexEventExpression_Right()
  {
    return (EReference)complexEventExpressionEClass.getEStructuralFeatures().get(1);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EReference getComplexEventExpression_Multiplicity()
  {
    return (EReference)complexEventExpressionEClass.getEStructuralFeatures().get(2);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EReference getComplexEventExpression_Timewindow()
  {
    return (EReference)complexEventExpressionEClass.getEStructuralFeatures().get(3);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EReference getComplexEventExpression_NegOperator()
  {
    return (EReference)complexEventExpressionEClass.getEStructuralFeatures().get(4);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EClass getChainedExpression()
  {
    return chainedExpressionEClass;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EReference getChainedExpression_Operator()
  {
    return (EReference)chainedExpressionEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EReference getChainedExpression_Expression()
  {
    return (EReference)chainedExpressionEClass.getEStructuralFeatures().get(1);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EClass getAtom()
  {
    return atomEClass;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EReference getAtom_PatternCall()
  {
    return (EReference)atomEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EClass getAbstractMultiplicity()
  {
    return abstractMultiplicityEClass;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EClass getTimewindow()
  {
    return timewindowEClass;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EAttribute getTimewindow_Length()
  {
    return (EAttribute)timewindowEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EClass getParameterizedPatternCall()
  {
    return parameterizedPatternCallEClass;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EReference getParameterizedPatternCall_EventPattern()
  {
    return (EReference)parameterizedPatternCallEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EReference getParameterizedPatternCall_ParameterList()
  {
    return (EReference)parameterizedPatternCallEClass.getEStructuralFeatures().get(1);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EClass getPatternCallParameterList()
  {
    return patternCallParameterListEClass;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EReference getPatternCallParameterList_Parameters()
  {
    return (EReference)patternCallParameterListEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EClass getPatternCallParameter()
  {
    return patternCallParameterEClass;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EAttribute getPatternCallParameter_Name()
  {
    return (EAttribute)patternCallParameterEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EClass getComplexEventOperator()
  {
    return complexEventOperatorEClass;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EClass getBinaryOperator()
  {
    return binaryOperatorEClass;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EClass getUnaryOperator()
  {
    return unaryOperatorEClass;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EClass getFollowsOperator()
  {
    return followsOperatorEClass;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EClass getOrOperator()
  {
    return orOperatorEClass;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EClass getAndOperator()
  {
    return andOperatorEClass;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EClass getUntilOperator()
  {
    return untilOperatorEClass;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EClass getNegOperator()
  {
    return negOperatorEClass;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EClass getMultiplicity()
  {
    return multiplicityEClass;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EAttribute getMultiplicity_Value()
  {
    return (EAttribute)multiplicityEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EClass getInfinite()
  {
    return infiniteEClass;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EClass getAtLeastOne()
  {
    return atLeastOneEClass;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EEnum getContextEnum()
  {
    return contextEnumEEnum;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EEnum getQueryResultChangeType()
  {
    return queryResultChangeTypeEEnum;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public VeplFactory getVeplFactory()
  {
    return (VeplFactory)getEFactoryInstance();
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
  public void createPackageContents()
  {
    if (isCreated) return;
    isCreated = true;

    // Create classes and their features
    eventModelEClass = createEClass(EVENT_MODEL);
    createEAttribute(eventModelEClass, EVENT_MODEL__NAME);
    createEReference(eventModelEClass, EVENT_MODEL__IMPORTS);
    createEAttribute(eventModelEClass, EVENT_MODEL__CONTEXT);
    createEReference(eventModelEClass, EVENT_MODEL__MODEL_ELEMENTS);

    importEClass = createEClass(IMPORT);
    createEAttribute(importEClass, IMPORT__IMPORTED_NAMESPACE);

    genericImportEClass = createEClass(GENERIC_IMPORT);

    queryImportEClass = createEClass(QUERY_IMPORT);

    modelElementEClass = createEClass(MODEL_ELEMENT);
    createEAttribute(modelElementEClass, MODEL_ELEMENT__NAME);

    traitEClass = createEClass(TRAIT);
    createEReference(traitEClass, TRAIT__PARAMETERS);

    eventPatternEClass = createEClass(EVENT_PATTERN);
    createEReference(eventPatternEClass, EVENT_PATTERN__PARAMETERS);

    abstractAtomicEventPatternEClass = createEClass(ABSTRACT_ATOMIC_EVENT_PATTERN);

    atomicEventPatternEClass = createEClass(ATOMIC_EVENT_PATTERN);
    createEReference(atomicEventPatternEClass, ATOMIC_EVENT_PATTERN__TRAITS);
    createEReference(atomicEventPatternEClass, ATOMIC_EVENT_PATTERN__CHECK_EXPRESSION);

    queryResultChangeEventPatternEClass = createEClass(QUERY_RESULT_CHANGE_EVENT_PATTERN);
    createEReference(queryResultChangeEventPatternEClass, QUERY_RESULT_CHANGE_EVENT_PATTERN__QUERY_REFERENCE);
    createEAttribute(queryResultChangeEventPatternEClass, QUERY_RESULT_CHANGE_EVENT_PATTERN__RESULT_CHANGE_TYPE);

    complexEventPatternEClass = createEClass(COMPLEX_EVENT_PATTERN);
    createEReference(complexEventPatternEClass, COMPLEX_EVENT_PATTERN__COMPLEX_EVENT_EXPRESSION);
    createEAttribute(complexEventPatternEClass, COMPLEX_EVENT_PATTERN__CONTEXT);

    ruleEClass = createEClass(RULE);
    createEReference(ruleEClass, RULE__EVENT_PATTERNS);
    createEReference(ruleEClass, RULE__ACTION);

    traitListEClass = createEClass(TRAIT_LIST);
    createEReference(traitListEClass, TRAIT_LIST__TRAITS);

    typedParameterListEClass = createEClass(TYPED_PARAMETER_LIST);
    createEReference(typedParameterListEClass, TYPED_PARAMETER_LIST__PARAMETERS);

    typedParameterEClass = createEClass(TYPED_PARAMETER);
    createEAttribute(typedParameterEClass, TYPED_PARAMETER__NAME);
    createEReference(typedParameterEClass, TYPED_PARAMETER__TYPE);

    typedParameterWithDefaultValueEClass = createEClass(TYPED_PARAMETER_WITH_DEFAULT_VALUE);
    createEReference(typedParameterWithDefaultValueEClass, TYPED_PARAMETER_WITH_DEFAULT_VALUE__TYPED_PARAMETER);
    createEReference(typedParameterWithDefaultValueEClass, TYPED_PARAMETER_WITH_DEFAULT_VALUE__VALUE);

    traitTypedParameterListEClass = createEClass(TRAIT_TYPED_PARAMETER_LIST);
    createEReference(traitTypedParameterListEClass, TRAIT_TYPED_PARAMETER_LIST__PARAMETERS);

    parametrizedQueryReferenceEClass = createEClass(PARAMETRIZED_QUERY_REFERENCE);
    createEReference(parametrizedQueryReferenceEClass, PARAMETRIZED_QUERY_REFERENCE__QUERY);
    createEReference(parametrizedQueryReferenceEClass, PARAMETRIZED_QUERY_REFERENCE__PARAMETER_LIST);

    complexEventExpressionEClass = createEClass(COMPLEX_EVENT_EXPRESSION);
    createEReference(complexEventExpressionEClass, COMPLEX_EVENT_EXPRESSION__LEFT);
    createEReference(complexEventExpressionEClass, COMPLEX_EVENT_EXPRESSION__RIGHT);
    createEReference(complexEventExpressionEClass, COMPLEX_EVENT_EXPRESSION__MULTIPLICITY);
    createEReference(complexEventExpressionEClass, COMPLEX_EVENT_EXPRESSION__TIMEWINDOW);
    createEReference(complexEventExpressionEClass, COMPLEX_EVENT_EXPRESSION__NEG_OPERATOR);

    chainedExpressionEClass = createEClass(CHAINED_EXPRESSION);
    createEReference(chainedExpressionEClass, CHAINED_EXPRESSION__OPERATOR);
    createEReference(chainedExpressionEClass, CHAINED_EXPRESSION__EXPRESSION);

    atomEClass = createEClass(ATOM);
    createEReference(atomEClass, ATOM__PATTERN_CALL);

    abstractMultiplicityEClass = createEClass(ABSTRACT_MULTIPLICITY);

    timewindowEClass = createEClass(TIMEWINDOW);
    createEAttribute(timewindowEClass, TIMEWINDOW__LENGTH);

    parameterizedPatternCallEClass = createEClass(PARAMETERIZED_PATTERN_CALL);
    createEReference(parameterizedPatternCallEClass, PARAMETERIZED_PATTERN_CALL__EVENT_PATTERN);
    createEReference(parameterizedPatternCallEClass, PARAMETERIZED_PATTERN_CALL__PARAMETER_LIST);

    patternCallParameterListEClass = createEClass(PATTERN_CALL_PARAMETER_LIST);
    createEReference(patternCallParameterListEClass, PATTERN_CALL_PARAMETER_LIST__PARAMETERS);

    patternCallParameterEClass = createEClass(PATTERN_CALL_PARAMETER);
    createEAttribute(patternCallParameterEClass, PATTERN_CALL_PARAMETER__NAME);

    complexEventOperatorEClass = createEClass(COMPLEX_EVENT_OPERATOR);

    binaryOperatorEClass = createEClass(BINARY_OPERATOR);

    unaryOperatorEClass = createEClass(UNARY_OPERATOR);

    followsOperatorEClass = createEClass(FOLLOWS_OPERATOR);

    orOperatorEClass = createEClass(OR_OPERATOR);

    andOperatorEClass = createEClass(AND_OPERATOR);

    untilOperatorEClass = createEClass(UNTIL_OPERATOR);

    negOperatorEClass = createEClass(NEG_OPERATOR);

    multiplicityEClass = createEClass(MULTIPLICITY);
    createEAttribute(multiplicityEClass, MULTIPLICITY__VALUE);

    infiniteEClass = createEClass(INFINITE);

    atLeastOneEClass = createEClass(AT_LEAST_ONE);

    // Create enums
    contextEnumEEnum = createEEnum(CONTEXT_ENUM);
    queryResultChangeTypeEEnum = createEEnum(QUERY_RESULT_CHANGE_TYPE);
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
  public void initializePackageContents()
  {
    if (isInitialized) return;
    isInitialized = true;

    // Initialize package
    setName(eNAME);
    setNsPrefix(eNS_PREFIX);
    setNsURI(eNS_URI);

    // Obtain other dependent packages
    XbasePackage theXbasePackage = (XbasePackage)EPackage.Registry.INSTANCE.getEPackage(XbasePackage.eNS_URI);
    TypesPackage theTypesPackage = (TypesPackage)EPackage.Registry.INSTANCE.getEPackage(TypesPackage.eNS_URI);
    PatternLanguagePackage thePatternLanguagePackage = (PatternLanguagePackage)EPackage.Registry.INSTANCE.getEPackage(PatternLanguagePackage.eNS_URI);

    // Create type parameters

    // Set bounds for type parameters

    // Add supertypes to classes
    genericImportEClass.getESuperTypes().add(this.getImport());
    queryImportEClass.getESuperTypes().add(this.getImport());
    traitEClass.getESuperTypes().add(this.getModelElement());
    eventPatternEClass.getESuperTypes().add(this.getModelElement());
    abstractAtomicEventPatternEClass.getESuperTypes().add(this.getEventPattern());
    atomicEventPatternEClass.getESuperTypes().add(this.getAbstractAtomicEventPattern());
    queryResultChangeEventPatternEClass.getESuperTypes().add(this.getAbstractAtomicEventPattern());
    complexEventPatternEClass.getESuperTypes().add(this.getEventPattern());
    ruleEClass.getESuperTypes().add(this.getModelElement());
    atomEClass.getESuperTypes().add(this.getComplexEventExpression());
    binaryOperatorEClass.getESuperTypes().add(this.getComplexEventOperator());
    unaryOperatorEClass.getESuperTypes().add(this.getComplexEventOperator());
    followsOperatorEClass.getESuperTypes().add(this.getBinaryOperator());
    orOperatorEClass.getESuperTypes().add(this.getBinaryOperator());
    andOperatorEClass.getESuperTypes().add(this.getBinaryOperator());
    negOperatorEClass.getESuperTypes().add(this.getUnaryOperator());
    multiplicityEClass.getESuperTypes().add(this.getAbstractMultiplicity());
    infiniteEClass.getESuperTypes().add(this.getAbstractMultiplicity());
    atLeastOneEClass.getESuperTypes().add(this.getAbstractMultiplicity());

    // Initialize classes and features; add operations and parameters
    initEClass(eventModelEClass, EventModel.class, "EventModel", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
    initEAttribute(getEventModel_Name(), ecorePackage.getEString(), "name", null, 0, 1, EventModel.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEReference(getEventModel_Imports(), this.getImport(), null, "imports", null, 0, -1, EventModel.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEAttribute(getEventModel_Context(), this.getContextEnum(), "context", null, 0, 1, EventModel.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEReference(getEventModel_ModelElements(), this.getModelElement(), null, "modelElements", null, 0, -1, EventModel.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

    initEClass(importEClass, Import.class, "Import", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
    initEAttribute(getImport_ImportedNamespace(), ecorePackage.getEString(), "importedNamespace", null, 0, 1, Import.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

    initEClass(genericImportEClass, GenericImport.class, "GenericImport", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);

    initEClass(queryImportEClass, QueryImport.class, "QueryImport", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);

    initEClass(modelElementEClass, ModelElement.class, "ModelElement", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
    initEAttribute(getModelElement_Name(), ecorePackage.getEString(), "name", null, 0, 1, ModelElement.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

    initEClass(traitEClass, Trait.class, "Trait", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
    initEReference(getTrait_Parameters(), this.getTraitTypedParameterList(), null, "parameters", null, 0, 1, Trait.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

    initEClass(eventPatternEClass, EventPattern.class, "EventPattern", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
    initEReference(getEventPattern_Parameters(), this.getTypedParameterList(), null, "parameters", null, 0, 1, EventPattern.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

    initEClass(abstractAtomicEventPatternEClass, AbstractAtomicEventPattern.class, "AbstractAtomicEventPattern", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);

    initEClass(atomicEventPatternEClass, AtomicEventPattern.class, "AtomicEventPattern", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
    initEReference(getAtomicEventPattern_Traits(), this.getTraitList(), null, "traits", null, 0, 1, AtomicEventPattern.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEReference(getAtomicEventPattern_CheckExpression(), theXbasePackage.getXExpression(), null, "checkExpression", null, 0, 1, AtomicEventPattern.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

    initEClass(queryResultChangeEventPatternEClass, QueryResultChangeEventPattern.class, "QueryResultChangeEventPattern", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
    initEReference(getQueryResultChangeEventPattern_QueryReference(), this.getParametrizedQueryReference(), null, "queryReference", null, 0, 1, QueryResultChangeEventPattern.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEAttribute(getQueryResultChangeEventPattern_ResultChangeType(), this.getQueryResultChangeType(), "resultChangeType", null, 0, 1, QueryResultChangeEventPattern.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

    initEClass(complexEventPatternEClass, ComplexEventPattern.class, "ComplexEventPattern", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
    initEReference(getComplexEventPattern_ComplexEventExpression(), this.getComplexEventExpression(), null, "complexEventExpression", null, 0, 1, ComplexEventPattern.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEAttribute(getComplexEventPattern_Context(), this.getContextEnum(), "context", null, 0, 1, ComplexEventPattern.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

    initEClass(ruleEClass, Rule.class, "Rule", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
    initEReference(getRule_EventPatterns(), this.getParameterizedPatternCall(), null, "eventPatterns", null, 0, -1, Rule.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEReference(getRule_Action(), theXbasePackage.getXExpression(), null, "action", null, 0, 1, Rule.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

    initEClass(traitListEClass, TraitList.class, "TraitList", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
    initEReference(getTraitList_Traits(), this.getTrait(), null, "traits", null, 0, -1, TraitList.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, !IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

    initEClass(typedParameterListEClass, TypedParameterList.class, "TypedParameterList", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
    initEReference(getTypedParameterList_Parameters(), this.getTypedParameter(), null, "parameters", null, 0, -1, TypedParameterList.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

    initEClass(typedParameterEClass, TypedParameter.class, "TypedParameter", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
    initEAttribute(getTypedParameter_Name(), ecorePackage.getEString(), "name", null, 0, 1, TypedParameter.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEReference(getTypedParameter_Type(), theTypesPackage.getJvmTypeReference(), null, "type", null, 0, 1, TypedParameter.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

    initEClass(typedParameterWithDefaultValueEClass, TypedParameterWithDefaultValue.class, "TypedParameterWithDefaultValue", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
    initEReference(getTypedParameterWithDefaultValue_TypedParameter(), this.getTypedParameter(), null, "typedParameter", null, 0, 1, TypedParameterWithDefaultValue.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEReference(getTypedParameterWithDefaultValue_Value(), theXbasePackage.getXExpression(), null, "value", null, 0, 1, TypedParameterWithDefaultValue.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

    initEClass(traitTypedParameterListEClass, TraitTypedParameterList.class, "TraitTypedParameterList", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
    initEReference(getTraitTypedParameterList_Parameters(), this.getTypedParameterWithDefaultValue(), null, "parameters", null, 0, -1, TraitTypedParameterList.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

    initEClass(parametrizedQueryReferenceEClass, ParametrizedQueryReference.class, "ParametrizedQueryReference", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
    initEReference(getParametrizedQueryReference_Query(), thePatternLanguagePackage.getPattern(), null, "query", null, 0, 1, ParametrizedQueryReference.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEReference(getParametrizedQueryReference_ParameterList(), this.getPatternCallParameterList(), null, "parameterList", null, 0, 1, ParametrizedQueryReference.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

    initEClass(complexEventExpressionEClass, ComplexEventExpression.class, "ComplexEventExpression", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
    initEReference(getComplexEventExpression_Left(), this.getComplexEventExpression(), null, "left", null, 0, 1, ComplexEventExpression.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEReference(getComplexEventExpression_Right(), this.getChainedExpression(), null, "right", null, 0, -1, ComplexEventExpression.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEReference(getComplexEventExpression_Multiplicity(), this.getAbstractMultiplicity(), null, "multiplicity", null, 0, 1, ComplexEventExpression.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEReference(getComplexEventExpression_Timewindow(), this.getTimewindow(), null, "timewindow", null, 0, 1, ComplexEventExpression.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEReference(getComplexEventExpression_NegOperator(), this.getNegOperator(), null, "negOperator", null, 0, 1, ComplexEventExpression.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

    initEClass(chainedExpressionEClass, ChainedExpression.class, "ChainedExpression", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
    initEReference(getChainedExpression_Operator(), this.getBinaryOperator(), null, "operator", null, 0, 1, ChainedExpression.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEReference(getChainedExpression_Expression(), this.getComplexEventExpression(), null, "expression", null, 0, 1, ChainedExpression.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

    initEClass(atomEClass, Atom.class, "Atom", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
    initEReference(getAtom_PatternCall(), this.getParameterizedPatternCall(), null, "patternCall", null, 0, 1, Atom.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

    initEClass(abstractMultiplicityEClass, AbstractMultiplicity.class, "AbstractMultiplicity", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);

    initEClass(timewindowEClass, Timewindow.class, "Timewindow", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
    initEAttribute(getTimewindow_Length(), ecorePackage.getEInt(), "length", null, 0, 1, Timewindow.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

    initEClass(parameterizedPatternCallEClass, ParameterizedPatternCall.class, "ParameterizedPatternCall", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
    initEReference(getParameterizedPatternCall_EventPattern(), this.getEventPattern(), null, "eventPattern", null, 0, 1, ParameterizedPatternCall.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEReference(getParameterizedPatternCall_ParameterList(), this.getPatternCallParameterList(), null, "parameterList", null, 0, 1, ParameterizedPatternCall.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

    initEClass(patternCallParameterListEClass, PatternCallParameterList.class, "PatternCallParameterList", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
    initEReference(getPatternCallParameterList_Parameters(), this.getPatternCallParameter(), null, "parameters", null, 0, -1, PatternCallParameterList.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

    initEClass(patternCallParameterEClass, PatternCallParameter.class, "PatternCallParameter", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
    initEAttribute(getPatternCallParameter_Name(), ecorePackage.getEString(), "name", null, 0, 1, PatternCallParameter.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

    initEClass(complexEventOperatorEClass, ComplexEventOperator.class, "ComplexEventOperator", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);

    initEClass(binaryOperatorEClass, BinaryOperator.class, "BinaryOperator", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);

    initEClass(unaryOperatorEClass, UnaryOperator.class, "UnaryOperator", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);

    initEClass(followsOperatorEClass, FollowsOperator.class, "FollowsOperator", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);

    initEClass(orOperatorEClass, OrOperator.class, "OrOperator", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);

    initEClass(andOperatorEClass, AndOperator.class, "AndOperator", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);

    initEClass(untilOperatorEClass, UntilOperator.class, "UntilOperator", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);

    initEClass(negOperatorEClass, NegOperator.class, "NegOperator", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);

    initEClass(multiplicityEClass, Multiplicity.class, "Multiplicity", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
    initEAttribute(getMultiplicity_Value(), ecorePackage.getEInt(), "value", null, 0, 1, Multiplicity.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

    initEClass(infiniteEClass, Infinite.class, "Infinite", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);

    initEClass(atLeastOneEClass, AtLeastOne.class, "AtLeastOne", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);

    // Initialize enums and add enum literals
    initEEnum(contextEnumEEnum, ContextEnum.class, "ContextEnum");
    addEEnumLiteral(contextEnumEEnum, ContextEnum.NOT_SET);
    addEEnumLiteral(contextEnumEEnum, ContextEnum.CHRONICLE);
    addEEnumLiteral(contextEnumEEnum, ContextEnum.IMMEDIATE);
    addEEnumLiteral(contextEnumEEnum, ContextEnum.STRICT);

    initEEnum(queryResultChangeTypeEEnum, QueryResultChangeType.class, "QueryResultChangeType");
    addEEnumLiteral(queryResultChangeTypeEEnum, QueryResultChangeType.FOUND);
    addEEnumLiteral(queryResultChangeTypeEEnum, QueryResultChangeType.LOST);

    // Create resource
    createResource(eNS_URI);
  }

} //VeplPackageImpl
