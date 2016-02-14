/*******************************************************************************
 * Copyright (c) 2004-2015, Marton Bur, Peter Lunk, Zoltan Ujhelyi and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Marton Bur, Peter Lunk - initial API and implementation
 *******************************************************************************/
package org.eclipse.viatra.transformation.runtime.emf.changemonitor

import com.google.common.collect.Multimap
import org.eclipse.viatra.query.runtime.api.IPatternMatch
import org.eclipse.viatra.query.runtime.api.IQuerySpecification
import org.eclipse.xtend.lib.annotations.Data
import org.eclipse.viatra.query.runtime.api.ViatraQueryMatcher

/**
 * Class representing the changes in a given instance model since the last checkpoint. 
 * It contains three MultiMaps which contain the changed elements sorted
 * by the detecting QuerySpecifications.
 * 
 * @author Lunk Péter
 */
@Data class ChangeDelta {
	public Multimap<IQuerySpecification<? extends ViatraQueryMatcher<IPatternMatch>>, IPatternMatch> appeared
	public Multimap<IQuerySpecification<? extends ViatraQueryMatcher<IPatternMatch>>, IPatternMatch> updated
	public Multimap<IQuerySpecification<? extends ViatraQueryMatcher<IPatternMatch>>, IPatternMatch> disappeared
}
