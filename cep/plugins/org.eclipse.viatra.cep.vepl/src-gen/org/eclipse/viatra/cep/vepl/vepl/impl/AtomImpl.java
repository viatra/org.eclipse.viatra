/**
 */
package org.eclipse.viatra.cep.vepl.vepl.impl;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.NotificationChain;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.InternalEObject;

import org.eclipse.emf.ecore.impl.ENotificationImpl;

import org.eclipse.viatra.cep.vepl.vepl.Atom;
import org.eclipse.viatra.cep.vepl.vepl.ParameterizedPatternCall;
import org.eclipse.viatra.cep.vepl.vepl.VeplPackage;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Atom</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.viatra.cep.vepl.vepl.impl.AtomImpl#getPatternCall <em>Pattern Call</em>}</li>
 * </ul>
 *
 * @generated
 */
public class AtomImpl extends ComplexEventExpressionImpl implements Atom
{
  /**
   * The cached value of the '{@link #getPatternCall() <em>Pattern Call</em>}' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getPatternCall()
   * @generated
   * @ordered
   */
  protected ParameterizedPatternCall patternCall;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  protected AtomImpl()
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
    return VeplPackage.Literals.ATOM;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public ParameterizedPatternCall getPatternCall()
  {
    return patternCall;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public NotificationChain basicSetPatternCall(ParameterizedPatternCall newPatternCall, NotificationChain msgs)
  {
    ParameterizedPatternCall oldPatternCall = patternCall;
    patternCall = newPatternCall;
    if (eNotificationRequired())
    {
      ENotificationImpl notification = new ENotificationImpl(this, Notification.SET, VeplPackage.ATOM__PATTERN_CALL, oldPatternCall, newPatternCall);
      if (msgs == null) msgs = notification; else msgs.add(notification);
    }
    return msgs;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public void setPatternCall(ParameterizedPatternCall newPatternCall)
  {
    if (newPatternCall != patternCall)
    {
      NotificationChain msgs = null;
      if (patternCall != null)
        msgs = ((InternalEObject)patternCall).eInverseRemove(this, EOPPOSITE_FEATURE_BASE - VeplPackage.ATOM__PATTERN_CALL, null, msgs);
      if (newPatternCall != null)
        msgs = ((InternalEObject)newPatternCall).eInverseAdd(this, EOPPOSITE_FEATURE_BASE - VeplPackage.ATOM__PATTERN_CALL, null, msgs);
      msgs = basicSetPatternCall(newPatternCall, msgs);
      if (msgs != null) msgs.dispatch();
    }
    else if (eNotificationRequired())
      eNotify(new ENotificationImpl(this, Notification.SET, VeplPackage.ATOM__PATTERN_CALL, newPatternCall, newPatternCall));
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
      case VeplPackage.ATOM__PATTERN_CALL:
        return basicSetPatternCall(null, msgs);
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
      case VeplPackage.ATOM__PATTERN_CALL:
        return getPatternCall();
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
      case VeplPackage.ATOM__PATTERN_CALL:
        setPatternCall((ParameterizedPatternCall)newValue);
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
      case VeplPackage.ATOM__PATTERN_CALL:
        setPatternCall((ParameterizedPatternCall)null);
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
      case VeplPackage.ATOM__PATTERN_CALL:
        return patternCall != null;
    }
    return super.eIsSet(featureID);
  }

} //AtomImpl
