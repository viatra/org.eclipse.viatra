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

/**
 * @author Abel Hegedus
 *
 */
class ViatraQueryEngineContentProvider implements ITreeContentProvider {
    
    protected AdapterFactoryContentProvider adapterFactoryContentProvider
    @Accessors
    protected boolean traverseResources = true
    @Accessors
    protected boolean traverseEObjects = true
    
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
        if(!parentElement.backendHints.empty){
            builder.add(parentElement.backendHints.entrySet)
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
        return element.children != null && !element.children.empty
    }
    
    /**
     * Default implementation does nothing
     */
    override dispose() {
    }
    
    /**
     * Default implementation does nothing
     */
    override inputChanged(Viewer viewer, Object oldInput, Object newInput) {
    }
    
}