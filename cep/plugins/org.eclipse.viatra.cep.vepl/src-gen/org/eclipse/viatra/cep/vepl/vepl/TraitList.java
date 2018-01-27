/**
 */
package org.eclipse.viatra.cep.vepl.vepl;

import org.eclipse.emf.common.util.EList;

import org.eclipse.emf.ecore.EObject;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Trait List</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.viatra.cep.vepl.vepl.TraitList#getTraits <em>Traits</em>}</li>
 * </ul>
 *
 * @see org.eclipse.viatra.cep.vepl.vepl.VeplPackage#getTraitList()
 * @model
 * @generated
 */
public interface TraitList extends EObject
{
  /**
   * Returns the value of the '<em><b>Traits</b></em>' reference list.
   * The list contents are of type {@link org.eclipse.viatra.cep.vepl.vepl.Trait}.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Traits</em>' reference list isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Traits</em>' reference list.
   * @see org.eclipse.viatra.cep.vepl.vepl.VeplPackage#getTraitList_Traits()
   * @model
   * @generated
   */
  EList<Trait> getTraits();

} // TraitList
