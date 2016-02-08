/*******************************************************************************
 * Copyright (c) 2004-2010 Gabor Bergmann, Istvan Rath and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Istvan Rath - initial API and implementation
 *******************************************************************************/

package org.eclipse.viatra.query.runtime;

/**
 * Interface for storing string constants related to IncQuery's extension points.
 *
 * @author Istvan Rath
 *
 */
public interface IExtensions {

    public static final String QUERY_SPECIFICATION_EXTENSION_POINT_ID = IncQueryRuntimePlugin.PLUGIN_ID + ".queryspecification";

    public static final String INJECTOREXTENSIONID = IncQueryRuntimePlugin.PLUGIN_ID + ".injectorprovider";

}
