/**
 */
package org.eclipse.incquery.runtime.rete.recipes;


/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Index</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link org.eclipse.incquery.runtime.rete.recipes.Index#getIndex <em>Index</em>}</li>
 * </ul>
 * </p>
 *
 * @see org.eclipse.incquery.runtime.rete.recipes.RecipesPackage#getIndex()
 * @model
 * @generated
 */
public interface Index extends ReteNodeRecipe
{
  /**
   * Returns the value of the '<em><b>Index</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Index</em>' attribute isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Index</em>' attribute.
   * @see #setIndex(int)
   * @see org.eclipse.incquery.runtime.rete.recipes.RecipesPackage#getIndex_Index()
   * @model unique="false"
   * @generated
   */
  int getIndex();

  /**
   * Sets the value of the '{@link org.eclipse.incquery.runtime.rete.recipes.Index#getIndex <em>Index</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>Index</em>' attribute.
   * @see #getIndex()
   * @generated
   */
  void setIndex(int value);

} // Index
