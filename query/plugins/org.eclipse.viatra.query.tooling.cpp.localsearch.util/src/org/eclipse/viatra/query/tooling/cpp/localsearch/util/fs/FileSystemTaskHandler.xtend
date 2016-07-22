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

import java.io.BufferedWriter
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.StandardOpenOption
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import org.eclipse.xtend.lib.annotations.Accessors
import java.nio.file.SimpleFileVisitor
import java.nio.file.attribute.BasicFileAttributes
import java.io.IOException
import java.nio.file.FileVisitResult
import java.nio.charset.Charset

/**
 * @author Robert Doczi
 */
class FileSystemTaskHandler {
	val extension ExecutorService executor;

	new() {

		// TODO: More threads seem slower, needs investigation
		//val pq = new PriorityBlockingQueue<Runnable>(20, Comparator::comparing[(it as FileTask).priority])
		//executor = new ThreadPoolExecutor(1, 2, 10, TimeUnit.SECONDS, pq)
		executor = Executors::newSingleThreadExecutor
	}

	def addTask(FileTask task) {
		task.execute
	}

	def flush(long timeout, TimeUnit tu) {
		executor.shutdown()
		executor.awaitTermination(timeout, tu)
	}
}

abstract class FileTask implements Runnable {

	protected Path path
	@Accessors(PUBLIC_GETTER)
	protected int priority

	new(Path path) {
		this.path = path;
	}
}

class CreateDirectoryTask extends FileTask {

	new(Path path) {
		super(path)

		priority = 0
	}

	override run() {
		Files.createDirectory(path)
	}

}

class GenerateFileTask extends FileTask {

	String content

	new(Path path, CharSequence content) {
		super(path)
		this.content = content.toString

		priority = 1
	}

	override run() {
		val parent = path.parent
		if (parent != null && !Files.exists(parent))
			Files.createDirectories(parent)
		if (Files.exists(path))
			Files.delete(path)
		Files.createFile(path)

		var BufferedWriter writer
		try {
			writer = Files.newBufferedWriter(path, Charset::defaultCharset, StandardOpenOption.CREATE)
			writer.write(content)
		} finally {
			writer?.close
		}
	}

}

class DeleteFileTask extends FileTask {

	new(Path path) {
		super(path)

		priority = 2
	}

	override run() {
		if(!Files.exists(path)) return;
		Files.walkFileTree(path,
			new SimpleFileVisitor<Path>() {

				override visitFile(Path file, BasicFileAttributes attrs) throws IOException {
					Files.delete(file)
					FileVisitResult.CONTINUE
				}

				override postVisitDirectory(Path dir, IOException exc) throws IOException {
					Files.delete(dir)
					FileVisitResult.CONTINUE
				}

			})
	}
}
