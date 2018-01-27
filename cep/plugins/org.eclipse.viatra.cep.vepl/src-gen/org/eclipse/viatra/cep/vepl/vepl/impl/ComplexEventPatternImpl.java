/**
 */
package org.eclipse.viatra.cep.vepl.vepl.impl;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.NotificationChain;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.InternalEObject;

import org.eclipse.emf.ecore.impl.ENotificationImpl;

import org.eclipse.viatra.cep.vepl.vepl.ComplexEventExpression;
import org.eclipse.viatra.cep.vepl.vepl.ComplexEventPattern;
import org.eclipse.viatra.cep.vepl.vepl.ContextEnum;
import org.eclipse.viatra.cep.vepl.vepl.VeplPackage;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Complex Event Pattern</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.viatra.cep.vepl.vepl.impl.ComplexEventPatternImpl#getComplexEventExpression <em>Complex Event Expression</em>}</li>
 *   <li>{@link org.eclipse.viatra.cep.vepl.vepl.impl.ComplexEventPatternImpl#getContext <em>Context</em>}</li>
 * </ul>
 *
 * @generated
 */
public class ComplexEventPatternImpl extends EventPatternImpl implements ComplexEventPattern
{
  /**
   * The cached value of the '{@link #getComplexEventExpression() <em>Complex Event Expression</em>}' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getComplexEventExpression()
   * @generated
   * @ordered
   */
  protected ComplexEventExpression complexEventExpression;

  /**
   * The default value of the '{@link #getContext() <em>Context</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getContext()
   * @generated
   * @ordered
   */
  protected static final ContextEnum CONTEXT_EDEFAULT = ContextEnum.NOT_SET;

  /**
   * The cached value of the '{@link #getContext() <em>Context</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getContext()
   * @generated
   * @ordered
   */
  protected ContextEnum context = CONTEXT_EDEFAULT;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  protected ComplexEventPatternImpl()
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
    return VeplPackage.Literals.COMPLEX_EVENT_PATTERN;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public ComplexEventExpression getComplexEventExpression()
  {
    return complexEventExpression;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public NotificationChain basicSetComplexEventExpression(ComplexEventExpression newComplexEventExpression, NotificationChain msgs)
  {
    ComplexEventExpression oldComplexEventExpression = complexEventExpression;
    complexEventExpression = newComplexEventExpression;
    if (eNotificationRequired())
    {
      ENotificationImpl notification = new ENotificationImpl(this, Notification.SET, VeplPackage.COMPLEX_EVENT_PATTERN__COMPLEX_EVENT_EXPRESSION, oldComplexEventExpression, newComplexEventExpression);
      if (msgs == null) msgs = notification; else msgs.add(notification);
    }
    return msgs;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public void setComplexEventExpression(ComplexEventExpression newComplexEventExpression)
  {
    if (newComplexEventExpression != complexEventExpression)
    {
      NotificationChain msgs = null;
      if (complexEventExpression != null)
        msgs = ((InternalEObject)complexEventExpression).eInverseRemove(this, EOPPOSITE_FEATURE_BASE - VeplPackage.COMPLEX_EVENT_PATTERN__COMPLEX_EVENT_EXPRESSION, null, msgs);
      if (newComplexEventExpression != null)
        msgs = ((InternalEObject)newComplexEventExpression).eInverseAdd(this, EOPPOSITE_FEATURE_BASE - VeplPackage.COMPLEX_EVENT_PATTERN__COMPLEX_EVENT_EXPRESSION, null, msgs);
      msgs = basicSetComplexEventExpression(newComplexEventExpression, msgs);
      if (msgs != null) msgs.dispatch();
    }
    else if (eNotificationRequired())
      eNotify(new ENotificationImpl(this, Notification.SET, VeplPackage.COMPLEX_EVENT_PATTERN__COMPLEX_EVENT_EXPRESSION, newComplexEventExpression, newComplexEventExpression));
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public ContextEnum getContext()
  {
    return context;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public void setContext(ContextEnum newContext)
  {
    ContextEnum oldContext = context;
    context = newContext == null ? CONTEXT_EDEFAULT : newContext;
    if (eNotificationRequired())
      eNotify(new ENotificationImpl(this, Notification.SET, VeplPackage.COMPLEX_EVENT_PATTERN__CONTEXT, oldContext, context));
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
      case VeplPackage.COMPLEX_EVENT_PATTERN__COMPLEX_EVENT_EXPRESSION:
        return basicSetComplexEventExpression(null, msgs);
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
      case VeplPackage.COMPLEX_EVENT_PATTERN__COMPLEX_EVENT_EXPRESSION:
        return getComplexEventExpression();
      case VeplPackage.COMPLEX_EVENT_PATTERN__CONTEXT:
        return getContext();
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
      case VeplPackage.COMPLEX_EVENT_PATTERN__COMPLEX_EVENT_EXPRESSION:
        setComplexEventExpression((ComplexEventExpression)newValue);
        return;
      case VeplPackage.COMPLEX_EVENT_PATTERN__CONTEXT:
        setContext((ContextEnum)newValue);
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
      case VeplPackage.COMPLEX_EVENT_PATTERN__COMPLEX_EVENT_EXPRESSION:
        setComplexEventExpression((ComplexEventExpression)null);
        return;
      case VeplPackage.COMPLEX_EVENT_PATTERN__CONTEXT:
        setContext(CONTEXT_EDEFAULT);
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
      case VeplPackage.COMPLEX_EVENT_PATTERN__COMPLEX_EVENT_EXPRESSION:
        return complexEventExpression != null;
      case VeplPackage.COMPLEX_EVENT_PATTERN__CONTEXT:
        return context != CONTEXT_EDEFAULT;
    }
    return super.eIsSet(featureID);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public String toString()
  {
    if (eIsProxy()) return super.toString();

    StringBuffer result = new StringBuffer(super.toString());
    result.append(" (context: ");
    result.append(context);
    result.append(')');
    return result.toString();
  }

} //ComplexEventPatternImpl
