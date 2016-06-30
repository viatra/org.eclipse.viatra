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

    private static final String HELP_TITLE = "VIATRA Query by Example - Help";
    private static final String HELP_MAIN_TEXT;

    private static class HelpDialog extends MessageDialog {
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
        HELP_MAIN_TEXT = 
  "VIATRA Query-by-Example tool\n\n"
+ "This tool can be used to automatically construct query patterns for the VIATRA framework, "
+ "based on an example - a set of EMF instance objects selected in a model editor or viewer. "
+ "The tool will discover how the selected elements are related to each other in the model, "
+ " and generate a VQL query that will find groups of model elements that are arranged similarly.\n\n"
+ "To get started, the user has to select a few elements in the model, and then press the green play button (labeled as"
+ "'Start') on the toolbar of the Query by Example View. "
+ "If the selection was recognized to consist of EObjects, a model exploration will be performed on the given EMF model. "
+ "The Query by Example View will present the results of the model discovery, where the user can follow up the "
+ "status of the pattern being generated, and make some fine tuning on it via the Properties view. "
+ "If the user is finished with the fine tuning, the pattern's code could be generated to a .vql file with the Save button on the toolbar. "
+ "After subsequent fine tuning, the Update button can be used to propagate any changes made to the same .vql file.\n\n"
+ "Main UI elements are described below:\n"
+ "  - Start: starts the exploration / discovery, if a proper active selection in an EMF model is present. \n"
+ "  - Expand (alternative action for the Start button): adds the current selection to previously selected elements, "
+ "and restarts exploration from the expanded set of anchors. \n"
+ "  - Model exploration: The model discovery will start separately from each selected EObject (anchor element), "
+ "will traverse reference links up to a given exploration depth limit, "
+ "and collect all paths (not longer than the given depth) connecting two anchors. "
+ "Initially, the tool automatically selects the smallest exploration depth that makes all anchors connected by the paths discovered. \n"
+ "  - 'Exploration depth' slider: displays the depth used for discovery. "
+ "Can be used to increase the depth beyond its initial value, to find more remote connections between the anchors"
+ " - this will re-trigger the exploration. \n"
+  " - 'Restore exploration depth': use this button to return the exploration depth to its initial setting.\n"
+ "  - Variables: these represent the pattern variables of the query. "
+ "They include the anchor points (given by the user as part of the selection) as well as "
+ "additional objects discovered as intermediate points along the paths. "
+ "Anchors appear as pattern parameter; this is optional for the latter kind of variable, "
+ "which can be excluded from the parameter list or the pattern altogether. "
+ "One can set certain properties of these variables (such as their name) in the Properties view.\n"
+ "  - Edge constraints: basically a reference between two variables, represented as a constraint in the pattern's body. "
+ "If such a constraint is deemed irrelevant and should not be part of the query specification, "
+ "it be excluded from the pattern in the code by marking as excluded all discovered paths traversing it.\n"
+  "  - Attributes: all the EAttributes of the variables are collected, and offered as additional, "
+ "opt-in attribute value constraints that can be individually selected to be included in the query.\n" 
+  "  - Negative constraints: the 'Find negative constraints' button is for finding those references between pairs of variables "
+ "that are permitted in the metamodel, but not present in the instance model itself. "
+ "Such missing references will be offered as additional, opt-in 'neg find' constraints "
+ "that can be individually selected to be included in the query.\n"
+  "  - Paths: this section visualizes all of the paths from one anchor to another, "
+ "that were discovered during the exploration with the current depth limit. "
+ "All paths are included in the query by default, so that references traversed along the path will appear as edge constraints in the output query."
+ "However, paths can be manually excluded in the Properties view.\n"
+  "  - Query package, EPackage URI and other settings: use the Properties view to fine-tune these settings. \n"
+  "  - Save: if the user is finished with the fine tuning, the pattern code can be generated to 3 destinations: "
+ " (a) create a brand new .vql file for the pattern code; "
+ " (b) replace an already existing .vql file in the workspace; "
+ " (c) put the generated code on the clipboard.\n"
+  "  - Update: if a file was selected previously, changes can be propagated to it, replacing its obsolete contents.\n ";

    }

    public Object execute(ExecutionEvent event) throws ExecutionException {

        new HelpDialog(HandlerUtil.getActiveShell(event), HELP_TITLE, null, HELP_MAIN_TEXT, MessageDialog.INFORMATION,
                new String[] { IDialogConstants.OK_LABEL }, 0).open();

        return null;
    }
}
