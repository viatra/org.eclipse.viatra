/*******************************************************************************
 * Copyright (c) 2010-2012, Andras Okros, Istvan Rath and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Andras Okros - initial API and implementation
 *******************************************************************************/
package org.eclipse.incquery.patternlanguage.validation.whitelist;

import org.eclipse.xtext.common.types.JvmAnnotationReference;
import org.eclipse.xtext.common.types.JvmOperation;
import org.eclipse.xtext.xbase.lib.Pure;

import com.google.inject.Singleton;

/**
 * A utility class for checking the "purity" of the JvmOperations in XBase check expressions. It checks the @Pure
 * annotation and the whitelists as well.
 */
@Singleton
public class PureClassChecker {

    public boolean isImpureElement(JvmOperation jvmOperation) {
        // First, check if it is tagged with the @Pure annotation
        if (!jvmOperation.getAnnotations().isEmpty()) {
            for (JvmAnnotationReference jvmAnnotationReference : jvmOperation.getAnnotations()) {
                if (Pure.class.getSimpleName().equals(jvmAnnotationReference.getAnnotation().getSimpleName())) {
                    return false;
                }
            }
        }

        // Neither option resulted false, so we return with true.
        return true;
    }

}
