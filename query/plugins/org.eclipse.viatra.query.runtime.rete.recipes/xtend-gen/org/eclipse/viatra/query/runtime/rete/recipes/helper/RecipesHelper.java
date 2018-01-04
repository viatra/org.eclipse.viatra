/**
 * Copyright (c) 2004-2014 Gabor Bergmann and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Gabor Bergmann - initial API and implementation
 */
package org.eclipse.viatra.query.runtime.rete.recipes.helper;

import com.google.common.collect.Iterables;
import org.eclipse.emf.common.util.EList;
import org.eclipse.viatra.query.runtime.rete.recipes.ExpressionDefinition;
import org.eclipse.viatra.query.runtime.rete.recipes.InputFilterRecipe;
import org.eclipse.viatra.query.runtime.rete.recipes.InputRecipe;
import org.eclipse.viatra.query.runtime.rete.recipes.Mask;
import org.eclipse.viatra.query.runtime.rete.recipes.ProjectionIndexerRecipe;
import org.eclipse.viatra.query.runtime.rete.recipes.RecipesFactory;
import org.eclipse.viatra.query.runtime.rete.recipes.ReteNodeRecipe;
import org.eclipse.xtext.xbase.lib.Conversions;
import org.eclipse.xtext.xbase.lib.ObjectExtensions;
import org.eclipse.xtext.xbase.lib.Procedures.Procedure1;

/**
 * Static helper class for easy construction of recipes.
 * @author Bergmann Gabor
 */
@SuppressWarnings("all")
public class RecipesHelper {
  private final static RecipesFactory FACTORY = RecipesFactory.eINSTANCE;
  
  public static Mask mask(final int sourceArity, final Iterable<Integer> sourceIndices) {
    Mask _createMask = RecipesHelper.FACTORY.createMask();
    final Procedure1<Mask> _function = (Mask it) -> {
      it.setSourceArity(sourceArity);
      EList<Integer> _sourceIndices = it.getSourceIndices();
      Iterables.<Integer>addAll(_sourceIndices, sourceIndices);
    };
    return ObjectExtensions.<Mask>operator_doubleArrow(_createMask, _function);
  }
  
  public static Mask mask(final int sourceArity, final int... sourceIndices) {
    Mask _createMask = RecipesHelper.FACTORY.createMask();
    final Procedure1<Mask> _function = (Mask it) -> {
      it.setSourceArity(sourceArity);
      EList<Integer> _sourceIndices = it.getSourceIndices();
      Iterables.<Integer>addAll(_sourceIndices, ((Iterable<? extends Integer>)Conversions.doWrapArray(sourceIndices)));
    };
    return ObjectExtensions.<Mask>operator_doubleArrow(_createMask, _function);
  }
  
  public static ProjectionIndexerRecipe projectionIndexerRecipe(final ReteNodeRecipe parent, final Mask mask) {
    ProjectionIndexerRecipe _createProjectionIndexerRecipe = RecipesHelper.FACTORY.createProjectionIndexerRecipe();
    final Procedure1<ProjectionIndexerRecipe> _function = (ProjectionIndexerRecipe it) -> {
      it.setParent(parent);
      it.setMask(mask);
    };
    return ObjectExtensions.<ProjectionIndexerRecipe>operator_doubleArrow(_createProjectionIndexerRecipe, _function);
  }
  
  public static ExpressionDefinition expressionDefinition(final Object evaluator) {
    ExpressionDefinition _createExpressionDefinition = RecipesHelper.FACTORY.createExpressionDefinition();
    final Procedure1<ExpressionDefinition> _function = (ExpressionDefinition it) -> {
      it.setEvaluator(evaluator);
    };
    return ObjectExtensions.<ExpressionDefinition>operator_doubleArrow(_createExpressionDefinition, _function);
  }
  
  public static InputRecipe inputRecipe(final Object inputKey, final String inputKeyID, final int arity) {
    InputRecipe _createInputRecipe = RecipesHelper.FACTORY.createInputRecipe();
    final Procedure1<InputRecipe> _function = (InputRecipe it) -> {
      it.setInputKey(inputKey);
      it.setKeyArity(arity);
      it.setKeyID(inputKeyID);
      it.setTraceInfo(inputKeyID);
    };
    return ObjectExtensions.<InputRecipe>operator_doubleArrow(_createInputRecipe, _function);
  }
  
  /**
   * Mask can be null in case no tuple reordering or trimming is needed
   */
  public static InputFilterRecipe inputFilterRecipe(final ReteNodeRecipe parent, final Object inputKey, final String inputKeyID, final Mask mask) {
    InputFilterRecipe _createInputFilterRecipe = RecipesHelper.FACTORY.createInputFilterRecipe();
    final Procedure1<InputFilterRecipe> _function = (InputFilterRecipe it) -> {
      it.setParent(parent);
      it.setInputKey(inputKey);
      it.setKeyID(inputKeyID);
      it.setTraceInfo(inputKeyID);
      it.setMask(mask);
    };
    return ObjectExtensions.<InputFilterRecipe>operator_doubleArrow(_createInputFilterRecipe, _function);
  }
}
