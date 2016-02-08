/*******************************************************************************
 * Copyright (c) 2010-2012, Zoltan Ujhelyi, Abel Hegedus, Tamas Szabo, Istvan Rath and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Zoltan Ujhelyi, Abel Hegedus, Tamas Szabo - initial API and implementation
 *******************************************************************************/

package org.eclipse.viatra.addon.validation.runtime;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.eclipse.emf.common.notify.Notifier;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.viatra.query.runtime.util.IncQueryLoggingUtil;

public final class ValidationUtil {

    /**
     * Constructor hidden for utility class
     */
    private ValidationUtil() {

    }

    private static Map<IWorkbenchPage, Set<IEditorPart>> pageMap = new HashMap<IWorkbenchPage, Set<IEditorPart>>();

    private static Map<IEditorPart, ConstraintAdapter> adapterMap = new HashMap<IEditorPart, ConstraintAdapter>();

    public static synchronized Map<IEditorPart, ConstraintAdapter> getAdapterMap() {
        return adapterMap;
    }

    public static synchronized void addNotifier(IEditorPart editorPart, Notifier notifier) {
        adapterMap.put(editorPart, new ConstraintAdapter(editorPart, notifier, IncQueryLoggingUtil.getLogger(ValidationUtil.class)));
    }

    public static void registerEditorPart(IEditorPart editorPart) {
        IWorkbenchPage page = editorPart.getSite().getPage();
        if (pageMap.containsKey(page)) {
            pageMap.get(page).add(editorPart);
        } else {
            Set<IEditorPart> editorParts = new HashSet<IEditorPart>();
            editorParts.add(editorPart);
            pageMap.put(page, editorParts);
            page.addPartListener(ValidationPartListener.getInstance());
        }
    }

    public static void unregisterEditorPart(IEditorPart editorPart) {
        IWorkbenchPage page = editorPart.getSite().getPage();
        if (pageMap.containsKey(page)) {
            pageMap.get(page).remove(editorPart);
            if (pageMap.get(page).size() == 0) {
                pageMap.remove(page);
                page.removePartListener(ValidationPartListener.getInstance());
            }
        }
    }

    /**
     * Returns whether there are constraint specifications registered for an editor Id.
     * 
     * @param editorId
     *            The editor Id which should be checked
     * @return <code>true</code> if there are registered constraint specifications
     */
    public static boolean isConstraintsRegisteredForEditorId(String editorId) {
        return ConstraintExtensionRegistry.isConstraintSpecificationsRegisteredForEditorId(editorId);
    }
}
