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

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EModelElement;
import org.eclipse.emf.ecore.EReference;

public class RuleMetaData {

    private final Map<EClass, ModelElementMetaData> usedClasses = new HashMap<EClass, ModelElementMetaData>();
    private final Map<EReference, ModelElementMetaData> usedReferences = new HashMap<EReference, ModelElementMetaData>();
    private final Map<EAttribute, ModelElementMetaData> usedAttributes = new HashMap<EAttribute, ModelElementMetaData>();

    public void addRuleMetaData(RuleMetaData subResult) {

        for (EClass clazz : subResult.getClasses()) {
            ModelElementMetaData m = subResult.getMetaDataForClass(clazz);
            addClassWithoutSuperType(clazz, m.getAppearsInLHS(), m.getAppearsInLHSNegative(), m.getCreatesInRHS(),
                    m.getDeletesInRHS());
        }

        for (EReference reference : subResult.getReferences()) {
            ModelElementMetaData m = subResult.getMetaDataForReference(reference);
            addReference(reference, m.getAppearsInLHS(), m.getAppearsInLHSNegative(), m.getCreatesInRHS(),
                    m.getDeletesInRHS());
        }

        for (EAttribute attr : subResult.getAttributes()) {
            ModelElementMetaData m = subResult.getMetaDataForAttribute(attr);
            addAttribute(attr, m.getAppearsInLHS(), m.getAppearsInLHSNegative(), m.getCreatesInRHS(),
                    m.getDeletesInRHS());
        }

    }

    /**
     * Adds all super types too!
     * 
     * @param clazz
     * @param appearsInLHS
     * @param appearsInLHSNegative
     * @param addsInRHS
     * @param deletesInRHS
     */
    public void addClass(EClass clazz, int appearsInLHS, int appearsInLHSNegative, int createsInRHS, int deletesInRHS) {
        for (EClass superType : clazz.getEAllSuperTypes()) {
            addClassWithoutSuperType(superType, appearsInLHS, appearsInLHSNegative, createsInRHS, deletesInRHS);
        }
        addClassWithoutSuperType(clazz, appearsInLHS, appearsInLHSNegative, createsInRHS, deletesInRHS);
    }

    public void addClassWithoutSuperType(EClass clazz, int appearsInLHS, int appearsInLHSNegative, int createsInRHS,
            int deletesInRHS) {
        ModelElementMetaData c = usedClasses.get(clazz);
        if (c != null) {
            c.addAppearsInLHS(appearsInLHS);
            c.addAppearsInLHSNegative(appearsInLHSNegative);
            c.addCreatesInRHS(createsInRHS);
            c.addDeletesInRHS(deletesInRHS);
        } else {
            usedClasses.put(clazz, new ModelElementMetaData(appearsInLHS, appearsInLHSNegative, createsInRHS,
                    deletesInRHS));
        }
    }

    public void addClass(EClass clazz, int appearsInLHS, int appearsInLHSNegative) {
        addClass(clazz, appearsInLHS, appearsInLHSNegative, 0, 0);
    }

    public void addReference(EReference refernece, int appearsInLHS, int appearsInLHSNegative, int createsInRHS,
            int deletesInRHS) {
        ModelElementMetaData c = usedReferences.get(refernece);
        if (c != null) {
            c.addAppearsInLHS(appearsInLHS);
            c.addAppearsInLHSNegative(appearsInLHSNegative);
            c.addCreatesInRHS(createsInRHS);
            c.addDeletesInRHS(deletesInRHS);
        } else {
            EClass eReferenceType = refernece.getEReferenceType();
            EClass eContainingClass = refernece.getEContainingClass();
            usedReferences.put(refernece, new ModelElementMetaData(appearsInLHS, appearsInLHSNegative, createsInRHS,
                    deletesInRHS, eContainingClass, eReferenceType));
        }
    }

    public void addAttribute(EAttribute attribute, int appearsInLHS, int appearsInLHSNegative, int createsInRHS,
            int deletesInRHS) {
        ModelElementMetaData c = usedReferences.get(attribute);
        if (c != null) {
            c.addAppearsInLHS(appearsInLHS);
            c.addAppearsInLHSNegative(appearsInLHSNegative);
            c.addCreatesInRHS(createsInRHS);
            c.addDeletesInRHS(deletesInRHS);
        } else {
            usedAttributes.put(attribute, new ModelElementMetaData(appearsInLHS, appearsInLHSNegative, createsInRHS,
                    deletesInRHS));
        }
    }

    public Collection<EClass> getClasses() {
        return usedClasses.keySet();
    }

    public Collection<EReference> getReferences() {
        return usedReferences.keySet();
    }

    public Collection<EAttribute> getAttributes() {
        return usedAttributes.keySet();
    }

    public ModelElementMetaData getMetaDataForClass(EClass classifier) {
        return usedClasses.get(classifier);
    }

    public ModelElementMetaData getMetaDataForReference(EReference reference) {
        return usedReferences.get(reference);
    }

    public ModelElementMetaData getMetaDataForAttribute(EAttribute attribute) {
        return usedAttributes.get(attribute);
    }

    public Map<EClass, Integer> getLHSNumbersForClasses() {
        HashMap<EClass, Integer> map = new HashMap<EClass, Integer>();
        for (EClass c : usedClasses.keySet()) {
            map.put(c, usedClasses.get(c).getAppearsInLHS());
        }
        return map;
    }

    public Map<EReference, Integer> getLHSNumbersForReferences() {
        HashMap<EReference, Integer> map = new HashMap<EReference, Integer>();
        for (EReference c : usedReferences.keySet()) {
            map.put(c, usedReferences.get(c).getAppearsInLHS());
        }
        return map;
    }

    public Map<EAttribute, Integer> getLHSNumbersForAttributes() {
        HashMap<EAttribute, Integer> map = new HashMap<EAttribute, Integer>();
        for (EAttribute c : usedAttributes.keySet()) {
            map.put(c, usedAttributes.get(c).getAppearsInLHS());
        }
        return map;
    }

    public Map<EClass, Integer> getLHSNACNumbersForClasses() {
        HashMap<EClass, Integer> map = new HashMap<EClass, Integer>();
        for (EClass c : usedClasses.keySet()) {
            map.put(c, usedClasses.get(c).getAppearsInLHSNegative());
        }
        return map;
    }

    public Map<EReference, Integer> getLHSNACNumbersForReferences() {
        HashMap<EReference, Integer> map = new HashMap<EReference, Integer>();
        for (EReference c : usedReferences.keySet()) {
            map.put(c, usedReferences.get(c).getAppearsInLHSNegative());
        }
        return map;
    }

    public Map<EAttribute, Integer> getLHSNACNumbersForAttributes() {
        HashMap<EAttribute, Integer> map = new HashMap<EAttribute, Integer>();
        for (EAttribute c : usedAttributes.keySet()) {
            map.put(c, usedAttributes.get(c).getAppearsInLHSNegative());
        }
        return map;
    }

    public Map<? extends EModelElement, ModelElementMetaData> getClassesAndReferences() {
        HashMap<EModelElement, ModelElementMetaData> result = new HashMap<EModelElement, ModelElementMetaData>(
                (Map<? extends EModelElement, ModelElementMetaData>) usedClasses);
        result.putAll((Map<? extends EModelElement, ModelElementMetaData>) usedReferences);
        return result;
    }

    @Override
    public String toString() {

        StringBuilder sb = new StringBuilder();

        for (EClass clazz : getClasses()) {
            ModelElementMetaData m = getMetaDataForClass(clazz);
            writeToStringBuilder(sb, clazz.getName(), m);
        }
        for (EReference ref : getReferences()) {
            ModelElementMetaData m = getMetaDataForReference(ref);
            writeToStringBuilder(sb, ref.getName(), m);
        }
        for (EAttribute attr : getAttributes()) {
            ModelElementMetaData m = getMetaDataForAttribute(attr);
            writeToStringBuilder(sb, attr.getName(), m);
        }

        return sb.toString();
    }

    private void writeToStringBuilder(StringBuilder sb, String name, ModelElementMetaData m) {
        sb.append(name + ": " + m.getAppearsInLHS() + ", " + m.getAppearsInLHSNegative() + ", " + m.getCreatesInRHS()
                + ", " + m.getDeletesInRHS() + "\n");
    }
}
