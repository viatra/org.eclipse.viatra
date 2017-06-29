/*******************************************************************************
 * Copyright (c) 2010-2017, Zoltan Ujhelyi, IncQuery Labs Ltd.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Zoltan Ujhelyi - initial API and implementation
 *******************************************************************************/
package org.eclipse.viatra.query.runtime.localsearch.planner.compiler;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.viatra.query.runtime.emf.EMFQueryRuntimeContext;
import org.eclipse.viatra.query.runtime.localsearch.matcher.MatcherReference;
import org.eclipse.viatra.query.runtime.localsearch.operations.ISearchOperation;
import org.eclipse.viatra.query.runtime.localsearch.operations.check.AggregatorCheck;
import org.eclipse.viatra.query.runtime.localsearch.operations.check.BinaryTransitiveClosureCheck;
import org.eclipse.viatra.query.runtime.localsearch.operations.check.CheckConstant;
import org.eclipse.viatra.query.runtime.localsearch.operations.check.CheckPositivePatternCall;
import org.eclipse.viatra.query.runtime.localsearch.operations.check.CountCheck;
import org.eclipse.viatra.query.runtime.localsearch.operations.check.ExpressionCheck;
import org.eclipse.viatra.query.runtime.localsearch.operations.check.ExpressionEvalCheck;
import org.eclipse.viatra.query.runtime.localsearch.operations.check.InequalityCheck;
import org.eclipse.viatra.query.runtime.localsearch.operations.check.NACOperation;
import org.eclipse.viatra.query.runtime.localsearch.operations.extend.AggregatorExtend;
import org.eclipse.viatra.query.runtime.localsearch.operations.extend.CountOperation;
import org.eclipse.viatra.query.runtime.localsearch.operations.extend.ExpressionEval;
import org.eclipse.viatra.query.runtime.localsearch.operations.extend.ExtendBinaryTransitiveClosure;
import org.eclipse.viatra.query.runtime.localsearch.operations.extend.ExtendConstant;
import org.eclipse.viatra.query.runtime.localsearch.operations.extend.ExtendPositivePatternCall;
import org.eclipse.viatra.query.runtime.localsearch.planner.util.CompilerHelper;
import org.eclipse.viatra.query.runtime.matchers.context.IQueryRuntimeContext;
import org.eclipse.viatra.query.runtime.matchers.planning.QueryProcessingException;
import org.eclipse.viatra.query.runtime.matchers.planning.SubPlan;
import org.eclipse.viatra.query.runtime.matchers.planning.operations.PApply;
import org.eclipse.viatra.query.runtime.matchers.planning.operations.POperation;
import org.eclipse.viatra.query.runtime.matchers.planning.operations.PProject;
import org.eclipse.viatra.query.runtime.matchers.planning.operations.PStart;
import org.eclipse.viatra.query.runtime.matchers.psystem.PConstraint;
import org.eclipse.viatra.query.runtime.matchers.psystem.PVariable;
import org.eclipse.viatra.query.runtime.matchers.psystem.basicdeferred.AggregatorConstraint;
import org.eclipse.viatra.query.runtime.matchers.psystem.basicdeferred.ExportedParameter;
import org.eclipse.viatra.query.runtime.matchers.psystem.basicdeferred.ExpressionEvaluation;
import org.eclipse.viatra.query.runtime.matchers.psystem.basicdeferred.Inequality;
import org.eclipse.viatra.query.runtime.matchers.psystem.basicdeferred.NegativePatternCall;
import org.eclipse.viatra.query.runtime.matchers.psystem.basicdeferred.PatternCallBasedDeferred;
import org.eclipse.viatra.query.runtime.matchers.psystem.basicdeferred.PatternMatchCounter;
import org.eclipse.viatra.query.runtime.matchers.psystem.basicdeferred.TypeFilterConstraint;
import org.eclipse.viatra.query.runtime.matchers.psystem.basicenumerables.BinaryTransitiveClosure;
import org.eclipse.viatra.query.runtime.matchers.psystem.basicenumerables.ConstantValue;
import org.eclipse.viatra.query.runtime.matchers.psystem.basicenumerables.PositivePatternCall;
import org.eclipse.viatra.query.runtime.matchers.psystem.basicenumerables.TypeConstraint;
import org.eclipse.viatra.query.runtime.matchers.psystem.queries.PParameter;
import org.eclipse.viatra.query.runtime.matchers.psystem.queries.PQuery;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

/**
 * @author Zoltan Ujhelyi
 * @since 1.7
 *
 */
public abstract class AbstractOperationCompiler implements IOperationCompiler {

    public class FrameMapping{
        public final Map<PParameter, Integer> mapping = Maps.newHashMap();
        public final Set<PParameter> adornment = Sets.newHashSet();
        
        public FrameMapping(PatternCallBasedDeferred constraint, Map<PVariable, Integer> variableMapping) {
            final Set<Integer> bindings = variableBindings.get(constraint);
            int keySize = constraint.getActualParametersTuple().getSize();
            for (int i = 0; i < keySize; i++) {
                PParameter symbolicParameter = constraint.getReferredQuery().getParameters().get(i);
                PVariable parameter = (PVariable) constraint.getActualParametersTuple().get(i);
                mapping.put(symbolicParameter, variableMapping.get(parameter));
                if (bindings.contains(variableMapping.get(parameter))) {
                    adornment.add(symbolicParameter);
                }
            }
        }
        
        public FrameMapping(PositivePatternCall pCall, Map<PVariable, Integer> variableMapping){
            final Set<Integer> bindings = variableBindings.get(pCall);
            int keySize = pCall.getVariablesTuple().getSize();
            for (int i = 0; i < keySize; i++) {
                PParameter symbolicParameter = pCall.getReferredQuery().getParameters().get(i);
                PVariable parameter = (PVariable) pCall.getVariablesTuple().get(i);
                mapping.put(symbolicParameter, variableMapping.get(parameter));
                if (bindings.contains(variableMapping.get(parameter))) {
                    adornment.add(symbolicParameter);
                }
            }
        }
    }
    
    protected static final String UNSUPPORTED_TYPE_MESSAGE = "Unsupported type: ";

    protected abstract void createExtend(TypeConstraint typeConstraint, Map<PVariable, Integer> variableMapping);

    protected abstract void createCheck(TypeConstraint typeConstraint, Map<PVariable, Integer> variableMapping) throws QueryProcessingException;

    protected abstract void createCheck(TypeFilterConstraint typeConstraint, Map<PVariable, Integer> variableMapping) throws QueryProcessingException;

    protected List<ISearchOperation> operations;
    protected Set<MatcherReference> dependencies = Sets.newHashSet();
    protected Map<PConstraint, Set<Integer>> variableBindings;
    private Map<PVariable, Integer> variableMappings;
    protected final EMFQueryRuntimeContext runtimeContext;

    public AbstractOperationCompiler(IQueryRuntimeContext runtimeContext) {
        this.runtimeContext = (EMFQueryRuntimeContext) runtimeContext;
    }

    /**
     * Compiles a plan of <code>POperation</code>s to a list of type <code>List&ltISearchOperation></code>
     * 
     * @param plan
     * @param boundParameters
     * @return an ordered list of POperations that make up the compiled search plan
     * @throws QueryProcessingException 
     */
    @Override
    public List<ISearchOperation> compile(SubPlan plan, Set<PParameter> boundParameters) throws QueryProcessingException {
    
        variableMappings = CompilerHelper.createVariableMapping(plan);
        variableBindings = CompilerHelper.cacheVariableBindings(plan, variableMappings, boundParameters);
    
        operations = Lists.newArrayList();
    
        List<POperation> operationList = CompilerHelper.createOperationsList(plan);
        for (POperation pOperation : operationList) {
            compile(pOperation, variableMappings);
        }
    
        return operations;
    }

    private void compile(POperation pOperation, Map<PVariable, Integer> variableMapping) throws QueryProcessingException {
    
        if (pOperation instanceof PApply) {
            PApply pApply = (PApply) pOperation;
            PConstraint pConstraint = pApply.getPConstraint();
           
            if (isCheck(pConstraint, variableMapping)) {
                // check
                createCheckDispatcher(pConstraint, variableMapping);
            } else {
                // extend
                createExtendDispatcher(pConstraint, variableMapping);
            }
    
        } else if (pOperation instanceof PStart) {
            // nop
        } else if (pOperation instanceof PProject) {
            // nop
        } else {
            throw new QueryProcessingException("PStart, PApply or PProject was expected, received: " + pOperation.getClass(), null,"Unexpected POperation type", null);
        }
    
    }

    private void createCheckDispatcher(PConstraint pConstraint, Map<PVariable, Integer> variableMapping) throws QueryProcessingException {
        
        
        // DeferredPConstraint subclasses
    
        // Equalities are normalized
    
        if (pConstraint instanceof Inequality) {
            createCheck((Inequality) pConstraint, variableMapping);
        } else if (pConstraint instanceof PositivePatternCall){
            createCheck((PositivePatternCall) pConstraint, variableMapping);
        } else if (pConstraint instanceof NegativePatternCall) {
            createCheck((NegativePatternCall) pConstraint,variableMapping);
        } else if (pConstraint instanceof AggregatorConstraint) {
            createCheck((AggregatorConstraint) pConstraint, variableMapping);
        } else if (pConstraint instanceof PatternMatchCounter) {
            createCheck((PatternMatchCounter) pConstraint, variableMapping);
        } else if (pConstraint instanceof ExpressionEvaluation) {
            createCheck((ExpressionEvaluation) pConstraint, variableMapping);
        } else if (pConstraint instanceof TypeFilterConstraint) {
            createCheck((TypeFilterConstraint) pConstraint,variableMapping);
        } else if (pConstraint instanceof ExportedParameter) {
            // Nothing to do here
        } else
        
        // EnumerablePConstraint subclasses
    
        if (pConstraint instanceof BinaryTransitiveClosure) {
            createCheck((BinaryTransitiveClosure) pConstraint, variableMapping);
        } else if (pConstraint instanceof ConstantValue) {
            createCheck((ConstantValue) pConstraint, variableMapping);
        } else if (pConstraint instanceof TypeConstraint) {
            createCheck((TypeConstraint) pConstraint,variableMapping);
        }  else {
            String msg = "Unsupported Check constraint: "+pConstraint.toString();
            throw new QueryProcessingException(msg, null, msg, null);
        }
    
    }
    
    protected void createExtendDispatcher(PConstraint pConstraint, Map<PVariable, Integer> variableMapping) throws QueryProcessingException {
    
        // DeferredPConstraint subclasses
        
        // Equalities are normalized
        if (pConstraint instanceof PositivePatternCall) {
            createExtend((PositivePatternCall)pConstraint, variableMapping);
        } else if (pConstraint instanceof AggregatorConstraint) {
            createExtend((AggregatorConstraint) pConstraint, variableMapping);
        } else if (pConstraint instanceof PatternMatchCounter) {
            createExtend((PatternMatchCounter) pConstraint, variableMapping);
        } else if (pConstraint instanceof ExpressionEvaluation) {
            createExtend((ExpressionEvaluation) pConstraint, variableMapping);
        } else if (pConstraint instanceof ExportedParameter) {
            // ExportedParameters are compiled to NOP
        } else
        
        // EnumerablePConstraint subclasses
    
        if (pConstraint instanceof ConstantValue) {
            createExtend((ConstantValue) pConstraint, variableMapping);
        } else if (pConstraint instanceof TypeConstraint) {
            createExtend((TypeConstraint) pConstraint, variableMapping);
        } else if (pConstraint instanceof BinaryTransitiveClosure) {
            createExtend((BinaryTransitiveClosure)pConstraint, variableMapping);
        } else {
            String msg = "Unsupported Extend constraint: "+pConstraint.toString();
            throw new QueryProcessingException(msg, null, msg, null);
        }
    }
    
    private boolean isCheck(PConstraint pConstraint, final Map<PVariable, Integer> variableMapping) {
        if (pConstraint instanceof NegativePatternCall){
            return true;
        }else if (pConstraint instanceof PositivePatternCall){
            // Positive pattern call is check if all non-single used variables are bound
            return variableBindings.get(pConstraint).containsAll(Collections2.transform(Sets.filter(pConstraint.getAffectedVariables(), new Predicate<PVariable>() {
    
                @Override
                public boolean apply(PVariable input) {
                    return input.getReferringConstraints().size() > 1;
                }
            }), new Function<PVariable, Integer>() {
    
                @Override
                public Integer apply(PVariable input) {
                    return variableMapping.get(input);
                }
                
            }));
        }else if (pConstraint instanceof AggregatorConstraint){
            PVariable outputvar = ((AggregatorConstraint) pConstraint).getResultVariable();
            return variableBindings.get(pConstraint).contains(variableMapping.get(outputvar));
        }else if (pConstraint instanceof PatternMatchCounter){
            PVariable outputvar = ((PatternMatchCounter) pConstraint).getResultVariable();
            return variableBindings.get(pConstraint).contains(variableMapping.get(outputvar));
        }else if (pConstraint instanceof ExpressionEvaluation){
            PVariable outputvar = ((ExpressionEvaluation) pConstraint).getOutputVariable();
            return outputvar == null || variableBindings.get(pConstraint).contains(variableMapping.get(outputvar));
        } else {
            // In other cases, all variables shall be bound to be a check
            Set<PVariable> affectedVariables = pConstraint.getAffectedVariables();
            Set<Integer> varIndices = Sets.newHashSet();
            for (PVariable variable : affectedVariables) {
                varIndices.add(variableMapping.get(variable));
            }
            return variableBindings.get(pConstraint).containsAll(varIndices);
        }
    }

    @Override
    public Set<MatcherReference> getDependencies() {
        return dependencies;
    }

    /**
     * @return the cached variable bindings for the previously created plan
     */
    @Override
    public Map<PVariable, Integer> getVariableMappings() {
        return variableMappings;
    }

    protected void createCheck(PatternMatchCounter counter, Map<PVariable, Integer> variableMapping) {
        FrameMapping mapping = new FrameMapping(counter, variableMapping);
    
        PQuery referredQuery = counter.getReferredQuery();
        MatcherReference matcherReference = new MatcherReference(referredQuery, mapping.adornment);
        operations.add(new CountCheck(matcherReference, mapping.mapping, variableMapping.get(counter.getResultVariable())));
        dependencies.add(matcherReference);
    }

    protected void createCheck(PositivePatternCall pCall, Map<PVariable, Integer> variableMapping) {
        FrameMapping mapping = new FrameMapping(pCall, variableMapping);
        MatcherReference matcherReference = new MatcherReference(pCall.getReferredQuery(), mapping.adornment);
        operations.add(new CheckPositivePatternCall(matcherReference, mapping.mapping));
        dependencies.add(matcherReference);
    }

    protected void createCheck(ConstantValue constant, Map<PVariable, Integer> variableMapping) {
        int position = variableMapping.get(constant.getVariablesTuple().get(0));
        operations.add(new CheckConstant(position, constant.getSupplierKey()));
    }

    protected void createCheck(BinaryTransitiveClosure binaryTransitiveColsure, Map<PVariable, Integer> variableMapping) {
        int sourcePosition = variableMapping.get(binaryTransitiveColsure.getVariablesTuple().get(0));
        int targetPosition = variableMapping.get(binaryTransitiveColsure.getVariablesTuple().get(1));
        
        PQuery referredQuery = binaryTransitiveColsure.getReferredQuery();
        
        operations.add(new BinaryTransitiveClosureCheck(new MatcherReference(referredQuery, ImmutableSet.of(referredQuery.getParameters().get(0), referredQuery.getParameters().get(1))), sourcePosition, targetPosition));
        //The second parameter is NOT bound during execution!
        Set<PParameter> adornment = ImmutableSet.of(referredQuery.getParameters().get(0));
        dependencies.add(new MatcherReference(referredQuery, adornment));
    }

    protected void createCheck(ExpressionEvaluation expressionEvaluation, Map<PVariable, Integer> variableMapping) {
        // Fill unbound variables with null; simply copy all variables. Unbound variables will be null anyway
        Iterable<String> inputParameterNames = expressionEvaluation.getEvaluator().getInputParameterNames();
        Map<String, Integer> nameMap = Maps.newHashMap();
        
        for (String pVariableName : inputParameterNames) {
            PVariable pVariable = expressionEvaluation.getPSystem().getVariableByNameChecked(pVariableName);
            nameMap.put(pVariableName, variableMapping.get(pVariable));
        }
        
        // output variable can be null; if null it is an ExpressionCheck
        if(expressionEvaluation.getOutputVariable() == null){
            operations.add(new ExpressionCheck(expressionEvaluation.getEvaluator(), nameMap));
        } else {
            operations.add(new ExpressionEvalCheck(expressionEvaluation.getEvaluator(), nameMap, variableMapping.get(expressionEvaluation.getOutputVariable())));
        }
    }

    protected void createCheck(AggregatorConstraint aggregator, Map<PVariable, Integer> variableMapping) {
        FrameMapping mapping = new FrameMapping(aggregator, variableMapping);
        
        PQuery referredQuery = aggregator.getReferredQuery();
        MatcherReference matcherReference = new MatcherReference(referredQuery, mapping.adornment);
        operations.add(new AggregatorCheck(matcherReference, aggregator, mapping.mapping, variableMapping.get(aggregator.getResultVariable())));
        dependencies.add(matcherReference);
    }

    protected void createCheck(NegativePatternCall negativePatternCall, Map<PVariable, Integer> variableMapping) {
        FrameMapping mapping = new FrameMapping(negativePatternCall, variableMapping);
        PQuery referredQuery = negativePatternCall.getReferredQuery();
        MatcherReference matcherReference = new MatcherReference(referredQuery, mapping.adornment);
        operations.add(new NACOperation(matcherReference, mapping.mapping));
        dependencies.add(matcherReference);
    }

    protected void createCheck(Inequality inequality, Map<PVariable, Integer> variableMapping) {
        operations.add(new InequalityCheck(variableMapping.get(inequality.getWho()), variableMapping.get(inequality.getWithWhom())));
    }

    protected void createExtend(PositivePatternCall pCall, Map<PVariable, Integer> variableMapping) {
        FrameMapping mapping = new FrameMapping(pCall, variableMapping);
        MatcherReference matcherReference = new MatcherReference(pCall.getReferredQuery(), mapping.adornment);
        operations.add(new ExtendPositivePatternCall(matcherReference, mapping.mapping));
        dependencies.add(matcherReference);
    }

    protected void createExtend(BinaryTransitiveClosure binaryTransitiveClosure, Map<PVariable, Integer> variableMapping) throws QueryProcessingException {
        int sourcePosition = variableMapping.get(binaryTransitiveClosure.getVariablesTuple().get(0));
        int targetPosition = variableMapping.get(binaryTransitiveClosure.getVariablesTuple().get(1));
        
        PQuery referredQuery = binaryTransitiveClosure.getReferredQuery();
        
        boolean sourceBound = variableBindings.get(binaryTransitiveClosure).contains(sourcePosition);
        boolean targetBound = variableBindings.get(binaryTransitiveClosure).contains(targetPosition);
        
        if (sourceBound && !targetBound) {
            Set<PParameter> adornment = ImmutableSet.of(referredQuery.getParameters().get(0));
            operations.add(new ExtendBinaryTransitiveClosure.Forward(new MatcherReference(referredQuery, adornment), sourcePosition, targetPosition));
            dependencies.add(new MatcherReference(referredQuery, adornment));            
        } else if (!sourceBound && targetBound) {
            Set<PParameter> adornment = ImmutableSet.of(referredQuery.getParameters().get(1));
            operations.add(new ExtendBinaryTransitiveClosure.Backward(new MatcherReference(referredQuery, adornment), sourcePosition, targetPosition));
            dependencies.add(new MatcherReference(referredQuery, adornment));                        
        } else {
            String msg = "Binary transitive closure not supported with two unbound parameters";
            throw new QueryProcessingException(msg, null, msg, binaryTransitiveClosure.getPSystem().getPattern());
        }
    }

    protected void createExtend(ConstantValue constant, Map<PVariable, Integer> variableMapping) {
        int position = variableMapping.get(constant.getVariablesTuple().get(0));
        operations.add(new ExtendConstant(position, constant.getSupplierKey()));        
    }

    protected void createExtend(ExpressionEvaluation expressionEvaluation, Map<PVariable, Integer> variableMapping) {
        // Fill unbound variables with null; simply copy all variables. Unbound variables will be null anyway
        Iterable<String> inputParameterNames = expressionEvaluation.getEvaluator().getInputParameterNames();
        Map<String, Integer> nameMap = Maps.newHashMap();
        
        for (String pVariableName : inputParameterNames) {
            PVariable pVariable = expressionEvaluation.getPSystem().getVariableByNameChecked(pVariableName);
            nameMap.put(pVariableName, variableMapping.get(pVariable));
        }
        
        // output variable can be null; if null it is an ExpressionCheck
        if(expressionEvaluation.getOutputVariable() == null){
            operations.add(new ExpressionCheck(expressionEvaluation.getEvaluator(), nameMap));
        } else {
            operations.add(new ExpressionEval(expressionEvaluation.getEvaluator(), nameMap, variableMapping.get(expressionEvaluation.getOutputVariable())));
        }
    }

    protected void createExtend(AggregatorConstraint aggregator, Map<PVariable, Integer> variableMapping) {
        FrameMapping mapping = new FrameMapping(aggregator, variableMapping);
        
        PQuery referredQuery = aggregator.getReferredQuery();
        MatcherReference matcherReference = new MatcherReference(referredQuery, mapping.adornment);
        operations.add(new AggregatorExtend(matcherReference, aggregator, mapping.mapping, variableMapping.get(aggregator.getResultVariable())));
        dependencies.add(matcherReference);
    }

    protected void createExtend(PatternMatchCounter patternMatchCounter, Map<PVariable, Integer> variableMapping) {
        FrameMapping mapping = new FrameMapping(patternMatchCounter, variableMapping);
        
        PQuery referredQuery = patternMatchCounter.getReferredQuery();
        MatcherReference matcherReference = new MatcherReference(referredQuery, mapping.adornment);
        operations.add(new CountOperation(matcherReference, mapping.mapping, variableMapping.get(patternMatchCounter.getResultVariable())));
        dependencies.add(matcherReference);
    }

}