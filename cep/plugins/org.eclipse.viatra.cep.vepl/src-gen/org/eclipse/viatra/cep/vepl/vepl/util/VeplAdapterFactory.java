/**
 */
package org.eclipse.viatra.cep.vepl.vepl.util;

import org.eclipse.emf.common.notify.Adapter;
import org.eclipse.emf.common.notify.Notifier;

import org.eclipse.emf.common.notify.impl.AdapterFactoryImpl;

import org.eclipse.emf.ecore.EObject;

import org.eclipse.viatra.cep.vepl.vepl.*;

/**
 * <!-- begin-user-doc -->
 * The <b>Adapter Factory</b> for the model.
 * It provides an adapter <code>createXXX</code> method for each class of the model.
 * <!-- end-user-doc -->
 * @see org.eclipse.viatra.cep.vepl.vepl.VeplPackage
 * @generated
 */
public class VeplAdapterFactory extends AdapterFactoryImpl
{
  /**
   * The cached model package.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  protected static VeplPackage modelPackage;

  /**
   * Creates an instance of the adapter factory.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public VeplAdapterFactory()
  {
    if (modelPackage == null)
    {
      modelPackage = VeplPackage.eINSTANCE;
    }
  }

  /**
   * Returns whether this factory is applicable for the type of the object.
   * <!-- begin-user-doc -->
   * This implementation returns <code>true</code> if the object is either the model's package or is an instance object of the model.
   * <!-- end-user-doc -->
   * @return whether this factory is applicable for the type of the object.
   * @generated
   */
  @Override
  public boolean isFactoryForType(Object object)
  {
    if (object == modelPackage)
    {
      return true;
    }
    if (object instanceof EObject)
    {
      return ((EObject)object).eClass().getEPackage() == modelPackage;
    }
    return false;
  }

  /**
   * The switch that delegates to the <code>createXXX</code> methods.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  protected VeplSwitch<Adapter> modelSwitch =
    new VeplSwitch<Adapter>()
    {
      @Override
      public Adapter caseEventModel(EventModel object)
      {
        return createEventModelAdapter();
      }
      @Override
      public Adapter caseImport(Import object)
      {
        return createImportAdapter();
      }
      @Override
      public Adapter caseGenericImport(GenericImport object)
      {
        return createGenericImportAdapter();
      }
      @Override
      public Adapter caseQueryImport(QueryImport object)
      {
        return createQueryImportAdapter();
      }
      @Override
      public Adapter caseModelElement(ModelElement object)
      {
        return createModelElementAdapter();
      }
      @Override
      public Adapter caseTrait(Trait object)
      {
        return createTraitAdapter();
      }
      @Override
      public Adapter caseEventPattern(EventPattern object)
      {
        return createEventPatternAdapter();
      }
      @Override
      public Adapter caseAbstractAtomicEventPattern(AbstractAtomicEventPattern object)
      {
        return createAbstractAtomicEventPatternAdapter();
      }
      @Override
      public Adapter caseAtomicEventPattern(AtomicEventPattern object)
      {
        return createAtomicEventPatternAdapter();
      }
      @Override
      public Adapter caseQueryResultChangeEventPattern(QueryResultChangeEventPattern object)
      {
        return createQueryResultChangeEventPatternAdapter();
      }
      @Override
      public Adapter caseComplexEventPattern(ComplexEventPattern object)
      {
        return createComplexEventPatternAdapter();
      }
      @Override
      public Adapter caseRule(Rule object)
      {
        return createRuleAdapter();
      }
      @Override
      public Adapter caseTraitList(TraitList object)
      {
        return createTraitListAdapter();
      }
      @Override
      public Adapter caseTypedParameterList(TypedParameterList object)
      {
        return createTypedParameterListAdapter();
      }
      @Override
      public Adapter caseTypedParameter(TypedParameter object)
      {
        return createTypedParameterAdapter();
      }
      @Override
      public Adapter caseTypedParameterWithDefaultValue(TypedParameterWithDefaultValue object)
      {
        return createTypedParameterWithDefaultValueAdapter();
      }
      @Override
      public Adapter caseTraitTypedParameterList(TraitTypedParameterList object)
      {
        return createTraitTypedParameterListAdapter();
      }
      @Override
      public Adapter caseParametrizedQueryReference(ParametrizedQueryReference object)
      {
        return createParametrizedQueryReferenceAdapter();
      }
      @Override
      public Adapter caseComplexEventExpression(ComplexEventExpression object)
      {
        return createComplexEventExpressionAdapter();
      }
      @Override
      public Adapter caseChainedExpression(ChainedExpression object)
      {
        return createChainedExpressionAdapter();
      }
      @Override
      public Adapter caseAtom(Atom object)
      {
        return createAtomAdapter();
      }
      @Override
      public Adapter caseAbstractMultiplicity(AbstractMultiplicity object)
      {
        return createAbstractMultiplicityAdapter();
      }
      @Override
      public Adapter caseTimewindow(Timewindow object)
      {
        return createTimewindowAdapter();
      }
      @Override
      public Adapter caseParameterizedPatternCall(ParameterizedPatternCall object)
      {
        return createParameterizedPatternCallAdapter();
      }
      @Override
      public Adapter casePatternCallParameterList(PatternCallParameterList object)
      {
        return createPatternCallParameterListAdapter();
      }
      @Override
      public Adapter casePatternCallParameter(PatternCallParameter object)
      {
        return createPatternCallParameterAdapter();
      }
      @Override
      public Adapter caseComplexEventOperator(ComplexEventOperator object)
      {
        return createComplexEventOperatorAdapter();
      }
      @Override
      public Adapter caseBinaryOperator(BinaryOperator object)
      {
        return createBinaryOperatorAdapter();
      }
      @Override
      public Adapter caseUnaryOperator(UnaryOperator object)
      {
        return createUnaryOperatorAdapter();
      }
      @Override
      public Adapter caseFollowsOperator(FollowsOperator object)
      {
        return createFollowsOperatorAdapter();
      }
      @Override
      public Adapter caseOrOperator(OrOperator object)
      {
        return createOrOperatorAdapter();
      }
      @Override
      public Adapter caseAndOperator(AndOperator object)
      {
        return createAndOperatorAdapter();
      }
      @Override
      public Adapter caseUntilOperator(UntilOperator object)
      {
        return createUntilOperatorAdapter();
      }
      @Override
      public Adapter caseNegOperator(NegOperator object)
      {
        return createNegOperatorAdapter();
      }
      @Override
      public Adapter caseMultiplicity(Multiplicity object)
      {
        return createMultiplicityAdapter();
      }
      @Override
      public Adapter caseInfinite(Infinite object)
      {
        return createInfiniteAdapter();
      }
      @Override
      public Adapter caseAtLeastOne(AtLeastOne object)
      {
        return createAtLeastOneAdapter();
      }
      @Override
      public Adapter defaultCase(EObject object)
      {
        return createEObjectAdapter();
      }
    };

  /**
   * Creates an adapter for the <code>target</code>.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param target the object to adapt.
   * @return the adapter for the <code>target</code>.
   * @generated
   */
  @Override
  public Adapter createAdapter(Notifier target)
  {
    return modelSwitch.doSwitch((EObject)target);
  }


  /**
   * Creates a new adapter for an object of class '{@link org.eclipse.viatra.cep.vepl.vepl.EventModel <em>Event Model</em>}'.
   * <!-- begin-user-doc -->
   * This default implementation returns null so that we can easily ignore cases;
   * it's useful to ignore a case when inheritance will catch all the cases anyway.
   * <!-- end-user-doc -->
   * @return the new adapter.
   * @see org.eclipse.viatra.cep.vepl.vepl.EventModel
   * @generated
   */
  public Adapter createEventModelAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link org.eclipse.viatra.cep.vepl.vepl.Import <em>Import</em>}'.
   * <!-- begin-user-doc -->
   * This default implementation returns null so that we can easily ignore cases;
   * it's useful to ignore a case when inheritance will catch all the cases anyway.
   * <!-- end-user-doc -->
   * @return the new adapter.
   * @see org.eclipse.viatra.cep.vepl.vepl.Import
   * @generated
   */
  public Adapter createImportAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link org.eclipse.viatra.cep.vepl.vepl.GenericImport <em>Generic Import</em>}'.
   * <!-- begin-user-doc -->
   * This default implementation returns null so that we can easily ignore cases;
   * it's useful to ignore a case when inheritance will catch all the cases anyway.
   * <!-- end-user-doc -->
   * @return the new adapter.
   * @see org.eclipse.viatra.cep.vepl.vepl.GenericImport
   * @generated
   */
  public Adapter createGenericImportAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link org.eclipse.viatra.cep.vepl.vepl.QueryImport <em>Query Import</em>}'.
   * <!-- begin-user-doc -->
   * This default implementation returns null so that we can easily ignore cases;
   * it's useful to ignore a case when inheritance will catch all the cases anyway.
   * <!-- end-user-doc -->
   * @return the new adapter.
   * @see org.eclipse.viatra.cep.vepl.vepl.QueryImport
   * @generated
   */
  public Adapter createQueryImportAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link org.eclipse.viatra.cep.vepl.vepl.ModelElement <em>Model Element</em>}'.
   * <!-- begin-user-doc -->
   * This default implementation returns null so that we can easily ignore cases;
   * it's useful to ignore a case when inheritance will catch all the cases anyway.
   * <!-- end-user-doc -->
   * @return the new adapter.
   * @see org.eclipse.viatra.cep.vepl.vepl.ModelElement
   * @generated
   */
  public Adapter createModelElementAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link org.eclipse.viatra.cep.vepl.vepl.Trait <em>Trait</em>}'.
   * <!-- begin-user-doc -->
   * This default implementation returns null so that we can easily ignore cases;
   * it's useful to ignore a case when inheritance will catch all the cases anyway.
   * <!-- end-user-doc -->
   * @return the new adapter.
   * @see org.eclipse.viatra.cep.vepl.vepl.Trait
   * @generated
   */
  public Adapter createTraitAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link org.eclipse.viatra.cep.vepl.vepl.EventPattern <em>Event Pattern</em>}'.
   * <!-- begin-user-doc -->
   * This default implementation returns null so that we can easily ignore cases;
   * it's useful to ignore a case when inheritance will catch all the cases anyway.
   * <!-- end-user-doc -->
   * @return the new adapter.
   * @see org.eclipse.viatra.cep.vepl.vepl.EventPattern
   * @generated
   */
  public Adapter createEventPatternAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link org.eclipse.viatra.cep.vepl.vepl.AbstractAtomicEventPattern <em>Abstract Atomic Event Pattern</em>}'.
   * <!-- begin-user-doc -->
   * This default implementation returns null so that we can easily ignore cases;
   * it's useful to ignore a case when inheritance will catch all the cases anyway.
   * <!-- end-user-doc -->
   * @return the new adapter.
   * @see org.eclipse.viatra.cep.vepl.vepl.AbstractAtomicEventPattern
   * @generated
   */
  public Adapter createAbstractAtomicEventPatternAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link org.eclipse.viatra.cep.vepl.vepl.AtomicEventPattern <em>Atomic Event Pattern</em>}'.
   * <!-- begin-user-doc -->
   * This default implementation returns null so that we can easily ignore cases;
   * it's useful to ignore a case when inheritance will catch all the cases anyway.
   * <!-- end-user-doc -->
   * @return the new adapter.
   * @see org.eclipse.viatra.cep.vepl.vepl.AtomicEventPattern
   * @generated
   */
  public Adapter createAtomicEventPatternAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link org.eclipse.viatra.cep.vepl.vepl.QueryResultChangeEventPattern <em>Query Result Change Event Pattern</em>}'.
   * <!-- begin-user-doc -->
   * This default implementation returns null so that we can easily ignore cases;
   * it's useful to ignore a case when inheritance will catch all the cases anyway.
   * <!-- end-user-doc -->
   * @return the new adapter.
   * @see org.eclipse.viatra.cep.vepl.vepl.QueryResultChangeEventPattern
   * @generated
   */
  public Adapter createQueryResultChangeEventPatternAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link org.eclipse.viatra.cep.vepl.vepl.ComplexEventPattern <em>Complex Event Pattern</em>}'.
   * <!-- begin-user-doc -->
   * This default implementation returns null so that we can easily ignore cases;
   * it's useful to ignore a case when inheritance will catch all the cases anyway.
   * <!-- end-user-doc -->
   * @return the new adapter.
   * @see org.eclipse.viatra.cep.vepl.vepl.ComplexEventPattern
   * @generated
   */
  public Adapter createComplexEventPatternAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link org.eclipse.viatra.cep.vepl.vepl.Rule <em>Rule</em>}'.
   * <!-- begin-user-doc -->
   * This default implementation returns null so that we can easily ignore cases;
   * it's useful to ignore a case when inheritance will catch all the cases anyway.
   * <!-- end-user-doc -->
   * @return the new adapter.
   * @see org.eclipse.viatra.cep.vepl.vepl.Rule
   * @generated
   */
  public Adapter createRuleAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link org.eclipse.viatra.cep.vepl.vepl.TraitList <em>Trait List</em>}'.
   * <!-- begin-user-doc -->
   * This default implementation returns null so that we can easily ignore cases;
   * it's useful to ignore a case when inheritance will catch all the cases anyway.
   * <!-- end-user-doc -->
   * @return the new adapter.
   * @see org.eclipse.viatra.cep.vepl.vepl.TraitList
   * @generated
   */
  public Adapter createTraitListAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link org.eclipse.viatra.cep.vepl.vepl.TypedParameterList <em>Typed Parameter List</em>}'.
   * <!-- begin-user-doc -->
   * This default implementation returns null so that we can easily ignore cases;
   * it's useful to ignore a case when inheritance will catch all the cases anyway.
   * <!-- end-user-doc -->
   * @return the new adapter.
   * @see org.eclipse.viatra.cep.vepl.vepl.TypedParameterList
   * @generated
   */
  public Adapter createTypedParameterListAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link org.eclipse.viatra.cep.vepl.vepl.TypedParameter <em>Typed Parameter</em>}'.
   * <!-- begin-user-doc -->
   * This default implementation returns null so that we can easily ignore cases;
   * it's useful to ignore a case when inheritance will catch all the cases anyway.
   * <!-- end-user-doc -->
   * @return the new adapter.
   * @see org.eclipse.viatra.cep.vepl.vepl.TypedParameter
   * @generated
   */
  public Adapter createTypedParameterAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link org.eclipse.viatra.cep.vepl.vepl.TypedParameterWithDefaultValue <em>Typed Parameter With Default Value</em>}'.
   * <!-- begin-user-doc -->
   * This default implementation returns null so that we can easily ignore cases;
   * it's useful to ignore a case when inheritance will catch all the cases anyway.
   * <!-- end-user-doc -->
   * @return the new adapter.
   * @see org.eclipse.viatra.cep.vepl.vepl.TypedParameterWithDefaultValue
   * @generated
   */
  public Adapter createTypedParameterWithDefaultValueAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link org.eclipse.viatra.cep.vepl.vepl.TraitTypedParameterList <em>Trait Typed Parameter List</em>}'.
   * <!-- begin-user-doc -->
   * This default implementation returns null so that we can easily ignore cases;
   * it's useful to ignore a case when inheritance will catch all the cases anyway.
   * <!-- end-user-doc -->
   * @return the new adapter.
   * @see org.eclipse.viatra.cep.vepl.vepl.TraitTypedParameterList
   * @generated
   */
  public Adapter createTraitTypedParameterListAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link org.eclipse.viatra.cep.vepl.vepl.ParametrizedQueryReference <em>Parametrized Query Reference</em>}'.
   * <!-- begin-user-doc -->
   * This default implementation returns null so that we can easily ignore cases;
   * it's useful to ignore a case when inheritance will catch all the cases anyway.
   * <!-- end-user-doc -->
   * @return the new adapter.
   * @see org.eclipse.viatra.cep.vepl.vepl.ParametrizedQueryReference
   * @generated
   */
  public Adapter createParametrizedQueryReferenceAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link org.eclipse.viatra.cep.vepl.vepl.ComplexEventExpression <em>Complex Event Expression</em>}'.
   * <!-- begin-user-doc -->
   * This default implementation returns null so that we can easily ignore cases;
   * it's useful to ignore a case when inheritance will catch all the cases anyway.
   * <!-- end-user-doc -->
   * @return the new adapter.
   * @see org.eclipse.viatra.cep.vepl.vepl.ComplexEventExpression
   * @generated
   */
  public Adapter createComplexEventExpressionAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link org.eclipse.viatra.cep.vepl.vepl.ChainedExpression <em>Chained Expression</em>}'.
   * <!-- begin-user-doc -->
   * This default implementation returns null so that we can easily ignore cases;
   * it's useful to ignore a case when inheritance will catch all the cases anyway.
   * <!-- end-user-doc -->
   * @return the new adapter.
   * @see org.eclipse.viatra.cep.vepl.vepl.ChainedExpression
   * @generated
   */
  public Adapter createChainedExpressionAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link org.eclipse.viatra.cep.vepl.vepl.Atom <em>Atom</em>}'.
   * <!-- begin-user-doc -->
   * This default implementation returns null so that we can easily ignore cases;
   * it's useful to ignore a case when inheritance will catch all the cases anyway.
   * <!-- end-user-doc -->
   * @return the new adapter.
   * @see org.eclipse.viatra.cep.vepl.vepl.Atom
   * @generated
   */
  public Adapter createAtomAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link org.eclipse.viatra.cep.vepl.vepl.AbstractMultiplicity <em>Abstract Multiplicity</em>}'.
   * <!-- begin-user-doc -->
   * This default implementation returns null so that we can easily ignore cases;
   * it's useful to ignore a case when inheritance will catch all the cases anyway.
   * <!-- end-user-doc -->
   * @return the new adapter.
   * @see org.eclipse.viatra.cep.vepl.vepl.AbstractMultiplicity
   * @generated
   */
  public Adapter createAbstractMultiplicityAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link org.eclipse.viatra.cep.vepl.vepl.Timewindow <em>Timewindow</em>}'.
   * <!-- begin-user-doc -->
   * This default implementation returns null so that we can easily ignore cases;
   * it's useful to ignore a case when inheritance will catch all the cases anyway.
   * <!-- end-user-doc -->
   * @return the new adapter.
   * @see org.eclipse.viatra.cep.vepl.vepl.Timewindow
   * @generated
   */
  public Adapter createTimewindowAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link org.eclipse.viatra.cep.vepl.vepl.ParameterizedPatternCall <em>Parameterized Pattern Call</em>}'.
   * <!-- begin-user-doc -->
   * This default implementation returns null so that we can easily ignore cases;
   * it's useful to ignore a case when inheritance will catch all the cases anyway.
   * <!-- end-user-doc -->
   * @return the new adapter.
   * @see org.eclipse.viatra.cep.vepl.vepl.ParameterizedPatternCall
   * @generated
   */
  public Adapter createParameterizedPatternCallAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link org.eclipse.viatra.cep.vepl.vepl.PatternCallParameterList <em>Pattern Call Parameter List</em>}'.
   * <!-- begin-user-doc -->
   * This default implementation returns null so that we can easily ignore cases;
   * it's useful to ignore a case when inheritance will catch all the cases anyway.
   * <!-- end-user-doc -->
   * @return the new adapter.
   * @see org.eclipse.viatra.cep.vepl.vepl.PatternCallParameterList
   * @generated
   */
  public Adapter createPatternCallParameterListAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link org.eclipse.viatra.cep.vepl.vepl.PatternCallParameter <em>Pattern Call Parameter</em>}'.
   * <!-- begin-user-doc -->
   * This default implementation returns null so that we can easily ignore cases;
   * it's useful to ignore a case when inheritance will catch all the cases anyway.
   * <!-- end-user-doc -->
   * @return the new adapter.
   * @see org.eclipse.viatra.cep.vepl.vepl.PatternCallParameter
   * @generated
   */
  public Adapter createPatternCallParameterAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link org.eclipse.viatra.cep.vepl.vepl.ComplexEventOperator <em>Complex Event Operator</em>}'.
   * <!-- begin-user-doc -->
   * This default implementation returns null so that we can easily ignore cases;
   * it's useful to ignore a case when inheritance will catch all the cases anyway.
   * <!-- end-user-doc -->
   * @return the new adapter.
   * @see org.eclipse.viatra.cep.vepl.vepl.ComplexEventOperator
   * @generated
   */
  public Adapter createComplexEventOperatorAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link org.eclipse.viatra.cep.vepl.vepl.BinaryOperator <em>Binary Operator</em>}'.
   * <!-- begin-user-doc -->
   * This default implementation returns null so that we can easily ignore cases;
   * it's useful to ignore a case when inheritance will catch all the cases anyway.
   * <!-- end-user-doc -->
   * @return the new adapter.
   * @see org.eclipse.viatra.cep.vepl.vepl.BinaryOperator
   * @generated
   */
  public Adapter createBinaryOperatorAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link org.eclipse.viatra.cep.vepl.vepl.UnaryOperator <em>Unary Operator</em>}'.
   * <!-- begin-user-doc -->
   * This default implementation returns null so that we can easily ignore cases;
   * it's useful to ignore a case when inheritance will catch all the cases anyway.
   * <!-- end-user-doc -->
   * @return the new adapter.
   * @see org.eclipse.viatra.cep.vepl.vepl.UnaryOperator
   * @generated
   */
  public Adapter createUnaryOperatorAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link org.eclipse.viatra.cep.vepl.vepl.FollowsOperator <em>Follows Operator</em>}'.
   * <!-- begin-user-doc -->
   * This default implementation returns null so that we can easily ignore cases;
   * it's useful to ignore a case when inheritance will catch all the cases anyway.
   * <!-- end-user-doc -->
   * @return the new adapter.
   * @see org.eclipse.viatra.cep.vepl.vepl.FollowsOperator
   * @generated
   */
  public Adapter createFollowsOperatorAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link org.eclipse.viatra.cep.vepl.vepl.OrOperator <em>Or Operator</em>}'.
   * <!-- begin-user-doc -->
   * This default implementation returns null so that we can easily ignore cases;
   * it's useful to ignore a case when inheritance will catch all the cases anyway.
   * <!-- end-user-doc -->
   * @return the new adapter.
   * @see org.eclipse.viatra.cep.vepl.vepl.OrOperator
   * @generated
   */
  public Adapter createOrOperatorAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link org.eclipse.viatra.cep.vepl.vepl.AndOperator <em>And Operator</em>}'.
   * <!-- begin-user-doc -->
   * This default implementation returns null so that we can easily ignore cases;
   * it's useful to ignore a case when inheritance will catch all the cases anyway.
   * <!-- end-user-doc -->
   * @return the new adapter.
   * @see org.eclipse.viatra.cep.vepl.vepl.AndOperator
   * @generated
   */
  public Adapter createAndOperatorAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link org.eclipse.viatra.cep.vepl.vepl.UntilOperator <em>Until Operator</em>}'.
   * <!-- begin-user-doc -->
   * This default implementation returns null so that we can easily ignore cases;
   * it's useful to ignore a case when inheritance will catch all the cases anyway.
   * <!-- end-user-doc -->
   * @return the new adapter.
   * @see org.eclipse.viatra.cep.vepl.vepl.UntilOperator
   * @generated
   */
  public Adapter createUntilOperatorAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link org.eclipse.viatra.cep.vepl.vepl.NegOperator <em>Neg Operator</em>}'.
   * <!-- begin-user-doc -->
   * This default implementation returns null so that we can easily ignore cases;
   * it's useful to ignore a case when inheritance will catch all the cases anyway.
   * <!-- end-user-doc -->
   * @return the new adapter.
   * @see org.eclipse.viatra.cep.vepl.vepl.NegOperator
   * @generated
   */
  public Adapter createNegOperatorAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link org.eclipse.viatra.cep.vepl.vepl.Multiplicity <em>Multiplicity</em>}'.
   * <!-- begin-user-doc -->
   * This default implementation returns null so that we can easily ignore cases;
   * it's useful to ignore a case when inheritance will catch all the cases anyway.
   * <!-- end-user-doc -->
   * @return the new adapter.
   * @see org.eclipse.viatra.cep.vepl.vepl.Multiplicity
   * @generated
   */
  public Adapter createMultiplicityAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link org.eclipse.viatra.cep.vepl.vepl.Infinite <em>Infinite</em>}'.
   * <!-- begin-user-doc -->
   * This default implementation returns null so that we can easily ignore cases;
   * it's useful to ignore a case when inheritance will catch all the cases anyway.
   * <!-- end-user-doc -->
   * @return the new adapter.
   * @see org.eclipse.viatra.cep.vepl.vepl.Infinite
   * @generated
   */
  public Adapter createInfiniteAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link org.eclipse.viatra.cep.vepl.vepl.AtLeastOne <em>At Least One</em>}'.
   * <!-- begin-user-doc -->
   * This default implementation returns null so that we can easily ignore cases;
   * it's useful to ignore a case when inheritance will catch all the cases anyway.
   * <!-- end-user-doc -->
   * @return the new adapter.
   * @see org.eclipse.viatra.cep.vepl.vepl.AtLeastOne
   * @generated
   */
  public Adapter createAtLeastOneAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for the default case.
   * <!-- begin-user-doc -->
   * This default implementation returns null.
   * <!-- end-user-doc -->
   * @return the new adapter.
   * @generated
   */
  public Adapter createEObjectAdapter()
  {
    return null;
  }

} //VeplAdapterFactory
