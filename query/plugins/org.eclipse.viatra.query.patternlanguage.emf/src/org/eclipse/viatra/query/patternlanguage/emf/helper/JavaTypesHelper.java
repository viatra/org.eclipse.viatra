/*******************************************************************************
 * Copyright (c) 2010-2016, Zoltan Ujhelyi, IncQuery Labs Ltd.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-v20.html.
 * 
 * SPDX-License-Identifier: EPL-2.0
 *******************************************************************************/
package org.eclipse.viatra.query.patternlanguage.emf.helper;

import java.util.Objects;

import org.eclipse.xtext.common.types.JvmOperation;
import org.eclipse.xtext.common.types.JvmType;
import org.eclipse.xtext.xbase.lib.Pure;

/**
 * @author Zoltan Ujhelyi
 * @since 2.0
 *
 */
public final class JavaTypesHelper {

    private JavaTypesHelper(){}
    
    /**
     * Copied from TypeReferences.is(JvmType,Class<?>) to make functionality available without injection 
     */
    public static boolean is(final JvmType type, final Class<?> clazz) {
        if (type == null)
            return false;
        String className = clazz.getName();
        if (className.charAt(0) == '[') {
            className = clazz.getCanonicalName();
        }
        boolean result = className.equals(type.getIdentifier());
        return result;
    }
    
    public static boolean hasPureAnnotation(JvmOperation jvmOperation) {
        return jvmOperation.getAnnotations().stream()
                .anyMatch(ref -> Objects.equals(ref.getAnnotation().getQualifiedName(), Pure.class.getName()));
    }
}
