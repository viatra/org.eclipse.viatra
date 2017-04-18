/*******************************************************************************
 * Copyright (c) 2017, Abel Hegedus, IncQuery Labs Ltd.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Abel Hegedus - initial API and implementation
 *******************************************************************************/

package org.eclipse.viatra.maven.querybuilder.helper;

/**
 * Configuration object which defines that an URI should be mapped to another one.
 * This class is named <code>UriMapping</code> instead of <code>URIMapping</code>
 * so that the POM can look like this (following Maven convention):
 * <pre>
 *  &lt;uriMappings>
 *      &lt;uriMapping>
 *          ...
 *      &lt;/uriMapping>
 *  &lt;/uriMappings>
 * </pre>
 */
public class UriMapping {

    /**
     * @property
     */
    private String sourceUri;

    /**
     * @property
     */
    private String targetUri;

    public String getSourceUri() {
        return sourceUri;
    }

    public void setSourceUri(String sourceUri) {
        this.sourceUri = sourceUri;
    }

    public String getTargetUri() {
        return targetUri;
    }

    public void setTargetUri(String targetUri) {
        this.targetUri = targetUri;
    }

    
}
