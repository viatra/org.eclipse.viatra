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

import java.util.HashMap;
import java.util.Map;

import org.eclipse.ui.AbstractSourceProvider;
import org.eclipse.ui.ISources;

public class QBEViewMainSourceProvider extends AbstractSourceProvider {

    public static final String QBEVIEW_MAIN_SOURCE_PROVIDER_PROVIDED_SOURCE_NAME = "org.eclipse.viatra.addon.querybyexample.ui.qbeviewmainvariable";

    private State currentState = State.MODEL_NOT_LOADED;

    enum State {
        MODEL_NOT_LOADED("MODEL_NOT_LOADED"), MODEL_LOADED("MODEL_LOADED"), LINKED_FILE_EXISTING(
                "LINKED_FILE_EXISTING");

        String value;

        State(String s) {
            this.value = s;
        }

        public String getValue() {
            return this.value;
        }
    }

    @Override
    public void dispose() {
    }

    @Override
    public Map getCurrentState() {
        Map<String, String> ret = new HashMap<String, String>();
        ret.put(QBEVIEW_MAIN_SOURCE_PROVIDER_PROVIDED_SOURCE_NAME, this.currentState.getValue());
        return ret;
    }

    @Override
    public String[] getProvidedSourceNames() {
        return new String[] { QBEVIEW_MAIN_SOURCE_PROVIDER_PROVIDED_SOURCE_NAME };
    }

    public void setModelLoadedState() {
        this.fireSourceChanged(ISources.WORKBENCH, QBEVIEW_MAIN_SOURCE_PROVIDER_PROVIDED_SOURCE_NAME,
                State.MODEL_LOADED.getValue());
    }

    public void setLinkedFileExistingState() {
        this.fireSourceChanged(ISources.WORKBENCH, QBEVIEW_MAIN_SOURCE_PROVIDER_PROVIDED_SOURCE_NAME,
                State.LINKED_FILE_EXISTING.getValue());
    }
}
