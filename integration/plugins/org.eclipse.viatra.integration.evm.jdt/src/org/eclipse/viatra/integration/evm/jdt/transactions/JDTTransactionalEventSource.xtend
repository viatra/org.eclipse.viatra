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
package org.eclipse.viatra.integration.evm.jdt.transactions

import org.eclipse.viatra.integration.evm.jdt.JDTEvent
import org.eclipse.viatra.integration.evm.jdt.JDTEventAtom
import org.eclipse.viatra.integration.evm.jdt.JDTEventSource
import org.eclipse.viatra.integration.evm.jdt.JDTEventSourceSpecification
import org.eclipse.viatra.integration.evm.jdt.JDTRealm
import org.eclipse.viatra.transformation.evm.api.event.EventSource
import org.eclipse.jdt.core.ICompilationUnit
import org.eclipse.jdt.core.IJavaElementDelta

import static extension org.eclipse.viatra.integration.evm.jdt.util.JDTChangeFlagDecoder.toChangeFlags
import org.eclipse.viatra.integration.evm.jdt.util.ChangeFlag
import org.apache.log4j.Logger
import org.apache.log4j.Level
import org.eclipse.viatra.transformation.evm.api.event.EventHandler
import org.eclipse.jdt.core.IJavaElement

class JDTTransactionalEventSource extends JDTEventSource implements EventSource<JDTEventAtom> {
	
	new(JDTEventSourceSpecification spec, JDTRealm realm) {
		super(spec, realm)
		logger.level = Level.DEBUG
	}
	
	override void createEvent(IJavaElementDelta delta) {
		// Transactional events are made only for CompilationUnits
		if((delta.element instanceof ICompilationUnit)){
			val eventAtom = new JDTEventAtom(delta)
			
			// Create events with the correct event types, and send them to the handlers
			val eventTypes = delta.transactionalEventTypes
			eventTypes.forEach[ eventTpye |
				val event = new JDTEvent(eventTpye, eventAtom)
				debug('''Created event with type «eventTpye» for «eventAtom.delta»''')
				handlers.forEach[handleEvent(event)]
			]
		} else {
			createEventsForAppearedPackageContents(delta)
		}
		
		// Always process child-deltas
		delta.affectedChildren.forEach[affectedChildren |
			createEvent(affectedChildren)
		]
	}
	
	override sendExistingEvents(EventHandler<JDTEventAtom> handler, IJavaElement element) {
		val eventAtom = new JDTEventAtom(element)
		val JDTEvent createEvent = new JDTEvent(JDTTransactionalEventType::CREATE, eventAtom)
		handler.handleEvent(createEvent)
		val JDTEvent commitEvent = new JDTEvent(JDTTransactionalEventType::COMMIT, eventAtom)
		handler.handleEvent(commitEvent)
	}
	
	private def getTransactionalEventTypes(IJavaElementDelta delta) {
		val result = newArrayList()
		val flags = delta.flags.toChangeFlags
		// If something is removed, send delete event
		if(delta.kind.bitwiseAnd(IJavaElementDelta::REMOVED) != 0) {
			result.add(JDTTransactionalEventType::DELETE)
		// If something is added, send modify and commit event
		} else if( delta.kind.bitwiseAnd(IJavaElementDelta::ADDED) != 0) {
			result.add(JDTTransactionalEventType::CREATE)
			result.add(JDTTransactionalEventType::COMMIT)
		// If something is modified
		} else {
			// If its content or its children are changed, send modify event
			if( flags.exists[ flag |
				flag == ChangeFlag::CONTENT ||
				flag == ChangeFlag::CHILDREN
			]) {
				result.add(JDTTransactionalEventType::MODIFY)
			}
			// If the primary resource is changed (aka saved) send an additional commit event
			if( flags.exists[ flag | 
				flag == ChangeFlag::PRIMARY_RESOURCE
			]) {
				result.add(JDTTransactionalEventType::COMMIT)
			}
		}
		return result
	}
}
