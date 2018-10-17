/*******************************************************************************
 * Copyright (c) 2010-2014, Zoltan Ujhelyi, Istvan Rath and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Zoltan Ujhelyi - initial API and implementation
 *******************************************************************************/
package org.eclipse.viatra.query.tooling.ui.migrator.properties;

import org.apache.log4j.Logger;
import org.eclipse.core.expressions.PropertyTester;
import org.eclipse.core.resources.ICommand;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.pde.internal.core.natures.PDE;
import org.eclipse.viatra.query.tooling.core.project.ViatraQueryNature;
import org.eclipse.viatra.query.tooling.ui.migrator.MigratorConstants;
import org.eclipse.xtext.ui.XtextProjectHelper;

/**
 * Helper class for deciding whether the selected project is (1) an EMF-IncQuery project, and (2) is defined using the current version.
 * @author Zoltan Ujhelyi
 *
 */
@SuppressWarnings("restriction")
public class QueryProjectVersionTester extends PropertyTester {

    private static final String VERSION_TESTER = "outdated";

    private boolean hasIncorrectBuildCommandOrdering(IProject project) throws CoreException {
        IProjectDescription desc = project.getDescription();
        ICommand[] commands = desc.getBuildSpec();

        //Lookup the IncQuery-related command indixii
        int xtextIndex = -1;
        int iqIndex = -1;
        int jdtIndex = -1;
        for (int i = 0; i < commands.length; i++) {
            String id = commands[i].getBuilderName();
            if (ViatraQueryNature.BUILDER_ID.equals(id)) {
                iqIndex = i;
            } else if (XtextProjectHelper.BUILDER_ID.equals(id)) {
                xtextIndex = i;
            } else if (JavaCore.BUILDER_ID.equals(id)) {
                jdtIndex = i;
            }
        }
        return jdtIndex < xtextIndex || jdtIndex < iqIndex;
    }

    private boolean hasLog4jDependency(IProject project) throws JavaModelException {
        
        return JavaCore.create(project).findType(Logger.class.getName()) == null;
    }
    
    @Override
    public boolean test(Object receiver, String property, Object[] args, Object expectedValue) {
        try {
            if (VERSION_TESTER.equals(property) && receiver instanceof IProject && ((IProject)receiver).isAccessible()) {
                IProject project = (IProject) receiver;
                for (String ID : MigratorConstants.INCORRECT_NATURE_IDS) {
                    if (project.hasNature(ID)) {
                        return true;
                    }
                }
                if (project.hasNature(ViatraQueryNature.NATURE_ID)) {
                    return project.findMember(MigratorConstants.GLOBAL_EIQ_PATH) != null
                       || hasIncorrectBuildCommandOrdering(project) || hasLog4jDependency(project);
               } else {
                   return project.hasNature(PDE.PLUGIN_NATURE);
               }
            }
        } catch (Exception e) {
            /*
             * In case of errors while reading the project descriptions no additional steps needed, safe to say we
             * cannot handle the project
             */
        }
        return false;
    }


}
