/*******************************************************************************
 * Copyright (c) 2010-2017, Gabor Bergmann, IncQueryLabs Ltd.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Gabor Bergmann - initial API and implementation
 *******************************************************************************/
package org.eclipse.viatra.query.runtime.rete.network;

/**
 * @author Gabor Bergmann
 * @since 1.7
 */
public interface IGroupable {

    /**
     * @return the current group of the mailbox
     * @since 1.7
     */
    CommunicationGroup getCurrentGroup();

    /**
     * Sets the current group of the mailbox
     * @since 1.7
     */
    void setCurrentGroup(CommunicationGroup group);

}