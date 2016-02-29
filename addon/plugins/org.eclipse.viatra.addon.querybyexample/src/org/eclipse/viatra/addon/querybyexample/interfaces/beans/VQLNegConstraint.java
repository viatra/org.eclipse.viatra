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
package org.eclipse.viatra.addon.querybyexample.interfaces.beans;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EReference;

public class VQLNegConstraint extends VQLConstraint {

    private String helperPatternName;

    private boolean queryExplorerChecked = false;

    private static Map<String, Integer> negConstraintsNameRegister = new HashMap<String, Integer>();

    public VQLNegConstraint(EObject start, EReference ref, EObject end) {
        super(start, ref, end);
        this.constructDefaultHelperPatternName();
        this.setVisible(false);
    }

    /**
     * constructing default helper pattern name, and setting it to 'helperPatternName' instance variable
     */
    private void constructDefaultHelperPatternName() {
        String referenceName = this.getReference().getName();
        String constructedName = "neg" + referenceName.substring(0, 1).toUpperCase() + referenceName.substring(1);
        Integer count = VQLNegConstraint.negConstraintsNameRegister.get(constructedName);
        if (count == null) {
            VQLNegConstraint.negConstraintsNameRegister.put(constructedName, 0);
            this.helperPatternName = constructedName;
        } else {
            int newCount = count + 1;
            VQLNegConstraint.negConstraintsNameRegister.put(constructedName, newCount);
            this.helperPatternName = constructedName + Integer.toString(newCount);
        }
    }

    public String getHelperPatternName() {
        return helperPatternName;
    }

    public void setHelperPatternName(String helperPatternName) {
        this.helperPatternName = helperPatternName;
    }

    public boolean isQueryExplorerChecked() {
        return queryExplorerChecked;
    }

    public void setQueryExplorerChecked(boolean queryExplorerChecked) {
        this.queryExplorerChecked = queryExplorerChecked;
    }
}
