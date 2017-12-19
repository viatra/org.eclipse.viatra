/*******************************************************************************
 * Copyright (c) 2010-2013, Istvan Rath and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Istvan Rath - initial API and implementation
 *******************************************************************************/
package org.eclipse.viatra.query.runtime.matchers.util;

import java.util.ArrayList;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

/**
 * Factory class used as an accessor to Collections implementations. 
 * @author istvanrath
 */
public final class CollectionsFactory
{
    
    /**
     * Instantiates a new empty map.
     * @since 1.7
     */
    public static <K, V> Map<K, V> createMap() {
        return FRAMEWORK.createMap();
    }

    /**
     * Instantiates a new map with the given initial contents.
     * @since 1.7
     */
    public static <K, V> Map<K, V> createMap(Map<K, V> initial) {
        return FRAMEWORK.createMap(initial);
    }

    /**
     * Instantiates a new empty set.
     * @since 1.7
     */
    public static <E> Set<E> createSet() {
        return FRAMEWORK.createSet();
    }

    /**
     * Instantiates a new set with the given initial contents.
     * @since 1.7
     */
    public static <E> Set<E> createSet(Collection<E> initial) {
        return FRAMEWORK.createSet(initial);
    }

    /**
     * Instantiates an empty set; the key parameter is used to allow using this as a method reference as a
     * {@link Function}, e.g. in {@link Map#computeIfAbsent(Object, Function)}.
     * 
     * @param key
     *            the value of this parameter is ignored
     * @since 2.0
     */
    public static <T> Set<T> emptySet(Object key) {
        return FRAMEWORK.createSet();
    }
    
    /**
     * Instantiates a new empty set. 
     * The set must be of a special type that is not allowed to be instantiated in any way except by a call to this method.
     * @see MarkedSet
     * 
     * @since 1.7
     */
    public static <E> MarkedSet<E> createMarkedSet() {
        return FRAMEWORK.createMarkedSet();
    }

    /**
     * Instantiates a new empty multiset.
     * @since 1.7
     */
    public static <T> IMultiset<T> createMultiset() {
        return FRAMEWORK.createMultiset();
    }

    /**
     * Instantiates an empty multiset; the key parameter is used to allow using this as a method reference as a
     * {@link Function}, e.g. in {@link Map#computeIfAbsent(Object, Function)}.
     * 
     * @param key
     *            the value of this parameter is ignored
     * @since 2.0
     */
    public static <T> IMultiset<T> emptyMultiset(Object key) {
        return FRAMEWORK.createMultiset();
    }
    
    /**
     * Instantiates a new empty delta bag.
     * @since 1.7
     */
    public static <T> IDeltaBag<T> createDeltaBag() {
        return FRAMEWORK.createDeltaBag();
    }

    /**
     * Instantiates a new list that is optimized for registering observers / callbacks.
     * @since 1.7
     */
    public static <O> List<O> createObserverList() {
        return FRAMEWORK.createObserverList();
    }
    
    /**
     * The collections framework of the current configuration.
     * @since 1.7
     */
    private static final ICollectionsFramework FRAMEWORK = new EclipseCollectionsFactory();
    
    /**
     * Interface abstracting over a collections technology that provides custom collection implementations.
     * @since 1.7
     */
    public static interface ICollectionsFramework {
        
        public abstract <K,V> Map<K,V> createMap();
        public abstract <K,V> Map<K,V> createMap(Map<K,V> initial);
        public abstract <E> Set<E> createSet();
        public abstract <E> Set<E> createSet(Collection<E> initial);
        public abstract <E> MarkedSet<E> createMarkedSet();
        public abstract <T> IMultiset<T> createMultiset();
        public abstract <T> IDeltaBag<T> createDeltaBag();
        public abstract <O> List<O> createObserverList();
    }
    
    /**
     * A special set that is not allowed to be instantiated in any way except through {@link CollectionsFactory}.
     * 
     * <p> The reason for this marker interface is that instances can represent a set of payloads, 
     *  and may be put into a heterogeneous collection together with single payload objects. 
     *  In that scenario, an instanceof check can always distinguish payload objects from marked sets of payload objects, 
     *  even if the payload objects happen to inherit from {@link Set}.  
     * 
     * @noimplement This interface is not intended to be implemented by clients.
     * @since 1.7
     */
    public static interface MarkedSet<E> extends Set<E> {
        
    }
    
    /**
     * Fall-back implementation with Java Collections.
     * @since 1.7
     */
    public static class JavaCollectionsFactory implements ICollectionsFramework {

        @Override
        public <K, V> Map<K, V> createMap() {
            return new HashMap<K, V>();
        }
        
        @Override
        public <K, V> Map<K, V> createMap(Map<K, V> initial) {
            return new HashMap<K, V>(initial);
        }

        @Override
        public <E> Set<E> createSet() {
            return new HashSet<E>();
        }

        @Override
        public <E> Set<E> createSet(Collection<E> initial) {
            return new HashSet<E>(initial);
        }

        @Override
        public <T> IMultiset<T> createMultiset() {
            return new JavaBagMemory<T>();
        }
        
        @Override
        public <O> List<O> createObserverList() {
            return new ArrayList<O>(1);
        }

        @Override
        public <T> IDeltaBag<T> createDeltaBag() {
            throw new UnsupportedOperationException();
        }

        @Override
        public <E> MarkedSet<E> createMarkedSet() {
            throw new UnsupportedOperationException();
        }
        
    }    
}