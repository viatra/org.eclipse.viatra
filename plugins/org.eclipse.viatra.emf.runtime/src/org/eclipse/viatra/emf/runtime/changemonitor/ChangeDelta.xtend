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
package org.eclipse.viatra.emf.runtime.changemonitor

import com.google.common.collect.Multimap
import org.eclipse.emf.ecore.EObject
import org.eclipse.incquery.runtime.api.IPatternMatch
import org.eclipse.incquery.runtime.api.IQuerySpecification
import org.eclipse.incquery.runtime.api.IncQueryMatcher
import org.eclipse.xtend.lib.annotations.Data

/**
 * Class representing the changes in a given instance model since the last checkpoint. 
 * It contains three MultiMaps which contain the changed elements sorted
 * by the detecting QuerySpecifications.
 * 
 * @author Lunk Péter
 */
@Data class ChangeDelta {
	public Multimap<IQuerySpecification<? extends IncQueryMatcher<IPatternMatch>>, EObject> appeared
	public Multimap<IQuerySpecification<? extends IncQueryMatcher<IPatternMatch>>, EObject> updated
	public Multimap<IQuerySpecification<? extends IncQueryMatcher<IPatternMatch>>, EObject> disappeared
	public boolean deploymentChanged
}
