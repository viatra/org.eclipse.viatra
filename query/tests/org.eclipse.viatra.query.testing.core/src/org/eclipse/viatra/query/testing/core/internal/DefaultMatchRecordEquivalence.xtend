/**
 * Copyright (c) 2010-2016, Peter Lunk, IncQuery Labs Ltd.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-v20.html.
 * 
 * SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.viatra.query.testing.core.internal

import com.google.common.base.Equivalence
import com.google.common.collect.Maps
import java.util.Map
import java.util.Objects
import org.eclipse.emf.ecore.InternalEObject
import org.eclipse.viatra.query.testing.core.api.JavaObjectAccess
import org.eclipse.viatra.query.testing.core.api.MatchRecordEquivalence
import org.eclipse.viatra.query.testing.snapshot.MatchRecord
import org.eclipse.viatra.query.testing.snapshot.SerializedJavaObjectSubstitution
import org.eclipse.viatra.query.testing.core.SnapshotHelper


/** 
 * TODO for CustomEMFSubstitutions, we don't check if the type attributes match
 * @author Peter Lunk
 */
class DefaultMatchRecordEquivalence extends MatchRecordEquivalence {
    
    /** 
     * @deprecated 
     * Use @link #DefaultMatchRecordEquivalence(SnapshotHelper) instead
     */      
    @Deprecated
    new(Map<String, JavaObjectAccess> accessMap) {
        super(accessMap)
    }
    
    /**
     * @since 2.2
     */
    new(SnapshotHelper helper) {
        super(helper)
    }
    
    new() {
        super(new SnapshotHelper)
    }
        
    override protected boolean doEquivalent(MatchRecord a, MatchRecord b) {
        Maps.difference(a.toMap, b.toMap, new Equivalence<Object> {

            override protected doEquivalent(Object a, Object b) {
                if(a instanceof SerializedJavaObjectSubstitution && b instanceof SerializedJavaObjectSubstitution){
                    val aSub = a as SerializedJavaObjectSubstitution
                    val bSub = b as SerializedJavaObjectSubstitution
                    if (aSub.type.equals(bSub.type)) {
                        val access = accessMap.get(aSub.type)
                        return access.equals(aSub, bSub)
                    }
                } else if(a instanceof InternalEObject && b instanceof InternalEObject){
                    if((a as InternalEObject).eIsProxy && (b as InternalEObject).eIsProxy ){
                        return (a as InternalEObject).eProxyURI == (b as InternalEObject).eProxyURI 
                    }
                } 
                return Objects.equals(a,b)
            }

            override protected doHash(Object t) {
                if(t instanceof SerializedJavaObjectSubstitution && !(t as InternalEObject).eIsProxy){
                    val access = accessMap.get((t as SerializedJavaObjectSubstitution).type)
                    return access.calculateHash((t as SerializedJavaObjectSubstitution))
                } else if(t instanceof InternalEObject && (t as InternalEObject).eIsProxy){
                        return (t as InternalEObject).eProxyURI.hashCode
                }
                t.hashCode
            }

        }).areEqual
    }

    override protected int doHash(MatchRecord t) {
        t.substitutions.map[
            val value = it.derivedValue
            
            if (value instanceof InternalEObject && (value as InternalEObject).eIsProxy) {
                (value as InternalEObject).eProxyURI.hashCode
            } else if (value instanceof SerializedJavaObjectSubstitution) {
                val access = accessMap.get(value.type)
                return access.calculateHash(value)
            } else {
                value?.hashCode
            }
                
        ].fold(0, [r, e | r+e])
    }
}
