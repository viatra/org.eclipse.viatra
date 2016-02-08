/*******************************************************************************
 * Copyright (c) 2010-2013, Zoltan Ujhelyi, Istvan Rath and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Zoltan Ujhelyi - initial API and implementation
 *******************************************************************************/
package org.eclipse.viatra.addon.viewers.runtime.util;

import org.eclipse.viatra.transformation.evm.specific.resolver.InvertedDisappearancePriorityConflictResolver;

/**
 * A specific conflict resolver to support inverse priorities for node deletions
 *  
 * @author Zoltan Ujhelyi
 * @deprecated Use {@link InvertedDisappearancePriorityConflictResolver} instead
 */
@Deprecated()
public class ViewersConflictResolver extends InvertedDisappearancePriorityConflictResolver {


}
