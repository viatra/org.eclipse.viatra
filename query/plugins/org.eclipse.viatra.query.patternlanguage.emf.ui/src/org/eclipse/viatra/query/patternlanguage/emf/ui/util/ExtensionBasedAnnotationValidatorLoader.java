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
package org.eclipse.viatra.query.patternlanguage.emf.ui.util;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.Platform;
import org.eclipse.viatra.query.patternlanguage.emf.annotations.IAnnotationValidatorLoader;
import org.eclipse.viatra.query.patternlanguage.emf.annotations.IPatternAnnotationValidator;

import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Singleton;

/**
 * @author Zoltan Ujhelyi
 * @since 2.0
 * 
 */
@Singleton
public class ExtensionBasedAnnotationValidatorLoader implements IAnnotationValidatorLoader {

    private static final String EXTENSIONID = "org.eclipse.viatra.query.patternlanguage.emf.annotation";
    private static final String PROVIDER_PARAMETER_NAME = "provider";

    @Inject
    private Logger log;
    @Inject
    private Injector injector;


    private Map<String, IPatternAnnotationValidator> validators;

    @Override
    public Map<String, IPatternAnnotationValidator> getKnownValidators() {
        if (validators != null) {
            return validators;
        }
        validators = new HashMap<>();
        if (Platform.isRunning()) {

            final IConfigurationElement[] config = Platform.getExtensionRegistry().getConfigurationElementsFor(
                    EXTENSIONID);
            for (IConfigurationElement e : config) {
                try {
                    IPatternAnnotationValidator validator = (IPatternAnnotationValidator) e
                            .createExecutableExtension(PROVIDER_PARAMETER_NAME);
                    injector.injectMembers(validator);
                    validators.put(validator.getAnnotationName(), validator);
                } catch (CoreException ex) {
                    log.error(
                            String.format("Error while initializing the validator for annotation %s from plugin %s.",
                                    e.getAttribute("name"), e.getContributor().getName()), ex);
                }
            }
        }
        return validators;
    }
}
