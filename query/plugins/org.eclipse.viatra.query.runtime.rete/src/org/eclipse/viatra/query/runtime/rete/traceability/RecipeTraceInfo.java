/*******************************************************************************
 * Copyright (c) 2010-2014, Bergmann Gabor, Istvan Rath and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Bergmann Gabor - initial API and implementation
 *******************************************************************************/
package org.eclipse.viatra.query.runtime.rete.traceability;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.eclipse.viatra.query.runtime.rete.network.Node;
import org.eclipse.viatra.query.runtime.rete.recipes.ReteNodeRecipe;

/**
 * A trace marker that indicates the recipe for which the node was built.
 * @author Bergmann Gabor
 */
public class RecipeTraceInfo implements TraceInfo {
	public ReteNodeRecipe getRecipe() {return recipe;}
	public List<RecipeTraceInfo> getParentRecipeTraces() {return Collections.unmodifiableList(parentRecipeTraces);}
	@Override 
	public Node getNode() {return node;}
	
	private Node node;
	ReteNodeRecipe recipe;
	ReteNodeRecipe shadowedRecipe;
	ParentTraceList parentRecipeTraces;
	
	
	public RecipeTraceInfo(ReteNodeRecipe recipe, Collection<? extends RecipeTraceInfo> parentRecipeTraces) {
		super();
		this.recipe = recipe;
		this.parentRecipeTraces = ParentTraceList.from(parentRecipeTraces); //Collections.unmodifiableList(new ArrayList<RecipeTraceInfo>(parentRecipeTraces));
	}
	public RecipeTraceInfo(ReteNodeRecipe recipe, RecipeTraceInfo... parentRecipeTraces) {
		this(recipe, Arrays.asList(parentRecipeTraces));
	}
	
	@Override
	public boolean propagateToIndexerParent() {return false;}
	@Override
	public boolean propagateFromIndexerToSupplierParent() {return false;}
	@Override
	public boolean propagateFromStandardNodeToSupplierParent() {return false;}
	@Override
	public boolean propagateToProductionNodeParentAlso() {return false;}
	@Override 
	public void assignNode(Node node) {this.node = node;}
	
	public static class ParentTraceList extends ArrayList<RecipeTraceInfo> {
		private static final long serialVersionUID = 8530268272318311419L;
		
		public static ParentTraceList from(Collection<? extends RecipeTraceInfo> parentRecipeTraces) {
			if (parentRecipeTraces instanceof ParentTraceList) {
				// We do not copy, merely refer. this way, modifications flow through if recursion is resolved later
				return (ParentTraceList) parentRecipeTraces;
			} else {
				ParentTraceList result = new ParentTraceList();
				result.addAll(parentRecipeTraces);
				return result;
			}
		}
	}

	/**
	 * @param knownRecipe a known recipe that is equivalent to the current recipe
	 */
	public void shadowWithEquivalentRecipe(ReteNodeRecipe knownRecipe) {
		this.shadowedRecipe = this.recipe;
		this.recipe = knownRecipe;
	}
	
	/**
	 * Get original recipe shadowed by an equivalent
	 */
	public ReteNodeRecipe getShadowedRecipe() {
		return shadowedRecipe;
	}
	
	
}