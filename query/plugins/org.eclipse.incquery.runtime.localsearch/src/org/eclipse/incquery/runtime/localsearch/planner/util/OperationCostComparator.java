/*******************************************************************************
 * Copyright (c) 2010-2015, Marton Bur, Zoltan Ujhelyi, Akos Horvath, Istvan Rath and Danil Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Marton Bur - initial API and implementation
 *******************************************************************************/
package org.eclipse.incquery.runtime.localsearch.planner.util;

import java.util.Comparator;

import org.eclipse.incquery.runtime.localsearch.planner.PConstraintInfo;

/**
 * @author Marton Bur
 *
 */
public class OperationCostComparator implements Comparator<PConstraintInfo>{

    @Override
    public int compare(PConstraintInfo o1, PConstraintInfo o2) {
        return Double.compare(o1.getCost(), o2.getCost());
    }

}
