/**
 */
package org.eclipse.viatra.cep.vepl.vepl.impl;

import java.util.Collection;

import org.eclipse.emf.common.util.EList;

import org.eclipse.emf.ecore.EClass;

import org.eclipse.emf.ecore.impl.MinimalEObjectImpl;

import org.eclipse.emf.ecore.util.EObjectResolvingEList;

import org.eclipse.viatra.cep.vepl.vepl.Trait;
import org.eclipse.viatra.cep.vepl.vepl.TraitList;
import org.eclipse.viatra.cep.vepl.vepl.VeplPackage;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Trait List</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.viatra.cep.vepl.vepl.impl.TraitListImpl#getTraits <em>Traits</em>}</li>
 * </ul>
 *
 * @generated
 */
public class TraitListImpl extends MinimalEObjectImpl.Container implements TraitList
{
  /**
   * The cached value of the '{@link #getTraits() <em>Traits</em>}' reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getTraits()
   * @generated
   * @ordered
   */
  protected EList<Trait> traits;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  protected TraitListImpl()
  {
    super();
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  protected EClass eStaticClass()
  {
    return VeplPackage.Literals.TRAIT_LIST;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EList<Trait> getTraits()
  {
    if (traits == null)
    {
      traits = new EObjectResolvingEList<Trait>(Trait.class, this, VeplPackage.TRAIT_LIST__TRAITS);
    }
    return traits;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public Object eGet(int featureID, boolean resolve, boolean coreType)
  {
    switch (featureID)
    {
      case VeplPackage.TRAIT_LIST__TRAITS:
        return getTraits();
    }
    return super.eGet(featureID, resolve, coreType);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @SuppressWarnings("unchecked")
  @Override
  public void eSet(int featureID, Object newValue)
  {
    switch (featureID)
    {
      case VeplPackage.TRAIT_LIST__TRAITS:
        getTraits().clear();
        getTraits().addAll((Collection<? extends Trait>)newValue);
        return;
    }
    super.eSet(featureID, newValue);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public void eUnset(int featureID)
  {
    switch (featureID)
    {
      case VeplPackage.TRAIT_LIST__TRAITS:
        getTraits().clear();
        return;
    }
    super.eUnset(featureID);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public boolean eIsSet(int featureID)
  {
    switch (featureID)
    {
      case VeplPackage.TRAIT_LIST__TRAITS:
        return traits != null && !traits.isEmpty();
    }
    return super.eIsSet(featureID);
  }

} //TraitListImpl
