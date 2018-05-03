/*******************************************************************************
 * Copyright (c) 2010-2017, Zoltan Ujhelyi, IncQuery Labs Ltd.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Zoltan Ujhelyi - initial API and implementation
 *******************************************************************************/
package org.eclipse.viatra.query.patternlanguage.emf.tests.generator

import org.eclipse.core.runtime.IPath
import org.eclipse.core.runtime.Path

class VQLGeneratorProperties {
    
    private new() {}
    
    static def IPath getVQLGeneratorPropertiesPath(String projectName) {
        return new Path(projectName).append(".settings").append("org.eclipse.viatra.query.patternlanguage.emf.EMFPatternLanguage.prefs")
    }
    
    public static val SEPARATE_CLASS_MATCHERS = '''
    BuilderConfiguration.is_project_specific=true
    autobuilding=true
    eclipse.preferences.version=1
    generateEclipseExtensions=true
    generateGeneratedAnnotation=true
    generateManifestEntries=true
    generateMatchProcessors=true
    generateMatchers=SEPARATE_CLASS
    generateSuppressWarnings=true
    generatedAnnotationComment=
    includeDateInGenerated=false
    outlet.DEFAULT_OUTPUT.cleanDirectory=false
    outlet.DEFAULT_OUTPUT.cleanupDerived=true
    outlet.DEFAULT_OUTPUT.createDirectory=true
    outlet.DEFAULT_OUTPUT.derived=true
    outlet.DEFAULT_OUTPUT.directory=./src-gen
    outlet.DEFAULT_OUTPUT.hideLocalSyntheticVariables=true
    outlet.DEFAULT_OUTPUT.installDslAsPrimarySource=false
    outlet.DEFAULT_OUTPUT.keepLocalHistory=true
    outlet.DEFAULT_OUTPUT.override=true
    outlet.DEFAULT_OUTPUT.sourceFolder.src-gen.directory=
    outlet.DEFAULT_OUTPUT.sourceFolder.src-gen.ignore=
    outlet.DEFAULT_OUTPUT.sourceFolder.src.directory=
    outlet.DEFAULT_OUTPUT.sourceFolder.src.ignore=
    outlet.DEFAULT_OUTPUT.userOutputPerSourceFolder=
    targetJavaVersion=JAVA5
    useJavaCompilerCompliance=true
    '''
    
    public static val NESTED_CLASS_MATCHERS = '''
    BuilderConfiguration.is_project_specific=true
    autobuilding=true
    eclipse.preferences.version=1
    generateEclipseExtensions=true
    generateGeneratedAnnotation=true
    generateManifestEntries=true
    generateMatchProcessors=true
    generateMatchers=NESTED_CLASS
    generateSuppressWarnings=true
    generatedAnnotationComment=
    includeDateInGenerated=false
    outlet.DEFAULT_OUTPUT.cleanDirectory=false
    outlet.DEFAULT_OUTPUT.cleanupDerived=true
    outlet.DEFAULT_OUTPUT.createDirectory=true
    outlet.DEFAULT_OUTPUT.derived=true
    outlet.DEFAULT_OUTPUT.directory=./src-gen
    outlet.DEFAULT_OUTPUT.hideLocalSyntheticVariables=true
    outlet.DEFAULT_OUTPUT.installDslAsPrimarySource=false
    outlet.DEFAULT_OUTPUT.keepLocalHistory=true
    outlet.DEFAULT_OUTPUT.override=true
    outlet.DEFAULT_OUTPUT.sourceFolder.src-gen.directory=
    outlet.DEFAULT_OUTPUT.sourceFolder.src-gen.ignore=
    outlet.DEFAULT_OUTPUT.sourceFolder.src.directory=
    outlet.DEFAULT_OUTPUT.sourceFolder.src.ignore=
    outlet.DEFAULT_OUTPUT.userOutputPerSourceFolder=
    targetJavaVersion=JAVA5
    useJavaCompilerCompliance=true
    '''
    
    public static val NESTED_CLASS_NO_PLUGINXML = '''
    BuilderConfiguration.is_project_specific=true
    autobuilding=true
    eclipse.preferences.version=1
    generateEclipseExtensions=false
    generateGeneratedAnnotation=false
    generateManifestEntries=true
    generateMatchProcessors=true
    generateMatchers=NESTED_CLASS
    generateSuppressWarnings=true
    generatedAnnotationComment=
    includeDateInGenerated=false
    outlet.DEFAULT_OUTPUT.cleanDirectory=false
    outlet.DEFAULT_OUTPUT.cleanupDerived=true
    outlet.DEFAULT_OUTPUT.createDirectory=true
    outlet.DEFAULT_OUTPUT.derived=true
    outlet.DEFAULT_OUTPUT.directory=./src-gen
    outlet.DEFAULT_OUTPUT.hideLocalSyntheticVariables=true
    outlet.DEFAULT_OUTPUT.installDslAsPrimarySource=false
    outlet.DEFAULT_OUTPUT.keepLocalHistory=true
    outlet.DEFAULT_OUTPUT.override=true
    outlet.DEFAULT_OUTPUT.sourceFolder.src-gen.directory=
    outlet.DEFAULT_OUTPUT.sourceFolder.src-gen.ignore=
    outlet.DEFAULT_OUTPUT.sourceFolder.src.directory=
    outlet.DEFAULT_OUTPUT.sourceFolder.src.ignore=
    outlet.DEFAULT_OUTPUT.userOutputPerSourceFolder=
    targetJavaVersion=JAVA5
    useJavaCompilerCompliance=true
    '''
    
    public static val NO_MATCHERS = '''
    BuilderConfiguration.is_project_specific=true
    autobuilding=true
    eclipse.preferences.version=1
    generateEclipseExtensions=true
    generateGeneratedAnnotation=true
    generateManifestEntries=true
    generateMatchProcessors=true
    generateMatchers=NESTED_CLASS
    generateSuppressWarnings=true
    generatedAnnotationComment=
    includeDateInGenerated=false
    outlet.DEFAULT_OUTPUT.cleanDirectory=false
    outlet.DEFAULT_OUTPUT.cleanupDerived=true
    outlet.DEFAULT_OUTPUT.createDirectory=true
    outlet.DEFAULT_OUTPUT.derived=true
    outlet.DEFAULT_OUTPUT.directory=./src-gen
    outlet.DEFAULT_OUTPUT.hideLocalSyntheticVariables=true
    outlet.DEFAULT_OUTPUT.installDslAsPrimarySource=false
    outlet.DEFAULT_OUTPUT.keepLocalHistory=true
    outlet.DEFAULT_OUTPUT.override=true
    outlet.DEFAULT_OUTPUT.sourceFolder.src-gen.directory=
    outlet.DEFAULT_OUTPUT.sourceFolder.src-gen.ignore=
    outlet.DEFAULT_OUTPUT.sourceFolder.src.directory=
    outlet.DEFAULT_OUTPUT.sourceFolder.src.ignore=
    outlet.DEFAULT_OUTPUT.userOutputPerSourceFolder=
    targetJavaVersion=JAVA5
    useJavaCompilerCompliance=true
    '''
    
    static def String customOutputDirectory(String directoryName) '''
    outlet.DEFAULT_OUTPUT.directory=./«directoryName»
    '''
}