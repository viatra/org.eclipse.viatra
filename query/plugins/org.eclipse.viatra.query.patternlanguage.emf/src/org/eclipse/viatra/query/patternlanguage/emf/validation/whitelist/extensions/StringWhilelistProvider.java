/*******************************************************************************
 * Copyright (c) 2010-2018, Zoltan Ujhelyi, IncQuery Labs Ltd.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-v20.html.
 * 
 * SPDX-License-Identifier: EPL-2.0
 *******************************************************************************/
package org.eclipse.viatra.query.patternlanguage.emf.validation.whitelist.extensions;

import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.Collection;
import java.util.Locale;

import org.eclipse.viatra.query.patternlanguage.emf.validation.whitelist.IPureElementProvider;
import org.eclipse.viatra.query.patternlanguage.emf.validation.whitelist.PureWhitelist.PureElement;

/**
 * @author Zoltan Ujhelyi
 * @since 2.0
 */
public class StringWhilelistProvider implements IPureElementProvider {


    @Override
    public Collection<PureElement> getPureElements() {
        try {
            return Arrays.asList(
                    pureMethod(String.class.getMethod("length")),
                    pureMethod(String.class.getMethod("isEmpty")),
                    pureMethod(String.class.getMethod("charAt", int.class)),
                    pureMethod(String.class.getMethod("codePointAt", int.class)),
                    pureMethod(String.class.getMethod("codePointBefore", int.class)),
                    pureMethod(String.class.getMethod("codePointCount", int.class, int.class)),
                    pureMethod(String.class.getMethod("offsetByCodePoints", int.class, int.class)),
                    pureMethod(String.class.getMethod("getBytes", String.class)),
                    pureMethod(String.class.getMethod("getBytes", Charset.class)),
                    pureMethod(String.class.getMethod("getBytes")),
                    pureMethod(String.class.getMethod("equals", Object.class)),
                    pureMethod(String.class.getMethod("contentEquals", StringBuffer.class)),
                    pureMethod(String.class.getMethod("contentEquals", CharSequence.class)),
                    pureMethod(String.class.getMethod("equalsIgnoreCase", String.class)),
                    pureMethod(String.class.getMethod("compareTo", String.class)),
                    pureMethod(String.class.getMethod("compareToIgnoreCase", String.class)),
                    pureMethod(String.class.getMethod("regionMatches", int.class, String.class, int.class, int.class)),
                    pureMethod(String.class.getMethod("regionMatches", boolean.class, int.class, String.class, int.class, int.class)),
                    pureMethod(String.class.getMethod("startsWith", String.class, int.class)),
                    pureMethod(String.class.getMethod("startsWith", String.class)),
                    pureMethod(String.class.getMethod("endsWith", String.class)),
                    pureMethod(String.class.getMethod("indexOf", int.class)),
                    pureMethod(String.class.getMethod("indexOf", int.class, int.class)),
                    pureMethod(String.class.getMethod("lastIndexOf", int.class)),
                    pureMethod(String.class.getMethod("lastIndexOf", int.class, int.class)),
                    pureMethod(String.class.getMethod("indexOf", String.class)),
                    pureMethod(String.class.getMethod("indexOf", String.class, int.class)),
                    pureMethod(String.class.getMethod("lastIndexOf", String.class)),
                    pureMethod(String.class.getMethod("lastIndexOf", String.class, int.class)),
                    pureMethod(String.class.getMethod("substring", int.class)),
                    pureMethod(String.class.getMethod("substring", int.class, int.class)),
                    pureMethod(String.class.getMethod("subSequence", int.class, int.class)),
                    pureMethod(String.class.getMethod("concat", String.class)),
                    pureMethod(String.class.getMethod("replace", char.class, char.class)),
                    pureMethod(String.class.getMethod("matches", String.class)),
                    pureMethod(String.class.getMethod("contains", CharSequence.class)),
                    pureMethod(String.class.getMethod("replaceFirst", String.class, String.class)),
                    pureMethod(String.class.getMethod("replaceAll", String.class, String.class)),
                    pureMethod(String.class.getMethod("replace", CharSequence.class, CharSequence.class)),
                    pureMethod(String.class.getMethod("split", String.class, int.class)),
                    pureMethod(String.class.getMethod("split", String.class)),
                    pureMethod(String.class.getMethod("toLowerCase", Locale.class)),
                    pureMethod(String.class.getMethod("toLowerCase")),
                    pureMethod(String.class.getMethod("toUpperCase", Locale.class)),
                    pureMethod(String.class.getMethod("toUpperCase")),
                    pureMethod(String.class.getMethod("trim")),
                    pureMethod(String.class.getMethod("toCharArray")),
                    pureMethod(String.class.getMethod("format", String.class, Object[].class)),
                    pureMethod(String.class.getMethod("format", Locale.class, String.class, Object[].class)),
                    pureMethod(String.class.getMethod("valueOf", Object.class)),
                    pureMethod(String.class.getMethod("valueOf", char[].class)),
                    pureMethod(String.class.getMethod("valueOf", char[].class, int.class, int.class)),
                    pureMethod(String.class.getMethod("copyValueOf", char[].class)),
                    pureMethod(String.class.getMethod("copyValueOf", char[].class, int.class, int.class)),
                    pureMethod(String.class.getMethod("valueOf", boolean.class)),
                    pureMethod(String.class.getMethod("valueOf", char.class)),
                    pureMethod(String.class.getMethod("valueOf", int.class)),
                    pureMethod(String.class.getMethod("valueOf", long.class)),
                    pureMethod(String.class.getMethod("valueOf", float.class)),
                    pureMethod(String.class.getMethod("valueOf", double.class)),
                    pureMethod(String.class.getMethod("toString"))
            );
        } catch (NoSuchMethodException | SecurityException e) {
            throw new RuntimeException("Error initializing white list: " + e.getMessage(), e);
        }
    }

}
