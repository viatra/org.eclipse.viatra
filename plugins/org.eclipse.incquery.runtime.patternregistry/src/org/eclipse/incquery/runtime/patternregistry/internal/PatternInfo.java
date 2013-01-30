/*******************************************************************************
 * Copyright (c) 2010-2012, Andras Okros, Istvan Rath and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Andras Okros - initial API and implementation
 *******************************************************************************/
package org.eclipse.incquery.runtime.patternregistry.internal;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.incquery.patternlanguage.patternLanguage.Annotation;
import org.eclipse.incquery.patternlanguage.patternLanguage.Pattern;
import org.eclipse.incquery.patternlanguage.patternLanguage.Variable;
import org.eclipse.incquery.runtime.api.IMatcherFactory;
import org.eclipse.incquery.runtime.api.IPatternMatch;
import org.eclipse.incquery.runtime.api.IncQueryMatcher;
import org.eclipse.incquery.runtime.patternregistry.IPatternInfo;
import org.eclipse.incquery.runtime.patternregistry.PatternRegistryUtil;
import org.eclipse.incquery.runtime.patternregistry.PatternTypeEnum;

public class PatternInfo implements IPatternInfo {

    private final PatternTypeEnum patternTypeEnum;

    private final Pattern pattern;

    private final IMatcherFactory<IncQueryMatcher<IPatternMatch>> matcherFactory;

    private final String id;

    private final String fqn;

    private final String source;

    private final List<Annotation> annotations;

    private final List<Variable> parameters;

    private final List<IPatternInfo> patternDependecies;

    // package info (?)

    public PatternInfo(PatternTypeEnum patternTypeEnum, Pattern pattern,
            IMatcherFactory<IncQueryMatcher<IPatternMatch>> matcherFactory) {
        super();
        this.patternTypeEnum = patternTypeEnum;
        this.pattern = pattern;
        this.matcherFactory = matcherFactory;
        this.fqn = PatternRegistryUtil.getFQN(pattern);
        this.source = "TODO";
        this.id = PatternRegistryUtil.getUniquePatternIdentifier(pattern);

        List<Annotation> patternAnnotations = pattern.getAnnotations();
        annotations = Collections.unmodifiableList(patternAnnotations);

        List<Variable> patternParameters = pattern.getParameters();
        parameters = Collections.unmodifiableList(patternParameters);

        // FIXME do it follow up the dependencies
        patternDependecies = Collections.unmodifiableList(new ArrayList<IPatternInfo>());
    }

    @Override
    public PatternTypeEnum getPatternTypeEnum() {
        return patternTypeEnum;
    }

    @Override
    public Pattern getPattern() {
        return pattern;
    }

    @Override
    public IMatcherFactory<IncQueryMatcher<IPatternMatch>> getMatcherFactory() {
        return matcherFactory;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public String getFqn() {
        return fqn;
    }

    @Override
    public String getSource() {
        return source;
    }

    @Override
    public List<Annotation> getAnnotations() {
        return annotations;
    }

    @Override
    public List<Variable> getParameters() {
        return parameters;
    }

    @Override
    public List<IPatternInfo> getPatternDependecies() {
        return patternDependecies;
    }

}
