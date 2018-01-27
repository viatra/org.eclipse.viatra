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

import org.eclipse.viatra.cep.vepl.vepl.ContextEnum;
import org.eclipse.viatra.cep.vepl.vepl.EventModel;
import org.eclipse.viatra.cep.vepl.vepl.Import;
import org.eclipse.viatra.cep.vepl.vepl.ModelElement;
import org.eclipse.viatra.cep.vepl.vepl.VeplPackage;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Event Model</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.viatra.cep.vepl.vepl.impl.EventModelImpl#getName <em>Name</em>}</li>
 *   <li>{@link org.eclipse.viatra.cep.vepl.vepl.impl.EventModelImpl#getImports <em>Imports</em>}</li>
 *   <li>{@link org.eclipse.viatra.cep.vepl.vepl.impl.EventModelImpl#getContext <em>Context</em>}</li>
 *   <li>{@link org.eclipse.viatra.cep.vepl.vepl.impl.EventModelImpl#getModelElements <em>Model Elements</em>}</li>
 * </ul>
 *
 * @generated
 */
public class EventModelImpl extends MinimalEObjectImpl.Container implements EventModel
{
  /**
   * The default value of the '{@link #getName() <em>Name</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getName()
   * @generated
   * @ordered
   */
  protected static final String NAME_EDEFAULT = null;

  /**
   * The cached value of the '{@link #getName() <em>Name</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getName()
   * @generated
   * @ordered
   */
  protected String name = NAME_EDEFAULT;

  /**
   * The cached value of the '{@link #getImports() <em>Imports</em>}' containment reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getImports()
   * @generated
   * @ordered
   */
  protected EList<Import> imports;

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
   * The cached value of the '{@link #getModelElements() <em>Model Elements</em>}' containment reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getModelElements()
   * @generated
   * @ordered
   */
  protected EList<ModelElement> modelElements;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  protected EventModelImpl()
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
    return VeplPackage.Literals.EVENT_MODEL;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public String getName()
  {
    return name;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public void setName(String newName)
  {
    String oldName = name;
    name = newName;
    if (eNotificationRequired())
      eNotify(new ENotificationImpl(this, Notification.SET, VeplPackage.EVENT_MODEL__NAME, oldName, name));
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EList<Import> getImports()
  {
    if (imports == null)
    {
      imports = new EObjectContainmentEList<Import>(Import.class, this, VeplPackage.EVENT_MODEL__IMPORTS);
    }
    return imports;
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
      eNotify(new ENotificationImpl(this, Notification.SET, VeplPackage.EVENT_MODEL__CONTEXT, oldContext, context));
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EList<ModelElement> getModelElements()
  {
    if (modelElements == null)
    {
      modelElements = new EObjectContainmentEList<ModelElement>(ModelElement.class, this, VeplPackage.EVENT_MODEL__MODEL_ELEMENTS);
    }
    return modelElements;
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
      case VeplPackage.EVENT_MODEL__IMPORTS:
        return ((InternalEList<?>)getImports()).basicRemove(otherEnd, msgs);
      case VeplPackage.EVENT_MODEL__MODEL_ELEMENTS:
        return ((InternalEList<?>)getModelElements()).basicRemove(otherEnd, msgs);
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
      case VeplPackage.EVENT_MODEL__NAME:
        return getName();
      case VeplPackage.EVENT_MODEL__IMPORTS:
        return getImports();
      case VeplPackage.EVENT_MODEL__CONTEXT:
        return getContext();
      case VeplPackage.EVENT_MODEL__MODEL_ELEMENTS:
        return getModelElements();
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
      case VeplPackage.EVENT_MODEL__NAME:
        setName((String)newValue);
        return;
      case VeplPackage.EVENT_MODEL__IMPORTS:
        getImports().clear();
        getImports().addAll((Collection<? extends Import>)newValue);
        return;
      case VeplPackage.EVENT_MODEL__CONTEXT:
        setContext((ContextEnum)newValue);
        return;
      case VeplPackage.EVENT_MODEL__MODEL_ELEMENTS:
        getModelElements().clear();
        getModelElements().addAll((Collection<? extends ModelElement>)newValue);
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
      case VeplPackage.EVENT_MODEL__NAME:
        setName(NAME_EDEFAULT);
        return;
      case VeplPackage.EVENT_MODEL__IMPORTS:
        getImports().clear();
        return;
      case VeplPackage.EVENT_MODEL__CONTEXT:
        setContext(CONTEXT_EDEFAULT);
        return;
      case VeplPackage.EVENT_MODEL__MODEL_ELEMENTS:
        getModelElements().clear();
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
      case VeplPackage.EVENT_MODEL__NAME:
        return NAME_EDEFAULT == null ? name != null : !NAME_EDEFAULT.equals(name);
      case VeplPackage.EVENT_MODEL__IMPORTS:
        return imports != null && !imports.isEmpty();
      case VeplPackage.EVENT_MODEL__CONTEXT:
        return context != CONTEXT_EDEFAULT;
      case VeplPackage.EVENT_MODEL__MODEL_ELEMENTS:
        return modelElements != null && !modelElements.isEmpty();
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
    result.append(" (name: ");
    result.append(name);
    result.append(", context: ");
    result.append(context);
    result.append(')');
    return result.toString();
  }

} //EventModelImpl
