/*******************************************************************************
 * Copyright (c) 2010-2018, Zoltan Ujhelyi, IncQuery Labs Ltd.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-v20.html.
 * 
 * SPDX-License-Identifier: EPL-2.0
 *******************************************************************************/
package org.eclipse.viatra.query.patternlanguage.emf.validation.whitelist;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Collection;

import org.eclipse.viatra.query.patternlanguage.emf.validation.whitelist.PureWhitelist.PureElement;

/**
 * @author Zoltan Ujhelyi
 * @since 2.0
 *
 */
public interface IPureElementProvider {

    Collection<PureElement> getPureElements();
    
    default PureElement pureMethod(Method method) {
        StringBuilder sb = new StringBuilder();
        sb.append(method.getDeclaringClass().getName());
        sb.append(".");
        sb.append(method.getName());
        sb.append("(");
        for (int i=0; i < method.getParameterCount(); i++) {
            final Parameter parameter = method.getParameters()[i];
            if (parameter.isVarArgs() || parameter.getType().isArray()) {
                sb.append(parameter.getType().getComponentType().getName());
                sb.append("[]");
            } else {
                sb.append(parameter.getType().getName());
            }
            if (i < method.getParameterCount() - 1) {
                sb.append(",");
            }
        }
        sb.append(")");
        return new PureElement(sb.toString(), PureElement.Type.METHOD);
    }
    default PureElement pureClass(Class<?> clazz) {
        return new PureElement(clazz.getName(), PureElement.Type.CLASS);
    }
}
