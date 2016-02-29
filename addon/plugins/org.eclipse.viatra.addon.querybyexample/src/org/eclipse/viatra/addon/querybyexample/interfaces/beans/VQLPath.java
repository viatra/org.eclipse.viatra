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

import java.util.LinkedHashSet;
import java.util.Set;

import org.eclipse.emf.ecore.EObject;

public class VQLPath {

    private Set<VQLConstraint> constraints = new LinkedHashSet<VQLConstraint>();

    private boolean visible = true;

    private EObject start;

    private EObject end;

    public boolean isVisible() {
        return visible;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    public Set<VQLConstraint> getConstraints() {
        return constraints;
    }

    public void setConstraints(Set<VQLConstraint> constraints) {
        this.constraints = constraints;
    }

    public EObject getStart() {
        return start;
    }

    public void setStart(EObject start) {
        this.start = start;
    }

    public EObject getEnd() {
        return end;
    }

    public void setEnd(EObject end) {
        this.end = end;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((end == null) ? 0 : end.hashCode()) + ((start == null) ? 0 : start.hashCode());
        if (constraints != null && !constraints.isEmpty()) {
            for (VQLConstraint constraint : constraints)
                result = prime * result + constraint.hashCode();
        } else {
            result = prime * result;
        }
        result = prime * result + 1231;
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
        VQLPath other = (VQLPath) obj;
        if (this.start != null && this.start.equals(other.end) && this.end != null && this.end.equals(other.start)) {
            if (constraints == null && other.constraints == null)
                return true;
            if (this.constraints != null && other.constraints != null
                    && this.constraints.size() == other.constraints.size()
                    && this.constraints.containsAll(other.constraints))
                return true;
            return false;
        } else {
            if (constraints == null) {
                if (other.constraints != null)
                    return false;
            } else if (!constraints.equals(other.constraints))
                return false;
            if (end == null) {
                if (other.end != null)
                    return false;
            } else if (!end.equals(other.end))
                return false;
            if (start == null) {
                if (other.start != null)
                    return false;
            } else if (!start.equals(other.start))
                return false;
            if (visible != other.visible)
                return false;
            return true;
        }
    }
}
