/*******************************************************************************
 * Copyright (c) 2004-2008 Akos Horvath, Gergely Varro and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Akos Horvath, Gergely Varro - initial API and implementation from the VIATRA2 project
 *    Zoltan Ujhelyi - adaptation to VIATRA Query engine
 *******************************************************************************/

package org.eclipse.viatra.query.runtime.localsearch;

import java.util.AbstractCollection;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;

import org.eclipse.viatra.query.runtime.matchers.tuple.FlatTuple;
import org.eclipse.viatra.query.runtime.matchers.tuple.Tuple;

import com.google.common.base.Function;
import com.google.common.collect.Iterables;



public class MatchingTable extends AbstractCollection<Tuple> {
    private Map<MatchingKey,Collection<MatchingFrame>> matchings;

    public MatchingTable() {
        matchings = new HashMap<MatchingKey, Collection<MatchingFrame>>();
    }
    
    @Override
    public Iterator<Tuple> iterator() {
        return Iterables.transform(matchings.keySet(), new Function<MatchingKey, Tuple>() {

            @Override
            public Tuple apply(MatchingKey input) {
                return new FlatTuple(input.keys);
            }
        }).iterator();
    }

    @Override
    public int size() {
        return matchings.keySet().size();
    }

    public void put(MatchingKey key, MatchingFrame value) {
        Collection<MatchingFrame> coll = matchings.get(key);
        if (coll == null) {
            coll = new HashSet<MatchingFrame>();
            matchings.put(key, coll);
        }
        coll.add(value);
    }
}
