/**
 */
package org.eclipse.viatra.cep.vepl.vepl.impl;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.NotificationChain;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.InternalEObject;

import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.impl.MinimalEObjectImpl;

import org.eclipse.viatra.cep.vepl.vepl.ParametrizedQueryReference;
import org.eclipse.viatra.cep.vepl.vepl.PatternCallParameterList;
import org.eclipse.viatra.cep.vepl.vepl.VeplPackage;

import org.eclipse.viatra.query.patternlanguage.patternLanguage.Pattern;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Parametrized Query Reference</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.viatra.cep.vepl.vepl.impl.ParametrizedQueryReferenceImpl#getQuery <em>Query</em>}</li>
 *   <li>{@link org.eclipse.viatra.cep.vepl.vepl.impl.ParametrizedQueryReferenceImpl#getParameterList <em>Parameter List</em>}</li>
 * </ul>
 *
 * @generated
 */
public class ParametrizedQueryReferenceImpl extends MinimalEObjectImpl.Container implements ParametrizedQueryReference
{
  /**
   * The cached value of the '{@link #getQuery() <em>Query</em>}' reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getQuery()
   * @generated
   * @ordered
   */
  protected Pattern query;

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
  protected ParametrizedQueryReferenceImpl()
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
    return VeplPackage.Literals.PARAMETRIZED_QUERY_REFERENCE;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public Pattern getQuery()
  {
    if (query != null && query.eIsProxy())
    {
      InternalEObject oldQuery = (InternalEObject)query;
      query = (Pattern)eResolveProxy(oldQuery);
      if (query != oldQuery)
      {
        if (eNotificationRequired())
          eNotify(new ENotificationImpl(this, Notification.RESOLVE, VeplPackage.PARAMETRIZED_QUERY_REFERENCE__QUERY, oldQuery, query));
      }
    }
    return query;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public Pattern basicGetQuery()
  {
    return query;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public void setQuery(Pattern newQuery)
  {
    Pattern oldQuery = query;
    query = newQuery;
    if (eNotificationRequired())
      eNotify(new ENotificationImpl(this, Notification.SET, VeplPackage.PARAMETRIZED_QUERY_REFERENCE__QUERY, oldQuery, query));
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
      ENotificationImpl notification = new ENotificationImpl(this, Notification.SET, VeplPackage.PARAMETRIZED_QUERY_REFERENCE__PARAMETER_LIST, oldParameterList, newParameterList);
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
        msgs = ((InternalEObject)parameterList).eInverseRemove(this, EOPPOSITE_FEATURE_BASE - VeplPackage.PARAMETRIZED_QUERY_REFERENCE__PARAMETER_LIST, null, msgs);
      if (newParameterList != null)
        msgs = ((InternalEObject)newParameterList).eInverseAdd(this, EOPPOSITE_FEATURE_BASE - VeplPackage.PARAMETRIZED_QUERY_REFERENCE__PARAMETER_LIST, null, msgs);
      msgs = basicSetParameterList(newParameterList, msgs);
      if (msgs != null) msgs.dispatch();
    }
    else if (eNotificationRequired())
      eNotify(new ENotificationImpl(this, Notification.SET, VeplPackage.PARAMETRIZED_QUERY_REFERENCE__PARAMETER_LIST, newParameterList, newParameterList));
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
      case VeplPackage.PARAMETRIZED_QUERY_REFERENCE__PARAMETER_LIST:
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
      case VeplPackage.PARAMETRIZED_QUERY_REFERENCE__QUERY:
        if (resolve) return getQuery();
        return basicGetQuery();
      case VeplPackage.PARAMETRIZED_QUERY_REFERENCE__PARAMETER_LIST:
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
      case VeplPackage.PARAMETRIZED_QUERY_REFERENCE__QUERY:
        setQuery((Pattern)newValue);
        return;
      case VeplPackage.PARAMETRIZED_QUERY_REFERENCE__PARAMETER_LIST:
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
      case VeplPackage.PARAMETRIZED_QUERY_REFERENCE__QUERY:
        setQuery((Pattern)null);
        return;
      case VeplPackage.PARAMETRIZED_QUERY_REFERENCE__PARAMETER_LIST:
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
      case VeplPackage.PARAMETRIZED_QUERY_REFERENCE__QUERY:
        return query != null;
      case VeplPackage.PARAMETRIZED_QUERY_REFERENCE__PARAMETER_LIST:
        return parameterList != null;
    }
    return super.eIsSet(featureID);
  }

} //ParametrizedQueryReferenceImpl
