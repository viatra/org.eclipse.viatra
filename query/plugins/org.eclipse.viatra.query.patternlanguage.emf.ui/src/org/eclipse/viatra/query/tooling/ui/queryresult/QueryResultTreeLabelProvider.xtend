/** 
 * Copyright (c) 2010-2016, Abel Hegedus, IncQuery Labs Ltd.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-v20.html.
 * 
 * SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.viatra.query.tooling.ui.queryresult

import org.eclipse.emf.edit.ui.provider.AdapterFactoryLabelProvider
import org.eclipse.jface.viewers.ColumnLabelProvider
import org.eclipse.swt.graphics.Image
import org.eclipse.viatra.query.runtime.api.IPatternMatch
import org.eclipse.viatra.query.runtime.emf.helper.ViatraQueryRuntimeHelper
import org.eclipse.viatra.query.tooling.ui.queryresult.util.QueryResultViewUtil
import org.eclipse.viatra.query.patternlanguage.emf.ui.EMFPatternLanguageUIPlugin

/**
 * @author Abel Hegedus
 */
package class QueryResultTreeLabelProvider extends ColumnLabelProvider {
    
    val imageRegistry = EMFPatternLanguageUIPlugin.getInstance().getImageRegistry()
    AdapterFactoryLabelProvider adapterFactoryLabelProvider
    static val String QUERY_EXPLORER_ANNOTATION = "QueryExplorer";
    static val String LABEL_ANNOTATION = "Label";
    
    new() {
        val adapterFactory = QueryResultViewUtil.getGenericAdapterFactory()
        adapterFactoryLabelProvider = new AdapterFactoryLabelProvider(adapterFactory)
    }
    
    override Image getImage(Object element) {
        return element.imageInternal;
    }
    
    dispatch def Image getImageInternal(QueryResultTreeInput element) {
        imageRegistry.get(EMFPatternLanguageUIPlugin.ICON_ROOT)
    }

    dispatch def Image getImageInternal(QueryResultTreeMatcher<?> element) {
        if(element.exception !== null){
            imageRegistry.get(EMFPatternLanguageUIPlugin.ICON_ERROR)
        } else {
            imageRegistry.get(EMFPatternLanguageUIPlugin.ICON_MATCHER)
        }
    }

    dispatch def Image getImageInternal(IPatternMatch element) {
        imageRegistry.get(EMFPatternLanguageUIPlugin.ICON_MATCH)
    }
    
    dispatch def Image getImageInternal(Object element) {
        return adapterFactoryLabelProvider.getImage(element)
    }
    
    override String getText(Object element) {
        return element.textInternal
    }
    
    dispatch def String getTextInternal(QueryResultTreeInput element) {
        return element.engine.toString
    }
    
    dispatch def String getTextInternal(QueryResultTreeMatcher<?> element) {
        if(element.exception !== null) {
            return '''«element.entry?.fullyQualifiedName» - «element.exception.message»'''
        }
        val matcher = element.matcher
        val count = element.matchCount
        val countMsg = switch count {
            case 0 : "No matches"
            case 1 : "1 match"
            default : '''«count» matches'''
        }
        val filterMsg = if (element.filtered) {
            " (Filtered)"
        } else {
            ""
        }
        return '''«matcher.specification.fullyQualifiedName» - «countMsg»«filterMsg»'''
    }
    
    dispatch def String getTextInternal(IPatternMatch element) {
        val message = getMessage(element)
        if(message !== null) {
            return ViatraQueryRuntimeHelper.getMessage(element, message, [adapterFactoryLabelProvider.getText(it)])
        }
        return element.prettyPrint
    }
    
    dispatch def String getTextInternal(Object element) {
        adapterFactoryLabelProvider.getText(element)
    }
    
    def String getMessage(IPatternMatch match) {
        // Check format annotation from the Label and Query Explorer annotations
        match.specification.getFirstAnnotationByName(LABEL_ANNOTATION).flatMap[getFirstValue("message", String)]
            .orElse(match.specification.getFirstAnnotationByName(QUERY_EXPLORER_ANNOTATION)
                .flatMap[getFirstValue("message", String)].orElse(// No formatting annotation found
                    if (match.parameterNames().isEmpty) {
                        "(Match)"
                    } else {
                        '''«FOR param : match.parameterNames SEPARATOR ", "»«param»=$«param»$«ENDFOR»'''
                    }
                )            
            )
    }
}
