/*******************************************************************************
 * Copyright (c) 2010-2012, Mark Czotter, Istvan Rath and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Mark Czotter - initial API and implementation
 *******************************************************************************/

package org.eclipse.viatra.query.patternlanguage.emf.util;

import org.apache.log4j.Logger;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.xtext.xbase.jvmmodel.JvmTypesBuilder;
import org.eclipse.xtext.xbase.lib.Procedures.Procedure1;

import com.google.inject.Inject;

/**
 * Custom {@link JvmTypesBuilder} for EMFPatternLanguage.
 * 
 * @author Mark Czotter
 * 
 */
public class EMFJvmTypesBuilder extends JvmTypesBuilder {

    @Inject
    private Logger logger;

    /**
     * Overriding parent method to replace logging {@inheritDoc}
     */
    protected <T extends EObject> T initializeSafely(T targetElement, Procedure1<? super T> initializer) {
        if (targetElement != null && initializer != null) {
            try {
                initializer.apply(targetElement);
            } catch (Exception e) {
                logger.error("Error initializing JvmElement", e);
            }
        }
        return targetElement;
    }
}
