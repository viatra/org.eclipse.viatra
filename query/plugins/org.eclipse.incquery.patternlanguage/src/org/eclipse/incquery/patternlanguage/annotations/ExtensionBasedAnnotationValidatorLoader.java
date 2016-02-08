/*******************************************************************************
 * Copyright (c) 2010-2014, Zoltan Ujhelyi, Istvan Rath and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Zoltan Ujhelyi - initial API and implementation
 *******************************************************************************/
package org.eclipse.incquery.patternlanguage.annotations;

import java.util.Arrays;
import java.util.Map;

import org.apache.log4j.Logger;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.Platform;
import org.eclipse.incquery.patternlanguage.annotations.impl.PatternAnnotationValidator;

import com.google.common.base.Function;
import com.google.common.base.Preconditions;
import com.google.common.collect.Iterables;
import com.google.common.collect.Maps;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Singleton;

/**
 * @author Zoltan Ujhelyi
 * 
 */
@Singleton
public class ExtensionBasedAnnotationValidatorLoader implements IAnnotationValidatorLoader {

    private static final String VALIDATOR_PARAMETER_NAME = "additionalValidator";

    @Inject
    private Logger log;
    @Inject
    private Injector injector;

    private static final class ExtensionConverter implements
            Function<IConfigurationElement, PatternAnnotationParameter> {
        @Override
        public PatternAnnotationParameter apply(IConfigurationElement input) {
            Preconditions.checkNotNull(input, "input");
            final String parameterName = input.getAttribute("name");
            final boolean mandatory = Boolean.parseBoolean(input.getAttribute("mandatory"));
            final boolean multiple = Boolean.parseBoolean(input.getAttribute("multiple"));
            final String deprecatedString = input.getAttribute("deprecated");
            final boolean deprecated = Boolean.parseBoolean(deprecatedString);
            final String type = input.getAttribute("type");
            final String description = input.getAttribute("description");
            return new PatternAnnotationParameter(parameterName, type, description, multiple, mandatory, deprecated);
        }
    }

    static final String EXTENSIONID = "org.eclipse.incquery.patternlanguage.annotation";

    private Map<String, IPatternAnnotationValidator> validators;

    @Override
    public Map<String, IPatternAnnotationValidator> getKnownValidators() {
        if (validators != null) {
            return validators;
        }
        validators = Maps.newHashMap();
        if (Platform.isRunning()) {

            final IConfigurationElement[] config = Platform.getExtensionRegistry().getConfigurationElementsFor(
                    EXTENSIONID);
            for (IConfigurationElement e : config) {
                try {
                    final String annotationName = e.getAttribute("name");
                    final String description = e.getAttribute("description");
                    final String deprecatedString = e.getAttribute("deprecated");
                    final boolean deprecated = Boolean.parseBoolean(deprecatedString);

                    final IConfigurationElement[] parameters = e.getChildren("annotationparameter");

                    IPatternAnnotationAdditionalValidator validator = null;

                    final Iterable<PatternAnnotationParameter> parameterIterable = Iterables.transform(
                            Arrays.asList(parameters), new ExtensionConverter());
                    if (e.getAttribute(VALIDATOR_PARAMETER_NAME) != null) {
                        validator = (IPatternAnnotationAdditionalValidator) e
                                .createExecutableExtension(VALIDATOR_PARAMETER_NAME);
                        injector.injectMembers(validator);
                    }

                    final IPatternAnnotationValidator annotationValidator = new PatternAnnotationValidator(
                            annotationName, description, deprecated, parameterIterable, validator);
                    validators.put(annotationName, annotationValidator);
                } catch (CoreException ex) {
                    log.error(
                            String.format("Error while initializing the validator for annotation %s.",
                                    e.getAttribute("name")), ex);
                }
            }
        }
        return validators;
    }
}
