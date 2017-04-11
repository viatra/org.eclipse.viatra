/*******************************************************************************
 * Copyright (c) 2010-2017, Grill Balázs, IncQueryLabs
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Grill Balázs - initial API and implementation
 *******************************************************************************/
package org.eclipse.viatra.query.runtime.matchers.psystem.rewriters;

/**
 * Common reasons for removing constraint through rewriters
 *
 * @noreference This enum is not intended to be referenced by clients.
 */
public enum ConstraintRemovalReason implements IDerivativeModificationReason {

    MOOT_EQUALITY,
    WEAK_INEQUALITY_SELF_LOOP,
    TYPE_SUBSUMED,
    DUPLICATE
    
}
