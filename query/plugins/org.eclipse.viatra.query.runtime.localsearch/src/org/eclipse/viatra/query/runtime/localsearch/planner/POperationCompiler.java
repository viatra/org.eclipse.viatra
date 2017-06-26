/*******************************************************************************
 * Copyright (c) 2010-2014, Marton Bur, Akos Horvath, Zoltan Ujhelyi, Istvan Rath and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Marton Bur - initial API and implementation
 *******************************************************************************/
package org.eclipse.viatra.query.runtime.localsearch.planner;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.viatra.query.runtime.emf.EMFQueryRuntimeContext;
import org.eclipse.viatra.query.runtime.emf.types.EClassTransitiveInstancesKey;
import org.eclipse.viatra.query.runtime.emf.types.EClassUnscopedTransitiveInstancesKey;
import org.eclipse.viatra.query.runtime.emf.types.EDataTypeInSlotsKey;
import org.eclipse.viatra.query.runtime.emf.types.EStructuralFeatureInstancesKey;
import org.eclipse.viatra.query.runtime.localsearch.matcher.MatcherReference;
import org.eclipse.viatra.query.runtime.localsearch.matcher.integration.LocalSearchBackend;
import org.eclipse.viatra.query.runtime.localsearch.operations.ISearchOperation;
import org.eclipse.viatra.query.runtime.localsearch.operations.check.AggregatorCheck;
import org.eclipse.viatra.query.runtime.localsearch.operations.check.BinaryTransitiveClosureCheck;
import org.eclipse.viatra.query.runtime.localsearch.operations.check.CheckConstant;
import org.eclipse.viatra.query.runtime.localsearch.operations.check.CheckPositivePatternCall;
import org.eclipse.viatra.query.runtime.localsearch.operations.check.CountCheck;
import org.eclipse.viatra.query.runtime.localsearch.operations.check.ExpressionCheck;
import org.eclipse.viatra.query.runtime.localsearch.operations.check.ExpressionEvalCheck;
import org.eclipse.viatra.query.runtime.localsearch.operations.check.InequalityCheck;
import org.eclipse.viatra.query.runtime.localsearch.operations.check.InstanceOfClassCheck;
import org.eclipse.viatra.query.runtime.localsearch.operations.check.InstanceOfDataTypeCheck;
import org.eclipse.viatra.query.runtime.localsearch.operations.check.InstanceOfJavaClassCheck;
import org.eclipse.viatra.query.runtime.localsearch.operations.check.NACOperation;
import org.eclipse.viatra.query.runtime.localsearch.operations.check.StructuralFeatureCheck;
import org.eclipse.viatra.query.runtime.localsearch.operations.check.nobase.ScopeCheck;
import org.eclipse.viatra.query.runtime.localsearch.operations.extend.AggregatorExtend;
import org.eclipse.viatra.query.runtime.localsearch.operations.extend.CountOperation;
import org.eclipse.viatra.query.runtime.localsearch.operations.extend.ExpressionEval;
import org.eclipse.viatra.query.runtime.localsearch.operations.extend.ExtendBinaryTransitiveClosure;
import org.eclipse.viatra.query.runtime.localsearch.operations.extend.ExtendConstant;
import org.eclipse.viatra.query.runtime.localsearch.operations.extend.ExtendPositivePatternCall;
import org.eclipse.viatra.query.runtime.localsearch.operations.extend.ExtendToEStructuralFeatureSource;
import org.eclipse.viatra.query.runtime.localsearch.operations.extend.ExtendToEStructuralFeatureTarget;
import org.eclipse.viatra.query.runtime.localsearch.operations.extend.IterateOverContainers;
import org.eclipse.viatra.query.runtime.localsearch.operations.extend.IterateOverEClassInstances;
import org.eclipse.viatra.query.runtime.localsearch.operations.extend.IterateOverEDatatypeInstances;
import org.eclipse.viatra.query.runtime.localsearch.planner.util.CompilerHelper;
import org.eclipse.viatra.query.runtime.matchers.backend.IQueryBackend;
import org.eclipse.viatra.query.runtime.matchers.context.IInputKey;
import org.eclipse.viatra.query.runtime.matchers.context.IQueryRuntimeContext;
import org.eclipse.viatra.query.runtime.matchers.context.common.JavaTransitiveInstancesKey;
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
 * An EMF specific plan compiler for the local search-based pattern matcher
 * 
 * @author Marton Bur
 * @noreference This class is not intended to be referenced by clients.
 */
public class POperationCompiler {

    /**
     * 
     */
    private static final String UNSUPPORTED_TYPE_MESSAGE = "Unsupported type: ";
    private List<ISearchOperation> operations;
    private Set<MatcherReference> dependencies = Sets.newHashSet();
    private Map<PConstraint, Set<Integer>> variableBindings;
    private Map<PVariable, Integer> variableMappings;
    private final boolean baseIndexAvailable;
    private final EMFQueryRuntimeContext runtimeContext;
    private final IQueryBackend backend;

    private class FrameMapping{
        final Map<PParameter, Integer> mapping = Maps.newHashMap();
        final Set<PParameter> adornment = Sets.newHashSet();
        
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
    
    public POperationCompiler(IQueryRuntimeContext runtimeContext, IQueryBackend backend) {
        this(runtimeContext, backend, false);
    }

    public POperationCompiler(IQueryRuntimeContext runtimeContext, IQueryBackend backend, boolean baseIndexAvailable) {
        this.backend = backend;
        this.runtimeContext = (EMFQueryRuntimeContext) runtimeContext;
        this.baseIndexAvailable = baseIndexAvailable;
    }

    /**
     * Compiles a plan of <code>POperation</code>s to a list of type <code>List&ltISearchOperation></code>
     * 
     * @param plan
     * @param boundParameters
     * @return an ordered list of POperations that make up the compiled search plan
     * @throws QueryProcessingException 
     */
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
    
    private boolean isCheck(PConstraint pConstraint, final Map<PVariable, Integer> variableMapping){
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

    /**
     * @param pConstraint
     * @param variableMapping
     */
    private void createCheck(PatternMatchCounter counter, Map<PVariable, Integer> variableMapping) {
        FrameMapping mapping = new FrameMapping(counter, variableMapping);

        PQuery referredQuery = counter.getReferredQuery();
        MatcherReference matcherReference = new MatcherReference(referredQuery, mapping.adornment);
        operations.add(new CountCheck(matcherReference, mapping.mapping, variableMapping.get(counter.getResultVariable())));
        dependencies.add(matcherReference);
    }

    private void createCheck(PositivePatternCall pCall, Map<PVariable, Integer> variableMapping) {
        FrameMapping mapping = new FrameMapping(pCall, variableMapping);
        MatcherReference matcherReference = new MatcherReference(pCall.getReferredQuery(), mapping.adornment);
        operations.add(new CheckPositivePatternCall(matcherReference, mapping.mapping));
        dependencies.add(matcherReference);
    }

    private void createCheck(ConstantValue constant, Map<PVariable, Integer> variableMapping) {
        int position = variableMapping.get(constant.getVariablesTuple().get(0));
        operations.add(new CheckConstant(position, constant.getSupplierKey()));
    }

    private void createCheck(TypeFilterConstraint typeConstraint, Map<PVariable, Integer> variableMapping) throws QueryProcessingException {
        final IInputKey inputKey = typeConstraint.getInputKey();
        if (inputKey instanceof JavaTransitiveInstancesKey) {
            operations.add(new InstanceOfJavaClassCheck(variableMapping.get(typeConstraint.getVariablesTuple().get(0)), ((JavaTransitiveInstancesKey) inputKey).getInstanceClass()));
        } else if (inputKey instanceof EDataTypeInSlotsKey) { // TODO probably only occurs as TypeConstraint
            operations.add(new InstanceOfDataTypeCheck(variableMapping.get(typeConstraint.getVariablesTuple().get(0)),
                    ((EDataTypeInSlotsKey) inputKey).getEmfKey()));
        } else if (inputKey instanceof EClassUnscopedTransitiveInstancesKey) {
            operations.add(new InstanceOfClassCheck(variableMapping.get(typeConstraint.getVariablesTuple().get(0)), ((EClassUnscopedTransitiveInstancesKey) inputKey).getEmfKey()));
        } else {
            String msg = UNSUPPORTED_TYPE_MESSAGE + inputKey;
            throw new QueryProcessingException(msg, null, msg, null);
        }
    }
    
    private void createCheck(TypeConstraint typeConstraint, Map<PVariable, Integer> variableMapping) throws QueryProcessingException {
        final IInputKey inputKey = typeConstraint.getSupplierKey();
        if (inputKey instanceof EClassTransitiveInstancesKey) {
            operations.add(new InstanceOfClassCheck(variableMapping.get(typeConstraint.getVariablesTuple().get(0)), ((EClassTransitiveInstancesKey) inputKey).getEmfKey()));
            operations.add(new ScopeCheck(variableMapping.get(typeConstraint.getVariablesTuple().get(0)), runtimeContext.getEmfScope()));
        } else if (inputKey instanceof EStructuralFeatureInstancesKey) {
            int sourcePosition = variableMapping.get(typeConstraint.getVariablesTuple().get(0));
            int targetPosition = variableMapping.get(typeConstraint.getVariablesTuple().get(1));
            operations.add(new StructuralFeatureCheck(sourcePosition, targetPosition,
                    ((EStructuralFeatureInstancesKey) inputKey).getEmfKey()));
        } else if (inputKey instanceof EDataTypeInSlotsKey) {
            operations.add(new InstanceOfDataTypeCheck(variableMapping.get(typeConstraint.getVariablesTuple().get(0)),
                    ((EDataTypeInSlotsKey) inputKey).getEmfKey()));
        } else {
            String msg = UNSUPPORTED_TYPE_MESSAGE + inputKey;
            throw new QueryProcessingException(msg, null, msg, null);
        }
    }

    private void createCheck(BinaryTransitiveClosure binaryTransitiveColsure, Map<PVariable, Integer> variableMapping) {
        int sourcePosition = variableMapping.get(binaryTransitiveColsure.getVariablesTuple().get(0));
        int targetPosition = variableMapping.get(binaryTransitiveColsure.getVariablesTuple().get(1));
        
        PQuery referredQuery = binaryTransitiveColsure.getReferredQuery();
        
        operations.add(new BinaryTransitiveClosureCheck(new MatcherReference(referredQuery, ImmutableSet.of(referredQuery.getParameters().get(0), referredQuery.getParameters().get(1))), sourcePosition, targetPosition));
        //The second parameter is NOT bound during execution!
        Set<PParameter> adornment = ImmutableSet.of(referredQuery.getParameters().get(0));
        dependencies.add(new MatcherReference(referredQuery, adornment));
    }


    private void createCheck(ExpressionEvaluation expressionEvaluation, Map<PVariable, Integer> variableMapping) {
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
    
    private void createCheck(AggregatorConstraint aggregator, Map<PVariable, Integer> variableMapping) {
        FrameMapping mapping = new FrameMapping(aggregator, variableMapping);
        
        PQuery referredQuery = aggregator.getReferredQuery();
        MatcherReference matcherReference = new MatcherReference(referredQuery, mapping.adornment);
        operations.add(new AggregatorCheck(matcherReference, aggregator, mapping.mapping, variableMapping.get(aggregator.getResultVariable())));
        dependencies.add(matcherReference);
    }
    
    private void createCheck(NegativePatternCall negativePatternCall, Map<PVariable, Integer> variableMapping) {
        FrameMapping mapping = new FrameMapping(negativePatternCall, variableMapping);
        PQuery referredQuery = negativePatternCall.getReferredQuery();
        MatcherReference matcherReference = new MatcherReference(referredQuery, mapping.adornment);
        operations.add(new NACOperation(matcherReference, mapping.mapping));
        dependencies.add(matcherReference);
    }
    
    private void createCheck(Inequality inequality, Map<PVariable, Integer> variableMapping) {
        operations.add(new InequalityCheck(variableMapping.get(inequality.getWho()), variableMapping.get(inequality.getWithWhom())));
    }

    
    private void createExtendDispatcher(PConstraint pConstraint, Map<PVariable, Integer> variableMapping) throws QueryProcessingException {

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

    private void createExtend(PositivePatternCall pCall, Map<PVariable, Integer> variableMapping) {
        FrameMapping mapping = new FrameMapping(pCall, variableMapping);
        MatcherReference matcherReference = new MatcherReference(pCall.getReferredQuery(), mapping.adornment);
        operations.add(new ExtendPositivePatternCall(matcherReference, mapping.mapping));
        dependencies.add(matcherReference);
    }

    private void createExtend(BinaryTransitiveClosure binaryTransitiveClosure, Map<PVariable, Integer> variableMapping) throws QueryProcessingException {
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
    
    private void createExtend(ConstantValue constant, Map<PVariable, Integer> variableMapping) {
        int position = variableMapping.get(constant.getVariablesTuple().get(0));
        operations.add(new ExtendConstant(position, constant.getSupplierKey()));        
    }

    private void createExtend(TypeConstraint typeConstraint, Map<PVariable, Integer> variableMapping) {
        final IInputKey inputKey = typeConstraint.getSupplierKey();
        if (inputKey instanceof EDataTypeInSlotsKey) {
            if(baseIndexAvailable){
                operations.add(new IterateOverEDatatypeInstances(variableMapping.get(typeConstraint.getVariableInTuple(0)), ((EDataTypeInSlotsKey) inputKey).getEmfKey()));		        
            } else {
                int position = variableMapping.get(typeConstraint.getVariableInTuple(0));
                operations
                        .add(new org.eclipse.viatra.query.runtime.localsearch.operations.extend.nobase.IterateOverEDatatypeInstances(position,
                                ((EDataTypeInSlotsKey) inputKey).getEmfKey(), runtimeContext.getEmfScope(), (LocalSearchBackend) backend));
                operations.add(new ScopeCheck(position, runtimeContext.getEmfScope()));
            }
        } else if (inputKey instanceof EClassTransitiveInstancesKey) {
            if(baseIndexAvailable){
                operations.add(new IterateOverEClassInstances(variableMapping.get(typeConstraint.getVariableInTuple(0)),
                        ((EClassTransitiveInstancesKey) inputKey).getEmfKey()));
            } else {
                int position = variableMapping.get(typeConstraint.getVariableInTuple(0));
                operations
                        .add(new org.eclipse.viatra.query.runtime.localsearch.operations.extend.nobase.IterateOverEClassInstances(
                                position,
                                ((EClassTransitiveInstancesKey) inputKey).getEmfKey(), runtimeContext.getEmfScope()));
                operations.add(new ScopeCheck(position, runtimeContext.getEmfScope()));
            }
        } else if (inputKey instanceof EStructuralFeatureInstancesKey) {
            final EStructuralFeature feature = ((EStructuralFeatureInstancesKey) inputKey).getEmfKey();
            
            int sourcePosition = variableMapping.get(typeConstraint.getVariablesTuple().get(0));
            int targetPosition = variableMapping.get(typeConstraint.getVariablesTuple().get(1));

            boolean fromBound = variableBindings.get(typeConstraint).contains(sourcePosition);
            boolean toBound = variableBindings.get(typeConstraint).contains(targetPosition);

            if (fromBound && !toBound) {
                if (baseIndexAvailable) {
                    operations.add(new ExtendToEStructuralFeatureTarget(sourcePosition, targetPosition, feature));
                } else {
                    operations
                            .add(new org.eclipse.viatra.query.runtime.localsearch.operations.extend.nobase.ExtendToEStructuralFeatureTarget(
                                    sourcePosition, targetPosition, feature));
                    operations.add(new ScopeCheck(targetPosition, runtimeContext.getEmfScope()));
                }
            }
            else if(!fromBound && toBound){
                if (feature instanceof EReference && ((EReference)feature).isContainment()) {
                    // The iterate is also used to traverse a single container (third parameter)
                    operations.add(new IterateOverContainers(sourcePosition, targetPosition, false));
                    operations.add(new ScopeCheck(sourcePosition, runtimeContext.getEmfScope()));
                } else if(baseIndexAvailable){
                    operations.add(new ExtendToEStructuralFeatureSource(sourcePosition, targetPosition, feature));	                
                } else {
                    operations.add(new org.eclipse.viatra.query.runtime.localsearch.operations.extend.nobase.ExtendToEStructuralFeatureSource(
                                    sourcePosition, targetPosition, feature));
                    operations.add(new ScopeCheck(sourcePosition, runtimeContext.getEmfScope()));
                }
            } else {
                // TODO Elaborate solution based on the navigability of edges
                // As of now a static solution is implemented
                if (baseIndexAvailable) {
                    operations.add(new IterateOverEClassInstances(sourcePosition, feature.getEContainingClass()));
                    operations.add(new ExtendToEStructuralFeatureTarget(sourcePosition, targetPosition, feature));
                } else {
                    operations
                            .add(new org.eclipse.viatra.query.runtime.localsearch.operations.extend.nobase.IterateOverEClassInstances(
                                    sourcePosition, feature.getEContainingClass(), runtimeContext.getEmfScope()));
                    operations.add(new ScopeCheck(sourcePosition, runtimeContext.getEmfScope()));
                    operations
                            .add(new org.eclipse.viatra.query.runtime.localsearch.operations.extend.nobase.ExtendToEStructuralFeatureTarget(
                                    sourcePosition, targetPosition, feature));
                    operations.add(new ScopeCheck(targetPosition, runtimeContext.getEmfScope()));
                }
            }

        } else {
            throw new IllegalArgumentException(UNSUPPORTED_TYPE_MESSAGE + inputKey);
        }        
    }

    private void createExtend(ExpressionEvaluation expressionEvaluation, Map<PVariable, Integer> variableMapping) {
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
    
    private void createExtend(AggregatorConstraint aggregator, Map<PVariable, Integer> variableMapping) {
        FrameMapping mapping = new FrameMapping(aggregator, variableMapping);
        
        PQuery referredQuery = aggregator.getReferredQuery();
        MatcherReference matcherReference = new MatcherReference(referredQuery, mapping.adornment);
        operations.add(new AggregatorExtend(matcherReference, aggregator, mapping.mapping, variableMapping.get(aggregator.getResultVariable())));
        dependencies.add(matcherReference);
    }
    
    private void createExtend(PatternMatchCounter patternMatchCounter, Map<PVariable, Integer> variableMapping) {
        FrameMapping mapping = new FrameMapping(patternMatchCounter, variableMapping);
        
        PQuery referredQuery = patternMatchCounter.getReferredQuery();
        MatcherReference matcherReference = new MatcherReference(referredQuery, mapping.adornment);
        operations.add(new CountOperation(matcherReference, mapping.mapping, variableMapping.get(patternMatchCounter.getResultVariable())));
        dependencies.add(matcherReference);
    }
    
    public Set<MatcherReference> getDependencies() {
        return dependencies;
    }
    /**
     * @return the cached variable bindings for the previously created plan
     */
    public Map<PVariable, Integer> getVariableMappings() {
        return variableMappings;
    }
}
