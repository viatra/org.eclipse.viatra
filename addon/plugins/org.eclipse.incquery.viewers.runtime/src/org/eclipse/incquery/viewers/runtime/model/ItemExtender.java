/*******************************************************************************
 * Copyright (c) 2010-2014, Csaba Debreceni, Istvan Rath and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Csaba Debreceni - initial API and implementation
 *******************************************************************************/
package org.eclipse.incquery.viewers.runtime.model;

import com.google.common.base.Predicate;

public abstract class ItemExtender implements Item {

	public static final class RootItem implements Predicate<Item> {

		@Override
		public boolean apply(Item item) {
			if (item == null) {
				return false;
			}
			return item.getPolicy() == HierarchyPolicy.ROOT
					|| item.getPolicy() == HierarchyPolicy.ALWAYS;
		}
	}

	public static final class ChildItem implements Predicate<Item> {

		@Override
		public boolean apply(Item item) {
			if (item == null) {
				return false;
			}
			return item.getPolicy() == HierarchyPolicy.CHILD
					|| item.getPolicy() == HierarchyPolicy.ALWAYS
					|| item.getPolicy() == HierarchyPolicy.PORT;
		}
	}
}
