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

package org.eclipse.incquery.patternlanguage.emf.specification.builder;

import java.util.List;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.incquery.patternlanguage.emf.eMFPatternLanguage.ClassType;
import org.eclipse.incquery.patternlanguage.emf.eMFPatternLanguage.EClassifierConstraint;
import org.eclipse.incquery.patternlanguage.emf.eMFPatternLanguage.EnumValue;
import org.eclipse.incquery.patternlanguage.emf.eMFPatternLanguage.ReferenceType;
import org.eclipse.incquery.patternlanguage.emf.specification.GenericEMFPatternPQuery;
import org.eclipse.incquery.patternlanguage.emf.specification.GenericQuerySpecification;
import org.eclipse.incquery.patternlanguage.emf.specification.XBaseEvaluator;
import org.eclipse.incquery.patternlanguage.helper.CorePatternLanguageHelper;
import org.eclipse.incquery.patternlanguage.patternLanguage.AggregatedValue;
import org.eclipse.incquery.patternlanguage.patternLanguage.AggregatorExpression;
import org.eclipse.incquery.patternlanguage.patternLanguage.Annotation;
import org.eclipse.incquery.patternlanguage.patternLanguage.AnnotationParameter;
import org.eclipse.incquery.patternlanguage.patternLanguage.BoolValue;
import org.eclipse.incquery.patternlanguage.patternLanguage.CheckConstraint;
import org.eclipse.incquery.patternlanguage.patternLanguage.CompareConstraint;
import org.eclipse.incquery.patternlanguage.patternLanguage.Constraint;
import org.eclipse.incquery.patternlanguage.patternLanguage.CountAggregator;
import org.eclipse.incquery.patternlanguage.patternLanguage.DoubleValue;
import org.eclipse.incquery.patternlanguage.patternLanguage.FunctionEvaluationValue;
import org.eclipse.incquery.patternlanguage.patternLanguage.IntValue;
import org.eclipse.incquery.patternlanguage.patternLanguage.ListValue;
import org.eclipse.incquery.patternlanguage.patternLanguage.ParameterRef;
import org.eclipse.incquery.patternlanguage.patternLanguage.PathExpressionConstraint;
import org.eclipse.incquery.patternlanguage.patternLanguage.PathExpressionHead;
import org.eclipse.incquery.patternlanguage.patternLanguage.PathExpressionTail;
import org.eclipse.incquery.patternlanguage.patternLanguage.Pattern;
import org.eclipse.incquery.patternlanguage.patternLanguage.PatternBody;
import org.eclipse.incquery.patternlanguage.patternLanguage.PatternCall;
import org.eclipse.incquery.patternlanguage.patternLanguage.PatternCompositionConstraint;
import org.eclipse.incquery.patternlanguage.patternLanguage.StringValue;
import org.eclipse.incquery.patternlanguage.patternLanguage.Type;
import org.eclipse.incquery.patternlanguage.patternLanguage.ValueReference;
import org.eclipse.incquery.patternlanguage.patternLanguage.Variable;
import org.eclipse.incquery.patternlanguage.patternLanguage.VariableReference;
import org.eclipse.incquery.patternlanguage.patternLanguage.VariableValue;
import org.eclipse.incquery.runtime.api.IQuerySpecification;
import org.eclipse.incquery.runtime.extensibility.QuerySpecificationRegistry;
import org.eclipse.incquery.runtime.matchers.context.IPatternMatcherContext;
import org.eclipse.incquery.runtime.matchers.context.IPatternMatcherContext.EdgeInterpretation;
import org.eclipse.incquery.runtime.matchers.context.surrogate.SurrogateQueryRegistry;
import org.eclipse.incquery.runtime.matchers.psystem.PBody;
import org.eclipse.incquery.runtime.matchers.psystem.PVariable;
import org.eclipse.incquery.runtime.matchers.psystem.annotations.PAnnotation;
import org.eclipse.incquery.runtime.matchers.psystem.annotations.ParameterReference;
import org.eclipse.incquery.runtime.matchers.psystem.basicdeferred.Equality;
import org.eclipse.incquery.runtime.matchers.psystem.basicdeferred.ExportedParameter;
import org.eclipse.incquery.runtime.matchers.psystem.basicdeferred.ExpressionEvaluation;
import org.eclipse.incquery.runtime.matchers.psystem.basicdeferred.Inequality;
import org.eclipse.incquery.runtime.matchers.psystem.basicdeferred.NegativePatternCall;
import org.eclipse.incquery.runtime.matchers.psystem.basicdeferred.PatternMatchCounter;
import org.eclipse.incquery.runtime.matchers.psystem.basicenumerables.BinaryTransitiveClosure;
import org.eclipse.incquery.runtime.matchers.psystem.basicenumerables.PositivePatternCall;
import org.eclipse.incquery.runtime.matchers.psystem.basicenumerables.TypeBinary;
import org.eclipse.incquery.runtime.matchers.psystem.basicenumerables.TypeTernary;
import org.eclipse.incquery.runtime.matchers.psystem.basicenumerables.TypeUnary;
import org.eclipse.incquery.runtime.matchers.psystem.queries.PQuery;
import org.eclipse.incquery.runtime.matchers.psystem.queries.QueryInitializationException;
import org.eclipse.incquery.runtime.matchers.tuple.FlatTuple;
import org.eclipse.incquery.runtime.matchers.tuple.Tuple;
import org.eclipse.xtext.xbase.XExpression;

import com.google.common.base.Function;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;

/**
 * @author Gabor Bergmann
 *
 */
public class EPMToPBody {

    protected Pattern pattern;
    protected IPatternMatcherContext context;

    String patternFQN;
    private PQuery query;
    private NameToSpecificationMap patternMap;

    public EPMToPBody(Pattern pattern, PQuery query, IPatternMatcherContext context, NameToSpecificationMap patternMap) {
        super();
        this.pattern = pattern;
        this.query = query;
        this.context = context;
        this.patternMap = patternMap;

        patternFQN = CorePatternLanguageHelper.getFullyQualifiedName(pattern);
    }

    public PBody toPBody(PatternBody body) throws QueryInitializationException {
        try {
            PBody pBody = new PBody(query);
            
            preProcessParameters(pBody);
            gatherBodyConstraints(body, pBody);
            return pBody;
        } catch (SpecificationBuilderException e) {
            e.setPatternDescription(pattern);
            throw e;
        }
    }

    public PAnnotation toPAnnotation(Annotation annotation) {
        PAnnotation pAnnotation = new PAnnotation(annotation.getName());
        for (AnnotationParameter param : annotation.getParameters()) {
            String parameterName = param.getName();
            ValueReference ref = param.getValue();
            if (ref != null) {
                final Object valueReference = getValue(ref);
                if (!Strings.isNullOrEmpty(parameterName) && valueReference != null) {
                    pAnnotation.addAttribute(parameterName, valueReference);
                }
            }
        }
        return pAnnotation;
    }

    protected Object getValue(ValueReference ref) {
        Object value = null;
        if (ref instanceof BoolValue) {
            value = ((BoolValue)ref).isValue();
        } else if (ref instanceof DoubleValue) {
            value = ((DoubleValue)ref).getValue();
        } else if (ref instanceof IntValue) {
            value = ((IntValue)ref).getValue();
        } else if (ref instanceof org.eclipse.incquery.patternlanguage.patternLanguage.StringValue) {
            value = ((org.eclipse.incquery.patternlanguage.patternLanguage.StringValue)ref).getValue();
        } else if (ref instanceof VariableReference) {
            value = new ParameterReference(((VariableReference) ref).getVar());
        } else if (ref instanceof VariableValue) {
            value = new ParameterReference(((VariableValue)ref).getValue().getVar());
        } else if (ref instanceof ListValue) {
            value = Lists.transform(((ListValue) ref).getValues(), new Function<ValueReference, Object>() {

                @Override
                public Object apply(ValueReference ref) {
                    return getValue(ref);
                }
            });
        } else {
            throw new UnsupportedOperationException("Unknown attribute parameter type");
        }
        return value;
    }

    protected PVariable getPNode(Variable variable, final PBody pBody) {
        if (variable instanceof ParameterRef) // handle referenced parameter variables
            return getPNode(((ParameterRef) variable).getReferredParam(), pBody); // assumed to be non-null
        else
            return pBody.getOrCreateVariableByName(variable.getName() /* now this is unique again! */);
    }

    protected PVariable getPNode(VariableReference variable, final PBody pBody) {
        // Warning! variable.getVar() does not differentiate between
        // multiple anonymous variables ('_')
        return getPNode(variable.getVariable(), pBody);
    }

    protected Tuple getPNodeTuple(List<? extends ValueReference> variables, final PBody pBody) throws SpecificationBuilderException {
        int k = 0;
        // The Object[] is required otherwise the new FlatTuple shows a warning
        Object[] pNodeArray = new PVariable[variables.size()];
        for (ValueReference varRef : variables) {
            pNodeArray[k++] = getPNode(varRef, pBody);
        }
        return new FlatTuple(pNodeArray);
    }

    protected PVariable getPNode(ValueReference reference, final PBody pBody) throws SpecificationBuilderException {
        if (reference instanceof VariableValue)
            return getPNode(((VariableValue) reference).getValue(), pBody);
        else if (reference instanceof AggregatedValue)
            return aggregate((AggregatedValue) reference, pBody);
        else if (reference instanceof FunctionEvaluationValue)
            return eval((FunctionEvaluationValue) reference, pBody);
        else if (reference instanceof IntValue)
            return pBody.newConstantVariable(((IntValue) reference).getValue());
        else if (reference instanceof StringValue)
            return pBody.newConstantVariable(((StringValue) reference).getValue());
        else if (reference instanceof EnumValue) // EMF-specific
            return pBody.newConstantVariable(((EnumValue) reference).getLiteral().getInstance());
        else if (reference instanceof DoubleValue) {
            return pBody.newConstantVariable(((DoubleValue) reference).getValue());
        } else if (reference instanceof BoolValue) {
            Boolean b = ((BoolValue) reference).isValue();
            return pBody.newConstantVariable(b);
        } else
            throw new SpecificationBuilderException(
                    "Unsupported value reference of type {1} from EPackage {2} currently unsupported by pattern builder in pattern {3}.",
                    new String[] { reference != null ? reference.eClass().getName() : "(null)",
                            reference != null ? reference.eClass().getEPackage().getNsURI() : "(null)",
                            pattern.getName() }, "Unsupported value expression", pattern);
    }

	protected PVariable newVirtual(final PBody pBody) {
        return pBody.newVirtualVariable();
    }

    private void preProcessParameters(final PBody pBody) {
        EList<Variable> parameters = pattern.getParameters();
        List<ExportedParameter> exportedParameters = Lists.newArrayList();
        for (Variable variable : parameters) {
            final ExportedParameter exportedParameter = new ExportedParameter(pBody, getPNode(variable, pBody), variable.getName());
            if (variable.getType() != null && variable.getType() instanceof ClassType) {
                EClassifier classname = ((ClassType) variable.getType()).getClassname();
                PVariable pNode = getPNode(variable, pBody);
                new TypeUnary(pBody, pNode, classname, context.printType(classname));
            }
            exportedParameters.add(exportedParameter);
        }
        pBody.setExportedParameters(exportedParameters);
    }

    private void gatherBodyConstraints(PatternBody body, final PBody pBody) throws SpecificationBuilderException {
        EList<Constraint> constraints = body.getConstraints();
        for (Constraint constraint : constraints) {
            gatherConstraint(constraint, pBody);
        }
    }

    protected void gatherConstraint(Constraint constraint, final PBody pBody) throws SpecificationBuilderException {
        if (constraint instanceof EClassifierConstraint) { // EMF-specific
            EClassifierConstraint constraint2 = (EClassifierConstraint) constraint;
            gatherClassifierConstraint(constraint2, pBody);
        } else if (constraint instanceof PatternCompositionConstraint) {
            PatternCompositionConstraint constraint2 = (PatternCompositionConstraint) constraint;
            gatherCompositionConstraint(constraint2, pBody);
        } else if (constraint instanceof CompareConstraint) {
            CompareConstraint compare = (CompareConstraint) constraint;
            gatherCompareConstraint(compare, pBody);
        } else if (constraint instanceof PathExpressionConstraint) {
            PathExpressionConstraint pathExpression = (PathExpressionConstraint) constraint;
            gatherPathExpression(pathExpression, pBody);
        } else if (constraint instanceof CheckConstraint) {
            final CheckConstraint check = (CheckConstraint) constraint;
            gatherCheckConstraint(check, pBody);
        } else {
            throw new SpecificationBuilderException("Unsupported constraint type {1} in pattern {2}.", new String[] {
                    constraint.eClass().getName(), patternFQN }, "Unsupported constraint type", pattern);
        }
    }

    protected void gatherPathExpression(PathExpressionConstraint pathExpression, final PBody pBody) throws SpecificationBuilderException {
        PathExpressionHead head = pathExpression.getHead();
        PVariable currentSrc = getPNode(head.getSrc(), pBody);
        PVariable finalDst = getPNode(head.getDst(), pBody);
        PathExpressionTail currentTail = head.getTail();

        // type constraint on source
        Type headType = head.getType();
        if (headType instanceof ClassType) {
            EClassifier headClassname = ((ClassType) headType).getClassname();
            new TypeUnary(pBody, currentSrc, headClassname, context.printType(headClassname));
        } else {
            throw new SpecificationBuilderException("Unsupported path expression head type {1} in pattern {2}: {3}",
                    new String[] { headType.eClass().getName(), patternFQN, typeStr(headType) },
                    "Unsupported navigation source", pattern);
        }

        // process each segment
        while (currentTail != null) {
            Type currentPathSegmentType = currentTail.getType();
            currentTail = currentTail.getTail();

            PVariable intermediate = newVirtual(pBody);
            gatherPathSegment(currentPathSegmentType, currentSrc, intermediate, pBody);

            currentSrc = intermediate;
        }
        // link the final step to the overall destination
        new Equality(pBody, currentSrc, finalDst);
    }

    protected void gatherCompareConstraint(CompareConstraint compare, final PBody pBody) throws SpecificationBuilderException {
        PVariable left = getPNode(compare.getLeftOperand(), pBody);
        PVariable right = getPNode(compare.getRightOperand(), pBody);
        switch (compare.getFeature()) {
        case EQUALITY:
            new Equality(pBody, left, right);
            break;
        case INEQUALITY:
            new Inequality(pBody, left, right, false);
            break;
        }
    }

    protected void gatherCompositionConstraint(PatternCompositionConstraint constraint, final PBody pBody)
            throws SpecificationBuilderException {
        PatternCall call = constraint.getCall();
        Pattern patternRef = call.getPatternRef();
        PQuery calledSpecification = findCalledPQuery(patternRef);
        Tuple pNodeTuple = getPNodeTuple(call.getParameters(), pBody);
        if (!call.isTransitive()) {
            if (constraint.isNegative())
                new NegativePatternCall(pBody, pNodeTuple, calledSpecification);
            else
                new PositivePatternCall(pBody, pNodeTuple, calledSpecification);
        } else {
            if (pNodeTuple.getSize() != 2)
                throw new SpecificationBuilderException(
                        "Transitive closure of {1} in pattern {2} is unsupported because called pattern is not binary.",
                        new String[] { CorePatternLanguageHelper.getFullyQualifiedName(patternRef), patternFQN },
                        "Transitive closure only supported for binary patterns.", pattern);
            else if (constraint.isNegative())
                throw new SpecificationBuilderException("Unsupported negated transitive closure of {1} in pattern {2}",
                        new String[] { CorePatternLanguageHelper.getFullyQualifiedName(patternRef), patternFQN },
                        "Unsupported negated transitive closure", pattern);
            else
                new BinaryTransitiveClosure(pBody, pNodeTuple, calledSpecification);
        }
    }

    protected void gatherClassifierConstraint(EClassifierConstraint constraint, final PBody pBody) {
        EClassifier classname = ((ClassType) constraint.getType()).getClassname();
        PVariable pNode = getPNode(constraint.getVar(), pBody);
        new TypeUnary(pBody, pNode, classname, context.printType(classname));
    }

    protected void gatherPathSegment(Type segmentType, PVariable src, PVariable trg, final PBody pBody) throws SpecificationBuilderException {
        if (segmentType instanceof ReferenceType) { // EMF-specific
            EStructuralFeature typeObject = ((ReferenceType) segmentType).getRefname();
            boolean hasSurrogateQueryFQN = SurrogateQueryRegistry.instance().hasSurrogateQueryFQN(typeObject);
            if(hasSurrogateQueryFQN) {
            	gatherSurrogateQueryCall(src, trg, pBody, typeObject);
            } else {
            	if (context.edgeInterpretation() == EdgeInterpretation.TERNARY) {
            		new TypeTernary(pBody, context, newVirtual(pBody), src, trg, typeObject, context.printType(typeObject));
            	} else {
            		new TypeBinary(pBody, context, src, trg, typeObject, context.printType(typeObject));
            	}
            }
        } else
            throw new SpecificationBuilderException("Unsupported path segment type {1} in pattern {2}: {3}", new String[] {
                    segmentType.eClass().getName(), patternFQN, typeStr(segmentType) }, "Unsupported navigation step",
                    pattern);
    }

	private void gatherSurrogateQueryCall(PVariable src, PVariable trg, final PBody pBody,
			EStructuralFeature typeObject) throws SpecificationBuilderException {
		String surrogateQueryFQN = SurrogateQueryRegistry.instance().getSurrogateQueryFQN(typeObject);
        IQuerySpecification<?> specification = patternMap.get(surrogateQueryFQN);
		if(specification == null) {
		   // XXX patternMap must contain surrogate query specifications, but QueryExplorer cannot put generated specifications into it
		   context.logWarning(String.format("Surrogate query specification %s not added to SpecificationBuilder, trying from QuerySpecificationRegistry", surrogateQueryFQN));
		   specification = QuerySpecificationRegistry.getQuerySpecification(surrogateQueryFQN);
		}
		if(specification == null) {
		    throw new SpecificationBuilderException("Could not resolve surrogate query {1}.", new String[] {surrogateQueryFQN}, "Could not resolve surrogate query", pattern);
		}
		PQuery internalQueryRepresentation = specification.getInternalQueryRepresentation();
		Tuple variablesTuple = new FlatTuple(src, trg);
		new PositivePatternCall(pBody, variablesTuple, internalQueryRepresentation);
	}

    protected void gatherCheckConstraint(final CheckConstraint check, final PBody pBody) throws SpecificationBuilderException {
        XExpression expression = check.getExpression();
        new ExpressionEvaluation(pBody, new XBaseEvaluator(expression, pattern), null);
    }

    protected PVariable eval(FunctionEvaluationValue eval, final PBody pBody) throws SpecificationBuilderException {
        PVariable result = newVirtual(pBody);

        XExpression expression = eval.getExpression();
        new ExpressionEvaluation(pBody, new XBaseEvaluator(expression, pattern), result);

        return result;
	}

    protected PVariable aggregate(AggregatedValue reference, final PBody pBody) throws SpecificationBuilderException {
        PVariable result = newVirtual(pBody);

        PatternCall call = reference.getCall();
        Pattern patternRef = call.getPatternRef();
        PQuery calledSpecification = findCalledPQuery(patternRef);
        Tuple pNodeTuple = getPNodeTuple(call.getParameters(), pBody);

        AggregatorExpression aggregator = reference.getAggregator();
        if (aggregator instanceof CountAggregator) {
            new PatternMatchCounter(pBody, pNodeTuple, calledSpecification, result);
        } else {
            throw new SpecificationBuilderException("Unsupported aggregator expression type {1} in pattern {2}.",
                    new String[] { aggregator.eClass().getName(), patternFQN }, "Unsupported aggregator expression",
                    pattern);
        }
        return result;
    }

    private PQuery findCalledPQuery(Pattern patternRef) {
        IQuerySpecification<?> calledSpecification = patternMap.get(CorePatternLanguageHelper.getFullyQualifiedName(patternRef));
        if (calledSpecification == null) {
        	// This should only happen in case of erroneous links, e.g. link to a proxy Pattern or similar
        	// otherwise pattern would be found in the name map (see SpecificationBuilder logic)
            try {
				calledSpecification = new GenericQuerySpecification(new GenericEMFPatternPQuery(patternRef, true));
			} catch (QueryInitializationException e) {
				// Cannot happen, as initialization is delayed
				throw new RuntimeException(e);
			}
        }
        return calledSpecification.getInternalQueryRepresentation();
    }


    /**
     * @return the string describing a metamodel type, for debug / exception purposes
     */
    private String typeStr(Type type) {
        return type.getTypename() == null ? "(null)" : type.getTypename();
    }

}
