/*******************************************************************************
 * Copyright (c) 2010-2016, Gyorgy Gerencser, Gabor Bergmann, Istvan Rath and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Gyorgy Gerencser - initial API and implementation
 *******************************************************************************/
package org.eclipse.viatra.addon.querybyexample.interfaces.beans;

import java.util.Objects;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EReference;

/**
 * Represents a constraint in an VIATRA Pattern, excluded variable declaring constraints. The overrided toString()
 * method will return with the exact syntax in the vql code. Each of these constraints consists of two variables and a
 * reference. Swapped sequence with the EOpposite reference means the exact same object (they're equals).
 * 
 * Example: Year.school(y, sch);
 * 
 *
 */
public class VQLConstraint {
    /**
     * Starting variable. In the example, the 'y' Year variable will be the starting EIQ variable.
     */
    private EObject start;

    /**
     * The reference between the two variables.
     */
    private EReference reference;

    /**
     * Ending variable. In the example, the 'sch' Shool variable will be the ending EIQ variable.
     */
    private EObject end;

    /**
     * If false, the given constraint won't be visible in the code
     */
    private boolean visible = true;

    public VQLConstraint(EObject start, EReference ref, EObject end) {
        this.start = start;
        this.reference = ref;
        this.end = end;
    }

    public EObject getStart() {
        return start;
    }

    public EReference getReference() {
        return reference;
    }

    public EObject getEnd() {
        return end;
    }

    public boolean isVisible() {
        return visible;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((end == null) ? 0 : end.hashCode()) + ((start == null) ? 0 : start.hashCode());

        if (reference == null || reference.getEOpposite() == null) {
            result = prime * result + ((reference == null) ? 0 : reference.hashCode());
        } else {
            result = prime * result + reference.hashCode() + reference.getEOpposite().hashCode();
        }

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
        VQLConstraint other = (VQLConstraint) obj;
        if (end == null && other.end != null)
            return false;
        if (reference == null && other.reference != null)
            return false;
        if (start == null && other.start != null)
            return false;

        if (Objects.equals(this.start, other.start) && Objects.equals(this.end, other.end) && Objects.equals(this.reference, other.reference))
            return true;
        else if (this.reference.getEOpposite() != null)
            return (Objects.equals(this.start, other.end) && Objects.equals(this.end, other.start)
                    && Objects.equals(this.reference.getEOpposite(), other.reference));
        else
            return false;
    }
}