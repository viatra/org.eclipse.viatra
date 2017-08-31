/*******************************************************************************
 * Copyright (c) 2010-2012, Zoltan Ujhelyi, Tamas Szabo, Istvan Rath and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Zoltan Ujhelyi, Tamas Szabo - initial API and implementation
 *******************************************************************************/

package org.eclipse.viatra.query.tooling.ui.queryexplorer.content.patternsviewer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.jface.viewers.CheckboxTreeViewer;
import org.eclipse.viatra.query.tooling.ui.queryexplorer.QueryExplorer;

/**
 * This class represents a composite element inside a pattern hierarchy.
 * 
 * @author Tamas Szabo
 * 
 */
public class PatternComposite extends PatternComponent {

    protected List<PatternComponent> children;
    private Map<String, PatternComposite> fragmentMap;

    public PatternComposite(String patternNameFragment, PatternComposite parent) {
        super();
        this.patternNameFragment = patternNameFragment;
        this.children = new ArrayList<>();
        this.fragmentMap = new HashMap<>();
        this.parent = parent;
    }
    
    public void clear() {
        this.children.clear();
        this.fragmentMap.clear();
    }

    /**
     * Returns the list of pattern components downwards the tree for the given fully qualified pattern name.
     * 
     * @param patternFragment
     *            the fully qualified name of the pattern
     * @return the list of components
     */
    public List<PatternComponent> find(String patternFragment) {
        List<PatternComponent> components = new ArrayList<>();
        find(patternFragment, components);
        return components;
    }

    /**
     * Returns the root above this composite element. This will result either the Plug-in or the Runtime composite.
     * 
     * @return the root composite
     */
    public PatternComposite getRoot() {
        if (parent == null) {
            return this;
        } else {
            return parent.getRoot();
        }
    }

    private void find(String patternFragment, List<PatternComponent> components) {
        String[] tokens = patternFragment.split("\\.");

        if (tokens.length == 1) {
            for (PatternComponent pc : children) {
                if (pc.getPatternNameFragment().matches(patternFragment)) {
                    components.add(pc);
                }
            }
        } else {
            String prefix = tokens[0];
            String suffix = patternFragment.substring(prefix.length() + 1);
            PatternComposite composite = fragmentMap.get(prefix);
            if (composite != null) {
                components.add(composite);
                composite.find(suffix, components);
            }
        }
    }

    /**
     * Add a new component under the composite element based on the given pattern name fragment.
     * 
     * @param patternFragment
     *            the pattern name fragment
     */
    public PatternComponent addComponent(String patternFragment) {
        if (patternFragment == null || "".equals(patternFragment)) {
            return null;
        }

        String[] tokens = patternFragment.split("\\.");

        if (tokens.length == 1) {
            PatternLeaf leaf = new PatternLeaf(patternFragment, this);
            leaf.selected = true;
            children.add(leaf);
            return leaf;
        } else {
            String prefix = tokens[0];
            String suffix = patternFragment.substring(prefix.length() + 1);

            PatternComposite composite = fragmentMap.get(prefix);

            if (composite == null) {
                composite = new PatternComposite(prefix, this);
                fragmentMap.put(prefix, composite);
                children.add(composite);
            }
            return composite.addComponent(suffix);
        }
    }

    /**
     * Returns the list of (ALL) leaf objects under this composite.
     * 
     * @return the list of leaves
     */
    public List<PatternLeaf> getAllLeaves() {
        List<PatternLeaf> leaves = new ArrayList<>();

        for (PatternComponent component : children) {
            if (component instanceof PatternLeaf) {
                leaves.add((PatternLeaf) component);
            } else {
                leaves.addAll(((PatternComposite) component).getAllLeaves());
            }
        }

        return leaves;
    }

    /**
     * Returns the direct leaf children elements under this composite.
     * 
     * @return the list of direct leaf elements
     */
    public List<PatternLeaf> getDirectLeaves() {
        List<PatternLeaf> leaves = new ArrayList<>();

        for (PatternComponent component : children) {
            if (component instanceof PatternLeaf) {
                leaves.add((PatternLeaf) component);
            }
        }

        return leaves;
    }

    /**
     * Removes all composite elements which do not have a leaf component under it.
     */
    public void purge() {
        List<PatternComponent> copyOfChildren = new ArrayList<>(children);

        for (PatternComponent component : copyOfChildren) {
            if (component instanceof PatternComposite) {
                PatternComposite composite = (PatternComposite) component;
                composite.purge();
            }
        }

        if (this.getAllLeaves().size() == 0) {
            QueryExplorer.getInstance().getPatternsViewerRoot().getGenericPatternsRoot()
                    .removeComponent(getFullPatternNamePrefix());
        }
    }

    /**
     * Returns ALL children elements under the given composite.
     * 
     * @return
     */
    public List<PatternComponent> getAllChildren() {
        List<PatternComponent> result = new ArrayList<>(this.children);

        for (PatternComponent component : children) {
            if (component instanceof PatternComposite) {
                result.addAll(((PatternComposite) component).getAllChildren());
            }
        }

        return result;
    }

    @Override
    protected Set<PatternComponent> propagateSelectionStateUpwards() {
        Set<PatternComponent> changedComponents = new HashSet<>();

        boolean allSelected = true;
        for (PatternComponent child : children) {
            if (!child.selected) {
                allSelected = false;
            }
        }

        if (allSelected != this.selected) {
            if(QueryExplorer.getInstance() != null) {
                QueryExplorer.getInstance().getPatternsViewer().setChecked(this, allSelected);
            }
            this.selected = allSelected;
            changedComponents.add(this);
        }

        if (this.parent != null) {
            changedComponents.addAll(this.parent.propagateSelectionStateUpwards());
        }

        return changedComponents;
    }

    @Override
    protected Set<PatternComponent> propagateSelectionStateDownwards() {
        Set<PatternComponent> changedComponents = new HashSet<>();

        for (PatternComponent child : children) {
            if (child.selected != this.selected) {
                changedComponents.add(child);
                child.selected = this.selected;
                if(QueryExplorer.getInstance() != null) {
                    QueryExplorer.getInstance().getPatternsViewer().setChecked(child, this.selected);
                }
            }
            changedComponents.addAll(child.propagateSelectionStateDownwards());
        }

        return changedComponents;
    }

    /**
     * This method removes the component matching the given pattern name fragment.
     * 
     * @param patternFragment
     *            the pattern name fragment
     */
    public void removeComponent(String patternFragment) {
        if (patternFragment == null || "".equals(patternFragment)) {
            return;
        }

        String[] tokens = patternFragment.split("\\.");
        if (tokens.length == 1) {
            PatternComponent component = null;
            for (PatternComponent c : children) {
                if (c.getPatternNameFragment().matches(patternFragment)) {
                    component = c;
                }
            }
            if (component != null) {
                children.remove(component);
                fragmentMap.remove(patternFragment);
            }
        } else {
            String prefix = tokens[0];
            String suffix = patternFragment.substring(prefix.length() + 1);

            PatternComposite composite = fragmentMap.get(prefix);

            if (composite != null) {
                composite.removeComponent(suffix);
            }
        }
    }

    /**
     * Returns the list of direct children elements under the composite.
     * 
     * @return the list of children elements
     */
    public List<PatternComponent> getDirectChildren() {
        return children;
    }

    @Override
    public String getFullPatternNamePrefix() {
        StringBuilder sb = new StringBuilder(patternNameFragment);

        if (parent != null && parent.getParent() != null) {
            sb.insert(0, ".");
            sb.insert(0, parent.getFullPatternNamePrefix());
        }
        return sb.toString();
    }

    @Override
    public void updateHasChildren() {
        if(QueryExplorer.getInstance() != null) {
            CheckboxTreeViewer patternsViewer = QueryExplorer.getInstance().getPatternsViewer();
            if (patternsViewer.getExpandedState(this)) {
                for (PatternComponent pc : this.children) {
                    pc.updateHasChildren();
                }
            } else {
                //patternsViewer.setChecked(this, this.selected);
                patternsViewer.setHasChildren(this, this.children.size() > 0);
            }
        }
    }

    @Override
    public int hashCode() {
        int hash = patternNameFragment.hashCode();
        for (PatternComponent pc : children) {
            hash += 31 * pc.hashCode();
        }
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if ((obj == null) || (obj.getClass() != this.getClass())) {
            return false;
        }

        PatternComposite composite = (PatternComposite) obj;

        return (this.patternNameFragment == composite.patternNameFragment) && (this.parent == composite.parent)
                && (this.children.equals(composite.children));
    }
}
