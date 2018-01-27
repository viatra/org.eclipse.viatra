/**
 */
package org.eclipse.viatra.cep.vepl.vepl.impl;

import java.util.Collection;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.NotificationChain;

import org.eclipse.emf.common.util.EList;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.InternalEObject;

import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.impl.MinimalEObjectImpl;

import org.eclipse.emf.ecore.util.EObjectContainmentEList;
import org.eclipse.emf.ecore.util.InternalEList;

import org.eclipse.viatra.cep.vepl.vepl.AbstractMultiplicity;
import org.eclipse.viatra.cep.vepl.vepl.ChainedExpression;
import org.eclipse.viatra.cep.vepl.vepl.ComplexEventExpression;
import org.eclipse.viatra.cep.vepl.vepl.NegOperator;
import org.eclipse.viatra.cep.vepl.vepl.Timewindow;
import org.eclipse.viatra.cep.vepl.vepl.VeplPackage;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Complex Event Expression</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.viatra.cep.vepl.vepl.impl.ComplexEventExpressionImpl#getLeft <em>Left</em>}</li>
 *   <li>{@link org.eclipse.viatra.cep.vepl.vepl.impl.ComplexEventExpressionImpl#getRight <em>Right</em>}</li>
 *   <li>{@link org.eclipse.viatra.cep.vepl.vepl.impl.ComplexEventExpressionImpl#getMultiplicity <em>Multiplicity</em>}</li>
 *   <li>{@link org.eclipse.viatra.cep.vepl.vepl.impl.ComplexEventExpressionImpl#getTimewindow <em>Timewindow</em>}</li>
 *   <li>{@link org.eclipse.viatra.cep.vepl.vepl.impl.ComplexEventExpressionImpl#getNegOperator <em>Neg Operator</em>}</li>
 * </ul>
 *
 * @generated
 */
public class ComplexEventExpressionImpl extends MinimalEObjectImpl.Container implements ComplexEventExpression
{
  /**
   * The cached value of the '{@link #getLeft() <em>Left</em>}' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getLeft()
   * @generated
   * @ordered
   */
  protected ComplexEventExpression left;

  /**
   * The cached value of the '{@link #getRight() <em>Right</em>}' containment reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getRight()
   * @generated
   * @ordered
   */
  protected EList<ChainedExpression> right;

  /**
   * The cached value of the '{@link #getMultiplicity() <em>Multiplicity</em>}' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getMultiplicity()
   * @generated
   * @ordered
   */
  protected AbstractMultiplicity multiplicity;

  /**
   * The cached value of the '{@link #getTimewindow() <em>Timewindow</em>}' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getTimewindow()
   * @generated
   * @ordered
   */
  protected Timewindow timewindow;

  /**
   * The cached value of the '{@link #getNegOperator() <em>Neg Operator</em>}' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getNegOperator()
   * @generated
   * @ordered
   */
  protected NegOperator negOperator;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  protected ComplexEventExpressionImpl()
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
    return VeplPackage.Literals.COMPLEX_EVENT_EXPRESSION;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public ComplexEventExpression getLeft()
  {
    return left;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public NotificationChain basicSetLeft(ComplexEventExpression newLeft, NotificationChain msgs)
  {
    ComplexEventExpression oldLeft = left;
    left = newLeft;
    if (eNotificationRequired())
    {
      ENotificationImpl notification = new ENotificationImpl(this, Notification.SET, VeplPackage.COMPLEX_EVENT_EXPRESSION__LEFT, oldLeft, newLeft);
      if (msgs == null) msgs = notification; else msgs.add(notification);
    }
    return msgs;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public void setLeft(ComplexEventExpression newLeft)
  {
    if (newLeft != left)
    {
      NotificationChain msgs = null;
      if (left != null)
        msgs = ((InternalEObject)left).eInverseRemove(this, EOPPOSITE_FEATURE_BASE - VeplPackage.COMPLEX_EVENT_EXPRESSION__LEFT, null, msgs);
      if (newLeft != null)
        msgs = ((InternalEObject)newLeft).eInverseAdd(this, EOPPOSITE_FEATURE_BASE - VeplPackage.COMPLEX_EVENT_EXPRESSION__LEFT, null, msgs);
      msgs = basicSetLeft(newLeft, msgs);
      if (msgs != null) msgs.dispatch();
    }
    else if (eNotificationRequired())
      eNotify(new ENotificationImpl(this, Notification.SET, VeplPackage.COMPLEX_EVENT_EXPRESSION__LEFT, newLeft, newLeft));
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EList<ChainedExpression> getRight()
  {
    if (right == null)
    {
      right = new EObjectContainmentEList<ChainedExpression>(ChainedExpression.class, this, VeplPackage.COMPLEX_EVENT_EXPRESSION__RIGHT);
    }
    return right;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public AbstractMultiplicity getMultiplicity()
  {
    return multiplicity;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public NotificationChain basicSetMultiplicity(AbstractMultiplicity newMultiplicity, NotificationChain msgs)
  {
    AbstractMultiplicity oldMultiplicity = multiplicity;
    multiplicity = newMultiplicity;
    if (eNotificationRequired())
    {
      ENotificationImpl notification = new ENotificationImpl(this, Notification.SET, VeplPackage.COMPLEX_EVENT_EXPRESSION__MULTIPLICITY, oldMultiplicity, newMultiplicity);
      if (msgs == null) msgs = notification; else msgs.add(notification);
    }
    return msgs;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public void setMultiplicity(AbstractMultiplicity newMultiplicity)
  {
    if (newMultiplicity != multiplicity)
    {
      NotificationChain msgs = null;
      if (multiplicity != null)
        msgs = ((InternalEObject)multiplicity).eInverseRemove(this, EOPPOSITE_FEATURE_BASE - VeplPackage.COMPLEX_EVENT_EXPRESSION__MULTIPLICITY, null, msgs);
      if (newMultiplicity != null)
        msgs = ((InternalEObject)newMultiplicity).eInverseAdd(this, EOPPOSITE_FEATURE_BASE - VeplPackage.COMPLEX_EVENT_EXPRESSION__MULTIPLICITY, null, msgs);
      msgs = basicSetMultiplicity(newMultiplicity, msgs);
      if (msgs != null) msgs.dispatch();
    }
    else if (eNotificationRequired())
      eNotify(new ENotificationImpl(this, Notification.SET, VeplPackage.COMPLEX_EVENT_EXPRESSION__MULTIPLICITY, newMultiplicity, newMultiplicity));
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public Timewindow getTimewindow()
  {
    return timewindow;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public NotificationChain basicSetTimewindow(Timewindow newTimewindow, NotificationChain msgs)
  {
    Timewindow oldTimewindow = timewindow;
    timewindow = newTimewindow;
    if (eNotificationRequired())
    {
      ENotificationImpl notification = new ENotificationImpl(this, Notification.SET, VeplPackage.COMPLEX_EVENT_EXPRESSION__TIMEWINDOW, oldTimewindow, newTimewindow);
      if (msgs == null) msgs = notification; else msgs.add(notification);
    }
    return msgs;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public void setTimewindow(Timewindow newTimewindow)
  {
    if (newTimewindow != timewindow)
    {
      NotificationChain msgs = null;
      if (timewindow != null)
        msgs = ((InternalEObject)timewindow).eInverseRemove(this, EOPPOSITE_FEATURE_BASE - VeplPackage.COMPLEX_EVENT_EXPRESSION__TIMEWINDOW, null, msgs);
      if (newTimewindow != null)
        msgs = ((InternalEObject)newTimewindow).eInverseAdd(this, EOPPOSITE_FEATURE_BASE - VeplPackage.COMPLEX_EVENT_EXPRESSION__TIMEWINDOW, null, msgs);
      msgs = basicSetTimewindow(newTimewindow, msgs);
      if (msgs != null) msgs.dispatch();
    }
    else if (eNotificationRequired())
      eNotify(new ENotificationImpl(this, Notification.SET, VeplPackage.COMPLEX_EVENT_EXPRESSION__TIMEWINDOW, newTimewindow, newTimewindow));
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public NegOperator getNegOperator()
  {
    return negOperator;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public NotificationChain basicSetNegOperator(NegOperator newNegOperator, NotificationChain msgs)
  {
    NegOperator oldNegOperator = negOperator;
    negOperator = newNegOperator;
    if (eNotificationRequired())
    {
      ENotificationImpl notification = new ENotificationImpl(this, Notification.SET, VeplPackage.COMPLEX_EVENT_EXPRESSION__NEG_OPERATOR, oldNegOperator, newNegOperator);
      if (msgs == null) msgs = notification; else msgs.add(notification);
    }
    return msgs;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public void setNegOperator(NegOperator newNegOperator)
  {
    if (newNegOperator != negOperator)
    {
      NotificationChain msgs = null;
      if (negOperator != null)
        msgs = ((InternalEObject)negOperator).eInverseRemove(this, EOPPOSITE_FEATURE_BASE - VeplPackage.COMPLEX_EVENT_EXPRESSION__NEG_OPERATOR, null, msgs);
      if (newNegOperator != null)
        msgs = ((InternalEObject)newNegOperator).eInverseAdd(this, EOPPOSITE_FEATURE_BASE - VeplPackage.COMPLEX_EVENT_EXPRESSION__NEG_OPERATOR, null, msgs);
      msgs = basicSetNegOperator(newNegOperator, msgs);
      if (msgs != null) msgs.dispatch();
    }
    else if (eNotificationRequired())
      eNotify(new ENotificationImpl(this, Notification.SET, VeplPackage.COMPLEX_EVENT_EXPRESSION__NEG_OPERATOR, newNegOperator, newNegOperator));
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
      case VeplPackage.COMPLEX_EVENT_EXPRESSION__LEFT:
        return basicSetLeft(null, msgs);
      case VeplPackage.COMPLEX_EVENT_EXPRESSION__RIGHT:
        return ((InternalEList<?>)getRight()).basicRemove(otherEnd, msgs);
      case VeplPackage.COMPLEX_EVENT_EXPRESSION__MULTIPLICITY:
        return basicSetMultiplicity(null, msgs);
      case VeplPackage.COMPLEX_EVENT_EXPRESSION__TIMEWINDOW:
        return basicSetTimewindow(null, msgs);
      case VeplPackage.COMPLEX_EVENT_EXPRESSION__NEG_OPERATOR:
        return basicSetNegOperator(null, msgs);
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
      case VeplPackage.COMPLEX_EVENT_EXPRESSION__LEFT:
        return getLeft();
      case VeplPackage.COMPLEX_EVENT_EXPRESSION__RIGHT:
        return getRight();
      case VeplPackage.COMPLEX_EVENT_EXPRESSION__MULTIPLICITY:
        return getMultiplicity();
      case VeplPackage.COMPLEX_EVENT_EXPRESSION__TIMEWINDOW:
        return getTimewindow();
      case VeplPackage.COMPLEX_EVENT_EXPRESSION__NEG_OPERATOR:
        return getNegOperator();
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
      case VeplPackage.COMPLEX_EVENT_EXPRESSION__LEFT:
        setLeft((ComplexEventExpression)newValue);
        return;
      case VeplPackage.COMPLEX_EVENT_EXPRESSION__RIGHT:
        getRight().clear();
        getRight().addAll((Collection<? extends ChainedExpression>)newValue);
        return;
      case VeplPackage.COMPLEX_EVENT_EXPRESSION__MULTIPLICITY:
        setMultiplicity((AbstractMultiplicity)newValue);
        return;
      case VeplPackage.COMPLEX_EVENT_EXPRESSION__TIMEWINDOW:
        setTimewindow((Timewindow)newValue);
        return;
      case VeplPackage.COMPLEX_EVENT_EXPRESSION__NEG_OPERATOR:
        setNegOperator((NegOperator)newValue);
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
      case VeplPackage.COMPLEX_EVENT_EXPRESSION__LEFT:
        setLeft((ComplexEventExpression)null);
        return;
      case VeplPackage.COMPLEX_EVENT_EXPRESSION__RIGHT:
        getRight().clear();
        return;
      case VeplPackage.COMPLEX_EVENT_EXPRESSION__MULTIPLICITY:
        setMultiplicity((AbstractMultiplicity)null);
        return;
      case VeplPackage.COMPLEX_EVENT_EXPRESSION__TIMEWINDOW:
        setTimewindow((Timewindow)null);
        return;
      case VeplPackage.COMPLEX_EVENT_EXPRESSION__NEG_OPERATOR:
        setNegOperator((NegOperator)null);
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
      case VeplPackage.COMPLEX_EVENT_EXPRESSION__LEFT:
        return left != null;
      case VeplPackage.COMPLEX_EVENT_EXPRESSION__RIGHT:
        return right != null && !right.isEmpty();
      case VeplPackage.COMPLEX_EVENT_EXPRESSION__MULTIPLICITY:
        return multiplicity != null;
      case VeplPackage.COMPLEX_EVENT_EXPRESSION__TIMEWINDOW:
        return timewindow != null;
      case VeplPackage.COMPLEX_EVENT_EXPRESSION__NEG_OPERATOR:
        return negOperator != null;
    }
    return super.eIsSet(featureID);
  }

} //ComplexEventExpressionImpl
