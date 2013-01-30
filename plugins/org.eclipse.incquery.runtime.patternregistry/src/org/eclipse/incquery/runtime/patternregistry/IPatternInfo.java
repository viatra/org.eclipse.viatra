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

import java.util.List;

import org.eclipse.incquery.patternlanguage.patternLanguage.Annotation;
import org.eclipse.incquery.patternlanguage.patternLanguage.Pattern;
import org.eclipse.incquery.patternlanguage.patternLanguage.Variable;
import org.eclipse.incquery.runtime.api.IMatcherFactory;
import org.eclipse.incquery.runtime.api.IPatternMatch;
import org.eclipse.incquery.runtime.api.IncQueryMatcher;

public interface IPatternInfo {

    public PatternTypeEnum getPatternTypeEnum();

    public Pattern getPattern();

    public IMatcherFactory<IncQueryMatcher<IPatternMatch>> getMatcherFactory();

    public String getId();

    public String getFqn();

    public String getSource();

    public List<Annotation> getAnnotations();

    public List<Variable> getParameters();

    public List<IPatternInfo> getPatternDependecies();

}
