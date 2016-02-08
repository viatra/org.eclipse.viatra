/*******************************************************************************
 * Copyright (c) 2010-2014, Tamas Szabo (itemis AG), Istvan Rath and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Tamas Szabo (itemis AG) - initial API and implementation
 *******************************************************************************/
package org.eclipse.viatra.query.tooling.ui.queryexplorer.util;

import org.eclipse.jface.viewers.ICheckStateProvider;
import org.eclipse.viatra.query.tooling.ui.queryexplorer.QueryExplorer;
import org.eclipse.viatra.query.tooling.ui.queryexplorer.content.patternsviewer.PatternComponent;

/**
 * A {@link ICheckStateProvider} implementation for the patterns viewer in the {@link QueryExplorer}.
 * 
 * @author Tamas Szabo (itemis AG)
 */
public class CheckStateProvider implements ICheckStateProvider {

    @Override
    public boolean isChecked(Object element) {
        return ((PatternComponent) element).getCheckedState();
    }

    @Override
    public boolean isGrayed(Object element) {
        return false;
    }

}
