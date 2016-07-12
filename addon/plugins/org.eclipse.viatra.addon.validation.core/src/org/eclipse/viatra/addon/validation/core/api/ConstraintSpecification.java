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

package org.eclipse.viatra.addon.validation.core.api;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.viatra.query.runtime.api.IPatternMatch;
import org.eclipse.viatra.query.runtime.api.IQuerySpecification;
import org.eclipse.viatra.query.runtime.api.ViatraQueryMatcher;
import org.eclipse.viatra.query.runtime.matchers.psystem.queries.PParameter;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;

/**
 * Generic implementation of the IConstraintSpecification interface for code buildable constraint specifications.
 * 
 * @author Balint Lorand
 *
 */
public class ConstraintSpecification implements IConstraintSpecification {

    private final IQuerySpecification<? extends ViatraQueryMatcher<? extends IPatternMatch>> querySpecification;
    private final String messageFormat;
    private final Severity severity;
    private final List<String> keyNames;
    private final List<String> propertyNames;
    private final Set<List<String>> symmetricKeyNames;
    private final Set<List<String>> symmetricPropertyNames;

    private ConstraintSpecification(ConstraintSpecificationBuilder builder) {
        this.querySpecification = builder.querySpecification;
        this.messageFormat = builder.messageFormat;
        this.severity = builder.severity;

        List<PParameter> parameters = querySpecification.getParameters();

        if (!builder.keys.isEmpty()) {
            this.keyNames = builder.keys;
        } else {
            this.keyNames = new ArrayList<String>();
            for (PParameter parameter : parameters) {
                String parameterName = parameter.getName();
                this.keyNames.add(parameterName);
            }
        }

        this.propertyNames = new ArrayList<String>();

        for (PParameter parameter : parameters) {
            String parameterName = parameter.getName();
            if (!keyNames.contains(parameterName)) {
                propertyNames.add(parameterName);
            }
        }

        this.symmetricKeyNames = new HashSet<List<String>>();
        this.symmetricPropertyNames = new HashSet<List<String>>();
        for (List<String> symmetrics : builder.symmetricParameterNames) {
            if (keyNames.containsAll(symmetrics)) {
                symmetricKeyNames.add(symmetrics);
            } else if (propertyNames.containsAll(symmetrics)) {
                symmetricPropertyNames.add(symmetrics);
            }
        }

    }

    @Override
    public String getMessageFormat() {
        return messageFormat;
    }

    @Override
    public Map<String, Object> getKeyObjects(IPatternMatch signature) {
        Map<String, Object> map = new HashMap<String, Object>();
        for (String key : keyNames) {
            Object keyObject = signature.get(key);
            map.put(key, keyObject);
        }
        return map;
    }

    @Override
    public List<String> getKeyNames() {
        return ImmutableList.copyOf(keyNames);
    }

    @Override
    public List<String> getPropertyNames() {
        return ImmutableList.copyOf(propertyNames);
    }

    @Override
    public Severity getSeverity() {
        return severity;
    }

    @Override
    public Set<List<String>> getSymmetricPropertyNames() {
        return ImmutableSet.copyOf(symmetricPropertyNames);
    }

    @Override
    public Set<List<String>> getSymmetricKeyNames() {
        return ImmutableSet.copyOf(symmetricKeyNames);
    }

    @Override
    public IQuerySpecification<? extends ViatraQueryMatcher<? extends IPatternMatch>> getQuerySpecification() {
        return querySpecification;
    }

    /**
     * Static builder class provided to construct ConstraintSpecification instances.
     * 
     * @author Balint Lorand
     *
     */
    public static class ConstraintSpecificationBuilder {
        private final IQuerySpecification<? extends ViatraQueryMatcher<? extends IPatternMatch>> querySpecification;
        private final String messageFormat;
        private final Severity severity;
        private List<String> keys;
        private Set<List<String>> symmetricParameterNames;

        public ConstraintSpecificationBuilder(
                IQuerySpecification<? extends ViatraQueryMatcher<? extends IPatternMatch>> querySpecification,
                String messageFormat, Severity severity) {
            this.querySpecification = querySpecification;
            this.messageFormat = messageFormat;
            this.severity = severity;
            this.keys = new ArrayList<String>();
            this.symmetricParameterNames = new HashSet<List<String>>();

            if (this.querySpecification == null) {
                throw new IllegalStateException("A queryspecification must be provided!");
            }
            if (this.messageFormat == null) {
                throw new IllegalStateException("A message format must be provided!");
            }
        }

        public ConstraintSpecificationBuilder keyNames(List<String> keyNames) {
            this.keys = keyNames;
            return this;
        }

        public ConstraintSpecificationBuilder symmetricParameters(List<String> symmetricParameters) {

            List<PParameter> parameters = querySpecification.getParameters();
            List<String> parameterNames = new ArrayList<String>();
            for (PParameter parameter : parameters) {
                String parameterName = parameter.getName();
                parameterNames.add(parameterName);
            }

            for (String symmetricParameter : symmetricParameters) {
                if (!parameterNames.contains(symmetricParameter)) {
                    throw new IllegalStateException("A provided symmetric parameter name (" + symmetricParameter
                            + ") is not in the query specification's pattern's parameter list!");
                }
            }

            if (!keys.containsAll(symmetricParameters)) {
                for (String symmetricParameter : symmetricParameters) {
                    if (keys.contains(symmetricParameter)) {
                        throw new IllegalStateException(
                                "The provided symmetric parameter names must be either all key parameters or neither of them can be a key parameter!");
                    }
                }
            }

            this.symmetricParameterNames.add(symmetricParameters);
            return this;
        }

        public ConstraintSpecification build() {
            ConstraintSpecification constraintSpecification = new ConstraintSpecification(this);

            List<PParameter> parameters = constraintSpecification.getQuerySpecification().getParameters();
            List<String> parameterNames = new ArrayList<String>();
            for (PParameter parameter : parameters) {
                String parameterName = parameter.getName();
                parameterNames.add(parameterName);
            }
            if (!parameterNames.containsAll(constraintSpecification.getKeyNames())) {
                throw new IllegalStateException(
                        "A provided key parameter name is not in the query specification's pattern's parameter list!");
            }

            return constraintSpecification;
        }
    }
}
