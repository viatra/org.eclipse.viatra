/*******************************************************************************
 * Copyright (c) 2010-2013, Csaba Debreceni, Zoltan Ujhelyi, Istvan Rath and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   ujhelyiz - initial API and implementation
 *   istvanrath - refactoring
 *   Csaba Debreceni - update for new viewers implementation
 *******************************************************************************/
package org.eclipse.viatra.addon.viewers.runtime.model;

import java.util.Collection;
import java.util.Collections;

import org.eclipse.viatra.addon.viewers.runtime.model.listeners.IViewerLabelListener;
import org.eclipse.viatra.addon.viewers.runtime.model.listeners.IViewerStateListener;
import org.eclipse.viatra.addon.viewers.runtime.model.patterns.Children;
import org.eclipse.viatra.addon.viewers.runtime.notation.Containment;
import org.eclipse.viatra.addon.viewers.runtime.notation.Edge;
import org.eclipse.viatra.addon.viewers.runtime.notation.Item;
import org.eclipse.viatra.addon.viewers.runtime.notation.NotationModel;
import org.eclipse.viatra.addon.viewers.runtime.specifications.ContainmentQuerySpecificationDescriptor;
import org.eclipse.viatra.addon.viewers.runtime.specifications.EdgeQuerySpecificationDescriptor;
import org.eclipse.viatra.addon.viewers.runtime.specifications.ItemQuerySpecificationDescriptor;
import org.eclipse.viatra.addon.viewers.runtime.util.ViewerTraceabilityUtil;
import org.eclipse.viatra.query.runtime.api.IQuerySpecification;
import org.eclipse.viatra.query.runtime.api.ViatraQueryEngine;
import org.eclipse.viatra.query.runtime.base.exception.ViatraBaseException;
import org.eclipse.viatra.query.runtime.exception.ViatraQueryException;
import org.eclipse.viatra.query.runtime.matchers.psystem.annotations.PAnnotation;
import org.eclipse.viatra.query.runtime.matchers.psystem.queries.QueryInitializationException;
import org.eclipse.viatra.query.runtime.util.ViatraQueryLoggingUtil;
import org.eclipse.viatra.transformation.views.core.ViewModelManager;
import org.eclipse.viatra.transformation.views.core.ViewModelRule;
import org.eclipse.viatra.transformation.views.traceability.Traceability;

import com.google.common.collect.Lists;

/**
 * <p>
 * A Viewer state represents a stateful data model for a VIATRA Viewer. The state is capable of either returning
 * lists of its content, and is also capable of sending state change notifications based to {@link IViewerStateListener}
 * implementations.
 * </p>
 * 
 * <p>
 * A Viewer can be initialized directly with a set of patterns and model, or a {@link ViatraViewerDataModel} can be
 * used to prepare and share such data between instances.
 * </p>
 * 
 * <p>
 * A ViewerState needs to be cleaned up using the {@link #dispose()} method to unregister all listeners.
 * </p>
 * 
 * @author Zoltan Ujhelyi, Istvan Rath, Csaba Debreceni
 *
 */
public class ViewerState implements IViewerStateListener, IViewerLabelListener {

    /**
     * If true, then the viewerstate has an "external" model that should not be disposed internally.
     */
    protected boolean hasExternalViewerDataModel = false;
    protected ViewerDataModel model;
    protected ViewerDataFilter filter;
    protected Collection<ViewerStateFeature> features;
    protected ViewModelManager manager;

    public ViewerState(ViewerDataModel model, ViewerDataFilter filter, Collection<ViewerStateFeature> features) {
        this.model = model;
        this.filter = filter;
        this.features = features;
        this.manager = new ViewModelManager();
        
        try {
            manager.setEngine(model.getEngine());
            manager.setRules(collectRules(model));
            manager.initialize();
        } catch (ViatraQueryException | QueryInitializationException | ViatraBaseException e) {
            ViatraQueryLoggingUtil.getLogger(getClass()).error(e.getMessage());
        }
    }

    private Collection<ViewModelRule> collectRules(ViewerDataModel model) throws QueryInitializationException {

        Collection<ViewModelRule> rules = Lists.newArrayList();
        for (IQuerySpecification<?> pattern : model.getPatterns()) {
            for(PAnnotation annotation : pattern.getAllAnnotations()) {
                if (features.contains(ViewerStateFeature.EDGE) && annotation.getName().equals(EdgeQuerySpecificationDescriptor.ANNOTATION_ID))
                    rules.add(EdgeRule.initiate(pattern, annotation, this, filter));
                
                if (features.contains(ViewerStateFeature.CONTAINMENT) && annotation.getName().equals(ContainmentQuerySpecificationDescriptor.ANNOTATION_ID))
                    rules.add(ContainmentRule.initiate(pattern, annotation, this, filter));
                
                if (annotation.getName().equals(ItemQuerySpecificationDescriptor.ANNOTATION_ID))
                    rules.add(ItemRule.initiate(pattern, annotation, this, filter));
            }
        }
        return rules;
    }

    public Collection<Item> getChildren(Item parent) {
        try {
            Children.Matcher matcher = model.getEngine().getMatcher(Children.instance());
            Collection<Children.Match> matches = matcher.getAllMatches(parent, null);
            Collection<Item> items = Lists.newArrayList();
            for (Children.Match match: matches) {
                items.add(match.getChild());
            }
            return items;
        } catch (ViatraQueryException e) {
            ViatraQueryLoggingUtil.getLogger(getClass()).error(e.getMessage());
        }
        return Collections.emptyList();
    }

    public Item getParent(Item child) {
        return child.getParent();
    }

    public enum ViewerStateFeature {
        EDGE, CONTAINMENT
    }

    protected Collection<IViewerStateListener> stateListeners = Lists.newArrayList();
    protected Collection<IViewerLabelListener> labelListeners = Lists.newArrayList();
    private boolean disposed;

    public final void itemAppeared(Item item) {
        for (IViewerStateListener l : stateListeners) {
            l.itemAppeared(item);
        }
    }

    public final void itemDisappeared(Item item) {
        for (IViewerStateListener l : stateListeners) {
            l.itemDisappeared(item);
        }
    }

    public final void containmentAppeared(Containment containment) {
        for (IViewerStateListener l : stateListeners) {
            l.containmentAppeared(containment);
        }
    }

    public final void containmentDisappeared(Containment containment) {
        for (IViewerStateListener l : stateListeners) {
            l.containmentDisappeared(containment);
        }
    }

    public final void edgeAppeared(Edge edge) {
        for (IViewerStateListener l : stateListeners) {
            l.edgeAppeared(edge);
        }
    }

    public final void edgeDisappeared(Edge edge) {
        for (IViewerStateListener l : stateListeners) {
            l.edgeDisappeared(edge);
        }
    }

    public final void labelUpdated(Item item, String newLabel) {
        for (IViewerLabelListener l : labelListeners) {
            l.labelUpdated(item, newLabel);
        }
    }

    public final void labelUpdated(Edge edge, String newLabel) {
        for (IViewerLabelListener l : labelListeners) {
            l.labelUpdated(edge, newLabel);
        }
    }

    /**
     * Adds a new state Listener to the Viewer State
     */
    public void addStateListener(IViewerStateListener listener) {
        stateListeners.add(listener);
    }

    /**
     * Removes a state Listener from the Viewer State
     */
    public void removeStateListener(IViewerStateListener listener) {
        stateListeners.remove(listener);
    }

    /**
     * Adds a new label Listener to the Viewer State
     */
    public void addLabelListener(IViewerLabelListener listener) {
        labelListeners.add(listener);
    }

    /**
     * Removes a label Listener from the Viewer State
     */
    public void removeLabelListener(IViewerLabelListener listener) {
        labelListeners.remove(listener);
    }

    /**
     * Access the Set of Items mapped to an EObject.
     */
    public Collection<Item> getItemsFor(Object target) {
        return ViewerTraceabilityUtil.traceToItem(model.getEngine(), target);
    }

    public Collection<Item> getItems() {
        return model.getNotationModel().getItems();
    }

    public Collection<Edge> getEdges() {
        return model.getNotationModel().getEdges();
    }

    public Collection<Containment> getContainments() {
        return model.getNotationModel().getContainments();
    }

    public ViewModelManager getManager() {
        return manager;
    }

    public NotationModel getNotationModel() {
        return model.getNotationModel();
    }

    public Traceability getTraceability() {
        return manager.getTraceability();
    }

    public ViatraQueryEngine getEngine() {
        return model.getEngine();
    }

    public void dispose() {
        manager.dispose();
        if(!hasExternalViewerDataModel)
            model.dispose();
        
        disposed = true;
    }

    public boolean isDisposed() {
        return disposed;
    }

}
