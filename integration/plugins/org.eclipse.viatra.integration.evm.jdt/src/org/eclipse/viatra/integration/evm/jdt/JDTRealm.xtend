/*******************************************************************************
 * Copyright (c) 2015-2016, IncQuery Labs Ltd. and Ericsson AB
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Abel Hegedus, Daniel Segesdi, Robert Doczi, Zoltan Ujhelyi - initial API and implementation
 *******************************************************************************/
package org.eclipse.viatra.integration.evm.jdt

import com.google.common.collect.Sets
import java.util.Set
import org.apache.log4j.Level
import org.apache.log4j.Logger
import org.eclipse.viatra.transformation.evm.api.event.EventRealm
import org.eclipse.jdt.core.ElementChangedEvent
import org.eclipse.jdt.core.IElementChangedListener
import org.eclipse.jdt.core.IJavaElement
import org.eclipse.jdt.core.IJavaElementDelta
import org.eclipse.jdt.core.JavaCore

class JDTRealm implements EventRealm {
	Set<JDTEventSource> sources = Sets.newHashSet()
	extension val Logger logger = Logger.getLogger(this.class)
	IElementChangedListener listener
	
	private static JDTRealm instance = null
	
	/**
	 * Constructor hidden for singleton class.
	 */
	protected new() {
		logger.level = Level.DEBUG
		listener = [ ElementChangedEvent event |
			val delta = event.delta
			notifySources(delta)
		]
		JavaCore.addElementChangedListener(listener)
	}
	
	static def JDTRealm getInstance() {
		if(instance == null) {
			instance = new JDTRealm
		}
		return instance
	}
	
	def notifySources(IJavaElement javaElement) {
		sources.forEach[
			createReferenceRefreshEvent(javaElement)
		]
	}
	
	private def notifySources(IJavaElementDelta delta) {
		sources.forEach[
			createEvent(delta)
		]
	}
	
	def protected void addSource(JDTEventSource source) {
		sources.add(source)
	}
	
	def protected void removeSource(JDTEventSource source) {
		sources.remove(source)
	}

}
