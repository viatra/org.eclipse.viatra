/*******************************************************************************
 * Copyright (c) 2010-2014, Balint Lorand, Zoltan Ujhelyi, Abel Hegedus, Tamas Szabo, Istvan Rath and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Zoltan Ujhelyi, Abel Hegedus, Tamas Szabo - original initial API and implementation
 *   Balint Lorand - revised API and implementation
 *******************************************************************************/

package org.eclipse.incquery.validation.core.api;

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
