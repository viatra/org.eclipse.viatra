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
package org.eclipse.viatra.dse.api;

import org.eclipse.emf.ecore.EClass;

public class ModelElementMetaData {

    private int appearsInLHS;
    private int appearsInLHSNegative;
    private int createsInRHS;
    private int deletesInRHS;
    private EClass referenceContainmentEClass;
    private EClass referencedEClass;

    public ModelElementMetaData(int appearsInLHS, int appearsInLHSNegative) {
        this(appearsInLHS, appearsInLHSNegative, 0, 0);
    }

    public ModelElementMetaData(int appearsInLHS, int appearsInLHSNegative, int createsInRHS, int deletesInRHS) {
        this.appearsInLHS = appearsInLHS;
        this.appearsInLHSNegative = appearsInLHSNegative;
        this.createsInRHS = createsInRHS;
        this.deletesInRHS = deletesInRHS;
    }

    public ModelElementMetaData(int appearsInLHS, int appearsInLHSNegative, int createsInRHS, int deletesInRHS,
            EClass referenceContainmentEClass, EClass referencedEClass) {
        this(appearsInLHS, appearsInLHSNegative, createsInRHS, deletesInRHS);
        this.referenceContainmentEClass = referenceContainmentEClass;
        this.referencedEClass = referencedEClass;
    }

    public void addAppearsInLHS(int number) {
        appearsInLHS += number;
    }

    public void addAppearsInLHSNegative(int number) {
        appearsInLHSNegative += number;
    }

    public void addCreatesInRHS(int number) {
        createsInRHS += number;
    }

    public void addDeletesInRHS(int number) {
        deletesInRHS += number;
    }

    public int getAppearsInLHS() {
        return appearsInLHS;
    }

    public int getAppearsInLHSNegative() {
        return appearsInLHSNegative;
    }

    public int getCreatesInRHS() {
        return createsInRHS;
    }

    public int getDeletesInRHS() {
        return deletesInRHS;
    }

    public EClass getReferenceContainmentEClass() {
        return referenceContainmentEClass;
    }

    public EClass getReferencedEClass() {
        return referencedEClass;
    }
}
