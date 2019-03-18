/*******************************************************************************
 * Copyright (c) 2010-2014, Csaba Debreceni, Istvan Rath and Daniel Varro
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-v20.html.
 * 
 * SPDX-License-Identifier: EPL-2.0
 *******************************************************************************/
package org.eclipse.viatra.addon.viewers.runtime.model;

import java.util.function.Predicate;

import org.eclipse.viatra.addon.viewers.runtime.notation.HierarchyPolicy;
import org.eclipse.viatra.addon.viewers.runtime.notation.Item;

public final class ItemExtender {

    private ItemExtender() {}
    
    public static final class RootItem implements Predicate<Item> {

        @Override
        public boolean test(Item item) {
            if (item == null) {
                return false;
            }
            return item.getPolicy() == HierarchyPolicy.ROOT
                    || item.getPolicy() == HierarchyPolicy.ALWAYS;
        }
    }

    public static final class ChildItem implements Predicate<Item> {

        @Override
        public boolean test(Item item) {
            if (item == null) {
                return false;
            }
            return item.getPolicy() == HierarchyPolicy.CHILD
                    || item.getPolicy() == HierarchyPolicy.ALWAYS
                    || item.getPolicy() == HierarchyPolicy.PORT;
        }
    }
}
