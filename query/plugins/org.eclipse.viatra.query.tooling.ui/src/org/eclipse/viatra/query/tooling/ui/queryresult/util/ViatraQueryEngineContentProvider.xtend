/*******************************************************************************
 * Copyright (c) 2010-2016, Abel Hegedus, IncQuery Labs Ltd.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Abel Hegedus - initial API and implementation
 *******************************************************************************/
package org.eclipse.viatra.query.tooling.ui.queryresult.util

import com.google.common.collect.ImmutableList
import java.util.List
import org.eclipse.emf.ecore.EObject
import org.eclipse.emf.ecore.resource.Resource
import org.eclipse.emf.ecore.resource.ResourceSet
import org.eclipse.emf.edit.ui.provider.AdapterFactoryContentProvider
import org.eclipse.jface.viewers.ITreeContentProvider
import org.eclipse.viatra.query.runtime.api.AdvancedViatraQueryEngine
import org.eclipse.viatra.query.runtime.api.ViatraQueryEngineManager
import org.eclipse.viatra.query.runtime.api.ViatraQueryEngineOptions
import org.eclipse.viatra.query.runtime.base.api.BaseIndexOptions
import org.eclipse.viatra.query.runtime.emf.EMFScope
import org.eclipse.viatra.query.runtime.matchers.backend.QueryEvaluationHint
import org.eclipse.xtend.lib.annotations.Accessors
import org.eclipse.jface.viewers.Viewer
import org.eclipse.viatra.query.runtime.api.ViatraQueryEngineLifecycleListener
import org.eclipse.viatra.query.runtime.api.ViatraQueryMatcher
import org.eclipse.viatra.query.runtime.api.IPatternMatch
import org.eclipse.jface.viewers.TreeViewer
import java.util.ArrayList

/**
 * @author Abel Hegedus
 *
 */
class ViatraQueryEngineContentProvider implements ITreeContentProvider {
    
    private static class EngineListener implements ViatraQueryEngineLifecycleListener {
        
        val TreeViewer viewer
        val AdvancedViatraQueryEngine engine
        var disposed = false
        
        
        new(AdvancedViatraQueryEngine engine, TreeViewer viewer) {
            this.viewer = viewer
            this.engine = engine
            engine.addLifecycleListener(this)
        }
        
        override engineBecameTainted(String message, Throwable t) {
            viewer.tree.display.asyncExec[viewer.add(engine, new EngineError(message, t))]
        }
        
        override engineDisposed() {
            disposed = true
        }
        
        override engineWiped() {}
        
        override matcherInstantiated(ViatraQueryMatcher<? extends IPatternMatch> matcher) {}
        
        def dispose() {
            if (disposed) {
                engine.removeLifecycleListener(this)
                disposed = true
            }
        }
    }
    
    protected AdapterFactoryContentProvider adapterFactoryContentProvider
    @Accessors
    protected boolean traverseResources = true
    @Accessors
    protected boolean traverseEObjects = true
    
    TreeViewer viewer
    List<EngineListener> listeners = new ArrayList()
    
    
    new(){
        val adapterFactory = QueryResultViewUtil.getGenericAdapterFactory()
        adapterFactoryContentProvider = new AdapterFactoryContentProvider(adapterFactory)
    }
    
    override getChildren(Object parentElement) {
        return parentElement.childrenInternal
    }
    
    dispatch def getChildrenInternal(Object parentElement) {
        return adapterFactoryContentProvider.getChildren(parentElement).toList
    }
    
    dispatch def getChildrenInternal(EObject parentElement) {
        if(traverseEObjects){
            return adapterFactoryContentProvider.getChildren(parentElement).toList
        } else {
            return emptyList
        }
    }
    
    dispatch def getChildrenInternal(Resource parentElement) {
        if(traverseResources){
            return adapterFactoryContentProvider.getChildren(parentElement).toList
        } else {
            return emptyList
        }
    }

    dispatch def getChildrenInternal(ViatraQueryEngineManager parentElement) {
        return parentElement.existingQueryEngines.toList
    }

    dispatch def getChildrenInternal(AdvancedViatraQueryEngine parentElement) {
        val engineOptions = parentElement.engineOptions
        val scope = parentElement.scope
        listeners.add(new EngineListener(parentElement, viewer))
        if(scope instanceof EMFScope) {
            val roots = scope.scopeRoots
            val options = scope.options
            return ImmutableList.builder.addAll(roots).add(engineOptions).add(options).build
        } else {
            return #[scope, engineOptions]
        }
    }

    dispatch def getChildrenInternal(BaseIndexOptions parentElement) {
        val baseOptions = #[
            '''Dynamic EMF mode''' -> parentElement.dynamicEMFMode
            ,'''Wildcard mode''' -> parentElement.wildcardMode
            ,'''Traverse only well-behaving features''' -> parentElement.traverseOnlyWellBehavingDerivedFeatures
            ,'''Resource filter''' -> parentElement.resourceFilterConfiguration
            ,'''Object filter''' -> parentElement.objectFilterConfiguration
        ]
        return baseOptions
    }
    
    dispatch def List<?> getChildrenInternal(ViatraQueryEngineOptions parentElement) {
        return parentElement.engineDefaultHints.childrenInternal
    }
    
    dispatch def getChildrenInternal(QueryEvaluationHint parentElement) {
        val builder = ImmutableList.builder.add(parentElement.queryBackendFactory)
        if(!parentElement.backendHintSettings.empty){
            builder.add(parentElement.backendHintSettings.entrySet)
        } else {
            builder.add("No hints specified")
        }
        return builder.build
    }
    
    dispatch def getChildrenInternal(ResourceSet parentElement) {
        return parentElement.resources
    }
    
    override getElements(Object inputElement) {
        return inputElement.children
    }
    
    override getParent(Object element) {
        return null
    }
    
    override hasChildren(Object element) {
        return element.children !== null && !element.children.empty
    }
    
    override dispose() {
        listeners.forEach[dispose]
    }
    
    override inputChanged(Viewer viewer, Object oldInput, Object newInput) {
        this.viewer = viewer as TreeViewer
    }
    
}