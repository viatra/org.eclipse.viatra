/*******************************************************************************
 * Copyright (c) 2010-2012, Mark Czotter, Istvan Rath and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Mark Czotter - initial API and implementation
 *******************************************************************************/
package org.eclipse.viatra.query.tooling.ui.dialog;

import java.util.Collection;

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.viatra.query.runtime.api.IPatternMatch;
import org.eclipse.viatra.query.runtime.api.ViatraQueryMatcher;

/**
 * @author Mark Czotter
 * 
 */
public class PatternMatchDialogContentProvider implements ITreeContentProvider {

    private ViatraQueryMatcher<? extends IPatternMatch> matcher;
    private Collection<? extends IPatternMatch> matches;

    public PatternMatchDialogContentProvider(ViatraQueryMatcher<? extends IPatternMatch> matcher,
            Collection<? extends IPatternMatch> matches) {
        this.matcher = matcher;
        this.matches = matches;
    }

    @Override
    public void dispose() {
        this.matcher = null;
        this.matches = null;
    }

    @Override
    public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
        if (newInput instanceof ViatraQueryMatcher<?> && newInput != oldInput) {
            this.matcher = (ViatraQueryMatcher<?>) newInput;
            this.matches = matcher.getAllMatches();
        }
    }

    @Override
    public Object[] getElements(Object inputElement) {
        if (inputElement instanceof ViatraQueryMatcher<?>) {
            return matches.toArray();
        }
        return null;
    }

    @Override
    public Object[] getChildren(Object parentElement) {
        if (parentElement instanceof ViatraQueryMatcher<?>) {
            return matches.toArray();
        }
        return null;
    }

    @Override
    public Object getParent(Object element) {
        if (element instanceof IPatternMatch) {
            return matcher;
        }
        return null;
    }

    @Override
    public boolean hasChildren(Object element) {
        if (element instanceof ViatraQueryMatcher<?>) {
            return !matches.isEmpty();
        }
        return false;
    }

}
