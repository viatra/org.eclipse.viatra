package org.eclipse.incquery.patternlanguage.emf.tests;

import org.eclipse.incquery.patternlanguage.emf.EMFPatternLanguageInjectorProvider;
import org.eclipse.incquery.patternlanguage.emf.EMFPatternLanguageStandaloneSetup;
import org.eclipse.incquery.tooling.core.generator.GeneratorModule;

import com.google.inject.Guice;
import com.google.inject.Injector;

public class EMFPatternLanguageGeneratorInjectorProvider extends
		EMFPatternLanguageInjectorProvider {

	@Override
	protected Injector internalCreateInjector() {
		EMFPatternLanguageStandaloneSetup setup = new EMFPatternLanguageStandaloneSetup(){

			@Override
			public Injector createInjector() {
				
				return Guice.createInjector(new GeneratorModule());
			}
        	
        };
		return setup.createInjectorAndDoEMFRegistration();
	}

}
