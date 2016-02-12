/*******************************************************************************
 * Copyright (c) 2010-2012, Balazs Grill, Istvan Rath and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Balazs Grill - initial API and implementation
 *******************************************************************************/
package org.eclipse.viatra.migrator;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;

public class FileStringReplacer {

	private final IFile file;
	
	private String data;
	
	public FileStringReplacer(IFile file) throws IOException, CoreException {
		this.file = file;
		try(InputStream input = file.getContents()){
			byte[] bytes = new byte[input.available()];
			input.read(bytes);
			data = new String(bytes, file.getCharset(true));
		}
	}
	
	/**
	 * Replace all matches of the given pattern with the given replacement and return true if
	 * the number of matches were greater than zero.
	 * @param pattern
	 * @param replacement
	 * @return
	 */
	public boolean replacePattern(String pattern, String replacement){
		String newData = data.replaceAll(pattern, replacement);
		boolean changed = !newData.equals(data);
		data = newData;
		return changed;
	}
	
	public void save() throws UnsupportedEncodingException, CoreException{
		file.setContents(new ByteArrayInputStream(data.getBytes(file.getCharset(true))), true, true, null);
	}
	
}
