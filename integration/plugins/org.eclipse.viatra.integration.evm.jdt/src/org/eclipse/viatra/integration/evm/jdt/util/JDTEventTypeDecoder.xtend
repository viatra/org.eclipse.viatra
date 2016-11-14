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
package org.eclipse.viatra.integration.evm.jdt.util

import org.eclipse.viatra.integration.evm.jdt.JDTEventType
import org.eclipse.jdt.core.IJavaElementDelta

class JDTEventTypeDecoder {
	public static def toEventType(int value) {
		switch value {
			case IJavaElementDelta.ADDED:
				return JDTEventType.APPEARED
			case IJavaElementDelta.REMOVED:
				return JDTEventType.DISAPPEARED
			case IJavaElementDelta.CHANGED:
				return JDTEventType.UPDATED
			default :
				throw new IllegalArgumentException("Event type value is invalid.")
				
		}
	}
}
