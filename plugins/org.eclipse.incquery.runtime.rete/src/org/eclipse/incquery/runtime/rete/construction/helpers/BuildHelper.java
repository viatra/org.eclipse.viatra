/*******************************************************************************
 * Copyright (c) 2004-2010 Gabor Bergmann and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Gabor Bergmann - initial API and implementation
 *******************************************************************************/

package org.eclipse.incquery.runtime.rete.construction.helpers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.eclipse.incquery.runtime.base.api.FunctionalDependencyHelper;
import org.eclipse.incquery.runtime.rete.construction.Buildable;
import org.eclipse.incquery.runtime.rete.construction.Stub;
import org.eclipse.incquery.runtime.rete.construction.psystem.PConstraint;
import org.eclipse.incquery.runtime.rete.construction.psystem.PVariable;
import org.eclipse.incquery.runtime.rete.construction.psystem.basicdeferred.ExportedParameter;
import org.eclipse.incquery.runtime.rete.tuple.TupleMask;

/**
 * @author Bergmann Gábor
 * 
 */
public class BuildHelper {

    /**
     * If two or more variables are the same in the variablesTuple of the stub, then a checker node is built to enforce
     * their equality.
     * 
     * @return the derived stub that contains the additional checkers, or the original if no action was neccessary.
     */
    public static <StubHandle> Stub<StubHandle> enforceVariableCoincidences(Buildable<?, StubHandle, ?> buildable,
            Stub<StubHandle> stub) {
        Map<Object, List<Integer>> indexWithMupliplicity = stub.getVariablesTuple().invertIndexWithMupliplicity();
        for (Map.Entry<Object, List<Integer>> pVariableIndices : indexWithMupliplicity.entrySet()) {
            List<Integer> indices = pVariableIndices.getValue();
            if (indices.size() > 1) {
                int[] indexArray = new int[indices.size()];
                int m = 0;
                for (Integer index : indices)
                    indexArray[m++] = index;
                stub = buildable.buildEqualityChecker(stub, indexArray);
                // TODO also trim here?
            }
        }
        return stub;

    }

    /**
     * Trims the results in the stub into a collector, by selecting exported variables in a particular order.
     */
    public static <StubHandle, Collector> void projectIntoCollector(Buildable<?, StubHandle, Collector> buildable,
            Stub<StubHandle> stub, Collector collector, PVariable[] selectedVariables) {
        Stub<StubHandle> trimmer = project(buildable, stub, selectedVariables, false);
        buildable.buildConnection(trimmer, collector);
    }

    /**
     * Trims the results in the stub by selecting exported variables in a particular order.
     * 
     * @return the derived stub.
     * @param enforceUniqueness if true, uniqueness after projection will be enforced
     */
	public static <StubHandle, Collector> Stub<StubHandle> project(
			Buildable<?, StubHandle, Collector> buildable,
			Stub<StubHandle> stub, PVariable[] selectedVariables,
			boolean enforceUniqueness) {
		int paramNum = selectedVariables.length;
        int[] tI = new int[paramNum];
        for (int i = 0; i < paramNum; i++) {
            tI[i] = stub.getVariablesIndex().get(selectedVariables[i]);
        }
        int tiW = stub.getVariablesTuple().getSize();
        TupleMask trim = new TupleMask(tI, tiW);
        Stub<StubHandle> trimmer = buildable.buildTrimmer(stub, trim, enforceUniqueness);
		return trimmer;
	}

    /**
     * Calculated index mappings for a join, based on the common variables of the two parent stubs.
     * 
     * @author Bergmann Gábor
     * 
     */
    public static class JoinHelper<StubHandle> {
        private TupleMask primaryMask;
        private TupleMask secondaryMask;
        private TupleMask complementerMask;

        /**
         * @pre enforceVariableCoincidences() has been called on both sides.
         * @param primaryStub
         * @param secondaryStub
         */
        public JoinHelper(Stub<StubHandle> primaryStub, Stub<StubHandle> secondaryStub) {
            super();

            Set<PVariable> primaryVariables = primaryStub.getVariablesTuple().getDistinctElements();
            Set<PVariable> secondaryVariables = secondaryStub.getVariablesTuple().getDistinctElements();
            int oldNodes = 0;
            Set<Integer> introducingSecondaryIndices = new TreeSet<Integer>();
            for (PVariable var : secondaryVariables) {
                if (primaryVariables.contains(var))
                    oldNodes++;
                else
                    introducingSecondaryIndices.add(secondaryStub.getVariablesIndex().get(var));
            }
            int[] primaryIndices = new int[oldNodes];
            final int[] secondaryIndices = new int[oldNodes];
            int k = 0;
            for (PVariable var : secondaryVariables) {
                if (primaryVariables.contains(var)) {
                    primaryIndices[k] = primaryStub.getVariablesIndex().get(var);
                    secondaryIndices[k] = secondaryStub.getVariablesIndex().get(var);
                    k++;
                }
            }
            int[] complementerIndices = new int[introducingSecondaryIndices.size()];
            int l = 0;
            for (Integer integer : introducingSecondaryIndices) {
                complementerIndices[l++] = integer;
            }
            primaryMask = new TupleMask(primaryIndices, primaryStub.getVariablesTuple().getSize());
            secondaryMask = new TupleMask(secondaryIndices, secondaryStub.getVariablesTuple().getSize());
            complementerMask = new TupleMask(complementerIndices, secondaryStub.getVariablesTuple().getSize());

        }

        /**
         * @return the primaryMask
         */
        public TupleMask getPrimaryMask() {
            return primaryMask;
        }

        /**
         * @return the secondaryMask
         */
        public TupleMask getSecondaryMask() {
            return secondaryMask;
        }

        /**
         * @return the complementerMask
         */
        public TupleMask getComplementerMask() {
            return complementerMask;
        }

    }

    public static <StubHandle> Stub<StubHandle> naturalJoin(Buildable<?, StubHandle, ?> buildable,
            Stub<StubHandle> primaryStub, Stub<StubHandle> secondaryStub) {
        JoinHelper<StubHandle> joinHelper = new JoinHelper<StubHandle>(primaryStub, secondaryStub);
        return buildable.buildBetaNode(primaryStub, secondaryStub, joinHelper.getPrimaryMask(),
                joinHelper.getSecondaryMask(), joinHelper.getComplementerMask(), false);
    }
    
    
    /**
     * Reduces the number of tuples by trimming (existentially quantifying) the set of variables that <ul>
     * <li> are in the tuple, 
     * <li> are not exported parameters, 
     * <li> have all their constraints already enforced,
     * </ul> and thus will not be needed anymore.
     * 
     * @param onlyIfNotDetermined if true, no trimming performed unless there is at least one such variable  
     * @return the stub after the trimming (possibly the original)
     */
    public static <StubHandle> Stub<StubHandle> trimUnneccessaryVariables(Buildable<?, StubHandle, ?> buildable,
            Stub<StubHandle> stub, boolean onlyIfNotDetermined) {
    	Set<PVariable> canBeTrimmed = new HashSet<PVariable>();
    	Set<PVariable> variablesInTuple = stub.getVariablesTuple().getDistinctElements();
    	for (PVariable trimCandidate : variablesInTuple) {
    		if (trimCandidate.getReferringConstraintsOfType(ExportedParameter.class).isEmpty()) {
    			if (stub.getAllEnforcedConstraints().containsAll(trimCandidate.getReferringConstraints()))
    				canBeTrimmed.add(trimCandidate);
    		}
    	}
		final Set<PVariable> retainedVars = setMinus(variablesInTuple, canBeTrimmed);   	
    	if (!canBeTrimmed.isEmpty() && !(onlyIfNotDetermined && areVariablesDetermined(stub, retainedVars, canBeTrimmed))) {
    		// TODO add ordering? 
    		final PVariable[] selectedVariablesArray = new ArrayList<PVariable>(retainedVars).toArray(new PVariable[retainedVars.size()]);
    		stub = project(buildable, stub, selectedVariablesArray, true);
    	}
    	return stub;
    }
    
    
    /**
     * @return true iff one set of given variables functionally determine the other set according to the stub's constraints
     */
    public static <StubHandle> boolean areVariablesDetermined(Stub<StubHandle> stub, Set<PVariable> determining, Set<PVariable> determined) {
        Map<Set<PVariable>, Set<PVariable>> dependencies = new HashMap<Set<PVariable>, Set<PVariable>>();
        for (PConstraint pConstraint : stub.getAllEnforcedConstraints())
            dependencies.putAll(pConstraint.getFunctionalDependencies());
		final Set<PVariable> closure = FunctionalDependencyHelper.closureOf(determining, dependencies);
		final boolean isDetermined = closure.containsAll(determined);
		return isDetermined;
	}

	private static <T> Set<T> setMinus(Set<T> a, Set<T> b) {
		Set<T> difference = new HashSet<T>(a);
		difference.removeAll(b);
		return difference;
	}
    
    

}
