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
package org.eclipse.viatra.dse.guidance.dependencygraph.interfaces;

public enum EdgeType {
    TRIGGER_CLASS(InfluanceType.TRIGGER, ClassType.CLASS),
    TRIGGER_REFERENCE(InfluanceType.TRIGGER, ClassType.REFERENCE),
    TRIGGER_ATTRIBUTE(InfluanceType.TRIGGER, ClassType.ATTRIBUTE),
    INHIBIT_CLASS(InfluanceType.INHIBIT, ClassType.CLASS),
    INHIBIT_REFERENCE(InfluanceType.INHIBIT, ClassType.REFERENCE),
    INHIBIT_ATTRIBUTE(InfluanceType.INHIBIT, ClassType.ATTRIBUTE),
    USES_ATTRIBUTE(InfluanceType.NONE, ClassType.ATTRIBUTE);

    public enum InfluanceType {
        TRIGGER,
        INHIBIT,
        NONE
    }

    public enum ClassType {
        CLASS,
        REFERENCE,
        ATTRIBUTE
    }

    private final InfluanceType iType;
    private final ClassType cType;

    private EdgeType(InfluanceType type, ClassType cType) {
        this.iType = type;
        this.cType = cType;
    }

    public boolean isTrigger() {
        if (iType == InfluanceType.TRIGGER) {
            return true;
        }
        return false;
    }

    public boolean isInhibit() {
        if (iType == InfluanceType.INHIBIT) {
            return true;
        }
        return false;
    }

    public boolean isClass() {
        if (cType == ClassType.CLASS) {
            return true;
        }
        return false;
    }

    public boolean isReference() {
        if (cType == ClassType.REFERENCE) {
            return true;
        }
        return false;
    }

    public boolean isAttribute() {
        if (cType == ClassType.ATTRIBUTE) {
            return true;
        }
        return false;
    }

    public static EdgeType getTriggerFor(ClassType type) {
        switch (type) {
        case ATTRIBUTE:
            return USES_ATTRIBUTE;
        case CLASS:
            return TRIGGER_CLASS;
        case REFERENCE:
            return TRIGGER_REFERENCE;
        }
        return null;
    }

    public static EdgeType getInhibitFor(ClassType type) {
        switch (type) {
        case ATTRIBUTE:
            return USES_ATTRIBUTE;
        case CLASS:
            return INHIBIT_CLASS;
        case REFERENCE:
            return INHIBIT_REFERENCE;
        }
        return null;
    }
}
