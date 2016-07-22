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
package org.eclipse.viatra.query.tooling.cpp.localsearch.serializer

import java.io.File
import org.eclipse.viatra.query.tooling.cpp.localsearch.api.IGeneratorOutputProvider

/**
 * @author Robert Doczi
 */
class DefaultSerializer implements ISerializer {
	
	override serialize(String folderPath, IGeneratorOutputProvider provider, IFileAccessor fileAccessor) {
		provider.output.forEach[
			fileAccessor.createFile('''«folderPath»«File.separator»«it.folderPath»''', fileName, content)
		]
	}
	
	override createFolder(String folderPath, String folderName, IFileAccessor fileAccessor) {
		fileAccessor.createFolder(folderPath, folderName)
	}
	
}