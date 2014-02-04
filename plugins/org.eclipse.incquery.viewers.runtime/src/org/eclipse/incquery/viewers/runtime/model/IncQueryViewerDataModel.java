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
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.UpdateListStrategy;
import org.eclipse.core.databinding.UpdateSetStrategy;
import org.eclipse.core.databinding.observable.list.IListChangeListener;
import org.eclipse.core.databinding.observable.list.IObservableList;
import org.eclipse.core.databinding.observable.list.ListChangeEvent;
import org.eclipse.core.databinding.observable.list.ListDiffVisitor;
import org.eclipse.core.databinding.observable.list.MultiList;
import org.eclipse.core.databinding.observable.list.ObservableList;
import org.eclipse.core.databinding.observable.list.WritableList;
import org.eclipse.core.databinding.observable.set.IObservableSet;
import org.eclipse.core.databinding.observable.set.ISetChangeListener;
import org.eclipse.core.databinding.observable.set.ObservableSet;
import org.eclipse.core.databinding.observable.set.SetChangeEvent;
import org.eclipse.core.databinding.observable.set.UnionSet;
import org.eclipse.core.databinding.observable.set.WritableSet;
import org.eclipse.incquery.databinding.runtime.collection.ObservablePatternMatchList;
import org.eclipse.incquery.databinding.runtime.collection.ObservablePatternMatchSet;
import org.eclipse.incquery.runtime.api.IPatternMatch;
import org.eclipse.incquery.runtime.api.IQuerySpecification;
import org.eclipse.incquery.runtime.api.IncQueryEngine;
import org.eclipse.incquery.runtime.api.IncQueryMatcher;
import org.eclipse.incquery.runtime.evm.api.RuleEngine;
import org.eclipse.incquery.runtime.evm.specific.ExecutionSchemas;
import org.eclipse.incquery.runtime.evm.specific.Schedulers;
import org.eclipse.incquery.runtime.evm.specific.resolver.FixedPriorityConflictResolver;
import org.eclipse.incquery.runtime.exception.IncQueryException;
import org.eclipse.incquery.runtime.matchers.psystem.annotations.PAnnotation;
import org.eclipse.incquery.viewers.runtime.model.ViewerState.ViewerStateFeature;
import org.eclipse.incquery.viewers.runtime.model.converters.ContainmentList;
import org.eclipse.incquery.viewers.runtime.model.converters.ContainmentSet;
import org.eclipse.incquery.viewers.runtime.model.converters.EdgeList;
import org.eclipse.incquery.viewers.runtime.model.converters.EdgeSet;
import org.eclipse.incquery.viewers.runtime.model.converters.ItemConverter;
import org.eclipse.incquery.viewers.runtime.util.ViewersConflictResolver;

import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import com.google.common.collect.Multimap;
import com.google.common.collect.Sets;

/**
 * A {@link ViewerDataModel} implementation for IncQuery/EVM model sources.
 * 
 * Caution: set-based helper methods are not tested thoroughly yet!
 * 
 * 
 * If you instantiate this class yourself, then be sure to dispose() of it once it is not needed anymore.
 * 
 * @author Zoltan Ujhelyi
 * @author Istvan Rath
 * 
 */
public class IncQueryViewerDataModel extends ViewerDataModel {
    private IncQueryEngine engine;
    private Logger logger;
//  private ResourceSet model;
    private Set<IQuerySpecification<?>> patterns;
    
    RuleEngine ruleEngine;
    FixedPriorityConflictResolver resolver;

    /**
     * Initializes a Viewer Data model using a set of patterns and a selected engine.
     * 
     * @param patterns
     * @param engine
     * @throws IncQueryException
     */
    public IncQueryViewerDataModel(Collection<IQuerySpecification<?>> patterns, IncQueryEngine engine) {
//      this.model = model;
        this.patterns = Sets.newHashSet(patterns);
        this.engine = engine;
        logger = engine.getLogger();
        ruleEngine = ExecutionSchemas.createIncQueryExecutionSchema(engine,
                Schedulers.getIQEngineSchedulerFactory(engine));
        resolver = new ViewersConflictResolver();
        ruleEngine.setConflictResolver(resolver);
    }



    public IncQueryEngine getEngine() {
        return engine;
    }

//    public ResourceSet getModel() {
//        return model;
//    }

	public Collection<IQuerySpecification<? extends IncQueryMatcher<? extends IPatternMatch>>> getPatterns(final String annotation) {
		return Collections2.filter(patterns, new Predicate<IQuerySpecification<?>>() {

			@Override
			public boolean apply(IQuerySpecification<?> pattern) {
				return !pattern.getAnnotationsByName(annotation).isEmpty();
			}
		});
	}

    public Logger getLogger() {
        return logger;
    }

    /**
     * Dispose of this {@link IncQueryViewerDataModel} instance.
     * Releases the underlying {@link RuleEngine}.
     */
    public void dispose(){
    	if (this.ruleEngine!=null) {
    		this.ruleEngine.dispose();
    	}
    }
    
    @Override
    public IObservableSet initializeObservableItemSet(ViewerDataFilter filter, final Multimap<Object, Item> itemMap) {
    	/*
	    Set<ObservableSet> nodeSetsObservable = new HashSet<ObservableSet>();
	    final String annotationName = Item.ANNOTATION_ID;
	    for (final Pattern nodePattern : getPatterns(annotationName)) {
	        DataBindingContext ctx = new DataBindingContext();
	        ObservablePatternMatchSet<IPatternMatch> nodeSet = filter.getObservableSet(nodePattern, ruleEngine);
	        resolver.setPriority(nodeSet.getSpecification(), NODE_PRIORITY);
	        Annotation formatAnnotation = CorePatternLanguageHelper.getFirstAnnotationByName(nodePattern,
	                FormatSpecification.FORMAT_ANNOTATION);
	        for (Annotation annotation : CorePatternLanguageHelper.getAnnotationsByName(nodePattern, annotationName)) {
	            ObservableSet resultSet = new WritableSet();
	            nodeSetsObservable.add(resultSet);
	
	            ctx.bindSet(resultSet, nodeSet, null,
	                    new UpdateSetStrategy().setConverter(new ItemConverter(annotation, formatAnnotation)));
	        }
	    }
	    */
    	Map<IObservableSet,Void> nodeSetsObservable = new IdentityHashMap<IObservableSet, Void>();
        final String annotationName = Item.ANNOTATION_ID;
        for (final IQuerySpecification<? extends IncQueryMatcher<? extends IPatternMatch>> nodePattern : getPatterns(annotationName)) {
            DataBindingContext ctx = new DataBindingContext();
            @SuppressWarnings("unchecked")
            ObservablePatternMatchSet<? extends IPatternMatch> nodeSet = createObservableSet(filter, (IQuerySpecification<? extends IncQueryMatcher<IPatternMatch>>)nodePattern);
            resolver.setPriority(nodeSet.getSpecification(), NODE_PRIORITY);
            PAnnotation formatAnnotation = nodePattern.getFirstAnnotationByName(FormatSpecification.FORMAT_ANNOTATION);
            for (PAnnotation annotation : nodePattern.getAnnotationsByName(annotationName)) {
                ObservableSet resultSet = new WritableSet();
                nodeSetsObservable.put(resultSet,null);

                ctx.bindSet(resultSet, nodeSet, null,
                        new UpdateSetStrategy().setConverter(new ItemConverter(annotation, formatAnnotation)));
            }
        }
	    
        //UnionSet Set = new UnionSet(nodeSetsObservable.toArray(new IObservableSet[]{}));
	    
	    UnionSet Set = new UnionSet(nodeSetsObservable.keySet().toArray(new IObservableSet[]{}));
	    
	    for (Object _item : Set) {
	        Item item = (Item) _item;
	        itemMap.put(item.getParamObject(), item);
	    }
	    Set.addSetChangeListener(new ISetChangeListener() {
	        
	        @Override
	        public void handleSetChange(SetChangeEvent event) {
	            for (Object element : event.diff.getAdditions()) {
	                if (element instanceof Item) {
	                    Item item = (Item) element;
	                    itemMap.put(item.getParamObject(), item);
	                }
	            }
	            for (Object element : event.diff.getRemovals()) {
	                if (element instanceof Item) {
	                    Item item = (Item) element;
	                    Object paramObject = item.getParamObject();
	                    itemMap.remove(paramObject, element);
	                }
	            }
	        }
	    });
	    return Set;
	}



    private <Match extends IPatternMatch> ObservablePatternMatchSet<? extends IPatternMatch> createObservableSet(ViewerDataFilter filter,
            final IQuerySpecification<? extends IncQueryMatcher<Match>> nodePattern) {
        return filter.getObservableSet(nodePattern, ruleEngine);
    }
    
    private <Match extends IPatternMatch> ObservablePatternMatchList<Match> createObservableList(ViewerDataFilter filter,
            final IQuerySpecification<? extends IncQueryMatcher<Match>> nodePattern) {
        return filter.getObservableList(nodePattern, ruleEngine);
    }
 
    @Override
    public IObservableList initializeObservableItemList(ViewerDataFilter filter, final Multimap<Object, Item> itemMap) {
	    List<ObservableList> nodeListsObservable = new ArrayList<ObservableList>();
	    final String annotationName = Item.ANNOTATION_ID;
	    for (final IQuerySpecification<? extends IncQueryMatcher<? extends IPatternMatch>> nodePattern : getPatterns(annotationName)) {
	        DataBindingContext ctx = new DataBindingContext();
	        @SuppressWarnings("unchecked")
            ObservablePatternMatchList<IPatternMatch> nodeList = createObservableList(filter, (IQuerySpecification<? extends IncQueryMatcher<IPatternMatch>>)nodePattern); 
	        resolver.setPriority(nodeList.getSpecification(), NODE_PRIORITY);
	        PAnnotation formatAnnotation = nodePattern.getFirstAnnotationByName(FormatSpecification.FORMAT_ANNOTATION);
	        for (PAnnotation annotation : nodePattern.getAnnotationsByName(annotationName)) {
	            ObservableList resultList = new WritableList();
	            nodeListsObservable.add(resultList);
	
	            ctx.bindList(resultList, nodeList, null,
	                    new UpdateListStrategy().setConverter(new ItemConverter(annotation, formatAnnotation)));
	        }
	    }
	    MultiList list = new MultiList(nodeListsObservable.toArray(new ObservableList[nodeListsObservable.size()]));
	    for (Object _item : list) {
	        Item item = (Item) _item;
	        itemMap.put(item.getParamObject(), item);
	    }
	    list.addListChangeListener(new IListChangeListener() {
	        
	        @Override
	        public void handleListChange(ListChangeEvent event) {
	            event.diff.accept(new ListDiffVisitor() {
	                
	                @Override
	                public void handleRemove(int index, Object element) {
	                    if (element instanceof Item) {
	                        Item item = (Item) element;
	                        Object paramObject = item.getParamObject();
	                        itemMap.remove(paramObject, element);
	                    }
	                }
	                
	                @Override
	                public void handleAdd(int index, Object element) {
	                    if (element instanceof Item) {
	                        Item item = (Item) element;
	                        itemMap.put(item.getParamObject(), item);
	                    }
	                    
	                }
	            });
	        }
	    });
	    return list;
	}

    @Override
    public IObservableSet initializeObservableEdgeSet(ViewerDataFilter filter, final Multimap<Object, Item> itemMap) {
	    final String annotationName = Edge.ANNOTATION_ID;
	    Set<IObservableSet> edgeSetsObservable = new HashSet<IObservableSet>();
	    for (final IQuerySpecification<? extends IncQueryMatcher<? extends IPatternMatch>> edgePattern : getPatterns(annotationName)) {
	        @SuppressWarnings("unchecked")
            ObservablePatternMatchSet<? extends IPatternMatch> edgeSet = createObservableSet(filter, (IQuerySpecification<? extends IncQueryMatcher<IPatternMatch>>)edgePattern); 
	        resolver.setPriority(edgeSet.getSpecification(), EDGE_PRIORITY);
	
	        PAnnotation formatAnnotation = edgePattern.getFirstAnnotationByName(FormatSpecification.FORMAT_ANNOTATION);
	        for (PAnnotation annotation : edgePattern.getAnnotationsByName(annotationName)) {
	            IObservableSet resultSet = new EdgeSet(annotation, formatAnnotation, itemMap, edgeSet);
	            edgeSetsObservable.add(resultSet);
	        }
	    }
	    UnionSet Set = new UnionSet(edgeSetsObservable.toArray(new IObservableSet[]{}));
	    return Set;
	}
    
    @Override
    public MultiList initializeObservableEdgeList(ViewerDataFilter filter, final Multimap<Object, Item> itemMap) {
	    final String annotationName = Edge.ANNOTATION_ID;
	    List<IObservableList> edgeListsObservable = new ArrayList<IObservableList>();
	    for (final IQuerySpecification<? extends IncQueryMatcher<? extends IPatternMatch>> edgePattern : getPatterns(annotationName)) {
	        @SuppressWarnings("unchecked")
            ObservablePatternMatchList<IPatternMatch> edgelist = createObservableList(filter, (IQuerySpecification<? extends IncQueryMatcher<IPatternMatch>>)edgePattern); 
	        resolver.setPriority(edgelist.getSpecification(), EDGE_PRIORITY);
	
	        PAnnotation formatAnnotation = edgePattern.getFirstAnnotationByName(FormatSpecification.FORMAT_ANNOTATION);
	        for (PAnnotation annotation : edgePattern.getAnnotationsByName(annotationName)) {
	            IObservableList resultList = new EdgeList(annotation, formatAnnotation, itemMap, edgelist);
	            edgeListsObservable.add(resultList);
	        }
	    }
	    MultiList list = new MultiList(edgeListsObservable.toArray(new IObservableList[edgeListsObservable.size()]));
	    return list;
	}

    @Override
    public IObservableSet initializeObservableContainmentSet(ViewerDataFilter filter, final Multimap<Object, Item> itemMap) {
	    final String annotationName = Containment.ANNOTATION_ID;
	    Set<IObservableSet> containmentSetsObservable = new HashSet<IObservableSet>();
	    for (final IQuerySpecification<? extends IncQueryMatcher<? extends IPatternMatch>> containmentPattern : getPatterns(annotationName)) {
	        @SuppressWarnings("unchecked")
            ObservablePatternMatchSet<? extends IPatternMatch> containmentSet = createObservableSet(filter, (IQuerySpecification<? extends IncQueryMatcher<IPatternMatch>>)containmentPattern); 
	        resolver.setPriority(containmentSet.getSpecification(), CONTAINMENT_PRIORITY);
	
	        for (PAnnotation annotation : containmentPattern.getAnnotationsByName(annotationName)) {
	            IObservableSet resultSet = new ContainmentSet(annotation, itemMap, containmentSet);
	            containmentSetsObservable.add(resultSet);
	        }
	    }
	    UnionSet Set = new UnionSet(containmentSetsObservable.toArray(new IObservableSet[]{}));
	    return Set;
	}
    
    @Override
    public MultiList initializeObservableContainmentList(ViewerDataFilter filter, final Multimap<Object, Item> itemMap) {
	    final String annotationName = Containment.ANNOTATION_ID;
	    List<IObservableList> containmentListsObservable = new ArrayList<IObservableList>();
	    for (final IQuerySpecification<? extends IncQueryMatcher<? extends IPatternMatch>> containmentPattern : getPatterns(annotationName)) {
	        @SuppressWarnings("unchecked")
            ObservablePatternMatchList<IPatternMatch> containmentList = createObservableList(filter, (IQuerySpecification<? extends IncQueryMatcher<IPatternMatch>>)containmentPattern);
	        resolver.setPriority(containmentList.getSpecification(), CONTAINMENT_PRIORITY);
	
	        for (PAnnotation annotation : containmentPattern.getAnnotationsByName(annotationName)) {
	            IObservableList resultList = new ContainmentList(annotation, itemMap, containmentList);
	            containmentListsObservable.add(resultList);
	        }
	    }
	    MultiList list = new MultiList(containmentListsObservable.toArray(new IObservableList[containmentListsObservable.size()]));
	    return list;
	}
    
    // XXX switch between set-list here
 	protected static boolean setMode = false;
 	
 	/* factory method */
 	
 	/**
 	 * Instantiate a {@link ViewerState} and its corresponding {@link ViewerDataModel} instance, for IncQuery-based 
 	 * model sources.
 	 * 
 	 * When the state is disposed, the model will be disposed too.
 	 * 
 	 * @param set
 	 * @param engine
 	 * @param patterns
 	 * @param filter
 	 * @param features
 	 * @return
 	 */
 	public static ViewerState newViewerState(IncQueryEngine engine,
 			Collection<IQuerySpecification<?>> patterns, ViewerDataFilter filter,
 			Collection<ViewerStateFeature> features) {
 		IncQueryViewerDataModel m = new IncQueryViewerDataModel(patterns, engine);
  		ViewerState r = newViewerState(m, filter, features);
  		r.hasExternalViewerDataModel=false;
  		return r;
 	}
 	
 	/**
 	 * Instantiate a new {@link ViewerState}, for an already existing {@link IncQueryViewerDataModel} instance.
 	 * 
 	 * This {@link ViewerDataModel} will not be disposed when the state is disposed.
 	 * 
 	 * @param model
 	 * @param filter
 	 * @param features
 	 * @return
 	 */
 	public static ViewerState newViewerState(IncQueryViewerDataModel model, ViewerDataFilter filter,
 			Collection<ViewerStateFeature> features)
 	{
 		ViewerState s = null;
 		if (setMode)
 			s = new ViewerStateSet(model, filter, features);
 		else
 			s = new ViewerStateList(model, filter, features);
 		
 		s.hasExternalViewerDataModel=true;
 		return s;
 	}
 	
    
}
