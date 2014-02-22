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
package org.eclipse.incquery.runtime.rete.traceability;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

import org.eclipse.incquery.runtime.rete.network.Node;
import org.eclipse.incquery.runtime.rete.recipes.ReteNodeRecipe;

/**
 * A trace marker that indicates the recipe for which the node was built.
 * @author Bergmann Gabor
 */
public class RecipeTraceInfo implements TraceInfo {
	public ReteNodeRecipe getRecipe() {return recipe;}
	public Collection<RecipeTraceInfo> getParentRecipeTraces() {return parentRecipeTraces;}
	@Override 
	public Node getNode() {return node;}
	
	private Node node;
	ReteNodeRecipe recipe;
	Collection<RecipeTraceInfo> parentRecipeTraces;
	
	
	public RecipeTraceInfo(ReteNodeRecipe recipe, Collection<RecipeTraceInfo> parentRecipeTraces) {
		super();
		this.recipe = recipe;
		this.parentRecipeTraces = Collections.unmodifiableList(new ArrayList<RecipeTraceInfo>(parentRecipeTraces));
	}
	public RecipeTraceInfo(ReteNodeRecipe recipe, RecipeTraceInfo... parentRecipeTraces) {
		super();
		this.recipe = recipe;
		this.parentRecipeTraces = Collections.unmodifiableList(Arrays.asList(parentRecipeTraces));
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
}