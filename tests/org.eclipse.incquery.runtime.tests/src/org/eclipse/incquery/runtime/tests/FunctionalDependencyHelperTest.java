/*******************************************************************************
 * Copyright (c) 2010-2013, Adam Dudas, Istvan Rath and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Adam Dudas - initial API and implementation
 *******************************************************************************/
package org.eclipse.incquery.runtime.tests;

import static org.eclipse.incquery.runtime.base.api.FunctionalDependencyHelper.closureOf;
import static org.junit.Assert.assertEquals;

import java.util.Map;
import java.util.Set;

import org.eclipse.incquery.runtime.base.api.FunctionalDependencyHelper;
import org.junit.Test;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;

/**
 * Tests for {@link FunctionalDependencyHelper}.
 * 
 * @author Adam Dudas
 * 
 */
public class FunctionalDependencyHelperTest {

    private static final Set<Object> emptySet = ImmutableSet.<Object> of();
    private static final Map<Set<Object>, Set<Object>> emptyMap = ImmutableMap.<Set<Object>, Set<Object>> of();
    private static final Object a = new Object();
    private static final Object b = new Object();
    private static final Object c = new Object();
    private static final Object d = new Object();
    private static final Object e = new Object();
    private static final Map<Set<Object>, Set<Object>> testDependencies = ImmutableMap.<Set<Object>, Set<Object>> of(
            ImmutableSet.of(a, b), ImmutableSet.of(c), // AB -> C
            ImmutableSet.of(a), ImmutableSet.of(d), // A -> D
            ImmutableSet.of(d), ImmutableSet.of(e), // D -> E
            ImmutableSet.of(a, c), ImmutableSet.of(b)); // AC -> B

    @Test
    public void testClosureOfEmptyAttributeSetEmptyDependencySet() {
        assertEquals(emptySet, closureOf(emptySet, emptyMap));
    }

    @Test
    public void testClosureOfEmptyAttributeSet() {
        assertEquals(emptySet, closureOf(emptySet, testDependencies));
    }

    @Test
    public void testClosureOfEmptyDependencySet() {
        Set<Object> X = ImmutableSet.of(a, b, c, d);
        assertEquals(X, closureOf(X, emptyMap));
    }

    @Test
    public void testClosureOf() {
        assertEquals(ImmutableSet.of(a, d, e), closureOf(ImmutableSet.of(a), testDependencies));
        assertEquals(ImmutableSet.of(a, b, c, d, e), closureOf(ImmutableSet.of(a, b), testDependencies));
        assertEquals(ImmutableSet.of(a, b, c, d, e), closureOf(ImmutableSet.of(a, c), testDependencies));
        assertEquals(ImmutableSet.of(b), closureOf(ImmutableSet.of(b), testDependencies));
        assertEquals(ImmutableSet.of(d, e), closureOf(ImmutableSet.of(d), testDependencies));
    }

}
