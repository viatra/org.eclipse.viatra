/*******************************************************************************
 * Copyright (c) 2010-2012, Zoltan Ujhelyi, IncQuery Labs Ltd.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-v20.html.
 * 
 * SPDX-License-Identifier: EPL-2.0
 *******************************************************************************/
package org.eclipse.viatra.query.patternlanguage.emf.tests.util;

import java.lang.reflect.InvocationTargetException;

import org.eclipse.xtext.testing.IInjectorProvider;
import org.eclipse.xtext.testing.IRegistryConfigurator;
import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;

import com.google.common.collect.ClassToInstanceMap;
import com.google.common.collect.MutableClassToInstanceMap;
import com.google.inject.Injector;

/**
 * 
 * Reimplements {@link org.eclipse.xtext.testing.XtextRunner} as a test rule, allowing it to be combined with other runners, such as ParameterizedTestRunners.
 *
 */
public class XtextInfrastructureRule implements TestRule {

    private static ClassToInstanceMap<IInjectorProvider> injectorProviderClassCache = MutableClassToInstanceMap.create();
    private final IInjectorProvider injectorProvider;

    public XtextInfrastructureRule(Object testCase, Class<? extends IInjectorProvider> klass) {
        injectorProvider = injectorProviderClassCache.computeIfAbsent(klass, kl -> {
            try {
                return klass.getDeclaredConstructor().newInstance();
            } catch (InstantiationException | IllegalAccessException | IllegalArgumentException
                    | InvocationTargetException | NoSuchMethodException | SecurityException e) {
                throw new RuntimeException(e);
            }
           });
        if (injectorProvider != null) {
            Injector injector = injectorProvider.getInjector();
            if (injector != null)
                injector.injectMembers(testCase);
        }
    }

    @Override
    public Statement apply(Statement base, Description description) {
        if (injectorProvider instanceof IRegistryConfigurator) {
            final IRegistryConfigurator registryConfigurator = (IRegistryConfigurator) injectorProvider;
            registryConfigurator.setupRegistry();
            return new Statement() {
                @Override
                public void evaluate() throws Throwable {
                    try {
                        base.evaluate();
                    } finally {
                        registryConfigurator.restoreRegistry();
                    }
                }
            };
        } else {
            return base;
        }
    }

    public Injector getInjector() {
        return injectorProvider.getInjector();
    }
    
}
