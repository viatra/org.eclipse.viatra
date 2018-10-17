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

import com.google.common.collect.Maps
import java.util.Map
import org.eclipse.core.runtime.IStatus
import org.eclipse.core.runtime.Status
import org.eclipse.jface.viewers.TreePath
import org.eclipse.viatra.query.runtime.registry.IQuerySpecificationRegistry
import org.eclipse.viatra.query.runtime.registry.IQuerySpecificationRegistryChangeListener
import org.eclipse.viatra.query.runtime.registry.IQuerySpecificationRegistryEntry
import org.eclipse.viatra.query.runtime.registry.IRegistryView
import org.eclipse.viatra.query.runtime.registry.view.AbstractRegistryView
import org.eclipse.xtend.lib.annotations.Accessors
import org.eclipse.xtend.lib.annotations.Data
import org.eclipse.xtend.lib.annotations.FinalFieldsConstructor
import org.eclipse.viatra.query.tooling.ui.ViatraQueryGUIPlugin

/** 
 * @author Abel Hegedus
 */
class QueryRegistryTreeInput {
 
    @Accessors(PUBLIC_GETTER)   
    Map<String,QueryRegistryTreeSource> sources = Maps.newTreeMap
    
    @Accessors(PUBLIC_GETTER)
    IQuerySpecificationRegistry registry
    
    @Accessors(PUBLIC_GETTER)
    IRegistryView view
    
    @Accessors(PUBLIC_GETTER)
    IQuerySpecificationRegistryChangeListener listener
    
    new(IQuerySpecificationRegistry registry) {
        this.registry = registry
        view = registry.createView[
            return new AbstractRegistryView(registry, true) {
                override protected isEntryRelevant(IQuerySpecificationRegistryEntry entry) {
                    true
                }
            }
        ]
        view.entries.forEach[
            val source = sourceIdentifier.getOrCreateSource
            val treePackage = source.source.getOrCreatePackage(fullyQualifiedName.packageName)
            val entry = new QueryRegistryTreeEntry(treePackage.pckg, it)
            treePackage.pckg.entries.put(fullyQualifiedName, entry)
        ]
    }
    
    def void setListener(IQuerySpecificationRegistryChangeListener listener) {
        if (this.listener !== null) {
            view.removeViewListener(this.listener)
        }
        this.listener = listener
        if (listener !== null) {
            view.addViewListener(listener)
        }
    }
    
    def QueryRegistryTreeInputChange addEntryToInput(IQuerySpecificationRegistryEntry entry) {
        val source = entry.sourceIdentifier.getOrCreateSource
        val treePackage = source.source.getOrCreatePackage(entry.fullyQualifiedName.packageName)
        val treePckg = treePackage.pckg
        val treeEntry = new QueryRegistryTreeEntry(treePckg, entry)
        val emptyPckg = treePckg.entries.empty
        treePackage.pckg.entries.put(entry.fullyQualifiedName, treeEntry)
        return new QueryRegistryTreeInputChange(true, treeEntry, treePackage.pckgAffected || emptyPckg, treePckg, source.sourceAffected, source.source)
    }
    
    def QueryRegistryTreeInputChange removeEntry(IQuerySpecificationRegistryEntry entry) {
        val sourceDTO = entry.sourceIdentifier.getOrCreateSource
        val source = sourceDTO.source
        val treePackageDTO = sourceDTO.source.getOrCreatePackage(entry.fullyQualifiedName.packageName)
        val treePckg = treePackageDTO.pckg
        val treeEntry = treePckg.entries.remove(entry.fullyQualifiedName)
        val emptyPckg = treePckg.entries.empty
        if(emptyPckg) {
            source.packages.remove(treePckg.packageName)
        }
        val emptySource = source.packages.empty
        if(emptySource) {
            sources.remove(source.sourceIdentifier)
        }
        return new QueryRegistryTreeInputChange(true, treeEntry, treePackageDTO.pckgAffected || emptyPckg, treePckg, sourceDTO.sourceAffected || emptySource, source)
    }
    
    def QueryRegistryTreeInputChange getOrCreateSource(String sourceIdentifier) {
        val existingSource = sources.get(sourceIdentifier)
        if(existingSource === null) {
            val newSource = new QueryRegistryTreeSource(this, sourceIdentifier)
            sources.put(sourceIdentifier, newSource)
            return new QueryRegistryTreeInputChange(false, null, false, null, true, newSource)
        } else {
            return new QueryRegistryTreeInputChange(false, null, false, null, false, existingSource)
        }
    }
    
    def QueryRegistryTreeInputChange getOrCreatePackage(QueryRegistryTreeSource source, String packageName) {
        val existingPackage = source.packages.get(packageName)
        if(existingPackage === null) {
            val newPackage = new QueryRegistryTreePackage(source, packageName)
            source.packages.put(packageName, newPackage)
            return new QueryRegistryTreeInputChange(false, null, true, newPackage, false, null)
        } else {
            return new QueryRegistryTreeInputChange(false, null, false, existingPackage, false, null)
        }
    }
    
    def String getPackageName(String fullyQualifiedName) {
        return fullyQualifiedName.substring(0, Math.max(0, fullyQualifiedName.lastIndexOf('.')))
    }
    
    def dispatch TreePath getTreePath(QueryRegistryTreePackage pckg) {
        return new TreePath(#[pckg.parent, pckg])
    }
    
    def dispatch TreePath getTreePath(QueryRegistryTreeEntry entry) {
        return entry.parent.treePath.createChildPath(entry)
    }
}

@FinalFieldsConstructor
class QueryRegistryTreeSource {
    
    @Accessors(PUBLIC_GETTER)
    final QueryRegistryTreeInput parent
    @Accessors(PUBLIC_GETTER)
    final String sourceIdentifier
    @Accessors(PUBLIC_GETTER)
    final Map<String, QueryRegistryTreePackage> packages = Maps.newTreeMap 
    
}

@FinalFieldsConstructor
class QueryRegistryTreePackage {
    
    @Accessors(PUBLIC_GETTER)
    final QueryRegistryTreeSource parent
    @Accessors(PUBLIC_GETTER)
    final String packageName
    @Accessors(PUBLIC_GETTER)
    final Map<String, QueryRegistryTreeEntry> entries = Maps.newTreeMap 
    
}

@FinalFieldsConstructor
class QueryRegistryTreeEntry {
    
    @Accessors(PUBLIC_GETTER)
    final QueryRegistryTreePackage parent
    @Accessors(PUBLIC_GETTER)
    final IQuerySpecificationRegistryEntry entry
    
    @Accessors(PUBLIC_GETTER)
    boolean isLoaded = false
    
    def boolean load() {
        if(!loaded) {
            try{
                // load specification class
                val specification = entry.get
                if(specification !== null){
                    isLoaded = true
                }
                return true
            } catch (Exception ex) {
                val logMessage = String.format("Query Registry has encountered an error during loading of query %s: %s", entry.fullyQualifiedName, ex.message)
                ViatraQueryGUIPlugin.getDefault().getLog().log(new Status(
                        IStatus.ERROR, ViatraQueryGUIPlugin.getDefault().getBundle().getSymbolicName(), logMessage, ex));
            }
        }
        return false
    }
    
    def String getSimpleName() {
        val fqn = entry.fullyQualifiedName
        val lastDotIndex = fqn.lastIndexOf('.')
        if(lastDotIndex > 0 && lastDotIndex < fqn.length){
            fqn.substring(lastDotIndex + 1, fqn.length)
        } else {
            return fqn
        }
    }
    
    def String getPackageName() {
        val fqn = entry.fullyQualifiedName
        val lastDotIndex = fqn.lastIndexOf('.')
        if(lastDotIndex > 0 && lastDotIndex < fqn.length){
            fqn.substring(0, fqn.lastIndexOf('.'))
        } else {
            return fqn
        }
    }
}

@Data
class QueryRegistryTreeInputChange {
    boolean entryAffected
    QueryRegistryTreeEntry entry
    boolean pckgAffected
    QueryRegistryTreePackage pckg
    boolean sourceAffected
    QueryRegistryTreeSource source
}