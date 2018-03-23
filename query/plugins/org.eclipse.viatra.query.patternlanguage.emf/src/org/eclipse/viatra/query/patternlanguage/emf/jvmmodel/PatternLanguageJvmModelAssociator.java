/*******************************************************************************
 * Copyright (c) 2010-2016, Zoltan Ujhelyi, Istvan Rath and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Zoltan Ujhelyi - initial API and implementation
 *******************************************************************************/
package org.eclipse.viatra.query.patternlanguage.emf.jvmmodel;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.TreeIterator;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.viatra.query.patternlanguage.emf.helper.PatternLanguageHelper;
import org.eclipse.viatra.query.patternlanguage.emf.types.ITypeInferrer;
import org.eclipse.viatra.query.patternlanguage.emf.vql.AggregatedValue;
import org.eclipse.viatra.query.patternlanguage.emf.vql.Annotation;
import org.eclipse.viatra.query.patternlanguage.emf.vql.AnnotationParameter;
import org.eclipse.viatra.query.patternlanguage.emf.vql.Constraint;
import org.eclipse.viatra.query.patternlanguage.emf.vql.ListValue;
import org.eclipse.viatra.query.patternlanguage.emf.vql.ParameterRef;
import org.eclipse.viatra.query.patternlanguage.emf.vql.Pattern;
import org.eclipse.viatra.query.patternlanguage.emf.vql.PatternBody;
import org.eclipse.viatra.query.patternlanguage.emf.vql.PatternLanguageFactory;
import org.eclipse.viatra.query.patternlanguage.emf.vql.PatternLanguagePackage;
import org.eclipse.viatra.query.patternlanguage.emf.vql.Variable;
import org.eclipse.viatra.query.patternlanguage.emf.vql.VariableReference;
import org.eclipse.viatra.query.patternlanguage.emf.util.AggregatorUtil;
import org.eclipse.xtext.common.types.JvmType;
import org.eclipse.xtext.common.types.JvmTypeReference;
import org.eclipse.xtext.common.types.util.TypeReferences;
import org.eclipse.xtext.resource.DerivedStateAwareResource;
import org.eclipse.xtext.xbase.jvmmodel.JvmModelAssociator;

import com.google.common.base.Strings;
import com.google.common.collect.Iterables;
import com.google.inject.Inject;

/**
 * @since 2.0
 */
public class PatternLanguageJvmModelAssociator extends JvmModelAssociator {

    @Inject
    private ITypeInferrer typeInferrer;
    @Inject
    private TypeReferences typeReferences;
    
    @Override
    public void installDerivedState(DerivedStateAwareResource resource, boolean preIndexingPhase) {
        calculateDerivedVariableObjects(resource);
        super.installDerivedState(resource, preIndexingPhase);
        if (!preIndexingPhase) {
            calculateAggregateTypes(resource);
        }
    }

    protected void calculateDerivedVariableObjects(DerivedStateAwareResource resource) {
        TreeIterator<EObject> it = resource.getAllContents();
        while(it.hasNext()) {
            EObject obj = it.next();
            if (obj instanceof Pattern) {
                Pattern pattern = (Pattern) obj;
                for (PatternBody body : pattern.getBodies()) {
                    EList<Variable> variables = body.getVariables();
                    variables.addAll(getAllVariablesInBody(body, variables));
                }
                for (Annotation annotation : pattern.getAnnotations()) {
                    for (AnnotationParameter parameter : annotation.getParameters()) {
                        if ((parameter.getValue()) instanceof VariableReference) {
                            final VariableReference reference = (VariableReference) parameter.getValue();
                            setDeclaredParameter(pattern, reference);
                        } else if ((parameter.getValue()) instanceof ListValue) {
                            ListValue listValue = (ListValue) (parameter.getValue());
                            for (VariableReference reference : Iterables.filter(listValue.getValues(), VariableReference.class)) {
                                setDeclaredParameter(pattern, reference);
                            }
                        }
                    }
                }
                it.prune();
                
            }
        }
    }

    private void setDeclaredParameter(Pattern pattern, final VariableReference reference) {
        pattern.getParameters().stream().filter(variable -> Objects.equals(variable.getName(), reference.getVar()))
                .findFirst().ifPresent(reference::setVariable);
    }

    private EList<Variable> getAllVariablesInBody(PatternBody body, EList<Variable> previous) {
        EList<Variable> variables = previous;

        Map<String, Variable> parameterMap = new HashMap<>();

        EList<Variable> parameters = ((Pattern) body.eContainer()).getParameters();
        for (Variable var : variables) {
            parameterMap.put(var.getName(), var);
        }
        for (Variable var : parameters) {
            if (!parameterMap.containsKey(var.getName())) {
                // Creating a new paramater ref variable
                ParameterRef refVar = initializeParameterRef(var);
                parameterMap.put(var.getName(), refVar);
                variables.add(refVar);
            }
        }
        int unnamedCounter = 0;
        for (Constraint constraint : body.getConstraints()) {
            Iterator<EObject> it = constraint.eAllContents();
            while (it.hasNext()) {
                EObject obj = it.next();
                if (obj instanceof VariableReference) {
                    VariableReference varRef = (VariableReference) obj;
                    String varName = varRef.getVar();
                    if (Strings.isNullOrEmpty(varName)) {
                        //This can happen only in invalid patterns or in unnamed aggregates
                        varName = String.format("#<%d>", unnamedCounter);
                        unnamedCounter++;
                    }
                    if ("_".equals(varName)) {
                        varName = String.format("_<%d>", unnamedCounter);
                        unnamedCounter++;
                    } else if (PatternLanguageHelper.isAggregateReference(varRef)) {
                        varName = PatternLanguageHelper.AGGREGATE_VARIABLE_PREFIX + varName;
                    }
                    Variable var;
                    if (parameterMap.containsKey(varName)) {
                        var = parameterMap.get(varName);
                    } else {
                        var = initializeLocalVariable(varName);
                        variables.add(var);
                        parameterMap.put(varName, var);
                    }
                    if (!varRef.eIsSet(PatternLanguagePackage.Literals.VARIABLE_REFERENCE__VARIABLE) || !varRef.getVariable().equals(var)) {
                        varRef.setVariable(var);
                    }
                }
            }
        }

        return variables;
    }

    private Variable initializeLocalVariable(String varName) {
        Variable decl;
        decl = PatternLanguageFactory.eINSTANCE.createVariable();
        decl.setName(varName);
        return decl;
    }

    private ParameterRef initializeParameterRef(Variable var) {
        ParameterRef refVar = PatternLanguageFactory.eINSTANCE.createParameterRef();
        refVar.setName(var.getName());
        // refVar.setType(var.getType());
        refVar.setReferredParam(var);
        return refVar;
    }
    
    /**
     * @since 1.4
     */
    protected void calculateAggregateTypes(DerivedStateAwareResource resource) {
        TreeIterator<EObject> it = resource.getAllContents();
        while (it.hasNext()) {
            EObject obj = it.next();
            if (obj instanceof AggregatedValue) {
                AggregatedValue aggregatedValue = (AggregatedValue) obj;
                if (AggregatorUtil.mustHaveAggregatorVariables(aggregatedValue)) {
                    VariableReference aggregateParameter = AggregatorUtil.getAggregatorVariable(aggregatedValue);
                    if (aggregateParameter == null) {
                        aggregatedValue.setAggregateType(typeReferences.findDeclaredType(Void.class, aggregatedValue));
                    } else {
                        JvmTypeReference jvmType = typeInferrer.getJvmType(aggregateParameter, aggregatedValue);
                        aggregatedValue.setAggregateType(jvmType.getType());
                    }
                }
            } else if (obj instanceof JvmType) {
                it.prune();
            }
        }
    }
}