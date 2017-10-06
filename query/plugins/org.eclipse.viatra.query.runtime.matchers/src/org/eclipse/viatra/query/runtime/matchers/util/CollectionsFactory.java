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

//import gnu.trove.map.hash.THashMap;
//import gnu.trove.set.hash.THashSet;
//import it.unimi.dsi.fastutil.objects.Object2ReferenceOpenHashMap;
//import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
     * Instantiates a new empty multiset.
     * @since 1.7
     */
    public static <T> IMultiset<T> createMultiset() {
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
        public abstract <T> IMultiset<T> createMultiset();
        public abstract <T> IDeltaBag<T> createDeltaBag();
        public abstract <O> List<O> createObserverList();
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
        
    }
    
    // OBSOLETE CODE FOLLOWS BELOW
    
    /**
     * @deprecated use {@link #FRAMEWORK}
     */
    @Deprecated
    public static <K,V> Map<K,V> getMap() {
        return FRAMEWORK.createMap();
    }
    
    /**
     * @deprecated use {@link #FRAMEWORK}
     */
    @Deprecated    
    public static <E> Set<E> getSet() {
        return FRAMEWORK.createSet();
    }
 
    /**
     * @deprecated use {@link #FRAMEWORK}
     */
    @Deprecated
    public static <E> Set<E> getSet(Collection<E> initial) {
        return FRAMEWORK.createSet(initial);
    }

    /**
     * @deprecated This enum is entirely unnecessary; don't use it
     */
    @Deprecated
    public enum CollectionsFramework {
        Java,
        HPPC,
        GS,
        FastUtil,
        Trove,
        Apache,
        Javolution
    }
    
    /**
     * @deprecated This enum is entirely unnecessary; don't use it
     */
    @Deprecated
    public static CollectionsFramework mode = CollectionsFramework.Java;
    
}