/*******************************************************************************
 * Copyright (c) 2010-2018, Zoltan Ujhelyi, IncQuery Labs Ltd.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Zoltan Ujhelyi - initial API and implementation
 *******************************************************************************/
package org.eclipse.viatra.query.patternlanguage.emf.util;

import java.util.Optional;
import java.util.stream.Collectors;

import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.viatra.query.patternlanguage.emf.helper.PatternLanguageHelper;
import org.eclipse.viatra.query.patternlanguage.emf.vql.AggregatedValue;
import org.eclipse.viatra.query.patternlanguage.emf.vql.Annotation;
import org.eclipse.viatra.query.patternlanguage.emf.vql.AnnotationParameter;
import org.eclipse.viatra.query.patternlanguage.emf.vql.BoolValue;
import org.eclipse.viatra.query.patternlanguage.emf.vql.CheckConstraint;
import org.eclipse.viatra.query.patternlanguage.emf.vql.ClassType;
import org.eclipse.viatra.query.patternlanguage.emf.vql.ClosureType;
import org.eclipse.viatra.query.patternlanguage.emf.vql.CompareConstraint;
import org.eclipse.viatra.query.patternlanguage.emf.vql.EClassifierConstraint;
import org.eclipse.viatra.query.patternlanguage.emf.vql.EntityType;
import org.eclipse.viatra.query.patternlanguage.emf.vql.EnumValue;
import org.eclipse.viatra.query.patternlanguage.emf.vql.FunctionEvaluationValue;
import org.eclipse.viatra.query.patternlanguage.emf.vql.JavaType;
import org.eclipse.viatra.query.patternlanguage.emf.vql.ListValue;
import org.eclipse.viatra.query.patternlanguage.emf.vql.Modifiers;
import org.eclipse.viatra.query.patternlanguage.emf.vql.NumberValue;
import org.eclipse.viatra.query.patternlanguage.emf.vql.PackageImport;
import org.eclipse.viatra.query.patternlanguage.emf.vql.Parameter;
import org.eclipse.viatra.query.patternlanguage.emf.vql.PathExpressionConstraint;
import org.eclipse.viatra.query.patternlanguage.emf.vql.Pattern;
import org.eclipse.viatra.query.patternlanguage.emf.vql.PatternCall;
import org.eclipse.viatra.query.patternlanguage.emf.vql.PatternCompositionConstraint;
import org.eclipse.viatra.query.patternlanguage.emf.vql.PatternImport;
import org.eclipse.viatra.query.patternlanguage.emf.vql.ReferenceType;
import org.eclipse.viatra.query.patternlanguage.emf.vql.StringValue;
import org.eclipse.viatra.query.patternlanguage.emf.vql.TypeCheckConstraint;
import org.eclipse.viatra.query.patternlanguage.emf.vql.Variable;
import org.eclipse.viatra.query.patternlanguage.emf.vql.VariableReference;
import org.eclipse.viatra.query.patternlanguage.emf.vql.util.PatternLanguageSwitch;
import org.eclipse.xtext.nodemodel.ICompositeNode;
import org.eclipse.xtext.nodemodel.util.NodeModelUtils;
import org.eclipse.xtext.xbase.XNumberLiteral;

/**
 * Helper class to provide String representation for pattern language AST elements
 * 
 * @author Zoltan Ujhelyi
 * @since 2.0
 *
 */
public class ASTStringProvider extends PatternLanguageSwitch<String> {

    private static final String UNDEFINED = "(undefined)";
    public static final ASTStringProvider INSTANCE = new ASTStringProvider();
    
    private ASTStringProvider() {
        // Utility class constructor
    }
    
    @Override
    public String defaultCase(EObject object) {
        return object.eClass().getName();
    }

    @Override
    public String casePackageImport(PackageImport object) {
        return "EPackage import " + Optional.ofNullable(object.getEPackage()).map(EPackage::getNsURI).orElse(UNDEFINED);
    }

    @Override
    public String casePatternImport(PatternImport object) {
        return "Pattern Import " + Optional.ofNullable(object.getPattern()).map(PatternLanguageHelper::getFullyQualifiedName).orElse(UNDEFINED);
    }

    @Override
    public String caseEClassifierConstraint(EClassifierConstraint object) {
        return String.format("%s(%s)", 
                Optional.ofNullable(object.getType()).map(EntityType::getTypename).orElse(UNDEFINED),
                Optional.ofNullable(object.getVar()).map(VariableReference::getVar).orElse(UNDEFINED)
        );
    }

    @Override
    public String caseEnumValue(EnumValue object) {
        return "Enum Literal " + Optional.ofNullable(object.getLiteral()).map(literal -> literal.getEEnum().getName() + "::" + literal.getName()).orElse(UNDEFINED);
    }

    @Override
    public String caseClassType(ClassType object) {
        return "Classifier " + Optional.ofNullable(object.getClassname()).map(EClassifier::getName).orElse(UNDEFINED);
    }

    @Override
    public String caseReferenceType(ReferenceType object) {
        return "Structural Feature " + Optional.ofNullable(object.getRefname()).map(EStructuralFeature::getName).orElse(UNDEFINED);
    }

    @Override
    public String casePattern(Pattern object) {
        return "Pattern " + Optional.ofNullable(object.getName()).orElse(UNDEFINED);
    }

    @Override
    public String caseAnnotation(Annotation object) {
        return "Annotation " + Optional.ofNullable(object.getName()).orElse(UNDEFINED);
    }

    @Override
    public String caseAnnotationParameter(AnnotationParameter object) {
        return "Annotation parameter " 
                + Optional.ofNullable(object.getName()).orElse(UNDEFINED)
                + " = "
                + Optional.ofNullable(object.getValue()).map(Object::toString).orElse(UNDEFINED);
    }

    @Override
    public String caseModifiers(Modifiers object) {
        return String.format("Modifiers: %s %s", 
                    object.isPrivate() ? "private" : "public",
                    object.getExecution().toString());
    }

    @Override
    public String caseVariable(Variable object) {
        return "Variable " + Optional.ofNullable(object.getName()).orElse(UNDEFINED);
    }

    @Override
    public String caseVariableReference(VariableReference object) {
        return "Variable Reference " + Optional.ofNullable(object.getVar()).orElse(UNDEFINED);
    }

    @Override
    public String casePatternCall(PatternCall object) {
        return String.format("Pattern call %s(%s)",
                object.getPatternRef().getName(),
                object.getParameters().stream().map(this::doSwitch).collect(Collectors.joining(", "))
            );
    }

    @Override
    public String caseParameter(Parameter object) {
        return "Parameter " + object.getName();
    }

    @Override
    public String caseJavaType(JavaType object) {
        return "Java type " + Optional.ofNullable(object.getTypename()).orElse(UNDEFINED);
    }

    @Override
    public String caseTypeCheckConstraint(TypeCheckConstraint object) {
        return String.format("Type Check %s(%s)", 
                Optional.ofNullable(object.getType()).map(EntityType::getTypename).orElse(UNDEFINED),
                Optional.ofNullable(object.getVar()).map(VariableReference::getVar).orElse(UNDEFINED)
        );
    }

    @Override
    public String casePatternCompositionConstraint(PatternCompositionConstraint object) {
        String modifiers = "";
        if (object.isNegative()) {
            modifiers = "negative";
        } else if (object.getCall().getTransitive() == ClosureType.TRANSITIVE) {
            modifiers = "transitive";
        } else if (object.getCall().getTransitive() == ClosureType.REFLEXIVE_TRANSITIVE) {
            modifiers = "reflexive transitive";
        }
        return String.format("Pattern composition %s %s(%s)",
                modifiers,
                object.getCall().getPatternRef().getName(),
                object.getCall().getParameters().stream().map(this::doSwitch).collect(Collectors.joining(", "))
            );
    }

    @Override
    public String caseCompareConstraint(CompareConstraint object) {
        return String.format("Compare %s %s %s",
                    this.doSwitch(object.getLeftOperand()),
                    object.getFeature().getLiteral(),
                    this.doSwitch(object.getRightOperand())
                );
    }

    @Override
    public String caseCheckConstraint(CheckConstraint object) {
        return String.format("Check %s", originalText(object.getExpression()));
    }

    @Override
    public String casePathExpressionConstraint(PathExpressionConstraint object) {
        return String.format("Path Expression %s.%s(%s, %s)",
                object.getSourceType().getClassname().getName(),
                object.getEdgeTypes().stream().map(t -> t.getRefname().getName()).collect(Collectors.joining(".")),
                object.getSrc().getVar(),
                this.doSwitch(object.getDst())
            );
    }

    @Override
    public String caseStringValue(StringValue object) {
        return "String " + Optional.ofNullable(object.getValue()).orElse(UNDEFINED);
    }

    @Override
    public String caseNumberValue(NumberValue object) {
        return "Number " + Optional.ofNullable(object.getValue()).map(XNumberLiteral::getValue).orElse(UNDEFINED);
    }

    @Override
    public String caseBoolValue(BoolValue object) {
        return "Boolean " + Optional.ofNullable(object.getValue()).map(l -> l.isIsTrue() ? "true" : "false").orElse(UNDEFINED);
    }

    @Override
    public String caseListValue(ListValue object) {
        return object.getValues().stream().map(this::doSwitch).collect(Collectors.joining(", ", "List [", "]"));
    }

    @Override
    public String caseFunctionEvaluationValue(FunctionEvaluationValue object) {
        return String.format("Function Evaluation %s", originalText(object.getExpression()));
    }

    @Override
    public String caseAggregatedValue(AggregatedValue object) {
        return String.format("Aggregate %s %s(%s)",
                    object.getAggregator().getSimpleName(),
                    object.getCall().getPatternRef().getName(),
                    object.getCall().getParameters().stream().map(this::doSwitch).collect(Collectors.joining(", "))
                );
    }

    private String originalText(EObject object) {
        final ICompositeNode node = NodeModelUtils.getNode(object);
        return node == null ? UNDEFINED : node.getText();
    }
}
