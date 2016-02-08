/*******************************************************************************
 * Copyright (c) 2010-2012, Andras Okros, Zoltan Ujhelyi, Tamas Szabo, Istvan Rath and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Zoltan Ujhelyi, Tamas Szabo - initial API and implementation
 *   Andras Okros - reworked to use new IModelConnector interface
 *******************************************************************************/
package org.eclipse.incquery.tooling.ui.queryexplorer.util;

import org.eclipse.incquery.tooling.ui.queryexplorer.IModelConnector;
import org.eclipse.incquery.tooling.ui.queryexplorer.QueryExplorer;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPart;

/**
 * The PartListener is used to observe {@link IEditorPart} close actions.
 */
public class ModelEditorPartListener extends BasePartListener {

    private final IModelConnector modelConnector;

    public ModelEditorPartListener(IModelConnector modelConnector) {
        this.modelConnector = modelConnector;
    }

    @Override
    public void partClosed(IWorkbenchPart part) {
        if (part instanceof IEditorPart) {
            // also check if the closed editor belongs to our model connector
            if (part.equals(this.modelConnector.getOwner()) && QueryExplorer.getInstance() != null) {
                QueryExplorer.getInstance().unload(modelConnector);
            }
        }
    }

}
