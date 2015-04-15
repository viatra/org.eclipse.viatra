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
package org.eclipse.incquery.runtime.matchers.psystem.rewriters;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.incquery.runtime.matchers.psystem.EnumerablePConstraint;
import org.eclipse.incquery.runtime.matchers.psystem.PBody;
import org.eclipse.incquery.runtime.matchers.psystem.PConstraint;
import org.eclipse.incquery.runtime.matchers.psystem.PVariable;
import org.eclipse.incquery.runtime.matchers.psystem.basicdeferred.Equality;
import org.eclipse.incquery.runtime.matchers.psystem.basicdeferred.ExportedParameter;
import org.eclipse.incquery.runtime.matchers.psystem.basicdeferred.ExpressionEvaluation;
import org.eclipse.incquery.runtime.matchers.psystem.basicdeferred.Inequality;
import org.eclipse.incquery.runtime.matchers.psystem.basicdeferred.NegativePatternCall;
import org.eclipse.incquery.runtime.matchers.psystem.basicdeferred.PatternCallBasedDeferred;
import org.eclipse.incquery.runtime.matchers.psystem.basicdeferred.PatternMatchCounter;
import org.eclipse.incquery.runtime.matchers.psystem.basicenumerables.BinaryTransitiveClosure;
import org.eclipse.incquery.runtime.matchers.psystem.basicenumerables.ConstantValue;
import org.eclipse.incquery.runtime.matchers.psystem.basicenumerables.PositivePatternCall;
import org.eclipse.incquery.runtime.matchers.psystem.basicenumerables.TypeBinary;
import org.eclipse.incquery.runtime.matchers.psystem.basicenumerables.TypeUnary;
import org.eclipse.incquery.runtime.matchers.psystem.queries.PQuery;
import org.eclipse.incquery.runtime.matchers.psystem.rewriters.IConstraintFilter.AllowAllFilter;
import org.eclipse.incquery.runtime.matchers.psystem.rewriters.IVariableRenamer.SameName;
import org.eclipse.incquery.runtime.matchers.tuple.FlatTuple;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

/**
 * This class can create a new PBody for a PQuery. The result body contains a copy of given variables and constraints. 
 * 
 * @author Marton Bur
 * 
 */
public class PBodyCopier {

    /**
     * The created body
     */
    PBody body;
    /**
     * Mapping between the original and the copied variables
     */
    Map<PVariable, PVariable> variableMapping = Maps.newHashMap();
    
    
    public Map<PVariable, PVariable> getVariableMapping() {
        return variableMapping;
    }
    
    public PBodyCopier(PBody body) {
        this.body = new PBody(body.getPattern());
        mergeBody(body);
        
    }
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

        // Copy constraints which are not filtered
        Set<PConstraint> constraints = sourceBody.getConstraints();
        for (PConstraint pConstraint : constraints) {
            if (!filter.filter(pConstraint) ) {
                copyConstraint(pConstraint);
            }
        }
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
        } else if (constraint instanceof TypeUnary) {
            copyTypeUnaryConstraint((TypeUnary) constraint);
        } else if (constraint instanceof TypeBinary) {
            copyTypeBinaryConstraint((TypeBinary) constraint);
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
        } else if (constraint instanceof ExpressionEvaluation) {
            copyExpressionEvaluationConstraint((ExpressionEvaluation) constraint);
        }
    }


    protected void copyExportedParameterConstraint(ExportedParameter exportedParameter) {
        PVariable mappedPVariable = variableMapping.get(exportedParameter.getParameterVariable());
        ExportedParameter newExportedParameter = new ExportedParameter(body, mappedPVariable, exportedParameter.getParameterName());
        if (body.getSymbolicParameters().size() == 0) {
            List<ExportedParameter> exportedParameters = Lists.<ExportedParameter> newArrayList();
            exportedParameters.add(newExportedParameter);
            body.setExportedParameters(exportedParameters);
        }
        else {
            body.getSymbolicParameters().add(newExportedParameter);
        }
    }

    protected void copyEqualityConstraint(Equality equality) {
        PVariable who = equality.getWho();
        PVariable withWhom = equality.getWithWhom();
        new Equality(body, variableMapping.get(who), variableMapping.get(withWhom));
    }

    protected void copyInequalityConstraint(Inequality inequality) {
        PVariable who = inequality.getWho();
        PVariable withWhom = inequality.getWithWhom();
        new Inequality(body, variableMapping.get(who), variableMapping.get(withWhom));
    }

    protected void copyTypeUnaryConstraint(TypeUnary typeUnary) {
        PVariable pVariable = (PVariable) typeUnary.getVariablesTuple().getElements()[0];
        new TypeUnary(body, variableMapping.get(pVariable), typeUnary.getSupplierKey(), typeUnary.getTypeString());
    }

    protected void copyTypeBinaryConstraint(TypeBinary typeBinary) {
        Object[] elements = typeBinary.getVariablesTuple().getElements();
        PVariable pVariable1 = (PVariable) elements[0];
        PVariable pVariable2 = (PVariable) elements[1];
        new TypeBinary(body, typeBinary.getContext(), variableMapping.get(pVariable1), variableMapping.get(pVariable2), typeBinary.getSupplierKey(), typeBinary.getTypeString());
    }

    protected void copyConstantValueConstraint(ConstantValue constantValue) {
        PVariable pVariable = (PVariable) constantValue.getVariablesTuple().getElements()[0];
        new ConstantValue(body, variableMapping.get(pVariable), constantValue.getSupplierKey());
    }

    protected void copyPositivePatternCallConstraint(PositivePatternCall positivePatternCall) {
        PVariable[] mappedVariables = extractMappedVariables(positivePatternCall);
        FlatTuple variablesTuple = new FlatTuple((Object[])mappedVariables);
        new PositivePatternCall(body, variablesTuple, positivePatternCall.getReferredQuery());
    }


    protected void copyNegativePatternCallConstraint(NegativePatternCall negativePatternCall) {
        PVariable[] mappedVariables = extractMappedVariables(negativePatternCall);
        FlatTuple variablesTuple = new FlatTuple((Object[])mappedVariables);
        new NegativePatternCall(body, variablesTuple, negativePatternCall.getReferredQuery());
    }

    protected void copyBinaryTransitiveClosureConstraint(BinaryTransitiveClosure binaryTransitiveClosure) {
        PVariable[] mappedVariables = extractMappedVariables(binaryTransitiveClosure);
        FlatTuple variablesTuple = new FlatTuple((Object[])mappedVariables);
        new BinaryTransitiveClosure(body, variablesTuple, binaryTransitiveClosure.getReferredQuery());
    }

    protected void copyPatternMatchCounterConstraint(PatternMatchCounter patternMatchCounter) {
        PVariable[] mappedVariables = extractMappedVariables(patternMatchCounter);
        PVariable mappedResultVariable = variableMapping.get(patternMatchCounter.getResultVariable());
        FlatTuple variablesTuple = new FlatTuple((Object[])mappedVariables);
        new PatternMatchCounter(body, variablesTuple, patternMatchCounter.getReferredQuery(), mappedResultVariable);
    }


    protected void copyExpressionEvaluationConstraint(ExpressionEvaluation expressionEvaluation) {
        PVariable mappedOutputVariable = variableMapping.get(expressionEvaluation.getOutputVariable());
        new ExpressionEvaluation(body, expressionEvaluation.getEvaluator(), mappedOutputVariable);
    }
    
    
    /**
     * For positive pattern calls
     * 
     * @param positivePatternCall
     * @return the mapped variables to the pattern's parameters
     */
    private PVariable[] extractMappedVariables(EnumerablePConstraint enumerablePConstraint) {
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
    
    private PVariable[] mapVariableList(Object[] pVariables) {
        List<PVariable> list = new ArrayList<PVariable>();
        for (int i = 0; i < pVariables.length; i++) {
            PVariable mappedVariable = variableMapping.get(pVariables[i]);
            list.add(mappedVariable);
        }
        return list.toArray(new PVariable[0]);
    }

}
