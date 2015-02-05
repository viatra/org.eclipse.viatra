/*******************************************************************************
 * Copyright (c) 2004-2014, Istvan David, Istvan Rath and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Istvan David - initial API and implementation
 *******************************************************************************/

package org.eclipse.viatra.cep.core.engine.compiler;

import java.util.List;

import org.eclipse.emf.common.util.ECollections;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;

/**
 * 
 * Helper class for calculating permutations for {@link List}s of type T.
 * 
 * @author Istvan David
 * 
 * @param <T>
 *            the type of the elements to be permutated
 */
public class PermutationsHelper<T> {
    private List<List<T>> permutations = Lists.newArrayList();

    /**
     * 
     * @param elements
     *            the elements to be permutated
     * @param fromElement
     *            the fist element of the list the permutation should be calculated from (i.e. the prefix)
     * @return a {@link List} of permutations, i.e. a {@link List} of {@link List}s
     */
    public List<List<T>> getAll(List<T> elements, int fromElement) {
        permute(elements, fromElement);
        return permutations;
    }

    /**
     * Shorthand method for {@link #getAll(List, int)}: fromElement==0.
     * 
     * @param elements
     *            the elements to be permutated
     * @return a {@link List} of permutations, i.e. a {@link List} of {@link List}s
     */
    public List<List<T>> getAll(List<T> elements) {
        return getAll(elements, 0);
    }

    private void permute(List<T> elements, int fromElement) {
        Preconditions.checkArgument(fromElement >= 0);
        Preconditions.checkArgument(elements != null);

        if (fromElement == elements.size() - 1) {
            List<T> permutation = Lists.newArrayList(elements);
            permutations.add(permutation);
            return;
        }
        for (int i = fromElement; i < elements.size(); i++) {
            ECollections.move(elements, i, fromElement);
            getAll(elements, fromElement + 1);
            ECollections.move(elements, fromElement, i);
        }

    }
}
