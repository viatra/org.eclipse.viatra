/*******************************************************************************
 * Copyright (c) 2010-2016, Peter Lunk, Zoltan Ujhelyi, IncQuery Labs Ltd.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Peter Lunk, Zoltan Ujhelyi - initial API and implementation
 *******************************************************************************/
package org.eclipse.viatra.query.runtime.localsearch.matcher;

import java.util.List;

/**
 * @author Zoltan Ujhelyi
 *
 */
public interface ILocalSearchAdaptable {

    List<ILocalSearchAdapter> getAdapters();

    void addAdapter(ILocalSearchAdapter adapter);

    void removeAdapter(ILocalSearchAdapter adapter);

    void removeAdapters(List<ILocalSearchAdapter> adapter);

    void addAdapters(List<ILocalSearchAdapter> adapter);

}