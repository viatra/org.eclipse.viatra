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
package org.eclipse.viatra.query.patternlanguage.emf.specification;

import java.util.List;

import org.eclipse.viatra.query.patternlanguage.emf.internal.XtextInjectorProvider;
import org.eclipse.viatra.query.patternlanguage.emf.util.IClassLoaderProvider;
import org.eclipse.viatra.query.patternlanguage.helper.CorePatternLanguageHelper;
import org.eclipse.viatra.query.patternlanguage.patternLanguage.Pattern;
import org.eclipse.viatra.query.patternlanguage.patternLanguage.PatternBody;
import org.eclipse.viatra.query.patternlanguage.patternLanguage.Variable;
import org.eclipse.viatra.query.runtime.exception.ViatraQueryException;
import org.eclipse.viatra.query.runtime.matchers.psystem.IExpressionEvaluator;
import org.eclipse.viatra.query.runtime.matchers.psystem.IValueProvider;
import org.eclipse.xtext.EcoreUtil2;
import org.eclipse.xtext.naming.IQualifiedNameConverter;
import org.eclipse.xtext.util.CancelIndicator;
import org.eclipse.xtext.xbase.XExpression;
import org.eclipse.xtext.xbase.interpreter.IEvaluationContext;
import org.eclipse.xtext.xbase.interpreter.IEvaluationResult;
import org.eclipse.xtext.xbase.interpreter.impl.XbaseInterpreter;

import com.google.common.collect.Iterables;
import com.google.inject.Inject;
import com.google.inject.Provider;

/**
 * Evaluates an XBase XExpression inside Rete.
 */
@SuppressWarnings("restriction")
public class XBaseEvaluator implements IExpressionEvaluator{

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

    /**
     * @param xExpression
     *            the expression to evaluate
     * @param pattern
     */
    public XBaseEvaluator(XExpression xExpression, Pattern pattern) {
        super();
        XtextInjectorProvider.INSTANCE.getInjector().injectMembers(this);
        this.xExpression = xExpression;
        this.pattern = pattern;
        ClassLoader classLoader = classLoaderProvider.getClassLoader(pattern);
        if (classLoader != null) {
            interpreter.setClassLoader(classLoader);
        }
        PatternBody body = EcoreUtil2.getContainerOfType(xExpression, PatternBody.class);
        List<Variable> usedVariables = CorePatternLanguageHelper.getUsedVariables(xExpression, body.getVariables());
        usedNames = Iterables.transform(usedVariables, Variable::getName);
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
            throw new ViatraQueryException(String.format(
                    "XBase expression interpreter returned no result while evaluating expression %s in pattern %s.",
                    xExpression, pattern), "XBase expression interpreter returned no result.");
        Throwable throwable = result.getException();
        if (throwable instanceof Error) {
            throw (Error) throwable;
        } else if (throwable instanceof Exception) {
            throw (Exception) throwable;
        } else if (throwable != null) {
            throw new ViatraQueryException(String.format("Strange throwable (%s) encountered: %s", throwable.getClass()
                    .getCanonicalName(), throwable.getMessage()), "Strange throwable encountered", throwable);
        }
        return result.getResult();
    }

    @Override
    public String getShortDescription() {
        return xExpression.toString();
    }

}
