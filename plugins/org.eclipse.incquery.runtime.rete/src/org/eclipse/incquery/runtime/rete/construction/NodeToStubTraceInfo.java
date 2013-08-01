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

import org.eclipse.incquery.runtime.rete.matcher.IPatternMatcherContext;
import org.eclipse.incquery.runtime.rete.network.Node;

/**
 * @author Bergmann Gabor
 *
 */
public class NodeToStubTraceInfo<PatternDescription> implements Node.TraceInfo.PatternTraceInfo {
	
    Stub<?> stub;
    PatternDescription pattern;
    IPatternMatcherContext<PatternDescription> context;

	/**
	 * @param stub
	 * @param pattern
	 * @param context
	 */
	public NodeToStubTraceInfo(Stub<?> stub, PatternDescription pattern,
			IPatternMatcherContext<PatternDescription> context) {
		super();
		this.stub = stub;
		this.pattern = pattern;
		this.context = context;
	}

	@Override
	public String toString() {
		return "->" + getPatternName() + "~" + stub.toString();
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

	/**
	 * @return the stub
	 */
	public Stub<?> getStub() {
		return stub;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.incquery.runtime.rete.network.Node.TraceInfo.PatternTraceInfo#getPatternName()
	 */
	@Override
	public String getPatternName() {
		if (pattern != null)
			return context.printPattern(pattern);
		else 
			return "";
	}
	

}
