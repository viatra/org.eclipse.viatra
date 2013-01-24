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
package org.eclipse.incquery.runtime.patternregistry;

import java.util.Collections;
import java.util.List;

import org.eclipse.incquery.patternlanguage.patternLanguage.Annotation;
import org.eclipse.incquery.patternlanguage.patternLanguage.Pattern;
import org.eclipse.incquery.patternlanguage.patternLanguage.Variable;

public class PatternInfo {

    private final Pattern pattern;

    // Source + FQN
    private final String id;

    private final String fqn;

    // Plugin or workspace, +classpath?
    private String source;

    // First prototype just throw them in here, should be changed
    private List<Annotation> annotations;

    // First prototype just throw them in here, should be changed
    private List<Variable> parameters;

    private List<PatternInfo> patternDependecies;

    // package info (?)

    // Maybe refactor this into a factory
    public PatternInfo(Pattern pattern) {
        super();
        this.pattern = pattern;
        this.id = PatternRegistryUtil.getUniquePatternIdentifier(pattern);
        this.fqn = PatternRegistryUtil.getFQN(pattern);

        List<Annotation> patternAnnotations = pattern.getAnnotations();
        if (!patternAnnotations.isEmpty()) {
            annotations = Collections.unmodifiableList(patternAnnotations);
        }

        List<Variable> patternParameters = pattern.getParameters();
        if (!patternParameters.isEmpty()) {
            parameters = Collections.unmodifiableList(patternParameters);
        }
    }

    public Pattern getPattern() {
        return pattern;
    }

    public String getId() {
        return id;
    }

    public String getFqn() {
        return fqn;
    }

    public String getSource() {
        return source;
    }

    public List<Annotation> getAnnotations() {
        return annotations;
    }

    public List<Variable> getParameters() {
        return parameters;
    }

    public List<PatternInfo> getPatternDependecies() {
        return patternDependecies;
    }

}
