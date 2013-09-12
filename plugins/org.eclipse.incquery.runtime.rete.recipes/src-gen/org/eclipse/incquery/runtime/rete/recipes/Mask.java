/**
 */
package org.eclipse.incquery.runtime.rete.recipes;

import org.eclipse.emf.common.util.EList;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Mask</b></em>'.
 * <!-- end-user-doc -->
 *
 * <!-- begin-model-doc -->
 * ** helper concepts: masks and indices ****
 * <!-- end-model-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link org.eclipse.incquery.runtime.rete.recipes.Mask#getSourceIndices <em>Source Indices</em>}</li>
 *   <li>{@link org.eclipse.incquery.runtime.rete.recipes.Mask#getSourceArity <em>Source Arity</em>}</li>
 * </ul>
 * </p>
 *
 * @see org.eclipse.incquery.runtime.rete.recipes.RecipesPackage#getMask()
 * @model
 * @generated
 */
public interface Mask extends ReteNodeRecipe
{
  /**
   * Returns the value of the '<em><b>Source Indices</b></em>' containment reference list.
   * The list contents are of type {@link org.eclipse.incquery.runtime.rete.recipes.Index}.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Source Indices</em>' containment reference list isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Source Indices</em>' containment reference list.
   * @see org.eclipse.incquery.runtime.rete.recipes.RecipesPackage#getMask_SourceIndices()
   * @model containment="true"
   * @generated
   */
  EList<Index> getSourceIndices();

  /**
   * Returns the value of the '<em><b>Source Arity</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Source Arity</em>' attribute isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Source Arity</em>' attribute.
   * @see #setSourceArity(int)
   * @see org.eclipse.incquery.runtime.rete.recipes.RecipesPackage#getMask_SourceArity()
   * @model unique="false"
   * @generated
   */
  int getSourceArity();

  /**
   * Sets the value of the '{@link org.eclipse.incquery.runtime.rete.recipes.Mask#getSourceArity <em>Source Arity</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>Source Arity</em>' attribute.
   * @see #getSourceArity()
   * @generated
   */
  void setSourceArity(int value);

} // Mask
