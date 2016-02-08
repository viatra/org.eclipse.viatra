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
package org.eclipse.viatra.dse.guidance;

import java.util.Map;

import org.eclipse.emf.ecore.EModelElement;

public class Predicate {

    public enum EquationType {
        EQUALS("="), GREATEROREQUAL(">="), LESSOREQUAL("<=");

        private final String toString;

        private EquationType(String toString) {
            this.toString = toString;
        }

        public String getToString() {
            return toString;
        }
    }

    private final Map<EModelElement, Integer> coefficients;
    private final EquationType type;
    private final int rhs;

    public Predicate(Map<EModelElement, Integer> coefficients, EquationType type, int rhs) {
        this.coefficients = coefficients;
        this.type = type;
        this.rhs = rhs;
    }

    public Map<EModelElement, Integer> getCoefficients() {
        return coefficients;
    }

    public EquationType getType() {
        return type;
    }

    public int getRhs() {
        return rhs;
    }

}
