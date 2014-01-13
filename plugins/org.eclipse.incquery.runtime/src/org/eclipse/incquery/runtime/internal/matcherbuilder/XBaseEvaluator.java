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

import java.net.MalformedURLException;
import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.Platform;
import org.eclipse.incquery.patternlanguage.helper.CorePatternLanguageHelper;
import org.eclipse.incquery.patternlanguage.patternLanguage.Pattern;
import org.eclipse.incquery.patternlanguage.patternLanguage.PatternBody;
import org.eclipse.incquery.patternlanguage.patternLanguage.Variable;
import org.eclipse.incquery.runtime.IExtensions;
import org.eclipse.incquery.runtime.exception.IncQueryException;
import org.eclipse.incquery.runtime.extensibility.IMatchChecker;
import org.eclipse.incquery.runtime.internal.XtextInjectorProvider;
import org.eclipse.incquery.runtime.matchers.psystem.IExpressionEvaluator;
import org.eclipse.incquery.runtime.matchers.psystem.IValueProvider;
import org.eclipse.incquery.runtime.util.ClassLoaderUtil;
import org.eclipse.incquery.runtime.util.ExpressionUtil;
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

    private IMatchChecker matchChecker;

    @Inject
    private XbaseInterpreter interpreter;
    @Inject
    private Provider<IEvaluationContext> contextProvider;
    @Inject
    private IQualifiedNameConverter nameConverter;

    private Iterable<String> usedNames;

    private boolean initialized = false;

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

        PatternBody body = EcoreUtil2.getContainerOfType(xExpression, PatternBody.class);
        List<Variable> usedVariables = CorePatternLanguageHelper.getUsedVariables(xExpression, body.getVariables());
        usedNames = Iterables.transform(usedVariables, new Function<Variable, String>() {
           @Override
           public String apply(Variable var) {
               return var.getName();
           }
        });
    }

    /**
     * make sure to call this after members have been injected.
     */
    public void init() {
        /* 
         * EMF-IncQuery is single-threaded, however re-entrant calls to init should not cause problems,
         *  only minor performance decrease.
         */
        if (!initialized) {
            // First try to setup the generated code from the extension point
            IConfigurationElement[] configurationElements = Platform.getExtensionRegistry()
                    .getConfigurationElementsFor(IExtensions.XEXPRESSIONEVALUATOR_EXTENSION_POINT_ID);
            for (IConfigurationElement configurationElement : configurationElements) {
                String id = configurationElement.getAttribute("id");
                if (id.equals(ExpressionUtil.getExpressionUniqueID(pattern, xExpression))) {
                    Object object = null;
                    try {
                        object = configurationElement.createExecutableExtension("evaluatorClass");
                    } catch (CoreException coreException) {
                        logger.error("XBase Java evaluator extension point initialization failed.", coreException);
                    }
                    if (object instanceof IMatchChecker) {
                        matchChecker = (IMatchChecker) object;
                    }
                }
            }

            // Second option, setup the attributes for the interpreted approach
            if (matchChecker == null) {
                try {
                    ClassLoader classLoader = ClassLoaderUtil.getClassLoader(ExpressionUtil.getIFile(pattern));
                    if (classLoader != null) {
                        interpreter.setClassLoader(ClassLoaderUtil.getClassLoader(ExpressionUtil.getIFile(pattern)));
                    }
                } catch (MalformedURLException malformedURLException) {
                    logger.error("XBase Java evaluator extension point initialization failed.", malformedURLException);
                } catch (CoreException coreException) {
                    logger.error("XBase Java evaluator extension point initialization failed.", coreException);
                }
            }

            if (initialized) {
                logger.warn("Re-entrant call to XBase Java evaluator initialization!");
            }
            initialized = true;
        }
    }

    @Override
    public Iterable<String> getInputParameterNames() {
        return usedNames;
    }

    @Override
    public Object evaluateExpression(IValueProvider provider) throws Exception {
        
        
        init();
        
        // First option: try to evalute with the generated code
        if (matchChecker != null) {
            return matchChecker.evaluateExpression(provider);
        }

        // Second option: try to evaluate with the interpreted approach
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
