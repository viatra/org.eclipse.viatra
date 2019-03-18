/*******************************************************************************
 * Copyright (c) 2010-2012, Zoltan Ujhelyi, Istvan Rath and Daniel Varro
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-v20.html.
 * 
 * SPDX-License-Identifier: EPL-2.0
 *******************************************************************************/

package org.eclipse.viatra.query.tooling.core.generator

import org.eclipse.viatra.query.tooling.core.project.XmlDocumentHelper
import org.w3c.dom.Document
import org.w3c.dom.Element

class ExtensionGenerator {
    
    Document document = XmlDocumentHelper.emptyXmlDocument
    
    def contribExtension(String id, String point, (Element) => void initializer) {
        val ex = document.createElement("extension")
        ex.setAttribute("id", id)
        
        ex.setAttribute("point", point)
        ex.init(initializer)
        ex.normalize
        new ExtensionData(ex)
    }
    
    def contribElement(Element parent, String name, (Element) => void initializer) {
        val el = document.createElement(name)
        parent.appendChild(el)
        el.init(initializer)
    }
    
    def contribAttribute(Element element, String name, String value) {
        element.setAttribute(name, value)
    }
    
    def private <T> T init (T obj, (T)=>void init) {
        init.apply(obj)
        return obj
    }
}