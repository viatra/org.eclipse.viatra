/*******************************************************************************
 * Copyright (c) 2010-2017, Andras Szabolcs Nagy and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Contributors:
 *   Andras Szabolcs Nagy - initial API and implementation
 *******************************************************************************/
package org.eclipse.viatra.dse.solutionstore;

/**
 * Provides file name with a String <code>[prefix][id].[extension]</code> pattern.
 * @author Andras Szabolcs Nagy
 *
 */
public class IdBasedSolutionNameProvider implements ISolutionNameProvider {

    private int id = 1;
    private String prefix;
    private String extension;

    public IdBasedSolutionNameProvider(String prefix, String extension) {
        this.extension = extension;
        this.prefix = prefix;

    }

    @Override
    public String getName() {
        StringBuilder sb = new StringBuilder(prefix);
        sb.append(id++);
        sb.append('.');
        sb.append(extension);
        return sb.toString();
    }

}