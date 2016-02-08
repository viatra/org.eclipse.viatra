/*******************************************************************************
 * Copyright (c) 2010-2015, Marton Bur, Zoltan Ujhelyi, Akos Horvath, Istvan Rath and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Marton Bur - initial API and implementation
 *******************************************************************************/
package org.eclipse.incquery.runtime.localsearch.matcher.integration;

/**
 * @author Marton Bur
 *
 */
public class LocalSearchHintKeys {
    public static final String ALLOW_INVERSE_NAVIGATION = "org.eclipse.incquery.runtime.localsearch - allow inverse navigation";
    public static final String USE_BASE_INDEX = "org.eclipse.incquery.runtime.localsearch - use base index";

    // This key can be used to influence the core planner algorithm
    public static final String PLANNER_TABLE_ROW_COUNT = "org.eclipse.incquery.runtime.localsearch - row count";
}
