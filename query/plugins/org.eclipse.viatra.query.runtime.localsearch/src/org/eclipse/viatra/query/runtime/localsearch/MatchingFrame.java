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

import org.eclipse.viatra.query.runtime.matchers.tuple.IModifiableTuple;
import org.eclipse.viatra.query.runtime.matchers.tuple.VolatileTuple;

import com.google.common.base.Preconditions;

/**
 * A MatchingFrame is a Volatile Tuple implementation used by the local search engine internally.
 */
public class MatchingFrame extends VolatileTuple implements IModifiableTuple {

    /**
     * The array that physically holds the values.
     */
    private Object[] frame;

    /**
     * @since 1.7
     */
    public MatchingFrame(int frameSize) {
        this.frame = new Object[frameSize];
    }
    
    /**
     * Creates a copy of another matching frame; the two frames can be updated separately
     * @param other
     * @since 1.7
     */
    public MatchingFrame(MatchingFrame other) {
        this.frame = Arrays.copyOf(other.frame, other.frame.length);
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
    
    public boolean testAndSetValue(Integer position, Object value) {
        Preconditions.checkElementIndex(position, frame.length);
        if (frame[position] == null) {
            frame[position] = value;
            return true;
        } else {
            return frame[position].equals(value);
        }
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
        return Arrays.copyOf(frame, frame.length);
    }

    @Override
    public void set(int index, Object value) {
        frame[index] = value;
    }
}
