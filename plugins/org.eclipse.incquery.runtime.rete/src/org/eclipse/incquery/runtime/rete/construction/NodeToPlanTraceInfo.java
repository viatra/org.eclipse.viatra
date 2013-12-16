/*******************************************************************************
 * Copyright (c) 2010-2013, Bergmann Gabor, Istvan Rath and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Bergmann Gabor - initial API and implementation
 *******************************************************************************/
package org.eclipse.incquery.runtime.rete.construction;

import org.eclipse.incquery.runtime.matchers.IPatternMatcherContext;
import org.eclipse.incquery.runtime.matchers.planning.SubPlan;
import org.eclipse.incquery.runtime.rete.network.Node;

/**
 * @author Bergmann Gabor
 *
 */
public class NodeToPlanTraceInfo implements Node.TraceInfo.PatternTraceInfo {
	
    SubPlan plan;
    Object pattern;
    IPatternMatcherContext context;

	public NodeToPlanTraceInfo(SubPlan plan, Object pattern,
			IPatternMatcherContext context) {
		super();
		this.plan = plan;
		this.pattern = pattern;
		this.context = context;
	}

	@Override
	public String toString() {
		return "->" + getPatternName() + "~" + plan.toString();
	}
	
	@Override
	public boolean propagateToIndexerParent() {
		return true;
	}
	@Override
	public boolean propagateFromIndexerToSupplierParent() {
		return false;
	}
	@Override
	public boolean propagateFromStandardNodeToSupplierParent() {
		return false;
	}
	@Override
	public boolean propagateToProductionNodeParentAlso() {
		return false;
	}

	public SubPlan getPlan() {
		return plan;
	}

	@Override
	public String getPatternName() {
		if (pattern != null)
			return context.printPattern(pattern);
		else 
			return "";
	}
	

}
