package org.eclipse.viatra.generators;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.viatra.query.patternlanguage.emf.EMFPatternLanguageStandaloneSetup;
import org.eclipse.xtext.workspace.FileProjectConfig;
import org.eclipse.xtext.workspace.ProjectConfigAdapter;

import com.google.inject.Injector;
import com.google.inject.Provider;

@SuppressWarnings("restriction")
public class ResourceSetProvider implements Provider<ResourceSet>{
    private String projectName;

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    @Override
    public ResourceSet get() {
        Injector injector = new EMFPatternLanguageStandaloneSetup().createInjector();
        ResourceSet instance = injector.getInstance(ResourceSet.class);
        ProjectConfigAdapter.install(instance, new FileProjectConfig(URI.createURI(projectName)));
        return instance;
    }

}
