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

package org.eclipse.incquery.maven.incquerybuilder;

import java.io.File;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.project.MavenProject;
import org.eclipse.emf.codegen.ecore.genmodel.GenModelPackage;
import org.eclipse.emf.codegen.ecore.genmodel.GenPackage;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emf.ecore.xmi.impl.EcoreResourceFactoryImpl;
import org.eclipse.incquery.maven.incquerybuilder.helper.Metamodel;
import org.eclipse.incquery.maven.incquerybuilder.setup.EMFPatternLanguageMavenStandaloneSetup;
import org.eclipse.incquery.maven.incquerybuilder.setup.MavenBuilderGenmodelLoader;
import org.eclipse.xtext.maven.Language;
import org.eclipse.xtext.maven.OutputConfiguration;

import com.google.common.base.Strings;
import com.google.common.collect.Iterators;
import com.google.common.collect.Maps;

/**
 * Goal which generates Java code for EMF-IncQuery patterns
 * 
 * @goal generate
 * @phase generate-sources
 * @requiresDependencyResolution compile
 */
public class IncqueryBuilderMojo extends AbstractMojo {

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

    public void execute() throws MojoExecutionException, MojoFailureException {

        registerGenmodelExtension();

        registerMetamodels();

        ResourceOrderingXtextGenerator generator = new ResourceOrderingXtextGenerator();

        setupXtextGenerator(generator);

        generator.execute();

    }

    /**
     * To parameterize the XtextGenerator object accordingly
     * 
     * @param generator
     *            The XtextGenerator object
     */
    private void setupXtextGenerator(ResourceOrderingXtextGenerator generator) {
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

        try {

            Class<?> generatorClass = generator.getClass();

            Field skipField = generatorClass.getDeclaredField("skip");
            skipField.setAccessible(true);
            skipField.set(generator, new Boolean(false));

            Field failOnErrorField = generatorClass.getDeclaredField("failOnValidationError");
            failOnErrorField.setAccessible(true);
            failOnErrorField.set(generator, new Boolean(true));

            Field projectField = generatorClass.getDeclaredField("project");
            projectField.setAccessible(true);
            projectField.set(generator, project);

            Field classpathField = generatorClass.getDeclaredField("classpathElements");
            classpathField.setAccessible(true);
            classpathField.set(generator, classpathElements);

            Field tmpDirectoryField = generatorClass.getDeclaredField("tmpClassDirectory");
            tmpDirectoryField.setAccessible(true);
            tmpDirectoryField.set(generator, tmpClassDirectory);

            Field encodingField = generatorClass.getDeclaredField("encoding");
            encodingField.setAccessible(true);
            encodingField.set(generator, encoding);

            Field sourceLevelField = generatorClass.getDeclaredField("compilerSourceLevel");
            sourceLevelField.setAccessible(true);
            sourceLevelField.set(generator, compilerSourceLevel);

            Field targetLevelField = generatorClass.getDeclaredField("compilerTargetLevel");
            targetLevelField.setAccessible(true);
            targetLevelField.set(generator, compilerTargetLevel);

        } catch (NoSuchFieldException e1) {

        } catch (SecurityException e1) {

        } catch (IllegalArgumentException e) {

        } catch (IllegalAccessException e) {

        }
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
