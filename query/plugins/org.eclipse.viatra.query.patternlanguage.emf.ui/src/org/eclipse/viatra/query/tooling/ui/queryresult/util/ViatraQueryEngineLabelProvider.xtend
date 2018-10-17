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

import com.google.common.base.Optional
import java.util.Map.Entry
import org.eclipse.emf.edit.ui.provider.AdapterFactoryLabelProvider
import org.eclipse.jface.viewers.LabelProvider
import org.eclipse.viatra.query.runtime.api.ViatraQueryEngine
import org.eclipse.viatra.query.runtime.api.ViatraQueryEngineOptions
import org.eclipse.viatra.query.runtime.base.api.BaseIndexOptions
import org.eclipse.viatra.query.runtime.matchers.backend.IQueryBackendFactory
import org.eclipse.viatra.query.runtime.matchers.backend.QueryEvaluationHint
import org.eclipse.viatra.query.patternlanguage.emf.ui.EMFPatternLanguageUIPlugin

/**
 * @author Abel Hegedus
 *
 */
class ViatraQueryEngineLabelProvider extends LabelProvider {

    val imageRegistry = EMFPatternLanguageUIPlugin.getInstance().getImageRegistry()
    AdapterFactoryLabelProvider adapterFactoryLabelProvider
    
    new() {
        val adapterFactory = QueryResultViewUtil.getGenericAdapterFactory()
        adapterFactoryLabelProvider = new AdapterFactoryLabelProvider(adapterFactory)
    }
    
    override getImage(Object element) {
        return element.imageInternal
    }
    
    dispatch def getImageInternal(ViatraQueryEngine element) {
        imageRegistry.get(EMFPatternLanguageUIPlugin.ICON_VIATRA)
    }
    
    dispatch def getImageInternal(BaseIndexOptions element) {
        imageRegistry.get(EMFPatternLanguageUIPlugin.ICON_BASE_OPTIONS)
    }
    
    dispatch def getImageInternal(ViatraQueryEngineOptions element) {
        imageRegistry.get(EMFPatternLanguageUIPlugin.ICON_ENGINE_OPTIONS)
    }
    
    dispatch def getImageInternal(IQueryBackendFactory element) {
        imageRegistry.get(EMFPatternLanguageUIPlugin.ICON_VQL)
    }
    
    dispatch def getImageInternal(QueryEvaluationHint element) {
        imageRegistry.get(EMFPatternLanguageUIPlugin.ICON_VQL)
    }
    
    dispatch def getImageInternal(EngineError element) {
        imageRegistry.get(EMFPatternLanguageUIPlugin.ICON_ERROR)
    }
    
    dispatch def getImageInternal(Entry<String,Object> element) {
        adapterFactoryLabelProvider.getImage(element.value)
    }
    
    dispatch def getImageInternal(Pair<String,Object> element) {
        adapterFactoryLabelProvider.getImage(element.value)
    }
    
    dispatch def getImageInternal(Object element) {
        adapterFactoryLabelProvider.getImage(element)
    }
    
    override getText(Object element) {
        return element.textInternal
    }
    
    dispatch def getTextInternal(ViatraQueryEngine element) {
        return '''Manage engine (hashcode: «element.hashCode»)'''
    }
    
    dispatch def getTextInternal(BaseIndexOptions element) {
        return '''Base index options: «element.toString»'''
    }
    
    dispatch def getTextInternal(ViatraQueryEngineOptions element) {
        return '''Engine options: «element.toString»'''
    }
    
    dispatch def getTextInternal(IQueryBackendFactory element) {
        return '''Backend factory: «element.backendClass.simpleName»'''
    }
    
    dispatch def getTextInternal(QueryEvaluationHint element) {
        return '''Query evaluation hint'''
    }
    
    dispatch def getTextInternal(Entry<String,Object> element) {
        return '''«element.key»: «Optional.fromNullable(element.value).or("null")»'''
    }
    
    dispatch def getTextInternal(Pair<String,Object> element) {
        return '''«element.key»: «Optional.fromNullable(element.value).or("null")»'''
    }
    
    dispatch def getTextInternal(EngineError element) {
        return "Engine tainted: " + element.message
    }
    
    dispatch def getTextInternal(Object element) {
        adapterFactoryLabelProvider.getText(element)
    }
    
}