/**
 */
package org.eclipse.viatra.cep.vepl.vepl;

import org.eclipse.emf.common.util.EList;

import org.eclipse.emf.ecore.EObject;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Trait Typed Parameter List</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.viatra.cep.vepl.vepl.TraitTypedParameterList#getParameters <em>Parameters</em>}</li>
 * </ul>
 *
 * @see org.eclipse.viatra.cep.vepl.vepl.VeplPackage#getTraitTypedParameterList()
 * @model
 * @generated
 */
public interface TraitTypedParameterList extends EObject
{
  /**
   * Returns the value of the '<em><b>Parameters</b></em>' containment reference list.
   * The list contents are of type {@link org.eclipse.viatra.cep.vepl.vepl.TypedParameterWithDefaultValue}.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Parameters</em>' containment reference list isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Parameters</em>' containment reference list.
   * @see org.eclipse.viatra.cep.vepl.vepl.VeplPackage#getTraitTypedParameterList_Parameters()
   * @model containment="true"
   * @generated
   */
  EList<TypedParameterWithDefaultValue> getParameters();

} // TraitTypedParameterList
