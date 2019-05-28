/** 
 * Copyright (c) 2010-2015, Grill Balázs, Istvan Rath and Daniel Varro
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-v20.html.
 * 
 * SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.viatra.query.testing.core.api

import com.google.common.base.Equivalence
import com.google.common.collect.Sets
import java.util.Map
import org.eclipse.viatra.query.testing.core.SnapshotHelper
import org.eclipse.viatra.query.testing.snapshot.MatchRecord

/** 
 * @author Grill Balázs
 */
abstract class MatchRecordEquivalence extends Equivalence<MatchRecord> {
    
    protected extension SnapshotHelper helper
    protected Map<String, JavaObjectAccess> accessMap
    
    /** 
     * @deprecated 
     * Use @link #MatchRecordEquivalence(SnapshotHelper) instead
     */
    @Deprecated
    new(Map<String, JavaObjectAccess> accessMap){
        this.accessMap = accessMap;
        helper = new SnapshotHelper(accessMap);
    }
    
    /**
     * @since 2.2
     */
    new(SnapshotHelper helper){
        this.accessMap = accessMap;
        this.helper = helper;
    }
    
    
    def wrap(Iterable<MatchRecord> matches){
        Sets.newHashSet(matches.map[it.wrap])
    }
    
    def unwrap(Iterable<Equivalence.Wrapper<MatchRecord>> wrapped){
        Sets.newHashSet(wrapped.map[it.get])
    }
    
    def toMap(MatchRecord record){
        val result = newHashMap()
        for(sub : record.substitutions){
            result.put(sub.parameterName, sub.derivedValue)
        }
        return result
    }
    
    
}
