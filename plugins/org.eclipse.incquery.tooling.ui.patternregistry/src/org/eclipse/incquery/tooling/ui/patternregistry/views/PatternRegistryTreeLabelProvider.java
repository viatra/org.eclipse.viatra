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
package org.eclipse.incquery.tooling.ui.patternregistry.views;

import org.eclipse.incquery.runtime.patternregistry.IPatternInfo;
import org.eclipse.jface.viewers.LabelProvider;

public class PatternRegistryTreeLabelProvider extends LabelProvider {

    @Override
    public String getText(Object element) {
        if (element instanceof IPatternInfo) {
            IPatternInfo patternInfo = (IPatternInfo) element;
            return patternInfo.getId();
        } else {
            return super.getText(element);
        }
    }

}
