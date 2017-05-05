/*******************************************************************************
 * Copyright (c) 2010-2012, Zoltan Ujhelyi, Balazs Grill, Istvan Rath and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Zoltan Ujhelyi, Balazs Grill - initial API and implementation
 *******************************************************************************/
package org.eclipse.viatra.migrator;

import com.google.common.collect.ImmutableList;

public class MigratorConstants {

    public static final ImmutableList<String> INCORRECT_BUILDER_IDS = ImmutableList.of(
            "org.eclipse.incquery.tooling.ui.projectbuilder", // $NON-NLS-1
            "org.eclipse.incquery.tooling.core.projectbuilder",// $NON-NLS-1
            "org.eclipse.viatra.query.tooling.core.projectbuilder"
    );
    public static final ImmutableList<String> INCORRECT_NATURE_IDS = ImmutableList.of(
            "org.eclipse.viatra2.emf.incquery.projectnature", // $NON-NLS-1
            "org.eclipse.incquery.projectnature" // $NON-NLS-1
    );
    public static final String GLOBAL_EIQ_PATH = "queries/globalEiqModel.xmi"; // $NON-NLS-1
    public static final String XEXPRESSIONEVALUATOR_EXTENSION_POINT_ID = "org.eclipse.incquery.runtime.xexpressionevaluator"; // $NON-NLS-1

    public static final String METADATA_MIGRATOR_COMMAND_ID = "org.eclipse.viatra.query.tooling.migrator.metadata.command"; // $NON-NLS-1
    public static final String API_MIGRATOR_COMMAND_ID = "org.eclipse.viatra.query.tooling.migrator.api.command"; // $NON-NLS-1

    public static boolean isIncorrectBuilderID(String id) {
        return INCORRECT_BUILDER_IDS.contains(id);
    }

    public static boolean isIncorrectNatureID(String id) {
        return INCORRECT_NATURE_IDS.contains(id);
    }
}
