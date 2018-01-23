/*******************************************************************************
 * Copyright (c) 2010-2018, Gabor Bergmann, IncQueryLabs Ltd.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Gabor Bergmann - initial API and implementation
 *******************************************************************************/
package org.eclipse.viatra.query.runtime.matchers.util;

import java.util.Collections;
import java.util.Iterator;
import java.util.NoSuchElementException;

import org.eclipse.viatra.query.runtime.matchers.util.MarkedMemory.MarkedSet;

/**
 * Specialized multimap implementation that saves memory 
 *  by storing singleton value objects (multiplicity 1) instead of multiset buckets 
 *  whenever there is only one value associated with a key.
 * 
 * <p> See specialized {@link ToSetsAbstract}, {@link ToMultisetsAbstract} for various bucket types.
 * 
 * <p> Implemented as a Key->Object map with invariant: <ul>
 * <li> key maps to null if associated with no values; 
 * <li> key maps to a single Value iff it is associated with a single value of multiplicity +1;  
 * <li> key maps to Bucket otherwise
 * </ul>
 * 
 * Note that due to the above invariant, handling +1 and -1 are asymmetric in case of delta maps.
 *
 * <p> Not intended as an API, but rather as a 'base class' for implementors.
 * Realized as an interface with default implementations, instead of an abstract class, 
 *  to ensure that implementors can easily choose a base class such as UnifiedMap to augment. 
 *  
 *  <p> Implementor should inherit from a Map<Key, Object>-like class (primitive map possible) 
 *      and bind the lowLevel* methods accordingly.
 * 
 * @noreference This interface is not intended to be referenced by clients.
 * 
 * 
 * @author Gabor Bergmann
 * @since 2.0
 *
 * 
 */
public interface IMultiLookupAbstract<Key, Value, Bucket extends MarkedMemory<Value>> extends IMultiLookup<Key, Value> {

    // the following methods must be bound to a concrete Map<Key,Object>-like structure (primitive implementation allowed)

    /**
     * Implementor shall bind to the low-level get() or equivalent of the underlying Key-to-Object map
     */
    abstract Object lowLevelGet(Key key);
    
    /**
     * Implementor shall bind to the low-level remove() or equivalent of the underlying Key-to-Object map
     */
    abstract void lowLevelRemove(Key key);
    
    /**
     * Implementor shall bind to the low-level putIfAbsent() or equivalent of the underlying Key-to-Object map
     */
    abstract Object lowLevelPutIfAbsent(Key key, Value value);

    /**
     * Implementor shall bind to the low-level put() or equivalent of the underlying Key-to-Object map
     */
    abstract void lowLevelPut(Key key, Object valueOrBucket);
    
    /**
     * Implementor shall bind to the low-level values() or equivalent of the underlying Key-to-Object map
     */
    abstract Iterable<Object> lowLevelValues();
    
    /**
     * Implementor shall bind to the low-level keySet() or equivalent of the underlying Key-to-Object map
     */
    abstract Iterable<Key> lowLevelKeySet();
    
    /**
     * Implementor shall bind to the low-level size() or equivalent of the underlying Key-to-Object map
     */
    abstract int lowLevelSize();

    
    // generic multi-lookup logic
   
    @Override
    default public Bucket lookup(Key key) {
        Object object = lowLevelGet(key);
        if (object == null) return null;
        if (object instanceof MarkedMemory) return (Bucket) object;
        return yieldSingleton((Value)object);
    }

    @Override
    default public ChangeGranularity addPair(Key key, Value value) {
        Object old = lowLevelPutIfAbsent(key, value);
        boolean keyChange = (old == null);

        if (keyChange) { // key was not present
            return ChangeGranularity.KEY;
        } else { // key was already present
            Bucket bucket;
            if (old instanceof MarkedMemory) { // ... as collection
                bucket = (Bucket) old;
            } else { // ... as singleton
                bucket = createSingletonBucket((Value) old);
                lowLevelPut(key, bucket);
            }
            // will throw if forbidden duplicate, return false if allowed duplicate
            if (addToBucket(bucket, value)) {
                // deltas may become empty or a singleton after addition!
                if (negativesAllowed()) {
                    if (bucket.isEmpty()) { 
                        lowLevelRemove(key);
                        return ChangeGranularity.KEY;
                    } else {
                        handleSingleton(key, bucket);                    
                        return ChangeGranularity.VALUE;
                    }                    
                } return ChangeGranularity.VALUE; 
            } else return ChangeGranularity.DUPLICATE;
        }
    }

    @Override
    default public ChangeGranularity removePair(Key key, Value value) {
        Object old = lowLevelGet(key);
        if (old instanceof MarkedMemory) { // ... as collection
            @SuppressWarnings("unchecked")
            Bucket bucket = (Bucket) old;
            // will throw if removing non-existent, return false if removing duplicate
            if (removeFromBucket(bucket, value)) { 
                handleSingleton(key, bucket);
                return ChangeGranularity.VALUE;
            } else return ChangeGranularity.DUPLICATE;
        } else if (value.equals(old)) { // matching singleton
            lowLevelRemove(key);
            return ChangeGranularity.KEY;
        } else { // different singleton, will produce a delta
            Bucket deltaBucket = createDeltaBucket((Value) old, value); // will throw if no deltas supported
            lowLevelPut(key, deltaBucket);
            return ChangeGranularity.VALUE; // no key change
        }
    }

    default public void handleSingleton(Key key, Bucket bucket) {
        Value remainingSingleton = asSingleton(bucket);
        if (remainingSingleton != null) { // only one remains
            lowLevelPut(key, remainingSingleton);
        }
    }

    @Override
    default public Iterable<Value> distinctValues() {
        return new Iterable<Value>() {
            private final Iterator<Value> EMPTY_ITERATOR = Collections.<Value>emptySet().iterator(); 
            @Override
            public Iterator<Value> iterator() {
                return new Iterator<Value>() {
                    Iterator<Object> bucketIterator = lowLevelValues().iterator();
                    Iterator<Value> elementIterator = EMPTY_ITERATOR;
                    
                    @Override
                    public boolean hasNext() {
                        return (elementIterator.hasNext() || bucketIterator.hasNext());
                    }
                    
                    @Override
                    public Value next() {
                        if (elementIterator.hasNext())
                            return elementIterator.next();
                        else if (bucketIterator.hasNext()) {
                            Object bucket = bucketIterator.next();
                            if (bucket instanceof MarkedMemory) {
                                elementIterator = 
                                        ((MarkedMemory) bucket).distinctValues().iterator();
                                return elementIterator.next();
                            } else {
                                elementIterator = EMPTY_ITERATOR;
                                return (Value) bucket;
                            }
                        } else
                            throw new NoSuchElementException();
                    }

                    /**
                     * Not implemented
                     */
                    public void remove() {
                        throw new UnsupportedOperationException();
                    }

                };
            }
        };
    }
    
    @Override
    default Iterable<Key> distinctKeys() {
        return lowLevelKeySet();
    }
    
    @Override
    default int countKeys() {
        return lowLevelSize();
    }
    
    // the following methods are customized for bucket type

    /**
     * @return iff negative multiplicites are allowed
     */
    abstract boolean negativesAllowed();
    
    /**
     * Increases the multiplicity of the value in the bucket.
     * @return true iff non-duplicate
     * @throws IllegalStateException if disallowed duplication 
     */
    abstract boolean addToBucket(Bucket bucket, Value value);

    /**
     * Decreases the multiplicity of the value in the bucket.
     * @return false if removing duplicate value
     * @throws IllegalStateException if removing non-existing value (unless delta map)
     */
    abstract boolean removeFromBucket(Bucket bucket, Value value);

    /**
     * Checks whether the bucket is a singleton, i.e. it contains a single value with multiplicity +1
     * @return the singleton value, or null if the bucket is not singleton
     */
    abstract Value asSingleton(Bucket bucket);

    /**
     * @return a new bucket consisting of a sole value
     */
    abstract Bucket createSingletonBucket(Value value);
    /**
     * @return a read-only bucket consisting of a sole value, to be returned to the user
     */
    default Bucket yieldSingleton(Value value) {
        return createSingletonBucket(value);
    }

   /**
    *  @param positive the previously existing value, or null if the delta is to contain a single negative tuple
     * @return a new bucket consisting of a delta of two values
     * @throws IllegalStateException if deltas not supported
     */
    abstract Bucket createDeltaBucket(Value positive, Value negative);
    
    /**
     * A multi-lookup whose buckets are sets.
     * 
     * <p> Not intended as an API, but rather as a 'base class' for implementors.
     * Realized as an interface with default implementations, instead of an abstract class, 
     *  to ensure that implementors can easily choose a base class such as UnifiedMap to augment. 
     *  
     *  <p> Implementor should inherit from a Map<Key, Object>-like class (primitive map possible) 
     *      and bind the lowLevel* methods accordingly.
     * 
     * @noreference This interface is not intended to be referenced by clients.
     * @author Gabor Bergmann
     */
    public static interface ToSetsAbstract<Key, Value> extends IMultiLookupAbstract<Key, Value, MarkedMemory.MarkedSet<Value>> {
        /**
         * @return a fresh, empty marked set
         */
        public MarkedSet<Value> createMarkedSet();

        @Override
        default public boolean negativesAllowed() {
            return false;
        }

        @Override
        default public boolean addToBucket(MarkedSet<Value> bucket, Value value) {
            if (bucket.addOne(value)) return true; 
            else throw new IllegalStateException();
        }

        @Override
        default public boolean removeFromBucket(MarkedSet<Value> bucket, Value value) {
            return bucket.removeOne(value);
        }

        @Override
        default public Value asSingleton(MarkedSet<Value> bucket) {
            return bucket.size() == 1 ? bucket.iterator().next() : null;
        }

        @Override
        default public MarkedSet<Value> createSingletonBucket(Value value) {
            MarkedSet<Value> result = createMarkedSet();
            result.addOne(value);
            return result;
        }

        @Override
        default public MarkedSet<Value> createDeltaBucket(Value positive, Value negative) {
            throw new IllegalStateException();
        }        
    }
    
    /**
     * A multi-lookup whose buckets are multisets.
     * 
     * <p> Not intended as an API, but rather as a 'base class' for implementors.
     * Realized as an interface with default implementations, instead of an abstract class, 
     *  to ensure that implementors can easily choose a base class such as UnifiedMap to augment. 
     *  
     *  <p> Implementor should inherit from a Map<Key, Object>-like class (primitive map possible) 
     *      and bind the lowLevel* methods accordingly.
     * 
     * @noreference This interface is not intended to be referenced by clients.
     * @author Gabor Bergmann
     */
    public static interface ToMultisetsAbstract<Key, Value> extends IMultiLookupAbstract<Key, Value, MarkedMemory.MarkedMultiset<Value>> {
        /**
         * @return a fresh, empty marked multiset
         */
        public MarkedMemory.MarkedMultiset<Value> createMarkedMultiset();

        @Override
        default public boolean negativesAllowed() {
            return false;
        }

        @Override
        default public boolean addToBucket(MarkedMemory.MarkedMultiset<Value> bucket, Value value) {
            return bucket.addOne(value); 
        }

        @Override
        default public boolean removeFromBucket(MarkedMemory.MarkedMultiset<Value> bucket, Value value) {
            return bucket.removeOne(value);
        }

        @Override
        default public Value asSingleton(MarkedMemory.MarkedMultiset<Value> bucket) {
            if (bucket.size() != 1) return null;
            Value candidate = bucket.iterator().next();
            return bucket.getCount(candidate) == 1 ? candidate : null;
        }

        @Override
        default public MarkedMemory.MarkedMultiset<Value> createSingletonBucket(Value value) {
            MarkedMemory.MarkedMultiset<Value> result = createMarkedMultiset();
            result.addOne(value);
            return result;
        }

        @Override
        default public MarkedMemory.MarkedMultiset<Value> createDeltaBucket(Value positive, Value negative) {
            throw new IllegalStateException();
        }        
    }
    
    
    // TODO add ToDeltaBagsAbstract

};

