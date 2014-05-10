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
package org.eclipse.incquery.runtime.rete.recipes.helper;

import com.google.common.collect.Iterables;
import org.eclipse.emf.common.util.EList;
import org.eclipse.incquery.runtime.rete.recipes.BinaryInputRecipe;
import org.eclipse.incquery.runtime.rete.recipes.ExpressionDefinition;
import org.eclipse.incquery.runtime.rete.recipes.Mask;
import org.eclipse.incquery.runtime.rete.recipes.ProjectionIndexerRecipe;
import org.eclipse.incquery.runtime.rete.recipes.RecipesFactory;
import org.eclipse.incquery.runtime.rete.recipes.ReteNodeRecipe;
import org.eclipse.incquery.runtime.rete.recipes.TypeInputRecipe;
import org.eclipse.incquery.runtime.rete.recipes.UnaryInputRecipe;
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
    final Procedure1<Mask> _function = new Procedure1<Mask>() {
      public void apply(final Mask it) {
        it.setSourceArity(sourceArity);
        EList<Integer> _sourceIndices = it.getSourceIndices();
        Iterables.<Integer>addAll(_sourceIndices, sourceIndices);
      }
    };
    return ObjectExtensions.<Mask>operator_doubleArrow(_createMask, _function);
  }
  
  public static Mask mask(final int sourceArity, final int... sourceIndices) {
    Mask _createMask = RecipesHelper.FACTORY.createMask();
    final Procedure1<Mask> _function = new Procedure1<Mask>() {
      public void apply(final Mask it) {
        it.setSourceArity(sourceArity);
        EList<Integer> _sourceIndices = it.getSourceIndices();
        Iterables.<Integer>addAll(_sourceIndices, ((Iterable<? extends Integer>)Conversions.doWrapArray(sourceIndices)));
      }
    };
    return ObjectExtensions.<Mask>operator_doubleArrow(_createMask, _function);
  }
  
  public static ProjectionIndexerRecipe projectionIndexerRecipe(final ReteNodeRecipe parent, final Mask mask) {
    ProjectionIndexerRecipe _createProjectionIndexerRecipe = RecipesHelper.FACTORY.createProjectionIndexerRecipe();
    final Procedure1<ProjectionIndexerRecipe> _function = new Procedure1<ProjectionIndexerRecipe>() {
      public void apply(final ProjectionIndexerRecipe it) {
        it.setParent(parent);
        it.setMask(mask);
      }
    };
    return ObjectExtensions.<ProjectionIndexerRecipe>operator_doubleArrow(_createProjectionIndexerRecipe, _function);
  }
  
  public static ExpressionDefinition expressionDefinition(final Object evaluator) {
    ExpressionDefinition _createExpressionDefinition = RecipesHelper.FACTORY.createExpressionDefinition();
    final Procedure1<ExpressionDefinition> _function = new Procedure1<ExpressionDefinition>() {
      public void apply(final ExpressionDefinition it) {
        it.setEvaluator(evaluator);
      }
    };
    return ObjectExtensions.<ExpressionDefinition>operator_doubleArrow(_createExpressionDefinition, _function);
  }
  
  public static TypeInputRecipe unaryInputRecipe(final Object typeKey, final String typeName) {
    UnaryInputRecipe _createUnaryInputRecipe = RecipesHelper.FACTORY.createUnaryInputRecipe();
    return RecipesHelper.fillOut(_createUnaryInputRecipe, typeKey, typeName);
  }
  
  public static TypeInputRecipe binaryInputRecipe(final Object typeKey, final String typeName) {
    BinaryInputRecipe _createBinaryInputRecipe = RecipesHelper.FACTORY.createBinaryInputRecipe();
    return RecipesHelper.fillOut(_createBinaryInputRecipe, typeKey, typeName);
  }
  
  private static TypeInputRecipe fillOut(final TypeInputRecipe typeInputRecipe, final Object typeKey, final String typeName) {
    final Procedure1<TypeInputRecipe> _function = new Procedure1<TypeInputRecipe>() {
      public void apply(final TypeInputRecipe it) {
        it.setTypeKey(typeKey);
        it.setTypeName(typeName);
      }
    };
    return ObjectExtensions.<TypeInputRecipe>operator_doubleArrow(typeInputRecipe, _function);
  }
}
