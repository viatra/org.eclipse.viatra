/** 
 * Copyright (c) 2010-2016, Abel Hegedus, IncQuery Labs Ltd.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-v20.html.
 * 
 * SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.viatra.query.tooling.ui.queryresult

import com.google.common.base.Preconditions
import org.eclipse.emf.edit.ui.provider.AdapterFactoryContentProvider
import org.eclipse.jface.viewers.ITreeContentProvider
import org.eclipse.jface.viewers.TreeViewer
import org.eclipse.jface.viewers.Viewer
import org.eclipse.viatra.query.runtime.api.IPatternMatch
import org.eclipse.viatra.query.tooling.ui.queryresult.util.QueryResultViewUtil

/**
 * @author Abel Hegedus
 */
package class QueryResultTreeContentProvider implements ITreeContentProvider, IQueryResultViewModelListener {
    
    protected TreeViewer viewer
    protected QueryResultTreeInput input
    protected AdapterFactoryContentProvider adapterFactoryContentProvider
    
    new(){
        val adapterFactory = QueryResultViewUtil.getGenericAdapterFactory()
        adapterFactoryContentProvider = new AdapterFactoryContentProvider(adapterFactory)
    }
    
    override void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
        Preconditions.checkArgument(viewer instanceof TreeViewer)
        this.viewer = viewer as TreeViewer
        if (oldInput instanceof QueryResultTreeInput) {
             // remove listeners
             oldInput.removeListener(this)
        }
        if (newInput instanceof QueryResultTreeInput) {
            this.input = newInput
            // initialize listeners
            newInput.addListener(this)
        } else if (newInput !== null) {
            throw new IllegalArgumentException(
                String.format("Invalid input type %s for List Viewer.", newInput.getClass().getName()))
        }
    }

    override void dispose() {
        this.input?.removeListener(this)
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
    
    def dispatch Object[] getChildrenInternal(QueryResultTreeMatcher<?> inputElement) {
        if(inputElement.exception !== null) {
            return null
        }
        return inputElement.filteredMatches
    }
    
    def dispatch Object[] getChildrenInternal(IPatternMatch inputElement) {
        if(!inputElement.parameterNames.empty){
            return inputElement.toArray
        } else {
            return null
        }
    }

    def dispatch Object[] getChildrenInternal(Object inputElement) {
        return null
    }
    override Object getParent(Object element) {
        return element.parentInternal
    }
    
    def dispatch Object getParentInternal(QueryResultTreeInput inputElement) {
        return null
    }
    
    def dispatch Object getParentInternal(QueryResultTreeMatcher<?> inputElement) {
        return inputElement.parent
    }

    def dispatch Object getParentInternal(IPatternMatch inputElement) {
        return input.matchers.get(inputElement.specification.fullyQualifiedName)
    }
    
    def dispatch Object getParentInternal(Object inputElement) {
        return null
    }
    
    override boolean hasChildren(Object element) {
        return element.hasChildrenInternal
    }
    
    def dispatch boolean hasChildrenInternal(QueryResultTreeInput inputElement) {
        return !inputElement.matchers.empty
    }
    
    def dispatch boolean hasChildrenInternal(QueryResultTreeMatcher<?> inputElement) {
        if(inputElement.exception !== null) {
            return false
        }
        return inputElement.matchCount > 0
    }
    
    def dispatch boolean hasChildrenInternal(IPatternMatch inputElement) {
        if(!inputElement.parameterNames.empty){
            return true
        } else {
            return false
        }
    }
    
    def dispatch boolean hasChildrenInternal(Object inputElement) {
        return false
    }
    
    override matcherAdded(QueryResultTreeMatcher<?> matcher) {
        viewer.tree.display.asyncExec[
            if(!viewer.tree.isDisposed){
                viewer.add(matcher.parent, matcher)
            }
        ]
    }
     
    override matcherFilterUpdated(QueryResultTreeMatcher<?> matcher) {
        matcher.matchCount = matcher.countFilteredMatches
        viewer.tree.display.asyncExec[
            if(!viewer.tree.isDisposed){
                viewer.refresh(matcher)
            }
        ]
    }
    
    override matcherRemoved(QueryResultTreeMatcher<?> matcher) {
        viewer.tree.display.asyncExec[
            if(!viewer.tree.isDisposed){
                viewer.remove(matcher)
            }
        ]
    }
    
    override matchAdded(QueryResultTreeMatcher<?> matcher, IPatternMatch match) {
        if (matcher.filterMatch.isCompatibleWith(match)) {
            matcher.matchCount = matcher.matchCount + 1
        }
        viewer.tree.display.asyncExec[
            if(!viewer.tree.isDisposed){
                viewer.add(matcher, match)
                viewer.update(matcher, null)
            }
        ]
    }
    
    override matchUpdated(QueryResultTreeMatcher<?> matcher, IPatternMatch match) {
        viewer.tree.display.asyncExec[
            if(!viewer.tree.isDisposed){
                viewer.refresh(match)
                viewer.update(matcher, null)
            }
        ]
    }
    
    override matchRemoved(QueryResultTreeMatcher<?> matcher, IPatternMatch match) {
        if (matcher.filterMatch.isCompatibleWith(match)) {
            matcher.matchCount = matcher.matchCount - 1
        }
        viewer.tree.display.asyncExec[
            if(!viewer.tree.isDisposed){
                viewer.remove(match)
                viewer.update(matcher, null)
            }
        ]
    }
    
}
