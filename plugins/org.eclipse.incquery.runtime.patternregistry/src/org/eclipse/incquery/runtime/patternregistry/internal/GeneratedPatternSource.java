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
package org.eclipse.incquery.runtime.patternregistry.internal;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.Platform;
import org.eclipse.incquery.patternlanguage.helper.CorePatternLanguageHelper;
import org.eclipse.incquery.patternlanguage.patternLanguage.Annotation;
import org.eclipse.incquery.patternlanguage.patternLanguage.AnnotationParameter;
import org.eclipse.incquery.patternlanguage.patternLanguage.Pattern;
import org.eclipse.incquery.patternlanguage.patternLanguage.impl.BoolValueImpl;
import org.eclipse.incquery.runtime.IExtensions;
import org.eclipse.incquery.runtime.api.IMatcherFactory;
import org.eclipse.incquery.runtime.api.IPatternMatch;
import org.eclipse.incquery.runtime.api.IncQueryEngine;
import org.eclipse.incquery.runtime.api.IncQueryMatcher;
import org.eclipse.incquery.runtime.extensibility.IMatcherFactoryProvider;
import org.eclipse.incquery.runtime.patternregistry.IPatternInfo;
import org.eclipse.incquery.runtime.patternregistry.PatternTypeEnum;

public class GeneratedPatternSource {

    public static List<IPatternInfo> initializeRegisteredPatterns() {
        List<IPatternInfo> resultList = new ArrayList<IPatternInfo>();
        IExtensionRegistry extensionRegistry = Platform.getExtensionRegistry();
        if (extensionRegistry != null) {
            IExtensionPoint extensionPoint = extensionRegistry
                    .getExtensionPoint(IExtensions.MATCHERFACTORY_EXTENSION_POINT_ID);
            if (extensionPoint != null) {
                for (IExtension extension : extensionPoint.getExtensions()) {
                    for (IConfigurationElement configurationElement : extension.getConfigurationElements()) {
                        if (configurationElement.getName().equals("matcher")) {
                            IPatternInfo patternInfo = intializeFromConfigurationElement(configurationElement);
                            if (patternInfo != null) {
                                resultList.add(patternInfo);
                            }
                        }
                    }
                }
            }
        }
        return resultList;
    }

    private static IPatternInfo intializeFromConfigurationElement(IConfigurationElement configurationElement) {
        try {
            String idAttributeInExtension = configurationElement.getAttribute("id");
            @SuppressWarnings("unchecked")
            IMatcherFactoryProvider<IMatcherFactory<IncQueryMatcher<IPatternMatch>>> matcherFactoryProvider = (IMatcherFactoryProvider<IMatcherFactory<IncQueryMatcher<IPatternMatch>>>) configurationElement
                    .createExecutableExtension("factoryProvider");
            IMatcherFactory<IncQueryMatcher<IPatternMatch>> matcherFactory = matcherFactoryProvider.get();
            String patternFullyQualifiedName = matcherFactory.getPatternFullyQualifiedName();
            if (idAttributeInExtension.equals(patternFullyQualifiedName)) {
                Pattern pattern = matcherFactory.getPattern();
                if (hasQueryExplorerAnnotation(pattern)) {
                    PatternInfo patternInfo = new PatternInfo(PatternTypeEnum.GENERATED, pattern, matcherFactory);
                    return patternInfo;
                }
            } else {
                IncQueryEngine.getDefaultLogger().warn(
                        "[Pattern Registry] Id attribute value " + idAttributeInExtension
                                + " does not equal pattern FQN of factory " + patternFullyQualifiedName
                                + " in plugin.xml of "
                                + configurationElement.getDeclaringExtension().getUniqueIdentifier());
            }
        } catch (Exception exception) {
            IncQueryEngine.getDefaultLogger().error(
                    "[Pattern Registry] Exception during matcher factory registry initialization", exception);
        }

        return null;
    }

    private static boolean hasQueryExplorerAnnotation(Pattern pattern) {
        Annotation annotation = CorePatternLanguageHelper.getFirstAnnotationByName(pattern,
                IExtensions.QUERY_EXPLORER_ANNOTATION);
        if (annotation != null) {
            for (AnnotationParameter annotationParameter : annotation.getParameters()) {
                if (annotationParameter.getName().equalsIgnoreCase("display")) {
                    // FIXME do it is it right?
                    return Boolean.valueOf(((BoolValueImpl) annotationParameter.getValue()).isValue());
                }
            }
            return true;
        }
        return false;
    }

}
