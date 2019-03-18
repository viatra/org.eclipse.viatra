/*******************************************************************************
 * Copyright (c) 2010-2014, Zoltan Ujhelyi, Istvan Rath and Daniel Varro
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-v20.html.
 * 
 * SPDX-License-Identifier: EPL-2.0
 *******************************************************************************/
package org.eclipse.viatra.query.patternlanguage.emf.types;

import java.util.Objects;

import org.eclipse.viatra.query.patternlanguage.emf.vql.JavaType;
import org.eclipse.viatra.query.patternlanguage.emf.vql.Type;
import org.eclipse.viatra.query.runtime.matchers.context.IInputKey;
import org.eclipse.viatra.query.runtime.matchers.context.IQueryMetaContext;
import org.eclipse.xtext.common.types.JvmDeclaredType;
import org.eclipse.xtext.common.types.JvmUnknownTypeReference;

/**
 * @author Zoltan Ujhelyi
 * @since 2.0
 */
public abstract class AbstractTypeSystem implements ITypeSystem {

    final IQueryMetaContext context;

    public AbstractTypeSystem(IQueryMetaContext context) {
        this.context = context;
    }

    @Override
    public String typeString(IInputKey type) {
        return type.getPrettyPrintableName();
    }

    /**
     * @param typeClass
     * @return The wrapper class if the input is primitive. If it is not, it returns with the input unchanged.
     * @since 1.3
     */
    protected static Class<?> getWrapperClassForType(Class<?> typeClass) {
        if (typeClass != null && typeClass.isPrimitive()) {
            if (typeClass == boolean.class) {
                return java.lang.Boolean.class;
            } else if (typeClass == byte.class) {
                return java.lang.Byte.class;
            } else if (typeClass == char.class) {
                return java.lang.Character.class;
            } else if (typeClass == double.class) {
                return java.lang.Double.class;
            } else if (typeClass == float.class) {
                return java.lang.Float.class;
            } else if (typeClass == int.class) {
                return java.lang.Integer.class;
            } else if (typeClass == long.class) {
                return java.lang.Long.class;
            } else if (typeClass == short.class) {
                return java.lang.Short.class;
            }
        }
        return typeClass;
    }
    
    /**
     * @since 2.1
     */
    protected static String getWrapperClassNameForTypeName(String className) {
        if (className != null) {
            if (Objects.equals(className, boolean.class.getName())) {
                return java.lang.Boolean.class.getName();
            } else if (Objects.equals(className, byte.class.getName())) {
                return Byte.class.getName();
            } else if (Objects.equals(className, char.class.getName())) {
                return Character.class.getName();
            } else if (Objects.equals(className, double.class.getName())) {
                return Double.class.getName();
            } else if (Objects.equals(className, float.class.getName())) {
                return Float.class.getName();
            } else if (Objects.equals(className, int.class.getName())) {
                return Integer.class.getName();
            } else if (Objects.equals(className, long.class.getName())) {
                return Long.class.getName();
            } else if (Objects.equals(className, short.class.getName())) {
                return Short.class.getName();
            }
        }
        return className;
    }

    /**
     * @since 1.4
     */
    @Override
    public boolean isValidType(Type type) {
        if (type instanceof JavaType) {
            return isValidType((JavaType)type);
        }
        return false;
    }
    
    /**
     * @since 1.4
     */
    protected boolean isValidType(JavaType type) {
        JvmDeclaredType classRef = type.getClassRef();
        return classRef != null && !classRef.eIsProxy() &&
                !(classRef instanceof JvmUnknownTypeReference);
    }
}
