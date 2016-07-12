/*******************************************************************************
 * Copyright (c) 2010-2014, Balint Lorand, Zoltan Ujhelyi, Abel Hegedus, Tamas Szabo, Istvan Rath and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Zoltan Ujhelyi, Abel Hegedus, Tamas Szabo - original initial API and implementation
 *   Balint Lorand - revised API and implementation
 *******************************************************************************/

package org.eclipse.viatra.addon.validation.core;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.eclipse.viatra.addon.validation.core.api.IConstraint;
import org.eclipse.viatra.addon.validation.core.api.IConstraintSpecification;
import org.eclipse.viatra.addon.validation.core.api.IViolation;
import org.eclipse.viatra.addon.validation.core.api.IViolationFilter;
import org.eclipse.viatra.addon.validation.core.listeners.ConstraintListener;
import org.eclipse.viatra.addon.validation.core.violationkey.CompositeSymmetricViolationKey;
import org.eclipse.viatra.addon.validation.core.violationkey.CompositeViolationKey;
import org.eclipse.viatra.addon.validation.core.violationkey.SimpleViolationKey;
import org.eclipse.viatra.addon.validation.core.violationkey.ViolationKey;
import org.eclipse.viatra.query.runtime.api.IPatternMatch;
import org.eclipse.viatra.query.runtime.api.ViatraQueryMatcher;
import org.eclipse.viatra.query.runtime.exception.ViatraQueryException;
import org.eclipse.viatra.transformation.evm.api.RuleSpecification;

import com.google.common.collect.ImmutableSet;

public class Constraint implements IConstraint {

    private Logger logger;

    protected Constraint(IConstraintSpecification specification, ValidationEngine validationEngine, Logger logger) {
        this.specification = specification;
        this.validationEngine = validationEngine;
        this.logger = logger;
        this.violationsMap = new HashMap<ViolationKey, Violation>();
        this.listeners = new HashMap<ConstraintListener, IViolationFilter>();
    }

    private IConstraintSpecification specification;

    @Override
    public IConstraintSpecification getSpecification() {
        return specification;
    }

    private ValidationEngine validationEngine;

    protected ValidationEngine getValidationEngine() {
        return validationEngine;
    }

    private RuleSpecification<IPatternMatch> ruleSpecification;

    protected RuleSpecification<IPatternMatch> getRuleSpecification() {
        return ruleSpecification;
    }

    protected void setRuleSpecification(RuleSpecification<IPatternMatch> ruleSpecification) {
        this.ruleSpecification = ruleSpecification;
    }

    private Map<ViolationKey, Violation> violationsMap;

    @SuppressWarnings("unchecked")
    @Override
    public Collection<IViolation> getStoredViolations() {
        return (Collection<IViolation>) (Collection<?>) violationsMap.values();
    }

    protected void addViolation(ViolationKey key, Violation violation) {
        violationsMap.put(key, violation);
    }

    protected Violation getViolation(ViolationKey key) {
        return violationsMap.get(key);
    }

    protected Violation removeViolation(ViolationKey key) {
        return violationsMap.remove(key);
    }

    @SuppressWarnings("unchecked")
    @Override
    public Collection<IViolation> listViolations() {
        ViatraQueryMatcher<? extends IPatternMatch> matcher = null;

        try {
            matcher = validationEngine.getQueryEngine().getMatcher(specification.getQuerySpecification());
        } catch (ViatraQueryException e) {
            logger.error(e.getMessage(), e);
            return Collections.emptyList();
        }

        Map<ViolationKey, Violation> violationMap = new HashMap<ViolationKey, Violation>();
        ViolationCreationProcessor violationCreationProcessor = new ViolationCreationProcessor(this, logger,
                violationMap);
        matcher.forEachMatch(violationCreationProcessor);
        return (Collection<IViolation>) (Collection<?>) violationMap.values();

    }

    @Override
    public Collection<IViolation> listViolations(IViolationFilter filter) {
        ViatraQueryMatcher<? extends IPatternMatch> matcher = null;

        try {
            matcher = validationEngine.getQueryEngine().getMatcher(specification.getQuerySpecification());
        } catch (ViatraQueryException e) {
            logger.error(e.getMessage(), e);
            return Collections.emptyList();
        }

        Map<ViolationKey, Violation> violationMap = new HashMap<ViolationKey, Violation>();
        ViolationCreationProcessor violationCreationProcessor = new ViolationCreationProcessor(this, logger,
                violationMap);
        matcher.forEachMatch(violationCreationProcessor);

        Set<IViolation> violations = new HashSet<IViolation>();
        for (Map.Entry<ViolationKey, Violation> entry : violationMap.entrySet()) {
            if (filter.apply(entry.getValue())) {
                violations.add(entry.getValue());
            }
        }

        return violations;

    }

    private Map<ConstraintListener, IViolationFilter> listeners;

    @Override
    public Set<ConstraintListener> getListeners() {
        return ImmutableSet.copyOf(listeners.keySet());
    }

    @Override
    public boolean addListener(ConstraintListener listener) {
        boolean isNew = listeners.put(listener, null) == null;

        if (ruleSpecification == null) {
            validationEngine.addRuleSpecificationToExecutionSchema(this);
        }

        return isNew;
    }

    @Override
    public boolean addListener(ConstraintListener listener, IViolationFilter filter) {
        boolean isNew = listeners.put(listener, filter) == null;

        if (ruleSpecification == null) {
            validationEngine.addRuleSpecificationToExecutionSchema(this);
        }

        return isNew;
    }

    @Override
    public boolean removeListener(ConstraintListener listener) {
        boolean result = listeners.remove(listener) != null;

        if (listeners.isEmpty()) {
            validationEngine.removeRuleSpecificationFromExecutionSchema(this);
        }

        return result;
    }

    protected void notifyListenersViolationAppeared(Violation violation) {
        for (Map.Entry<ConstraintListener, IViolationFilter> entry : listeners.entrySet()) {
            if (entry.getValue() == null || entry.getValue().apply(violation)) {
                entry.getKey().violationAppeared(violation);
            }
        }
    }

    protected void notifyListenersViolationDisappeared(Violation violation) {
        for (Map.Entry<ConstraintListener, IViolationFilter> entry : listeners.entrySet()) {
            if (entry.getValue() == null || entry.getValue().apply(violation)) {
                entry.getKey().violationDisappeared(violation);
            }
        }
    }

    protected ViolationKey getViolationKey(IPatternMatch match) {
        Map<String, Object> keyObjectMap = this.getSpecification().getKeyObjects(match);
        Set<List<String>> symmetricKeyNames = this.getSpecification().getSymmetricKeyNames();

        if (keyObjectMap.size() < 2) {
            return new SimpleViolationKey(keyObjectMap.values().iterator().next());
        } else if (symmetricKeyNames.isEmpty()) {
            return new CompositeViolationKey(keyObjectMap);
        } else {
            return new CompositeSymmetricViolationKey(keyObjectMap, symmetricKeyNames);
        }
    }

    protected ViolationKey getViolationKey(Map<String, Object> keyObjectMap) {
        Set<List<String>> symmetricKeyNames = this.getSpecification().getSymmetricKeyNames();

        if (keyObjectMap.size() < 2) {
            return new SimpleViolationKey(keyObjectMap.values().iterator().next());
        } else if (symmetricKeyNames.isEmpty()) {
            return new CompositeViolationKey(keyObjectMap);
        } else {
            return new CompositeSymmetricViolationKey(keyObjectMap, symmetricKeyNames);
        }
    }
}
