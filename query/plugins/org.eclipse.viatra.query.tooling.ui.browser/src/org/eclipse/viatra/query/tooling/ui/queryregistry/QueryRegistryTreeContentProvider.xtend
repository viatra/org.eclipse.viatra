/** 
 * Copyright (c) 2010-2016, Abel Hegedus, IncQuery Labs Ltd.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Contributors:
 * Abel Hegedus - initial API and implementation
 */
package org.eclipse.viatra.query.tooling.ui.queryregistry

import com.google.common.base.Preconditions
import org.eclipse.jface.viewers.ITreeContentProvider
import org.eclipse.jface.viewers.TreeViewer
import org.eclipse.jface.viewers.Viewer
import org.eclipse.viatra.query.runtime.registry.IQuerySpecificationRegistryChangeListener
import org.eclipse.viatra.query.runtime.registry.IQuerySpecificationRegistryEntry
import org.eclipse.xtend.lib.annotations.FinalFieldsConstructor

class QueryRegistryTreeContentProvider implements ITreeContentProvider {
    protected TreeViewer viewer
    protected QueryRegistryTreeInput input

    @FinalFieldsConstructor
    static class QueryRegistryTreeViewListener implements IQuerySpecificationRegistryChangeListener {
        
        val QueryRegistryTreeInput input
        val TreeViewer viewer
        
        override entryAdded(IQuerySpecificationRegistryEntry entry) {
            val newEntry = input.addEntryToInput(entry)
            viewer.tree.display.asyncExec[
                if(newEntry.sourceAffected){
                    viewer.add(input, newEntry.source)
                }
                if(newEntry.pckgAffected){
                    viewer.add(newEntry.source, newEntry.pckg)
                }
                viewer.add(newEntry.pckg, newEntry.entry)
            ]
        }
        
        override entryRemoved(IQuerySpecificationRegistryEntry entry) {
            val oldEntry = input.removeEntry(entry)
            if(oldEntry !== null) {
                viewer.tree.display.asyncExec[
                    viewer.remove(oldEntry.entry)
                    if(oldEntry.pckgAffected){
                        viewer.remove(oldEntry.pckg)
                    }
                    if(oldEntry.sourceAffected){
                        viewer.remove(oldEntry.source)
                    }
                ]
            }
        }
        
    }

    override void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
        Preconditions.checkArgument(viewer instanceof TreeViewer)
        this.viewer = viewer as TreeViewer
        if (oldInput instanceof QueryRegistryTreeInput) {
             // remove listeners
             oldInput.listener = null
        }
        if (newInput instanceof QueryRegistryTreeInput) {
            this.input = newInput
            // initialize listener
            newInput.listener = new QueryRegistryTreeViewListener(newInput, this.viewer)
        } else if (newInput !== null) {
            throw new IllegalArgumentException(
                String.format("Invalid input type %s for Query Registry.", newInput.getClass().getName()))
        }
    }

    override void dispose() {
    }

    override Object[] getElements(Object inputElement) {
        return inputElement.children
    }

    override Object[] getChildren(Object parentElement) {
        return parentElement.childrenInternal
    }
    
    def dispatch Object[] getChildrenInternal(QueryRegistryTreeInput parentElement) {
        return parentElement.sources.values
    }
    
    def dispatch Object[] getChildrenInternal(QueryRegistryTreeSource parentElement) {
        return parentElement.packages.values
    }
    
    def dispatch Object[] getChildrenInternal(QueryRegistryTreePackage parentElement) {
        return parentElement.entries.values
    }
    
    def dispatch Object[] getChildrenInternal(QueryRegistryTreeEntry parentElement) {
        return null
    }

    override Object getParent(Object element) {
        return element.parentInternal
    }
    
    def dispatch Object getParentInternal(QueryRegistryTreeInput element) {
        return null
    }
    
    def dispatch Object getParentInternal(QueryRegistryTreeSource element) {
        return element.parent
    }
    
    def dispatch Object getParentInternal(QueryRegistryTreePackage element) {
        return element.parent
    }
    
    def dispatch Object getParentInternal(QueryRegistryTreeEntry element) {
        return element.parent
    }

    override boolean hasChildren(Object element) {
        return element.hasChildrenInternal
    }
    
    def dispatch boolean hasChildrenInternal(QueryRegistryTreeInput element) {
        return !element.sources.empty
    }
    
    def dispatch boolean hasChildrenInternal(QueryRegistryTreeSource element) {
        return !element.packages.empty
    }
    
    def dispatch boolean hasChildrenInternal(QueryRegistryTreePackage element) {
        return !element.entries.empty
    }
    
    def dispatch boolean hasChildrenInternal(QueryRegistryTreeEntry element) {
        return false
    }
}


