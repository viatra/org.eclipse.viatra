/*******************************************************************************
 * Copyright (c) 2010-2016, Gyorgy Gerencser, Gabor Bergmann, Istvan Rath and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Gyorgy Gerencser - initial API and implementation
 *******************************************************************************/
package org.eclipse.viatra.addon.querybyexample.ui.handlers;

import java.util.Collection;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.viatra.addon.querybyexample.ui.ui.QBEView;

public class StartHandler extends AbstractEObjectSelectionHandler {

    @Override
    protected void handleSelection(QBEView qbeView, Collection<EObject> selectedEObjects) {
        qbeView.start(selectedEObjects);
    }
}
