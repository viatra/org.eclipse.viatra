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
package org.eclipse.viatra.query.runtime.matchers.psystem.rewriters;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.viatra.query.runtime.matchers.psystem.EnumerablePConstraint;
import org.eclipse.viatra.query.runtime.matchers.psystem.PBody;
import org.eclipse.viatra.query.runtime.matchers.psystem.PConstraint;
import org.eclipse.viatra.query.runtime.matchers.psystem.PVariable;
import org.eclipse.viatra.query.runtime.matchers.psystem.basicdeferred.AggregatorConstraint;
import org.eclipse.viatra.query.runtime.matchers.psystem.basicdeferred.Equality;
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
import org.eclipse.viatra.query.runtime.matchers.psystem.rewriters.IConstraintFilter.AllowAllFilter;
import org.eclipse.viatra.query.runtime.matchers.psystem.rewriters.IVariableRenamer.SameName;
import org.eclipse.viatra.query.runtime.matchers.tuple.FlatTuple;

import com.google.common.base.Function;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

/**
 * This class can create a new PBody for a PQuery. The result body contains a copy of given variables and constraints. 
 * 
 * @author Marton Bur
 * 
 */
public class PBodyCopier extends AbstractRewriterTraceSource{

    /**
     * The created body
     */
    protected PBody body;
    /**
     * Mapping between the original and the copied variables
     */
    protected Map<PVariable, PVariable> variableMapping = Maps.newHashMap();
    
    public Map<PVariable, PVariable> getVariableMapping() {
        return variableMapping;
    }
    
    /**
     * This constructor is deprecated as it performs a copy without allowing callers
     * to set the trace collector before.
     * 
     * @deprecated use {@link #PBodyCopier(PBody, IRewriterTraceCollector)} instead
     */
    @Deprecated
    public PBodyCopier(PBody body){
        this.body = new PBody(body.getPattern());
        
        // do the actual copying
        mergeBody(body);
    }
    
    /**
     * @since 1.6
     */
    public PBodyCopier(PBody body, IRewriterTraceCollector traceCollector) {
        this.body = new PBody(body.getPattern());
        setTraceCollector(traceCollector);

        // do the actual copying
        mergeBody(body);
    }
    
    /**
     * @since 1.6
     */
    public PBodyCopier(PQuery query) {
        this.body = new PBody(query);
    }

    public void mergeBody(PBody sourceBody) {
        mergeBody(sourceBody, new SameName(), new AllowAllFilter());
    }
    
    /**
     * Merge all variables and constraints from a source body to a target body. If multiple bodies are merged into a single one, use the renamer and filter options to avoid collisions.
     * @param sourceBody
     * @param namingTool
     * @param filter
     */
    public void mergeBody(PBody sourceBody, IVariableRenamer namingTool, IConstraintFilter filter) {

        // Copy variables
        Set<PVariable> allVariables = sourceBody.getAllVariables();
        for (PVariable pVariable : allVariables) {
            if (pVariable.isUnique()) {
                copyVariable(pVariable, namingTool.createVariableName(pVariable, sourceBody.getPattern()));
            }
        }

        // Copy exported parameters
        this.body.setSymbolicParameters(Lists.transform(sourceBody.getSymbolicParameters(),
                new Function<ExportedParameter, ExportedParameter>() {

                    @Override
                    public ExportedParameter apply(ExportedParameter input) {
                        return copyExportedParameterConstraint(input);
                    }
                }));

        // Copy constraints which are not filtered
        Set<PConstraint> constraints = sourceBody.getConstraints();
        for (PConstraint pConstraint : constraints) {
            if (!(pConstraint instanceof ExportedParameter) && !filter.filter(pConstraint)) {
                copyConstraint(pConstraint);
            }
        }
        
        // Add trace between original and copied body
        addTrace(sourceBody, body);
    }
    
    protected void copyVariable(PVariable variable, String newName) {
        PVariable newPVariable = body.getOrCreateVariableByName(newName);
        variableMapping.put(variable, newPVariable);
    }

    /**
     * Returns the body with the copied variables and constraints. The returned body is still uninitialized. 
     */
    public PBody getCopiedBody() {
        return body;
    }

    protected void copyConstraint(PConstraint constraint) {
        if (constraint instanceof ExportedParameter) {
            copyExportedParameterConstraint((ExportedParameter) constraint);
        } else if (constraint instanceof Equality) {
            copyEqualityConstraint((Equality) constraint);
        } else if (constraint instanceof Inequality) {
            copyInequalityConstraint((Inequality) constraint);
        } else if (constraint instanceof TypeConstraint) {
            copyTypeConstraint((TypeConstraint) constraint);
        } else if (constraint instanceof TypeFilterConstraint) {
            copyTypeFilterConstraint((TypeFilterConstraint) constraint);
        } else if (constraint instanceof ConstantValue) {
            copyConstantValueConstraint((ConstantValue) constraint);
        } else if (constraint instanceof PositivePatternCall) {
            copyPositivePatternCallConstraint((PositivePatternCall) constraint);
        } else if (constraint instanceof NegativePatternCall) {
            copyNegativePatternCallConstraint((NegativePatternCall) constraint);
        } else if (constraint instanceof BinaryTransitiveClosure) {
            copyBinaryTransitiveClosureConstraint((BinaryTransitiveClosure) constraint);
        } else if (constraint instanceof PatternMatchCounter) {
            copyPatternMatchCounterConstraint((PatternMatchCounter) constraint);
        } else if (constraint instanceof AggregatorConstraint) {
            copyAggregatorConstraint((AggregatorConstraint) constraint);
        } else if (constraint instanceof ExpressionEvaluation) {
            copyExpressionEvaluationConstraint((ExpressionEvaluation) constraint);
        } else {
            throw new RuntimeException("Unknown PConstraint encountered while copying PBody: " + constraint.getClass().getName());
        }
    }


    protected ExportedParameter copyExportedParameterConstraint(ExportedParameter exportedParameter) {
        PVariable mappedPVariable = variableMapping.get(exportedParameter.getParameterVariable());
        PParameter parameter = exportedParameter.getPatternParameter();
        ExportedParameter newExportedParameter;
        if (parameter == null) {
            newExportedParameter = new ExportedParameter(body, mappedPVariable, exportedParameter.getParameterName());
        } else {
            newExportedParameter = new ExportedParameter(body, mappedPVariable, parameter);
        }
        body.getSymbolicParameters().add(newExportedParameter);
        addTrace(exportedParameter, newExportedParameter);
        return newExportedParameter;
    }

    protected void copyEqualityConstraint(Equality equality) {
        PVariable who = equality.getWho();
        PVariable withWhom = equality.getWithWhom();
        addTrace(equality, new Equality(body, variableMapping.get(who), variableMapping.get(withWhom)));
    }

    protected void copyInequalityConstraint(Inequality inequality) {
        PVariable who = inequality.getWho();
        PVariable withWhom = inequality.getWithWhom();
        addTrace(inequality, new Inequality(body, variableMapping.get(who), variableMapping.get(withWhom)));
    }
    
    protected void copyTypeConstraint(TypeConstraint typeConstraint) {
        PVariable[] mappedVariables = extractMappedVariables(typeConstraint);
        FlatTuple variablesTuple = new FlatTuple((Object[])mappedVariables); 	
        addTrace(typeConstraint, new TypeConstraint(body, variablesTuple, typeConstraint.getSupplierKey()));
    }
    
    protected void copyTypeFilterConstraint(TypeFilterConstraint typeConstraint) {
        PVariable[] mappedVariables = extractMappedVariables(typeConstraint);
        FlatTuple variablesTuple = new FlatTuple((Object[])mappedVariables); 	
        addTrace(typeConstraint, new TypeFilterConstraint(body, variablesTuple, typeConstraint.getInputKey()));
    }

    protected void copyConstantValueConstraint(ConstantValue constantValue) {
        PVariable pVariable = (PVariable) constantValue.getVariablesTuple().getElements()[0];
        addTrace(constantValue, new ConstantValue(body, variableMapping.get(pVariable), constantValue.getSupplierKey()));
    }

    protected void copyPositivePatternCallConstraint(PositivePatternCall positivePatternCall) {
        PVariable[] mappedVariables = extractMappedVariables(positivePatternCall);
        FlatTuple variablesTuple = new FlatTuple((Object[])mappedVariables);
        addTrace(positivePatternCall, new PositivePatternCall(body, variablesTuple, positivePatternCall.getReferredQuery()));
    }


    protected void copyNegativePatternCallConstraint(NegativePatternCall negativePatternCall) {
        PVariable[] mappedVariables = extractMappedVariables(negativePatternCall);
        FlatTuple variablesTuple = new FlatTuple((Object[])mappedVariables);
        addTrace(negativePatternCall, new NegativePatternCall(body, variablesTuple, negativePatternCall.getReferredQuery()));
    }

    protected void copyBinaryTransitiveClosureConstraint(BinaryTransitiveClosure binaryTransitiveClosure) {
        PVariable[] mappedVariables = extractMappedVariables(binaryTransitiveClosure);
        FlatTuple variablesTuple = new FlatTuple((Object[])mappedVariables);
        addTrace(binaryTransitiveClosure, new BinaryTransitiveClosure(body, variablesTuple, binaryTransitiveClosure.getReferredQuery()));
    }

    protected void copyPatternMatchCounterConstraint(PatternMatchCounter patternMatchCounter) {
        PVariable[] mappedVariables = extractMappedVariables(patternMatchCounter);
        PVariable mappedResultVariable = variableMapping.get(patternMatchCounter.getResultVariable());
        FlatTuple variablesTuple = new FlatTuple((Object[])mappedVariables);
        addTrace(patternMatchCounter, new PatternMatchCounter(body, variablesTuple, patternMatchCounter.getReferredQuery(), mappedResultVariable));
    }
    
    /**
     * @since 1.4
     */
    protected void copyAggregatorConstraint(AggregatorConstraint constraint) {
        PVariable[] mappedVariables = extractMappedVariables(constraint);
        PVariable mappedResultVariable = variableMapping.get(constraint.getResultVariable());
        FlatTuple variablesTuple = new FlatTuple((Object[])mappedVariables);
        addTrace(constraint, new AggregatorConstraint(constraint.getAggregator(), body, variablesTuple, constraint.getReferredQuery(), mappedResultVariable, constraint.getAggregatedColumn()));
    }


    protected void copyExpressionEvaluationConstraint(ExpressionEvaluation expressionEvaluation) {
        PVariable mappedOutputVariable = variableMapping.get(expressionEvaluation.getOutputVariable());
        addTrace(expressionEvaluation, new ExpressionEvaluation(body, new VariableMappingExpressionEvaluatorWrapper(expressionEvaluation.getEvaluator(), variableMapping), mappedOutputVariable));
    }
    
    
    /**
     * For positive pattern calls
     * 
     * @param positivePatternCall
     * @return the mapped variables to the pattern's parameters
     */
    protected PVariable[] extractMappedVariables(EnumerablePConstraint enumerablePConstraint) {
        Object[] pVariables = enumerablePConstraint.getVariablesTuple().getElements();
        return mapVariableList(pVariables);
    }

    /**
     * For negative and count pattern calls.
     * 
     * @param patternMatchCounter
     * @return the mapped variables to the pattern's parameters
     */
    private PVariable[] extractMappedVariables(PatternCallBasedDeferred patternCallBasedDeferred) {
        Object[] pVariables = patternCallBasedDeferred.getActualParametersTuple().getElements();
        return mapVariableList(pVariables);
    }

    /**
     * For type filters.
     */
    private PVariable[] extractMappedVariables(TypeFilterConstraint typeFilterConstraint) {
        Object[] pVariables = typeFilterConstraint.getVariablesTuple().getElements();
        return mapVariableList(pVariables);
    }
  
    private PVariable[] mapVariableList(Object[] pVariables) {
        List<PVariable> list = new ArrayList<PVariable>();
        for (int i = 0; i < pVariables.length; i++) {
            PVariable mappedVariable = variableMapping.get(pVariables[i]);
            list.add(mappedVariable);
        }
        return list.toArray(new PVariable[0]);
    }

}
