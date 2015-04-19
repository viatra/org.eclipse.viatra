/*******************************************************************************
 * Copyright (c) 2010-2015, Bergmann Gabor, Istvan Rath and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Bergmann Gabor - initial API and implementation
 *******************************************************************************/
package org.eclipse.incquery.runtime.matchers.psystem;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.incquery.runtime.matchers.context.IInputKey;
import org.eclipse.incquery.runtime.matchers.context.IQueryMetaContext;
import org.eclipse.incquery.runtime.matchers.context.InputKeyImplication;
import org.eclipse.incquery.runtime.matchers.tuple.FlatTuple;
import org.eclipse.incquery.runtime.matchers.tuple.Tuple;

/**
 * A judgement that means that the given tuple of variables will represent a tuple of values that is a member of the extensional relation identified by the given input key.
 * @author Bergmann Gabor
 *
 */
public class TypeJudgement {
	
	private IInputKey inputKey;
	private Tuple variablesTuple;
	/**
	 * @param inputKey
	 * @param variablesTuple
	 */
	public TypeJudgement(IInputKey inputKey, Tuple variablesTuple) {
		super();
		this.inputKey = inputKey;
		this.variablesTuple = variablesTuple;
	}
	public IInputKey getInputKey() {
		return inputKey;
	}
	public Tuple getVariablesTuple() {
		return variablesTuple;
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((inputKey == null) ? 0 : inputKey.hashCode());
		result = prime * result
				+ ((variablesTuple == null) ? 0 : variablesTuple.hashCode());
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof TypeJudgement))
			return false;
		TypeJudgement other = (TypeJudgement) obj;
		if (inputKey == null) {
			if (other.inputKey != null)
				return false;
		} else if (!inputKey.equals(other.inputKey))
			return false;
		if (variablesTuple == null) {
			if (other.variablesTuple != null)
				return false;
		} else if (!variablesTuple.equals(other.variablesTuple))
			return false;
		return true;
	}
	
	public Set<TypeJudgement> getDirectlyImpliedJudgements(IQueryMetaContext context) {
		Set<TypeJudgement> results = new HashSet<TypeJudgement>();
		results.add(this);
		
		Collection<InputKeyImplication> implications = context.getImplications(this.inputKey);
		for (InputKeyImplication inputKeyImplication : implications) {
			results.add(
				new TypeJudgement(
						inputKeyImplication.getImpliedKey(), 
						transcribeVariablesToTuple(inputKeyImplication.getImpliedIndices())
				)
			);
		}
		
		return results;
	}
	private Tuple transcribeVariablesToTuple(List<Integer> indices) {
		Object[] elements = new Object[indices.size()];
		for (int i = 0; i < indices.size(); ++i)
			elements[i] = variablesTuple.get(indices.get(i));
		return new FlatTuple(elements);
	}


}
