/*******************************************************************************
 * Copyright (c) 2004-2010 Gabor Bergmann and Daniel Varro
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-v20.html.
 * 
 * SPDX-License-Identifier: EPL-2.0
 *******************************************************************************/
package org.eclipse.viatra.query.patternlanguage.emf.specification;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

import org.eclipse.viatra.query.patternlanguage.emf.internal.XtextInjectorProvider;
import org.eclipse.viatra.query.patternlanguage.emf.util.IClassLoaderProvider;
import org.eclipse.viatra.query.patternlanguage.emf.vql.Pattern;
import org.eclipse.viatra.query.runtime.exception.ViatraQueryException;
import org.eclipse.xtext.common.types.JvmField;
import org.eclipse.xtext.common.types.util.JavaReflectAccess;

import com.google.inject.Inject;

/**
 * Evaluates an Jvm Constant, based on XbaseInterpreter#featureCall.
 * @since 2.7
 */
@SuppressWarnings("restriction")
public class JvmConstantEvaluator {

    private final JvmField jvmField;
    private final Pattern pattern;

    @Inject
    private IClassLoaderProvider classLoaderProvider;
    @Inject
    private JavaReflectAccess javaReflect;

    /**
     * @param xExpression
     *            the expression to evaluate
     * @param pattern
     * @since 2.0
     */
    public JvmConstantEvaluator(JvmField field, Pattern pattern) {
        this.jvmField = field;
        this.pattern = pattern;
        
        XtextInjectorProvider.INSTANCE.getInjector().injectMembers(this);
        ClassLoader classLoader = classLoaderProvider.getClassLoader(pattern);
        if (classLoader != null) {
            javaReflect.setClassLoader(classLoader);
        }
    }

    public Object evaluateConstantExpression() {
        Field field = javaReflect.getField(jvmField);
        if (field == null || !Modifier.isStatic(field.getModifiers())) {
            throw new ViatraQueryException(String.format(
                    "Invalid constant expression %s in pattern %s.",
                    jvmField.getIdentifier(), pattern), "Invalid constant.");
        }
        try {
            field.setAccessible(true);
            Object result = field.get(null);
            return result;
        } catch (Exception e) {
            throw new ViatraQueryException(String.format(
                    "Error while evaluating constant expression %s in pattern %s.",
                    jvmField.getIdentifier(), pattern), "Cannot evaluate constant.", e);
        }
    }

}
