/**
 */
package org.eclipse.viatra.cep.vepl.vepl;

import org.eclipse.emf.common.util.EList;

import org.eclipse.emf.ecore.EObject;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Event Model</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.viatra.cep.vepl.vepl.EventModel#getName <em>Name</em>}</li>
 *   <li>{@link org.eclipse.viatra.cep.vepl.vepl.EventModel#getImports <em>Imports</em>}</li>
 *   <li>{@link org.eclipse.viatra.cep.vepl.vepl.EventModel#getContext <em>Context</em>}</li>
 *   <li>{@link org.eclipse.viatra.cep.vepl.vepl.EventModel#getModelElements <em>Model Elements</em>}</li>
 * </ul>
 *
 * @see org.eclipse.viatra.cep.vepl.vepl.VeplPackage#getEventModel()
 * @model
 * @generated
 */
public interface EventModel extends EObject
{
  /**
   * Returns the value of the '<em><b>Name</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Name</em>' attribute isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Name</em>' attribute.
   * @see #setName(String)
   * @see org.eclipse.viatra.cep.vepl.vepl.VeplPackage#getEventModel_Name()
   * @model
   * @generated
   */
  String getName();

  /**
   * Sets the value of the '{@link org.eclipse.viatra.cep.vepl.vepl.EventModel#getName <em>Name</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>Name</em>' attribute.
   * @see #getName()
   * @generated
   */
  void setName(String value);

  /**
   * Returns the value of the '<em><b>Imports</b></em>' containment reference list.
   * The list contents are of type {@link org.eclipse.viatra.cep.vepl.vepl.Import}.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Imports</em>' containment reference list isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Imports</em>' containment reference list.
   * @see org.eclipse.viatra.cep.vepl.vepl.VeplPackage#getEventModel_Imports()
   * @model containment="true"
   * @generated
   */
  EList<Import> getImports();

  /**
   * Returns the value of the '<em><b>Context</b></em>' attribute.
   * The literals are from the enumeration {@link org.eclipse.viatra.cep.vepl.vepl.ContextEnum}.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Context</em>' attribute isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Context</em>' attribute.
   * @see org.eclipse.viatra.cep.vepl.vepl.ContextEnum
   * @see #setContext(ContextEnum)
   * @see org.eclipse.viatra.cep.vepl.vepl.VeplPackage#getEventModel_Context()
   * @model
   * @generated
   */
  ContextEnum getContext();

  /**
   * Sets the value of the '{@link org.eclipse.viatra.cep.vepl.vepl.EventModel#getContext <em>Context</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>Context</em>' attribute.
   * @see org.eclipse.viatra.cep.vepl.vepl.ContextEnum
   * @see #getContext()
   * @generated
   */
  void setContext(ContextEnum value);

  /**
   * Returns the value of the '<em><b>Model Elements</b></em>' containment reference list.
   * The list contents are of type {@link org.eclipse.viatra.cep.vepl.vepl.ModelElement}.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Model Elements</em>' containment reference list isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Model Elements</em>' containment reference list.
   * @see org.eclipse.viatra.cep.vepl.vepl.VeplPackage#getEventModel_ModelElements()
   * @model containment="true"
   * @generated
   */
  EList<ModelElement> getModelElements();

} // EventModel
