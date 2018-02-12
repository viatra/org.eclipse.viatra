/*******************************************************************************
 * Copyright (c) 2010-2018, Zoltan Ujhelyi, IncQuery Labs Ltd.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Zoltan Ujhelyi - initial API and implementation
 *******************************************************************************/
package org.eclipse.viatra.query.patternlanguage.emf.validation.whitelist;

import java.util.Arrays;
import java.util.Objects;
import java.util.stream.Collectors;

import org.apache.log4j.Logger;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.Platform;

import com.google.inject.Inject;

/**
 * @author Zoltan Ujhelyi
 * @since 2.0
 *
 */
public class EclipseExtensionBasedWhitelistProvider implements IPureWhitelistExtensionProvider {

    public static final String EXTENSION_ID = "org.eclipse.viatra.query.patternlanguage.emf.purewhitelist";
    
    @Inject
    Logger logger;
    
    @Override
    public Iterable<IPureElementProvider> getPureElementExtensions() {
        IConfigurationElement[] configurationElements = Platform.getExtensionRegistry().getConfigurationElementsFor(EXTENSION_ID);
        return Arrays.stream(configurationElements).map(this::toPureElement).filter(Objects::nonNull).collect(Collectors.toList());
    }

    private IPureElementProvider toPureElement(IConfigurationElement el) {
        try {
            return (IPureElementProvider) el.createExecutableExtension("provider");
        } catch (CoreException e) {
            logger.error("Error while loading pure element specification: " + e.getMessage(), e);
            return null;
        }
    }
}
