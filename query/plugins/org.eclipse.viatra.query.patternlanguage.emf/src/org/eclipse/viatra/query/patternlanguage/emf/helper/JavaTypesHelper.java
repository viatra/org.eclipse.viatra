/*******************************************************************************
 * Copyright (c) 2010-2016, Zoltan Ujhelyi, IncQuery Labs Ltd.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Zoltan Ujhelyi - initial API and implementation
 *******************************************************************************/
package org.eclipse.viatra.query.patternlanguage.emf.helper;

import org.eclipse.xtext.common.types.JvmType;

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
}
