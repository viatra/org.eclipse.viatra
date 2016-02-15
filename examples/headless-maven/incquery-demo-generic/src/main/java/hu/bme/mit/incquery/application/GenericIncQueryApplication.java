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
package hu.bme.mit.incquery.application;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.xmi.impl.EcoreResourceFactoryImpl;

/**
 * @author Abel Hegedus
 * 
 */
public class GenericIncQueryApplication {

	private static String modelParam = "-m";
	private static String patternParam = "-p";
	private static String eiqFileParam = "-e";


	public static void main(String[] args) {
		String model = null;
		String eiqFile = null;
		String patternFQN = null;
		if (args == null || args.length == 0) {
			displayHelp();
			return;
		}
		int i = 0;
		while (i < args.length) {
			if (args[i].equals(modelParam)) {
				model = args[i + 1];
				i += 2;
				continue;
			} else if (args[i].equals(patternParam)) {
				patternFQN = args[i + 1];
				i += 2;
				continue;
			} else if (args[i].equals(eiqFileParam)) {
			    eiqFile = args[i + 1];
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
			return;
		}
		if (patternFQN == null) {
		  System.out.println("PatternFQN parameter not set");
			displayHelp();
			return;
		}
		if (eiqFile == null) {
		    System.out.println("EIQ file parameter not set");
		    displayHelp();
		    return;
		}

		IncQueryHeadlessAdvanced hla = new IncQueryHeadlessAdvanced();
		// Initializing metamodel		
		Resource.Factory.Registry.INSTANCE.getExtensionToFactoryMap().put("ecore", new EcoreResourceFactoryImpl());
		URI fileURI = URI.createFileURI(eiqFile);
	    URI modelURI = URI.createFileURI(model);
		System.out.println(hla.executeDemo_GenericAPI_LoadFromEIQ(modelURI, fileURI, patternFQN));
		System.out.println(hla.executeTrackChangesDemo_Advanced(modelURI, patternFQN));
		
	}

	private static void displayHelp() {
		System.out.println("Usage:\n<call> -m <modelFilePath> -e <eiqFilePath> -p <patternFQN>");
		System.out.println("  -m    :  Required, the model to match on.");
		System.out.println("  -e    :  Required, the pattern definition file (.eiq) to match on.");
		System.out.println("  -p    :  Required, the pattern fqn to match");
	}
}
