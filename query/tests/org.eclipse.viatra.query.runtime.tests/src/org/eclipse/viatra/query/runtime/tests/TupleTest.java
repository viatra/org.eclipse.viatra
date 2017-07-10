/*******************************************************************************
 * Copyright (c) 2010-2017, Gabor Bergmann, Istvan Rath and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Gabor Bergmann - initial API and implementation
 *******************************************************************************/
package org.eclipse.viatra.query.runtime.tests;

import org.eclipse.viatra.query.runtime.matchers.tuple.BaseFlatTuple;
import org.eclipse.viatra.query.runtime.matchers.tuple.BaseLeftInheritanceTuple;
import org.eclipse.viatra.query.runtime.matchers.tuple.FlatTuple;
import org.eclipse.viatra.query.runtime.matchers.tuple.LeftInheritanceTuple;
import org.eclipse.viatra.query.runtime.matchers.tuple.Tuple;
import org.eclipse.viatra.query.runtime.matchers.tuple.Tuples;
import org.junit.Test;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertTrue;

/**
 * Tests for tuples to ensure equivalence between implementations
 * @author Gabor Bergmann
 *
 */
public class TupleTest {
    
    public static final int SPECIALIZED_ARITY_LIMIT = 4;
    public static final Tuple ANCESTOR = Tuples.staticArityFlatTupleOf(-3, -2, -1);
    
    @Test
    public void testFlatTuples() {
        for (int arity = 0; arity <= SPECIALIZED_ARITY_LIMIT + 1; ++arity) {
            Object[] values = new Object[arity];
            for (int i=0; i<arity; ++i) values[i] = i;
            
            boolean highArity = arity > SPECIALIZED_ARITY_LIMIT;
            Tuple tuple = Tuples.flatTupleOf(values);
            
            assertEquals("size", arity, tuple.getSize());            
            assertTrue("baseClass", tuple instanceof BaseFlatTuple);            
            assertEquals("specialized iff low arity", highArity, (tuple instanceof FlatTuple));            
            for (int i=0; i<arity; ++i) {
                assertEquals("get" + i, i, tuple.get(i));
            }
            assertArrayEquals("elements[]", values, tuple.getElements());
            
            Tuple flatTupleReference = Tuples.wideFlatTupleOf(values);
            assertTrue("equality(ft)",  flatTupleReference.equals(tuple));
            assertTrue("equality(spec)", tuple.equals(flatTupleReference));
            assertTrue("equality(other)", tuple.equals(Tuples.flatTupleOf(values)));
            assertEquals("hashCode", flatTupleReference.hashCode(), tuple.hashCode());
        }
    }
    
    @Test
    public void testLeftInheritanceTuples() {
        for (int localArity = 0; localArity <= SPECIALIZED_ARITY_LIMIT + 1; ++localArity) {
            int totalArity = ANCESTOR.getSize() + localArity;
            Object[] allValues   = new Object[totalArity];
            Object[] localValues = new Object[localArity];
            int k;
            for (k=0; k<ANCESTOR.getSize(); ++k) allValues[k] = ANCESTOR.get(k);
            for (int i=0; i<localArity; ++i) localValues[i] = allValues[k++] = i;
            
            boolean highArity = localArity > SPECIALIZED_ARITY_LIMIT;
            Tuple liTuple = Tuples.leftInheritanceTupleOf(ANCESTOR, localValues);
            
            assertEquals("size", totalArity, liTuple.getSize());            
            assertEquals("baseClass", localArity != 0, liTuple instanceof BaseLeftInheritanceTuple);            
            assertEquals("specialized iff low arity", highArity, (liTuple instanceof LeftInheritanceTuple));            
            for (int i=0; i<totalArity; ++i) {
                assertEquals("get" + i, i - ANCESTOR.getSize(), liTuple.get(i));
            }
            assertArrayEquals("elements[]", allValues, liTuple.getElements());
                        
            Tuple liTupleReference = Tuples.wideLeftInheritanceTupleOf(ANCESTOR, localValues);
            assertTrue("equality(lit)",  liTupleReference.equals(liTuple));
            assertTrue("equality(spec)", liTuple.equals(liTupleReference));
            assertTrue("equality(other)", liTuple.equals(Tuples.leftInheritanceTupleOf(ANCESTOR, localValues)));
            assertEquals("hashCode", liTupleReference.hashCode(), liTuple.hashCode());
            
            Tuple flatTupleReference = Tuples.flatTupleOf(allValues);
            assertTrue("equality(lit)",  flatTupleReference.equals(liTuple));
            assertTrue("equality(spec)", liTuple.equals(flatTupleReference));
            assertEquals("hashCode", flatTupleReference.hashCode(), liTuple.hashCode());
        }
    }
}
