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

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.viatra.addon.querybyexample.ui.QBEViewUtils;
import org.eclipse.viatra.addon.querybyexample.ui.ui.QBEView;

public class NegConstraintsHandler extends AbstractHandler {

    public Object execute(ExecutionEvent event) throws ExecutionException {
        QBEView qbeView = QBEViewUtils.getQBEView(event);
        qbeView.getService().findAndRegisterNegativeConstraints();
        qbeView.updateViewer();
        return null;
    }
}
