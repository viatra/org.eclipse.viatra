/*******************************************************************************
 * Copyright (c) 2004-2008 Gabor Bergmann and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Gabor Bergmann - initial API and implementation
 *******************************************************************************/

package org.eclipse.viatra.query.runtime.matchers.tuple;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.OptionalInt;
import java.util.Set;

/**
 * 
 * Specifies select indices of a tuple. If viewed through this mask, the signature of the pattern will consist of its
 * individual substitutions at the given positions, in the exact same order as they appear in indices[].
 * 
 * @author Gabor Bergmann
 */
public class TupleMask {
    /**
     * indices[i] specifies the index of the substitution in the original tuple that occupies the i-th place in the
     * masked signature.
     */
    public final int[] indices;
    /**
     * indicesSorted is indices, sorted in ascending order.
     */
    public int[] indicesSorted;
    /**
     * the size of the tuple this mask is applied to
     */
    public int sourceWidth;

    /**
     * Creates a TupleMask instance with the given indices array
     */
    TupleMask(int[] indices, int sourceWidth) {
        this.sourceWidth = sourceWidth;
        this.indices = indices;
        indicesSorted = null;
    }
    /**
     * Creates a TupleMask instance that selects positions where keep is true
     */
    TupleMask(boolean[] keep) {
        this.sourceWidth = keep.length;
        int size = 0;
        for (int k = 0; k < keep.length; ++k)
            if (keep[k])
                size++;
        this.indices = new int[size];
        int l = 0;
        for (int k = 0; k < keep.length; ++k)
            if (keep[k])
                indices[l++] = k;
        indicesSorted = null;
    }
    
    /**
     * Creates a TupleMask instance that selects given positions.
     * The mask takes ownership of the array selectedIndices, the client must not modify it afterwards.
     * @since 1.7
     */
    protected static TupleMask fromSelectedIndicesInternal(int[] selectedIndices, int sourceArity) {
        if (selectedIndices.length == 0) // is it nullary?
            return new TupleMask0(selectedIndices, sourceArity);
        
        // is it identity?
        boolean identity = sourceArity == selectedIndices.length;
        if (identity) {
            for (int k=0; k < sourceArity; ++k) {
                if (selectedIndices[k] != k) {
                    identity = false;
                    break;
                }
            }            
        }
        if (identity)
            return new TupleMaskIdentity(selectedIndices, sourceArity);
         
        // generic case
        return new TupleMask(selectedIndices, sourceArity);
    }

    /**
     * Creates a TupleMask instance of the given size that maps the first 'size' elements intact
     */
    public static TupleMask linear(int size, int sourceWidth) {
        int[] indices = new int[size];
        for (int i = 0; i < size; i++)
            indices[i] = i;
        return fromSelectedIndicesInternal(indices, sourceWidth);
    }

    /**
     * Creates a TupleMask instance of the given size that maps every single element intact
     */
    public static TupleMask identity(int size) {
        return linear(size, size);
    }

    /**
     * Creates a TupleMask instance of the given size that does not emit output.
     */
    public static TupleMask empty(int sourceWidth) {
        return linear(0, sourceWidth);
    }

    /**
     * Creates a TupleMask instance that maps the tuple intact save for a single element at the specified index which is
     * omitted
     */
    public static TupleMask omit(int omission, int sourceWidth) {
        int size = sourceWidth - 1;
        int[] indices = new int[size];
        for (int i = 0; i < omission; i++)
            indices[i] = i;
        for (int i = omission; i < size; i++)
            indices[i] = i + 1;
        return fromSelectedIndicesInternal(indices, sourceWidth);
    }

    
    /**
     * Creates a TupleMask instance that selects positions where keep is true
     * @since 1.7
     */
    public static TupleMask fromKeepIndicators(boolean[] keep) {
        int size = 0;
        for (int k = 0; k < keep.length; ++k)
            if (keep[k])
                size++;
        int[] indices = new int[size];
        int l = 0;
        for (int k = 0; k < keep.length; ++k)
            if (keep[k])
                indices[l++] = k;
        return fromSelectedIndicesInternal(indices, keep.length);
    }
    
    /**
     * Creates a TupleMask instance that selects given positions.
     * @since 1.7
     */
    public static TupleMask fromSelectedIndices(int sourceArity, Collection<Integer> selectedIndices) {
        int[] selected = integersToIntArray(selectedIndices);
        return fromSelectedIndicesInternal(selected, sourceArity);
    }
    /**
     * Creates a TupleMask instance that selects given positions.
     * @since 1.7
     */
    public static TupleMask fromSelectedIndices(int sourceArity, int[] selectedIndices) {
        return fromSelectedIndicesInternal(Arrays.copyOf(selectedIndices, selectedIndices.length), sourceArity);
    }
    /**
     * Creates a TupleMask instance that selects non-null positions of a given tuple
     * @since 1.7
     */
    public static TupleMask fromNonNullIndices(ITuple tuple) {
        List<Integer> indices = new ArrayList<>();
        for (int i=0; i < tuple.getSize(); i++) {
            if (tuple.get(i) != null) {
                indices.add(i);
            }
        }
        return fromSelectedIndicesInternal(integersToIntArray(indices), tuple.getSize());
    }
    /**
     * @since 1.7
     */
    public static int[] integersToIntArray(Collection<Integer> selectedIndices) {
        int[] selected = new int[selectedIndices.size()];
        int k=0;
        for (Integer integer : selectedIndices) {
            selected[k++] = integer;
        }
        return selected;
    }


    /**
     * Creates a TupleMask instance that moves an element from one index to other, shifting the others if neccessary.
     */
    public static TupleMask displace(int from, int to, int sourceWidth) {
        int[] indices = new int[sourceWidth];
        for (int i = 0; i < sourceWidth; i++)
            if (i == to)
                indices[i] = from;
            else if (i >= from && i < to)
                indices[i] = i + 1;
            else if (i > to && i <= from)
                indices[i] = i - 1;
            else
                indices[i] = i;
        return fromSelectedIndicesInternal(indices, sourceWidth);
    }

    /**
     * Creates a TupleMask instance that selects a single element of the tuple.
     */
    public static TupleMask selectSingle(int selected, int sourceWidth) {
        int[] indices = { selected };
        return fromSelectedIndicesInternal(indices, sourceWidth);
    }

    /**
     * Creates a TupleMask instance that selects whatever is selected by left, and appends whatever is selected by
     * right. PRE: left and right have the same sourcewidth
     */
    public static TupleMask append(TupleMask left, TupleMask right) {
        int leftLength = left.indices.length;
        int rightLength = right.indices.length;
        int[] indices = new int[leftLength + rightLength];
        for (int i = 0; i < leftLength; ++i)
            indices[i] = left.indices[i];
        for (int i = 0; i < rightLength; ++i)
            indices[i + leftLength] = right.indices[i];
        return fromSelectedIndicesInternal(indices, left.sourceWidth);
    }

    /**
     * Generates indicesSorted from indices
     * 
     */
    public void sort() {
        indicesSorted = new int[indices.length];
        List<Integer> list = new LinkedList<Integer>();
        for (int i = 0; i < indices.length; ++i)
            list.add(indices[i]);
        java.util.Collections.sort(list);
        int i = 0;
        for (Integer a : list)
            indicesSorted[i++] = a;
    }
    
    /**
     * Returns the first index of the source that is not selected by the mask, or empty if all indices are selected.
     * <p> PRE: mask indices are all different
     * @since 2.0
     */
    public OptionalInt getFirstOmittedIndex() {
        if (indicesSorted == null) sort();
        int column = 0; 
        while (column < getSize() && indicesSorted[column] == column) column++;
        if (column < getSize()) return OptionalInt.of(column);
        else return OptionalInt.empty();
    }

    /**
     * Returns a selected masked value from the selected tuple.
     * @pre: 0 <= index < getSize() 
     * @since 1.7
     */
    public Object getValue(ITuple original, int index) {
        return original.get(indices[index]);
    }
    
    /**
     * Sets the selected value in the original tuple based on the mask definition
     * 
     * @pre: 0 <= index < getSize()
     * @since 1.7
     */
    public void set(IModifiableTuple tuple, int index, Object value) {
        tuple.set(indices[index], value);
    }
    
    /**
     * Generates an immutable, masked view of the original tuple.
     * @since 1.7
     */
    public Tuple transform(ITuple original) {
        switch (indices.length) {
        case 0:
            return FlatTuple0.INSTANCE;
        case 1:
            return new FlatTuple1(original.get(indices[0]));
        case 2:
            return new FlatTuple2(original.get(indices[0]), original.get(indices[1]));
        case 3:
            return new FlatTuple3(original.get(indices[0]), original.get(indices[1]), original.get(indices[2]));
        case 4:
            return new FlatTuple4(original.get(indices[0]), original.get(indices[1]), original.get(indices[2]), original.get(indices[3]));
        default:
            Object signature[] = new Object[indices.length];
            for (int i = 0; i < indices.length; ++i)
                signature[i] = original.get(indices[i]);
            return new FlatTuple(signature);
        }
    }
    
    /**
     * Returns a tuple `result` that satisfies `this.transform(result).equals(masked)`. Positions of the result tuple
     * that are not determined this way will be filled with null.
     * 
     * @pre: all indices of the mask must be different
     * @since 1.7
     */
    public Tuple revertFrom(ITuple masked) {
        Object[] signature = new Object[sourceWidth];
        for (int i = 0; i < indices.length; ++i)
            signature[indices[i]] = masked.get(i);
        return Tuples.flatTupleOf(signature);
    }

    /**
     * Generates an immutable, masked view of the original tuple.
     */
    public <T> List<T> transform(List<T> original) {
        List<T> signature = new ArrayList<T>(indices.length);
        for (int i = 0; i < indices.length; ++i)
            signature.add(original.get(indices[i]));
        return signature;
    }

    /**
     * Transforms a given mask directly, instead of transforming tuples that were transformed by the other mask.
     * 
     * @return a mask that cascades the effects this mask after the mask provided as parameter.
     */
    public TupleMask transform(TupleMask mask) {
        int[] cascadeIndices = new int[indices.length];
        for (int i = 0; i < indices.length; ++i)
            cascadeIndices[i] = mask.indices[indices[i]];
        return fromSelectedIndicesInternal(cascadeIndices, mask.sourceWidth);
    }

    // /**
    // * Generates a complementer mask that maps those elements that were
    // untouched by the original mask.
    // * Ordering is left intact.
    // * A Tuple is used for reference concerning possible equalities among
    // elements.
    // */
    // public TupleMask complementer(Tuple reference)
    // {
    // HashSet<Object> touched = new HashSet<Object>();
    // LinkedList<Integer> untouched = new LinkedList<Integer>();
    //
    // for (int index : indices) touched.add(reference.get(index));
    // for (int index=0; index<reference.getSize(); ++index)
    // {
    // if (touched.add(reference.get(index))) untouched.addLast(index);
    // }
    //
    // int[] complementer = new int[untouched.size()];
    // int k = 0;
    // for (Integer integer : untouched) complementer[k++] = integer;
    // return new TupleMask(complementer, reference.getSize());
    // }

    /**
     * Combines two substitutions. The new pattern will contain all substitutions of masked and unmasked, assuming that
     * the elements of masked indicated by this mask are already matched against unmasked.
     * 
     * POST: the result will start with an exact copy of unmasked
     * 
     * @param unmasked
     *            primary pattern substitution that is left intact.
     * @param masked
     *            secondary pattern substitution that is transformed to the end of the result.
     * @param useInheritance
     *            whether to use inheritance or copy umasked into result instead.
     * @param asComplementer
     *            whether this mask maps from the masked Tuple to the tail of the result or to the unmasked one.
     * @return new pattern that is a combination of unmasked and masked.
     */
    public Tuple combine(Tuple unmasked, Tuple masked, boolean useInheritance, boolean asComplementer) {

        int combinedLength = asComplementer ? indices.length : masked.getSize() - indices.length;
        if (!useInheritance)
            combinedLength += unmasked.getSize();
        Object combined[] = new Object[combinedLength];

        int cPos = 0;
        if (!useInheritance) {
            for (int i = 0; i < unmasked.getSize(); ++i)
                combined[cPos++] = unmasked.get(i);
        }

        if (asComplementer) {
            for (int i = 0; i < indices.length; ++i)
                combined[cPos++] = masked.get(indices[i]);
        } else {
            if (indicesSorted == null)
                sort();
            int mPos = 0;
            for (int i = 0; i < masked.getSize(); ++i)
                if (mPos < indicesSorted.length && i == indicesSorted[mPos])
                    mPos++;
                else
                    combined[cPos++] = masked.get(i);
        }

        return useInheritance ? Tuples.leftInheritanceTupleOf(unmasked, combined) : Tuples.flatTupleOf(combined);
    }

    @Override
    public int hashCode() {
        final int PRIME = 31;
        int result = sourceWidth;
        for (int i : indices)
            result = PRIME * result + i;
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        final TupleMask other = (TupleMask) obj;
        if (sourceWidth != other.sourceWidth)
            return false;
        if (indices.length != other.indices.length)
            return false;
        for (int k = 0; k < indices.length; k++)
            if (indices[k] != other.indices[k])
                return false;
        return true;
    }

    @Override
    public String toString() {
        StringBuilder s = new StringBuilder();
        s.append("M(" + sourceWidth + "->");
        for (int i : indices) {
            s.append(i);
            s.append(',');
        }
        s.append(')');
        return s.toString();
    }
    
    /**
     * Returns the size of the masked tuples described by this mask
     * @since 1.7 
     */
    public int getSize() {
        return indices.length;
    }

    /**
     * Returns the size of the original tuples handled by this mask
     * @since 1.7
     */
    public int getSourceWidth() {
        return sourceWidth;
    }
    
    /**
     * Transforms the given list by applying the mask and putting all results into a set; keeping only a single element
     * in case of the mapping result in duplicate values.
     * 
     * @since 1.7
     */
    public <T> Set<T> transformUnique(List<T> original) {
        Set<T> signature = new HashSet<>();
        for (int i = 0; i < indices.length; ++i)
            signature.add(original.get(indices[i]));
        return signature;
    }
}
