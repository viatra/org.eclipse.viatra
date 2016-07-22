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
import org.eclipse.xtend.lib.annotations.AccessorType
import org.eclipse.xtend.lib.annotations.Accessors

import static extension org.eclipse.viatra.query.tooling.cpp.localsearch.util.fs.PathUtils.*
import static extension org.eclipse.xtend.lib.annotations.AccessorType.*

/**
 * @author Robert Doczi
 */
class FileSystemAccess {

	@Accessors(AccessorType.PUBLIC_GETTER)
	FileSystemTaskHandler handler

	@Accessors(AccessorType.PUBLIC_GETTER)
	Path root

	new(Path root, FileSystemTaskHandler handler) {
		this.root = root;
		this.handler = handler
	}
	
	def generateFile(Iterable<String> name, CharSequence content) {
		handler.addTask(new GenerateFileTask(Paths.get(root.toString, name), content.toString))
	}

	def generateFile(String name, CharSequence content) {
		handler.addTask(new GenerateFileTask(Paths.get(root.toString, name), content.toString))
	}

	def deleteFile(String name) {
		handler.addTask(new DeleteFileTask(Paths.get(root.toString, name)))
	}
	
	def createInSubfolder(String name) {
		new FileSystemAccess(root + name, handler)
	}
}
