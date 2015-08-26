/*******************************************************************************
 * Copyright (c) 2010-2015, Andras Szabolcs Nagy, Abel Hegedus, Akos Horvath, Zoltan Ujhelyi and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Contributors:
 *   Andras Szabolcs Nagy - initial API and implementation
 *******************************************************************************/
package org.eclipse.viatra.dse.objectives.impl;

import java.util.Comparator;

import org.eclipse.viatra.dse.objectives.Comparators;
import org.eclipse.viatra.dse.objectives.IObjective;

import com.google.common.base.Preconditions;

/**
 * This abstract class implements the basic functionality of an objective ({@link IObjective} namely its name,
 * comparator and level.
 * 
 * @author Andras Szabolcs Nagy
 *
 */
public abstract class BaseObjective implements IObjective {

    protected final String name;
    protected Comparator<Double> comparator = Comparators.HIGHER_IS_BETTER;
    protected int level = 0;

    public BaseObjective(String name) {
        Preconditions.checkNotNull(name, "Name of the objective cannot be null.");
        this.name = name;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setComparator(Comparator<Double> comparator) {
        this.comparator = comparator;
    }

    @Override
    public Comparator<Double> getComparator() {
        return comparator;
    }

    @Override
    public void setLevel(int level) {
        this.level = level;
    }

    @Override
    public int getLevel() {
        return level;
    }

    public BaseObjective withLevel(int level) {
        setLevel(level);
        return this;
    }

    public BaseObjective withComparator(Comparator<Double> comparator) {
        setComparator(comparator);
        return this;
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj instanceof BaseObjective) {
            BaseObjective baseObjective = (BaseObjective) obj;
            return name.equals(baseObjective.getName());
        }
        return false;
    }

    @Override
    public String toString() {
        return name;
    }

}
