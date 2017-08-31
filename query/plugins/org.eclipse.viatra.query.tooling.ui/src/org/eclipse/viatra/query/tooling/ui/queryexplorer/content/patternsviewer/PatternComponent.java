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

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.eclipse.viatra.query.tooling.ui.queryexplorer.QueryExplorer;

/**
 * A component inside the pattern hierarchy.
 * 
 * @author Tamas Szabo (itemis AG)
 * 
 */
public abstract class PatternComponent {

    protected String patternNameFragment;
    protected boolean selected;
    protected PatternComposite parent;

    public PatternComponent() {
        selected = true;
    }

    /**
     * Returns the parent element of the component. The root component will should return null.
     * 
     * @return the parent of the component
     */
    public PatternComposite getParent() {
        return this.parent;
    }

    public boolean getCheckedState() {
        return this.selected;
    }

    /**
     * Updates the checked and the "has children" states of this {@link PatternComponent} in the patterns viewer.
     */
    public abstract void updateHasChildren();

    public Collection<PatternComponent> setCheckedState(boolean checked) {
        Set<PatternComponent> changedComponents = new HashSet<>();

        if (this.selected != checked) {
            changedComponents.add(this);
            this.selected = checked;
            if(QueryExplorer.getInstance() != null) {
                QueryExplorer.getInstance().getPatternsViewer().setChecked(this, checked);
            }
        }

        changedComponents.addAll(propagateSelectionStateDownwards());
        if (this.getParent() != null) {
            changedComponents.addAll(this.getParent().propagateSelectionStateUpwards());
        }

        return changedComponents;
    }

    protected Set<PatternComponent> propagateSelectionStateUpwards() {
        // by default it does nothing
        return Collections.emptySet();
    }

    protected Set<PatternComponent> propagateSelectionStateDownwards() {
        // by default it does nothing
        return Collections.emptySet();
    }

    /**
     * Returns the prefix of the fully qualified pattern name for the given component.
     * 
     * @return the prefix of the pattern fqn
     */
    public abstract String getFullPatternNamePrefix();

    /**
     * Returns the fragment inside the fully qualified pattern name for the given component.
     * 
     * @return the pattern fqn fragment
     */
    public String getPatternNameFragment() {
        return patternNameFragment;
    }

    @Override
    public String toString() {
        return patternNameFragment;
    }
}
