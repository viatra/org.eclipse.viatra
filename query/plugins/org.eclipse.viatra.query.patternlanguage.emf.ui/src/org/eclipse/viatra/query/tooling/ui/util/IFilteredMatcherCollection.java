/*******************************************************************************
 * Copyright (c) 2010-2016, Abel Hegedus, IncQuery Labs Ltd.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-v20.html.
 * 
 * SPDX-License-Identifier: EPL-2.0
 *******************************************************************************/
package org.eclipse.viatra.query.tooling.ui.util;

/**
 * @author Abel Hegedus
 * @since 1.4
 *
 */
public interface IFilteredMatcherCollection {

    Iterable<IFilteredMatcherContent<?>> getFilteredMatchers();
    
}
