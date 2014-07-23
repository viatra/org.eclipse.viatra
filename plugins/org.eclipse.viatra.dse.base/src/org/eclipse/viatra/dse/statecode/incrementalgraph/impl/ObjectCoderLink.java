/*******************************************************************************
 * Copyright (c) 2010-2014, Miklos Foldenyi, Andras Szabolcs Nagy, Abel Hegedus, Akos Horvath, Zoltan Ujhelyi and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Contributors:
 *   Miklos Foldenyi - initial API and implementation
 *   Andras Szabolcs Nagy - initial API and implementation
 *******************************************************************************/
package org.eclipse.viatra.dse.statecode.incrementalgraph.impl;

import org.eclipse.viatra.dse.statecode.graph.impl.IModelReference;

public class ObjectCoderLink {
    private final ObjectCoderNode parent;

    private final ObjectCoderNode child;

    private final IModelReference reference;

    private final EdgeType linkType;

    public ObjectCoderLink(ObjectCoderNode parent, ObjectCoderNode child, IModelReference reference, EdgeType linkType) {
        this.parent = parent;
        this.child = child;
        this.reference = reference;
        this.linkType = linkType;
        child.getParentLinks().add(this);
    }

    public enum EdgeType {
        NO_PARENT {
            @Override
            public String toString() {
                return "R";
            }
        },
        OUTGOING_EDGE {
            @Override
            public String toString() {
                return "O";
            }
        },
        INCOMING_EDGE {
            @Override
            public String toString() {
                return "I";
            }
        },
        SELF_EDGE {
            @Override
            public String toString() {
                return "S";
            }
        }
    }

    public ObjectCoderNode getParent() {
        return parent;
    }

    public ObjectCoderNode getChild() {
        return child;
    }

    public IModelReference getReference() {
        return reference;
    }

    public EdgeType getLinkType() {
        return linkType;
    }

}
