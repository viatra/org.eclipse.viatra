/*******************************************************************************
 * Copyright (c) 2010-2014, Marton Bur, Akos Horvath, Zoltan Ujhelyi, Istvan Rath and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Marton Bur - initial API and implementation
 *******************************************************************************/
package org.eclipse.incquery.tooling.localsearch.ui.debugger.provider;

import org.eclipse.jface.viewers.LabelProvider;

/**
 * 
 * @author Marton Bur
 *
 */
public class ZestLabelProvider extends LabelProvider {

    @Override
    public String getText(Object element) {
        if (element != null) {
            return element.getClass().toString();
        } else {
            return null;
        }
    }
    
}
