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
package org.eclipse.incquery.viewers.tooling.ui.views;

import java.util.Collection;

import org.eclipse.emf.common.notify.Notifier;
import org.eclipse.incquery.runtime.api.IQuerySpecification;
import org.eclipse.incquery.runtime.exception.IncQueryException;
import org.eclipse.incquery.viewers.runtime.model.ViewerDataFilter;

/**
 * Utility class for handling Viewers Tooling Views.
 * @author istvanrath
 *
 */
public final class ViewersToolingViewsUtil {

	public static final String SANDBOX_TAB_EXTENSION_ID = "org.eclipse.incquery.viewers.tooling.ui.viewersandboxtab";

	public static void initializeContentsOnView(Notifier model, Collection<IQuerySpecification<?>> queries, ViewerDataFilter filter) throws IncQueryException {
		//ViewersSandboxView.getInstance().setContents(model, patterns, filter);
		ViewersMultiSandboxView.ensureOpen();
		ViewersMultiSandboxView.getInstance().initializeContents(model, queries, filter);
	}
	
	
}
