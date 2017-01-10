/*******************************************************************************
 * Copyright (c) 2015-2016, IncQuery Labs Ltd., Ericsson AB, CEA LIST
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
import org.eclipse.jdt.core.ElementChangedEvent
import org.eclipse.jdt.core.IElementChangedListener
import org.eclipse.jdt.core.IJavaElement
import org.eclipse.jdt.core.IJavaElementDelta
import org.eclipse.jdt.core.IJavaProject
import org.eclipse.jdt.core.JavaCore
import org.eclipse.viatra.transformation.evm.api.event.EventRealm
import org.eclipse.viatra.transformation.evm.specific.scheduler.UpdateCompleteBasedScheduler.UpdateCompleteBasedSchedulerFactory
import org.eclipse.viatra.transformation.evm.update.UpdateCompleteProvider
import org.eclipse.xtend.lib.annotations.Accessors
import org.eclipse.viatra.integration.evm.jdt.util.JDTElementChangedEventTypeDecoder
import com.google.common.base.Joiner
import org.eclipse.viatra.integration.evm.jdt.util.ElementChangedEventType

class JDTRealm implements EventRealm {
	Set<JDTEventSource> sources = Sets.newHashSet()
	IElementChangedListener listener
	JDTUpdateCompleteProvider provider
	UpdateCompleteBasedSchedulerFactory schedulerFactory
	@Accessors(PROTECTED_GETTER)
	boolean active = false

	private static JDTRealm instance = null

	/**
	 * Constructor hidden for singleton class.
	 */
	protected new() {
		listener = [ ElementChangedEvent event |
			val delta = event.delta
			val types = JDTElementChangedEventTypeDecoder.toEventTypes(event.type)
			val typeString = Joiner.on(",").join(types)
			// Ignore POST_RECONCILE events as we only care about saved changes
			if (types.contains(ElementChangedEventType.POST_CHANGE)) {
				notifySources(delta)
			}
		]
		provider = new JDTUpdateCompleteProvider
		schedulerFactory = new UpdateCompleteBasedSchedulerFactory(provider)
	}

	static def JDTRealm getInstance() {
		if (instance == null) {
			instance = new JDTRealm
		}
		return instance
	}

	/**
	 * All events JDT sends on various threads should be handled synchronously.
	 */
	protected synchronized def notifySources(IJavaElement javaElement) {
		sources.forEach [
			createReferenceRefreshEvent(javaElement)
		]
	}

	/**
	 * All events JDT sends on various threads should be handled synchronously.
	 */
	private synchronized def notifySources(IJavaElementDelta delta) {
		sources.forEach [
			createEvent(delta)
		]
	}

	protected def void addSource(JDTEventSource source) {
		if (sources.empty) {
			JavaCore.addElementChangedListener(listener)
			active = true
		}
		sources.add(source)
	}

	protected def void removeSource(JDTEventSource source) {
		sources.remove(source)
	}

	protected def void buildFinishedOnProject(IJavaProject project) {
		provider.updateCompleted
	}

	def UpdateCompleteBasedSchedulerFactory getJDTBuilderSchedulerFactory() {
		return schedulerFactory
	}

}

class JDTUpdateCompleteProvider extends UpdateCompleteProvider {

	override protected updateCompleted() {
		super.updateCompleted()
	}

}
