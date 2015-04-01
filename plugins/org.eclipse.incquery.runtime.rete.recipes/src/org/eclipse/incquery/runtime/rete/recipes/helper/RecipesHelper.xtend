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
import org.eclipse.incquery.runtime.rete.recipes.TypeInputRecipe

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
	
	def public static unaryInputRecipe(Object typeKey, String typeName) {
		fillOut(FACTORY.createUnaryInputRecipe(), typeKey, typeName)
	}
	def public static binaryInputRecipe(Object typeKey, String typeName) {
		fillOut(FACTORY.createBinaryInputRecipe(), typeKey, typeName)
	}
	
	private def static fillOut(TypeInputRecipe typeInputRecipe, Object typeKey, String typeName) {
		typeInputRecipe => [
			it.setTypeKey(typeKey)
			it.setTypeName(typeName)
		]
	}

}