/*******************************************************************************
 * Copyright (c) 2010-2018, Gabor Bergmann, IncQuery Labs Ltd.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Gabor Bergmann - initial API and implementation
 *******************************************************************************/
package org.eclipse.viatra.query.runtime.matchers.scopes.tables;

import org.eclipse.viatra.query.runtime.matchers.context.IInputKey;

/**
 * @since 2.0
 * @author Gabor Bergmann
 */
public abstract class AbstractIndexTable implements IIndexTable {

    private IInputKey inputKey;
    protected ITableContext tableContext;

    public AbstractIndexTable(IInputKey inputKey, ITableContext tableContext) {
        this.inputKey = inputKey;
        this.tableContext = tableContext;
    }

    @Override
    public String toString() {
        return this.getClass().getSimpleName() + ":" + inputKey.getPrettyPrintableName();
    }

    @Override
    public IInputKey getInputKey() {
        return inputKey;
    }

    protected void logError(String message) {
        tableContext.logError(message);
    }

}