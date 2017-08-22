/*******************************************************************************
 * Copyright (c) 2004-2008 Akos Horvath, Gergely Varro and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Akos Horvath, Gergely Varro - initial API and implementation from the VIATRA2 project
 *    Zoltan Ujhelyi - update used in VIATRA Query API
 *******************************************************************************/

package org.eclipse.viatra.query.runtime.localsearch;

import java.util.Arrays;

import org.eclipse.viatra.query.runtime.localsearch.matcher.LocalSearchMatcher;
import org.eclipse.viatra.query.runtime.localsearch.plan.SearchPlanExecutor;
import org.eclipse.viatra.query.runtime.matchers.tuple.Tuple;

import com.google.common.base.Preconditions;

/**
 * MatchingFrame represents the actual mappings of variables to constants. A MatchingFrame maintains a reference to its
 * corresponding Pattern (or possibly flattened pattern).
 * 
 * The following mappings are used by the interpreted engine:
 * <ul>
 * <li>VariableType => PatternVariable</li>
 * <li>ValueType => AnyModelElement</li>
 * </ul>
 */
public class MatchingFrame extends Tuple {

    private static final String KEYS_ARRAY_SETUP_MISSING_MESSAGE = "A non-null key array has to be set up before getElements() is called.";
    private static final String KEYS_ARRAY_MUST_NOT_BE_NULL_MESSAGE = "Argument keys must not be null.";

    /**
     * The pattern variant for which this MatchingFrame is a
     * matching. 
     */
    private Object pattern;

    /**
     * The array that physically holds the values.
     */
    private Object[] frame;

    private int[] keys;
    private Object[] parameterValues;

    public MatchingFrame(Object pattern, int frameSize) {
        this.pattern = pattern;
        this.frame = new Object[frameSize];
    }
    
    /**
     * Creates a copy of another matching frame; the two frames can be updated separately
     * @param other
     * @since 1.7
     */
    public MatchingFrame(MatchingFrame other) {
        this.pattern = other.pattern;
        this.frame = Arrays.copyOf(other.frame, other.frame.length);
        if (other.keys == null) {
            this.keys = null;
        } else {
            this.keys = Arrays.copyOf(other.keys, other.keys.length);
        }
        this.parameterValues = Arrays.copyOf(other.parameterValues, other.parameterValues.length);
    }

    /**
     * The keys describes which elements of the frame corresponds to the parameters. The parameters are identified by
     * the index, and a value might be added to multiple indexes in case of parameters made equal with a == constraint.
     * As this array is different for each body instance, this method is called by the {@link SearchPlanExecutor} class
     * later than initialization (done by the {@link LocalSearchMatcher}.
     * 
     * @param keys
     * @see {@linkplain #setParameterValues(Object[])} for setting the initial parameter
     * @since 1.3
     */
    public boolean setKeys(int[] keys) {
        Preconditions.checkArgument(keys != null, KEYS_ARRAY_MUST_NOT_BE_NULL_MESSAGE);
        this.keys = Arrays.copyOf(keys, keys.length);
        if (parameterValues != null) {
            for (int i=0; i<parameterValues.length; i++) {
                if (parameterValues[i] != null){
                    if (frame[keys[i]] != null && !frame[keys[i]].equals(parameterValues[i])){
                        return false;
                    }else{
                        frame[keys[i]] = parameterValues[i];
                    }
                }
            }
        }
        return true;
    }



    /**
     * Returns the value stored inside the matching frame.
     * 
     * @param position
     * @return the element stored in the selected position in the frame, or null if it is not yet set
     * @throws IndexOutOfBoundsException
     *             if position is negative
     * @throws IllegalArgumentException
     *             if the position is larger then the length of the frame
     */
    public Object getValue(int position) {
        Preconditions.checkElementIndex(position, frame.length);
        return frame[position];
    }
    
    /**
     * Sets the value of the variable at the given position. For internal use in LS matching only.
     * 
     * @param position the position of the variable within the frame
     * @param value the value to be set for the variable
     */
    public void setValue(int position, Object value) {
        Preconditions.checkElementIndex(position, frame.length);
        frame[position] = value;
    }
    
    /**
     * Call for setting the parameter values (or null if a value is unspecified). The values will only be visible in the
     * MatchingFrame instance after {@link #setKeys(int[])} is called.
     * 
     * @param parameterValues a non-null array of values; a value might be null if it is not specified early
     */
    public void setParameterValues(Object[] parameterValues) {
        //Cannot write precondition checking here, as required information is not always available
        this.parameterValues = Arrays.copyOf(parameterValues, parameterValues.length);
    }
    
    public boolean testAndSetValue(Integer position, Object value) {
        Preconditions.checkElementIndex(position, frame.length);
        if (frame[position] == null) {
            frame[position] = value;
            return true;
        } else {
            return frame[position].equals(value);
        }
    }

    /**
     * @return the pattern this frame is attached to
     */
    public Object getPattern() {
        return pattern;
    }
    
    public MatchingKey getKey() {
        Object[] key = new Object[keys.length];
        for (int i=0; i < keys.length; i++) {
            key[i] = frame[keys[i]];
        }
        return new MatchingKey(key);
    }
    
    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < frame.length; i++) {
            builder.append("frame[" + i + "]\t" + (frame[i] == null ? "null" : frame[i]).toString() + "\n");
        }
        return builder.toString();
    }
    
    @Override
    public int getSize() {
        return frame.length;
    }

    @Override
    public Object get(int index) {
        return getValue(index);
    }
    
    @Override
    public Object[] getElements() {
        Preconditions.checkState(keys != null, KEYS_ARRAY_SETUP_MISSING_MESSAGE);
        //Redefining to trim the results to keySize
        Object[] allElements = new Object[keys.length];
        for (int i = 0; i < keys.length; ++i)
            allElements[i] = get(keys[i]);
        return allElements;
    }
}
