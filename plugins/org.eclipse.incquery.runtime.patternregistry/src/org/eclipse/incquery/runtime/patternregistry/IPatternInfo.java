/*******************************************************************************
 * Copyright (c) 2010-2013, Andras Okros, Istvan Rath and Daniel Varro
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

import org.eclipse.core.resources.IFile;
import org.eclipse.incquery.patternlanguage.patternLanguage.Annotation;
import org.eclipse.incquery.patternlanguage.patternLanguage.Pattern;
import org.eclipse.incquery.patternlanguage.patternLanguage.Variable;
import org.eclipse.incquery.runtime.api.IQuerySpecification;

public interface IPatternInfo {

    public PatternTypeEnum getPatternTypeEnum();

    public Pattern getPattern();

    public IQuerySpecification<?> getQuerySpecification();

    public String getId();

    public String getFqn();

    public IFile getRelatedFile();

    public boolean isActive();

    public void setActive(boolean active);

    public List<Annotation> getAnnotations();

    public List<Variable> getParameters();

    public List<IPatternInfo> getPatternDependecies();

}
