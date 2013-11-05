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
package org.eclipse.incquery.querybasedfeatures.runtime;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.eclipse.emf.common.notify.Notifier;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.incquery.runtime.api.AdvancedIncQueryEngine;
import org.eclipse.incquery.runtime.api.IPatternMatch;
import org.eclipse.incquery.runtime.api.IncQueryEngineLifecycleListener;
import org.eclipse.incquery.runtime.api.IncQueryMatcher;
import org.eclipse.incquery.runtime.api.IncQueryModelUpdateListener;
import org.eclipse.incquery.runtime.api.IncQueryModelUpdateListener.ChangeLevel;
import org.eclipse.incquery.runtime.exception.IncQueryException;
import org.eclipse.incquery.runtime.extensibility.QuerySpecificationRegistry;
import org.eclipse.incquery.runtime.rete.misc.DeltaMonitor;
import org.eclipse.incquery.runtime.util.IncQueryLoggingUtil;

/**
 * @author Abel Hegedus
 * 
 * TODO add generics for type-safe API
 * 
 */
public abstract class QueryBasedFeature {

    /**
     * @author Abel Hegedus
     * 
     */
    private final class ModelUpdateListener implements IncQueryModelUpdateListener {
        @Override
        public void notifyChanged(ChangeLevel changeLevel) {
            beforeUpdate();
            dm.matchFoundEvents.removeAll(processNewMatches(dm.matchFoundEvents));
            dm.matchLostEvents.removeAll(processLostMatches(dm.matchLostEvents));
            afterUpdate();
            sendNotfications();
            // engineForMatcher().getLogger()
            // .error("[IncqueryFeatureHandler] Exception during update: " + e.getMessage(), e);
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
    private final class EngineLifecycleListener implements IncQueryEngineLifecycleListener {
        @Override
        public void matcherInstantiated(IncQueryMatcher<? extends IPatternMatch> matcher) {
        }

        @SuppressWarnings("unchecked")
        @Override
        public void engineWiped() {
            String patternName = matcher.getPatternName();
            try {
                matcher = (IncQueryMatcher<IPatternMatch>) QuerySpecificationRegistry
                        .getQuerySpecification(patternName).getMatcher(engineForMatcher());
            } catch (IncQueryException e) {
                engineForMatcher().getLogger().error(
                        "[QueryBasedFeature] Exception during wipe callback: " + e.getMessage(), e);
            }
            dm = matcher.newDeltaMonitor(false);
        }

        @Override
        public void engineDisposed() {
        }

        @Override
        public void engineBecameTainted() {
        }
    }

    private IncQueryMatcher<IPatternMatch> matcher;
    private DeltaMonitor<IPatternMatch> dm;
    private final EStructuralFeature feature;
    private String sourceParamName;
    private String targetParamName;

    private boolean keepCache = true;
    private boolean initialized = false;

    private final List<ENotificationImpl> notifications = new ArrayList<ENotificationImpl>();

    private ModelUpdateListener listener;
    private EngineLifecycleListener engineLifecycleListener;

    protected void initialize(final IncQueryMatcher<IPatternMatch> matcher, String sourceParamName,
            String targetParamName) {
        if (initialized) {
            IncQueryLoggingUtil.getDefaultLogger().error("[QueryBasedFeature] Feature already initialized!");
            return;
        }
        initialized = true;
        this.matcher = matcher;
        this.sourceParamName = sourceParamName;
        this.targetParamName = targetParamName;
        if (matcher.getPositionOfParameter(sourceParamName) == null) {
            engineForMatcher().getLogger().error(
                    "[QueryBasedFeature] Source parameter " + sourceParamName + " not found!");
        }
        if (targetParamName != null && matcher.getPositionOfParameter(targetParamName) == null) {
            engineForMatcher().getLogger().error(
                    "[QueryBasedFeature] Target parameter " + targetParamName + " not found!");
        }
        if ((targetParamName == null) != (getKind() == QueryBasedFeatureKind.COUNTER)) {
            engineForMatcher().getLogger().error(
                    "[QueryBasedFeature] Invalid configuration (no targetParamName needed for Counter)!");
        }
        // IPatternMatch partialMatch = matcher.newEmptyMatch();
        // partialMatch.set(sourceParamName, source);
        this.dm = matcher.newDeltaMonitor(true);
        engineLifecycleListener = new EngineLifecycleListener();
        listener = new ModelUpdateListener();
    }

    private void sendNotfications() {
        while (!notifications.isEmpty()) {
            ENotificationImpl remove = notifications.remove(0);
            // engineForMatcher().getLogger().logError(this + " : " +remove.toString());
            ((Notifier) remove.getNotifier()).eNotify(remove);
        }
    }

    /**
	 * 
	 */
    public QueryBasedFeature(EStructuralFeature feature, boolean keepCache) {
        this.feature = feature;
        this.keepCache = keepCache;
    }

    /**
     * @return the matcher
     */
    protected IncQueryMatcher<IPatternMatch> getMatcher() {
        return matcher;
    }

    /**
     * @return the feature
     */
    protected EStructuralFeature getFeature() {
        return feature;
    }

    /**
     * @return the sourceParamName
     */
    protected String getSourceParamName() {
        return sourceParamName;
    }

    /**
     * @return the targetParamName
     */
    protected String getTargetParamName() {
        return targetParamName;
    }

    /**
     * @return the keepCache
     */
    protected boolean isCached() {
        return keepCache;
    }

    /**
     * @return the initialized
     */
    protected boolean isInitialized() {
        return initialized;
    }

    /**
     * @return the kind
     */
    public abstract QueryBasedFeatureKind getKind();

    protected abstract void afterUpdate();

    protected abstract void beforeUpdate();

    /**
     * Call this once to start handling callbacks.
     */
    protected void startMonitoring() {
        AdvancedIncQueryEngine engine = engineForMatcher();

        engine.addLifecycleListener(engineLifecycleListener);
        engine.addModelUpdateListener(listener);
        listener.notifyChanged(ChangeLevel.MATCHSET);
    }

    protected AdvancedIncQueryEngine engineForMatcher() {
        return (AdvancedIncQueryEngine) matcher.getEngine();
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
