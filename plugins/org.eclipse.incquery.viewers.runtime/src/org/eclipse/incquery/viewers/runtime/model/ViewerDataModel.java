/*******************************************************************************
 * Copyright (c) 2010-2013, Zoltan Ujhelyi, Istvan Rath and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Zoltan Ujhelyi - initial API and implementation
 *******************************************************************************/
package org.eclipse.incquery.viewers.runtime.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.UpdateListStrategy;
import org.eclipse.core.databinding.observable.list.IListChangeListener;
import org.eclipse.core.databinding.observable.list.IObservableList;
import org.eclipse.core.databinding.observable.list.ListChangeEvent;
import org.eclipse.core.databinding.observable.list.ListDiffVisitor;
import org.eclipse.core.databinding.observable.list.MultiList;
import org.eclipse.core.databinding.observable.list.ObservableList;
import org.eclipse.core.databinding.observable.list.WritableList;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.incquery.databinding.runtime.api.IncQueryObservables;
import org.eclipse.incquery.patternlanguage.helper.CorePatternLanguageHelper;
import org.eclipse.incquery.patternlanguage.patternLanguage.Annotation;
import org.eclipse.incquery.patternlanguage.patternLanguage.Pattern;
import org.eclipse.incquery.runtime.api.EngineManager;
import org.eclipse.incquery.runtime.api.IMatcherFactory;
import org.eclipse.incquery.runtime.api.IPatternMatch;
import org.eclipse.incquery.runtime.api.IncQueryEngine;
import org.eclipse.incquery.runtime.api.IncQueryMatcher;
import org.eclipse.incquery.runtime.exception.IncQueryException;
import org.eclipse.incquery.runtime.extensibility.MatcherFactoryRegistry;
import org.eclipse.incquery.viewers.runtime.model.converters.ContainmentConverter;
import org.eclipse.incquery.viewers.runtime.model.converters.EdgeConverter;
import org.eclipse.incquery.viewers.runtime.model.converters.ItemConverter;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

/**
 * Data model collecting input from multiple query results, and returns them as {@link ObservableList} instances.
 * 
 * @author Zoltan Ujhelyi
 * 
 */
@SuppressWarnings({ "unchecked" })
public class ViewerDataModel {
    private IncQueryEngine engine;
    Logger logger;
    private ResourceSet model;
    private Set<Pattern> patterns;
    private Map<Object, Item> itemMap;

    public ViewerDataModel(ResourceSet model, Collection<Pattern> patterns) throws IncQueryException {
        this(model, patterns, EngineManager.getInstance().createUnmanagedIncQueryEngine(model));
        logger = engine.getLogger();
    }

    public ViewerDataModel(ResourceSet model, Collection<Pattern> patterns, IncQueryEngine engine) {
        this.model = model;
        this.patterns = Sets.newHashSet(patterns);
        this.engine = engine;
        itemMap = Maps.newHashMap();
    }

    public IncQueryEngine getEngine() {
        return engine;
    }

    public ResourceSet getModel() {
        return model;
    }

    public Collection<Pattern> getPatterns(String annotation) {
        return patterns;
    }

    public Logger getLogger() {
        return logger;
    }

    /**
     * Initializes and returns an observable list of nodes. Each call re-initializes a new observable
     * 
     * @return an observable list of {@link Item} elements representing the match results in the model.
     */
    public IObservableList initializeObservableItemList() {
        itemMap.clear();
        List<ObservableList> nodeListsObservable = new ArrayList<ObservableList>();
        final String annotationName = Item.ANNOTATION_ID;
        for (final Pattern nodePattern : getPatterns(annotationName)) {
            DataBindingContext ctx = new DataBindingContext();
            IMatcherFactory<IncQueryMatcher<IPatternMatch>> factory = (IMatcherFactory<IncQueryMatcher<IPatternMatch>>) MatcherFactoryRegistry
                    .getOrCreateMatcherFactory(nodePattern);

            IObservableList obspatternmatchlist = IncQueryObservables.observeMatchesAsList(factory, getEngine());
            Annotation formatAnnotation = CorePatternLanguageHelper.getFirstAnnotationByName(nodePattern,
                    FormatSpecification.FORMAT_ANNOTATION);
            for (Annotation annotation : CorePatternLanguageHelper.getAnnotationsByName(nodePattern, annotationName)) {
                ObservableList resultList = new WritableList();
                nodeListsObservable.add(resultList);

                ctx.bindList(resultList, obspatternmatchlist, null,
                        new UpdateListStrategy().setConverter(new ItemConverter(itemMap, annotation, formatAnnotation)));
            }
        }
        MultiList list = new MultiList(nodeListsObservable.toArray(new ObservableList[nodeListsObservable.size()]));
        list.addListChangeListener(new IListChangeListener() {
            
            @Override
            public void handleListChange(ListChangeEvent event) {
                event.diff.accept(new ListDiffVisitor() {
                    
                    @Override
                    public void handleRemove(int index, Object element) {
                        if (element instanceof Item) {
                            Item item = (Item) element;
                            EObject paramObject = item.getParamObject();
                            if (itemMap.containsKey(paramObject) && itemMap.get(paramObject).equals(item)) {
                                itemMap.remove(paramObject);
                            }
                        }
                    }
                    
                    @Override
                    public void handleAdd(int index, Object element) {
                        if (element instanceof Item) {
                            Item item = (Item) element;
                            if (!itemMap.containsKey(item.getParamObject())) {
                                itemMap.put(item.getParamObject(), item);
                            }
                        }
                        
                    }
                });
            }
        });
        return list;
    }

    /**
     * Initializes and returns an observable list of edges. Each call re-initializes a new observable
     * 
     * @return an observable list of {@link Edge} elements representing the match results in the model.
     */
    public MultiList initializeObservableEdgeList() {
        final String annotationName = Edge.ANNOTATION_ID;
        List<ObservableList> edgeListsObservable = new ArrayList<ObservableList>();
        for (final Pattern edgePattern : getPatterns(annotationName)) {
            DataBindingContext ctx = new DataBindingContext();
            IMatcherFactory<IncQueryMatcher<IPatternMatch>> factory = (IMatcherFactory<IncQueryMatcher<IPatternMatch>>) MatcherFactoryRegistry
                    .getOrCreateMatcherFactory(edgePattern);

            IObservableList obspatternmatchlist = IncQueryObservables.observeMatchesAsList(factory, getEngine());

            Annotation formatAnnotation = CorePatternLanguageHelper.getFirstAnnotationByName(edgePattern,
                    FormatSpecification.FORMAT_ANNOTATION);
            for (Annotation annotation : CorePatternLanguageHelper.getAnnotationsByName(edgePattern, annotationName)) {
                ObservableList resultList = new WritableList();
                edgeListsObservable.add(resultList);

                ctx.bindList(resultList, obspatternmatchlist, null,
                        new UpdateListStrategy().setConverter(new EdgeConverter(annotation, formatAnnotation, itemMap)));
            }
        }
        MultiList list = new MultiList(edgeListsObservable.toArray(new ObservableList[edgeListsObservable.size()]));
        return list;
    }

    /**
     * Initializes and returns an observable list of edges. Each call re-initializes a new observable
     * 
     * @return an observable list of {@link Edge} elements representing the match results in the model.
     */
    public MultiList initializeObservableContainmentList() {
        final String annotationName = Containment.ANNOTATION_ID;
        List<ObservableList> containmentListsObservable = new ArrayList<ObservableList>();
        for (final Pattern containmentPattern : getPatterns(annotationName)) {
            DataBindingContext ctx = new DataBindingContext();
            IMatcherFactory<IncQueryMatcher<IPatternMatch>> factory = (IMatcherFactory<IncQueryMatcher<IPatternMatch>>) MatcherFactoryRegistry
                    .getOrCreateMatcherFactory(containmentPattern);

            IObservableList obspatternmatchlist = IncQueryObservables.observeMatchesAsList(factory, getEngine());

            for (Annotation annotation : CorePatternLanguageHelper.getAnnotationsByName(containmentPattern, annotationName)) {
                ObservableList resultList = new WritableList();
                containmentListsObservable.add(resultList);

                ctx.bindList(resultList, obspatternmatchlist, null,
                        new UpdateListStrategy().setConverter(new ContainmentConverter(annotation, itemMap)));
            }
        }
        MultiList list = new MultiList(containmentListsObservable.toArray(new ObservableList[containmentListsObservable.size()]));
        return list;
    }
}
