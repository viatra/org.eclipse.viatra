/*******************************************************************************
 * Copyright (c) 2010-2018, Gabor Bergmann, IncQuery Labs Ltd.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Gabor Bergmann - initial API and implementation
 *******************************************************************************/
package org.eclipse.viatra.query.runtime.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

import javax.lang.model.type.UnionType;

import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.viatra.query.runtime.emf.types.EClassTransitiveInstancesKey;
import org.eclipse.viatra.query.runtime.emf.types.EStructuralFeatureInstancesKey;
import org.eclipse.viatra.query.runtime.matchers.context.IInputKey;
import org.eclipse.viatra.query.runtime.matchers.context.IQueryRuntimeContextListener;
import org.eclipse.viatra.query.runtime.matchers.scopes.tables.DefaultIndexTable;
import org.eclipse.viatra.query.runtime.matchers.scopes.tables.DisjointUnionTable;
import org.eclipse.viatra.query.runtime.matchers.scopes.tables.IIndexTable;
import org.eclipse.viatra.query.runtime.matchers.scopes.tables.ITableContext;
import org.eclipse.viatra.query.runtime.matchers.scopes.tables.ITableWriterBinary;
import org.eclipse.viatra.query.runtime.matchers.scopes.tables.ITableWriterGeneric;
import org.eclipse.viatra.query.runtime.matchers.scopes.tables.ITableWriterUnary;
import org.eclipse.viatra.query.runtime.matchers.scopes.tables.SimpleBinaryTable;
import org.eclipse.viatra.query.runtime.matchers.scopes.tables.SimpleUnaryTable;
import org.eclipse.viatra.query.runtime.matchers.tuple.Tuple;
import org.eclipse.viatra.query.runtime.matchers.tuple.TupleMask;
import org.eclipse.viatra.query.runtime.matchers.tuple.Tuples;
import org.eclipse.viatra.query.runtime.matchers.util.CollectionsFactory;
import org.eclipse.viatra.query.runtime.matchers.util.CollectionsFactory.MemoryType;
import org.eclipse.viatra.query.runtime.matchers.util.Direction;
import org.eclipse.viatra.query.runtime.matchers.util.IMemory;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameter;
import org.junit.runners.Parameterized.Parameters;

/**
 * @author Gabor Bergmann
 * Tests the behaviour of various {@link TableWriter} implementations.
 */
@RunWith(Parameterized.class)
public class TablesTest {

    private static final int POOL_LIMIT = 7;
    private static final ITableContext CONTEXT = new ITableContext() {
        @Override
        public void logError(String message) {
            fail("Error logged: " + message);
        }
    };    
    private final static Tuple NEVER_USED_UNARY = Tuples.staticArityFlatTupleOf(0);
    private final static Tuple[] UNARY_POOL = {  null, 
            Tuples.staticArityFlatTupleOf(1), 
            Tuples.staticArityFlatTupleOf(2), 
            Tuples.staticArityFlatTupleOf(3), 
            Tuples.staticArityFlatTupleOf(4), 
            Tuples.staticArityFlatTupleOf(5), 
            Tuples.staticArityFlatTupleOf(6), 
            Tuples.staticArityFlatTupleOf(7)
    };
    private final static IInputKey UNARY_KEY = new EClassTransitiveInstancesKey(EcorePackage.eINSTANCE.getEModelElement());
 // private final static Supplier<IIndexTable> UNARY_MULTI_FACTORY = () -> new SimpleUnaryTable<>(UNARY_KEY, CONTEXT, false);
    private final static Supplier<IIndexTable> UNARY_UNIQUE_FACTORY = () -> new SimpleUnaryTable<>(UNARY_KEY, CONTEXT, true);

    private final static Tuple NEVER_USED_BINARY = Tuples.staticArityFlatTupleOf(0,0);
    private final static Tuple[] BINARY_POOL = {  null, 
            Tuples.staticArityFlatTupleOf(1,'a'), 
            Tuples.staticArityFlatTupleOf(2,'b'), 
            Tuples.staticArityFlatTupleOf(3,'c'), 
            Tuples.staticArityFlatTupleOf(1,'b'), 
            Tuples.staticArityFlatTupleOf(2,'c'), 
            Tuples.staticArityFlatTupleOf(3,'a'), 
            Tuples.staticArityFlatTupleOf(2,'a')
    };
    private final static IInputKey BINARY_KEY = new EStructuralFeatureInstancesKey(EcorePackage.eINSTANCE.getEModelElement_EAnnotations());
 // private final static Supplier<IIndexTable> BINARY_MULTI_FACTORY = () -> new SimpleBinaryTable<>(BINARY_KEY, CONTEXT, false);
    private final static Supplier<IIndexTable> BINARY_UNIQUE_FACTORY = () -> new SimpleBinaryTable<>(BINARY_KEY, CONTEXT, true);

    private final static Tuple NEVER_USED_TERNARY = Tuples.staticArityFlatTupleOf(0,0,0);
    private final static Tuple[] TERNARY_POOL = {  null, 
            Tuples.staticArityFlatTupleOf(1,'a','A'), 
            Tuples.staticArityFlatTupleOf(2,'b','B'), 
            Tuples.staticArityFlatTupleOf(3,'c','C'), 
            Tuples.staticArityFlatTupleOf(1,'b','C'), 
            Tuples.staticArityFlatTupleOf(1,'a','C'), 
            Tuples.staticArityFlatTupleOf(1,'a','B'), 
            Tuples.staticArityFlatTupleOf(2,'a','C')
    };
    private final static IInputKey TERNARY_KEY = new IInputKey() {
        @Override
        public boolean isEnumerable() {
            return true;
        }
        
        @Override
        public String getStringID() {
            return "TERNARY";
        }
        
        @Override
        public String getPrettyPrintableName() {
            return "TERNARY";
        }
        
        @Override
        public int getArity() {
            return 3;
        }
    };
 // private static Supplier<IIndexTable> defaultMultiFactory(IInputKey key) { return () -> new DefaultIndexTable(key, CONTEXT, false);}
    private static Supplier<IIndexTable> defaultUniqueFactory(IInputKey key) { return () -> new DefaultIndexTable(key, CONTEXT, true);}
 
    private final static IInputKey TERNARY_KEY_EVEN = new IInputKey() {
        @Override
        public boolean isEnumerable() {
            return true;
        }
        
        @Override
        public String getStringID() {
            return "TERNARY_EVEN";
        }
        
        @Override
        public String getPrettyPrintableName() {
            return "TERNARY_EVEN";
        }
        
        @Override
        public int getArity() {
            return 3;
        }
    };
    private final static IInputKey TERNARY_KEY_ODD = new IInputKey() {
        @Override
        public boolean isEnumerable() {
            return true;
        }
        
        @Override
        public String getStringID() {
            return "TERNARY_ODD";
        }
        
        @Override
        public String getPrettyPrintableName() {
            return "TERNARY_ODD";
        }
        
        @Override
        public int getArity() {
            return 3;
        }
    };
    private static Supplier<IIndexTable> DISJOINT_UNION_FACTORY = () -> {
        DefaultIndexTable tableEven = new DefaultIndexTable(TERNARY_KEY_EVEN, CONTEXT, true);
        DefaultIndexTable tableOdd = new DefaultIndexTable(TERNARY_KEY_ODD, CONTEXT, true);
        DisjointUnionTable disjointUnionTable = new DisjointUnionTable(TERNARY_KEY, CONTEXT);
        disjointUnionTable.addChildTable(tableEven);
        disjointUnionTable.addChildTable(tableOdd);
        return disjointUnionTable;
    };
    
    
    private final static Function<IIndexTable, ITableWriterGeneric> FROM_DEFAULT = (table) -> (ITableWriterGeneric) table; 
    private final static Function<IIndexTable, ITableWriterGeneric> FROM_SIMPLE_UNARY = (table) -> {
        ITableWriterUnary writer = (ITableWriterUnary<?>) table;
        return (direction, row) -> {writer.write(direction, row.get(0));};
    }; 
    private final static Function<IIndexTable, ITableWriterGeneric> FROM_SIMPLE_BINARY = (table) -> {
        ITableWriterBinary writer = (ITableWriterBinary<?, ?>) table;
        return (direction, row) -> {writer.write(direction, row.get(0), row.get(1));};
    }; 
    private final static Function<IIndexTable, ITableWriterGeneric> FROM_DISJOINT_UNION = (table) -> {
        DisjointUnionTable unionTable = (DisjointUnionTable) table;
        ITableWriterGeneric writerEven = (ITableWriterGeneric) unionTable.getChildTables().get(0);
        ITableWriterGeneric writerOdd = (ITableWriterGeneric) unionTable.getChildTables().get(1);
        return (direction, row) -> {
            ((1 == row.hashCode()%2) ? writerOdd : writerEven).write(direction, row);
        };
    }; 
    
    @Parameter(0)
    public int arity;
    @Parameter(1)
    public Tuple[] tuplePool;
    @Parameter(2)
    public Tuple neverUsed;
    @Parameter(3)
    public Class<?> tableClass;
    @Parameter(4)
    public Supplier<IIndexTable> tableFactory;
    @Parameter(5)
    public Function<IIndexTable, ITableWriterGeneric> writerProvider;
    
    @Parameters(name= "{index}: arity {0}, class {3}")
    public static Collection<Object[]> data() {
        return Arrays.asList(
                new Object[] {1, UNARY_POOL,    NEVER_USED_UNARY,   SimpleUnaryTable.class, UNARY_UNIQUE_FACTORY,               FROM_SIMPLE_UNARY},
                new Object[] {1, UNARY_POOL,    NEVER_USED_UNARY,   DefaultIndexTable.class,defaultUniqueFactory(UNARY_KEY),    FROM_DEFAULT},
                new Object[] {2, BINARY_POOL,   NEVER_USED_BINARY,  SimpleBinaryTable.class,BINARY_UNIQUE_FACTORY,              FROM_SIMPLE_BINARY},
                new Object[] {2, BINARY_POOL,   NEVER_USED_BINARY,  DefaultIndexTable.class,defaultUniqueFactory(BINARY_KEY),   FROM_DEFAULT},
                new Object[] {3, TERNARY_POOL,  NEVER_USED_TERNARY, DefaultIndexTable.class,defaultUniqueFactory(TERNARY_KEY),  FROM_DEFAULT},
                new Object[] {3, TERNARY_POOL,  NEVER_USED_TERNARY, DisjointUnionTable.class,DISJOINT_UNION_FACTORY,            FROM_DISJOINT_UNION}
           );
    }
    private final Step[] OPERATION_SEQ_1 = { 
            _add(1), _add(2), _add(3), _add(4), _add(5), _add(6), _add(7), 
            _remove(1), _remove(2), _remove(3), _remove(4), _remove(5), _remove(6), _remove(7)}; 
    private final Step[] OPERATION_SEQ_2 = { 
            _add(1), _add(2), _add(3), _add(4), _add(5), _add(6), _add(7), 
            _remove(2), _remove(3), _remove(4), _remove(5), _remove(6), _remove(7), _remove(1)}; 
    
    
    @Test
    public void tableSeq1() {
        performSequence("seq1", OPERATION_SEQ_1);            
    }
    @Test
    public void tableSeq2() {
        performSequence("seq2", OPERATION_SEQ_2);            
    }
 
    

    private interface Step { 
        public void accept(ITableWriterGeneric writer, IIndexTable table, IMemory<Tuple> expected);
    }

    
    private String stepPrefix;
    public void performSequence(String casePrefix, Step[] operations) {
        assertEquals(POOL_LIMIT + 1, tuplePool.length);
        String seqPrefix = tableClass.getSimpleName()+'/'+arity+'.'+casePrefix;
        
        IMemory<Tuple> expected = CollectionsFactory.createMemory(Object.class, MemoryType.SETS);
        IIndexTable table = tableFactory.get();
        ITableWriterGeneric writer = writerProvider.apply(table);
        
        for (int opIndex = 0; opIndex < operations.length; ++opIndex) {
            @SuppressWarnings("unchecked")
            Step op = (Step)operations[opIndex];

            stepPrefix = String.format("%s[%d]", seqPrefix, opIndex);
            
            op.accept(writer, table, expected);
        }
    }
    
    public Step _remove(int selectorIndex) {
        assertTrue(selectorIndex > 0);
        assertFalse(selectorIndex > POOL_LIMIT);
        return (writer, table, expected) -> {
            Tuple selectedTuple = tuplePool[selectorIndex];
            String messagePrefix = String.format("%s-remove(%d=%s)", stepPrefix, selectorIndex, selectedTuple);

            // REPLICATE BEHAVIOUR 
            boolean changed = expected.removeOne(selectedTuple);

            // SET UP LISTENERS
            List<MockListener> listeners = setupMockListeners(
                    messagePrefix, table, expected, 
                    selectedTuple, Direction.DELETE, changed);

            // PERFORM ACTUAL OPERATION
            writer.write(Direction.DELETE, selectedTuple); 
            
            // CHECK ASSERTIONS
            //assertEquals(messagePrefix+":return", nowAbsent || wasAbsent, returned);
            makeStateAssertions(messagePrefix, table, expected);
            for (MockListener listener: listeners) {
                listener.assertHappenedAndUnregister();
            }
        };
    }
    
    public Step _add(int selectorIndex) {
        assertTrue(selectorIndex > 0);
        assertFalse(selectorIndex > POOL_LIMIT);
        return (writer, table, expected) -> {
            Tuple selectedTuple = tuplePool[selectorIndex];
            String messagePrefix = String.format("%s-add(%d=%s)", stepPrefix, selectorIndex, selectedTuple);
            
            // REPLICATE BEHAVIOUR
            boolean changed = expected.addOne(selectedTuple);

            // SET UP LISTENERS
            List<MockListener> listeners = setupMockListeners(
                    messagePrefix, table, expected, 
                    selectedTuple, Direction.INSERT, changed);

            // PERFORM ACTUAL OPERATION
            writer.write(Direction.INSERT, selectedTuple); 
            
            // CHECK ASSERTIONS
            //assertEquals(messagePrefix+":return", nowAbsent || wasAbsent, returned);
            makeStateAssertions(messagePrefix, table, expected);
            for (MockListener listener: listeners) {
                listener.assertHappenedAndUnregister();
            }
        };
    }

    private void makeStateAssertions(String messagePrefix, IIndexTable table, IMemory<Tuple> expected) {
        for (Tuple tuple : expected.distinctValues()) {
            assertTrue(messagePrefix+":containsTuple/" + tuple, table.containsTuple(tuple));
        }
        assertFalse(messagePrefix+":containsTuple/never=" + neverUsed, table.containsTuple(neverUsed));
        for (TupleMask mask: selectorMasksFromArity()) {
            assertTrue(messagePrefix+":nonrepeating/"+mask,
                    mask.isNonrepeating());
            for (Tuple seedingTuple : expected.distinctValues()) {
                makeAssertionsTuplesForSeed(messagePrefix, table, expected, mask, seedingTuple);
            }
            makeAssertionsTuplesForSeed(messagePrefix, table, expected, mask, neverUsed);
            if (mask.getSize() == mask.getSourceWidth() - 1) {
                int omittedIndex = mask.getFirstOmittedIndex().getAsInt();
                for (Tuple seedingTuple : expected.distinctValues()) {
                    makeAssertionsValuesForSeed(messagePrefix, table, expected, mask, omittedIndex, seedingTuple);
                }
                makeAssertionsValuesForSeed(messagePrefix, table, expected, mask, omittedIndex, neverUsed);
            }
        }
    }
    
    private void makeAssertionsValuesForSeed(String messagePrefix, IIndexTable table, IMemory<Tuple> expected,
            TupleMask mask, int omittedIndex, Tuple seedingTuple) {
        Tuple seed = mask.transform(seedingTuple);
        
        HashSet<Object> expectedSet = new HashSet<Object>();
        for (Tuple candidateTuple : expected.distinctValues()) 
            if (seed.equals(mask.transform(candidateTuple)))
                expectedSet.add(candidateTuple.get(omittedIndex));
        
        HashSet<Object> actualSet = new HashSet<Object>();
        for (Object actualValue : table.enumerateValues(mask, seed)) 
            assertTrue(messagePrefix+":enumerateValues/"+mask+"@"+seed+"/non-duplicate:"+actualValue,
                    actualSet.add(actualValue));
        
        assertEquals(messagePrefix+":enumerateValues/"+mask+"@"+seed, 
                expectedSet, actualSet);
    }
    private void makeAssertionsTuplesForSeed(String messagePrefix, IIndexTable table, IMemory<Tuple> expected,
            TupleMask mask, Tuple seedingTuple) {
        Tuple seed = mask.transform(seedingTuple);
        
        HashSet<Tuple> expectedSet = new HashSet<Tuple>();
        for (Tuple candidateTuple : expected.distinctValues()) 
            if (seed.equals(mask.transform(candidateTuple)))
                expectedSet.add(candidateTuple);
        
        assertEquals(messagePrefix+":countTuples/"+mask+"@"+seed, 
                expectedSet.size(), table.countTuples(mask, seed));
        
        HashSet<Tuple> actualSet = new HashSet<Tuple>();
        for (Tuple actualTuple : table.enumerateTuples(mask, seed)) 
            assertTrue(messagePrefix+":enumerateTuples/"+mask+"@"+seed+"/non-duplicate:"+actualTuple,
                    actualSet.add(actualTuple));
        
        assertEquals(messagePrefix+":enumerateTuples/"+mask+"@"+seed, 
                expectedSet, actualSet);
    }
    
    private Iterable<TupleMask> selectorMasksFromArity() {
        Set<TupleMask> accumulator = new HashSet<>();
        forEachKeepIndicator((keepIndicators) -> accumulator.add(TupleMask.fromKeepIndicators(keepIndicators)));
        return accumulator;
    }
    private void forEachKeepIndicator(Consumer<boolean[]> consumer) {
        boolean[] keepIndicators = new boolean[arity];
        fillKeepIndicatorsFromPosition(keepIndicators, 0, consumer);
    }
    private void fillKeepIndicatorsFromPosition(boolean[] keepIndicators, int position, Consumer<boolean[]> consumer) {
        if (position < arity) {
            keepIndicators[position] = false;
            fillKeepIndicatorsFromPosition(keepIndicators, position+1, consumer);
            keepIndicators[position] = true;
            fillKeepIndicatorsFromPosition(keepIndicators, position+1, consumer);
        } else {
            consumer.accept(keepIndicators);
        }
    }
    
    private List<MockListener> setupMockListeners(String messagePrefix, IIndexTable table,
            IMemory<Tuple> expectedContent, Tuple updateTuple, Direction direction, boolean changed) {
        List<MockListener> listeners = new ArrayList<>();
        listeners.add(new MockListener(table, null, messagePrefix, updateTuple, direction, changed));
        listeners.add(new MockListener(table, neverUsed, messagePrefix, updateTuple, direction, false));
        for (TupleMask mask: selectorMasksFromArity()) {
            for (Tuple seedingTuple : expectedContent.distinctValues()) {
                Tuple seed = mask.keepSelectedIndices(seedingTuple);
                boolean detectable = changed && mask.keepSelectedIndices(updateTuple).equals(seed);
                listeners.add(new MockListener(table, seed, messagePrefix, updateTuple, direction, detectable));
            }
        }
        return listeners;
    }
    private static final class MockListener implements IQueryRuntimeContextListener {
        private final IIndexTable table;
        private final Tuple seed;
        private final IInputKey expectedKey;
        private final String listenerPrefix;
        private final Tuple expectedTuple;
        private final Direction expectedDirection;
        private boolean happened = false;
        private boolean expectedToHappen;

        public MockListener(IIndexTable table, Tuple seed, String externalPrefix, Tuple expectedTuple,
                Direction expectedDirection, boolean expectedToHappen) {
            this.table = table;
            this.seed = seed;
            this.expectedKey = table.getInputKey();
            this.listenerPrefix = externalPrefix+((seed==null)?"listenerGlobal":"listener@"+seed);
            this.expectedTuple = expectedTuple;
            this.expectedDirection = expectedDirection;
            this.expectedToHappen = expectedToHappen;
            
            table.addUpdateListener(seed, this);
        }

        @Override
        public void update(IInputKey actualKey, Tuple actualTuple, boolean isInsertion) {
            assertEquals(listenerPrefix+"/key", expectedKey, actualKey);
            assertEquals(listenerPrefix+"/tuple", expectedTuple, actualTuple);
            assertEquals(listenerPrefix+"/dir", expectedDirection, isInsertion ? Direction.INSERT : Direction.DELETE);
            assertFalse(listenerPrefix+"/duplicate", happened);
            happened = true;
        }
        
        public void assertHappenedAndUnregister() {
            assertEquals(listenerPrefix+"/expectedToHappen", expectedToHappen, happened);
            table.removeUpdateListener(seed, this);
        }
    }
}
