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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.eclipse.emf.ecore.EObject;

public class VQLPattern {
    /**
     * Settable package name of the pattern
     */
    private String packageName;

    /**
     * Automatically specified namespace URI
     */
    private String nsUri;

    /**
     * Settable pattern name
     */
    private String patternName;

    private Set<EObject> selectedEObjects;

    private Set<EObject> discoveredEObjects;

    /**
     * All of the constraints in the body of the pattern
     */
    private Set<VQLConstraint> constraints;

    private Set<VQLNegConstraint> negConstraints;

    private List<VQLPath> paths;

    private List<VQLAttribute> attributes;

    private Set<VQLAttribute> discoveredObjectsAttributes;

    /**
     * Constant for default package name
     */
    public static final String DEFAULT_PACKAGENAME = "pckgname";

    /**
     * Constant for default pattern name
     */
    public static final String DEFAULT_PATTERNNAME = "patternOne";

    public VQLPattern(Set<EObject> selectedEObjects) {

        // initializing with default values
        this.packageName = VQLPattern.DEFAULT_PACKAGENAME;
        this.patternName = VQLPattern.DEFAULT_PATTERNNAME;
        this.selectedEObjects = selectedEObjects;

        if (this.selectedEObjects != null && !this.selectedEObjects.isEmpty())
            this.nsUri = this.selectedEObjects.iterator().next().eClass().getEPackage().getNsURI(); // workaround for
                                                                                                    // determining the
                                                                                                    // nsuri

        this.discoveredEObjects = new HashSet<EObject>();
        this.constraints = new HashSet<VQLConstraint>();
        this.negConstraints = new HashSet<VQLNegConstraint>();
        this.paths = new ArrayList<VQLPath>();
        this.attributes = new ArrayList<VQLAttribute>();
        this.discoveredObjectsAttributes = new HashSet<VQLAttribute>();
    }

    public boolean validate() {
        return (this.packageName.trim().isEmpty() || this.nsUri.trim().isEmpty() || this.patternName.trim().isEmpty()
                || this.selectedEObjects.isEmpty());
    }

    public void reset() {
        this.constraints.clear();
        this.negConstraints.clear();
        this.discoveredEObjects.clear();
        this.paths.clear();
        this.discoveredObjectsAttributes.clear();
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public String getPatternName() {
        return patternName;
    }

    public void setPatternName(String patternName) {
        this.patternName = patternName;
    }

    public String getNsUri() {
        return nsUri;
    }

    public Set<EObject> getSelectedEObjects() {
        return selectedEObjects;
    }

    public Set<EObject> getDiscoveredEObjects() {
        return discoveredEObjects;
    }

    public Set<VQLConstraint> getConstraints() {
        return constraints;
    }

    public Set<VQLNegConstraint> getNegConstraints() {
        return negConstraints;
    }

    public List<VQLPath> getPaths() {
        return paths;
    }

    public List<VQLAttribute> getAttributes() {
        return attributes;
    }

    public Set<VQLAttribute> getDiscoveredObjectsAttributes() {
        return discoveredObjectsAttributes;
    }

    public boolean isPatternConnected() {
        EObject start = this.selectedEObjects.iterator().next();
        LinkedList<EObject> queue = new LinkedList<EObject>();
        List<EObject> vectorSet = new ArrayList<EObject>();
        queue.add(start);
        vectorSet.add(start);
        while (!queue.isEmpty()) {
            EObject t = queue.poll();
            for (EObject u : this.getAdjacentNodes(t)) {
                if (!vectorSet.contains(u)) {
                    vectorSet.add(u);
                    if (vectorSet.containsAll(this.selectedEObjects))
                        return true;

                    queue.add(u);
                }
            }
        }
        return false;
    }

    private Set<EObject> getAdjacentNodes(EObject o) {
        Set<EObject> ret = new HashSet<EObject>();
        for (VQLConstraint constraint : this.constraints) {
            if (constraint.getStart().equals(o))
                ret.add(constraint.getEnd());
            if (constraint.getEnd().equals(o))
                ret.add(constraint.getStart());
        }
        return ret;
    }
}