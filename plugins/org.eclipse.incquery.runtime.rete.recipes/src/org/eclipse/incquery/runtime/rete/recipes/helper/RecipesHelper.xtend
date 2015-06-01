/*******************************************************************************
 * Copyright (c) 2004-2014 Gabor Bergmann and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Gabor Bergmann - initial API and implementation
 *******************************************************************************/

package org.eclipse.incquery.runtime.rete.recipes.helper

import org.eclipse.incquery.runtime.rete.recipes.Mask
import org.eclipse.incquery.runtime.rete.recipes.RecipesFactory
import org.eclipse.incquery.runtime.rete.recipes.ReteNodeRecipe

/**
 * Static helper class for easy construction of recipes.
 * @author Bergmann Gabor
 *
 */
class RecipesHelper {
	val static RecipesFactory FACTORY = RecipesFactory.eINSTANCE;
	
	def public static mask(int sourceArity, Iterable<Integer> sourceIndices) {
		FACTORY.createMask() => [
			it.sourceArity = sourceArity
			it.sourceIndices += sourceIndices
		]
	}
	def public static mask(int sourceArity, int... sourceIndices) {
		FACTORY.createMask() => [
			it.sourceArity = sourceArity
			it.sourceIndices += sourceIndices
		]
	}
	
	def public static projectionIndexerRecipe(ReteNodeRecipe parent, Mask mask) {
		FACTORY.createProjectionIndexerRecipe() => [
			it.parent = parent
			it.mask = mask
		]
	}
	
	def public static expressionDefinition(Object evaluator) {
		FACTORY.createExpressionDefinition() => [
			it.evaluator = evaluator
		]
	}
	
	def public static inputRecipe(Object inputKey, String inputKeyID, int arity) {
		FACTORY.createInputRecipe() => [
			it.inputKey = inputKey
			it.keyArity = arity
			it.keyID = inputKeyID
			it.traceInfo = inputKeyID
		]
	}
	
	/** Mask can be null in case no tuple reordering or trimming is needed  */
	def public static inputFilterRecipe(ReteNodeRecipe parent, Object inputKey, String inputKeyID, Mask mask) {
		FACTORY.createInputFilterRecipe() => [
			it.parent = parent
			it.inputKey = inputKey
			it.keyID = inputKeyID
			it.traceInfo = inputKeyID
			it.mask = mask
		]
	}

}