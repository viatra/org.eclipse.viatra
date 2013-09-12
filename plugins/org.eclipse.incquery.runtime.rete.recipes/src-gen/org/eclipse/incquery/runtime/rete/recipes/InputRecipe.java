/**
 */
package org.eclipse.incquery.runtime.rete.recipes;


/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Input Recipe</b></em>'.
 * <!-- end-user-doc -->
 *
 * <!-- begin-model-doc -->
 * **** Input and output ****
 * <!-- end-model-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link org.eclipse.incquery.runtime.rete.recipes.InputRecipe#getTypeIdentifier <em>Type Identifier</em>}</li>
 * </ul>
 * </p>
 *
 * @see org.eclipse.incquery.runtime.rete.recipes.RecipesPackage#getInputRecipe()
 * @model abstract="true"
 * @generated
 */
public interface InputRecipe extends ReteNodeRecipe
{
  /**
   * Returns the value of the '<em><b>Type Identifier</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Type Identifier</em>' attribute isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Type Identifier</em>' attribute.
   * @see #setTypeIdentifier(String)
   * @see org.eclipse.incquery.runtime.rete.recipes.RecipesPackage#getInputRecipe_TypeIdentifier()
   * @model unique="false"
   * @generated
   */
  String getTypeIdentifier();

  /**
   * Sets the value of the '{@link org.eclipse.incquery.runtime.rete.recipes.InputRecipe#getTypeIdentifier <em>Type Identifier</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>Type Identifier</em>' attribute.
   * @see #getTypeIdentifier()
   * @generated
   */
  void setTypeIdentifier(String value);

} // InputRecipe
