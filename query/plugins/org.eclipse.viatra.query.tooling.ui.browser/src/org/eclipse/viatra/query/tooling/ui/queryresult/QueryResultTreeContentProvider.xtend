/** 
 * Copyright (c) 2010-2016, Abel Hegedus, IncQuery Labs Ltd.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Contributors:
 * Abel Hegedus - initial API and implementation
 */
package org.eclipse.viatra.query.tooling.ui.queryresult

import org.eclipse.jface.viewers.ITreeContentProvider
import org.eclipse.jface.viewers.TreeViewer
import org.eclipse.jface.viewers.Viewer
import com.google.common.base.Preconditions
import org.eclipse.viatra.query.runtime.api.IPatternMatch

/**
 * @author Abel Hegedus
 */
package class QueryResultTreeContentProvider implements ITreeContentProvider, IQueryResultViewModelListener {
    
    protected TreeViewer viewer
    protected QueryResultTreeInput input
    
    override void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
        Preconditions.checkArgument(viewer instanceof TreeViewer)
        this.viewer = viewer as TreeViewer
        if (oldInput instanceof QueryResultTreeInput) {
             // remove listeners
             oldInput.removeListener(this)
        }
        if (newInput instanceof QueryResultTreeInput) {
            this.input = newInput as QueryResultTreeInput
            // initialize listeners
            newInput.addListener(this)
        } else if (newInput !== null) {
            throw new IllegalArgumentException(
                String.format("Invalid input type %s for List Viewer.", newInput.getClass().getName()))
        }
    }

    override void dispose() {
        if(this.input != null) {
            this.input.removeListener(this)
        }
    }

    override Object[] getElements(Object inputElement) {
        return inputElement.children
    }

    override Object[] getChildren(Object parentElement) {
        return parentElement.childrenInternal
    }
    
    def dispatch Object[] getChildrenInternal(QueryResultTreeInput inputElement) {
        inputElement.matchers.values
    }
    
    def dispatch Object[] getChildrenInternal(QueryResultTreeMatcher inputElement) {
        if(inputElement.exception != null) {
            return null
        }
        inputElement.matcher.allMatches
    }
    
    def dispatch Object[] getChildrenInternal(IPatternMatch inputElement) {
        null
    }

    override Object getParent(Object element) {
        return element.parentInternal
    }
    
    def dispatch Object getParentInternal(QueryResultTreeInput inputElement) {
        return null
    }
    
    def dispatch Object getParentInternal(QueryResultTreeMatcher inputElement) {
        return inputElement.parent
    }

    def dispatch Object getParentInternal(IPatternMatch inputElement) {
        return input.matchers.get(inputElement.specification.fullyQualifiedName)
    }
    
    override boolean hasChildren(Object element) {
        return element.hasChildrenInternal
    }
    
    def dispatch boolean hasChildrenInternal(QueryResultTreeInput inputElement) {
        return !inputElement.matchers.empty
    }
    
    def dispatch boolean hasChildrenInternal(QueryResultTreeMatcher inputElement) {
        if(inputElement.exception != null) {
            return false
        }
        return inputElement.matcher.countMatches > 0
    }
    
    def dispatch boolean hasChildrenInternal(IPatternMatch inputElement) {
        false
    }
    
    override matcherAdded(QueryResultTreeMatcher matcher) {
        viewer.tree.display.asyncExec[
            if(!viewer.tree.isDisposed){
                viewer.add(matcher.parent, matcher)
            }
        ]
    }
    
    override matcherRemoved(QueryResultTreeMatcher matcher) {
        viewer.tree.display.asyncExec[
            if(!viewer.tree.isDisposed){
                viewer.remove(matcher)
            }
        ]
    }
    
    override matchAdded(QueryResultTreeMatcher matcher, IPatternMatch match) {
        viewer.tree.display.asyncExec[
            if(!viewer.tree.isDisposed){
                viewer.add(matcher, match)
                viewer.update(matcher, null)
            }
        ]
    }
    
    override matchUpdated(QueryResultTreeMatcher matcher, IPatternMatch match) {
        viewer.tree.display.asyncExec[
            if(!viewer.tree.isDisposed){
                viewer.update(match, null)
                viewer.update(matcher, null)
            }
        ]
    }
    
    override matchRemoved(QueryResultTreeMatcher matcher, IPatternMatch match) {
        viewer.tree.display.asyncExec[
            if(!viewer.tree.isDisposed){
                viewer.remove(match)
                viewer.update(matcher, null)
            }
        ]
    }
    
}
