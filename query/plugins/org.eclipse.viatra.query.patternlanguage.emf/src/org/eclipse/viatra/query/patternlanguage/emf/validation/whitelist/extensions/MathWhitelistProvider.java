/*******************************************************************************
 * Copyright (c) 2010-2018, Zoltan Ujhelyi, IncQuery Labs Ltd.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-v20.html.
 * 
 * SPDX-License-Identifier: EPL-2.0
 *******************************************************************************/
package org.eclipse.viatra.query.patternlanguage.emf.validation.whitelist.extensions;

import java.util.Arrays;
import java.util.Collection;

import org.eclipse.viatra.query.patternlanguage.emf.validation.whitelist.IPureElementProvider;
import org.eclipse.viatra.query.patternlanguage.emf.validation.whitelist.PureWhitelist.PureElement;

/**
 * @author Zoltan Ujhelyi
 * @since 2.0
 *
 */
public class MathWhitelistProvider implements IPureElementProvider {

    @Override
    public Collection<PureElement> getPureElements() {
        try {
            return Arrays.asList(
                    pureMethod(Math.class.getMethod("sin", double.class)),
                    pureMethod(Math.class.getMethod("cos", double.class)),
                    pureMethod(Math.class.getMethod("tan", double.class)),
                    pureMethod(Math.class.getMethod("asin", double.class)),
                    pureMethod(Math.class.getMethod("acos", double.class)),
                    pureMethod(Math.class.getMethod("atan", double.class)),
                    pureMethod(Math.class.getMethod("toRadians", double.class)),
                    pureMethod(Math.class.getMethod("exp", double.class)),
                    pureMethod(Math.class.getMethod("log", double.class)),
                    pureMethod(Math.class.getMethod("log10", double.class)),
                    pureMethod(Math.class.getMethod("sqrt", double.class)),
                    pureMethod(Math.class.getMethod("cbrt", double.class)),
                    pureMethod(Math.class.getMethod("IEEEremainder", double.class, double.class)),
                    pureMethod(Math.class.getMethod("ceil", double.class)),
                    pureMethod(Math.class.getMethod("floor", double.class)),
                    pureMethod(Math.class.getMethod("rint", double.class)),
                    pureMethod(Math.class.getMethod("atan2", double.class, double.class)),
                    pureMethod(Math.class.getMethod("pow", double.class, double.class)),
                    pureMethod(Math.class.getMethod("round", float.class)),
                    pureMethod(Math.class.getMethod("round", double.class)),
                    pureMethod(Math.class.getMethod("abs", int.class)),
                    pureMethod(Math.class.getMethod("abs", long.class)),
                    pureMethod(Math.class.getMethod("abs", float.class)),
                    pureMethod(Math.class.getMethod("abs", double.class)),
                    pureMethod(Math.class.getMethod("max", int.class, int.class)),
                    pureMethod(Math.class.getMethod("max", long.class, long.class)),
                    pureMethod(Math.class.getMethod("max", float.class, float.class)),
                    pureMethod(Math.class.getMethod("max", double.class, double.class)),
                    pureMethod(Math.class.getMethod("min", int.class, int.class)),
                    pureMethod(Math.class.getMethod("min", long.class, long.class)),
                    pureMethod(Math.class.getMethod("min", float.class, float.class)),
                    pureMethod(Math.class.getMethod("min", double.class, double.class)),
                    pureMethod(Math.class.getMethod("ulp", double.class)),
                    pureMethod(Math.class.getMethod("ulp", float.class)),
                    pureMethod(Math.class.getMethod("signum", double.class)),
                    pureMethod(Math.class.getMethod("signum", float.class)),
                    pureMethod(Math.class.getMethod("sinh", double.class)),
                    pureMethod(Math.class.getMethod("cosh", double.class)),
                    pureMethod(Math.class.getMethod("tanh", double.class)),
                    pureMethod(Math.class.getMethod("hypot", double.class, double.class)),
                    pureMethod(Math.class.getMethod("expm1", double.class)),
                    pureMethod(Math.class.getMethod("log1p", double.class)),
                    pureMethod(Math.class.getMethod("copySign", double.class, double.class)),
                    pureMethod(Math.class.getMethod("copySign", float.class, float.class)),
                    pureMethod(Math.class.getMethod("getExponent", float.class)),
                    pureMethod(Math.class.getMethod("getExponent", double.class)),
                    pureMethod(Math.class.getMethod("nextAfter", double.class, double.class)),
                    pureMethod(Math.class.getMethod("nextAfter", float.class, double.class)),
                    pureMethod(Math.class.getMethod("nextUp", float.class)),
                    pureMethod(Math.class.getMethod("nextUp", double.class)),
                    pureMethod(Math.class.getMethod("scalb", double.class, int.class)),
                    pureMethod(Math.class.getMethod("scalb", float.class, int.class))
            );
        } catch (NoSuchMethodException | SecurityException e) {
            throw new RuntimeException("Error initializing white list: " + e.getMessage(), e);
        }
    }

}
