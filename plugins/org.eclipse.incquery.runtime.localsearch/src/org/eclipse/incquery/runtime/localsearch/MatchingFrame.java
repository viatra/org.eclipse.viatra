/*******************************************************************************
 * Copyright (c) 2004-2008 Akos Horvath, Gergely Varro and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Akos Horvath, Gergely Varro - initial API and implementation from the VIATRA2 project
 *    Zoltan Ujhelyi - update used in EMF-IncQuery API
 *******************************************************************************/

package org.eclipse.incquery.runtime.localsearch;

import java.util.Arrays;

import org.eclipse.incquery.runtime.matchers.tuple.Tuple;

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
public class MatchingFrame extends Tuple implements Cloneable {

	/**
     * The pattern variant for which this MatchingFrame is a
     * matching. 
	 */
    private Object pattern;

	/**
     * The array that physically holds the values.
	 */
    private Object[] frame;

    private int keySize;

    public MatchingFrame(Object pattern, int keySize, int frameSize) {
        this.pattern = pattern;
        this.keySize = keySize;
        this.frame = new Object[frameSize];
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
	public Object getValue(Integer position) {
        Preconditions.checkElementIndex(position, frame.length);
        return frame[position];
	}
    
    /**
     * 
     * @param position
     * @param value
     */
    public void setValue(Integer position, Object value) {
        Preconditions.checkElementIndex(position, frame.length);
        frame[position] = value;
    }
    
    public boolean testAndSetValue(Integer position, Object value) {
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
        return new MatchingKey(Arrays.copyOfRange(frame, 0, keySize));
    }

    public MatchingFrame clone() {
        MatchingFrame clone = new MatchingFrame(pattern, keySize, frame.length);
        clone.frame = frame.clone();
        return clone;
    }
    
    public String toString() {
    	StringBuilder builder = new StringBuilder();
    	for (int i = 0; i < frame.length; i++) {
			builder.append("frame[" + i + "]\t" + frame[i].toString() + "\n");
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
        //Redefining to trim the results to keySize
        Object[] allElements = new Object[keySize];
        for (int i = 0; i < keySize; ++i)
            allElements[i] = get(i);
        return allElements;
    }
}
