/*******************************************************************************
 * Copyright (c) 2010-2012, Zoltan Ujhelyi, Mark Czotter, Istvan Rath and Daniel Varro
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-v20.html.
 * 
 * SPDX-License-Identifier: EPL-2.0
 *******************************************************************************/

package org.eclipse.viatra.query.tooling.core.generator.fragments;

import org.eclipse.core.resources.IProject;
import org.eclipse.viatra.query.patternlanguage.emf.vql.Pattern;

/**
 * An interface for collecting code generation fragments for specific patterns. The concrete value is injected using the
 * pattern language UI injectors.
 *
 * @author Zoltan Ujhelyi
 *
 */
public interface IGenerationFragmentProvider {

    /**
     * Collects the generation fragments applicable for a selected pattern.
     *
     * @param pattern
     * @return a non-null collection of code generation fragments. May be empty.
     */
    Iterable<IGenerationFragment> getFragmentsForPattern(Pattern pattern);

    /**
     * Collects all {@link IGenerationFragment}.
     *
     * @return a non-null collection of code generation fragments.
     */
    Iterable<IGenerationFragment> getAllFragments();

    /**
     * Returns the fragment project for the {@link IGenerationFragment} based on the modelProject.
     *
     * @param modelProject
     * @param fragment
     */
    IProject getFragmentProject(IProject modelProject, IGenerationFragment fragment);
}
