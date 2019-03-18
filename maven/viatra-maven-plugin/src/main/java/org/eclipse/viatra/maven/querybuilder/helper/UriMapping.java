/*******************************************************************************
 * Copyright (c) 2017, Abel Hegedus, IncQuery Labs Ltd.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-v20.html.
 * 
 * SPDX-License-Identifier: EPL-2.0
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
