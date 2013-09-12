/**
 */
package org.eclipse.incquery.runtime.rete.recipes.impl;

import java.util.Collection;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.NotificationChain;

import org.eclipse.emf.common.util.EList;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.InternalEObject;

import org.eclipse.emf.ecore.impl.ENotificationImpl;

import org.eclipse.emf.ecore.util.EObjectContainmentEList;
import org.eclipse.emf.ecore.util.InternalEList;

import org.eclipse.incquery.runtime.rete.recipes.Index;
import org.eclipse.incquery.runtime.rete.recipes.InequalityFilterRecipe;
import org.eclipse.incquery.runtime.rete.recipes.RecipesPackage;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Inequality Filter Recipe</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link org.eclipse.incquery.runtime.rete.recipes.impl.InequalityFilterRecipeImpl#getSubject <em>Subject</em>}</li>
 *   <li>{@link org.eclipse.incquery.runtime.rete.recipes.impl.InequalityFilterRecipeImpl#getInequals <em>Inequals</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class InequalityFilterRecipeImpl extends FilterRecipeImpl implements InequalityFilterRecipe
{
  /**
   * The cached value of the '{@link #getSubject() <em>Subject</em>}' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getSubject()
   * @generated
   * @ordered
   */
  protected Index subject;

  /**
   * The cached value of the '{@link #getInequals() <em>Inequals</em>}' containment reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getInequals()
   * @generated
   * @ordered
   */
  protected EList<Index> inequals;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  protected InequalityFilterRecipeImpl()
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
    return RecipesPackage.Literals.INEQUALITY_FILTER_RECIPE;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public Index getSubject()
  {
    return subject;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public NotificationChain basicSetSubject(Index newSubject, NotificationChain msgs)
  {
    Index oldSubject = subject;
    subject = newSubject;
    if (eNotificationRequired())
    {
      ENotificationImpl notification = new ENotificationImpl(this, Notification.SET, RecipesPackage.INEQUALITY_FILTER_RECIPE__SUBJECT, oldSubject, newSubject);
      if (msgs == null) msgs = notification; else msgs.add(notification);
    }
    return msgs;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public void setSubject(Index newSubject)
  {
    if (newSubject != subject)
    {
      NotificationChain msgs = null;
      if (subject != null)
        msgs = ((InternalEObject)subject).eInverseRemove(this, EOPPOSITE_FEATURE_BASE - RecipesPackage.INEQUALITY_FILTER_RECIPE__SUBJECT, null, msgs);
      if (newSubject != null)
        msgs = ((InternalEObject)newSubject).eInverseAdd(this, EOPPOSITE_FEATURE_BASE - RecipesPackage.INEQUALITY_FILTER_RECIPE__SUBJECT, null, msgs);
      msgs = basicSetSubject(newSubject, msgs);
      if (msgs != null) msgs.dispatch();
    }
    else if (eNotificationRequired())
      eNotify(new ENotificationImpl(this, Notification.SET, RecipesPackage.INEQUALITY_FILTER_RECIPE__SUBJECT, newSubject, newSubject));
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EList<Index> getInequals()
  {
    if (inequals == null)
    {
      inequals = new EObjectContainmentEList<Index>(Index.class, this, RecipesPackage.INEQUALITY_FILTER_RECIPE__INEQUALS);
    }
    return inequals;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public NotificationChain eInverseRemove(InternalEObject otherEnd, int featureID, NotificationChain msgs)
  {
    switch (featureID)
    {
      case RecipesPackage.INEQUALITY_FILTER_RECIPE__SUBJECT:
        return basicSetSubject(null, msgs);
      case RecipesPackage.INEQUALITY_FILTER_RECIPE__INEQUALS:
        return ((InternalEList<?>)getInequals()).basicRemove(otherEnd, msgs);
    }
    return super.eInverseRemove(otherEnd, featureID, msgs);
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
      case RecipesPackage.INEQUALITY_FILTER_RECIPE__SUBJECT:
        return getSubject();
      case RecipesPackage.INEQUALITY_FILTER_RECIPE__INEQUALS:
        return getInequals();
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
      case RecipesPackage.INEQUALITY_FILTER_RECIPE__SUBJECT:
        setSubject((Index)newValue);
        return;
      case RecipesPackage.INEQUALITY_FILTER_RECIPE__INEQUALS:
        getInequals().clear();
        getInequals().addAll((Collection<? extends Index>)newValue);
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
      case RecipesPackage.INEQUALITY_FILTER_RECIPE__SUBJECT:
        setSubject((Index)null);
        return;
      case RecipesPackage.INEQUALITY_FILTER_RECIPE__INEQUALS:
        getInequals().clear();
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
      case RecipesPackage.INEQUALITY_FILTER_RECIPE__SUBJECT:
        return subject != null;
      case RecipesPackage.INEQUALITY_FILTER_RECIPE__INEQUALS:
        return inequals != null && !inequals.isEmpty();
    }
    return super.eIsSet(featureID);
  }

} //InequalityFilterRecipeImpl
