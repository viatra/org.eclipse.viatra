/*******************************************************************************
 * Copyright (c) 2010-2013, istvanrath, Istvan Rath and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   istvanrath - initial API and implementation
 *******************************************************************************/
package org.eclipse.incquery.databinding.runtime.util;

import java.lang.annotation.Documented;

import org.eclipse.incquery.databinding.runtime.adapter.DatabindingAdapter;
import org.eclipse.incquery.databinding.runtime.adapter.GenericDatabindingAdapter;
import org.eclipse.incquery.patternlanguage.emf.specification.SpecificationBuilder;
import org.eclipse.incquery.patternlanguage.patternLanguage.Pattern;
import org.eclipse.incquery.runtime.api.IPatternMatch;
import org.eclipse.incquery.runtime.api.IQuerySpecification;
import org.eclipse.incquery.runtime.exception.IncQueryException;

/**
 * @author istvanrath
 *
 */
public class DatabindingUtil {

 //   private static final String DATABINDING_EXTENSION = "org.eclipse.incquery.databinding.runtime.databinding";

 //   private static ILog logger = IncQueryDatabindingRuntimePlugin.getDefault().getLog();


   /* removed generated databinding adapter functionality entirely
    @SuppressWarnings("unchecked")
    public static DatabindingAdapter<IPatternMatch> getDatabindingAdapterForGeneratedMatcher(Pattern _pattern) {
        String patternName = _pattern.getName();
        try {
            IExtensionRegistry reg = Platform.getExtensionRegistry();
            IExtensionPoint ep = reg.getExtensionPoint(DATABINDING_EXTENSION);
            for (IExtension e : ep.getExtensions()) {
                for (IConfigurationElement ce : e.getConfigurationElements()) {
                    String[] tokens = patternName.split("\\.");
                    String pattern = tokens[tokens.length - 1];

                    if (ce.getName().equals("databinding") && ce.getAttribute("patternName").equalsIgnoreCase(pattern)) {
                        Object obj = ce.createExecutableExtension("class");

                        if (obj instanceof DatabindingAdapter) {
                            return (DatabindingAdapter<IPatternMatch>) obj;
                        }
                    }
                }
            }
        } catch (Exception e) {
            logger.log(new Status(IStatus.ERROR, IncQueryDatabindingRuntimePlugin.PLUGIN_ID,
                    "Could not find DatabindableMatcher for pattern named: " + patternName, e));
        }

        return null;
    }
    */


    /**
     *
     * @throws IncQueryException
     * @deprecated "Use {@link #getDatabindingAdapter(IQuerySpecification)} instead"
     */
    @Deprecated
    public static DatabindingAdapter<IPatternMatch> getDatabindingAdapter(Pattern pattern) throws IncQueryException {
        IQuerySpecification<?> spec = new SpecificationBuilder().getOrCreateSpecification(pattern);
        return getDatabindingAdapter(spec);
    }

    public static DatabindingAdapter<IPatternMatch> getDatabindingAdapter(IQuerySpecification<?> query) {
        GenericDatabindingAdapter adapter = new GenericDatabindingAdapter(query);
        return adapter;
    }

    /*
    public static String getDatabindingMessageForGeneratedMatcher(IPatternMatch match) {
        String patternName = match.patternName();
        try {
            IExtensionRegistry reg = Platform.getExtensionRegistry();
            IExtensionPoint ep = reg.getExtensionPoint(DATABINDING_EXTENSION);
            for (IExtension e : ep.getExtensions()) {
                for (IConfigurationElement ce : e.getConfigurationElements()) {
                    String[] tokens = patternName.split("\\.");
                    //String pattern = tokens[tokens.length - 1];

                    if (ce.getName().equals("databinding") && ce.getAttribute("patternName").equalsIgnoreCase(patternName)) {
                        return ce.getAttribute("message");
                    }
                }
            }
        } catch (Exception e) {
            logger.log(new Status(IStatus.ERROR, IncQueryDatabindingRuntimePlugin.PLUGIN_ID,
                    "Message could not be retrieved for generated matcher.", e));
        }

        return null;
    }
    */
}
