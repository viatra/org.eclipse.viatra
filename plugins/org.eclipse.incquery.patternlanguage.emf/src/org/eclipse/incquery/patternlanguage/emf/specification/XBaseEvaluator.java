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
package org.eclipse.incquery.patternlanguage.emf.specification;

import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.incquery.patternlanguage.emf.internal.XtextInjectorProvider;
import org.eclipse.incquery.patternlanguage.emf.util.IClassLoaderProvider;
import org.eclipse.incquery.patternlanguage.helper.CorePatternLanguageHelper;
import org.eclipse.incquery.patternlanguage.patternLanguage.Pattern;
import org.eclipse.incquery.patternlanguage.patternLanguage.PatternBody;
import org.eclipse.incquery.patternlanguage.patternLanguage.Variable;
import org.eclipse.incquery.runtime.exception.IncQueryException;
import org.eclipse.incquery.runtime.matchers.psystem.IExpressionEvaluator;
import org.eclipse.incquery.runtime.matchers.psystem.IValueProvider;
import org.eclipse.xtext.EcoreUtil2;
import org.eclipse.xtext.naming.IQualifiedNameConverter;
import org.eclipse.xtext.util.CancelIndicator;
import org.eclipse.xtext.xbase.XExpression;
import org.eclipse.xtext.xbase.interpreter.IEvaluationContext;
import org.eclipse.xtext.xbase.interpreter.IEvaluationResult;
import org.eclipse.xtext.xbase.interpreter.impl.XbaseInterpreter;

import com.google.common.base.Function;
import com.google.common.collect.Iterables;
import com.google.inject.Inject;
import com.google.inject.Provider;

/**
 * Evaluates an XBase XExpression inside Rete.
 */
@SuppressWarnings("restriction")
public class XBaseEvaluator implements IExpressionEvaluator{

    @Inject
    private Logger logger;

    private final XExpression xExpression;
    private final Pattern pattern;

    @Inject
    private XbaseInterpreter interpreter;
    @Inject
    private Provider<IEvaluationContext> contextProvider;
    @Inject
    private IQualifiedNameConverter nameConverter;
    @Inject
    private IClassLoaderProvider classLoaderProvider;

    private Iterable<String> usedNames;

    private ClassLoader classLoader;

    /**
     * @param xExpression
     *            the expression to evaluate
     * @param qualifiedMapping
     *            maps variable qualified names to positions.
     * @param pattern
     */
    public XBaseEvaluator(XExpression xExpression, Pattern pattern) {
        super();
        XtextInjectorProvider.INSTANCE.getInjector().injectMembers(this);
        this.xExpression = xExpression;
        this.pattern = pattern;
        try {
            classLoader = classLoaderProvider.getClassLoader(pattern);
            if (classLoader != null) {
                interpreter.setClassLoader(classLoader);
            }
        } catch (IncQueryException e) {
            logger.error("XBase Java evaluator extension point initialization failed.", e);
        }

        PatternBody body = EcoreUtil2.getContainerOfType(xExpression, PatternBody.class);
        List<Variable> usedVariables = CorePatternLanguageHelper.getUsedVariables(xExpression, body.getVariables());
        usedNames = Iterables.transform(usedVariables, new Function<Variable, String>() {
           @Override
           public String apply(Variable var) {
               return var.getName();
           }
        });
    }

    @Override
    public Iterable<String> getInputParameterNames() {
        return usedNames;
    }

    public XExpression getExpression() {
        return xExpression;
    }

    @Override
    public Object evaluateExpression(IValueProvider provider) throws Exception {

        IEvaluationContext context = contextProvider.get();
        for (String name : getInputParameterNames()) {
            context.newValue(nameConverter.toQualifiedName(name), provider.getValue(name));
        }
        IEvaluationResult result = interpreter.evaluate(xExpression, context, CancelIndicator.NullImpl);
        if (result == null)
            throw new IncQueryException(String.format(
                    "XBase expression interpreter returned no result while evaluating expression %s in pattern %s.",
                    xExpression, pattern), "XBase expression interpreter returned no result.");
        Throwable throwable = result.getException();
        if (throwable instanceof Error) {
            throw (Error) throwable;
        } else if (throwable instanceof Exception) {
            throw (Exception) throwable;
        } else if (throwable != null) {
            throw new IncQueryException(String.format("Strange throwable (%s) encountered: %s", throwable.getClass()
                    .getCanonicalName(), throwable.getMessage()), "Strange throwable encountered", throwable);
        }
        return result.getResult();
    }

    @Override
    public String getShortDescription() {
        return xExpression.toString();
    }

}
