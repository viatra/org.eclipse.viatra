/**
 * Copyright (c) 2010-2016, Peter Lunk, IncQuery Labs Ltd.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *   Peter Lunk - initial API and implementation
 */
package org.eclipse.viatra.query.testing.core.api

import org.eclipse.viatra.query.testing.snapshot.SerializedJavaObjectSubstitution
import org.eclipse.xtend.lib.annotations.Data

/** 
 * Inheriting classes should implement the abstract methods of this class, allowing the VIATRA 
 * Query Testing Framework to access and compare plain java typed objects as well. 
 * 
 * @author Peter Lunk
 */
@Data
abstract class JavaObjectAccess {
    final val Class<?> type
    
    /**
     * Constructs a {@link SerializedJavaObjectSubstitution} element based on the provided 
     * object. Implementation should provide a serialized representation of the given object.
     * This is called when a match set is converted to a snapshot.
     * 
     * @since 1.6
     */
    def SerializedJavaObjectSubstitution toSubstitution(Object value)
    
    /**
     * Calculates the hash code for a given {@link SerializedJavaObjectSubstitution}. Note, that if 
     * two {@link SerializedJavaObjectSubstitution} elements are equal, their hash code should be the same as well.
     * 
     * @since 1.6
     */
    def int calculateHash(SerializedJavaObjectSubstitution substitution)
    
    /**
     * Returns if two {@link SerializedJavaObjectSubstitution} elements are equal or not. 
     * Implementations should cover the deserialization of {@link SerializedJavaObjectSubstitution} contents. 
     * 
     * @since 1.6
     */
    def boolean equals(SerializedJavaObjectSubstitution a, SerializedJavaObjectSubstitution b)
    
}
