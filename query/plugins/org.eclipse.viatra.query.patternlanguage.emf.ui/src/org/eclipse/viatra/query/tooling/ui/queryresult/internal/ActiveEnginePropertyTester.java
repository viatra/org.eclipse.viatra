/*******************************************************************************
 * Copyright (c) 2010-2016, Abel Hegedus, IncQuery Labs Ltd.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-v20.html.
 * 
 * SPDX-License-Identifier: EPL-2.0
 *******************************************************************************/
package org.eclipse.viatra.query.tooling.ui.queryresult.internal;

import org.eclipse.core.expressions.PropertyTester;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.viatra.query.tooling.ui.queryresult.QueryResultView;

/**
 * @author Abel Hegedus
 *
 * @since 1.4
 */
public class ActiveEnginePropertyTester extends PropertyTester {

    private static final String ACTIVE_ENGINE = "activeengine";
    public static final String ACTIVE_ENGINE_ID = "org.eclipse.viatra.query.tooling.ui.browser.result.activeengine";

    @Override
    public boolean test(Object receiver, String property, Object[] args, Object expectedValue) {
        if (ACTIVE_ENGINE.equals(property) && receiver instanceof IWorkbenchPart) {
            final IWorkbenchPage page = ((IWorkbenchPart) receiver).getSite().getPage();
            if (page != null) {
                IViewPart view = page.findView(QueryResultView.ID);
                if (view instanceof QueryResultView) {
                    QueryResultView queryResultView = (QueryResultView) view;
                    boolean activeEngine = queryResultView.hasActiveEngine();
                    return activeEngine;
                }
            }
        }
        return false;
    }

}
