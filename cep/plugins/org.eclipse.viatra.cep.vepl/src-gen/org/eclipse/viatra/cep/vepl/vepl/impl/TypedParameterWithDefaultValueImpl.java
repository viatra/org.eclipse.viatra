/**
 */
package org.eclipse.viatra.cep.vepl.vepl.impl;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.NotificationChain;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.InternalEObject;

import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.impl.MinimalEObjectImpl;

import org.eclipse.viatra.cep.vepl.vepl.TypedParameter;
import org.eclipse.viatra.cep.vepl.vepl.TypedParameterWithDefaultValue;
import org.eclipse.viatra.cep.vepl.vepl.VeplPackage;

import org.eclipse.xtext.xbase.XExpression;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Typed Parameter With Default Value</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.viatra.cep.vepl.vepl.impl.TypedParameterWithDefaultValueImpl#getTypedParameter <em>Typed Parameter</em>}</li>
 *   <li>{@link org.eclipse.viatra.cep.vepl.vepl.impl.TypedParameterWithDefaultValueImpl#getValue <em>Value</em>}</li>
 * </ul>
 *
 * @generated
 */
public class TypedParameterWithDefaultValueImpl extends MinimalEObjectImpl.Container implements TypedParameterWithDefaultValue
{
  /**
   * The cached value of the '{@link #getTypedParameter() <em>Typed Parameter</em>}' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getTypedParameter()
   * @generated
   * @ordered
   */
  protected TypedParameter typedParameter;

  /**
   * The cached value of the '{@link #getValue() <em>Value</em>}' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getValue()
   * @generated
   * @ordered
   */
  protected XExpression value;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  protected TypedParameterWithDefaultValueImpl()
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
    return VeplPackage.Literals.TYPED_PARAMETER_WITH_DEFAULT_VALUE;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public TypedParameter getTypedParameter()
  {
    return typedParameter;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public NotificationChain basicSetTypedParameter(TypedParameter newTypedParameter, NotificationChain msgs)
  {
    TypedParameter oldTypedParameter = typedParameter;
    typedParameter = newTypedParameter;
    if (eNotificationRequired())
    {
      ENotificationImpl notification = new ENotificationImpl(this, Notification.SET, VeplPackage.TYPED_PARAMETER_WITH_DEFAULT_VALUE__TYPED_PARAMETER, oldTypedParameter, newTypedParameter);
      if (msgs == null) msgs = notification; else msgs.add(notification);
    }
    return msgs;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public void setTypedParameter(TypedParameter newTypedParameter)
  {
    if (newTypedParameter != typedParameter)
    {
      NotificationChain msgs = null;
      if (typedParameter != null)
        msgs = ((InternalEObject)typedParameter).eInverseRemove(this, EOPPOSITE_FEATURE_BASE - VeplPackage.TYPED_PARAMETER_WITH_DEFAULT_VALUE__TYPED_PARAMETER, null, msgs);
      if (newTypedParameter != null)
        msgs = ((InternalEObject)newTypedParameter).eInverseAdd(this, EOPPOSITE_FEATURE_BASE - VeplPackage.TYPED_PARAMETER_WITH_DEFAULT_VALUE__TYPED_PARAMETER, null, msgs);
      msgs = basicSetTypedParameter(newTypedParameter, msgs);
      if (msgs != null) msgs.dispatch();
    }
    else if (eNotificationRequired())
      eNotify(new ENotificationImpl(this, Notification.SET, VeplPackage.TYPED_PARAMETER_WITH_DEFAULT_VALUE__TYPED_PARAMETER, newTypedParameter, newTypedParameter));
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public XExpression getValue()
  {
    return value;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public NotificationChain basicSetValue(XExpression newValue, NotificationChain msgs)
  {
    XExpression oldValue = value;
    value = newValue;
    if (eNotificationRequired())
    {
      ENotificationImpl notification = new ENotificationImpl(this, Notification.SET, VeplPackage.TYPED_PARAMETER_WITH_DEFAULT_VALUE__VALUE, oldValue, newValue);
      if (msgs == null) msgs = notification; else msgs.add(notification);
    }
    return msgs;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public void setValue(XExpression newValue)
  {
    if (newValue != value)
    {
      NotificationChain msgs = null;
      if (value != null)
        msgs = ((InternalEObject)value).eInverseRemove(this, EOPPOSITE_FEATURE_BASE - VeplPackage.TYPED_PARAMETER_WITH_DEFAULT_VALUE__VALUE, null, msgs);
      if (newValue != null)
        msgs = ((InternalEObject)newValue).eInverseAdd(this, EOPPOSITE_FEATURE_BASE - VeplPackage.TYPED_PARAMETER_WITH_DEFAULT_VALUE__VALUE, null, msgs);
      msgs = basicSetValue(newValue, msgs);
      if (msgs != null) msgs.dispatch();
    }
    else if (eNotificationRequired())
      eNotify(new ENotificationImpl(this, Notification.SET, VeplPackage.TYPED_PARAMETER_WITH_DEFAULT_VALUE__VALUE, newValue, newValue));
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
      case VeplPackage.TYPED_PARAMETER_WITH_DEFAULT_VALUE__TYPED_PARAMETER:
        return basicSetTypedParameter(null, msgs);
      case VeplPackage.TYPED_PARAMETER_WITH_DEFAULT_VALUE__VALUE:
        return basicSetValue(null, msgs);
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
      case VeplPackage.TYPED_PARAMETER_WITH_DEFAULT_VALUE__TYPED_PARAMETER:
        return getTypedParameter();
      case VeplPackage.TYPED_PARAMETER_WITH_DEFAULT_VALUE__VALUE:
        return getValue();
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
      case VeplPackage.TYPED_PARAMETER_WITH_DEFAULT_VALUE__TYPED_PARAMETER:
        setTypedParameter((TypedParameter)newValue);
        return;
      case VeplPackage.TYPED_PARAMETER_WITH_DEFAULT_VALUE__VALUE:
        setValue((XExpression)newValue);
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
      case VeplPackage.TYPED_PARAMETER_WITH_DEFAULT_VALUE__TYPED_PARAMETER:
        setTypedParameter((TypedParameter)null);
        return;
      case VeplPackage.TYPED_PARAMETER_WITH_DEFAULT_VALUE__VALUE:
        setValue((XExpression)null);
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
      case VeplPackage.TYPED_PARAMETER_WITH_DEFAULT_VALUE__TYPED_PARAMETER:
        return typedParameter != null;
      case VeplPackage.TYPED_PARAMETER_WITH_DEFAULT_VALUE__VALUE:
        return value != null;
    }
    return super.eIsSet(featureID);
  }

} //TypedParameterWithDefaultValueImpl
