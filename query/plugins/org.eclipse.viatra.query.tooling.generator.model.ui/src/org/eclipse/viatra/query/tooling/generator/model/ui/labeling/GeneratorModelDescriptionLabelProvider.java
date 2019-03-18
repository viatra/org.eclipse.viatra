/*******************************************************************************
 * Copyright (c) 2010-2012, Zoltan Ujhelyi, Istvan Rath and Daniel Varro
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-v20.html.
 * 
 * SPDX-License-Identifier: EPL-2.0
 *******************************************************************************/
package org.eclipse.viatra.query.tooling.generator.model.ui.labeling;

import org.eclipse.xtext.ui.label.DefaultDescriptionLabelProvider;

/**
 * Provides labels for a IEObjectDescriptions and IResourceDescriptions.
 * 
 * see http://www.eclipse.org/Xtext/documentation/latest/xtext.html#labelProvider
 */
public class GeneratorModelDescriptionLabelProvider extends DefaultDescriptionLabelProvider {

    /*
     * //Labels and icons can be computed like this:
     * 
     * String text(IEObjectDescription ele) { return "my "+ele.getName(); }
     * 
     * String image(IEObjectDescription ele) { return ele.getEClass().getName() + ".gif"; }
     */

}
