/*******************************************************************************
 * Copyright (c) 2010-2014, Balint Lorand, Zoltan Ujhelyi, Abel Hegedus, Tamas Szabo, Istvan Rath and Daniel Varro
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-v20.html.
 * 
 * SPDX-License-Identifier: EPL-2.0
 *******************************************************************************/

package org.eclipse.viatra.addon.validation.core.api;

/**
 * Severity type to define the severity of a violation.
 * 
 * @author Balint Lorand
 *
 */
public enum Severity {
    INFO, WARNING, ERROR;

    public static Severity fromInteger(int x) {
        switch (x) {
        case 0:
            return INFO;
        case 1:
            return WARNING;
        case 2:
            return ERROR;
        }
        return null;
    }
}
