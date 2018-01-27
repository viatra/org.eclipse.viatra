/**
 */
package org.eclipse.viatra.cep.vepl.vepl.impl;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.NotificationChain;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.InternalEObject;

import org.eclipse.emf.ecore.impl.ENotificationImpl;

import org.eclipse.viatra.cep.vepl.vepl.AtomicEventPattern;
import org.eclipse.viatra.cep.vepl.vepl.TraitList;
import org.eclipse.viatra.cep.vepl.vepl.VeplPackage;

import org.eclipse.xtext.xbase.XExpression;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Atomic Event Pattern</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.viatra.cep.vepl.vepl.impl.AtomicEventPatternImpl#getTraits <em>Traits</em>}</li>
 *   <li>{@link org.eclipse.viatra.cep.vepl.vepl.impl.AtomicEventPatternImpl#getCheckExpression <em>Check Expression</em>}</li>
 * </ul>
 *
 * @generated
 */
public class AtomicEventPatternImpl extends AbstractAtomicEventPatternImpl implements AtomicEventPattern
{
  /**
   * The cached value of the '{@link #getTraits() <em>Traits</em>}' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getTraits()
   * @generated
   * @ordered
   */
  protected TraitList traits;

  /**
   * The cached value of the '{@link #getCheckExpression() <em>Check Expression</em>}' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getCheckExpression()
   * @generated
   * @ordered
   */
  protected XExpression checkExpression;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  protected AtomicEventPatternImpl()
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
    return VeplPackage.Literals.ATOMIC_EVENT_PATTERN;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public TraitList getTraits()
  {
    return traits;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public NotificationChain basicSetTraits(TraitList newTraits, NotificationChain msgs)
  {
    TraitList oldTraits = traits;
    traits = newTraits;
    if (eNotificationRequired())
    {
      ENotificationImpl notification = new ENotificationImpl(this, Notification.SET, VeplPackage.ATOMIC_EVENT_PATTERN__TRAITS, oldTraits, newTraits);
      if (msgs == null) msgs = notification; else msgs.add(notification);
    }
    return msgs;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public void setTraits(TraitList newTraits)
  {
    if (newTraits != traits)
    {
      NotificationChain msgs = null;
      if (traits != null)
        msgs = ((InternalEObject)traits).eInverseRemove(this, EOPPOSITE_FEATURE_BASE - VeplPackage.ATOMIC_EVENT_PATTERN__TRAITS, null, msgs);
      if (newTraits != null)
        msgs = ((InternalEObject)newTraits).eInverseAdd(this, EOPPOSITE_FEATURE_BASE - VeplPackage.ATOMIC_EVENT_PATTERN__TRAITS, null, msgs);
      msgs = basicSetTraits(newTraits, msgs);
      if (msgs != null) msgs.dispatch();
    }
    else if (eNotificationRequired())
      eNotify(new ENotificationImpl(this, Notification.SET, VeplPackage.ATOMIC_EVENT_PATTERN__TRAITS, newTraits, newTraits));
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public XExpression getCheckExpression()
  {
    return checkExpression;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public NotificationChain basicSetCheckExpression(XExpression newCheckExpression, NotificationChain msgs)
  {
    XExpression oldCheckExpression = checkExpression;
    checkExpression = newCheckExpression;
    if (eNotificationRequired())
    {
      ENotificationImpl notification = new ENotificationImpl(this, Notification.SET, VeplPackage.ATOMIC_EVENT_PATTERN__CHECK_EXPRESSION, oldCheckExpression, newCheckExpression);
      if (msgs == null) msgs = notification; else msgs.add(notification);
    }
    return msgs;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public void setCheckExpression(XExpression newCheckExpression)
  {
    if (newCheckExpression != checkExpression)
    {
      NotificationChain msgs = null;
      if (checkExpression != null)
        msgs = ((InternalEObject)checkExpression).eInverseRemove(this, EOPPOSITE_FEATURE_BASE - VeplPackage.ATOMIC_EVENT_PATTERN__CHECK_EXPRESSION, null, msgs);
      if (newCheckExpression != null)
        msgs = ((InternalEObject)newCheckExpression).eInverseAdd(this, EOPPOSITE_FEATURE_BASE - VeplPackage.ATOMIC_EVENT_PATTERN__CHECK_EXPRESSION, null, msgs);
      msgs = basicSetCheckExpression(newCheckExpression, msgs);
      if (msgs != null) msgs.dispatch();
    }
    else if (eNotificationRequired())
      eNotify(new ENotificationImpl(this, Notification.SET, VeplPackage.ATOMIC_EVENT_PATTERN__CHECK_EXPRESSION, newCheckExpression, newCheckExpression));
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
      case VeplPackage.ATOMIC_EVENT_PATTERN__TRAITS:
        return basicSetTraits(null, msgs);
      case VeplPackage.ATOMIC_EVENT_PATTERN__CHECK_EXPRESSION:
        return basicSetCheckExpression(null, msgs);
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
      case VeplPackage.ATOMIC_EVENT_PATTERN__TRAITS:
        return getTraits();
      case VeplPackage.ATOMIC_EVENT_PATTERN__CHECK_EXPRESSION:
        return getCheckExpression();
    }
    return super.eGet(featureID, resolve, coreType);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public void eSet(int featureID, Object newValue)
  {
    switch (featureID)
    {
      case VeplPackage.ATOMIC_EVENT_PATTERN__TRAITS:
        setTraits((TraitList)newValue);
        return;
      case VeplPackage.ATOMIC_EVENT_PATTERN__CHECK_EXPRESSION:
        setCheckExpression((XExpression)newValue);
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
      case VeplPackage.ATOMIC_EVENT_PATTERN__TRAITS:
        setTraits((TraitList)null);
        return;
      case VeplPackage.ATOMIC_EVENT_PATTERN__CHECK_EXPRESSION:
        setCheckExpression((XExpression)null);
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
      case VeplPackage.ATOMIC_EVENT_PATTERN__TRAITS:
        return traits != null;
      case VeplPackage.ATOMIC_EVENT_PATTERN__CHECK_EXPRESSION:
        return checkExpression != null;
    }
    return super.eIsSet(featureID);
  }

} //AtomicEventPatternImpl
