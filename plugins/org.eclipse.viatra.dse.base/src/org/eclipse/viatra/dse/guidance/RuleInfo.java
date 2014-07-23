/*******************************************************************************
 * Copyright (c) 2010-2014, Miklos Foldenyi, Andras Szabolcs Nagy, Abel Hegedus, Akos Horvath, Zoltan Ujhelyi and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Contributors:
 *   Miklos Foldenyi - initial API and implementation
 *   Andras Szabolcs Nagy - initial API and implementation
 *******************************************************************************/
package org.eclipse.viatra.dse.guidance;

import java.util.Set;

import org.eclipse.incquery.runtime.evm.api.Activation;

public class RuleInfo implements Cloneable {

    private int occurrence = 0;
    private int applications = 0;
    private Set<Activation<?>> activations = null;
    private double priority = 0;
    private double selectionPriority = 0;
    private double cost = 0;

    public RuleInfo() {
    }

    public RuleInfo(double priority, double cost) {
        this.priority = priority;
        this.cost = cost;
    }

    public int getOccurrence() {
        return occurrence;
    }

    public void setOccurrence(int occurrence) {
        this.occurrence = occurrence;
    }

    public int getApplications() {
        return applications;
    }

    public void setApplications(int applications) {
        this.applications = applications;
    }

    public Set<Activation<?>> getActivations() {
        return activations;
    }

    public void setActivations(Set<Activation<?>> activations) {
        this.activations = activations;
    }

    public boolean isEnabled() {
        return activations.size() > 0;
    }

    public void incApp() {
        ++applications;
    }

    public void decApp() {
        --applications;
    }

    public int getRemainingApp() {
        return occurrence - applications;
    }

    public void resetSelectionPriority() {
        selectionPriority = 0;
    }

    public void addToSelectionPriority(double number) {
        selectionPriority += number;
    }

    public double getSelectionPriority() {
        return selectionPriority;
    }

    public double getPriority() {
        return priority;
    }

    public void setPriority(double priority) {
        this.priority = priority;
    }

    public double getCost() {
        return cost;
    }

    public void setCost(double cost) {
        this.cost = cost;
    }

    @Override
    public RuleInfo clone() {
        RuleInfo ruleInfo = new RuleInfo(priority, cost);
        ruleInfo.setOccurrence(occurrence);
        ruleInfo.setApplications(applications);
        return ruleInfo;
    }
}