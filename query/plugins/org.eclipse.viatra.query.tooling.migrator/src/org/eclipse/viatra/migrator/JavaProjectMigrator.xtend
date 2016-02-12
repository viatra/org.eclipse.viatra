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
package org.eclipse.viatra.migrator

import java.util.Map
import java.util.Map.Entry
import org.eclipse.core.filebuffers.FileBuffers
import org.eclipse.core.filebuffers.LocationKind
import org.eclipse.core.resources.IProject
import org.eclipse.core.runtime.IProgressMonitor
import org.eclipse.core.runtime.SubMonitor
import org.eclipse.jdt.core.ICompilationUnit
import org.eclipse.jdt.core.IJavaProject
import org.eclipse.jdt.core.IPackageFragmentRoot
import org.eclipse.jdt.core.JavaCore
import org.eclipse.jdt.core.dom.AST
import org.eclipse.jdt.core.dom.ASTNode
import org.eclipse.jdt.core.dom.ASTParser
import org.eclipse.jdt.core.dom.ASTVisitor
import org.eclipse.jdt.core.dom.ImportDeclaration
import org.eclipse.jdt.core.dom.Name
import org.eclipse.jdt.core.dom.QualifiedName
import org.eclipse.jdt.core.dom.SimpleName
import org.eclipse.jdt.core.dom.SimpleType
import org.eclipse.jdt.core.dom.rewrite.ASTRewrite
import org.eclipse.viatra.query.tooling.core.project.ProjectGenerationHelper
import org.eclipse.core.resources.IFile
import org.eclipse.core.resources.IContainer

class JavaProjectMigrator extends JavaProjectMigratorData{
	
	val IJavaProject javaProject
	
	new(IProject project) {
		javaProject = JavaCore.create(project)
	}
	
	new(IJavaProject project){
		javaProject = project
	}
	
	def migrate(IProgressMonitor monitor){
		val SubMonitor m = SubMonitor.convert(monitor);
		
		/*
		 * Collect compilation units
		 */
		val list = newLinkedList()
		for(p : javaProject.packageFragments){
			if (p.kind == IPackageFragmentRoot::K_SOURCE){
				list.addAll(p.compilationUnits)
			}
		}
		m.beginTask("Migrating project", list.size*2+1)
		
		/*
		 * Update PDE dependencies 
		 */
		val project = javaProject.project
		if (ProjectGenerationHelper.isOpenPDEProject(project)){
			ProjectGenerationHelper.replaceBundledependencies(project, bundleRenames, bundleVersions, m.newChild(1))
		}
		
		/*
		 * Refactor compilation units
		 */
		val bufferManager = FileBuffers::textFileBufferManager
		for(unit : list){
			val ast = parse(unit, m.newChild(1));
			val rewrite = collectChanges(ast);
			m.worked(1)
			
			val textEdit = rewrite.rewriteAST()
			val path = unit.path
			try{
				bufferManager.connect(path, LocationKind::IFILE, null)
				val textFileBuffer = bufferManager.getTextFileBuffer(path, LocationKind::IFILE)
				val document = textFileBuffer.document
			
				textEdit.apply(document)
				
				textFileBuffer.commit(null, false)
			}finally{
				bufferManager.disconnect(path, LocationKind::IFILE, null)
				m.worked(1)
			}
		}
		
		/*
		 * Collect XTend files
		 */
		val xtendlist = <IFile>newLinkedList()
		javaProject.project.accept([
			if (it instanceof IFile && "xtend".equalsIgnoreCase(it.fileExtension)){
				xtendlist.add(it as IFile)
			}
			return it instanceof IContainer
		])
		
		/*
		 * Replace imports in XTend files
		 */
		for(IFile file : xtendlist){
			updateXTend(file)
		}
	}
	
	def updateXTend(IFile xtendFile){
		val replacer = new FileStringReplacer(xtendFile)
		for(entry : qualifiedNameRenames.entrySet){
			if (entry.key.endsWith(".")){
				//Package rename
				replacer.replacePattern(entry.key, entry.value)
			}else{
				//Class rename
				if (replacer.replacePattern("import "+entry.key, "import "+entry.value)){
					//Class was imported, replace all occurences
					replacer.replacePattern(getLastSegment(entry.key), getLastSegment(entry.value))
				}else{
					//Attempt to replace FQN class references
					replacer.replacePattern(entry.key, entry.value)
				}
			}
		}
		replacer.save
	}
	
	def parse(ICompilationUnit unit, IProgressMonitor monitor){
		val parser = ASTParser.newParser(AST.JLS8); 
		parser.setKind(ASTParser.K_COMPILATION_UNIT);
		parser.setSource(unit);
		parser.setResolveBindings(true);
		return parser.createAST(monitor)
	}
	
	def replaceName(String oldValue, Entry<String, String> entry){
		return oldValue.replace(entry.key, entry.value)
	}
	
	def getLastSegment(String fqn){
		if (fqn.endsWith('.')){
			return ""
		}
		val i = fqn.lastIndexOf('.')
		if (i >= 0){
			return fqn.substring(i+1);
		}
		return fqn
	}
	
	def createChange(ASTRewrite rewrite, ImportDeclaration importDeclaration, Map<String, String> typeRenames){
		val fullyQualifiedName = importDeclaration.name.fullyQualifiedName
		for(entry : JavaProjectMigrator.qualifiedNameRenames.entrySet){
			if ((fullyQualifiedName).startsWith(entry.key)){
				val newName = replaceName(fullyQualifiedName, entry)
				val tn_old = getLastSegment(fullyQualifiedName)
				val tn_new = getLastSegment(newName)
				if (tn_old != tn_new){
					typeRenames.put(tn_old, tn_new)
				}
				rewrite.set(importDeclaration, ImportDeclaration::NAME_PROPERTY, importDeclaration.AST.newName(newName), null)
				return
			}
		}
	}
	
	def createChange(ASTRewrite rewrite, SimpleType type, Map<String, String> typeRenames){
		val Name name = type.name;
		if (name instanceof QualifiedName){
			val fullyQualifiedName = name.fullyQualifiedName
			for(entry : JavaProjectMigrator.qualifiedNameRenames.entrySet){
				if ((fullyQualifiedName).startsWith(entry.key)){
					rewrite.set(type, ImportDeclaration::NAME_PROPERTY, type.AST.newName(replaceName(fullyQualifiedName, entry)), null)
					return
				}
			}
		}
		if (name instanceof SimpleName){
			val n = name.fullyQualifiedName
			if (typeRenames.containsKey(n)){
				rewrite.set(type, SimpleType::NAME_PROPERTY, type.AST.newName(typeRenames.get(n)), null)
			}
		}
	}
	
	def collectChanges(ASTNode node){
		val rewrite = ASTRewrite.create(node.AST)
		val typeRenames = <String, String>newHashMap
		
		node.accept(new ASTVisitor(){
			
			override visit(ImportDeclaration node) {
				createChange(rewrite, node, typeRenames);
				super.visit(node)
			}
			
			override visit(SimpleType node) {
				createChange(rewrite, node, typeRenames)
				super.visit(node)
			}
			
		})
		return rewrite
	}
	
}