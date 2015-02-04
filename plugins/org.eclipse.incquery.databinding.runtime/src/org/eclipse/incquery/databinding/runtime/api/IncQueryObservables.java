/*******************************************************************************
 * Copyright (c) 2010-2012, Abel Hegedus, Istvan Rath and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Abel Hegedus - initial API and implementation
 *******************************************************************************/
package org.eclipse.incquery.databinding.runtime.api;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.eclipse.core.databinding.observable.Observables;
import org.eclipse.core.databinding.observable.list.IObservableList;
import org.eclipse.core.databinding.observable.set.IObservableSet;
import org.eclipse.core.databinding.observable.value.ComputedValue;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.core.databinding.observable.value.IValueChangeListener;
import org.eclipse.emf.databinding.EMFProperties;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.incquery.databinding.runtime.adapter.DatabindingAdapter;
import org.eclipse.incquery.databinding.runtime.adapter.GenericDatabindingAdapter;
import org.eclipse.incquery.databinding.runtime.adapter.ObservableDefinition;
import org.eclipse.incquery.databinding.runtime.adapter.ObservableDefinition.ObservableType;
import org.eclipse.incquery.databinding.runtime.collection.ObservablePatternMatchCollectionBuilder;
import org.eclipse.incquery.databinding.runtime.observables.ObservableLabelFeature;
import org.eclipse.incquery.runtime.api.IPatternMatch;
import org.eclipse.incquery.runtime.api.IQuerySpecification;
import org.eclipse.incquery.runtime.api.IncQueryEngine;
import org.eclipse.incquery.runtime.api.IncQueryMatcher;
import org.eclipse.incquery.runtime.emf.helper.IncQueryRuntimeHelper;
import org.eclipse.incquery.runtime.matchers.psystem.annotations.PAnnotation;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

/**
 * Utility class for observing EMF-IncQuery related objects, such as match sets, match parameters.
 *
 * @author Abel Hegedus
 *
 */
public final class IncQueryObservables {

    public static final String OBSERVABLEVALUE_ANNOTATION = "ObservableValue";
    /**
     * Hidden constructor for utility class
     */
    private IncQueryObservables() {

    }

    /**
     * Create an observable list of the match set of the given {@link IncQueryMatcher}.
     *
     * <p>
     * The matches are ordered by appearance, so a new match is always put on the end of the list.
     *
     * @param matcher
     *            the matcher to observe
     * @return an observable list of matches
     */
    public static <Match extends IPatternMatch, Matcher extends IncQueryMatcher<Match>> IObservableList observeMatchesAsList(
            Matcher matcher) {
        return ObservablePatternMatchCollectionBuilder.create(matcher).buildList();
    }

    /**
     * Create an observable list of the match set of the given query using a selected {@link IncQueryEngine}.
     *
     * <p>
     * The matches are ordered by appearance, so a new match is always put on the end of the list.
     *
     * <p>
     * Use the generated query specifications for initialization, in the generic case, you may have to accept an unchecked
     * invocation (or use the Generic classes if you are sure).
     *
     * @param querySpecification
     *            the matcher querySpecification for the query to observe
     * @param engine
     *            the engine used with the matcher
     * @return an observable list of matches
     */
    public static <Match extends IPatternMatch, Matcher extends IncQueryMatcher<Match>> IObservableList observeMatchesAsList(
            IQuerySpecification<Matcher> querySpecification, IncQueryEngine engine) {
        return ObservablePatternMatchCollectionBuilder.create(querySpecification).setEngine(engine).buildList();
    }

    /**
     * Create an observable list of the match set of the given query using a selected {@link IncQueryEngine}.
     *
     * <p>
     * The matches are ordered by appearance, so a new match is always put on the end of the list.
     *
     * <p>
     * Use the generated query specifications for initialization, in the generic case, you may have to accept an unchecked
     * invocation (or use the Generic classes if you are sure).
     *
     * @param querySpecification
     *            the matcher querySpecification for the query to observe
     * @param engine
     *            the engine used with the matcher
     * @param filter the partial match to be used as filter
     * @return an observable list of matches
     */
    public static <Match extends IPatternMatch, Matcher extends IncQueryMatcher<Match>> IObservableList observeMatchesAsList(
            IQuerySpecification<Matcher> querySpecification, IncQueryEngine engine, Match filter) {
        return ObservablePatternMatchCollectionBuilder.create(querySpecification).setFilter(filter).setEngine(engine)
                .buildList();
    }

    /**
     * Create an observable set of the match set of the given {@link IncQueryMatcher}.
     *
     * @param matcher
     *            the matcher to observe
     * @return an observable list of matches
     */
    public static <Match extends IPatternMatch, Matcher extends IncQueryMatcher<Match>> IObservableSet observeMatchesAsSet(
            Matcher matcher) {
        return ObservablePatternMatchCollectionBuilder.create(matcher).buildSet();
    }

    /**
     * Create an observable set of the match set of the given query using a selected {@link IncQueryEngine}.
     *
     * <p>
     * Use the generated query specifications for initialization, in the generic case, you may have to accept an unchecked
     * invocation (or use the Generic classes if you are sure).
     *
     * @param querySpecification
     *            the matcher querySpecification for the query to observe
     * @param engine
     *            the engine used with the matcher
     * @return an observable set of matches
     */
    public static <Match extends IPatternMatch, Matcher extends IncQueryMatcher<Match>> IObservableSet observeMatchesAsSet(
            IQuerySpecification<Matcher> querySpecification, IncQueryEngine engine) {
        return ObservablePatternMatchCollectionBuilder.create(querySpecification).setEngine(engine).buildSet();
    }

    /**
     * Create an observable set of the match set of the given query using a selected {@link IncQueryEngine}.
     *
     * <p>
     * Use the generated query specifications for initialization, in the generic case, you may have to accept an unchecked
     * invocation (or use the Generic classes if you are sure).
     *
     * @param querySpecification
     *            the matcher querySpecification for the query to observe
     * @param engine
     *            the engine used with the matcher
     * @param filter the partial match to be used as filter
     * @return an observable set of matches
     */
    public static <Match extends IPatternMatch, Matcher extends IncQueryMatcher<Match>> IObservableSet observeMatchesAsSet(
            IQuerySpecification<Matcher> querySpecification, IncQueryEngine engine, Match filter) {
        return ObservablePatternMatchCollectionBuilder.create(querySpecification).setFilter(filter).setEngine(engine).buildSet();
    }

    /**
     * Registers the given changeListener for the appropriate features of the given signature. The features will be
     * computed based on the message parameter.
     *
     * @param match
     * @param changeListener
     *            the change listener
     * @param message
     *            the message which can be found in the appropriate PatternUI annotation
     * @return the list of IObservableValue instances for which the IValueChangeListener was registered
     */
    public static List<IObservableValue> observeFeatures(IPatternMatch match, IValueChangeListener changeListener,
            String message) {
        if (message == null) {
            return Lists.newArrayList();
        }

        List<IObservableValue> affectedValues = new ArrayList<IObservableValue>();
        String[] tokens = message.split("\\$");
        //[425735] If i<1, affectedValues will be empty, thus a constant output is created
        // Processing only odd tokens
        for (int i = 1; i < tokens.length; i = i + 2) { 
            IObservableValue value = IncQueryObservables.getObservableValue(match, tokens[i]);
            if (value != null) {
                value.addValueChangeListener(changeListener);
                affectedValues.add(value);
            }
        }
        return affectedValues;
    }

    /**
     * Registers the given change listener on the given object's all accessible fields. This function uses Java
     * Reflection.
     *
     * @param changeListener
     *            the change listener
     * @param object
     *            the observed object
     * @return the list of IObservableValue instances for which the IValueChangeListener was registered
     */
    public static List<IObservableValue> observeAllAttributes(IValueChangeListener changeListener, Object object) {
        List<IObservableValue> affectedValues = new ArrayList<IObservableValue>();
        if (object instanceof EObject) {
            for (EStructuralFeature feature : ((EObject) object).eClass().getEAllStructuralFeatures()) {
                IObservableValue val = EMFProperties.value(feature).observe(object);
                affectedValues.add(val);
                val.addValueChangeListener(changeListener);
            }
        }
        return affectedValues;
    }

    /**
     * Returns an IObservableValue for the given match based on the given expression. If an attribute is not present in
     * the expression than it tries with the 'name' attribute. If it is not present the returned value will be null.
     *
     * @param match
     *            the match object
     * @param expression
     *            the expression
     * @return IObservableValue instance or null
     */
    public static IObservableValue getObservableValue(IPatternMatch match, String expression) {
        IObservableValue val = null;
        String[] objectTokens = expression.split("\\.");

        if (objectTokens.length > 0) {
            Object o = null;
            EStructuralFeature feature = null;

            if (objectTokens.length == 2) {
                o = match.get(objectTokens[0]);
                feature = IncQueryRuntimeHelper.getFeature(o, objectTokens[1]);
            }
            if (objectTokens.length == 1) {
                o = match.get(objectTokens[0]);
                feature = IncQueryRuntimeHelper.getFeature(o, "name");
            }
            if (o != null && feature != null) {
                val = EMFProperties.value(feature).observe(o);
            } else if (o != null && feature == null) {
                // No feature found, falling back to constant toString
                val = Observables.constantObservableValue(o.toString());
            } else {
                val = Observables.constantObservableValue(expression);
            }
        }

        return val;
    }

    /**
     * Returns an {@link IObservableValue} that observes the pattern match and converts it to the given expression.
     * 
     */
    public static IObservableValue getObservableLabelFeature(final IPatternMatch match, final String expression) {
        return getObservableLabelFeature(match, expression, null);
    }
    
    /**
     * Returns an {@link IObservableValue} that observes the pattern match and converts it to the given expression.
     * The given container is also stored in the created {@link ObservableLabelFeature}.
     * 
     */
    public static IObservableValue getObservableLabelFeature(final IPatternMatch match, final String expression,
            Object container) {
        ComputedValue value = new ObservableLabelFeature(match, expression, container);
        return value;
    }

    /**
     * Returns a {@link DatabindingAdapter} that can be used to observe details of pattern matches.
     * 
     * @param query
     */
    public static DatabindingAdapter<IPatternMatch> getDatabindingAdapter(IQuerySpecification<?> query) {
        GenericDatabindingAdapter adapter = new GenericDatabindingAdapter(query);
        return adapter;
    }

    /**
     * Calculates the list of {@link ObservableDefinition}s from a query. 
     * 
     * @param query
     */
    public static Map<String, ObservableDefinition> calculateObservableValues(IQuerySpecification<?> query) {
        Map<String, ObservableDefinition> propertyMap = Maps.newHashMap();
        for (String v : query.getParameterNames()) {
            ObservableDefinition def = new ObservableDefinition(v, v,
                    ObservableType.OBSERVABLE_FEATURE);
            propertyMap.put(v, def);
        }
        for (PAnnotation annotation : query.getAnnotationsByName(OBSERVABLEVALUE_ANNOTATION)) {
            String name = (String) annotation.getFirstValue("name");
            String expr = (String) annotation.getFirstValue("expression");
            String label = (String) annotation.getFirstValue("labelExpression");
            if (name == null) {
                Preconditions.checkArgument(expr == null && label == null,
                        "Name attribute must not be empty");
                continue;
            }
            Preconditions.checkArgument(expr != null ^ label != null,
                    "Either expression or label expression attribute must not be empty.");
            String obsExpr = null;
            ObservableType type;
            if (expr != null) {
                obsExpr = expr;
                type = ObservableType.OBSERVABLE_FEATURE;
            } else {// if (labelRef != null)
                obsExpr = label;
                type = ObservableType.OBSERVABLE_LABEL;
            }
            ObservableDefinition def = new ObservableDefinition(name, obsExpr, type);
    
            propertyMap.put(name, def);
        }
        return propertyMap;
    }

}
