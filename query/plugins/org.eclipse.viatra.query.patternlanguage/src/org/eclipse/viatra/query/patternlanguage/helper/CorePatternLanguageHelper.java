/*******************************************************************************
 * Copyright (c) 2010-2012, Zoltan Ujhelyi, Istvan Rath and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Zoltan Ujhelyi - initial API and implementation
 *   Andras Okros - minor changes
 *******************************************************************************/
package org.eclipse.viatra.query.patternlanguage.helper;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeSet;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.TreeIterator;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.viatra.query.patternlanguage.patternLanguage.AggregatedValue;
import org.eclipse.viatra.query.patternlanguage.patternLanguage.Annotation;
import org.eclipse.viatra.query.patternlanguage.patternLanguage.AnnotationParameter;
import org.eclipse.viatra.query.patternlanguage.patternLanguage.BoolValue;
import org.eclipse.viatra.query.patternlanguage.patternLanguage.CompareConstraint;
import org.eclipse.viatra.query.patternlanguage.patternLanguage.Constraint;
import org.eclipse.viatra.query.patternlanguage.patternLanguage.Expression;
import org.eclipse.viatra.query.patternlanguage.patternLanguage.FunctionEvaluationValue;
import org.eclipse.viatra.query.patternlanguage.patternLanguage.ListValue;
import org.eclipse.viatra.query.patternlanguage.patternLanguage.Modifiers;
import org.eclipse.viatra.query.patternlanguage.patternLanguage.NumberValue;
import org.eclipse.viatra.query.patternlanguage.patternLanguage.ParameterRef;
import org.eclipse.viatra.query.patternlanguage.patternLanguage.PathExpressionConstraint;
import org.eclipse.viatra.query.patternlanguage.patternLanguage.PathExpressionHead;
import org.eclipse.viatra.query.patternlanguage.patternLanguage.Pattern;
import org.eclipse.viatra.query.patternlanguage.patternLanguage.PatternBody;
import org.eclipse.viatra.query.patternlanguage.patternLanguage.PatternCall;
import org.eclipse.viatra.query.patternlanguage.patternLanguage.PatternCompositionConstraint;
import org.eclipse.viatra.query.patternlanguage.patternLanguage.PatternModel;
import org.eclipse.viatra.query.patternlanguage.patternLanguage.StringValue;
import org.eclipse.viatra.query.patternlanguage.patternLanguage.ValueReference;
import org.eclipse.viatra.query.patternlanguage.patternLanguage.Variable;
import org.eclipse.viatra.query.patternlanguage.patternLanguage.VariableReference;
import org.eclipse.viatra.query.patternlanguage.patternLanguage.VariableValue;
import org.eclipse.viatra.query.runtime.matchers.psystem.annotations.ParameterReference;
import org.eclipse.xtext.common.types.JvmFormalParameter;
import org.eclipse.xtext.util.OnChangeEvictingCache;
import org.eclipse.xtext.xbase.XExpression;
import org.eclipse.xtext.xbase.XFeatureCall;
import org.eclipse.xtext.xbase.XNumberLiteral;
import org.eclipse.xtext.xbase.jvmmodel.IJvmModelAssociations;
import org.eclipse.xtext.xbase.lib.Functions.Function1;
import org.eclipse.xtext.xbase.lib.IterableExtensions;
import org.eclipse.xtext.xbase.lib.Pair;
import org.eclipse.xtext.xbase.typesystem.computation.NumberLiterals;

import com.google.common.base.Function;
import com.google.common.base.Preconditions;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.base.Strings;
import com.google.common.collect.Collections2;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import com.google.common.collect.LinkedHashMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.google.inject.Provider;

public final class CorePatternLanguageHelper {

    private CorePatternLanguageHelper() {
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
     * @return the requested parameter of the pattern, or null if none exists
     * @since 0.7.0
     */
    public static Variable getParameterByName(final Pattern pattern, final String name) {
        return Iterables.find(pattern.getParameters(), new Predicate<Variable>() {

            @Override
            public boolean apply(Variable variable) {
                return name.equals(variable.getName());
            }
        }, null);
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
        Iterable<XFeatureCall> featuredCalls = (xExpression instanceof XFeatureCall) ? 
                Iterables.concat(ImmutableList.of((XFeatureCall)xExpression), Iterables.filter(contents, XFeatureCall.class))
                : Iterables.filter(contents, XFeatureCall.class);
        final Set<String> valNames = Sets.newHashSet(Iterables.transform(featuredCalls, new Function<XFeatureCall,String>() {
            @Override
            public String apply(final XFeatureCall call) {
              return call.getConcreteSyntaxFeatureName();
            }
          }));
        Iterable<Variable> calledVariables = Iterables.filter(allVariables, new Predicate<Variable>() {
            @Override
            public boolean apply(final Variable var) {
              return valNames.contains(var.getName());
            }
          });
        return IterableExtensions.sortBy(calledVariables, new Function1<Variable,String>() {
            public String apply(final Variable var) {
              return var.getName();
            }
          });
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
        return getReferencedPatternsTransitive(pattern, orderPatterns, Predicates.<Pattern>alwaysTrue());
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
     * @since 1.6
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
            return cache.get(key, pattern.eResource(), new Provider<Set<Pattern>>() {

                @Override
                public Set<Pattern> get() {
                    Set<Pattern> patterns = new HashSet<>();
                    calculateReferencedPatternsTransitive(pattern, patterns, filter);
                    return patterns;
                }
            });
        }
    }
    
    private static void calculateReferencedPatternsTransitive(Pattern pattern, Set<Pattern> addedPatterns, final Predicate<Pattern> filter) {
        Set<Pattern> candidates = Sets.filter(getReferencedPatterns(pattern), filter);
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
        public boolean apply(Annotation annotation) {
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
     * @returns the first annotation or null if no such annotation exists
     * @since 0.7.0
     */
    public static Annotation getFirstAnnotationByName(Pattern pattern, String name) {
        return Iterables.find(pattern.getAnnotations(), new AnnotationNameFilter(name), null);
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
        return Collections2.filter(pattern.getAnnotations(), new AnnotationNameFilter(name));
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
        return Collections2.transform(
                Collections2.filter(annotation.getParameters(), new Predicate<AnnotationParameter>() {
                    @Override
                    public boolean apply(AnnotationParameter parameter) {
                        Preconditions.checkArgument(parameter != null);
                        return parameter.getName().equals(parameterName);
                    }
                }), new Function<AnnotationParameter, ValueReference>() {
                    @Override
                    public ValueReference apply(AnnotationParameter parameter) {
                        Preconditions.checkArgument(parameter != null);
                        return parameter.getValue();
                    }
                });
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
            if (valueReference instanceof VariableValue) {
                resultSet.add(((VariableValue) valueReference).getValue().getVariable());
            } else if (valueReference instanceof AggregatedValue) {
                AggregatedValue aggregatedValue = (AggregatedValue) valueReference;
                for (ValueReference valueReferenceInner : aggregatedValue.getCall().getParameters()) {
                    for (Variable variable : getVariablesFromValueReference(valueReferenceInner)) {
                        resultSet.add(variable);
                    }
                }
            } else if (valueReference instanceof FunctionEvaluationValue) {
                FunctionEvaluationValue eval = (FunctionEvaluationValue) valueReference;
                final List<Variable> usedVariables =
                        CorePatternLanguageHelper.getUsedVariables(eval.getExpression(), containerPatternBody(eval).getVariables());
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
                for (ValueReference valueReference : patternCompositionConstraint.getCall().getParameters()) {
                    resultList.addAll(getUnnamedVariablesFromValueReference(valueReference, false));
                }
            } else if (constraint instanceof PathExpressionConstraint) {
                // Just from aggregated elements
                PathExpressionConstraint pathExpressionConstraint = (PathExpressionConstraint) constraint;
                PathExpressionHead pathExpressionHead = pathExpressionConstraint.getHead();
                ValueReference valueReference = pathExpressionHead.getDst();
                resultList.addAll(getUnnamedVariablesFromValueReference(valueReference, true));
            }
        }
        return resultList;
    }

    private static Set<Variable> getUnnamedVariablesFromValueReference(ValueReference valueReference,
            boolean onlyFromAggregatedValues) {
        Set<Variable> resultSet = new HashSet<>();
        if (valueReference != null) {
            if (valueReference instanceof VariableValue) {
                Variable variable = ((VariableValue) valueReference).getValue().getVariable();
                if ((variable.getName().startsWith("_") || hasAggregateReference(variable)) && !onlyFromAggregatedValues) {
                    resultSet.add(variable);
                }
            } else if (valueReference instanceof AggregatedValue) {
                AggregatedValue aggregatedValue = (AggregatedValue) valueReference;
                for (ValueReference valueReferenceInner : aggregatedValue.getCall().getParameters()) {
                    for (Variable variable : getUnnamedVariablesFromValueReference(valueReferenceInner, false)) {
                        if (variable.getName().startsWith("_") || hasAggregateReference(variable)) {
                            resultSet.add(variable);
                        }
                    }
                }
            } else if (valueReference instanceof FunctionEvaluationValue) {
                // TODO this is constant empty?
                FunctionEvaluationValue eval = (FunctionEvaluationValue) valueReference;
                final List<Variable> usedVariables =
                        CorePatternLanguageHelper.getUsedVariables(eval.getExpression(),
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
                final Object valueReference = CorePatternLanguageHelper.getValue(ref);
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
        return Sets.newHashSet(Iterables.filter(Iterables.transform(pattern.getBodies(), new Function<PatternBody, Variable>(){

            @Override
            public Variable apply(final PatternBody body) {
                return Iterables.find(body.getVariables(), new Predicate<Variable>() {

                    @Override
                    public boolean apply(Variable input) {
                        return (input instanceof ParameterRef)
                                && ((ParameterRef) input).getReferredParam().equals(variable);
                    }
                }, null);
            }}), Predicates.notNull()));
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
        return Iterables.any(var.getReferences(), new Predicate<VariableReference>() {

            @Override
            public boolean apply(VariableReference input) {
                return input != null && isAggregateReference(input);
            }
            
        });
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
}
