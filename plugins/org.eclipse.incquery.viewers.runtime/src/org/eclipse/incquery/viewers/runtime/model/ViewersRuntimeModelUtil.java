/*******************************************************************************
 * Copyright (c) 2010-2013, Istvan Rath and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   istvanrath - initial API and implementation
 *******************************************************************************/
package org.eclipse.incquery.viewers.runtime.model;

import org.eclipse.incquery.patternlanguage.patternLanguage.Annotation;
import org.eclipse.incquery.patternlanguage.patternLanguage.Pattern;

/**
 * @author istvanrath
 *
 */
public class ViewersRuntimeModelUtil {

	public static boolean isItemPattern(Pattern p) {
		for (Annotation a : p.getAnnotations()) {
			if (a.getName().equals(Item.ANNOTATION_ID)) {
				return true;
			}
		}
		return false;
	}
	
	public static boolean isEdgePattern(Pattern p) {
		for (Annotation a : p.getAnnotations()) {
			if (a.getName().equals(Edge.ANNOTATION_ID)) {
				return true;
			}
		}
		return false;
	}
	
	public static boolean isContainmentPattern(Pattern p) {
		for (Annotation a : p.getAnnotations()) {
			if (a.getName().equals(Containment.ANNOTATION_ID)) {
				return true;
			}
		}
		return false;
	}
	
}
