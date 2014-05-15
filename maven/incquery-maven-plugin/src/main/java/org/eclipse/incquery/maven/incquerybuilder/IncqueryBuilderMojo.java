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
import java.util.List;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.project.MavenProject;
import org.eclipse.emf.codegen.ecore.genmodel.GenModelPackage;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.xmi.impl.EcoreResourceFactoryImpl;
import org.eclipse.incquery.maven.incquerybuilder.helper.ModelPair;
import org.eclipse.incquery.maven.incquerybuilder.setup.EMFPatternLanguageMavenStandaloneSetup;
import org.eclipse.incquery.maven.incquerybuilder.setup.MavenBuilderGenmodelLoader;
import org.eclipse.xtext.maven.Language;
import org.eclipse.xtext.maven.OutputConfiguration;
import org.eclipse.xtext.maven.XtextGenerator;

/**
 * Goal which generates Java code for EMF-IncQuery patterns
 * 
 * @goal generate
 * @phase generate-sources
 * @requiresDependencyResolution compile
 */
public class IncqueryBuilderMojo extends AbstractMojo {
    /**
     * FQN-s of the EPackage classes
     * 
     * @parameter
     * @required
     */
    private List<String> ePackages;

    /**
     * @parameter
     * @required
     */
    private List<ModelPair> modelPairs;

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
     * @parameter expression="project.build.directory/xtext-temp"
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

        resolveEPackages();

        registerModelPairs();

        XtextGenerator generator = new XtextGenerator();

        setupXtextGenerator(generator);

        generator.execute();

    }

    /**
     * To parameterize the XtextGenerator object accordingly
     * 
     * @param generator
     *            The XtextGenerator object
     */
    private void setupXtextGenerator(XtextGenerator generator) {
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
     * Register user given metamodel NSURI and genmodel URI pairs
     */
    private void registerModelPairs() {
        for (ModelPair modelPair : modelPairs) {
            String genmodelUri = modelPair.getGenmodelUri();
            if (URI.createURI(genmodelUri).isRelative()) {
                genmodelUri = project.getBasedir().getAbsolutePath() + File.separator + genmodelUri;
            }
            MavenBuilderGenmodelLoader.addGenmodel(modelPair.getModelNsUri(), "file://" + genmodelUri);
        }
    }

    /**
     * To access the eINSTANCE field of the user given EPackage classes
     * 
     * @throws MojoExecutionException
     */
    private void resolveEPackages() throws MojoExecutionException {
        for (String fqnOfEPackageClass : ePackages) {
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

                instanceField.get(null);

            } catch (ClassNotFoundException e) {
                getLog().error("Couldn't find class " + fqnOfEPackageClass + " on the classpath.");
                throw new MojoExecutionException("Execution failed due to wrong classname.");
            } catch (NoSuchFieldException e) {
                getLog().error("The " + fqnOfEPackageClass + " class doesn't have eINSTANCE field.");
            } catch (SecurityException e) {

            } catch (IllegalArgumentException e) {

            } catch (IllegalAccessException e) {

            }
        }
    }

    /**
     * To register genmodel extension and according factory to the extension factory
     */
    private void registerGenmodelExtension() {
        Resource.Factory.Registry.INSTANCE.getExtensionToFactoryMap().put("genmodel", new EcoreResourceFactoryImpl());
        GenModelPackage.eINSTANCE.eClass();
    }
}
