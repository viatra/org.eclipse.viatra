/*******************************************************************************
 * Copyright (c) 2004-2013, Abel Hegedus, Zoltan Ujhelyi and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Abel Hegedus, Zoltan Ujhelyi - initial API and implementation
 *******************************************************************************/
package org.eclipse.viatra2.emf.runtime.rules;

import java.util.HashSet;

/**
 * Helper collection for grouping transformation rules 
 */
@SuppressWarnings("rawtypes")
public class TransformationRuleGroup extends HashSet<BatchTransformationRule> {

	private static final long serialVersionUID = 1L;

	public TransformationRuleGroup() {
		super();
	}
	
	public TransformationRuleGroup(BatchTransformationRule... rules) {
		super(rules.length);
		for (BatchTransformationRule rule : rules) {
			add(rule);
		}
	}
}
