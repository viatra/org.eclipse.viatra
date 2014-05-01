/*******************************************************************************
 * Copyright (c) 2010-2012, Tamas Szabo, Istvan Rath and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Tamas Szabo - initial API and implementation
 *******************************************************************************/
package org.eclipse.incquery.tooling.debug.common;

import java.lang.reflect.Field;
import java.util.Collections;
import java.util.List;

import com.sun.jdi.IncompatibleThreadStateException;
import com.sun.jdi.Method;
import com.sun.jdi.ObjectReference;
import com.sun.jdi.ThreadReference;
import com.sun.jdi.Value;

/**
 * Utility methods based on either Java Reflection or on the Java Debug API.
 * 
 * @author Tamas Szabo (itemis AG)
 * 
 */
@SuppressWarnings("restriction")
public class IncQueryDebugUtil {

    /**
     * Returns the value of the {@link Field} with name fieldName in the given {@link Object}. This method uses Java
     * Reflection.
     * 
     * @param obj
     *            the instance
     * @param fieldName
     *            the name of the field
     * @return the value of the field
     */
    public static Object getField(Object obj, String fieldName) {
        try {
            Field field = obj.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);
            return field.get(obj);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Returns the value of the {@link com.sun.jdi.Field} with name fieldName in the given {@link ObjectReference}. This
     * method uses the Java Debug API.
     * 
     * @param ref
     *            the object reference
     * @param fieldName
     *            the name of the field
     * @return the value of the field
     */
    public static Value getField(ObjectReference ref, String fieldName) {
        com.sun.jdi.Field field = ref.referenceType().fieldByName(fieldName);
        return (field == null) ? null : ref.getValue(field);
    }

    /**
     * Invokes the method with name methodName on the given {@link ObjectReference} in the given {@link ThreadReference}
     * . The method must be a parameterless method and in the case when multiple methods are present with the same name,
     * the first one will be selected and invoked. <br/>
     * <br/>
     * Note that the method attempts to invoke the given method for at most 5 consecutive times, if an
     * {@link IncompatibleThreadStateException} is thrown during an invocation. This works most of the time based on the
     * experiences, the limited number of tries is required to avoid an infinite loop. If the method invocation fails
     * for 5 consecutive times then null will be returned.
     * 
     * @param threadReference
     *            the thread reference
     * @param ref
     *            the object reference
     * @param methodName
     *            the name of the method to invoke
     * @return the result of the method invocation, or null if an error occurred during invocation
     */
    @SuppressWarnings("unchecked")
    public static Value invokeMethod(ThreadReference threadReference, ObjectReference ref, String methodName) {
        Value result = null;
        int t = 0;

        Method method = null;
        List<Method> methods = ref.referenceType().methodsByName(methodName);
        for (Method m : methods) {
            // argumentTypeNames is the safest method from the ones which deal with arguments because
            // this one will not throw any kind of Exception
            if (m.argumentTypeNames().isEmpty()) {
                method = m;
                break;
            }
        }

        if (method != null) {
            while (result == null && t < 5) {
                try {
                    result = ref.invokeMethod(threadReference, method, (List<? extends Value>) Collections.emptyList(),
                            0);
                } catch (Exception e) {
                    result = null;
                }
                t++;
            }
        }

        return result;
    }

}
