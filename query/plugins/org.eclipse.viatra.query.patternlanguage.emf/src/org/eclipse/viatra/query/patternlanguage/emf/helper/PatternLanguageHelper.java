/*******************************************************************************
 * Copyright (c) 2010-2012, Zoltan Ujhelyi, Andras Okros, Istvan Rath and Daniel Varro
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-v20.html.
 * 
 * SPDX-License-Identifier: EPL-2.0
 *******************************************************************************/
package org.eclipse.viatra.query.patternlanguage.emf.helper;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.TreeSet;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.TreeIterator;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.viatra.query.patternlanguage.emf.types.BottomTypeKey;
import org.eclipse.viatra.query.patternlanguage.emf.types.ITypeInferrer;
import org.eclipse.viatra.query.patternlanguage.emf.types.ITypeSystem;
import org.eclipse.viatra.query.patternlanguage.emf.vql.AggregatedValue;
import org.eclipse.viatra.query.patternlanguage.emf.vql.Annotation;
import org.eclipse.viatra.query.patternlanguage.emf.vql.AnnotationParameter;
import org.eclipse.viatra.query.patternlanguage.emf.vql.BoolValue;
import org.eclipse.viatra.query.patternlanguage.emf.vql.CallableRelation;
import org.eclipse.viatra.query.patternlanguage.emf.vql.ClosureType;
import org.eclipse.viatra.query.patternlanguage.emf.vql.CompareConstraint;
import org.eclipse.viatra.query.patternlanguage.emf.vql.Constraint;
import org.eclipse.viatra.query.patternlanguage.emf.vql.Expression;
import org.eclipse.viatra.query.patternlanguage.emf.vql.FunctionEvaluationValue;
import org.eclipse.viatra.query.patternlanguage.emf.vql.ListValue;
import org.eclipse.viatra.query.patternlanguage.emf.vql.Modifiers;
import org.eclipse.viatra.query.patternlanguage.emf.vql.NumberValue;
import org.eclipse.viatra.query.patternlanguage.emf.vql.PackageImport;
import org.eclipse.viatra.query.patternlanguage.emf.vql.Parameter;
import org.eclipse.viatra.query.patternlanguage.emf.vql.ParameterRef;
import org.eclipse.viatra.query.patternlanguage.emf.vql.PathExpressionConstraint;
import org.eclipse.viatra.query.patternlanguage.emf.vql.Pattern;
import org.eclipse.viatra.query.patternlanguage.emf.vql.PatternBody;
import org.eclipse.viatra.query.patternlanguage.emf.vql.PatternCall;
import org.eclipse.viatra.query.patternlanguage.emf.vql.PatternCompositionConstraint;
import org.eclipse.viatra.query.patternlanguage.emf.vql.PatternModel;
import org.eclipse.viatra.query.patternlanguage.emf.vql.ReferenceType;
import org.eclipse.viatra.query.patternlanguage.emf.vql.StringValue;
import org.eclipse.viatra.query.patternlanguage.emf.vql.UnaryTypeConstraint;
import org.eclipse.viatra.query.patternlanguage.emf.vql.VQLImportSection;
import org.eclipse.viatra.query.patternlanguage.emf.vql.ValueReference;
import org.eclipse.viatra.query.patternlanguage.emf.vql.Variable;
import org.eclipse.viatra.query.patternlanguage.emf.vql.VariableReference;
import org.eclipse.viatra.query.runtime.matchers.context.IInputKey;
import org.eclipse.viatra.query.runtime.matchers.psystem.annotations.ParameterReference;
import org.eclipse.viatra.query.runtime.matchers.psystem.queries.PVisibility;
import org.eclipse.viatra.query.runtime.matchers.util.Preconditions;
import org.eclipse.xtext.common.types.JvmFormalParameter;
import org.eclipse.xtext.util.OnChangeEvictingCache;
import org.eclipse.xtext.xbase.XExpression;
import org.eclipse.xtext.xbase.XFeatureCall;
import org.eclipse.xtext.xbase.XNumberLiteral;
import org.eclipse.xtext.xbase.jvmmodel.IJvmModelAssociations;
import org.eclipse.xtext.xbase.lib.IterableExtensions;
import org.eclipse.xtext.xbase.lib.Pair;
import org.eclipse.xtext.xbase.typesystem.computation.NumberLiterals;

import com.google.common.base.Strings;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import com.google.common.collect.LinkedHashMultimap;
import com.google.common.collect.Lists;

/**
 * @since 2.0
 */
public final class PatternLanguageHelper {

    private PatternLanguageHelper() {
    }

    /**
     * @since 1.4
     */
    public static final String AGGREGATE_VARIABLE_PREFIX = "#";
    
    /**
     * Returns the name of the container package of the selected pattern
     * @return a name of the pattern; never null, but may be empty string
     */
    public static String getPackageName(Pattern pattern) {
        if(pattern == null || pattern.eIsProxy()) {
            return "";
        }
        PatternModel patternModel = (PatternModel) pattern.eContainer();

        return (patternModel == null) ? null : patternModel.getPackageName();
    }
    
    /**
     * Returns the name of the pattern, qualified by package name.
     */
    public static String getFullyQualifiedName(Pattern pattern) {
        if (pattern == null) {
            return "";
        }
        String packageName = getPackageName(pattern);
        if (packageName == null || packageName.isEmpty()) {
            return pattern.getName();
        } else {
            return packageName + "." + pattern.getName();
        }
        // TODO ("local pattern?")
    }

    /**
     * @param pattern
     * @return true if the pattern has a private modifier, false otherwise.
     */
    public static boolean isPrivate(Pattern pattern) {
        Modifiers mod = pattern.getModifiers();
        if (mod != null){
            return mod.isPrivate();
        }
        return false;
    }
    
    /**
     * @since 2.0
     */
    public static PVisibility calculatePVisibility(Pattern pattern) {
        Modifiers mod = pattern.getModifiers();
        if (mod != null && mod.isPrivate()){
            return PVisibility.PRIVATE;
        }
        return PVisibility.PUBLIC;
    }

    
    /**
     * @param pattern
     * @return true if the pattern contains xbase check() or eval() expressions, false otherwise.
     */
    public static boolean hasXBaseExpression(Pattern pattern) {
        final TreeIterator<EObject> eAllContents = pattern.eAllContents();
        while (eAllContents.hasNext()) {
            if (eAllContents.next() instanceof XExpression)
                return true;
        }
        return false;
    }

    /**
     * @return all xbase check() or eval() expressions in the pattern
     */
    public static Collection<XExpression> getAllTopLevelXBaseExpressions(EObject patternOrBody) {
        final List<XExpression> result = new ArrayList<>();
        final TreeIterator<EObject> eAllContents = patternOrBody.eAllContents();
        while (eAllContents.hasNext()) {
            final EObject content = eAllContents.next();
            if (content instanceof XExpression) {
                result.add((XExpression) content);
                // do not include subexpressions
                eAllContents.prune();
            }
        }
        return result;
    }

    /**
     * Returns the parameter of a pattern by name
     *
     * @param pattern
     * @param name
     * @return the requested parameter of the pattern if exists
     * @since 2.0
     */
    public static Optional<Variable> getParameterByName(final Pattern pattern, final String name) {
        return pattern.getParameters().stream().filter(variable -> name.equals(variable.getName())).findAny();
    }

    /** Compiles a map for name-based lookup of symbolic parameter positions. */
    public static Map<String, Integer> getParameterPositionsByName(Pattern pattern) {
        EList<Variable> parameters = pattern.getParameters();
        Map<String, Integer> posMapping = new HashMap<>();
        int parameterPosition = 0;
        for (Variable parameter : parameters) {
            posMapping.put(parameter.getName(), parameterPosition++);
        }
        return posMapping;
    }

    /**
     * Finds all pattern variables referenced from the given XExpression. </p>
     * <p>
     * <strong>Warning</strong> This method cannot be used in JvmModelInferrer,
     * as that is used to set up the list of available local variables.
     */
    public static Set<Variable> getReferencedPatternVariablesOfXExpression(XExpression xExpression, IJvmModelAssociations associations) {
        Set<Variable> result = new HashSet<>();
        if (xExpression != null) {
            collectVariableFromExpression(xExpression, associations, result, xExpression);
            TreeIterator<EObject> eAllContents = xExpression.eAllContents();
            while (eAllContents.hasNext()) {
                EObject expression = eAllContents.next();
                collectVariableFromExpression(xExpression, associations, result, expression);
            }
        }
        return result;
    }

    private static void collectVariableFromExpression(XExpression xExpression, IJvmModelAssociations associations,
            Set<Variable> result, EObject expression) {
        EList<EObject> eCrossReferences = expression.eCrossReferences();
        for (EObject eObject : eCrossReferences) {
            if (eObject instanceof JvmFormalParameter && !EcoreUtil.isAncestor(xExpression, eObject)) {
                for (EObject obj : associations.getSourceElements(eObject)) {
                    if (obj instanceof Variable) {
                    result.add((Variable) obj);
                    }
                }
            }
        }
    }

    public static List<Variable> getUsedVariables(XExpression xExpression, Iterable<Variable> allVariables){
        if (xExpression == null) return Collections.emptyList();
        List<EObject> contents = Lists.newArrayList(xExpression.eAllContents());
        Stream<XFeatureCall> contentStream = contents.stream().filter(XFeatureCall.class::isInstance).map(XFeatureCall.class::cast);
        Stream<XFeatureCall> featuredCalls = (xExpression instanceof XFeatureCall) 
                ? Stream.concat(Stream.of((XFeatureCall)xExpression), contentStream)
                : contentStream;
        final Set<String> valNames = featuredCalls.map(XFeatureCall::getConcreteSyntaxFeatureName).collect(Collectors.toSet()); 
        Iterable<Variable> calledVariables = Iterables.filter(allVariables, var -> valNames.contains(var.getName()));
        return IterableExtensions.sortBy(calledVariables, Variable::getName);
    }



    /** Finds all patterns referenced from the given pattern. */
    public static Set<Pattern> getReferencedPatterns(Pattern sourcePattern) {
        Set<Pattern> result = new HashSet<>();
        TreeIterator<EObject> eAllContents = sourcePattern.eAllContents();
        while (eAllContents.hasNext()) {
            EObject element = eAllContents.next();
            if (element instanceof PatternCall) {
                PatternCall call = (PatternCall) element;
                final Pattern patternRef = call.getPatternRef();
                if (patternRef != null && !patternRef.eIsProxy()) {
                    result.add(patternRef);
                }
            }
        }
        return result;
    }

    private static class CallComparator implements Comparator<Pattern> {

        private Predicate<Pattern> filter;
        
        private CallComparator(Predicate<Pattern> filter) {
            this.filter = filter;
        }
        
        public int compare(Pattern left, Pattern right) {
            boolean rightCalled = doGetReferencedPatternsTransitively(left, filter).contains(right);
            boolean leftCalled = doGetReferencedPatternsTransitively(right, filter).contains(left);
            
            if (leftCalled && !rightCalled) {
                return -1;
            } else if (!leftCalled && rightCalled) {
                return +1;
            } else {
                return getFullyQualifiedName(left).compareTo(getFullyQualifiedName(right));
            }
            
          }
    }

    /**
     * This method returns a set of patterns that are reachable from the selected pattern through various pattern
     * composition calls. The patterns in the returned set not ordered.
     * 
     * @param pattern the source pattern
     * @since 1.4
     */
    public static Set<Pattern> getReferencedPatternsTransitive(Pattern pattern) {
        return getReferencedPatternsTransitive(pattern, false);
    }
    
    /**
     * This method returns a set of patterns that are reachable from the selected pattern through various pattern
     * composition calls.
     * 
     * @param pattern the source pattern
     * @param orderPatterns if true, the returned set will be ordered based on the call edges
     * @since 1.4
     */
    public static Set<Pattern> getReferencedPatternsTransitive(Pattern pattern, boolean orderPatterns) {
        return getReferencedPatternsTransitive(pattern, orderPatterns, i -> true);
    }
    
    /**
     * This method returns a set of patterns that are reachable from the selected pattern through various pattern
     * composition calls, while each called element fulfills the filter predicate. If a pattern does not match the
     * filter predicate, both the pattern and all patterns called by it will be absent from the returned set of the
     * nodes.
     * 
     * @param pattern the source pattern
     * @param orderPatterns if true, the returned set will be ordered based on the call edges
     * @param filter the filter predicate
     * @since 2.0
     */
    public static Set<Pattern> getReferencedPatternsTransitive(Pattern pattern, boolean orderPatterns, Predicate<Pattern> filter) {
        Set<Pattern> referencedPatterns = null;
        if (orderPatterns) {
            referencedPatterns = new TreeSet<>(new CallComparator(filter));
        } else {
            referencedPatterns = new HashSet<>();
        }
        referencedPatterns.addAll(doGetReferencedPatternsTransitively(pattern, filter));
        return referencedPatterns;
    }

    private static OnChangeEvictingCache cache = new OnChangeEvictingCache();
    
    private static Set<Pattern> doGetReferencedPatternsTransitively(final Pattern pattern, final Predicate<Pattern> filter) {
        if (pattern.eResource() == null) {
            Set<Pattern> patterns = new HashSet<>();
            calculateReferencedPatternsTransitive(pattern, patterns, filter);
            return patterns;
        } else {
            Pair<Pattern, Predicate<Pattern>> key = new Pair<>(pattern, filter);
            return cache.get(key, pattern.eResource(), () -> calculateReferencedPatternsTransitive(pattern, filter));
        }
    }
    
    private static Set<Pattern> calculateReferencedPatternsTransitive(Pattern pattern, final Predicate<Pattern> filter) {
        Set<Pattern> patterns = new HashSet<>();
        calculateReferencedPatternsTransitive(pattern, patterns, filter);
        return patterns;
    }
    
    private static void calculateReferencedPatternsTransitive(Pattern pattern, Set<Pattern> addedPatterns, final Predicate<Pattern> filter) {
        Set<Pattern> candidates = getReferencedPatterns(pattern).stream().filter(filter).collect(Collectors.toSet());
        candidates.removeAll(addedPatterns);
        addedPatterns.addAll(candidates);
        for (Pattern newCandidate : candidates) {
            calculateReferencedPatternsTransitive(newCandidate, addedPatterns, filter);
        }
    }

    private static class AnnotationNameFilter implements Predicate<Annotation> {

        private final String name;

        public AnnotationNameFilter(String name) {
            this.name = name;
        }

        @Override
        public boolean test(Annotation annotation) {
            return name.equals(annotation.getName());
        }
    }

    /**
     * Returns the first annotation of a given name from a pattern. This method ignores multiple defined annotations by
     * the same name. For getting a filtered collections of annotations, see
     * {@link #getAnnotationsByName(Pattern, String)}
     *
     * @param pattern
     *            the pattern instance
     * @param name
     *            the name of the annotation to return
     * @returns the first annotation if exists
     * @since 2.0
     */
    public static Optional<Annotation> getFirstAnnotationByName(Pattern pattern, String name) {
        return pattern.getAnnotations().stream().filter(new AnnotationNameFilter(name)).findFirst();
    }

    /**
     * Returns the collection of annotations of a pattern by a name. For getting the first annotations by name, see
     * {@link #getAnnotationByName(Pattern, String)}
     *
     * @param pattern
     *            the pattern instance
     * @param name
     *            the name of the annotation to return
     * @returns a non-null, but possibly empty collection of annotations
     * @since 0.7.0
     */
    public static Collection<Annotation> getAnnotationsByName(Pattern pattern, String name) {
        return pattern.getAnnotations().stream().filter(new AnnotationNameFilter(name)).collect(Collectors.toList());
    }

    /**
     * Returns all annotation parameters with a selected name
     *
     * @param annotation
     * @param parameterName
     * @return a lazy collection of annotation parameters with the selected name. May be empty, but is never null.
     */
    public static Collection<ValueReference> getAnnotationParameters(final Annotation annotation,
            final String parameterName) {
        return annotation.getParameters().stream()
            .filter(Objects::nonNull)
            .filter(parameter -> parameter.getName().equals(parameterName))
            .map(AnnotationParameter::getValue)
            .collect(Collectors.toList());
    }

    /**
     * Returns the first annotation parameter with a selected name.
     *
     * @param annotation
     * @param parameterName
     * @return the annotation with the selected name, or null if no such annotation exists.
     */
    public static ValueReference getFirstAnnotationParameter(final Annotation annotation, final String parameterName) {
        Collection<ValueReference> parameters = getAnnotationParameters(annotation, parameterName);
        return (!parameters.isEmpty()) ? parameters.iterator().next() : null;
    }
    
    /**
     * Returns the value of the first Boolean annotation parameter with the given name. If the parameter with the given name
     * is not Boolean or is not defined then the default value is returned.
     *  
     * @param annotation
     * @param parameterName
     * @param defaultValue
     * @return the value of the first boolean parameter with the given name, otherwise defaultValue
     */
    public static boolean getValueOfFirstBooleanAnnotationParameter(Annotation annotation, String parameterName, boolean defaultValue){
        ValueReference useAsSurrogateRef = getFirstAnnotationParameter(annotation,parameterName);
        if(useAsSurrogateRef != null){
            if(useAsSurrogateRef instanceof BoolValue){
                return getValue(useAsSurrogateRef, Boolean.class);
            } else {
                return defaultValue;
            }
        } else {
            return defaultValue;
        }
    }

    /**
     * @param valueReference
     * @return all variables from the ValueReference object. (Either referenced directly, or referenced throught an
     *         AggregatedValue.)
     */
    public static Set<Variable> getVariablesFromValueReference(ValueReference valueReference) {
        Set<Variable> resultSet = new HashSet<>();
        if (valueReference != null) {
            if (valueReference instanceof VariableReference) {
                resultSet.add(((VariableReference) valueReference).getVariable());
            } else if (valueReference instanceof AggregatedValue) {
                AggregatedValue aggregatedValue = (AggregatedValue) valueReference;
                for (ValueReference valueReferenceInner : getCallParameters(aggregatedValue.getCall())) {
                    for (Variable variable : getVariablesFromValueReference(valueReferenceInner)) {
                        resultSet.add(variable);
                    }
                }
            } else if (valueReference instanceof FunctionEvaluationValue) {
                FunctionEvaluationValue eval = (FunctionEvaluationValue) valueReference;
                final List<Variable> usedVariables =
                        getUsedVariables(eval.getExpression(), containerPatternBody(eval).getVariables());
                resultSet.addAll(usedVariables);
            }
        }
        return resultSet;
    }

    /**
     * @return the pattern body that contains the value reference
     */
    public static PatternBody containerPatternBody(ValueReference val) {
        for (EObject cursor = val; cursor!=null; cursor = cursor.eContainer())
            if (cursor instanceof PatternBody)
                return (PatternBody) cursor;
        // cursor == null --> not contained in PatternBody
        throw new IllegalArgumentException(
                String.format(
                        "Misplaced value reference %s not contained in any pattern body",
                        val));
    }

    /**
     * @param patternBody
     * @return A list of variables, which are running/unnamed variables in the pattern body. These variables' name
     *         starts with the "_" prefix, and can be found in find, count find calls.
     */
    public static List<Variable> getUnnamedRunningVariables(PatternBody patternBody) {
        List<Variable> resultList = new ArrayList<>();
        for (Constraint constraint : patternBody.getConstraints()) {
            if (constraint instanceof CompareConstraint) {
                // Just from aggregated elements
                CompareConstraint compareConstraint = (CompareConstraint) constraint;
                ValueReference leftValueReference = compareConstraint.getLeftOperand();
                ValueReference rightValueReference = compareConstraint.getRightOperand();
                resultList.addAll(getUnnamedVariablesFromValueReference(leftValueReference, true));
                resultList.addAll(getUnnamedVariablesFromValueReference(rightValueReference, true));
            } else if (constraint instanceof PatternCompositionConstraint) {
                // All from here, aggregates and normal running variables
                PatternCompositionConstraint patternCompositionConstraint = (PatternCompositionConstraint) constraint;
                for (ValueReference valueReference : getCallParameters(patternCompositionConstraint.getCall())) {
                    resultList.addAll(getUnnamedVariablesFromValueReference(valueReference, false));
                }
            } else if (constraint instanceof PathExpressionConstraint) {
                // Just from aggregated elements
                PathExpressionConstraint pathExpressionConstraint = (PathExpressionConstraint) constraint;
                ValueReference valueReference = pathExpressionConstraint.getDst();
                resultList.addAll(getUnnamedVariablesFromValueReference(valueReference, true));
            }
        }
        return resultList;
    }

    private static Set<Variable> getUnnamedVariablesFromValueReference(ValueReference valueReference,
            boolean onlyFromAggregatedValues) {
        Set<Variable> resultSet = new HashSet<>();
        if (valueReference != null) {
            if (valueReference instanceof VariableReference) {
                Variable variable = ((VariableReference) valueReference).getVariable();
                if ((variable.getName().startsWith("_") || hasAggregateReference(variable)) && !onlyFromAggregatedValues) {
                    resultSet.add(variable);
                }
            } else if (valueReference instanceof AggregatedValue) {
                AggregatedValue aggregatedValue = (AggregatedValue) valueReference;
                for (ValueReference valueReferenceInner : getCallParameters(aggregatedValue.getCall())) {
                    for (Variable variable : getUnnamedVariablesFromValueReference(valueReferenceInner, false)) {
                        if (variable.getName().startsWith("_") || hasAggregateReference(variable)) {
                            resultSet.add(variable);
                        }
                    }
                }
            } else if (valueReference instanceof FunctionEvaluationValue) {
                FunctionEvaluationValue eval = (FunctionEvaluationValue) valueReference;
                final List<Variable> usedVariables =
                        getUsedVariables(eval.getExpression(),
                                containerPatternBody(eval).getVariables());
                if (!onlyFromAggregatedValues) {
                    for (Variable variable : usedVariables) {
                        // XXX can this ever be true?
                        if (variable.getName().startsWith("_")) {
                            resultSet.add(variable);
                        }
                    }
                }
            }
        }
        return resultSet;
    }


    /**
     * Retains all parameters, even those with duplicate names.
     * Parameter values of the same name are traversible in their original order.
     * @since 1.5
     */
    public static LinkedHashMultimap<String, Object> evaluateAnnotationParametersWithMultiplicity(Annotation annotation) {
        LinkedHashMultimap<String, Object> result = LinkedHashMultimap.create();
        for (AnnotationParameter param : annotation.getParameters()) {
            String parameterName = param.getName();
            ValueReference ref = param.getValue();
            if (ref != null) {
                final Object valueReference = getValue(ref);
                if (!Strings.isNullOrEmpty(parameterName) && valueReference != null) {
                    result.put(parameterName, valueReference);
                }
            }
        }
        return result;
    }
    /**
     * Retains the first parameter of each parameter name.
     */
    public static Map<String, Object> evaluateAnnotationParameters(Annotation annotation) {
       Map<String, Object> result = new HashMap<>();
       for (Entry<String, Object> entry : evaluateAnnotationParametersWithMultiplicity(annotation).entries()) {
           if (! result.containsKey(entry.getKey()))
               result.put(entry.getKey(), entry.getValue());
       }
       return result;
    }

    /**
     * Extracts the value stored by a selected reference
     * 
     * @param ref
     *            The value reference to extract the value from
     * @param clazz
     *            The class to cast the results; if the value of the reference cannot be expressed with this class, an
     *            {@link IllegalArgumentException} is thrown.
     * @since 1.5
     */
    @SuppressWarnings("unchecked")
    public static <T> T getValue(ValueReference ref, Class<T> clazz) {
        Object value = getValue(ref);
        Preconditions.checkArgument(clazz.isInstance(value),
                "Value reference %s does not refer to a class %s", ref.getClass().getName(), clazz.getName());
        return (T) value;
    }
    
    
    
    @SuppressWarnings("restriction")
    private static Object getValue(ValueReference ref) {
        Object value = null;
        if (ref instanceof BoolValue) {
            value = ((BoolValue)ref).getValue().isIsTrue();
        } else if (ref instanceof NumberValue) {
            NumberLiterals literals = new NumberLiterals();
            XNumberLiteral xLiteral = ((NumberValue)ref).getValue();
            value = literals.numberValue(xLiteral, literals.getJavaType(xLiteral));
        } else if (ref instanceof StringValue) {
            value = ((StringValue)ref).getValue();
        } else if (ref instanceof VariableReference) {
            value = new ParameterReference(((VariableReference) ref).getVar());
        } else if (ref instanceof ListValue) {
            value = ((ListValue) ref).getValues().stream().map(PatternLanguageHelper::getValue).collect(Collectors.toList());
        } else {
            throw new UnsupportedOperationException("Unknown attribute parameter type");
        }
        return value;
    }

    /**
     * @since 1.3
     */
    public static boolean isParameter(Expression ex) {
        return (ex instanceof Variable) && (ex.eContainer() instanceof Pattern); 
    }
    
    /**
     * @since 1.3
     */
    public static Set<Variable> getLocalReferencesOfParameter(final Variable variable) {
        Preconditions.checkArgument(isParameter(variable), "Variable must represent a pattern parameter.");
        Pattern pattern = (Pattern) variable.eContainer();
        
        return pattern.getBodies().stream().map(body -> body.getVariables().stream()
                        .filter(ParameterRef.class::isInstance)
                        .map(ParameterRef.class::cast)
                        .filter(input -> input.getReferredParam().equals(variable)))
                .flatMap(i -> i)
                .collect(Collectors.toSet());
    }
    
    /**
     * Returns whether a variable reference is an aggregate reference (e.g. is started in the grammar with a '#' symbol).
     * @since 1.4
     */
    public static boolean isAggregateReference(VariableReference reference) {
        return reference.isAggregator(); 
    }
    
    /**
     * Returns whether a variable has an aggregate reference.
     * @since 1.4
     */
    public static boolean hasAggregateReference(Variable var) {
        return getReferences(var).anyMatch(input -> input != null && isAggregateReference(input));
    }
    
    /**
     * Returns a stream of all references from a given variable
     * @since 2.0
     */
    public static Stream<VariableReference> getReferences(Variable var) {
        Collection<PatternBody> bodies = null;
        if (var instanceof Parameter) {
            // Parameters are defined in patterns
            bodies = ((Pattern) var.eContainer()).getBodies();
        } else {
            // Local variables are defined in pattern bodies
            bodies = Collections.singleton((PatternBody)var.eContainer());
        }
        return bodies.stream().flatMap(b -> 
                StreamSupport.stream(Spliterators.spliteratorUnknownSize(b.eAllContents(), Spliterator.DISTINCT | Spliterator.SORTED), false)
                .filter(VariableReference.class::isInstance)
                .map(VariableReference.class::cast)
                .filter(ref -> Objects.equals(var, ref.getVariable())));
    }
    
    /**
     * @return true if the variable is single-use a named variable
     * @since 1.6
     */
    public static boolean isNamedSingleUse(Variable variable) {
        String name = variable.getName();
        return hasAggregateReference(variable) || (name != null && name.startsWith("_") && !name.contains("<"));
    }

    /**
     * @return true if the variable is an unnamed single-use variable
     * @since 1.6
     */
    public static boolean isUnnamedSingleUseVariable(Variable variable) {
        String name = variable.getName();
        return name != null && name.startsWith("_") && name.contains("<");
    }
    
    /**
     * @since 1.7
     */
    public static String getModelFileName(EObject object) {
        Resource eResource = object.eResource();
        if (eResource != null) {
            return eResource.getURI().trimFileExtension().lastSegment();
        } else {
            return "";
        }
    }
    
    /**
     * Initializes a new list of package imports defined in a selected pattern model
     * 
     * @param model
     * @since 2.0
     */
    public static List<PackageImport> getAllPackageImports(PatternModel model) {
        return Lists.newArrayList(getPackageImportsIterable(model));
    }

    /**
     * Returns an iterable of package imports in a selected pattern model. If an import package is an unresolvable
     * proxy, it is omitted.
     * @since 2.0
     */
    public static Iterable<PackageImport> getPackageImportsIterable(PatternModel model) {
        VQLImportSection imports = model.getImportPackages();
        if (imports == null) {
            return ImmutableList.of();
        }
        return Iterables.filter(imports.getPackageImport(), pImport -> !pImport.eIsProxy());
    }
    
    /**
     * Returns an iterable of imported EPackages in a selected pattern model. If an import package is an unresolvable
     * proxy, it is omitted.
     *
     * @since 2.0
     */
    public static Iterable<EPackage> getEPackageImportsIterable(PatternModel model) {
        return Iterables.transform(getPackageImportsIterable(model), PackageImport::getEPackage);
    }
    
    public static Optional<ReferenceType> getPathExpressionTailType(PathExpressionConstraint expression) {
        return Optional.ofNullable(expression.getEdgeTypes())
                .map(types -> types.get(types.size() - 1));
    }
               
    public static Optional<EClassifier> getPathExpressionEMFTailType(PathExpressionConstraint expression) {
        return getPathExpressionTailType(expression)
                .map(ReferenceType::getRefname)
                .map(EStructuralFeature::getEType);
        
    }
    
    /**
     * Extracts the parameters from a given callable relation
     * @since 2.0
     */
    public static List<ValueReference> getCallParameters(CallableRelation relation) {
        if (relation instanceof PatternCall) {
            return ((PatternCall) relation).getParameters();
        } else if (relation instanceof UnaryTypeConstraint) {
            return Collections.singletonList(((UnaryTypeConstraint) relation).getVar());
        } else if (relation instanceof PathExpressionConstraint) {
            PathExpressionConstraint constraint = (PathExpressionConstraint) relation;
            List<ValueReference> parameters = new ArrayList<ValueReference>();
            parameters.add(constraint.getSrc());
            parameters.add(constraint.getDst());
            return parameters;
        } else {
            throw new IllegalArgumentException("Unknown relation type " + relation.eClass().getName());
        }
    }
    
    /**
     * Returns the parameters required to call the mentioned patterns with their corresponding types. In case of
     * embedded subpatterns, duplicate parameters are removed.
     * 
     * @param relation
     * @since 2.0
     */
    public static LinkedHashMap<String, IInputKey> getParameterVariables(CallableRelation relation, ITypeSystem typeSystem, ITypeInferrer typeInferrer) {
        LinkedHashMap<String, IInputKey> variableMap = new LinkedHashMap<>();
        
        List<IInputKey> expectedTypes = calculateExpectedTypes(relation, typeSystem, typeInferrer);
                
        for (int i=0; i < expectedTypes.size(); i++) {
            variableMap.put("p" + Integer.toString(i), expectedTypes.get(i));
        }
        return variableMap;
    }
    
    /**
     * Returns the list of expected types for a given callable relation
     * @since 2.0
     */
    public static List<IInputKey> calculateExpectedTypes(CallableRelation relation, ITypeSystem typeSystem, ITypeInferrer typeInferrer) {
        if (relation instanceof PatternCall) {
            List<Variable> patternParameters = ((PatternCall)relation).getPatternRef().getParameters();
            return patternParameters.stream().map(typeInferrer::getType).collect(Collectors.toList());
        } else if (relation instanceof UnaryTypeConstraint) {
            return Collections.singletonList(typeSystem.extractTypeDescriptor(((UnaryTypeConstraint) relation).getType()));
        } else if (relation instanceof PathExpressionConstraint) {
            PathExpressionConstraint constraint = (PathExpressionConstraint) relation;
            final EList<ReferenceType> edges = constraint.getEdgeTypes();
            
            List<IInputKey> types = new ArrayList<>();
            types.add(typeSystem.extractTypeDescriptor(constraint.getSourceType()));
            types.add(edges.isEmpty() ? BottomTypeKey.INSTANCE : typeSystem.extractColumnDescriptor(edges.get(edges.size() - 1), 1));
            return types;
        } else {
            throw new IllegalArgumentException("Unknown relation type");
        }
    }
    
    /**
     * @return true if a given call is negated, aggregated or transitive closure is calculated; false otherwise
     * @since 2.0
     */
    public static boolean isNonSimpleConstraint(CallableRelation constraint) {
        return isNegative(constraint) || constraint.eContainer() instanceof AggregatedValue || isTransitive(constraint);

    }
    
    /**
     * Decides whether a call is negative
     * @since 2.0
     */
    public static boolean isNegative(CallableRelation call) {
        return call.eContainer() instanceof PatternCompositionConstraint && ((PatternCompositionConstraint)call.eContainer()).isNegative();
    }
    
    /**
     * Decides whether a call is transitive or reflexive transitive
     * @since 2.0
     */
    public static boolean isTransitive(CallableRelation call) {
        return call.getTransitive() == ClosureType.REFLEXIVE_TRANSITIVE || call.getTransitive() == ClosureType.TRANSITIVE;
    }
}
