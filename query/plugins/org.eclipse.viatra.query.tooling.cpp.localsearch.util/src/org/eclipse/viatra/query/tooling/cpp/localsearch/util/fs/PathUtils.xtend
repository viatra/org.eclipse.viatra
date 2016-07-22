/*******************************************************************************
 * Copyright (c) 2014-2016 Robert Doczi, IncQuery Labs Ltd.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Robert Doczi - initial API and implementation
 *******************************************************************************/
package org.eclipse.viatra.query.tooling.cpp.localsearch.util.fs

import java.nio.file.Path
import java.nio.file.Paths

/**
 * @author Robert Doczi
 */
class PathUtils {

	static val String SOURCE_EXT = ".cpp"
	static val String HEADER_EXT = ".h"
	
	static val String DEFINITION_HEADER_EXT = "_def.h"
	static val String DECLARATION_HEADER_EXT = "_decl.h"

	/**
	 * Appends the string to the end of the path
	 */
	static def operator_plus(Path p1, String a) {
		Paths.get(p1.toString, a);
	}

	static def h(String fileName) {
		fileName + HEADER_EXT
	}

	static def cpp(String fileName) {
		fileName + SOURCE_EXT
	}
	
	static def definition(String fileName) {
		fileName + DEFINITION_HEADER_EXT
	}
	
	static def declaration(String fileName) {
		fileName + DECLARATION_HEADER_EXT
	}
}
