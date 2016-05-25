/*******************************************************************************
 * Copyright (c) 2010-2016, Abel Hegedus, IncQuery Labs Ltd.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Abel Hegedus - initial API and implementation
 *******************************************************************************/
package org.eclipse.viatra.query.tooling.ui.perspective;

import org.eclipse.ui.IFolderLayout;
import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;

/**
 * The VIATRA Transformation Development perspective defines the default setup for the different views provided by the
 * Query and Transformation SDK.
 * Note that the configuration is provided by perspective extensions instead of listing them here.
 * 
 * @author Abel Hegedus
 *
 */
public class TransformationDevelopmentPerspective implements IPerspectiveFactory {

    private IPageLayout factory;

    public TransformationDevelopmentPerspective() {
        super();
    }

    public void createInitialLayout(IPageLayout factory) {
        this.factory = factory;
        addViews();
    }

    private void addViews() {
        // Creates the overall folder layout.
        // Note that each new Folder uses a percentage of the remaining EditorArea.
        IFolderLayout left = factory.createFolder("left", // NON-NLS-1
                IPageLayout.LEFT, 0.25f, factory.getEditorArea());
        left.addView(IPageLayout.ID_PROJECT_EXPLORER);

        IFolderLayout bottom = factory.createFolder("bottomRight", // NON-NLS-1
                IPageLayout.BOTTOM, 0.75f, factory.getEditorArea());
        bottom.addView(IPageLayout.ID_PROBLEM_VIEW);

        IFolderLayout right = factory.createFolder("right", IPageLayout.RIGHT, 0.75f, factory.getEditorArea()); //$NON-NLS-1$
        right.addView(IPageLayout.ID_OUTLINE);

    }

}
