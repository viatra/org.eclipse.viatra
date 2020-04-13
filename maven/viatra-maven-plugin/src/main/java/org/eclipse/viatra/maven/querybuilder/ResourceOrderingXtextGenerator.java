/*******************************************************************************
 * Copyright (c) 2010-2012, Denes Harmath, Zoltan Ujhelyi and Daniel Varro
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-v20.html.
 * 
 * SPDX-License-Identifier: EPL-2.0
 *******************************************************************************/
package org.eclipse.viatra.maven.querybuilder;

import static com.google.common.collect.Iterables.filter;
import static com.google.common.collect.Sets.newLinkedHashSet;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;
import org.eclipse.xtext.builder.standalone.LanguageAccess;
import org.eclipse.xtext.builder.standalone.LanguageAccessFactory;
import org.eclipse.xtext.builder.standalone.StandaloneBuilder;
import org.eclipse.xtext.builder.standalone.StandaloneBuilderModule;
import org.eclipse.xtext.builder.standalone.compiler.CompilerConfiguration;
import org.eclipse.xtext.builder.standalone.compiler.IJavaCompiler;
import org.eclipse.xtext.maven.ClusteringConfig;
import org.eclipse.xtext.maven.Language;
import org.eclipse.xtext.maven.MavenLog4JConfigurator;
import org.eclipse.xtext.maven.XtextGenerator;
import org.eclipse.xtext.util.Strings;
import org.eclipse.xtext.xbase.lib.IterableExtensions;

import com.google.common.collect.Lists;
import com.google.inject.Guice;
import com.google.inject.Injector;

/**
 * {@link XtextGenerator} 2.20.0 implementation
 * adapted to use {@link ResourceOrderingStandaloneBuilder} instead of {@link StandaloneBuilder} in {@link #internalExecute()}.
 */
public class ResourceOrderingXtextGenerator extends AbstractMojo {

    /**
     * Location of the generated source files.
     */
    @Parameter(defaultValue = "${project.build.directory}/xtext-temp")
    private String tmpClassDirectory;

    /**
     * File encoding argument for the generator.
     */
    @Parameter(property = "xtext.encoding", defaultValue = "${project.build.sourceEncoding}")
    protected String encoding;

    /**
     * The project itself. This parameter is set by maven.
     */
    @Parameter(defaultValue = "${project}", readonly = true, required = true)
    protected MavenProject project;

    /**
     * Project classpath.
     */
    @Parameter(defaultValue = "${project.compileClasspathElements}", readonly = true, required = true)
    private List<String> classpathElements;

    /**
     * Project source roots. List of folders, where the source models are located.<br>
     * The default value is a reference to the project's ${project.compileSourceRoots}.<br>
     * When adding a new entry the default value will be overwritten not extended. 
     */
    @Parameter
    private List<String> sourceRoots;
    
    /**
     * Java source roots. List of folders, where the java source files are located.<br>
     * The default value is a reference to the project's ${project.compileSourceRoots}.<br>
     * When adding a new entry the default value will be overwritten not extended.<br>
     * Used when your language needs java.
     */
    @Parameter
    private List<String> javaSourceRoots;

    @Parameter(required = true)
    private List<Language> languages;

    @Parameter(property = "xtext.generator.skip", defaultValue = "false")
    private Boolean skip;

    @Parameter(defaultValue = "true")
    private Boolean failOnValidationError;

    @Parameter(property = "maven.compiler.source", defaultValue = "1.8")
    private String compilerSourceLevel;

    @Parameter(property = "maven.compiler.source", defaultValue = "1.8")
    private String compilerTargetLevel;

    /**
     * RegEx expression to filter class path during model files look up
     */
    @Parameter
    private String classPathLookupFilter;

    /**
     * Clustering configuration to avoid OOME
     */
    @Parameter
    private ClusteringConfig clusteringConfig;

    public void execute() throws MojoExecutionException, MojoFailureException {
        if (skip) {
            getLog().info("skipped.");
        } else {
            new MavenLog4JConfigurator().configureLog4j(getLog());
            configureDefaults();
            internalExecute();
        }
    }

    protected void internalExecute() throws MojoExecutionException {
        Map<String, LanguageAccess> languageMap = new LanguageAccessFactory().createLanguageAccess(getLanguages(), this
                .getClass().getClassLoader());
        Iterable<String> classPathEntries = filter(getClasspathElements(), input -> !Strings.isEmpty(input.trim()));
        Injector injector = Guice.createInjector(new StandaloneBuilderModule());
        ResourceOrderingStandaloneBuilder builder = injector.getInstance(ResourceOrderingStandaloneBuilder.class); // XXX StandaloneBuilder has been changed to ResourceOrderingStandaloneBuilder
        builder.setBaseDir(project.getBasedir().getAbsolutePath());
        builder.setLanguages(languageMap);
        builder.setEncoding(encoding);
        builder.setClassPathEntries(classPathEntries);
        builder.setClassPathLookUpFilter(classPathLookupFilter);
        builder.setSourceDirs(sourceRoots);
        builder.setJavaSourceDirs(javaSourceRoots);
        builder.setFailOnValidationError(failOnValidationError);
        builder.setTempDir(createTempDir().getAbsolutePath());
        builder.setDebugLog(getLog().isDebugEnabled());
        if(clusteringConfig != null)
            builder.setClusteringConfig(clusteringConfig.convertToStandaloneConfig());
        configureCompiler(builder.getCompiler());
        logState();
        boolean errorDetected = !builder.launch();
        if (errorDetected && failOnValidationError) {
            throw new MojoExecutionException("Execution failed due to a severe validation error.");
        }
    }

    private void configureCompiler(IJavaCompiler compiler) {
        CompilerConfiguration conf = compiler.getConfiguration();
        conf.setSourceLevel(compilerSourceLevel);
        conf.setTargetLevel(compilerTargetLevel);
        conf.setVerbose(getLog().isDebugEnabled());
    }

    private void logState() {
        getLog().info("Encoding: " + (encoding == null ? "not set. Encoding provider will be used." : encoding));
        getLog().info("Compiler source level: " + compilerSourceLevel);
        getLog().info("Compiler target level: " + compilerTargetLevel);
        if (getLog().isDebugEnabled()) {
            getLog().debug("Source dirs: " + IterableExtensions.join(sourceRoots, ", "));
            getLog().debug("Java source dirs: " + IterableExtensions.join(javaSourceRoots, ", "));
            getLog().debug("Classpath entries: " + IterableExtensions.join(classpathElements, ", "));
        }
    }

    private File createTempDir() {
        File tmpDir = new File(tmpClassDirectory);
        if (!tmpDir.mkdirs() && !tmpDir.exists()) {
            throw new IllegalArgumentException("Couldn't create directory '" + tmpClassDirectory + "'.");
        }
        return tmpDir;
    }

    public Set<String> getClasspathElements() {
        Set<String> classpathElements = newLinkedHashSet();
        classpathElements.addAll(this.classpathElements);
        classpathElements.remove(project.getBuild().getOutputDirectory());
        classpathElements.remove(project.getBuild().getTestOutputDirectory());
        Set<String> nonEmptyElements = newLinkedHashSet(filter(classpathElements, input -> !Strings.isEmpty(input.trim())));
        return nonEmptyElements;
    }

    public List<Language> getLanguages() {
        return languages;
    }

    public void setLanguages(List<Language> languages) {
        this.languages = languages;
    }
    
    public String getTmpClassDirectory() {
        return tmpClassDirectory;
    }

    public void setTmpClassDirectory(String tmpClassDirectory) {
        this.tmpClassDirectory = tmpClassDirectory;
    }

    public String getEncoding() {
        return encoding;
    }

    public void setEncoding(String encoding) {
        this.encoding = encoding;
    }

    public MavenProject getProject() {
        return project;
    }

    public void setProject(MavenProject project) {
        this.project = project;
    }

    public List<String> getSourceRoots() {
        return sourceRoots;
    }

    public void setSourceRoots(List<String> sourceRoots) {
        this.sourceRoots = sourceRoots;
    }

    public List<String> getJavaSourceRoots() {
        return javaSourceRoots;
    }

    public void setJavaSourceRoots(List<String> javaSourceRoots) {
        this.javaSourceRoots = javaSourceRoots;
    }

    public Boolean getSkip() {
        return skip;
    }

    public void setSkip(Boolean skip) {
        this.skip = skip;
    }

    public Boolean getFailOnValidationError() {
        return failOnValidationError;
    }

    public void setFailOnValidationError(Boolean failOnValidationError) {
        this.failOnValidationError = failOnValidationError;
    }

    public String getCompilerSourceLevel() {
        return compilerSourceLevel;
    }

    public void setCompilerSourceLevel(String compilerSourceLevel) {
        this.compilerSourceLevel = compilerSourceLevel;
    }

    public String getCompilerTargetLevel() {
        return compilerTargetLevel;
    }

    public void setCompilerTargetLevel(String compilerTargetLevel) {
        this.compilerTargetLevel = compilerTargetLevel;
    }

    public String getClassPathLookupFilter() {
        return classPathLookupFilter;
    }

    public void setClassPathLookupFilter(String classPathLookupFilter) {
        this.classPathLookupFilter = classPathLookupFilter;
    }

    public ClusteringConfig getClusteringConfig() {
        return clusteringConfig;
    }

    public void setClusteringConfig(ClusteringConfig clusteringConfig) {
        this.clusteringConfig = clusteringConfig;
    }

    public void setClasspathElements(List<String> classpathElements) {
        this.classpathElements = classpathElements;
    }

    private void configureDefaults() {
        if (sourceRoots == null) {
            sourceRoots = Lists.newArrayList(project.getCompileSourceRoots());
        }
        if (javaSourceRoots == null) {
            javaSourceRoots = Lists.newArrayList(project.getCompileSourceRoots());
        }
    }
}
