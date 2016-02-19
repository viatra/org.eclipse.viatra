/*******************************************************************************
 * Copyright (c) 2004-2011 Abel Hegedus and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Abel Hegedus - initial API and implementation
 *******************************************************************************/
package org.eclipse.viatra.query.application.generic;

import java.util.Map;

import org.eclipse.emf.common.util.URI;
import org.eclipse.equinox.app.IApplication;
import org.eclipse.equinox.app.IApplicationContext;
import org.eclipse.viatra.query.application.common.ViatraQueryHeadlessAdvanced;

/**
 * @author Abel Hegedus
 * 
 */
public class GenericEclipseViatraQueryApplication implements IApplication {

	private static String modelParam = "-m";
	private static String patternParam = "-p";


	
	@Override
	public Object start(IApplicationContext context) throws Exception {

		Map<String, Object> arguments = context.getArguments();
		String[] args = (String[]) arguments.get("application.args");
		String model = null;
		String patternFQN = null;
		if (args == null || args.length == 0) {
			displayHelp();
			return IApplication.EXIT_OK;
		}
		int i = 0;
		while (i < args.length) {
			if (args[i].equals(modelParam)) {
				model = args[i + 1];
				i += 2;
				continue;
			}
			if (args[i].equals(patternParam)) {
				patternFQN = args[i + 1];
				i += 2;
				continue;
			} else {
				i++;
				continue;
			}
		}

		if (model == null) {
		  System.out.println("Model parameter not set");
			displayHelp();
			return IApplication.EXIT_OK;
		}
		if (patternFQN == null) {
		  System.out.println("PatternFQN parameter not set");
			displayHelp();
			return IApplication.EXIT_OK;
		}

		ViatraQueryHeadlessAdvanced hla = new ViatraQueryHeadlessAdvanced();
//		System.out.println(hla.executeDemo_GenericAPI(model, patternFQN));
		URI fileURI = URI.createPlatformPluginURI("org.eclipse.viatra.query.application.queries/src/org/eclipse/viatra/query/application/queries/headlessQueries.vql", false);
	    URI modelURI = URI.createFileURI(model);
		System.out.println(hla.executeDemo_GenericAPI_LoadFromVQL(modelURI, fileURI, patternFQN));
		System.out.println(hla.executeTrackChangesDemo_Advanced(modelURI, patternFQN));
		
		return IApplication.EXIT_OK;
	}

	@Override
	public void stop() {}

	private void displayHelp() {
		System.out.println("Usage:\n<call> -m <modelFilePAth> -p <patternFQN>\n  -m    :  Required, the model to match on.\n  -p    :  Required, the pattern fqn to match");
	}
}
