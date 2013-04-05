/*******************************************************************************
 * Copyright (c) 2010-2013, Andras Okros, Istvan Rath and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Andras Okros - initial API and implementation
 *******************************************************************************/
package org.eclipse.incquery.tooling.ui.patternregistry.views;

import org.eclipse.incquery.runtime.patternregistry.PatternRegistry;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;

public class PatternRegistryTreeContentProvider implements ITreeContentProvider {

    @Override
    public void dispose() {
        // Not needed now.
    }

    @Override
    public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
        // Input is constant.
    }

    @Override
    public Object[] getElements(Object inputElement) {
        return PatternRegistry.INSTANCE.getAllPatternInfos().toArray();
    }

    @Override
    public Object[] getChildren(Object parentElement) {
        return null;
    }

    @Override
    public Object getParent(Object element) {
        return null;
    }

    @Override
    public boolean hasChildren(Object element) {
        return false;
    }

}
