package org.eclipse.incquery.tooling.debug.variables;

import java.util.Collections;
import java.util.List;

import com.sun.jdi.ArrayReference;
import com.sun.jdi.Field;
import com.sun.jdi.IncompatibleThreadStateException;
import com.sun.jdi.Method;
import com.sun.jdi.ObjectReference;
import com.sun.jdi.ThreadReference;
import com.sun.jdi.Value;

@SuppressWarnings("restriction")
public class ValueWrapper implements Comparable<ValueWrapper> {

    private Value value;
    private ThreadReference threadReference;
    private static ValueWrapper NULL_WRAPPER = ValueWrapper.wrap(null, null);

    private ValueWrapper(Value value, ThreadReference threadReference) {
        this.value = value;
        this.threadReference = threadReference;
    }

    public static ValueWrapper wrap(Value value, ThreadReference threadReference) {
        return new ValueWrapper(value, threadReference);
    }

    public Value getValue() {
        return value;
    }

    public ThreadReference getThreadReference() {
        return threadReference;
    }

    public boolean isArray() {
        return this.value != null && this.value instanceof ArrayReference;
    }

    public boolean isReference() {
        return this.value != null && this.value instanceof ObjectReference;
    }

    public int getArrayLength() {
        if (isArray()) {
            return ((ArrayReference) value).length();
        } else {
            return 0;
        }
    }

    /**
     * Tries to access the {@link Field} of the wrapped {@link Value} and returns it as {@link ValueWrapper}. Accessing
     * the field may fail if (1) the wrapped value is null or (2) the wrapped value is not an {@link ObjectReference}.
     * 
     * @param fieldName
     *            the name of the field
     * @return the wrapped value of the field
     */
    public ValueWrapper get(String fieldName) {
        if (this.value == null || !(this.value instanceof ObjectReference)) {
            return NULL_WRAPPER;
        } else {
            Field field = ((ObjectReference) this.value).referenceType().fieldByName(fieldName);
            return ValueWrapper.wrap((field == null) ? null : ((ObjectReference) this.value).getValue(field),
                    this.threadReference);
        }
    }

    /**
     * Invokes the method with name methodName on the wrapped {@link Value} in the context of the
     * {@link ThreadReference} that was used during wrapper creation. The method must be a parameterless method and in
     * the case when multiple methods are present with the same name, the first one will be selected and invoked. <br/>
     * <br/>
     * Note that the method attempts to invoke the given method for at most 5 consecutive times, if an
     * {@link IncompatibleThreadStateException} is thrown during an invocation. This works most of the time based on the
     * experiences, the limited number of tries is required to avoid an infinite loop. If the method invocation fails
     * for 5 consecutive times then the null value will be wrapped as a result.
     * 
     * @param methodName
     *            the name of the method to invoke
     * @return the wrapped result of the method invocation
     */
    @SuppressWarnings("unchecked")
    public ValueWrapper invoke(String methodName) {
        if (this.value == null || this.threadReference == null || !(this.value instanceof ObjectReference)) {
            return NULL_WRAPPER;
        } else {
            Value result = null;
            int t = 0;

            Method method = null;
            List<Method> methods = ((ObjectReference) this.value).referenceType().methodsByName(methodName);
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
                        result = ((ObjectReference) this.value).invokeMethod(this.threadReference, method,
                                (List<? extends Value>) Collections.emptyList(), 0);
                    } catch (Exception e) {
                        result = null;
                    }
                    t++;
                }
            }

            return ValueWrapper.wrap(result, threadReference);
        }
    }

    @Override
    public int compareTo(ValueWrapper that) {
        if (this.value instanceof ObjectReference && that.value instanceof ObjectReference) {
            return new Long(((ObjectReference) this.value).uniqueID()).compareTo(((ObjectReference) that.value)
                    .uniqueID());
        } else {
            return new Integer(this.hashCode()).compareTo(that.hashCode());
        }
    }
}
