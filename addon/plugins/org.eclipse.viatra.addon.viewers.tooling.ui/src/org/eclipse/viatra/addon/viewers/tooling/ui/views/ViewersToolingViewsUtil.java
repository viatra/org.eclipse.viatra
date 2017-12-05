/*******************************************************************************
 * Copyright (c) 2010-2013, Istvan Rath and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Istvan Rath - initial API and implementation
 *******************************************************************************/
package org.eclipse.viatra.addon.viewers.tooling.ui.views;

import java.util.Collection;

import org.eclipse.emf.common.notify.Notifier;
import org.eclipse.viatra.addon.viewers.runtime.model.ViewerDataFilter;
import org.eclipse.viatra.query.runtime.api.IQuerySpecification;
import org.eclipse.viatra.query.runtime.emf.EMFScope;

/**
 * Utility class for handling Viewers Tooling Views.
 * @author istvanrath
 *
 */
public final class ViewersToolingViewsUtil {

    public static final String SANDBOX_TAB_EXTENSION_ID = "org.eclipse.viatra.addon.viewers.tooling.ui.viewersandboxtab";

    private ViewersToolingViewsUtil() {}
    
    public static void initializeContentsOnView(Notifier model, Collection<IQuerySpecification<?>> queries, ViewerDataFilter filter) {
        //ViewersSandboxView.getInstance().setContents(model, patterns, filter);
        ViewersMultiSandboxView.ensureOpen();
        ViewersMultiSandboxView.getInstance().initializeContents(new EMFScope(model), queries, filter);
    }
    
    
}
