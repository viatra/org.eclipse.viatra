/*******************************************************************************
 * Copyright (c) 2004-2010 Gabor Bergmann and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Gabor Bergmann - initial API and implementation
 *******************************************************************************/

package org.eclipse.incquery.runtime.internal.matcherbuilder;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.emf.common.util.EList;
import org.eclipse.incquery.patternlanguage.patternLanguage.Pattern;
import org.eclipse.incquery.patternlanguage.patternLanguage.Variable;
import org.eclipse.incquery.runtime.rete.construction.IOperationCompiler;
import org.eclipse.incquery.runtime.rete.construction.IRetePatternBuilder;
import org.eclipse.incquery.runtime.rete.construction.RetePatternBuildException;
import org.eclipse.incquery.runtime.rete.matcher.IPatternMatcherContext;

/**
 * @author Bergmann Gábor
 * 
 */
public class EPMBuilder<Collector> implements IRetePatternBuilder<Pattern, Collector> {
    protected IOperationCompiler<Pattern, Collector> baseBuildable;
    protected IPatternMatcherContext context;

    /**
     * @param baseBuildable
     * @param context
     */
    public EPMBuilder(IOperationCompiler<Pattern, Collector> baseBuildable, IPatternMatcherContext context) {
        super();
        this.baseBuildable = baseBuildable;
        this.context = context;
    }

    @Override
    public IPatternMatcherContext getContext() {
        return context;
    }

    @Override
    public void refresh() {
        baseBuildable.reinitialize();
    }

    @Override
    public Collector construct(Pattern pattern) throws RetePatternBuildException {
        try {
            EPMBuildScaffold<Collector> epmBuildScaffold = new EPMBuildScaffold<Collector>(
                    baseBuildable, context);
            return epmBuildScaffold.construct(pattern);
        } catch (RuntimeException ex) {
            throw new RetePatternBuildException(
                    "Error during constructing Rete pattern matcher; please review Error Log and consult developers",
                    new String[0], "Error during pattern matcher construction", pattern, ex);
        }
    }

    @Override
    public Map<Object, Integer> getPosMapping(Pattern gtPattern) {
        HashMap<Object, Integer> result = new HashMap<Object, Integer>();
        EList<Variable> parameters = gtPattern.getParameters();
        for (int i = 0; i < parameters.size(); ++i)
            result.put(parameters.get(i), i);
        return result;
    }
}
