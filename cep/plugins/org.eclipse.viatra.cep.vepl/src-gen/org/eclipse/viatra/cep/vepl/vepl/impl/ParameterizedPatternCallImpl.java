/**
 */
package org.eclipse.viatra.cep.vepl.vepl.impl;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.NotificationChain;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.InternalEObject;

import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.impl.MinimalEObjectImpl;

import org.eclipse.viatra.cep.vepl.vepl.EventPattern;
import org.eclipse.viatra.cep.vepl.vepl.ParameterizedPatternCall;
import org.eclipse.viatra.cep.vepl.vepl.PatternCallParameterList;
import org.eclipse.viatra.cep.vepl.vepl.VeplPackage;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Parameterized Pattern Call</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.viatra.cep.vepl.vepl.impl.ParameterizedPatternCallImpl#getEventPattern <em>Event Pattern</em>}</li>
 *   <li>{@link org.eclipse.viatra.cep.vepl.vepl.impl.ParameterizedPatternCallImpl#getParameterList <em>Parameter List</em>}</li>
 * </ul>
 *
 * @generated
 */
public class ParameterizedPatternCallImpl extends MinimalEObjectImpl.Container implements ParameterizedPatternCall
{
  /**
   * The cached value of the '{@link #getEventPattern() <em>Event Pattern</em>}' reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getEventPattern()
   * @generated
   * @ordered
   */
  protected EventPattern eventPattern;

  /**
   * The cached value of the '{@link #getParameterList() <em>Parameter List</em>}' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getParameterList()
   * @generated
   * @ordered
   */
  protected PatternCallParameterList parameterList;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  protected ParameterizedPatternCallImpl()
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
    return VeplPackage.Literals.PARAMETERIZED_PATTERN_CALL;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EventPattern getEventPattern()
  {
    if (eventPattern != null && eventPattern.eIsProxy())
    {
      InternalEObject oldEventPattern = (InternalEObject)eventPattern;
      eventPattern = (EventPattern)eResolveProxy(oldEventPattern);
      if (eventPattern != oldEventPattern)
      {
        if (eNotificationRequired())
          eNotify(new ENotificationImpl(this, Notification.RESOLVE, VeplPackage.PARAMETERIZED_PATTERN_CALL__EVENT_PATTERN, oldEventPattern, eventPattern));
      }
    }
    return eventPattern;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EventPattern basicGetEventPattern()
  {
    return eventPattern;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public void setEventPattern(EventPattern newEventPattern)
  {
    EventPattern oldEventPattern = eventPattern;
    eventPattern = newEventPattern;
    if (eNotificationRequired())
      eNotify(new ENotificationImpl(this, Notification.SET, VeplPackage.PARAMETERIZED_PATTERN_CALL__EVENT_PATTERN, oldEventPattern, eventPattern));
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public PatternCallParameterList getParameterList()
  {
    return parameterList;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public NotificationChain basicSetParameterList(PatternCallParameterList newParameterList, NotificationChain msgs)
  {
    PatternCallParameterList oldParameterList = parameterList;
    parameterList = newParameterList;
    if (eNotificationRequired())
    {
      ENotificationImpl notification = new ENotificationImpl(this, Notification.SET, VeplPackage.PARAMETERIZED_PATTERN_CALL__PARAMETER_LIST, oldParameterList, newParameterList);
      if (msgs == null) msgs = notification; else msgs.add(notification);
    }
    return msgs;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public void setParameterList(PatternCallParameterList newParameterList)
  {
    if (newParameterList != parameterList)
    {
      NotificationChain msgs = null;
      if (parameterList != null)
        msgs = ((InternalEObject)parameterList).eInverseRemove(this, EOPPOSITE_FEATURE_BASE - VeplPackage.PARAMETERIZED_PATTERN_CALL__PARAMETER_LIST, null, msgs);
      if (newParameterList != null)
        msgs = ((InternalEObject)newParameterList).eInverseAdd(this, EOPPOSITE_FEATURE_BASE - VeplPackage.PARAMETERIZED_PATTERN_CALL__PARAMETER_LIST, null, msgs);
      msgs = basicSetParameterList(newParameterList, msgs);
      if (msgs != null) msgs.dispatch();
    }
    else if (eNotificationRequired())
      eNotify(new ENotificationImpl(this, Notification.SET, VeplPackage.PARAMETERIZED_PATTERN_CALL__PARAMETER_LIST, newParameterList, newParameterList));
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
      case VeplPackage.PARAMETERIZED_PATTERN_CALL__PARAMETER_LIST:
        return basicSetParameterList(null, msgs);
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
      case VeplPackage.PARAMETERIZED_PATTERN_CALL__EVENT_PATTERN:
        if (resolve) return getEventPattern();
        return basicGetEventPattern();
      case VeplPackage.PARAMETERIZED_PATTERN_CALL__PARAMETER_LIST:
        return getParameterList();
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
      case VeplPackage.PARAMETERIZED_PATTERN_CALL__EVENT_PATTERN:
        setEventPattern((EventPattern)newValue);
        return;
      case VeplPackage.PARAMETERIZED_PATTERN_CALL__PARAMETER_LIST:
        setParameterList((PatternCallParameterList)newValue);
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
      case VeplPackage.PARAMETERIZED_PATTERN_CALL__EVENT_PATTERN:
        setEventPattern((EventPattern)null);
        return;
      case VeplPackage.PARAMETERIZED_PATTERN_CALL__PARAMETER_LIST:
        setParameterList((PatternCallParameterList)null);
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
      case VeplPackage.PARAMETERIZED_PATTERN_CALL__EVENT_PATTERN:
        return eventPattern != null;
      case VeplPackage.PARAMETERIZED_PATTERN_CALL__PARAMETER_LIST:
        return parameterList != null;
    }
    return super.eIsSet(featureID);
  }

} //ParameterizedPatternCallImpl
