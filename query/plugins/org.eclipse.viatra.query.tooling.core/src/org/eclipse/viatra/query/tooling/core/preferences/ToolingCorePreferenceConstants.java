/*******************************************************************************
 * Copyright (c) 2017, Abel Hegedus, IncQuery Labs Ltd.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-v20.html.
 * 
 * SPDX-License-Identifier: EPL-2.0
 *******************************************************************************/
package org.eclipse.viatra.query.tooling.core.preferences;

/**
 * Constant definitions for VIATRA Query Tooling Core plug-in preferences
 * 
 * @noreference This experimental class is not intended to be referenced by clients.
 */
public class ToolingCorePreferenceConstants {

    private ToolingCorePreferenceConstants() {/*Utility class constructor*/}
    
    /**
     * This preference stores the user's preference to disable automatic target platform metamodel index updates.
     * 
     * @see org.eclipse.viatra.query.tooling.core.targetplatform.TargetPlatformMetamodelsIndex
     * @noreference This experimental constant is not intended to be referenced by clients.
     */
    public static final String P_DISABLE_TARGET_PLATFORM_METAMODEL_INDEX_UPDATE = "org.eclipse.viatra.query.tooling.core.disableTargetPlatformIndex";

}
