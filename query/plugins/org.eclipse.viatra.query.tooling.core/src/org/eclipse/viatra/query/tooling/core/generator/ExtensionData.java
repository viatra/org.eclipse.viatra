/*******************************************************************************
 * Copyright (c) 2010-2012, Zoltan Ujhelyi, Istvan Rath and Daniel Varro
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-v20.html.
 * 
 * SPDX-License-Identifier: EPL-2.0
 *******************************************************************************/
package org.eclipse.viatra.query.tooling.core.generator;

import org.eclipse.viatra.query.runtime.matchers.util.Preconditions;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

import com.google.common.base.Strings;

/**
 * A data descriptor class for describing extension contributions. The class uses a {@link Node} object to store all
 * related data in a DOM format, and stores additional metadata in a typed form for easier management.
 * 
 * @author Zoltan Ujhelyi
 * 
 */
public class ExtensionData {

    private String id;
    private String point;
    private Node node;

    public ExtensionData(Node node) {
        Preconditions.checkArgument("extension".equals(node.getNodeName()),
                "Cannot create extension data from %s nodes", node.getNodeName());
        this.node = node;
        NamedNodeMap atts = node.getAttributes();
        for (int i = 0; i < atts.getLength(); i++) {
            Node att = atts.item(i);
            if ("id".equals(att.getNodeName())) {
                id = att.getNodeValue();
            } else if ("point".equals(att.getNodeName())) {
                point = att.getNodeValue();
            }
        }
        Preconditions.checkArgument(!Strings.isNullOrEmpty(point), "Extension must specify extension point id");
    }

    /**
     * @return the id of the extension declaration; might be null
     */
    public String getId() {
        return id;
    }

    /**
     * @return the extension point id; never null
     */
    public String getPoint() {
        return point;
    }

    /**
     * @return the DOM node describing the entire contents of the extension
     */
    public Node getNode() {
        return node;
    }

    @Override
    public String toString() {
        return String.format("id=%s, point=%s => %s", id, point, node);
    }

    
}
