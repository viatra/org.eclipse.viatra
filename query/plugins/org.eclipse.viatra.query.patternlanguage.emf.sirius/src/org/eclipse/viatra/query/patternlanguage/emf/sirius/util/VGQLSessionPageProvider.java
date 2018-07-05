/*******************************************************************************
 * Copyright (c) 2010-2018, Zoltan Ujhelyi, IncQuery Labs Ltd.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Zoltan Ujhelyi - initial API and implementation
 *******************************************************************************/
package org.eclipse.viatra.query.patternlanguage.emf.sirius.util;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Supplier;

import org.eclipse.sirius.ui.editor.SessionEditor;
import org.eclipse.sirius.ui.editor.api.pages.AbstractSessionEditorPage;
import org.eclipse.sirius.ui.editor.api.pages.PageProvider;

/**
 *
 */
public class VGQLSessionPageProvider extends PageProvider {

    @Override
    public Map<String, Supplier<AbstractSessionEditorPage>> getPages(SessionEditor editor) {
        Map<String, Supplier<AbstractSessionEditorPage>> resultMap = new HashMap<>();
        resultMap.put(VGQLSessionEditorPage.PAGE_ID, () -> {
            return new VGQLSessionEditorPage(editor);
        });
        return resultMap;
    }

    @Override
    public boolean provides(String pageId) {
        return Objects.equals(pageId, VGQLSessionEditorPage.PAGE_ID);
    }

}
