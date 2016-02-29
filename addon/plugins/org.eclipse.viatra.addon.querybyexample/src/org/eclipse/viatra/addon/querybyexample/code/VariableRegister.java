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
package org.eclipse.viatra.addon.querybyexample.code;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.viatra.addon.querybyexample.interfaces.beans.VQLVariableSetting;

/**
 * Registering EIQ variables to a pattern
 */
public class VariableRegister {

    /**
     * Records the count of each variable type. Used for generating default variable names. <b>Key:</b> class name of
     * that belonging EClass <b>Value:</b> how many instances that class have
     */
    private Map<String, Integer> classNameCounter;

    private Map<EObject, VQLVariableSetting> fixVariables;

    private Map<EObject, VQLVariableSetting> freeVariables;

    public VariableRegister() {
        this.classNameCounter = new HashMap<String, Integer>();
        this.fixVariables = new HashMap<EObject, VQLVariableSetting>();
        this.freeVariables = new HashMap<EObject, VQLVariableSetting>();
    }

    /**
     * Registers a new fix variable by adding 1-1 new proper entry to the two map fields, fixVariables and
     * classNameCounter
     */
    public void registerFixVariable(EObject eo) {

        if (fixVariables.containsKey(eo))
            return;

        // updating the variable counter
        String className = eo.eClass().getName().toLowerCase();
        Integer count = this.classNameCounter.get(className);
        int newCount = (count == null) ? 0 : count + 1;
        this.classNameCounter.put(className, newCount);

        // giving a default variable name and registering the fix EIQ variable
        VQLVariableSetting varSetting = new VQLVariableSetting();
        varSetting.setInputVariable(true);
        varSetting.setVariableName(className + Integer.toString(newCount));
        varSetting.setType(eo.eClass());
        fixVariables.put(eo, varSetting);
    }

    /**
     * Registers a new free variable by adding 1-1 new proper entry to the two map fields, freeVariables and
     * classNameCounter
     */
    public void registerFreeVariable(EObject eo) {

        if (freeVariables.containsKey(eo))
            return;

        // updating the variable counter
        String className = eo.eClass().getName().toLowerCase();
        Integer count = this.classNameCounter.get(className);
        int newCount = (count == null) ? 0 : count + 1;
        this.classNameCounter.put(className, newCount);

        // giving a default variable name and registering the fix EIQ variable
        VQLVariableSetting varSetting = new VQLVariableSetting();
        varSetting.setInputVariable(false);
        varSetting.setVariableName(className + Integer.toString(newCount));
        varSetting.setType(eo.eClass());
        freeVariables.put(eo, varSetting);
    }

    public VQLVariableSetting getVariableSetting(EObject eo) {
        VQLVariableSetting ret = this.fixVariables.get(eo);
        if (ret == null)
            ret = this.freeVariables.get(eo);
        return ret;
    }

    public void reset() {
        this.classNameCounter.clear();
        this.freeVariables.clear();

        for (EObject eo : fixVariables.keySet()) {
            // updating the variable counter
            String className = eo.eClass().getName().toLowerCase();
            Integer count = this.classNameCounter.get(className);
            int newCount = (count == null) ? 0 : count + 1;
            this.classNameCounter.put(className, newCount);
        }
    }

    public Map<EObject, VQLVariableSetting> getFixVariables() {
        return this.fixVariables;
    }

    public Map<EObject, VQLVariableSetting> getFreeVariables() {
        return this.freeVariables;
    }
}
