/*******************************************************************************
 * Copyright (c) 2010-2016, Balázs Grill, Zoltan Ujhelyi, IncQuery Labs Ltd.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Grill Balázs, Zoltan Ujhelyi - initial API and implementation
 *******************************************************************************/
package org.eclipse.viatra.query.runtime.api;


import java.util.Objects;

import org.eclipse.viatra.query.runtime.matchers.backend.IQueryBackendFactory;
import org.eclipse.viatra.query.runtime.matchers.backend.QueryEvaluationHint;
import org.eclipse.viatra.query.runtime.rete.matcher.ReteBackendFactory;

/**
 * This class is intended to provide options to a created {@link ViatraQueryEngine} instance. The {@link #DEFAULT}
 * instance represents the configuration that is selected when no explicit options are provided by the user. To create
 * new configurations, use the static builder methods {@link #defineOptions()} (starts with empty options) or
 * {@link #copyOptions(ViatraQueryEngineOptions)} (starts with all options from an existing configuration).
 * 
 * @author Balázs Grill, Zoltan Ujhelyi
 * @since 1.4
 *
 */
public final class ViatraQueryEngineOptions {

    private final QueryEvaluationHint engineDefaultHints;

    private final IQueryBackendFactory defaultCachingBackendFactory;

    /** The default engine options; if options are not defined, this version will be used. */
    public static final ViatraQueryEngineOptions DEFAULT = new Builder().build();

    public static final class Builder {
        private QueryEvaluationHint engineDefaultHints;

        private IQueryBackendFactory defaultBackendFactory;
        private IQueryBackendFactory defaultCachingBackendFactory;

        public Builder() {

        }

        public Builder(ViatraQueryEngineOptions from) {
            this.engineDefaultHints = from.engineDefaultHints;
            this.defaultBackendFactory = engineDefaultHints.getQueryBackendFactory();
            this.defaultCachingBackendFactory = from.defaultCachingBackendFactory;
        }

        /**
         * Note that the backend factory in the hint is overridden by a factory added with
         * {@link #withDefaultBackend(IQueryBackendFactory)}.
         */
        public Builder withDefaultHint(QueryEvaluationHint engineDefaultHints) {
            this.engineDefaultHints = engineDefaultHints;
            return this;
        }

        /**
         * Note that this backend factory overrides the factory defined by the hint added by
         * {@link #withDefaultHint(QueryEvaluationHint)}.
         */
        public Builder withDefaultBackend(IQueryBackendFactory defaultBackendFactory) {
            this.defaultBackendFactory = defaultBackendFactory;
            return this;
        }

        public Builder withDefaultCachingBackend(IQueryBackendFactory defaultCachingBackendFactory) {
            this.defaultCachingBackendFactory = defaultCachingBackendFactory;
            return this;
        }

        public ViatraQueryEngineOptions build() {
            IQueryBackendFactory defaultFactory = getDefaultBackend();
            IQueryBackendFactory defaultCachingFactory = getDefaultCachingBackend();
            QueryEvaluationHint hint = getEngineDefaultHints(defaultFactory);
            return new ViatraQueryEngineOptions(hint, defaultCachingFactory);
        }

        private IQueryBackendFactory getDefaultBackend() {
            if (defaultBackendFactory != null){
                return defaultBackendFactory;
            } else if (engineDefaultHints != null) {
                return engineDefaultHints.getQueryBackendFactory();
            } else {
                return new ReteBackendFactory();
            }
        }

        private IQueryBackendFactory getDefaultCachingBackend() {
            if (defaultCachingBackendFactory != null) {
                return defaultBackendFactory;
            } else {
                return new ReteBackendFactory(); // TODO this should be defaultFactory if it is caching
            }
        }

        private QueryEvaluationHint getEngineDefaultHints(IQueryBackendFactory defaultFactory) {
            if (engineDefaultHints != null){
                return engineDefaultHints.overrideBy(new QueryEvaluationHint(null, defaultFactory));
            } else {
                return new QueryEvaluationHint(null, defaultFactory);
            }
        }
    }

    /**
     * Initializes an option builder with no previously set options.
     * 
     * @return
     */
    public static Builder defineOptions() {
        return new Builder();
    }

    /**
     * Initializes an option builder with settings from an existing configuration.
     * 
     * @param options
     * @return
     */
    public static Builder copyOptions(ViatraQueryEngineOptions options) {
        return new Builder(options);
    }

    private ViatraQueryEngineOptions(QueryEvaluationHint engineDefaultHints, IQueryBackendFactory defaultCachingBackendFactory) {
        this.engineDefaultHints = engineDefaultHints;
        this.defaultCachingBackendFactory = defaultCachingBackendFactory;
    }

    public QueryEvaluationHint getEngineDefaultHints() {
        return engineDefaultHints;
    }

    /**
     * Returns the configured default backend
     * 
     * @return the defaultBackendFactory
     */
    public IQueryBackendFactory getDefaultBackendFactory() {
        return engineDefaultHints.getQueryBackendFactory();
    }

    /**
     * Returns the configured default caching backed. If the default backend caches matches, it is usually expected, but
     * not mandatory for the two default backends to be the same.
     * 
     * @return
     */
    public IQueryBackendFactory getDefaultCachingBackendFactory() {
        return defaultCachingBackendFactory;
    }

    @Override
    public String toString() {
        // TODO defaultCachingBackendFactory is ignored
        if(Objects.equals(engineDefaultHints, DEFAULT.engineDefaultHints)) 
            return "defaults";
        else
            return engineDefaultHints.toString();
    }
}
