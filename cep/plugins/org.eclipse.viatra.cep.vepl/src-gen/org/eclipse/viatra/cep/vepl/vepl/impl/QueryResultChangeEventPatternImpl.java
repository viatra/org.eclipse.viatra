/**
 */
package org.eclipse.viatra.cep.vepl.vepl.impl;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.NotificationChain;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.InternalEObject;

import org.eclipse.emf.ecore.impl.ENotificationImpl;

import org.eclipse.viatra.cep.vepl.vepl.ParametrizedQueryReference;
import org.eclipse.viatra.cep.vepl.vepl.QueryResultChangeEventPattern;
import org.eclipse.viatra.cep.vepl.vepl.QueryResultChangeType;
import org.eclipse.viatra.cep.vepl.vepl.VeplPackage;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Query Result Change Event Pattern</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.viatra.cep.vepl.vepl.impl.QueryResultChangeEventPatternImpl#getQueryReference <em>Query Reference</em>}</li>
 *   <li>{@link org.eclipse.viatra.cep.vepl.vepl.impl.QueryResultChangeEventPatternImpl#getResultChangeType <em>Result Change Type</em>}</li>
 * </ul>
 *
 * @generated
 */
public class QueryResultChangeEventPatternImpl extends AbstractAtomicEventPatternImpl implements QueryResultChangeEventPattern
{
  /**
   * The cached value of the '{@link #getQueryReference() <em>Query Reference</em>}' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getQueryReference()
   * @generated
   * @ordered
   */
  protected ParametrizedQueryReference queryReference;

  /**
   * The default value of the '{@link #getResultChangeType() <em>Result Change Type</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getResultChangeType()
   * @generated
   * @ordered
   */
  protected static final QueryResultChangeType RESULT_CHANGE_TYPE_EDEFAULT = QueryResultChangeType.FOUND;

  /**
   * The cached value of the '{@link #getResultChangeType() <em>Result Change Type</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getResultChangeType()
   * @generated
   * @ordered
   */
  protected QueryResultChangeType resultChangeType = RESULT_CHANGE_TYPE_EDEFAULT;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  protected QueryResultChangeEventPatternImpl()
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
    return VeplPackage.Literals.QUERY_RESULT_CHANGE_EVENT_PATTERN;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public ParametrizedQueryReference getQueryReference()
  {
    return queryReference;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public NotificationChain basicSetQueryReference(ParametrizedQueryReference newQueryReference, NotificationChain msgs)
  {
    ParametrizedQueryReference oldQueryReference = queryReference;
    queryReference = newQueryReference;
    if (eNotificationRequired())
    {
      ENotificationImpl notification = new ENotificationImpl(this, Notification.SET, VeplPackage.QUERY_RESULT_CHANGE_EVENT_PATTERN__QUERY_REFERENCE, oldQueryReference, newQueryReference);
      if (msgs == null) msgs = notification; else msgs.add(notification);
    }
    return msgs;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public void setQueryReference(ParametrizedQueryReference newQueryReference)
  {
    if (newQueryReference != queryReference)
    {
      NotificationChain msgs = null;
      if (queryReference != null)
        msgs = ((InternalEObject)queryReference).eInverseRemove(this, EOPPOSITE_FEATURE_BASE - VeplPackage.QUERY_RESULT_CHANGE_EVENT_PATTERN__QUERY_REFERENCE, null, msgs);
      if (newQueryReference != null)
        msgs = ((InternalEObject)newQueryReference).eInverseAdd(this, EOPPOSITE_FEATURE_BASE - VeplPackage.QUERY_RESULT_CHANGE_EVENT_PATTERN__QUERY_REFERENCE, null, msgs);
      msgs = basicSetQueryReference(newQueryReference, msgs);
      if (msgs != null) msgs.dispatch();
    }
    else if (eNotificationRequired())
      eNotify(new ENotificationImpl(this, Notification.SET, VeplPackage.QUERY_RESULT_CHANGE_EVENT_PATTERN__QUERY_REFERENCE, newQueryReference, newQueryReference));
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public QueryResultChangeType getResultChangeType()
  {
    return resultChangeType;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public void setResultChangeType(QueryResultChangeType newResultChangeType)
  {
    QueryResultChangeType oldResultChangeType = resultChangeType;
    resultChangeType = newResultChangeType == null ? RESULT_CHANGE_TYPE_EDEFAULT : newResultChangeType;
    if (eNotificationRequired())
      eNotify(new ENotificationImpl(this, Notification.SET, VeplPackage.QUERY_RESULT_CHANGE_EVENT_PATTERN__RESULT_CHANGE_TYPE, oldResultChangeType, resultChangeType));
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
      case VeplPackage.QUERY_RESULT_CHANGE_EVENT_PATTERN__QUERY_REFERENCE:
        return basicSetQueryReference(null, msgs);
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
      case VeplPackage.QUERY_RESULT_CHANGE_EVENT_PATTERN__QUERY_REFERENCE:
        return getQueryReference();
      case VeplPackage.QUERY_RESULT_CHANGE_EVENT_PATTERN__RESULT_CHANGE_TYPE:
        return getResultChangeType();
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
      case VeplPackage.QUERY_RESULT_CHANGE_EVENT_PATTERN__QUERY_REFERENCE:
        setQueryReference((ParametrizedQueryReference)newValue);
        return;
      case VeplPackage.QUERY_RESULT_CHANGE_EVENT_PATTERN__RESULT_CHANGE_TYPE:
        setResultChangeType((QueryResultChangeType)newValue);
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
      case VeplPackage.QUERY_RESULT_CHANGE_EVENT_PATTERN__QUERY_REFERENCE:
        setQueryReference((ParametrizedQueryReference)null);
        return;
      case VeplPackage.QUERY_RESULT_CHANGE_EVENT_PATTERN__RESULT_CHANGE_TYPE:
        setResultChangeType(RESULT_CHANGE_TYPE_EDEFAULT);
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
      case VeplPackage.QUERY_RESULT_CHANGE_EVENT_PATTERN__QUERY_REFERENCE:
        return queryReference != null;
      case VeplPackage.QUERY_RESULT_CHANGE_EVENT_PATTERN__RESULT_CHANGE_TYPE:
        return resultChangeType != RESULT_CHANGE_TYPE_EDEFAULT;
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
    result.append(" (resultChangeType: ");
    result.append(resultChangeType);
    result.append(')');
    return result.toString();
  }

} //QueryResultChangeEventPatternImpl
