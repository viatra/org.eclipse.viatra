/*******************************************************************************
 * Copyright (c) 2004-2008 Akos Horvath, Gergely Varro and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Akos Horvath, Gergely Varro - initial API and implementation
 *    Zoltan Ujhelyi - update used in EMF-IncQuery API
 *******************************************************************************/

package org.eclipse.incquery.runtime.localsearch;


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
public class MatchingFrame implements Cloneable {

	/**
     * The pattern variant for which this MatchingFrame is a
     * matching. 
	 */
    private Object pattern;

	/**
     * The array that physically holds the values.
	 */
    private Object[] frame;

    public MatchingFrame(Object pattern, int frameSize) {
        this.pattern = pattern;
        this.frame = new Object[frameSize];
    }

    /**
     * 
     * @param position
     * @return
     */
	public Object getValue(Integer position) {
		// TODO gervarro: return (position < frame.length ? frame[position] : pattern.get);
        return frame[position];
	}
    
    /**
     * 
     * @param position
     * @param value
     */
    public void setValue(Integer position, Object value) {
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
    
    public MatchingFrame clone() {
        MatchingFrame clone = new MatchingFrame(pattern, frame.length);
        clone.frame = frame.clone();
        return clone;
    }
    
    public String toString() {
    	StringBuffer buf = new StringBuffer();
    	for (int i = 0; i < frame.length; i++) {
			buf.append("frame[" + i + "]\t" + frame[i].toString() + "\n");
		}
    	return buf.toString();
    }

    public Object lookup(int position) {
        if (position >= 0 && position < frame.length) {
            return frame[position];
        } else {
            // TODO gervarro: Exception or return null;
            return null;
        }
    }
    
    public int size() {
    	return frame.length;
    }
}
