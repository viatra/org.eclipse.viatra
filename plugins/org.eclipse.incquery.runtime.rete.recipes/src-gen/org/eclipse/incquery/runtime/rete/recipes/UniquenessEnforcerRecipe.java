/**
 */
package org.eclipse.incquery.runtime.rete.recipes;

import org.eclipse.emf.common.util.EList;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Uniqueness Enforcer Recipe</b></em>'.
 * <!-- end-user-doc -->
 *
 * <!-- begin-model-doc -->
 * Represents nodes that enforce tuple uniqueness, i.e. filter out
 * duplicate input tuples for output.
 * <!-- end-model-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link org.eclipse.incquery.runtime.rete.recipes.UniquenessEnforcerRecipe#getParents <em>Parents</em>}</li>
 * </ul>
 * </p>
 *
 * @see org.eclipse.incquery.runtime.rete.recipes.RecipesPackage#getUniquenessEnforcerRecipe()
 * @model
 * @generated
 */
public interface UniquenessEnforcerRecipe extends ReteNodeRecipe
{
  /**
   * Returns the value of the '<em><b>Parents</b></em>' reference list.
   * The list contents are of type {@link org.eclipse.incquery.runtime.rete.recipes.ReteNodeRecipe}.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Parents</em>' reference list isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Parents</em>' reference list.
   * @see org.eclipse.incquery.runtime.rete.recipes.RecipesPackage#getUniquenessEnforcerRecipe_Parents()
   * @model
   * @generated
   */
  EList<ReteNodeRecipe> getParents();

} // UniquenessEnforcerRecipe
