/*******************************************************************************
 * Copyright (c) 2010-2012, Andras Okros, Istvan Rath and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Andras Okros - initial API and implementation
 *******************************************************************************/
package org.eclipse.incquery.tooling.ui.patternregistry.views;

import org.eclipse.incquery.runtime.patternregistry.PatternInfo;
import org.eclipse.incquery.runtime.patternregistry.PatternRegistry;
import org.eclipse.incquery.runtime.patternregistry.listeners.IPatternRegistryListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.part.ViewPart;

public class PatternRegistryViewPart extends ViewPart {

    private Label label;

    public PatternRegistryViewPart() {
        super();
    }

    @Override
    public void setFocus() {
        label.setFocus();
    }

    @Override
    public void createPartControl(Composite parent) {
        label = new Label(parent, 0);
        updateLabelText();

        PatternRegistry.INSTANCE.registerListener(new IPatternRegistryListener() {
            @Override
            public void patternRemoved(PatternInfo patternInfo) {
                updateLabelText();
            }

            @Override
            public void patternAdded(PatternInfo patternInfo) {
                updateLabelText();
            }
        });
    }

    private void updateLabelText() {
        String labelText = "";
        for (PatternInfo patternInfo : PatternRegistry.INSTANCE.getAllPatternInfosInAspect()) {
            labelText = labelText.concat(patternInfo.getId() + "\n");
        }
        label.setText(labelText);
    }

}
