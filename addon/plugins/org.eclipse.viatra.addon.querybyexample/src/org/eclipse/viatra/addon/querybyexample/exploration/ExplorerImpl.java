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
package org.eclipse.viatra.addon.querybyexample.exploration;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.viatra.addon.querybyexample.code.VariableRegister;
import org.eclipse.viatra.addon.querybyexample.interfaces.IExplorer;
import org.eclipse.viatra.addon.querybyexample.interfaces.beans.VQLAttribute;
import org.eclipse.viatra.addon.querybyexample.interfaces.beans.VQLConstraint;
import org.eclipse.viatra.addon.querybyexample.interfaces.beans.VQLNegConstraint;
import org.eclipse.viatra.addon.querybyexample.interfaces.beans.VQLPath;
import org.eclipse.viatra.addon.querybyexample.interfaces.beans.VQLPattern;
import org.eclipse.viatra.query.runtime.base.api.NavigationHelper;

public class ExplorerImpl implements IExplorer {

    private static final int NO_LONGER_DISCOVER_LIMIT = 20;

    private static class NodeHelper {
        private EObject node;
        private int depth;

        public NodeHelper(EObject n, int d) {
            this.node = n;
            this.depth = d;
        }

        public EObject getNode() {
            return this.node;
        }

        public int getDepth() {
            return this.depth;
        }
    }

    private VQLPattern pattern;

    private VariableRegister variableRegister;

    /**
     * Helper object for (among other things) EIQ API-based backward navigation
     */
    private NavigationHelper navigationHelper;

    public ExplorerImpl(Set<EObject> selectedEObjects, NavigationHelper nh, VariableRegister register) {
        this.navigationHelper = nh;

        // registering the input variables (anchors)
        this.variableRegister = register;
        for (EObject selectedEObject : selectedEObjects) {
            this.variableRegister.registerFixVariable(selectedEObject);
        }

        // creating pattern instance
        this.pattern = new VQLPattern(selectedEObjects);

        // adding attributes
        for (EObject eo : selectedEObjects) {
            this.pattern.getAttributes().addAll(this.getAttributesForEObject(eo));
        }
    }

    @Override
    public void explore(int depth) {
        this.reset();
        for (EObject currentSelectedEObject : this.pattern.getSelectedEObjects()) {
            this.eObjectsDLS(currentSelectedEObject, depth + 1);
        }
    }

    @Override
    public void eObjectsDLS(EObject root, int limit) {
        LinkedList<NodeHelper> stack = new LinkedList<NodeHelper>();
        LinkedList<NodeHelper> actualPathStack = new LinkedList<NodeHelper>();
        NodeHelper startNodeHelper = new NodeHelper(root, 1);
        stack.push(startNodeHelper);
        while (!stack.isEmpty()) {
            NodeHelper v = stack.pop();
            if (v.getDepth() > limit)
                continue;
            if (!actualPathStack.isEmpty() && actualPathStack.peek().getDepth() >= v.getDepth()) {
                while (actualPathStack.peek().getDepth() >= v.getDepth())
                    actualPathStack.pop();
            }
            actualPathStack.push(v);

            if (this.pattern.getSelectedEObjects().contains(v.getNode())) {
                this.registerEIQElements(actualPathStack, root, v.getNode());
            }

            Set<EObject> referredEObjects = this.getAllReferredEObjects(v.getNode());
            outer: for (EObject u : referredEObjects) {
                for (NodeHelper nh : actualPathStack) {
                    if (nh.getNode().equals(u))
                        continue outer;
                }
                stack.push(new NodeHelper(u, v.getDepth() + 1));
            }
        }
    }

    @Override
    public Set<EObject> getAllReferredEObjects(EObject eo) {
        Set<EObject> ret = new HashSet<EObject>();

        List<EReference> refList = eo.eClass().getEAllReferences();
        for (EReference ref : refList) {
            ret.addAll(this.navigationHelper.getReferenceValues(eo, ref));
            ret.addAll(this.navigationHelper.getInverseReferences(eo, ref));
        }

        return ret;
    }

    /**
     * This method registers all EIQ constraints and variables (if not already present) in the pattern, based on the
     * given path in the parameter.
     */
    private void registerEIQElements(LinkedList<NodeHelper> path, EObject pathStart, EObject pathEnd) {
        VQLPath currentPath = new VQLPath();
        currentPath.setStart(pathStart);
        currentPath.setEnd(pathEnd);

        // iteration is for registering the elements in order: from root to current anchor
        for (int i = path.size() - 1; i > 0; i--) {
            EObject start = path.get(i).getNode();
            EObject end = path.get(i - 1).getNode();

            if (!this.pattern.getSelectedEObjects().contains(start)) {
                this.variableRegister.registerFreeVariable(start);
                this.pattern.getDiscoveredEObjects().add(start);
                this.pattern.getDiscoveredObjectsAttributes().addAll(this.getAttributesForEObject(start));
            }
            if (!this.pattern.getSelectedEObjects().contains(end)) {
                this.variableRegister.registerFreeVariable(end);
                this.pattern.getDiscoveredEObjects().add(end);
                this.pattern.getDiscoveredObjectsAttributes().addAll(this.getAttributesForEObject(end));
            }

            Set<VQLConstraint> constraintsToRegister = new HashSet<VQLConstraint>();
            constraintsToRegister.addAll(this.determineAllConstraints(start, end));
            constraintsToRegister.addAll(this.determineAllConstraints(end, start));

            outer: for (VQLConstraint constraintToRegister : constraintsToRegister) {
                if (this.pattern.getConstraints().contains(constraintToRegister)) {
                    for (VQLConstraint c : this.pattern.getConstraints()) {
                        if (c.equals(constraintToRegister)) {
                            currentPath.getConstraints().add(c);
                            continue outer;
                        }
                    }
                } else {
                    this.pattern.getConstraints().add(constraintToRegister);
                    currentPath.getConstraints().add(constraintToRegister);
                }
            }
        }

        // it is not necessary to register empty paths
        if (!currentPath.getConstraints().isEmpty())
            this.pattern.getPaths().add(currentPath);
    }

    @Override
    public Set<VQLConstraint> determineAllConstraints(EObject first, EObject second) {
        Set<VQLConstraint> ret = new HashSet<VQLConstraint>();
        EList<EReference> refList = first.eClass().getEAllReferences();
        for (EReference ref : refList) {
            Collection<EObject> referredObjects = this.navigationHelper.getReferenceValues(first, ref);
            if (referredObjects.contains(second))
                ret.add(new VQLConstraint(first, ref, second));
        }
        return ret;
    }

    private void determineAndRegisterAllNegConstraints(EObject first, EObject second) {
        EList<EReference> refList = first.eClass().getEAllReferences();
        for (EReference ref : refList) {
            if (ref.getEReferenceType().equals(second.eClass())) {
                if (ref.isMany()) {
                    Collection<?> targets = (Collection<?>) first.eGet(ref);
                    if (!targets.contains(second))
                        this.pattern.getNegConstraints().add(new VQLNegConstraint(first, ref, second));
                } else {
                    Object target = first.eGet(ref);
                    if (!target.equals(second))
                        this.pattern.getNegConstraints().add(new VQLNegConstraint(first, ref, second));
                }
            }
        }
    }

    @Override
    public VQLPattern getPattern() {
        return pattern;
    }

    @Override
    public void reset() {
        this.variableRegister.reset();
        this.pattern.reset();
    }

    @Override
    public boolean isPatternConnected() {
        return this.pattern.isPatternConnected();
    }

    @Override
    public int determineCoherenceMinimumDepth() {

        if (this.pattern.getSelectedEObjects() == null || this.pattern.getSelectedEObjects().isEmpty())
            return 0;

        // if there is only one selected item, the pattern will surely be coherent
        if (this.pattern.getSelectedEObjects().size() == 1)
            return 1;

        int ret = 1;
        EObject selectedEObject = this.pattern.getSelectedEObjects().iterator().next();
        while (ret <= NO_LONGER_DISCOVER_LIMIT) {
            this.reset();
            this.eObjectsDLS(selectedEObject, ret + 1);

            if (this.pattern.isPatternConnected())
                return ret;
            ret = ret + 1;
        }
        return 1;
    }

    @Override
    public void findAndRegisterNegativeConstraints() {

        Set<EObject> allEObjectsInPattern = new HashSet<EObject>();
        allEObjectsInPattern.addAll(this.pattern.getSelectedEObjects());
        allEObjectsInPattern.addAll(this.pattern.getDiscoveredEObjects());

        for (EObject first : allEObjectsInPattern) {
            for (EObject second : allEObjectsInPattern) {
                if (!first.equals(second))
                    this.determineAndRegisterAllNegConstraints(first, second);
            }
        }
    }

    private List<VQLAttribute> getAttributesForEObject(EObject eo) {
        List<VQLAttribute> ret = new ArrayList<VQLAttribute>();
        EList<EAttribute> attrList = eo.eClass().getEAllAttributes();
        for (EAttribute attr : attrList) {
            Set<Object> attributeValues = this.navigationHelper.getFeatureTargets(eo, attr);
            if (attributeValues != null && attributeValues.size() == 1)
                ret.add(new VQLAttribute(eo, attr, attributeValues.iterator().next()));
        }
        return ret;
    }
}
