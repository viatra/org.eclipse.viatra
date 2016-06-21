/*******************************************************************************
 * Copyright (c) 2010-2016, Gabor Bergmann, Istvan Rath and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Gabor Bergmann - initial API and implementation
 *******************************************************************************/
package org.eclipse.viatra.query.runtime.rete.recipes.helper;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Set;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.viatra.query.runtime.rete.recipes.RecipesPackage;
import org.eclipse.viatra.query.runtime.rete.recipes.ReteNodeRecipe;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

/**
 * Stores a set of known <em>canonical</em> recipes, each representing a disjoint equivalence class of recipes, modulo
 * {@link #isEquivalentRecipe(ReteNodeRecipe, ReteNodeRecipe)}.
 * 
 * @author Gabor Bergmann
 * @since 1.3
 *
 */
public class RecipeRecognizer {
    private static long nextRecipeEquivalenceClassID = 0;

    /**
     * if EcoreUtil.equals(recipe1, recipe2), only one of them will be included here
     */
    Map<EClass, Set<ReteNodeRecipe>> canonicalRecipesByClass = Maps.newHashMap();
    Map<Long, ReteNodeRecipe> canonicalRecipeByEquivalenceClassID = Maps.newHashMap();

    /**
     * Recognizes when an equivalent canonical recipe is already known.
     * 
     * @return an equivalent canonical recipe, or the null if no known equivalent found
     */
    public ReteNodeRecipe peekCanonicalRecipe(final ReteNodeRecipe recipe) {
        // equivalence class already known
        for (Long classID : recipe.getEquivalenceClassIDs()) {
            ReteNodeRecipe knownRecipe = canonicalRecipeByEquivalenceClassID.get(classID);
            if (knownRecipe != null)
                return knownRecipe;
        }

        // equivalence class not known, but maybe equivalent recipe still
        // available
        Collection<ReteNodeRecipe> sameClassRecipes = getSameClassCanonicalRecipes(recipe);
        for (ReteNodeRecipe knownRecipe : sameClassRecipes) {
            if (isEquivalentRecipe(recipe, knownRecipe)) {
                // FOUND EQUIVALENT RECIPE
                recipe.getEquivalenceClassIDs().add(knownRecipe.getEquivalenceClassIDs().get(0));
                return knownRecipe;
            }
        }

        return null;
    }

    /**
     * This recipe will be remembered as a canonical recipe. Method maintains both internal data structures and the
     * equivalence class attribute of the recipe. PRECONDITION: {@link #peekCanonicalRecipe(ReteNodeRecipe)} must return
     * null or the recipe itself
     */
    public void makeCanonical(final ReteNodeRecipe recipe) {
        // this is a canonical recipe, chosen representative of its new
        // equivalence class
        if (recipe.getEquivalenceClassIDs().isEmpty()) {
            recipe.getEquivalenceClassIDs().add(nextRecipeEquivalenceClassID++);
        }
        for (Long classID : recipe.getEquivalenceClassIDs()) {
            canonicalRecipeByEquivalenceClassID.put(classID, recipe);
        }
        getSameClassCanonicalRecipes(recipe).add(recipe);
    }

    /**
     * Ensures that there is an equivalent canonical recipe; if none is known yet, this recipe will be remembered as
     * canonical.
     * 
     * @return an equivalent canonical recipe; the argument recipe itself (which is made canonical) if no known
     *         equivalent found
     */
    public ReteNodeRecipe canonicalizeRecipe(final ReteNodeRecipe recipe) {
        ReteNodeRecipe knownRecipe = peekCanonicalRecipe(recipe);
        if (knownRecipe == null) {
            knownRecipe = recipe;
            makeCanonical(recipe);
        }
        return knownRecipe;
    }

    /**
     * @return true iff recipe is a canonical recipe
     */
    public boolean isKnownCanonicalRecipe(final ReteNodeRecipe recipe) {
        if (recipe.getEquivalenceClassIDs().isEmpty()) {
            return false;
        }
        ReteNodeRecipe knownRecipe = canonicalRecipeByEquivalenceClassID.get(recipe.getEquivalenceClassIDs().get(0));
        return recipe == knownRecipe;
    }

    private Set<ReteNodeRecipe> getSameClassCanonicalRecipes(final ReteNodeRecipe recipe) {
        Set<ReteNodeRecipe> sameClassRecipes = canonicalRecipesByClass.get(recipe.eClass());
        if (sameClassRecipes == null) {
            sameClassRecipes = Sets.newHashSet();
            canonicalRecipesByClass.put(recipe.eClass(), sameClassRecipes);
        }
        return sameClassRecipes;
    }

    private boolean isEquivalentRecipe(ReteNodeRecipe recipe, ReteNodeRecipe knownRecipe) {
        return new EqualityHelper().equals(recipe, knownRecipe);
    }

    // TODO reuse in more cases later, e.g. switching join node parents, etc.
    private static class EqualityHelper extends EcoreUtil.EqualityHelper {

        private static final long serialVersionUID = -8841971394686015188L;
        
        final static EAttribute reteNodeRecipe_EquivalenceClassIDs = RecipesPackage.eINSTANCE
                .getReteNodeRecipe_EquivalenceClassIDs();

        @Override
        protected boolean haveEqualFeature(EObject eObject1, EObject eObject2, EStructuralFeature feature) {
            // ignore differences in this attribute, as it may only be assigned
            // after the equivalence check
            if (reteNodeRecipe_EquivalenceClassIDs.equals(feature))
                return true;

            // fallback to general comparison
            return super.haveEqualFeature(eObject1, eObject2, feature);
        }

        @Override
        public boolean equals(EObject eObject1, EObject eObject2) {
            // short-circuit if already known to be equivalent
            if (eObject1 instanceof ReteNodeRecipe) {
                if (eObject2 instanceof ReteNodeRecipe) {
                    EList<Long> eqClassIDs1 = ((ReteNodeRecipe) eObject1).getEquivalenceClassIDs();
                    EList<Long> eqClassIDs2 = ((ReteNodeRecipe) eObject2).getEquivalenceClassIDs();

                    if (!Collections.disjoint(eqClassIDs1, eqClassIDs2))
                        return true;
                }
            }

            // fallback to general comparison
            return super.equals(eObject1, eObject2);
        }
    }
}