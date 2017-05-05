/*******************************************************************************
 * Copyright (c) 2004-2015, Istvan David, Istvan Rath and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Istvan David - initial API and implementation
 *******************************************************************************/

package org.eclipse.viatra.cep.core.engine.compiler.rules

import com.google.common.base.Function
import com.google.common.base.Preconditions
import com.google.common.collect.Multimaps
import java.util.Collection
import org.eclipse.viatra.cep.core.engine.compiler.EpsilonTransitionMatcher
import org.eclipse.viatra.cep.core.engine.compiler.EquivalentStatesMatcher
import org.eclipse.viatra.cep.core.engine.compiler.EquivalentTransitionsMatcher
import org.eclipse.viatra.cep.core.engine.compiler.TransformationBasedCompiler
import org.eclipse.viatra.cep.core.engine.compiler.builders.BuilderPrimitives
import org.eclipse.viatra.cep.core.metamodels.automaton.Automaton
import org.eclipse.viatra.cep.core.metamodels.automaton.EpsilonTransition
import org.eclipse.viatra.cep.core.metamodels.automaton.FinalState
import org.eclipse.viatra.cep.core.metamodels.automaton.InitState
import org.eclipse.viatra.cep.core.metamodels.automaton.InternalModel
import org.eclipse.viatra.cep.core.metamodels.automaton.Parameter
import org.eclipse.viatra.cep.core.metamodels.automaton.State
import org.eclipse.viatra.cep.core.metamodels.automaton.Transition
import org.eclipse.viatra.cep.core.metamodels.automaton.TypedTransition
import org.eclipse.viatra.cep.core.metamodels.trace.TraceModel

class OptimizationRules extends MappingRules {

    private extension BuilderPrimitives builderPrimitives

    new(InternalModel internalModel, TraceModel traceModel) {
        super(internalModel, traceModel)
        builderPrimitives = new BuilderPrimitives(traceModel)
    }

    override getAllRules() {
        return #[mergeUponEpsilonTransitionRule, mergeEquivalentTransitionsRule, mergeEquivalentStatesRule]
    }

    /**
     * Transformation rule to merge {@link State}s connected by an {@link EpsilonTransition}.
     */
    val mergeUponEpsilonTransitionRule = createRule.precondition(EpsilonTransitionMatcher::querySpecification).action[
        if (preState == postState) {
           removeTransition(transition)	        
        } else {	        
           val keepPreState = preState instanceof InitState || preState instanceof FinalState
           val keepPostState = postState instanceof InitState || postState instanceof FinalState
           Preconditions::checkArgument(!(keepPreState && keepPostState))
           if(keepPreState){
               mergeStates(postState, preState, transition)
           } else if(keepPostState){
               mergeStates(preState, postState, transition)	
           } else{
               mergeStates(preState, postState, transition)
           }
        }
    ].build

    /**
     * Transformation rule to merge equivalent {@link Transition}s.
     */
    val mergeEquivalentTransitionsRule = createRule.precondition(EquivalentTransitionsMatcher::querySpecification).action [
        unifyParameters(transition2, transition1)
        removeTransition(transition1)
    ].build

    /**
     * Transformation rule to merge equivalent {@link State}s.
     */
    val mergeEquivalentStatesRule = createRule.precondition(EquivalentStatesMatcher::querySpecification).action [
        if (postState1 == postState2) {
            mergeStates(postState2, postState1, transition2, transition1) 
        } else {
            Preconditions::checkArgument(!((postState1 instanceof InitState) && (postState2 instanceof FinalState)))
            Preconditions::checkArgument(!((postState1 instanceof FinalState) && (postState2 instanceof InitState)))
    
            switch (postState1) {
                InitState:
                    mergeStates(postState2, postState1, transition2, transition1)
                FinalState:
                    mergeStates(postState2, postState1, transition2, transition1)
                default:
                    switch (postState2) {
                        InitState: mergeStates(postState1, postState2, transition1, transition2)
                        FinalState: mergeStates(postState1, postState2, transition1, transition2)
                        default: mergeStates(postState1, postState2, transition1, transition2)
                    }
            }
        }	
    ].build

//	/**
//	 * Merge a {@link State} into an {@link InitState} through a {@link Transition}.
//	 */
//	private def mergeStates(InitState stateToKeep, State stateToDelete, Transition transition) {
//		mergeStates(stateToDelete, stateToKeep, transition)
//	}
//
//	/**
//	 * Merge a {@link State} into an {@link FinalState} through a {@link Transition}.
//	 */
//	private def mergeStates(FinalState stateToKeep, State stateToDelete, Transition transition) {
//		mergeStates(stateToDelete, stateToKeep, transition)
//	}

    /**
     * Merge two {@link State}s with the respective associated {@link Transition}s.
     */
    private def mergeStates(State stateToDelete, State stateToKeep, TypedTransition transitionToRemove,
        TypedTransition transitionToKeep) {
        unifyParameters(transitionToKeep, transitionToRemove)
        mergeStates(stateToDelete, stateToKeep, transitionToRemove)
    }

    /**
     * Merge two {@link State}s through a {@link Transition}.
     */
    private def mergeStates(State stateToDelete, State stateToKeep, Transition transition) {
        removeTransition(transition)
        if (stateToDelete != stateToKeep) 
          mergeStates(stateToDelete, stateToKeep)
    }

    /**
     * Merge two {@link State}s.
     */
    private def mergeStates(State stateToDelete, State stateToKeep) {
        stateToKeep.inTransitions += stateToDelete.inTransitions
        stateToKeep.outTransitions += stateToDelete.outTransitions

        stateToKeep.inStateOf += stateToDelete.inStateOf
        stateToKeep.outStateOf += stateToDelete.outStateOf

        Preconditions::checkArgument(stateToDelete.outTransitions.forall[t|t instanceof EpsilonTransition])
        removeState(stateToDelete)
    }
    

    /**
     * Unify the parameters of two {@link Transitions}s.
     */
    protected def void unifyParameters(TypedTransition transitionToKeep, TypedTransition transitionToRemove) {
        Preconditions::checkArgument(transitionToKeep.guards.map[eventType.id] == transitionToRemove.guards.map[eventType.id])
        
        val automaton = transitionToKeep.eContainer.eContainer as Automaton
        val Collection<Runnable> unifications = newArrayList()
        
        val toKeepParamsByPosition = Multimaps.index(transitionToKeep.parameters) [position]
        for (Parameter b : transitionToRemove.parameters) {
            val bName = b.symbolicName
            val toKeepParams = toKeepParamsByPosition.get(b.position)
            if (toKeepParams.empty) {
                unifications += [
                    transitionToKeep.parameters += b
                ] as Runnable
            } else for (Parameter a : toKeepParams) { 
                val aName = a.symbolicName
                if (aName != bName) {
                    if (bName == TransformationBasedCompiler.OMITTED_PARAMETER_SYMBOLIC_NAME) {
                        // NOP
                    } else if (aName == TransformationBasedCompiler.OMITTED_PARAMETER_SYMBOLIC_NAME){
                        unifications += getLocalUnification(transitionToKeep, b, a)
                    } else if (aName.contains('|')) {
                        unifications += getGlobalUnification(automaton, bName, aName)
                    } else {
                        Preconditions::checkState(bName.contains('|'))
                        unifications += getGlobalUnification(automaton, aName, bName)
                    }
                }               
            }
        }
        
        unifications.forEach[run]
    }
    
    def Runnable getLocalUnification(TypedTransition transition, Parameter toKeep, Parameter toRemove) {
        return [
            transition.parameters -= toRemove
            transition.parameters += toKeep            
        ]
    }
    
    def Runnable getGlobalUnification(Automaton automaton, String keepName, String removeName) {
        return [
            automaton.states.forEach[outTransitions.filter(TypedTransition).forEach[parameters.forEach[
                if (symbolicName == removeName)
                    symbolicName = keepName
            ]]]
        ]
    }
    
    
}