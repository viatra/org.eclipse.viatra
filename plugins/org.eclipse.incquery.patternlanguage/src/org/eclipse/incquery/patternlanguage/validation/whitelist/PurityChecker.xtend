/*******************************************************************************
 * Copyright (c) 2010-2015, Denes Harmath, Istvan Rath and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *   Denes Harmath - initial API and implementation
 *******************************************************************************/
package org.eclipse.incquery.patternlanguage.validation.whitelist

import org.eclipse.xtext.common.types.JvmOperation

/**
 * Checks whether a {@link JvmOperation} is pure.
 */
class PurityChecker {

    static def boolean isPure(JvmOperation jvmOperation) {
        jvmOperation.hasPureAnnotation || PureWhitelist.INSTANCE.contains(jvmOperation)
    }

    static def boolean hasPureAnnotation(JvmOperation jvmOperation) {
        jvmOperation.annotations.exists[annotation.qualifiedName == Pure.name]
    }

}