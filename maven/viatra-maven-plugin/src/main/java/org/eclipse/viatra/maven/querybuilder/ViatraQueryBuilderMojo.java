/*******************************************************************************
 * Copyright (c) 2010-2014, Jozsef Makai, Zoltan Ujhelyi, Istvan Rath and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Jozsef Makai - initial API and implementation
 *******************************************************************************/

package org.eclipse.viatra.maven.querybuilder;

import java.io.File;
import java.lang.reflect.Field;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugin.descriptor.PluginDescriptor;
import org.apache.maven.project.MavenProject;
import org.codehaus.plexus.classworlds.realm.ClassRealm;
import org.eclipse.emf.codegen.ecore.genmodel.GenModelPackage;
import org.eclipse.emf.codegen.ecore.genmodel.GenPackage;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emf.ecore.xmi.impl.EcoreResourceFactoryImpl;
import org.eclipse.viatra.maven.querybuilder.helper.Metamodel;
import org.eclipse.viatra.maven.querybuilder.setup.EMFPatternLanguageMavenStandaloneSetup;
import org.eclipse.viatra.maven.querybuilder.setup.MavenBuilderGenmodelLoader;
import org.eclipse.xtext.maven.Language;
import org.eclipse.xtext.maven.OutputConfiguration;

import com.google.common.base.Strings;
import com.google.common.collect.Iterators;
import com.google.common.collect.Maps;

/**
 * Goal which generates Java code from patterns of the VIATRA Query Language.
 * 
 * @goal generate
 * @phase generate-sources
 * @requiresDependencyResolution compile
 */
public class ViatraQueryBuilderMojo extends AbstractMojo {

    /**
     * @parameter
     * @required
     */
    private List<Metamodel> metamodels;

    /**
     * the project relative path to the output directory
     * 
     * @parameter
     * @required
     */
    private String outputDirectory;

    /**
     * whether the output directory should be created if t doesn't already exist.
     * 
     * @property
     */
    private boolean createOutputDirectory = true;

    /**
     * whether existing resources should be overridden.
     * 
     * @property
     */
    private boolean overrideExistingResources = true;

    /**
     * The project itself. This parameter is set by maven.
     * 
     * @parameter expression="${project}"
     * @readonly
     * @required
     */
    protected MavenProject project;

    /**
     * The plugin descriptor
     * 
     * @parameter default-value="${plugin}"
     * @readonly
     * @required
     */
    protected PluginDescriptor descriptor;
    
    /**
     * Project classpath.
     * 
     * @parameter default-value="${project.compileClasspathElements}"
     * @readonly
     * @required
     */
    private List<String> classpathElements;

    /**
     * Location of the generated source files.
     * 
     * @parameter expression="${project.build.directory}/xtext-temp"
     */
    private String tmpClassDirectory;

    /**
     * File encoding argument for the generator.
     * 
     * @parameter expression="${xtext.encoding}" default-value="${project.build.sourceEncoding}"
     */
    protected String encoding;

    /**
     * @parameter expression="${maven.compiler.source}" default-value="1.6"
     */
    private String compilerSourceLevel;

    /**
     * @parameter expression="${maven.compiler.target}" default-value="1.6"
     */
    private String compilerTargetLevel;
    
    /**
     * whether the dependencies of the project should be added to the classpath of the plugin.
     * 
     * @parameter
     */
    private boolean useProjectDependencies = false;

    public void execute() throws MojoExecutionException, MojoFailureException {

        prepareClasspath();
        
        registerGenmodelExtension();

        registerMetamodels();

        ResourceOrderingXtextGenerator generator = new ResourceOrderingXtextGenerator();

        setupXtextGenerator(generator);

        generator.execute();

    }

    /**
     * The classpath of the Maven plugin does not include the dependencies of the Maven project
     * that the plugin runs on. However, the dependencies of the project are passed by Maven to
     * the plugin in the {@link #classpathElements} list. These URLs can be added to the realm
     * of the plugin.
     * 
     * @throws MojoExecutionException
     */
    protected void prepareClasspath() throws MojoExecutionException {
        if(useProjectDependencies) {
            ClassRealm realm = descriptor.getClassRealm();
            getLog().info("Adding project dependencies to classpath");
            for (String element : classpathElements)
            {
                File elementFile = new File(element);
                try {
                    realm.addURL(elementFile.toURI().toURL());
                } catch (MalformedURLException e) {
                    final String msg = "Error while adding classpath URL " + element;
                    getLog().error(msg);
                    throw new MojoExecutionException(msg, e);
                }
            }
        }
    }

    /**
     * To parameterize the XtextGenerator object accordingly
     * 
     * @param generator
     *            The XtextGenerator object
     */
    private void setupXtextGenerator(ResourceOrderingXtextGenerator generator) throws MojoFailureException {
        generator.setLog(getLog()); // it's needed to give our Logger to the
                                    // other plugin to avoid exceptions

        Language language = new Language();
        language.setSetup(EMFPatternLanguageMavenStandaloneSetup.class.getCanonicalName());

        OutputConfiguration outputConfiguration = new OutputConfiguration();
        outputConfiguration.setOutputDirectory(outputDirectory);
        outputConfiguration.setCreateOutputDirectory(createOutputDirectory);
        outputConfiguration.setOverrideExistingResources(overrideExistingResources);

        List<OutputConfiguration> ocList = new ArrayList<OutputConfiguration>();
        ocList.add(outputConfiguration);

        language.setOutputConfigurations(ocList);

        List<Language> languageList = new ArrayList<Language>();
        languageList.add(language);

        generator.setLanguages(languageList);
        generator.setSkip(false);
        generator.setFailOnValidationError(true);
        generator.setProject(project);
        generator.setClasspathElements(classpathElements);
        generator.setTmpClassDirectory(tmpClassDirectory);
        generator.setEncoding(encoding);
        generator.setCompilerSourceLevel(compilerSourceLevel);
        generator.setCompilerTargetLevel(compilerTargetLevel);
    }

    /**
     * Register user given metamodel NSURI (determining from the EPackage) and genmodel URI pairs
     * @throws MojoExecutionException 
     */
    private void registerMetamodels() throws MojoExecutionException {
        for (Metamodel metamodel : metamodels) {
            String fqnOfEPackageClass = metamodel.getPackageClass();
            String genmodelUri = metamodel.getGenmodelUri();
            String metamodelNSURI = null;
            
            if (Strings.isNullOrEmpty(fqnOfEPackageClass) && Strings.isNullOrEmpty(genmodelUri)) {
                final String msg = "For a metamodel definition, either EPackage class of Genmodel URI must be set.";
                getLog().error(msg);
                throw new MojoExecutionException(msg);
            }
            
            if (!Strings.isNullOrEmpty(fqnOfEPackageClass) && !Strings.isNullOrEmpty(genmodelUri)) {
                getLog().warn(String.format("For metamodel %s both EPackage class and genmodel are set. Using EPackage class", fqnOfEPackageClass));
            }
            
            if (!Strings.isNullOrEmpty(fqnOfEPackageClass)) {
                /*
                 * Note that the side effect of this call is that the EPackage will be added to the Registry 
                 */
                loadNSUriFromClass(fqnOfEPackageClass);
            }
            
            if (!Strings.isNullOrEmpty(genmodelUri) && Strings.isNullOrEmpty(fqnOfEPackageClass)) {
                if (URI.createURI(genmodelUri).isRelative()) {
                    genmodelUri = "file://" + project.getBasedir().getAbsolutePath() + File.separator + genmodelUri;
                }
                if (Strings.isNullOrEmpty(metamodelNSURI)) {
                    try {
                    ResourceSet set = new ResourceSetImpl();
                    final Resource resource = set.getResource(URI.createURI(genmodelUri), true);
                    resource.load(Maps.newHashMap());
                    EcoreUtil.resolveAll(resource);
                    final Iterator<GenPackage> it = Iterators.filter(resource.getAllContents(), GenPackage.class);
                    while (it.hasNext()) {
                        final GenPackage genPackage = it.next();
                        final EPackage ecorePackage = genPackage.getEcorePackage();
                        EPackage.Registry.INSTANCE.put(ecorePackage.getNsURI(), ecorePackage);
                        MavenBuilderGenmodelLoader.addGenmodel(ecorePackage.getNsURI(), genmodelUri);
                    }
                    } catch (Exception e) {
                        final String msg = "Error while loading metamodel specification from " + genmodelUri;
                        getLog().error(msg);
                        throw new MojoExecutionException(msg, e);
                    }
                } else {
                    MavenBuilderGenmodelLoader.addGenmodel(metamodelNSURI, genmodelUri);
                }
            }
        }
    }

    /**
     * Returns the nsUri of the EPackage loaded from the class for the given qualified name.<br/>
     * 
     * IMPORTANT: as a side effect, the EPackage will be added to the {@link EPackage.Registry}! 
     * 
     * @param fqnOfEPackageClass the qualified name of the EPackage class
     * @return the nsUri of the EPackage that was loaded
     * @throws MojoExecutionException if the EPackage cannot be loaded or processed
     */
    private String loadNSUriFromClass(String fqnOfEPackageClass) throws MojoExecutionException {
        try {
            Class<?> ePackageClass = Class.forName(fqnOfEPackageClass);
            
            Field instanceField = ePackageClass.getDeclaredField("eINSTANCE");

            Class<?> instanceFieldType = instanceField.getType();

            if (ePackageClass != instanceFieldType) {
                getLog().error(
                        "eINSTANCE is not type of " + fqnOfEPackageClass + " in class " + fqnOfEPackageClass
                                + ". It's type of " + instanceFieldType.getCanonicalName() + ".");
                throw new MojoExecutionException("Execution failed due to wrong type of eINSTANCE");
            }
            
            // This call will load the EPackage into the Registry!  
            EPackage ePackage = (EPackage)instanceField.get(null);
            return ePackage.getNsURI();

        } catch (ClassNotFoundException e) {
            getLog().error("Couldn't find class " + fqnOfEPackageClass + " on the classpath.");
            throw new MojoExecutionException("Execution failed due to wrong classname.", e);
        } catch (NoSuchFieldException e) {
            getLog().error("The " + fqnOfEPackageClass + " class doesn't have eINSTANCE field.");
            throw new MojoExecutionException("Execution failed due to wrong classname.", e);
        } catch (Exception e) {
            throw new MojoExecutionException("Error while loading EPackage " + fqnOfEPackageClass, e);
        }
    }

    /**
     * To register genmodel extension and according factory to the extension factory
     */
    private void registerGenmodelExtension() {
        Resource.Factory.Registry.INSTANCE.getExtensionToFactoryMap().put("ecore", new EcoreResourceFactoryImpl());
        Resource.Factory.Registry.INSTANCE.getExtensionToFactoryMap().put("genmodel", new EcoreResourceFactoryImpl());
        GenModelPackage.eINSTANCE.eClass();
    }
}
