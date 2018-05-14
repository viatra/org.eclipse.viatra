/*******************************************************************************
 * Copyright (c) 2004-2011 Abel Hegedus and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Abel Hegedus - initial API and implementation
 *******************************************************************************/
package org.eclipse.viatra.addon.querybasedfeatures.runtime;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.emf.common.notify.Notifier;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.viatra.query.runtime.api.AdvancedViatraQueryEngine;
import org.eclipse.viatra.query.runtime.api.IMatchUpdateListener;
import org.eclipse.viatra.query.runtime.api.IPatternMatch;
import org.eclipse.viatra.query.runtime.api.IQuerySpecification;
import org.eclipse.viatra.query.runtime.api.ViatraQueryEngineLifecycleListener;
import org.eclipse.viatra.query.runtime.api.ViatraQueryMatcher;
import org.eclipse.viatra.query.runtime.api.ViatraQueryModelUpdateListener;
import org.eclipse.viatra.query.runtime.api.ViatraQueryModelUpdateListener.ChangeLevel;
import org.eclipse.viatra.query.runtime.exception.ViatraQueryException;
import org.eclipse.viatra.query.runtime.registry.IQuerySpecificationRegistry;
import org.eclipse.viatra.query.runtime.registry.QuerySpecificationRegistry;
import org.eclipse.viatra.query.runtime.util.ViatraQueryLoggingUtil;

/**
 * @author Abel Hegedus
 * 
 * TODO add generics for type-safe API
 * 
 */
public abstract class QueryBasedFeature {

    private final class MatchUpdateListener implements
            IMatchUpdateListener<IPatternMatch> {
        @Override
        public void notifyAppearance(IPatternMatch match) {
            boolean removed = matchLostEvents.remove(match);
            if(!removed){
                matchFoundEvents.add(match);
            }
        }

        @Override
        public void notifyDisappearance(IPatternMatch match) {
            boolean removed = matchFoundEvents.remove(match);
            if(!removed){
                matchLostEvents.add(match);
            }
        }
    }

    /**
     * @author Abel Hegedus
     * 
     */
    private final class ModelUpdateListener implements ViatraQueryModelUpdateListener {
        @Override
        public void notifyChanged(ChangeLevel changeLevel) {
            beforeUpdate();
            matchFoundEvents.removeAll(processNewMatches(matchFoundEvents));
            matchLostEvents.removeAll(processLostMatches(matchLostEvents));
            afterUpdate();
            sendNotfications();
            // engineForMatcher().getLogger()
            // .error("[ViatraqueryFeatureHandler] Exception during update: " + e.getMessage(), e);
        }

        @Override
        public ChangeLevel getLevel() {
            return ChangeLevel.MATCHSET;
        }
    }

    /**
     * @author Abel Hegedus
     * 
     */
    private final class EngineLifecycleListener implements ViatraQueryEngineLifecycleListener {
        @Override
        public void matcherInstantiated(ViatraQueryMatcher<? extends IPatternMatch> matcher) {
        }

        @SuppressWarnings("unchecked")
        @Override
        public void engineWiped() {
            String patternName = matcher.getPatternName();
            try {
                IQuerySpecificationRegistry registry = QuerySpecificationRegistry.getInstance();
                IQuerySpecification<?> querySpecification = registry.getDefaultView().getEntry(patternName).get();
                matcher = (ViatraQueryMatcher<IPatternMatch>) querySpecification.getMatcher(engineForMatcher());
            } catch (ViatraQueryException e) {
                ViatraQueryLoggingUtil.getLogger(getClass()).error(
                        "[QueryBasedFeature] Exception during wipe callback: " + e.getMessage(), e);
            }
//            dm = matcher.newDeltaMonitor(false);
            matchFoundEvents.clear();
            matchLostEvents.clear();
        }

        @Override
        public void engineDisposed() {
        }

        @Override
        public void engineBecameTainted(String description, Throwable t) {
        }
    }

    private ViatraQueryMatcher<IPatternMatch> matcher;
    private Set<IPatternMatch> matchFoundEvents;
    private Set<IPatternMatch> matchLostEvents;
    private final EStructuralFeature feature;
    private String sourceParamName;
    private String targetParamName;

    private boolean keepCache = true;
    private boolean initialized = false;

    private final List<ENotificationImpl> notifications = new ArrayList<ENotificationImpl>();

    private ModelUpdateListener listener;
    private EngineLifecycleListener engineLifecycleListener;
    private MatchUpdateListener matchUpdateListener;

    protected void initialize(final ViatraQueryMatcher<IPatternMatch> matcher, String sourceParamName,
            String targetParamName) {
        if (initialized) {
            ViatraQueryLoggingUtil.getLogger(getClass()).error("[QueryBasedFeature] Feature already initialized!");
            return;
        }
        initialized = true;
        this.matcher = matcher;
        this.sourceParamName = sourceParamName;
        this.targetParamName = targetParamName;
        if (matcher.getPositionOfParameter(sourceParamName) == null) {
            ViatraQueryLoggingUtil.getLogger(getClass()).error(
                    "[QueryBasedFeature] Source parameter " + sourceParamName + " not found!");
        }
        if (targetParamName != null && matcher.getPositionOfParameter(targetParamName) == null) {
            ViatraQueryLoggingUtil.getLogger(getClass()).error(
                    "[QueryBasedFeature] Target parameter " + targetParamName + " not found!");
        }
//        this.dm = matcher.newDeltaMonitor(true);
        this.matchFoundEvents = new HashSet<>();
        this.matchLostEvents = new HashSet<>();
        matchUpdateListener = new MatchUpdateListener();
        engineLifecycleListener = new EngineLifecycleListener();
        listener = new ModelUpdateListener();
    }

    private void sendNotfications() {
        while (!notifications.isEmpty()) {
            ENotificationImpl remove = notifications.remove(0);
            ((Notifier) remove.getNotifier()).eNotify(remove);
        }
    }

    public QueryBasedFeature(EStructuralFeature feature, boolean keepCache) {
        this.feature = feature;
        this.keepCache = keepCache;
    }

    protected ViatraQueryMatcher<IPatternMatch> getMatcher() {
        return matcher;
    }
    
    protected void setMatcher(ViatraQueryMatcher<IPatternMatch> matcher) {
        this.matcher = matcher;
    }

    protected EStructuralFeature getFeature() {
        return feature;
    }

    protected String getSourceParamName() {
        return sourceParamName;
    }
    
    protected void setSourceParamName(String sourceParamName) {
        this.sourceParamName = sourceParamName;
    }

    protected String getTargetParamName() {
        return targetParamName;
    }
    
    protected void setTargetParamName(String targetParamName) {
        this.targetParamName = targetParamName;
    }

    protected boolean isCached() {
        return keepCache;
    }

    protected boolean isInitialized() {
        return initialized;
    }

    public abstract QueryBasedFeatureKind getKind();

    protected abstract void afterUpdate();

    protected abstract void beforeUpdate();

    /**
     * Call this once to start handling callbacks.
     */
    protected void startMonitoring() {
        AdvancedViatraQueryEngine engine = engineForMatcher();
        engine.addMatchUpdateListener(matcher, matchUpdateListener, true);
        engine.addLifecycleListener(engineLifecycleListener);
        engine.addModelUpdateListener(listener);
        listener.notifyChanged(ChangeLevel.MATCHSET);
    }

    protected AdvancedViatraQueryEngine engineForMatcher() {
        return (AdvancedViatraQueryEngine) matcher.getEngine();
    }

    public abstract Object getValue(Object source);

    private Collection<IPatternMatch> processNewMatches(Collection<IPatternMatch> signatures) {
        List<IPatternMatch> processed = new ArrayList<IPatternMatch>();
        for (IPatternMatch signature : signatures) {
            processAppearedMatch(signature);
            processed.add(signature);
        }
        return processed;
    }

    protected abstract void processAppearedMatch(IPatternMatch signature);

    protected InternalEObject getSourceValue(IPatternMatch signature) {
        return (InternalEObject) signature.get(sourceParamName);
    }

    protected Object getTargetValue(IPatternMatch signature) {
        return signature.get(targetParamName);
    }

    private Collection<IPatternMatch> processLostMatches(Collection<IPatternMatch> signatures) {
        List<IPatternMatch> processed = new ArrayList<IPatternMatch>();
        for (IPatternMatch signature : signatures) {
            processDisappearedMatch(signature);
            processed.add(signature);
        }
        return processed;
    }

    protected abstract void processDisappearedMatch(IPatternMatch signature);

    protected void appendNotificationToList(ENotificationImpl notification) {
        notifications.add(notification);
    }

}
