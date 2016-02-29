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
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.handlers.HandlerUtil;

public class HelpHandler extends AbstractHandler {

    private static final String HELP_TITLE = "VIATRA Query-by-Example - Help";
    private static final String HELP_MAIN_TEXT;

    private class HelpDialog extends MessageDialog {
        public HelpDialog(Shell parentShell, String dialogTitle, Image dialogTitleImage, String dialogMessage,
                int dialogImageType, String[] dialogButtonLabels, int defaultIndex) {
            super(parentShell, dialogTitle, dialogTitleImage, dialogMessage, dialogImageType, dialogButtonLabels,
                    defaultIndex);
        }

        @Override
        protected void configureShell(Shell newShell) {
            Point newSize = newShell.getSize();
            newSize.x += 100;
            newShell.setSize(newSize);
            super.configureShell(newShell);
        }
    }

    static {
        StringBuilder sb = new StringBuilder("VIATRA Query-by-Example module!\n\n");
        sb.append(
                "This plugin can be used to automatically construct runnable patterns for the VIATRA framework, ");
        sb.append("by an active selection in an EMF instance model (so called example).\n");
        sb.append(
                "First, the user has to designate this selection, and after that press the green play button (labeled as");
        sb.append(
                "'Start Query-by-Example') on the toolbar. If the user's selection was proper, a discovery will be performed by the plugin, ");
        sb.append(
                "on the given EMF model.\n A tree view visualizer is given to the plugin's user interface, where the user can follow up the ");
        sb.append(
                "status of the pattern, and make some fine tuning on it via the Properties view. If the user is finished with the fine tuning, ");
        sb.append("the pattern's code could be generated with the buttons on the toolbar.\n");
        sb.append("Below, the module's functions are described.\n\n");
        sb.append(
                "  - Start Query-by-Example: starts the exploration, if a proper active selection in an EMF model is present. The determination of ");
        sb.append(
                "the auto adjust minimum depth is also happening in the initial search, and will be visible in the 'Exploration depth' scale. At ");
        sb.append("successful search, the tree view will be filled with initial data\n");
        sb.append(
                "  - Variables: there are two types of variables in this context: anchor points and discovered variables. Anchors are those, that are related to the ");
        sb.append(
                "user's given selection. These will be compulsory registered as input variables of the pattern. Discovered variables are those, that are not explicitly");
        sb.append(
                " selected by the user, but came up during a broader exploration, and are necessary for constructing a proper VIATRA pattern. These variables are by ");
        sb.append(
                "default represented as constraints in the pattern body, but can be also input parameters, or their declaration can be hidden from the pattern code\n");
        sb.append(
                "  - Edge constraints: basically a reference between two variables, but represented as a constraint in the pattern's body. Can be hidden in the code");
        sb.append(" via hiding paths\n");
        sb.append(
                "  - Exploration depth: this value is represented by the scale under this name. At the value of 1, only the anchor points, and the direct edges between ");
        sb.append(
                "them will be considered. By increasing the depth, the range of the exploration will be broaden, and new variables will be registered to the pattern\n");
        sb.append(
                "  - Auto adjust: during the initial search, a minimum value is determined, that is about a minimum depth, the pattern will be connected on the given ");
        sb.append("selection. Later, the user can return to this value, with the 'Auto adjust depth' button\n");
        sb.append(
                "  - Package, name space and other settings: package, pattern, variable and negative constraints' helper patterns' name can be set via the Eclipse ");
        sb.append(
                "Properties view. There are other fine tuning settings that can be performed on this view, so it is recommended to open the Properties view together ");
        sb.append("with the QBE view\n");
        sb.append(
                "  - Negative constraints: the 'Find negative constraints' button is for finding those constraints that are in the metamodel, but not present in the ");
        sb.append(
                "instance model itself. This function is searching these not presented constraints between all registered variables in the pattern, but registers no ");
        sb.append("new one. By default these constraints are hidden in the code\n");
        sb.append(
                "  - Attributes: all the EAttributes of the variables are registered as well in the constructing pattern. By default, all of them are hidden, so the ");
        sb.append("user has to manually enable them to be visible in the pattern code\n");
        sb.append(
                "  - Paths: this section visualizes all of the paths, that are 'walked through' the exploration. The paths can be hidden, which means, the constraints ");
        sb.append("on them are not present in the generated code\n");
        sb.append(
                "  - Code generation: if the user is finished with the fine tuning, the code can be generated to 3 destinations: 1. the user can create an entire new ");
        sb.append(
                ".eiq file, and the code goes there; 2. the user can insert the code to an already existing .eiq file in the workspace; 3. the user can put the ");
        sb.append("generated code on the clipboards\n");
        HELP_MAIN_TEXT = sb.toString();
    }

    public Object execute(ExecutionEvent event) throws ExecutionException {

        new HelpDialog(HandlerUtil.getActiveShell(event), HELP_TITLE, null, HELP_MAIN_TEXT, MessageDialog.INFORMATION,
                new String[] { IDialogConstants.OK_LABEL }, 0).open();

        return null;
    }
}
